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

package org.netbeans.modules.corba.idl.compiler.orbix32;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

class Compiler {

    public static final String _S_cmd = "idlj"; // NOI18N
    
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
