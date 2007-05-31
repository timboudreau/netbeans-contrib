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

package org.netbeans.core.registry.serialdataconv;

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
public class SerialDataConvertorTest extends NbTestCase {
    
    public SerialDataConvertorTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(SerialDataConvertorTest.class));
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
        Context ctx = SpiUtils.createContext(rootCtx).getSubcontext("folder2");
        
        SerializableCD c = (SerializableCD)ctx.getObject("cd1", null);
        assertNotNull(c);
        assertEquals(c, new SerializableCD("Giya Kancheli", "Mourned by the Wind"));
        
        c = (SerializableCD)ctx.getObject("cd2", null);
        assertNotNull(c);
        assertEquals(c, new SerializableCD());
        
        c = (SerializableCD)ctx.getObject("cd3", null);
        assertNotNull(c);
        assertEquals(c, new SerializableCD("V.A.", "Radio Kiss100 Mixes"));
        
        SerializableCD cd = new SerializableCD("Arvo Part", "Kanon Pokajanen");
        SerializableCD cd2 = new SerializableCD("Arvo Part", "Kanon Pokajanen");

        ctx.putObject("newcd", cd);
        assertEquals(ctx.getObject("newcd", null), cd2);
        cd = null;
        System.gc();System.gc();System.gc();System.gc();System.gc();
        assertEquals(ctx.getObject("newcd", null), cd2);
        
    }
    
}
