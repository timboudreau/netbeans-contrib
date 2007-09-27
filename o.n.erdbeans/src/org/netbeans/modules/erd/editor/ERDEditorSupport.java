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
package org.netbeans.modules.erd.editor;


import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeSupport;
import org.openide.cookies.*;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.DataEditorSupport;
import org.openide.windows.CloneableOpenSupport;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;
import java.io.*;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import org.netbeans.modules.erd.io.ERDDataObject;
import org.netbeans.modules.erd.model.DocumentSerializer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.text.CloneableEditorSupport.Pane;
import org.openide.text.NbDocument;
import org.openide.text.PrintSettings;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * @author uszaty
 */
public class ERDEditorSupport extends CloneableOpenSupport implements  OpenCookie, EditCookie, PrintCookie {
    
    private enum ShowingType {
        OPEN, EDIT
    }
    PropertyChangeSupport propertyChangeSupport;
    private ERDDataObject dataObject;
    private Save saveCookie = new Save();
    
    private TopComponent topComponent;
    private ShowingType showingType;
    /** Lock used for access to <code>printing</code> variable. */
    private final Object LOCK_PRINTING = new Object();
    /** Helper variable to prevent multiple cocurrent printing of this
     * instance. */
    private boolean printing;
    
    
    public ERDEditorSupport(ERDDataObject dataObject) {
        super( new Env(dataObject));
        this.dataObject = dataObject;
        
    }
    
    public void saveDocument() throws IOException {
        DocumentSerializer documentSerializer = dataObject.getDocumentSerializer();
        documentSerializer.waitDocumentLoaded();
        
        documentSerializer.saveDocument();
        notifyUnmodified();
        
    }
    
    public boolean notifyModified() {
        
        if (dataObject.getCookie(SaveCookie.class) == null) {
            dataObject.addSaveCookie(saveCookie);
            dataObject.setModified(true);
            updateDisplayName();
        }
        return true;
    }
    
    protected void notifyUnmodified() {
        
        SaveCookie save = (SaveCookie)dataObject.getCookie(SaveCookie.class);
        if (save != null) {
            dataObject.removeSaveCookie(save);
            dataObject.setModified(false);
            updateDisplayName();
        }
    }
    
    protected boolean notifyClosed() {
        boolean can=canClose();
        if(can){
            topComponent = null;
            //super.close();
            dataObject.notifyClosed();
        }
        return can;
    }
    
    public void open() {
        showingType = ShowingType.OPEN;
        super.open();
        
    }
    
