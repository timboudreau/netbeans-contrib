/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.TopManager;
import org.netbeans.modules.vcscore.caching.*;
import org.netbeans.modules.vcscore.actions.CommandActionSupporter;
import org.netbeans.modules.vcscore.actions.GeneralCommandAction;
import org.netbeans.modules.vcscore.commands.*;
import java.lang.ref.WeakReference;
import java.util.*;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.*;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.Debug;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
import org.netbeans.modules.vcscore.util.WeakList;


/**
 *
 * @author  Milos Kleint
 */
public class VcsActionSupporter extends CommandActionSupporter implements java.io.Serializable {

    protected  transient WeakReference fileSystem = new WeakReference(null);

    private HashMap commandMap;
    
    static final long serialVersionUID = -613064726657052221L;
    
    /** Creates new VcsActionSupporter */
    public VcsActionSupporter(VcsFileSystem filesystem) {
        fileSystem = new WeakReference(filesystem);
        commandMap = new HashMap();
    }
    
    public void setFileSystem(VcsFileSystem fs) {
        fileSystem = new WeakReference(fs);
    }

    public void addSupportForAction(Class actionClass, String commandName) {
        commandMap.put(actionClass, commandName);
    }
    
    public void removeSupportForAction(Class actionClass) {
        commandMap.remove(actionClass);
    }
    
