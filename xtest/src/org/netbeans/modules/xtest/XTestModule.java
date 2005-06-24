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
package org.netbeans.modules.xtest;

import java.io.File;
import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.ModuleInstall;

public class XTestModule extends ModuleInstall {

    public void restored () {
        File xtestHome = InstalledFileLocator.getDefault().
            locate("xtest-distribution", "org.netbeans.modules.xtest", false);  // NOI18N
        try {
            System.setProperty("xtest.home", xtestHome.getCanonicalPath()); // NOI18N
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
        }
    }
}
