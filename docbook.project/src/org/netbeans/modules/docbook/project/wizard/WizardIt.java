/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

public class WizardIt implements WizardDescriptor.InstantiatingIterator<Set<FileObject>>, ChangeListener {
    static final String BOOK_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //NOI18N
        "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" " + //NOI18N
        "\"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">\n"; //NOI18N
    static final String SLIDES_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //NOI18N
        "<!DOCTYPE slides PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" " + //NOI18N
        "\"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">\n"; //NOI18N
    static final String ARTICLE_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //NOI18N
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" " + //NOI18N
        "\"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">\n"; //NOI18N
    static final String CHAPTER_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //NOI18N
        "<!DOCTYPE chapter PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" " + //NOI18N
        "\"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">\n"; //NOI18N
    static final String BOOK_HEAD =
        "<book>\n"; //NOI18N

    static final String BOOK_FOOTER =
        "</book>\n"; //NOI18N

    static final String ARTICLE_HEAD =
        "<article>\n"; //NOI18N

    static final String ARTICLE_FOOTER =
        "</article>\n"; //NOI18N

    static final String SLIDES_HEAD =
        "<slides>\n"; //NOI18N

    static final String SLIDES_FOOTER =
        "</slides>\n"; //NOI18N
    private static final long serialVersionUID = 1L;
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wiz;
    
    public static WizardIt createIterator() {
        return new WizardIt();
    }

    private WizardDescriptor.Panel<?>[] createPanels() {
        return new WizardDescriptor.Panel<?>[] {
            new ProjectInfoPanelPanel(),
            new ProjectOutlinePanelPanel(),
        };
    }

    private String[] createSteps() {
        return new String[] {
            NbBundle.getMessage(WizardIt.class, "LBL_CreateProjectStep"), //NOI18N
            NbBundle.getMessage(WizardIt.class, "LBL_OutlineProjectStep"), //NOI18N
        };
    }

    public Set <FileObject> instantiate() throws IOException {
        String dirName = (String) wiz.getProperty("location"); //NOI18N
        String projName = (String) wiz.getProperty("projectName"); //NOI18N
        ProjectKind projKind = (ProjectKind) wiz.getProperty("kind"); //NOI18N
        String title = (String) wiz.getProperty("title"); //NOI18N
        String author = (String) wiz.getProperty("author"); //NOI18N
        if (projName == null || projName.trim().length() == 0) {
            throw new IOException ("Name not specified"); //NOI18N
        }
        if (projKind == null) {
            throw new IOException ("Kind not specified"); //NOI18N
        }
        if (author == null) {
            throw new IOException ("Author not specified"); //NOI18N
        }
        if (title == null) {
            throw new IOException ("Title not specified"); //NOI18N
        }
        FileObject dirF = FileUtil.toFileObject(FileUtil.normalizeFile(new File(dirName)));
        if (dirF == null) {
            throw new IOException (dirName + " does not exist"); //NOI18N
        }
        String out = (String) wiz.getProperty("outline"); //NOI18N
        boolean split = Boolean.TRUE.equals(wiz.getProperty("split")); //NOI18N
        Outline outline = out == null ? projKind.defaultOutline() : new Outline(out);
        String subtitle = (String) wiz.getProperty("subtitle"); //NOI18N
        Info info = new Info (projName, subtitle, author);
        String projDirName = FileUtil.findFreeFolderName(dirF, ProjectKind.toFilename(projName));
        FileObject projDir = dirF.createFolder (projDirName);
        List<FileObject> fos = projKind.createProject(projName, projDir, info, outline, split);
        fos.add(projDir);
        return new LinkedHashSet<FileObject>(fos);
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
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty("WizardPanel_contentSelectedIndex", i); //NOI18N
                jc.putClientProperty("WizardPanel_contentData", steps); //NOI18N
            }
        }
        wiz.setValid(false);
    }

    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir",null); //NOI18N
        this.wiz.putProperty("name",null); //NOI18N
        this.wiz = null;
        panels = null;
    }

    public String name() {
        return MessageFormat.format("{0} of {1}", //NOI18N
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

    private final ChangeSupport supp = new ChangeSupport(this);
    public final void addChangeListener(ChangeListener l) {
        supp.addChangeListener(l);
    }

    public final void removeChangeListener(ChangeListener l) {
        supp.removeChangeListener(l);
    }

    private void fire() {
        supp.fireChange();
    }

    public void stateChanged (ChangeEvent ce) {
        wiz.setValid(((ProjectInfoPanelPanel) panels[0]).isValid());
        fire();
    }
}
