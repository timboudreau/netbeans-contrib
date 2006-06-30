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
