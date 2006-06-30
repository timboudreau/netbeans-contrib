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

import java.rmi.*;
import java.rmi.activation.*;
import java.util.*;

import org.openide.*;
import org.openide.nodes.Children;
import org.openide.nodes.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 * Class represents an item in the activation system.
 * @author  mryzl, Jan Pokorsky
 * @version
 */
public abstract class ActivationItem extends java.lang.Object implements Comparable {

    protected ActivationSystemItem asItem;

    /** Creates new ActivationItem */
    public ActivationItem(ActivationSystemItem asItem) {
        this.asItem = asItem;
    }
    
    /** Gets an activation system item.
     * @return an activation system item.
     */
    public ActivationSystemItem getActivationSystemItem() {
        return asItem;
    }
    
    /** Unregister the activation item. Just call update on ActivationSystemItem.
     */
    public abstract void unregister() throws ActivationException, RemoteException;
    
    /** Inactivate activation item (group or object).
     * @throws RemoteException - if remote call fails
     * @throws ServerException - detail = RemoteException - if call informing monitor fails
     * @throws UnknownGroupException - unknown group id
     * @throws UnknownObjectException - if id is not registered
     * @throws WrongImplException - wrong rmid implementation
     */
    public abstract void inactivate()
    throws RemoteException, UnknownGroupException, UnknownObjectException;
    
    public int compareTo(java.lang.Object obj) {
        if (obj instanceof ActivationObjectItem) return 1;
        return -1;
    }
    
}
