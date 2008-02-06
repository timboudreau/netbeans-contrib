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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.clearcase.client.status;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.netbeans.modules.clearcase.ClearcaseException;
import org.netbeans.modules.clearcase.client.ClearcaseCommand;
import org.netbeans.modules.clearcase.client.test.DummyCleartool;

/**
 *
 * @author Tomas Stupka
 */
public class ListStatusTest extends TestCase {
    
    public ListStatusTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUptodate() throws IOException, ClearcaseException {
        String rawOutput = "version                Main.java@@/main/1                     Rule: element * /main/LATEST";

        List<FileEntry> entryList = execList(rawOutput);
        assertEquals(1, entryList.size());
        assertListOutput(entryList.get(0), null, new File("Main.java"), "/main", 1L, "/main/1", null, -1, null, false, "version");                
    }
        
    public void testViewPrivate() throws IOException, ClearcaseException {
        String rawOutput = "view private object    file0";
        List<FileEntry> entryList = execList(rawOutput);
        
        assertEquals(1, entryList.size());
        assertListOutput(entryList.get(0), null, new File("file0"), null, -1, null, null, -1, null, false, "view private object");        
    }

    public void testCheckedout() throws IOException, ClearcaseException {
        String rawOutput = "version                README@@/main/CHECKEDOUT from /main/3  Rule: element * CHECKEDOUT";
        List<FileEntry> entryList = execList(rawOutput);
        
        assertEquals(1, entryList.size());        
        assertListOutput(entryList.get(0), null, new File("README"), "/main", FileVersionSelector.CHECKEDOUT_VERSION, "/main/CHECKEDOUT", "/main", 3L, "/main/3", true, "version");                
    }

    public void testCheckedoutButRemoved() throws IOException, ClearcaseException {
        String[] rawOutput = new String[] {"version                README@@/main/CHECKEDOUT from /main/1 [checkedout but removed]",
                                           "version                test1@@/main/CHECKEDOUT from /main/2 [not loaded, checkedout but removed]"};
        List<FileEntry> entryList = execList(rawOutput);
        
        assertEquals(2, entryList.size());        
        assertListOutput(entryList.get(0), "[checkedout but removed]", new File("README"), "/main", FileVersionSelector.CHECKEDOUT_VERSION, "/main/CHECKEDOUT", "/main", 1L, "/main/1", true, "version");                
        assertListOutput(entryList.get(1), "[not loaded, checkedout but removed]", new File("test1"), "/main", FileVersionSelector.CHECKEDOUT_VERSION, "/main/CHECKEDOUT", "/main", 2L, "/main/2", true, "version");                        
    }
        
    public void testLoadedButMissing() throws IOException, ClearcaseException {
        String rawOutput = "version                test1@@/main/2 [loaded but missing]    Rule: element * /main/LATEST";
        List<FileEntry> entryList = execList(rawOutput);
        
        assertEquals(1, entryList.size());        
        assertListOutput(entryList.get(0), "[loaded but missing]", new File("test1"), "/main", 2L, "/main/2", null, -1, null, false, "version");                
    }        

    public void testHijacked() throws IOException, ClearcaseException {
        String rawOutput = "version                test1@@/main/2 [hijacked]              Rule: element * /main/LATEST";
        List<FileEntry> entryList = execList(rawOutput);
        
        assertEquals(1, entryList.size());        
        assertListOutput(entryList.get(0), "[hijacked]", new File("test1"), "/main", 2L, "/main/2", null, -1, null, false, "version");                
    }        

    public void testEclipsed() throws IOException, ClearcaseException {
        String[] rawOutput = new String[] {            
            "file element           Makefile@@ [eclipsed]",
            "view private object    Makefile"
        };
        List<FileEntry> entryList = execList(rawOutput);
        
        assertEquals(1, entryList.size());                
        assertListOutput(entryList.get(0), "[eclipsed]", new File("Makefile"), null, -1, null, null, -1, null, false, "file element");                        
        
        rawOutput = new String[] {                        
            "view private object    Makefile",
            "file element           Makefile@@ [eclipsed]"
        };
        entryList = execList(rawOutput);
        
        assertEquals(1, entryList.size());                
        assertListOutput(entryList.get(0), "[eclipsed]", new File("Makefile"), null, -1, null, null, -1, null, false, "file element");                        
        
    }        
    
    public void testCrap() throws IOException, ClearcaseException {
        List<FileEntry> entryList = execList(null);
        assertEquals(entryList.size(), 0);       
        
        String[] rawOutput = new String[] {
            "x",
            "",
            "crap crap crap",            
        };
        
        entryList = execList(rawOutput);        
        assertEquals(0, entryList.size());               
        
        rawOutput = new String[] {            
            "version                Main.java@@/main/1                     ",            
            "version                Main1.java@@/main/",
            "version                Main2.java@@/main/xxx",
            "version                Main3.java@@",
            "version                Main4.java@",
            "version                Main5.java",
            "version       ",            
        };
        entryList = execList(rawOutput);   
        assertEquals(7, entryList.size());               
                
    }        
            
    private void assertListOutput(FileEntry fe, String annotation, File file, String versionPath, 
                                  long version, String versionSelector, String originVersionPath, 
                                  long originVersion, String originVersionSelector, boolean checkedout, String type) {         
        assertEquals(annotation, fe.getAnnotation());
        assertEquals(file, fe.getFile());
        if(versionSelector != null) {
            assertNotNull(fe.getVersion());
            assertEquals(versionPath,       fe.getVersion().getPath());
            assertEquals(version,           fe.getVersion().getVersionNumber());
            assertEquals(versionSelector,   fe.getVersion().getVersionSelector());   
            assertEquals(checkedout,        fe.getVersion().isCheckedout());            
        } else {
            assertNull(fe.getVersion());   
        }        
        if(originVersionSelector != null) {
            assertNotNull(fe.getOriginVersion());
            assertEquals(originVersionPath,         fe.getOriginVersion().getPath());
            assertEquals(originVersion,             fe.getOriginVersion().getVersionNumber());
            assertEquals(originVersionSelector,     fe.getOriginVersion().getVersionSelector());               
        } else {
            assertNull(fe.getOriginVersion());
        }                                
        assertEquals(type, fe.getType());
    }
    
    private List<FileEntry> execList(String ...rawOutput) throws IOException, ClearcaseException {

        ListCommandExecutor executor = new ListCommandExecutor(rawOutput);
        DummyCleartool ct = new DummyCleartool(executor);

        ListStatus lf = new ListStatus(new File(""), false);
        for (ClearcaseCommand c : lf) {
            ct.exec(c);
        }

        List<FileEntry> entryList = new  ArrayList<FileEntry>(lf.getOutput());
        return entryList;
    }

    private class ListCommandExecutor implements DummyCleartool.CommandExecutor {
        final private String[] output;

        public ListCommandExecutor(String ...output) {
            this.output = output;
        }
        
        public void exec(ClearcaseCommand command) {
            if(output == null) {
                command.outputText(null);
                return;
            }
            for (String string : output) {
                command.outputText(string);                        
            }
        }        
    }    
}