    public boolean isEnabled(GeneralCommandAction action, FileObject[] fileObjects) {
        if (fileObjects == null || fileObjects.length == 0) {
            return false;
        }
        String cmdName = (String)commandMap.get(action.getClass());
        if (cmdName == null) {
            return false;
        }
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        final VcsCommand cmd = fileSystem.getCommand(cmdName);
        
        if (cmd == null) return false;
        Set foSet = new HashSet();
        for (int i = 0; i < fileObjects.length; i++) {
            foSet.add(fileObjects[i]);
        }
        boolean onRoot = isOnRoot(foSet);
        boolean onDir = isOnDirectory(foSet);
        boolean onFile = isOnFile(foSet);
        Set statuses = getSelectedFileStatusAttributes(foSet);
        
        if (    onDir && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_DIR)
        || onFile && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_FILE)
        || onRoot && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_ROOT)
        || VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)) {
            return false;
        }
        boolean disabled = VcsUtilities.isSetContainedInQuotedStrings(
        (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS), statuses);
        //System.out.println("VcsAction: isSetContainedInQuotedStrings("+(String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS)+
        //                   ", "+VcsUtilities.arrayToString((String[]) statuses.toArray(new String[0]))+") = "+disabled);
        if (disabled) {
            return false;
        }
        return true;
    }

    public void performAction(GeneralCommandAction action, FileObject[] fileObjects) {
        if (fileObjects == null || fileObjects.length == 0) {
            return;
        }
        String cmdName = (String)commandMap.get(action.getClass());
        if (cmdName == null) {
            return;
        }
        Set foSet = new HashSet();
        for (int i = 0; i < fileObjects.length; i++) {
            foSet.add(fileObjects[i]);
        }
        performCommand(cmdName, foSet);
    }

    
    /** Remove the files for which the command is disabled */
    private static Table removeDisabled(FileStatusProvider statusProvider, Table files, VcsCommand cmd) {
        if (statusProvider == null) return files;
        String disabledStatus = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS);
        if (disabledStatus == null) return files;
        Table remaining = new Table();
        for (Enumeration enum = files.keys(); enum.hasMoreElements(); ) {
            String name = (String) enum.nextElement();
            String status = statusProvider.getFileStatus(name);
            boolean disabled = VcsUtilities.isSetContainedInQuotedStrings(
                disabledStatus, Collections.singleton(status));
            if (!disabled) {
                remaining.put(name, files.get(name));
            }
        }
        return remaining;
    }
            
    protected void performCommand(final String cmdName, Set fos) {
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        final VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) return;
        boolean processAll = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_PROCESS_ALL_FILES) || fileSystem.isProcessUnimportantFiles();
        Table files = new Table();
        //String mimeType = null;
        //String path = "";
        boolean refreshDone = false;
        addImportantFiles(fos, files, processAll, false);
        files = removeDisabled(fileSystem.getStatusProvider(), files, cmd);
        if (cmdName.equals(VcsCommand.NAME_REFRESH)) {
            ArrayList paths = new ArrayList();
            for (Iterator it = files.values().iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                String path = fo.getPackageName('/');
                if (!paths.contains(path)) {
                    doList(path);
                    paths.add(path);
                }
            }
        } else if (cmdName.equals(VcsCommand.NAME_REFRESH_RECURSIVELY)) {
            ArrayList paths = new ArrayList();
            for (Iterator it = files.values().iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                String path = fo.getPackageName('/');
                if (!paths.contains(path)) {
                    doListSub(path);
                    paths.add(path);
                }
            }
        } else if (files.size() > 0) {
            doCommand (files, cmd);
        }
    }


    public void actionPerformed(String commandName, FileObject[] fos){
        //D.deb("actionPerformed("+e+")"); // NOI18N
        //System.out.println("actionPerformed("+e+")");
        final String cmdName = commandName;
        //D.deb("cmd="+cmd); // NOI18N
        final Set fosSet = new HashSet();
        for (int i = 0; i < fos.length; i++) {
            fosSet.add(fos[i]);
        }
        Runnable cpr;
        cpr = new Runnable() {
             public void run() {
                 performCommand(cmdName, fosSet);
             }
        };
        new Thread(cpr, "Vcs Commands Performing Thread").start();
    }

    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     */
    private void doCommand(Table files, VcsCommand cmd) {
        Hashtable map = new Hashtable(1);
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        VcsAction.doCommand(files, cmd, map, fileSystem);
    }
    
    /** Make sure, that the files are saved. If not, save them.
     * @param fos the collection of FileObjects
     */
    private static void assureFilesSaved(Collection fos) {
        for (Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            DataObject dobj = null;
            try {
                dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException exc) {
                // ignored
            }
            if (dobj != null || dobj.isModified()) {
                Node.Cookie cake = dobj.getCookie(SaveCookie.class);
                try {
                    if (cake != null) ((SaveCookie) cake).save();
                } catch (java.io.IOException exc) {
                    TopManager.getDefault().notifyException(exc);
                }
            }
        }
    }
    
    /** Reorder the table of files by the path hierarchical order.
     * @param files the table of pairs of files and file objects
     * @return the reordered table
     */
    private static Table createHierarchicalOrder(Table files) {
        TreeMap sorted = new TreeMap(files);
        Table sortedFiles = new Table();
        for (Iterator it = sorted.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            sortedFiles.put(key, files.get(key));
        }
        return sortedFiles;
    }

    /**
     * Test if some of the selected nodes are directories.
     * @return <code>true</code> if some of the selected nodes are directories,
     *         <code>false</code> otherwise.
     */
    protected boolean isOnDirectory(Collection fos) {
        boolean is = false;
        if (fos != null) {
            for (Iterator it = fos.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo.isFolder()) is = true;
            }
            //return false;
        }
        return is && !isOnRoot(fos);
    }

    /**
     * Test if some of the selected nodes are files.
     * @return <code>true</code> if some of the selected nodes are files,
     *         <code>false</code> otherwise.
     */
    protected boolean isOnFile(Collection fos) {
        if (fos != null) {
            for (Iterator it = fos.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                //System.out.println("  fo = "+fo);
                if (!fo.isFolder()) return true;
            }
        }
        return false;
    }

    /**
     * Test if one of the selected nodes is the root node.
     * @return <code>true</code> if at least one of the selected nodes is the root node,
     *         <code>false</code> otherwise.
     */
    protected boolean isOnRoot(Collection fos) {
        if (fos != null) {
           for (Iterator it = fos.iterator(); it.hasNext(); ) {
           FileObject fo = (FileObject) it.next();
                if (fo.getPackageNameExt('/', '.').length() == 0) return true;
           }
        }
       return false;
    }

    /**
     * Add files marked as important.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs which are important.
     * @param all whether to add unimportant files as well
     */
    protected void addImportantFiles(Collection fos, Table res, boolean all, boolean doNotTestFS) {
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        addImportantFiles(fos, res, all, fileSystem, doNotTestFS);
    }

    /**
     * Add files.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs.
     * @param all whether to add unimportant files as well
     * @param fileSystem the file system
     */
    public static void addImportantFiles(Collection fos, Table res, boolean all, VcsFileSystem fileSystem) {
        addImportantFiles(fos, res, all, fileSystem, false);
    }
    
    /**
     * Add files.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs.
     * @param all whether to add unimportant files as well
     * @param fileSystem the file system
     * @param doNotTestFS if true, FileObjects will not be tested whether they belong to VcsFileSystem
     */
    public static void addImportantFiles(Collection fos, Table res, boolean all, VcsFileSystem fileSystem, boolean doNotTestFS) {
        for(Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject ff = (FileObject) it.next();
            try {
                if (!doNotTestFS && ff.getFileSystem() != fileSystem)
                    continue;
            } catch (FileStateInvalidException exc) {
                continue;
            }
            String fileName = ff.getPackageNameExt('/','.');
            //VcsFile file = fileSystem.getCache().getFile(fileName);
            //D.deb("file = "+file+" for "+fileName);
            //if (file == null || file.isImportant()) {
            if (all || fileSystem.isImportant(fileName)) {
                //D.deb(fileName+" is important");
                res.put(fileName, ff);
            }
            //else D.deb(fileName+" is NOT important");
        }
    }
    
    private Set getSelectedFileStatusAttributes(Set fileObjects) {
        Set statuses = new HashSet();
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        FileStatusProvider statusProv = fileSystem.getStatusProvider();
        boolean processAll = fileSystem.isProcessUnimportantFiles();
        if (statusProv != null) {
            for (Iterator it = fileObjects.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                String path = fo.getPackageNameExt('/', '.');
                if (processAll || fileSystem.isImportant(path)) {
                    String status = statusProv.getFileStatus(path);
                    if (status != null) statuses.add(status);
                }
            }
        }
        return statuses;
    }

 
    /**
     * Do refresh of a directory.
     * @param path the directory path
     */
    public void doList(String path) {
        //D.deb("doList('"+path+"')"); // NOI18N
        //System.out.println("VcsAction.doList("+path+")");
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        FileCacheProvider cache = fileSystem.getCacheProvider();
        if (statusProvider == null) return;
        //System.out.println("cache = "+cache+", cache.isDir("+path+") = "+cache.isDir(path));
        if (cache == null || cache.isDir(path)) {
            statusProvider.refreshDir(path);
        } else {
            String dirName = VcsUtilities.getDirNamePart(path);
            statusProvider.refreshDir(dirName);
        }
    }

    /**
     * Do recursive refresh of a directory.
     * @param path the directory path
     */
    public void doListSub(String path) {
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        CommandExecutorSupport.doRefresh(fileSystem, path, true);
    }

    
}
