/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.modules.nodejs.NodeProjectSourceNodeFactory.Key;
import org.netbeans.modules.nodejs.NodeProjectSourceNodeFactory.KeyTypes;
import org.netbeans.modules.nodejs.json.SimpleJSONParser;
import org.netbeans.modules.nodejs.json.SimpleJSONParser.JsonException;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbCollections;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 */
public class NodeProjectSourceNodeFactory implements NodeFactory, NodeList<Key>, FileChangeListener {

    private final ChangeSupport supp = new ChangeSupport(this);
    private final Project project;

    private static final String[] builtInNodeLibs = new String[]{"assert", "buffer", "buffer_ieee754", "child_process", "cluster", "console", "constants", "crypto", "dgram", "dns", "events", "freelist", "fs", "http", "https", "module", "net", "os", "path", "punycode", "querystring", "readline", "repl", "stream", "string_decoder", "sys", "timers", "tls", "tty", "url", "util", "vm", "zlib"};
    public static final Pattern CHECK_FOR_REQUIRE = Pattern.compile("require\\s??\\(\\s??['\"](.*?)['\"]\\s??\\)", 40);

    public NodeProjectSourceNodeFactory(Project p) {
        this.project = p;
    }

    public NodeProjectSourceNodeFactory() {
        this.project = null;
    }

    @Override
    public NodeList<?> createNodes(Project p) {
        return new NodeProjectSourceNodeFactory(p);
    }

    public List<Key> keys() {
        List<Key> keys = new ArrayList<Key>();

        FileObject libFolder = project.getProjectDirectory().getFileObject("node_modules");
        VisibilityQuery q = VisibilityQuery.getDefault();
        FileObject[] files = this.project.getProjectDirectory().getChildren();
        Arrays.sort(files, new FOComparator());
        List<FileObject> flds = new LinkedList();
        for (FileObject fo : files) {
            if (fo.equals(libFolder)) {
                continue;
            }
            if (q.isVisible(fo)) {
                if (fo.isData()) {
                    keys.add(new Key(KeyTypes.SOURCE, fo));
                } else if (fo.isFolder()) {
                    if (fo.getName().equals("package.json") || fo.equals(libFolder)) {
                        continue;
                    }
                    flds.add(fo);
                }
            }
        }
        Map<String, List<FileObject>> otherLibs = findOtherModules(this.project.getProjectDirectory());

        if (libFolder != null) {
            List libFolders = new ArrayList();
            for (FileObject lib : libFolder.getChildren()) {
                boolean visible = q.isVisible(lib);
                if ((visible) && (!"node_modules".equals(lib.getName())) && (!"nbproject".equals(lib.getName())) && (lib.isFolder())) {
                    if (otherLibs.containsKey(lib.getName())) {
                        otherLibs.remove(lib.getName());
                    }
                    Key key = new Key(KeyTypes.LIBRARY, lib);
                    key.direct = true;
                    keys.add(key);
                    recurseLibraries(lib, libFolders);
                }
            }
            keys.addAll(libFolders);
        }
        //XXX get this from "npm root"
        File userHomeModules = new File(new File(System.getProperty("user.home")), "node_modules");
        userHomeModules = (userHomeModules.exists()) && (userHomeModules.isDirectory()) ? userHomeModules : null;

        //XXX get this from "npm root -g"
        File libModules = new File("/usr/lib/node_modules");
        libModules = (libModules.exists()) && (libModules.isDirectory()) ? libModules : null;

        String src = DefaultExectable.get().getSourcesLocation();
        File nodeSources = src == null ? null : new File(src);
        File libDir = nodeSources == null ? null : new File(nodeSources, "lib");

        for (String lib : otherLibs.keySet()) {
            if ("./".equals(lib)) {
                continue;
            }
            if (userHomeModules != null) {
                File f = new File(userHomeModules, lib);
                if ((f.exists()) && (f.isDirectory())) {
                    Key key = new Key(KeyTypes.LIBRARY, FileUtil.toFileObject(FileUtil.normalizeFile(f)));
                    key.direct = true;
                    keys.add(key);
                    continue;
                }
            }
            if (libModules != null) {
                File f = new File(libModules, lib);
                if ((f.exists()) && (f.isDirectory())) {
                    Key key = new Key(KeyTypes.LIBRARY, FileUtil.toFileObject(FileUtil.normalizeFile(f)));
                    keys.add(key);
                    continue;
                }
            }
            if (libDir != null) {
                File f = new File(libDir, lib + ".js");
                if ((f.exists()) && (f.isFile()) && (f.canRead())) {
                    Key key = new Key(KeyTypes.LIBRARY, FileUtil.toFileObject(FileUtil.normalizeFile(f)));
                    keys.add(key);
                    continue;
                }
            }
            if (Arrays.binarySearch(builtInNodeLibs, lib) >= 0) {
                Key key = new NodeProjectSourceNodeFactory.Key.BuiltInLibrary(lib);
                keys.add(key);
                key.direct = true;
                continue;
            }
            if (lib.startsWith("./") || lib.startsWith("../")) {
//                FileObject fo = project.getProjectDirectory().getFileObject(lib + ".js");
//                if (fo != null) {
                    continue;
//                }
            }
            Key.MissingLibrary key = new Key.MissingLibrary(lib);
            List<FileObject> referencedBy = otherLibs.get(lib);
            List<String> paths = new LinkedList<String>();
            for (FileObject fo : referencedBy) {
                if (FileUtil.isParentOf(project.getProjectDirectory(), fo)) {
                    paths.add (FileUtil.getRelativePath(project.getProjectDirectory(), fo));
                } else {
                    paths.add(fo.getPath());
                }
            }
            key.references = paths;
            keys.add(key);
        }

        for (FileObject fo : flds) {
            if (fo.getName().equals("node_modules")) {
                continue;
            }
            keys.add(new Key(KeyTypes.SOURCE, fo));
        }
        return keys;
    }

