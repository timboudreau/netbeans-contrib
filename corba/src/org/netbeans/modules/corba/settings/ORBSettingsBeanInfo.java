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

import java.awt.Image;
import java.beans.*;
import java.util.ResourceBundle;

import org.openide.util.NbBundle;
//import org.netbeans.beaninfo.editors.FileOnlyEditor;
import org.netbeans.beaninfo.editors.NbProcessDescriptorEditor;


/** BeanInfo for ORBSettings - defines property editor
*
* @author Karel Gardas
* @version 0.11, March 27, 1999
*/

import org.netbeans.modules.corba.*;

public class ORBSettingsBeanInfo extends SimpleBeanInfo {
    /** Icons for compiler settings objects. */
    static Image icon;
    static Image icon32;

    //static final ResourceBundle bundle = NbBundle.getBundle(ORBSettingsBeanInfo.class);

    /** Array of property descriptors. */
    private static PropertyDescriptor[] desc;

    // initialization of the array of descriptors
    static {
	//System.out.println ("ORBSettingsBeanInfo::static....");
        try {
            desc = new PropertyDescriptor[] {
                       new PropertyDescriptor ("_M_skeletons", ORBSettings.class, // NOI18N
					       "getSkeletons", "setSkeletons"), // NOI18N
                       /* new PropertyDescriptor ("orb", ORBSettings.class), */ // NOI18N
                       new PropertyDescriptor ("_M_params", ORBSettings.class, // NOI18N
                                               "getParams", "setParams"), // NOI18N
                       new PropertyDescriptor ("_M_client_binding", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getClientBinding", // NOI18N
					       "setClientBinding"), // NOI18N
                       new PropertyDescriptor ("_M_server_binding", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getServerBinding", // NOI18N
					       "setServerBinding"), // NOI18N

                       // advanced settings

                       new PropertyDescriptor ("_M_package_param", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getPackageParam", // NOI18N
					       "setPackageParam"), // NOI18N
                       new PropertyDescriptor ("_M_dir_param", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getDirParam", "setDirParam"), // NOI18N
                       new PropertyDescriptor ("_M_package_delimiter", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getPackageDelimiter", // NOI18N
					       "setPackageDelimiter"), // NOI18N
                       new PropertyDescriptor ("_M__error_expression", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getErrorExpression", // NOI18N
					       "setErrorExpression"), // NOI18N
                       new PropertyDescriptor ("_M_file_position", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getFilePosition", // NOI18N
					       "setFilePosition"), // NOI18N
                       new PropertyDescriptor ("_M_line_position", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getLinePosition", // NOI18N
					       "setLinePosition"), // NOI18N
                       new PropertyDescriptor ("_M_column_position", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getColumnPosition", // NOI18N
					       "setColumnPosition"), // NOI18N
                       new PropertyDescriptor ("_M_message_position", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getMessagePosition", // NOI18N
					       "setMessagePosition"), // NOI18N
                       new PropertyDescriptor ("idl", ORBSettings.class, // NOI18N
					       "getIdl", "setIdl"), // NOI18N
                       new PropertyDescriptor ("_M_table", ORBSettings.class, // NOI18N
                                               "getReplaceableStringsTable", // NOI18N
					       "setReplaceableStringsTable"), // NOI18N
                       new PropertyDescriptor ("_M_tie_param", ORBSettings.class, // NOI18N
                                               "getTieParam", "setTieParam"), // NOI18N
                       new PropertyDescriptor ("_M_implbase_impl_prefix", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getImplBaseImplPrefix", // NOI18N
					       "setImplBaseImplPrefix"), // NOI18N
                       new PropertyDescriptor ("_M_implbase_impl_postfix", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getImplBaseImplPostfix", // NOI18N
					       "setImplBaseImplPostfix"), // NOI18N
                       new PropertyDescriptor ("_M_ext_class_prefix", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getExtClassPrefix", // NOI18N
					       "setExtClassPrefix"), // NOI18N
                       new PropertyDescriptor ("_M_ext_class_postfix", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getExtClassPostfix", // NOI18N
					       "setExtClassPostfix"), // NOI18N
                       new PropertyDescriptor ("_M_tie_impl_prefix", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getTieImplPrefix", // NOI18N
					       "setTieImplPrefix"), // NOI18N
                       new PropertyDescriptor ("_M_tie_impl_postfix", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getTieImplPostfix", // NOI18N
					       "setTieImplPostfix"), // NOI18N
                       new PropertyDescriptor ("_M_impl_int_prefix", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getImplIntPrefix", // NOI18N
					       "setImplIntPrefix"), // NOI18N
                       new PropertyDescriptor ("_M_impl_int_postfix", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "getImplIntPostfix", // NOI18N
					       "setImplIntPostfix"), // NOI18N
                       new PropertyDescriptor ("_M_hide_generated_files", // NOI18N
					       ORBSettings.class, // NOI18N
                                               "hideGeneratedFiles", // NOI18N
					       "setHideGeneratedFiles"), // NOI18N
                       new PropertyDescriptor ("_M_generation", ORBSettings.class, // NOI18N
                                               "getGeneration", "setGeneration"), // NOI18N
                       new PropertyDescriptor ("_M_synchro", ORBSettings.class, // NOI18N
                                               "getSynchro", "setSynchro"), // NOI18N
		       
		       // added for new implgenerator 

                       new PropertyDescriptor ("_M_delegation", ORBSettings.class, // NOI18N
                                               "getDelegation", "setDelegation"), // NOI18N
                       new PropertyDescriptor ("_M_use_guarded_blocks", // NOI18N 
					       ORBSettings.class, // NOI18N
                                               "getUseGuardedBlocks", // NOI18N
					       "setUseGuardedBlocks"), // NOI18N
		       new PropertyDescriptor ("_M_value_impl_prefix", // NOI18N
					       ORBSettings.class,
					       "getValueImplPrefix", // NOI18N
					       "setValueImplPrefix"),
		       new PropertyDescriptor ("_M_value_impl_postfix", // NOI18N
					       ORBSettings.class,
					       "getValueImplPostfix", // NOI18N
					       "setValueImplPostfix"),
		       new PropertyDescriptor ("_M_valuefactory_impl_prefix", // NOI18N
					       ORBSettings.class,
					       "getValueFactoryImplPrefix", // NOI18N
					       "setValueFactoryImplPrefix"),
		       new PropertyDescriptor ("_M_valuefactory_impl_postfix", // NOI18N
					       ORBSettings.class,
					       "getValueFactoryImplPostfix", // NOI18N
					       "setValueFactoryImplPostfix"),
		       /*
			 new PropertyDescriptor ("_M_cpp_directories", // NOI18N
			 ORBSettings.class,
			 "getCPPDirectories", // NOI18N
			 "setCPPDirectories"),
			 new PropertyDescriptor ("_M_cpp_defined_symbols", // NOI18N
			 ORBSettings.class,
			 "getCPPDefinedSymbols", // NOI18N
			 "setCPPDefinedSymbols"),
			 new PropertyDescriptor ("_M_cpp_undefined_symbols", // NOI18N
			 ORBSettings.class,
			 "getCPPUndefinedSymbols", // NOI18N
			 "setCPPUndefinedSymbols")
		       */
		       new PropertyDescriptor ("_M_cpp_params", // NOI18N
					       ORBSettings.class,
					       "getCPPParams", // NOI18N
					       "setCPPParams"),
		       new PropertyDescriptor ("_M_find_method", // NOI18N
					       ORBSettings.class,
					       "getFindMethod", // NOI18N
					       "setFindMethod"),
		       new PropertyDescriptor ("_M_tie_class_prefix", // NOI18N
					       ORBSettings.class,
					       "getTieClassPrefix", // NOI18N
					       "setTieClassPrefix"),
		       new PropertyDescriptor ("_M_tie_class_postfix", // NOI18N
					       ORBSettings.class,
					       "getTieClassPostfix", // NOI18N
					       "setTieClassPostfix")
			   };

            desc[0].setDisplayName (ORBSettingsBundle.PROP_SKELS);
            desc[0].setShortDescription (ORBSettingsBundle.HINT_SKELS);
            desc[0].setPropertyEditorClass (SkelPropertyEditor.class);
            desc[1].setDisplayName (ORBSettingsBundle.PROP_PARAMS);
            desc[1].setShortDescription (ORBSettingsBundle.HINT_PARAMS);
            desc[2].setDisplayName (ORBSettingsBundle.PROP_CLIENT_BINDING);
            desc[2].setShortDescription (ORBSettingsBundle.HINT_CLIENT_BINDING);
            desc[2].setPropertyEditorClass (ClientBindingPropertyEditor.class);
            desc[3].setDisplayName (ORBSettingsBundle.PROP_SERVER_BINDING);
            desc[3].setShortDescription (ORBSettingsBundle.HINT_SERVER_BINDING);
            desc[3].setPropertyEditorClass (ServerBindingPropertyEditor.class);

            // advanced settings

            desc[4].setDisplayName (ORBSettingsBundle.PROP_PACKAGE_PARAM);
            desc[4].setShortDescription (ORBSettingsBundle.HINT_PACKAGE_PARAM);
            desc[4].setExpert (true);
            desc[5].setDisplayName (ORBSettingsBundle.PROP_DIR_PARAM);
            desc[5].setShortDescription (ORBSettingsBundle.HINT_DIR_PARAM);
            desc[5].setExpert (true);
            //desc[6].setDisplayName ("Package delimiter"); // NOI18N
            desc[6].setDisplayName (ORBSettingsBundle.PROP_PACKAGE_DELIMITER);
	    desc[6].setShortDescription (ORBSettingsBundle.HINT_PACKAGE_DELIMITER);
            desc[6].setExpert (true);
            //desc[7].setDisplayName ("Error Expression"); // NOI18N
            desc[7].setDisplayName (ORBSettingsBundle.PROP_ERROR_EXPRESSION);
	    desc[7].setShortDescription (ORBSettingsBundle.HINT_ERROR_EXPRESSION);
            desc[7].setExpert (true);
            //desc[8].setDisplayName ("File Position"); // NOI18N
            desc[8].setDisplayName (ORBSettingsBundle.PROP_FILE_POSITION);
	    desc[8].setShortDescription (ORBSettingsBundle.HINT_FILE_POSITION);
            desc[8].setExpert (true);
            //desc[9].setDisplayName ("Line Position"); // NOI18N
            desc[9].setDisplayName (ORBSettingsBundle.PROP_LINE_POSITION);
	    desc[9].setShortDescription (ORBSettingsBundle.HINT_LINE_POSITION);
            desc[9].setExpert (true);
            //desc[10].setDisplayName ("Column Position"); // NOI18N
            desc[10].setDisplayName (ORBSettingsBundle.PROP_COLUMN_POSITION);
	    desc[10].setShortDescription (ORBSettingsBundle.HINT_COLUMN_POSITION);
            desc[10].setExpert (true);
            //desc[11].setDisplayName ("Message Position"); // NOI18N
            desc[11].setDisplayName (ORBSettingsBundle.PROP_MESSAGE_POSITION);
	    desc[11].setShortDescription (ORBSettingsBundle.HINT_MESSAGE_POSITION);
            desc[11].setExpert (true);
            desc[12].setDisplayName (ORBSettingsBundle.PROP_IDL);
            desc[12].setShortDescription (ORBSettingsBundle.HINT_IDL);
            desc[12].setPropertyEditorClass (NbProcessDescriptorEditor.class);
            desc[12].setExpert (true);
            //desc[13].setDisplayName ("Template table"); // NOI18N
            desc[13].setDisplayName (ORBSettingsBundle.PROP_TEMPLATE_TABLE);
	    desc[13].setShortDescription (ORBSettingsBundle.HINT_TEMPLATE_TABLE);
            desc[13].setExpert (true);
            //desc[14].setDisplayName ("Tie parameter"); // NOI18N
            desc[14].setDisplayName (ORBSettingsBundle.PROP_TIE_PARAM);
	    desc[14].setShortDescription (ORBSettingsBundle.HINT_TIE_PARAM);
            desc[14].setExpert (true);
            //desc[15].setDisplayName ("ImplBase Implementation Prefix"); // NOI18N
            desc[15].setDisplayName (ORBSettingsBundle.PROP_IMPLBASE_PREFIX);
	    desc[15].setShortDescription (ORBSettingsBundle.HINT_IMPLBASE_PREFIX);
            desc[15].setExpert (true);
            //desc[16].setDisplayName ("ImplBase Implementation Postfix"); // NOI18N
            desc[16].setDisplayName (ORBSettingsBundle.PROP_IMPLBASE_POSTFIX);
	    desc[16].setShortDescription (ORBSettingsBundle.HINT_IMPLBASE_POSTFIX);
            desc[16].setExpert (true);
            //desc[17].setDisplayName ("Extended Class Prefix"); // NOI18N
            desc[17].setDisplayName (ORBSettingsBundle.PROP_EXTCLASS_PREFIX);
	    desc[17].setShortDescription (ORBSettingsBundle.HINT_EXTCLASS_PREFIX);
            desc[17].setExpert (true);
            //desc[18].setDisplayName ("Extended Class Postfix"); // NOI18N
            desc[18].setDisplayName (ORBSettingsBundle.PROP_EXTCLASS_POSTFIX);
	    desc[18].setShortDescription (ORBSettingsBundle.HINT_EXTCLASS_POSTFIX);
            desc[18].setExpert (true);
            //desc[19].setDisplayName ("Tie Implementation Prefix"); // NOI18N
            desc[19].setDisplayName (ORBSettingsBundle.PROP_TIEIMPL_PREFIX);
	    desc[19].setShortDescription (ORBSettingsBundle.HINT_TIEIMPL_PREFIX);
            desc[19].setExpert (true);
            //desc[20].setDisplayName ("Tie Implementation Postfix"); // NOI18N
            desc[20].setDisplayName (ORBSettingsBundle.PROP_TIEIMPL_POSTFIX);
	    desc[20].setShortDescription (ORBSettingsBundle.HINT_TIEIMPL_POSTFIX);
            desc[20].setExpert (true);
            //desc[21].setDisplayName ("Implemented Interface Prefix"); // NOI18N
	    desc[21].setDisplayName (ORBSettingsBundle.PROP_IMPLINT_PREFIX);
	    desc[21].setShortDescription (ORBSettingsBundle.HINT_IMPLINT_PREFIX);
            desc[21].setExpert (true);
            //desc[22].setDisplayName ("Implemented Interface Postfix"); // NOI18N
            desc[22].setDisplayName (ORBSettingsBundle.PROP_IMPLINT_POSTFIX);
	    desc[22].setShortDescription (ORBSettingsBundle.HINT_IMPLINT_POSTFIX);
            desc[22].setExpert (true);
            //desc[23].setHidden (true);  // children of persistent NamingService Browser

            //desc[23].setDisplayName ("Hide Generated Files"); // NOI18N
            desc[23].setDisplayName (ORBSettingsBundle.PROP_HIDE);
            desc[23].setShortDescription (ORBSettingsBundle.HINT_HIDE);

            //desc[25].setHidden (true); // children of persistent Interface Repository Browser

            desc[24].setDisplayName (ORBSettingsBundle.PROP_GENERATION);
            desc[24].setShortDescription (ORBSettingsBundle.HINT_GENERATION);
            desc[24].setPropertyEditorClass (GenerationPropertyEditor.class);
            desc[25].setDisplayName (ORBSettingsBundle.PROP_SYNCHRO);
            desc[25].setShortDescription (ORBSettingsBundle.HINT_SYNCHRO);
            desc[25].setPropertyEditorClass (SynchronizationPropertyEditor.class);
            desc[26].setDisplayName (ORBSettingsBundle.PROP_DELEGATION);
            desc[26].setShortDescription (ORBSettingsBundle.HINT_DELEGATION);
            desc[26].setPropertyEditorClass (DelegationPropertyEditor.class);
            desc[27].setDisplayName (ORBSettingsBundle.PROP_USE_GUARDED_BLOCKS);
            desc[27].setShortDescription (ORBSettingsBundle.HINT_USE_GUARDED_BLOCKS);
            //desc[27].setPropertyEditorClass (SynchronizationPropertyEditor.class);


	    desc[28].setDisplayName (ORBSettingsBundle.PROP_VALUE_IMPL_PREFIX);
	    desc[28].setShortDescription (ORBSettingsBundle.HINT_VALUE_IMPL_PREFIX);
	    desc[28].setExpert (true);
	    desc[29].setDisplayName (ORBSettingsBundle.PROP_VALUE_IMPL_POSTFIX);
	    desc[29].setShortDescription (ORBSettingsBundle.HINT_VALUE_IMPL_POSTFIX);
	    desc[29].setExpert (true);
	    desc[30].setDisplayName (ORBSettingsBundle.PROP_VALUEFACTORY_IMPL_PREFIX);
	    desc[30].setShortDescription (ORBSettingsBundle.HINT_VALUEFACTORY_IMPL_PREFIX);
	    desc[30].setExpert (true);
	    desc[31].setDisplayName (ORBSettingsBundle.PROP_VALUEFACTORY_IMPL_POSTFIX);
	    desc[31].setShortDescription (ORBSettingsBundle.HINT_VALUEFACTORY_IMPL_POSTFIX);
	    desc[31].setExpert (true);

	    // CPP Options
	    /*
	      desc[32].setDisplayName (ORBSettingsBundle.PROP_CPP_DIRECTORIES);
	      desc[32].setShortDescription (ORBSettingsBundle.HINT_CPP_DIRECTORIES);
	      desc[33].setDisplayName (ORBSettingsBundle.PROP_CPP_DEFINED_SYMBOLS);
	      desc[33].setShortDescription (ORBSettingsBundle.HINT_CPP_DEFINED_SYMBOLS);
	      desc[34].setDisplayName (ORBSettingsBundle.PROP_CPP_UNDEFINED_SYMBOLS);
	      desc[34].setShortDescription (ORBSettingsBundle.HINT_CPP_UNDEFINED_SYMBOLS);
	    */
	    desc[32].setDisplayName (ORBSettingsBundle.PROP_CPP_PARAMS);
	    desc[32].setShortDescription (ORBSettingsBundle.HINT_CPP_PARAMS);
	    // find method
	    desc[33].setDisplayName (ORBSettingsBundle.PROP_LOOK_FOR_IMPLEMENTATIONS);
	    desc[33].setShortDescription (ORBSettingsBundle.HINT_LOOK_FOR_IMPLEMENTATIONS);
            desc[33].setPropertyEditorClass (FinderPropertyEditor.class);

	    desc[34].setDisplayName (ORBSettingsBundle.PROP_TIE_CLASS_PREFIX);
	    desc[34].setShortDescription (ORBSettingsBundle.HINT_TIE_CLASS_PREFIX);
	    desc[34].setExpert (true);
	    desc[35].setDisplayName (ORBSettingsBundle.PROP_TIE_CLASS_POSTFIX);
	    desc[35].setShortDescription (ORBSettingsBundle.HINT_TIE_CLASS_POSTFIX);
	    desc[35].setExpert (true);
	    
        } catch (IntrospectionException ex) {
            //throw new InternalError ();
	    //if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
	    ex.printStackTrace ();
        }
    }

    /**
     * loads icons
     */
    public ORBSettingsBeanInfo () {
    }

    /** Returns the ExternalCompilerSettings' icon */
    public Image getIcon(int type) {
        if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) || (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
            if (icon == null)
                icon = loadImage("/org/netbeans/modules/corba/settings/orb.gif"); // NOI18N
            return icon;
        } else {
            if (icon32 == null)
                icon32 = loadImage ("/org/netbeans/modules/corba/settings/orb32.gif"); // NOI18N
            return icon32;
        }
    }

    /** Descriptor of valid properties
     * @return array of properties
     */
    public PropertyDescriptor[] getPropertyDescriptors () {
	//System.out.println ("ORBSettingsBeanInfo::getPropertyDescriptors () -> " + desc);
        return desc;
    }
}

/*
 * <<Log>>
 *  14   Gandalf   1.13        11/4/99  Karel Gardas    - update from CVS
 *  13   Gandalf   1.12        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  12   Gandalf   1.11        10/1/99  Karel Gardas    updates from CVS
 *  11   Gandalf   1.10        8/7/99   Karel Gardas    added option for hidding
 *       generated files
 *  10   Gandalf   1.9         8/3/99   Karel Gardas    
 *  9    Gandalf   1.8         7/10/99  Karel Gardas    
 *  8    Gandalf   1.7         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/28/99  Karel Gardas    
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */




