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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.javahints;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class RunOnceFactoryTest extends NbTestCase {
    
    public RunOnceFactoryTest(String testName) {
        super(testName);
    }

//    @Override
//    protected Level logLevel() {
//        return Level.FINE;
//    }

    
    private FileObject file1;
    private FileObject file2;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        clearWorkDir();
        
        FileObject work = FileUtil.toFileObject(getWorkDir());
        FileObject userdir = FileUtil.createFolder(work, "userdir");
        
        System.setProperty("netbeans.user", FileUtil.toFile(userdir).getAbsolutePath());   //NOI18N

        file1 = FileUtil.createData(work, "src/Test.java");
        file2 = FileUtil.createData(work, "src/Test2.java");
        
        new RunOnceFactory();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    public void testTasksRun() throws Exception {
//        CountDownLatch task1 = new CountDownLatch(1);
//        CountDownLatch task2 = new CountDownLatch(1);
//        
//        RunOnceFactory.add(file1, new TaskImpl(task1));
//        RunOnceFactory.add(file2, new TaskImpl(task2));
//        
//        assertTrue(task1.await(10, TimeUnit.SECONDS));
//        assertTrue(task2.await(10, TimeUnit.SECONDS));
//    }
    
    public void testTasksRunSameFile() throws Exception {
        CountDownLatch task1 = new CountDownLatch(1);
        CountDownLatch task2 = new CountDownLatch(1);
        
        RunOnceFactory.add(file1, new TaskImpl(task1));
        RunOnceFactory.add(file1, new TaskImpl(task2));
        
        assertTrue(task1.await(10, TimeUnit.SECONDS));
        assertTrue(task2.await(10, TimeUnit.SECONDS));
    }
    
    private static class TaskImpl implements CancellableTask<CompilationInfo> {
        private CountDownLatch l;

        public TaskImpl(CountDownLatch l) {
            this.l = l;
        }
        
        public void cancel() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void run(CompilationInfo parameter) throws Exception {
            l.countDown();
        }
        
    }
}
