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
