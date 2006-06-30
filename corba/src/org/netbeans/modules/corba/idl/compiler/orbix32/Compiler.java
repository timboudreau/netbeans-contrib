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

package org.netbeans.modules.corba.idl.compiler.orbix32;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

class Compiler {

    public static String _S_cmd = "idlj"; // NOI18N

    public static void main (String[] __args) throws Exception {
	//System.out.println ("OrbixWeb 3.2c IDL Compiler Wrapper 1.0");
	String[] __cmd = new String [__args.length + 1];
	__cmd[0] = _S_cmd;
	int __exit_status = 0;
	for (int __i = 0; __i < __args.length; __i++) {
	    __cmd[__i + 1] = __args[__i];
	}
	//for (int __i = 0; __i < __cmd.length; __i++) {
	//    System.out.println (__i + " : " + __cmd[__i]); // NOI18N
	//}	
	try {
	    Process __proc = Runtime.getRuntime ().exec (__cmd);
	    //OutputStream __out = __proc.getOutputStream ();
	    InputStream __tmp_in = __proc.getInputStream ();
	    InputStream __tmp_err = __proc.getErrorStream ();
	    BufferedReader __in = new BufferedReader (new InputStreamReader (__tmp_in));
	    BufferedReader __err = new BufferedReader (new InputStreamReader (__tmp_err));
	    
	    String __file_line, __err_line, __file_name = ""; // NOI18N
	    for (;;) {
		if ((__file_line = __in.readLine ()) != null) {
		    //System.out.println (__file_line);
		    __file_name = __file_line.substring (14, __file_line.length ());
		}
		else 
		    break;
		for (;;) {
		    __err_line = __err.readLine ();
		    if (__err_line != null && !__err_line.equals ("")) { // NOI18N
			//System.out.println (__err_line);
			System.out.println (__file_name + ":" + __err_line); // NOI18N
		    }
		    else
			break;
		    /*
		      if (__err_line.equals ("")) {
		      //System.out.println ("new line...");
		      break;
		      }
		    */
		}
	    }
	    __proc.waitFor ();
	} catch (SecurityException __ex) {
	    __ex.printStackTrace ();
	    __exit_status = 2;
	} catch (java.io.IOException __ex) {
	    __ex.printStackTrace ();
	    __exit_status = 1;
	}
	
	System.exit (__exit_status);
    }

}

/*
 * $Log
 * $
 */
