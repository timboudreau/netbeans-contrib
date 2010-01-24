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

import java.io.BufferedOutputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.docbook.project.DbProject;
import org.netbeans.modules.docbook.project.wizard.Outline.Item;
import org.netbeans.modules.docbook.project.wizard.Outline.TagProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.xml.XMLUtil;

/**
 *
 * @author Tim Boudreau
 */
enum ProjectKind {
    //annoyingly, you can't refer to constants in the same file in an enum
    //declaration
    Article (WizardIt.ARTICLE_HEADER, WizardIt.ARTICLE_HEAD, WizardIt.ARTICLE_FOOTER),
    Slides(WizardIt.SLIDES_HEADER, WizardIt.SLIDES_HEAD, WizardIt.SLIDES_FOOTER),
    Book(WizardIt.BOOK_HEADER, WizardIt.BOOK_HEAD, WizardIt.BOOK_FOOTER),
    ;
    private final String header;
    private final String contentHead;
    private final String footer;

    private ProjectKind(String header, String contentHead, String footer) {
        this.header = header;
        this.contentHead = contentHead;
        this.footer = footer;
    }

    String infoTag() {
        switch (this) {
            case Article :
                return "articleinfo";
            case Book :
                return "bookinfo";
            case Slides:
                return "slidesinfo";
            default : throw new AssertionError();
        }
    }

    Outline defaultOutline() {
        switch (this) {
            case Article :
                return new Outline(NbBundle.getMessage(ProjectKind.class, 
                        "ARTICLE_DEFAULT_OUTLINE")); //NOI18N
            case Book :
                return new Outline(NbBundle.getMessage(ProjectKind.class, 
                        "BOOK_DEFAULT_OUTLINE")); //NOI18N
            case Slides :
                return new Outline(NbBundle.getMessage(ProjectKind.class, 
                        "SLIDES_DEFAULT_OUTLINE")); //NOI18N
            default :
                throw new AssertionError();
        }
    }

    TagProvider createTagProvider() {
        switch (this) {
            case Article :
                return new SectionTagProvider();
            case Book :
                return new BookTagProvider();
            case Slides :
                return new TagProvider() {

                    public String getTag(int depth) {
                        return depth == 0 ? "foil" : "section"; //NOI18N
                    }

                    public String getPlaceholderText(int depth) {
                        return getPlaceholderString();
                    }

                    public boolean skip(Item item, int depth) {
                        return false;
                    }

                };
            default :
                throw new AssertionError();
        }
    }
    
    private static final class BookTagProvider implements TagProvider {
        public String getTag(int depth) {
            return depth < 1 ? "chapter" : "section"; //NOI18N
        }

        public String getPlaceholderText(int depth) {
            return getPlaceholderString();
        }

        public boolean skip(Item item, int depth) {
            boolean result = depth < 1 ? true : false;
            return result;
        }
    }

    private static class SectionTagProvider implements TagProvider {
        public String getTag(int depth) {
            return "section"; //NOI18N
        }

        public String getPlaceholderText(int depth) {
            return getPlaceholderString();
        }

        public boolean skip(Item item, int depth) {
            return false;
        }
    }

    private static final class NoSkipChapterTagProvider extends SectionTagProvider {
        @Override
        public String getTag(int depth) {
            return depth == 0 ? "chapter" : "section"; //NOI18N
        }
    }

    private static String getPlaceholderString() {
        return NbBundle.getMessage(ProjectKind.class, "CONTENT_PLACEHOLDER"); //NOI18N
    }

