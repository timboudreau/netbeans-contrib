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

/**
 * Class represents an activatable object.
 * @author  Jan Pokorsky
 * @version 
 */
public final class ActivationObjectItem extends ActivationItem {
    /** Property for activation descriptor of activation item. */
    public static final String PROP_DESCRIPTOR = "aoiDesc"; // NOI18N
    protected ActivationID id;

    private ActivationDesc desc;
    private PropertyChangeSupport support;

    /** Holds value of property deleted. */
    private boolean deleted = false;
    
    public ActivationObjectItem(ActivationID id, ActivationDesc desc, ActivationSystemItem asItem) {
        super(asItem);
        this.id = id;
        this.desc = desc;
    }
    
    /** Set a descriptor and propagate a change.
     * @param desc activation descriptor.
     */
    public void setDesc(ActivationDesc desc) {
        boolean fire = !this.desc.equals(desc);
        this.desc = desc;
        if (fire) firePropertyChange(PROP_DESCRIPTOR, null, null);
    }
    
    /** Get an activation descriptor.
     * @return an activation descriptor.
     */
    public ActivationDesc getDesc() {
        return desc;
    }
    
    /** Sets new descriptor in particular remote activation system and call setDesc.
     * @param desc - new activation group descriptor
     * @throws ActivationException for general failure (e.g., unable to update log).
     * @throws UnknownGroupException the group associated with desc is not
     *         a registered group.
     * @throws UnknownObjectException the activation id is not registered.
     * @throws RemoteException if remote call fails.
     */
    public void modifyDesc(ActivationDesc desc) throws ActivationException,
                                                       UnknownGroupException,
                                                       UnknownObjectException,
                                                       RemoteException
    {
        // ToDo: catch java.rmi.connection exception and fire activation system unreachable.
        asItem.getActivationSystem().setActivationDesc(id, desc);
        setDesc(desc);
    }

    /** Get an activation identifier.
     * @return an activation identifier.
     */
    public ActivationID getActivationID() {
        return id;
    }
    
    /** Blocking method that unregisters the activatable object from
     * an activatable system and calls
     * <code>ActivationSystemItem.removeActivationObjectItem</code>.
     * @throws ActivationException if unregister fails (e.g., database update failure, etc).
     * @throws UnknownObjectException if object is unknown (not registered)
     * @throws RemoteException if remote call fails
     */
    public void unregister() throws ActivationException, UnknownObjectException, RemoteException {
        if (isDeleted()) return;
        asItem.getActivationSystem().unregisterObject(id);
        setDeleted(true);
        asItem.removeActivationObjectItem(this);
    }
    
    /** Inactivate activatable object.
     * @throws RemoteException - if remote call fails
     * @throws ServerException - detail = RemoteException - if call informing monitor fails
     * @throws UnknownObjectException - if id is not registered
     */
    public void inactivate()
    throws RemoteException, UnknownGroupException, UnknownObjectException {
        asItem.inactivateObject(id);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ActivationObjectItem) {
            ActivationObjectItem it = (ActivationObjectItem) obj;
            return getActivationID().equals(it.getActivationID());
        }
        return false;
    }

    public int compareTo(java.lang.Object obj) {
        if (obj instanceof ActivationObjectItem) {
            ActivationObjectItem it = (ActivationObjectItem) obj;
            String className = desc.getClassName();
            String className2 = it.getDesc().getClassName();
            if (className == null) className = "";  // NOI18N
            if (className2 == null) className2 = "";  // NOI18N
            int ret = className.compareTo(className2);
            if (ret == 0)
                ret = id.hashCode() - it.getActivationID().hashCode();
            return ret;
        } else
            return super.compareTo(obj);
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
    
    /** Is item already deleted? (from activation system)
     * @return Value of property deleted.
 */
    public boolean isDeleted() {
        return deleted;
    }
    
    /** Setter for property deleted.
     * @param deleted New value of property deleted.
 */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
}
