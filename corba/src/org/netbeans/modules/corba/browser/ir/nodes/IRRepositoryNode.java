/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
    
    private class RepositoryCodeGenerator implements GenerateSupport {
        
        
        /** Does not have prefix, cause its not Contained type
         */
        public String generateHead (int indent, StringHolder currentPrefix){
            String code ="";
            for (int i=0; i<indent; i++)
                code =code + SPACE;
            return code + "// Repository: " + IRRepositoryNode.this.getName() + "\n";
        }
        
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead(indent, currentPrefix);
            String prefixBackup = currentPrefix.value;
            
/**            Contained[] contained = container.contents (DefinitionKind.dk_all, true);
 * for (int i=0 ; i < contained.length; i++){
 * if (contained[i]._is_a("IDL:omg.org/CORBA/AbstractInterfaceDef:1.0")) {
 * code = code + IRInterfaceDefNode.createGeneratorFor(contained[i], true).generateSelf(indent, currentPrefix);
 * }
 * else {
 * switch (contained[i].def_kind().value()){
 * case DefinitionKind._dk_Interface:
 * code = code + IRInterfaceDefNode.createGeneratorFor(contained[i], false).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Module:
 * code = code + IRModuleDefNode.createGeneratorFor (contained[i]).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Exception:
 * code = code + IRExceptionDefNode.createGeneratorFor (contained[i]).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Struct:
 * code = code + IRStructDefNode.createGeneratorFor (contained[i]).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Union:
 * code = code + IRUnionDefNode.createGeneratorFor (contained[i]).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Constant:
 * code = code + IRConstantDefNode.createGeneratorFor (contained[i]).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Attribute:
 * code = code + IRAttributeDefNode.createGeneratorFor (contained[i]).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Operation:
 * code = code + IROperationDefNode.createGeneratorFor (contained[i]).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Alias:
 * code = code + IRAliasDefNode.createGeneratorFor (contained[i]).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Enum:
 * code = code + IREnumDefNode.createGeneratorFor (contained[i]).generateSelf(indent, currentPrefix);
 * break;
 * case DefinitionKind._dk_Native:
 * code = code + IRNativeDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
 * break;
 * case DefinitionKind._dk_ValueBox:
 * code = code + IRValueBoxDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
 * break;
 * case DefinitionKind._dk_Value:
 * code = code + IRValueDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
 * break;
 * }
 * }
 * }*/
            Children cld = (Children) IRRepositoryNode.this.getChildren();
            if (cld.getState() == Children.NOT_INITIALIZED)
                ((Children)getChildren()).state = Children.SYNCHRONOUS;
            Node[] nodes = cld.getNodes();
            for (int i=0; i< nodes.length; i++) {
                GenerateSupport gs = (GenerateSupport) nodes[i].getCookie (GenerateSupport.class);
                if (gs != null)
                    code = code + gs.generateSelf (indent+1, currentPrefix);
            }
            
            code = code + generateTail(indent);
            currentPrefix.value = prefixBackup;
            return code;
        }
        
        public String generateTail (int indent){
            return "";
        }
        
        public String getRepositoryId() {
            return (Util.getLocalizedString("MSG_Repository")+" " + name);
        }
    }
    
    public IRRepositoryNode(String _name,org.omg.CORBA.Container _rep) {
        super (new ContainerChildren (_rep));
        repository = _rep;
        name = _name;
        this.getCookieSet().add ( new RepositoryCodeGenerator());
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
    
    public org.omg.CORBA.Contained getOwner () {
        return null;
    }
    
    public org.omg.CORBA.IRObject getIRObject() {
        return this.repository;
    }    
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx ( IRRepositoryNode.class.getName());
    }
}

/*
 * $Log
 * $
 */


