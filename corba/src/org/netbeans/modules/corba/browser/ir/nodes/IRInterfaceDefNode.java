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

package com.netbeans.enterprise.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;
import com.netbeans.enterprise.modules.corba.browser.ir.Util;
import com.netbeans.enterprise.modules.corba.browser.ir.util.GenerateSupport;



public class IRInterfaceDefNode extends IRContainerNode {

  InterfaceDef _interface;

  private static final String INTERFACE_ICON_BASE =
    "com/netbeans/enterprise/modules/corba/idl/node/interface";
  
  private static class InterfaceCodeGenerator implements GenerateSupport {
    private InterfaceDef _interface;
    
    public InterfaceCodeGenerator ( InterfaceDef interf ){
      this._interface = interf;
    }
    
    public String generateHead (int indent){
      String code ="";
      for (int i=0; i<indent; i++)
        code = code + "  ";
      code = code + "interface " + _interface.name () + " {\n";
      return code;
    }
    
    public String generateSelf (int indent){
      String code = "";
      int dk;
      for (int i=0; i<indent; i++)
        code = code + "  ";
      code = code + "interface " + _interface.name ();
      InterfaceDef[] base = _interface.base_interfaces();
      if (base.length > 0){
        code = code + " : ";
        for (int i = 0; i<base.length; i++){
          if (i != 0)
            code = code + ", ";
          code = code + base[i].name();    
        }
      }
      code = code +" {\n";
      Contained[] contained = _interface.contents (DefinitionKind.dk_all, true);
      for (int i=0 ; i < contained.length; i++){
        // Workaround for bug in Jdk 1.2 implementation
        // if MARSHAL exception ocured, try to introspect
        // object in another way.
        try{
          dk = contained[i].describe().kind.value();
        }catch (org.omg.CORBA.MARSHAL marshalException){
          if (contained[i]._is_a("IDL:omg.org/CORBA/OperationDef:1.0"))
            dk = DefinitionKind._dk_Operation;
          else 
            throw new RuntimeException ("Inner Exception is: "+marshalException);
        }
        switch (dk){
          case DefinitionKind._dk_Exception:
            code = code + IRExceptionDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1);
            break;
          case DefinitionKind._dk_Struct:
            code = code + IRStructDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1);
            break;
          case DefinitionKind._dk_Union:
            code = code + IRUnionDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1);
            break;
          case DefinitionKind._dk_Constant:
            code = code + IRConstantDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1);
            break;
          case DefinitionKind._dk_Attribute:
            code = code + IRAttributeDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1);
            break;
          case DefinitionKind._dk_Operation:
            code = code + IROperationDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1);
            break;
          case DefinitionKind._dk_Alias:
            code = code + IRAliasDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1);
            break;
          case DefinitionKind._dk_Enum:
            code = code + IREnumDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1);
            break;
        }
      }
      code = code + generateTail(indent);
      return code;
    }
    
    public String generateTail (int indent){
      String code = "";
      for (int i=0; i< indent; i++)
        code = code + "  ";
      return code + "}; // " + _interface.name() + "\n\n";
    }
    
  }

  public IRInterfaceDefNode(Container value) {
    super (new ContainerChildren(value));
    setIconBase (INTERFACE_ICON_BASE);
    _interface = InterfaceDefHelper.narrow (value);
  }

  public String getDisplayName () {
    if (_interface != null)
      return _interface.name ();
    else 
      return "";
  }

  public String getName () {
    return this.getDisplayName();
  }

  public SystemAction getDefaultAction () {
    SystemAction result = super.getDefaultAction();
    return result == null ? SystemAction.get(OpenAction.class) : result;
  }

  protected Sheet createSheet () {
    Sheet s = Sheet.createDefault ();
    Sheet.Set ss = s.get (Sheet.PROPERTIES);
    ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_InterfaceName")) {
      public java.lang.Object getValue () {
	return _interface.name ();
      }
    });
    ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"), String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString("TIP_InterfaceId")) {
      public java.lang.Object getValue () {
	return _interface.id ();
      }
    });
    ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), 
					  Util.getLocalizedString("TIP_InterfaceVersion")) {
      public java.lang.Object getValue () {
	return _interface.version ();
      }
    });
    
    ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Super"), String.class, Util.getLocalizedString("TITLE_Super"), 
					  Util.getLocalizedString("TIP_InterfaceSuper")) {
      public java.lang.Object getValue () {
	String inher = "";
	if (_interface.base_interfaces().length > 0) {
          InterfaceDef[] base = _interface.base_interfaces();
	  for (int i=0; i<base.length; i++)
	    inher = inher + (base[i]).name () + ", ";
	  inher = inher.substring (0, inher.length () - 2);
	}
	else
	  inher = "";
	return inher;
      }
    });
    return s;
  }
  
  public GenerateSupport createGenerator(){
    if (this.generator == null)
      this.generator = new InterfaceCodeGenerator(_interface);
    return this.generator;
  }
  
  public static GenerateSupport createGeneratorFor (Contained type){
    InterfaceDef interf = InterfaceDefHelper.narrow (type);
    if (type == null)
      return null;
    return new InterfaceCodeGenerator(interf);
  }

}

/*
 * $Log
 * $
 */
