/*
The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
package org.netbeans.modules.htmlproject;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.actions.FileSystemAction;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Tim Boudreau
 */
final class HtmlLogicalView extends AbstractNode implements PropertyChangeListener {
    private HtmlProject proj;
    private static final RequestProcessor rp =
            new RequestProcessor ("HtmlLogicalView finder" //NOI18N
            + " thread", //NOI18N
            Thread.MIN_PRIORITY);

    private static final ByteBuffer buf = ByteBuffer.allocate(8192);
    private static final CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
    private static final Pattern pat = Pattern.compile(".*<title>(.*?)</title>", //NOI18N
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** Creates a new instance of HtmlLogicalView */
    public HtmlLogicalView(HtmlProject proj, Lookup toProxy) {
        super (new Kids(proj, null), new ProxyLookup (new Lookup[] {
            Lookups.fixed(new Object[] { proj, new Locator() }), toProxy,
        }));
        Locator l = (Locator) getLookup().lookup (Locator.class);
        l.view = this;
        this.proj = proj;
        setDisplayName (proj.getDisplayName());
        setIconBaseWithExtension("org/netbeans/modules/htmlproject/" + //NOI18N
                "htmlProject.png"); //NOI18N
        proj.addPropertyChangeListener(WeakListeners.propertyChange(this, proj));
    }

    static class Locator {
        HtmlLogicalView view;
        public Node locate (Node root, Object o) {
            if (o instanceof FileObject) {
                o = FileUtil.toFile((FileObject)o);
            }
            return traverse (root, o.getClass(), o);
        }

        private Node traverse (Node n, Class clazz, Object o) {
            System.err.println("TRAVERSE " + n.getName() + " lkp " + n.getLookup());
            if (o.equals(n.getLookup().lookup(clazz))) {
                return n;
            } else {
                Node[] more = n.getChildren().getNodes(true);
                for (int i = 0; i < more.length; i++) {
                    Node result = traverse (more[i], clazz, o);
                    if (result != null) {
                        System.err.println("FOUND " + result);
                        return result;
                    }
                }
            }
            return null;
        }
    }

    public Action[] getActions(boolean popup) {
        Action fsAction = findFsAction (proj);
        List l = new ArrayList (11);

        int[] order = new int[] {
            MutableAction.VIEW, MutableAction.ZIP, MutableAction.CLEAN,
            -1, MutableAction.CLOSE, MutableAction.DELETE,
        };

        for (int i = 0; i < order.length; i++) {
            if (order[i] == -1) {
                l.add (null);
            } else {
                l.add (new MutableAction (order[i], proj));
            }
        }
        l.add (null);
        if (fsAction != null) {
            l.add (fsAction);
            l.add (null);
        }
        boolean currSort = ((Kids) getChildren()).sortByFolder;
        l.add (new SortAction (false, currSort));
        l.add (new SortAction (true, currSort));
        l.add (null);
        l.add (new MutableAction (MutableAction.PROPS, proj));

        Action[] result = (Action[]) l.toArray(new Action[l.size()]);
        return result;
    }

    private class SortAction extends AbstractAction implements Presenter.Popup {
        private boolean byFilename;
        private boolean currentlySortedByFilename;
        SortAction (boolean filename, boolean currIsFilename) {
            this.byFilename = filename;
            this.currentlySortedByFilename = currIsFilename;
            putValue (NAME, filename ? "Sort by Folder" : "Sort by Filename");
        }

        public void actionPerformed(ActionEvent e) {
            Kids k = (Kids) getChildren();
            k.setSortByFilename (byFilename);
        }

        public JMenuItem getPopupPresenter() {
            JCheckBoxMenuItem result = new JCheckBoxMenuItem (this);
            if (byFilename == currentlySortedByFilename) {
                result.setSelected(true);
            }
            return result;
        }
    }

    private void upd() {
        if (((Kids) getChildren()).showing) {
            rp.post((Runnable) getChildren());
        }
    }

    private Action findFsAction (Project p) {
        FileObject fob = p.getProjectDirectory();
        Node n;
        try {
            n = DataObject.find(fob).getNodeDelegate();
        } catch (DataObjectNotFoundException ex) {
            return null;
        }
        Action[] a = n.getActions(true);
        for (int i = 0; i < a.length; i++) {
            if (a[i] instanceof FileSystemAction) {
                return a[i];
            }
        }
        return null;
    }

    static final class Kids extends Children.Keys implements Runnable, Comparator, FileChangeListener {
        private final String projPath;
        private final File dir;
        Kids (HtmlProject proj, File file) {
            this.proj = proj;
            if (file == null) {
                file = FileUtil.toFile(proj.getProjectDirectory());
            }
            dir = file;
            projPath = dir.getPath();
        }

        public void addNotify() {
            setKeys (new String[] { "Finding files..."});
            showing = true;
            rp.post (this);
            proj.getProjectDirectory().addFileChangeListener(this);
        }

        public void removeNotify() {
            showing = false;
            keys.clear();
            proj.getProjectDirectory().removeFileChangeListener(this);
        }

        private boolean showing = false;

        protected Node[] createNodes(Object o) {
            Node result = null;
            if (o instanceof String) {
                result = new AbstractNode (Children.LEAF);
                result.setDisplayName(o.toString());
            } else if (o instanceof File) {
                File f = (File) o;
                String ext = getExt (f).toUpperCase(Locale.ENGLISH);
                boolean isHtml = isHtml (f);
                boolean usable = isUsable (f);
                if (!sortByFolder) {
                    if (isHtml) {
                        result = new HtmlFileNode (f, projPath,
                                proj.getProjectDirectory(), this);
                    } else if (usable) {
                        try {
                            result = new FilterNode (DataObject.find(
                                    FileUtil.toFileObject(f)).getNodeDelegate());
                        } catch (DataObjectNotFoundException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                } else {
                    if (f.isDirectory()) {
                        result = new AbstractNode (new Kids(proj, f)) {
                            public Image getIcon(int type) {
                                Image result = fldImage();
                                return result == null ? super.getIcon(type) :
                                    result;
                            }

                            public Image getOpenedIcon(int type) {
                                Image result = fldOpenImage();
                                return result == null ? super.getIcon(type) :
                                    result;
                            }

                        };
                        result.setName(f.getName());
                        result.setShortDescription(HtmlLogicalView.snipPath (f,
                                projPath));
                    }
                }
            }
            return result == null ? new Node[0] : new Node[] { result };
        }

        private static Image fldImage() {
            Image fldImage;
            fldImage = (Image) UIManager.get("Nb.Explorer.Folder.icon"); //NOI18N
            return fldImage;
        }

        private static Image fldOpenImage() {
            Image fldImage;
            fldImage = (Image) UIManager.get("Nb.Explorer.Folder.openedIcon"); //NOI18N
            return fldImage;
        }

        private List keys = new ArrayList();
        private HtmlProject proj;
        private void appendKeys(List more) {
            more.removeAll (keys);
            keys.addAll (more);
            Collections.sort(keys, this);
            setKeys (keys);
        }

        public void run() {
            upd (true);
        }
        
        public void removeFile (File f) {
            keys.remove (f);
            setKeys (keys);
        }


        public void upd(boolean incremental) {
            File f = dir;
            List l = new ArrayList();
            searchFiles (f, l, false);
            if (l.size() > 0) {
                appendKeys (l);
            }
        }

        private void searchFiles(File f, List l, boolean incremental) {
            if (!showing) {
                return;
            }
            if (f.isDirectory()) {
                if (!sortByFolder) {
                    File[] ff = f.listFiles();
                    int sz = l.size();
                    for (int i = 0; i < ff.length; i++) {
                        searchFiles (ff[i], l, incremental);
                    }
                    int nsz = l.size();
                    if (sortByFolder && nsz != sz) {
                        l.add(f);
                    }
                    if (incremental && (nsz != 0 && nsz > sz)) {
                        appendKeys(l);
                        l.clear();
                    }
                } else {
                    File[] ff = f.listFiles();
                    boolean found = false;
                    for (int i = 0; i < ff.length; i++) {
                        found = isUsable(ff[i]);
                        if (ff[i].isDirectory()) {
                            searchFiles (ff[i], l, incremental);
                        }
                    }
                    if (found) {
                        l.add(f);
                    }
                }
            } else {
                if (!sortByFolder && isUsable(f)) { //NOI18N
                    l.add (f);
                }
            }
        }

        private boolean sortByFolder = false;
        public int compare(Object o1, Object o2) {
            File a = (File) o1;
            File b = (File) o2;
            boolean aHtml = isHtml(a);
            boolean bHtml = isHtml(b);
            int result = a.getName().compareToIgnoreCase(b.getName());
            if (bHtml && !aHtml) {
                result += 65536;
            } else if (aHtml & !bHtml) {
                result -= 65536;
            }
            return result;
        }

        private void setSortByFilename(boolean b) {
            if (b != sortByFolder) {
                sortByFolder = b;
                removeNotify();
                addNotify();
            }
        }

        public void fileFolderCreated(FileEvent fileEvent) {
            System.err.println("FOLDER CREATED "  + fileEvent.getFile().getPath());
            fileEvent.getFile().addFileChangeListener(this);
            upd (false);
        }

        public void fileDataCreated(FileEvent e) {
            System.err.println("DATA CREATED " + e.getFile().getPath());
            if (showing) {
                System.err.println("Adding to keys");
                keys.add(FileUtil.toFile(e.getFile()));
                setKeys (keys);
            }
        }

        public void fileChanged(FileEvent e) {
            System.err.println("FILE CHANGED " + e.getFile().getPath());
            File f = FileUtil.toFile (e.getFile());
            String nm = snipPath (f, projPath);
            
            Node n = findChild (nm);
            if (n instanceof HtmlFileNode) {
                rp.post ((Runnable) n);
            } else {
                System.err.println("CANNOT FIND CHILD NODE NAMED " + nm);
            }
        }

        public void fileDeleted(FileEvent fileEvent) {
            System.err.println("File DELETED " + fileEvent.getFile().getPath());
            File f = FileUtil.toFile (fileEvent.getFile());
            String nm = snipPath (f, projPath);
            try {
                findChild(nm).destroy();
                removeFile (f);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify (ex);
            }
        }

        public void fileRenamed(FileRenameEvent fre) {
            upd (false);
        }

        public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent) {
            //do nothing
        }
    }

    private static boolean isUsable (File f) {
        String ext = HtmlLogicalView.getExt(f).toUpperCase(Locale.ENGLISH);
        boolean usable =
                "HTML".equals (ext) ||"HTM".equals(ext) || //NOI18N
                "JS".equals(ext) || "CSS".equals(ext) || //NOI18N
                "GIF".equals(ext) || "JPG".equals(ext) || //NOI18N
                "PNG".equals(ext) || "JPEG".equals(ext); //NOI18N
        return usable;
    }

    private static boolean isHtml (File f) {
        String ext = HtmlLogicalView.getExt(f).toUpperCase(Locale.ENGLISH);
        boolean html =
                "HTML".equals (ext) ||"HTM".equals(ext); //NOI18N
        return html;
    }


    private static String getExt(File f) {
        String s = f.getName();
        int ix = s.lastIndexOf('.');
        if (ix != -1 && ix != s.length() - 1) {
            return s.substring(ix + 1);
        } else {
            return "";
        }
    }

    static String getName (File f) {
        String s = f.getName();
        return s.substring(0, s.length() - getExt(f).length() - 1);
    }

    private static String snipPath (File f, String projPath) {
        char[] c = f.getPath().substring(projPath.length()).toCharArray();
        for (int i = 0; i < c.length; i++) {
            c[i] = c[i] == '\\' ? '/' : c[i]; //NOI18N
        }
        if (c.length == 0) {
            return ""; //NOI18N
        } else {
            return new String(c, c[0] == '/' ? 1 : 0, c[0] == '/' ? c.length - 1 : //NOI18N
                c.length);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        setDisplayName (proj.getDisplayName());
    }


    private static class HtmlFileNode extends AbstractNode implements Runnable, OpenCookie {
        private final boolean index;
        private final FileObject projDir;
        public HtmlFileNode (File f, String projPath, FileObject projDir, Kids kids) {
            super (Children.LEAF, Lookups.fixed(new Object[] { f, kids }));
            String relPath = snipPath (f, projPath);
            this.projDir = projDir;
            setName (relPath);
            setDisplayName (f.getName());
            setShortDescription(relPath);
            index = "INDEX".equals(HtmlLogicalView.getName(f).toUpperCase(Locale.ENGLISH)) // NOI18N
                    && projDir.equals(FileUtil.toFileObject(f).getParent());
        }

        private boolean checked = false;
        public String getDisplayName() {
            if (!checked) {
                rp.post(this, 200);
            }
            return super.getDisplayName();
        }

        public String getHtmlDisplayName() {
            if (index) {
                return "<b>" + getDisplayName();
            } else {
                return getDisplayName() +
                        "<font color='!controlShadow'> (" + getFile().getName()  //NOI18N
                        + ')'; //NOI18N
            }
        }

        private File getFile() {
            return (File) getLookup().lookup (File.class);
        }
        
        private Kids getKids() {
            return (Kids) getLookup().lookup(Kids.class);
        }

        public Action[] getActions(boolean popup) {
            File f = getFile();
            if (f == null) {
                try {
                    destroy();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify (ex);
                }
                return new Action[0];
            }
            String trimmedPath = getShortDescription().substring(0, f.getName().length());
            return new Action[] {
                new OpenAction(),
                new ViewAction(f),
                null,
                new DelAction(),
                null,
                new ColocatedHtmlFileAction(f, trimmedPath, projDir, getKids()),
            };
        }

        public Action getPreferredAction() {
            return getActions (true) [0];
        }

        class OpenAction extends AbstractAction {
            public OpenAction() {
                putValue (NAME, "Open");
            }

            public void actionPerformed (ActionEvent ae) {
                OpenCookie ie = realCookie();
                if (ie != null) {
                    ie.open();
                }
            }

            private OpenCookie realCookie() {
                File f = getFile();
                FileObject fob = FileUtil.toFileObject (f);
                DataObject dob;
                try {
                    dob = DataObject.find(fob);
                    OpenCookie oc = (OpenCookie) dob.getCookie(OpenCookie.class);
                    if (oc == null) {
                        final EditCookie ec = (EditCookie) dob.getCookie(EditCookie.class);
                        if (ec != null) {
                            oc = new OpenCookie() {
                                public void open() {
                                    ec.edit();
                                }
                            };
                        }
                    }
                    return oc;
                } catch (DataObjectNotFoundException ex) {
                    throw new IllegalStateException (ex);
                }
            }

            public boolean isEnabled() {
                return realCookie() != null;
            }
        }

        class DelAction extends AbstractAction {
            public DelAction () {
                putValue (NAME, "Delete");
            }

            public void actionPerformed (ActionEvent ae) {
                try {
                    FileObject fob = FileUtil.toFileObject (getFile());
                    DataObject dob = DataObject.find (fob);
                    dob.delete();
                    HtmlFileNode.this.fireNodeDestroyed();
                    Kids kids = (Kids) getLookup().lookup (Kids.class);
                    kids.upd(false);
                } catch (DataObjectNotFoundException donfe) {
                    ErrorManager.getDefault().notify (donfe);
                } catch (IOException ioe) {
                    ErrorManager.getDefault().notify (ioe);
                }
            }

            public boolean isEnabled() {
                return getFile().exists();
            }
        }


        static final class ViewAction extends AbstractAction {
            private final File f;
            public ViewAction(File f) {
                putValue (NAME, "View");
                this.f = f;
            }

            public void actionPerformed (ActionEvent ae) {
                try {
                    URLDisplayer.getDefault().showURL(f.toURI().toURL());
                } catch (MalformedURLException ex) {
                    ErrorManager.getDefault().notify (ex);
                }
            }
        }

        public void reset() {
            checked = false;
            rp.post(this);
        }

        public void run() {
            checked = true;
            String[] result = new String[1];
            if (!EventQueue.isDispatchThread()) {
                File f = getFile();
                if (f.exists() && f.isFile() && f.length() > 20) {
                    FileChannel fc;
                    try {
                        fc = new FileInputStream(f).getChannel();
                        buf.clear();
                        fc.read(buf);
                        fc.close();
                        buf.flip();
                        //XXX actually detect the encoding in the file header
                        CharSequence seq = decoder.decode(buf);
                        Matcher matcher = pat.matcher(seq);
                        if (matcher.lookingAt()) {
                            result[0] = matcher.group(1);
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                checked = true;
                if (result[0] != null) {
                    setDisplayName(result[0]);
                }
            }
        }

        public void open() {
            OpenAction oe = new OpenAction();
            if (oe.isEnabled()) {
                oe.actionPerformed(null);
            }
        }

        private static Image htmlIcon =
                Utilities.loadImage ("org/netbeans/modules/htmlproject/" + //NOI18N
                "htmlObject.png"); //NOI18N
        public Image getOpenedIcon(int i) {
            return htmlIcon;
        }

        public Image getIcon(int i) {
            return htmlIcon;
        }
    }
}
