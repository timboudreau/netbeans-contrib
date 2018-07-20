/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.vcscore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.api.vcs.FileStatusInfo;
import org.netbeans.modules.vcscore.caching.IgnoreListSupport;
import org.netbeans.modules.vcscore.caching.StatusFormat;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.ActionCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.runtime.RuntimeFolderNode;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.util.VariableInputDescriptorCompat;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.versioning.RevisionEvent;
import org.netbeans.modules.vcscore.versioning.RevisionListener;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;
import org.openide.util.Utilities;

import org.netbeans.api.queries.SharabilityQuery;
import org.openide.filesystems.FileObject;

/**
 *
 * @author  Martin Entlicher
 */
public class VcsProvider implements CommandExecutionContext, FilesModificationSupport {
    
    /** Property name giving root folder of the provider. */
    public static final String PROP_ROOT = "root"; // NOI18N
    /** Property name giving display name of the provider. */
    public static final String PROP_DISPLAY_NAME = "displayName"; // NOI18N
    
    public static final String PROP_VARIABLES = "variables"; // NOI18N
    public static final String PROP_COMMANDS = "commands"; // NOI18N
    public static final String PROP_COMMAND_NOTIFICATION = "commandNotification"; // NOI18N
    public static final String PROP_ANNOTATION_PATTERN = "annotationPattern"; // NOI18N
    public static final String PROP_CALL_EDIT = "edit"; // NOI18N
    public static final String PROP_CALL_EDIT_PROMPT = "editPrompt"; // NOI18N
    public static final String PROP_CALL_LOCK = "lock"; // NOI18N
    public static final String PROP_CALL_LOCK_PROMPT = "lockPrompt"; // NOI18N
    public static final String PROP_PASSWORD = "password"; // NOI18N
    public static final String PROP_REMEMBER_PASSWORD = "rememberPassword"; // NOI18N
    public static final String PROP_EXPERT_MODE = "expertMode"; // NOI18N
    public static final String PROP_PROMPT_FOR_VARS_FOR_EACH_FILE = "promptForVarsForEachFile"; // NOI18N
    public static final String PROP_FILE_FILTER = "fileFilter"; // NOI18N
    
    protected static final String PROP_NOT_MODIFIABLE_STATUSES = "notModifiableStatuses"; // NOI18N
    
    public static final String VCS_PROVIDER_ICON_BASE = "VCS Icon Base";
    
    /**
     * This variable can contain the display name, which is prepended to the
     * root path of the file system to create the display name.
     */
    public static final String VAR_FS_DISPLAY_NAME = "FS_DISPLAY_NAME"; // NOI18N
    /**
     * This variable can contain the whole annotation of file system display name.
     */
    public static final String VAR_FS_DISPLAY_NAME_ANNOTATION = "FS_DISPLAY_NAME_ANNOTATION"; // NOI18N
    
    /**
     * This is a prefix, for environment variable. If a variable name starts with
     * this prefix, it is considered as an environment variable, and its value
     * is added to the process environment.
     */
    public static final String VAR_ENVIRONMENT_PREFIX = "ENVIRONMENT_VAR_";
    /**
     * This is a prefix, for environment variable, that should be removed from the command
     * environment. If a variable name starts with this prefix, variable of this name is
     * removed from the list of environment variables. It's value is ignored.
     */
    public static final String VAR_ENVIRONMENT_REMOVE_PREFIX = "ENVIRONMENT_REMOVE_VAR_";
    
    /** The unique type of VCS that is provided. */
    public static final String VAR_VCS_TYPE = "VCS_TYPE"; // NOI18N
    
    /**
     * The test on this variable can be performed in the exec string to decide
     * what options to use or what to ask the user for. When the expert mode
     * is on, this variable is non empty.
     */
    public static final String VAR_EXPERT_MODE = "EXPERT_MODE"; // NOI18N

    public static final String VAR_TRUE = "true"; // NOI18N
    public static final String VAR_FALSE = "false"; // NOI18N

    private static final String LOCK_FILES_ON = "LOCKFILES"; // NOI18N
    private static final String PROMPT_FOR_LOCK_ON = "PROMPTFORLOCK"; // NOI18N
    private static final String EDIT_FILES_ON = "CALLEDITONFILES"; // NOI18N
    private static final String PROMPT_FOR_EDIT_ON = "PROMPTFOREDIT"; // NOI18N

    private static volatile File last_rootFile = new File(System.getProperty("user.home")); // NOI18N
    
    /** The root file */
    private File rootFile = last_rootFile;
    
    /** The display name of the filesystem */
    private transient String displayName;
    
    /** The table of variable names and appropriate VcsConfigVariable objects */
    private Hashtable variablesByName = new Hashtable();
    private Map variableValues = new HashMap();
    
    private VariableValueAdjustment varValueAdjustment;

    private volatile boolean commandNotification = true;
    
    private volatile boolean offLine;
    
    /** The expert mode. When true, the user might be prompted for other options. */
    private boolean expertMode = false;

    private boolean lockFilesOn = false;
    private boolean promptForLockOn = true;
    
    private boolean promptForEditOn = true;
    private boolean callEditFilesOn = true;

    private String password = null;

    private boolean rememberPassword = false;

    /**
     * Whether to prompt the user for variables for each selected file. Value of this variable
     * will be the default value in the VariableInputDialog and changing the value there will
     * change the value of this variable.
     */
    private boolean promptForVarsForEachFile = false;
    
    private String annotationPattern;
    private int fileAnnotation;

    /**
     * This variable stores the environment variables and their values
     * in the form: "VAR1=Value1", "VAR2=Value2", etc.
     */
    private transient String[] environmentVars = null;