    private void recurseLibraries(FileObject libFolder, List<Key> keys) {
        FileObject libs = libFolder.getFileObject("node_modules");
        if (libs != null) {
            for (FileObject fo : libFolder.getChildren()) {
                for (FileObject lib : fo.getChildren()) {
                    if ((!"node_modules".equals(lib.getName())) && (!"nbproject".equals(lib.getName())) && (lib.isFolder())) {
                        boolean jsFound = false;
                        for (FileObject kid : lib.getChildren()) {
                            jsFound = "js".equals(kid.getExt());
                            if (jsFound) {
                                break;
                            }
                        }
                        if (jsFound) {
                            Key key = new Key(jsFound ? KeyTypes.LIBRARY : KeyTypes.SOURCE, lib);
                            key.direct = false;
                            keys.add(key);
                            recurseLibraries(lib, keys);
                        }
                    }
                }
            }
        }
    }

    private Map<String, List<FileObject>> findOtherModules(FileObject fld) {
        Map<String, List<FileObject>> libs = new HashMap<String,List<FileObject>>();
        assert (!EventQueue.isDispatchThread());
        for (FileObject fo : NbCollections.iterable(fld.getChildren(true))) {
            if (("js".equals(fo.getExt())) && (fo.isData()) && (fo.canRead())) {
                checkForLibraries(fo, libs);
            }
        }
        return libs;
    }

    private void checkForLibraries(FileObject jsFile,Map<String, List<FileObject>> all) {
        try {
            String text = jsFile.asText();
            Matcher m = CHECK_FOR_REQUIRE.matcher(text);
            while (m.find()) {
//                all.add(m.group(1));
                List<FileObject> l = all.get(m.group(1));
                if (l == null) {
                    l = new LinkedList<FileObject>();
                    all.put(m.group(1), l);
                }
                l.add(jsFile);
            }
        } catch (IOException ex) {
            Logger.getLogger(NodeProjectSourceNodeFactory.class.getName()).log(Level.INFO, jsFile.getPath(), ex);
        }
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        supp.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        supp.removeChangeListener(l);
    }

