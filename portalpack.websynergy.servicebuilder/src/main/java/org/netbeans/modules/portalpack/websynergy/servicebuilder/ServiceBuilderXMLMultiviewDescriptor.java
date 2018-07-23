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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.portalpack.websynergy.servicebuilder;

import java.awt.Image;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.Document;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.api.ServiceBuilderEditorContext;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.loader.ServiceBuilderDataObject;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.CloneableEditor;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;

/*
 * @author Petr Pisl
 */

public class ServiceBuilderXMLMultiviewDescriptor implements MultiViewDescription, Serializable{
    static final long serialVersionUID = -6305897237371751564L;
    private final static String XML_CONSTANT = "XML"; //NOI18N
    
    private ServiceBuilderEditorContext context;
    
    /** Creates a new instance of StrutsConfigMultiviewDescriptor */
    public ServiceBuilderXMLMultiviewDescriptor(ServiceBuilderEditorContext context) {
        this.context = context;
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }
    
    public String getDisplayName() {
        return XML_CONSTANT;
    }
    
    private static final Image serviceBuilderICON = org.openide.util.Utilities.loadImage("org/netbeans/modules/portalpack/websynergy/servicebuilder/resources/images/icon.png"); // NOI18N
    public Image getIcon() {
        return serviceBuilderICON;
    }
    
    public HelpCtx getHelpCtx() {
        return null;
    }
    
    public String preferredID() {
        return XML_CONSTANT;
    }
    
    public MultiViewElement createElement() {
        MultiViewElement element = null;
        try {
            DataObject dObject = DataObject.find(context.getServiceBuilderFile());
            ServiceBuilderDataObject jsfDataObject = (ServiceBuilderDataObject) dObject;
            element =  new ServiceBuilderXMLMultiviewElement(context, jsfDataObject.getEditorSupport());
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return element;
    }
    
    
    
    class ServiceBuilderXMLMultiviewElement extends CloneableEditor implements MultiViewElement, Serializable {
        static final long serialVersionUID = -6305897237371751564L;
        
        private ServiceBuilderEditorContext context;
        private transient JComponent toolbar;
        private transient ServiceBuilderDataObject sbDataObject;
        
        public ServiceBuilderXMLMultiviewElement(ServiceBuilderEditorContext context, ServiceBuilderEditorSupport support) {
            super(support);
            support.initializeCloneableEditor(this);
            this.context = context;
            init();
        }
        
        private void init() {            
            try {
                DataObject dObject = DataObject.find(context.getServiceBuilderFile());

                sbDataObject = (ServiceBuilderDataObject) dObject;
            }
            catch (DataObjectNotFoundException ex) {
                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE,
                                                                 ex.getMessage(),
                                                                 ex);
            }
        }
        
        public JComponent getVisualRepresentation() {
            return this;
        }
        
        public JComponent getToolbarRepresentation() {
            if (toolbar == null) {
                JEditorPane editorPane = getEditorPane();
                if (editorPane != null) {
                    Document doc = editorPane.getDocument();
                    if (doc instanceof NbDocument.CustomToolbar)
                        toolbar = ((NbDocument.CustomToolbar) doc).createToolbar(editorPane);
                }
                if (toolbar == null)
                    toolbar = new JPanel();
            }
            return toolbar;
        }
        
        @Override
        public void componentOpened() {
            super.componentOpened();
        }
        
        @Override
        public void componentClosed() {
            super.componentClosed();
        }
        
        @Override
        public void componentShowing() {
            super.componentShowing();
        }
        
        @Override
        public void componentHidden() {
             super.componentHidden();
        }
        
        @Override
        public void componentActivated() {
            super.componentActivated();
        }
        
        @Override
        public void componentDeactivated() {
            super.componentDeactivated();
        }
        
        @Override
        public UndoRedo getUndoRedo() {
            return super.getUndoRedo();
        }
        
        public void setMultiViewCallback(MultiViewElementCallback callback) {
            context.setMultiViewTopComponent(callback.getTopComponent());
        }
        
        public CloseOperationState canCloseElement() {
            // the savin operation is handled by CloseHander form JSFCOnfigEditorSuport
            return MultiViewFactory.createUnsafeCloseState("ID_FACES_CONFIG_CLOSING", MultiViewFactory.NOOP_CLOSE_ACTION, MultiViewFactory.NOOP_CLOSE_ACTION);
        }
        
        @Override
        public javax.swing.Action[] getActions() {
            return super.getActions();
        }
    }
}
