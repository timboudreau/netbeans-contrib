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

import org.netbeans.modules.ide.analysis.modernize.impl.DiagnosticsTool;
import org.netbeans.modules.ide.analysis.modernize.impl.ModernizeAnalyzerImpl;
import org.netbeans.modules.ide.analysis.modernize.utils.AnalyticsTools;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.modules.analysis.SPIAccessor;
import org.netbeans.modules.analysis.spi.Analyzer;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.cnd.test.CndCoreTestUtils;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironmentFactory;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.netbeans.modules.refactoring.api.Scope;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Ilia Gromov
 */
public class YamlTest extends TidyTestCase {

    public YamlTest() {
        super("hello-world");
    }

    @Test
    public void testYaml() throws IOException, ConnectionManager.CancellationException {
        FileObject singleFileObject = getSourceFile("welcome.cc");

        File goldenFile = getGoldenFile("hello-world.json");
        String goldenYaml = new String(Files.readAllBytes(goldenFile.toPath()), "UTF-8").replaceAll("\\$path", getDataDir().getAbsolutePath());

        Scope scope = Scope.create(null, null, Arrays.asList(new FileObject[]{singleFileObject}));
        Analyzer.Context context = SPIAccessor.ACCESSOR.createContext(scope, null, null, ProgressHandle.createHandle("dummy"), 0, 0);

        Analyzer.AnalyzerFactory factory = new ModernizeAnalyzerImpl.AnalyzerFactoryImpl();
        ModernizeAnalyzerImpl analyzer = (ModernizeAnalyzerImpl) factory.createAnalyzer(context);

        ExecutionEnvironment execEnv = ExecutionEnvironmentFactory.getLocal();

        CsmFile csmFile = CsmUtilities.getCsmFile(singleFileObject, false, false);
        Item item = AnalyticsTools.findItem(csmFile, getProject());

        DiagnosticsTool diagnosticsTool = new DiagnosticsTool(execEnv, item, getProject(), getBinaryFile().getAbsolutePath());

        int exitCode = diagnosticsTool.process(Collections.singleton("*"), csmFile, true);

        if (exitCode != DiagnosticsTool.STATUS_OK) {
            fail();
        }
        String yamlAsString = diagnosticsTool.getYamlAsString();

        File real = File.createTempFile("test", null);
        Files.write(real.toPath(), yamlAsString.getBytes());

        File expected = File.createTempFile("test", null);
        Files.write(expected.toPath(), goldenYaml.getBytes());

        File diff = File.createTempFile("test", null);
        if (CndCoreTestUtils.diff(real, expected, diff)) {
            System.err.println("============DIFF============");
            System.err.println(new String(Files.readAllBytes(diff.toPath()), "UTF-8"));
            System.err.println("============DIFF============");
            fail();
        }
    }
}
