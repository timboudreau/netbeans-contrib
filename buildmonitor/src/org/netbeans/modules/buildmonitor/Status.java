/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
