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

import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;


public class IRModuleDefNode extends IRContainerNode {

    private ModuleDef _module;
    private static final String MODULE_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/module";
  
    private class ModuleCodeGenerator implements GenerateSupport {
    
        public ModuleCodeGenerator (){
        }
    
        public String generateHead (int indent, StringHolder currentPrefix){
            String code = Util.generatePreTypePragmas (_module.id(), _module.absolute_name(), currentPrefix, indent);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            return code + fill +"module " + _module.name() +" {\n";
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = "";
            code = code + generateHead(indent, currentPrefix);
            String prefixBackUp = currentPrefix.value;
            Children cld = (Children) getChildren();
            if (cld.getState() == Children.NOT_INITIALIZED)
                ((Children)getChildren()).state = Children.SYNCHRONOUS;
            Node[] nodes = cld.getNodes();
            for (int i=0; i< nodes.length; i++) {
                GenerateSupport gs = (GenerateSupport) nodes[i].getCookie (GenerateSupport.class);
                if (gs != null)
                    code = code + gs.generateSelf (indent +1, currentPrefix);
            }
            code = code + generateTail(indent);
            // We go out of scope, restore prefix
            currentPrefix.value = prefixBackUp;
            return code;
        }
    
        public String generateTail (int indent) {
            String code ="";
            for (int i=0; i<indent; i++)
                code = code + SPACE;
            return code + "}; // "+_module.name() + "\n" + Util.generatePostTypePragmas(_module.name(), _module.id(), indent)+"\n";
        }
        
        
        public String getRepositoryId () {
            return _module.id();
        }
        
    } // End of Inner Class

    public IRModuleDefNode(Container value) {
        super (new ContainerChildren (value));
        _module = ModuleDefHelper.narrow (value);
        setIconBase (MODULE_ICON_BASE);
        this.getCookieSet().add ( new ModuleCodeGenerator());
    }

    public String getDisplayName () {
        if (this.name == null) {
            if (_module != null)
                this.name = _module.name ();
            else 
                this.name = "";
        }
        return this.name;
    }

    public String getName () {
        return this.getDisplayName ();
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_ModuleName")) {
                public java.lang.Object getValue () {
                    return _module.name ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"), String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString("TIP_ModuleId")) {
                public java.lang.Object getValue () {
                    return _module.id ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), 
                                              Util.getLocalizedString("TIP_ModuleVersion")) {
                public java.lang.Object getValue () {
                    return _module.version ();
                }
            });

        return s;
    }
    
    public org.omg.CORBA.Contained getOwner () {
        return this._module;
    }
    
    public org.omg.CORBA.IRObject getIRObject() {
        return this._module;
    }    
	
}

/*
 * $Log
 * $
 */
