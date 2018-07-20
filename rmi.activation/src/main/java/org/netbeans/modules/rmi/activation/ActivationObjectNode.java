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

package org.netbeans.modules.rmi.activation;

import java.awt.Dialog;
import java.awt.Component;
import java.beans.*;
import java.rmi.activation.*;

import org.openide.*;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.InstanceCookie;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.WeakListener;
import org.openide.util.actions.SystemAction;

/**
 * Class that represents one ActivationObjectItem.
 * @author  Jan Pokorsky
 * @version 
 */
public final class ActivationObjectNode extends ActivationNode implements PropertyChangeListener {
    
    private static boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N
    
    private PropertyChangeListener listener;
    /** Dialog for customizer. */
    private Dialog dialog;
    
    public ActivationObjectNode(ActivationObjectItem item) {
        super(Children.LEAF, item);
        setName(item.getDesc().getClassName());
        setIconBase("org/netbeans/modules/rmi/resources/activationItem"); // NOI18N
        systemActions = new SystemAction[] {
            SystemAction.get(ActivateAction.class),
            SystemAction.get(InactivateAction.class),
            null,
            SystemAction.get(RegisterAction.class),
            null,
            /* CustomizeBeanAction is deprecated due to security restriction (granting
             * new code domain is not possible without restart of ide yet). */
//            SystemAction.get(org.openide.actions.CustomizeBeanAction.class),
//            null,
//            SystemAction.get(org.openide.actions.CutAction.class),
//            SystemAction.get(org.openide.actions.CopyAction.class),
//            null,
            SystemAction.get(GenerateSetupAction.class),
            null,
            SystemAction.get(org.openide.actions.DeleteAction.class),
            null,
            SystemAction.get(org.openide.actions.ToolsAction.class),
            null,
            SystemAction.get(org.openide.actions.PropertiesAction.class),
        };
        
        setDefaultAction(SystemAction.get(org.openide.actions.CustomizeAction.class));
//        CookieSet cookies = getCookieSet();
//        cookies.add(new RemoteInstance());
//        cookies.add(this);
        
        this.listener = WeakListener.propertyChange(this, item);
        item.addPropertyChangeListener(listener);
    }

    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        setSheetImpl(sheet);
        return sheet;
    }
    
    private void setSheetImpl(Sheet sheet) {
        Sheet.Set props = sheet.get(Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet();
            sheet.put(props);
        }

        try {
            ActivationDesc desc = ((ActivationObjectItem) getItem()).getDesc();
            java.util.ResourceBundle bundle = NbBundle.getBundle(ActivationObjectNode.class);
            
            Node.Property np = new PropertySupport.Reflection(desc, String.class, "getClassName", null); // NOI18N
            np.setName("className"); // NOI18N
            np.setDisplayName(bundle.getString("PROP_ClassName")); // NOI18N
            np.setShortDescription(bundle.getString("HINT_ClassName")); // NOI18N
            props.put(np);
            
            np = new PropertySupport.Reflection(desc, String.class, "getLocation", null); // NOI18N
            np.setName("location"); // NOI18N
            np.setDisplayName(bundle.getString("PROP_Location")); // NOI18N
            np.setShortDescription(bundle.getString("HINT_Location")); // NOI18N
            props.put(np);
            
            np = new PropertySupport.Reflection(desc, Boolean.TYPE, "getRestartMode", null); // NOI18N
            np.setName("restartMode"); // NOI18N
            np.setDisplayName(bundle.getString("PROP_RestartMode")); // NOI18N
            np.setShortDescription(bundle.getString("HINT_RestartMode")); // NOI18N
            props.put(np);
            
            np = new PropertySupport.Reflection(desc, java.rmi.MarshalledObject.class, "getData", null); // NOI18N
            np.setName("data"); // NOI18N
            np.setDisplayName(bundle.getString("PROP_Data")); // NOI18N
            np.setShortDescription(bundle.getString("HINT_Data")); // NOI18N
            props.put(np);
            
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
    }
    
    private void update() {
        setDisplayName(((ActivationObjectItem) getItem()).getDesc().getClassName());
        Sheet sheet = getSheet();
        setSheetImpl(sheet);
        setSheet(sheet);
    }

    
    public void propertyChange(java.beans.PropertyChangeEvent pce) {
        if (ActivationObjectItem.PROP_DESCRIPTOR.equals(pce.getPropertyName())) {
            update();
        }
    }
    
    /** Does a property editor of ActivationGroupDesc support a custom editor? */
    public boolean hasCustomizer() {
        PropertyEditor pe = PropertyEditorManager.findEditor(ActivationDesc.class);
        if (pe == null)
            return false;
        else
            return pe.supportsCustomEditor();
    }
    
    /** Returns the custom editor inside a dialog which can set new activation
     * group descriptor in particular activation system.
     * @return <code>java.awt.Dialog</code>
     */
    public java.awt.Component getCustomizer() {
        PropertyEditor pe = PropertyEditorManager.findEditor(ActivationDesc.class);
        Component panel = null;
        if (pe != null && pe.supportsCustomEditor()) {
            pe.setValue(((ActivationObjectItem) getItem()).getDesc());
            panel = pe.getCustomEditor();
        }

        // is it proper editor?
        if (!(panel instanceof EnhancedCustomPropertyEditor)) 
            return null;

        final EnhancedCustomPropertyEditor editor = (EnhancedCustomPropertyEditor) panel;
        // create customizer dialog
        dialog = DialogDisplayer.getDefault().createDialog(
            new DialogDescriptor(
                panel,
                getString("LBL_ChngObjDesc"), // NOI18N
                true,
                DialogDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN,
                new HelpCtx(ActivationObjectNode.class),
                new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent ae) {
                        if (ae.getSource() == DialogDescriptor.OK_OPTION) {
                            // use the reqest processor for blocking operation
                            getItem().getActivationSystemItem().getRP().postRequest(new Runnable() {
                                public void run() {
                                    try {
                                        ActivationDesc desc = (ActivationDesc) editor.getPropertyValue();
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngObjDesc", "MSG_ChngObjDescStart")); // NOI18N
                                        ((ActivationObjectItem) getItem()).modifyDesc(desc);
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngObjDesc", "MSG_ChngObjDescSuccessful")); // NOI18N
                                    } catch (IllegalStateException ex) {
                                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_PropertyObjDesc"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                                    } catch (UnknownGroupException ex) {
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngObjDesc", "MSG_ChngObjDescFailed")); // NOI18N
                                        getItem().getActivationSystemItem().updateActivationItems();
                                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_NewObjGroup"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                                    } catch (UnknownObjectException ex) {
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngObjDesc", "MSG_ChngObjDescFailed")); // NOI18N
                                        getItem().getActivationSystemItem().updateActivationItems();
                                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_UnregisteredObj"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                                    } catch (java.rmi.ConnectException ex) {
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngObjDesc", "MSG_ChngObjDescFailed")); // NOI18N
                                        getItem().getActivationSystemItem().updateActivationItems();
                                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_ConnectException"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                                    } catch (ActivationException ex) {
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngObjDesc", "MSG_ChngObjDescFailed")); // NOI18N
                                        getItem().getActivationSystemItem().updateActivationItems();
                                        ErrorManager em = RMIModule.getErrorManager(this.getClass());
                                        em.annotate(ex, getString("FMT_ChngObjDesc", "ERR_ChngObjDesc")); // NOI18N
                                        em.notify(ex);
                                    } catch (java.rmi.RemoteException ex) {
                                        //ActivationException, RemoteException
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngObjDesc", "MSG_ChngObjDescFailed")); // NOI18N
                                        getItem().getActivationSystemItem().updateActivationItems();
                                        ErrorManager em = RMIModule.getErrorManager(this.getClass());
                                        em.annotate(ex, getString("FMT_ChngObjDesc", "ERR_ChngObjDesc")); // NOI18N
                                        em.notify(ex);
                                    }
                                }
                            });
                        }
                    }
                }
            )
        );
        return dialog;
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (ActivationObjectNode.class.getName());
    }
    
    /** Instance of the remote reference to the activatable object. It used for serialization.*/
/*    public class RemoteInstance implements InstanceCookie {
        
        public Object instanceCreate() throws java.io.IOException, ClassNotFoundException {
            ActivationObjectItem item = (ActivationObjectItem) getItem();
            try {
                Object obj = item.getActivationSystemItem().getStub(item.getActivationID());
//                System.err.println(obj.getClass().getDeclaredMethod("getServiceName", null).invoke(obj, null));
                return obj;
            } catch (java.rmi.ServerException sex) {
                if (debug) sex.printStackTrace();
                if (sex.detail instanceof java.io.IOException) throw (java.io.IOException) sex.detail;
                else throw sex;
            } catch (java.rmi.StubNotFoundException ex) {
                if (debug) ex.printStackTrace();
                throw ex;
            } catch (Exception ex) {
                if (debug) ex.printStackTrace();
                throw new java.rmi.StubNotFoundException(null, ex);
            }
        }
        
        public java.lang.String instanceName() {
            return ((ActivationObjectItem) getItem()).getDesc().getClassName();
        }

        public java.lang.Class instanceClass() throws java.io.IOException, java.lang.ClassNotFoundException {
            return java.rmi.Remote.class;
        }
    }
 */
}
