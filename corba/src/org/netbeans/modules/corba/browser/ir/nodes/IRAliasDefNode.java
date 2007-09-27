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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
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
  
    private class AliasCodeGenerator implements GenerateSupport {
    
    
        public String generateHead (int indent, StringHolder currentPrefix) {
            return Util.generatePreTypePragmas (_alias.id(), _alias.absolute_name(), currentPrefix, indent);
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix) {
            String fill ="";
            String code = generateHead (indent, currentPrefix);
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "typedef ";
            StringHolder dimension = new StringHolder();
            code = code + Util.idlType2TypeString (_alias.original_type_def(),((IRContainerNode)getParentNode()).getOwner(), dimension) + " ";
            code = code + _alias.name()+((dimension.value==null)?"":dimension.value)+";\n";
            code = code + generateTail (indent);
            return code;
        }
    
        public String generateTail (int indent) {
            return Util.generatePostTypePragmas (_alias.name(), _alias.id(), indent) + "\n";
        }
        
        public String getRepositoryId () {
            return _alias.id();
        }
    
    }
  
  
    /** Creates new AliasDefNode */
    public IRAliasDefNode(Contained value) {
        super();
        this._alias = AliasDefHelper.narrow(value);
        this.getCookieSet().add(this);
        this.setIconBase(ALIAS_ICON_BASE);
        this.getCookieSet().add ( new AliasCodeGenerator ());
    }
  
    public String getName(){
        return this.getDisplayName();
    }
  
    public String getDisplayName(){

        if (this.name == null) {
            if (this._alias != null) {
                this.name = this._alias.name();
            }
            else
                this.name = "";
        }
        return this.name;
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
  
    public org.omg.CORBA.IRObject getIRObject () {
        return this._alias;
    }
    
    public void generateCode() {  
        ExClipboard clipboard = TopManager.getDefault().getClipboard();
        StringSelection genCode = new StringSelection ( this.generateHierarchy ());
        clipboard.setContents(genCode,genCode);
    }

    public void generateCode (PrintWriter out) throws IOException {
        out.println ( this.generateHierarchy ());
    }

    private String generateHierarchy () {
        Node node = this.getParentNode();
        String code ="";
        // Generate the start of namespace
        ArrayList stack = new ArrayList();
        while ( node instanceof IRContainerNode){
            stack.add(node.getCookie(GenerateSupport.class));
            node = node.getParentNode();
        }
        StringHolder currentPrefix = new StringHolder ("");
        int size = stack.size();
        for (int i = size - 1; i>=0; i--)
            code = code + ((GenerateSupport)stack.get(i)).generateHead((size -i -1), currentPrefix);
        // Generate element itself
        code = code + ((GenerateSupport)this.getCookie(GenerateSupport.class)).generateSelf(size, currentPrefix);
        //Generate tail of namespace
        for (int i = 0; i< stack.size(); i++)
            code = code + ((GenerateSupport)stack.get(i)).generateTail((size -i));
        return code;
    }
  
}
