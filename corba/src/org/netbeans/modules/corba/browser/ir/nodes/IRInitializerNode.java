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

/*
 * IRInitializerNode.java
 *
 * Created on August 28, 2000, 9:37 PM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.*;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;


/**
 *
 * @author  tzezula
 * @version 
 */
public class IRInitializerNode extends IRLeafNode implements Node.Cookie {
    
    private Initializer _initializer;
    private static final String ICON_BASE = 
        "org/netbeans/modules/corba/idl/node/const";
    
    private class InitializerCodeGenerator implements GenerateSupport {
    
        public InitializerCodeGenerator () {
        }
    
        public String generateHead(int indent, StringHolder currentPrefix){
            // Can not have its own prefix
            return "";
        }
    
        /** Because this type of entity has weak identity and is identified by its name and parent
         *  there is no Repository for it, that implies fact that this can't have pragmas at all
         */
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = "";
            for (int i=0; i<indent; i++)
                code =code + SPACE;
            StringHolder dimension = new StringHolder();
            code = code + "factory " + _initializer.name + " (";
            for (int i=0; i<_initializer.members.length; i++) {
                if (i!=0) 
                    code = code + ", in " + Util.idlType2TypeString(_initializer.members[i].type_def, ((IRContainerNode)getParentNode()).getOwner()) + " " + _initializer.members[i].name;
                else
                    code = code + "in " +  Util.idlType2TypeString(_initializer.members[i].type_def, ((IRContainerNode)getParentNode()).getOwner()) + " " + _initializer.members[i].name;
            }
            code = code +");\n";
            
            return code;
        }
    
        public String generateTail (int indent){
            return "";
        }
        
        public String getRepositoryId () {
            return Util.getLocalizedString("MSG_ValueTypeFactory");
        }
    }

    /** Creates new IRInitializerNode */
    public IRInitializerNode(Initializer initializer) {
        super ();
        this._initializer = initializer;
        this.setIconBase (ICON_BASE);
        this.getCookieSet().add (this);
        this.getCookieSet().add (new InitializerCodeGenerator());
    }
    
    public String getName () {
        return this.getDisplayName();
    }
    
    public String getDisplayName () {
        if (this.name == null) {
            if (this._initializer != null) {
                this.name = this._initializer.name;
            }
            else {
                this.name = "";
            }
        }
        return this.name;
    }
    
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set set = s.get (Sheet.PROPERTIES);
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"),String.class,Util.getLocalizedString("TITLE_Name"),Util.getLocalizedString("TIP_InitializerName")) {
            public java.lang.Object getValue () {
                return name;
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Params"),String.class,Util.getLocalizedString("TITLE_Params"),Util.getLocalizedString("TIP_InitializerParams")) {
            public java.lang.Object getValue () {
                String params = "";
                StructMember[] mbrs = _initializer.members;
                for (int i=0; i< mbrs.length; i++) {
                    if (i>0)
                        params = params + ", in " + Util.typeCode2TypeString (mbrs[i].type)+" "+mbrs[i].name;
                    else
                        params = "in " + Util.typeCode2TypeString (mbrs[i].type)+" "+mbrs[i].name;
                }
                return params;
            }
        });
        return s;
    }
    

}
