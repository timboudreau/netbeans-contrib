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

package org.netbeans.modules.corba.idl.src;

public class ModuleElement extends IDLElement {

    static final long serialVersionUID =7432704129176496385L;
    public ModuleElement (int id) {
        super(id);
    }

    public ModuleElement (IDLParser p, int id) {
        super(p, id);
    }

}
