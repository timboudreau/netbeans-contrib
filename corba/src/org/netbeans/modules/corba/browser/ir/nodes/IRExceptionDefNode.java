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

/** 
 *
 * @author  Tomas Zezula
 * @version 
 */
public class IRExceptionDefNode extends IRContainerNode {

    private ExceptionDef _exception;
    private static final String EXCEPTION_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/exception";
  
    private static class ExceptionCodeGenerator implements GenerateSupport {
        private ExceptionDef _exception;
    
        public ExceptionCodeGenerator (ExceptionDef exception){
            this._exception = exception;
        }
    
        public String generateHead (int indent){
            String code = "";
            for (int i=0; i<indent; i++)
                code = code + "  ";
            code = code + "exception " + _exception.name() + " {\n";
            return code;	
        }
    
        public String generateSelf (int indent){
            String code = generateHead(indent);
            String fill = "";
            for (int i=0; i<=indent; i++)
                fill = fill + "  ";
            StructMember[] members = _exception.members();
            StringHolder dimension = new StringHolder();
            for (int i = 0; i < members.length; i++){
                dimension.value = null;
                code = code + fill + Util.typeCode2TypeString ( members[i].type, dimension) + " "+members[i].name+((dimension.value==null)?"":dimension.value)+";\n";
            }
            code = code + generateTail (indent);
            return code;
        }
    
        public String generateTail (int indent){
            String code = "";
            for (int i=0; i<indent; i++)
                code = code + "  ";
            code = code + "}; // Exception " + _exception.name() + "\n\n";
            return code;
        }
    
    }
  
    /** Creates new IRExceptionDefNode */
    public IRExceptionDefNode(Contained value) {
        super(new ExceptionChildren(ExceptionDefHelper.narrow(value)));
        _exception = ExceptionDefHelper.narrow(value);
        setIconBase(EXCEPTION_ICON_BASE);
    }
  
  
    public String getDisplayName(){
        if (_exception != null)
            return _exception.name();
        else 
            return "";
    }
  
  
    public String getName(){
        return this.getDisplayName();
    }
  
    public Sheet createSheet (){
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Name"),String.class,Util.getLocalizedString("TITLE_Name"),Util.getLocalizedString("TIP_ExceptionName")){
                public java.lang.Object getValue(){
                    return _exception.name();
                }
            });
        ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Id"),String.class,Util.getLocalizedString("TITLE_Id"),Util.getLocalizedString("TIP_ExceptionId")){
                public java.lang.Object getValue(){
                    return _exception.id();
                }
            });
        ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Version"),String.class,Util.getLocalizedString("TITLE_Version"),Util.getLocalizedString("TIP_ExceptionVersion")){
                public java.lang.Object getValue(){
                    return _exception.version();
                }
            });
        return s;
    }
  
    public String getRepositoryId () {
        return this._exception.id();
    }
  
    public GenerateSupport createGenerator () {
        if (this.generator == null)
            this.generator = new ExceptionCodeGenerator (_exception);
        return this.generator;
    }
  
    public static GenerateSupport createGeneratorFor (Contained type){
        ExceptionDef exception = ExceptionDefHelper.narrow (type);
        if (exception == null)
            return null;
        return new ExceptionCodeGenerator (exception);
    }
  
}
