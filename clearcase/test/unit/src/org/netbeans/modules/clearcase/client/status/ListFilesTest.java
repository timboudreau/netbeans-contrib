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

    public void testListStatus() throws IOException, ClearcaseException {
        System.out.println("getOutputList");
        
        String[] rawOutput = new String[] {
            "version                Main.java@@/main/1                     Rule: element * /main/LATEST",
            "view private object    file0"
        };
        ListCommandExecutor executor = new ListCommandExecutor(rawOutput);
        DummyCleartool ct = new DummyCleartool(executor);
        
        ListFiles lf = new ListFiles(new File(""), false);
        for (ClearcaseCommand c : lf.getCommands()) {
            ct.exec(c);
        }

        List<ListOutput> outputList = lf.getOutputList();
        assertEquals(outputList.size(), 2);
        
        ListOutput o = outputList.get(0);
        assertEquals(null, o.getAnnotation());
        assertEquals(new File("Main.java"), o.getFile());
        assertEquals("/main", o.getVersion().getPath());
        assertEquals(1, o.getVersion().getVersionNumber());
        assertEquals("/main/1", o.getVersion().getVersionSelector());
        assertEquals(false, o.getVersion().isCheckedout());
        assertEquals(null, o.getOriginVersion());
        assertEquals("version", o.getType());
        
        o = outputList.get(1);
        assertEquals(null, o.getAnnotation());
        assertEquals(new File("file0"), o.getFile());
        assertEquals(null, o.getVersion());        
        assertEquals(null, o.getOriginVersion());
        assertEquals("view private object", o.getType());        
    }
    
    private class ListCommandExecutor implements DummyCleartool.CommandExecutor {
        final private String[] output;

        public ListCommandExecutor(String[] output) {
            this.output = output;
        }
        
        public void exec(ClearcaseCommand command) {
            for (String string : output) {
                command.outputText(string);                        
            }
        }        
    }    
}
