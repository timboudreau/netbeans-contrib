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
import org.openide.util.actions.*;
import org.openide.util.HelpCtx;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.loaders.*;

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

    protected VcsFileSystem fileSystem = null;
    protected Collection selectedFileObjects = null;
    
    private int actionCommandSubtree; // the command subtree to construct actions from

    public VcsAction(VcsFileSystem fileSystem) {
        this(fileSystem, null, 0);
    }

    public VcsAction(VcsFileSystem fileSystem, int commandSubtree) {
        this(fileSystem, null, commandSubtree);
    }

    public VcsAction(VcsFileSystem fileSystem, Collection fos) {
        this(fileSystem, fos, 0);
    }

    public VcsAction(VcsFileSystem fileSystem, Collection fos, int commandSubtree) {
        setFileSystem(fileSystem);
        setSelectedFileObjects(fos);
        actionCommandSubtree = commandSubtree;
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    protected void setSelectedFileObjects(Collection fos) {
        this.selectedFileObjects = fos;
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
    }

    /**
     * Lock files in VCS.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     */
    public static void doLock(Table files, VcsFileSystem fileSystem) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_LOCK);
        if (cmd != null) doCommand(files, cmd, null, fileSystem);
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
     * @return the command executor of the last executed command.
     */
    public static VcsCommandExecutor doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, VcsFileSystem fileSystem) {
        //System.out.println("doCommand("+files+", "+cmd+")");
        boolean[] askForEachFile = null;
        String quoting = null;
        if (files.size() > 1) {
            askForEachFile = new boolean[1];
            askForEachFile[0] = true;
            quoting = fileSystem.getQuoting();
        }
        int preprocessStatus;
        boolean cmdCanRunOnMultipleFiles = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES);
        CommandsPool pool = fileSystem.getCommandsPool();
        VcsCommandExecutor vce;
        Hashtable vars = fileSystem.getVariablesAsHashtable();
        if (additionalVars != null) vars.putAll(additionalVars);
        do {
            setVariables(files, vars, quoting);
            vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            preprocessStatus = pool.preprocessCommand(vce, vars, askForEachFile);
            if (CommandsPool.PREPROCESS_CANCELLED == preprocessStatus) {
                vce = null;
                break;
            }
            pool.startExecutor(vce);
            if (!cmdCanRunOnMultipleFiles) {
                // When the executor can not run on more than one file, it has to be processed one by one.
                preprocessStatus = CommandsPool.PREPROCESS_NEXT_FILE;
            }
            if (files.size() == 1) preprocessStatus = CommandsPool.PREPROCESS_DONE;
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
                if (files.size() == 1 && askForEachFile != null) {
                    askForEachFile = null; // Do not show the check box for the last file.
                }
            }
        } while (CommandsPool.PREPROCESS_NEXT_FILE == preprocessStatus);
        return vce;
    }
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param fileSystem the VCS file system
     * @return the command executor of the last executed command.
     *
    public static VcsCommandExecutor doCommand_OLD(Table files, VcsCommand cmd, Hashtable additionalVars, VcsFileSystem fileSystem) {
        //System.out.println("doCommand("+files+", "+cmd+")");
        boolean[] askForEachFile = null;
        if (files.size() > 1) {
            askForEachFile = new boolean[1];
            askForEachFile[0] = true;
        }
        Hashtable vars = fileSystem.getVariablesAsHashtable();
        VcsCommandExecutor ec = null;
        //Integer synchAccess = new Integer(0);
        //int n = files.size();
        Enumeration filesEnum = files.keys();
        while(filesEnum.hasMoreElements()) {
            String fullName = (String) filesEnum.nextElement();
            if (!filesEnum.hasMoreElements() && askForEachFile != null && askForEachFile[0] == true) {
                askForEachFile = null; // Do not show the check box for the last file.
            }
            /*
            boolean concurrentExecution = false;
            if (!concurrentExecution && ec != null) {
                try {
                    ec.join();
                } catch (InterruptedException e) {
                    // ignoring the interruption -- continuing for next command
                }
            }
             *
            fileSystem.getCommandsPool().waitToRun(cmd, Collections.singletonList(fullName));
            if (additionalVars != null) vars.putAll(additionalVars);
            ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            if (ec == null) return null; // No executor for this command.
            fileSystem.getCommandsPool().add(ec);
            String exec = VcsAction.prepareCommandToExecute(fileSystem, ec, cmd, vars, /*additionalVars, *fullName, (FileObject) files.get(fullName), askForEachFile);
            if (exec == null) return null; // The whole command is canceled
            if (exec.length() == 0) continue; // The command is canceled for this file
            //OutputContainer container = new OutputContainer(cmd);
            //ec = new ExecuteCommand(fileSystem, cmd, vars, exec);
            //ec.setErrorNoRegexListener(container);
            //ec.setOutputNoRegexListener(container);
            //ec.setErrorContainer(container);
            ec.updateExec(exec);
            fileSystem.getCommandsPool().startExecutor(ec);
            //ec.start();
            //cache.setFileStatus(fullName,"Unknown"); // NOI18N
            synchronized (vars) {
                if (askForEachFile != null && askForEachFile[0] == true) {
                    vars = new Hashtable(fileSystem.getVariablesAsHashtable());
                } else {
                    vars = new Hashtable(vars);
                }
            }
        }
        return ec;
    }
     */
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     */
    private void doCommand(Table files, VcsCommand cmd) {
        VcsAction.doCommand(files, cmd, null, fileSystem);
    }

    /**
     * Test if one of the selected nodes is a directory.
     * @return <code>true</code> if at least one selected node is a directory,
     *         <code>false</code> otherwise.
     */
    protected boolean isOnDirectory() {
        if (selectedFileObjects != null) {
            for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo.isFolder()) return true;
            }
            return false;
        }
        Node[] nodes = getActivatedNodes();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd != null && dd.getPrimaryFile().isFolder()) return true;
        }
        return false;
    }

    /**
     * Test if one of the selected nodes is a file.
     * @return <code>true</code> if at least one selected node is a file,
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
     * @return <code>true</code> if at least one selected node is the root node,
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
     */
    protected void addImportantFiles(Collection fos, Table res) {
        addImportantFiles(fos, res, false);
    }

    /**
     * Add files.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs.
     * @param all whether to add unimportant files as well
     */
    protected void addImportantFiles(Collection fos, Table res, boolean all){
        for(Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject ff = (FileObject) it.next();
            try {
                if (ff.getFileSystem() != fileSystem)
                    continue;
            } catch (FileStateInvalidException exc) {
                continue;
            }
            String fileName = ff.getPackageNameExt('/','.');
            //VcsFile file = fileSystem.getCache().getFile(fileName);
            //D.deb("file = "+file+" for "+fileName);
            //if (file == null || file.isImportant()) {
            if (all || fileSystem.isImportant(fileName)) {
                D.deb(fileName+" is important");
                res.put(fileName, ff);
            }
            else D.deb(fileName+" is NOT important");
        }
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
    private void addMenu(Node commands, JMenu parent,
                        boolean onDir, boolean onFile, boolean onRoot) {
        Children children = commands.getChildren();
        for (Enumeration subnodes = children.nodes(); subnodes.hasMoreElements(); ) {
            Node child = (Node) subnodes.nextElement();
            VcsCommand cmd = (VcsCommand) child.getCookie(VcsCommand.class);
            if (cmd == null) {
                parent.addSeparator();
                continue;
            }
            if (cmd.getDisplayName() == null
                || onDir && !VcsCommandIO.getBooleanPropertyAssumeTrue(cmd, VcsCommand.PROPERTY_ON_DIR)
                || onFile && !VcsCommandIO.getBooleanPropertyAssumeTrue(cmd, VcsCommand.PROPERTY_ON_FILE)
                || onRoot && !VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_ON_ROOT)
                || VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_HIDDEN)) {

                continue;
            }
            if (!child.isLeaf()) {
                JMenu submenu;
                String[] props = cmd.getPropertyNames();
                //if (props == null || props.length == 0) {
                submenu = new JMenuPlus(cmd.getDisplayName());
                //} else {
                //    submenu = new JMenuPlus();
                //}
                addMenu(child, submenu, onDir, onFile, onRoot);
                parent.add(submenu);
            } else {
                JMenuItem item = createItem(cmd.getName());
                parent.add(item);
            }
        }
    }

    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        //JMenuItem item=null;
        //Vector commands = fileSystem.getCommands();
        Node commands = fileSystem.getCommands();
        //int len = commands.size();
        //int[] lastOrder = new int[0];
        boolean onRoot = isOnRoot();
        boolean onDir;
        boolean onFile;
        if (onRoot) {
            onDir = onFile = false;
        } else {
            onDir = isOnDirectory();
            onFile = isOnFile();
        }
        Children children = commands.getChildren();
        Node[] commandRoots = children.getNodes();
        if (commandRoots.length <= actionCommandSubtree) return null;
        //int first = 0;
        String name = commandRoots[actionCommandSubtree].getDisplayName();
        /*
        if (len > 0) {
            VcsCommand uc = (VcsCommand) commands.get(first);
            String[] props = uc.getPropertyNames();
            if ((props == null || props.length == 0) && uc.getOrder().length == 1) {
                first++;
                name = uc.getLabel();
            }
        }
        if (name == null) {
            name = fileSystem.getBundleProperty("CTL_Version_Control");
        }
         */
        JMenu menu = new JMenuPlus(name);
        addMenu(commandRoots[actionCommandSubtree], /*first, lastOrder, */menu, onDir, onFile, onRoot);
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
     * <br>MIMETYPE - the MIME type of the first file
     * <br>
     * <br>When more then one file is to be processed, following additional variables are defined:
     * <br>FILES - all files delimeted by the system file separator
     * <br>PATHS - full paths to all files delimeted by two system file separators
     * <br>QPATHS - full paths to all files quoted by filesystem quotation string and delimeted by spaces
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
        vars.put("DIR", path); // NOI18N
        if (path.length() == 0 && file.length() > 0 && file.charAt(0) == '/') file = file.substring (1, file.length ());
        vars.put("FILE", file); // NOI18N
        vars.put("MIMETYPE", fo.getMIMEType());
        // Second, set the multifiles variables
        if (files.size() > 1) {
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
        }
    }

    /*
     * Perform some initial steps before the command can be executed.
     * @return the exec String to run the command on success,
     *         null when execution of this and all associated commands should be cancelled,
     *         an empty String when this command should be skipped, but successive commands
     *         should be run.
     *
    public static String prepareCommandToExecute(VcsFileSystem fileSystem, VcsCommandExecutor ec, VcsCommand cmd,
                                                 Hashtable vars, /*Hashtable additionalVars, *
                                                 String fullName, FileObject fo, boolean[] askForEachFile) {
        String path=""; // NOI18N
        String file=""; // NOI18N
        //if( fileSystem.folder(fullName) ){
        //path=fullName;
        //file=""; // NOI18N
        //}
        //else{
        path = VcsUtilities.getDirNamePart(fullName);
        file = VcsUtilities.getFileNamePart(fullName);
        //}
        
        if (java.io.File.separatorChar != '/') {
            path = path.replace('/', java.io.File.separatorChar);
            fullName = fullName.replace('/', java.io.File.separatorChar);
        }
        vars.put("PATH",fullName); // NOI18N
        vars.put("DIR",path); // NOI18N
        String osName=System.getProperty("os.name");
        //D.deb("osName="+osName); // NOI18N
        if (path.length() == 0 && file.length() > 0 && file.charAt(0) == '/') file = file.substring (1, file.length ());
        vars.put("FILE",file); // NOI18N
        vars.put("MIMETYPE", fo.getMIMEType());
        /*
        if (additionalVars != null) {
            Enumeration keys = additionalVars.keys();
            while(keys.hasMoreElements()) {
                Object key = keys.nextElement();
                vars.put(key, additionalVars.get(key));
            }
        }
         *
        //if (path.length() == 0) vars.put("DIR", "."); // NOI18N
        
        Object confObj = cmd.getProperty(VcsCommand.PROPERTY_CONFIRMATION_MSG);
        String confirmation = (confObj == null) ? "" : (String) confObj; //cmd.getConfirmationMsg();
        if (fileSystem.isImportant(fullName)) {
            confirmation = Variables.expand(vars, confirmation, true);
        } else {
            confirmation = null;
        }
        if (confirmation != null && confirmation.length() > 0) {
            if (NotifyDescriptor.Confirmation.NO_OPTION.equals (
                    TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (
                        confirmation, NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
                return ""; // The command is cancelled for that file
            }
        }
        
        String exec = ec.preprocessCommand(cmd, vars);
        //PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, cmd, vars);
        //String exec = cmdPerf.process();
        //D.deb("exec from performer = "+exec);
        if (!fileSystem.promptForVariables(exec, vars, cmd, askForEachFile)) {
            fileSystem.debug(fileSystem.getBundleProperty("MSG_CommandCanceled")+"\n"); // NOI18N
            return null;
        }
        
        fileSystem.setNumDoAutoRefresh(fileSystem.getNumDoAutoRefresh(path) + 1, path);
        return exec;
    }
     */
    
    protected void performCommand(final String cmdName, final Node[] nodes) {
        //System.out.println("performCommand("+cmdName+") on "+nodes.length+" nodes.");
        if (cmdName.equals("KILL_ALL_CMDS")) {
            killAllCommands();
            return;
        }
        final VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) return;
        boolean processAll = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_PROCESS_ALL_FILES) || fileSystem.isProcessUnimportantFiles();
        Table files = new Table();
        //String mimeType = null;
        //String path = "";
        boolean refreshDone = false;
        if (selectedFileObjects != null) {
            addImportantFiles(selectedFileObjects, files, processAll);
            //files.put(selectedFileObject.getPackageNameExt('/','.'), selectedFileObject);
            //mimeType = selectedFileObject.getMIMEType();
            /*
            if (cmdName.equals(VcsCommand.NAME_REFRESH)) {
                path = selectedFileObject.getPackageName('/');//getNodePath(nodes[i]);
                doList(path);
                refreshDone = true;
            } else if (cmdName.equals(VcsCommand.NAME_REFRESH_RECURSIVELY)) {
                path = selectedFileObject.getPackageName('/');//getNodePath(nodes[i]);
                doListSub(path);
                refreshDone = true;
            }
             */
        } else {
            for(int i = 0; i < nodes.length; i++) {
                //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd != null) addImportantFiles(dd.files(), files, processAll);
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
        }
        /*
                //FileObject ff = dd.getPrimaryFile();
                //mimeType = ff.getMIMEType();
                //ec = (EditorCookie) nodes[i].getCookie(EditorCookie.class);
                if (cmdName.equals(VcsCommand.NAME_REFRESH)) {
                    path = dd.getPrimaryFile().getPackageName('/'); //getNodePath(nodes[i]);
                    doList(path);
                    refreshDone = true;
                } else if (cmdName.equals(VcsCommand.NAME_REFRESH_RECURSIVELY)) {
                    path = dd.getPrimaryFile().getPackageName('/'); //getNodePath(nodes[i]);
                    doListSub(path);
                    refreshDone = true;
                }
            }
         */
            //D.deb("files="+files); // NOI18N
            /*
            if (nodes.length < 1) {
                E.err("No selected nodes error."); // NOI18N
                return ;
            }
             */
        //}

        //D.deb("path='"+path+"'"); // NOI18N

        //if (mimeType != null) additionalVars.put("MIMETYPE", mimeType); // NOI18N
        //D.deb("I have MIME = "+mimeType); // NOI18N

        //System.out.println("refreshDone = "+refreshDone+", files.size() = "+files.size());
        else if (/*!refreshDone &&*/ files.size() > 0) {
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
