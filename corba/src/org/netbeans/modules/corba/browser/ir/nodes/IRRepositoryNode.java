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
import java.io.*;
import java.net.*;
import java.util.Vector;
import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.settings.*;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.IRRootNode;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.Removable;
/*
 * @author Karel Gardas
 */
 
public class IRRepositoryNode extends IRContainerNode implements Node.Cookie, Removable {

    static final String ICON_BASE_ROOT 
        = "org/netbeans/modules/corba/browser/ir/resources/ir-root";
    private SystemAction[] actions;
    private org.omg.CORBA.Container repository;
    private String name;
  
    private static class RepositoryCodeGenerator implements GenerateSupport {
        private org.omg.CORBA.Container container;
        private String name;
    
        public RepositoryCodeGenerator (String name, org.omg.CORBA.Container container){
            this.container = container;
            this.name = name;
        }
    
        public String generateHead (int indent){
            String code ="";
            for (int i=0; i<indent; i++)
                code =code + SPACE;
            return code + "// Repository: " + name + "\n";
        }
    
        public String generateSelf (int indent){
            String code = generateHead(indent);
            Contained[] contained = container.contents (DefinitionKind.dk_all, true);
            for (int i=0 ; i < contained.length; i++){
                switch (contained[i].def_kind().value()){
                case DefinitionKind._dk_Interface:
                    code = code + IRInterfaceDefNode.createGeneratorFor(contained[i]).generateSelf(indent);
                    break;
                case DefinitionKind._dk_Module:
                    code = code + IRModuleDefNode.createGeneratorFor (contained[i]).generateSelf(indent);
                    break;
                case DefinitionKind._dk_Exception:
                    code = code + IRExceptionDefNode.createGeneratorFor (contained[i]).generateSelf(indent);
                    break;
                case DefinitionKind._dk_Struct:
                    code = code + IRStructDefNode.createGeneratorFor (contained[i]).generateSelf(indent);
                    break;
                case DefinitionKind._dk_Union:
                    code = code + IRUnionDefNode.createGeneratorFor (contained[i]).generateSelf(indent);
                    break;
                case DefinitionKind._dk_Constant:
                    code = code + IRConstantDefNode.createGeneratorFor (contained[i]).generateSelf(indent);
                    break;
                case DefinitionKind._dk_Attribute:
                    code = code + IRAttributeDefNode.createGeneratorFor (contained[i]).generateSelf(indent);
                    break;
                case DefinitionKind._dk_Operation:
                    code = code + IROperationDefNode.createGeneratorFor (contained[i]).generateSelf(indent);
                    break;
                case DefinitionKind._dk_Alias:
                    code = code + IRAliasDefNode.createGeneratorFor (contained[i]).generateSelf(indent);
                    break;
                case DefinitionKind._dk_Enum:
                    code = code + IREnumDefNode.createGeneratorFor (contained[i]).generateSelf(indent);
                    break;
                }
            }
            code = code + generateTail(indent);
            return code;
        }
    
        public String generateTail (int indent){
            return "";
        }
    }

    public IRRepositoryNode(String _name,org.omg.CORBA.Container _rep) {
        super (new ContainerChildren (_rep));
        repository = _rep;
        name = _name;
        init ();
    }
  
    public void init () {
        ((ContainerChildren)getChildren ()).setContainer ((Container)repository);
        setIconBase (ICON_BASE_ROOT);
        setDisplayName (name);
    }
  
    public SystemAction[] createActions () {
        return new SystemAction[] { 
            SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.RemoveRepository.class),
            SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.GenerateCodeAction.class),
            null,
            SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.RefreshAction.class),
            null,
            SystemAction.get(org.openide.actions.PropertiesAction.class)
        };
    }
  
    public SystemAction[] getActions () {
        if (this.actions == null) {
            this.actions = this.createActions();
        }
        return this.actions;
    }

    public Node.Cookie getCookie(Class c) {
        if (c.isInstance(this)) 
            return this;
        else 
            return super.getCookie(c);
    }

    public void setName (String n) {
        name = n;
    }

    public String getName () {
        return name;
    }
  
  
    public Sheet createSheet (){
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get( Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Name"), String.class, Util.getLocalizedString ("TITLE_Name"), Util.getLocalizedString ("TIP_RepositoryName")){
                public java.lang.Object getValue(){
                    return name;
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_IOR"), String.class, Util.getLocalizedString ("TITLE_IOR"), Util.getLocalizedString ("TIP_RepositoryIOR")){
                public java.lang.Object getValue(){
                    IRRootNode root = IRRootNode.getDefault();
                    if (root != null){
                        ORB orb = root.getORB();
                        if (orb != null){
                            return orb.object_to_string ( repository );
                        }
                    }
                    return "";
                }
            });
        return s;
    }

    // This node has only instance generatror, 
    // 'cause it's an top level node
    public GenerateSupport createGenerator () {
        if (this.generator == null)
            this.generator = new RepositoryCodeGenerator(this.name, this.repository);
        return this.generator;
    }
  
    public String getRepositoryId() {
        return (Util.getLocalizedString("MSG_Repository")+" " + name);
    }

    /*
      public Vector getRepos () {
      return repos;
      }

      public ORB getORB () {
      return orb;
      }
  
      public boolean loaded () {
      return _loaded;
      }
    */

}

/*
 * $Log
 * $
 */


