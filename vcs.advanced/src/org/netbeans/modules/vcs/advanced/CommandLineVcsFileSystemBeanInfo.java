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

package com.netbeans.enterprise.modules.vcs.cmdline;
import java.beans.*;
import java.util.ResourceBundle;

import com.netbeans.ide.util.NbBundle;
import com.netbeans.ide.filesystems.*;
import com.netbeans.enterprise.modules.vcs.util.*;

/** BeanInfo to CommandLineVcsFileSystem.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class CommandLineVcsFileSystemBeanInfo extends SimpleBeanInfo {
  private static Debug E=new Debug("CommandLineVcsFileSystemBeanInfo",true);
  private static Debug D=E;

  /** Array of property descriptors. */
  private static PropertyDescriptor[] desc;

  static {
    PropertyDescriptor rootDirectory=null;
    PropertyDescriptor debug=null;
    PropertyDescriptor variables=null;
    PropertyDescriptor commands=null;

    try {
      rootDirectory=new PropertyDescriptor
	("rootDirectory", CommandLineVcsFileSystem.class, "getRootDirectory", "setRootDirectory");
      debug=new PropertyDescriptor
	("debug",CommandLineVcsFileSystem.class,"getDebug","setDebug");
      
      variables=new PropertyDescriptor
	("variables",CommandLineVcsFileSystem.class,"getVariables","setVariables");
      variables.setPropertyEditorClass
	(com.netbeans.enterprise.modules.vcs.cmdline.UserVariablesEditor.class);

      commands=new PropertyDescriptor
	("commands",CommandLineVcsFileSystem.class,"getCommands","setCommands");
      commands.setPropertyEditorClass
	(com.netbeans.enterprise.modules.vcs.cmdline.UserCommandsEditor.class);

      desc = new PropertyDescriptor[] {
	rootDirectory, debug, variables, commands
      };

      ResourceBundle bundle = NbBundle.getBundle
	("com.netbeans.enterprise.modules.vcs.cmdline.Bundle");
      rootDirectory.setDisplayName      (bundle.getString("PROP_rootDirectory"));
      rootDirectory.setShortDescription (bundle.getString("HINT_rootDirectory"));
      debug.setDisplayName              (bundle.getString("PROP_debug"));
      debug.setShortDescription         (bundle.getString("HINT_debug"));
      variables.setDisplayName          (bundle.getString("PROP_variables"));
      variables.setShortDescription     (bundle.getString("HINT_variables"));
      commands.setDisplayName           (bundle.getString("PROP_commands"));
      commands.setShortDescription      (bundle.getString("HINT_commands"));

    } catch (IntrospectionException ex) {
      ex.printStackTrace ();
    }
  }

  /* Descriptor of valid properties
  * @return array of properties
  */
  public PropertyDescriptor[] getPropertyDescriptors () {
    return desc;
  }
}

/*
* <<Log>>
*  3    Gandalf   1.2         4/26/99  Michal Fadljevic 
*  2    Gandalf   1.1         4/21/99  Michal Fadljevic 
*  1    Gandalf   1.0         4/15/99  Michal Fadljevic 
* $
*/