    public void edit() {
        showingType = ShowingType.EDIT;
        super.open();
    }
    
    
    public void setMVTC(TopComponent tc) {
        this.topComponent = tc;
    }
    
    
    public void print() {
        
        synchronized (LOCK_PRINTING) {
            if (printing) {
                return;
            }
            
            printing = true;
        }
        

        
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            ERDTopComponent tc=(ERDTopComponent)topComponent;
            
            Printable o =new ERDPrintable(tc);
            
            
           PageFormat pf = PrintSettings.getPageFormat(job);
           job.setPrintable((Printable) o, pf);
           
            
            if (job.printDialog()) {
                job.print();
            }
        }  catch (PrinterAbortException e) { // user exception
            notifyProblem(e, "CTL_Printer_Abort"); // NOI18N
        }catch (PrinterException e) {
            notifyProblem(e, "EXC_Printer_Problem"); // NOI18N
        } finally {
            synchronized (LOCK_PRINTING) {
                printing = false;
            }
        }
    }
    
    
   static class ERDPrintable implements Printable {
        private ERDTopComponent mComponent;
        
        public ERDPrintable(ERDTopComponent c) {
            mComponent = c;
        }
        
        public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0)
                return NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            mComponent.print(g2);
            return PAGE_EXISTS;
        }
    }
    
    
    private static void notifyProblem(Exception e, String key) {
        String msg = NbBundle.getMessage(CloneableEditorSupport.class, key, e.getLocalizedMessage());
        Exceptions.attachLocalizedMessage(e, msg);
        DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Exception(e));
    }
    
    private synchronized PropertyChangeSupport getPropertyChangeSupport() {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        
        return propertyChangeSupport;
    }
    
    public final void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        getPropertyChangeSupport().addPropertyChangeListener(l);
    }
    
    /** Constructs message that should be used to name the editor component.
     *
     * @return name of the editor
     */
    protected String messageName() {
        if (! dataObject.isValid()) return ""; // NOI18N
        
        return addFlagsToName(dataObject.getNodeDelegate().getDisplayName());
    }
    
    protected String messageOpening() {
        return NbBundle.getMessage(DataObject.class , "CTL_ObjectOpen", // NOI18N
                dataObject.getPrimaryFile().getNameExt(),
                FileUtil.getFileDisplayName(dataObject.getPrimaryFile())
                );
    }
    
    
    
    private String addFlagsToName(String name) {
        int version = 3;
        if (isModified()) {
            if (!dataObject.getPrimaryFile().canWrite()) {
                version = 2;
            } else {
                version = 1;
            }
        } else {
            if (!dataObject.getPrimaryFile().canWrite()) {
                version = 0;
            }
        }
        
        return NbBundle.getMessage(DataObject.class, "LAB_EditorName",
                new Integer(version), name );
    }
    
    
    protected String messageOpened() {
        return NbBundle.getMessage(DataObject.class, "CTL_ObjectOpened", // NOI18N
                dataObject.getPrimaryFile().getNameExt(),
                FileUtil.getFileDisplayName(dataObject.getPrimaryFile())
                );
    }
    
    
    final Env cesEnv() {
        return (Env) env;
    }
    
    public boolean isModified() {
        return cesEnv().isModified();
    }
    
    protected CloneableTopComponent createCloneableTopComponent() {
        //CloneableTopComponent tc = super.createCloneableTopComponent();
        ERDTopComponent component=new ERDTopComponent(dataObject,this);
        
        this.topComponent = component;
        return (CloneableTopComponent)topComponent;
    }
    
    
    
    public final void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        getPropertyChangeSupport().removePropertyChangeListener(l);
    }
    
    
    protected String messageToolTip() {
        // update tooltip
        return FileUtil.getFileDisplayName(dataObject.getPrimaryFile());
    }
    
    public void updateDisplayName() {
        final TopComponent tc = topComponent;
        if (tc == null)
            return;
        
        Runnable run=new Runnable() {
            public void run() {
                String displayName = messageName();
                if (! displayName.equals(tc.getDisplayName()))
                    tc.setDisplayName(displayName);
                tc.setToolTipText(dataObject.getPrimaryFile().getPath());
            }
        };
        
        if (SwingUtilities.isEventDispatchThread())
            run.run();
        else
            SwingUtilities.invokeLater(run);
        
        
        
    }
    
    
    
    protected String messageSave() {
        return NbBundle.getMessage(
                DataObject.class,
                "MSG_SaveFile", // NOI18N
                dataObject.getPrimaryFile().getNameExt()
                );
    }
    
    private class Save implements SaveCookie {
        
        public void save() throws IOException {
            saveDocument();
            getDataObject().setModified(false);
        }
    }
    
    
    public DataObject getDataObject(){
        return dataObject;
    }
    
    public void openERD() {
        if (EventQueue.isDispatchThread()) {
            openERDInAWT();
        } else {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    openERDInAWT();
                }
            });
        }
    }
    
    private void openERDInAWT() {
        startLoadingDesign();
        
        openCloneableTopComponent();
        
    }
    
    public void startLoadingDesign() {
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                loadDesignData();
            }
        });
    }
    
    public void loadDesignData(){
        
    }
    
    
    
    public static class Env extends DataEditorSupport.Env /*implements Externalizable*/ {
        
        private static final long serialVersionUID = -1;
        
        public Env(DataObject obj) {
            super(obj);
        }
        
        public Env() {
            super(null);
        }
        
        protected FileObject getFile() {
            return getDataObject().getPrimaryFile();
        }
        
        protected FileLock takeLock() throws IOException {
            return ((ERDDataObject) getDataObject()).getPrimaryEntry().takeLock();
        }
        
        public CloneableOpenSupport findCloneableOpenSupport() {
            return (CloneableOpenSupport)getDataObject().getCookie(ERDEditorSupport.class);
        }
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            String path = (String) in.readObject();
            DataObject dataObject = DataObject.find(FileUtil.toFileObject(new File(path)));
            if (dataObject == null)
                return;
            OpenCookie cookie = (OpenCookie)dataObject.getCookie(OpenCookie.class);
            if (cookie != null)
                cookie.open();
        }
        
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(FileUtil.toFile(getDataObject().getPrimaryFile()).getPath());
        }
        
    }
    
    
}
