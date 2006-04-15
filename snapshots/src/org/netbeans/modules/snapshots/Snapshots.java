package org.netbeans.modules.snapshots;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.core.NbTopManager;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author Timothy Boudreau
 */
public class Snapshots {
    
    /** Creates a new instance of Snapshots */
    private Snapshots() {
    }

    private static final String WIN_PATH = "snapshots/windows";
    private static final String PRJS_PATH = "snapshots/projects";
    public static String[] getNames() {
        FileObject ob = getWindowsFolder(false);
        if (ob == null) {
            return new String[0];
        }
        FileObject[] fob = ob.getChildren();

        List results = new ArrayList();
        for (int i=0; i < fob.length; i++) {
            String nm = fob[i].getName();
            results.add(nm);
            if (!Snapshots.isValid(nm)) {
                try {
                    fob[i].delete();
                } catch (IOException ioe) {
                    ErrorManager.getDefault().notify(ioe);
                }
            }
        }
        String[] result = (String[]) results.toArray(new String[results.size()]);
        return result;
    }

    private static FileObject getProjectsFolder(boolean create) {
        FileObject result = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(PRJS_PATH);
        if (result == null && create) {
            try {
                FileUtil.createFolder(Repository.getDefault().getDefaultFileSystem().getRoot(),
                        PRJS_PATH);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return result;
    }

    private static FileObject getWindowsFolder(boolean create) {
        FileObject result = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(WIN_PATH);
        if (result == null && create) {
            try {
                result = FileUtil.createFolder(Repository.getDefault().getDefaultFileSystem().getRoot(),
                        WIN_PATH);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return result;
    }

    public static boolean isValid (String name) {
        return getWindowsSnapshotFolder(name,  false) != null && getProjectsSnapshotFolder(name,  false) != null;
    }

    private static FileObject getWindowsSnapshotFolder (String name,  boolean create) {
        String path = getWindowsFolderPath(name);
        FileObject result = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(path);
        if (result == null && create) {
            try {
                result = FileUtil.createFolder(Repository.getDefault().getDefaultFileSystem().getRoot(),
                        path);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return result;
    }

    private static FileObject getProjectsSnapshotFolder (String name,  boolean create) {
        String path = getProjectsFolderPath(name);
        FileObject result = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(path);
        if (result == null && create) {
            try {
                result = FileUtil.createFolder(Repository.getDefault().getDefaultFileSystem().getRoot(),
                        path);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return result;
    }

    private static String getWindowsFolderPath(String name) {
        String path = WIN_PATH + "/" + name;
        return path;
    }

    private static String getProjectsFolderPath (String name) {
        String path = PRJS_PATH + "/" + name;
        return path;
    }

    public static boolean takeSnapshot(String name) {
        try {
            Repository.getDefault().getDefaultFileSystem().runAtomicAction(
                new SnapshotTaker(name));
            return true;
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
            return false;
        }
    }

    public static boolean restoreSnapshot(String name) {
        try {
            Repository.getDefault().getDefaultFileSystem().runAtomicAction(
                new SnapshotRestorer(name));
            return true;
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
            return false;
        }
    }


    private static FileObject getWindowSystemFolder() {
        return Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Windows2Local");
    }

    private static class SnapshotTaker implements FileSystem.AtomicAction {
        private final String name;
        public SnapshotTaker (String name) {
            this.name=name;
        }

        public void run() throws IOException {
            NbTopManager.WindowSystem wsys = (NbTopManager.WindowSystem)
                Lookup.getDefault().lookup(NbTopManager.WindowSystem.class);

            if (wsys == null) {
                throw new IOException ("Couldn't find NbTopManager.WindowSystem" +
                        " in the default lookup");
            }
            System.err.println("Begin winsys save " + System.currentTimeMillis());
            wsys.save();
            System.err.println("End winsys save " + System.currentTimeMillis());

            FileObject winFld = getWindowsSnapshotFolder(name,  false);
            FileObject prjFld = getProjectsSnapshotFolder(name,  false);
            if (winFld != null) {
                winFld.delete();
            }
            if (prjFld != null) {
                prjFld.delete();
            }
            winFld = getWindowsSnapshotFolder(name,  true);
            prjFld = getProjectsSnapshotFolder(name,  true);
            if (winFld == null || prjFld == null) {
                throw new IOException ("Failed to create folder for " + name);
            }

            FileObject winsys = getWindowSystemFolder();

            if (winsys == null || !winsys.isFolder()) {
                throw new IOException ("Window system folder missing");
            }

            System.err.println("BEGIN COPY Windows2Local to SNAPSHOT - copy " + winFld.getPath() + " to " + winsys.getPath() );
            copy(winFld, winsys);

            Project[] prjs = OpenProjects.getDefault().getOpenProjects();

            DataFolder fld = DataFolder.findFolder(prjFld);

            for (int i = 0; i < prjs.length; i++) {
                FileObject dir = prjs[i].getProjectDirectory();
                String pth = Utilities.replaceString(dir.getPath(), "/", "-");
                DataObject orig = DataObject.find (dir);
                DataShadow.create(fld, pth, orig);
            }
        }
    }

    private static void copy (FileObject to, FileObject src) throws IOException {
        System.err.println("COPY " + src.getPath() + " to " + to.getPath());
        FileObject result;
        if (!src.isFolder()) {
            result = to.getFileObject(src.getNameExt());
            if (result == null) {
                result = FileUtil.copyFile(src, to, src.getName(), src.getExt());
            } else {
                FileLock srcLock = src.lock();
                FileUtil.copy(result.getInputStream(),
                        src.getOutputStream(srcLock));
                srcLock.releaseLock();
            }
        } else {
            FileObject fld = to.getFileObject(src.getNameExt());
            if (fld == null) {
                fld = FileUtil.createFolder(to, src.getNameExt());
            }
            result = fld;
            FileObject[] kidz = src.getChildren();
            System.err.println("Start recurse copy - " + kidz.length +
                    "children of " + fld.getPath());
            
            for (int i=0; i < kidz.length; i++) {
                copy (fld, kidz[i]);
            }
        }
        System.err.println("Copied " + src.getPath() + " to " + result.getPath());
        FileUtil.copyAttributes(src, result);
    }

    private static class SnapshotRestorer implements FileSystem.AtomicAction {
        private final String name;
        public SnapshotRestorer (String name) {
            this.name = name;
        }

        private void deleteChildrenOf (FileObject parent, String subfolderName) throws IOException {
            System.err.println("Delete children of " + parent.getPath() + "/" + subfolderName );
            FileObject fob = parent.getFileObject(subfolderName);
            if (fob.isValid() && fob.isFolder()) {
                FileObject[] kidz = fob.getChildren();
                for (int i=0; i < kidz.length; i++) {
                    kidz[i].delete();
                }
            }
        }

        public void run() throws IOException {
            NbTopManager.WindowSystem wsys = (NbTopManager.WindowSystem)
                Lookup.getDefault().lookup(NbTopManager.WindowSystem.class);
            if (wsys == null) {
                throw new IOException ("Couldn't find NbTopManager.WindowSystem" +
                        " in the default lookup");
            }

            FileObject winFld = getWindowsSnapshotFolder(name,  false);
            if (winFld == null || !winFld.isFolder()) {
                throw new IOException ("Window system folder missing from snapshot");
            }
            winFld = winFld.getFileObject ("Windows2Local"); //fixme
            if (winFld == null || !winFld.isFolder()) {
                throw new IOException ("Windows2Local folder missing from snapshot");
            }
            FileObject prjFld = getProjectsSnapshotFolder(name,  false);

            if (winFld == null) {
                throw new IOException ("No saved winsys folder for " + name);
            }
            if (prjFld == null) {
                throw new IOException ("No saved projects folder for " + name);
            }

            FileObject[] prjLinks = prjFld.getChildren();
            Set prjsToOpen = new HashSet();
            for (int i=0; i < prjLinks.length; i++) {
                DataObject ob = DataObject.find (prjLinks[i]);
                while (ob instanceof DataShadow) {
                    ob = ((DataShadow) ob).getOriginal();
                }
                if (ob != null) {
                    FileObject prjFolder = ob.getPrimaryFile();
                    Project prj = FileOwnerQuery.getOwner(prjFolder);
                    if (prj != null) {
                        prjsToOpen.add (prj);
                    }
                }
            }

            Set currentPrjs = new HashSet (Arrays.asList(OpenProjects.getDefault().getOpenProjects()));
            Set toClose = new HashSet (currentPrjs);
            toClose.removeAll (prjsToOpen);
            prjsToOpen.removeAll(currentPrjs);
            Project[] toCloseArr = (Project[]) toClose.toArray(new Project[toClose.size()]);
            Project[] toOpenArr = (Project[]) prjsToOpen.toArray(new Project[prjsToOpen.size()]);

            OpenProjects.getDefault().close(toCloseArr);
            OpenProjects.getDefault().open(toOpenArr, false);

            FileObject winsys = getWindowSystemFolder();
            System.err.println("DELETING CURRENT WINDOW SYSTEM CONFIGURATION");
            deleteChildrenOf (winsys, "Components");
            deleteChildrenOf (winsys, "Groups");
            deleteChildrenOf (winsys, "Modes");

            FileObject wsmgr = winsys.getFileObject("WindowManager.wsmgr");
            if (wsmgr != null) {
                wsmgr.delete();
            }
            FileObject[] repl = winFld.getChildren();

            System.err.println("BEGIN COPY SNAPSHOT WINSYS DATA TO Windows2Local " + repl.length + " files/folders");
            for (int i=0; i < repl.length; i++) {
                copy (winsys, repl[i]);
            }
            
            wsys.load();
        }
    }


}

