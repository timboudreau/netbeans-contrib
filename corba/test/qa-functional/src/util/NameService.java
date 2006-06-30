/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
