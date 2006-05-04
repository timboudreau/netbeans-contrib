/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.buildmonitor;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import javax.swing.*;

/**
 * Typesafe enum for build status states.
 *
 * @author tball
 */
public class Status {
    /** Build succeeded and passed all tests. */
    public static final Status SUCCESS = 
	new Status("LBL_SUCCESS", "org/netbeans/modules/buildmonitor/resources/build_passed.gif");     //NOI18N
    
    /** Build failed. */
    public static final Status FAILED = 
	new Status("LBL_FAILED", "org/netbeans/modules/buildmonitor/resources/build_failed.gif");      //NOI18N
    
    /** Build succeeded, but one or more tests failed. */
    public static final Status TESTS_FAILED = 
	new Status("LBL_TEST_FAILED", "org/netbeans/modules/buildmonitor/resources/tests_failed.gif"); //NOI18N
    
    /** No status available from build.  It may be too soon, or the
     *  URL isn't currently accessible. 
     */
    public static final Status NO_STATUS_AVAIL = 
	new Status("LBL_NO_STATUS", "org/netbeans/modules/buildmonitor/resources/no_status.gif");      //NOI18N
    
    public static final String CRUISECONTROL_SUCCESS = ", passed";
    public static final String CRUISECONTROL_FAILED = ", FAILED!";
    
    Icon getIcon() {
	return icon;
    }
    
    String getName() {
        return name;
    }
    
    public static Status lookup(String text) {
        if (SUCCESS.name.equals(text) || text.endsWith(CRUISECONTROL_SUCCESS))
            return SUCCESS;
        if (FAILED.name.equals(text) || text.endsWith(CRUISECONTROL_FAILED))
            return FAILED;
        if (TESTS_FAILED.name.equals(text))
            return TESTS_FAILED;
        return NO_STATUS_AVAIL;
    }

    private final String name;
    private final ImageIcon icon;
    private Status(String nameKey, String iconResource) {
	this.name = NbBundle.getBundle(Status.class).getString(nameKey);
	this.icon = new ImageIcon(Utilities.loadImage(iconResource), name);
    }
}
