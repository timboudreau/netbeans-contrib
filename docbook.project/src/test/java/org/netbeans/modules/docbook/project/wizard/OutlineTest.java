/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.docbook.project.wizard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.Test;
import org.netbeans.modules.docbook.project.DbProject;
import org.netbeans.modules.docbook.project.DbProjectFactory;
import org.netbeans.modules.docbook.project.wizard.Outline.Item;
import static org.junit.Assert.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public final class OutlineTest {

    private static final String TEST_TEXT="Chapter 1\n" +
            "Chapter 2\n" +
            "  Section 1\n" +
            "  Section 2\n" +
            "      Section 2a\n" +
            "      Section 2b\n" +
            "        Section 2b1\n" +
            "        Section 2b2\n" +
            "  Section 3\n" +
            "\n" +
            "Chapter 3\n" +
            "  Section 3a\n" +
            "   Section 3a1\n" +
            "\n" +
            "   Section 3a2\n" +
            "  Section 3b\n";

    private static final String TEST_TEXT_OUT = "Chapter 1\n" +
            "Chapter 2\n" +
            "    Section 1\n" +
            "    Section 2\n" +
            "        Section 2a\n" +
            "        Section 2b\n" +
            "            Section 2b1\n" +
            "            Section 2b2\n" +
            "    Section 3\n" +
            "Chapter 3\n" +
            "    Section 3a\n" +
            "        Section 3a1\n" +
            "        Section 3a2\n" +
            "    Section 3b\n";

    private static final String XML_OUT = "<chapter id=\"chapter1\">\n" +
            "    <title>Chapter 1</title>\n" +
            "    <para>\n" +
            "    [TODO:  content here]\n" +
            "    </para>\n" +
            "</chapter>\n" +
            "<chapter id=\"chapter2\">\n" +
            "    <title>Chapter 2</title>\n" +
            "    <section id=\"section1\">\n" +
            "        <title>Section 1</title>\n" +
            "        <para>\n" +
            "        [TODO:  content here]\n" +
            "        </para>\n" +
            "    </section>\n" +
            "    <section id=\"section2\">\n" +
            "        <title>Section 2</title>\n" +
            "        <section id=\"section2a\">\n" +
            "            <title>Section 2a</title>\n" +
            "            <para>\n" +
            "            [TODO:  content here]\n" +
            "            </para>\n" +
            "        </section>\n" +
            "        <section id=\"section2b\">\n" +
            "            <title>Section 2b</title>\n" +
            "            <section id=\"section2b1\">\n" +
            "                <title>Section 2b1</title>\n" +
            "                <para>\n" +
            "                [TODO:  content here]\n" +
            "                </para>\n" +
            "            </section>\n" +
            "            <section id=\"section2b2\">\n" +
            "                <title>Section 2b2</title>\n" +
            "                <para>\n" +
            "                [TODO:  content here]\n" +
            "                </para>\n" +
            "            </section>\n" +
            "        </section>\n" +
            "    </section>\n" +
            "    <section id=\"section3\">\n" +
            "        <title>Section 3</title>\n" +
            "        <para>\n" +
            "        [TODO:  content here]\n" +
            "        </para>\n" +
            "    </section>\n" +
            "</chapter>\n" +
            "<chapter id=\"chapter3\">\n" +
            "    <title>Chapter 3</title>\n" +
            "    <section id=\"section3a\">\n" +
            "        <title>Section 3a</title>\n" +
            "        <section id=\"section3a1\">\n" +
            "            <title>Section 3a1</title>\n" +
            "            <para>\n" +
            "            [TODO:  content here]\n" +
            "            </para>\n" +
            "        </section>\n" +
            "        <section id=\"section3a2\">\n" +
            "            <title>Section 3a2</title>\n" +
            "            <para>\n" +
            "            [TODO:  content here]\n" +
            "            </para>\n" +
            "        </section>\n" +
            "    </section>\n" +
            "    <section id=\"section3b\">\n" +
            "        <title>Section 3b</title>\n" +
            "        <para>\n" +
            "        [TODO:  content here]\n" +
            "        </para>\n" +
            "    </section>\n" +
            "</chapter>\n";
    @After
    public void tearDown() {
    }

    @Test
    public void testToString() {
        Outline o = new Outline (TEST_TEXT);
        assertEquals(TEST_TEXT_OUT, o.toString());
        String found = o.toXml(new Outline.TagProvider() {

            public String getTag(int depth) {
                return depth == 0 ? "chapter" : "section";
            }

            public String getPlaceholderText(int depth) {
                return "[TODO:  content here]";
            }

            public boolean skip(Item item, int depth) {
                return false;
            }
        }, 0);

        String[] l1 = XML_OUT.split("\n");
        String[] l2 = found.split("\n");
        for (int i=0; i < Math.min (l1.length, l2.length); i++) {
//            System.err.println(" exp '" + l1[i] + "'\n got '" + l2[i] + "'");
            assertEquals ("Mismatch at line " + i + " expected '" + l1[i]
                    + "' got '" + l2[i] + "'", l1[i], l2[i]);
        }
    }

    @Test
    public void testInfo() {
        Info info = new Info ("Test Project", "A test of a project", "First Last");
        assertEquals ("First", info.firstName());
        assertEquals ("Last", info.lastName());

        info = new Info ("Test Project", "A test of a project", "First Last, Esq.");
        assertEquals ("First", info.firstName());
        assertEquals ("Last", info.lastName());
        assertEquals ("Esq.", info.honorific());

        info = new Info ("Test Project", "A test of a project", "First Middle Middler Last, Esq.");
        assertEquals ("First", info.firstName());
        assertEquals ("Last", info.lastName());
        assertEquals ("Esq.", info.honorific());
        assertEquals (0, info.otherNames().indexOf("Middle"));
        assertEquals (1, info.otherNames().indexOf("Middler"));

        info = new Info ("Test Project", "A test of a project", "First Middle Last");
        assertEquals (0, info.otherNames().indexOf("Middle"));
        assertEquals (-1, info.otherNames().indexOf("Middler"));

        info = new Info ("Test Project", "A test of a project", "First Middle Middler Last");
        assertEquals (0, info.otherNames().indexOf("Middle"));
        assertEquals (1, info.otherNames().indexOf("Middler"));
    }

    @Test
    public void testCreateProject() throws IOException {
        File tmp = new File (System.getProperty("java.io.tmpdir"));
        assertTrue (tmp.exists());
        assertTrue (tmp.isDirectory());
        FileObject tmpDir = FileUtil.toFileObject(FileUtil.normalizeFile(tmp));
        assertNotNull (tmpDir);
        String pDirName = FileUtil.findFreeFolderName(tmpDir, "dbprj" + System.currentTimeMillis());
        FileObject projDir = tmpDir.createFolder(pDirName);
        System.err.println("PROJECT IN " + projDir.getPath());
        String bookOutline = "Chapter 1\n  Section 1\n    SubSection 1a\n  Section 2\nChapter 2\n";
        Info info = new Info ("Test Project", "A test of a project", "First Last");
        ProjectKind.Book.createProject("Test Project", projDir, info, new Outline(bookOutline), ChapterGenerationStyle.DIRECTORY_PER_CHAPTER);
        DbProjectFactory f = Lookup.getDefault().lookup(DbProjectFactory.class);
        assertNotNull (f);
        assertTrue (f.isProject(projDir));
        FileObject fo = projDir.getFileObject("Chapter1/Chapter1.xml");
        assertNotNull(fo);
        fo = projDir.getFileObject("Chapter2/Chapter2.xml");
        assertNotNull(fo);
        fo = projDir.getFileObject("TestProject.xml");
        assertNotNull(fo);
        fo = projDir.getFileObject(DbProject.PROJECT_DIR);
        assertNotNull(fo);
        fo = projDir.getFileObject(DbProject.PROJECT_DIR +  '/' + DbProject.PROPS_FILE);
        assertNotNull(fo);

        String chap1 = loadGoldenFile ("Chapter1.xml");
        String chap2 = loadGoldenFile ("Chapter2.xml");
        String project = loadGoldenFile ("TestProject.xml");

        assertEqualss (chap1, loadFile(projDir.getFileObject("Chapter1/Chapter1.xml")));
        assertEqualss (chap2, loadFile(projDir.getFileObject("Chapter2/Chapter2.xml")));
        assertEqualss (project, loadFile(projDir.getFileObject("TestProject.xml")));
        projDir.delete();
    }

    @Test
    public void testCreateProjectFiles() throws IOException {
        File tmp = new File (System.getProperty("java.io.tmpdir"));
        assertTrue (tmp.exists());
        assertTrue (tmp.isDirectory());
        FileObject tmpDir = FileUtil.toFileObject(FileUtil.normalizeFile(tmp));
        assertNotNull (tmpDir);
        String pDirName = FileUtil.findFreeFolderName(tmpDir, "dbprj" + System.currentTimeMillis());
        FileObject projDir = tmpDir.createFolder(pDirName);
        System.err.println("PROJECT IN " + projDir.getPath());
        String bookOutline = "Chapter 1\n  Section 1\n    SubSection 1a\n  Section 2\nChapter 2\n";
        Info info = new Info ("Test Project", "A test of a project", "First Last");
        ProjectKind.Book.createProject("Test Project", projDir, info, new Outline(bookOutline), ChapterGenerationStyle.FILE_PER_CHAPTER);
        DbProjectFactory f = Lookup.getDefault().lookup(DbProjectFactory.class);
        assertNotNull (f);
        assertTrue (f.isProject(projDir));
        FileObject fo = projDir.getFileObject("Chapter1.xml");
        assertNotNull(fo);
        fo = projDir.getFileObject("Chapter2.xml");
        assertNotNull(fo);
        fo = projDir.getFileObject("TestProject.xml");
        assertNotNull(fo);
        fo = projDir.getFileObject(DbProject.PROJECT_DIR);
        assertNotNull(fo);
        fo = projDir.getFileObject(DbProject.PROJECT_DIR +  '/' + DbProject.PROPS_FILE);
        assertNotNull(fo);

        String chap1 = loadGoldenFile ("Chapter1.xml");
        String chap2 = loadGoldenFile ("Chapter2.xml");
        String project = loadGoldenFile ("TestProject.xml");

        assertEqualss (chap1, loadFile(projDir.getFileObject("Chapter1.xml")));
        assertEqualss (chap2, loadFile(projDir.getFileObject("Chapter2.xml")));
        assertEqualss (project, loadFile(projDir.getFileObject("TestProject.xml")));
        projDir.delete();
    }

    @Test
    public void testCreateProjectInline() throws IOException {
        File tmp = new File (System.getProperty("java.io.tmpdir"));
        assertTrue (tmp.exists());
        assertTrue (tmp.isDirectory());
        FileObject tmpDir = FileUtil.toFileObject(FileUtil.normalizeFile(tmp));
        assertNotNull (tmpDir);
        String pDirName = FileUtil.findFreeFolderName(tmpDir, "dbprj" + System.currentTimeMillis());
        FileObject projDir = tmpDir.createFolder(pDirName);
        System.err.println("PROJECT IN " + projDir.getPath());
        String bookOutline = "Chapter 1\n  Section 1\n    SubSection 1a\n  Section 2\nChapter 2\n";
        Info info = new Info ("Test Project", "A test of a project", "First Last");
        ProjectKind.Book.createProject("Test Project", projDir, info, new Outline(bookOutline), ChapterGenerationStyle.INLINE);
        DbProjectFactory f = Lookup.getDefault().lookup(DbProjectFactory.class);
        assertNotNull (f);
        assertTrue (f.isProject(projDir));
        FileObject fo = projDir.getFileObject("TestProject.xml");
        assertNotNull(fo);
        fo = projDir.getFileObject(DbProject.PROJECT_DIR);
        assertNotNull(fo);
        fo = projDir.getFileObject(DbProject.PROJECT_DIR +  '/' + DbProject.PROPS_FILE);
        assertNotNull(fo);
        String project = loadGoldenFile ("TestProjectInline.xml");
        assertEqualss (project, loadFile(projDir.getFileObject("TestProject.xml")));
        projDir.delete();
    }

    void assertEqualss (String a, String b) {
        String[] l1 = a.split("\n");
        String[] l2 = b.split("\n");
        for (int i=0; i < Math.min (l1.length, l2.length); i++) {
            //Trim to avoid CRLF issues on Windows
            String one = l1[i].trim();
            String two = l2[i].trim();
            assertEquals ("At line " + i + " expected '" + one + "' but got '" + two + "' Full text: '" + a + "'\n GOT: '" + b, one, two);
        }
    }

    private static String loadFile (FileObject fo) throws IOException {
        return fo.asText("UTF-8");
    }

    private static String loadGoldenFile (String fname) throws IOException {
        InputStream in = OutlineTest.class.getResourceAsStream(fname);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileUtil.copy (in, out);
        in.close();
        out.close();
        return new String (out.toByteArray(), "UTF-8");
    }

}
