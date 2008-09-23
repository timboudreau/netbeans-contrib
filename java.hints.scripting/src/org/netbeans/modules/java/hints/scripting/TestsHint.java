/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2008 Sun
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
package org.netbeans.modules.java.hints.scripting;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author Jan Lahoda
 */
public class TestsHint extends AbstractHint {

    public TestsHint() {
        super(true, false, AbstractHint.HintSeverity.WARNING);
    }

    @Override
    public String getDescription() {
        return "TestsHint";
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.of(Kind.COMPILATION_UNIT);
    }

    public List<ErrorDescription> run(CompilationInfo info, TreePath tp) {
        List<Fix> fixes = new LinkedList<Fix>();
        if (Utilities.getFolder().equals(info.getFileObject().getParent())) {
            fixes.add(new AddTest(info.getFileObject()));
            fixes.add(new RunTests(info.getFileObject()));
        }
        
        if (Utilities.getFolder().equals(info.getFileObject().getParent().getParent())) {
            fixes.add(new RecordCurrentResults(info.getFileObject()));
        }

        if (!fixes.isEmpty()) {
            return Arrays.asList(ErrorDescriptionFactory.createErrorDescription(Severity.HINT, "Tests", fixes, info.getFileObject(), 0, 0));
        } else {
            return null;
        }
    }

    public String getId() {
        return TestsHint.class.getName();
    }

    public String getDisplayName() {
        return "TestsHint";
    }

    public void cancel() {
    }
    
    private static Map<FileObject, List<? extends ErrorDescription>> computeResults(FileObject hint, Collection<FileObject> tests) throws IOException {
        if (tests.isEmpty()) {
            return Collections.emptyMap();
        }
        
        final HintsTask t = new HintsTask(UberHint.INSTANCE.hints);
        final Map<FileObject, List<? extends ErrorDescription>> result = new  HashMap<FileObject, List<? extends ErrorDescription>>();
        
        JavaSource.create(ClasspathInfo.create(tests.iterator().next()), tests).runUserActionTask(new  Task<CompilationController>() {
            public void run(CompilationController cc) throws Exception {
                if (Phase.RESOLVED.compareTo(cc.toPhase(Phase.RESOLVED)) > 0) {
                    return;
                }
                
                result.put(cc.getFileObject(), t.compute(cc));
            }
        }, true);
        
        return result;
    }
    
    private static List<FileObject> filterJava(FileObject... files) {
        List<FileObject> r = new  LinkedList<FileObject>();
        
        for (FileObject f : files) {
            if ("text/x-java".equals(FileUtil.getMIMEType(f))) {
                r.add(f);
            }
        }
        
        return r;
    }

    private static final class AddTest implements Fix {

        private FileObject forFile;

        public AddTest(FileObject forFile) {
            this.forFile = forFile;
        }
        
        public String getText() {
            return "Add Test";
        }

        public ChangeInfo implement() throws Exception {
            FileObject folder = FileUtil.createFolder(forFile.getParent(), forFile.getName());
            
            if (folder == null) {
                return null;
            }
            
            Set<String> names = new  HashSet<String>();
            
            for (FileObject f : folder.getChildren()) {
                names.add(f.getName());
            }
            
            int index = 1;
            
            while (names.contains("Test" + index))
                index++;
            
            FileObject file = FileUtil.createData(folder, "Test" + index + ".java");
            
            return new ChangeInfo(file, null, null);
        }
        
    }
    
    private static final class RecordCurrentResults implements Fix {

        private FileObject forFile;

        public RecordCurrentResults(FileObject forFile) {
            this.forFile = forFile;
        }
        
        public String getText() {
            return "Record Current Results";
        }

        public ChangeInfo implement() throws Exception {
            String hintName = forFile.getParent().getName();
            FileObject hint = forFile.getParent().getParent().getFileObject(hintName + ".java");
            
            if (hint == null) {
                return null;
            }

            Map<FileObject, List<? extends ErrorDescription>> computed = computeResults(hint, Collections.singleton(forFile));
            List<? extends ErrorDescription> eds;
            
            if (computed.get(forFile) == null) {
                eds = Collections.emptyList();
            } else {
                eds = computed.get(forFile);
            }
            
            FileObject pass = FileUtil.createData(forFile.getParent(), forFile.getName() + ".pass");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(pass.getOutputStream(), "UTF-8"));
            
            for (ErrorDescription ed : eds) {
                pw.println(ed.toString()); //XXX
            }
            
            pw.close(); //XXX
            
            return null;
        }
        
    }
    
    private static final class RunTests implements Fix {

        private FileObject forFile;

        public RunTests(FileObject forFile) {
            this.forFile = forFile;
        }
        
        public String getText() {
            return "Run Tests";
        }

        public ChangeInfo implement() throws Exception {
            String hintName = forFile.getName();
            FileObject testFolder = forFile.getParent().getFileObject(hintName);
            
            if (testFolder == null) {
                StatusDisplayer.getDefault().setStatusText("No tests");
                return null;
            }
            
            List<FileObject> tests = filterJava(testFolder.getChildren());
            
            if (tests.isEmpty()) {
                StatusDisplayer.getDefault().setStatusText("No tests");
                return null;
            }
            
            Map<FileObject, List<? extends ErrorDescription>> computed = computeResults(forFile, tests);
            
            int testsCount = 0;
            int failedCount = 0;
            FileObject firstFail = null;
            
            for (Entry<FileObject, List<? extends ErrorDescription>> e : computed.entrySet()) {
                FileObject pass = e.getKey().getParent().getFileObject(e.getKey().getName() + ".pass");
                String passText = Utilities.copyFileToString(pass);
                
                StringBuilder out = new StringBuilder();
                
                for (ErrorDescription ed : e.getValue()) {
                    out.append(ed.toString());
                    out.append("\n");
                }
                
                if (!passText.equals(out.toString())) {
                    failedCount++;
                    if (firstFail == null) {
                        firstFail = e.getKey();
                    }
                }
                
                testsCount++;
            }
            
            StringBuilder hlaska = new StringBuilder();
            
            hlaska.append(testsCount);
            hlaska.append(" test(s) passed");
            
            if (failedCount > 0) {
                hlaska.append(", ");
                hlaska.append(failedCount);
                hlaska.append(" test(s) failed.");
            } else {
                hlaska.append(".");
            }
            
            StatusDisplayer.getDefault().setStatusText(hlaska.toString());
            
            return new ChangeInfo(firstFail, null, null);
        }
        
    }
    
}
