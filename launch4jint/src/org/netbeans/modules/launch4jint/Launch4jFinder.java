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

package org.netbeans.modules.launch4jint;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/** Handles location of Launch4j installation, asks the user and persists
 * received information.
 *
 * @author Dafe Simonek
 */
public class Launch4jFinder {

    private static final String JAR_NAME = "launch4j.jar";

    private static final String KEY_DIR = "launch4jDir";

    /** A little hack - file object as placeholder to store simple data as its
     * properties */
    private static String CONFIG_PATH = "Services/org-netbeans-modules-launch4jint-Config.data";
    private static final FileObject configFO = Repository.getDefault().getDefaultFileSystem().
                                findResource(CONFIG_PATH);
    
    /** Accepts only files named "launch4j.jar" */
    private static final FilenameFilter LAUNCH4J_JAR = new FilenameFilter () {
        public boolean accept(File dir, String name) {
            return JAR_NAME.equalsIgnoreCase(name);
        }
    };
    
    /** utility class, no need to instantiate */
    private Launch4jFinder () {
    }
    
    /** Retrieves and returns installation directory of Launch4j product.
     *
     * @return Launch4j install dir or null if Launch4j couldn't be located.
     */
    public static String getLaunch4jDir () {
        // check persisted data first
        String dir = getPersistedDir();
        if (dir != null && checkDir(dir)) {
            return dir;
        }
        
        // ask user to install/locate Launch4j
        String result = LocatePanel.obtainLaunch4jDir();
        if (result != null && checkDir(result)) {
            setPersistedDir(result);
            return result;
        }
        
        return null;
    }
    
    private static String getPersistedDir() {
        return (String) configFO.getAttribute(KEY_DIR);
    }
    
    private static void setPersistedDir(String dir) {
        try {
            configFO.setAttribute(KEY_DIR, dir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    static boolean checkDir(String dir) {
        File file = new File(dir);
        File[] list = file.listFiles(LAUNCH4J_JAR);
        return list != null && list.length == 1;
    }
    
    
}
