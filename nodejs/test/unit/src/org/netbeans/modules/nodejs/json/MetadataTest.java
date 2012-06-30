/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs.json;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.netbeans.modules.nodejs.ProjectMetadataImpl;

import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public class MetadataTest {
    Fake fake = new Fake();
    ProjectMetadataImpl impl = new ProjectMetadataImpl(fake);
    
    @Test
    public void testLoading() {
        Map m = impl.getMap();
        System.out.println("GOT " + m);
        assertNotNull(m);
        assertFalse(m.isEmpty());
        assertEquals("recon", impl.getValue("name"));
        assertEquals("0.0.8", impl.getValue("version"));
        assertEquals("git", impl.getValue("repository.type"));
    }

    @Test
    public void test() {
        impl.setValue("name", "thing");
        assertEquals ("thing", impl.getValue("name"));
        impl.setValue("name", "another");
        assertEquals ("another", impl.getValue("name"));
        test("foo.bar", "foobar");
        test("foo.baz", "foobaz");
        test("foo.fung.hey", "hey");
        System.out.println(impl);
    }
    
    private void test (String key, String val) {
        impl.setValue(key, val);
        assertEquals (val, impl.getValue(key));
    }
    
    static class Fake implements Project {
        FileObject root = FileUtil.createMemoryFileSystem().getRoot();
        
        Fake() {
            try {
                InputStream in = MetadataTest.class.getResourceAsStream("package_0.json");
                try {
                    FileObject fo = root.createData("package.json");
                    OutputStream out = fo.getOutputStream();
                    try {
                        FileUtil.copy(in, out);
                    } finally {
                        out.close();
                    }
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new Error(ex);
            }
        }

        public FileObject getProjectDirectory() {
            return root;
        }

        public Lookup getLookup() {
            return Lookup.EMPTY;
        }
        
    }
}
