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
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;


public class IRPrimitiveNode extends IRLeafNode {
  
  private static final String PRIMITIVE_ICON_BASE=
    "org/netbeans/modules/corba/idl/node/declarator";
  private String name;
  private TypeCode tc;
  
  private static class PrimitiveCodeGenerator implements GenerateSupport {
    private TypeCode tc;
    private String name;
    
    public PrimitiveCodeGenerator (String name, TypeCode tc){
      this.tc = tc;
      this.name = name;
    }
    
    public String generateHead(int indent){
      return "";
    }
    
    public String generateSelf (int indent){
      String code = "";
      for (int i=0; i<indent; i++)
        code =code + "  ";
      StringHolder dimension = new StringHolder();
      code = code + Util.typeCode2TypeString(tc, dimension) + " " + name + ((dimension.value==null)?"":dimension.value)+";\n";
      return code;
    }
    
    public String generateTail (int indent){
      return "";
    }
    
  }

  /** Creates new IRPrimitiveNode */
  public IRPrimitiveNode(TypeCode tc, String name) {
    this.name = name;
    this.tc = tc;
    this.setIconBase(PRIMITIVE_ICON_BASE);
  }
  
  public String getName(){
    return this.name;
  }
  
  public String getDisplayName(){
    return this.name;
  }
  
  public Sheet createSheet(){
    Sheet s = Sheet.createDefault();
    Sheet.Set ss = s.get( Sheet.PROPERTIES);
    ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_PrimitiveName")){ 
      public java.lang.Object getValue() {
        return name;
      }
    });
    ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Type"), String.class, Util.getLocalizedString("TITLE_Type"), Util.getLocalizedString("TIP_PrimitiveType")){
      public java.lang.Object getValue() {
        return Util.typeCode2TypeString(tc, new StringHolder());
      }
    });
    ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Dimension"), String.class, Util.getLocalizedString("TITLE_Dimension"), Util.getLocalizedString("TIP_PrimitiveDimension")){
      public java.lang.Object getValue() {
        StringHolder holder = new StringHolder();
        Util.typeCode2TypeString (tc, holder);
        return (holder.value==null)?"":holder.value;
      }
    });
    return s;
  }
  
  // This node has only the generator for instance,
  // because the generation is handled by its paarent
  public GenerateSupport createGenerator (){
    if (this.generator == null)
      this.generator = new PrimitiveCodeGenerator (this.name, this.tc);
    return this.generator;
  }
  
}
