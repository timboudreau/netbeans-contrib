/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package util;

import java.io.*;


/** Helper class for Basic CORBA Test controling running of NameService
 */
public class NameService {
    
    Process process = null;
    PrintStream info;
    
    /** Creates new NameService
     */
    public NameService (PrintStream info) {
        this.info = info;
    }
    
    /** Starts NameService
     * @param port Port of NameService
     * @return IOR of started NameService
     */
    public String start (int port) {
        Runtime rt = Runtime.getRuntime();
        String home = System.getProperty ("java.home");
        String path = home + File.separator + "bin" + File.separator + "tnameserv";
        try {
            process = rt.exec (new String [] {path, "-ORBInitialPort", Integer.toString (port)});
            BufferedReader br = new BufferedReader (new InputStreamReader (process.getInputStream ()));
            br.readLine();
            String ior = br.readLine();
            return (ior != null  &&  ior.startsWith("IOR:")) ? ior : null;
        } catch (IOException e) {
            info.println("IOException while starting Naming Service");
            e.printStackTrace (info);
            return null;
        }
    }
    
    /** Stops NameService */
    public void stop () {
        process.destroy ();
    }
}
