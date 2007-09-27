/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.


The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s): */
package org.netbeans.modules.htmlproject;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 *
 * @author Tim Boudreau
 */
public class HtmlProject implements Project, ProjectInformation, LogicalViewProvider, ActionProvider, AuxiliaryConfiguration, CustomizerProvider {
    private final FileObject dir;
    private ProjectState state;
    private Lookup lkp;
    private String parentProjectName = null;
    private String parentProjectDisplayName = null;

    public HtmlProject(FileObject dir, ProjectState state) {
        this.dir = dir;
        this.state = state;
    }

    public FileObject getProjectDirectory() {
        return dir;
    }

    public Lookup getLookup() {
        if (lkp == null) {
            lkp = createLookup();
        }
        return lkp;
    }

    private Lookup createLookup() {
        return Lookups.fixed(new Object[] {
            this,
        });
    }

    private static final String PROP_NAME = "name"; //NOI18N
    private String name = null;
    public String getName() {
        if (parentProjectName == null) {
            findParentProject();
        }
        if (!"".equals(parentProjectName)) {
            return parentProjectName + ".website";
        } else {
            return dir.getName();
        }
    }

    private void findParentProject() {
        FileObject f = dir.getParent();
        if (f != null) {
            Project p = FileOwnerQuery.getOwner(f);
            if (p != null) {
                ProjectInformation pi = (ProjectInformation)
                    p.getLookup().lookup(ProjectInformation.class);
                if (pi != null) {
                    parentProjectName = pi.getName();
                    parentProjectDisplayName = pi.getDisplayName();
                }
            }
        }
    }

    public void setDisplayName (String s) throws IOException {
        String old = getDisplayName();
        if (!old.equals(s)) {
            HtmlProjectFactory.putHtmlProjectName(dir, s);
            fire (PROP_DISPLAY_NAME, old, s);
        }
    }

    String getMainFilePath() {
        File f = getMainFile(false);
        if (f != null) {
            return f.getPath();
        } else {
            return "";
        }
    }

    public String getDisplayName() {
        String result = HtmlProjectFactory.getHtmlProjectName(dir);
        if (result == null) {
            if (parentProjectDisplayName == null) {
                findParentProject();
            }
            if (parentProjectDisplayName != null && parentProjectDisplayName.length() > 0) {
                result = parentProjectDisplayName + " Web Site";
            } else {
                result = dir.getName();
                if (result.equals("www")) {
                    // XXX look up title of any index.html and use that instead?
                }
            }
        }
        return result;
    }

    public Icon getIcon() {
        return new ImageIcon (Utilities.loadImage (
                "org/netbeans/modules/htmlproject/htmlProject.png"));
    }

    public Project getProject() {
        return this;
    }

    private HtmlLogicalView logicalView = null;
    public Node createLogicalView() {
        if (logicalView == null) {
            Lookup lkp = null;
            try {
                lkp = DataObject.find(dir).getNodeDelegate().getLookup();
            } catch (DataObjectNotFoundException ex) {
                ErrorManager.getDefault().notify (ex);
            }
            logicalView = new HtmlLogicalView (this, lkp == null ? //should never be null
                Lookups.fixed(new Object[0]) : lkp);
        }
        return logicalView;
    }

    public Node findPath(Node node, Object object) {
        HtmlLogicalView.Locator loc = (HtmlLogicalView.Locator) 
            node.getLookup().lookup (HtmlLogicalView.Locator.class);
        if (loc == null) {
            System.err.println("Bad.");
            return null;
        } else {
            return loc.locate(node, object);
        }
    }

    private List pcls = Collections.synchronizedList (new LinkedList());
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcls.add (pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcls.remove (pcl);
    }

