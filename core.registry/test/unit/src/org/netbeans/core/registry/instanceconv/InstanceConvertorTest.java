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

package org.netbeans.core.registry.instanceconv;

import junit.textui.TestRunner;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.fs.FileSystemContextFactory;
import org.netbeans.core.registry.TestMFS;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import java.net.URL;

/**
 *
 * @author  David Konecny
 */
public class InstanceConvertorTest extends NbTestCase {
    
    public InstanceConvertorTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(InstanceConvertorTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }

    public void testConvertor() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());
        
        URL u1 = getClass().getResource("data/layer.xml");
               
        FileSystem xfs1 = new XMLFileSystem( u1 );
        FileSystem mfs = new TestMFS( new FileSystem[] { lfs, xfs1 } );
    
        BasicContext rootCtx = FileSystemContextFactory.createContext(mfs.getRoot());
        Context ctx = SpiUtils.createContext(rootCtx).getSubcontext("folder3");
        
        CD c = (CD)ctx.getObject("org-netbeans-core-registry-instanceconv-CD", null);
        assertNotNull(c);
        assertEquals(c, new CD());
        
        c = (CD)ctx.getObject("cd2", null);
        assertNotNull(c);
        assertEquals(c, new CD());
        
        c = (CD)ctx.getObject("cd3", null);
        assertNotNull(c);
        assertEquals(c, CD.createDefault());
        
        c = (CD)ctx.getObject("cd4", null);
        assertNotNull(c);
        assertEquals(c, new CD("The Hafler Trio", "Cleave: 9 Great Openings"));
        
    }
    
}
