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
        try {
            desc = new PropertyDescriptor[] {
                       new PropertyDescriptor ("_M_skeletons", ORBSettings.class, // NOI18N
					       "getSkeletons", "setSkeletons"), // NOI18N
                       /* new PropertyDescriptor ("orb", ORBSettings.class), */ // NOI18N
                       new PropertyDescriptor ("_M_params", ORBSettings.class, // NOI18N
                                               "getParams", "setParams"), // NOI18N
                       new PropertyDescriptor ("_M_client_binding", ORBSettings.class, // NOI18N
                                               "getClientBinding", "setClientBinding"), // NOI18N
                       new PropertyDescriptor ("_M_server_binding", ORBSettings.class, // NOI18N
                                               "getServerBinding", "setServerBinding"), // NOI18N

                       // advanced settings

                       new PropertyDescriptor ("_M_package_param", ORBSettings.class, // NOI18N
                                               "getPackageParam", "setPackageParam"), // NOI18N
                       new PropertyDescriptor ("_M_dir_param", ORBSettings.class, // NOI18N
                                               "getDirParam",	"setDirParam"), // NOI18N
                       new PropertyDescriptor ("_M_package_delimiter", ORBSettings.class, // NOI18N
                                               "getPackageDelimiter", "setPackageDelimiter"), // NOI18N
                       new PropertyDescriptor ("_M__error_expression", ORBSettings.class, // NOI18N
                                               "getErrorExpression", "setErrorExpression"), // NOI18N
                       new PropertyDescriptor ("_M_file_position", ORBSettings.class, // NOI18N
                                               "getFilePosition", "setFilePosition"), // NOI18N
                       new PropertyDescriptor ("_M_line_position", ORBSettings.class, // NOI18N
                                               "getLinePosition", "setLinePosition"), // NOI18N
                       new PropertyDescriptor ("_M_column_position", ORBSettings.class, // NOI18N
                                               "getColumnPosition", "setColumnPosition"), // NOI18N
                       new PropertyDescriptor ("_M_message_position", ORBSettings.class, // NOI18N
                                               "getMessagePosition", "setMessagePosition"), // NOI18N
                       new PropertyDescriptor ("idl", ORBSettings.class), // NOI18N
                       new PropertyDescriptor ("_M_table", ORBSettings.class, // NOI18N
                                               "getRaplaceableStringsTable", // NOI18N
					       "setReplaceableStringsTable"), // NOI18N
                       new PropertyDescriptor ("_M_tie_param", ORBSettings.class, // NOI18N
                                               "getTieParam", "setTieParam"), // NOI18N
                       new PropertyDescriptor ("_M_impl_prefix", ORBSettings.class, // NOI18N
                                               "getImplBasePrefix", "setImplBasePrefix"), // NOI18N
                       new PropertyDescriptor ("_M_impl_postfix", ORBSettings.class, // NOI18N
                                               "getImplBasePostfix", "setImplBasePostfix"), // NOI18N
                       new PropertyDescriptor ("_M_ext_class_prefix", ORBSettings.class, // NOI18N
                                               "getExtClassPrefix", "setExtClassPrefix"), // NOI18N
                       new PropertyDescriptor ("_M_ext_class_postfix", ORBSettings.class, // NOI18N
                                               "getExtClassPostfix", "setExtClassPostfix"), // NOI18N
                       new PropertyDescriptor ("_M_tie_prefix", ORBSettings.class, // NOI18N
                                               "getTiePrefix", "setTiePrefix"), // NOI18N
                       new PropertyDescriptor ("_M_tie_postfix", ORBSettings.class, // NOI18N
                                               "getTiePostfix", "setTiePostfix"), // NOI18N
                       new PropertyDescriptor ("_M_impl_int_prefix", ORBSettings.class, // NOI18N
                                               "getImplIntPrefix", "setImplIntPrefix"), // NOI18N
                       new PropertyDescriptor ("_M_impl_int_postfix", ORBSettings.class, // NOI18N
                                               "getImplIntPostfix", "setImplIntPostfix"), // NOI18N
                       /* new PropertyDescriptor ("namingChildren", ORBSettings.class,
			  "getNamingServiceChildren", "setNamingServiceChildren"), */ // NOI18N
                       new PropertyDescriptor ("_M_hide_generated_files", ORBSettings.class, // NOI18N
                                               "hideGeneratedFiles", "setHideGeneratedFiles"), // NOI18N
                       /* new PropertyDescriptor ("IRChildren", ORBSettings.class,
			  "getInterfaceRepositoryChildren",
			  "setInterfaceRepositoryChildren"), */ // NOI18N

                       new PropertyDescriptor ("_M_generation", ORBSettings.class, // NOI18N
                                               "getGeneration", "setGeneration"), // NOI18N
                       new PropertyDescriptor ("_M_synchro", ORBSettings.class, // NOI18N
                                               "getSynchro", "setSynchro") // NOI18N

                   };

            desc[0].setDisplayName (ORBSettingsBundle.PROP_SKELS);
            desc[0].setShortDescription (ORBSettingsBundle.HINT_SKELS);
            desc[0].setPropertyEditorClass (SkelPropertyEditor.class);
            //desc[1].setDisplayName (CORBASupport.bundle.getString ("PROP_ORB"));
            //desc[1].setShortDescription (CORBASupport.bundle.getString ("HINT_ORB"));
            //desc[1].setPropertyEditorClass (OrbPropertyEditor.class);
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
        } catch (IntrospectionException ex) {
            //throw new InternalError ();
	    if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
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




