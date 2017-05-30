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
package org.netbeans.modules.ide.analysis.modernize.impl;

import org.netbeans.modules.ide.analysis.modernize.utils.AnalyticsTools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.syntaxerr.AuditPreferences;
import org.netbeans.modules.cnd.api.model.syntaxerr.CodeAudit;
import org.netbeans.modules.cnd.api.toolchain.AbstractCompiler;
import static org.netbeans.modules.cnd.api.toolchain.PredefinedToolKind.CCCompiler;
import static org.netbeans.modules.cnd.api.toolchain.PredefinedToolKind.CCompiler;
import org.netbeans.modules.cnd.api.toolchain.Tool;
import org.netbeans.modules.cnd.makeproject.api.MakeProject;
import org.netbeans.modules.cnd.makeproject.api.configurations.CCCCompilerConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.CCCompilerConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.CCompilerConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfiguration;
import org.netbeans.modules.cnd.makeproject.api.support.MakeProjectOptionsFormat;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.HostInfo;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.netbeans.modules.nativeexecution.api.util.HostInfoUtils;
import org.netbeans.modules.nativeexecution.api.util.ProcessUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Ilia Gromov
 */
public class DiagnosticsTool {

    public static final Logger LOG = Logger.getLogger("ide.analysis.tidy"); //NOI18N
    public static final String CACHE_ID = "TIDY-CACHE"; //NOI18N

    public static final int STATUS_OK = 0;
    public static final int STATUS_IO_ERROR = -1;

    public static List<CodeAudit> getAudits(String path, ExecutionEnvironment env, AuditPreferences preferences) {
        List<CodeAudit> res = new ArrayList<>();

        ProcessUtils.ExitStatus status = ProcessUtils.execute(env, path, new String[]{"-checks=*", "-list-checks", "dummy", "--"}); // NOI18N

        if (status.exitCode == 0) {
            String out = status.getOutputString();
            out = out.substring(status.getOutputString().indexOf('\n') + 1);
            String[] checks = out.split("\n"); // NOI18N

            FileObject disabledChecksFolder = FileUtil.getConfigFile("Analysis/Clang-Tidy/Disabled_Default"); // NOI18N

            List<String> disabledChecks = new ArrayList<>();
            for (FileObject fileObject : disabledChecksFolder.getChildren()) {
                disabledChecks.add(fileObject.getName());
            }

            for (String check : checks) {
                check = check.trim();
                ModernizeCodeAudit myCodeAudit = new ModernizeCodeAudit(
                        check,
                        check,
                        check,
                        "warning", //NOI18N
                        !disabledChecks.contains(check),
                        preferences);
                res.add(myCodeAudit);
            }
        }

        return res;
    }

    private final ExecutionEnvironment execEnv;
    private final Item item;
    private final MakeProject project;
    private final String binaryPath;

    private StringBuilder buf;

    public DiagnosticsTool(ExecutionEnvironment execEnv, Item item, MakeProject project, String binaryPath) {
        this.execEnv = execEnv;
        this.item = item;
        this.project = project;
        this.binaryPath = binaryPath;
    }

