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

package org.netbeans.modules.corba.settings;

import java.util.ResourceBundle;

import org.openide.util.NbBundle;

/** Support for execution applets for applets
*
* @author Karel Gardas
* @version 0.01 August 14, 2000
*/
public class ORBSettingsBundle {

    /** bundle to obtain text information from */
    public static ResourceBundle bundle = NbBundle.getBundle(ORBSettingsBundle.class);

    public static final String PROP_ORB = ORBSettingsBundle.bundle.getString
	("PROP_ORB"); // NOI18N
    public static final String HINT_ORB = ORBSettingsBundle.bundle.getString
	("HINT_ORB"); // NOI18N

    public static final String PROP_CLIENT_BINDING = ORBSettingsBundle.bundle.getString
	("PROP_CLIENT_BINDING"); // NOI18N
    public static final String HINT_CLIENT_BINDING = ORBSettingsBundle.bundle.getString
	("HINT_CLIENT_BINDING"); // NOI18N
    public static final String CLIENT_NS = ORBSettingsBundle.bundle.getString
	("CTL_CLIENT_NS"); // NOI18N
    public static final String CLIENT_IOR_FROM_FILE = ORBSettingsBundle.bundle.getString
	("CTL_CLIENT_IOR_FROM_FILE"); // NOI18N
    public static final String CLIENT_IOR_FROM_INPUT = ORBSettingsBundle.bundle.getString
	("CTL_CLIENT_IOR_FROM_INPUT"); // NOI18N
    public static final String CLIENT_BINDER = ORBSettingsBundle.bundle.getString
	("CTL_CLIENT_BINDER"); // NOI18N

    public static final String PROP_SKELS = ORBSettingsBundle.bundle.getString
	("PROP_SKELS"); // NOI18N
    public static final String HINT_SKELS = ORBSettingsBundle.bundle.getString
	("HINT_SKELS"); // NOI18N
    public static final String INHER = ORBSettingsBundle.bundle.getString
	("CTL_INHER"); // NOI18N    
    public static final String TIE = ORBSettingsBundle.bundle.getString
	("CTL_TIE"); // NOI18N

    public static final String PROP_GENERATION = ORBSettingsBundle.bundle.getString
	("PROP_GENERATION"); // NOI18N
    public static final String HINT_GENERATION = ORBSettingsBundle.bundle.getString
	("HINT_GENERATION"); // NOI18N
    public static final String GEN_NOTHING = ORBSettingsBundle.bundle.getString
	("CTL_GEN_NOTHING"); // NOI18N    
    public static final String GEN_EXCEPTION = ORBSettingsBundle.bundle.getString
	("CTL_GEN_EXCEPTION"); // NOI18N
    public static final String GEN_RETURN_NULL = ORBSettingsBundle.bundle.getString
	("CTL_GEN_RETURN_NULL"); // NOI18N

    public static final String PROP_HIDE = ORBSettingsBundle.bundle.getString
            ("PROP_HIDE"); // NOI18N
    public static final String HINT_HIDE = ORBSettingsBundle.bundle.getString
            ("HINT_HIDE"); // NOI18N

    public static final String PROP_PARAMS = ORBSettingsBundle.bundle.getString
	("PROP_PARAMS"); // NOI18N
    public static final String HINT_PARAMS = ORBSettingsBundle.bundle.getString
	("HINT_PARAMS"); // NOI18N

    public static final String PROP_SERVER_BINDING = ORBSettingsBundle.bundle.getString
	("PROP_SERVER_BINDING"); // NOI18N
    public static final String HINT_SERVER_BINDING = ORBSettingsBundle.bundle.getString
	("HINT_SERVER_BINDING"); // NOI18N
    public static final String SERVER_NS = ORBSettingsBundle.bundle.getString
	("CTL_SERVER_NS"); // NOI18N
    public static final String SERVER_IOR_TO_FILE = ORBSettingsBundle.bundle.getString
	("CTL_SERVER_IOR_TO_FILE"); // NOI18N
    public static final String SERVER_IOR_TO_OUTPUT = ORBSettingsBundle.bundle.getString
	("CTL_SERVER_IOR_TO_OUTPUT"); // NOI18N
    public static final String SERVER_BINDER = ORBSettingsBundle.bundle.getString
	("CTL_SERVER_BINDER"); // NOI18N
    
