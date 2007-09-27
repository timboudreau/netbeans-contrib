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
