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


package org.netbeans.modules.buildmonitor;

import java.util.Locale;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Typesafe enum for build status states.
 *
 * @author Tom Ball, Jesse Glick
 */
public enum Status {
    /** Build succeeded and passed all tests. */
    SUCCESS("LBL_SUCCESS", "org/netbeans/modules/buildmonitor/resources/build_passed.gif"),     //NOI18N

    /** Build failed. */
    FAILED("LBL_FAILED", "org/netbeans/modules/buildmonitor/resources/build_failed.gif"),      //NOI18N
    
    /** Build succeeded, but one or more tests failed. */
    TESTS_FAILED("LBL_TEST_FAILED", "org/netbeans/modules/buildmonitor/resources/tests_failed.gif"), //NOI18N
    
    /** No status available from build.  It may be too soon, or the
     *  URL isn't currently accessible. 
     */
    NO_STATUS_AVAIL("LBL_NO_STATUS", "org/netbeans/modules/buildmonitor/resources/no_status.gif");      //NOI18N
    
    public static final String CRUISECONTROL_SUCCESS = ", passed";
    public static final String CRUISECONTROL_FAILED = ", FAILED!";
    public static final String HUDSON_SUCCESS = "(SUCCESS)";
    public static final String HUDSON_FAILURE = "(FAILURE)";
    public static final String HUDSON_UNSTABLE = "(UNSTABLE)";
    
    Icon getIcon() {
	return icon;
    }
    
    public static Status lookup(String text) {
        if (SUCCESS.displayName.equals(text) || text.endsWith(CRUISECONTROL_SUCCESS) || text.endsWith(HUDSON_SUCCESS))
            return SUCCESS;
        if (FAILED.displayName.equals(text) || text.endsWith(CRUISECONTROL_FAILED) || text.endsWith(HUDSON_FAILURE))
            return FAILED;
        if (TESTS_FAILED.displayName.equals(text) || text.endsWith(HUDSON_UNSTABLE))
            return TESTS_FAILED;
        if (text.toLowerCase(Locale.US).indexOf("succe") != -1) {
            return SUCCESS;
        }
        if (text.toLowerCase(Locale.US).indexOf("fail") != -1) {
            return FAILED;
        }
        ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, 
                                      "BuildMonitor: unable to parse \"" + text + "\"");
        return NO_STATUS_AVAIL;
    }

    private final String displayName;
    private final Icon icon;
    private Status(String nameKey, String iconResource) {
	displayName = NbBundle.getMessage(BuildMonitor.class, nameKey);
	icon = new ImageIcon(Utilities.loadImage(iconResource), displayName);
    }
    
}
