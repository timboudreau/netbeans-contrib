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

import java.beans.*;

import com.netbeans.ide.util.NbBundle;

/** property editor for viewer property AppletSettings class
*
* @author Karel Gardas
* @version 0.01 March 29, 1999
*/

import com.netbeans.enterprise.modules.corba.*;

public class OrbPropertyEditor extends PropertyEditorSupport {

  /** array of orbs */
  private static final String[] orbs = {CORBASupport.ORBIX, CORBASupport.VISIBROKER, 
					CORBASupport.ORBACUS, CORBASupport.JAVAORB};

  /** @return names of the supported orbs*/
  public String[] getTags() {
    return orbs;
  }

  /** @return text for the current value */
  public String getAsText () {
    return (String) getValue();
  }

  /** @param text A text for the current value. */
  public void setAsText (String text) {
      setValue(text);
  }
}

/*
 * <<Log>>
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */






