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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.docbook.project.wizard;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class WizardIt implements WizardDescriptor.InstantiatingIterator, ChangeListener {

    private static final long serialVersionUID = 1L;

    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wiz;

    public WizardIt () {}

    public static WizardIt createIterator() {
        return new WizardIt();
    }

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[] {
            new ProjectInfoPanelPanel(),
        };
    }

    private String[] createSteps() {
        return new String[] {
            NbBundle.getMessage(WizardIt.class, "LBL_CreateProjectStep")
        };
    }

    public Set/*<FileObject>*/ instantiate() throws IOException {
        Set resultSet = new LinkedHashSet();
        String dirName = (String) wiz.getProperty("location"); //NOI18N
        String projName = (String) wiz.getProperty("projectName"); //NOI18N
        String projCodeName = codeNamify (projName);
        String projKind = (String) wiz.getProperty("kind"); //NOI18N
        if (projName == null || projName.trim().length() == 0) {
            throw new IOException ("Name not specified");
        }
        if (projKind == null) {
            throw new IOException ("Kind not specified");
        }

        File dirF = FileUtil.normalizeFile(new File(dirName));

        File projDirectory = new File (dirF, projCodeName);
        projDirectory.mkdirs();

        FileObject template = Templates.getTemplate(wiz);
        FileObject dir = FileUtil.toFileObject(projDirectory);
        dir.getFileSystem().runAtomicAction(new FSWriter(dir, projName, projCodeName, projKind, resultSet));
        File parent = dirF.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }

        return resultSet;
    }

    private class FSWriter implements FileSystem.AtomicAction {
        private final Set resultSet;
        private final String codeName;
        private final String projName;
        private final FileObject dir;
        private final String projKind;
        FSWriter (FileObject dir, String projName, String codeName, String projKind, Set resultSet) {
            this.dir = dir;
            this.projName = projName;
            this.codeName = codeName;
            this.projKind = projKind;
            this.resultSet = resultSet;
        }

        public void run() throws IOException {
            String mainFileName = codeName + ".xml";
            FileObject projDir = dir.createFolder ("dbproject"); //NOI18N
            FileObject metadata = projDir.createData("project", "properties"); //NOI18N

            FileObject mainFile = dir.createData(mainFileName);
            writeMainFile (mainFile, projKind);
            writeMetadata (metadata, mainFile);

            // Always open top dir as a project:
            resultSet.add(dir);
            resultSet.add (mainFile);
            // Look for nested projects to open as well:
            Enumeration e = dir.getFolders(true);
            while (e.hasMoreElements()) {
                FileObject subfolder = (FileObject) e.nextElement();
                if (ProjectManager.getDefault().isProject(subfolder)) {
                    resultSet.add(subfolder);
                }
            }

        }
    }

    private void writeMetadata (FileObject metadata, FileObject mainFile) throws IOException {
        String s = "main.file=" + mainFile.getNameExt() + "\n";
        FileLock lock = metadata.lock();
        OutputStream os = metadata.getOutputStream(lock);
        PrintWriter pw = new PrintWriter (os);
        try {
            pw.println (s);
        } finally {
            pw.close();
            os.close();
            lock.releaseLock();
        }
    }

    private static final String BOOK_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">\n";
    private static final String SLIDES_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE slides PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">\n";
    private static final String ARTICLE_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">\n";

    private void writeMainFile (FileObject file, String kind) throws IOException {
        FileLock lock = file.lock();
        OutputStream os = file.getOutputStream(lock);
        PrintWriter pw = new PrintWriter (os);
        try {
            String header;
            String content;
            if ("Article".equals(kind)) {
                header = ARTICLE_HEADER;
                content = articleContent();
            } else if ("Slides".equals(kind)) {
                header = SLIDES_HEADER;
                content = slidesContent();
            } else { //if ("Book".equals(kind)) {
                header = BOOK_HEADER;
                content = bookContent();
            }
            pw.println(header);
            pw.println(content);
        } finally {
            pw.close();
            lock.releaseLock();
            os.close();
        }
    }

    private static final String BOOK_CONTENT =
        "<book>\n" +
        "    <bookinfo>\n" +
        "        <title>$TITLE</title>\n" +
        "        <subtitle>$SUBTITLE</subtitle>\n" +
        "        <author>\n" +
        "            <personname>\n" +
        "                <firstname>$FIRSTNAME</firstname>\n" +
        "                <surname>$LASTNAME</surname>\n" +
        "            </personname>\n" +
        "        </author>\n" +
        "    </bookinfo>\n" +
        "    <chapter id=\"chapter one\">\n" +
        "        <title>$CHAPTERTITLE</title>\n" +
        "        <para>\n" +
        "            $CHAPTERCONTENT\n" +
        "        </para>\n" +
        "        <section id=\"section-one\">\n" +
        "            <para>\n" +
        "                $CHAPTERCONTENT\n" +
        "            </para>\n" +
        "        </section>\n" +
        "    </chapter>\n" +
        "    &chapterOne;\n" +
        "</book>\n";

    private static final String ARTICLE_CONTENT =
        "<article>\n" +
        "    <articleinfo>\n" +
        "        <title>$TITLE</title>\n" +
        "        <subtitle>$SUBTITLE</subtitle>\n" +
        "        <author>\n" +
        "            <personname>\n" +
        "                <firstname>$FIRSTNAME</firstname>\n" +
        "                <surname>$LASTNAME</surname>\n" +
        "            </personname>\n" +
        "        </author>\n" +
        "    </articleinfo>\n" +
        "    <section>\n" +
        "        <title>$sectionTITLE</title>\n" +
        "        <para>\n" +
        "            $sectionCONTENT\n" +
        "        </para>\n" +
        "    </section>\n" +
        "    &sectionOne;\n" +
        "</article>\n";

    private static final String SLIDES_CONTENT = 
        "<slides>\n" +
        "    <slidesinfo>\n" +
        "        <title>$TITLE</title>\n" +
        "        <subtitle>$SUBTITLE</subtitle>\n" +
        "        <date>$DATE</date>\n" +
        "        <author>\n" +
        "            <firstname>$FIRSTNAME</firstname>\n" +
        "            <surname>$LASTNAME</surname>\n" +
        "        </author>\n" +
        "        <copyright>\n" +
        "            <year>$YEAR</year>\n" +
        "            <holder>$FIRSTNAME $LASTNAME</holder>\n" +
        "        </copyright>\n" +
        "        <abstract>\n" +
        "            <para>\n" +
        "                $CHAPTERCONTENT\n" +
        "            </para>\n" +
        "        </abstract>\n" +
        "    </slidesinfo>\n" +
        "    <foil>\n" +
        "        <title>$CHAPTERTITLE</title>\n" +
        "        <para>\n" +
        "            $CHAPTERCONTENT\n" +
        "        </para>\n" +
        "    </foil>\n" +
        "</slides>\n";            

    private String articleContent() {
        StringBuilder b = new StringBuilder();
        b.append(ARTICLE_CONTENT);
        return doSubstitutions (b);
    }

    private String doSubstitutions (StringBuilder b) {
        String title = (String) wiz.getProperty ("title"); //NOI18N
        title = title == null ? "My Article" : title;
        subst ("$TITLE", title, b); //NOI18N
        String subtitle = (String) wiz.getProperty ("subtitle"); //NOI18N
        subtitle = subtitle == null ? "" : subtitle;
        subst ("$SUBTITLE", subtitle, b); //NOI18N
        String author = (String) wiz.getProperty ("author"); //NOI18N
        author = author == null || "".equals (author.trim())
                ? System.getProperty ("user.name") : author; //NOI18N
        author = author == null || "".equals(author.trim()) ? "No One" : author;
        String[] names = author.split(" "); //NOI18N
        subst ("$LASTNAME", names[names.length - 1], b); //NOI18N
        StringBuilder first = new StringBuilder();
        for (int i = 0; i < names.length - 1; i++) {
            first.append (names[i]);
            if (i != names.length - 1) {
                first.append (' ');
            }
        }
        subst ("$FIRSTNAME", first.toString(), b); //NOI18N
        subst ("$CHAPTERTITLE", "Chapter One", b);
        subst ("$CHAPTERCONTENT", "[add your content here]", b);
        String year = Integer.toString (new Date().getYear() + 1900);
        String date = new Date().toLocaleString();
        subst ("$YEAR", year, b);
        subst ("$DATE", date, b);
        return b.toString();
    }

    private void subst (String key, String value, StringBuilder b) {
        int ix = b.indexOf(key);
        while (ix > 0) {
            if (ix >= 0) {
                b.replace(ix, ix + key.length(), value);
            }
            ix = b.indexOf(key);
        }
    }

    private String slidesContent() {
        StringBuilder b = new StringBuilder();
        b.append (SLIDES_CONTENT);
        return doSubstitutions(b);
    }

    private String bookContent() {
        StringBuilder b = new StringBuilder();
        b.append(BOOK_CONTENT);
        return doSubstitutions (b);
    }

    private String codeNamify (String name) {
        StringBuilder b = new StringBuilder (name.toLowerCase());
        for (int i=0; i < b.length(); i++) {
            if (Character.isWhitespace(b.charAt(i))) {
                b.setCharAt(i, '_');
            }
        }
        return b.toString();
    }

    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
        wiz.setValid(false);
    }

    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir",null);
        this.wiz.putProperty("name",null);
        this.wiz = null;
        panels = null;
    }

    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[] {new Integer(index + 1), new Integer(panels.length)});
    }

    public boolean hasNext() {
        return index < panels.length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    List <ChangeListener> listeners = Collections.synchronizedList(new LinkedList<ChangeListener>());

    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {
        listeners.add (l);
    }

    public final void removeChangeListener(ChangeListener l) {
        listeners.remove (l);
    }

    private void fire() {
        ChangeListener[] l =
                (ChangeListener[]) listeners.toArray (new ChangeListener[0]);
        for (int i = 0; i < l.length; i++) {
            l[i].stateChanged (new ChangeEvent(this));
        }
    }


    public void stateChanged (ChangeEvent ce) {
        wiz.setValid(((ProjectInfoPanelPanel) panels[0]).isValid());
        fire();
    }

}
