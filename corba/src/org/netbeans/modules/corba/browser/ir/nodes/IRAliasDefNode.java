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

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import org.omg.CORBA.*;
import org.openide.TopManager;
import org.openide.nodes.Sheet;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.datatransfer.ExClipboard;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;


public class IRAliasDefNode extends IRLeafNode implements Node.Cookie, Generatable {

  private static final String ALIAS_ICON_BASE =
   "org/netbeans/modules/corba/idl/node/declarator";
  private AliasDef _alias;
  
  private static class AliasCodeGenerator implements GenerateSupport {
    private AliasDef _alias;
    
    public AliasCodeGenerator (AliasDef alias){
      this._alias = alias;
    }
    
    public String generateHead (int indent) {
      return "";
    }
    
    public String generateSelf (int indent) {
      String code ="";
      for (int i=0; i<indent; i++)
        code = code +"  ";
      code = code + "typedef ";
      StringHolder dimension = new StringHolder();
      code = code + Util.typeCode2TypeString (_alias.original_type_def().type(), dimension) + " ";
      code = code + _alias.name()+((dimension.value==null)?"":dimension.value)+";\n\n";
      return code;
    }
    
    public String generateTail (int indent) {
      return "";
    }
    
  }
  
  
  /** Creates new AliasDefNode */
  public IRAliasDefNode(Contained value) {
    super();
    this._alias = AliasDefHelper.narrow(value);
    this.getCookieSet().add(this);
    this.setIconBase(ALIAS_ICON_BASE);
  }
  
  public String getName(){
    return this.getDisplayName();
  }
  
  public String getDisplayName(){
    if (this._alias != null){
      return this._alias.name();
    }
    else
      return "";
  }
  
  public Sheet createSheet(){
    Sheet s = Sheet.createDefault();
    Sheet.Set ss = s.get( Sheet.PROPERTIES);
    ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Name"),String.class,Util.getLocalizedString("TITLE_Name"),Util.getLocalizedString("TIP_AliasName")){
      public java.lang.Object getValue(){
        return _alias.name();
      }
    });
    ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"),String.class,Util.getLocalizedString("TITLE_Id"),Util.getLocalizedString("TIP_AliasId")){
      public java.lang.Object getValue(){
        return _alias.id();
      }
    });
    ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"),String.class,Util.getLocalizedString("TITLE_Version"),Util.getLocalizedString("TIP_AliasVersion")){
      public java.lang.Object getValue (){
        return _alias.version();
      }
    });
    ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_OriginalType"),String.class,Util.getLocalizedString("TITLE_OriginalType"),Util.getLocalizedString("TIP_AliasOriginalType")){
      public java.lang.Object getValue(){
        IDLType idlType = _alias.original_type_def();
        return Util.typeCode2TypeString(idlType.type());
      }
    });
    ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Dimension"),String.class,Util.getLocalizedString("TITLE_Dimension"),Util.getLocalizedString("TIP_AliasDimension")){
      public java.lang.Object getValue (){
        StringHolder dimension = new StringHolder();
        IDLType idlType = _alias.original_type_def();
        Util.typeCode2TypeString(idlType.type(),dimension);
        return dimension.value;
      }
    });
    return s;
  }
  
  public void generateCode(){
     Node node = this.getParentNode();
      String code ="";
      // Generate the start of namespace
      ArrayList stack = new ArrayList();
      while ( node instanceof IRContainerNode){
	  stack.add(((GenerateSupportFactory)node).createGenerator());
	  node = node.getParentNode();
      }
      int size = stack.size();
      for (int i = size - 1; i>=0; i--)
        code = code + ((GenerateSupport)stack.get(i)).generateHead((size -i -1));
      // Generate element itself
      code = code + this.createGenerator().generateSelf(size);
      //Generate tail of namespace
      for (int i = 0; i< stack.size(); i++)
        code = code + ((GenerateSupport)stack.get(i)).generateTail((size -i));
      ExClipboard clipboard = TopManager.getDefault().getClipboard();
      StringSelection genCode = new StringSelection (code);
      clipboard.setContents(genCode,genCode);
  }
  
  
  public GenerateSupport createGenerator () {
    if (this.generator == null)
      this.generator = new AliasCodeGenerator (_alias);
    return this.generator;
  }
  
  public static GenerateSupport createGeneratorFor (Contained type){
    AliasDef alias = AliasDefHelper.narrow ( type);
    if (alias == null) 
      return null;
    return new AliasCodeGenerator (alias);
  }
  
}
