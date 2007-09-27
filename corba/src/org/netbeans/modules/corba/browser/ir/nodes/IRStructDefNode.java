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
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;

public class IRStructDefNode extends IRContainerNode {

    private StructDef _struct;
    private static final String STRUCT_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/struct";

    private class StructCodeGenerator implements GenerateSupport {
    
    
        public String generateHead (int indent, StringHolder currentPrefix) {
            String code = Util.generatePreTypePragmas (_struct.id(), _struct.absolute_name(), currentPrefix, indent);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "struct " + _struct.name() + " {\n";
            return code;
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix) {
            String code = "";
            code = code + generateHead (indent, currentPrefix);
            String prefixBackUp = currentPrefix.value;
            Children cld = (Children)IRStructDefNode.this.getChildren();
            if (cld.getState() == Children.NOT_INITIALIZED)
                ((Children)getChildren()).state = Children.SYNCHRONOUS;
            Node[] nodes = cld.getNodes();
            int varIndex = 0;
            for (int i = 0; i< nodes.length; i++) {
                if (nodes[i] instanceof IRPrimitiveNode){
                    varIndex = i;
                    break;
                }
            }
            
            for (int i=varIndex; i< nodes.length; i++) {
                boolean generated = false;
                GenerateSupport gs = (GenerateSupport) nodes[i].getCookie (GenerateSupport.class);
                for (int j=0; j< varIndex; j++) {
                    if (nodes[j] == null)
                        continue;
                    GenerateSupport tgs = (GenerateSupport) nodes[j].getCookie (GenerateSupport.class);
                    if (tgs == null)
                        continue;
                    try {
                        if (tgs.getRepositoryId().equals(((IRPrimitiveNode)nodes[i]).getTypeCode().id())) {
                            String tmp = tgs.generateSelf (indent+1, currentPrefix);
                            tmp = tmp.substring (0, tmp.lastIndexOf(';')) + " ";
                            boolean first = true;
                            try {
                                do {
                                    if (first) {
                                        first =false;
                                        tmp = tmp + nodes[i].getName();
                                    }
                                    else {
                                        tmp = tmp + ", " + nodes[i].getName();
                                    }
                                    i++;
                                }while (i < nodes.length && tgs.getRepositoryId().equals(((IRPrimitiveNode)nodes[i]).getTypeCode().id()));
                            }catch (org.omg.CORBA.TypeCodePackage.BadKind badKind){}
                            i--;
                            code = code + tmp + ";\n";
                            nodes[j] = null;
                            generated = true;
                            break;
                        }
                    } catch (org.omg.CORBA.TypeCodePackage.BadKind bk){}
                }
                if (!generated && gs != null)
                    code = code + gs.generateSelf (indent+1, currentPrefix);
            }
            code = code + generateTail (indent);
            currentPrefix.value = prefixBackUp;
            return code;
        }
    
        public String generateTail (int indent) {
            String code = "";
            for (int i=0; i<indent; i++)
                code =code + SPACE;
            code = code + "}; //" + _struct.name() + "\n"+Util.generatePostTypePragmas(_struct.name(),_struct.id(),indent)+"\n";
            return code;
        }
        
        public String getRepositoryId() {
            return _struct.id();
        }
    
    }
  
    /** Creates new IRStructDefNode */
    public IRStructDefNode(Contained value) {
        super ( new StructChildren (value));
        _struct = ((StructChildren)this.getChildren()).getStructStub();
        setIconBase(STRUCT_ICON_BASE);
        this.getCookieSet().add ( new StructCodeGenerator ());
    }
  
    public String getDisplayName() {
        if (this.name == null) {
        if (_struct != null)
            this.name = _struct.name();
        else
            this.name = "";
        }
        return this.name;
    }
  
  
    public String getName(){
        return this.getDisplayName();
    }
  
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Name"), String.class, Util.getLocalizedString ("TITLE_Name"),Util.getLocalizedString ("TIP_StructName")){
                public java.lang.Object getValue () {
                    return _struct.name();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Id"), String.class, Util.getLocalizedString ("TITLE_Id"), Util.getLocalizedString ("TIP_StructId")){
                public java.lang.Object getValue () {
                    return _struct.id();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Version"), String.class, Util.getLocalizedString ("TITLE_Version"),Util.getLocalizedString ("TIP_StructVersion")){
                public java.lang.Object getValue () {
                    return _struct.version();
                }
            });
        return s;
    }
  
    public org.omg.CORBA.Contained getOwner () {
        return this._struct;
    }
  
    public org.omg.CORBA.IRObject getIRObject() {
        return this._struct;
    }    
  
}
