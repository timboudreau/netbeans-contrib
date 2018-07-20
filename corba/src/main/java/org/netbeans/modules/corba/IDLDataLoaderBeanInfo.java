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
            org.openide.ErrorManager.getDefault().notify(ex);
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
            org.openide.ErrorManager.getDefault().notify(ex);
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
	    org.openide.ErrorManager.getDefault().notify(e);
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
