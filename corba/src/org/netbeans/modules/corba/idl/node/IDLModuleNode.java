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

package org.netbeans.modules.corba.idl.node;

import org.openide.nodes.CookieSet;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.ModuleElement;


/**
 * Class IDLModuleNode
 *
 * @author Karel Gardas
 */

public class IDLModuleNode extends IDLAbstractNode {

    ModuleElement _module;

    private static final String MODULE_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/module"; // NOI18N

    public IDLModuleNode (ModuleElement value) {
        super (new IDLDocumentChildren ((IDLElement)value));
        setIconBase (MODULE_ICON_BASE);
        _module = value;
        setCookieForDataObject (_module.getDataObject ());
    }

    public IDLElement getIDLElement () {
        return _module;
    }
    /*
      public String getDisplayName () {
      if (_module != null)
      //return ((Identifier)_interface.jjtGetChild (0)).getName ();
      return _module.getName ();
      else
      return "NoName :)";
      }
    */
    public String getName () {
        return "module"; // NOI18N
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_MODULE) { // NOI18N
                    public Object getValue () {
                        return _module.getName ();
                    }
                });

        return s;
    }

}

/*
 * $Log
 * $
 */
