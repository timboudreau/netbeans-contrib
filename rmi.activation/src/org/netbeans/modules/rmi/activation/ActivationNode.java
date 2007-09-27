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
import org.openide.nodes.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

import org.openide.awt.StatusDisplayer;

/**
 * Class that represents one ActivationItem.
 * @author  Jan Pokorsky
 * @version
 */
public class ActivationNode extends org.openide.nodes.AbstractNode implements org.openide.nodes.Node.Cookie {

    private ActivationItem item;

    public ActivationNode(Children ch, ActivationItem item) {
        super(ch);
        this.item = item;
        setName(getString("LBL_InvalidActivationItem")); // NOI18N
        setIconBase("org/netbeans/modules/rmi/resources/activationItemOff"); // NOI18N
        CookieSet cookies = getCookieSet();
        cookies.add(this);
    }
    
    /** Get associated activation item.
     * @return an activation item.
     */
    public ActivationItem getItem() {
        return item;
    }
    
    /** Call destroy on ActivationItem using a request processor.
     */
    public void destroy() throws java.io.IOException {
        RMIModule.getRP().postRequest(new Runnable() {
            public void run() {
                try {
                    StatusDisplayer.getDefault().setStatusText(getString("MSG_UnregisterStart")); // NOI18N
                    item.unregister();
                    StatusDisplayer.getDefault().setStatusText(getString("MSG_UnregisterSuccessful")); // NOI18N
                } catch (UnknownGroupException ex) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_UnregisteredGroup"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                } catch (UnknownObjectException ex) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(getString("ERR_UnregisteredObj"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                } catch (ActivationException ex) {
                    ErrorManager em = ActivationModule.getErrorManager(this.getClass());
                    em.annotate(ex, getString("ERR_Unregistration")); // NOI18N
                    em.notify(ex);
                    StatusDisplayer.getDefault().setStatusText(getString("MSG_UnregisterFailed")); // NOI18N
                } catch (RemoteException ex) {
                    ErrorManager em = ActivationModule.getErrorManager(this.getClass());
                    em.annotate(ex, getString("ERR_Unregistration")); // NOI18N
                    em.notify(ex);
                    StatusDisplayer.getDefault().setStatusText(getString("MSG_UnregisterFailed")); // NOI18N
                }
            }
        });
    }

    public boolean canDestroy() {
        return true;
    }
    
    protected static String getString(String key) {
        return NbBundle.getMessage(ActivationNode.class, key);
    }
    
    protected static String getString(String formatKey, String optionKey) {
        return NbBundle.getMessage(ActivationNode.class,
                                   formatKey,
                                   getString(optionKey)
                                   );
    }

}
