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

package com.netbeans.enterprise.modules.corba.idl.editor.settings;

import org.openide.util.NbBundle;

import com.netbeans.developer.modules.text.options.BaseOptions;
import com.netbeans.enterprise.modules.corba.idl.editor.coloring.IDLKit;

/**
 * Options for the IDL Editor Kit
 *
 * @author Libor Kramolis
 */
public class IDLOptions extends BaseOptions {
  
  public static final String IDL = "idl";

  public IDLOptions() {
    super (IDLKit.class, IDL);
    //System.out.println ("IDLOptions ()");
  }

  public String displayName () {
    //System.out.println ("name: " + NbBundle.getBundle (IDLOptions.class).getString 
    //		("CTL_IDLOptions_Name"));
    return NbBundle.getBundle (IDLOptions.class).getString ("CTL_IDLOptions_Name");
  }

}

/*
 * <<Log>>
 *  1    Gandalf   1.0         11/9/99  Karel Gardas    
 * $
 */
