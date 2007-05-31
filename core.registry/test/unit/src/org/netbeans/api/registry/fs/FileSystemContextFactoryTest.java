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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.registry.fs;

import junit.textui.TestRunner;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.MultiFileSystem;

public class FileSystemContextFactoryTest extends NbTestCase {

    private FileObject root = null;

    public FileSystemContextFactoryTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(FileSystemContextFactoryTest.class));
    }
    
    protected void setUp () throws Exception {
    }
    
    public void testRootContextCreation() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());
        BasicContext rootCtx = FileSystemContextFactory.createContext(lfs.getRoot());
        rootCtx.createSubcontext("abcd");
        FileObject fo = lfs.getRoot().getFileObject("abcd");
        assertTrue ("Cannot create initial context", fo != null);
        rootCtx.destroySubcontext("abcd");
    }
    
    public static class TestMFS extends MultiFileSystem {
        
        public TestMFS() {
            super();
        }
        
        public TestMFS( FileSystem[] delegates ) {
            super( delegates );
        }
        
        public void setDels( FileSystem[] fss ) {
            setDelegates( fss );
        }
        
    }
    
}
