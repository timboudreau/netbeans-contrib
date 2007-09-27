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
import java.rmi.*;
import java.rmi.activation.*;
import java.util.Properties;

import org.openide.*;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.WeakListener;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;

/**
 * Class that represents one ActivationGroupItem.
 * @author  Jan Pokorsky
 * @version 
 */
public final class ActivationGroupNode extends ActivationNode implements PropertyChangeListener {
    
    private PropertyChangeListener listener;
    /** Dialog for customizer. */
    private Dialog dialog;
    
    public ActivationGroupNode(ActivationGroupItem item) {
        super(new ActivationGroupChildren(item), item);
        setDisplayName(item.getDesc().getClassName());
        setIconBase("org/netbeans/modules/rmi/resources/activationGroup"); // NOI18N
        systemActions = new SystemAction[] {
            SystemAction.get(org.openide.actions.NewAction.class),
            null,
//            SystemAction.get(org.openide.actions.CutAction.class),
//            SystemAction.get(org.openide.actions.CopyAction.class),
//            SystemAction.get(org.openide.actions.PasteAction.class),
//            null,
            SystemAction.get(GenerateSetupAction.class),
            null,
            SystemAction.get(InactivateAction.class),
            null,
            SystemAction.get(org.openide.actions.DeleteAction.class),
            null,
            SystemAction.get(org.openide.actions.ToolsAction.class),
            null,
            SystemAction.get(org.openide.actions.PropertiesAction.class),
        };
        
        setDefaultAction(SystemAction.get(org.openide.actions.CustomizeAction.class));
        
        this.listener = WeakListener.propertyChange(this, item);
        item.addPropertyChangeListener(listener);
//        CookieSet cookies = getCookieSet();
//        cookies.add(this);
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
            ActivationGroupDesc desc = ((ActivationGroupItem) getItem()).getDesc();

            Node.Property np = new PropertySupport.Reflection(desc, String.class, "getClassName", null); // NOI18N
            np.setName("className"); // NOI18N
            np.setDisplayName(getString("PROP_ClassName")); // NOI18N
            np.setShortDescription(getString("HINT_ClassName")); // NOI18N
            props.put(np);

            np = new PropertySupport.Reflection(desc, String.class, "getLocation", null); // NOI18N
            np.setName("location"); // NOI18N
            np.setDisplayName(getString("PROP_Location")); // NOI18N
            np.setShortDescription(getString("HINT_Location")); // NOI18N
            props.put(np);

            ActivationGroupDesc.CommandEnvironment comEnv = desc.getCommandEnvironment();
            if (comEnv == null) comEnv = new ActivationGroupDesc.CommandEnvironment(null, null);
            np = new PropertySupport.Reflection(comEnv, String.class, "getCommandPath", null); // NOI18N
            np.setName("commandPath"); // NOI18N
            np.setDisplayName(getString("PROP_CommandPath")); // NOI18N
            np.setShortDescription(getString("HINT_CommandPath")); // NOI18N
            props.put(np);

            np = new PropertySupport.Reflection(comEnv, String[].class, "getCommandOptions", null); // NOI18N
            np.setName("commandEnvironment"); // NOI18N
            np.setDisplayName(getString("PROP_CommandEnvironment")); // NOI18N
            np.setShortDescription(getString("HINT_CommandEnvironment")); // NOI18N
            props.put(np);

            np = new PropertySupport.Reflection(desc, Properties.class, "getPropertyOverrides", null); // NOI18N
            np.setName("propertyOverrides"); // NOI18N
            np.setDisplayName(getString("PROP_PropertyOverrides")); // NOI18N
            np.setShortDescription(getString("HINT_PropertyOverrides")); // NOI18N
            props.put(np);

            np = new PropertySupport.Reflection(desc, java.rmi.MarshalledObject.class, "getData", null); // NOI18N
            np.setName("data"); // NOI18N
            np.setDisplayName(getString("PROP_Data")); // NOI18N
            np.setShortDescription(getString("HINT_Data")); // NOI18N
            props.put(np);
            
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
    }
    
    /** Updates the display name and the sheet set when descriptor is changed. */
    private void update() {
        setDisplayName(((ActivationGroupItem) getItem()).getDesc().getClassName());
        Sheet sheet = getSheet();
        setSheetImpl(sheet);
        setSheet(sheet);
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent pce) {
        if (ActivationGroupItem.PROP_DESCRIPTOR.equals(pce.getPropertyName())) {
            update();
        }
    }
    
    public void setDisplayName(String name) {
        if (name == null) name = getString("LBL_ActivationGroupNodeDefault"); // NOI18N
        super.setDisplayName(name);
    }
    
