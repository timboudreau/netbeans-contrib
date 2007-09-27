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
import java.io.*;
import java.rmi.activation.ActivationGroupDesc;
import java.text.MessageFormat;
import java.util.*;

import org.openide.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.util.datatransfer.NewType;

import org.openide.awt.StatusDisplayer;
import org.netbeans.modules.rmi.activation.util.RefreshAction;
import org.netbeans.modules.rmi.activation.util.RefreshCookie;

/** 
 * Class represents ActivationSystemItem.
 * @author  mryzl, Jan Pokorsky
 */

public class ActivationSystemNode extends AbstractNode implements Node.Cookie, RefreshCookie, PropertyChangeListener {
    private static final String ON_ICON_BASE = "org/netbeans/modules/rmi/resources/activationSystem"; // NOI18N
    private static final String OFF_ICON_BASE = "org/netbeans/modules/rmi/resources/activationSystemOff"; // NOI18N
    private static final String ERR_ICON_BASE = "org/netbeans/modules/rmi/resources/activationSystemErr"; // NOI18N
    
    private static final SystemAction[] saValid = new SystemAction[] {
            SystemAction.get(org.openide.actions.NewAction.class),
//            SystemAction.get(org.openide.actions.PasteAction.class),
            null,
            SystemAction.get(RefreshAction.class),
            SystemAction.get(ActivationSystemNode.ShutdownAction.class),
            null,
            SystemAction.get(GenerateSetupAction.class),
            null,
            SystemAction.get(org.openide.actions.DeleteAction.class),
            null,
            SystemAction.get(org.openide.actions.ToolsAction.class),
            SystemAction.get(org.openide.actions.PropertiesAction.class),
        };
        
    private static final SystemAction[] saInvalid = new SystemAction[] {
            SystemAction.get(RefreshAction.class),
            null,
            SystemAction.get(org.openide.actions.DeleteAction.class),
            null,
            SystemAction.get(org.openide.actions.ToolsAction.class),
            SystemAction.get(org.openide.actions.PropertiesAction.class),
        };
        
    private ActivationSystemItem item;
    private PropertyChangeListener listener;
    
   /** Creates new ActivationSystemNode. */
    public ActivationSystemNode(ActivationSystemItem item, Children children) {
        super(children);
        this.item = item;
        setIconBase(OFF_ICON_BASE);
        systemActions = saInvalid;
        setName(NbBundle.getMessage(ActivationSystemNode.class, "FMT_ValidItem", item.getHostName(), new Integer(item.getPort())));
        setShortDescription(getLocalizedString("HINT_ActivationSystemNodeNotRunning")); // NOI18N
        
        CookieSet cookies = getCookieSet();
        cookies.add(this);
        
        listener = WeakListener.propertyChange(this, item);
        item.addPropertyChangeListener(listener);
    }

    /** Remove the node from its parent and deletes it. */
    public void destroy() throws IOException {
        super.destroy();
        item.removePropertyChangeListener(listener);
        RMIRegistryItems.getInstance().removeAS(item);
    }
    
    public boolean canDestroy() {
        return true;
    }  
    
    /** Calls updateActivationItems on activation system item. */
    public void refresh() {
        item.updateActivationItems();
    }
  
    private static String getLocalizedString(String key) {
        return NbBundle.getBundle(ActivationSystemNode.class).getString(key);
    }
    
    /** Get activation system item.
     * @return activation system item.
     */
    public ActivationSystemItem getActivationSystem() {
        return item;
    }
    
    /** Get the new types(ActivationGroupType) that can be created in this node. */
    public NewType[] getNewTypes() {
        return new NewType[] { new ActivationGroupType() };
    }

    public void propertyChange(java.beans.PropertyChangeEvent p) {
        if (ActivationSystemItem.PROP_ACTIVATION_ITEMS.equals(p.getPropertyName())) {
            switch (item.getRMID()) {
                case ActivationSystemItem.RMID_RUNNING:
                    systemActions = saValid;
                    setIconBase(ON_ICON_BASE);
                    setShortDescription(getLocalizedString("HINT_ActivationSystemNodeRunning")); // NOI18N
                    break;
                case ActivationSystemItem.RMID_NOT_RUNNING:
                    systemActions = saInvalid;
                    setIconBase(OFF_ICON_BASE);
                    setShortDescription(getLocalizedString("HINT_ActivationSystemNodeNotRunning")); // NOI18N
                    break;
                case ActivationSystemItem.RMID_UNKNOWN:
                    systemActions = saInvalid;
                    setIconBase(ERR_ICON_BASE);
                    setShortDescription(getLocalizedString("HINT_ActivationSystemNodeUnknown")); // NOI18N
                    break;
            }
            
            fireIconChange();
        }
    }
    
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set props = sheet.get(Sheet.PROPERTIES);
        
        try {
            PropertySupport.Reflection p = new PropertySupport.Reflection(item, String.class, "getHostName", null);  // NOI18N
            p.setName("host");  // NOI18N
            p.setDisplayName(getLocalizedString("PROP_Host"));  // NOI18N
            p.setShortDescription(getLocalizedString("HINT_Host"));  // NOI18N
            props.put(p);
            
            p = new PropertySupport.Reflection(item, Integer.TYPE, "getPort", null);  // NOI18N
            p.setName("port");  // NOI18N
            p.setDisplayName(getLocalizedString("PROP_Port"));  // NOI18N
            p.setShortDescription(getLocalizedString("HINT_Port"));  // NOI18N
            props.put(p);
        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        
        return sheet;
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (ActivationSystemNode.class.getName());
    }
    