    public static final String PROP_SYNCHRO = ORBSettingsBundle.bundle.getString
	("PROP_SYNCHRO"); // NOI18N
    public static final String HINT_SYNCHRO = ORBSettingsBundle.bundle.getString
	("HINT_SYNCHRO"); // NOI18N
    public static final String SYNCHRO_DISABLED = ORBSettingsBundle.bundle.getString
	("CTL_SYNCHRO_DISABLED"); // NOI18N
    public static final String SYNCHRO_ON_UPDATE = ORBSettingsBundle.bundle.getString
	("CTL_SYNCHRO_ON_UPDATE"); // NOI18N
    public static final String SYNCHRO_ON_SAVE = ORBSettingsBundle.bundle.getString
	("CTL_SYNCHRO_ON_SAVE"); // NOI18N
    

    // expert options 

    public static final String PROP_PACKAGE_DELIMITER = ORBSettingsBundle.bundle.getString
	("PROP_PACKAGE_DELIMITER"); // NOI18N
    public static final String HINT_PACKAGE_DELIMITER = ORBSettingsBundle.bundle.getString
	("HINT_PACKAGE_DELIMITER"); // NOI18N
    public static final String PROP_ERROR_EXPRESSION = ORBSettingsBundle.bundle.getString
	("PROP_ERROR_EXPRESSION"); // NOI18N
    public static final String HINT_ERROR_EXPRESSION = ORBSettingsBundle.bundle.getString
	("HINT_ERROR_EXPRESSION"); // NOI18N
    public static final String PROP_FILE_POSITION = ORBSettingsBundle.bundle.getString
	("PROP_FILE_POSITION"); // NOI18N
    public static final String HINT_FILE_POSITION = ORBSettingsBundle.bundle.getString
	("HINT_FILE_POSITION"); // NOI18N
    public static final String PROP_LINE_POSITION = ORBSettingsBundle.bundle.getString
	("PROP_LINE_POSITION"); // NOI18N
    public static final String HINT_LINE_POSITION = ORBSettingsBundle.bundle.getString
	("HINT_LINE_POSITION"); // NOI18N
    public static final String PROP_COLUMN_POSITION = ORBSettingsBundle.bundle.getString
	("PROP_COLUMN_POSITION"); // NOI18N
    public static final String HINT_COLUMN_POSITION = ORBSettingsBundle.bundle.getString
	("HINT_COLUMN_POSITION"); // NOI18N
    public static final String PROP_MESSAGE_POSITION = ORBSettingsBundle.bundle.getString
	("PROP_MESSAGE_POSITION"); // NOI18N
    public static final String HINT_MESSAGE_POSITION = ORBSettingsBundle.bundle.getString
	("HINT_MESSAGE_POSITION"); // NOI18N
    public static final String PROP_IDL = ORBSettingsBundle.bundle.getString
	("PROP_IDL"); // NOI18N
    public static final String HINT_IDL = ORBSettingsBundle.bundle.getString
	("HINT_IDL"); // NOI18N
    public static final String PROP_PACKAGE_PARAM = ORBSettingsBundle.bundle.getString
	("PROP_PACKAGE_PARAM"); // NOI18N
    public static final String HINT_PACKAGE_PARAM = ORBSettingsBundle.bundle.getString
	("HINT_PACKAGE_PARAM"); // NOI18N
    public static final String PROP_DIR_PARAM = ORBSettingsBundle.bundle.getString
	("PROP_DIR_PARAM"); // NOI18N
    public static final String HINT_DIR_PARAM = ORBSettingsBundle.bundle.getString
	("HINT_DIR_PARAM"); // NOI18N
    public static final String PROP_TEMPLATE_TABLE = ORBSettingsBundle.bundle.getString
	("PROP_TEMPLATE_TABLE"); // NOI18N
    public static final String HINT_TEMPLATE_TABLE = ORBSettingsBundle.bundle.getString
	("HINT_TEMPLATE_TABLE"); // NOI18N
    public static final String PROP_TIE_PARAM = ORBSettingsBundle.bundle.getString
	("PROP_TIE_PARAM"); // NOI18N
    public static final String HINT_TIE_PARAM = ORBSettingsBundle.bundle.getString
	("HINT_TIE_PARAM"); // NOI18N
    public static final String PROP_IMPLBASE_PREFIX = ORBSettingsBundle.bundle.getString
	("PROP_IMPLBASE_PREFIX"); // NOI18N
    public static final String HINT_IMPLBASE_PREFIX = ORBSettingsBundle.bundle.getString
	("HINT_IMPLBASE_PREFIX"); // NOI18N
    public static final String PROP_IMPLBASE_POSTFIX = ORBSettingsBundle.bundle.getString
	("PROP_IMPLBASE_POSTFIX"); // NOI18N
    public static final String HINT_IMPLBASE_POSTFIX = ORBSettingsBundle.bundle.getString
	("HINT_IMPLBASE_POSTFIX"); // NOI18N
    public static final String PROP_EXTCLASS_PREFIX = ORBSettingsBundle.bundle.getString
	("PROP_EXTCLASS_PREFIX"); // NOI18N
    public static final String HINT_EXTCLASS_PREFIX = ORBSettingsBundle.bundle.getString
	("HINT_EXTCLASS_PREFIX"); // NOI18N
    public static final String PROP_EXTCLASS_POSTFIX = ORBSettingsBundle.bundle.getString
	("PROP_EXTCLASS_POSTFIX"); // NOI18N
    public static final String HINT_EXTCLASS_POSTFIX = ORBSettingsBundle.bundle.getString
	("HINT_EXTCLASS_POSTFIX"); // NOI18N
    public static final String PROP_TIEIMPL_PREFIX = ORBSettingsBundle.bundle.getString
	("PROP_TIEIMPL_PREFIX"); // NOI18N
    public static final String HINT_TIEIMPL_PREFIX = ORBSettingsBundle.bundle.getString
	("HINT_TIEIMPL_PREFIX"); // NOI18N
    public static final String PROP_TIEIMPL_POSTFIX = ORBSettingsBundle.bundle.getString
	("PROP_TIEIMPL_POSTFIX"); // NOI18N
    public static final String HINT_TIEIMPL_POSTFIX = ORBSettingsBundle.bundle.getString
	("HINT_TIEIMPL_POSTFIX"); // NOI18N
    public static final String PROP_IMPLINT_PREFIX = ORBSettingsBundle.bundle.getString
	("PROP_IMPLINT_PREFIX"); // NOI18N
    public static final String HINT_IMPLINT_PREFIX = ORBSettingsBundle.bundle.getString
	("HINT_IMPLINT_PREFIX"); // NOI18N
    public static final String PROP_IMPLINT_POSTFIX = ORBSettingsBundle.bundle.getString
	("PROP_IMPLINT_POSTFIX"); // NOI18N
    public static final String HINT_IMPLINT_POSTFIX = ORBSettingsBundle.bundle.getString
	("HINT_IMPLINT_POSTFIX"); // NOI18N
    public static final String PROP_VALUE_IMPL_PREFIX = ORBSettingsBundle.bundle.getString
	("PROP_VALUE_IMPL_PREFIX"); // NOI18N
    public static final String HINT_VALUE_IMPL_PREFIX = ORBSettingsBundle.bundle.getString
	("HINT_VALUE_IMPL_PREFIX"); // NOI18N
    public static final String PROP_VALUE_IMPL_POSTFIX = ORBSettingsBundle.bundle.getString
	("PROP_VALUE_IMPL_POSTFIX"); // NOI18N
    public static final String HINT_VALUE_IMPL_POSTFIX = ORBSettingsBundle.bundle.getString
	("HINT_VALUE_IMPL_POSTFIX"); // NOI18N
    public static final String PROP_VALUEFACTORY_IMPL_PREFIX 
	= ORBSettingsBundle.bundle.getString ("PROP_VALUEFACTORY_IMPL_PREFIX"); // NOI18N
    public static final String HINT_VALUEFACTORY_IMPL_PREFIX
	= ORBSettingsBundle.bundle.getString ("HINT_VALUEFACTORY_IMPL_PREFIX"); // NOI18N
    public static final String PROP_VALUEFACTORY_IMPL_POSTFIX
	= ORBSettingsBundle.bundle.getString ("PROP_VALUEFACTORY_IMPL_POSTFIX"); // NOI18N
    public static final String HINT_VALUEFACTORY_IMPL_POSTFIX
	= ORBSettingsBundle.bundle.getString ("HINT_VALUEFACTORY_IMPL_POSTFIX"); // NOI18N
    
