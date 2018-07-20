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

import java.io.IOException;
import javax.swing.text.Document;
import org.netbeans.junit.NbTestCase;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/** That the behaviour of whole editor support.
 *
 * @author Jaroslav Tulach
 */
public class ManEditorTest extends NbTestCase {
    private DataObject obj;
    private ManEditor support;
    
    public ManEditorTest(String testName) {
        super(testName);
    }

    @SuppressWarnings("deprecation")
    private static void initMimeType() {
        // in real system this is done by the XML Mime Resolver registrations,
        // however in testing environment, the infrastructure that reads the XML
        // is not started
        FileUtil.setMIMEType("mf", "text/x-manifest");
    }
    
    static {
        initMimeType();
    }
    
    protected boolean runInEQ() {
        return true;
    }
    
    protected void setUp() throws Exception {
        clearWorkDir();
        
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());
        FileObject fo = FileUtil.createData(lfs.getRoot(), "test/mani.mf");
        
        obj = DataObject.find(fo);
        
        assertEquals("Right class", ManDataObject.class, obj.getClass());
        
        support = ((ManDataObject)obj).getLookup().lookup(ManEditor.class);
        assertNotNull("Support found", support);
    }

    protected void tearDown() throws Exception {
    }
    
    public void testCanReadAndWrite() throws Exception {
        Document d = support.openDocument();
        
        assertEquals(ManEditor.Visual.class, support.descriptions[1].createElement().getClass());
        ManEditor.Visual v = (ManEditor.Visual)support.descriptions[1].createElement();
        
        assertEquals("Just main section", 1, v.getExplorerManager().getRootContext().getChildren().getNodes().length);
        Node.PropertySet[] sets = v.getExplorerManager().getRootContext().getChildren().getNodes()[0].getPropertySets();
        assertEquals("One", 1, sets.length);
        Node.Property[] props = sets[0].getProperties();
        assertEquals("No property", 0, props.length);
        
        d.insertString(0, "Main-Class: x.java\n", null);

        props = sets[0].getProperties();
        assertEquals("One property", 1, props.length);
        assertEquals("Main-Class", props[0].getName());
        assertEquals("x.java", props[0].getValue());
        
        SaveCookie s = obj.getCookie(SaveCookie.class);
        assertNotNull("Document is modified", s);
        s.save();
        
        assertNull("No longer modified", obj.getCookie(SaveCookie.class));
        
        props[0].setValue("y.html");
        
        props = sets[0].getProperties();
        assertEquals("One property", 1, props.length);
        assertEquals("Main-Class", props[0].getName());
        assertEquals("y.html", props[0].getValue());

        s = obj.getCookie(SaveCookie.class);
        assertNotNull("Document is modified2", s);
        s.save();
        
        assertNull("No longer modified2", obj.getCookie(SaveCookie.class));
    }
}
