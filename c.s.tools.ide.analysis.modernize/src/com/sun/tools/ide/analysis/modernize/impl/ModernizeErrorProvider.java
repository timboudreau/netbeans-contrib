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

import com.sun.tools.ide.analysis.modernize.impl.ModernizeAnalyzerImpl.ResponseImpl;
import com.sun.tools.ide.analysis.modernize.impl.YamlParser.Replacement;
import com.sun.tools.ide.analysis.modernize.options.AnalyzerPreferences;
import com.sun.tools.ide.analysis.modernize.options.ClangAnalyzerOptions;
import static com.sun.tools.ide.analysis.modernize.utils.AnalyticsTools.fatalError;
import static com.sun.tools.ide.analysis.modernize.utils.AnalyticsTools.findItem;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.netbeans.modules.cnd.analysis.api.AbstractCustomizerProvider;
import org.netbeans.modules.cnd.analysis.api.AnalyzerResponse;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.syntaxerr.AbstractCodeAudit;
import org.netbeans.modules.cnd.api.model.syntaxerr.AuditPreferences;
import org.netbeans.modules.cnd.api.model.syntaxerr.CodeAudit;
import org.netbeans.modules.cnd.api.model.syntaxerr.CodeAuditFactory;
import org.netbeans.modules.cnd.api.model.syntaxerr.CodeAuditProvider;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorInfo;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorInfo.Severity;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorInfoHintProvider;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorProvider;
import org.netbeans.modules.cnd.api.project.NativeFileItem.Language;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.netbeans.modules.cnd.api.remote.RemoteProject;
import org.netbeans.modules.cnd.makeproject.api.MakeProject;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.cnd.utils.MIMENames;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironmentFactory;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders({
    @ServiceProvider(service = CsmErrorProvider.class, position = 2100)
    ,
    @ServiceProvider(service = CodeAuditProvider.class, position = 2100)
})
public final class ModernizeErrorProvider extends CsmErrorProvider implements CodeAuditProvider, AbstractCustomizerProvider {

    public static final Logger LOG = Logger.getLogger("ide.analysis.tidy"); //NOI18N
    private Collection<CodeAudit> audits;
    public static final String NAME = "Modernize"; //NOI18N

    public static ModernizeErrorProvider getInstance() {
        for (CsmErrorProvider provider : Lookup.getDefault().lookupAll(CsmErrorProvider.class)) {
            if (NAME.equals(provider.getName()) && provider instanceof ModernizeErrorProvider) {
                return (ModernizeErrorProvider) provider;
            }
        }
        return null;
    }

    @Override
    protected boolean validate(Request request) {
        CsmFile file = request.getFile();
        return file != null;
    }

