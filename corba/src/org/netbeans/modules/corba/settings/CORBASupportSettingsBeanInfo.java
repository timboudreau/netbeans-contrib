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

package com.netbeans.enterprise.modules.corba.settings;

import java.awt.Image;
import java.beans.*;
import java.util.ResourceBundle;

import com.netbeans.ide.util.NbBundle;
import com.netbeans.developer.editors.FileOnlyEditor;

/** BeanInfo for CORBASupportSettings - defines property editor
*
* @author Karel Gardas
* @version 0.11, March 27, 1999
*/

import com.netbeans.enterprise.modules.corba.*;

public class CORBASupportSettingsBeanInfo extends SimpleBeanInfo {
    /** Icons for compiler settings objects. */
    static Image icon;
    static Image icon32;

    //static final ResourceBundle bundle = NbBundle.getBundle(CORBASupportSettingsBeanInfo.class);

    /** Array of property descriptors. */
    private static PropertyDescriptor[] desc;

    // initialization of the array of descriptors
    static {
	try {
	    desc = new PropertyDescriptor[] {
		new PropertyDescriptor ("skels", CORBASupportSettings.class), 
		new PropertyDescriptor ("orb", CORBASupportSettings.class),
		new PropertyDescriptor ("params", CORBASupportSettings.class, 
					"getParams", "setParams"),
		new PropertyDescriptor ("_client_binding", CORBASupportSettings.class, 
					"getClientBinding", "setClientBinding"),
		new PropertyDescriptor ("_server_binding", CORBASupportSettings.class, 
					"getServerBinding", "setServerBinding"),

		// advanced settings

		new PropertyDescriptor ("_package_param", CORBASupportSettings.class, 
					"getPackageParam", "setPackageParam"),
		new PropertyDescriptor ("_dir_param", CORBASupportSettings.class, 
					"getDirParam",	"setDirParam"),
		new PropertyDescriptor ("_package_delimiter", CORBASupportSettings.class,
					"getPackageDelimiter", "setPackageDelimiter"),
		new PropertyDescriptor ("_error_expression", CORBASupportSettings.class,
					"getErrorExpression", "setErrorExpression"),
		new PropertyDescriptor ("_file_position", CORBASupportSettings.class,
					"getFilePosition", "setFilePosition"),
		new PropertyDescriptor ("_line_position", CORBASupportSettings.class,
					"getLinePosition", "setLinePosition"),
		new PropertyDescriptor ("_column_position", CORBASupportSettings.class,
					"getColumnPosition", "setColumnPosition"),
		new PropertyDescriptor ("_message_position", CORBASupportSettings.class,
					"getMessagePosition", "setMessagePosition"),
		new PropertyDescriptor ("idl", CORBASupportSettings.class),
		new PropertyDescriptor ("_table", CORBASupportSettings.class,
					"getRaplaceableStringsTable", "setReplaceableStringsTable"),
		new PropertyDescriptor ("_tie_param", CORBASupportSettings.class, 
					"getTieParam", "setTieParam") 
			    
	    };

	    desc[0].setDisplayName (CORBASupport.bundle.getString ("PROP_SKELS"));
	    desc[0].setShortDescription (CORBASupport.bundle.getString ("HINT_SKELS"));
	    desc[0].setPropertyEditorClass (SkelPropertyEditor.class);
	    desc[1].setDisplayName (CORBASupport.bundle.getString ("PROP_ORB"));
	    desc[1].setShortDescription (CORBASupport.bundle.getString ("HINT_ORB"));
	    desc[1].setPropertyEditorClass (OrbPropertyEditor.class);
	    desc[2].setDisplayName (CORBASupport.bundle.getString ("PROP_PARAMS"));
	    desc[2].setShortDescription (CORBASupport.bundle.getString ("HINT_PARAMS"));
	    desc[3].setDisplayName (CORBASupport.bundle.getString ("PROP_CLIENT_BINDING"));
	    desc[3].setShortDescription (CORBASupport.bundle.getString ("HINT_CLIENT_BINDING"));
	    desc[3].setPropertyEditorClass (ClientBindingPropertyEditor.class);
	    desc[4].setDisplayName (CORBASupport.bundle.getString ("PROP_SERVER_BINDING"));
	    desc[4].setShortDescription (CORBASupport.bundle.getString ("HINT_SERVER_BINDING"));
	    desc[4].setPropertyEditorClass (ServerBindingPropertyEditor.class);

	    // advanced settings

	    desc[5].setDisplayName (CORBASupport.bundle.getString ("PROP_PACKAGE_PARAM"));
	    desc[5].setShortDescription (CORBASupport.bundle.getString ("HINT_PACKAGE_PARAM"));
	    desc[5].setExpert (true);
	    desc[6].setDisplayName (CORBASupport.bundle.getString ("PROP_DIR_PARAM"));
	    desc[6].setShortDescription (CORBASupport.bundle.getString ("HINT_DIR_PARAM"));
	    desc[6].setExpert (true);
	    desc[7].setDisplayName ("Package delimiter");
	    desc[7].setExpert (true);
	    desc[8].setDisplayName ("Error Expression");
	    desc[8].setExpert (true);
	    desc[9].setDisplayName ("File Position");
	    desc[9].setExpert (true);
	    desc[10].setDisplayName ("Line Position");
	    desc[10].setExpert (true);
	    desc[11].setDisplayName ("Column Position");
	    desc[11].setExpert (true);
	    desc[12].setDisplayName ("Message Position");
	    desc[12].setExpert (true);
	    desc[13].setDisplayName (CORBASupport.bundle.getString ("PROP_IDL"));
            desc[13].setShortDescription (CORBASupport.bundle.getString ("HINT_IDL"));
            desc[13].setPropertyEditorClass (FileOnlyEditor.class);
	    desc[13].setExpert (true);
	    desc[14].setDisplayName ("Template table");
	    desc[14].setExpert (true);
	    desc[15].setDisplayName ("Tie parameter");
	    desc[15].setExpert (true);
	} catch (IntrospectionException ex) {
	    //throw new InternalError ();
	    ex.printStackTrace ();
	}
    }

    /**
     * loads icons
     */
    public CORBASupportSettingsBeanInfo () {
    }

    /** Returns the ExternalCompilerSettings' icon */
    public Image getIcon(int type) {
	if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) || (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
	    if (icon == null)
		icon = loadImage("/com/netbeans/enterprise/modules/corba/settings/orb.gif");
	    return icon;
	} else {
	    if (icon32 == null)
		icon32 = loadImage ("/com/netbeans/enterprise/modules/corba/settings/orb32.gif");
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
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */




