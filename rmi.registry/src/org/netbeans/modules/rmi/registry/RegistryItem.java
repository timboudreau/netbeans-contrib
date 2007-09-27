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

package org.netbeans.modules.rmi.registry;

import java.beans.*;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.net.*;
import java.net.UnknownHostException;
import java.text.*;
import java.util.*;

import org.openide.*;
import org.openide.util.NbBundle;
import org.openide.nodes.Node;

/**  Class representing one registry, i.e. pair hostname:port.
 *
 * @author Martin Ryzl
 */
public class RegistryItem implements Node.Cookie, java.io.Serializable {

    /** Serial version UID. */
    static final long serialVersionUID = -4780961588255414026L;

    /** Name of the property services*/
    public static final String PROP_SERVICES = "services"; // NOI18N

    /** Icon for server. */
    static final String SERVER_ICON_BASE = "/org/netbeans/modules/rmi/registry/resources/rmiServer"; // NOI18N
    /** Icon for interface. */
    static final String INTERFACE_ICON_BASE = "/org/openide/src/resources/interface"; // NOI18N

    /** Message format for valid node. */
    static final MessageFormat FMT_URL = new MessageFormat(getBundle("FMT_ItemURL")); // NOI18N
    /** Message format for invalid node. */
    static final MessageFormat FMT_REGISTRY_ITEM = new MessageFormat(getBundle("FMT_RegistryItem")); // NOI18N

    /** Localhost name. */
    public static final String LOCALHOST = "localhost"; // NOI18N

    /** Address. */
    protected InetAddress address;
    /** Port. */
    protected int port;

    /** Holds value of property services. */
    transient private Collection services;
    /** Property change support. */
    transient private PropertyChangeSupport support;

    /** Creates a new RegistryItem with localhost and the default port.
     */
    public RegistryItem() throws RemoteException, UnknownHostException {
        this(LOCALHOST, Registry.REGISTRY_PORT);
    }

    /** Creates a new RegistryItem with given host and the port.
     * @param hostname Hostname.
     * @param port Port.
     */
    public RegistryItem(String hostname, int port) throws RemoteException, UnknownHostException {
        this(InetAddress.getByName(hostname), port);
    }

    /** Creates a new RegistryItem with given host and the port.
     * @param address Address.
     * @param port Port.
     */
    public RegistryItem(InetAddress address, int port) throws RemoteException {
        this.address = address;
        this.port = port;
    }

    /** Equals.
     * @return true if the objects is a RegistryItem and the adress and port are
     * equal.
     */
    public boolean equals(Object o) {
        if (o == this) return true;
        if ((o != null) && (getClass() == o.getClass())) {
            RegistryItem ri = (RegistryItem) o;
            if (    ( ri.getAddress().equals( getAddress() ) )
                    && ( ri.getPort() == getPort() )
               ) return true;
        }
        return false;
    }

    /** Hash code.
    * @return has code of address xored with hash code of port.
    */
    public int hashCode() {
        return getAddress().hashCode() ^ getPort();
    }

    /**
    */
    public String toString() {
        return FMT_REGISTRY_ITEM.format(getItemObjects());
    }

    /** Get the registry for the RegitryItem.
     * @return Registry reference.
     */
    public Registry getRegistry() throws RemoteException {
        return LocateRegistry.getRegistry(getHostName(), getPort());
    }

    /** Creates an URL for the RegistryItem.
     * @return URL of registry.
     */
    public String getURLString() {
        return FMT_URL.format(getItemObjects());
    }

    /** Getter for the address.
    * @return address
    */
    public InetAddress getAddress() {
        return address;
    }

    /** Returns the host name.
    * @return host
    */
    public String getHostName() {
        return getAddress().getHostName();
    }

    /** Getter for the port.
    * @return host
    */
    public int getPort() {
        return port;
    }

    /** Return item as array of objects. Used for formating texts.
    * @return an array of Objects. [0] = hostname, [1] = port.
    */
    protected Object[] getItemObjects() {
        return new Object[] {
                   getHostName(),
                   new Integer(getPort())
               };
    }

    /** Getter for property services.
     * @return Value of property services.
     */
    public synchronized Collection getServices() {
        return services;
    }

    /** Setter for property services.
     * @param services New value of property services.
     */
    public synchronized void setServices(Collection services) {
        Object old = this.services;
        this.services = services;
        firePropertyChange(PROP_SERVICES, old, services);
    }

    /** Update services. This operation could be VERY time consuming and should
    * be called from dedicated thread.
    */
    public void updateServices() {
        try {
            Registry registry = getRegistry();
            String[] services = registry.list();
            Set set = new TreeSet();
            for(int i = 0; i < services.length; i++) {
                Class clazz = null;
                try {
                    Remote remote = registry.lookup(services[i]);
                    clazz = remote.getClass();
                } catch (Exception ex) {
                    // no clazz
                }
                set.add(new ServiceItem(services[i], clazz));
            }
            setServices(set);
        } catch (Exception ex) {
            setServices(null);
        }
    }

    /** Adds property listener.
    */
    public synchronized void addPropertyChangeListener (PropertyChangeListener l) {
        if (support == null) {
            synchronized (this) {
                // new test under synchronized block
                if (support == null) {
                    support = new PropertyChangeSupport (this);
                }
            }
        }
        support.addPropertyChangeListener (l);
    }

    /** Removes property listener.
    */
    public void removePropertyChangeListener (PropertyChangeListener l) {
        if (support != null) {
            support.removePropertyChangeListener (l);
        }
    }

    /** Fires property change event.
    * @param name property name
    * @param o old value
    * @param n new value
    */
    protected final void firePropertyChange(String name, Object o, Object n) {
        if (support != null) {
            support.firePropertyChange (name, o, n);
        }
    }

    /** Finalize object. It sets the list of services to null.
    */
    protected void finalize() throws Throwable  {
        setServices(null);
        super.finalize();
    }
    
    private static String getBundle( String key ) {
        return NbBundle.getMessage( RegistryItem.class, key );
    }
}















