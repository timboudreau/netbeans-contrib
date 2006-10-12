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
 * The Original Software is NetBeans. 
 *
 * Portions Copyrighted 2006 Sun Microsystems, Inc.
 */

package org.netbeans.modules.manifesteditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Locale;
import java.util.jar.Manifest;
import org.netbeans.junit.NbTestCase;
import org.openide.nodes.Node;

/**
 *
 * @author Jaroslav Tulach
 */
public class ManNodeTest extends NbTestCase {
    
    public ManNodeTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        Locale.setDefault(Locale.US);
    }

    protected void tearDown() throws Exception {
    }

    public void testNodesFromManifest() throws Exception {
        File f = new File(getWorkDir(), "man.mf");
        FileWriter w = new FileWriter(f);
        String txt = "" +
            "Main-Class: x.jarda\n" +
            "\n" +
            "Name: file.f\n" +
            "Value: nic\n" +
            "";
        w.write(txt);
        w.close();
        
        Manifest mf = new Manifest(new FileInputStream(f));
        
        Node n = ManNode.createManifestModel(mf);
        
        assertNotNull("Created", n);
        assertEquals("Two sections", 2, n.getChildren().getNodes(true).length);
        
        
        assertEquals("1st is named", "Main", n.getChildren().getNodes(true)[0].getName());
        assertEquals("2nd is named", "file.f", n.getChildren().getNodes(true)[1].getName());

        assertEquals("1st is named", "Main Attributes", n.getChildren().getNodes(true)[0].getDisplayName());
        assertEquals("2nd is named", "Section for file.f", n.getChildren().getNodes(true)[1].getDisplayName());

        {
            Node.PropertySet[] pss = n.getChildren().getNodes(true)[0].getPropertySets();
            assertEquals("One", 1, pss.length);
            assertEquals("Attributes", pss[0].getName());

            Node.Property[] props = pss[0].getProperties();
            assertEquals("One property", 1, props.length);
            assertEquals("Main-Class", props[0].getName());
            assertEquals("x.jarda", props[0].getValue());
        }            

        {
            Node.PropertySet[] pss = n.getChildren().getNodes(true)[1].getPropertySets();
            assertEquals("One", 1, pss.length);
            assertEquals("Attributes", pss[0].getName());

            Node.Property[] props = pss[0].getProperties();
            assertEquals("One property", 1, props.length);
            assertEquals("Value", props[0].getName());
            assertEquals("nic", props[0].getValue());
        }            
        
    }
}