    @Override
    public Node node(final Key key) {
        switch (key.type) {
            case LIBRARY:
                return new LibraryFilterNode(key);
            case SOURCE:
                return new FilterNode(nodeFromKey(key));
            case BUILT_IN_LIBRARY:
                AbstractNode li = new AbstractNode(Children.LEAF);
                li.setName(key.toString());
                li.setDisplayName(key.toString());
                li.setShortDescription("Built-in library '" + key + "'");
                li.setIconBaseWithExtension("org/netbeans/modules/nodejs/resources/libs.png"); //NOI18N
                return li;
            case MISSING_LIBRARY:
                AbstractNode an = new AbstractNode(Children.LEAF) {
                    @Override
                    public String getHtmlDisplayName() {
                        return "<font color=\"#EE0000\">" + key; //NOI18N
                    }
                };
                an.setName(key.toString());
                an.setDisplayName(key.toString());
                StringBuilder sb = new StringBuilder("<html>Missing library <b><i>" + key + "</i></b>");
                if (key instanceof Key.MissingLibrary && ((Key.MissingLibrary) key).references != null && !((Key.MissingLibrary) key).references.isEmpty()) {
                    sb.append("<p>Referenced By<br><ul>");
                    for (String path : ((Key.MissingLibrary) key).references) {
                        sb.append("<li>").append(path).append("</li>\n");
                    }
                    sb.append("</ul></pre></blockquote></html>");
                }
                an.setShortDescription(sb.toString());
                an.setIconBaseWithExtension("org/netbeans/modules/nodejs/resources/libs.png");
                return an;                
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void addNotify() {
        FileUtil.addRecursiveListener(this, FileUtil.toFile(project.getProjectDirectory()));
    }

    @Override
    public void removeNotify() {
        //do nothing
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        supp.fireChange();
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        supp.fireChange();
    }

    @Override
    public void fileChanged(FileEvent fe) {
        //do nothing
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        supp.fireChange();
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
        supp.fireChange();
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe) {
        //do nothing
    }

    static class Key {

        private final KeyTypes type;
        private final FileObject fld;
        private boolean direct;

        public Key(KeyTypes type, FileObject fld) {
            this.type = type;
            this.fld = fld;
        }

        public String toString() {
            return type + " " + fld.getName() + (direct ? " direct" : " indirect"); //NOI18N
        }
        
        static class BuiltInLibrary extends Key {
            private final String name;
            BuiltInLibrary(String name) {
                super (KeyTypes.BUILT_IN_LIBRARY, null);
                this.name = name;
            }
            
            @Override
            public String toString() {
                return name;
            }
        }
        
        static class MissingLibrary extends Key {
            private final String name;
            private List<String> references;
            MissingLibrary(String name) {
                super (KeyTypes.MISSING_LIBRARY, null);
                this.name = name;
            }
            
            @Override
            public String toString() {
                return name;
            }
        }
        
    }

    static enum KeyTypes {
        SOURCE,
        LIBRARY,
        BUILT_IN_LIBRARY,
        MISSING_LIBRARY
    }

    interface LibrariesFolderFinder {

        public FileObject getLibrariesFolder();
    }

    static final Node nodeFromKey(Key key) {
        try {
            return DataObject.find(key.fld).getNodeDelegate();
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            return Node.EMPTY;
        }
    }

    private static final class LibraryFilterNode extends FilterNode {

        private final Key key;
        private static RequestProcessor jsonReader = new RequestProcessor("Node lib json loader", 1);

        public LibraryFilterNode(Key key) {
            this(nodeFromKey(key), key);
        }

        private LibraryFilterNode(Node original, final Key key) {
            super(nodeFromKey(key), Children.create(new LibraryNodeChildren(original.getLookup().lookup(DataObject.class)), true));
            assert key.type == KeyTypes.LIBRARY;
            this.key = key;
            jsonReader.post(new Runnable() {

                @Override
                public void run() {
                    Map<String, Object> json = getPackageInfo();
                    synchronized (key) {
                        LibraryFilterNode.this.name = getString(json, "name", getDisplayName());
                        LibraryFilterNode.this.description = getString(json, "description", "[no description]");
                        LibraryFilterNode.this.author = getString(json, "author", null);
                        LibraryFilterNode.this.version = getString(json, "version", null);
                    }
                    Object license = json.get("license");
                    List<String> l = new ArrayList<String>();
                    if (license == null) {
                        license = json.get("licenses");
                    }
                    if (license instanceof String) {
                        l.add(license.toString());
                    }
                    if (license instanceof List) {
                        for (Object o : (List<?>) license) {
                            if (o instanceof String) {
                                l.add(o.toString());
                            } else if (o instanceof Map) {
                                Map<?, ?> m = (Map<?, ?>) o;
                                Object val = m.get("type");
                                if (val != null) {
                                    l.add(val.toString());
                                }
                            }
                        }
                    }
                    if (license instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>) license;
                        Object val = m.get("type");
                        if (val != null) {
                            l.add(val.toString());
                        }
                    }
                    Object repo = json.get("repository");
                    if (repo instanceof String) {
                        synchronized (key) {
                            LibraryFilterNode.this.repo = repo.toString();
                            LibraryFilterNode.this.repoType = "[unknown]";
                        }
                    }
                    if (repo instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>) repo;
                        Object rType = m.get("type");
                        if (rType instanceof String) {
                            synchronized (key) {
                                LibraryFilterNode.this.repoType = rType.toString();
                            }
                        }
                        Object r = m.get("url");
                        if (r instanceof String) {
                            synchronized (key) {
                                LibraryFilterNode.this.repo = r.toString();
                            }
                        }
                    }
                    if (author == null) {
                        Object a = json.get("author");
                        if (a instanceof Map) {
                            StringBuilder sb = new StringBuilder();
                            Object nm = ((Map) a).get("name");
                            if (nm != null) {
                                sb.append(nm);
                            }
                            nm = ((Map) a).get("email");
                            if (nm != null) {
                                sb.append(" <").append(nm).append(">");
                            }
                            synchronized (key) {
                                LibraryFilterNode.this.author = sb.toString();
                            }
                        } else if (a instanceof List) {
                            StringBuilder sb = new StringBuilder();
                            List<?> list = (List<?>) a;
                            for (Iterator<?> it = list.iterator(); it.hasNext();) {
                                Object o = it.next();
                                if (o instanceof String) {
                                    sb.append(o);
                                    if (it.hasNext()) {
                                        sb.append(", ");
                                    }
                                } else if (o instanceof Map) {
                                    Object nm = ((Map) o).get("name");
                                    if (nm != null) {
                                        sb.append(nm);
                                    }
                                    nm = ((Map) o).get("email");
                                    if (nm != null) {
                                        sb.append(" <").append(nm).append(">");
                                    }
                                }
                            }
                            synchronized (key) {
                                LibraryFilterNode.this.author = sb.toString();
                            }
                        }
                    }
                    Object o = json.get("bugs");
                    if (o instanceof String) {
                        synchronized (key) {
                            LibraryFilterNode.this.bugUrl = o.toString();
                        }
                    } else if (o instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>) o;
                        Object web = m.get("web");
                        if (web instanceof String) {
                            synchronized (key) {
                                LibraryFilterNode.this.bugUrl = web.toString();
                            }

                        }
                    }
                    synchronized (key) {
                        LibraryFilterNode.this.licenses = l.toArray(new String[l.size()]);
                    }
                }
            });
        }
        private String version;
        private String author;
        private String name;
        private String description;
        private String[] licenses;
        private String repoType;
        private String repo;
        private String bugUrl;

