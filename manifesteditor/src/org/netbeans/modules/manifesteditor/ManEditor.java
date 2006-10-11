/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. 
 *
 * Portions Copyrighted 2006 Sun Microsystems, Inc.
 */
package org.netbeans.modules.manifesteditor;

import java.awt.EventQueue;
import java.awt.Image;
import java.beans.BeanInfo;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.Document;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.DataEditorSupport;
import org.openide.text.NbDocument;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.windows.TopComponent;

public class ManEditor extends DataEditorSupport 
implements OpenCookie, EditorCookie, EditCookie {
    private MultiViewDescription[] descriptions = {
        new Text(this), new Visual()
    };
    
    
    private ManEditor(ManDataObject obj) {
        super(obj, new MyEnv(obj));
    }
    
    
    public static ManEditor create(ManDataObject obj) {
        return new ManEditor(obj);
    }

    protected CloneableEditorSupport.Pane createPane() {
        return (CloneableEditorSupport.Pane)MultiViewFactory.createCloneableMultiView(descriptions, descriptions[0]);
    }

    
    private static final class MyEnv extends DataEditorSupport.Env {
        public MyEnv(ManDataObject obj) {
            super(obj);
        }
        
        protected FileObject getFile() {
            return super.getDataObject().getPrimaryFile();
        }

        protected FileLock takeLock() throws IOException {
            return ((ManDataObject)super.getDataObject()).getPrimaryEntry().takeLock();
        }
        
    }
    
    private static final class Text implements MultiViewDescription {
        private MyEd editor;
        private ManEditor support;
        
        public Text(ManEditor ed) {
            this.support = ed;
        }
        
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_ONLY_OPENED;
        }

        public String getDisplayName() {
            return "Text";
        }

        public Image getIcon() {
            return null;
        }

        public HelpCtx getHelpCtx() {
            return HelpCtx.DEFAULT_HELP;
        }

        public String preferredID() {
            return "text";
        }

        public MultiViewElement createElement() {
            return getEd();
        }
        
        private MyEd getEd() {
            assert EventQueue.isDispatchThread();
            if (editor == null) {
                editor = new MyEd(support);
            }
            return editor;
        }
        
    }
    private static final class Visual implements MultiViewDescription, MultiViewElement {
       // private Node node;
        
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_ONLY_OPENED;
        }

        public String getDisplayName() {
            return "Visual";
        }

        public Image getIcon() {
            return null;
        }

        public HelpCtx getHelpCtx() {
            return HelpCtx.DEFAULT_HELP;
        }

        public String preferredID() {
            return "visual";
        }

        public MultiViewElement createElement() {
            assert EventQueue.isDispatchThread();
            
            
            
            return this;
        }

        JButton b = new JButton ("Ahoj");
        public JComponent getVisualRepresentation() {
            return b;
        }

        JButton c = new JButton ("Ahoj");
        public JComponent getToolbarRepresentation() {
            return c;
        }

        public Action[] getActions() {
            return new Action[0];
        }

        public Lookup getLookup() {
            return Lookup.EMPTY;
        }

        public void componentOpened() {
        }

        public void componentClosed() {
        }

        public void componentShowing() {
        }

        public void componentHidden() {
        }

        public void componentActivated() {
        }

        public void componentDeactivated() {
        }

        public org.openide.awt.UndoRedo getUndoRedo() {
            return null;
        }

        public void setMultiViewCallback(MultiViewElementCallback callback) {
        }

        public CloseOperationState canCloseElement() {
            return CloseOperationState.STATE_OK;
        }
    }
    
    private static final class MyEd extends CloneableEditor 
    implements MultiViewElement, Runnable {
        private JComponent toolbar;
        private MultiViewElementCallback callback;
        
        public MyEd() {
        }
        
        MyEd(ManEditor ed) {
            super(ed);
        }
        
        public JComponent getVisualRepresentation() {
            return this;
        }

        public JComponent getToolbarRepresentation() {
            if (toolbar == null) {
                 JEditorPane pane = this.pane;
                 if (pane != null) {
                     Document doc = pane.getDocument();
                     if (doc instanceof NbDocument.CustomToolbar) {
                         toolbar = ((NbDocument.CustomToolbar)doc).createToolbar(pane);
                     }
                 }
                 if (toolbar == null) {
                     //attempt to create own toolbar?
                     toolbar = new JPanel();
                 }
             }
             return toolbar;
        }

        public void setMultiViewCallback(MultiViewElementCallback callback) {
            this.callback = callback;
            updateName();
        }
        
        public void componentOpened() {
            super.componentOpened();
        }

        public void componentClosed() {
            super.componentClosed();
        }

        public void componentShowing() {
            super.componentShowing();
        }

        public void componentHidden() {
            super.componentHidden();
        }

        public void componentActivated() {
            super.componentActivated();
        }

        public void componentDeactivated() {
            super.componentDeactivated();
        }

        public CloseOperationState canCloseElement() {
            return CloseOperationState.STATE_OK;
        }

        public void updateName() {
            Mutex.EVENT.readAccess(this);
        }
        
        public void run() {
            MultiViewElementCallback c = callback;
            if (c == null) {
                return;
            }
            TopComponent tc = c.getTopComponent();
            if (tc == null) {
                return;
            }
            
            super.updateName();
            tc.setName(this.getName());
            tc.setDisplayName(this.getDisplayName());
            tc.setHtmlDisplayName(this.getHtmlDisplayName());
        }
    }
}
