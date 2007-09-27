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
import java.net.*;
import java.rmi.*;
import java.rmi.activation.*;
import java.rmi.registry.*;
import java.util.*;

import org.openide.*;
import org.openide.util.*;

/**
 * Class repesents an activation system.
 * @author  mryzl, Jan Pokorsky
 */

public class ActivationSystemItem extends Object implements java.io.Serializable, org.openide.nodes.Node.Cookie {

    /** Serial version UID. */
    static final long serialVersionUID = -1871355455413536595L;

    private static boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N
    
    /** Property for activation items. */
    public static final String PROP_ACTIVATION_ITEMS = "activationItems"; // NOI18N
    
    /** RMID (wrapper) running. */
    public static final int RMID_RUNNING = 0;
    /** RMID (wrapper) not running. */
    public static final int RMID_NOT_RUNNING = 1;
    /** RMID (wrapper) running but with unknown activation system implementation. */
    public static final int RMID_UNKNOWN = 2;
    
    /** Address where the activation system is running. */
    private InetAddress address;
    /** Port where the activation system is running. */
    private int port;
    /** Mapping ActivationGroupID to ActivationGroupItem. */
    private transient HashMap aGroupItems;
    /** Mapping ActivationID to ActivationObjectItem. */
    private transient HashMap aObjectItems;
    
    private transient PropertyChangeSupport support;
    /** Monitored activation system. */
    private transient ActivationSystem asystem = null;
    /** Processor for each activation system. */
    private transient RequestProcessor processor;
    /** Currently running task. */
    private transient RequestProcessor.Task taskUpdate = null;
    /** Task for update items. */
    private transient UpdateTask task;
    /** RMID state. */
    private transient int rmid = RMID_NOT_RUNNING;
    
    /** Task for update of activation system item. */
    class UpdateTask implements Runnable {
        public void run() {
            updateActivationItemsImpl();
            if (debug) System.err.println("ASItem: " + getHostName() + ':' + getPort() + " finished"); // NOI18N
        }
    }
    
   /** Creates a new ActivationSystemItem. 
    */
    public ActivationSystemItem(String host, int port) throws java.net.UnknownHostException {
        this.address = InetAddress.getByName(host);
        this.port = port;
        initProcessor();
    }
    
