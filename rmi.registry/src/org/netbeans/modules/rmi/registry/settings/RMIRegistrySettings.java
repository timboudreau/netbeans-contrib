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

package org.netbeans.modules.rmi.registry.settings;

import java.io.*;
import java.util.*;

import org.openide.explorer.propertysheet.editors.*;
import org.openide.options.*;
import org.openide.util.*;

import org.netbeans.modules.rmi.registry.*;

/**
 *
 * @author  mryzl
 */

public class RMIRegistrySettings extends SystemOption {

    /** Serial version UID. */
    static final long serialVersionUID = -5414182127521791783L;

    /** Name of the property refresh time. */
    public static final String PROP_REFRESH_TIME = "refreshTime"; // NOI18N

    /** Name of the property internal registry port. */
    public static final String PROP_INTERNAL_REGISTRY_PORT = "internalRegistryPort"; // NOI18N
    
    /** Name of the property internal registry port. */
    public static final String PROP_REGISTRY_ITEMS = "registryItems"; // NOI18N

    /** Default time between thwo registry updates. */
    public static final int DEFAULT_REFRESH_TIME = 60000;

    /** No registry. */
    public static final int DEFAULT_REGISTRY_PORT = 2099;

    /** Creates new RMIRegistrySettings. 
    */
    public RMIRegistrySettings() {
        // don't use setXXX here !!!
    }
    
    
    /** Get default instance.
     * @return default instance
     */
    public static RMIRegistrySettings getInstance() {
        return (RMIRegistrySettings) findObject(RMIRegistrySettings.class, true);
    }

    /** Defines whether the option is project or global specific.
    * @return true if the option is global.
    */
    public boolean isGlobal() {
        return true;
    }

    /** Get a human presentable name of the action.
    * This may be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String displayName () {
        return NbBundle.getBundle(RMIRegistrySettings.class).getString("PROP_RegistrySettingsName"); // NOI18N
    }

    /** Get a help context for the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx () {
        return new HelpCtx(RMIRegistrySettings.class);
    }

    /** Getter for property refreshTime.
     * @return Value of property refreshTime.
     */
    public int getRefreshTime() {
        Integer rt = (Integer) getProperty(PROP_REFRESH_TIME);
        return rt != null? rt.intValue(): DEFAULT_REFRESH_TIME;
    }

    /** Setter for property refreshTime.
     * @param refreshTIme New value of property refreshTime.
     */
    public void setRefreshTime(int refreshTime) {
        if (refreshTime < 0) refreshTime = 0;
        putProperty(PROP_REFRESH_TIME, new Integer(refreshTime), true);
    }

    /** Getter for property internalRegistryPort.
     * @return Value of property internalRegistryPort.
     */
    public int getInternalRegistryPort() {
        Integer rt = (Integer) getProperty(PROP_INTERNAL_REGISTRY_PORT);
        return rt != null? rt.intValue(): DEFAULT_REGISTRY_PORT;
    }

    /** Setter for property internalRegistryPort.
     * @param internalRegistryPort New value of property internalRegistryPort.
     */
    public void setInternalRegistryPort(int internalRegistryPort) {
        if (internalRegistryPort < 0) internalRegistryPort = DEFAULT_REGISTRY_PORT;
        putProperty(PROP_INTERNAL_REGISTRY_PORT, new Integer(internalRegistryPort), true);
    }

    /** Start registry. Don't stop the current version, throw an exception instead.
    * @param internalRegistryPort port value. 
    */
//    public void startRegistry(int internalRegistryPort) throws IOException {
//        int oldInternalRegistryPort = RMIRegistrySettings.internalRegistryPort;
//
//        if (oldInternalRegistryPort != internalRegistryPort) {
//            registry = java.rmi.registry.LocateRegistry.createRegistry(
//                internalRegistryPort, 
//                java.rmi.server.RMISocketFactory.getDefaultSocketFactory(), 
//                new RMIRegistrySSF()
//            );
//            RMIRegistrySettings.internalRegistryPort = internalRegistryPort;
//            System.out.println(java.text.MessageFormat.format(
//                NbBundle.getBundle(RMIRegistrySettings.class).getString("FMT_RegistryStarted"), // NOI18N
//                new Object[] {new Integer(internalRegistryPort)}
//            ));
//            firePropertyChange(PROP_INTERNAL_REGISTRY_PORT, new Integer(oldInternalRegistryPort), new Integer(internalRegistryPort));
//        }
//    }

    /** Stop registry.
    */
//    public static void stopRegistry(java.rmi.registry.Registry registry) throws IOException {
//        if (registry != null) {
//            sun.rmi.transport.ObjectTable.unexportObject(registry, true);
//            RMIRegistrySSF.cancelSocket(internalRegistryPort, RMIRegistrySSF.DEFAULT_TIMEOUT);
//            System.out.println(java.text.MessageFormat.format(
//                NbBundle.getBundle(RMIRegistrySettings.class).getString("FMT_RegistryStopped"), // NOI18N
//                new Object[] {new Integer(internalRegistryPort)}
//            ));
//            registry = null;
//        }
//    }
    
    /** Getter for property regs.
     * @return a new set of RegistryItems
     */
    public RegistryItem[] getRegistryItems() {
        RegistryItem[] items = (RegistryItem[]) getProperty(PROP_REGISTRY_ITEMS);
        return items != null ? items: new RegistryItem[0];
    }

    /** Setter for property regs.
     * @param regs New value of property regs.
     */
    public void setRegistryItems(RegistryItem[] items) {
        putProperty(PROP_REGISTRY_ITEMS, items, true);
    }
    
    /** Add a RegistryItem.
     * @param ri Registry Item
     */
    public void addRegistryItem(RegistryItem ri) {
        HashSet set = new HashSet(Arrays.asList(getRegistryItems()));
        set.add(ri);
        setRegistryItems((RegistryItem[]) set.toArray(new RegistryItem[set.size()]));
    }
    
    /** Remove a RegistryItem.
     * @param ri Registry Item
     */
    public void removeRegistryItem(RegistryItem ri) {
        HashSet set = new HashSet(Arrays.asList(getRegistryItems()));
        set.remove(ri);
        setRegistryItems((RegistryItem[]) set.toArray(new RegistryItem[set.size()]));
    }
}
