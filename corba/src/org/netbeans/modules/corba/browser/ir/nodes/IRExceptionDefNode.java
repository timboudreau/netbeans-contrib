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
import org.openide.nodes.Node;
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
  
    private class ExceptionCodeGenerator implements GenerateSupport {
    
        public ExceptionCodeGenerator (){
        }
    
        public String generateHead (int indent, StringHolder currentPrefix){
            String code = Util.generatePreTypePragmas (_exception.id(), _exception.absolute_name(), currentPrefix, indent);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "exception " + _exception.name() + " {\n";
            return code;	
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead(indent, currentPrefix);
            String prefixBackUp = currentPrefix.value;
            String fill = "";
            for (int i=0; i<=indent; i++)
                fill = fill + SPACE;
            Children cld = (Children) getChildren();
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
    
        public String generateTail (int indent){
            String code = "";
            for (int i=0; i<indent; i++)
                code = code + SPACE;
            code = code + "}; // Exception " + _exception.name() + "\n";
            code = code + Util.generatePostTypePragmas (_exception.name(),_exception.id(),indent)+"\n";
            return code;
        }
        
        public String getRepositoryId () {
            return _exception.id();
        }
    
    }
  
    /** Creates new IRExceptionDefNode */
    public IRExceptionDefNode(Contained value) {
        super(new ExceptionChildren(value));
        _exception = ((ExceptionChildren)this.getChildren()).getExceptionStub();
        setIconBase(EXCEPTION_ICON_BASE);
        this.getCookieSet().add (new ExceptionCodeGenerator ());
    }
  
  
    public String getDisplayName(){
        if (this.name == null) {
            if (_exception != null)
               this.name = _exception.name();
            else 
                this.name = "";
        }
        return this.name;
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
    
    public org.omg.CORBA.Contained getOwner () {
        return this._exception;
    }
    
    public org.omg.CORBA.IRObject getIRObject() {
        return this._exception;
    }    
  
}
