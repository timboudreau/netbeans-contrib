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

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
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