    /** Describes Activation Group as a new type that can be created in Activation
     * System node. */
    class ActivationGroupType extends NewType implements java.awt.event.ActionListener {
        EnhancedCustomPropertyEditor editor;
        Dialog dialog;

        public void create() throws java.io.IOException {
            PropertyEditor pe = PropertyEditorManager.findEditor(ActivationGroupDesc.class);
            Component panel = null;
            if (pe != null && pe.supportsCustomEditor())
                panel = pe.getCustomEditor();

            if (!(panel instanceof EnhancedCustomPropertyEditor)) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getLocalizedString("ERR_CustomEditor"), NotifyDescriptor.ERROR_MESSAGE));   // NOI18N
                return;
            }

            editor = (EnhancedCustomPropertyEditor) panel;

            DialogDescriptor dialogDesc = new DialogDescriptor(
                panel,
                getLocalizedString("LBL_NewGroup"), // NOI18N
                true,
                this
            );
            // added help button - must change location of help !!!
            dialogDesc.setHelpCtx(new HelpCtx(ActivationGroupType.class));
            dialogDesc.setClosingOptions(new Object[0]);
            dialog = DialogDisplayer.getDefault().createDialog(dialogDesc);
            dialog.show();
        }

        public java.lang.String getName() {
            return getLocalizedString("LBL_NewGroup"); // NOI18N
        }

        public void actionPerformed(java.awt.event.ActionEvent ae) {
            if (ae.getSource() == DialogDescriptor.OK_OPTION) {
                item.getRP().postRequest(new Runnable() {
                    public void run() {
                        try {
                            ActivationGroupDesc desc = (ActivationGroupDesc) editor.getPropertyValue();
                            StatusDisplayer.getDefault().setStatusText(getLocalizedString("MSG_NewGroupStart")); // NOI18N
                            item.addActivationGroupItem(desc);
                            StatusDisplayer.getDefault().setStatusText(getLocalizedString("MSG_NewGroupSuccessful")); // NOI18N
                            dialog.dispose();
                            dialog = null;
                        } catch (IllegalStateException ex) {
                            DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(
                                    getLocalizedString("ERR_PropertyGroupDesc"), // NOI18N
                                    NotifyDescriptor.ERROR_MESSAGE
                                )
                            );
                        } catch (java.rmi.activation.ActivationException ex) {
                            registrationFailed(ex, getLocalizedString("ERR_NewGroup")); // NOI18N
                        } catch (java.rmi.ConnectException ex) {
                            StatusDisplayer.getDefault().setStatusText(getLocalizedString("MSG_NewGroupFailed")); // NOI18N
                            item.updateActivationItems();
                            DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(
                                    getLocalizedString("ERR_ConnectException"), // NOI18N
                                    NotifyDescriptor.ERROR_MESSAGE
                                )
                            );
                        } catch (java.rmi.RemoteException ex) {
                            registrationFailed(ex, getLocalizedString("ERR_NewGroup")); // NOI18N
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
            StatusDisplayer.getDefault().setStatusText(getLocalizedString("MSG_NewGroupFailed")); // NOI18N
            refresh();
            ErrorManager em = RMIModule.getErrorManager(this.getClass());
            em.annotate(ex, annotate);
            em.notify(ex);
        }
    }
    
    /** Action for activation system shutdown. */
    static class ShutdownAction extends CookieAction {
        /** Action is sensitive to ActivationSystemNode. */
        protected Class[] cookieClasses () {
            return new Class[] { ActivationSystemNode.class };
        }
        /** All nodes must implement cookies. */
        protected int mode () {
            return MODE_ALL;
        }

        protected void performAction (Node[] nodes) {
            ActivationSystemNode node;
            for (int i = 0; i < nodes.length; i++) {
                node = (ActivationSystemNode) nodes[i].getCookie (ActivationSystemNode.class);
                if (node != null)
                    shutdown(node.getActivationSystem());
            }
        }

        /** Calls shutdown on activation system item as nonblocking operation. */
        private void shutdown(final ActivationSystemItem item) {
            item.getRP().postRequest(new Runnable() {
                public void run() {
                    try {
                        StatusDisplayer.getDefault().setStatusText(getLocalizedString("MSG_ShutdownStart")); // NOI18N
                        item.shutdown();
                        StatusDisplayer.getDefault().setStatusText(getLocalizedString("MSG_ShutdownSuccessful")); // NOI18N
                    } catch (java.rmi.ConnectException ex) {
                        StatusDisplayer.getDefault().setStatusText(getLocalizedString("MSG_ShutdownFailed")); // NOI18N
                        item.updateActivationItems();
                        DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message(
                                getLocalizedString("ERR_ConnectException"), // NOI18N
                                NotifyDescriptor.ERROR_MESSAGE
                            )
                        );
                    } catch (java.rmi.RemoteException ex) {
                        StatusDisplayer.getDefault().setStatusText(getLocalizedString("MSG_ShutdownFailed")); // NOI18N
                        ErrorManager em = RMIModule.getErrorManager(this.getClass());
                        em.annotate(ex, getLocalizedString("MSG_ShutdownFailed")); // NOI18N
                        item.updateActivationItems();
                        em.notify(ex);
                    }
                }
            });
        }
  
        public String getName () {
            return NbBundle.getMessage (ActivationSystemNode.class, "LBL_ShutdownAction");  // NOI18N
        }

        public HelpCtx getHelpCtx () {
            return HelpCtx.DEFAULT_HELP;
        }
    }
}