    // added for new impl generator
    public static final String PROP_DELEGATION = ORBSettingsBundle.bundle.getString
	("PROP_DELEGATION"); // NOI18N
    public static final String HINT_DELEGATION = ORBSettingsBundle.bundle.getString
	("HINT_DELEGATION"); // NOI18N
    public static final String DELEGATION_STATIC = ORBSettingsBundle.bundle.getString
	("DELEGATION_STATIC"); // NOI18N
    public static final String DELEGATION_VIRTUAL = ORBSettingsBundle.bundle.getString
	("DELEGATION_VIRTUAL"); // NOI18N
    public static final String DELEGATION_NONE = ORBSettingsBundle.bundle.getString
	("DELEGATION_NONE"); // NOI18N
    
    public static final String PROP_USE_GUARDED_BLOCKS = ORBSettingsBundle.bundle.getString
	("PROP_USE_GUARDED_BLOCKS"); // NOI18N
    public static final String HINT_USE_GUARDED_BLOCKS = ORBSettingsBundle.bundle.getString
	("HINT_USE_GUARDED_BLOCKS"); // NOI18N


    public static final String CTL_UNSUPPORTED = ORBSettingsBundle.bundle.getString
	("CTL_UNSUPPORTED");

    // Added for CPP Support
    /*
      public static final String PROP_CPP_DIRECTORIES = ORBSettingsBundle.bundle.getString
      ("PROP_CPP_DIRECTORIES");
      public static final String HINT_CPP_DIRECTORIES = ORBSettingsBundle.bundle.getString
      ("HINT_CPP_DIRECTORIES");
      public static final String PROP_CPP_DEFINED_SYMBOLS = ORBSettingsBundle.bundle.getString
      ("PROP_CPP_DEFINED_SYMBOLS");
      public static final String HINT_CPP_DEFINED_SYMBOLS = ORBSettingsBundle.bundle.getString
      ("HINT_CPP_DEFINED_SYMBOLS");
      public static final String PROP_CPP_UNDEFINED_SYMBOLS 
      = ORBSettingsBundle.bundle.getString ("PROP_CPP_UNDEFINED_SYMBOLS");
      public static final String HINT_CPP_UNDEFINED_SYMBOLS
      = ORBSettingsBundle.bundle.getString ("HINT_CPP_UNDEFINED_SYMBOLS");
    */
    public static final String PROP_CPP_PARAMS = ORBSettingsBundle.bundle.getString
	("PROP_CPP_PARAMS");
    public static final String HINT_CPP_PARAMS = ORBSettingsBundle.bundle.getString
	("HINT_CPP_PARAMS");

