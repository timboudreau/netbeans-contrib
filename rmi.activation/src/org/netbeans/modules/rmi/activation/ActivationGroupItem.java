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
