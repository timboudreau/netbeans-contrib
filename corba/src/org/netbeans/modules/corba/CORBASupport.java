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

    public static final String NOT_SETUPED = CORBASupport.bundle.getString
            ("CTL_NotSetuped");

    public static final String INHER = CORBASupport.bundle.getString ("CTL_Inher");

    public static final String TIE = CORBASupport.bundle.getString ("CTL_Tie");

    public static final String SERVER_NS = CORBASupport.bundle.getString
                                           ("CTL_SERVER_NS");

    public static final String SERVER_IOR_TO_FILE = CORBASupport.bundle.getString
            ("CTL_SERVER_IOR_TO_FILE");

    public static final String SERVER_IOR_TO_OUTPUT = CORBASupport.bundle.getString
            ("CTL_SERVER_IOR_TO_OUTPUT");

    public static final String SERVER_BINDER = CORBASupport.bundle.getString
            ("CTL_SERVER_BINDER");

    public static final String CLIENT_NS = CORBASupport.bundle.getString
                                           ("CTL_CLIENT_NS");

    public static final String CLIENT_IOR_FROM_FILE = CORBASupport.bundle.getString
            ("CTL_CLIENT_IOR_FROM_FILE");

    public static final String CLIENT_IOR_FROM_INPUT = CORBASupport.bundle.getString
            ("CTL_CLIENT_IOR_FROM_INPUT");

    public static final String CLIENT_BINDER = CORBASupport.bundle.getString
            ("CTL_CLIENT_BINDER");

    public static final String SYNCHRO_DISABLE = CORBASupport.bundle.getString
            ("CTL_SYNCHRO_DISABLE");

    public static final String SYNCHRO_ON_UPDATE = CORBASupport.bundle.getString
            ("CTL_SYNCHRO_ON_UPDATE");

    public static final String SYNCHRO_ON_SAVE = CORBASupport.bundle.getString
            ("CTL_SYNCHRO_ON_SAVE");

    public static final String GEN_NOTHING = CORBASupport.bundle.getString
            ("CTL_GEN_NOTHING");

    public static final String GEN_EXCEPTION = CORBASupport.bundle.getString
            ("CTL_GEN_EXCEPTION");

    public static final String GEN_RETURN_NULL = CORBASupport.bundle.getString
	("CTL_GEN_RETURN_NULL");

    public static final String IDL_COMPILATION = CORBASupport.bundle.getString
	("PROP_COMPILATION");

    public static final String IDL_COMPILATION_HINT = CORBASupport.bundle.getString
	("HINT_COMPILATION");

    /** constant for idl extension */
    private static final String IDL_EXT = "idl";

    /** constant for java extension */
    private static final String JAVA_EXT = "java";

    /**
     * no-arg constructor
     */
    CORBASupport() {
        if (DEBUG)
            System.out.println ("CORBASupport");
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
