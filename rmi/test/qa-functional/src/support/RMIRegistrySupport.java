/*
 *
 * 
 * RMIRegistrySupport.java -- synopsis.
 * 
 * 
 *  April 28, 2000
 *  <<Revision>>
 * 
 *  SUN PROPRIETARY/CONFIDENTIAL:  INTERNAL USE ONLY.
 * 
 *  Copyright © 1997-1999 Sun Microsystems, Inc. All rights reserved.
 *  Use is subject to license terms.
 */
package support;

import java.util.*;
import java.net.*;
import org.openide.options.*;
import org.netbeans.modules.rmi.registry.*;
import org.netbeans.modules.rmi.settings.*;
import org.openide.util.datatransfer.NewType;
import org.openide.nodes.Node;

/** RMI Registry Support
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class RMIRegistrySupport  {


  /** Stops internal registry */
  public static void stopInternalRegistry() {
    RMIRegistrySettings registrysettings = (RMIRegistrySettings) SystemOption.findObject(RMIRegistrySettings.class, true);
    registrysettings.setInternalRegistryPort(RMIRegistrySettings.REGISTRY_NONE);
  }
  
  /* Set internal registry with default port 1099 and start registry */
  public static void setInternalRegistryPort() {
    setInternalRegistryPort(1099);
  }
  
/* Set internal registry and start registry */
  public static void setInternalRegistryPort(int port) {
    RMIRegistrySettings registrysettings = (RMIRegistrySettings) SystemOption.findObject(RMIRegistrySettings.class, true);
    registrysettings.setInternalRegistryPort(port);
  }

  /** Get registry item for specified host and port */
  public static RegistryItem getRegistryItem(String host, int port) {
    Set s = RMIRegistryItems.getInstance().getRegs(); 
    
    Iterator iter = s.iterator();
    RegistryItem regitem;
    try {
      InetAddress inetaddr = InetAddress.getByName(host);
      while (iter.hasNext()) {
        regitem = (RegistryItem)(iter.next());
        if (regitem.getAddress().equals(inetaddr) && regitem.getPort() == port)
          return regitem;
      }
    }
    catch (UnknownHostException e) { }
    return null;
  }

  /** Get local registry item */
  public static RegistryItem getLocalRegistryItem() {
      return getRegistryItem("localhost",1099); // NOI18N
  }
  /** Remove all registry items from registry */
  public static void removeAllRegistryItems() {
    Iterator iter;
    RegistryItem regitem;
    while ((iter = RMIRegistryItems.getInstance().getRegs().iterator()).hasNext()) {
      regitem = (RegistryItem)(iter.next());
      RMIRegistryPool.getDefault().remove(regitem);
    }
  }
  
  /** Update all registry items */
  public static void updateAllRegistryItems() {
    Iterator iter = RMIRegistryItems.getInstance().getRegs().iterator();
    RegistryItem regitem;
    while (iter.hasNext()) {
      regitem = (RegistryItem)(iter.next());
      regitem.updateServices();
    }
  }
  

  /** Find service in Registry item */
  public static String getAllServiceNames() {
    Iterator iter = RMIRegistryItems.getInstance().getRegs().iterator();
    Iterator iter2;
    Collection services;
    RegistryItem regitem;
    String s=""; // NOI18N
    while (iter.hasNext()) {
      regitem=(RegistryItem)iter.next();
      s=s+regitem.getURLString()+"\n";   // NOI18N
      services = regitem.getServices();
      if (services==null) {
          s=s+"    -dead-\n"; // NOI18N
      } else {
        iter2 = services.iterator();
        while (iter2.hasNext()) {
          ServiceItem si = (ServiceItem)(iter2.next());
          s=s+"    "+si.getName()+"\n"; // NOI18N
        }
      }
    }
    return s;
  }
  
  /** Find service in Registry item */
  public static ServiceItem getService(RegistryItem regitem, String service) {
    Iterator iter = regitem.getServices().iterator();
    while (iter.hasNext()) {
      ServiceItem si = (ServiceItem)(iter.next());
      System.out.println("RMI Services: " + si.getName());
      if (si.getName().compareTo(service) == 0) return si;
    }
    return null;
  }
  
  /** Add local registry item to the registry pool */
  public static RegistryItem addLocalRegistryItem() {
    return addRegistryItem("localhost",1099); // NOI18N
  }

  /** Add registry item of the specified host and default port to the registry pool */
  public static RegistryItem addRegistryItem(String host) {
    return addRegistryItem(host,1099);
  }

  /** Add registry item of the specified host andd port to the registry pool */
  public static RegistryItem addRegistryItem(String host, int port) {
    try { RMIRegistryPool.getDefault().add(new RegistryItem(host,port)); }
    catch(java.rmi.RemoteException e) { return null; }
    catch(UnknownHostException e) { return null; }
    return getRegistryItem(host,port);
  }
}