    @Override
    public boolean hasHintControlPanel() {
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(ModernizeErrorProvider.class, "Modernize_NAME"); //NOI18N
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ModernizeErrorProvider.class, "Modernize_DESCRIPTION"); //NOI18N
    }

    @Override
    public String getMimeType() {
        return MIMENames.SOURCES_MIME_TYPE;
    }

    @Override
    public boolean isSupportedEvent(EditorEvent kind) {
        return kind == EditorEvent.FileBased;
    }

    @Override
    protected void doGetErrors(CsmErrorProvider.Request request, CsmErrorProvider.Response response) {
        CsmFile file = request.getFile();
        if (file != null) {
            if (request.isCancelled()) {
                return;
            }
            Object platformProject = file.getProject().getPlatformProject();
            if (platformProject instanceof NativeProject) {
                Lookup.Provider project = ((NativeProject) platformProject).getProject();
                if (project != null) {
                    if (request.isCancelled()) {
                        return;
                    }
                    Thread currentThread = Thread.currentThread();
                    currentThread.setName("Provider " + getName() + " prosess " + file.getAbsolutePath()); // NOI18N
                    RemoteProject info = project.getLookup().lookup(RemoteProject.class);
                    if (info != null) {
                        ExecutionEnvironment execEnv = info.getDevelopmentHost();
                        if (execEnv != null) {
                            if (request.isCancelled()) {
                                return;
                            }
                            if (ConnectionManager.getInstance().isConnectedTo(execEnv)) {
                                Item item = findItem(file, project);
                                if (item != null) {
                                    if (request.isCancelled()) {
                                        return;
                                    }
                                    // Temporarily analyzing even excluded items
                                    if (/* !item.isExcluded() &&  */(item.getLanguage() == Language.C || item.getLanguage() == Language.CPP || item.getLanguage() == Language.C_HEADER)) {
                                        analyze(execEnv, item, project, request, response);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void analyze(ExecutionEnvironment execEnv, Item item, Lookup.Provider project, CsmErrorProvider.Request request, CsmErrorProvider.Response response) {
        String binaryPath = ClangAnalyzerOptions.getClangAnalyzerPath();
        boolean isAnalyzer = response instanceof ModernizeAnalyzerImpl.ResponseImpl;
        if (binaryPath == null) {
            Level level = isAnalyzer ? Level.INFO : Level.FINE;
            LOG.log(level, "clang-tidy needs to be installed as a plugin"); //NOI18N
            return;
        }

        if (isAnalyzer && isNewRun()) {
            AnalyzedFiles.getDefault().clear();
        }

        DiagnosticsTool diagnosticsTool = new DiagnosticsTool(execEnv, item, (MakeProject) project, binaryPath);
        try {
            CsmFile csmFile = request.getFile();
            Collection<String> checks = /*isAnalyzer ? Collections.singleton("*") : */ getEnabledChecks(); //NOI18N

            Collection<CsmFile> tu = new ArrayList<CsmFile>();
            if (isAnalyzer) {
                tu.add(csmFile);
            } else {
                if (AnalyzedFiles.getDefault().isStartFile(csmFile)) {
                    tu.add(csmFile);
                } else {
                    tu.addAll(AnalyzedFiles.getDefault().getStartFiles(csmFile));
                }
            }

            if (!isAnalyzer) {
                response = new ResponseMerger(response);
            }

            for (CsmFile startFile : tu) {
                int exitCode = diagnosticsTool.process(checks, startFile, true);
                if (exitCode != DiagnosticsTool.STATUS_OK) {
                    String error = NbBundle.getMessage(ModernizeErrorProvider.class, "compile.file.error"); //NOI18N
                    String info = NbBundle.getMessage(ModernizeErrorProvider.class, "compile.file.error.info", "" + exitCode); //NOI18N
                    fatalError(AnalyzerResponse.AnalyzerSeverity.FileError, "fatal.analyze.error", error + "\n" + info, csmFile, response); //NOI18N
                    return;
                }
                List<YamlParser.Diagnostics> results = YamlParser.getDefault().parseYaml(diagnosticsTool.getYamlAsString());
                postProcess(isAnalyzer, startFile, project, results, request, response);
            }

            if (!isAnalyzer) {
                response.done();
            }

        } catch (ConnectionManager.CancellationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static CsmErrorProvider last;

    private boolean isNewRun() {
        if (last == null || this != last) {
            last = this;
            return true;
        }
        return false;
    }

    public void postProcess(boolean isAnalyzer, CsmFile startFile, Lookup.Provider project, List<YamlParser.Diagnostics> results, CsmErrorProvider.Request request, CsmErrorProvider.Response response) {
        CsmFile file = request.getFile();
        List<CsmFile> otherCsmFiles = new ArrayList<CsmFile>();

        for (YamlParser.Diagnostics diag : results) {
            // TODO: don't add "Configure Hint" fix multiple times for one line
            FileObject fo = FileUtil.toFileObject(new File(diag.getMessageFilePath()));
            CsmFile csmFile = CsmUtilities.getCsmFile(fo, false, false);

            // Composing a preview message. Showing a start file for compilation unit
            // in case we analysing a header file
            ModernizeErrorInfo info;
            if (startFile.equals(file) && csmFile.equals(file)) {
                String message = String.format("[%s]: %s", diag.getCheckName(), diag.getMessage()); //NOI18N
                info = ModernizeErrorInfo.withFixedMessage(diag, message, project);
            } else {
                info = ModernizeErrorInfo.withMutableMessage(diag, diag.getCheckName(), startFile.getName().toString(), diag.getMessage(), project);
            }

            if (isAnalyzer) {
                // Add found errors for all files (can be other files from compileUnit)
                ((ResponseImpl) response).addError(AnalyzerResponse.AnalyzerSeverity.DetectedError, null, fo, info);

                if (!csmFile.equals(file)) {
                    // May be not header (e.g BBB.cc: AAA.cc -> (includes) BBB.cc -> ... )
                    otherCsmFiles.add(csmFile);
                }
            } else if (fo.equals(file.getFileObject())) {
                // Add found errors only for file displayed in Editor
                response.addError(info);
            }
        }

        if (isAnalyzer /* and not empty? */) {
            AnalyzedFiles.getDefault().cacheHierarchy(file, otherCsmFiles);
        }
    }

    @ServiceProvider(path = CodeAuditFactory.REGISTRATION_PATH + ModernizeErrorProvider.NAME, service = CodeAuditFactory.class, position = 4000)
    public static final class Factory implements CodeAuditFactory {

        @Override
        public AbstractCodeAudit create(AuditPreferences preferences) {
            String id = NbBundle.getMessage(ModernizeCodeAudit.class, "LBL_ProviderName");  // NOI18N
            String description = NbBundle.getMessage(ModernizeCodeAudit.class, "LBL_ProviderDescription");  // NOI18N
            return new ModernizeCodeAudit(id, id, description, "error", false, preferences);  // NOI18N
        }
    }

    private String oldPath;

    @Override
    public synchronized Collection<CodeAudit> getAudits() {
        String path = ClangAnalyzerOptions.getClangAnalyzerPath();

        if (path == null) {
            return Collections.emptyList();
        }

        if (oldPath == null) {
            oldPath = path;
        }

        if (audits == null || !oldPath.equals(path)) {
            List<CodeAudit> res = DiagnosticsTool.getAudits(path, ExecutionEnvironmentFactory.getLocal(), AnalyzerPreferences.getAuditPreferences());

            audits = res;
            oldPath = path;
        }
        return audits;
    }

    public Collection<String> getEnabledChecks() {
        Collection<CodeAudit> auditList = getAudits();
        List<String> enabled = new ArrayList<String>();
        for (CodeAudit codeAudit : auditList) {
            if (codeAudit.isEnabled()) {
                enabled.add(codeAudit.getID());
            }
        }
        return enabled.size() == auditList.size() ? Collections.singleton("*") : enabled; //NOI18N
    }

    @Override
    public AuditPreferences getPreferences() {
        return AnalyzerPreferences.getAuditPreferences();
    }

    @Override
    public JComponent createComponent(Preferences context) {
        return new JLabel();
    }

    public static interface ErrorInfoWithId {

        String getId();
    }

    public static final class FatalErrorInfo implements CsmErrorInfo, ErrorInfoWithId {

        private final String id;
        private final String message;

        public FatalErrorInfo(String id, String message) {
            this.id = id;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public Severity getSeverity() {
            return Severity.WARNING;
        }

        @Override
        public int getStartOffset() {
            return 0;
        }

        @Override
        public int getEndOffset() {
            return 1;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    @ServiceProvider(service = CsmErrorInfoHintProvider.class, position = 9100)
    public final static class ModerinzeHintProvider extends CsmErrorInfoHintProvider {

        @Override
        protected List<Fix> doGetFixes(CsmErrorInfo info, List<Fix> alreadyFound) {
            if (info instanceof ModernizeErrorInfo) {
                alreadyFound.add(new ConfigureHintsFix((ModernizeErrorInfo) info));
            }
            return alreadyFound;
        }
    }

    @ServiceProvider(service = CsmErrorInfoHintProvider.class, position = 1600)
    public static final class ModernizeFixProvider extends CsmErrorInfoHintProvider {

        @Override
        protected List<Fix> doGetFixes(CsmErrorInfo info, List<Fix> alreadyFound) {
            alreadyFound.addAll(createFixes(info));
            return alreadyFound;
        }
    }

    private static List<? extends Fix> createFixes(CsmErrorInfo info) {
        if (info instanceof ModernizeErrorInfo) {
            ModernizeErrorInfo mei = (ModernizeErrorInfo) info;
            List<Replacement> replacements = mei.getDiagnostics().getReplacements();
            if (!replacements.isEmpty()) {
                return Collections.singletonList(new ModernizeFix(replacements, mei.getId()));
            }
        }
        return Collections.EMPTY_LIST;
    }
}