    private void fire(String name, Object old, Object nue) {
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) this.pcls.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent (this, name, old, nue));
        }
    }

    public String[] getSupportedActions() {
        return new String[0];
    }

    RequestProcessor.Task task = null;
    public void invokeAction(String string, Lookup lookup) throws IllegalArgumentException {
        if (ActionProvider.COMMAND_BUILD.equals(string)) {
            synchronized(this) {
                if (task != null) {
                    StatusDisplayer.getDefault().setStatusText("Already building" +
                            getDisplayName());
                } else {
                    task = RequestProcessor.getDefault().post (new Runnable() {
                        public void run() {
                            try {
                                zipProject();
                            } catch (IOException ioe) {
                                ErrorManager.getDefault().notify (ErrorManager.USER,
                                         ioe);
                            } finally {
                                synchronized (HtmlProject.this) {
                                    task = null;
                                }
                            }
                        }
                    });
                }
            }
        } else if (ActionProvider.COMMAND_RUN.equals(string)) {
            runProject();
        } else if (ActionProvider.COMMAND_CLEAN.equals(string)) {
            try {
                File f = getZipFile(false);
                if (f != null && f.exists()) {
                    if (!f.delete()) {
                        StatusDisplayer.getDefault().setStatusText("Could not " +
                                "delete " + f.getPath());
                    } else {
                        StatusDisplayer.getDefault().setStatusText("Deleted " +
                                f.getPath());
                    }
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify (ErrorManager.USER, ioe);
            }
        } else {
            throw new IllegalArgumentException (string);
        }
    }

    public boolean isActionEnabled(String string, Lookup lookup) throws IllegalArgumentException {
        return ActionProvider.COMMAND_BUILD.equals(string) ||
               ActionProvider.COMMAND_RUN.equals(string) ||
               ActionProvider.COMMAND_CLEAN.equals(string);
    }

    File getZipFile(boolean create) throws IOException {
        File dir = getZipFileDir(true);
        if (dir == null) {
            StatusDisplayer.getDefault().setStatusText("No zip file specified");
        }
        File f = new File (dir, getDisplayName() + ".zip");
        if (!f.exists() && !f.createNewFile()) {
            f = new File (dir, this.dir.getName() + ".zip");
            if (!f.exists() && !f.createNewFile()) {
                StatusDisplayer.getDefault().setStatusText("Cannot create file " +
                        f.getPath());
                return null;
            }
        }
        if (!f.canWrite()) {
            throw new IOException (dir.getName() + " is not writable");
        }
        return f;
    }

    private void zipProject() throws IOException {
        File f = getZipFile(true);
        StatusDisplayer.getDefault().setStatusText("Zipping project to " +
                f.getPath());
        ZipOutputStream zos = new ZipOutputStream (
            new BufferedOutputStream (new FileOutputStream (f)));
        try {
            zos.setLevel(9);
            File toZip = FileUtil.toFile (this.dir);
            assert toZip.isDirectory();
            zip (toZip, zos);
        } catch (IOException ioe) {
            StatusDisplayer.getDefault().setStatusText("Build failed: " +
                    ioe.getMessage());
            throw ioe;
        } finally {
            zos.close();
        }

        StatusDisplayer.getDefault().setStatusText("Finished zipping project to " +
                f.getPath());
    }

    private void zip (File toZip, ZipOutputStream zos) throws IOException {
        if (toZip.isDirectory()) {
            File[] f = toZip.listFiles();
            for (int i = 0; i < f.length; i++) {
                zip (f[i], zos);
            }
        } else {
            String name = getEntryPath (toZip);
            ZipEntry entry = new ZipEntry (name);
            zos.putNextEntry(entry);
            InputStream in = new BufferedInputStream (
                    new FileInputStream(toZip));
            try {
                FileUtil.copy(in, zos);
            } finally {
                zos.closeEntry();
                in.close();
            }
        }
    }

    private String getEntryPath(File toZip) {
        String s = FileUtil.toFile(dir).getPath();
        String result = toZip.getPath();
        assert result.startsWith (s);
        result = result.substring (s.length(), result.length());
        result = Utilities.replaceString(result, "\\", "/"); //NOI18N
        result = getDisplayName() + result;
        return result;
    }

    private void runProject() {
        File f = getMainFile(true);
        if (f != null) {
            URL url;
            try {
                url = f.toURI().toURL();
                URLDisplayer.getDefault().showURL(url);
            } catch (MalformedURLException ex) {
                ErrorManager.getDefault().notify (ex);
            }
        }
    }

    private File getMainFile(boolean ask) {
        String path = HtmlProjectFactory.getProjectMainFile(dir);
        if (path == null && ask) {
            JFileChooser ch = new JFileChooser();
            ch.setDialogType(JFileChooser.CUSTOM_DIALOG);
            ch.setCurrentDirectory(FileUtil.toFile (dir));
            ch.setMultiSelectionEnabled(false);
            ch.setDialogTitle("Choose a File to View on Run");
            ch.setApproveButtonText("Select");
            ch.setFileSelectionMode(JFileChooser.FILES_ONLY);
            ch.setFileHidingEnabled(true);
            ch.setFileFilter(new FF());
            if (ch.showOpenDialog(null) == JFileChooser.APPROVE_OPTION &&
                    ch.getSelectedFile().exists() && ch.getSelectedFile().canWrite()) {
                path = FileUtil.normalizeFile(ch.getSelectedFile()).getPath();
                try {
                    HtmlProjectFactory.putHtmlMainFile(dir, path);
                } catch (IOException x) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, x);
                }
            } else {
                StatusDisplayer.getDefault().setStatusText("No main file set ");
                Toolkit.getDefaultToolkit().beep();
                return null;
            }
        }
        return path == null ? null : new File (path);
    }

    static class FF extends FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else {
                String s = f.getName().toLowerCase();
                return s.endsWith(".html") || s.endsWith (".htm") ||  //NOI18N
                        s.endsWith(".jsp"); //NOI18N
            }
        }

        public String getDescription() {
            return "HTML Files";
        }
    }

    File getZipFileDir(boolean create) {
        String s = HtmlProjectFactory.getZipDestDir(dir);
        if (s == null && create) {
            File f = FileUtil.toFile(dir);
            f = f.getParentFile();
            JFileChooser ch = new JFileChooser();
            ch.setDialogType(JFileChooser.CUSTOM_DIALOG);
            ch.setCurrentDirectory(f);
            ch.setMultiSelectionEnabled(false);
            ch.setDialogTitle("Choose a Destination Directory for the ZIP File"); // XXX I18N
            ch.setApproveButtonText("Select");
            ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            ch.setFileHidingEnabled(true);
            if (ch.showOpenDialog(null) == JFileChooser.APPROVE_OPTION &&
                    ch.getSelectedFile().exists() && ch.getSelectedFile().isDirectory()) {
                s = ch.getSelectedFile().getPath();
                try {
                    HtmlProjectFactory.putHtmlZipDestDir(dir, s);
                } catch (IOException x) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, x);
                }
            } else {
                StatusDisplayer.getDefault().setStatusText("Cannot write to " + // XXX I18N
                        ch.getSelectedFile().getName());
                Toolkit.getDefaultToolkit().beep();
                return null;
            }
        } else if (s == null) {
            return null;
        }
        return new File (s);
    }

    public void showCustomizer() {
        ProjectPropertiesDlg.showDialog(this);
    }

    public Element getConfigurationFragment(String elementName, String namespace, boolean shared) {
        String s = (String) dir.getAttribute(namespace + "#" + elementName);
        if (s != null) {
            try {
                return XMLUtil.parse(new InputSource(new StringReader(s)), false, true, null, null).getDocumentElement();
            } catch (Exception x) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, x);
            }
        }
        return null;
    }

    public void putConfigurationFragment(Element fragment, boolean shared) throws IllegalArgumentException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            doc.appendChild(doc.importNode(fragment, true));
            XMLUtil.write(doc, baos, "UTF-8");
            dir.setAttribute(fragment.getNamespaceURI() + "#" + fragment.getLocalName(), baos.toString("UTF-8"));
        } catch (Exception x) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, x);
        }
    }

    public boolean removeConfigurationFragment(String elementName, String namespace, boolean shared) throws IllegalArgumentException {
        String k = namespace + "#" + elementName;
        if (dir.getAttribute(k) != null) {
            try {
                dir.setAttribute(k, null);
            } catch (IOException x) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, x);
                return true;
            }
        }
        return false;
    }
}
