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
                       new PropertyDescriptor ("_M_skeletons", ORBSettings.class,
					       "getSkeletons", "setSkeletons"),
                       /* new PropertyDescriptor ("orb", ORBSettings.class), */
                       new PropertyDescriptor ("_M_params", ORBSettings.class,
                                               "getParams", "setParams"),
                       new PropertyDescriptor ("_M_client_binding", ORBSettings.class,
                                               "getClientBinding", "setClientBinding"),
                       new PropertyDescriptor ("_M_server_binding", ORBSettings.class,
                                               "getServerBinding", "setServerBinding"),

                       // advanced settings

                       new PropertyDescriptor ("_M_package_param", ORBSettings.class,
                                               "getPackageParam", "setPackageParam"),
                       new PropertyDescriptor ("_M_dir_param", ORBSettings.class,
                                               "getDirParam",	"setDirParam"),
                       new PropertyDescriptor ("_M_package_delimiter", ORBSettings.class,
                                               "getPackageDelimiter", "setPackageDelimiter"),
                       new PropertyDescriptor ("_M__error_expression", ORBSettings.class,
                                               "getErrorExpression", "setErrorExpression"),
                       new PropertyDescriptor ("_M_file_position", ORBSettings.class,
                                               "getFilePosition", "setFilePosition"),
                       new PropertyDescriptor ("_M_line_position", ORBSettings.class,
                                               "getLinePosition", "setLinePosition"),
                       new PropertyDescriptor ("_M_column_position", ORBSettings.class,
                                               "getColumnPosition", "setColumnPosition"),
                       new PropertyDescriptor ("_M_message_position", ORBSettings.class,
                                               "getMessagePosition", "setMessagePosition"),
                       new PropertyDescriptor ("idl", ORBSettings.class),
                       new PropertyDescriptor ("_M_table", ORBSettings.class,
                                               "getRaplaceableStringsTable", 
					       "setReplaceableStringsTable"),
                       new PropertyDescriptor ("_M_tie_param", ORBSettings.class,
                                               "getTieParam", "setTieParam"),
                       new PropertyDescriptor ("_M_impl_prefix", ORBSettings.class,
                                               "getImplBasePrefix", "setImplBasePrefix"),
                       new PropertyDescriptor ("_M_impl_postfix", ORBSettings.class,
                                               "getImplBasePostfix", "setImplBasePostfix"),
                       new PropertyDescriptor ("_M_ext_class_prefix", ORBSettings.class,
                                               "getExtClassPrefix", "setExtClassPrefix"),
                       new PropertyDescriptor ("_M_ext_class_postfix", ORBSettings.class,
                                               "getExtClassPostfix", "setExtClassPostfix"),
                       new PropertyDescriptor ("_M_tie_prefix", ORBSettings.class,
                                               "getTiePrefix", "setTiePrefix"),
                       new PropertyDescriptor ("_M_tie_postfix", ORBSettings.class,
                                               "getTiePostfix", "setTiePostfix"),
                       new PropertyDescriptor ("_M_impl_int_prefix", ORBSettings.class,
                                               "getImplIntPrefix", "setImplIntPrefix"),
                       new PropertyDescriptor ("_M_impl_int_postfix", ORBSettings.class,
                                               "getImplIntPostfix", "setImplIntPostfix"),
                       /* new PropertyDescriptor ("namingChildren", ORBSettings.class,
			  "getNamingServiceChildren", "setNamingServiceChildren"), */
                       new PropertyDescriptor ("_M_hide_generated_files", ORBSettings.class,
                                               "hideGeneratedFiles", "setHideGeneratedFiles"),
                       /* new PropertyDescriptor ("IRChildren", ORBSettings.class,
			  "getInterfaceRepositoryChildren",
			  "setInterfaceRepositoryChildren"), */

                       new PropertyDescriptor ("_M_generation", ORBSettings.class,
                                               "getGeneration", "setGeneration"),
                       new PropertyDescriptor ("_M_synchro", ORBSettings.class,
                                               "getSynchro", "setSynchro")

                   };

            desc[0].setDisplayName (CORBASupport.bundle.getString ("PROP_SKELS"));
            desc[0].setShortDescription (CORBASupport.bundle.getString ("HINT_SKELS"));
            desc[0].setPropertyEditorClass (SkelPropertyEditor.class);
            //desc[1].setDisplayName (CORBASupport.bundle.getString ("PROP_ORB"));
            //desc[1].setShortDescription (CORBASupport.bundle.getString ("HINT_ORB"));
            //desc[1].setPropertyEditorClass (OrbPropertyEditor.class);
            desc[1].setDisplayName (CORBASupport.bundle.getString ("PROP_PARAMS"));
            desc[1].setShortDescription (CORBASupport.bundle.getString ("HINT_PARAMS"));
            desc[2].setDisplayName (CORBASupport.bundle.getString ("PROP_CLIENT_BINDING"));
            desc[2].setShortDescription (CORBASupport.bundle.getString ("HINT_CLIENT_BINDING"));
            desc[2].setPropertyEditorClass (ClientBindingPropertyEditor.class);
            desc[3].setDisplayName (CORBASupport.bundle.getString ("PROP_SERVER_BINDING"));
            desc[3].setShortDescription (CORBASupport.bundle.getString ("HINT_SERVER_BINDING"));
            desc[3].setPropertyEditorClass (ServerBindingPropertyEditor.class);
            // advanced settings

            desc[4].setDisplayName (CORBASupport.bundle.getString ("PROP_PACKAGE_PARAM"));
            desc[4].setShortDescription (CORBASupport.bundle.getString ("HINT_PACKAGE_PARAM"));
            desc[4].setExpert (true);
            desc[5].setDisplayName (CORBASupport.bundle.getString ("PROP_DIR_PARAM"));
            desc[5].setShortDescription (CORBASupport.bundle.getString ("HINT_DIR_PARAM"));
            desc[5].setExpert (true);
            desc[6].setDisplayName ("Package delimiter");
            desc[6].setExpert (true);
            desc[7].setDisplayName ("Error Expression");
            desc[7].setExpert (true);
            desc[8].setDisplayName ("File Position");
            desc[8].setExpert (true);
            desc[9].setDisplayName ("Line Position");
            desc[9].setExpert (true);
            desc[10].setDisplayName ("Column Position");
            desc[10].setExpert (true);
            desc[11].setDisplayName ("Message Position");
            desc[11].setExpert (true);
            desc[12].setDisplayName (CORBASupport.bundle.getString ("PROP_IDL"));
            desc[12].setShortDescription (CORBASupport.bundle.getString ("HINT_IDL"));
            desc[12].setPropertyEditorClass (NbProcessDescriptorEditor.class);
            desc[12].setExpert (true);
            desc[13].setDisplayName ("Template table");
            desc[13].setExpert (true);
            desc[14].setDisplayName ("Tie parameter");
            desc[14].setExpert (true);
            desc[15].setDisplayName ("ImplBase Implementation Prefix");
            desc[15].setExpert (true);
            desc[16].setDisplayName ("ImplBase Implementation Postfix");
            desc[16].setExpert (true);
            desc[17].setDisplayName ("Extended Class Prefix");
            desc[17].setExpert (true);
            desc[18].setDisplayName ("Extended Class Postfix");
            desc[18].setExpert (true);
            desc[19].setDisplayName ("Tie Implementation Prefix");
            desc[19].setExpert (true);
            desc[20].setDisplayName ("Tie Implementation Postfix");
            desc[20].setExpert (true);
            desc[21].setDisplayName ("Implemented Interface Prefix");
            desc[21].setExpert (true);
            desc[22].setDisplayName ("Implemented Interface Postfix");
            desc[22].setExpert (true);
            //desc[23].setHidden (true);  // children of persistent NamingService Browser

            desc[23].setDisplayName ("Hide Generated Files");
            desc[23].setShortDescription ("Hide Generated Files from IDL file");

            //desc[25].setHidden (true); // children of persistent Interface Repository Browser

            desc[24].setDisplayName (CORBASupport.bundle.getString ("PROP_GENERATION"));
            desc[24].setShortDescription (CORBASupport.bundle.getString ("HINT_GENERATION"));
            desc[24].setPropertyEditorClass (GenerationPropertyEditor.class);
            desc[25].setDisplayName (CORBASupport.bundle.getString ("PROP_SYNCHRO"));
            desc[25].setShortDescription (CORBASupport.bundle.getString ("HINT_SYNCHRO"));
            desc[25].setPropertyEditorClass (SynchronizationPropertyEditor.class);
        } catch (IntrospectionException ex) {
            //throw new InternalError ();
	    if (Boolean.getBoolean ("netbeans.debug.exceptions"))
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
                icon = loadImage("/org/netbeans/modules/corba/settings/orb.gif");
            return icon;
        } else {
            if (icon32 == null)
                icon32 = loadImage ("/org/netbeans/modules/corba/settings/orb32.gif");
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