        @Override
        public Image getIcon(int type) {
            Image result = ImageUtilities.loadImage("org/netbeans/modules/nodejs/resources/libs.png");
            if (!key.direct) {
                result = ImageUtilities.createDisabledImage(result);
            }
            return result;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public String getHtmlDisplayName() {
            StringBuilder sb = new StringBuilder();
            if (!key.direct) {
                sb.append("<font color='!controlDkShadow'>");
            }
            sb.append(getDisplayName());
            if (version != null) {
                sb.append(" <i><font color='#9999AA'> ").append(version).append("</i>");
                if (!key.direct) {
                    sb.append("<font color='!controlDkShadow'>");
                }
            }
            if (!key.direct) {
                sb.append(" (&lt;-").append(key.fld.getParent().getParent().getName()).append(")");
            }
            return sb.toString();
        }

        @Override
        public String getShortDescription() {
            if (this.description != null || this.name != null) {
                StringBuilder sb = new StringBuilder("<html><body>");
                synchronized (key) {
                    sb.append("<b><u>").append(name == null ? getDisplayName() : name).append("</u></b><br>\n");
                    sb.append("<table border=0>");
                    if (description != null) {
                        sb.append("<tr><th align=\"left\">").append("Description").append("</th><td>").append(description).append("</td></tr>\n");
                    }
                    if (version != null) {
                        sb.append("<tr><th align=\"left\">").append("Version").append("</th><td>").append(version).append("</td></tr>\n");
                    }
                    if (author != null) {
                        sb.append("<tr><th align=\"left\">").append(author.indexOf(',') > 0 ? "Authors"
                                : "Author").append("</th><td>").append(author).append("</td></tr>\n");
                    }
                    if (licenses != null && licenses.length > 0) {
                        sb.append("<tr><th align=\"left\">");
                        sb.append(licenses.length > 0 ? "Licenses" : "License");
                        sb.append("</th><td>");
                        for (int i = 0; i < licenses.length; i++) {
                            sb.append(licenses[i]);
                            if (i != licenses.length - 1) {
                                sb.append(", ");
                            }
                        }
                        sb.append("</td></tr>");
                    }
                    if (repo != null) {
                        sb.append("<tr><th align=\"left\">");
                        sb.append("Repository");
                        if (repoType != null) {
                            sb.append('(').append(repoType).append(')');
                        }
                        sb.append("</th><td>");
                        sb.append(repo);
                        sb.append("</td></tr>");
                    }
                    if (bugUrl != null) {
                        sb.append("<tr><th align=\"left\">");
                        sb.append("Bugs:");
                        sb.append("</th><td>");
                        sb.append(bugUrl);
                        sb.append("</td></tr>");
                    }
                    sb.append("</table>");
                }
                return sb.toString();
            }
            return super.getShortDescription();
        }

        private String getString(Map<String, Object> m, String key, String def) {
            Object o = m.get(key);
            if (o instanceof String) {
                return ((String) o);
            }
            if (o instanceof List) {
                StringBuilder sb = new StringBuilder();
                for (Iterator<?> it = ((List<?>) o).iterator(); it.hasNext();) {
                    sb.append(it.next());
                    if (it.hasNext()) {
                        sb.append(',');
                    }
                }
                return sb.toString();
            }
            return def;
        }

        public Action[] getActions(boolean ignored) {
            Action[] result = super.getActions(ignored);
            List<Action> l = new ArrayList<Action>(Arrays.asList(result));
            if (bugUrl != null) {
                try {
                    URL url = new URL(bugUrl);
                    l.add(0, new BugAction(url));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(NodeProjectSourceNodeFactory.class.getName()).log(Level.INFO,
                            "Bad bug URL in " + getLookup().lookup(DataObject.class).getPrimaryFile().getPath() + ":" + bugUrl, ex);
                }
            }
            FileObject packageInfo = getLookup().lookup(DataObject.class).getPrimaryFile().getFileObject("package.json"); //NOI18N
            if (packageInfo != null) {
                l.add (0, new OpenInfoAction(packageInfo));
            }
            if (l.size() != result.length) {
                result = l.toArray(new Action[l.size()]);
            }
            return result;
        }

        private static final class OpenInfoAction extends AbstractAction {

            private final FileObject fo;

            OpenInfoAction(FileObject fo) {
                this.fo = fo;
                putValue(NAME, NbBundle.getMessage(OpenInfoAction.class, "OPEN_INFO_ACTION"));
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    org.netbeans.modules.nodejs.json.JsonPanel jp = new org.netbeans.modules.nodejs.json.JsonPanel(fo);
                    DataObject dob = DataObject.find(fo);
                    TopComponent tc = new TopComponent(dob.getLookup()) {
                        @Override
                        public int getPersistenceType() {
                            return TopComponent.PERSISTENCE_NEVER;
                        }
                    };
                    tc.setDisplayName(fo.getParent().getName());
                    tc.setLayout(new BorderLayout());
                    JScrollPane ssc = new JScrollPane(jp);
                    ssc.setBorder(BorderFactory.createEmptyBorder());
                    ssc.setViewportBorder(BorderFactory.createEmptyBorder());
                    tc.add(ssc, BorderLayout.CENTER);
                    tc.open();
                    tc.requestActive();
                } catch (Exception ex) {
                    //already logged
                }
                
                /*
                try {
                    DataObject dob = DataObject.find(fo);
                    OpenCookie oc = dob.getLookup().lookup(OpenCookie.class);
                    if (oc == null) {
                        EditCookie ek = dob.getLookup().lookup(EditCookie.class);
                        if (ek != null) {
                            ek.edit();
                        }
                    } else {
                        oc.open();
                    }
                } catch (DataObjectNotFoundException ex) {
                    //do nothing
                }
                */

            }
        }

