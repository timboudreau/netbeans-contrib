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

import org.openide.util.NbBundle;
import org.openide.filesystems.*;

import com.netbeans.developer.modules.vcs.util.*;

/** BeanInfo for CommandLineVcsFileSystem.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class CommandLineVcsFileSystemBeanInfo extends SimpleBeanInfo {
  private static Debug E=new Debug("CommandLineVcsFileSystemBeanInfo", true);
  private static Debug D=E;

  /** Array of property descriptors. */
  private static PropertyDescriptor[] desc;

  /** Icon for VCS filesystem. */
  private static java.awt.Image icon;
  private static java.awt.Image icon32;

  static {
    PropertyDescriptor rootDirectory=null;
    PropertyDescriptor debug=null;
    PropertyDescriptor variables=null;
    PropertyDescriptor commands=null;
    PropertyDescriptor cacheId=null;
    PropertyDescriptor config=null;
    

    try {
      rootDirectory=new PropertyDescriptor
	("rootDirectory", CommandLineVcsFileSystem.class, "getRootDirectory", "setRootDirectory");
      debug=new PropertyDescriptor
	("debug",CommandLineVcsFileSystem.class,"getDebug","setDebug");
      
      variables=new PropertyDescriptor
	("variables",CommandLineVcsFileSystem.class,"getVariables","setVariables");
      variables.setPropertyEditorClass
	(com.netbeans.developer.modules.vcs.UserVariablesEditor.class);

      commands=new PropertyDescriptor
	("commands",CommandLineVcsFileSystem.class,"getCommands","setCommands");
      commands.setPropertyEditorClass
	(com.netbeans.enterprise.modules.vcs.cmdline.UserCommandsEditor.class);
      
      cacheId=new PropertyDescriptor
	("cacheId",CommandLineVcsFileSystem.class,"getCacheId",null);

      config=new PropertyDescriptor
	("config",CommandLineVcsFileSystem.class,"getConfig",null);


      desc = new PropertyDescriptor[] {
	rootDirectory, debug, variables, commands, cacheId, config
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
      cacheId.setDisplayName            (bundle.getString("PROP_cacheId"));
      cacheId.setShortDescription       (bundle.getString("HINT_cacheId"));
      config.setDisplayName             (bundle.getString("PROP_config"));
      config.setShortDescription        (bundle.getString("HINT_config"));

    } catch (IntrospectionException ex) {
      ex.printStackTrace ();
    }
  }

  /* Provides the VCSFileSystem's icon */
  public java.awt.Image getIcon(int type) {
    if (icon == null) {
      icon = loadImage("/com/netbeans/enterprise/modules/vcs/cmdline/vcs2.gif");
      icon32 = icon;
    }
    if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) || (type == java.beans.BeanInfo.ICON_MONO_16x16))
      return icon;
    else
      return icon32;
  }

  /* Descriptor of valid properties
  * @return array of properties
  */
  public PropertyDescriptor[] getPropertyDescriptors () {
    return desc;
  }


  public BeanDescriptor getBeanDescriptor(){
    D.deb("getBeanDescriptor()");
    return new BeanDescriptor(CommandLineVcsFileSystem.class, com.netbeans.developer.modules.vcs.VcsCustomizer.class);
  }
  
}

/*
* <<Log>>
*  15   Gandalf   1.14        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  14   Gandalf   1.13        9/30/99  Pavel Buzek     
*  13   Gandalf   1.12        9/8/99   Pavel Buzek     class model changed, 
*       customization improved, several bugs fixed
*  12   Gandalf   1.11        8/31/99  Pavel Buzek     
*  11   Gandalf   1.10        8/7/99   Ian Formanek    Icon for VCS Filesystem
*  10   Gandalf   1.9         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  9    Gandalf   1.8         5/19/99  Michal Fadljevic 
*  8    Gandalf   1.7         5/14/99  Michal Fadljevic 
*  7    Gandalf   1.6         5/4/99   Michal Fadljevic 
*  6    Gandalf   1.5         5/4/99   Michal Fadljevic 
*  5    Gandalf   1.4         4/30/99  Michal Fadljevic 
*  4    Gandalf   1.3         4/29/99  Michal Fadljevic 
*  3    Gandalf   1.2         4/26/99  Michal Fadljevic 
*  2    Gandalf   1.1         4/21/99  Michal Fadljevic 
*  1    Gandalf   1.0         4/15/99  Michal Fadljevic 
* $
*/
