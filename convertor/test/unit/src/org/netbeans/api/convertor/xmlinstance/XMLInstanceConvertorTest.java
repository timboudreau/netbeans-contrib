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

package org.netbeans.api.convertor.xmlinstance;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import junit.textui.TestRunner;
import org.netbeans.api.convertor.Convertors;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/**
 *
 * @author  David Konecny
 */
public class XMLInstanceConvertorTest extends NbTestCase {
    
    public XMLInstanceConvertorTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(XMLInstanceConvertorTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }

    public void testConvertor() throws Exception {
        String name = XMLInstanceConvertorTest.class.getResource("data").getFile() + "/CD1.xml";
        InputStream is = new FileInputStream(name);
        CD c = (CD)Convertors.read(is);
        assertNotNull(c);
        assertEquals(c, new CD());
        is.close();
        
        name = XMLInstanceConvertorTest.class.getResource("data").getFile() + "/CD2.xml";
        is = new FileInputStream(name);
        c = (CD)Convertors.read(is);
        assertNotNull(c);
        assertEquals(c, new CD("V.A.", "Radio 1 Essential Mixes"));
        is.close();
        
        name = XMLInstanceConvertorTest.class.getResource("data").getFile() + "/CD3.xml";
        is = new FileInputStream(name);
        c = (CD)Convertors.read(is);
        assertNotNull(c);
        assertEquals(c, new CD("The Hafler Trio", "Cleave: 9 Great Openings"));
        is.close();
        
    }
    
}
