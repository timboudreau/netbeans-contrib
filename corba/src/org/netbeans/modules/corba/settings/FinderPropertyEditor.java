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

/** property editor for skel property CORBASupportSettings class
*
* @author Karel Gardas
* @version 0.01 March 8, 2001
*/

import org.netbeans.modules.corba.*;

public class FinderPropertyEditor extends PropertyEditorSupport {

    /** array of hosts */
    private static final String[] viewers = {ORBSettingsBundle.PACKAGE, ORBSettingsBundle.PACKAGE_AND_SUB_PACKAGES, ORBSettingsBundle.FILESYSTEM, ORBSettingsBundle.REPOSITORY};

    /** @return names of the supported LookAndFeels */
    public String[] getTags() {
        return viewers;
    }

    /** @return text for the current value */
    public String getAsText () {
        return (String) getValue();
    }

    /** @param text A text for the current value. */
    public void setAsText (String text) {
        setValue(text);
    }
}