    public static final String PROP_LOOK_FOR_IMPLEMENTATIONS 
	= ORBSettingsBundle.bundle.getString ("PROP_LOOK_FOR_IMPLEMENTATIONS");
    public static final String HINT_LOOK_FOR_IMPLEMENTATIONS
	= ORBSettingsBundle.bundle.getString ("HINT_LOOK_FOR_IMPLEMENTATIONS");
    public static final String PACKAGE = ORBSettingsBundle.bundle.getString
	("PACKAGE");
    public static final String PACKAGE_AND_SUB_PACKAGES = ORBSettingsBundle.bundle.getString
	("PACKAGE_AND_SUB_PACKAGES");
    public static final String FILESYSTEM = ORBSettingsBundle.bundle.getString
	("FILESYSTEM");
    public static final String REPOSITORY = ORBSettingsBundle.bundle.getString
	("REPOSITORY");

    public static final String PROP_TIE_CLASS_PREFIX = ORBSettingsBundle.bundle.getString
	("PROP_TIE_CLASS_PREFIX");
    public static final String HINT_TIE_CLASS_PREFIX = ORBSettingsBundle.bundle.getString
	("HINT_TIE_CLASS_PREFIX");
    public static final String PROP_TIE_CLASS_POSTFIX = ORBSettingsBundle.bundle.getString
	("PROP_TIE_CLASS_POSTFIX");
    public static final String HINT_TIE_CLASS_POSTFIX = ORBSettingsBundle.bundle.getString
	("HINT_TIE_CLASS_POSTFIX");
    
}

