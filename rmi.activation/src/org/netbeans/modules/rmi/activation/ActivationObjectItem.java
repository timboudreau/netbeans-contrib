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
