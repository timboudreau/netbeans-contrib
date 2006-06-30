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

package org.netbeans.modules.corba.idl.editor.settings;

import org.openide.util.NbBundle;

import org.netbeans.modules.editor.options.BaseOptions;
import org.netbeans.modules.corba.idl.editor.coloring.IDLKit;

/**
 * Options for the IDL Editor Kit
 *
 * @author Libor Kramolis
 */
public class IDLOptions extends BaseOptions {

    public static final String IDL = "idl";

    static final long serialVersionUID =6740905428824290883L;
    public IDLOptions() {
        super (IDLKit.class, IDL);
        //System.out.println ("IDLOptions ()");
    }

    public String displayName () {
        //System.out.println ("name: " + NbBundle.getBundle (IDLOptions.class).getString
        //		("CTL_IDLOptions_Name"));
        return NbBundle.getBundle (IDLOptions.class).getString ("CTL_IDLOptions_Name");
    }
    
    public Class getDefaultIndentEngineClass () {
        return org.netbeans.modules.corba.idl.editor.indent.IDLIndentEngine.class;
    }

}

/*
 * <<Log>>
 *  2    Gandalf   1.1         11/27/99 Patrik Knakal   
 *  1    Gandalf   1.0         11/9/99  Karel Gardas    
 * $
 */
