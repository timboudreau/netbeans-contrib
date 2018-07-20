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
public class ManNodeTest extends NbTestCase implements ManNode.ChangeCallback {
    
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
        
        Node n = ManNode.createManifestModel(mf, this);
        
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

    public void change(String section, String name, String oldValue, String newValue) throws IllegalArgumentException {
    }
}
