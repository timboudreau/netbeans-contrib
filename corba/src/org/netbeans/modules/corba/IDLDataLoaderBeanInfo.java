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

import java.beans.*;
import java.awt.Image;
import java.util.ResourceBundle;

import org.openide.util.NbBundle;


/** BeanInfo for IDL loader.
*
* @author Karel Gardas
*/
public final class IDLDataLoaderBeanInfo extends SimpleBeanInfo {

    /** Icons for compiler settings objects. */
    private static Image icon;
    private static Image icon32;

    /** Propertydescriptors */
    private static PropertyDescriptor[] descriptors;


    // initialization of the array of descriptors
/*    static {
        final ResourceBundle bundle =
            NbBundle.getBundle(IDLDataLoaderBeanInfo.class);
	//System.out.println ("// initialization of the array of descriptors"); // NOI18N
        try {
            descriptors = new PropertyDescriptor[] {
	        new PropertyDescriptor("extensions", IDLDataLoader.class, "getExtensions", "setExtensions"), // NOI18N
		new PropertyDescriptor ("_M_hide_generated_files", IDLDataLoader.class, // NOI18N
					"getHide", "setHide") // NOI18N
            };
            descriptors[0].setDisplayName(bundle.getString("PROP_Extensions"));
            descriptors[0].setShortDescription(bundle.getString("HINT_Extensions"));
	    // hidden options for serialization
	    descriptors[1].setHidden (true);

	    //System.out.println ("// initialization of the array of descriptors"); // NOI18N
	} catch (IntrospectionException ex) {
            //throw new InternalError ();
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		ex.printStackTrace ();
        }
    }
*/
    /** Default constructor.
    */
    public IDLDataLoaderBeanInfo() {
    }

    /**
    * @return Returns an array of PropertyDescriptors
    * describing the editable properties supported by this bean.
    */
    public PropertyDescriptor[] getPropertyDescriptors () {
        if (descriptors == null) initializeDescriptors();
        return descriptors;
    }

    /** @param type Desired type of the icon
    * @return returns the Idl loader's icon
    */
    public Image getIcon(final int type) {
        if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) ||
                (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
            if (icon == null)
                icon = loadImage("/org/netbeans/modules/corba/settings/idl.gif"); // NOI18N
            return icon;
        } else {
            if (icon32 == null)
                icon32 = loadImage ("/org/netbeans/modules/corba/settings/idl32.gif"); // NOI18N
            return icon32;
        }
    }

    private static void initializeDescriptors () {
        final ResourceBundle bundle =
            NbBundle.getBundle(IDLDataLoaderBeanInfo.class);
	//System.out.println ("// initialization of the array of descriptors"); // NOI18N
        try {
            descriptors = new PropertyDescriptor[] {
	        new PropertyDescriptor("extensions", IDLDataLoader.class, "getExtensions", "setExtensions"), // NOI18N
		new PropertyDescriptor ("_M_hide_generated_files", IDLDataLoader.class, // NOI18N
					"getHide", "setHide") // NOI18N
            };
            descriptors[0].setDisplayName(bundle.getString("PROP_Extensions"));
            descriptors[0].setShortDescription(bundle.getString("HINT_Extensions"));
	    // hidden options for serialization
	    descriptors[1].setHidden (true);

	    //System.out.println ("// initialization of the array of descriptors"); // NOI18N
	} catch (IntrospectionException ex) {
            //throw new InternalError ();
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		ex.printStackTrace ();
        }
/*        final ResourceBundle bundle =
            NbBundle.getBundle(IDLDataLoaderBeanInfo.class);
	//System.out.println ("initializeDescriptors"); // NOI18N
        try {
            descriptors =  new PropertyDescriptor[] {
                               new PropertyDescriptor ("displayName", IDLDataLoader.class, // NOI18N
                                                       "getDisplayName", null), // NOI18N
                           };
            descriptors[0].setDisplayName(bundle.getString("PROP_Name"));
            descriptors[0].setShortDescription(bundle.getString("HINT_Name"));
        } catch (IntrospectionException e) {
	    if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		e.printStackTrace ();
        }
*/
    }
    
    public BeanInfo[] getAdditionalBeanInfo() {
        try {
            return new BeanInfo[] {
                java.beans.Introspector.getBeanInfo(org.openide.loaders.MultiFileLoader.class)
            };
        } catch (IntrospectionException e) {
            // ignore
        }
        return super.getAdditionalBeanInfo();
    }

}

/*
* <<Log>>
*  15   Gandalf   1.14        11/4/99  Karel Gardas    - update from CVS
*  14   Gandalf   1.13        11/4/99  Karel Gardas    update from CVS
*  13   Gandalf   1.12        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  12   Gandalf   1.11        10/13/99 Karel Gardas    Update from CVS
*  11   Gandalf   1.10        10/1/99  Karel Gardas    updates from CVS
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
