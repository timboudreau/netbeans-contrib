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
 * The Original Software is NetBeans.
 *
 * Portions Copyrighted 2006 Sun Microsystems, Inc.
 */
package org.netbeans.modules.manifesteditor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.awt.Mnemonics;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.explorer.view.ChoiceView;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.DataEditorSupport;
import org.openide.text.NbDocument;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

public class ManEditor extends DataEditorSupport 
implements OpenCookie, EditorCookie, EditCookie {
    private static Logger LOG = Logger.getLogger(ManEditor.class.getName());
    
    final MultiViewDescription[] descriptions = {
        new Text(this), new Visual(this)
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

    protected boolean notifyModified() {
        boolean retValue;
        retValue = super.notifyModified();
        if (retValue) {
            ManDataObject obj = (ManDataObject)getDataObject();
            obj.ic.add(env);
        }
        return retValue;
    }

    protected void notifyUnmodified() {
        super.notifyUnmodified();
        
        ManDataObject obj = (ManDataObject)getDataObject();
        obj.ic.remove(env);
    }

    
    private static final class MyEnv extends DataEditorSupport.Env 
    implements SaveCookie {
        public MyEnv(ManDataObject obj) {
            super(obj);
        }
        
        public void save() throws IOException {
            ManEditor ed = (ManEditor)this.findCloneableOpenSupport();
            ed.saveDocument();
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
    static final class Visual extends JPanel
    implements MultiViewDescription, MultiViewElement, ExplorerManager.Provider,
    PropertyChangeListener, DocumentListener, ManNode.ChangeCallback {
        private ExplorerManager em;
        private ManEditor support;
        private PropertySheet sheet;
        
        public Visual(ManEditor ed) {
            support = ed;
        }
        
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

            em = new ExplorerManager();
            em.addPropertyChangeListener(this);
            
            ChoiceView view = new ChoiceView();
            this.setLayout(new BorderLayout());
            JLabel l = new JLabel();
            Mnemonics.setLocalizedText(l, NbBundle.getMessage(ManEditor.class, "LBL_Sections"));
            l.setLabelFor(view);
            l.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            this.add(BorderLayout.WEST, l);
            this.add(BorderLayout.CENTER, view);
            sheet = new PropertySheet();
            try {
                Manifest mf = new Manifest(support.getInputStream());
                em.setRootContext(ManNode.createManifestModel(mf, this));
                em.setSelectedNodes(new Node[] { em.getRootContext().getChildren().getNodes(true)[0] });
                support.openDocument().addDocumentListener(this);
            } catch (Exception ex) {
                LOG.log(Level.INFO, null, ex);
            }
            
            return this;
        }

        public JComponent getVisualRepresentation() {
            return sheet;
        }

        public JComponent getToolbarRepresentation() {
            return this;
        }

        public Action[] getActions() {
            return support.getDataObject().getNodeDelegate().getActions(false);
        }

        public Lookup getLookup() {
            return ((ManDataObject)support.getDataObject()).getNodeDelegate().getLookup();
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

        public ExplorerManager getExplorerManager() {
            return em;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            sheet.setNodes(em.getSelectedNodes());
        }

        public void insertUpdate(DocumentEvent e) {
            refresh();
        }

        public void removeUpdate(DocumentEvent e) {
            refresh();
        }

        public void changedUpdate(DocumentEvent e) {
            refresh();
        }
        
        public void refresh() {
            try {
                Manifest mf = new Manifest(support.getInputStream());
                ManNode.refresh(em.getRootContext(), mf);
            } catch (Exception ex) {
            }
        }

        public void change(
            final String section, final String name, final String oldValue, final String newValue
        ) throws IllegalArgumentException {
            final StyledDocument d = support.getDocument();
            if (d == null) {
                throw new IllegalArgumentException("document is null");
            }
            
            
            class Ch implements Runnable {
                public void run() {
                    try {
                        String text = d.getText(0, d.getLength());

                        int beginSection;
                        int endSection;

                        if ("Main".equals(section)) {
                            beginSection = 0;
                        } else {
                            Matcher m = Pattern.compile("Name:.*" + section).matcher(text);
                            if (!m.find()) {
                                throw new IllegalArgumentException("Section not found");
                            }
                            beginSection = m.start();
                        }
                        endSection = text.indexOf("\n\n", beginSection);

                        if (endSection == -1) {
                            endSection = d.getLength();
                        }

                        String sec = text.substring(beginSection, endSection);
                        Matcher line = Pattern.compile(name + ".*:.*" + oldValue).matcher(sec);
                        if (!line.find()) {
                            throw new IllegalArgumentException("Value definition not found in: " + sec);
                        }

                        int offset = beginSection + line.start();
                        d.remove(offset, line.end() - line.start());
                        d.insertString(offset, name + ": " + newValue, null);
                    } catch (BadLocationException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                }
            }
            
            
            try {
                Ch ch = new Ch();
                d.removeDocumentListener(this);
                NbDocument.runAtomic(d, ch);
                refresh();
            } finally {
                d.addDocumentListener(this);
            }
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

        public Lookup getLookup() {
            return ((ManDataObject)((ManEditor)cloneableEditorSupport()).getDataObject()).getNodeDelegate().getLookup();
        }
    }
}
