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

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.swing.UIManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.ErrorManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;


final class Kids extends Children.Keys implements Runnable, Comparator, FileChangeListener, PropertyChangeListener {
    private final String projPath;
    private final File dir;
    Kids(HtmlProject proj, File file) {
        this.proj = proj;
        if (file == null) {
            file = FileUtil.toFile(proj.getProjectDirectory());
        }
        dir = file;
        projPath = dir.getPath();
    }
    
    private RequestProcessor rp = new RequestProcessor("HtmlLogicalView finder" //NOI18N
            + " thread", Thread.MIN_PRIORITY, true);

    RequestProcessor.Task post(Runnable run) {
        synchronized (this) {
            System.err.println("post " + run);
            return rp.post (run);
        }
    }

    RequestProcessor.Task post(Runnable run, int timeToWait) {
        synchronized (this) {
            return rp.post (run, timeToWait);
        }
    }
    
    boolean isSortByFolder() {
        return sortByFolder;
    }

    private void dequeueAll() {
        synchronized (this) {
            System.err.println("DQ ALL");
            
            //We can have a huge number of HtmlNodes waiting to scan files for 
            //their html titles.  Rather than try to find them and cancel
            //all their runnables, just stop the RP they're queued on
            //and create another
            rp.stop();
            rp = new RequestProcessor("HtmlLogicalView finder" //NOI18N
                + " thread", Thread.MIN_PRIORITY, true);
        }
    }

    
    void launch() {
        synchronized (this) {
            if (task == null) {
                task = post (this);
            } else if (sortModeChanged) {
                sortModeChanged = false;
                task.cancel();
                task = post (this);
            }
        }
    }

    private RequestProcessor.Task task;
    public void addNotify() {
        System.err.println("AddNotify");
        setKeys (new String[]{ "Finding files..."});
        showing = true;
        launch();
        proj.getProjectDirectory().addFileChangeListener(this);
        sortModeChanged = false;
        OpenProjects.getDefault().addPropertyChangeListener(this);
    }
    
    void closed() {
        synchronized (this) {
            if (task != null) {
                task.cancel();
                task = null;
            }
        }
        setKeys (Collections.EMPTY_LIST);
        if (fetcher != null) {
            fetcher.cancel();
        }
        if (processor != null) {
            processor.cancel();
        }
        keys.clear();
        dequeueAll();
        proj.getProjectDirectory().removeFileChangeListener(this);
        OpenProjects.getDefault().removePropertyChangeListener(this);
        System.err.println("KIDS CLOSED");
    }
    
    private void finished() {
        synchronized (this) {
            task = null;
        }
    }

    public void removeNotify() {
        showing = false;
        System.err.println("REMOVE NOTIFY");
        closed();
    }
    
    static boolean isUsable (File f) {
        String ext = FileProcessor.getExt(f).toUpperCase(Locale.ENGLISH);
        boolean usable =
                "HTML".equals (ext) ||"HTM".equals(ext) || //NOI18N
                "JS".equals(ext) || "CSS".equals(ext) || //NOI18N
                "GIF".equals(ext) || "JPG".equals(ext) || //NOI18N
                "PNG".equals(ext) || "JPEG".equals(ext); //NOI18N
        return usable;
    }

    static boolean isHtml (File f) {
        String ext = FileProcessor.getExt(f).toUpperCase(Locale.ENGLISH);
        boolean html =
                "HTML".equals (ext) ||"HTM".equals(ext); //NOI18N
        return html;
    }

    boolean showing = false;

    protected Node[] createNodes(Object o) {
        Node result = null;
        if (o instanceof String) {
            result = new AbstractNode(Children.LEAF);
            result.setDisplayName(o.toString());
        }  else if (o instanceof File) {
                File f = (File) o;
                String ext = FileProcessor.getExt (f).toUpperCase(Locale.ENGLISH);
                boolean isHtml = isHtml (f);
                boolean usable = isUsable (f);
                if (!sortByFolder) {
                    if (isHtml) {
                        result = new HtmlFileNode(f, projPath, proj.getProjectDirectory(), this);
                    }  else                        if (usable) {
                            try {
                                result = new FilterNode(DataObject.find(
                                    FileUtil.toFileObject(f)).getNodeDelegate());
                            }  catch (DataObjectNotFoundException ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                        }
                }  else {
                    if (f.isDirectory()) {
                        result = new AbstractNode(new Kids(proj, f)) {
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
                        result.setShortDescription(snipPath (f,
                                projPath));
                    }
                }
            }
        return result == null ? new Node[0] : new Node[]{ result };
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
    void appendKeys(List more) {
        more.removeAll (keys);
        keys.addAll (more);
        Collections.sort(keys, this);
        System.err.println("BATCH ADD " + more.size() + " files ");
        Thread.currentThread().yield();
        setKeys (keys);
    }

    public void run() {
        upd (true);
    }
    
    public void removeFile(File f) {
        keys.remove (f);
        setKeys (keys);
    }
    
    private FileProcessor processor;
    private HtmlNameFetcher fetcher;
    private HtmlNameFetcher getFetcher() {
        if (fetcher == null) {
            fetcher = new HtmlNameFetcher();
        }
        return fetcher;
    }
    
    void doneScanning() {
        synchronized (this) {
            processor = null;
        }
    }
    
    public void upd(boolean incremental) {
        synchronized (this) {
            if (processor == null) {
                processor = new FileProcessor (this, dir,
                    incremental, sortByFolder);
            }
        }
        task = rp.post (processor);
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

    boolean sortModeChanged = false;
    void setSortByFilename(boolean b) {
        if (b != sortByFolder) {
            sortModeChanged = true;
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
    
    static String snipPath (File f, String projPath) {
        String path = f.getPath();
        if (path.length() > projPath.length()) {
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
        return path;
    }

    public void fileChanged(FileEvent e) {
        System.err.println("FILE CHANGED " + e.getFile().getPath());
        File f = FileUtil.toFile (e.getFile());
        String nm = snipPath (f, projPath);
        
        Node n = findChild (nm);
        if (n instanceof HtmlFileNode) {
            synchronized (this) {
                task = post (this);
            }
        }  else {
            System.err.println("CANNOT FIND CHILD NODE NAMED " + nm);
        }
    }

    public void fileDeleted(FileEvent fileEvent) {
        System.err.println("File DELETED " + fileEvent.getFile().getPath());
        File f = FileUtil.toFile (fileEvent.getFile());
        String nm = snipPath (f, projPath);
        try {
            Node nd = findChild(nm);
            //Can be null when deleting a project
            if (nd != null) {
                nd.destroy();
                removeFile (f);
            }
        }  catch (IOException ex) {
            ErrorManager.getDefault().notify (ex);
        }
    }

    public void fileRenamed(FileRenameEvent fre) {
        upd (false);
    }

    public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent) {
        //do nothing
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (OpenProjects.PROPERTY_OPEN_PROJECTS.equals(evt.getPropertyName())) {
            List l = Arrays.asList(OpenProjects.getDefault().getOpenProjects());
            if (!l.contains(proj)) {
                closed();
            }
        }
    }

    void enqueue(HtmlFileNode htmlFileNode, File file) {
        getFetcher().add(this, htmlFileNode, file);
    }
}