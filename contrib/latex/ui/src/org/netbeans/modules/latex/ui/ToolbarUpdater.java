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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source.Priority;
import org.netbeans.napi.gsfret.source.support.CaretAwareSourceTaskFactory;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.Node;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author Jan Lahoda
 */
public class ToolbarUpdater extends CaretAwareSourceTaskFactory {

    public ToolbarUpdater() {
        super(Phase.RESOLVED, Priority.ABOVE_NORMAL);
    }
    protected CancellableTask<CompilationInfo> createTask(FileObject file) {
        return new UpdatingTask(file);
    }

    @Override
    public List<FileObject> getFileObjects() {
        List<FileObject> result = super.getFileObjects();
        
        if (result.isEmpty()) {
            fireToolbarEnableChange(false);
        }
        
        return result;
    }
    
    private static final class UpdatingTask implements CancellableTask<CompilationInfo> {

        private FileObject file;
        
        public UpdatingTask(FileObject file) {
            this.file = file;
        }
        
        public void cancel() {
        }

        private Document getDocument() {
            try {
                DataObject od = DataObject.find(file);
                EditorCookie ec = od.getLookup().lookup(EditorCookie.class);

                if (ec == null)
                    return null;

                return ec.getDocument();
            } catch (DataObjectNotFoundException donfe) {
                Logger.getLogger(ToolbarUpdater.class.getName()).log(Level.FINE, null, donfe);
                return null;
            }
        }
        
        public void run(CompilationInfo parameter) throws Exception {
            LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
            Document doc = getDocument();
            Node node = lpr.getCommandUtilities().findNode(doc, CaretAwareSourceTaskFactory.getLastPosition(file));
            
            if (node == null) {
                fireToolbarEnableChange(false);
                
                return ;
            }
            
            List<Reference<ToolbarUpdatable>> toUpdate = new LinkedList<Reference<ToolbarUpdatable>>();
            synchronized (ToolbarUpdater.class) {
                toUpdate.addAll(ToolbarUpdater.toUpdate);
            }
            
            for (Reference<ToolbarUpdatable> r : toUpdate) {
                ToolbarUpdatable t = r.get();
                
                if (t != null) {
                    t.update(lpr);
                }
            }
            
            fireToolbarStatusChange(node);
            fireToolbarEnableChange(true);
        }
        
    }
    
    public static interface ToolbarUpdatable {
        public void update(LaTeXParserResult lpr);
    }
    
    private static List<ToolbarStatusChangeListener> listeners = new LinkedList<ToolbarStatusChangeListener>();
    private static List<Reference<ToolbarUpdatable>> toUpdate = new LinkedList<Reference<ToolbarUpdater.ToolbarUpdatable>>();
    
    protected static void fireToolbarStatusChange(Node node) {
        ToolbarStatusChangeListener[] listnrs = null;
        
        synchronized (ToolbarUpdater.class) {
            listnrs = (ToolbarStatusChangeListener[] ) listeners.toArray(new ToolbarStatusChangeListener[0]);
        }
        
        for (int cntr = 0; cntr < listnrs.length; cntr++) {
            listnrs[cntr].statusChange(node);
        }
    }
    
    protected static void fireToolbarEnableChange(boolean enable) {
        ToolbarStatusChangeListener[] listnrs = null;
        
        synchronized (ToolbarUpdater.class) {
            listnrs = (ToolbarStatusChangeListener[] ) listeners.toArray(new ToolbarStatusChangeListener[0]);
        }
        
        for (int cntr = 0; cntr < listnrs.length; cntr++) {
            listnrs[cntr].enableChange(enable);
        }
    }
    
    public static synchronized void addToolbarStatusChangeListener(ToolbarStatusChangeListener l) {
        listeners.add(l);
    }
    
    public static synchronized void removeToolbarStatusChangeListener(ToolbarStatusChangeListener l) {
        listeners.remove(l);
    }
    
    public static synchronized void addToUpdate(ToolbarUpdatable u) {
        toUpdate.add(new WeakReference<ToolbarUpdatable>(u));
    }
    