    private transient VcsCommandsProvider commandsProvider = new DefaultVcsCommandsProvider(new CommandsTree(null));
    private transient CommandsTree commandsRoot = null;
    private transient Map commandsByName = null;
    private transient VcsActionSupporter actionSupporter = null;
    
    private transient PropertyChangeSupport changeSupport;
    private transient List vetoableChangeList;    
    
    private transient ArrayList revisionListeners;
    private transient Object revisionListenersLock = new Object();

    /** Original file status infos provided by file status provider, that should
     * be retained. */
    private transient Set origPossibleFileStatusInfos = null;
    /** Possible file status infos. */
    private transient Set possibleFileStatusInfos = null;
    /** Map of status names obtained by refresh to the FileStatusInfo object. */
    private transient Map possibleFileStatusInfoMap = null;
    /** Map of generic names (in FileStatusInfo class) to possible VCS status names. */
    private transient Map genericStatusTranslation = null;
    /** The lock for synchronized access to structures holding possible file status information. */
    private transient Object possibleFileStatusesLock = new Object();

    private Collection notModifiableStatuses = Collections.EMPTY_SET;
    private String missingFileStatus = null;
    private String missingFolderStatus = null;
    private Collection notMissingableFileStatuses = Collections.EMPTY_SET;
    private Collection notMissingableFolderStatuses = Collections.EMPTY_SET;
    
    private Integer numberOfFinishedCmdsToCollect = new Integer(RuntimeFolderNode.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT);
    
    private transient FilenameFilter filenameFilter = null;

    private transient VersioningFileSystem versioningSystem = null;

    /** Used for synchronization purpose*/
    private static  Object internLock = new Object ();
    

    /** Creates a new instance of VcsProvider */
    public VcsProvider() {
        init();
    }
    
    /**
     * Get the VCS provider for a given FileObject.
     */
    public static VcsProvider getProvider(FileObject fo) {
        FSInfo[] registered = FSRegistry.getDefault().getRegistered();
        for (int i = 0; i < registered.length; i++) {
            File root = registered[i].getFSRoot();
            FileObject r = FileUtil.toFileObject(root);
            if (FileUtil.isParentOf(r, fo)) {
                return registered[i].getProvider();
            }
        }
        return null;
    }
    
    private void init() {
        GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        setOffLine(settings.isOffLine());
        //setAutoRefresh(settings.getAutoRefresh());
        fileAnnotation = settings.getFileAnnotation();
        
        if (varValueAdjustment == null) varValueAdjustment = new VariableValueAdjustment();
        if (commandsProvider == null) {
            commandsProvider = new DefaultVcsCommandsProvider(new CommandsTree(null));
        }
    }
    
    /*
    private VcsFileSystemReduced fileSystem;
    public VcsFileSystemReduced getAssociatedFileSystem() {
        synchronized (this) {
            if (fileSystem == null) {
                fileSystem = new VcsFileSystemReduced(this);
            }
        }
        return fileSystem;
    }
     */
    
    protected boolean isCreateVersioningSystem() {
        return true;
    }

    /**
     * Get the versioning system. It's created by {@link #createVersioningSystem}
     * when necessary.
     * @return The versioning system or <code>null</code> when no versioning
     *         system is available.
     */
    public final VersioningFileSystem getVersioningSystem() {
        if (isCreateVersioningSystem()) {
            synchronized (this) {
                if (versioningSystem == null) {
                    versioningSystem = createVersioningSystem();
                }
            }
        }
        return versioningSystem;
    }

    protected VersioningFileSystem createVersioningSystem() {
        return new VcsVersioningSystem(this);
    }
    
    /**
     * Get a human presentable name of the provider
     */
    public final String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get a system name. Every registered provider must have a distinct system name.
     * The default implementation returns the absolute path of the working directory.
     */
    public String getName() {
        return getRootDirectory().getAbsolutePath();
    }

    /**
     * Get the file where cache information should be stored for a resource
     * of path <code>path</code>. The default implementation returns <code>null</code>.
     * @param path the path reative to the root directory. Directories are separated
     *        by forward slashes.
     * @return The cache file or <code>null</code>.
     */
    public File getCacheFile(String path) {
        return null;
    }
    
    /**
     * Get the ignore list support.
     * The default implementation returns <code>null</code>.
     * @return The ignore list support or <code>null<code> when no ignore list support is available.
     */
    public IgnoreListSupport getIgnoreListSupport () {
        return null;
    }
    
    /** Get the root directory of the provider.
     * @return root directory
     */
    public File getRootDirectory () {
        return rootFile;
    }

    /** Set the root directory of the provider.
     * @param r file to set the root to
     */
    public void setRootDirectory (File r) throws PropertyVetoException {
        setRootDirectory(r, false);
    }

