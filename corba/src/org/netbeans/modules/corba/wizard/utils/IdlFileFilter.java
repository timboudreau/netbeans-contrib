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

package org.netbeans.modules.corba.wizard.utils;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.corba.wizard.CorbaWizardAction;

/** 
 *
 * @author  tzezula
 * @version 
 */
public class IdlFileFilter extends FileFilter {

  /** Creates new IdlFileFilter */
  public IdlFileFilter() {
  }
  
  public boolean accept (File file) {
    if (file.isDirectory())
      return true;
    if (!file.canRead())
      return false;
    return file.getName().endsWith(".idl");
  }
  
  public String getDescription () {
    return CorbaWizardAction.getLocalizedString("TXT_IdlFile");
  }
  
}