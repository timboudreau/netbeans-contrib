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
package com.sun.tools.ide.analysis.modernize.impl;

import com.sun.tools.ide.analysis.modernize.options.ClangAnalyzerOptions;
import com.sun.tools.ide.analysis.modernize.resources.BundleUtilities;
import java.io.CharConversionException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;
import org.netbeans.modules.analysis.spi.Analyzer;
import org.netbeans.modules.cnd.analysis.api.AbstractAnalyzer;
import org.netbeans.modules.cnd.analysis.api.AbstractHintsPanel;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.syntaxerr.CodeAudit;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorInfo;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorProvider;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.cnd.utils.CndPathUtilities;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.LazyFixList;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.xml.XMLUtil;

public class ModernizeAnalyzerImpl extends AbstractAnalyzer {

    private static final String PREFIX = "tidy-"; //NOI18N

    private ModernizeErrorProvider currentErrorProvider;

    private ModernizeAnalyzerImpl(Context ctx) {
        super(ctx);
    }

    @Override
    public ModernizeErrorProvider getErrorProvider(Preferences preferences) {
        return ModernizeErrorProvider.getInstance();
    }

    @Override
    protected boolean isCompileUnitBased() {
        return true;
    }

    @Override
    protected Collection<ErrorDescription> done() {
        if (currentErrorProvider != null) {
            Collection<ErrorDescription> results = currentErrorProvider.done();
            currentErrorProvider = null;
            return results;
        }
        return Collections.<ErrorDescription>emptyList();
    }

    @Override
    protected Collection<? extends ErrorDescription> doRunImpl(final FileObject sr, final Context ctx, final CsmErrorProvider provider, final AtomicBoolean cancel) {
        final CsmFile csmFile = CsmUtilities.getCsmFile(sr, false, false);
        if (csmFile == null) {
            return Collections.<ErrorDescription>emptyList();
        }
        CsmErrorProvider.Request request = new RequestImpl(csmFile, ctx, cancel);
        final ArrayList<ErrorDescription> res = new ArrayList<>();
        CsmErrorProvider.Response response = new ModernizeResponse(sr, res, cancel);
        this.currentErrorProvider = (ModernizeErrorProvider) provider;
        provider.getErrors(request, response);
        return res;
    }

    protected static class ModernizeResponse extends AbstractResponse {

        public ModernizeResponse(FileObject sr, ArrayList<ErrorDescription> res, AtomicBoolean cancel) {
            super(sr, res, cancel);
        }

        @Override
        public ErrorDescription addErrorImpl(CsmErrorInfo errorInfo, FileObject fo) {
            String messages[] = errorInfo.getMessage().split("\n"); //NOI18N
            if (messages.length > 0) {
                StringBuilder sb = new StringBuilder();
                if (errorInfo instanceof ModernizeErrorInfo) {
                    ModernizeErrorInfo mei = (ModernizeErrorInfo) errorInfo;
                    sb.append("<pre>"); //NOI18N
                    sb.append("["); //NOI18N
                    sb.append(mei.getId());
                    sb.append("]"); //NOI18N
                    sb.append("\n"); //NOI18N
                    List<YamlParser.Replacement> replacements = mei.getDiagnostics().getReplacements();
                    if (replacements.isEmpty()) {
                        sb.append(CndPathUtilities.getRelativePath(mei.getProject().getProjectRoot(), mei.getDiagnostics().getMessageFilePath())).append(" "); //NOI18N
                        sb.append(mei.getDiagnostics().getMessageFileOffset()).append("\n");
                    } else {
                        for (YamlParser.Replacement replacement : replacements) {
                            sb.append("\n"); //NOI18N
                            sb.append("Replacement: "); //NOI18N
                            sb.append(CndPathUtilities.getRelativePath(mei.getProject().getProjectRoot(), replacement.filePath)).append(" "); //NOI18N
                            sb.append(replacement.offset).append("-").append(replacement.offset + replacement.length).append("\n"); //NOI18N
                            String attributeValue = replacement.replacementText;
                            try {
                                attributeValue = XMLUtil.toAttributeValue(replacement.replacementText);
                            } catch (CharConversionException ex) {
                            }
                            sb.append(attributeValue).append("\n"); //NOI18N
                        }
                    }
                    sb.append(mei.getMessage());
                    sb.append("</pre>"); //NOI18N

                    LazyFixList list = new LazyFixListImpl();

                    return ErrorDescriptionFactory.createErrorDescription(PREFIX + mei.getId(), Severity.valueOf(mei.getSeverity().toString().toUpperCase()),
                            messages[0], sb.toString(), list, fo, errorInfo.getStartOffset(), errorInfo.getStartOffset());
                }
            }
            return null;
        }
    }

    @ServiceProvider(service = AnalyzerFactory.class)
    public static final class AnalyzerFactoryImpl extends AnalyzerFactory {

        public AnalyzerFactoryImpl() {
            super(ModernizeErrorProvider.NAME,
                    NbBundle.getMessage(ModernizeErrorProvider.class, "Modernize_NAME"), //NOI18N
                    ImageUtilities.loadImage("com/sun/tools/ide/analysis/modernize/impl/bugs.png")); //NOI18N
        }

        @Override
        public Iterable<? extends WarningDescription> getWarnings() {
            List<WarningDescription> result = new ArrayList<>();
            final ModernizeErrorProvider provider = ModernizeErrorProvider.getInstance();
            for (CodeAudit audit : provider.getAudits()) {
                result.add(WarningDescription.create(PREFIX + audit.getID(), audit.getName(), ModernizeErrorProvider.NAME, provider.getDisplayName()));
            }
            String[] fatals = BundleUtilities.getFatalErrors();
            for (String id : fatals) { //NOI18N
                if (!id.isEmpty()) {
                    result.add(WarningDescription.create(PREFIX + id, BundleUtilities.getDescription(id), ModernizeErrorProvider.NAME, provider.getDisplayName()));
                }
            }
            return result;
        }

        @Override
        public CustomizerProvider<Void, AbstractHintsPanel> getCustomizerProvider() {
            return new CustomizerProvider<Void, AbstractHintsPanel>() {

                @Override
                public Void initialize() {
                    return null;
                }

                @Override
                public AbstractHintsPanel createComponent(CustomizerContext<Void, AbstractHintsPanel> context) {
                    return AbstractAnalyzer.createComponent(ModernizeErrorProvider.getInstance());
                }
            };
        }

        @Override
        public Collection<? extends MissingPlugin> requiredPlugins(Context context) {
//            ExecutionEnvironment env = detectEnvironment(context);
//            if (env == null) {
//                env = ExecutionEnvironmentFactory.getLocal();
//            }
            String installedTool = ClangAnalyzerOptions.getClangAnalyzerPath();
            if (installedTool == null || !new File(installedTool).exists()) {
                String codeBase = ClangAnalyzerOptions.getMissingModuleName();
                if (codeBase != null) {
                    return Arrays.asList(new MissingPlugin(codeBase, NbBundle.getMessage(ModernizeAnalyzerImpl.class, "Modernize_NAME"))); //NOI18N
                }
            }
            return Collections.emptyList();
        }

        @Override
        public Analyzer createAnalyzer(Context context) {
            return new ModernizeAnalyzerImpl(context);
        }
    }
}
