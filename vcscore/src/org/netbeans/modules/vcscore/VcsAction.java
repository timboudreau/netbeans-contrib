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

import java.util.*;
import javax.swing.*;
import java.awt.event.ActionListener;

import org.openide.*;
import org.openide.awt.JMenuPlus;
import org.openide.awt.JInlineMenu;
import org.openide.util.actions.*;
import org.openide.util.HelpCtx;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.cookies.SaveCookie;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.Debug;
import org.netbeans.modules.vcscore.caching.VcsFSCache;
import org.netbeans.modules.vcscore.caching.VcsCacheFile;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.commands.*;

/**
 * The system action of the VcsFileSystem.
 * @author  Pavel Buzek, Martin Entlicher
 */
public class VcsAction extends NodeAction implements ActionListener {
    private Debug E=new Debug("VcsAction", true); // NOI18N
    private Debug D=E;

    /**
     * Whether to remove disabled commands from the popup menu.
     */
    protected boolean REMOVE_DISABLED = false;

    protected VcsFileSystem fileSystem = null;
    protected Collection selectedFileObjects = null;
    
    private Node[] actionCommandsSubTrees = null; // the commands subtrees to construct actions from
    
    public VcsAction() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    public void setSelectedFileObjects(Collection fos) {
        this.selectedFileObjects = fos;
    }
    
    public void setCommandsSubTrees(Node[] commandsSubTrees) {
        actionCommandsSubTrees = commandsSubTrees;
    }

    /*
    public VcsCacheFile parseFromCache (String[] cacheRecord) {
        return null; // TODO
    }
     */

    /**
     * Get a human presentable name of the action.
     * @return the name of the action
     */
    public String getName() {
        return fileSystem.getBundleProperty("CTL_Version_Control"); // NOI18N
    }

    /**
     * Get a help context for the action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx(){
        //D.deb("getHelpCtx()"); // NOI18N
        return null;
    }

    //public abstract void doList(String path);
    //public abstract void doDetails(Vector files);
    //public abstract void doCheckIn(Vector files);
    //public abstract void doCheckOut(Vector files);
    //public abstract void doAdd(Vector files);
    //public abstract void doRemove(Vector files);
    //public abstract JMenuItem getPopupPresenter();
    //public abstract void doAdditionalCommand(String name, Vector files);
    //protected abstract void doCommand(Vector files, VcsCommand cmd);

    private void killAllCommands() {
        CommandsPool cmdPool = fileSystem.getCommandsPool();
        String[] labels = cmdPool.getRunningCommandsLabels();
        if (labels.length > 0) {
            if (NotifyDescriptor.Confirmation.YES_OPTION.equals (
                            TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (
                                                             fileSystem.getBundleProperty("MSG_KILL_ALL_CMDS", VcsUtilities.arrayToString(labels)), NotifyDescriptor.Confirmation.YES_NO_OPTION)))) {
                cmdPool.killAll();
            }
        }
    }

    /**
     * Do refresh of a directory.
     * @param path the directory path
     */
    public void doList(String path) {
        //D.deb("doList('"+path+"')"); // NOI18N
        //System.out.println("VcsAction.doList("+path+")");
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
        CommandExecutorSupport.doRefresh(fileSystem, path, true);
        /*
        //D.deb("doListSub('"+path+"')"); // NOI18N
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        FileCacheProvider cache = fileSystem.getCacheProvider();
        if (statusProvider == null) return;
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_REFRESH_RECURSIVELY);
        String dirName = ""; // NOI18N
        if (cache == null || cache.isDir(path)) {
            dirName = path;
        }
        else{
            dirName = VcsUtilities.getDirNamePart(path);
        }
        //Object exec = VcsCommandIO.getCommandProperty(cmd, "exec", String.class);
        Object exec = cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        if (cmd != null && (exec != null && ((String) exec).trim().length() > 0)) {
            statusProvider.refreshDirRecursive(dirName);
        } else {
            RetrievingDialog rd = new RetrievingDialog(fileSystem, dirName, new JFrame(), false);
            VcsUtilities.centerWindow(rd);
            Thread t = new Thread(rd, "VCS Recursive Retrieving Thread - "+dirName); // NOI18N
            t.start();
        }
         */
    }

