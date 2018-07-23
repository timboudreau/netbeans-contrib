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
package com.sun.tools.ide.analysis.modernize.utils;

import com.sun.tools.ide.analysis.modernize.impl.ModernizeErrorProvider;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.analysis.spi.Analyzer;
import org.netbeans.modules.cnd.analysis.api.AnalyzerResponse;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorInfo;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorProvider;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.api.remote.RemoteProject;
import org.netbeans.modules.cnd.api.toolchain.CompilerSet;
import org.netbeans.modules.cnd.api.toolchain.PredefinedToolKind;
import org.netbeans.modules.cnd.api.toolchain.Tool;
import org.netbeans.modules.cnd.makeproject.api.configurations.ConfigurationDescriptorProvider;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfigurationDescriptor;
import org.netbeans.modules.cnd.spi.toolchain.ToolchainProject;
import org.netbeans.modules.cnd.utils.MIMENames;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Ilia Gromov
 */
public class AnalyticsTools {

    public static ExecutionEnvironment detectEnvironment(Analyzer.Context context) {
        if (context.getScope() == null) {
            return null;
        }
        for (FileObject fo : context.getScope().getFiles()) {
            String mimeType = fo.getMIMEType();
            if (MIMENames.isHeaderOrCppOrC(mimeType)) {
                Project project = FileOwnerQuery.getOwner(fo);
                RemoteProject info = project.getLookup().lookup(RemoteProject.class);
                if (info != null) {
                    ExecutionEnvironment dh = info.getDevelopmentHost();
                    if (dh != null) {
                        return dh;
                    }
                }
            }
        }
        return null;
    }

    public static CompilerSet toolchain(Lookup.Provider project) {
        ToolchainProject toolchain = project.getLookup().lookup(ToolchainProject.class);
        if (toolchain != null) {
            return toolchain.getCompilerSet();
        }
        return null;
    }

    public static MakeConfigurationDescriptor getConfigurationDescriptor(CsmFile file, Lookup.Provider project) {
        if (file != null) {
            ConfigurationDescriptorProvider pdp = project.getLookup().lookup(ConfigurationDescriptorProvider.class);
            if (pdp != null) {
                MakeConfigurationDescriptor makeConfigurationDescriptor = pdp.getConfigurationDescriptor();
                if (makeConfigurationDescriptor != null) {
                    return makeConfigurationDescriptor;
                }
            }
        }

        return null;
    }

    public static Item findItem(CsmFile file, Lookup.Provider project) {
        return getConfigurationDescriptor(file, project).findProjectItemByPath(file.getAbsolutePath().toString());
    }

    public static void fatalError(AnalyzerResponse.AnalyzerSeverity severity, String id, String message, CsmFile file, CsmErrorProvider.Response response) {
        CsmErrorInfo fatal = new ModernizeErrorProvider.FatalErrorInfo(id, message);
        if (response instanceof AnalyzerResponse) {
            AnalyzerResponse ar = (AnalyzerResponse) response;
            ar.addError(severity, message, file.getFileObject(), fatal); //NOI18N
        } else {
            response.addError(fatal); //NOI18N
        }
    }

    public static String useTool(Item item, Lookup.Provider project) {
        ToolchainProject toolchain = project.getLookup().lookup(ToolchainProject.class);
        if (toolchain != null) {
            CompilerSet set = toolchain.getCompilerSet();
            if (set != null) {
                if (set.getCompilerFlavor().isSunStudioCompiler()) {
                    if (item.getLanguage() == NativeFileItem.Language.C) {
                        return set.findTool(PredefinedToolKind.CCompiler).getPath();
                    } else {
                        return set.findTool(PredefinedToolKind.CCCompiler).getPath();
                    }
                }
            }
        }
        return null;
    }

    public static Tool compiler(Item item, Lookup.Provider project) {
        CompilerSet set = toolchain(project);
        if (set != null) {
            if (item.getLanguage() == NativeFileItem.Language.C) {
                return set.findTool(PredefinedToolKind.CCompiler);
            } else {
                return set.findTool(PredefinedToolKind.CCCompiler);
            }
        }
        return null;
    }

    public static List<String> scanCommandLine(String line) {
        List<String> res = new ArrayList<String>();
        int i = 0;
        StringBuilder current = new StringBuilder();
        boolean isSingleQuoteMode = false;
        boolean isDoubleQuoteMode = false;
        while (i < line.length()) {
            char c = line.charAt(i);
            i++;
            switch (c) {
                case '\'': // NOI18N
                    if (isSingleQuoteMode) {
                        isSingleQuoteMode = false;
                    } else if (!isDoubleQuoteMode) {
                        isSingleQuoteMode = true;
                    }
                    current.append(c);
                    break;
                case '\"': // NOI18N
                    if (isDoubleQuoteMode) {
                        isDoubleQuoteMode = false;
                    } else if (!isSingleQuoteMode) {
                        isDoubleQuoteMode = true;
                    }
                    current.append(c);
                    break;
                case ' ': // NOI18N
                case '\t': // NOI18N
                case '\n': // NOI18N
                case '\r': // NOI18N
                    if (isSingleQuoteMode || isDoubleQuoteMode) {
                        current.append(c);
                        break;
                    } else {
                        if (current.length() > 0) {
                            res.add(current.toString());
                            current.setLength(0);
                        }
                    }
                    break;
                default:
                    current.append(c);
                    break;
            }
        }
        if (current.length() > 0) {
            res.add(current.toString());
        }
        return res;
    }

}
