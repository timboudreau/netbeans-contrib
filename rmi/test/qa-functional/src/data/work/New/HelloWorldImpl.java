/*
 *
 * 
 * HelloWorldImpl.java -- synopsis.
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

package data.work.New;

import java.rmi.*;
import java.io.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

/** HelloWorldImpl - UnicastRemoteObject implementation of HelloWorld
 *
 * @author  Adam Sotona
 * @version 1.0
 */


public class HelloWorldImpl extends UnicastRemoteObject implements HelloWorld {
  public static final java.util.ResourceBundle bundle=java.util.ResourceBundle.getBundle("data/RMITests");

  /** Registration name */
  static protected String objURLName;
    
  /** Constructs HelloWorldImpl object and exports it on default port.
   */
  public HelloWorldImpl() throws RemoteException {
    super();
  }

  /** Constructs HelloWorldImpl object and exports it on specified port.
   * @param port The port for exporting
   */
  public HelloWorldImpl(int port) throws RemoteException {
    super(port);
  }

  /** Register HelloWorldImpl object with the RMI registry.
  * @param rmiName name identifying the service in the RMI registry, e.g. "testRMI/HelloWorld"
  * @throw RemoteException if cannot be exported or bound to RMI registry
  * @throw MalformedURLException if rmiName cannot be used to construct a valid URL
  * @throw IllegalArgumentException if null passed as rmiName
  */
  public void register(String rmiName) throws RemoteException, MalformedURLException{
    
    if (rmiName == null) throw new IllegalArgumentException(bundle.getString("Registration_name_can_not_be_null"));
    
    Naming.rebind(rmiName, this);
  }
  
  /** Main method.
   */
  public static void main(String[] args)/* throws Exception*/{
   try {   
       /*
    if (args.length != 2) 
        System.err.println(bundle.getString("Parameter_expected__service_URL_and_output_file..1"));
    else {
        try {
            //redirects Err and Out to given file 
            
            PrintStream ps=new PrintStream(new FileOutputStream(args[1],true),true);
            System.setErr(ps);
            System.setOut(ps);
             
        } catch(Exception e) {
            e.printStackTrace();
        };*/
       
        objURLName = (args.length > 0) ? args[0] : HelloWorldImpl.class.getName();;
        
        System.setSecurityManager(new RMISecurityManager());

        HelloWorldImpl obj;
        if (args.length == 2) obj = new HelloWorldImpl (new Integer(args[1]).intValue());
        else obj = new HelloWorldImpl ();
        
        obj.register(objURLName);
        
        System.out.println(bundle.getString("Registered."));
    //} 
   } catch(Exception e) {
     e.printStackTrace();
   } 
  }
 
   public String hello() throws RemoteException {
    return bundle.getString("Hello_World");
  }
  
   public void exit() throws RemoteException {
     System.out.println(bundle.getString("Finished."));
     System.exit(0);
  }
}