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
package org.netbeans.modules.corba.idl.editor.indent;

/**
 *
 * @author  tzezula
 */
public class IDLIndentAcceptor implements org.netbeans.editor.Acceptor {

    /** Creates new IDLIndentAcceptor */
    public IDLIndentAcceptor() {
    }

    public boolean accept (char c) {
        switch (c) {
            case IDLIndentEngine.L_CPAR:
            case IDLIndentEngine.R_CPAR:
                return true;
            default:
                return false;
        }
    }
}