    /** Set the root directory of the provider.
     * @param r file to set the root to
     */
    protected final void setRootDirectory (File r, boolean forceToSet) throws PropertyVetoException {
        if (/*!r.exists() ||*/ r.isFile ()) {
            throw new IllegalArgumentException("Not a folder: "+r.getAbsolutePath()); // NOI18N
        }

        r = FileUtil.normalizeFile(r);
        String rDir = r.getPath();
        if (rDir.length() == 0) {
            throw new IllegalArgumentException("Can not set empty root.");
        }
        if (org.openide.util.Utilities.isWindows() && rDir.length() == 2 &&
            Character.isLetter(rDir.charAt(0)) && ':' == rDir.charAt(1)) {
            rDir += "\\"; // A special case for C:\
            r = new File(rDir);
        }
        if (!forceToSet && rootFile.equals(r)) return ;
        // Provide a possibility to veto the change of the root.
        fireVetoableChange (PROP_ROOT, rootFile, r);
        //String name = computeSystemName (root);
        File lastRoot;
        synchronized (this) {
            /* Ignoring other filesystems' names => it is possible to mount VCS filesystem with the same name.
            Enumeration en = TopManager.getDefault ().getRepository ().fileSystems ();
            while (en.hasMoreElements ()) {
                FileSystem fs = (FileSystem) en.nextElement ();
                if (((org.openide.util.Utilities.isWindows() && fs.getSystemName().equalsIgnoreCase(name))
                     || (!org.openide.util.Utilities.isWindows() && fs.getSystemName().equals(name)))
                    && !fs.equals(this)) { // Ignore my name if I'm already mounted
                    // NotifyDescriptor.Exception nd = new NotifyDescriptor.Exception (
                    throw (PropertyVetoException) TopManager.getDefault().getErrorManager().annotate(
                        new PropertyVetoException (g("EXC_DirectoryMounted"), // NOI18N
                            new PropertyChangeEvent (this, PROP_ROOT, getSystemName (), name)), // NOI18N
                        g("EXC_DirectoryMounted")); // NOI18N
                    // TopManager.getDefault ().notify (nd);
                }
            }
             */
            //setAdjustedSystemName(name);
            lastRoot = rootFile;
            rootFile = r;
            last_rootFile = r;
            //ready=true ;
        }
        String oldDisplayName = displayName;
        displayName = computeDisplayName();
        firePropertyChange(PROP_DISPLAY_NAME, oldDisplayName, displayName);
        firePropertyChange(PROP_ROOT, lastRoot, r);
    }

    protected String computeDisplayName() {
        Map vars = getVariableValuesMap();
        String displayNameAnnotation = (String) vars.get(VAR_FS_DISPLAY_NAME_ANNOTATION);
        if (displayNameAnnotation != null) {
            FileObject root = FileUtil.toFileObject(getRootDirectory());
            displayNameAnnotation = StatusFormat.getStatusAnnotation("", findResource(""), displayNameAnnotation, fileAnnotation, vars);
            return displayNameAnnotation;
        }
        VcsConfigVariable preDisplayNameVar = (VcsConfigVariable) variablesByName.get(VAR_FS_DISPLAY_NAME);
        if (preDisplayNameVar != null) {
            // TODO Better to fill all system props to SYSTEM_<prop name> variables
            //      and add a condition for this into CVS profile
            if (Boolean.getBoolean("netbeans.vcs.T9Y")) {
                String builtIn = (String) vars.get("BUILT-IN");
                if (builtIn != null) {
                    if (builtIn.length() == 0) {
                        return preDisplayNameVar.getValue() + "-EXT " + rootFile.toString();
                    } else {
                        return preDisplayNameVar.getValue() + "-INT " + rootFile.toString();
                    }
                }
            }
            //System.out.println(preDisplayNameVar.getValue() + " " + rootFile.toString());
            return preDisplayNameVar.getValue() + " " + rootFile.toString();
        } else if (commandsRoot != null) {
            CommandSupport cmdSupport = commandsRoot.getCommandSupport();
            if (cmdSupport != null) {
                String VCSName = cmdSupport.getDisplayName();
                //System.out.println("VCSName = '"+VCSName+"'");
                if (VCSName != null && VCSName.length() > 0) {
                    //System.out.println("VcsFileSystem.getDisplayName() = "+VCSName + " " + rootFile.toString());
                    return VCSName + " " + rootFile.toString();
                }
            }
        }
        return NbBundle.getMessage(VcsProvider.class, "LAB_FileSystemValid", rootFile.getAbsolutePath()); // NOI18N
    }

    
    public final String getAnnotationPattern() {
        return annotationPattern;
    }

    protected final void setAnnotationPattern(final String annotationPattern) throws IllegalArgumentException {
        if (!StatusFormat.isValidAnnotationPattern(annotationPattern)) {
            throw (IllegalArgumentException) ErrorManager.getDefault().annotate(
                new IllegalArgumentException("Not valid HTML annotation pattern '"+annotationPattern+"' !"),
                NbBundle.getMessage(VcsProvider.class, "EXC_InvalidAnnotationPatern", annotationPattern));
        }
        String old = this.annotationPattern;
        this.annotationPattern = annotationPattern;
        firePropertyChange(PROP_ANNOTATION_PATTERN, old, this.annotationPattern);
    }
    
    int getFileAnnotation() {
        return fileAnnotation;
    }

    /**
     * Should be called when the modification in a file or folder is expected
     * and its content should be refreshed.
     */
    public void checkForModifications(String path) {
        checkForModifications(path, true, true, true);
    }
    
    /**
     * Should be called when the modification in a file or folder is expected
     * and its content should be refreshed.
     */
    private void checkForModifications(String path, final boolean recursively,
                                       boolean refreshData, boolean refreshFolders) {
        //System.out.println("checkForModifications("+path+")");
        if (".".equals(path)) path = "";
        FileObject first = this.findResource(path);
        /*
        Enumeration en = existingFileObjects(first);
        while(en.hasMoreElements()) {
            FileObject fo = (FileObject) en.nextElement();
            if (!(fo.isData() && refreshData || fo.isFolder() && refreshFolders)) {
                continue;
            }
            if (!recursively) {
                if (!first.equals(fo.getParent())) {
                    if (!first.equals(fo)) break;
                }
            }
            fo.refresh(true);
        }
         */
        // Can not refresh just existing, must refresh ALL by hard:
        if (refreshFolders && recursively) {
            refreshRecursively(first);
        }
        fireFilesStructureModified(getFile(path));
    }
    
    private void refreshRecursively(FileObject fo) {
        fo.refresh(true);
        FileObject[] fos = fo.getChildren();
        for (int i = 0; i < fos.length; i++) {
            refreshRecursively(fos[i]);
        }
    }
    
    private Collection filesStructureListeners;
    
