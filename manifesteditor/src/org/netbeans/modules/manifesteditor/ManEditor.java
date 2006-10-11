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
import java.io.IOException;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

public class ManEditor extends DataEditorSupport 
implements OpenCookie, EditorCookie, EditCookie {
    private MultiViewDescription[] descriptions = {
        new Text(), new Visual()
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
                editor = new MyEd();
            }
            return editor;
        }
        
    }
    private static final class Visual implements MultiViewDescription, MultiViewElement {
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
            return this;
        }

        JButton b = new JButton ("Ahoj");
        public JComponent getVisualRepresentation() {
            return b;
        }

        public JComponent getToolbarRepresentation() {
            return null;
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
    implements MultiViewElement {
        public JComponent getVisualRepresentation() {
            return this;
        }

        public JComponent getToolbarRepresentation() {
            return null;
        }

        public void setMultiViewCallback(MultiViewElementCallback callback) {
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
    }
}
