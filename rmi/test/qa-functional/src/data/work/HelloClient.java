/*
 *
 * 
 * HelloClient.java -- synopsis.
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
import java.io.*;
import java.net.*;

/** HelloClient - RMI Client for HelloWorld RMI Server
 *
 * @author  Adam Sotona
 * @version 1.0
 */


public class HelloClient extends Object {
  public static final java.util.ResourceBundle bundle=java.util.ResourceBundle.getBundle("data/RMITests");

  /** Creates new RMIClient */
  public HelloClient() {
  }
  
  /**
  * @param args the command line arguments
  */
  public static void main (String args[]) throws IOException {
    String result = ""; // NOI18N
    HelloWorld obj;
    if (args.length != 2) 
        System.err.println(bundle.getString("Parameter_expected__service_URL_and_output_file."));
    else {
        try {
            //redirects Err and Out to given file
            /*
            PrintStream ps=new PrintStream(new FileOutputStream(args[1],true),true);
            System.setErr(ps);
            System.setOut(ps);
             */
        } catch(Exception e) {};
        System.setSecurityManager(new RMISecurityManager());
        try {
          obj = (HelloWorld) Naming.lookup(args[0]);
          result = obj.sayHello();
          try {
            obj.exit();
          } catch (Exception ex) { } 
          Naming.unbind(args[0]);
          System.out.println(bundle.getString("Finished."));
        } catch (Exception ex) {
          ex.printStackTrace();
        }    
    }
 }

}