    public final synchronized void addFilesStructureModificationListener(ChangeListener chl) {
        if (filesStructureListeners == null) {
            filesStructureListeners = new ArrayList();
        }
        filesStructureListeners.add(chl);
    }
    
    public final synchronized void removeFilesStructureModificationListener(ChangeListener chl) {
        if (filesStructureListeners != null) {
            filesStructureListeners.remove(chl);
            if (filesStructureListeners.size() == 0) {
                filesStructureListeners = null;
            }
        }
    }
    
    protected final void fireFilesStructureModified(File file) {
        java.util.List listeners;
        ChangeEvent che = null;
        synchronized (this) {
            if (filesStructureListeners != null) {
                che = new ChangeEvent(file);
                listeners = new ArrayList(filesStructureListeners);
            } else {
                listeners = Collections.EMPTY_LIST;
            }
        }
        for (Iterator it = listeners.iterator(); it.hasNext(); ) {
            ChangeListener l = (ChangeListener) it.next();
            l.stateChanged(che);
        }
    }

    public final int getNumberOfFinishedCmdsToCollect() {
        //System.out.println("VcsFileSystem.getNumberOfFinishedCmdsToCollect() = "+numberOfFinishedCmdsToCollect.intValue());
        return numberOfFinishedCmdsToCollect.intValue();
    }

    public final void setNumberOfFinishedCmdsToCollect(int numberOfFinishedCmdsToCollect) {
        this.numberOfFinishedCmdsToCollect = new Integer(numberOfFinishedCmdsToCollect);
        //System.out.println("VcsFileSystem.setNumberOfFinishedCmdsToCollect("+numberOfFinishedCmdsToCollect+")");
        firePropertyChange(org.netbeans.modules.vcscore.runtime.RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT, null, null);
    }

    public void debug(String msg) {
    }
    
    public void debugErr(String msg) {
    }
    
    /** Get the commands.
     * @return the root command
     */
    public CommandsTree getCommands() {
        return commandsRoot;
    }

    public VcsCommand getCommand(String name) {
        CommandSupport support = getCommandSupport(name);
        if (support instanceof UserCommandSupport) {
            return ((UserCommandSupport) support).getVcsCommand();
        } else {
            return null;
        }
    }
    
    public CommandSupport getCommandSupport(String name) {
        if (commandsByName == null) {
            CommandsTree commands = getCommands();
            if (commands == null) return null;
            setCommands (commands);
        }
        return (CommandSupport) commandsByName.get(name);
    }
    
    public VcsCommandsProvider getCommandsProvider() {
        return commandsProvider;
    }
    
    /** Set the tree structure of commands.
     * @param root the tree of {@link VcsCommandNode} objects.
     */
    protected void setCommands(CommandsTree root) {
        //System.out.println("setCommands()");
        Object old = commandsRoot;
        if (commandsByName != null) {
            removeCmdActionsFromSupporter();
        }
        commandsRoot = root;
        commandsByName = new Hashtable();
        addCommandsToHashTable(root);
        addCmdActionsToSupporter();
        VariableInputDescriptorCompat.createInputDescriptorFormExec(commandsByName);
        ((DefaultVcsCommandsProvider) commandsProvider).setCommands(root);
        firePropertyChange(PROP_COMMANDS, old, commandsRoot);
    }

    private void addCmdActionsToSupporter() {
        synchronized (commandsByName) {
            for (Iterator it = commandsByName.values().iterator(); it.hasNext(); ) {
                CommandSupport cmdSupport = (CommandSupport) it.next();
                if (cmdSupport instanceof ActionCommandSupport) {
                    Class actionClass = ((ActionCommandSupport) cmdSupport).getActionClass();
                    if (actionClass != null) {
                        actionSupporter.addSupportForAction(actionClass, cmdSupport);
                    }
                }
            }
        }
    }

    private void removeCmdActionsFromSupporter() {
        synchronized (commandsByName) {
            for (Iterator it = commandsByName.values().iterator(); it.hasNext(); ) {
                CommandSupport cmdSupport = (CommandSupport) it.next();
                if (cmdSupport instanceof ActionCommandSupport) {
                    Class actionClass = ((ActionCommandSupport) cmdSupport).getActionClass();
                    if (actionClass != null) {
                        actionSupporter.removeSupportForAction(actionClass);
                    }
                }
            }
        }
    }

    private void addCommandsToHashTable(CommandsTree root) {
        CommandsTree[] children = root.children();
        synchronized (commandsByName) {
            for (int i = 0; i < children.length; i++) {
                CommandSupport cmdSupp = children[i].getCommandSupport();
                if (cmdSupp != null) {
                    commandsByName.put(cmdSupp.getName(), cmdSupp);
                }
                if (children[i].hasChildren()) addCommandsToHashTable(children[i]);
            }
        }
    }

    protected VcsActionSupporter getVcsActionSupporter() {
        return actionSupporter;
    }
    
