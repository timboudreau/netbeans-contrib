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

import java.io.IOException;
import java.io.PrintWriter;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
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
public class IRValueBoxDefNode extends IRLeafNode implements Node.Cookie, Generatable {
    
    private static final String ICON_BASE = 
        "org/netbeans/modules/corba/idl/node/declarator";
    private ValueBoxDef _valuebox;

    private class ValueBoxCodeGenerator implements GenerateSupport {
        
        public String generateHead (int indent, StringHolder currentPrefix){
            return Util.generatePreTypePragmas (_valuebox.id(), _valuebox.absolute_name(), currentPrefix, indent);      //NOI18N
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead (indent, currentPrefix);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill;
            code = code + "valuetype " + _valuebox.name() + " " +Util.idlType2TypeString(_valuebox.original_type_def(),((IRContainerNode)getParentNode()).getOwner())+ ";\n";        //NOI18N
            code = code + generateTail (indent);
            return code;  
        }
    
        public String generateTail (int indent){
            return Util.generatePostTypePragmas (_valuebox.name(), _valuebox.id(), indent);      //NOI18N
        }
        
        public String getRepositoryId () {
           return _valuebox.id();
        }
        
    }
    
    /** Creates new ValueBoxDefNode */
    public IRValueBoxDefNode(Contained contained) {
        super ();
        this._valuebox = ValueBoxDefHelper.narrow (contained);
        this.setIconBase (ICON_BASE);
        this.getCookieSet().add (this);
        this.getCookieSet().add ( new ValueBoxCodeGenerator());
    }
    
    public String getDisplayName () {
        if (this.name == null) {
            if (this._valuebox != null) {
                this.name  = this._valuebox.name();
            }
            else {
                this.name = "";
            }
        }
        return this.name;
    }
    
    public String getName () {
        return this.getDisplayName();
    }
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set set = s.get (Sheet.PROPERTIES);
        set.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_ValueBoxName")) {
            public java.lang.Object getValue () {
                return name;
            }
        });
        
        set.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"),String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString ("TIP_ValueBoxId")) {
            public java.lang.Object getValue () {
                return _valuebox.id ();
            }
        });
        
        set.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), Util.getLocalizedString ("TIP_ValueBoxVersion")) {
            public java.lang.Object getValue () {
                return _valuebox.version ();
            }
        });
        
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_OriginalType"), String.class, Util.getLocalizedString("TITLE_OriginalType"), Util.getLocalizedString ("TIP_ValueBoxOriginalType")) {
            public java.lang.Object getValue () {
                return Util.typeCode2TypeString (_valuebox.original_type_def().type());
            }
        });
        
        return s;
    }
    
    public void generateCode() {
        ExClipboard clipboard = TopManager.getDefault().getClipboard();
        StringSelection genCode = new StringSelection ( this.generateHierarchy ());
        clipboard.setContents(genCode,genCode);
    }

    public void generateCode (PrintWriter out) throws IOException {
        String hierarchy = this.generateHierarchy ();
        out.println (hierarchy);
    }
    
    private String generateHierarchy () {
        Node node = this.getParentNode();
        String code ="";

        // Generate the start of namespace
        ArrayList stack = new ArrayList();
        while ( node instanceof IRContainerNode){
            stack.add(node.getCookie (GenerateSupport.class));
            node = node.getParentNode();
        }
        int size = stack.size();
        org.omg.CORBA.StringHolder currentPrefix = new org.omg.CORBA.StringHolder ();
        for (int i = size -1 ; i>=0; i--)
            code = code + ((GenerateSupport)stack.get(i)).generateHead((size -i -1), currentPrefix);

        // Generate element itself
        code = code + ((GenerateSupport)this.getCookie (GenerateSupport.class)).generateSelf(size, currentPrefix);
        //Generate tail of namespace
        for (int i = 0; i< stack.size(); i++)
            code = code + ((GenerateSupport)stack.get(i)).generateTail((size -i));
        return code;
    }

}
