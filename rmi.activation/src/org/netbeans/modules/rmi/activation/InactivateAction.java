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

import org.openide.*;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 * Action inactivating activation groups and activatable objects.
 * @author  Jan Pokorsky
 */
public class InactivateAction extends CookieAction {

    private static boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N

    protected Class[] cookieClasses () {
        return new Class[] { ActivationNode.class };
    }

    protected int mode () {
        return MODE_EXACTLY_ONE;
    }

    protected void performAction (final Node[] nodes) {
        ActivationNode node;
        for (int i = 0; i < nodes.length; i++) {
            node = (ActivationNode) nodes[i].getCookie(ActivationNode.class);
            inactivate(node.getItem());
        }
    }
    
    public String getName () {
        return NbBundle.getMessage (InactivateAction.class, "LBL_InactivateAction"); // NOI18N
    }

    protected String iconResource () {
        return "org/netbeans/modules/rmi/resources/InactivateActionIcon.gif"; // NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    private String getString(String key) {
        return NbBundle.getMessage(InactivateAction.class, key);
    }
    
    /** Inactivate activation item.
     * @param item activation item
     */
    private void inactivate(final ActivationItem item) {
        item.getActivationSystemItem().getRP().postRequest(new Runnable() {
            public void run() {
                try {
                    StatusDisplayer.getDefault().setStatusText(getString("MSG_InactivateStart")); // NOI18N
                    item.inactivate();
                    StatusDisplayer.getDefault().setStatusText(getString("MSG_InactivateSuccessful")); // NOI18N
                } catch (java.rmi.ServerException sex) {
                    try {
                        throw sex.detail;
                    } catch (java.rmi.ConnectException ex) {
                        failed(sex, "ERR_ConnectException", item); // NOI18N
                    } catch (java.rmi.RemoteException ex) {
                        failed(sex, "ERR_InactivateRemoteException", item); // NOI18N
                    } catch (ThreadDeath ex) {
                        throw ex;
                    } catch (Throwable ex) {
                        StatusDisplayer.getDefault().setStatusText(getString("MSG_InactivateFailed")); // NOI18N
                        ErrorManager em = RMIModule.getErrorManager(this.getClass());
                        em.annotate(ex, getString("ERR_Inactivation")); // NOI18N
                        em.notify(ex);
                    }
                } catch (java.rmi.ConnectException ex) {
                    failed(ex, "ERR_ConnectException", item); // NOI18N
                } catch (java.rmi.RemoteException ex) {
                    failed(ex, "ERR_InactivateRemoteException", item); // NOI18N
                } catch (java.rmi.activation.UnknownObjectException ex) {
                    failed(ex, "ERR_InactivateUnknownObjectException", item); // NOI18N
                } catch (java.rmi.activation.UnknownGroupException ex) {
                    failed(ex, "ERR_InactivateUnknownGroupException", item); // NOI18N
                }
            }
        });
    }

    /** Notify user about failure.
     * @param ex exception
     * @param key bundle message key
     * @param item an activation item
     */
    private void failed(Exception ex, String key, ActivationItem item) {
        if (debug) ex.printStackTrace();
        StatusDisplayer.getDefault().setStatusText(getString("MSG_InactivateFailed")); // NOI18N
        item.getActivationSystemItem().updateActivationItems();
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString(key), NotifyDescriptor.ERROR_MESSAGE));
    }
    
}
