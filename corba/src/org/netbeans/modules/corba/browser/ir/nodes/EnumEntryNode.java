/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;

/**
 *
 * @author  tzezula
 * @version
 */
public class EnumEntryNode extends IRLeafNode {

    private static final String ENUM_ENTRY_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/declarator";

    private class EnumEntryCodeGenerator implements GenerateSupport {
        
        public String generateHead (int indent, StringHolder currentPrefix) {
            return "";
        }
        
        public String generateTail (int indent) {
            return "";
        }
        
        public String generateSelf (int indent, StringHolder currentPrefix) {
            return getName();
        }
        
        public String getRepositoryId () {
            return Util.getLocalizedString("MSG_EnumEntry");
        }
    }
  
    /** Creates new EnumEntryNode */
    public EnumEntryNode(String name) {
        super();
        this.name = name;
        this.setIconBase(ENUM_ENTRY_ICON_BASE);
        this.getCookieSet().add(new EnumEntryCodeGenerator ());
    }
  
  
    public final String getName(){
        return this.getDisplayName();
    }
  
    public final String getDisplayName(){
        return this.name;
    }
  
    public Sheet createSheet (){
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Name"),String.class,Util.getLocalizedString("TITLE_Name"),Util.getLocalizedString("TIP_EnumEntryName")){
                public java.lang.Object getValue(){
                    return name;
                }
            });
        return s;
    }
  
}