    /** Does a property editor of ActivationGroupDesc support a custom editor? */
    public boolean hasCustomizer() {
        PropertyEditor pe = PropertyEditorManager.findEditor(ActivationGroupDesc.class);
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
        PropertyEditor pe = PropertyEditorManager.findEditor(ActivationGroupDesc.class);
        Component panel = null;
        if (pe != null && pe.supportsCustomEditor()) {
            pe.setValue(((ActivationGroupItem) getItem()).getDesc());
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
                getString("LBL_ChngGroupDesc"), // NOI18N
                true,
                DialogDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN,
                new HelpCtx(ActivationGroupNode.class),
                new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent ae) {
                        if (ae.getSource() == DialogDescriptor.OK_OPTION) {
                            // use the reqest processor for blocking operation
                            getItem().getActivationSystemItem().getRP().postRequest(new Runnable() {
                                public void run() {
                                    try {
                                        ActivationGroupDesc desc = (ActivationGroupDesc) editor.getPropertyValue();
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngGroupDesc", "MSG_ChngGroupDescStart")); // NOI18N
                                        ((ActivationGroupItem) getItem()).modifyDesc(desc);
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngGroupDesc", "MSG_ChngGroupDescSuccessful")); // NOI18N
                                    } catch (java.rmi.ConnectException ex) {
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngGroupDesc", "MSG_ChngGroupDescFailed")); // NOI18N
                                        getItem().getActivationSystemItem().updateActivationItems();
                                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_ConnectException"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                                    } catch (IllegalStateException ex) {
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngGroupDesc", "MSG_ChngGroupDescFailed")); // NOI18N
                                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_PropertyGroupDesc"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                                    } catch (UnknownGroupException ex) {
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngGroupDesc", "MSG_ChngGroupDescFailed")); // NOI18N
                                        getItem().getActivationSystemItem().updateActivationItems();
                                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_UnregisteredGroup"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                                    } catch (Exception ex) {
                                        //ActivationException, RemoteException
                                        StatusDisplayer.getDefault().setStatusText(getString("FMT_ChngGroupDesc", "MSG_ChngGroupDescFailed")); // NOI18N
                                        getItem().getActivationSystemItem().updateActivationItems();
                                        ErrorManager em = ActivationModule.getErrorManager(this.getClass());
                                        em.annotate(ex, getString("FMT_ChngGroupDesc", "ERR_ChngGroupDesc")); // NOI18N
                                        em.notify(ex);
                                    }
                                }
                            });
                        }
                        dialog.dispose();
                        dialog = null;
                    }
                }
            )
        );
        return dialog;
    }
    
    public NewType[] getNewTypes() {
        return new NewType[] { new ActivationObjectType() };
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx ( ActivationGroupNode.class.getName());
    }
    
    /** Describes Activation Object as a new type that can be created in Activation
     * Group node. */
    class ActivationObjectType extends NewType implements java.awt.event.ActionListener {
        private EnhancedCustomPropertyEditor editor;
        private Dialog dialog;
        
        public void create() throws java.io.IOException {
            PropertyEditor pe = PropertyEditorManager.findEditor(ActivationDesc.class);
            Component panel = null;
            if (pe != null && pe.supportsCustomEditor()) {
                pe.setValue(new ActivationDesc(((ActivationGroupItem) getItem()).getActivationGroupID(), null, null, null));
                panel = pe.getCustomEditor();
            }
            
            if (!(panel instanceof EnhancedCustomPropertyEditor)) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_CustomEditor"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                return;
            }
            
            editor = (EnhancedCustomPropertyEditor) panel;
            
            DialogDescriptor dialogDesc = new DialogDescriptor(
                panel,
                getString("LBL_NewObject"), // NOI18N
                true,
                this
            );
            // added help button - must change location of help !!!
            dialogDesc.setHelpCtx(new HelpCtx(ActivationObjectType.class));
            dialogDesc.setClosingOptions(new Object[0]);
            dialog = DialogDisplayer.getDefault().createDialog(dialogDesc);
            dialog.show();
        }
        
        public java.lang.String getName() {
            return getString("LBL_NewObject"); // NOI18N
        }
        
        public void actionPerformed(java.awt.event.ActionEvent ae) {
            if (ae.getSource() == DialogDescriptor.OK_OPTION) {
                getItem().getActivationSystemItem().getRP().postRequest(new Runnable() {
                    public void run() {
                        try {
                            ActivationDesc desc = (ActivationDesc) editor.getPropertyValue();
                            StatusDisplayer.getDefault().setStatusText(getString("MSG_NewObjStart")); // NOI18N
                            getItem().getActivationSystemItem().addActivationObjectItem(desc);
                            dialog.dispose();
                            dialog = null;
                            StatusDisplayer.getDefault().setStatusText(getString("MSG_NewObjSuccessful")); // NOI18N
                        } catch (IllegalStateException ex) {
                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_PropertyObjDesc"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                        } catch (UnknownGroupException ex) {
                            StatusDisplayer.getDefault().setStatusText(getString("MSG_NewObjFailed")); // NOI18N
                            getItem().getActivationSystemItem().updateActivationItems();
                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_NewObjGroup"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                        } catch (java.rmi.ConnectException ex) {
                            StatusDisplayer.getDefault().setStatusText(getString("MSG_NewObjFailed")); // NOI18N
                            getItem().getActivationSystemItem().updateActivationItems();
                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_ConnectException"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                        } catch (java.rmi.activation.ActivationException ex) {
                            registrationFailed(ex, getString("ERR_NewObject")); // NOI18N
                        } catch (java.rmi.RemoteException ex) {
                            registrationFailed(ex, getString("ERR_NewObject")); // NOI18N
                        }
                    }
                });
            } else {
                dialog.dispose();
                dialog = null;
            }
        }
        
        /** Notify registration exception. */
        private void registrationFailed(Throwable ex, String annotate) {
            StatusDisplayer.getDefault().setStatusText(getString("MSG_NewObjFailed")); // NOI18N
            getItem().getActivationSystemItem().updateActivationItems();
            ErrorManager em = ActivationModule.getErrorManager(this.getClass());
            em.annotate(ex, annotate);
            em.notify(ex);
        }
    }
}
