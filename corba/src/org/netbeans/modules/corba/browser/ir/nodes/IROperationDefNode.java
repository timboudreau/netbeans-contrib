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


public class IROperationDefNode extends IRLeafNode {

    private OperationDef _operation;
    private static final String OPERATION_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/const";

    private static class OperationCodeGenerator implements GenerateSupport {
        private OperationDef _operation;

        public OperationCodeGenerator (OperationDef operation){
            this._operation = operation;
        }

        public String generateHead (int indent){
            return "";
        }

        public String generateSelf (int indent){
            String code = "";
            for (int i=0; i<indent; i++)
                code =code + "  ";
            if (_operation.mode() == OperationMode.OP_ONEWAY)
                code = code + "oneway ";
            code = code + Util.typeCode2TypeString(_operation.result())+" ";
            code = code + _operation.name() + " (";
            ParameterDescription[] params = _operation.params();
            for (int i = 0; i < params.length; i++){
                if (i != 0)
                    code = code +", ";
                code = code + OperationCodeGenerator.pm2Str(params[i].mode) + " ";
                code = code + Util.typeCode2TypeString (params[i].type) + " ";
                code = code + params[i].name;
            }
            code = code +" ) ";
            ExceptionDef[] exceptions = _operation.exceptions();
            if (exceptions.length > 0){
                code = code +"raises (";
                for (int i=0; i<exceptions.length; i++){
                    if ( i!= 0)
                        code = code+", ";
                    code = code + exceptions[i].name();
                }
                code = code +" )";
            }
            String[] context = _operation.contexts();
            if (context.length >0){
                code = code + "context ( ";
                for (int i=0; i< context.length; i++){
                    if (i != 0)
                        code = code+", ";
                    code = code + "\""+ context[i]+"\"";
                }
                code = code +" )";
            }
            code = code + ";\n";
            return code;
        }

        public String generateTail (int indent){
            return "";
        }

        public static String pm2Str(ParameterMode pm){
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

    /** Creates new IROperationDefNode */
    public IROperationDefNode(Contained value) {
        super();
        _operation = OperationDefHelper.narrow(value);
        setIconBase(OPERATION_ICON_BASE);
    }


    public String getDisplayName(){
        if (_operation != null)
            return _operation.name();
        else
            return "";
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
                             parameter+=OperationCodeGenerator.pm2Str(parameters[i].mode)+" "+Util.typeCode2TypeString(parameters[i].type)+" "+parameters[i].name;
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

    public GenerateSupport createGenerator () {
        if (this.generator == null)
            this.generator = new OperationCodeGenerator (_operation);
        return this.generator;
    }

    public static GenerateSupport createGeneratorFor (Contained type){
        OperationDef operation = OperationDefHelper.narrow (type);
        if (operation == null)
            return null;
        return new OperationCodeGenerator ( operation);
    }

}