    List<FileObject> createProject (String projectName, FileObject projectDir, Info info, Outline content, boolean useChapterDirs) throws IOException {
        assert projectDir != null;
        assert projectName != null;
        content = content == null ? defaultOutline() : content;
        StringBuilder bodyContent = new StringBuilder();
        StringBuilder headerContent = new StringBuilder();
        String fileName = toFilename(projectName);
        List<Item> separateFiles = content.toXml(bodyContent, createTagProvider(), 1);
        List<FileObject> result = new LinkedList<FileObject>();
        for (Item chapter : separateFiles) {
//            System.err.println("CHAPTER " + chapter.title);
            String chapterFileName = toFilename(chapter.title);
            headerContent.append ("<!ENTITY " + chapterFileName + " SYSTEM \"" + //NOI18N
                    chapterFileName + '/' + chapterFileName + ".xml\">\n"); //NOI18N
            StringBuilder chapterBody = new StringBuilder();
            chapterBody.append (WizardIt.CHAPTER_HEADER);
            chapterBody.append('\n');
            chapter.toXml(chapterBody, 0, new NoSkipChapterTagProvider(), 0);
            String fname = FileUtil.findFreeFolderName(projectDir, chapterFileName);
            FileObject chapterFolder = useChapterDirs ? projectDir.createFolder(fname) : projectDir;
            FileObject chapterFile = chapterFolder.createData(fname, "xml"); //NOI18N
            OutputStream out = new BufferedOutputStream(chapterFile.getOutputStream());
            PrintWriter w = new PrintWriter(out);
            try {
                w.println(chapterBody);
            } finally {
                w.flush();
                w.close();
                out.close();
            }
            result.add (chapterFile);
//            System.err.println("WROTE TO " + chapterFile.getPath() + "\n" + chapterBody);
        }
        StringBuilder mainFileContent = new StringBuilder();
        mainFileContent.append (header);
        mainFileContent.append('\n'); //NOI18N
        mainFileContent.append (headerContent);
        mainFileContent.append('\n'); //NOI18N
        mainFileContent.append (contentHead);
        mainFileContent.append('\n'); //NOI18N
        mainFileContent.append (info.toXml(infoTag()));
        mainFileContent.append(bodyContent);
        mainFileContent.append('\n'); //NOI18N
        mainFileContent.append(footer);
        FileObject mainFile = projectDir.createData(fileName, "xml"); //NOI18N
        OutputStream out = new BufferedOutputStream(mainFile.getOutputStream());
        PrintWriter w = new PrintWriter(out);
        try {
            w.println(mainFileContent);
        } finally {
            w.flush();
            w.close();
            out.close();
        }
//        System.err.println("WROTE TO " + mainFile.getPath() + "\n" + mainFileContent);
        result.add (mainFile);
        FileObject metaDir = projectDir.createFolder (DbProject.PROJECT_DIR);
        FileObject propsFile = metaDir.createData (DbProject.PROPS_FILE);
        Properties p = new Properties();
        p.setProperty(DbProject.MAIN_FILE_KEY, mainFile.getNameExt());
        out = new BufferedOutputStream(propsFile.getOutputStream());
        try {
            p.store(out, "NetBeans Docbook Project Metadata"); //NOI18N
        } finally {
            out.close();
        }
//        System.err.println("WROTE TO " + propsFile.getPath() + "\n" + p);
        return result;
    }
    
    static String toId(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }
            if ('"' == c || //NOI18N
                '\'' == c ||  //NOI18N
                '<' == c || //NOI18N
                '>' == c || //NOI18N
                '&' == c || //NOI18N
                '"' == c || //NOI18N
                '\'' == c || //NOI18N
                ':' == c || //NOI18N
                ';' == c || //NOI18N
                '$' == c || //NOI18N
                ',' == c || //NOI18N
                '.' == c|| //NOI18N
                '/' == c|| //NOI18N
                '\\' == c) continue;//NOI18N
            sb.append(Character.toLowerCase(c));
        }
        try {
            s = XMLUtil.toAttributeValue(sb.toString());
        } catch (CharConversionException ex) {
            Logger.getLogger(ProjectKind.class.getName()).log(Level.INFO, null,
                    ex);
        }
        return s;
    }

    static String toFilename (String title) {
        StringBuilder sb = new StringBuilder();
        boolean lastWasWhitespace = true;
        for (char c : title.toCharArray()) {
            if (sb.length() > 16) {
                break;
            }
            boolean whitespace = Character.isWhitespace(c);
            if (!whitespace && lastWasWhitespace) {
                c = Character.toUpperCase(c);
            } else if (!whitespace) {
                c = Character.toLowerCase(c);
            }
            lastWasWhitespace = whitespace;
            if (whitespace) {
                continue;
            }
            if (!Character.isLetter(c) && !Character.isDigit(c)) {
                continue;
            }
            sb.append (c);
        }
        if (sb.length() == 0) {
            sb.append(title.toLowerCase().replaceAll("\\w", "_")); //NOI18N
        }
        return sb.toString();
    }
}
