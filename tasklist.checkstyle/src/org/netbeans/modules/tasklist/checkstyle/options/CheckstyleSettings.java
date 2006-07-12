/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.checkstyle.options;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

/**
 * checkstyle module settings
 * @author hair
 * @version $Id$
 */
public final class CheckstyleSettings extends SystemOption {

    private static final long serialVersionUID = 1;

    public static final String PROP_SCAN_CHECKSTYLE = "checkstyle";	//NOI18N

    /**
     * @return The url to the checkstyle rules to use
     * copyright problems.
     */
    public String getCheckstyle() {
        String c = (String) getProperty(PROP_SCAN_CHECKSTYLE);
        if( c == null ){
            return getClass().getResource("checkstyle_checks.xml").toExternalForm();
        }
        return c;
    }

    /** Sets the checkstyle property
     * @param checkstyle The rules to use 
     * copyright problems.
     */
    public void setCheckstyle(String checkstyle) {
        putProperty(PROP_SCAN_CHECKSTYLE, checkstyle, true);
    }

    public String displayName() {
        return NbBundle.getMessage(CheckstyleSettings.class, "DisplayName");
    }

}
