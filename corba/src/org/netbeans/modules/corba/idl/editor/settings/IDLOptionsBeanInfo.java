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

package org.netbeans.modules.corba.idl.editor.settings;

import java.beans.*;
import java.awt.Image;
import java.util.ResourceBundle;

import org.openide.util.NbBundle;

import org.netbeans.modules.editor.options.PlainOptionsBeanInfo;

/** BeanInfo for plain options
 *
 * @author Libor Karmolis
 */
public class IDLOptionsBeanInfo extends PlainOptionsBeanInfo {

    public IDLOptionsBeanInfo () {
        super ("/org/netbeans/modules/editor/resources/htmlOptions");
        //System.out.println ("IDLOptionsBeanInfo ()");
    }

    protected Class getBeanClass() {
        return IDLOptions.class;
    }
}

/*
 * <<Log>>
 *  1    Gandalf   1.0         11/9/99  Karel Gardas    
 * $
 */