    /**
     * @return clang-tidy's exit code or negative code if some other problem has
     * occurred.
     */
    public int process(Collection<String> checks, CsmFile csmFile, boolean isStartFile) throws ConnectionManager.CancellationException, IOException {
        // TODO: can we split analyzer (Source -> Inspect) ant editor error providing?

        final String directoryMacro = "xDIRx"; //NOI18N

        File tmpDir = null;
        try {
            List<String> args = new ArrayList<String>();

            HostInfo hostInfo = HostInfoUtils.getHostInfo(execEnv);

            try {
                if (execEnv.isRemote()) {
                    tmpDir = Files.createTempDirectory(hostInfo.getTempDirFile().toPath(), "tidy").toFile();  //NOI18N
                } else {
                    tmpDir = Files.createTempDirectory("tidy").toFile();  //NOI18N
                }
            } catch (IOException ex) {
                return -1;
            }

            Path tmpFile = Files.createTempFile(tmpDir.toPath(), null, null);

            args.add("-header-filter=.*"); //NOI18N
            StringBuilder sb = new StringBuilder("-checks=");// NOI18N
            for (String check : checks) {
                sb.append(check);
                sb.append(","); // NOI18N
            }
            args.add(sb.toString().substring(0, sb.length() - 1));

            args.add("-export-fixes=" + tmpFile); // NOI18N
            int directoryIdx = args.size() - 1;

            Collection<? extends CsmFile> startFiles = Collections.EMPTY_LIST;
            args.add(csmFile.getFileObject().getPath());

            args.add("--");  //NOI18N
            args.addAll(getAdditionalFlags(csmFile));

            List<String> copy = new ArrayList<String>(args);

            copy.set(directoryIdx, copy.get(directoryIdx).replace(directoryMacro, tmpDir.getAbsolutePath()));

            // TODO: execute in the ${WORKING_DIR} ? Because we can have relative path includes (-Isrc/libs/abcd)
            ProcessUtils.ExitStatus executeStatus = ProcessUtils.executeInDir(tmpDir.getAbsolutePath(),
                    execEnv,
                    binaryPath,
                    copy.toArray(new String[copy.size()]));

            if (executeStatus.exitCode != STATUS_OK) {
                LOG.log(Level.INFO, "clang-tidy exit code {0}: {1}", new Object[]{executeStatus.exitCode, arrayAsString(binaryPath, copy)});
                LOG.fine(executeStatus.getOutputString());
                LOG.fine(executeStatus.getErrorString());
                return executeStatus.exitCode;
            } else {
                LOG.log(Level.FINEST, "{0}", arrayAsString(binaryPath, copy));
                LOG.finest(executeStatus.getOutputString());
                LOG.finest(executeStatus.getErrorString());
            }

            File[] listFiles = tmpDir.listFiles();

            if (listFiles.length != 1) {
                return STATUS_IO_ERROR;
            }

            File yamlFile = listFiles[0];

            BufferedReader in = new BufferedReader(new FileReader(yamlFile));
            buf = new StringBuilder();
            while (true) {
                String s = in.readLine();
                if (s == null) {
                    break;
                }
                buf.append(s).append('\n'); //NOI18N
            }

        } finally {
            try {
                tmpDir.delete();
            } catch (Exception x) {

            }
        }

        return STATUS_OK;
    }

    private List<String> getAdditionalFlags(CsmFile csmFile) {
        List<String> all = new ArrayList<String>();

        Tool tool = AnalyticsTools.compiler(item, project);

        if (tool instanceof AbstractCompiler) {
            AbstractCompiler compiler = (AbstractCompiler) tool;

            for (String systemIncludeDirectory : compiler.getSystemIncludeDirectories()) {
                all.add("-I" + systemIncludeDirectory); //NOI18N
            }

            MakeConfiguration activeConfiguration = AnalyticsTools.getConfigurationDescriptor(csmFile, project).getActiveConfiguration();

            String options = "";
//            // compileSingleUnmanage as reference
//            // don't use getAllOptions - we need not -w flag
            if (tool.getKind() == CCCompiler) {
                CCCompilerConfiguration ccc = activeConfiguration.getCCCompilerConfiguration();
                options += ccc.getPreprocessorOptions(compiler.getCompilerSet());
                options += ccc.getIncludeDirectoriesOptions(compiler.getCompilerSet());
//            options += getLibrariesFlags();
                options += compiler.getCppStandardOptions(ccc.getInheritedCppStandard());
                if (!options.contains("-std=")) { //NOI18N
                    switch (item.getLanguageFlavor()) {
                        case CPP98:
                            options += " -std=c++98"; //NOI18N
                            break;
                        case CPP11:
                            options += " -std=c++11"; //NOI18N
                            break;
                        case CPP14:
                            options += " -std=c++14"; //NOI18N
                            break;
                    }
                }
            } else if (tool.getKind() == CCompiler) {
                CCompilerConfiguration cc = activeConfiguration.getCCompilerConfiguration();
                options += cc.getPreprocessorOptions(compiler.getCompilerSet());
                options += cc.getIncludeDirectoriesOptions(compiler.getCompilerSet());
                options += compiler.getCStandardOptions(cc.getInheritedCStandard());
                if (!options.contains("-std=")) { //NOI18N
                    switch (item.getLanguageFlavor()) {
                        case C11:
                            options += " -std=c11"; //NOI18N
                            break;
                        case C89:
                            options += " -std=c89"; //NOI18N
                            break;
                        case C99:
                            options += " -std=c99"; //NOI18N
                            break;
                    }
                }
            }

            List<String> optionsList = AnalyticsTools.scanCommandLine(MakeProjectOptionsFormat.reformatWhitespaces(options));
            all.addAll(optionsList);
        }

        return all;
    }

    public String getYamlAsString() {
        if (buf == null) {
            throw new IllegalStateException();
        }
        return buf.toString();
    }

    private static String arrayAsString(String binary, List<String> list) {
        StringBuilder sb = new StringBuilder(binary).append(" "); //NOI18N
        for (String string : list) {
            sb.append(string).append(" "); //NOI18N
        }
        return sb.toString();
    }
}
