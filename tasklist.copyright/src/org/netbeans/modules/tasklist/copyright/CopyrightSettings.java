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

package org.netbeans.modules.tasklist.copyright;

import org.openide.options.SystemOption;

/**
 * Copyright module settings
 */
public final class CopyrightSettings extends SystemOption {

    public static final String PROP_SCAN_COPYRIGHT = "scanCopyright";	//NOI18N

    /**
     * @return The copyright to insert when fixing missing
     * copyright problems.
     */
    public String getScanCopyright() {
        String c = (String) getProperty(PROP_SCAN_COPYRIGHT);
        return c;
    }

    /** Sets the scanCopyright type
     * @param copyright The copyright to insert when fixing missing
     * copyright problems.
     */
    public void setScanCopyright(String copyright) {
        putProperty(PROP_SCAN_COPYRIGHT, copyright, true);
        //Done above: firePropertyChange(PROP_SCAN_COPYRIGHT, null, copyright);
    }

    public String displayName() {
        return "Copyright settings";  //XXX
    }

}
