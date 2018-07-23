/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.editor.hints.support;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.Registry;
import org.netbeans.modules.editor.hints.HintsUI;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.support.ErrorParserSupport;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;


/**
 * @author Jan Lahoda
 * @author leon chiver
 */
public final class HintsOperator implements CaretListener, ChangeListener, 
        FocusListener, DocumentListener, PropertyChangeListener {
    private static ErrorManager ERR = ErrorManager.getDefault().getInstance("org.netbeans.modules.hints");
    private static RequestProcessor HINTS_REQUEST_PROCESSOR = new RequestProcessor("Hints RP", 1);
    
    private JTextComponent        component;
    private boolean               isScheduled;
    private RequestProcessor.Task rescheduleTask;
//    private List                  hintsProviders;
    private int                   hintsType;
    
    private List<FileObject> toProcess;
    
    private int remainingWork;
    private int currentWork;
    private ProgressHandle handle;
    
    /** Creates a new instance of HintsOperator */
    private HintsOperator() {
        this.component = null;
        this.rescheduleTask = RequestProcessor.getDefault().create(new Runnable() {
            public void run() {
                JTextComponent component = null;
                
                synchronized (HintsOperator.this) {
                    component = HintsOperator.this.component;
                }
                
                if (component == null) {
                    return ;
                }
                
                DataObject od = (DataObject) component.getDocument().getProperty(Document.StreamDescriptionProperty);
                
                if (od != null) {
                    enqueue(od.getPrimaryFile());
                }
            }
        });
        
        this.rescheduleTask.setPriority(Thread.MIN_PRIORITY);
        
        HINTS_REQUEST_PROCESSOR.post(new HintPopupTaskImpl());
        
        this.isScheduled = false;
        
        this.toProcess = new LinkedList<FileObject>();
        
        Registry.addChangeListener(this);
    }
    
    private static HintsOperator instance = new HintsOperator();
    
    public static HintsOperator getDefault() {
        return instance;
    }
    
    public synchronized void caretUpdate(CaretEvent e) {
        if (!component.isFocusOwner()) {
            return;
        }
        int offset = component.getCaretPosition();

        if (isScheduled) {
            rescheduleTask.schedule(1000);
        }
    }
    
    private Object ENQUEUE_LOCK = new Object();
    
    public void enqueue(FileObject fo) {
        synchronized (ENQUEUE_LOCK) {
            toProcess.add(fo);
            ENQUEUE_LOCK.notifyAll();
        }
    }
    
    private FileObject pop() {
        synchronized (ENQUEUE_LOCK) {
            while (toProcess.isEmpty()) {
                try {
                    ENQUEUE_LOCK.wait();
                } catch (InterruptedException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                }
            }
            
            return toProcess.remove(0);
        }
    }
    
    public void startProgress() {
        synchronized (ENQUEUE_LOCK) {
            if (toProcess.isEmpty())
                return ;
            
            this.remainingWork = toProcess.size();
            this.currentWork = 0;
            
            handle = ProgressHandleFactory.createHandle("Parsing");
            
            handle.start(remainingWork);
        }
    }
    
    private class HintPopupTaskImpl implements Runnable {
        
        public HintPopupTaskImpl() {
        }
        
        public void run() {
            while (true) {
                FileObject file = pop();
                
                assert file != null;
                
                if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
                    ERR.log("processing=" + file);
                }
                
                if (handle != null) {
                    handle.progress("Parsing: " + FileUtil.getFileDisplayName(file));
                }
                
                try {
                    DataObject od = DataObject.find(file);
                    EditorCookie ec = (EditorCookie) od.getCookie(EditorCookie.class);
                    
                    if (ec != null) {
                        Document doc = ec.openDocument();
                        ErrorSupportUpToDateProviderFactory.getProvider(doc).setUpToDate(UpToDateStatus.UP_TO_DATE_PROCESSING);
                        
                        List<ErrorParserSupport> hintsProviders = getProvidersForDocument(doc);
                        List<ErrorDescription> result = new ArrayList<ErrorDescription>();
                        
                        if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
                            ERR.log("hintsProviders = " + hintsProviders );
                        }
                        
                        for (ErrorParserSupport parser : hintsProviders) {
                            if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
                                ERR.log("parser=" + parser);
                            }
                            
                            result.addAll(parser.parseForErrors(doc));
                        }
                        
                        HintsController.setErrors(doc, "<HintsOperator-errors>", result);
                        
                        ErrorSupportUpToDateProviderFactory.getProvider(doc).setUpToDate(UpToDateStatus.UP_TO_DATE_OK);
                    }
                    
                    if (handle != null) {
                        remainingWork--;
                        currentWork++;
                        
                        handle.progress(currentWork);
                        
                        if (remainingWork == 0) {
                            handle.finish();
                            handle = null;
                        }
                    }
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }
        }
        
    }
    
    public synchronized void stateChanged(ChangeEvent e) {
        JTextComponent active = Registry.getMostActiveComponent();
        
        if (component != active) {
            unregisterFromComponent();
            registerNewComponent(active);
        }
    }
    
    private synchronized void unregisterFromComponent() {
        if (component != null) {
            component.removeCaretListener(this);
            component.removeFocusListener(this);
            component.removePropertyChangeListener("document", this);
            component.getDocument().removeDocumentListener(this);
        }
        component = null;
    }
    
    private synchronized void registerNewComponent(JTextComponent c) {
        if (c == null)
            return ;
        
        rescheduleTask.cancel();
        this.component = c;
        
//        gatherProviders();
        
        component.addCaretListener(this);
        component.addFocusListener(this);
        component.addPropertyChangeListener("document", this);
        component.getDocument().addDocumentListener(this);
        ErrorSupportUpToDateProviderFactory.getProvider(component.getDocument()).setUpToDate(UpToDateStatus.UP_TO_DATE_DIRTY);
        rescheduleTask.schedule(500);
    }
    
    private static List<ErrorParserSupport> getProvidersForDocument(Document doc/*, BaseKit kit*/) {
        Object mimeTypeObj = doc.getProperty("mimeType");  //NOI18N
        String mimeType;
        
        if (mimeTypeObj instanceof String)
            mimeType = (String) mimeTypeObj;
        else {
//            if (kit == null) {
                return Collections.emptyList();
//            }
//            
//            mimeType = kit.getContentType();
        }

        if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
            DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
            
            ERR.log(ErrorManager.INFORMATIONAL, "HintsOperator.getProvidersForDocument: mimeType=" + mimeType);
            
            if (od != null)
                ERR.log(ErrorManager.INFORMATIONAL, "file's mime-type: " + FileUtil.getMIMEType(od.getPrimaryFile()));
        }
        
        Lookup lookup = MimeLookup.getLookup(MimePath.get(mimeType));
        
        List<ErrorParserSupport> providers = new ArrayList<ErrorParserSupport>(lookup.lookupAll(ErrorParserSupport.class));
        
        if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
            ERR.log(ErrorManager.INFORMATIONAL, "providers=" + providers);
        }
        
        return providers;
    }
    
    static boolean isSupported(Document doc) {
        return !getProvidersForDocument(doc).isEmpty();
    }

//    private void gatherProviders() {
//        hintsProviders = getProvidersForDocument(component.getDocument(), Utilities.getKit(component));
//    }
    
    public void focusGained (FocusEvent fe) {
        if (!HintsUI.getDefault().isActive()) {
            markDirty();
        }
    }
    
    public void focusLost (FocusEvent fe) {
        if (!rescheduleTask.isFinished()) {
            rescheduleTask.cancel();
        }
    }
    
    public void insertUpdate(DocumentEvent e) {
        markDirty();
    }

    public synchronized void removeUpdate(DocumentEvent e) {
        markDirty();
    }

    public void changedUpdate(DocumentEvent e) {
    }
    
    private synchronized void markDirty() {
        isScheduled = true;
        ErrorSupportUpToDateProviderFactory.getProvider(component.getDocument()).setUpToDate(UpToDateStatus.UP_TO_DATE_DIRTY);
        rescheduleTask.schedule(1000);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (component != null) {
            JTextComponent c = component;
            
            unregisterFromComponent();
            registerNewComponent(c);
        }
    }

}
