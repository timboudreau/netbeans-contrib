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

package org.netbeans.modules.corba.settings;

import java.beans.*;

import org.openide.util.NbBundle;

/** property editor for delegation property of ORBSettings class
*
* @author Karel Gardas
* @version 0.01 Nov 27, 2000
*/

import org.netbeans.modules.corba.*;

public class DelegationPropertyEditor extends PropertyEditorSupport {

    private static final String[] modes = {ORBSettingsBundle.DELEGATION_NONE,
					   ORBSettingsBundle.DELEGATION_STATIC,
                                           ORBSettingsBundle.DELEGATION_VIRTUAL};

    public String[] getTags() {
        return modes;
    }

    public String getAsText () {
        return (String) getValue();
    }

    public void setAsText (String text) {
        setValue(text);
    }
}

/*
 * $Log 
 * $
 */






