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

package org.netbeans.modules.corba;

import java.io.*;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.openide.util.NbBundle;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/** Support for execution applets for applets
*
* @author Karel Gardas
* @version 0.02 May 01, 1999
*/
public class CORBASupport {

    public static final boolean DEBUG = false;

    /** bundle to obtain text information from */
    public static ResourceBundle bundle = NbBundle.getBundle(CORBASupport.class);

    public static final String SETTINGS = CORBASupport.bundle.getString
	("CTL_CORBA_SETTINGS"); // NOI18N

    public static final String NOT_SETUPED = CORBASupport.bundle.getString
	("CTL_NOTSETUPED"); // NOI18N
    
    public static final String CANT_GENERATE = CORBASupport.bundle.getString
	("CTL_CANTGENERATE"); // NOI18N
    
    public static final String CANT_GENERATE_INTO_RO_FS = CORBASupport.bundle.getString
	("CTL_CANTGENERATE_INTO_RO_FS"); // NOI18N
    
    public static final String GENERATE = CORBASupport.bundle.getString
	("CTL_GENERATE"); // NOI18N

    public static final String PARTIAL_CONFIGURATION = CORBASupport.bundle.getString
	("CTL_PARTIAL_CONFIGURATION");

    public static final String OPENORB_CONFIGURATION = CORBASupport.bundle.getString
	("CTL_OPENORB_CONFIGURATION");

    /*
      public static final String INHER = CORBASupport.bundle.getString ("CTL_Inher");
      
      public static final String TIE = CORBASupport.bundle.getString ("CTL_Tie");

      public static final String SERVER_NS = CORBASupport.bundle.getString
      ("CTL_SERVER_NS"); // NOI18N
      
      public static final String SERVER_IOR_TO_FILE = CORBASupport.bundle.getString
      ("CTL_SERVER_IOR_TO_FILE"); // NOI18N
      
      public static final String SERVER_IOR_TO_OUTPUT = CORBASupport.bundle.getString
      ("CTL_SERVER_IOR_TO_OUTPUT"); // NOI18N
      
      public static final String SERVER_BINDER = CORBASupport.bundle.getString
      ("CTL_SERVER_BINDER"); // NOI18N
      
      public static final String CLIENT_NS = CORBASupport.bundle.getString
      ("CTL_CLIENT_NS"); // NOI18N
      
      public static final String CLIENT_IOR_FROM_FILE = CORBASupport.bundle.getString
      ("CTL_CLIENT_IOR_FROM_FILE"); // NOI18N
      
      public static final String CLIENT_IOR_FROM_INPUT = CORBASupport.bundle.getString
      ("CTL_CLIENT_IOR_FROM_INPUT"); // NOI18N
      
      public static final String CLIENT_BINDER = CORBASupport.bundle.getString
      ("CTL_CLIENT_BINDER"); // NOI18N
      
      public static final String SYNCHRO_DISABLE = CORBASupport.bundle.getString
      ("CTL_SYNCHRO_DISABLE"); // NOI18N
      
      public static final String SYNCHRO_ON_UPDATE = CORBASupport.bundle.getString
      ("CTL_SYNCHRO_ON_UPDATE"); // NOI18N
      
      public static final String SYNCHRO_ON_SAVE = CORBASupport.bundle.getString
      ("CTL_SYNCHRO_ON_SAVE"); // NOI18N
      
      public static final String GEN_NOTHING = CORBASupport.bundle.getString
      ("CTL_GEN_NOTHING"); // NOI18N
      
      public static final String GEN_EXCEPTION = CORBASupport.bundle.getString
      ("CTL_GEN_EXCEPTION"); // NOI18N
      
      public static final String GEN_RETURN_NULL = CORBASupport.bundle.getString
      ("CTL_GEN_RETURN_NULL"); // NOI18N
    */
    public static final String IDL_COMPILATION = CORBASupport.bundle.getString
	("PROP_COMPILATION"); // NOI18N

    public static final String IDL_COMPILATION_HINT = CORBASupport.bundle.getString
	("HINT_COMPILATION"); // NOI18N

    public static final String ORB_FOR_COMPILATION = CORBASupport.bundle.getString
	("PROP_ORB_FOR_COMPILATION"); // NOI18N