    /**
     * Get file representation for given string name.
     * @param name the name relative to root directory
     * @return the file
     */
    public final File getFile (String name) {
        File file = new File (rootFile, name);
        String path = file.getAbsolutePath();
        boolean repaired = false;
        if (path.endsWith(File.separator + ".")) {
            path = path.substring(0, path.length() - 2);
            repaired = true;
        }
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
            repaired = true;
        }
        if (repaired) {
            file = new File(path);
            if (!file.isAbsolute()) {
                // On some systems it might not be safe to remove the last
                // File.separator from the path!!! (e.g. "D:\" must be kept
                // with the last backslash!)
                file = new File(path + File.separator);
            }
        }
        return file;
    }
    
    /**
     * Find the FileObject for the given name relative to root directory.
     */
    public final FileObject findResource(String name) {
        File file = getFile(name);
        try {
            return FileUtil.toFileObject(file);
        } catch (IllegalArgumentException iaex) {
            return null;
        }
    }
    
    /**
     * Get the root FileObject of this provider.
     */
    public final FileObject getRoot() {
        try {
            return FileUtil.toFileObject(getRootDirectory());
        } catch (IllegalArgumentException iaex) {
            return null;
        }
    }
    
    public final String getRelativePath(FileObject fo) {
        return FileUtil.getRelativePath(getRoot(), fo);
    }
    
    /**
     * Returns true when the given file is sharable.
     * @param name The file name relative to FS root
     */
    public boolean isImportant(String name) {
        int sharability = SharabilityQuery.getSharability(getFile(name));
        return sharability != SharabilityQuery.NOT_SHARABLE;
    }

    /**
     * Get the environment variables and their values
     * in the form: "VAR1=Value1", "VAR2=Value2", etc.
     */
    public String[] getEnvironmentVars() {
        return environmentVars;
    }

    public void addEnvironmentVar (String key, String value) {
	String[] tmp = new String[environmentVars.length+1];
	System.arraycopy (this.environmentVars,0,tmp,0,this.environmentVars.length);
	tmp[environmentVars.length] = key+"="+value;
	this.environmentVars = tmp;
    }

    public void setEnvironmentVar (String key, String value) {
	for (int i=0; i<this.environmentVars.length; i++) {
	    StringTokenizer tk = new StringTokenizer (this.environmentVars[i],"=");
            String pairKey;
            if (tk.countTokens() != 2) { // Broken env variable
		if (environmentVars[i].endsWith("=")) { // the value is not defined
                    pairKey = environmentVars[i].substring(0, environmentVars[i].length() - 1);
                } else {
                    continue; // Something's bad
                }
            } else {
                pairKey = tk.nextToken();
            }
	    if (pairKey.equals(key)) {
		this.environmentVars[i]=key+"="+value;
		return;
	    }
	}
	addEnvironmentVar (key, value);
    }

    private void updateEnvironmentVars() {
        Map systemEnv = VcsUtilities.getSystemEnvVars();
        Map env = VcsUtilities.addEnvVars(systemEnv, getVariableValuesMap(),
                                          VAR_ENVIRONMENT_PREFIX, VAR_ENVIRONMENT_REMOVE_PREFIX);
        environmentVars = VcsUtilities.getEnvString(env);
    }

    public String getPassword() {
        return password;
    }
    
    /**
     * Get the description of the password, typically the name of the service
     * that requests the password. <p>
     * This method returns <code>null</code> by default, subclasses are expected
     * to override this method and return meaningful value.
     * @return The description or <code>null</code> when no description is available.
     */
    public String getPasswordDescription() {
        return null;
    }

    public final Map getPossibleFileStatusInfoMap() {
        return possibleFileStatusInfoMap;
    }
    
    /**
     * Get the map of generic file status names (that are defined in
     * FileStatusInfo class) as keys and translated file status names
     * (that are provided in the possibleFileStatusInfos set) as values.
     */
    public final Map getGenericStatusTranslation() {
        return genericStatusTranslation;
    }

    /**
     * Set a set of all possible FileStatusInfo objects. Also a translation of
     * generic status names to provided possible status names is supplied.
     * @param fileStatusInfos The set of FileStatusInfo objects
     * @param genericStatusTranslation The map of generic file status names
     *        (that are defined in FileStatusInfo class) as keys and translated
     *        file status names (that are provided in the fileStatusInfos set)
     *        as values.
     */
    protected final void setPossibleFileStatusInfos(Set fileStatusInfos, Map genericStatusTranslation) {
        synchronized (possibleFileStatusesLock) {
            if (this.origPossibleFileStatusInfos == null || this.origPossibleFileStatusInfos.size() == 0) {
                this.possibleFileStatusInfos = Collections.unmodifiableSet(fileStatusInfos);
            } else {
                this.possibleFileStatusInfos = new HashSet(origPossibleFileStatusInfos);
                for (Iterator newIt = fileStatusInfos.iterator(); newIt.hasNext(); ) {
                    FileStatusInfo newStatusInfo = (FileStatusInfo) newIt.next();
                    for (Iterator origIt = this.origPossibleFileStatusInfos.iterator(); origIt.hasNext(); ) {
                        FileStatusInfo origStatusInfo = (FileStatusInfo) origIt.next();
                        if (origStatusInfo.equals(newStatusInfo)) {
                            possibleFileStatusInfos.remove(origStatusInfo);
                            break;
                        }
                    }
                    possibleFileStatusInfos.add(newStatusInfo);
                }
                this.possibleFileStatusInfos = Collections.unmodifiableSet(this.possibleFileStatusInfos);
            }
            possibleFileStatusInfoMap = new HashMap();
            for (Iterator it = this.possibleFileStatusInfos.iterator(); it.hasNext(); ) {
                FileStatusInfo statusInfo = (FileStatusInfo) it.next();
                possibleFileStatusInfoMap.put(statusInfo.getName(), statusInfo);
            }
            possibleFileStatusInfoMap = Collections.unmodifiableMap(possibleFileStatusInfoMap);
            this.genericStatusTranslation = Collections.unmodifiableMap(genericStatusTranslation);
        }
    }
    
    protected final void setNotModifiableStatuses(Collection notModifiableStatuses) {
        this.notModifiableStatuses = notModifiableStatuses;
        firePropertyChange(PROP_NOT_MODIFIABLE_STATUSES, null, notModifiableStatuses);
    }
    
    Collection getNotModifiableStatuses() {
        return notModifiableStatuses;
    }

    protected final void setMissingFileStatus(String missingFileStatus) {
        this.missingFileStatus = missingFileStatus;
    }
    
    String getMissingFileStatus() {
        return missingFileStatus;
    }

    protected final void setMissingFolderStatus(String missingFolderStatus) {
        this.missingFolderStatus = missingFolderStatus;
    }

    String getMissingFolderStatus() {
        return missingFolderStatus;
    }

    protected final void setNotMissingableFileStatuses(Collection notMissingableFileStatuses) {
        this.notMissingableFileStatuses = notMissingableFileStatuses;
    }
    
    Collection getNotMissingableFileStatuses() {
        return notMissingableFileStatuses;
    }

    protected final void setNotMissingableFolderStatuses(Collection notMissingableFolderStatuses) {
        this.notMissingableFolderStatuses = notMissingableFolderStatuses;
    }
    
    Collection getNotMissingableFolderStatuses() {
        return notMissingableFolderStatuses;
    }

    public VariableValueAdjustment getVarValueAdjustment() {
        return varValueAdjustment;
    }
    
    public Collection getVariables() {
        return variablesByName.values();
    }
    
    public Map getVariableValuesMap() {
        return new HashMap(variableValues);
    }
    
    protected Map getVariablesByName() {
        return Collections.unmodifiableMap(variablesByName);
    }
    
    public boolean isCommandNotification() {
        return commandNotification;
    }
    
    public void setExpertMode(boolean expertMode) {
        if (expertMode != this.expertMode) {
            this.expertMode = expertMode;
            if (commandsProvider instanceof CommandsTree.Provider) {
                ((CommandsTree.Provider) commandsProvider).setExpertMode(expertMode);
            }
            firePropertyChange(PROP_EXPERT_MODE, !expertMode ? Boolean.TRUE : Boolean.FALSE, expertMode ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public boolean isExpertMode() {
        return expertMode;
    }
    
    public final void setOffLine(boolean offLine) {
        if (offLine != this.offLine) {
            this.offLine = offLine;
            firePropertyChange (GeneralVcsSettings.PROP_OFFLINE, !offLine ? Boolean.TRUE : Boolean.FALSE, offLine ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public final boolean isOffLine() {
        return offLine;
    }
    
    protected final boolean isRememberPassword() {
        return rememberPassword;
    }
    
    public final boolean isPromptForVarsForEachFile() {
        return promptForVarsForEachFile;
    }
    
    public final void setCommandNotification(boolean commandNotification) {
        if (commandNotification != this.commandNotification) {
            this.commandNotification = commandNotification;
            firePropertyChange(PROP_COMMAND_NOTIFICATION, !commandNotification ? Boolean.TRUE : Boolean.FALSE, commandNotification ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    public final void setPassword(String password) {
        if (this.password == null && password != null ||
            this.password != null && !this.password.equals(password)) {

            this.password = password;
            firePropertyChange(PROP_PASSWORD, null, password);
        }
    }
    
    protected final void setRememberPassword(boolean remember) {
        if (this.rememberPassword != remember) {
            this.rememberPassword = remember;
            firePropertyChange(PROP_REMEMBER_PASSWORD, !remember ? Boolean.TRUE : Boolean.FALSE, remember ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public final void setPromptForVarsForEachFile(boolean promptForVarsForEachFile) {
        if (this.promptForVarsForEachFile != promptForVarsForEachFile) {
            this.promptForVarsForEachFile = promptForVarsForEachFile;
            firePropertyChange(PROP_PROMPT_FOR_VARS_FOR_EACH_FILE, !promptForVarsForEachFile ? Boolean.TRUE : Boolean.FALSE, promptForVarsForEachFile ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    private void setPropertiesFromVar(VcsConfigVariable var) {
        if(var.getName ().equalsIgnoreCase (LOCK_FILES_ON)) {
            if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                setLockFilesOn (true);
            }
            if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                setLockFilesOn (false);
            }
        }
        if(var.getName ().equalsIgnoreCase (PROMPT_FOR_LOCK_ON)) {
            if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                setPromptForLockOn (true);
            }
            if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                setPromptForLockOn (false);
            }
        }
        if(var.getName ().equalsIgnoreCase (EDIT_FILES_ON)) {
            if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                setCallEditFilesOn (true);
            }
            if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                setCallEditFilesOn (false);
            }
        }
        if(var.getName ().equalsIgnoreCase (PROMPT_FOR_EDIT_ON)) {
            if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                setPromptForEditOn (true);
            }
            if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                setPromptForEditOn (false);
            }
        }
        if (var.getName().equals("PASSWORD")) { // NOI18N
            // When the variables contain the password, set it.
            if (getPassword() == null) {
                setPassword(var.getValue());
            }
        }
    }
    
    public void setVariables(Collection variables) {
        boolean containsCd = false;
        // Windows != 95 && != 98 needs "cd /D" to change the directory accross disks !!!
        // Windows 95 || 98 do not recognize /D => change the directory accross disks is NOT possible by a single command !!!
        int os = Utilities.getOperatingSystem();
        String cdValue = (Utilities.isWindows()
                          && os != Utilities.OS_WIN95
                          && os != Utilities.OS_WIN98) ? "cd /D" : "cd";
        int len = variables.size ();
        VcsConfigVariable var;
        for (Iterator it = variables.iterator(); it.hasNext(); ) {
            var = (VcsConfigVariable) it.next();
            setPropertiesFromVar(var);
            if(var.getName ().equals ("CD")) { // NOI18N
                //var.setValue (cdValue); <- I don't want to change the value if it is set !!
                containsCd = true;
            }
        }
        if (!containsCd) {
            variables.add (new VcsConfigVariable ("CD", "cd", cdValue, false, false, false, "", 0)); // NOI18N
        }
        Collection old = getVariables();
        
        HashMap newVarsByName = new HashMap();
        for (Iterator it = variables.iterator(); it.hasNext(); ) {
            var = (VcsConfigVariable) it.next();
            String name = var.getName();
            if (newVarsByName.containsKey(name)) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new IllegalArgumentException("Variable '"+name+"' defined more then once, only one value is considered."));
            }
            newVarsByName.put (var.getName (), var);
        }
        synchronized (this) {
            //this.variables = variables;
            variablesByName = new Hashtable(newVarsByName);
            variableValues = computeVariableValues(newVarsByName);
        }
        updateEnvironmentVars();
        varValueAdjustment.setAdjust(getVariableValuesMap());

        if (variables.equals(old)) old = null; // To fire the event even when the variables were changed in this vector.
        firePropertyChange(PROP_VARIABLES, old, variables);
        //try {
        //setAdjustedSystemName(computeSystemName(rootFile));
        //} catch (PropertyVetoException exc) {}
        String oldDisplayName = displayName;
        displayName = computeDisplayName();
        String vcsType = (String) getVariableValuesMap().get(VAR_VCS_TYPE);
        ((DefaultVcsCommandsProvider) commandsProvider).setType(vcsType);
        firePropertyChange(PROP_DISPLAY_NAME, oldDisplayName, getDisplayName());
    }
    
    private Map computeVariableValues(Map variablesByName) {
        // variablesByName masks the this.variablesByName field
        Map result;
        int len = variablesByName.size();
        Map defVars = Variables.getDefaultVariablesMap();
        result = new HashMap(len + defVars.size() + 5);
        result.putAll(defVars);
        for (Iterator it = variablesByName.values().iterator(); it.hasNext(); ) {
            VcsConfigVariable var = (VcsConfigVariable) it.next();
            String value = var.getValue();
            if (value != null) result.put(var.getName (), value);
        }
        
        if (result.get("PS") == null) { // NOI18N
            result.put("PS", File.separator); // NOI18N
        }

        result.put("ROOTDIR", rootFile.getAbsolutePath()); // NOI18N
        result.put(VAR_EXPERT_MODE, expertMode ? "expert" : "");

        return result;
    }
    
    /**
     * Set just one new variables (or update a value of an existing variable).
     */
    public void setVariable(VcsConfigVariable variable) {
        setPropertiesFromVar(variable);
        synchronized (this) {
            variablesByName.put(variable.getName(), variable);
            variableValues.put(variable.getName(), variable.getValue());
        }
        updateEnvironmentVars();
        varValueAdjustment.setAdjust(getVariableValuesMap());
        firePropertyChange(PROP_VARIABLES, null, getVariables());
        String oldDisplayName = displayName;
        displayName = computeDisplayName();
        String vcsType = (String) getVariableValuesMap().get(VAR_VCS_TYPE);
        ((DefaultVcsCommandsProvider) commandsProvider).setType(vcsType);
        firePropertyChange(PROP_DISPLAY_NAME, oldDisplayName, getDisplayName());
    }
    
    public final boolean isLockFilesOn () {
        return lockFilesOn && isEnabledLockFiles();
    }
    public final void setLockFilesOn (boolean lock) {
        if (lock != lockFilesOn) {
            lockFilesOn = lock;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(LOCK_FILES_ON);
            if (var == null) {
                var = new VcsConfigVariable(LOCK_FILES_ON, null, "", false, false, false, null);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(lock ? "true" : "false"); // NOI18N
            firePropertyChange(PROP_CALL_LOCK, !lockFilesOn ? Boolean.TRUE : Boolean.FALSE, lockFilesOn ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    public final boolean isPromptForLockOn () { return promptForLockOn; }
    public final void setPromptForLockOn (boolean prompt) {
        if (prompt != promptForLockOn) {
            promptForLockOn = prompt;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(PROMPT_FOR_LOCK_ON);
            if (var == null) {
                var = new VcsConfigVariable(PROMPT_FOR_LOCK_ON, null, "", false, false, false, null);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(prompt ? "true" : "false"); // NOI18N
            firePropertyChange(PROP_CALL_LOCK_PROMPT, !promptForLockOn ? Boolean.TRUE : Boolean.FALSE, promptForLockOn ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    public final boolean isCallEditFilesOn() {
        return callEditFilesOn && isEnabledEditFiles();
    }
    public final void setCallEditFilesOn(boolean edit) {
        if (edit != callEditFilesOn) {
            callEditFilesOn = edit;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(EDIT_FILES_ON);
            if (var == null) {
                var = new VcsConfigVariable(EDIT_FILES_ON, null, "", false, false, false, null);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(edit ? "true" : "false"); // NOI18N
            firePropertyChange(PROP_CALL_EDIT, !callEditFilesOn ? Boolean.TRUE : Boolean.FALSE, callEditFilesOn ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    public final boolean isPromptForEditOn () { return promptForEditOn; }
    public final void setPromptForEditOn (boolean prompt) {
        if (prompt != promptForEditOn) {
            promptForEditOn = prompt;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(PROMPT_FOR_EDIT_ON);
            if (var == null) {
                var = new VcsConfigVariable(PROMPT_FOR_EDIT_ON, null, "", false, false, false, null);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(prompt ? "true" : "false"); // NOI18N
            firePropertyChange(PROP_CALL_EDIT_PROMPT, !promptForEditOn ? Boolean.TRUE : Boolean.FALSE, promptForEditOn ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    public boolean isEnabledLockFiles() {
        return (getCommandSupport(VcsCommand.NAME_LOCK) != null);
    }

    public boolean isEnabledEditFiles() {
        return (getCommandSupport(VcsCommand.NAME_EDIT) != null);
    }

    /**
     * The subclasses can define their own filename filter. This additional
     * filename filter can be set through this method.
     */
    protected final void setFileFilter(FilenameFilter filenameFilter) {
        FilenameFilter old = this.filenameFilter;
        this.filenameFilter = filenameFilter;
        firePropertyChange(PROP_FILE_FILTER, old, filenameFilter);
    }
    
    /**
     * Get the filename filter of this provider.
     * @return the filter or <code>null</code>.
     */
    public final FilenameFilter getFileFilter() {
        return filenameFilter;
    }
    
    /** Adds listener for the veto of property change.
    * @param listener the listener
    */
    public final void addVetoableChangeListener(VetoableChangeListener listener) {
        synchronized (internLock) {
            if (vetoableChangeList == null) {
                vetoableChangeList = new LinkedList();
            }
            vetoableChangeList.add (listener);
        }
    }

    /** Removes listener for the veto of property change.
    * @param listener the listener
    */
    public final void removeVetoableChangeListener(VetoableChangeListener listener) {
        if (vetoableChangeList == null) return;
        vetoableChangeList.remove (listener);
    }

    /** Fires property vetoable event.
    * @param name name of the property
    * @param o old value of the property
    * @param n new value of the property
    * @exception PropertyVetoException if an listener vetoed the change
    */
    protected final void fireVetoableChange (String name, Object o, Object n) throws PropertyVetoException {
        if (vetoableChangeList == null) return;
        Object[] listeners;
        synchronized (internLock) {
            listeners = vetoableChangeList.toArray();
        }
        if (listeners.length == 0) return ;
        PropertyChangeEvent e = new java.beans.PropertyChangeEvent(this, name, o, n);
        for (int i = 0; i < listeners.length; i++) {
            ((VetoableChangeListener) listeners[i]).vetoableChange(e);
        }
    }

    /** Registers PropertyChangeListener to receive events.
    *@param listener The listener to register.
    */
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        synchronized (internLock) {
            if (changeSupport == null) {
                changeSupport = new PropertyChangeSupport(this);
            }
            changeSupport.addPropertyChangeListener(listener);
        }
    }

    /** Removes PropertyChangeListener from the list of listeners.
    *@param listener The listener to remove.
    */
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }
    
    /** Fires property change event.
    * @param name name of the property
    * @param o old value of the property
    * @param n new value of the property
    */
    protected final void firePropertyChange (String name, Object o, Object n) {
        firePropertyChange (name, o, n, null);
    }
        
    protected final void firePropertyChange (String name, Object o, Object n, Object propagationId) {
        if (changeSupport == null) return;
        if (o != null && o.equals(n)) return;
        
        PropertyChangeEvent e = new PropertyChangeEvent(this, name, o, n);
        e.setPropagationId(propagationId);
        changeSupport.firePropertyChange(e);
    }

    public void addRevisionListener(RevisionListener listener) {
        synchronized (revisionListenersLock) {
            if (revisionListeners == null) revisionListeners = new ArrayList();
            revisionListeners.add(listener);
        }
    }

    public boolean removeRevisionListener(RevisionListener listener) {
        synchronized (revisionListenersLock) {
            if (revisionListeners == null) return false;
            return revisionListeners.remove(listener);
        }
    }

    public void fireRevisionsChanged(RevisionEvent event) {//int whatChanged, FileObject fo, Object info) {
        java.util.List listeners;
        synchronized (revisionListenersLock) {
            if (revisionListeners == null) return;
            listeners = new ArrayList(revisionListeners);
        }
        for(Iterator it = listeners.iterator(); it.hasNext(); ) {
            //((RevisionListener) it.next()).revisionsChanged(whatChanged, fo, info);
            ((RevisionListener) it.next()).stateChanged(event);
        }
    }

    private Map fileChangeHandlers;
    
    /**
     * We allow to have custom listeners on file changes. These listeners are
     * retrieved from FILE_CHANGE_HANDLERS, where we expect pairs of status
     * string and class name (separated by commas, optionally quoted).
     * The class name should be an implementation of ActionListener.
     * When a file of the given status is changed, the action listener is called
     * with ActionEvent, whose source is this filesystem instance and command
     * String is the file path (relative to FS root).
     */
    private Map collectFileChangeHandlers() {
        VcsConfigVariable chhVar = (VcsConfigVariable) variablesByName.get("FILE_CHANGE_HANDLERS"); // NOI18N
        if (chhVar == null) {
            return Collections.EMPTY_MAP;
        } else {
            String chh = chhVar.getValue();
            String[] statusHandlers = VcsUtilities.getQuotedStrings(chh);
            Map fileChangeHandlers = new HashMap();
            for (int i = 0; i < statusHandlers.length - 1; i += 2) {
                String status = statusHandlers[i];
                String className = statusHandlers[i+1];
                try {
                    Class clazz = Class.forName(className, true, VcsUtilities.getSFSClassLoader());
                    Object instance = clazz.newInstance();
                    if (!(instance instanceof ActionListener)) {
                        ErrorManager.getDefault().notify(ErrorManager.getDefault().annotate(new IllegalArgumentException(), "Class '"+className+"' is not an instance of ActionListener, in FILE_CHANGE_HANDLERS."));
                        continue;
                    }
                    fileChangeHandlers.put(status, instance);
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ErrorManager.getDefault().annotate(ex, "Can not create object from class name '"+className+"' when introspecting FILE_CHANGE_HANDLERS."));
                }
            }
            return fileChangeHandlers;
        }
    }
    
    void callFileChangeHandler(FileObject fo, String status) {
        ActionListener handler;
        synchronized (this) {
            if (fileChangeHandlers == null) {
                fileChangeHandlers = collectFileChangeHandlers();
            }
            handler = (ActionListener) fileChangeHandlers.get(status);
        }
        if (handler != null) {
            handler.actionPerformed(new ActionEvent(this, 0, getRelativePath(fo)));
        }
    }

}
