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
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;


public class IREnumDefNode extends IRContainerNode {

    private EnumDef _enum;
    private static final String ENUM_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/enum";


    private class EnumCodeGenerator implements GenerateSupport {
    
        public EnumCodeGenerator (){
        }
    
        public String generateHead (int indent, StringHolder currentPrefix){
            String code = Util.generatePreTypePragmas ( _enum.id(), _enum.absolute_name(), currentPrefix, indent);
            String fill ="";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "enum " + _enum.name()+" { ";
            return code;
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead(indent, currentPrefix);
            if (((Children)getChildren()).getState() == Children.NOT_INITIALIZED)
                ((Children)getChildren()).state = Children.SYNCHRONOUS;
            Node[] nodes = getChildren().getNodes();
            for (int i=0; i< nodes.length; i++) {
                GenerateSupport gs = (GenerateSupport) nodes[i].getCookie (GenerateSupport.class);
                if (i!=0)
                    code = code +", ";
                if (gs != null)
                    code = code + gs.generateSelf (indent+1, currentPrefix);
            }
            code = code + generateTail(indent);
            return code;
        }
    
        public String generateTail (int indent){
            return "};\n"+Util.generatePostTypePragmas(_enum.name(),_enum.id(), indent)+"\n";	 
        }
        
        public String getRepositoryId () {
            return _enum.id();
        }
    
    }

    /** Creates new IREnumDefNode */
    public IREnumDefNode(Contained value) {
        super(new EnumChildren(EnumDefHelper.narrow(value)));
        _enum = ((EnumChildren)this.getChildren()).getEnumStub();
        setIconBase(ENUM_ICON_BASE);
        this.getCookieSet().add ( new EnumCodeGenerator ());
    }
  
    public String getName(){
        return this.getDisplayName();
    }
  
    public String getDisplayName(){
        if (this.name == null) {
            if ( _enum != null){
                this.name = this._enum.name();
            }
            else
                this.name = "";
        }
        return this.name;
    }
  
    public Sheet createSheet(){
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_EnumName")){
                public java.lang.Object getValue(){
                    return _enum.name();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Id"),String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString ("TIP_EnumId")){
                public java.lang.Object getValue(){
                    return _enum.id();
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), Util.getLocalizedString ("TIP_EnumVersion")){
                public java.lang.Object getValue() {
                    return _enum.version();
                }
            });
        return s;
    }
    
    public org.omg.CORBA.Contained getOwner () {
        return this._enum;
    }
    
    public org.omg.CORBA.IRObject getIRObject () {
        return this._enum;
    }

}
