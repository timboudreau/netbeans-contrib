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
import java.util.List;
import junit.framework.TestCase;
import org.netbeans.modules.clearcase.ClearcaseException;
import org.netbeans.modules.clearcase.client.ClearcaseCommand;
import org.netbeans.modules.clearcase.client.status.ListFiles.ListOutput;
import org.netbeans.modules.clearcase.client.test.DummyCleartool;

/**
 *
 * @author Tomas Stupka
 */
public class ListFilesTest extends TestCase {
    
    public ListFilesTest(String testName) {
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

        List<ListOutput> outputList = execList(rawOutput);
        assertEquals(1, outputList.size());
        assertListOutput(outputList.get(0), null, new File("Main.java"), "/main", 1L, "/main/1", null, -1, null, false, "version");                
    }
        
    public void testViewPrivate() throws IOException, ClearcaseException {
        String rawOutput = "view private object    file0";
        List<ListOutput> outputList = execList(rawOutput);
        
        assertEquals(1, outputList.size());
        assertListOutput(outputList.get(0), null, new File("file0"), null, -1, null, null, -1, null, false, "view private object");        
    }

    public void testCheckedout() throws IOException, ClearcaseException {
        String rawOutput = "version                README@@/main/CHECKEDOUT from /main/3  Rule: element * CHECKEDOUT";
        List<ListOutput> outputList = execList(rawOutput);
        
        assertEquals(1, outputList.size());        
        assertListOutput(outputList.get(0), null, new File("README"), "/main", FileVersionSelector.CHECKEDOUT_VERSION, "/main/CHECKEDOUT", "/main", 3L, "/main/3", true, "version");                
    }

    public void testCheckedoutButRemoved() throws IOException, ClearcaseException {
        String[] rawOutput = new String[] {"version                README@@/main/CHECKEDOUT from /main/1 [checkedout but removed]",
                                           "version                test1@@/main/CHECKEDOUT from /main/2 [not loaded, checkedout but removed]"};
        List<ListOutput> outputList = execList(rawOutput);
        
        assertEquals(2, outputList.size());        
        assertListOutput(outputList.get(0), "[checkedout but removed]", new File("README"), "/main", FileVersionSelector.CHECKEDOUT_VERSION, "/main/CHECKEDOUT", "/main", 1L, "/main/1", true, "version");                
        assertListOutput(outputList.get(1), "[not loaded, checkedout but removed]", new File("test1"), "/main", FileVersionSelector.CHECKEDOUT_VERSION, "/main/CHECKEDOUT", "/main", 2L, "/main/2", true, "version");                        
    }
        
    public void testLoadedButMissing() throws IOException, ClearcaseException {
        String rawOutput = "version                test1@@/main/2 [loaded but missing]    Rule: element * /main/LATEST";
        List<ListOutput> outputList = execList(rawOutput);
        
        assertEquals(1, outputList.size());        
        assertListOutput(outputList.get(0), "[loaded but missing]", new File("test1"), "/main", 2L, "/main/2", null, -1, null, false, "version");                
    }        

    public void testHijacked() throws IOException, ClearcaseException {
        String rawOutput = "version                test1@@/main/2 [hijacked]              Rule: element * /main/LATEST";
        List<ListOutput> outputList = execList(rawOutput);
        
        assertEquals(1, outputList.size());        
        assertListOutput(outputList.get(0), "[hijacked]", new File("test1"), "/main", 2L, "/main/2", null, -1, null, false, "version");                
    }        

    public void testEclipsed() throws IOException, ClearcaseException {
        String[] rawOutput = new String[] {
            "view private object    Makefile",
            "file element           Makefile@@ [eclipsed]"
        };
        List<ListOutput> outputList = execList(rawOutput);
        
        assertEquals(2, outputList.size());        
        assertListOutput(outputList.get(0), null, new File("Makefile"), null, -1, null, null, -1, null, false, "view private object");                
        assertListOutput(outputList.get(1), "[eclipsed]", new File("Makefile"), null, -1, null, null, -1, null, false, "file element");                        
    }        
    
    public void testCrap() throws IOException, ClearcaseException {
        List<ListOutput> outputList = execList(null);
        assertEquals(outputList.size(), 0);       
        
        String[] rawOutput = new String[] {
            "x",
            "",
            "crap crap crap",            
        };
        
        outputList = execList(rawOutput);        
        assertEquals(0, outputList.size());               
        
        rawOutput = new String[] {            
            "version                Main.java@@/main/1                     ",            
            "version                Main.java@@/main/",
            "version                Main.java@@/main/xxx",
            "version                Main.java@@",
            "version                Main.java@",
            "version                Main.java",
            "version       ",            
        };
        outputList = execList(rawOutput);   
        assertEquals(7, outputList.size());               
                
    }        
            
    private void assertListOutput(ListOutput o, String annotation, File file, String versionPath, 
                                  long version, String versionSelector, String originVersionPath, 
                                  long originVersion, String originVersionSelector, boolean checkedout, String type) {         
        assertEquals(annotation, o.getAnnotation());
        assertEquals(file, o.getFile());
        if(versionSelector != null) {
            assertNotNull(o.getVersion());
            assertEquals(versionPath,       o.getVersion().getPath());
            assertEquals(version,           o.getVersion().getVersionNumber());
            assertEquals(versionSelector,   o.getVersion().getVersionSelector());   
            assertEquals(checkedout,        o.getVersion().isCheckedout());            
        } else {
            assertNull(o.getVersion());   
        }        
        if(originVersionSelector != null) {
            assertNotNull(o.getOriginVersion());
            assertEquals(originVersionPath,         o.getOriginVersion().getPath());
            assertEquals(originVersion,             o.getOriginVersion().getVersionNumber());
            assertEquals(originVersionSelector,     o.getOriginVersion().getVersionSelector());               
        } else {
            assertNull(o.getOriginVersion());
        }                                
        assertEquals(type, o.getType());
    }
    
    private List<ListOutput> execList(String ...rawOutput) throws IOException, ClearcaseException {

        ListCommandExecutor executor = new ListCommandExecutor(rawOutput);
        DummyCleartool ct = new DummyCleartool(executor);

        ListFiles lf = new ListFiles(new File(""), false);
        for (ClearcaseCommand c : lf) {
            ct.exec(c);
        }

        List<ListOutput> outputList = lf.getOutputList();
        return outputList;
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
