/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
