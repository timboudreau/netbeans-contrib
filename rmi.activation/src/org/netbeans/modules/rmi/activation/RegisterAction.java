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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.activation;

import java.awt.Dialog;
import java.awt.event.*;

import org.openide.*;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 * Register the activatable object to the registry.
 * @author  Jan Pokorsky
 * @version
 */
public final class RegisterAction extends CookieAction {

    private static boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N

    protected Class[] cookieClasses () {
        return new Class[] { ActivationObjectNode.class };
    }

    protected int mode () {
        return MODE_EXACTLY_ONE;
    }

    protected void performAction (final Node[] nodes) {
        ActivationObjectNode node;
        for (int i = 0; i < nodes.length; i++) {
            node = (ActivationObjectNode) nodes[i].getCookie(ActivationObjectNode.class);
            register((ActivationObjectItem) node.getItem());
        }
    }
    
    public String getName () {
        return NbBundle.getMessage (RegisterAction.class, "LBL_RegisterAction"); // NOI18N
    }

    protected String iconResource () {
        return "org/netbeans/modules/rmi/resources/RegisterActionIcon.gif"; // NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    private String getString(String key) {
        return NbBundle.getMessage(RegisterAction.class, key);
    }
    
    private Dialog dialog;
    
    /** Register the activatable object to the registry.
     * @param item activatable object item
     */
    private void register(final ActivationObjectItem item) {
        // show dialog
        final RegistrationPanel panel = new RegistrationPanel(item.getActivationSystemItem().getHostName());
        
        DialogDescriptor dialogDesc = new DialogDescriptor(
            panel,
            NbBundle.getBundle(RegisterAction.class).getString("LBL_RegisterNew"), // NOI18N
            false,
            new ActionListener() {
                public void actionPerformed(final java.awt.event.ActionEvent ev) {
                    if (ev.getSource() == DialogDescriptor.OK_OPTION) {
                        item.getActivationSystemItem().getRP().postRequest(new Runnable() {
                            public void run() {
                                ActivationSystemItem system = item.getActivationSystemItem();
                                try {
                                    int port = panel.getPort();
                                    dialog.dispose();
                                    dialog = null;
                                    StatusDisplayer.getDefault().setStatusText(getString("MSG_RegisterStart")); // NOI18N
                                    system.rebind(item.getActivationID(), panel.getServiceName(), panel.getPort());
                                    StatusDisplayer.getDefault().setStatusText(getString("MSG_RegisterSuccessful")); // NOI18N
                                } catch (java.rmi.ServerException sex) {
                                    try {
                                        throw sex.detail;
                                    } catch (java.rmi.ConnectException ex) {
                                        failed(sex, "ERR_RegisterConnectException"); // NOI18N
                                    } catch (java.rmi.AccessException ex) {
                                        failed(sex, "ERR_RegisterAccessException"); // NOI18N
                                    } catch (java.rmi.StubNotFoundException ex) {
                                        failed(sex, "ERR_RegisterStubNotFoundException"); // NOI18N
                                    } catch (java.rmi.RemoteException ex) {
                                        failed(sex, "ERR_RegisterRemoteException"); // NOI18N
                                    } catch (ThreadDeath ex) {
                                        throw ex;
                                    } catch (Throwable ex) {
                                        StatusDisplayer.getDefault().setStatusText(getString("MSG_RegisterFailed")); // NOI18N
                                        ErrorManager em = RMIModule.getErrorManager(this.getClass());
                                        em.annotate(ex, getString("ERR_Register")); // NOI18N
                                        em.notify(ex);
                                    }
                                } catch (java.rmi.ConnectException ex) {
                                    system.updateActivationItems();
                                    failed(ex, "ERR_ConnectException"); // NOI18N
                                } catch (java.rmi.RemoteException ex) {
                                    system.updateActivationItems();
                                    failed(ex, "ERR_RegisterRemoteException"); // NOI18N
                                } catch (java.rmi.activation.UnknownObjectException ex) {
                                    system.updateActivationItems();
                                    failed(ex, "ERR_RegisterUnknownObjectException"); // NOI18N
                                } catch (java.rmi.activation.ActivationException ex) {
                                    system.updateActivationItems();
                                    failed(ex, "ERR_RegisterActivationException"); // NOI18N
                                } catch (java.net.MalformedURLException ex) {
                                    failed(ex, "ERR_RegisterMalformedURLException"); // NOI18N
                                } catch (NumberFormatException ex) {
                                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_ActivationTypePort"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                                }
                            }
                        });
                    } else {
                        dialog.dispose();
                        dialog = null;
                    }
                }
            }
        );
        dialogDesc.setHelpCtx(new HelpCtx(RegisterAction.class));
        dialogDesc.setClosingOptions(new Object[0]);
        dialog = DialogDisplayer.getDefault().createDialog(dialogDesc);
        dialog.show();
    }
    
    
    /** Notify user about failure.
     * @param ex exception
     * @param key bundle message key
     */
    private void failed(Exception ex, String key) {
        if (debug) ex.printStackTrace();
        StatusDisplayer.getDefault().setStatusText(getString("MSG_RegisterFailed")); // NOI18N
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString(key), NotifyDescriptor.ERROR_MESSAGE));
    }
}
