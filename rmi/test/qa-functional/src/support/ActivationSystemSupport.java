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
import org.netbeans.modules.rmi.activation.*;
import org.netbeans.modules.rmi.settings.*;
import org.openide.util.datatransfer.NewType;
import org.openide.nodes.Node;

public class ActivationSystemSupport  {

  /** Remove all registry items from registry */
  public static void removeAllActivationSystemItems() {
        RMIRegistryItems regs=RMIRegistryItems.getInstance();
        java.util.Iterator asi=regs.getASSet().iterator();
        while (asi.hasNext()) {
            regs.removeAS((ActivationSystemItem)asi.next());
        }
  }
  
  public static void updateAllActivationSystemItems() {
        RMIRegistryItems regs=RMIRegistryItems.getInstance();
        java.util.Iterator asi=regs.getASSet().iterator();
        while (asi.hasNext()) {
            ((ActivationSystemItem)asi.next()).updateActivationItems();
        }
  }

  public static void removeAllActivationItems(ActivationSystemItem it) throws Exception{
        java.util.Iterator asi=it.getActivationGroupItems().iterator();
        while (asi.hasNext()) {
            ((ActivationGroupItem)asi.next()).unregister();
        }
  }

  public static ActivationSystemItem getActivationSystemItem(String host, int port) {
    Iterator iter = RMIRegistryItems.getInstance().getASSet().iterator();
    ActivationSystemItem actitem;
    try {
      InetAddress inetaddr = InetAddress.getByName(host);
      while (iter.hasNext()) {
        actitem = (ActivationSystemItem)(iter.next());
        if (actitem.getAddress().equals(inetaddr) && actitem.getPort() == port)
          return actitem;
      }
    }
    catch (UnknownHostException e) { }
    return null;
  }

/**
   public static String getAllServiceNames() {
    Iterator iter = RMIRegistryPool.getDefault().getItems().iterator();
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
  
  public static ServiceItem getService(RegistryItem regitem, String service) {
    Iterator iter = regitem.getServices().iterator();
    while (iter.hasNext()) {
      ServiceItem si = (ServiceItem)(iter.next());
      //System.out.println("RMI Services: "+si.getName());
      if (si.getName().equals(service)) return si;
    }
    return null;
  }
*/  
  public static ActivationSystemItem addLocalActivationSystemItem() {
      try {
            RMIRegistryItems.getInstance().addAS(new ActivationSystemItem("localhost",1098)); // NOI18N
      }catch(UnknownHostException e) { return null; }
    return getActivationSystemItem("localhost",1098);
  }

 public static boolean logState(Support sup,int state, boolean running) {
    String s="Local Activation System is ";
    if (state==ActivationSystemItem.RMID_RUNNING) {
        if(running) {
            sup.log(s+="running.");
            return true;
        } else 
            sup.errorlog(s+="running!");
    } else if (state==ActivationSystemItem.RMID_NOT_RUNNING) {
        if(!running) {
            sup.log(s+="not running.");
            return true;
        } else 
            sup.errorlog(s+="not running!");
    } else 
        sup.errorlog(s+="in unknown state!");
    return false;
}

}