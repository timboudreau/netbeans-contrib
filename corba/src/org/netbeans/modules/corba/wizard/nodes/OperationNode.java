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

package org.netbeans.modules.corba.wizard.nodes;

import java.util.StringTokenizer;
import org.openide.util.actions.SystemAction;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;

/** 
 *
 * @author  root
 * @version 
 */
public class OperationNode extends AbstractMutableLeafNode implements Node.Cookie {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/operation";
  
    /** Creates new OperationNode */
    public OperationNode (NamedKey key) {
        super (key);
        this.getCookieSet ().add (this);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
    }
  
    public SystemAction[] createActions () {
        return new SystemAction [] {
            SystemAction.get (DestroyAction.class),
            SystemAction.get (RenameAction.class)
        };
    }
  
    public String generateSelf (int indent) {
        String code = new String ();
        for (int i=0; i<indent; i++)
            code = code + SPACE;  // No I18N
        OperationKey key = (OperationKey) this.key;
        if (key.isOneway ())
            code = code + "oneway "; // No I18N
        code = code + key.getReturnType () + " "; // No I18N
        code = code + this.getName () + " ("; // No I18N
        code = code + key.getParameters () + ")"; // No I18N
        if (key.getExceptions().length() > 0) {
            code = code + "raises ("+key.getExceptions () +")"; // No I18N
        }
        if (key.getContext().length() > 0) {
            code = code + "context (";  // No I18N
            String ctx = key.getContext ();
            StringTokenizer tk = new StringTokenizer (ctx, ","); // No I18N
            while (tk.hasMoreTokens()) {
                String one = tk.nextToken ().trim();
                if (one.startsWith ("\"") && one.endsWith ("\"")) { // No I18N
                    code = code + one + ", "; // No I18N
                }
                else {
                    code = code + "\"" + one +"\", "; // No I18N
                }
        
            }
            code = code.substring(0,code.length() -2);
            code = code + ")";  // No I18N
        }
        code = code + ";\n"; // No I18N
        return code;
    }
  
}
