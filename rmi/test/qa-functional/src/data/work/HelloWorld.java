/*
 *
 * 
 * HelloWorld.java -- synopsis.
 * 
 * 
 *  August 31, 2000
 *  <<Revision>>
 * 
 *  SUN PROPRIETARY/CONFIDENTIAL:  INTERNAL USE ONLY.
 * 
 *  Copyright © 1997-1999 Sun Microsystems, Inc. All rights reserved.
 *  Use is subject to license terms.
 */

package data.work;

import java.rmi.*;

/** HelloWorld interface for HelloClient RMI Client and HelloWorldImpl RMI Server
 *
 * @author  Adam Sotona
 * @version 1.0
 */


public interface HelloWorld extends java.rmi.Remote {  
  
  public String sayHello() throws RemoteException;

  public void exit() throws RemoteException;
}