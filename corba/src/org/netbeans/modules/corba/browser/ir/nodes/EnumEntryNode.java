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

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;

/** 
 *
 * @author  tzezula
 * @version 
 */
public class EnumEntryNode extends IRLeafNode {

  private String name;
  private static final String ENUM_ENTRY_ICON_BASE =
   "org/netbeans/modules/corba/idl/node/declarator";
  
  /** Creates new EnumEntryNode */
  public EnumEntryNode(String name) {
    super();
    this.name = name;
    this.setIconBase(ENUM_ENTRY_ICON_BASE);
  }
  
  
  public final String getName(){
    return this.getDisplayName();
  }
  
  public final String getDisplayName(){
    return this.name;
  }
  
  public Sheet createSheet (){
    Sheet s = Sheet.createDefault();
    Sheet.Set ss = s.get(Sheet.PROPERTIES);
    ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Name"),String.class,Util.getLocalizedString("TITLE_Name"),Util.getLocalizedString("TIP_EnumEntryName")){
      public java.lang.Object getValue(){
        return name;
      }
    });
    return s;
  }
  
  /** This node does not support generation of content
   */
  public GenerateSupport createGenerator(){
    return null;
  }
  
  /** This node does not support generation of content
   */
  public static GenerateSupport createGeneratorFor (Contained type){
    return null;
  }
  
}
