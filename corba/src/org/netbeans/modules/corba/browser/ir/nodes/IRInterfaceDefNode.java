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
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;



public class IRInterfaceDefNode extends IRContainerNode {

    InterfaceDef _interface;
    boolean isAbstract;

    private static final String INTERFACE_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/interface";
  
    private class InterfaceCodeGenerator implements GenerateSupport {
     
    
    
        //* DANGER!!! Semantic shift of function */
        public String generateHead (int indent, StringHolder currentPrefix) {
            String code = Util.generatePreTypePragmas (_interface.id(), _interface.absolute_name(), currentPrefix, indent);
            String fill ="";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill;
            try {
                if (isAbstract || _interface.is_abstract ())
                    code = code + "abstract ";
            }catch (org.omg.CORBA.SystemException sysExc) {}
            code = code + "interface " + _interface.name () + " {\n";
            return code;
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = Util.generatePreTypePragmas (_interface.id(), _interface.absolute_name(), currentPrefix, indent);
            String prefixBackUp = currentPrefix.value;
            String fill = "";
            int dk;
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill;
            try {
                if (isAbstract || _interface.is_abstract ())
                    code = code + "abstract ";
            } catch (org.omg.CORBA.SystemException sysExc) {}
            code = code + "interface " + _interface.name ();
            InterfaceDef[] base = _interface.base_interfaces();
            if (base.length > 0){
                code = code + " : ";
                for (int i = 0; i<base.length; i++){
                    if (i != 0)
                        code = code + ", ";
                    code = code + base[i].name();    
                }
            }
            code = code +" {\n";
            Children cld = (Children) getChildren ();
            if (cld.getState() == Children.NOT_INITIALIZED)
                ((Children)getChildren()).state = Children.SYNCHRONOUS;
            Node[] nodes = cld.getNodes();
            for (int i=0; i< nodes.length; i++) {
                GenerateSupport gs = (GenerateSupport) nodes[i].getCookie (GenerateSupport.class);
                if (gs != null)
                    code = code + gs.generateSelf (indent+1, currentPrefix);
            }
            code = code + generateTail(indent);
            // Going out of scope restore prefix
            currentPrefix.value = prefixBackUp;
            return code;
        }
    
        public String generateTail (int indent){
            String code = "";
            for (int i=0; i< indent; i++)
                code = code + SPACE;
            return code + "}; // " + _interface.name() + "\n" +Util.generatePostTypePragmas (_interface.name(), _interface.id(), indent) +"\n";
        }
        
        public String getRepositoryId () {
            return _interface.id();
        }
    
    }
    
    public IRInterfaceDefNode (Container value) {
        this (value, false);
    }

    public IRInterfaceDefNode(Container value, boolean isAbstract) {
        super (new ContainerChildren(value));
        setIconBase (INTERFACE_ICON_BASE);
        this._interface = InterfaceDefHelper.narrow (value);
        this.isAbstract = isAbstract;
        this.getCookieSet().add ( new InterfaceCodeGenerator ());
    }

    public String getDisplayName () {
        if (this.name == null) {
            if (_interface != null)
                this.name = _interface.name ();
            else 
                this.name = "";
        }
        return this.name;
    }

    public String getName () {
        return this.getDisplayName();
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_InterfaceName")) {
                public java.lang.Object getValue () {
                    return _interface.name ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"), String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString("TIP_InterfaceId")) {
                public java.lang.Object getValue () {
                    return _interface.id ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), 
                                              Util.getLocalizedString("TIP_InterfaceVersion")) {
                public java.lang.Object getValue () {
                    return _interface.version ();
                }
            });
    
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Super"), String.class, Util.getLocalizedString("TITLE_Super"), 
                                              Util.getLocalizedString("TIP_InterfaceSuper")) {
                public java.lang.Object getValue () {
                    String inher = "";
                    if (_interface.base_interfaces().length > 0) {
                        InterfaceDef[] base = _interface.base_interfaces();
                        for (int i=0; i<base.length; i++)
                            inher = inher + (base[i]).name () + ", ";
                        inher = inher.substring (0, inher.length () - 2);
                    }
                    else
                        inher = "";
                    return inher;
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Abstract"), String.class, Util.getLocalizedString("TITLE_Abstract"), Util.getLocalizedString ("TIP_InterfaceAbstract")) {
            public java.lang.Object getValue () {
                try {
                    if (isAbstract || _interface.is_abstract())
                        return Util.getLocalizedString ("MSG_Yes");
                    else
                        return Util.getLocalizedString ("MSG_No");
                }catch (org.omg.CORBA.SystemException sysExc) {
                    return Util.getLocalizedString ("MSG_No");
                }
            }
        });
        return s;
    }
  
    
    public org.omg.CORBA.Contained getOwner () {
        return this._interface;
    }
    

}

/*
 * $Log
 * $
 */