        private static class BugAction extends AbstractAction {

            private final URL url;

            BugAction(URL url) {
                putValue(NAME, NbBundle.getMessage(BugAction.class, "FILE_BUG"));
                this.url = url;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                URLDisplayer.getDefault().showURL(url);
            }
        }

        private Map<String, Object> getPackageInfo() {
            assert !EventQueue.isDispatchThread();
            FileObject json = getLookup().lookup(DataObject.class).getPrimaryFile().getFileObject("package.json");
            if (json != null) {
                try {
                    Map<String, Object> m = new SimpleJSONParser().parse(json);
                    return m;
                } catch (JsonException ex) {
                    Logger.getLogger(NodeProjectSourceNodeFactory.class.getName()).log(Level.INFO, "Bad JSON in " + json.getPath(), ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(NodeProjectSourceNodeFactory.class.getName()).log(Level.INFO, "Failed to read JSON in " + json.getPath(), ex);
                }
            }
            return Collections.<String, Object>emptyMap();
        }
    }

    private static final class LibraryNodeChildren extends ChildFactory<FileObject> implements FileChangeListener {

        private final DataObject dob;

        private LibraryNodeChildren(DataObject dob) {
            this.dob = dob;

        }

        @Override
        protected boolean createKeys(List<FileObject> toPopulate) {
            for (FileObject fo : dob.getPrimaryFile().getChildren()) {
                if ("node_modules".equals(fo) && fo.isFolder()) {
                }
                toPopulate.add(fo);
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(FileObject key) {
            try {
                DataObject dob = DataObject.find(key);
                return new FilterNode(dob.getNodeDelegate());
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                return Node.EMPTY;
            }
        }

        @Override
        public void fileFolderCreated(FileEvent fe) {
            refresh(false);
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            refresh(false);
        }

        @Override
        public void fileChanged(FileEvent fe) {
            //do nothing
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            //do nothing
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            refresh(true);
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fe) {
            //do nothing
        }
    }
    private static class FOComparator
            implements Comparator<FileObject> {

        public int compare(FileObject o1, FileObject o2) {
            boolean aJs = ("js".equals(o1.getExt())) || ("json".equals(o1.getExt()));
            boolean bJs = ("js".equals(o2.getExt())) || ("json".equals(o2.getExt()));

            boolean aFld = o1.isFolder();
            boolean bFld = o2.isFolder();

            if (aJs == bJs) {
                if (aFld == bFld) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
                if (aFld) {
                    return 1;
                }
                return -1;
            }

            if (aJs) {
                return -1;
            }
            return 1;
        }
    }
}
