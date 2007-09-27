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
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.IRDelegate;

public class IROperationDefNode extends IRLeafNode implements IRDelegate {

    private OperationDef _operation;
    private static final String OPERATION_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/const";
  
    private class OperationCodeGenerator implements GenerateSupport {
    
        public OperationCodeGenerator (){
        }
    
        public String generateHead (int indent, StringHolder currentPrefix){
            return Util.generatePreTypePragmas (_operation.id(), _operation.absolute_name(), currentPrefix, indent);
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead (indent, currentPrefix);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill;
            if (_operation.mode() == OperationMode.OP_ONEWAY)
                code = code + "oneway ";
            code = code + Util.idlType2TypeString(_operation.result_def(),((IRContainerNode)getParentNode()).getOwner())+" ";
            code = code + _operation.name() + " (";
            ParameterDescription[] params = _operation.params();
            for (int i = 0; i < params.length; i++){
                if (i != 0)
                    code = code +", ";
                code = code + pm2Str(params[i].mode) + " ";
                code = code + Util.idlType2TypeString (params[i].type_def,((IRContainerNode)getParentNode()).getOwner()) + " ";
                code = code + params[i].name;
            }
            code = code +")";
            ExceptionDef[] exceptions = _operation.exceptions();
            if (exceptions.length > 0){
                code = code +" raises (";
                for (int i=0; i<exceptions.length; i++){
                    if ( i!= 0)
                        code = code+", ";
                    code = code + exceptions[i].name();
                }
                code = code +")";
            }
            String[] context = _operation.contexts();
            if (context.length >0){
                code = code + " context (";
                for (int i=0; i< context.length; i++){
                    if (i != 0)
                        code = code+", ";
                    code = code + "\""+ context[i]+"\"";
                } 
                code = code +")";
            }
            code = code + ";\n";
            code = code + generateTail (indent);
            return code;
        }
    
        public String generateTail (int indent){
            return Util.generatePostTypePragmas (_operation.name(), _operation.id(), indent);
        }
        
        public String getRepositoryId () {
            return _operation.id();
        }
    
    }
  
    /** Creates new IROperationDefNode */
    public IROperationDefNode(Contained value) {
        super();
        _operation = OperationDefHelper.narrow(value);
        setIconBase(OPERATION_ICON_BASE);
        this.getCookieSet().add (new OperationCodeGenerator());
    }
  
  
    public String getDisplayName(){
        if (this.name == null) {
            if (_operation != null)
                this.name = _operation.name();
            else
                this.name = "";
        }
        return this.name;
    }
  
  
    public String getName(){
        return this.getDisplayName();
    }
  
    public Sheet createSheet(){
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"),String.class,Util.getLocalizedString("TITLE_Name"),Util.getLocalizedString("TIP_OperationName")){
                public java.lang.Object getValue(){
                    return _operation.name();
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"),String.class,Util.getLocalizedString("TITLE_Id"),Util.getLocalizedString("TIP_OperationId")){
                public java.lang.Object getValue(){
                    return _operation.id();
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"),String.class,Util.getLocalizedString("TITLE_Version"),Util.getLocalizedString("TIP_OperationVersion")){
                public java.lang.Object getValue(){
                    return _operation.version();
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Params"),String.class,Util.getLocalizedString("TITLE_Params"),Util.getLocalizedString("TIP_OperationParams")){
      
                public java.lang.Object getValue(){
                    ParameterDescription[] parameters = _operation.params();
                    String parameter = "";
                    for (int i=0; i< parameters.length; i++){
                        if (i!=0) parameter+=", ";
                        parameter+= pm2Str(parameters[i].mode)+" "+Util.typeCode2TypeString(parameters[i].type)+" "+parameters[i].name;
                    }
                    return parameter;
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Return"),String.class,Util.getLocalizedString("TITLE_Return"),Util.getLocalizedString("TIP_OperationReturn")){
                public java.lang.Object getValue(){
                    return Util.typeCode2TypeString(_operation.result());
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Except"),String.class,Util.getLocalizedString("TITLE_Except"),Util.getLocalizedString("TIP_OperationExcept")){
                public java.lang.Object getValue(){
                    ExceptionDef[] exceptions = _operation.exceptions();
                    String exception="";
                    for (int i=0; i<exceptions.length;i++){
                        if (i!=0) exception+=", ";
                        exception+=exceptions[i].name();
                    }
                    return exception;
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Context"),String.class,Util.getLocalizedString("TITLE_Context"),Util.getLocalizedString("TIP_OperationContext")){
                public java.lang.Object getValue(){
                    String[] contexts = _operation.contexts();
                    String context = "";
                    for (int i=0; i<contexts.length; i++){
                        if (i!= 0) context+=", ";
                        context+=contexts[i];
                    }
                    return context;
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Modifiers"),String.class,Util.getLocalizedString("TITLE_Modifiers"),Util.getLocalizedString("TIP_OperationModifiers")){
                public java.lang.Object getValue(){
                    switch (_operation.mode().value()){
                    case OperationMode._OP_NORMAL:
                        return "normal";                  // NO I18N
                    case OperationMode._OP_ONEWAY:
                        return "oneway";                  // NO I18N
                    default:
                        return "unknown";                 // NO I18N
                    }
                }
            });
        return s;
    }
    
    public org.omg.CORBA.IRObject getIRObject () {
        return this._operation;
    }
    
    private String pm2Str(ParameterMode pm) {
        switch (pm.value()){
            case ParameterMode._PARAM_IN:
                return "in";          // NO I18N
            case ParameterMode._PARAM_OUT:
                return "out";         // NO I18N
            case ParameterMode._PARAM_INOUT:
                return "inout";       // NO I18N
            default:
                return "";            // NO I18N
            }
    }
  
}
