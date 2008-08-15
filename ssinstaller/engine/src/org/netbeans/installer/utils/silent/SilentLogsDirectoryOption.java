/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.utils.silent;

import java.io.File;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.cli.CLIArgumentsList;
import org.netbeans.installer.utils.cli.CLIOptionOneArgument;
import org.netbeans.installer.utils.exceptions.CLIOptionException;

public class SilentLogsDirectoryOption extends CLIOptionOneArgument {

    public static final String SILENT_LOGS_DIR_ARG = "--silent-logs-dir";// NOI18N
    
    private final String WARNING_INCONSISTEN_LOGS_DIRECTORY_KEY = "S.warning.inconsistent.logs.directory"; // NOI18N
    private final String WARNING_BAD_SILENT_LOGS_DIRECTORY_ARG_KEY = "S.warning.bad.silent.logs.directory.arg"; // NOI18N
    
    @Override
    public String getName() {
        return SILENT_LOGS_DIR_ARG;
    }

    @Override
    public void execute(CLIArgumentsList arguments) throws CLIOptionException {
        File dir = new File(arguments.next()).getAbsoluteFile();
        if (dir.exists() && dir.isDirectory() && FileUtils.canWrite(dir)) {
            System.setProperty(SilentLogManager.SILENT_LOGS_DIRECTORY_OPTION, dir.getAbsolutePath());
        } else {
            throw new CLIOptionException(ResourceUtils.getString(
                    SilentLogsDirectoryOption.class,
                    WARNING_INCONSISTEN_LOGS_DIRECTORY_KEY,
                    SILENT_LOGS_DIR_ARG,
                    dir));            
        }
    }
    
    @Override
    protected String getLackOfArgumentsMessage() {
        return ResourceUtils.getString(
                SilentLogsDirectoryOption.class,
                WARNING_BAD_SILENT_LOGS_DIRECTORY_ARG_KEY,
                SILENT_LOGS_DIR_ARG);
    }

}
