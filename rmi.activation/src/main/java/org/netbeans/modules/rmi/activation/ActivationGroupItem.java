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

import java.beans.*;
import java.rmi.*;
import java.rmi.activation.*;
import java.util.*;

/**
 * Class represents an activation group.
 * @author  Jan Pokorsky
 * @version
 */
public final class ActivationGroupItem extends ActivationItem {
    /** Property for activation object items contained in the group. */
    public static final String PROP_ACTIVATABLES = "activatables"; // NOI18N
    /** Property for an activation group descriptor. */
    public static final String PROP_DESCRIPTOR = "agiDesc"; // NOI18N
    
    private ActivationGroupDesc desc;
    private ActivationGroupID gid;
    /** Set of activation object items contained in the group. */
    private HashSet activatables = new HashSet();
    
    private PropertyChangeSupport support;
    
    /** Creates new activation group item.
     * @param gid - activation group id
     * @param desc - activation group descriptor
     * @param desc - activation system item for which the group belongs.
     */
    public ActivationGroupItem(ActivationGroupID gid, ActivationGroupDesc desc, ActivationSystemItem asItem) {
        super(asItem);
        this.desc = desc;
        this.gid = gid;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        if (support == null) support = new PropertyChangeSupport(this);
        support.addPropertyChangeListener(pcl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        if (support != null) support.removePropertyChangeListener(pcl);
    }
    
    public void firePropertyChange(String name, Object oldVal, Object newVal) {
        if (support != null) support.firePropertyChange(name, null, null);
    }
    
    /** Sets new descriptor and fires an event with PROP_DESCRIPTOR if descriptor
     * is not same.
     */
    public void setDesc(ActivationGroupDesc desc) {
        boolean fire = !this.desc.equals(desc);
        this.desc = desc;
        if (fire) firePropertyChange(PROP_DESCRIPTOR, null, null);
    }
    
    /** Sets new descriptor in particular remote activation system and call setDesc.
     * @param desc - new activation group descriptor
     * @throws ActivationException for general failure (e.g., unable to update log).
     * @throws UnknownGroupException the group associated with id is not
     *         a registered group.
     * @throws RemoteException if remote call fails.
     */
    public void modifyDesc(ActivationGroupDesc desc)
    throws ActivationException, UnknownGroupException, RemoteException {
        asItem.getActivationSystem().setActivationGroupDesc(gid, desc);
        setDesc(desc);
    }
    
    /** Gets the activation group descriptor.
     * @return the activation group descriptor.
     */
    public ActivationGroupDesc getDesc() {
        return desc;
    }

    /** Gets the activation group id.
     * @return the activation group id.
     */
    public ActivationGroupID getActivationGroupID () {
        return gid;
    }
    
    /** Sets a collection of activation object items which belong to the group
     * and fires an event with PROP_ACTIVATABLES.
     * @param col - a collection of activation object items,
     *              can be <code>null</code>.
     */
    public void setActivatables(Collection col) {
        col = (col != null) ? col : Collections.EMPTY_SET;
        activatables = new HashSet(col);
        firePropertyChange(PROP_ACTIVATABLES, null, null);
    }
    
    /** Gets a set of a collection of activation object items.
     * @return a set of a collection of activation object items
     */
    public Set getActivatables() {
        return activatables;
    }

    /** Adds new activation object item to the group and fires an event
     * with PROP_ACTIVATABLES.
     * @param activatable - an activation object item
     */
    public void addActivatable(ActivationObjectItem activatable) {
        activatables.add(activatable);
        firePropertyChange(PROP_ACTIVATABLES, null, null);
    }

    /** Removes an activation object item from the group and fire an event
     * with PROP_ACTIVATABLES.
     * @param activatable - an activation object item
     */
    public void removeActivatable(ActivationObjectItem activatable) {
        activatables.remove(activatable);
        firePropertyChange(PROP_ACTIVATABLES, null, null);
    }
    
    /** Unregisters the group from an activation system and calls
     * <code>ActivationSystemItem.removeActivationGroupItem</code>.
     * @throws ActivationException if unregister fails (e.g., database update
     *         failure, etc).
     * @throws UnknownGroupException if group is not registered.
     * @throws RemoteException if remote call fails.
     */
    public void unregister() throws ActivationException, UnknownGroupException, RemoteException {
        asItem.getActivationSystem().unregisterGroup(gid);
        asItem.removeActivationGroupItem(this);
    }

    /** Inactivate group.
     * @throws RemoteException - if remote call fails
     * @throws ServerException - detail = RemoteException - if call informing monitor fails
     * @throws UnknownGroupException - unknown group id
     */
    public void inactivate()
    throws RemoteException, UnknownGroupException, UnknownObjectException {
        asItem.inactivateGroup(gid);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ActivationGroupItem) {
            ActivationGroupItem it = (ActivationGroupItem) obj;
            return getActivationGroupID().equals(it.getActivationGroupID());
        }
        return false;
    }

    public int compareTo(java.lang.Object obj) {
        if (obj instanceof ActivationGroupItem) {
            ActivationGroupItem it = (ActivationGroupItem) obj;
            return gid.hashCode() - it.getActivationGroupID().hashCode();
        } else
            return super.compareTo(obj);
    }
}
