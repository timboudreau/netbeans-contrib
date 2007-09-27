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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.netbeans.junit.MockServices;
import org.netbeans.modules.latex.UnitUtilities;

import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.netbeans.modules.latex.bibtex.IncrementalParserTestStub.*;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModel;
import org.netbeans.modules.latex.model.bibtex.Entry;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;

/**
 *
 * @author Jan Lahoda
 */
public class IncrementalParserTest extends TestCase {
    
    /** Creates a new instance of IncrementalParserTest */
    public IncrementalParserTest(String name) {
        super(name);
    }
    
    public PrintStream getLog() {
        return System.err;
    }
    
    public void setUp() throws Exception {
        UnitUtilities.prepareTest(new String[0], new Object[] {new BiBTeXModelFactoryImpl()});
    }
    
    private URL getTestFile() throws MalformedURLException {
        URL file = IncrementalParserTest.class.getResource("data/test.bib");
        
        if (file == null) {
            throw new NullPointerException("The given resource not found!");
        }
        
        return file;
//        return new URL("file://" + new File(getDataDir(), "test.tex").getAbsolutePath());//TODO: CORRECT!
    }
    
    private void performTest(URL url, Description description) throws Exception {
        try {
            FileObject file = URLMapper.findFileObject(url);
            
            assertNotNull(file);
            
            IncrementalParserTestStub.performTest(file, description);
        } catch (Exception e) {
            AssertionFailedError error = new AssertionFailedError("Test Case Failed");
            
            error.initCause(e);
            
            throw error;
        }
    }
    
    public void testTyping() throws Exception {
        performTest(getTestFile(),
        new Description(
"\n@INPROCEEDINGS{FB03,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}\n"+
"\n"+
"@INPROCEEDINGS{FB05,\n"+
"    title = \"title\",\n"+
"    journal = \"test - journal\",\n"+
"    author = \"test\",\n"+
"}\n", new Change[] {
        new AddChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "year = \"2003\",\n", "note = \"this is a note\",\n"),
        new AddChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "\n@INPROCEEDINGS", "@ARTICLE{FB01,\nauthor=\"X. Test and Y. Coder\",\ntitle=\"Testing Environment\"\n}"),
        new RemoveChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "    pages = \"142-143\",", "    pages = \"142-143\",".length()),
        new AddChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "author = \"X. Foo and Y. Bar\",\n", " pages = \"142-143\",\n"),
        }));
    }
    
    public void testCopyPaste1() throws Exception {
        Validator pasteValidator = new ProxyValidator(new Validator[] {
            IncrementalParserTestStub.DEFAULT_VALIDATOR,
            new TagValidator(new String[] {"FB01", "FB03", "FB05"}),
        }
        );
        performTest(getTestFile(),
        new Description(
"%d\n@INPROCEEDINGS{FB03,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}\n"+
"\n"+
"@INPROCEEDINGS{FB05,\n"+
"    title = \"title\",\n"+
"    journal = \"test - journal\",\n"+
"    author = \"test\",\n"+
"}\n", new Change[] {
        new PasteChange(pasteValidator, "@INPROCEEDINGS{FB03", "@INPROCEEDINGS{FB01,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}"),
        new DeleteChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "@INPROCEEDINGS{FB01", ("@INPROCEEDINGS{FB01,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}").length()),
        }));
    }
    
    public void testAddEntry1() throws Exception {
        Validator v = new ProxyValidator(new Validator[] {
            IncrementalParserTestStub.DEFAULT_VALIDATOR,
            new TagValidator(new String[] {"FB03", "FB05", "FB07"})
        });
        
        PublicationEntry newEntry = new PublicationEntry();
        
        newEntry.setTag("FB07");
        newEntry.setType("ARTICLE");
        newEntry.setTitle("On BiBTeX Files Processing");
        newEntry.setAuthor("Joe Hacker");
        
        Map content = new HashMap();
        
        content.put("url", "http://somwhere.org");
        
        newEntry.setContent(content);
        
        performTest(getTestFile(),
        new Description(
"%d\n@INPROCEEDINGS{FB03,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}\n"+
"\n"+
"@INPROCEEDINGS{FB05,\n"+
"    title = \"title\",\n"+
"    journal = \"test - journal\",\n"+
"    author = \"test\",\n"+
"}\n\n", new Change[] {
        new AddEntryChange(v, newEntry)
        }));
    }

    public void testAddEntryIntoEmptyFile() throws Exception {
        Validator v = new ProxyValidator(new Validator[] {
            IncrementalParserTestStub.DEFAULT_VALIDATOR,
            new TagValidator(new String[] {"FB07"})
        });
        
        PublicationEntry newEntry = new PublicationEntry();
        
        newEntry.setTag("FB07");
        newEntry.setType("ARTICLE");
        newEntry.setTitle("On BiBTeX Files Processing");
        newEntry.setAuthor("Joe Hacker");
        
        Map<String, String> content = new HashMap<String, String>();
        
        content.put("url", "http://somwhere.org");
        
        newEntry.setContent(content);
        
        performTest(getTestFile(),
        new Description(
"", new Change[] {
        new AddEntryChange(v, newEntry)
        }));
    }

    public void testParsingZajo1() throws Exception {
        performTest(getTestFile(),
        new Description(
"\n@INPROCEEDINGS{FB_03,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}\n"+
"\n"+
"@INPROCEEDINGS{FB-05,\n"+
"    title = \"title\",\n"+
"    journal = \"test - journal\",\n"+
"    author = \"test\",\n"+
"}\n", new Change[] {
        new ValidateChange(new Validator() {
            public void validate(Document doc) throws BadLocationException, IOException {
                BiBTeXModel model = BiBTeXModel.getModel(Utilities.getDefault().getFile(doc));
                
                assertEquals(2, model.getEntries().size());
                
                Entry e1 = (Entry) model.getEntries().get(0);
                Entry e2 = (Entry) model.getEntries().get(1);
                
                assertNotNull(e1);
                assertNotNull(e2);
                
                assertTrue(e1 instanceof PublicationEntry);
                assertTrue(e2 instanceof PublicationEntry);
                
                assertEquals("FB_03", ((PublicationEntry) e1).getTag());
                assertEquals("FB-05", ((PublicationEntry) e2).getTag());
            }
        })
        
        }));
    }

    public void testParsingZajo2() throws Exception {
        performTest(getTestFile(),
        new Description(
"\n@INPROCEEDINGS{FB_03,\n" +
"    author = {X. Foo and Y. {Bar}},\n"+
"    title = {Do anything:{test} Continued},\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"}\n", new Change[] {
        new ValidateChange(new Validator() {
            public void validate(Document doc) throws BadLocationException, IOException {
                BiBTeXModel model = BiBTeXModel.getModel(Utilities.getDefault().getFile(doc));
                
                assertEquals(1, model.getEntries().size());
                
                Entry e1 = (Entry) model.getEntries().get(0);
                
                assertNotNull(e1);
                
                assertTrue(e1 instanceof PublicationEntry);
                
                assertEquals("Do anything:{test} Continued", ((PublicationEntry) e1).getTitle());
            }
        })
        
        }));
    }
    
}