    /** Deserialization. */
    private void readObject(java.io.ObjectInputStream in)
    throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        initProcessor();
    }
    
    /** Inicialization of request processor and update task. */
    private void initProcessor() {
        processor = new RequestProcessor();
        task = new UpdateTask();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (support == null) support = new PropertyChangeSupport(this);
        support.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (support != null) support.removePropertyChangeListener(listener);
    }
    
    private void firePropertyChange(String name, Object oldVal, Object newVal) {
        if (support != null) support.firePropertyChange(name, oldVal, newVal);
    }
    
   /** Address where the activation system is running.
    * @return address
    */
    public InetAddress getAddress() {
        return address;
    }
    
    /** Host name where the activation system is running.
     * @return host
     */
    public String getHostName() {
        return getAddress().getHostName();
    }
    
    /** Port where the activation system is running.
     * @return host
     */
    public int getPort() {
        return port;
    }
    
    /** Get activation items (objects + groups).
     * @return a new set of ActivationItems.
     */
    public Set getActivationItems() {
        HashSet items = new HashSet();

        if (aObjectItems != null) items.addAll(aObjectItems.values());
        if (aGroupItems != null) items.addAll(aGroupItems.values());
        return items;
    }

    /** Set new activation items.
     * @param aObjectItems new ActivationObjectItems.
     * @param aGroupItems new ActivationGroupItems.
     */
    private void setActivationItems(HashMap aObjectItems, HashMap aGroupItems) {
        this.aObjectItems = aObjectItems;
        this.aGroupItems = aGroupItems;
        firePropertyChange(PROP_ACTIVATION_ITEMS, null, null);
    }
    
    /** Get only ActivationObjectItems.
     * @return set of ActivationObjectItems.
     */
    public Set getActivationObjectItems() {
        if (aObjectItems != null) return new HashSet(aObjectItems.values());
        else return Collections.EMPTY_SET;
    }
    
    /** Get only ActivationGroupItems.
     * @return set of ActivationGroupItems.
     */
    public Set getActivationGroupItems() {
        if (aGroupItems != null) return new HashSet(aGroupItems.values());
        else return Collections.EMPTY_SET;
    }
    
    /** Register the activatable object, add new activation object item
     * into an activation system and proper group and fire an event
     * with PROP_ACTIVATION_ITEMS.
     * @param desc - an activation descriptor of new activation object item.
     * @throws UnknownGroupException - if group referred to in desc is not
     *         registered with this system.
     * @throws ActivationException - if registration fails
     *         (e.g., database update failure, etc).
     * @throws RemoteException - if remote call fails.
     */
    public void addActivationObjectItem(ActivationDesc desc)
    throws UnknownGroupException, ActivationException, RemoteException {
        ActivationID aID = asystem.registerObject(desc);
        ActivationObjectItem aoi = new ActivationObjectItem(aID, desc, this);
        if (aObjectItems == null) aObjectItems = new HashMap();
        aObjectItems.put(aID, aoi);
        
        if (aGroupItems != null) {
            ActivationGroupItem agi = (ActivationGroupItem) aGroupItems.get(desc.getGroupID());
            if (agi != null) agi.addActivatable(aoi);
        }
        
        firePropertyChange(PROP_ACTIVATION_ITEMS, null, null);
    }
    
    /** Remove ActivationObjectItem from the activation system tree (root + related group)
     * and propagate a change.
     * @param aoi object to remove.
     */
    public void removeActivationObjectItem(ActivationObjectItem aoi) {
        ActivationID aID = aoi.getActivationID();
        ActivationGroupID agID = aoi.getDesc().getGroupID();
        if (aObjectItems != null) aObjectItems.remove(aID);
        if (aGroupItems != null) {
            ActivationGroupItem agi = (ActivationGroupItem) aGroupItems.get(agID);
            if (agi != null) agi.removeActivatable(aoi);
        }
        
        firePropertyChange(PROP_ACTIVATION_ITEMS, null, null);
    }
    
    /** Register the activation group, add new activation group item
     * into an activation system and fire an event with PROP_ACTIVATION_ITEMS.
     * @param desc - an activation group descriptor of new activation group item
     * @throws ActivationException - if group registration fails.
     * @throws RemoteException - if remote call fails.
     */
    public void addActivationGroupItem(ActivationGroupDesc desc)
    throws ActivationException, RemoteException{
        ActivationGroupID agID = asystem.registerGroup(desc);
        ActivationGroupItem agi = new ActivationGroupItem(agID, desc, this);
        if (aGroupItems == null) aGroupItems = new HashMap();
        aGroupItems.put(agID, agi);
        firePropertyChange(PROP_ACTIVATION_ITEMS, null, null);
    }
    
    /** Gets proper group item for object item in an activation system.
     * @param item activation object item
     * @return group item or <code>null</code>.
     */
    public ActivationGroupItem getActivationGroupItem(ActivationObjectItem item) {
        if (aGroupItems != null) return (ActivationGroupItem) aGroupItems.get(item.getDesc().getGroupID());
        else return null;
    }
    
    /** Remove ActivationGroupItem and all its subnoutes from the activation system tree
     * and propagate a change.
     * @param agi group to remove.
     */
    public void removeActivationGroupItem(ActivationGroupItem agi) {
        Iterator iter = agi.getActivatables().iterator();
        while (iter.hasNext()) {
            ActivationObjectItem aoi = (ActivationObjectItem) iter.next();
            if (aObjectItems != null) {
                aObjectItems.remove(aoi.getActivationID());
            }
            aoi.setDeleted(true);
        }
        
        if (aGroupItems != null) aGroupItems.remove(agi.getActivationGroupID());
        
        firePropertyChange(PROP_ACTIVATION_ITEMS, null, null);
    }
    
    /**
     * Set new activation system.
     * @param as new activation system
     */
    public void setActivationSystem(ActivationSystem as) {
        this.asystem = as;
    }
    
    /**
     * Get activation system.
     * @return remote reference to activation system. Could be <code>null</code>.
     */
    public ActivationSystem getActivationSystem() {
        return asystem;
    }
    
    /** Shutdown remote activation system.
     * @throws RemoteException if failed to contact/shutdown the activation daemon
     */
    public void shutdown() throws RemoteException {
        asystem.shutdown();
        updateActivationItems();
//        setActivationSystem(null);
//        setActivationItems(null, null);
    }
    
    /** Update items in separate thread. */
    public synchronized void updateActivationItems() {
//        if (debug) System.err.println("ASItem: try run update for: "+getHostName()+":"+getPort()); // NOI18N
        if (taskUpdate != null && !taskUpdate.isFinished()) {
            if (debug) System.err.println("ASItem: not yet finished: " + getHostName() + ':' + getPort()); // NOI18N
            return;
        }
        taskUpdate = getRP().post(task);
    }
    
    /** Blocking method that gets all activation items. It calls
     * setActivationItems.
     */
    private void updateActivationItemsImpl() {
        RMIDWrapper wrapper;
        HashMap newAOItems = new HashMap();
        HashMap newAGItems = new HashMap();
        HashMap mapping = new HashMap();    // {ActivationGroupID, ArrayList(ActivationObjectItem)}
        
        // get wrapper
        rmid = RMID_NOT_RUNNING;
        try {
            Registry reg = LocateRegistry.getRegistry(getHostName(), getPort());
            reg.lookup("java.rmi.activation.ActivationSystem"); // NOI18N
            rmid = RMID_UNKNOWN;
            wrapper = (RMIDWrapper) reg.lookup("org.netbeans.rmi.RMIDWrapper"); // NOI18N
            rmid = RMID_RUNNING;

            // set wrapper as the activation system
            setActivationSystem(wrapper);
            
            // obtain activatable an group ids from the activation system
            ActivationID[] ids = wrapper.getActivationIDs();
            ActivationGroupID[] gids = wrapper.getActivationGroupIDs();

            // refresh all groups and their properties
            for(int i = 0; i < gids.length; i++) {
                ActivationGroupItem aiGrp = null;
                if (aGroupItems != null) aiGrp = (ActivationGroupItem) aGroupItems.get(gids[i]);
                ActivationGroupDesc grDesc = wrapper.getActivationGroupDesc(gids[i]);
                if (aiGrp == null) {
                    aiGrp = new ActivationGroupItem(gids[i], grDesc, this);
                } else {
                    aiGrp.setDesc(grDesc);
                }
                newAGItems.put(gids[i], aiGrp);
            }
            
            // refresh all activatables and their properties
            for(int i = 0; i < ids.length; i++) {
                ActivationObjectItem aiObj = null;
                if (aObjectItems != null) aiObj = (ActivationObjectItem) aObjectItems.get(ids[i]);
                ActivationDesc desc = wrapper.getActivationDesc(ids[i]);
                if (aiObj == null) {
                    aiObj = new ActivationObjectItem(ids[i], desc, this);
                } else {
                    aiObj.setDesc(desc);
                }
                newAOItems.put(ids[i], aiObj);
                
                // create temporary mapping ActivationGroupID => ArrayList(ActivationObjectItem)
                ActivationGroupID grID = desc.getGroupID();
                ArrayList objList = (ArrayList) mapping.get(grID);
                if (objList == null) {
                    objList = new ArrayList();
                    mapping.put(grID, objList);
                }
                objList.add(aiObj);
            }
            
            // propagate a change of activation items
            setActivationItems(newAOItems, newAGItems);
            
            // propagate a change of members of groups
            Iterator iter = newAGItems.keySet().iterator();
            while (iter.hasNext()) {
                ActivationGroupID grID = (ActivationGroupID) iter.next();
                ActivationGroupItem aiGrp = (ActivationGroupItem) newAGItems.get(grID);
                aiGrp.setActivatables((List) mapping.get(grID));
            }
            
        } catch (AccessException ex) {
            fault(ex);
        } catch (UnknownRMIDException ex) {
            rmid = RMID_UNKNOWN;
            fault(ex);
        } catch (NotBoundException ex) {
            fault(ex);
        } catch (ActivationException ex) {
            fault(ex);
        } catch (java.rmi.ConnectException ex) {
            fault(ex);
        } catch (RemoteException ex) {
            fault(ex);
        }
    }
    
    /** Handling failure during update. */
    private void fault(Exception ex) {
        if (debug) ex.printStackTrace();
        setActivationSystem(null);
        setActivationItems(null, null);
    }
    
    /** Returns the remote reference to the activatable object.
     * @param desc activatable object descriptor
     * @param id activation id
     * @return a remote reference.
     * @throws RemoteException - if remote call fails
     * @throws ActivationException - if activation system is not running or for general failure
     * @throws UnknownObjectException - if id is not registered
     * @throws StubNotFoundException
     */
    public java.rmi.Remote getStub(java.rmi.activation.ActivationID id)
    throws java.rmi.RemoteException,
           java.rmi.activation.ActivationException,
           java.rmi.activation.UnknownObjectException,
           java.rmi.StubNotFoundException
    {
        RMIDWrapper wrapper = (RMIDWrapper) asystem;
        return wrapper.getStub(id);
    }
    
    /** Registers the remote reference of the activatable object to 
     * a local registry.
     * @param id activation id
     * @param name name for the remote object
     * @param port port on which the registry accepts requests
     * @throws RemoteException - if remote call fails
     * @throws AccessException  if Registry.rebind operation is not permitted.
     * @throws MalformedURLException - if the name is not an appropriately formatted URL
     * @throws ActivationException - if activation system is not running or for general failure
     * @throws UnknownObjectException - if id is not registered
     * @throws StubNotFoundException
     */
    public void rebind(java.rmi.activation.ActivationID id,
                       String name,
                       int port)
    throws java.rmi.RemoteException,
           java.rmi.AccessException,
           java.net.MalformedURLException,
           java.rmi.activation.ActivationException,
           java.rmi.activation.UnknownObjectException,
           java.rmi.StubNotFoundException
    {
        RMIDWrapper wrapper = (RMIDWrapper) asystem;
        wrapper.rebind(id, name, port);
    }
    
    /** Inactivate activatable object.
     * @param id activation id
     * @throws RemoteException - if remote call fails
     * @throws ServerException - detail = RemoteException - if call informing monitor fails
     * @throws UnknownObjectException - if id is not registered
     */
    public void inactivateObject(java.rmi.activation.ActivationID id)
    throws java.rmi.RemoteException,
           java.rmi.activation.UnknownObjectException
    {
        try {
            RMIDWrapper wrapper = (RMIDWrapper) asystem;
            wrapper.inactivateObject(id);
        } catch (UnknownRMIDException ex) {
            if (debug) ex.printStackTrace();
            rmid = RMID_UNKNOWN;
            setActivationSystem(null);
            setActivationItems(null, null);
        }
    }
    
    /** Inactivate group.
     * @param gid activation group id
     * @throws RemoteException - if remote call fails
     * @throws ServerException - detail = RemoteException - if call informing monitor fails
     * @throws UnknownGroupException - unknown group id
     */
    public void inactivateGroup(java.rmi.activation.ActivationGroupID gid)
    throws java.rmi.RemoteException,
           java.rmi.activation.UnknownGroupException
    {
        try {
            RMIDWrapper wrapper = (RMIDWrapper) asystem;
            wrapper.inactivateGroup(gid);
        } catch (UnknownRMIDException ex) {
            if (debug) ex.printStackTrace();
            rmid = RMID_UNKNOWN;
            setActivationSystem(null);
            setActivationItems(null, null);
        }
    }
    
    /** Get request processor for mounted activation system. */
    public RequestProcessor getRP() {
        return processor;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof ActivationSystemItem) {
            ActivationSystemItem asi = (ActivationSystemItem) obj;
            if ((asi.getPort() == getPort()) && (asi.getAddress().equals(getAddress()))) return true;
        }
        return false;
    }
    
    public int hashCode() {
        return getAddress().hashCode() ^ getPort();
    }
    
    /** Get RMID state.
     * @return RMID state.
     */
    public int getRMID() {
        return rmid;
    }
    
    /** Called when item is unmounted. */
    public void destroy() {
        processor.stop();
    }
    
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }
    
}
 