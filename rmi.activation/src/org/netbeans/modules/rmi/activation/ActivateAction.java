/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.activation;

import java.rmi.RemoteException;
import java.rmi.activation.*;

import org.openide.*;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

import org.openide.awt.StatusDisplayer;

/** Action sensitive to some cookie that does something useful.
 *
 * @author  Jan Pokorsky
 */
public class ActivateAction extends CookieAction {

    private static boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N
    
    protected Class[] cookieClasses () {
        return new Class[] { ActivationNode.class };
    }

    protected int mode () {
        return MODE_EXACTLY_ONE;
    }

    protected void performAction (Node[] nodes) {
        for(int i = 0; i < nodes.length; i++) {
            ActivationNode node = (ActivationNode) nodes[i].getCookie(ActivationNode.class);
            final ActivationObjectItem item = (ActivationObjectItem) node.getItem();
            item.getActivationSystemItem().getRP().postRequest(new Runnable() {
                public void run() {
                    try {
                        ActivationID id = item.getActivationID();
                        StatusDisplayer.getDefault().setStatusText(getString("MSG_ActivateStart")); // NOI18N
                        id.activate(true);
                        StatusDisplayer.getDefault().setStatusText(getString("MSG_ActivateSuccessful")); // NOI18N
                    } catch (java.rmi.ConnectException ex) {
                        if (debug) ex.printStackTrace();
                        StatusDisplayer.getDefault().setStatusText(getString("MSG_ActivateFailed")); // NOI18N
                        item.getActivationSystemItem().updateActivationItems();
                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_ConnectException"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                    } catch (UnknownObjectException ex) {
                        StatusDisplayer.getDefault().setStatusText(getString("MSG_ActivateFailed")); // NOI18N
                        item.getActivationSystemItem().updateActivationItems();
                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_UnregisteredObj"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                    } catch (ActivationException aex) {
                        if (debug) aex.printStackTrace();
                        StatusDisplayer.getDefault().setStatusText(getString("MSG_ActivateFailed")); // NOI18N
                        item.getActivationSystemItem().updateActivationItems();
                        try {
                            throw aex.detail;
                        } catch (ClassNotFoundException ex) {
                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(ActivateAction.class, "ERR_ActivationClassNotFoundException", ex.getMessage()), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                        } catch (ThreadDeath ex) {
                            throw ex;
                        } catch (Throwable ex) {
                            ErrorManager em = ActivationModule.getErrorManager(this.getClass());
                            em.annotate(aex, getString("ERR_Activation")); // NOI18N
                            em.notify(aex);
                        }
                    } catch (RemoteException ex) {
                        if (debug) ex.printStackTrace();
                        StatusDisplayer.getDefault().setStatusText(getString("MSG_ActivateFailed")); // NOI18N
                        item.getActivationSystemItem().updateActivationItems();
                        ErrorManager em = ActivationModule.getErrorManager(this.getClass());
                        em.annotate(ex, getString("ERR_Activation")); // NOI18N
                        em.notify(ex);
                    }
                }
            });
        }
    }

    public String getName () {
        return NbBundle.getMessage (ActivateAction.class, "LBL_ActivateAction"); // NOI18N
    }

    protected String iconResource () {
        return "org/netbeans/modules/rmi/resources/ActivateActionIcon.gif"; // NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    private String getString(String key) {
        return NbBundle.getMessage(ActivateAction.class, key);
    }

}
