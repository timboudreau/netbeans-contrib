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
import org.openide.TopManager;
import org.openide.nodes.Sheet;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.datatransfer.ExClipboard;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;
/**
 *
 * @author  tzezula
 * @version 
 */
public class IRValueMemberDefNode extends IRLeafNode implements Node.Cookie {
    
    private static final String ICON_BASE = 
        "org/netbeans/modules/corba/idl/node/declarator";
    private ValueMemberDef _member;
    
    private class ValueMemberCodeGenerator implements GenerateSupport {
    
        public String generateHead (int indent, StringHolder currentPrefix){
            return Util.generatePreTypePragmas (_member.id(), _member.absolute_name(), currentPrefix, indent);      //NOI18N
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead (indent, currentPrefix);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill;
            short modifier = _member.access();
            switch (modifier) {
                case PRIVATE_MEMBER.value:
                    code = code + "private ";
                    break;
                case PUBLIC_MEMBER.value:
                    code = code + "public ";
                    break;
                }
            code = code + Util.typeCode2TypeString(_member.type()) + " " + _member.name()+ ";\n";        //NOI18N
            code = code + generateTail (indent);
            return code;  
        }
    
        public String generateTail (int indent){
            return Util.generatePostTypePragmas (_member.name(), _member.id(), indent);      //NOI18N
        }
        
        public String getRepositoryId () {
            return _member.id();
        }
    }

    /** Creates new IRValueMemberDefNode */
    public IRValueMemberDefNode(Contained contained) {
        super ();
        this._member = ValueMemberDefHelper.narrow (contained);
        this.setIconBase (ICON_BASE);
        this.getCookieSet().add (this);
        this.getCookieSet().add ( new ValueMemberCodeGenerator());
    }
    
    public String getDisplayName () {
        return this.getName();
    }
    
    public String getName () {
        if (this.name == null) {
            if (this._member != null) {
                this.name = this._member.name();
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
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_ValueMemberName")) {
            public java.lang.Object getValue () {
                return name;
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"),String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString ("TIP_ValueMemberId")) {
            public java.lang.Object getValue () {
                return _member.id();
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), Util.getLocalizedString ("TIP_ValueMemberVersion")) {
            public java.lang.Object getValue () {
                return _member.version();
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Type"), String.class, Util.getLocalizedString("TITLE_Type"), Util.getLocalizedString ("TIP_ValueMemberType")) {
            public java.lang.Object getValue () {
                return Util.typeCode2TypeString(_member.type());
            }
        });
        set.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Visibility"), String.class, Util.getLocalizedString("TITLE_Visibility"), Util.getLocalizedString ("TIP_ValueMemberVisibility")) {
            public java.lang.Object getValue () {
                short modifier = _member.access();
                switch (modifier) {
                    case PRIVATE_MEMBER.value:
                        return "private";
                    case PUBLIC_MEMBER.value:
                        return "public";
                    default:
                        return "unknown";
                }
            }
        });
        
        return s;
    }

}