    public static final String ORB_FOR_COMPILATION_HINT = CORBASupport.bundle.getString
	("HINT_ORB_FOR_COMPILATION"); // NOI18N

    public static final String CANT_FIND_IMPLS = CORBASupport.bundle.getString 
	("CTL_CANT_FIND_IMPLS"); // NOI18N

    public static final String WAIT =  CORBASupport.bundle.getString
	("CTL_WAIT"); // NOI18N

    public static final String PARSE_ERROR = CORBASupport.bundle.getString ("CTL_PARSE_ERROR");

    public static final String WAITING_FOR_PARSER = CORBASupport.bundle.getString 
	("CTL_WAITING_FOR_PARSER"); // NOI18N

    public static final String PARSING = CORBASupport.bundle.getString ("CTL_PARSING");

    public static final String CANT_FIND_SYMBOL = CORBASupport.bundle.getString
	("CTL_CANT_FIND_SYMBOL"); // NOI18N

    public static final String SUCESS_GENERATED = CORBASupport.bundle.getString
	("CTL_SUCESS_GENERATED"); // NOI18N
    
    public static final String RECURSIVE_INHERITANCE = CORBASupport.bundle.getString
	("CTL_RECURSIVE_INHERITANCE");

    /*
      public static final String SYNC_DISABLED = CORBASupport.bundle.getString
      ("CTL_SYNC_DISABLED"); // NOI18N
    */
    public static final  String ADD_METHOD = CORBASupport.bundle.getString
	("CTL_ADD_METHOD"); // NOI18N
      
    public static final  String UPDATE_METHOD = CORBASupport.bundle.getString
	("CTL_UPDATE_METHOD"); // NOI18N
   
    public static final String UNDEFINED_INTERFACE = CORBASupport.bundle.getString
	("CTL_UNDEFINED_INTERFACE"); // NOI18N

    public static final String UNDEFINED_VALUE = CORBASupport.bundle.getString
	("CTL_UNDEFINED_VALUE"); // NOI18N

    public static final String DUPLICATE_EXCEPTION = CORBASupport.bundle.getString
	("CTL_DUPLICATE_EXCEPTION"); // NOI18N

    public static final String GENERATOR_ERROR = CORBASupport.bundle.getString
	("CTL_GENERATOR_ERROR"); // NOI18N

    public static final String CANNOT_SUPPORT = CORBASupport.bundle.getString
	("CTL_CANNOT_SUPPORT");

    public static final String CANNOT_INHERIT_FROM = CORBASupport.bundle.getString
	("CTL_CANNOT_INHERIT_FROM");

    public static final String ALREADY_DEFINED_SYMBOL = CORBASupport.bundle.getString
	("CTL_ALREADY_DEFINED_SYMBOL");
    
    /** constant for idl extension */
    public static final String IDL_EXT = "idl"; // NOI18N

    /** constant for java extension */
    private static final String JAVA_EXT = "java"; // NOI18N

    /**
     * no-arg constructor
     */
    CORBASupport() {
        if (DEBUG)
            System.out.println ("CORBASupport"); // NOI18N
    }

}

/*
 * <<Log>>
 *  18   Gandalf   1.17        11/4/99  Karel Gardas    - update from CVS
 *  17   Gandalf   1.16        11/4/99  Karel Gardas    update from CVS
 *  16   Gandalf   1.15        11/4/99  Karel Gardas    update from CVS
 *  15   Gandalf   1.14        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  14   Gandalf   1.13        10/1/99  Karel Gardas    updates from CVS
 *  13   Gandalf   1.12        8/7/99   Karel Gardas    changes in code which 
 *       hide generated files
 *  12   Gandalf   1.11        8/3/99   Karel Gardas    
 *  11   Gandalf   1.10        7/10/99  Karel Gardas    
 *  10   Gandalf   1.9         6/11/99  Jaroslav Tulach System.out commented
 *  9    Gandalf   1.8         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  8    Gandalf   1.7         5/28/99  Karel Gardas    
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/22/99  Karel Gardas    
 *  5    Gandalf   1.4         5/15/99  Karel Gardas    
 *  4    Gandalf   1.3         5/10/99  Ian Formanek    Fixed to compile
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */
