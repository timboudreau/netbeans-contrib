/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package ide.analysis.modernize;

import com.sun.tools.ide.analysis.modernize.impl.DiagnosticsTool;
import com.sun.tools.ide.analysis.modernize.impl.ModernizeAnalyzerImpl;
import com.sun.tools.ide.analysis.modernize.impl.ModernizeFix;
import com.sun.tools.ide.analysis.modernize.impl.YamlParser;
import com.sun.tools.ide.analysis.modernize.utils.AnalyticsTools;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.prefs.Preferences;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.analysis.SPIAccessor;
import org.netbeans.modules.analysis.spi.Analyzer;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmModel;
import org.netbeans.modules.cnd.api.model.CsmModelAccessor;
import org.netbeans.modules.cnd.api.model.CsmProject;
import org.netbeans.modules.cnd.api.model.syntaxerr.AuditPreferences;
import org.netbeans.modules.cnd.api.model.syntaxerr.CodeAudit;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorProvider;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.netbeans.modules.cnd.api.project.NativeProjectRegistry;
import org.netbeans.modules.cnd.api.toolchain.CompilerSet;
import org.netbeans.modules.cnd.api.toolchain.CompilerSetManager;
import org.netbeans.modules.cnd.makeproject.api.MakeProject;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.cnd.test.CndBaseTestCase;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironmentFactory;
import org.netbeans.modules.refactoring.api.Scope;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Ilia Gromov
 */
public abstract class TidyTestCase extends CndBaseTestCase {

    private final String projectName;

    private List<FileObject> fileObjects;
    private MakeProject project;
    private FileObject projectDirFileObject;

    private final String CLANG_TIDY_PATH = "clang-tidy-3.9p";

    public TidyTestCase(String projectName) {
        super(projectName);

        this.projectName = projectName;
    }

    protected static CodeAudit findAudit(Collection<CodeAudit> audits, String id) {
        for (CodeAudit audit : audits) {
            if (audit.getID().equals(id)) {
                return audit;
            }
        }
        return null;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LogManager.getLogManager().reset();

        java.util.logging.Logger LOG = java.util.logging.Logger.getLogger("ide.analysis.tidy");
        LOG.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        LOG.addHandler(consoleHandler);

        File clangTidyFile = getBinaryFile();
        Files.setPosixFilePermissions(clangTidyFile.toPath(), PosixFilePermissions.fromString("rwxrwxrwx"));

        assertTrue(getBinaryFile().exists());

        File projectDirFile = getDataFile(projectName);
        projectDirFileObject = FileUtil.toFileObject(projectDirFile);

        fileObjects = new ArrayList<FileObject>();

        project = (MakeProject) ProjectManager.getDefault().findProject(projectDirFileObject);

        OpenProjects.getDefault().open(new Project[]{project}, false);
        while (!OpenProjects.getDefault().isProjectOpen(project)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        CsmModel model = CsmModelAccessor.getModel();
        assertNotNull(project);

        NativeProject np = project.getLookup().lookup(NativeProject.class);
        NativeProjectRegistry.getDefault().register(np);
        model.enableProject(np);
        assertNotNull(np);

        CsmProject csmProject = model.getProject(np);
        assertNotNull(csmProject);

        csmProject.waitParse();

    }

    public ExecutionEnvironment getExecEnv() {
        ExecutionEnvironment execEnv = ExecutionEnvironmentFactory.getLocal();
        return execEnv;
    }

    public CompilerSet getCompilerSet() {
        CompilerSetManager csm = CompilerSetManager.get(getExecEnv());
        return csm.getDefaultCompilerSet();
    }

    public MakeProject getProject() {
        return project;
    }

    public FileObject getProjectDir() {
        return projectDirFileObject;
    }

    public FileObject getSourceFile(String name) {
        return FileUtil.toFileObject(new File(FileUtil.toFile(getProjectDir()), name));
    }

    public File getBinaryFile() {
        return new File(getDataDir(), CLANG_TIDY_PATH);
    }

    protected final void performTest(String startFileName, String checkName, boolean isStartFile) {
        try {
            FileObject startFile = getSourceFile(startFileName);
            assertTrue(startFile.isValid());

            Scope scope = Scope.create(null, null, Arrays.asList(new FileObject[]{startFile}));
            Analyzer.Context context = SPIAccessor.ACCESSOR.createContext(scope, null, null, ProgressHandle.createHandle("dummy"), 0, 0);
            Analyzer.AnalyzerFactory factory = new ModernizeAnalyzerImpl.AnalyzerFactoryImpl();

            ModernizeAnalyzerImpl analyzer = (ModernizeAnalyzerImpl) factory.createAnalyzer(context);
            ExecutionEnvironment execEnv = ExecutionEnvironmentFactory.getLocal();
            final CsmFile csmFile = CsmUtilities.getCsmFile(startFile, true, false);
            Item item = AnalyticsTools.findItem(csmFile, getProject());

            Collection<CodeAudit> audits = DiagnosticsTool.getAudits(getBinaryFile().getAbsolutePath(), execEnv, new AuditPreferences(Preferences.userNodeForPackage(this.getClass())));
            CodeAudit audit = findAudit(audits, checkName);
            assertNotNull(audit);

            // ----- BEGIN diagnostics -----
            DiagnosticsTool diagnosticsTool = new DiagnosticsTool(execEnv, item, getProject(), getBinaryFile().getAbsolutePath());

            int exitCode = diagnosticsTool.process(Collections.singleton(audit.getID()), csmFile, true);
            if (exitCode != DiagnosticsTool.STATUS_OK) {
                fail();
            }

            List<YamlParser.Diagnostics> diags = YamlParser.getDefault().parseYaml(diagnosticsTool.getYamlAsString());
            List<ModernizeFix> fixes = new ArrayList<ModernizeFix>();

            CsmErrorProvider.Request request = new RequestImpl(csmFile);
            CsmErrorProvider.Response response = new ResponseImpl(fixes);

            analyzer.getErrorProvider(null).postProcess(false, csmFile, getProject(), diags, request, response);
            // ----- END diagnostics -----

            processTestResults(fixes, startFileName);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            fail(ex.toString());
        }
    }

    protected void processTestResults(List<ModernizeFix> fixes, String startFileName) throws Exception {

    }
}
