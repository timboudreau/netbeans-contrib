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

package com.netbeans.enterprise.modules.corba.wizard;

import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.actions.*;
import java.util.ResourceBundle;

/** 
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
public class CorbaWizardAction extends CallableSystemAction {
  
  public static final String ICON = "/com/netbeans/enterprise/modules/corba/wizard/resources/CorbaWizard.gif";
  private static ResourceBundle bundle = null;
  

  /** Creates new CorbaWizardAction */
  public CorbaWizardAction() {
  }
  
  public String getName () {
    return getLocalizedString("CLT_CorbaWizardAction");
  }
  
  /** No help jet */
  public HelpCtx getHelpCtx(){
    return HelpCtx.DEFAULT_HELP;
  }
  
  public void performAction () {
    new CorbaWizard().run();
  }
  
  protected String iconResource () {
    return ICON;
  }
  
  public static String getLocalizedString (String text){
    if (bundle == null)
      bundle = NbBundle.getBundle(CorbaWizardAction.class);
    return bundle.getString(text);
  }
  
}