    /*
     * Whether the files should be locked in VCS. This command does not save the file contents.
     * Executes the <code>VcsCommand.NAME_SHOULD_DO_LOCK</code> command, which should
     * find out, whether the file is already locked.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     * @return true if the command succeeds and therefore the file should be locked,
     *         false otherwise
     *
    public static boolean shouldDoLock(Table files, VcsFileSystem fileSystem) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_SHOULD_DO_LOCK);
        if (cmd != null) {
            VcsCommandExecutor[] execs = doCommand(files, cmd, null, fileSystem, null, null, null, null, false);
            for (int i = 0; i < execs.length; i++) {
                if (execs[i].getExitStatus() != VcsCommandExecutor.SUCCEEDED) return false;
            }
        }
        return true;
    }
     */
    
    /**
     * Lock files in VCS. This command does not save the file contents.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     */
    public static void doLock(Table files, VcsFileSystem fileSystem) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_LOCK);
        if (cmd != null) {
            doCommand(files, cmd, null, fileSystem, null, null, null, null, false);
        }
    }
    
    /**
     * Unlock files in VCS.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     */
    public static void doUnlock(Table files, VcsFileSystem fileSystem) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_UNLOCK);
        if (cmd != null) doCommand(files, cmd, null, fileSystem);
    }
    
    /**
     * Prepare for edit files in VCS.
     * Note that this method has to block until the command is finished.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     */
    public static void doEdit(Table files, VcsFileSystem fileSystem) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_EDIT);
        if (cmd != null) {
            doCommand(files, cmd, null, fileSystem);
            //fileSystem.getCommandsPool().waitToFinish(cmd, files.keySet());
        }
    }

    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param fileSystem the VCS file system
     * @return the command executors of all executed commands.
     */
    public static VcsCommandExecutor[] doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, VcsFileSystem fileSystem) {
        return doCommand(files, cmd, additionalVars, fileSystem, null, null, null, null);
    }
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param fileSystem the VCS file system
     * @return the command executors of all executed commands.
     */
    public static VcsCommandExecutor[] doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, VcsFileSystem fileSystem,
                                                 CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                                                 CommandDataOutputListener stdoutDataListener, CommandDataOutputListener stderrDataListener) {
        return doCommand(files, cmd, additionalVars, fileSystem, stdoutListener, stderrListener, stdoutDataListener, stderrDataListener, true);
    }
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param fileSystem the VCS file system
     * @param saveProcessingFiles whether save processing files prior command execution
     * @return the command executors of all executed commands.
     */
    public static VcsCommandExecutor[] doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, VcsFileSystem fileSystem,
                                                 CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                                                 CommandDataOutputListener stdoutDataListener, CommandDataOutputListener stderrDataListener,
                                                 boolean saveProcessingFiles) {
        //System.out.println("doCommand("+files+", "+cmd+")");
        if (files.size() == 0) return new VcsCommandExecutor[0];
        if (saveProcessingFiles) {
            assureFilesSaved(files.values());
        }
        if (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_NEEDS_HIERARCHICAL_ORDER)) {
            files = createHierarchicalOrder(files);
        }
        ArrayList executors = new ArrayList();
        boolean[] askForEachFile = null;
        String quoting = fileSystem.getQuoting();
        if (files.size() > 1) {
            askForEachFile = new boolean[1];
            askForEachFile[0] = true;
        }
        int preprocessStatus;
        boolean cmdCanRunOnMultipleFiles = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES);
        CommandsPool pool = fileSystem.getCommandsPool();
        VcsCommandExecutor vce;
        Hashtable vars = fileSystem.getVariablesAsHashtable();
        if (additionalVars != null) vars.putAll(additionalVars);
        do {
            setVariables(files, vars, quoting);
            vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            preprocessStatus = pool.preprocessCommand(vce, vars, askForEachFile);
            //System.out.println("VcsAction.doCommand(): CommandsPool.preprocessCommand() = "+preprocessStatus+", askForEachFile = "+((askForEachFile.length > 0) ? ""+askForEachFile : ""+askForEachFile[0]));
            if (CommandsPool.PREPROCESS_CANCELLED == preprocessStatus) {
                vce = null;
                break;
            }
            if (!cmdCanRunOnMultipleFiles) {
                // When the executor can not run on more than one file, it has to be processed one by one.
                preprocessStatus = CommandsPool.PREPROCESS_NEXT_FILE;
            }
            if (files.size() == 1) preprocessStatus = CommandsPool.PREPROCESS_DONE;
            if (CommandsPool.PREPROCESS_NEXT_FILE == preprocessStatus) {
                Table singleFileTable = new Table();
                Object singleFile = files.keys().nextElement();
                singleFileTable.put(singleFile, files.get(singleFile));
                setVariables(singleFileTable, vars, quoting);
            }
            executors.add(vce);
            if (stdoutListener != null) vce.addOutputListener(stdoutListener);
            if (stderrListener != null) vce.addErrorOutputListener(stderrListener);
            if (stdoutDataListener != null) vce.addDataOutputListener(stdoutDataListener);
            if (stderrDataListener != null) vce.addDataErrorOutputListener(stderrDataListener);
            pool.startExecutor(vce);
            if (CommandsPool.PREPROCESS_NEXT_FILE == preprocessStatus) {
                files.remove(files.keys().nextElement()); // remove the processed file
                synchronized (vars) {
                    if (askForEachFile != null && askForEachFile[0] == true) {
                        vars = new Hashtable(fileSystem.getVariablesAsHashtable());
                        if (additionalVars != null) vars.putAll(additionalVars);
                    } else {
                        vars = new Hashtable(vars);
                    }
                }
                if (files.size() == 1 && askForEachFile != null && askForEachFile[0] == true) {
                    askForEachFile = null; // Do not show the check box for the last file.
                }
            }
        } while (CommandsPool.PREPROCESS_NEXT_FILE == preprocessStatus);
        //System.out.println("VcsAction.doCommand(): executors started = "+executors.size());
        return (VcsCommandExecutor[]) executors.toArray(new VcsCommandExecutor[executors.size()]);
    }
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     */
    private void doCommand(Table files, VcsCommand cmd) {
        VcsAction.doCommand(files, cmd, null, fileSystem);
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
    protected boolean isOnDirectory() {
        boolean is = false;
        if (selectedFileObjects != null) {
            for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo.isFolder()) is = true;
            }
            //return false;
        } else {
            Node[] nodes = getActivatedNodes();
            for (int i = 0; i < nodes.length; i++) {
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd != null && dd.getPrimaryFile().isFolder()) is = true;
            }
            //return nodes.length > 0;
        }
        return is && !isOnRoot();
    }

    /**
     * Test if some of the selected nodes are files.
     * @return <code>true</code> if some of the selected nodes are files,
     *         <code>false</code> otherwise.
     */
    protected boolean isOnFile() {
        if (selectedFileObjects != null) {
            for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (!fo.isFolder()) return true;
            }
            return false;
        }
        Node[] nodes = getActivatedNodes();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd != null && !dd.getPrimaryFile().isFolder()) return true;
        }
        return false;
    }

    /**
     * Test if one of the selected nodes is the root node.
     * @return <code>true</code> if at least one of the selected nodes is the root node,
     *         <code>false</code> otherwise.
     */
    protected boolean isOnRoot() {
        if (selectedFileObjects != null) {
            for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo.getPackageNameExt('/', '.').length() == 0) return true;
            }
            return false;
        }
        Node[] nodes = getActivatedNodes();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd == null) return false;
            String path = dd.getPrimaryFile().getPackageNameExt('/','.');
            //String path = getNodePath(nodes[i]);
            if (path.length() == 0) return true;
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
    
    private Set getSelectedFileStatusAttributes() {
        Set statuses = new HashSet();
        FileStatusProvider statusProv = fileSystem.getStatusProvider();
        if (statusProv != null) {
            if (selectedFileObjects != null) {
                for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
                    FileObject fo = (FileObject) it.next();
                    String path = fo.getPackageNameExt('/', '.');
                    if (fileSystem.isImportant(path)) {
                        String status = statusProv.getFileStatus(path);
                        if (status != null) statuses.add(status);
                    }
                }
            } else {
                Node[] nodes = getActivatedNodes();
                for (int i = 0; i < nodes.length; i++) {
                    DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                    if (dd == null) continue;
                    Set files = dd.files();
                    for (Iterator it = files.iterator(); it.hasNext(); ) {
                        FileObject fo = (FileObject) it.next();
                        String path = fo.getPackageNameExt('/', '.');
                        if (fileSystem.isImportant(path)) {
                            String status = statusProv.getFileStatus(path);
                            if (status != null) statuses.add(status);
                        }
                    }
                }
            }
        }
        return statuses;
    }

    /**
     * Create the command menu item.
     * @param name tha name of the command
     */
    protected JMenuItem createItem(String name){
        JMenuItem item = null;
        VcsCommand cmd = fileSystem.getCommand(name);

        if (cmd == null) {
            //E.err("Command "+name+" not configured."); // NOI18N
            item = new JMenuItem("'"+name+"' not configured.");
            item.setEnabled(false);
            return item;
        }

        //Hashtable vars=fileSystem.getVariablesAsHashtable();
        String label = cmd.getDisplayName();
        //if (label.indexOf('$') >= 0) {
        //    Variables v = new Variables();
        //    label = v.expandFast(vars, label, true);
        //}
        //System.out.println("VcsAction.createItem("+name+"): menu '"+label+"' created.");
        item = new JMenuItem(label);
        String[] props = cmd.getPropertyNames();
        if (props != null && props.length > 0) {
            item.setActionCommand(cmd.getName());
            item.addActionListener(this);
        }
        return item;
    }

    /**
     * Add a popup submenu.
     */
    private void addMenu(Node commands, JMenu parent, boolean onDir, boolean onFile,
                         boolean onRoot, Set statuses) {
        Children children = commands.getChildren();
        for (Enumeration subnodes = children.nodes(); subnodes.hasMoreElements(); ) {
            Node child = (Node) subnodes.nextElement();
            VcsCommand cmd = (VcsCommand) child.getCookie(VcsCommand.class);
            if (cmd == null) {
                parent.addSeparator();
                continue;
            }
            //System.out.println("VcsAction.addMenu(): cmd = "+cmd.getName());
            if (cmd.getDisplayName() == null
                || onDir && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_DIR)
                || onFile && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_FILE)
                || onRoot && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_ROOT)
                || VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)) {

                continue;
            }
            boolean disabled = VcsUtilities.matchQuotedStringToSet(
                (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS), statuses);
            if (disabled && REMOVE_DISABLED) continue;
            JMenuItem item;
            if (!child.isLeaf()) {
                JMenu submenu;
                String[] props = cmd.getPropertyNames();
                //if (props == null || props.length == 0) {
                submenu = new JMenuPlus(cmd.getDisplayName());
                //} else {
                //    submenu = new JMenuPlus();
                //}
                addMenu(child, submenu, onDir, onFile, onRoot, statuses);
                parent.add(submenu);
                item = submenu;
            } else {
                item = createItem(cmd.getName());
                parent.add(item);
            }
            if (disabled) {
                item.setEnabled(false);
            }
        }
    }

    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        boolean onRoot = isOnRoot();
        boolean onDir;
        boolean onFile;
        if (onRoot) {
            onDir = onFile = false;
        } else {
            onDir = isOnDirectory();
            onFile = isOnFile();
        }
        Set statuses = getSelectedFileStatusAttributes();
        JInlineMenu inlineMenu = new JInlineMenu();
        ArrayList menuItems = new ArrayList();
        for (int i = 0; i < actionCommandsSubTrees.length; i++) {
            JMenuItem menuItem = getPopupPresenter(actionCommandsSubTrees[i], onDir,
                                                   onFile, onRoot, statuses);
            if (menuItem != null) menuItems.add(menuItem);
        }
        inlineMenu.setMenuItems((JMenuItem[]) menuItems.toArray(new JMenuItem[menuItems.size()]));
        return inlineMenu;
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    private JMenuItem getPopupPresenter(Node commandRoot, boolean onDir, boolean onFile,
                                        boolean onRoot, Set statuses) {
        String name = commandRoot.getDisplayName();
        /*
        if (name == null) {
            name = fileSystem.getBundleProperty("CTL_Version_Control");
        }
         */
        JMenuItem menu = new JMenuPlus(name);
        addMenu(commandRoot, /*first, lastOrder, */(JMenu) menu, onDir, onFile, onRoot, statuses);
        if (menu.getSubElements().length == 0) {
            VcsCommand cmd = (VcsCommand) commandRoot.getCookie(VcsCommand.class);
            if (cmd == null) {
                //menu = new JPopupMenu.Separator();
            } else {
                if (cmd.getDisplayName() == null
                    || onDir && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_DIR)
                    || onFile && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_FILE)
                    || onRoot && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_ROOT)
                    || VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)) {
                        menu = null;
                } else {
                    menu = createItem(cmd.getName());
                }
            }
        }
        return menu;
    }

    /**
     * Test whether the action should be enabled based on the currently activated nodes.
     * @return true for non-empty set of nodes.
     */
    public boolean enable(Node[] nodes) {
        return (nodes.length > 0);
    }
    
    /** Add files specific variables.
     * The following variables are added:
     * <br>PATH - the full path to the first file from the filesystem root
     * <br>DIR - the directory of the first file from the filesystem root
     * <br>FILE - the first file
     * <br>QFILE - the first file quoted
     * <br>MIMETYPE - the MIME type of the first file
     * <br>
     * <br>FILES - all files delimeted by the system file separator
     * <br>PATHS - full paths to all files delimeted by two system file separators
     * <br>QPATHS - full paths to all files quoted by filesystem quotation string and delimeted by spaces
     * <br>NUM_FILES - the number of files
     * <br>MULTIPLE_FILES - "true" when more than one file is to be processed, "" otherwise
     *
     * @param files the table of files
     * @param vars the table of variables to extend
     * @param quoting the quotation string used when more than one file is to be processed
     */
    protected static void setVariables(Table files, Hashtable vars, String quoting) {
        // At first, find the first file and set the variables
        String fullName = (String) files.keys().nextElement();
        FileObject fo = (FileObject) files.get(fullName);
        String path = VcsUtilities.getDirNamePart(fullName);
        String file = VcsUtilities.getFileNamePart(fullName);
        path = path.replace('/', java.io.File.separatorChar);
        fullName = fullName.replace('/', java.io.File.separatorChar);
        vars.put("PATH", fullName); // NOI18N
        vars.put("QPATH", (fullName.length() > 0) ? quoting+fullName+quoting : fullName); // NOI18N
        vars.put("DIR", path); // NOI18N
        if (path.length() == 0 && file.length() > 0 && file.charAt(0) == '/') file = file.substring (1, file.length ());
        vars.put("FILE", file); // NOI18N
        vars.put("QFILE", quoting+file+quoting); // NOI18N
        vars.put("MIMETYPE", fo.getMIMEType());
        // Second, set the multifiles variables
        StringBuffer qpaths = new StringBuffer();
        StringBuffer paths = new StringBuffer();
        StringBuffer vfiles = new StringBuffer();
        for (Enumeration enum = files.keys(); enum.hasMoreElements(); ) {
            fullName = (String) enum.nextElement();
            file = VcsUtilities.getFileNamePart(fullName);
            fullName = fullName.replace('/', java.io.File.separatorChar);
            vfiles.append(file);
            vfiles.append(java.io.File.separator);
            paths.append(fullName);
            paths.append(java.io.File.separator+java.io.File.separator);
            qpaths.append(quoting);
            qpaths.append(fullName);
            qpaths.append(quoting);
            qpaths.append(" ");
        }
        vars.put("FILES", vfiles.delete(vfiles.length() - 1, vfiles.length()).toString());
        vars.put("PATHS", paths.delete(paths.length() - 2, paths.length()).toString());
        vars.put("QPATHS", qpaths.toString().trim());
        vars.put("NUM_FILES", ""+files.size());
        vars.put("MULTIPLE_FILES", (files.size() > 1) ? "true" : "");
    }
    
    protected void performCommand(final String cmdName, final Node[] nodes) {
        System.out.println("performCommand("+cmdName+")");// on "+nodes.length+" nodes.");
        /* should not be used any more:
        if (cmdName.equals("KILL_ALL_CMDS")) {
            killAllCommands();
            return;
        }
         */
        final VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) return;
        boolean processAll = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_PROCESS_ALL_FILES) || fileSystem.isProcessUnimportantFiles();
        Table files = new Table();
        //String mimeType = null;
        //String path = "";
        boolean refreshDone = false;
        if (selectedFileObjects != null) {
            addImportantFiles(selectedFileObjects, files, processAll, true);
        } else {
            for(int i = 0; i < nodes.length; i++) {
                //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd != null) addImportantFiles(dd.files(), files, processAll, false);
                else continue;
            }
        }
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

    public void performAction(Node[] nodes) {
        //D.deb("performAction()"); // NOI18N
        //System.out.println("performAction("+nodes+")");
    }

    public void actionPerformed(final java.awt.event.ActionEvent e){
        //D.deb("actionPerformed("+e+")"); // NOI18N
        //System.out.println("actionPerformed("+e+")");
        final String cmdName = e.getActionCommand();
        //D.deb("cmd="+cmd); // NOI18N
        Runnable cpr;
        if (selectedFileObjects != null) {
            cpr = new Runnable() {
                public void run() {
                    performCommand(cmdName, null);
                }
            };
        } else {
            final Node[] nodes = getActivatedNodes();
            cpr = new Runnable() {
                public void run() {
                    performCommand(cmdName, nodes);
                }
            };
        }
        new Thread(cpr, "Vcs Commands Performing Thread").start();
    }
    
}
