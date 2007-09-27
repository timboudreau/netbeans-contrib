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
package org.netbeans.modules.docbook.project;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.docbook.MainFileProvider;
import org.netbeans.api.docbook.Renderer;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectState;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tim Boudreau
 */
public class DbProject extends Renderer.JobStatus implements Project, ProjectInformation, ActionProvider, MainFileProvider {
    private final FileObject dir;
    private final Lookup lkp;
    private static final String MAIN_FILE_KEY = "main.file";
    private static final String PROJECT_DIR = "dbproject";
    final RequestProcessor rp;
    final Properties props = new Properties();
    public DbProject(FileObject dir, ProjectState state) {
        this.dir = dir;
        lkp = Lookups.fixed (new Object[] {
            this,
            new DbLogicalViewProvider (this),
            state,
            props,
        });
        if (dir.getFileObject(PROJECT_DIR) != null) {
            try {
                loadProperties (props);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        rp = new RequestProcessor ("Resolver thread for "
                + dir.getName() + " Docbook Project", Thread.MIN_PRIORITY,
                true);
    }

    public FileObject getProjectDirectory() {
        return dir;
    }

    public Lookup getLookup() {
        return lkp;
    }

    public String getName() {
        return dir.getName();
    }

    public String getDisplayName() {
        return dir.getName();
    }

    public Icon getIcon() {
        return new ImageIcon (
            Utilities.loadImage(
                "org/netbeans/modules/docbook/project/docbook.png"));
    }

    public Project getProject() {
        return this;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    public String[] getSupportedActions() {
        return new String[] {
            COMMAND_BUILD,
            COMMAND_CLEAN,
        };
    }

    private static final String OUTPUT_FOLDER = "dist";
    private FileObject getOutputFolder() throws IOException {
        FileObject result = dir.getFileObject(OUTPUT_FOLDER);
        if (result == null) {
            result = dir.createFolder(OUTPUT_FOLDER);
            //XXX add dist to cvsignore...
        }
        return result;
    }

    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        if (COMMAND_BUILD.equals(command)) {
            FileObject ob = getMainFile();
            if (ob != null) {
                try {
                    DataObject dob = DataObject.find(ob);
                    Node n = dob.getNodeDelegate();
                    Renderer ren = n.getLookup().lookup (Renderer.class);
                    if (ren != null) {
                        ren.render(FileUtil.toFile(getOutputFolder()), this);
                    } else {
                        StatusDisplayer.getDefault().setStatusText("Could not render " +
                            ob.getPath());
                    }
                } catch (DataObjectNotFoundException ex) {
                    throw new IllegalArgumentException (ex);
                } catch (IOException ex) {
                    throw new IllegalArgumentException (ex);
                }
            } else {
                StatusDisplayer.getDefault().setStatusText("Could not render " +
                        ob.getPath());
            }
        } else if (COMMAND_CLEAN.equals(command)) {
            FileObject ob = dir.getFileObject(OUTPUT_FOLDER);
            if (ob != null) {
                try {
                    ob.delete();
                    StatusDisplayer.getDefault().setStatusText("Cleaned " + getDisplayName());
                } catch (IOException ioe) {
                    throw new IllegalArgumentException (ioe);
                }
            }
        }
    }

    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        return (COMMAND_BUILD.equals(command) && getMainFile() != null) ||
                (COMMAND_CLEAN.equals(command) && dir.getFileObject(OUTPUT_FOLDER)
                != null);
    }

    public boolean isMainFile(FileObject ob) {
        return ob.equals(getMainFile());
    }

    FileObject getMainFile() {
        String path = props.getProperty(MAIN_FILE_KEY);
        if (path == null) {
            return null;
        }
        FileObject result = dir.getFileObject(path);
        return result;
    }

    void setMainFile(FileObject ob) {
        FileObject old = getMainFile();
        if (!ob.equals(old)) {
            String dirPath = dir.getPath();
            String obPath = ob == null ? dirPath : ob.getPath();
            assert ob == null || obPath.startsWith(dirPath);
            String path = ob == null ? "" : 
                obPath.substring(dirPath.length());
            props.setProperty(MAIN_FILE_KEY, path);
            ProjectState state = getLookup().lookup(ProjectState.class);
            state.markModified();
            try {
                if (old != null) {
                    Node n = DataObject.find (old).getNodeDelegate();
                    MainFileProvider.Notifier not1 =
                        n.getLookup().lookup (MainFileProvider.Notifier.class);
                    if (not1 != null) {
                        not1.change();
                    }
                }
                if (ob != null) {
                    Node n1 = DataObject.find (ob).getNodeDelegate();
                    MainFileProvider.Notifier not2 =
                        n1.getLookup().lookup (MainFileProvider.Notifier.class);
                    if (not2 != null) {
                        not2.change();
                    }
                }
                DbLogicalViewProvider prov = getLookup().lookup (
                        DbLogicalViewProvider.class);
                prov.notifyChange();
            } catch (DataObjectNotFoundException donfe) {
                throw new IllegalStateException (donfe);
            }
            assert (ob == null && getMainFile() == null) || 
                    ob.equals(getMainFile());
        }
    }

    void loadProperties (Properties props) throws IOException {
        FileObject ob = dir.getFileObject(PROJECT_DIR);
        if (ob != null) {
            FileObject pob = ob.getFileObject("project", "properties");
            if (pob != null) {
                InputStream is = pob.getInputStream();
                try {
                    props.load(is);
                } finally {
                    is.close();
                }
            }
        }
    }

    void saveProperties (Properties props) throws IOException {
        FileObject ob = dir.getFileObject(PROJECT_DIR);
        FileObject pp;
        if (ob == null) {
            dir.createFolder(PROJECT_DIR);
            pp = ob.createData("project", "properties");
        } else {
            pp = ob.getFileObject("project.properties");
            if (pp == null) {
                pp = ob.createData("project", "properties");
            }
        }
        FileLock lock = pp.lock();
        try {
            OutputStream out = pp.getOutputStream(lock);
            props.store(out, "NetBeans Docbook Project Properties " + new Date());
        } finally {
            lock.releaseLock();
        }
    }

    public void save() throws IOException {
        saveProperties (props);
    }

    static boolean isProject(FileObject projectDirectory) {
        return projectDirectory.getFileObject(PROJECT_DIR) != null;
    }

    Action[] getActions() {
        return new Action[] {
            new GA (COMMAND_BUILD),
            new GA (COMMAND_CLEAN),
        };
    }

    private class GA extends AbstractAction {
        private final String key;
        public GA (String key) {
            this.key = key;
            putValue (NAME, NbBundle.getMessage(DbProject.class, key));
        }

        public boolean isEnabled() {
            return isActionEnabled(key, null);
        }

        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                invokeAction (key, null);
            }
        }
    }
}
