/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s): Ilia Gromov
 */

package org.netbeans.modules.ide.analysis.modernize.options;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironmentFactory;
import org.netbeans.modules.nativeexecution.api.HostInfo;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.netbeans.modules.nativeexecution.api.util.HostInfoUtils;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author Ilia Gromov
 */
public class ClangAnalyzerOptions {

    public static final String CLANG_BINARY_NAME = "clang-tidy"; //NOI18N
    public static final String CLANG_BINARY_PATH = "clang-tidy-path"; //NOI18N

    private static String findInPaths() {
        String binaryName = CLANG_BINARY_NAME + "-" + getCodeBase(); //NOI18N
        String result = HostInfoUtils.searchFile(ExecutionEnvironmentFactory.getLocal(), Collections.<String>emptyList(), binaryName, true); // NOI18N
        return result;
    }

    public static String getClangAnalyzerPath() {
        String result = AnalyzerPreferences.getPreferences().get(CLANG_BINARY_PATH, ""); //NOI18N
        String oldValue = result;
        if (result.isEmpty()) {
            String toolPath = System.getProperty("devstudio.clangtidy.path"); //NOI18N
            if (toolPath != null) {
                result = toolPath;
            }
        }
        if (result.isEmpty()) {
            final String codeBase = getCodeBase();
            String relativePath = String.format("%s/%s-%s", CLANG_BINARY_NAME, CLANG_BINARY_NAME, codeBase); //NOI18N
            File toolFile = InstalledFileLocator.getDefault().locate(relativePath, codeBase, false);
            if (toolFile != null && toolFile.exists()) {
                result = toolFile.getAbsolutePath();
            }
        }
        if (result.isEmpty()) {
            String toolPath = findInPaths();
            if (toolPath != null) {
                result = toolPath;
            }
        }
        if (result.isEmpty()) {
            return null;
        } else {
            if (!oldValue.equals(result)) {
                AnalyzerPreferences.getPreferences().put(CLANG_BINARY_PATH, result);
            }
        }
        return result;
    }

    public static String getMissingModuleName() {
        return "com.oracle.tools.analysis.clangtidy." + getCodeBase(); //NOI18N
    }

    public static String getCodeBase() {
        HostInfo hostInfo = null;
        try {
            hostInfo = HostInfoUtils.getHostInfo(ExecutionEnvironmentFactory.getLocal());
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } catch (ConnectionManager.CancellationException ex) {
            ex.printStackTrace(System.err);
        }
        String module = null;
        if (hostInfo != null) {
            switch (hostInfo.getOS().getFamily()) {
                case SUNOS:
                    switch (hostInfo.getCpuFamily()) {
                        case X86:
                            switch (hostInfo.getOS().getBitness()) {
                                case _32:
                                    module = "SunOS_x86"; // NOI18N
                                    break;
                                case _64:
                                    module = "SunOS_x86_64"; // NOI18N
                                    break;
                            }
                            break;
                        case SPARC:
                            module = "SunOS_sparc"; // NOI18N
                            break;
                    }
                    break;
                case LINUX:
                    switch (hostInfo.getOS().getBitness()) {
                        case _32:
                            module = "Linux_x86"; // NOI18N
                            break;
                        case _64:
                            module = "Linux_x86_64"; // NOI18N
                            break;
                    }
                    break;
                case WINDOWS:
                    switch (hostInfo.getOS().getBitness()) {
                        case _32:
                            module = "Windows_x86"; // NOI18N
                            break;
                        case _64:
                            module = "Windows_x86_64"; // NOI18N
                            break;
                    }
                    break;
                case MACOSX:
                    switch (hostInfo.getOS().getBitness()) {
                        case _32:
                            module = "MacOSX_x86"; // NOI18N
                            break;
                        case _64:
                            module = "MacOSX_x86_64"; // NOI18N
                            break;
                    }
                    break;
            }
        }
        return module;
    }
}
