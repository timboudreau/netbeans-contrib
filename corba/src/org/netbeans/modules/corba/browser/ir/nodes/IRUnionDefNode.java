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
import org.omg.CORBA.ORBPackage.InconsistentTypeCode;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;


public class IRUnionDefNode extends IRContainerNode {

    // public static final boolean DYN_ANY_WORKAROUND = false;
    public static final boolean DYN_ANY_WORKAROUND = true;
    private UnionDef _union;
    private static final String UNION_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/union";
  
    private class UnionCodeGenerator implements GenerateSupport {
        
        public String generateHead (int indent, StringHolder currentPrefix) {
            String code = Util.generatePreTypePragmas (_union.id(), _union.absolute_name(), currentPrefix, indent);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "union " + _union.name() + " switch ( " +Util.idlType2TypeString(_union.discriminator_type_def(),((IRContainerNode)getParentNode()).getOwner())+ " ) {\n";
            return code;
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = "";
            code = code + generateHead(indent, currentPrefix);
            String prefixBackUp = currentPrefix.value;
            /* String fill = "";
            for (int i=0; i<=indent; i++)
                fill =fill + SPACE;
            UnionMember[] members = _union.members();
            StringHolder dimension = new StringHolder();
            for ( int i = 0; i < members.length; i++){
                code = code + fill;
                TypeCode tc = members[i].label.type();
                switch (tc.kind().value()){
                case TCKind._tk_boolean:
                    code = code +"case ";
                    if (members[i].label.extract_boolean())
                        code = code + "TRUE";
                    else
                        code = code + "FALSE";
                    break;
                case TCKind._tk_char:
                    code = code +"case "+ "\'" + new Character (members[i].label.extract_char()).toString() + "\'";
                    break;
                case TCKind._tk_short:
                    code = code +"case "+ Short.toString ( members[i].label.extract_short());
                    break;
                case TCKind._tk_long:
                    code = code +"case "+ Integer.toString ( members[i].label.extract_long());
                    break;
                case TCKind._tk_longlong:
                    code = code +"case "+ Long.toString ( members[i].label.extract_longlong());
                    break;
                case TCKind._tk_ushort:
                    code = code +"case "+ Short.toString ( members[i].label.extract_ushort());
                    break;
                case TCKind._tk_ulong:
                    code = code +"case "+ Integer.toString ( members[i].label.extract_ulong());
                    break;
                case TCKind._tk_ulonglong:
                    code = code +"case "+ Long.toString ( members[i].label.extract_ulonglong());
                    break;
                case TCKind._tk_enum:
                    // Workaround for some CORBA implementations, which do not support DynAny
                    if (DYN_ANY_WORKAROUND){
                        try{
                            org.omg.CORBA.portable.InputStream in = members[i].label.create_input_stream();
                            int value = in.read_long();
                            String name = tc.member_name(value);
                            code = code +"case "+ name;
                        }catch(Exception e){org.openide.TopManager.getDefault().notifyException(e);};
                    }
                    else{
                        try {
                            DynAnyFactory factory = DynAnyFactoryHelper.narrow(ORB.init().resolve_initial_references ("DynAnyFactory"));
                            if (factory != null) {
                                org.omg.DynamicAny.DynEnum denum = org.omg.DynamicAny.DynEnumHelper.narrow (factory.create_dyn_any(members[i].label));
                                code = code +"case "+ denum.get_as_string();
                            }
                            else {
                                code = code +"case ???";
                            }
                        }catch (org.omg.CORBA.ORBPackage.InvalidName invalid){}
                        catch (org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode inconsistent) {}
                    }
                    break;
                default:
                    code = code + "default";
                }
                code= code + ": ";
                dimension.value = null;
                code = code + Util.typeCode2TypeString (members[i].type, dimension)+" ";
                code = code + members[i].name + ((dimension.value==null)?"":dimension.value) + ";\n";
            }
             */
            Children cld = (Children) getChildren();
            if (cld.getState() == Children.NOT_INITIALIZED)
                ((Children)getChildren()).state = Children.SYNCHRONOUS;
            Node[] nodes = cld.getNodes ();
            int varIndex = 0;
            for (int i = 0; i< nodes.length; i++) {
                if (nodes[i] instanceof IRUnionMemberNode) {
                    varIndex = i;
                    break;
                }
            }
            for (int i=varIndex; i< nodes.length; i++) {
                boolean generated = false;
                IRUnionMemberNode.UnionMemberCodeGenerator gs = (IRUnionMemberNode.UnionMemberCodeGenerator) nodes[i].getCookie (GenerateSupport.class);
                for (int j=0; j< varIndex; j++) {
                    if (nodes[j] == null)
                        continue;
                    GenerateSupport tgs = (GenerateSupport) nodes[j].getCookie (GenerateSupport.class);
                    if (tgs == null)
                        continue;
                    try {
                        if (tgs.getRepositoryId().equals(((IRUnionMemberNode)nodes[i]).getTypeCode().id())) {
                            String tmp = gs.generateLabel (indent+1, currentPrefix);
                            tmp = tmp + tgs.generateSelf (indent+1, currentPrefix).trim();
                            tmp = tmp.substring (0, tmp.lastIndexOf (';')) + " ";
                            tmp = tmp + nodes[i].getName();
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
    
        public String generateTail (int indent){
            String code = "";
            for (int i=0; i<indent; i++)
                code =code + SPACE;
            return code + "}; // " + _union.name() +"\n"+Util.generatePostTypePragmas(_union.name(),_union.id(),indent)+"\n";
        }
        
        public String getRepositoryId () {
            return _union.id();
        }
    
    }

    /** Creates new IRUnionDefNode */
    public IRUnionDefNode(Contained value) {
        super ( new UnionChildren(value));
        _union = ((UnionChildren)this.getChildren()).getUnionStub();
        setIconBase(UNION_ICON_BASE);
        this.getCookieSet().add ( new UnionCodeGenerator());
    }
  
    public String getDisplayName(){
	if (this.name == null) {
    	    if (_union != null)
        	this.name = _union.name();
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
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Name"), String.class, Util.getLocalizedString ("TITLE_Name"),Util.getLocalizedString ("TIP_UnionName")){
                public java.lang.Object getValue () {
                    return _union.name();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Id"), String.class, Util.getLocalizedString ("TITLE_Id"), Util.getLocalizedString ("TIP_UnionId")){
                public java.lang.Object getValue () {
                    return _union.id();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Version"), String.class, Util.getLocalizedString ("TITLE_Version"),Util.getLocalizedString ("TIP_UnionVersion")){
                public java.lang.Object getValue () {
                    return _union.version();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_DiscriminatorType"), String.class, Util.getLocalizedString ("TITLE_DiscriminatorType"), Util.getLocalizedString ("TIP_UnionDiscriminatorType")){
                public java.lang.Object getValue () {
                    return Util.typeCode2TypeString (_union.discriminator_type());
                }
            });
        return s;
    }
  
    public org.omg.CORBA.Contained getOwner () {
        return this._union;
    }
    
    public org.omg.CORBA.IRObject getIRObject() {
        return this._union;
    }
    
}
