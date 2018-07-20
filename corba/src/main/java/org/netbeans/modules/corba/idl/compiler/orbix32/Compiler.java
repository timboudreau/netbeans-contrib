/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