    public static synchronized void removeToUpdate(ToolbarUpdatable u) {
        for (Reference<ToolbarUpdatable> r : toUpdate) {
            if (r.get() == u) {
                toUpdate.remove(r);
                return ;
            }
        }
    }
    
}
//        
//        
//        implements CaretListener, PropertyChangeListener, DocumentChangedListener {
//    
//    private Reference   currentNode;
//    private LaTeXSource source;
//    private JEditorPane currentPane;
//    
//    
//    private static ToolbarUpdater instance = null;
//    
//    public static synchronized ToolbarUpdater getDefault() {
//        if (instance == null) {
//            instance = new ToolbarUpdater();
//        }
//        
//        return instance;
//    }
//    
//    protected static synchronized void destroy() {
//        TopComponent.getRegistry().removePropertyChangeListener(instance);
//        instance = null;
//    }
//    
//    /** Creates a new instance of ToolbarUpdater */
//    protected ToolbarUpdater() {
//        listeners = new ArrayList();
//        
//        TopComponent.getRegistry().addPropertyChangeListener(this);
//        setup();
//    }
//    
//    private Node getCurrentNodeImpl() {
//        if (currentNode == null)
//            return null;
//        
//        Node node = (Node) currentNode.get();
//        
//        if (node == null)
//            return null;
//        
//        if (source.getDocument() != node.getDocumentNode())
//            return null;
//        
//        return node;
//    }
//    
//    public void nodesAdded(DocumentChangeEvent evt) {
//        heavyUpdate();
//    }
//    
//    public void nodesChanged(DocumentChangeEvent evt) {
//        heavyUpdate();
//    }
//    
//    public void nodesRemoved(DocumentChangeEvent evt) {
//        heavyUpdate();
//    }
//    
//    private RequestProcessor.Task updateTask = null;
//    
//    private synchronized void prepareUpdateTask(Runnable r) {
//        if (updateTask != null) {
//            updateTask.cancel();
//            updateTask = null;
//        }
//        
//        updateTask = RequestProcessor.getDefault().post(r, 200);
//    }
//    
//    public void caretUpdate(CaretEvent e) {
//        prepareUpdateTask(new Runnable() {
//            public void run() {
//                if (currentPane == null)
//                    return ;
//                
//                LaTeXSource.Lock lock        = null;
//                boolean          heavyUpdate = false;
//                
//                try {
//                    lock = source.lock(false);
//                    if (lock != null) {
//                        Node node = getCurrentNodeImpl();
//                        
//                        if (node == null) {
//                            heavyUpdate = true;
//                        } else {
//                            Document doc = currentPane.getDocument();
//                            
//                            heavyUpdate = !node.contains(new SourcePosition(Utilities.getDefault().getFile(doc), doc, /*e.getDot()*/currentPane.getCaret().getDot()));
//                        }
//                    } else {
//                        //no update in this case...
//                    }
//                } finally {
//                    if (lock != null)
//                        source.unlock(lock);
//                }
//                
//                if (heavyUpdate)
//                    heavyUpdate();
//            }
//        });
//    }
//    
//    public void heavyUpdate() {
//        LaTeXSource.Lock lock   = null;
//        boolean          enable = false;
//        
//        try {
//            lock = source.lock(false);
//            
//            if (lock != null) {
//                Node node = source.findNode(currentPane.getDocument(), currentPane.getCaret().getDot());
//                
//                if (node == null) {
//                    fireToolbarEnableChange(false);
//                    
//                    return ;
//                }
//                
//                currentNode = new WeakReference(node);
//                
//                enable = true;
//                fireToolbarStatusChange(node);
//            }
//        } catch (IOException e) {
//            //cannot find ;-(
//            ErrorManager.getDefault().notify(e);
//        } finally {
//            if (lock != null)
//                source.unlock(lock);
//        }
//        
//        fireToolbarEnableChange(enable);
//    }
//    
//    
//    public void propertyChange(PropertyChangeEvent evt) {
//        setup();
//    }
//    
//    private synchronized void setup() {
//        if (currentPane != null)
//            currentPane.removeCaretListener(this);
//        
//        if (source != null)
//            source.removeDocumentChangedListener(this);
//        
//        currentPane = UIUtilities.getCurrentEditorPane();
//        
//        if (currentPane == null) {
//            fireToolbarEnableChange(false);
//            return ;
//        }
//        
//        if (currentPane.getEditorKit().getContentType() != "text/x-tex") {
//            currentPane = null;
//            fireToolbarEnableChange(false);
//            return ;
//        }
//        
//        try {
//            source      = LaTeXSource.get(Utilities.getDefault().getFile(currentPane.getDocument()));
//        } catch (LaTeXSource.UnsupportedFileTypeException e) {
//            //ehm. nothing.
//            //...
//            fireToolbarEnableChange(false);
//            return ;
//        }
//        
//        currentPane.addCaretListener(this);
//        
//        if (source != null) //!!!
//            source.addDocumentChangedListener(this);
//    }
//    
//}
