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

import java.awt.*;
import java.io.*;
import java.lang.ref.Reference;
import java.util.*;
import java.beans.*;
import java.text.*;
import javax.swing.*;

import org.openide.*;
import org.openide.util.actions.*;
import org.openide.util.NbBundle;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystem.Status;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.DefaultAttributes;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.RepositoryListener;
import org.openide.filesystems.RepositoryEvent;
import org.openide.filesystems.RepositoryReorderedEvent;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.SharedClassObject;
import org.openide.util.UserQuestionException;
import org.openide.util.WeakListener;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import org.netbeans.modules.vcscore.cache.CacheHandlerListener;
import org.netbeans.modules.vcscore.cache.CacheHandlerEvent;
import org.netbeans.modules.vcscore.cache.CacheFile;
import org.netbeans.modules.vcscore.cache.CacheDir;
import org.netbeans.modules.vcscore.cache.CacheReference;
import org.netbeans.modules.vcscore.caching.*;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.util.virtuals.VcsRefreshRequest;
import org.netbeans.modules.vcscore.util.virtuals.VirtualsDataLoader;
import org.netbeans.modules.vcscore.util.virtuals.VirtualsRefreshing;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.grouping.AddToGroupDialog;
import org.netbeans.modules.vcscore.grouping.GroupUtils;
import org.netbeans.modules.vcscore.grouping.VcsGroupSettings;
import org.netbeans.modules.vcscore.runtime.RuntimeSupport;
import org.netbeans.modules.vcscore.search.VcsSearchTypeFileSystem;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.versioning.RevisionEvent;
import org.netbeans.modules.vcscore.versioning.RevisionListener;
//import org.netbeans.modules.vcscore.versioning.VcsFileObject;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
import org.netbeans.modules.vcscore.versioning.VersioningRepository;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;

/** Generic VCS filesystem.
 * 
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public abstract class VcsFileSystem extends AbstractFileSystem implements VariableInputDialog.FilePromptDocumentListener,
                                                                          VcsSearchTypeFileSystem, VirtualsRefreshing,
                                                                          AbstractFileSystem.List, AbstractFileSystem.Info,
                                                                          AbstractFileSystem.Change, FileSystem.Status,
                                                                          CacheHandlerListener, Serializable {
                                                                              
    public static interface IgnoreListSupport {
        
        public ArrayList createInitialIgnoreList ();
        public ArrayList createIgnoreList (String fileName, ArrayList parentIgnoreList);
    }
                                                                                                                                                           
                                                                              
    private Debug E=new Debug("VcsFileSystem", false); // NOI18N
    private Debug D=E;
    
    private static final int BADGE_ICON_SHIFT_X = 16;
    private static final int BADGE_ICON_SHIFT_Y = 8;

    public static final String VCS_PROVIDER_ATTRIBUTE = "VCS Provider";
    public static final String VCS_FILESYSTEM_ICON_BASE = "VCS Icon Base";

    //public static final String PROP_ROOT = "root"; // NOI18N
    public static final String PROP_VARIABLES = "variables"; // NOI18N
    public static final String PROP_COMMANDS = "commands"; // NOI18N
    public static final String PROP_DEBUG = "debug"; // NOI18N
    public static final String PROP_CALL_EDIT = "edit"; // NOI18N
    public static final String PROP_CALL_EDIT_PROMPT = "editPrompt"; // NOI18N
    public static final String PROP_CALL_LOCK = "lock"; // NOI18N
    public static final String PROP_CALL_LOCK_PROMPT = "lockPrompt"; // NOI18N
    public static final String PROP_EXPERT_MODE = "expertMode"; // NOI18N
    public static final String PROP_PROCESS_UNIMPORTANT_FILES = "processUnimportantFiles"; // NOI18N
    public static final String PROP_ANNOTATION_PATTERN = "annotationPattern"; // NOI18N
    public static final String PROP_ANNOTATION_TYPES = "annotationTypes"; // NOI18N
    public static final String PROP_COMMAND_NOTIFICATION = "commandNotification"; // NOI18N
    public static final String PROP_IGNORED_GARBAGE_FILES = "ignoredGarbageFiles"; // NOI18N
    public static final String PROP_PASSWORD = "password"; // NOI18N
    public static final String PROP_REMEMBER_PASSWORD = "rememberPassword"; // NOI18N
    public static final String PROP_CREATE_RUNTIME_COMMANDS = "createRuntimeCommands"; // NOI18N
    public static final String PROP_CREATE_VERSIONING_EXPLORER = "createVersioningExplorer"; // NOI18N
    public static final String PROP_CREATE_BACKUP_FILES = "createBackupFiles"; // NOI18N
    public static final String PROP_FILTER_BACKUP_FILES = "filterBackupFiles"; // NOI18N
    public static final String PROP_PROMPT_FOR_VARS_FOR_EACH_FILE = "promptForVarsForEachFile"; // NOI18N
    protected static final String PROP_USE_UNIX_SHELL = "useUnixShell"; // NOI18N
    protected static final String PROP_NOT_MODIFIABLE_STATUSES = "notModifiableStatuses"; // NOI18N
    
    public static final String VAR_TRUE = "true"; // NOI18N
    public static final String VAR_FALSE = "false"; // NOI18N

    /**
     * The value of this variable is used to surround file names.
     */
    public static final String VAR_QUOTING = "QUOTE"; // NOI18N
    /**
     * The test on this variable can be performed in the exec string to decide
     * what options to use or what to ask the user for. When the expert mode
     * is on, this variable is non empty.
     */
    public static final String VAR_EXPERT_MODE = "EXPERT_MODE"; // NOI18N
    
    /**
     * If Expert mode is off.. this variable carries information that CTrl was pressed down when
     * displaying the Vcsaction = user requested to have the advanced options displayed..
     */
    
    public static final String VAR_CTRL_DOWN_IN_ACTION = "CTRL_DOWN_IN_ACTION";
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
     * This variable can contain the whole annotation of file system system name.
     */
    public static final String VAR_FS_SYSTEM_NAME_ANNOTATION = "FS_SYSTEM_NAME_ANNOTATION"; // NOI18N

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
    
    public static final String VAR_STATUS_SCHEDULED_ADD = "STATUS_SCHEDULED_ADD";
    public static final String VAR_STATUS_SCHEDULED_REMOVE = "STATUS_SCHEDULED_REMOVE";

    protected static final int REFRESH_TIME = 15000; // This is default in LocalFileSystem
    protected volatile int refreshTimeToSet = REFRESH_TIME;

    private static final String LOCAL_FILES_ADD_VAR = "SHOWLOCALFILES"; // NOI18N
    private static final String LOCK_FILES_ON = "LOCKFILES"; // NOI18N
    private static final String PROMPT_FOR_LOCK_ON = "PROMPTFORLOCK"; // NOI18N
    private static final String EDIT_FILES_ON = "CALLEDITONFILES"; // NOI18N
    private static final String PROMPT_FOR_EDIT_ON = "PROMPTFOREDIT"; // NOI18N

    private static final String DEFAULT_QUOTING_VALUE = "\\\""; // NOI18N

    private static final String DEFAULT_CACHE_ID = "VCS_Cache"; // NOI18N

    private static final String FILE_PROMPT_PREFIX = "tmppf"; // NOI18N
    /*
     * The name of the variable for the global additional parameters.
     *
    private static final String USER_GLOBAL_PARAM = "USER_GLOBAL_PARAM";
    /*
     * The name of the variable for the local additional parameters.
     *
    private static final String USER_PARAM = "USER_PARAM";
     */
    private static int last_refreshTime = REFRESH_TIME;
    private static volatile File last_rootFile = new File (System.getProperty("user.home")); // NOI18N

    private static boolean last_useUnixShell = false;

    private transient Hashtable commandsByName=null;
    /** root file */
    private volatile File rootFile = last_rootFile; // NOI18N

    private boolean useUnixShell = last_useUnixShell;

    /** is read only */
    private boolean readOnly;
    protected Hashtable variablesByName = new Hashtable ();

    private boolean lockFilesOn = false;
    private boolean promptForLockOn = true;
    //private volatile boolean promptForLockResult = false;
    private boolean promptForEditOn = true;
    private boolean callEditFilesOn = true;

    private boolean debug = false;

    /** user variables Vector<String> 'name=value' */
    private Vector variables = new Vector(10);
    /**
     * This variable stores the environment variables and their values
     * in the form: "VAR1=Value1", "VAR2=Value2", etc.
     */
    private transient String[] environmentVars = null;

    private String password = null;
    
    private boolean rememberPassword = false;

    /** Advanced confgiguration.
     * Not used any more, use commandsRoot instead.
     * Needed only for deserialization from NetBeans 3.1 and older
     */
    private Object advanced = null;
    private transient Node commandsRoot = null;

    private String cacheID = null;
    protected transient FileCacheProvider cache = null;
    protected transient FileStatusProvider statusProvider = null;
    
    private int[] multiFilesAnnotationTypes = null;
    private String annotationPattern = null;

    //private long cacheId = 0;
    //private String cacheRoot = null; // NOI18N

    private transient VcsAction action = null;
    private transient VcsFactory factory = null;

    private Boolean processUnimportantFiles = Boolean.FALSE;

    /**
     * Table used to transfer status name obtained by refresh to the name presented to the user.
     * Can be used to make localization of file statuses.
     */
    protected HashMap possibleFileStatusesMap = null;
    
    /**
     * The table used to get the icon badge on the objects' data node.
     * The table contains the original statuses (obtained from the VCS tool)
     * as keys and the icons of type <code>Image</code> as values.
     */
    protected transient HashMap statusIconMap = null;
    
    /**
     * The default icon badge, that is used when no icon can be obtained from {@link statusIconMap}.
     */
    protected transient Image statusIconDefault = null;

    protected boolean ready=false;
    private boolean askIfDownloadRecursively = true;
    private volatile Hashtable numDoAutoRefreshes = new Hashtable();
    
    private transient boolean deserialized; // Whether this class was created by deserialization

    /**
     * Whether to prompt the user for variables for each selected file. Value of this variable
     * will be the default value in the VariableInputDialog and changing the value there will
     * change the value of this variable.
     */
    private boolean promptForVarsForEachFile = false;

    private Vector tempFiles = new Vector();
    
    /**
     * Additional user parameters to the command. These are global parameters to all commands.
     * Parameters local to each command are stored in UserCommand.userParams.
     * The user is asked for them when acceptUserParams = true
     */
    private volatile String[] userParams = null;
    /**
     * Labels to additional user parameters.
     */
    private volatile String[] userParamsLabels = null;
    /**
     * Labels to local additional user parameters.
     */
    private volatile String[] userLocalParamsLabels = null;
    
    private volatile boolean acceptUserParams = false;
    
    /** The expert mode. When true, the user might be prompted for other options.
     */
    private boolean expertMode = false;
    
    /**
     * Whether to run command when doing refresh of folders. Recommended to turn this property off when working off-line.
     */
    //private boolean doCommandRefresh = true;
    
    private volatile transient CommandsPool commandsPool = null;
    private Integer numberOfFinishedCmdsToCollect = new Integer(RuntimeSupport.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT);
    private int versioningFileSystemMessageLength = 20;
    private boolean versioningFileSystemShowMessage = true;
    private String versioningFileSystemShowGarbageFiles = "";
    private boolean versioningFileSystemShowLocalFiles = true;
    private boolean versioningFileSystemShowUnimportantFiles = false;
    private boolean versioningFileSystemShowDeadFiles = false;
    
    private transient ArrayList revisionListeners;

    /** The offline mode.
     * Whether to run command when doing refresh of folders.
     * Recommended to turn this property on when working off-line.
     */
    private volatile boolean offLine; // is set in the constructor
    private volatile int autoRefresh; // is set in the constructor
    private volatile boolean hideShadowFiles; // is set in the constructor
    
    private transient PropertyChangeListener settingsChangeListener = null;
    
    private VariableValueAdjustment varValueAdjustment;
    
    private volatile boolean commandNotification = true;

    private Collection notModifiableStatuses = Collections.EMPTY_SET;
    private String missingFileStatus = null;
    private String missingFolderStatus = null;
    private Collection notMissingableFileStatuses = Collections.EMPTY_SET;
    private Collection notMissingableFolderStatuses = Collections.EMPTY_SET;
    
    private Boolean createRuntimeCommands = Boolean.TRUE;
    
    private Boolean createVersioningSystem = Boolean.FALSE;
    
    private transient VcsActionSupporter actionSupporter = null;
    
    private transient IgnoreListSupport ignoreListSupport = null;
    
    private transient Set unimportantFiles;
    
    /** regexp of ignorable children */
    private String ignoredGarbageFiles = ""; // NOI18N
    
    /** regexp matcher for ignoredFiles, null if not needed */
    private transient RE ignoredGarbageRE = null;
    private Boolean createBackupFiles = null;
    private Boolean filterBackupFiles = null;

    private transient VersioningFileSystem versioningSystem = null;
    
    private transient AbstractFileSystem.List vcsList = null;
    
    /** The refresh request instead of the standard refreshing. */
    private transient VcsRefreshRequest refresher;
    
//    private transient Hashtable revisionListsByName = null;

    public boolean isLockFilesOn () {
        return lockFilesOn && isEnabledLockFiles();
    }
    public void setLockFilesOn (boolean lock) {
        if (lock != lockFilesOn) {
            lockFilesOn = lock;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(LOCK_FILES_ON);
            if (var == null) {
                var = new VcsConfigVariable(LOCK_FILES_ON, null, "", false, false, false, null);
                variables.add(var);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(new Boolean(lock).toString());
            firePropertyChange(PROP_CALL_LOCK, new Boolean(!lockFilesOn), new Boolean(lockFilesOn));
        }
    }
    public boolean isPromptForLockOn () { return promptForLockOn; }
    public void setPromptForLockOn (boolean prompt) {
        if (prompt != promptForLockOn) {
            promptForLockOn = prompt;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(PROMPT_FOR_LOCK_ON);
            if (var == null) {
                var = new VcsConfigVariable(PROMPT_FOR_LOCK_ON, null, "", false, false, false, null);
                variables.add(var);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(new Boolean(prompt).toString());
            firePropertyChange(PROP_CALL_LOCK_PROMPT, new Boolean(!promptForLockOn), new Boolean(promptForLockOn));
        }
    }
    public boolean getAskIfDownloadRecursively () { return askIfDownloadRecursively; }
    public void setAskIfDownloadRecursively (boolean ask) { askIfDownloadRecursively = ask; }
    public boolean isCallEditFilesOn() {
        return callEditFilesOn && isEnabledEditFiles();
    }
    public void setCallEditFilesOn(boolean edit) {
        if (edit != callEditFilesOn) {
            callEditFilesOn = edit;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(EDIT_FILES_ON);
            if (var == null) {
                var = new VcsConfigVariable(EDIT_FILES_ON, null, "", false, false, false, null);
                variables.add(var);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(new Boolean(edit).toString());
            firePropertyChange(PROP_CALL_EDIT, new Boolean(!callEditFilesOn), new Boolean(callEditFilesOn));
        }
    }
    public boolean isPromptForEditOn () { return promptForEditOn; }
    public void setPromptForEditOn (boolean prompt) {
        if (prompt != promptForEditOn) {
            promptForEditOn = prompt;
            VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(PROMPT_FOR_EDIT_ON);
            if (var == null) {
                var = new VcsConfigVariable(PROMPT_FOR_EDIT_ON, null, "", false, false, false, null);
                variables.add(var);
                variablesByName.put(var.getName(), var);
            }
            var.setValue(new Boolean(prompt).toString());
            firePropertyChange(PROP_CALL_EDIT_PROMPT, new Boolean(!promptForEditOn), new Boolean(promptForEditOn));
        }
    }
    public boolean isUseUnixShell () { return useUnixShell; }
    
    public boolean isEnabledLockFiles() {
        return (getCommand(VcsCommand.NAME_LOCK) != null);
    }

    public boolean isEnabledEditFiles() {
        return (getCommand(VcsCommand.NAME_EDIT) != null);
    }

    protected void setUseUnixShell (boolean unixShell) {
        if (unixShell != useUnixShell) {
            useUnixShell = unixShell;
            last_useUnixShell = unixShell;
            firePropertyChange(PROP_USE_UNIX_SHELL, new Boolean(!unixShell), new Boolean(unixShell));
        }
    }
    
    public void setAcceptUserParams(boolean acceptUserParams) {
        this.acceptUserParams = acceptUserParams;
    }
    
    public boolean isAcceptUserParams() {
        return acceptUserParams;
    }

    public void setUserLocalParamsLabels(String[] labels) {
        userLocalParamsLabels = labels;
    }
    
    public String[] getUserLocalParamsLabels() {
        return userLocalParamsLabels;
    }
    
    public void setUserParamsLabels(String[] labels) {
        userParamsLabels = labels;
        userParams = new String[labels.length];
    }
    
    public String[] getUserParamsLabels() {
        return userParamsLabels;
    }
    
    public void setUserParams(String[] userParams) {
        this.userParams = userParams;
    }
    
    public String[] getUserParams() {
        return userParams;
    }
    
    public void setExpertMode(boolean expertMode) {
        if (expertMode != this.expertMode) {
            this.expertMode = expertMode;
            setAcceptUserParams(expertMode);
            firePropertyChange(PROP_EXPERT_MODE, new Boolean(!expertMode), new Boolean(expertMode));
        }
    }
    
    public boolean isExpertMode() {
        return expertMode;
    }
    
    public void setCommandNotification(boolean commandNotification) {
        if (commandNotification != this.commandNotification) {
            this.commandNotification = commandNotification;
            firePropertyChange(PROP_COMMAND_NOTIFICATION, new Boolean(!commandNotification), new Boolean(commandNotification));
        }
    }
    
    public boolean isCommandNotification() {
        return commandNotification;
    }
    
    /**
     * Set whether to prompt the user for variables for each selected file. Set the
     * initial default value in the VariableInputDialog.
     */
    public void setPromptForVarsForEachFile(boolean promptForVarsForEachFile) {
        if (this.promptForVarsForEachFile != promptForVarsForEachFile) {
            this.promptForVarsForEachFile = promptForVarsForEachFile;
            firePropertyChange(PROP_PROMPT_FOR_VARS_FOR_EACH_FILE, new Boolean(!promptForVarsForEachFile), new Boolean(promptForVarsForEachFile));
        }
    }
    
    /**
     * Whether to prompt the user for variables for each selected file. This value
     * will be the default value in the VariableInputDialog and changing the value
     * there will change the value returned.
     */
    public boolean isPromptForVarsForEachFile() {
        return promptForVarsForEachFile;
    }

    public CommandsPool getCommandsPool() {
        return commandsPool;
    }
    
    public void setProcessUnimportantFiles(boolean processUnimportantFiles) {
        boolean fire = false;
        Boolean old = null;
        synchronized (this.processUnimportantFiles) {
            if (processUnimportantFiles != this.processUnimportantFiles.booleanValue()) {
                old = this.processUnimportantFiles;
                this.processUnimportantFiles = new Boolean(processUnimportantFiles);
                fire = true;
            }
        }
        if (fire) {
            firePropertyChange(PROP_PROCESS_UNIMPORTANT_FILES, old, this.processUnimportantFiles);
        }
    }
    
    public boolean isProcessUnimportantFiles() {
        synchronized (processUnimportantFiles) {
            return processUnimportantFiles.booleanValue();
        }
    }
    
    public String getIgnoredGarbageFiles () {
        return ignoredGarbageFiles;
    }
    
    public synchronized void setIgnoredGarbageFiles (String nue) throws IllegalArgumentException {
        if (! nue.equals (ignoredGarbageFiles)) {
            if (nue.length () > 0) {
                try {
                    ignoredGarbageRE = new RE (nue);
                } catch (RESyntaxException rese) {
                    IllegalArgumentException iae = new IllegalArgumentException ();
                    TopManager.getDefault ().getErrorManager ().annotate (iae, rese);
                    throw iae;
                }
            } else {
                ignoredGarbageRE = null;
            }
            ignoredGarbageFiles = nue;
            firePropertyChange (PROP_IGNORED_GARBAGE_FILES, null, null); // NOI18N
            refreshExistingFolders();
        }
    }
    
    public void numOfFinishedCmdsToCollectChanged() {
        firePropertyChange(org.netbeans.modules.vcscore.runtime.RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT, null, null);
    }

    protected void refreshExistingFolders() {
        refreshExistingFolders(null);
    }
    
    protected void refreshExistingFolders(final String name) {
        org.openide.util.RequestProcessor.postRequest(new Runnable() {
            public void run() {
                FileObject root;
                if (name == null) {
                    root = getRoot();
                } else {
                    root = findResource(name);
                }
                if (root == null) return ;
                Enumeration e = existingFileObjects(root);
                while (e.hasMoreElements()) {
                    FileObject fo = (FileObject) e.nextElement();
                    if (fo.isFolder()) {
                        fo.refresh(true);
                    }
                }
            }
        });
    }
    
    protected boolean isCreateBackupFilesSet() {
        return createBackupFiles != null;
    }
    
    public boolean isCreateBackupFiles() {
        return createBackupFiles != null && createBackupFiles.booleanValue();
    }
    
    public void setCreateBackupFiles(boolean createBackupFiles) {
        if (!new Boolean(createBackupFiles).equals(this.createBackupFiles)) {
            this.createBackupFiles = new Boolean(createBackupFiles);
            firePropertyChange(PROP_CREATE_BACKUP_FILES, null, this.createBackupFiles);
        }
    }
    
    protected boolean isFilterBackupFilesSet() {
        return filterBackupFiles != null;
    }
    
    public boolean isFilterBackupFiles() {
        return filterBackupFiles != null && filterBackupFiles.booleanValue();
    }
    
    public void setFilterBackupFiles(boolean filterBackupFiles) {
        if (!new Boolean(filterBackupFiles).equals(this.filterBackupFiles)) {
            this.filterBackupFiles = new Boolean(filterBackupFiles);
            firePropertyChange(PROP_FILTER_BACKUP_FILES, null, this.filterBackupFiles);
        }
    }
    
    public void setOffLine(boolean offLine) {
        if (offLine != this.offLine) {
            this.offLine = offLine;
            firePropertyChange (GeneralVcsSettings.PROP_OFFLINE, new Boolean(!offLine), new Boolean(offLine));
        }
    }
    
    public boolean isOffLine() {
        return offLine;
    }

    /** Get the mode of auto refresh. */
    public int getAutoRefresh() {
        return autoRefresh;
    }
    
    /** Set the mode of auto refresh. */
    public void setAutoRefresh(int newAuto) {
        if (newAuto != autoRefresh) {
            int oldAuto = autoRefresh;
            autoRefresh = newAuto;
            firePropertyChange (GeneralVcsSettings.PROP_AUTO_REFRESH, new Integer(oldAuto), new Integer(autoRefresh));
        }
    }
    
    /** Whether to hide files, which does not exist locally. */
    public boolean isHideShadowFiles() {
        return hideShadowFiles;
    }
    
    /** Set whether to hide files, which does not exist locally. */
    public void setHideShadowFiles(boolean hideShadowFiles) {
        if (hideShadowFiles != this.hideShadowFiles) {
            this.hideShadowFiles = hideShadowFiles;
            firePropertyChange (GeneralVcsSettings.PROP_HIDE_SHADOW_FILES, new Boolean(!hideShadowFiles), new Boolean(hideShadowFiles));
        }
    }
    
    public boolean isShowDeadFiles() {
        VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(Variables.SHOW_DEAD_FILES);
        if (var == null) {
            return false;
        } else {
            return (var.getValue().trim().length() > 0);
        }
    }
    
    public void setShowDeadFiles(boolean showDeadFiles) {
        VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(Variables.SHOW_DEAD_FILES);
        if (var == null) {
            if (showDeadFiles) {
                var = new VcsConfigVariable(Variables.SHOW_DEAD_FILES, null, "true", false, false, false, null);
                synchronized (this) {
                    variablesByName.put(Variables.SHOW_DEAD_FILES, var);
                    variables.add(var);
                }
            }
        } else {
            var.setValue(showDeadFiles ? "true" : "");
        }
    }
    
    public int getNumberOfFinishedCmdsToCollect() {
        return RuntimeSupport.getInstance().getCollectFinishedCmdsNum(getSystemName());
    }
    
    public void setNumberOfFinishedCmdsToCollect(int numberOfFinishedCmdsToCollect) {
        this.numberOfFinishedCmdsToCollect = new Integer(numberOfFinishedCmdsToCollect);
        RuntimeSupport.getInstance().setCollectFinishedCmdsNum(numberOfFinishedCmdsToCollect, getSystemName());
        // Do NOT fire a property change here !!!
        // The property change is propagated from RuntimeSupport.getInstance().setCollectFinishedCmdsNum()
        //                                   to   this.numOfFinishedCmdsToCollectChanged()
    }
    
    public int getVFSMessageLength() {
        return versioningFileSystemMessageLength;
    }
    public void setVFSMessageLength(int newVal) {
        versioningFileSystemMessageLength = newVal;
    }

    public boolean getVFSShowLocalFiles() {
        return versioningFileSystemShowLocalFiles;
    }
    public void setVFSShowLocalFiles(boolean newVal) {
        versioningFileSystemShowLocalFiles = newVal;
    }

    public boolean getVFSShowMessage() {
        return versioningFileSystemShowMessage;
    }
    public void setVFSShowMessage(boolean newVal) {
        versioningFileSystemShowMessage = newVal;
    }

    public boolean getVFSShowUnimportantFiles() {
        return versioningFileSystemShowUnimportantFiles;
    }
    public void setVFSShowUnimportantFiles(boolean newVal) {
        versioningFileSystemShowUnimportantFiles = newVal;
    }

    public String getVFSShowGarbageFiles() {
        return versioningFileSystemShowGarbageFiles;
    }
    public void setVFSShowGarbageFiles(String newVal) {
        versioningFileSystemShowGarbageFiles = newVal;
    }
    
    public boolean getVFSShowDeadFiles() {
        return versioningFileSystemShowDeadFiles;
    }
    public void setVFSShowDeadFiles(boolean newVal) {
        versioningFileSystemShowDeadFiles = newVal;
    }
    
    private void copyFromVersioningFs() {
        if (versioningSystem != null && versioningSystem instanceof VcsVersioningSystem) {
            VcsVersioningSystem versFs = (VcsVersioningSystem)versioningSystem;
            setVFSMessageLength(versFs.getMessageLength());
            setVFSShowGarbageFiles(versFs.getIgnoredGarbageFiles());
            setVFSShowLocalFiles(versFs.isShowLocalFiles());
            setVFSShowMessage(versFs.isShowMessages());
            setVFSShowUnimportantFiles(versFs.isShowUnimportantFiles());
            setVFSShowDeadFiles(versFs.isShowDeadFiles());
        }
    }
    
    
    /**
     * this method is invoked by reflection from the Versioning filesystem to enable saving of it's changed properties..
     * these should be mirrored in the filesystem so that they get saved..
     */ 
    public void saveVersioningFileSystemProperties(String propName, Object newValue) {
        copyFromVersioningFs();
        firePropertyChange(propName, null, newValue);
    }
    
    
    public void addRevisionListener(RevisionListener listener) {
        if (revisionListeners == null) revisionListeners = new ArrayList();
        revisionListeners.add(listener);
    }
    
    public boolean removeRevisionListener(RevisionListener listener) {
        if (revisionListeners == null) return false;
        return revisionListeners.remove(listener);
    }

    public void fireRevisionsChanged(RevisionEvent event) {//int whatChanged, FileObject fo, Object info) {
        if (revisionListeners == null) return;
        for(Iterator it = revisionListeners.iterator(); it.hasNext(); ) {
            //((RevisionListener) it.next()).revisionsChanged(whatChanged, fo, info);
            ((RevisionListener) it.next()).stateChanged(event);
        }
    }
    
    protected void setNotModifiableStatuses(Collection notModifiableStatuses) {
        this.notModifiableStatuses = notModifiableStatuses;
        firePropertyChange(PROP_NOT_MODIFIABLE_STATUSES, null, notModifiableStatuses);
    }
    
    protected void setMissingFileStatus(String missingFileStatus) {
        this.missingFileStatus = missingFileStatus;
    }
    
    protected void setMissingFolderStatus(String missingFolderStatus) {
        this.missingFolderStatus = missingFolderStatus;
    }
    
    protected void setNotMissingableFileStatuses(Collection notMissingableFileStatuses) {
        this.notMissingableFileStatuses = notMissingableFileStatuses;
    }
    
    protected void setNotMissingableFolderStatuses(Collection notMissingableFolderStatuses) {
        this.notMissingableFolderStatuses = notMissingableFolderStatuses;
    }
    
    /**
     * Get whether to perform the auto-refresh in the given directory path.
     * @param path The given directory path
     */
    public boolean getDoAutoRefresh(String path) {
        synchronized (numDoAutoRefreshes) {
            D.deb("getDoAutoRefresh("+path+") ..."); // NOI18N
            int numDoAutoRefresh = getNumDoAutoRefresh(path);
            if (numDoAutoRefresh > 0) {
                numDoAutoRefresh--;
                if (numDoAutoRefresh > 0) setNumDoAutoRefresh(numDoAutoRefresh, path);
                else removeNumDoAutoRefresh(path);
                D.deb("  return "+(numDoAutoRefresh == 0)); // NOI18N
                return (numDoAutoRefresh == 0);
            } else {D.deb("  return true"); return true;} // nothing known about that path, but refresh requested. // NOI18N
        }
    }

    /**
     * Set how many times I call a command after which the auto-refresh is executed in the given path.
     * @param numDoAutoRefresh The number of auto-refreshes
     * @param path The given directory path
     */
    public void setNumDoAutoRefresh(int numDoAutoRefresh, String path) {
        synchronized (numDoAutoRefreshes) {
            D.deb("setNumDoAutoRefresh("+numDoAutoRefresh+", "+path+")"); // NOI18N
            numDoAutoRefreshes.put(path, new Integer(numDoAutoRefresh));
        }
    }

    /**
     * Get the number of command calls after which perform the auto-refresh command in the given path.
     * @param path The given path
     */
    public int getNumDoAutoRefresh(String path) {
        synchronized (numDoAutoRefreshes) {
            Integer numDoAutoRefreshObj = (Integer) numDoAutoRefreshes.get(path);
            int numDoAutoRefresh = 0;
            if (numDoAutoRefreshObj != null) {
                numDoAutoRefresh = numDoAutoRefreshObj.intValue();
            }
            D.deb("getNumDoAutoRefresh("+path+") = "+numDoAutoRefresh); // NOI18N
            return numDoAutoRefresh;
        }
    }

    /**
     * Remove the number of command calls after which perform the auto-refresh command in the given path.
     * @param path The given path
     */
    public void removeNumDoAutoRefresh(String path) {
        if (path == null) return;
        synchronized (numDoAutoRefreshes) {
            D.deb("removeNumDoAutoRefresh("+path+")"); // NOI18N
            numDoAutoRefreshes.remove(path);
        }
    }
    
    /** Set the number of milliseconds between automatic refreshes of the
     * directory structure. Replaces the functionality of setRefreshTime.
     * You should not use setRefreshTime() in subclasses of VcsFileSystem at all!
     * Use this method instead.
     *
     * @param ms number of milliseconds between two refreshes; if <code><= 0</code> then refreshing is disabled
     */
    protected synchronized final void setVcsRefreshTime(int ms) {
        if (refresher != null) {
            refresher.stop ();
        }
        if (ms <= 0 || System.getProperty ("netbeans.debug.heap") != null) { // NOI18N
            refresher = null;
        } else {
            refresher = new VcsRefreshRequest (this, ms, this);
        }
    }
    
    /** Get the number of milliseconds between automatic refreshes of the
     * directory structure. Replaces the functionality of getRefreshTime.
     * You should not use getRefreshTime() in subclasses of VcsFileSystem at all!
     * Use this method instead.
     * By default, automatic refreshing is disabled.
     *
     * @return the number of milliseconds, or <code>0</code> if refreshing is disabled
     */
    protected final int getVcsRefreshTime() {
        VcsRefreshRequest r = refresher;
        return r == null ? 0 : r.getRefreshTime ();
     }

    public Enumeration getExistingFolders() {
        return this.existingFileObjects(getRoot());
    }
    
    /**
     * Do the refresh of a folder.
     */
    public void doVirtualsRefresh(FileObject fo) {
        fo.refresh();
        if (fo.isFolder()) {
            Enumeration en = existingFileObjects(fo);
            while (en.hasMoreElements()) {
                FileObject fo2 = (FileObject) en.nextElement();
                if (fo2 != null && (fo2.getParent() == null || !fo2.getParent().equals(fo))) {
                    // uses the feature of the existingFileObject method that the closest siblings are the first to come..
                    //HACK..
                    break;
                }
                if (!fo2.isFolder()) {
                    setVirtualDataLoader(fo2);
                }
            }
        }
    }
    
    /** Return the working directory of the file system. 
     *  To that, relative mountpoints are added later to enable compilation etc.
     */
    public String getFSRoot() {
      return VcsFileSystem.substractRootDir(getRootDirectory().toString(), getRelativeMountPoint());
    }

        
    public synchronized String getRelativeMountPoint() {
      Hashtable vars = variablesByName;
      VcsConfigVariable module = (VcsConfigVariable) vars.get("MODULE");
      if (module == null) return "";
      return module.getValue();
    }    
    
    public void setRelativeMountPoint(String module) throws PropertyVetoException, IOException {
        synchronized (this) {
            Hashtable vars = variablesByName;
            String root = this.getFSRoot();
            VcsConfigVariable mod = (VcsConfigVariable) vars.get("MODULE");
            if (mod != null && module.equals(mod.getValue())) return ;
            if (mod == null) {
                mod = new VcsConfigVariable("MODULE", "", module, false, false, false, null);
                variables.add(mod);
                variablesByName.put("MODULE", mod);
            }
            String oldModule = mod.getValue();
            mod.setValue(module);
            try {
                this.setRootDirectory(new File(root));
            } catch (PropertyVetoException prop) {
                mod.setValue(oldModule);
                throw prop;
            } catch (IOException io) {
                mod.setValue(oldModule);
                throw io;
            }
        }
         
        if (isValid()) {
            if (cache != null) {
                cache.refreshDirFromDiskCache(getFile(""));
            }
        }
    }    
    
        
    /*
     * Mark the file as being unimportant.
     * @param name the file name
     */
    public void markUnimportant(String name) {
        Reference ref = findReference(name);
        if (ref != null && ref instanceof CacheReference) {
            ((CacheReference) ref).markUnimportant();
        } else {
            unimportantFiles.add(name);
        }
    }
    
    /*
     * Mark the file as being important or unimportant.
     * @param name the file name
     * @param important whether the file is important or not.
     */
    public void markImportant(String name, boolean important) {
        Reference ref = findReference(name);
        if (ref != null && ref instanceof CacheReference) {
            if (important) {
                ((CacheReference) ref).markImportant();
            } else {
                ((CacheReference) ref).markUnimportant();
            }
        } else {
            if (important) {
                unimportantFiles.remove(name);
            } else {
                unimportantFiles.add(name);
            }
        }
        if (versioningFolderListeners != null) {
            FileObject fo = findResource(name);
            if (fo != null) {
                FileObject folder = fo.getParent();
                if (folder != null) {
                    FileChangeListener l = (FileChangeListener) versioningFolderListeners.get(folder);
                    if (l != null) l.fileChanged(null);
                }
            }
        }
    }
    
    public boolean isImportant(String name) {
        Reference ref = findReference(name);
        if (ref != null && ref instanceof CacheReference) {
            return ((CacheReference) ref).isImportant();
        } else {
            return !unimportantFiles.contains(name);
        }
    }
    
    private static RequestProcessor statusRequestProcessor;
    private static final Object STATUS_REQUEST_PROCESSOR_LOCK = new Object();
    
    /**
     * Get a special request processor, that is used to refresh the file status.
     * This is necessary to prevent some kind of deadlocks, that can occure
     * if the default RequestProcessor is used.
     */
    public static RequestProcessor getStatusChangeRequestProcessor() {
        synchronized (STATUS_REQUEST_PROCESSOR_LOCK) {
            if (statusRequestProcessor == null) {
                statusRequestProcessor = new RequestProcessor("VCS Status Update Request Processor"); // NOI18N
            }
        }
        return statusRequestProcessor;
    }

    /**
     * Perform refresh of status information on all children of a directory
     * @param path the directory path
     * @param recursivey whether to refresh recursively
     */
    public void statusChanged (final String path, final boolean recursively) {
        getStatusChangeRequestProcessor().post(new Runnable() {
            public void run() {
                //D.deb("statusChanged("+path+")"); // NOI18N
                FileObject fo = findResource(path);
                if (fo == null) return;
                Enumeration enum = existingFileObjects(fo);
                //D.deb("I have root = "+fo.getName()); // NOI18N
                //Enumeration enum = fo.getChildren(recursively);
                HashSet hs = new HashSet();
                if (enum.hasMoreElements()) {
                    // First add the root FileObject
                    hs.add(enum.nextElement());
                }
                while(enum.hasMoreElements()) {
                    //fo = (FileObject) enum.nextElement();
                    //hs.add(fo);
                    FileObject chfo = (FileObject) enum.nextElement();
                    if (!fo.equals(chfo.getParent()) && !recursively) break;
                    hs.add(chfo);
                    //D.deb("Added "+fo.getName()+" fileObject to update status"+fo.getName()); // NOI18N
                }
                Set s = Collections.synchronizedSet(hs);
                fireFileStatusChanged(new FileStatusEvent(VcsFileSystem.this, s, true, true));
                checkScheduledStates(s);
                checkVirtualFiles(s);
            }
        });
        if (versioningSystem != null) versioningSystem.statusChanged(path, recursively);
    }
    
    /**
     * Perform refresh of status information of a file
     * @param name the full file name
     */
    public void statusChanged (final String name) {
        getStatusChangeRequestProcessor().post(new Runnable() {
            public void run() {
                FileObject fo = findExistingResource(name);
                //System.out.println("statusChanged: findResource("+name+") = "+fo);
                if (fo == null) return;
                fireFileStatusChanged(new FileStatusEvent(VcsFileSystem.this, fo, true, true));
                Set fileSet = Collections.singleton(fo);
                checkScheduledStates(fileSet);
                checkVirtualFiles(fileSet);
            }
        });
        if (versioningSystem != null) versioningSystem.statusChanged(name);
    }
    
    protected FileObject findExistingResource(String name) {
        Enumeration enum = existingFileObjects(getRoot());
        FileObject fo = null;
        while (enum.hasMoreElements()) {
            FileObject obj = (FileObject) enum.nextElement();
            if (name.equals(obj.getPackageNameExt('/', '.'))) {
                fo = obj;
                break;
            }
        }
        return fo;
    }
    
    /**
     * Check the scheduled states for the set of file objects.
     * Files scheduled for ADD are controlled by this method. When they change their
     * status, they are removed from the list of scheduled files.
     * @param fos the set of files to check.
     */
    private void checkScheduledStates(Set fos) {
        FileStatusProvider status = getStatusProvider();
        if (status == null) return ;
        VcsConfigVariable schVar = (VcsConfigVariable) variablesByName.get(VAR_STATUS_SCHEDULED_ADD);
        String scheduledStatusAdd = (schVar != null) ? schVar.getValue() : null;
        schVar = (VcsConfigVariable) variablesByName.get(VAR_STATUS_SCHEDULED_REMOVE);
        String scheduledStatusRemove = (schVar != null) ? schVar.getValue() : null;
        //System.out.println("checkScheduledStates(): scheduledStatusAdd = "+scheduledStatusAdd+", scheduledStatusRemove = "+scheduledStatusRemove);
        for (Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            //System.out.println("checkScheduledStates("+fo.getPackageNameExt('/', '.')+")");
            String attr = (String) fo.getAttribute(VcsAttributes.VCS_SCHEDULED_FILE_ATTR);
            //System.out.println("attr("+VcsAttributes.VCS_SCHEDULED_FILE_ATTR+") = "+attr);
            if (VcsAttributes.VCS_SCHEDULING_ADD.equals(attr) && scheduledStatusAdd != null &&
                !scheduledStatusAdd.equals(status.getFileStatus(fo.getPackageNameExt('/', '.'))) &&
                isSchedulingDone(fo.getPackageNameExt('/', '.'))) {
                try {
                    fo.setAttribute(VcsAttributes.VCS_SCHEDULED_FILE_ATTR, null);
                } catch (IOException exc) {}
                removeScheduledFromPrimary(fo, 1);
            }
            /*
            if (VcsAttributes.VCS_SCHEDULING_REMOVE.equals(attr) && scheduledStatusRemove != null &&
                !scheduledStatusRemove.equals(status.getFileStatus(fo.getPackageNameExt('/', '.')))) {
                try {
                    fo.setAttribute(VcsAttributes.VCS_SCHEDULED_FILE_ATTR, null);
                } catch (IOException exc) {}
                removeScheduledFromPrimary(fo, 0);
            }
             */
        }
    }
    
    /**
     * Remove a file from the list of scheduled files.
     * @param fo the scheduled file object
     * @param id the scheduling ID (0 for removed, 1 for added).
     */
    private static void removeScheduledFromPrimary(FileObject fo, int id) {
        DataObject dobj;
        try {
            dobj = DataObject.find(fo);
        } catch (org.openide.loaders.DataObjectNotFoundException exc) {
            return ;
        }
        FileObject primary = dobj.getPrimaryFile();
        Set[] scheduled = (Set[]) primary.getAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
        if (scheduled != null && scheduled[id] != null) {
            scheduled[id].remove(fo.getPackageNameExt('/', '.'));
            scheduled = cleanScheduledAttrs(scheduled);
            try {
                primary.setAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR, scheduled);
                if (scheduled == null) {
                    primary.setAttribute(VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR, null);
                }
            } catch (IOException exc) {}
        }
    }
    
    /**
     * Remove a file from the list of scheduled files.
     * @param scheduledFile the scheduled file name
     * @param primaryFile the associated primary file name
     * @param id the scheduling ID (0 for removed, 1 for added).
     */
    private void removeScheduledFromPrimary(String scheduledFile, String primaryFile, int id) {
        Set[] scheduled = (Set[]) attr.readAttribute(primaryFile, VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
        if (scheduled != null && scheduled[id] != null) {
            scheduled[id].remove(scheduledFile);
            scheduled = cleanScheduledAttrs(scheduled);
            try {
                attr.writeAttribute(primaryFile, VcsAttributes.VCS_SCHEDULED_FILES_ATTR, scheduled);
                if (scheduled == null) {
                    attr.writeAttribute(primaryFile, VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR, null);
                }
            } catch (IOException exc) {}
        }
    }
    
    public void disableRefresh() {
        synchronized (this) {
            refreshTimeToSet = getVcsRefreshTime();
            setVcsRefreshTime(0);
        }
    }
    
    public void enableRefresh() {
        synchronized (this) {
            setVcsRefreshTime(refreshTimeToSet);
        }
    }
    
    public void setRefreshTimeToSet() {
        setVcsRefreshTime(refreshTimeToSet);
    }

    public void setCustomRefreshTime (int time) {
        if (isValid ()) {
            D.deb("Filesystem valid, setting the refresh time to "+time); // NOI18N
            setVcsRefreshTime (time);
        } else {
            D.deb("Filesystem not valid yet for refresh time "+time); // NOI18N
            refreshTimeToSet = time;
        }
        last_refreshTime = time;
    }

    public int getCustomRefreshTime () {
        if (isValid ()) {
            //D.deb("Filesystem valid, getting the refresh time "+getVcsRefreshTime ()); // NOI18N
            return getVcsRefreshTime ();
        } else {
            return refreshTimeToSet;
        }
    }
    
    public void setZeroRefreshTime() {
        setVcsRefreshTime(0);
    }

    /**
     * Clear the debug output.
     */
    public void debugClear(){
        if( getDebug() ){
            try{
                TopManager.getDefault().getStdOut().reset();
            }catch (IOException e){}
        }
    }


    /**
     * Print a debug output. If the debug property is true, the message
     * is printed to the Output Window.
     * @param msg the message to print out.
     */
    public void debug(String msg){
        if( getDebug() ){
            TopManager.getDefault().getStdOut().println(msg);
        }
    }

    /**
     * Print an error output. Force the message to print to the Output Window.
     * The debug property is not considered.
     * @param msg the message to print out.
     */
    public void debugErr(String msg){
        TopManager.getDefault().getStdOut().println(msg);
    }


    private static ArrayList createIgnoreListQueue = new ArrayList();
    private static Thread ignoreListCreationThread = null;
    
    static void addCreateIgnoreList(FileObject fo) {
        synchronized (createIgnoreListQueue) {
            if (createIgnoreListQueue == null) createIgnoreListQueue = new ArrayList();
            createIgnoreListQueue.add(fo);
            //System.out.println("addCreateIgnoreList("+fo+"), createIgnoreListQueue.size() = "+createIgnoreListQueue.size());
            createIgnoreListQueue.notifyAll();
            if (ignoreListCreationThread == null || !ignoreListCreationThread.isAlive()) {
                ignoreListCreationThread = createIgnoreListCreationThread();
                ignoreListCreationThread.start();
            }
        }
    }
    
    private static void waitForIgnoreListToCreate() {
        synchronized (createIgnoreListQueue) {
            try {
                createIgnoreListQueue.wait(60000);
            } catch (InterruptedException exc) {}
        }
    }
    
    private static Thread createIgnoreListCreationThread() {
        return new Thread(new Runnable() {
            public void run() {
                //IgnoreListSupport ignSupport = VcsFileSystem.this.getIgnoreListSupport();
                //System.out.println("createIgnoreListCreationThread STARTING...");
                do {
                    //System.out.println("createIgnoreListCreationThread RESUMED...");
                    while (createIgnoreListQueue.size() > 0) {
                        FileObject fo = (FileObject) createIgnoreListQueue.remove(0);
                        FileSystem fs = null;
                        try {
                            fs = fo.getFileSystem();
                        } catch (FileStateInvalidException fsiexc) {}
                        VcsFileSystem vfs = null;
                        if (fs == null || !(fs instanceof VcsFileSystem)) {
                            if (fs instanceof VcsVersioningSystem) {
                                vfs = (VcsFileSystem) ((VcsVersioningSystem) fs).getFileSystem();
                            } else {
                                continue;
                            }
                        } else {
                            vfs = (VcsFileSystem) fs;
                        }
                        IgnoreListSupport ignSupport = vfs.getIgnoreListSupport();
                        String path = fo.getPackageNameExt('/','.');
                        //System.out.println(" creating ignore list: "+fo);
                        vfs.createIgnoreList(fo, path, ignSupport);
                    }
                    waitForIgnoreListToCreate();
                } while (createIgnoreListQueue.size() > 0);
                //System.out.println("createIgnoreListCreationThread FINISHED.");
            }
        }, "VCS Ignore List Creation Thread");
    }

    /** Creates Reference. In FileSystem, which subclasses AbstractFileSystem, you can overload method
     * createReference(FileObject fo) to achieve another type of Reference (weak, strong etc.)
     * @param fo is FileObject. It`s reference yourequire to get.
     * @return Reference to FileObject
     */
    protected java.lang.ref.Reference createReference(final FileObject fo) {
	if (cache != null) {
            java.lang.ref.Reference ref = cache.createReference(fo);
            final IgnoreListSupport ignSupport = this.getIgnoreListSupport();
            if (ignSupport != null) {
                final String path = fo.getPackageNameExt('/','.');
                if (cache.isDir (path)) {
                    addCreateIgnoreList(fo);
                }
            }
	    return ref;
	}
	else return super.createReference(fo);
    }
    
    /**
     * Utility method that find the fileobject reference and if it exists, retypes it to CacheReference.
     * @param name pathname of the resource.
     * @returns  the cacheReference instance if one exists or null
     */
    protected CacheReference getCacheReference(String name) {
       Reference ref = findReference(name);
       if (ref != null && ref instanceof CacheReference) {
          CacheReference cref = (CacheReference) ref;
          return cref;
       }
       return null;
    }
    
    private void createIgnoreList(final FileObject fo, final String path, final IgnoreListSupport ignSupport) {
        CacheDir dir = cache.getDir(path);
        if (dir == null || dir.isIgnoreListSet()) return ;
        dir.setIgnoreList(VcsUtilities.createIgnoreList(dir, path, ignSupport));//ignorelist);
        Enumeration existingFOEnum = existingFileObjects(fo);
        if (existingFOEnum.hasMoreElements()) existingFOEnum.nextElement(); // take out the root FileObject
        else return ; // there are no existing file objects.
        if (existingFOEnum.hasMoreElements()) { // there are some children
            statusChanged(path, false);
        } else {
            statusChanged(path);
        }
    }

    /**
     * Get the provider of the cache.
     */
    public FileCacheProvider getCacheProvider() {
        return cache;
    }
    
    /**
     * Get the provider of file status attributes.
     */
    public FileStatusProvider getStatusProvider() {
        return statusProvider;
    }

    //-------------------------------------------
    public void setCache(FileCacheProvider cache) {
        this.cache = cache;
    }
    
    /**
     * Get the full file path where cache information should be stored.
     * @return the cache file path or null, if no disk cache should be used for this path
     */
    public abstract String getCacheFileName(String path);
    
    /**
     * Initialize the identification of cache used.
     * The default implementation returns a unique string each time it is called.
     * @return the cache identification string
     */
    public String initCacheIdStr() {
        return DEFAULT_CACHE_ID + new Object().hashCode();
    }

    /**
     * Get the cache identification.
     */
    public String getCacheIdStr() {
        synchronized (this) {
            if (cacheID == null) {
                cacheID = initCacheIdStr();
                firePropertyChange("cacheID", null, cacheID); // NOI18N
            }
        }
        return cacheID;
    }
    
    /**
     * Gets the default factory {@link DefaultVcsFactory}. Subclasses may override this to return different instance of {@link VcsFactory}.
     */
    public VcsFactory getVcsFactory () {
        if (factory == null) {
            synchronized (this) {
                if (factory == null) {
                    factory = new DefaultVcsFactory(this);
                }
            }
        }
        return factory;
    }


    /*
    private void createDir(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            return ;
        }
        if (dir.mkdirs() == false) {
            E.err(g("MSG_UnableToCreateDirectory", path)); // NOI18N
            debug(g("MSG_UnableToCreateDirectory", path)); // NOI18N
        }
    }
     */

    /**
     * Perform some initialization job. This method is called both when a new instance
     * of this class is created and after deserialization. Subclasses should call super.init().
     */
    protected void init() {
        D.deb ("init()"); // NOI18N
        if (tempFiles == null) tempFiles = new Vector();
        unimportantFiles = Collections.synchronizedSet(new HashSet());
        //cache = new VcsFSCache(this/*, createNewCacheDir ()*/);
        cache = getVcsFactory().getFileCacheProvider();
        statusProvider = getVcsFactory().getFileStatusProvider();
        if (possibleFileStatusesMap == null) {
            if (statusProvider != null) {
                possibleFileStatusesMap = statusProvider.getPossibleFileStatusesTable();
            } else {
                possibleFileStatusesMap = new HashMap();
            }
        }
        if (statusIconMap == null) {
            if (statusProvider != null) {
                statusIconMap = statusProvider.getStatusIconMap();
            } else {
                statusIconMap = new HashMap();
            }
        }
        //errorDialog = new ErrorCommandDialog(null, new JFrame(), false);
        try {
            setInitRootDirectory(rootFile);
        } catch (PropertyVetoException e) {
            // Could not set root directory
        } catch (IOException e) {
            // Could not set root directory
        }
        if (multiFilesAnnotationTypes == null) {
            multiFilesAnnotationTypes = RefreshCommandSupport.DEFAULT_MULTI_FILES_ANNOTATION_TYPES;
        }
        if (annotationPattern == null) {
            annotationPattern = RefreshCommandSupport.DEFAULT_ANNOTATION_PATTERN;
        }
        if (notModifiableStatuses == null) {
            notModifiableStatuses = Collections.EMPTY_SET;
        }
        if (createRuntimeCommands == null) createRuntimeCommands = Boolean.TRUE;
        if (createVersioningSystem == null) createVersioningSystem = Boolean.FALSE;
        //if (revisionListsByName == null) revisionListsByName = new Hashtable();
        commandsPool = CommandsPool.getInstance();//new CommandsPool(this, false);
        if (numberOfFinishedCmdsToCollect == null) {
            numberOfFinishedCmdsToCollect = new Integer(RuntimeSupport.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT);
        }
        RuntimeSupport.getInstance().setCollectFinishedCmdsNum(numberOfFinishedCmdsToCollect.intValue(), getSystemName());
        if (varValueAdjustment == null) varValueAdjustment = new VariableValueAdjustment();
        initListeners();
    }

    private void initListeners() {
        settingsChangeListener = new SettingsPropertyChangeListener();
        GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        settings.addPropertyChangeListener(WeakListener.propertyChange(settingsChangeListener, settings));
        addPropertyChangeListener(new FSPropertyChangeListener());
    }
    
    protected AbstractFileSystem.List getVcsList() {
        return vcsList;
    }
    
    protected AbstractFileSystem.Info getVcsInfo() {
        return info;
    }
    
    private void assignVersioningProperties(VcsVersioningSystem vers) {
        vers.setShowDeadFiles(versioningFileSystemShowDeadFiles);
        vers.setIgnoredGarbageFiles(versioningFileSystemShowGarbageFiles);
        vers.setShowLocalFiles(versioningFileSystemShowLocalFiles);
        vers.setShowMessages(versioningFileSystemShowMessage);
        vers.setShowUnimportantFiles(versioningFileSystemShowUnimportantFiles);
        vers.setMessageLength(versioningFileSystemMessageLength);
    }
    
    /** Notifies this file system that it has been added to the repository. 
     */
    public void addNotify() {
        //System.out.println("fileSystemAdded("+this+")");
        //System.out.println("isOffLine() = "+isOffLine()+", auto refresh = "+getAutoRefresh()+", deserialized = "+deserialized);
//        if (Boolean.TRUE.equals(createRuntimeCommands)) commandsPool.setupRuntime();
        if (!isOffLine()
            && (getAutoRefresh() == GeneralVcsSettings.AUTO_REFRESH_ON_MOUNT_AND_RESTART
            || (deserialized && getAutoRefresh() == GeneralVcsSettings.AUTO_REFRESH_ON_RESTART)
            || (!deserialized && getAutoRefresh() == GeneralVcsSettings.AUTO_REFRESH_ON_MOUNT))) {
                CommandExecutorSupport.doRefresh(VcsFileSystem.this, "", true);
        }
        super.addNotify();
        if (isCreateVersioningSystem()) {
            org.openide.util.RequestProcessor.postRequest(new Runnable() {
                public void run() {
                    //VersioningExplorer.getRevisionExplorer().open();
                    if (versioningSystem == null) {
                        versioningSystem = new VcsVersioningSystem(VcsFileSystem.this);//new DefaultVersioningSystem(new VcsFileSystemInfo());
                        assignVersioningProperties((VcsVersioningSystem)versioningSystem);
                        if (cache != null) {
                            org.netbeans.modules.vcscore.cache.FileSystemCache fsCache =
                                org.netbeans.modules.vcscore.cache.CacheHandler.getInstance().getCache(cache);
                            if (fsCache != null) {
                                fsCache.addCacheHandlerListener((CacheHandlerListener) WeakListener.create(CacheHandlerListener.class, (CacheHandlerListener) versioningSystem, fsCache));
                            }
                        }
                        VersioningRepository.getRepository().addVersioningFileSystem(versioningSystem);
                    }
                }
            });
        }
        enableRefresh();
    }
    
    /** Notifies this file system that it has been removed from the repository. 
     */
    public void removeNotify() {
        disableRefresh();
        //System.out.println("fileSystem Removed("+this+")");
        //commandsPool.cleanup();
        super.removeNotify();
        if (versioningSystem != null) {
            org.openide.util.RequestProcessor.postRequest(new Runnable() {
                public void run() {
                    if (versioningSystem == null) return ;
                    VersioningRepository.getRepository().removeVersioningFileSystem(versioningSystem);
                    try {
                        VcsFileSystem.this.runAtomicAction(new FileSystem.AtomicAction() {
                            public void run() {
                                if (versioningFolderListeners != null) {
                                    for (Iterator it = versioningFolderListeners.keySet().iterator(); it.hasNext(); ) {
                                        FileObject fo = (FileObject) it.next();
                                        FileChangeListener changeL = (FileChangeListener) versioningFolderListeners.get(fo);
                                        fo.removeFileChangeListener(changeL);
                                    }
                                    versioningFolderListeners = null;
                                }
                                versioningSystem = null;
                            }
                        });
                    } catch (IOException exc) {
                        TopManager.getDefault().notifyException(exc);
                    }
                }
            });
        }
    }
    
    protected void setCreateRuntimeCommands(boolean createRuntimeCommands) {
        if (!new Boolean(createRuntimeCommands).equals(this.createRuntimeCommands)) {
            this.createRuntimeCommands = new Boolean(createRuntimeCommands);
            firePropertyChange(PROP_CREATE_RUNTIME_COMMANDS, null, this.createRuntimeCommands);
        }
    }
    
    public boolean isCreateRuntimeCommands() {
        return createRuntimeCommands.booleanValue();
    }
    
    protected void setCreateVersioningSystem(boolean createVersioningSystem) {
        if (!new Boolean(createVersioningSystem).equals(this.createVersioningSystem)) {
            this.createVersioningSystem = new Boolean(createVersioningSystem);
            firePropertyChange(PROP_CREATE_VERSIONING_EXPLORER, null, this.createVersioningSystem);
        }
    }
    
    protected boolean isCreateVersioningSystem() {
        return createVersioningSystem.booleanValue();
    }
    
    public VersioningFileSystem getVersioningFileSystem() {
        return versioningSystem;
    }
    
    protected VcsActionSupporter getVcsActionSupporter() {
        return actionSupporter;
    }
    
    protected VcsAttributes getVcsAttributes() {
        return (VcsAttributes) attr;
    }
    
    private static final long serialVersionUID =8108342718973310275L;

    /**
     * Create a new VCS filesystem.
     */
    public VcsFileSystem() {
        D.deb("VcsFileSystem()"); // NOI18N
        deserialized = false;
        info = this;
        change = this;
        actionSupporter = new VcsActionSupporter(this);
        VcsAttributes a = new VcsAttributes (info, change, this, this, actionSupporter);
        attr = a;
        list = a;
        vcsList = new VcsList();
        setRefreshTime (0); // disable the standard refresh, VcsRefreshRequest is used instead
        setVcsRefreshTime (0); // due to customization
        refreshTimeToSet = last_refreshTime;
        /*
        cacheRoot = System.getProperty("netbeans.user")+File.separator+
                    "system"+File.separator+"vcs"+File.separator+"cache"; // NOI18N
         */
        GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        setOffLine(settings.isOffLine());
        setAutoRefresh(settings.getAutoRefresh());
        setHideShadowFiles(settings.isHideShadowFiles());
        init();
        //possibleFileStatusesMap = statusProvider.getPossibleFileStatusesTable();
        D.deb("constructor done.");
    }

    public String[] getPossibleFileStatuses() {
        String[] statuses;
        if (possibleFileStatusesMap == null) return null;
        synchronized (possibleFileStatusesMap) {
            statuses = new String[possibleFileStatusesMap.size()];
            int i = 0;
            for(Iterator it = possibleFileStatusesMap.values().iterator(); it.hasNext(); i++) {
                Object obj = it.next();
                //System.out.println("getPossibleFileStatuses(): '"+obj+"', class = "+obj.getClass());
                if (obj instanceof String) statuses[i] = (String) obj;
            }
        }
        D.deb("getPossibleFileStatuses() return = "+VcsUtilities.array2string(statuses));
        return statuses;
    }

    /**
     * Get a copy of stauses transfer table.
     */
    public HashMap getPossibleFileStatusesTable() {
        HashMap statusesTable;
        if (possibleFileStatusesMap == null) return null;
        synchronized (possibleFileStatusesMap) {
            statusesTable = new HashMap(possibleFileStatusesMap);
        }
        return statusesTable;
    }

    //-------------------------------------------
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException, NotActiveException {
        // cache is transient
        //System.out.println("VcsFileSystem.readObject() ...");
        //try {
            //ObjectInputStream din = (ObjectInputStream) in;
        deserialized = true;
        boolean localFilesOn = in.readBoolean ();
        in.defaultReadObject();
        actionSupporter = new VcsActionSupporter(this);
        if (!(attr instanceof VcsAttributes)) {
            VcsAttributes a = new VcsAttributes (info, change, this, this, actionSupporter);
            attr = a;
            list = a;
        } else {
            ((VcsAttributes) attr).setCurrentSupporter(actionSupporter);
        }
        if (vcsList == null) vcsList = new VcsList();
        //last_rootFile = rootFile;
        last_refreshTime = getCustomRefreshTime ();
        last_useUnixShell = useUnixShell;
        updateEnvironmentVars();
        init();
        if (ignoredGarbageFiles == null) {
            ignoredGarbageFiles = "";
        } else if (ignoredGarbageFiles.length () > 0) {
            try {
                ignoredGarbageRE = new RE (ignoredGarbageFiles);
            } catch (RESyntaxException rese) {
                TopManager.getDefault ().notifyException (rese);
            }
        }
        //cache.setLocalFilesAdd (localFilesOn);
        if (null == processUnimportantFiles) processUnimportantFiles = Boolean.FALSE;
        last_rootFile = new File(getFSRoot());
        //} catch (Throwable thr) {
        //    System.out.println("VcsFileSystem.readObject():");
        //    thr.printStackTrace();
        //}
        setExpertMode(isAcceptUserParams()); // to be compatible with 3.1
    }


    //-------------------------------------------
    private void writeObject(ObjectOutputStream out) throws IOException {
        //D.deb("writeObject() - saving bean"); // NOI18N
        // cache is transient
        numberOfFinishedCmdsToCollect = new Integer(RuntimeSupport.getInstance().getCollectFinishedCmdsNum(getSystemName()));
        
        out.writeBoolean (true/*cache.isLocalFilesAdd ()*/); // for compatibility
        String myPassword = password;
        if (!rememberPassword) password = null;
        out.defaultWriteObject();
        password = myPassword; // to keep the password set if the object is written,
                               // but still in use.
    }


    //-------------------------------------------
    public void setDebug(boolean debug){
        if (this.debug != debug) {
            this.debug = debug;
            firePropertyChange(PROP_DEBUG, new Boolean(!debug), new Boolean(debug));
        }
    }


    //-------------------------------------------
    public boolean getDebug(){
        return debug;
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
        Map env = VcsUtilities.addEnvVars(systemEnv, getVariablesAsHashtable(),
                                          VAR_ENVIRONMENT_PREFIX, VAR_ENVIRONMENT_REMOVE_PREFIX);
        environmentVars = VcsUtilities.getEnvString(env);
    }

    //-------------------------------------------
    public Vector getVariables(){
        return variables;
    }

    /**
     * Get the variable value adjustment utility object.
     */
    public VariableValueAdjustment getVarValueAdjustment() {
        return varValueAdjustment;
    }
    
    /** Notify the filesystem, that an variable has changed and should be saved.
     * @param name the name of a changed variables. If <code>null</code> we suppose,
     *        that all variables changed. */
    public void variableChanged(String name) {
        firePropertyChange(PROP_VARIABLES, null, variables);
    }

    /**
     * Set the file system's variables.
     * @param variables the vector of <code>VcsConfigVariable</code> objects.
     */
    public void setVariables(Vector variables) {
        //System.out.println("setVariables("+VcsUtilities.toSpaceSeparatedString(variables)+")");
        //D.deb ("setVariables()"); // NOI18N
        boolean containsCd = false;
        // Windows != 95 && != 98 needs "cd /D" to change the directory accross disks !!!
        // Windows 95 || 98 do not recognize /D => change the directory accross disks is NOT possible by a single command !!!
        int os = Utilities.getOperatingSystem();
        String cdValue = (Utilities.isWindows()
                          && os != Utilities.OS_WIN95
                          && os != Utilities.OS_WIN98) ? "cd /D" : "cd";
        int len = variables.size ();
        VcsConfigVariable var;
        for(int i = 0; i < len; i++) {
            var = (VcsConfigVariable) variables.get (i);
            /*
            if(var.getName ().equalsIgnoreCase (LOCAL_FILES_ADD_VAR)) {
                if(var.getValue ().equalsIgnoreCase (VAR_TRUE)) {
                    cache.setLocalFilesAdd (true);
                }
                if(var.getValue ().equalsIgnoreCase (VAR_FALSE)) {
                    cache.setLocalFilesAdd (false);
                }
            }
             */
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
            if(var.getName ().equals ("CD")) { // NOI18N
                //var.setValue (cdValue); <- I don't want to change the value if it is set !!
                containsCd = true;
            }
        }
        /*
        if (variables.equals(this.variables)) {
            return ;
        }
         */
        if (!containsCd) {
            variables.add (new VcsConfigVariable ("CD", "cd", cdValue, false, false, false, "", 0)); // NOI18N
        }
        Vector old = this.variables;
        synchronized (this) {
            this.variables = variables;
        }
        
        VcsConfigVariable mod = (VcsConfigVariable) variablesByName.get("MODULE");
        HashMap newVarsByName = new HashMap();
        for (int i = 0, n = variables.size (); i < n; i++) {
            var = (VcsConfigVariable) variables.get (i);
            newVarsByName.put (var.getName (), var);
        }
        if (!newVarsByName.containsKey("MODULE") && mod != null) { // The module was previosly defined, it has to be copied to new variables.
            this.variables.add(mod);
            newVarsByName.put (mod.getName (), mod);
        }
        synchronized (this) {
            variablesByName = new Hashtable(newVarsByName);
        }
        updateEnvironmentVars();
        varValueAdjustment.setAdjust(getVariablesAsHashtable());

        firePropertyChange(PROP_VARIABLES, old, variables);
        //try {
        setAdjustedSystemName(computeSystemName(rootFile));
        //} catch (PropertyVetoException exc) {}
    }

    public static String substractRootDir(String rDir, String module) {
        if (module == null || module.length() == 0) return rDir;
        String m;
        if (module.charAt(module.length() - 1) == File.separatorChar)
            m = module.substring(0, module.length() - 1);
        else
            m = module.substring(0);
        String rDirSlashes;
        boolean chRDir = false;
        if (File.separatorChar != '/' && rDir.indexOf(File.separatorChar) > 0) {
            rDirSlashes = rDir.replace(File.separatorChar, '/');
            chRDir = true;
        } else rDirSlashes = rDir;
        String moduleSlashes;
        if (File.separatorChar != '/' && m.indexOf(File.separatorChar) > 0) {
            moduleSlashes = m.replace(File.separatorChar, '/');
        } else moduleSlashes = m;
        int i = rDirSlashes.lastIndexOf(moduleSlashes);
        if (i <= 0) return rDir;
        if (chRDir) return rDir.substring(0, i-1).replace('/', File.separatorChar);
        else return rDir.substring(0, i-1); // I have to remove the slash also.
    }


    //-------------------------------------------
    public synchronized Hashtable getVariablesAsHashtable() {
        int len = getVariables().size();
        Hashtable result = new Hashtable(len+10);
        for(int i = 0; i < len; i++) {
            VcsConfigVariable var = (VcsConfigVariable) getVariables().elementAt (i);
            result.put(var.getName (), var.getValue ());
        }

        result.put("netbeans.home", System.getProperty("netbeans.home"));
        result.put("netbeans.user", System.getProperty("netbeans.user"));
        result.put("java.home", System.getProperty("java.home"));
        String osName=System.getProperty("os.name");
        result.put("classpath.separator", File.pathSeparator); // NOI18N
        result.put("path.separator", ""+File.separator); // NOI18N
        if(result.get("PS")==null) { // NOI18N
            result.put("PS", ""+File.separator); // NOI18N
        }

        String rootDir = getRootDirectory().toString();
        String module = (String) result.get("MODULE"); // NOI18N
        //if (osName.indexOf("Win") >= 0) // NOI18N
        //module=module.replace('\\','/');
        result.put("ROOTDIR", VcsFileSystem.substractRootDir(rootDir, module)); // NOI18N
        result.put(VAR_EXPERT_MODE, expertMode ? "expert" : "");

        return result;
    }

    public String getQuoting() {
        VcsConfigVariable quotingVar = (VcsConfigVariable) variablesByName.get(VAR_QUOTING);
        String quoting = null;
        if (quotingVar != null) quoting = quotingVar.getValue();
        if (quoting == null) quoting = DEFAULT_QUOTING_VALUE;
        return quoting;
    }


    //-------------------------------------------
    public void setPassword(String password){
        if (this.password == null && password != null ||
            this.password != null && !this.password.equals(password)) {
            
            this.password = password;
            firePropertyChange(PROP_PASSWORD, null, password);
        }
    }

    //-------------------------------------------
    public String getPassword(){
        return password;
    }
    
    public void setRememberPassword(boolean remember) {
        if (this.rememberPassword != remember) {
            this.rememberPassword = remember;
            firePropertyChange(PROP_REMEMBER_PASSWORD, new Boolean(!remember), new Boolean(remember));
        }
    }
    
    public boolean isRememberPassword() {
        return rememberPassword;
    }

    private void createTempPromptFiles(Hashtable promptFile) {
        for(Enumeration enum = promptFile.keys(); enum.hasMoreElements(); ) {
            String key = (String) enum.nextElement();
            String fileName = (String) promptFile.get(key);
            try {
                File file = File.createTempFile(FILE_PROMPT_PREFIX, null);
                file.deleteOnExit(); // automatically delete the file when JVM goes down
                tempFiles.add(file);
                promptFile.put(key, file.getAbsolutePath());
                if (fileName.length() > 0) {
                    File fileOrig = new File(fileName);
                    if (fileOrig.exists() && fileOrig.canRead()) {
                        try {
                            FileWriter writer = new FileWriter(file);
                            FileReader reader = new FileReader(fileOrig);
                            char[] buf = new char[500];
                            int len = 0;
                            while((len = reader.read(buf)) > 0) writer.write(buf, 0, len);
                            reader.close();
                            writer.close();
                        } catch (FileNotFoundException exc) {
                            TopManager.getDefault().notifyException(exc);
                        }
                    }
                }
            } catch (IOException exc) {
                TopManager.getDefault().notifyException(exc);
            }
        }
    }

    public void removeTempFiles() {
        for(Enumeration enum = tempFiles.elements(); enum.hasMoreElements(); ) {
            File file = (File) enum.nextElement();
            //File file = new File(name);
            boolean success = file.delete();
        }
        tempFiles.removeAllElements();
        // TODO: should be called when the last VCS command finished
    }

    /**
     * Allows some cleanup of the document which the user is asked for.
     * doc The Document
     * promptNum the order of the document
     * docIdentif some identification that can be set in settting the listener.
     */
    public void filePromptDocumentCleanup(javax.swing.JTextArea ta, int promptNum, Object docIdentif) {
        // Let the document unchanged by default
    }

    protected void warnDirectoriesDoNotExists() {
        D.deb("warnDirectoriesDoNotExists()");

        String module;
        File root;
        synchronized (this) {
            Hashtable vars = getVariablesAsHashtable();
            module = (String) vars.get("MODULE");
            if (module == null) module = "";
            String rootDir = VcsFileSystem.substractRootDir(getRootDirectory().toString(), module);
            root = new File(rootDir);
            //D.deb("RootDirectory = "+rootDir);
        }
        if( root == null || !root.isDirectory() ){
            //E.err("not directory "+root); // NOI18N
            D.deb("NOT DIRECTORY: "+root);
            final String badDir = root.toString();
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
                                                       public void run () {
                                                           TopManager.getDefault ().notify (new NotifyDescriptor.Message(MessageFormat.format (org.openide.util.NbBundle.getBundle(VcsFileSystem.class).getString("Filesystem.notRootDirectory"), new Object[] { badDir } )));
                                                       }
                                                   });
            return ;
        }
        File moduleDir = new File(root, module);
        D.deb("moduleDir = "+moduleDir);
        if (moduleDir == null || !moduleDir.isDirectory()) {
            D.deb("NOT DIRECTORY: "+moduleDir);
            //System.out.println("NOT DIRECTORY: "+moduleDir);
            final String badDir = module;
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
                                                       public void run () {
                                                           TopManager.getDefault ().notify (new NotifyDescriptor.Message(MessageFormat.format (org.openide.util.NbBundle.getBundle(VcsFileSystem.class).getString("Filesystem.notModuleDirectory"), new Object[] { badDir } )));
                                                       }
                                                   });
        }
    }
    
    public String getAnnotationPattern() {
        return annotationPattern;
    }
    
    public void setAnnotationPattern(String annotationPattern) {
        String old = this.annotationPattern;
        this.annotationPattern = annotationPattern;
        firePropertyChange(PROP_ANNOTATION_PATTERN, old, this.annotationPattern);
    }
    
    public int[] getMultiFileAnnotationTypes() {
        return multiFilesAnnotationTypes;
    }
    
    public void setMultiFileAnnotationTypes(int[] multiFileAnnotationTypes) {
        if (multiFilesAnnotationTypes.length != RefreshCommandSupport.NUM_ELEMENTS) {
            throw new IllegalArgumentException("Wrong length of the array ("+multiFileAnnotationTypes.length+" != "+RefreshCommandSupport.NUM_ELEMENTS+")");
        }
        int[] old = this.multiFilesAnnotationTypes;
        this.multiFilesAnnotationTypes = multiFilesAnnotationTypes;
        firePropertyChange(PROP_ANNOTATION_TYPES, old, this.multiFilesAnnotationTypes);
    }

    //-------------------------------------------
    public FileSystem.Status getStatus(){
        return this;
    }

    public String getStatus(FileObject fo) {
        String status;
        if (statusProvider != null) {
            String fullName = fo.getPackageNameExt('/','.');
            status = statusProvider.getFileStatus(fullName).trim();
        } else status = "";
        return status;
    }

    public String getStatus(DataObject dobj) {
        Set files = dobj.files();
        Object[] oo = files.toArray();
        int len = oo.length;
        if (len == 0) return null;
        if (statusProvider != null) {
            if (len == 1) return RefreshCommandSupport.getStatusAnnotation("", ((FileObject) oo[0]).getPackageNameExt('/', '.'),
                                                                           "${"+RefreshCommandSupport.ANNOTATION_PATTERN_STATUS+"}", statusProvider);
            else          return RefreshCommandSupport.getStatusAnnotation("", getImportantFiles(oo),
                                                                           "${"+RefreshCommandSupport.ANNOTATION_PATTERN_STATUS+"}", statusProvider,
                                                                           multiFilesAnnotationTypes);
        } else return "";
    }

    public String getLocker(FileObject fo) {
        String locker;
        if (statusProvider != null) {
            String fullName = fo.getPackageNameExt('/','.');
            locker = statusProvider.getFileLocker(fullName).trim();
        } else locker = "";
        return locker;
    }
    
    /**
     * Get the annotate icon for a single file. It does not have to be represented by a FileObject.
     */
    Image annotateIcon(Image icon, int iconType, String fullName) {
        if (statusProvider != null) {
            String status = statusProvider.getFileStatus(fullName);
            if (status != null) {
                Image img = (Image) statusIconMap.get(status);
                //System.out.println("annotateIcon: status = "+status+" => img = "+img);
                if (img == null) img = statusIconDefault;
                if (img != null) {
                    icon = org.openide.util.Utilities.mergeImages(icon, img, BADGE_ICON_SHIFT_X, BADGE_ICON_SHIFT_Y);
                }
            }
        }
        return icon;
    }

    //-------------------------------------------
    public Image annotateIcon(Image icon, int iconType, Set files) {
        //D.deb("annotateIcon()"); // NOI18N
        /*
        System.out.print("annotateIcon(");
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            System.out.print(it.next()+", ");
        }
        System.out.println(")");
        Frame f = new Frame();
        f.add(new Label("Original :"));
        f.add(new JButton(new ImageIcon(icon)));
        f.pack();
        f.setVisible(true);
         */
        Object[] oo = files.toArray();
        int len = oo.length;
        if (len == 0/* || name.indexOf(getRootDirectory().toString()) >= 0*/) {
            return icon;
        }

        if (statusProvider != null) {
            ArrayList importantFiles = getImportantFiles(oo);
            len = importantFiles.size();
            String status = null;
            if (len == 1) {
                String fullName = (String) importantFiles.get(0);
                if ("".equals(fullName)) return icon; // DO NOT override the root icon !
                status = statusProvider.getFileStatus(fullName);
            } else {
                for (Iterator it = importantFiles.iterator(); it.hasNext(); ) {
                    String fullName = (String) it.next();
                    String fileStatus = statusProvider.getFileStatus(fullName);
                    if (status == null) status = fileStatus;
                    if (!status.equals(fileStatus)) {
                        status = statusProvider.getNotInSynchStatus();
                        break;
                    }
                }
            }
            if (status != null) {
                Image img = (Image) statusIconMap.get(status);
                //System.out.println("annotateIcon: status = "+status+" => img = "+img);
                if (img == null) img = statusIconDefault;
                if (img != null) {
                    icon = org.openide.util.Utilities.mergeImages(icon, img, BADGE_ICON_SHIFT_X, BADGE_ICON_SHIFT_Y);
                }
            }
        }
        /*
        f = new Frame();
        f.add(new Label("MyReturned :"));
        f.add(new JButton(new ImageIcon(icon)));
        f.pack();
        f.setVisible(true);
         */
        return icon;
    }

    /**
     * Get the annotate name for a single file. It does not have to be represented by a FileObject.
     */
    String annotateName(String fullName, String displayName) {
        String result;
        if (statusProvider != null) {
            result = RefreshCommandSupport.getStatusAnnotation(displayName, fullName, annotationPattern, statusProvider);
        } else {
            result = displayName;
        }
        return result;
    }

    /** Annotate a single file. It can annotate only files represented by a FileObject.
     * @params fullName The full path to the file.
     * @return the annotation string
     */
    public String annotateName(String fullName) {
        FileObject fo = findResource(fullName);
        if (fo == null) throw new IllegalArgumentException(fullName);
        HashSet hset = new HashSet(1);
        hset.add(fo);
        String ext = fo.getExt();
        String name = fo.getName();
        if (ext != null && ext.length() > 0) name += "."+ext;
        return annotateName(name, Collections.synchronizedSet(hset));
    }

    /** Find the file object from a file path.
     * @param fullName the full path to the file
     * @return the file object of that file
     */
    public FileObject findFileObject(String fullName) {
        return findResource(fullName);
    }

    /** Annotate the Data Object file.
     * @params fullName The full path to the file.
     * @return the annotation string
     */
    public String annotateDOName(String fullName) throws org.openide.loaders.DataObjectNotFoundException {
        FileObject fo = findResource(fullName);
        if (fo == null) throw new IllegalArgumentException(fullName);
        //try {
        DataObject dobj = DataObject.find(fo);
        //} catch (org.openide.loaders.DataObjectNotFoundException exc) {
        //    throw new org.openide.loaders.DataObjectNotFoundException(exc.getFileObject());
        //}
        return annotateName(fo.getName(), dobj.files());
    }
    
    /** Annotate the set of files with additional version control attributes.
     * @param name the original annotation
     * @param files the files to annotate
     * @return the annotation string
     */
    public String annotateName(String name, Set files) {
        /*
        boolean print = false; //"Class1".equals(name);
        if (print) {
            String filesStr = "";
            for (Iterator it = files.iterator(); it.hasNext(); ) filesStr += ((FileObject) it.next()).getNameExt() + ", ";
            System.out.println("annotateName("+name+", "+filesStr.substring(0, filesStr.length() - 2)+")");
            Thread.currentThread().dumpStack();
        }
         */
        String result = name;
        if (result == null)
            return result;  // Null name, ignore it
        //Object[] oo = files.toArray();
        int len = files.size();
        if (len == 0 || name.indexOf(getRootDirectory().toString()) >= 0) {
            return result;
        }

        if (statusProvider != null) {
            ArrayList importantFiles = getImportantFiles(files.toArray());
            len = importantFiles.size();
            //if (print) System.out.println(" length of important = "+len);
            if (len == 1) {
                String fullName = (String) importantFiles.get(0);
                //System.out.println(" fullName = "+fullName);
                result = RefreshCommandSupport.getStatusAnnotation(name, fullName, annotationPattern, statusProvider);
            } else {
                importantFiles = VcsUtilities.reorderFileObjects(importantFiles);
                //if (print) System.out.println(" importantFiles = "+VcsUtilities.arrayToString((String[]) importantFiles.toArray(new String[0])));
                result = RefreshCommandSupport.getStatusAnnotation(name, importantFiles, annotationPattern, statusProvider, multiFilesAnnotationTypes);
            }
        }
        //if (print) System.out.println("  annotateName("+name+") -> result='"+result+"'");
        //D.deb("annotateName("+name+") -> result='"+result+"'"); // NOI18N
        return result;
    }


    /**
     * Get the important files. Also add files scheduled for remove.
     * @return the Vector of important files as Strings
     */
    private ArrayList/*VcsFile*/ getImportantFiles(Object[] oo){
        //D.deb("getImportantFiles()"); // NOI18N
        ArrayList result = new ArrayList();
        int len=oo.length;

        boolean processAll = isProcessUnimportantFiles();
        for(int i = 0; i < len; i++) {
            FileObject ff = (FileObject) oo[i];
            //System.out.println(" getImportantFiles("+ff+")");
            boolean isFromThisFs = true;
            try {
                isFromThisFs = ff.getFileSystem().equals(this);
            } catch (org.openide.filesystems.FileStateInvalidException exc) {
                isFromThisFs = true;
            }
            //System.out.println(" isFromThisFs = "+isFromThisFs);
            if (isFromThisFs) {            
                String fullName = ff.getPackageNameExt('/','.');
                //System.out.println(" fullName = "+fullName+", isImportant = "+isImportant(fullName));
                if (processAll || isImportant(fullName)) {
                    result.add(fullName);
                }
            } else {
                try {
                    if (ff.getFileSystem() instanceof VersioningFileSystem) {
                        String fullName = ff.getPackageNameExt('/','.');
                        result.add(fullName);
                    }
                } catch (FileStateInvalidException exc) {}
            }
            // check for scheduled files:
            Set[] scheduled = (Set[]) ff.getAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
            if (scheduled != null && scheduled[0] != null) {
                String filePath = (String) ff.getAttribute(VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR);
                if (filePath != null) {
                    File currentFile = org.openide.execution.NbClassPath.toFile(ff);
                    if (currentFile != null && !filePath.equals(currentFile.getAbsolutePath())) {
                        // the file was moved/copied/renamed to another location.
                        // Delete all it's scheduling attributes, because they are out-of-date
                        try {
                            ff.setAttribute(VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR, null);
                            ff.setAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR, null);
                            scheduled[0].clear();
                        } catch (IOException exc) {}
                    }
                }
                // add all scheduled files
                result.addAll(scheduled[0]);
            }
        }
        return result;
    }

    /**
     * Get the VCS actions.
     * @return the actions retrieved from <code>VcsFactory.getActions(null)</code>
     */
    public SystemAction[] getActions() {
        return getVcsFactory ().getActions(null);
    }

    /**
     * Get the VCS actions on a collection of <code>FileObject</code>s.
     * @param fos the collection of <code>FileObject</code>s to act on.
     * @return the actions retrieved from <code>VcsFactory.getActions(fos)</code>
     */
    public SystemAction[] getActions(Set fos) {
        /*
        System.out.print("getActions(");
        for(Iterator it = fos.iterator(); it.hasNext(); ) {
            System.out.print(((FileObject) it.next()).getNameExt()+(it.hasNext() ? ", " : ""));
        }
        System.out.println(")");
         */
        return getVcsFactory ().getActions(fos);
    }

    /**
     * Get a human presentable name of the file system
     */
    public String getDisplayName() {
        //System.out.print("VcsFileSystem.getDisplayName(): commandsRoot = "+commandsRoot);
        Hashtable vars = getVariablesAsHashtable();
        String displayNameAnnotation = (String) vars.get(VAR_FS_DISPLAY_NAME_ANNOTATION);
        if (displayNameAnnotation != null) {
            if (statusProvider != null) {
                displayNameAnnotation = RefreshCommandSupport.getStatusAnnotation("", "", displayNameAnnotation, statusProvider, vars);
            } else {
                displayNameAnnotation = Variables.expand(vars, displayNameAnnotation, false);
            }
            //System.out.println(displayNameAnnotation);
            return displayNameAnnotation;
        }
        VcsConfigVariable preDisplayNameVar = (VcsConfigVariable) variablesByName.get(VAR_FS_DISPLAY_NAME);
        if (preDisplayNameVar != null) {
            //System.out.println(preDisplayNameVar.getValue() + " " + rootFile.toString());
            return preDisplayNameVar.getValue() + " " + rootFile.toString();
        } else if (commandsRoot != null) {
            String VCSName = commandsRoot.getDisplayName();
            //System.out.println("VCSName = '"+VCSName+"'");
            if (VCSName != null && VCSName.length() > 0) {
                //System.out.println(VCSName + " " + rootFile.toString());
                return VCSName + " " + rootFile.toString();
            }
        }
        //System.out.println(g("LAB_FileSystemValid", rootFile.toString ()));
        return g("LAB_FileSystemValid", rootFile.toString ()); // NOI18N
    }

    /**
     * Set the root directory of the filesystem to the parameter passed.
     * @param r file to set root to
     * @exception PropertyVetoException if the value if vetoed by someone else (usually
     *    by the {@link org.openide.filesystems.Repository Repository})
     * @exception IOException if the root does not exists or some other error occured
     */
    private synchronized void setInitRootDirectory(File r) throws PropertyVetoException, IOException {
        Hashtable vars = getVariablesAsHashtable();
        String module = (String) vars.get("MODULE");
        if (module == null) module = "";
        String root = r.getCanonicalPath();
        if (module.length() > 0) {
            int i = root.lastIndexOf(module);
            if (i > 0) root = root.substring(0, i - 1);
        }
        r = new File(root);
        setRootDirectory(r);
    }

    /** Set the root directory of the file system. It adds the module name to the parameter.
     * @param r file to set root to plus module name
     * @exception PropertyVetoException if the value if vetoed by someone else (usually
     *    by the {@link org.openide.filesystems.Repository Repository})
     * @exception IOException if the root does not exists or some other error occured
     */
    public synchronized void setRootDirectory (File r) throws PropertyVetoException, IOException {
        setRootDirectory(r, false);
    }

    /** Set the root directory of the file system. It adds the module name to the parameter.
     * @param r file to set root to plus module name
     * @exception PropertyVetoException if the value if vetoed by someone else (usually
     *    by the {@link org.openide.filesystems.Repository Repository})
     * @exception IOException if the root does not exists or some other error occured
     */
    protected final synchronized void setRootDirectory (File r, boolean forceToSet) throws PropertyVetoException, IOException {
        //D.deb("setRootDirectory("+r+")"); // NOI18N
        if (/*!r.exists() ||*/ r.isFile ()) {
            throw new IOException(g("EXC_RootNotExist", r.toString ())); // NOI18N
        }

        Hashtable vars = getVariablesAsHashtable();
        String module = (String) vars.get("MODULE"); // NOI18N
        if (module == null) module = ""; // NOI18N
        File root = new File(r, module);
        if (!forceToSet && rootFile.equals(root)) return ;
        String name = computeSystemName (root);
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
        D.deb("Setting system name '"+name+"'"); // NOI18N
        setAdjustedSystemName(name);

        rootFile = root;
        last_rootFile = new File(getFSRoot());
        ready=true ;
        
        //HACK 
  //      this.cache.refreshDir(this.getRelativeMountPoint());
         
        firePropertyChange(PROP_ROOT, null, refreshRoot ());
        if (cache != null) {
            cache.setFSRoot(r.getAbsolutePath());
            cache.setRelativeMountPoint(module);
        }
    }
    
    /** Modified to never throw PropertyVetoException by the name adjustment. */
    public void setAdjustedSystemName(String name) {
        int seed = 0;
        String testName = name;
        do {
            try {
                setSystemName(testName);
                testName = null;
            } catch (PropertyVetoException pvexc) {
                testName = name + " - " + (++seed); // NOI18N
            }
        } while (testName != null);
    }

    /** Get the root directory of the file system.
     * @return root directory
     */
    public File getRootDirectory () {
        return rootFile;
    }

    /** Set whether the file system should be read only.
     * @param flag <code>true</code> if it should
     */
    public void setReadOnly(boolean flag) {
        D.deb("setReadOnly("+flag+")"); // NOI18N
        if (flag != readOnly) {
            readOnly = flag;
            firePropertyChange (PROP_READ_ONLY, new Boolean (!flag), new Boolean (flag));
        }
    }

    /* Test whether file system is read only.
     * @return <code>true</code> if file system is read only
     */
    public boolean isReadOnly() {
        //D.deb("isReadOnly() ->"+readOnly); // NOI18N
        return readOnly;
    }

    /** Prepare environment by adding the root directory of the file system to the class path.
     * @param environment the environment to add to
     */
    public void prepareEnvironment(FileSystem.Environment environment) {
        D.deb("prepareEnvironment() ->"+rootFile.toString()); // NOI18N
        environment.addClassPath(rootFile.toString ());
    }

    /** Compute the system name of this file system for a given root directory.
     * <P>
     * The default implementation simply returns the filename separated by slashes.
     * @see FileSystem#setSystemName
     * @param rootFile root directory for the filesystem
     * @return system name for the filesystem
     */
    private String computeSystemNameBase (File rootFile) {
        //System.out.println("computeSystemName()");
        //D.deb("computeSystemName() ->"+rootFile.toString ().replace(File.separatorChar, '/') ); // NOI18N
        Hashtable vars = getVariablesAsHashtable();
        String systemNameAnnotation = (String) vars.get(VAR_FS_SYSTEM_NAME_ANNOTATION);
        if (systemNameAnnotation != null) {
            if (statusProvider != null) {
                systemNameAnnotation = RefreshCommandSupport.getStatusAnnotation("", "", systemNameAnnotation, statusProvider, vars);
            } else {
                systemNameAnnotation = Variables.expand(vars, systemNameAnnotation, false);
            }
            return systemNameAnnotation;
        }
        return this.getClass().getName()+" "+rootFile.toString ().replace(File.separatorChar, '/');
    }

    /** Compute the system name of this file system for a given root directory.
     * <P>
     * The default implementation simply returns the filename separated by slashes
     * or looks for special system name annotation variable.
     * This method also assures, that the system name will be unique among
     * mounted filesystems. Thus when this filesystem will be mounted it will
     * not be invalidated.
     * @see FileSystem#setSystemName
     * @param rootFile root directory for the filesystem
     * @return system name for the filesystem
     */
    protected String computeSystemName(File rootFile) {
        String name = computeSystemNameBase(rootFile);
        String testName = name;
        int seed = 0;
        do {
            Enumeration en = TopManager.getDefault ().getRepository ().fileSystems ();
            while (en.hasMoreElements ()) {
                FileSystem fs = (FileSystem) en.nextElement ();
                if (fs == this) continue;
                if (fs.getSystemName().equals(testName)) {
                    testName = name + " - " + (++seed);
                    break;
                }
            }
            if (!en.hasMoreElements()) {
                name = testName;
                testName = null;
            }
        } while (testName != null);
        return name;
    }

    /** Get file representation for given string name.
     * @param name the name
     * @return the file
     */
    public File getFile (String name) {
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
            return new File(path);
        } else {
            return file;
        }
    }

    //-------------------------------------------
    //
    // List
    //

    String[] getLocalFiles(String name) {
        File dir = new File(getRootDirectory(), name);
        if (dir == null || !dir.exists() || !dir.canRead()) return new String[0];
        String files[] = dir.list(getLocalFileFilter());
        return files;
    }
    
    /**
     * The file scheduled for remove is on the disk.
     * If it does not contain VcsAttributes.VCS_SCHEDULING_REMOVE,
     * it will be removed from the list of scheduled files, because it was
     * deleted and reappeared.
     */
    private void checkScheduledLocals(String path, Collection locals, Map removedFilesScheduledForRemove) {
        FileStatusProvider status = getStatusProvider();
        if (status == null) return ;
        VcsConfigVariable schVar = (VcsConfigVariable) variablesByName.get(VAR_STATUS_SCHEDULED_REMOVE);
        String scheduledStatusRemove = (schVar != null) ? schVar.getValue() : null;
        if (scheduledStatusRemove == null) return ;
        for (Iterator it = locals.iterator(); it.hasNext(); ) {
            String name = (path.length() > 0) ? (path + "/" + it.next()) : (""+it.next());
            if (removedFilesScheduledForRemove.containsKey(name)) {
                String primary = (String) removedFilesScheduledForRemove.get(name);
                //System.out.println("checkScheduledLocals("+name+")");
                String attribute = (String) attr.readAttribute(name, VcsAttributes.VCS_SCHEDULED_FILE_ATTR);
                //System.out.println("attr("+VcsAttributes.VCS_SCHEDULED_FILE_ATTR+") = "+attr);
                if (!VcsAttributes.VCS_SCHEDULING_REMOVE.equals(attribute) && isSchedulingDone(name)) {
                    removeScheduledFromPrimary(name, primary, 0);
                }
            }
        }
    }

    String[] addLocalFiles(String name, String[] cachedFiles, Map removedFilesScheduledForRemove) {
        String[] files = getLocalFiles(name);
        String[] mergedFiles;
        if (files == null || files.length == 0) {
            files = new String[0];
            mergedFiles = cachedFiles;
        } else {
            Vector cached = new Vector(Arrays.asList(cachedFiles));
            Vector local = new Vector(Arrays.asList(files));
            local.removeAll(cached);
            checkScheduledLocals(name, local, removedFilesScheduledForRemove);
            cached.addAll(local);
            mergedFiles = (String[]) cached.toArray(new String[0]);
        }
        if (cache != null) {
            if (missingFileStatus != null || missingFolderStatus != null) {
                markAsMissingFiles(name, files, cachedFiles);
            }
        }
        return mergedFiles;
    }
    
    private void markAsMissingFiles(String name, String[] local, String[] cached) {
        java.util.List locals = Arrays.asList(local);
        //if (name.length() > 0) name += "/";
        ArrayList files = new ArrayList();
        if (missingFileStatus != null && missingFileStatus.equals(missingFolderStatus) &&
            notMissingableFileStatuses.equals(notMissingableFolderStatuses)) {
            
            for (int i = 0; i < cached.length; i++) {
                if (!locals.contains(cached[i])) files.add(cached[i]);//statusProvider.setFileMissing(name + cached[i]);
            }
            if (files.size() > 0) {
                cache.setExistingFileStatus(name, (String[]) files.toArray(new String[0]),
                                            missingFileStatus, notMissingableFileStatuses, true);
            }                        
        } else {
            ArrayList folders = new ArrayList();
            String name1 = (name.length() > 0) ? name + "/" : name;
            for (int i = 0; i < cached.length; i++) {
                if (!locals.contains(cached[i])) {
                    if (folder(name1 + cached[i])) folders.add(cached[i]);//statusProvider.setFileMissing(name + cached[i]);
                    else files.add(cached[i]);
                }
            }
            if (missingFileStatus != null && files.size() > 0) {
                cache.setExistingFileStatus(name, (String[]) files.toArray(new String[0]),
                                            missingFileStatus, notMissingableFileStatuses, true);
            }
            if (missingFolderStatus != null && folders.size() > 0) {
                cache.setExistingFileStatus(name, (String[]) folders.toArray(new String[0]),
                                            missingFolderStatus, notMissingableFolderStatuses, true);
            }
        }
    }

    /* Get children files inside a folder
     * @param name the name of the folder
     */
    public String[] children (String name) {
        //D.deb("children('"+name+"')"); // NOI18N
        //System.out.println("children('"+name+"'), refresh time = "+getVcsRefreshTime());
        String[] vcsFiles = null;
        String[] files = null;

        if (!ready) {
            D.deb("not ready"); // NOI18N
            //System.out.println("children: not ready !!"); // NOI18N
            return new String[0];
        }

        HashMap removedFilesScheduledForRemove = new HashMap();
        if (cache != null && !isHideShadowFiles()) {// && cache.isDir(name)) {
            cache.readDirFromDiskCache(name);
            vcsFiles = cache.getFilesAndSubdirs(name);
            if (!isShowDeadFiles()) {
                vcsFiles = filterDeadFilesOut(name, vcsFiles);
            }
            //System.out.println("  getFilesAndSubdirs = "+VcsUtilities.arrayToString(vcsFiles));
            //D.deb("vcsFiles=" + VcsUtilities.arrayToString(vcsFiles)); // NOI18N
            /*
            String p=""; // NOI18N
            try{
                p=rootFile.getCanonicalPath();
            }
            catch (IOException e){
                E.err(e,"getCanonicalPath() failed"); // NOI18N
            }
            files=cache.dirsFirst(p+File.separator+name,vcsFiles);
            D.deb("files="+VcsUtilities.arrayToString(files)); // NOI18N
            return files;
            */
            if (vcsFiles != null) {
                vcsFiles = filterScheduledSecondaryFiles(name, vcsFiles, removedFilesScheduledForRemove);
            }
        }
        if (vcsFiles == null) files = getLocalFiles(name);
        else files = addLocalFiles(name, vcsFiles, removedFilesScheduledForRemove);
        //cleanupNonExistingAddedFiles(name, files);
        //D.deb("children('"+name+"') = "+VcsUtilities.arrayToString(files));
        if (cache != null) {
            VcsCacheDir cacheDir = (VcsCacheDir) cache.getDir(name);
            //System.out.println("files = "+VcsUtilities.arrayToString(files));
            //System.out.println("cacheDir = "+cacheDir+"; is Loaded = "+((cacheDir != null) ? ""+cacheDir.isLoaded() : "x")+", is Local = "+((cacheDir != null) ? ""+cacheDir.isLocal() : "x"));
            if (files.length == 0 && (cacheDir == null || (!cacheDir.isLoaded() && !cacheDir.isLocal())) ||
                (cacheDir == null || (!cacheDir.isLoaded() && !cacheDir.isLocal())) && areOnlyHiddenFiles(files)) cache.readDir(name/*, false*/); // DO refresh when the local directory is empty !
        }
        //System.out.println("children = "+files);
        //System.out.println("  children = "+VcsUtilities.arrayToString(files));
        if (versioningSystem != null) addVersioningFolderListener(name);
        
        for (int i = 0; i < files.length; i++) {
            if (isFilterBackupFiles() && files[i].endsWith(getBackupExtension()) ||
                ignoredGarbageRE != null && ignoredGarbageRE.match (files[i])) {
                
                files[i] = null;
            }
        }
        return files;
    }
    
    boolean areOnlyHiddenFiles(String[] files) {
        ArrayList fileList = new ArrayList(Arrays.asList(files));
        fileList.remove(".nbattrs"); // NOI18N
        fileList.remove("fileSystem.attributes"); // NOI18N
        for (int i = 0; i < fileList.size(); i++) {
            String file = (String) fileList.get(i);
            if (file.endsWith("~")) fileList.remove(i--); // NOI18N
        }
        return fileList.size() == 0;
    }
    
    String[] filterDeadFilesOut(String name, String[] vcsFiles) {
        if (vcsFiles == null) return null;
        FileStatusProvider statusProvider = getStatusProvider();
        if (statusProvider == null) return vcsFiles;
        FileCacheProvider cacheProvider = getCacheProvider();
        // If the folder is not in the cache, do not search it's status
        if (cacheProvider != null && !cacheProvider.isDir(name)) return vcsFiles;
        ArrayList files = new ArrayList(Arrays.asList(vcsFiles));
        int n = files.size();
        for (int i = 0; i < n; i++) {
            String file = (name.length() > 0) ? (name + "/" + (String) files.get(i)) : (String) files.get(i);
            if (cacheProvider != null && !cacheProvider.isFile(file)) continue;
            if (VcsCacheFile.STATUS_DEAD.equals(statusProvider.getFileStatus(file))) {
                files.remove(i--);
                n--;
            }
        }
        return (String[]) files.toArray(new String[0]);
    }
    
    private transient WeakHashMap versioningFolderListeners;
    private static final Object versioningFolderListenersLock = new Object();
    
    private void addVersioningFolderListener(String name) {
        //System.out.println("addVersioningFolderListener("+name+")");
        FileObject fo = findResource(name);
        //System.out.println("  fo = "+fo);
        if (fo != null) {
            synchronized (versioningFolderListenersLock) {
                if (versioningFolderListeners == null) {
                    versioningFolderListeners = new WeakHashMap();
                }
                FileChangeListener listener = (FileChangeListener) versioningFolderListeners.get(fo);
                //System.out.println(" listener for "+fo+" = "+listener);
                if (listener == null) {
                    FileChangeListener chListener = new VersioningFolderChangeListener(name);
                    listener = WeakListener.fileChange(chListener, fo);
                    fo.addFileChangeListener(listener);
                    versioningFolderListeners.put(fo, chListener);
                }
            }
        }
    }
    
    private transient Vector scheduledFilesToBeProcessed;
    
    /**
     * The scheduling action is started for a file.
     * Mark this file as being processed by a scheduling action.
     */
    void addScheduledFileToBeProcessed(String name) {
        if (scheduledFilesToBeProcessed == null) {
            scheduledFilesToBeProcessed = new Vector();
        }
        scheduledFilesToBeProcessed.add(name);
    }
    
    /**
     * The scheduling action is done for a file.
     * Remove this file from the list of files being processed by a scheduling action.
     */
    void removeScheduledFileToBeProcessed(String name) {
        if (scheduledFilesToBeProcessed == null) {
            scheduledFilesToBeProcessed = new Vector();
        }
        scheduledFilesToBeProcessed.remove(name);
    }
    
    /** test, whether the scheduling action was done */
    private boolean isSchedulingDone(String name) {
        return scheduledFilesToBeProcessed == null || !scheduledFilesToBeProcessed.contains(name);
    }

    /**
     * Filter files, that are scheduled for remove.
     * @param packageName the name of a package the files come from.
     * @param files the original list of files
     * @param removedFiles the map of pairs of removed file names and package names.
     *        This map is filled by these pairs in the method.
     * @return the filtered list of files. All files, that are scheduled for remove
     *         are filtered out.
     */
    private String[] filterScheduledSecondaryFiles(String packageName, String[] files, Map removedFiles) {
        ArrayList filtered = new ArrayList(Arrays.asList(files));
        boolean emptyPackage = (packageName.length() == 0);
        for (int i = 0; i < files.length; i++) {
            String fileName = (emptyPackage) ? files[i] : (packageName + "/" + files[i]);
            Set[] scheduled = (Set[]) attr.readAttribute(fileName, VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
            //System.out.println("filterScheduledSecondaryFiles("+packageName+"): "+ packageName + "/" + files[i]+" scheduled = "+scheduled);
            if (scheduled != null && scheduled[0] != null) {
                LinkedList toRemove = new LinkedList();
                for (Iterator it = scheduled[0].iterator(); it.hasNext(); ) {
                    String secFile = (String) it.next();
                    //System.out.println("secFile = '"+secFile+"'");
                    if (!emptyPackage && secFile.startsWith(packageName) ||
                        emptyPackage && secFile.indexOf('/') < 0) {
                        //System.out.println("removing '"+secFile.substring(packageName.length() + 1)+"'");
                        String nameOnly = (emptyPackage) ? secFile : secFile.substring(packageName.length() + 1);
                        boolean removed = filtered.remove(nameOnly);
                        // if removed is true, the file was in the original list
                        //System.out.println("removed = "+removed+", filtered.contains("+nameOnly+") = "+filtered.contains(nameOnly));
                        if (!removed) {
                            // if the file was not in the original list, delete it's attibutes,
                            // because it's supposed to be deleted in the VCS repository.
                            toRemove.add(secFile);
                            try {
                                attr.writeAttribute(secFile, VcsAttributes.VCS_SCHEDULED_FILE_ATTR, null);
                            } catch (IOException exc) {}
                        } else {
                            // the file was removed from the list and put to the list of removed files
                            removedFiles.put(secFile, fileName);
                        }
                    }
                }
                if (toRemove.size() > 0) {
                    // we have some files, that were not present in the original list
                    // and therefore their scheduling attributes has to be cleaned.
                    scheduled[0].removeAll(toRemove);
                    scheduled = cleanScheduledAttrs(scheduled);
                    try {
                        attr.writeAttribute(fileName, VcsAttributes.VCS_SCHEDULED_FILES_ATTR, scheduled);
                        if (scheduled == null) {
                            attr.writeAttribute(fileName, VcsAttributes.VCS_SCHEDULING_MASTER_FILE_NAME_ATTR, null);
                        }
                    } catch (IOException exc) {}
                }
            }
        }
        return (String[]) filtered.toArray(new String[0]);
    }
    
    /**
     * Remove scheduling attributes for files scheduled for ADD, but not present any more.
     * @param packageName the name of the package
     * @param files the array of all files in this package
     */
    private void cleanupNonExistingAddedFiles(String packageName, String[] files) {
        ArrayList filtered = new ArrayList(Arrays.asList(files));
        boolean emptyPackage = (packageName.length() == 0);
        HashMap addedFilesNotPresentAnyMore = new HashMap();
        for (int i = 0; i < files.length; i++) {
            String fileName = (emptyPackage) ? files[i] : (packageName + "/" + files[i]);
            Set[] scheduled = (Set[]) attr.readAttribute(fileName, VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
            if (scheduled != null && scheduled[1] != null) {
                for (Iterator it = scheduled[1].iterator(); it.hasNext(); ) {
                    String secFile = (String) it.next();
                    if (!emptyPackage && secFile.startsWith(packageName) ||
                        emptyPackage && secFile.indexOf('/') < 0) {
                        //System.out.println("removing '"+secFile.substring(packageName.length() + 1)+"'");
                        String nameOnly = (emptyPackage) ? secFile : secFile.substring(packageName.length() + 1);
                        if (!filtered.contains(nameOnly)) {
                            addedFilesNotPresentAnyMore.put(secFile, fileName);
                        }
                    }
                }
            }
        }
        if (addedFilesNotPresentAnyMore.size() > 0) {
            for (Iterator secIt = addedFilesNotPresentAnyMore.keySet().iterator(); secIt.hasNext(); ) {
                String secFile = (String) secIt.next();
                String primaryFile = (String) addedFilesNotPresentAnyMore.get(secFile);
                removeScheduledFromPrimary(secFile, primaryFile, 1);
            }
        }
    }
    
    /**
     * Clean the array of sets of scheduled file names.
     * If any set in this array becomes empty, substitute it by <code>null</code>,
     * if all items in the array are <code>null</code>, return <code>null</code>.
     * @param scheduled the array of sets of scheduled file names.
     * @return the array cleaned as much as possible
     */
    private static final Set[] cleanScheduledAttrs(Set[] scheduled) {
        //System.out.print("cleanScheduledAttrs("+scheduled+") = ");
        boolean canClean = true;
        for (int k = 0; k < scheduled.length; k++) {
            if (scheduled[k] != null && scheduled[k].size() == 0) scheduled[k] = null;
            if (scheduled[k] != null) {
                canClean = false;
                break;
            }
        }
        if (canClean) scheduled = null;
        //System.out.println(scheduled);
        return scheduled;
    }

    //-------------------------------------------
    //
    // Change
    //
    
    /**
     * Should be called when the modification in a file or folder is expected
     * and its content should be refreshed.
     */
    public void checkForModifications(String path) {
        //System.out.println("checkForModifications("+path+")");
        Enumeration enum = existingFileObjects(this.findResource(path));
        while(enum.hasMoreElements()) {
            FileObject fo = (FileObject) enum.nextElement();
            String name = fo.getPackageNameExt('/', '.');
            //System.out.println("refreshResource("+name+")");
            refreshResource(name, true);
        }
    }

    /* Creates new folder named name.
     * @param name name of folder
     * @throws IOException if operation fails
     */
    public void createFolder (String name) throws java.io.IOException {
        D.deb("createFolder('"+name+"')"); // NOI18N
        if( name.startsWith("/") ){ // NOI18N
            // Jarda TODO
            name=name.substring(1);
            D.deb("corrected name='"+name+"'"); // NOI18N
        }

        File f = getFile (name);
        Object[] errorParams = new Object[] {
                                   f.getName (),
                                   getDisplayName (),
                                   f.toString ()
                               };

        if (name.equals ("")) { // NOI18N
            throw new IOException(MessageFormat.format (g("EXC_CannotCreateF"), errorParams)); // NOI18N
        }

        if (f.exists()) {
            throw new IOException(MessageFormat.format (g("EXC_FolderAlreadyExist"), errorParams)); // NOI18N
        }

        int lastSeparator = name.lastIndexOf ("/"); // NOI18N
        if (lastSeparator > 0) {
            File folder = getFile(name.substring (0, lastSeparator));
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new IOException(MessageFormat.format (g("EXC_CannotCreateF"),
                        new Object[] { folder.getName(), getDisplayName(), folder.toString() } )); // NOI18N
                }
            }
        }

        boolean b = f.mkdir();
        if (!b) {
            throw new IOException(MessageFormat.format (g("EXC_CannotCreateF"), errorParams)); // NOI18N
        }
        if (cache != null) cache.addFolder(name);
    }

    /** Create new data file.
     * @param name name of the file
     * @return the new data file object
     * @exception IOException if the file cannot be created (e.g. already exists)
     */
    public void createData (String name) throws IOException {
        D.deb("createData("+name+")"); // NOI18N
        if( name.startsWith("/") ){ // NOI18N
            // Jarda TODO
            name=name.substring(1);
            D.deb("corrected name='"+name+"'"); // NOI18N
        }

        File f = getFile (name);

        int lastSeparator = name.lastIndexOf ("/"); // NOI18N
        if (lastSeparator > 0) {
            File folder = getFile(name.substring (0, lastSeparator));
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new IOException(MessageFormat.format (g("EXC_CannotCreateF"),
                        new Object[] { folder.getName(), getDisplayName(), folder.toString() } )); // NOI18N
                }
            }
        }

        if (!f.createNewFile ()) {
            throw new IOException(MessageFormat.format (g("EXC_DataAlreadyExist"),
                new Object[] { f.getName (), getDisplayName (), f.toString () } )); // NOI18N
        }
        /* we do not store local files to cache.
        if (cache != null) {
            cache.addFile(name);
        }
         */
        if (statusProvider != null) {
            statusProvider.setFileStatus(name, statusProvider.getLocalFileStatus());
        }
    }

    private void addParentToRefresher(String name) {
        //System.out.println("addParentToRefresher("+name+")");
        int lastIndex = name.lastIndexOf('/');
        String parent;
        if (lastIndex > 0) {
            parent = name.substring(0, lastIndex);
        } else {
            parent = "";
        }
        if (refresher != null) {
            //System.out.println("addPrefferedFolder("+parent+")");
            refresher.addPrefferedFolder(parent);
        }
    }
    
    /** Rename a file.
     * @param oldName old name of the file
     * @param newName new name of the file
     */
    public void rename(String oldName, String newName) throws IOException {
        D.deb("rename(oldName="+oldName+",newName="+newName+")"); // NOI18N
        File of = getFile (oldName);
        File nf = getFile (newName);

        // #7086 - (nf.exists() && !nf.equals(of)) instead of nf.exists() - fix for Win32
        if ((nf.exists() && !nf.equals(of)) || !of.renameTo (nf)) {
            final String msg = g("EXC_CannotRename", oldName, getDisplayName (), newName); // NOI18N
            throw new IOException(msg) {
                public String getLocalizedMessage() {
                    return msg;
                }
            };
        }
        if (cache != null) cache.rename(oldName, newName);
        addParentToRefresher(oldName);
    }

    /** Delete a file.
     * @param name name of the file
     * @exception IOException if the file could not be deleted
     */
    public void delete (final String name) throws IOException {
        D.deb("delete('"+name+"')"); // NOI18N
        final File file = getFile (name);
        if (!deleteFile(file, name)) {
            throw new IOException () {
                public String getLocalizedMessage () {
                    return g("EXC_CannotDelete", name, getDisplayName (), file.toString ()); // NOI18N
                }
            };
        }
    }
    
    protected boolean deleteFile(final File file, String name) throws IOException {
        if (!file.exists()) return true; // non existing file is successfully deleted
        if (!file.canWrite()/* || !file.canRead()*/) {
            throw new IOException() {
                /** Localized message. */
                public String getLocalizedMessage () {
                    return g("EXC_CannotDeleteReadOnly", file.toString());
                }
            };
        }
        boolean wasDir = file.isDirectory();
        if (wasDir) {
            // first of all delete whole content
            File[] arr = file.listFiles();
            if (arr != null) {
                for (int i = 0; i < arr.length; i++) {
                    if (!deleteFile (arr[i], name + "/" + arr[i].getName())) {
                        return false;
                    }
                }
            }
        }
        boolean success = file.delete();
        //if (cache != null) cache.remove(name, wasDir);
        // This delete deletes ONLY local files, to delete a file from the repository also
        // (if it's really desired to do it here), use the delete command with it's own refreshing
        // The files are removed from the cache if the missing status is not set
        // (thus the new status would need to be retrieved later)
        if (cache != null) {
            if (!wasDir) {
                if (missingFileStatus == null) cache.remove(name, wasDir);
            } else {
                if (missingFolderStatus == null) cache.remove(name, wasDir);
            }
        }
        //if (cache != null && wasDir) cache.remove(name, wasDir);
        /*
        if (statusProvider != null) {
            statusProvider.setFileMissing(name);
        }
         */
        addParentToRefresher(name);
        callDeleteCommand(name, wasDir);
        return success;
    }
    
    /**
     * When a file or folder was deleted, a command DELETE_FILE
     * or DELETE_DIR is called. Subclasses can do their own actions here.
     */
    protected void callDeleteCommand(String name, boolean isDir) {
        VcsCommand cmd;
        if (isDir) {
            cmd = getCommand(VcsCommand.NAME_DELETE_DIR);
        } else {
            cmd = getCommand(VcsCommand.NAME_DELETE_FILE);
        }
        if (cmd != null) {
            if (VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES)) {
                addDeleteCommand(name, isDir);
            } else {
                Table files = new Table();
                files.put(name, findResource(name));
                VcsAction.doCommand(files, cmd, null, this);
            }
        }
    }

    private transient ArrayList deleteFileCommandQueue = new ArrayList();
    private transient ArrayList deleteFolderCommandQueue = new ArrayList();
    private transient Thread deleteCommandThread = null;
    
    private void addDeleteCommand(String name, boolean isDir) {
        synchronized (deleteFileCommandQueue) {
            if (isDir) {
                deleteFolderCommandQueue.add(name);
            } else {
                deleteFileCommandQueue.add(name);
            }
            deleteFileCommandQueue.notifyAll();
            if (deleteCommandThread == null || !deleteCommandThread.isAlive()) {
                deleteCommandThread = createDeleteCommandThread();
                deleteCommandThread.start();
            }
        }
    }
    
    private Thread createDeleteCommandThread() {
        return new Thread(new Runnable() {
            public void run() {
                do {
                    boolean changed = true;
                    int n = deleteFolderCommandQueue.size() + deleteFileCommandQueue.size();
                    while (changed) {
                        synchronized (deleteFileCommandQueue) {
                            try {
                                deleteFileCommandQueue.wait(1000);
                            } catch (InterruptedException exc) {}
                        }
                        int n1 = deleteFolderCommandQueue.size() + deleteFileCommandQueue.size();
                        changed = n != n1;
                        n = n1;
                    }
                    ArrayList fileCommand = new ArrayList();
                    ArrayList folderCommand = new ArrayList();
                    synchronized (deleteFileCommandQueue) {
                        fileCommand.addAll(deleteFileCommandQueue);
                        deleteFileCommandQueue.clear();
                        folderCommand.addAll(deleteFolderCommandQueue);
                        deleteFolderCommandQueue.clear();
                    }
                    if (fileCommand.size() > 0) {
                        runDeleteFilesCommand(fileCommand, getCommand(VcsCommand.NAME_DELETE_FILE));
                    }
                    if (folderCommand.size() > 0) {
                        runDeleteFilesCommand(folderCommand, getCommand(VcsCommand.NAME_DELETE_DIR));
                    }
                    synchronized (deleteFileCommandQueue) {
                        try {
                            deleteFileCommandQueue.wait(1000);
                        } catch (InterruptedException exc) {}
                    }
                } while (deleteFolderCommandQueue.size() + deleteFileCommandQueue.size() > 0);
            }
        }, "VCS Delete file/dir command");
    }
    
    private void runDeleteFilesCommand(java.util.List filesList, VcsCommand cmd) {
        Table files = new Table();
        for (Iterator it = filesList.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            files.put(name, findResource(name));
        }
        VcsAction.doCommand(files, cmd, null, this);
    }
    
    //-------------------------------------------
    //
    // Info
    //

    /**
     * Get last modification time.
     * @param name the file to test
     * @return the date
     */
    public java.util.Date lastModified(String name) {
        D.deb("lastModified("+name+")"); // NOI18N
        File file = getFile(name);
        if (!file.exists()) {
            if (cache != null) {
                CacheFile cFile = cache.getDir(name);
                if (cFile == null) cFile = cache.getFile(name);
                if (cFile != null) {
                    String date = cFile.getDate();
                    String time = cFile.getTime();
                    if (date != null && date.length() > 0
                        && time != null && time.length() > 0) {
                            Date pdate = null;
                            try {
                                pdate = java.text.DateFormat.getInstance().parse(date + " " + time);
                            } catch (java.text.ParseException pexc) {}
                            if (pdate != null) return pdate;
                    }
                }
            }
            return new Date(System.currentTimeMillis());
        } else {
            return new Date (file.lastModified ());
        }
        //return new java.util.Date (getFile (name).lastModified ());
    }

    /** Test if the file is folder or contains data.
     * @param name name of the file
     * @return true if the file is folder, false otherwise
     */
    public boolean folder (String name) {
        boolean isFolder;
        if (cache != null) {
            isFolder = cache.isDir(name);
            if (!isFolder && !cache.isFile(name)) {
                isFolder = getFile(name).isDirectory();
            }
        } else {
            isFolder = getFile(name).isDirectory();
        }
        //D.deb("folder('"+name+"') = "+isFolder);
        return isFolder;
        // return getFile (name).isDirectory ();
    }

    /** Test whether this file can be written to or not.
     * All folders are not read only, they are created before writting into them.
     * @param name the file to test
     * @return <CODE>true</CODE> if file is read-only
     */
    public boolean readOnly (String name) {
        //D.deb("readOnly('"+name+"')"); // NOI18N
        D.deb("readOnly("+name+") return "+(!getFile (name).canWrite ()));
        if(folder(name)) return false;
        return !getFile (name).canWrite ();
    }

    /** Get the MIME type of the file.
     * Uses {@link FileUtil#getMIMEType}.
     *
     * @param name the file to test
     * @return the MIME type textual representation, e.g. <code>"text/plain"</code>
     */
    public String mimeType (String name) {
        D.deb("mimeType('"+name+"')"); // NOI18N
        FileObject fo = findResource(name);
        String mimeType = (fo != null) ? FileUtil.getMIMEType(fo) : "content/unknown"; // NOI18N
        return mimeType;
    }

    /** Get the size of a file.
     * @param name the file to test
     * @return the size of the file in bytes or zero if the file does not contain data (does not
     *  exist or is a folder).
     */
    public long size (String name) {
        D.deb("size("+name+")"); // NOI18N
        return getFile (name).length ();
    }

    /** Get input stream to a file.
     * @param name the file to test
     * @return an input stream to read the contents of this file
     * @exception FileNotFoundException if the file does not exists or is invalid
     */
    public InputStream inputStream (String name) throws java.io.FileNotFoundException {
        //D.deb("inputStream("+name+")"); // NOI18N
        InputStream in = null;
        try {
            in = new FileInputStream (getFile (name));
        } catch (java.io.FileNotFoundException exc) {
            final String fname = name;
            throw (java.io.FileNotFoundException) TopManager.getDefault().getErrorManager().annotate(
                new java.io.FileNotFoundException() {
                    public String getLocalizedMessage() {
                        return g("MSG_FileNotExist", fname);
                    }
                }, g("MSG_FileNotExist", fname));
        }
        return in;
    }

    private static final Object GROUP_LOCK = new Object();
    private void fileChanged(final String name) {
        D.deb("fileChanged("+name+")");
        if (statusProvider != null) {
            // Fire the change asynchronously to prevent deadlocks.
            org.openide.util.RequestProcessor.postRequest(new Runnable() {
                public void run() {
                    String oldStatus = statusProvider.getFileStatus(name);
                    if (!notModifiableStatuses.contains(oldStatus)) {
                        statusProvider.setFileModified(name);
                    }
                    VcsGroupSettings grSettings = (VcsGroupSettings) SharedClassObject.findObject(VcsGroupSettings.class, true);
                    if (!grSettings.isDisableGroups()) {
                        if (grSettings.getAutoAddition() == VcsGroupSettings.ADDITION_TO_DEFAULT
                        || grSettings.getAutoAddition() == VcsGroupSettings.ADDITION_ASK) {
                            
                            FileObject fo = findResource(name);
                            if (fo != null) {
                                try {
                                    DataObject dobj = DataObject.find(fo);
                                    synchronized (GROUP_LOCK) {
                                        DataShadow shadow = GroupUtils.findDOInGroups(dobj);
                                        if (shadow == null) {
                                            // it doesn't exist in groups, add it..
                                            if (grSettings.getAutoAddition() == VcsGroupSettings.ADDITION_ASK) {
                                                AddToGroupDialog.openChooseDialog(dobj);
                                            } else {
                                                GroupUtils.addToDefaultGroup(new Node[] {dobj.getNodeDelegate()});
                                            }
                                        }
                                    }
                                } catch (DataObjectNotFoundException exc) {
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class FileOutputStreamPlus extends FileOutputStream {
        private String name;
        public FileOutputStreamPlus(File file, String name) throws IOException {
            super(file);
            this.name = name;
        }

        public void close() throws IOException {
            super.close();
            if (name != null) VcsFileSystem.this.fileChanged(name);
        }
    }
    
    protected String getBackupExtension() {
        return "~"; // NOI18N
    }
    
    private boolean isIDESettingsFile(String name) {
        name = name.replace(java.io.File.separatorChar, '/');
        return name.equals(".nbattrs") ||               // NOI18N
               name.endsWith("/.nbattrs") ||            // NOI18N
               name.equals("filesystem.attributes") ||  // NOI18N
               name.endsWith("/filesystem.attributes"); // NOI18N
    }
    
    protected void createBackupFile(String name) throws java.io.IOException {
        if (isIDESettingsFile(name)) return ;
        if (!isImportant(name) || name.endsWith(getBackupExtension())) return ;
        if (!getFile(name).exists()) return ;
        InputStream in = inputStream(name);
        try {
            OutputStream out = outputStream (name + getBackupExtension());
            try {
                FileUtil.copy(in, out);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    /** Get output stream to a file.
     * @param name the file to test
     * @return output stream to overwrite the contents of this file
     * @exception IOException if an error occures (the file is invalid, etc.)
     */
    public OutputStream outputStream (String name) throws java.io.IOException {
        D.deb("outputStream("+name+")"); // NOI18N
        if (isCreateBackupFiles()) {
            createBackupFile(name);
        }
        FileOutputStream output = new FileOutputStreamPlus (getFile (name), name);
        return output;
    }

    /*
    public synchronized boolean getPromptForLockResult() {
        return promptForLockResult;
    }

    public synchronized void setPromptForLockResult(boolean promptForLockResult) {
        this.promptForLockResult = promptForLockResult;
    }
     */
    
    /**
     * Whether the LOCK command should be performed for this file. This method should
     * check whether the file is already locked. The default implementation
     * search for the locker status and compare with user.name property.
     * @return true if the file is not locked yet and lock command should run.
     */
    protected boolean shouldLock(String name) {
        /*
        if (getCommand(VcsCommand.NAME_SHOULD_DO_LOCK) != null) {
            Table files = new Table();
            files.put(name, findResource(name));
            return VcsAction.shouldDoLock (files, VcsFileSystem.this);
        } else */
        if (getCommand(VcsCommand.NAME_LOCK) == null) return false; // The LOCK command is not defined
        if (statusProvider != null) {
            String locker = statusProvider.getFileLocker(name);
            if (locker != null && locker.equals(System.getProperty("user.name"))) {
                return false;
            }
        }
        return true;
    }

    /** Run the LOCK command to lock the file.
     *
     * @param name name of the file
     */
    public void lock (String name_) throws IOException {
        //System.out.println("lock("+name_+")");
        if (!isImportant(name_)) return; // ignore locking of unimportant files
        final String name = name_;
        //final VcsFileSystem current = this;
        final File file = getFile (name);
        if (!file.exists()) return; // Ignore the lock when the file does not exist.
        if (isReadOnly()) { // I'm on a read-only filesystem => can not lock
            throw new IOException ("Cannot Lock "+name); // NOI18N
        }
        if (isCallEditFilesOn()) {
            if (!file.canWrite ()) {
                VcsCacheFile vcsFile = (cache != null) ? ((VcsCacheFile) cache.getFile (name)) : null;
                if (vcsFile != null && !vcsFile.isLocal () && !name.endsWith (".orig")) { // NOI18N
                    if (isPromptForEditOn()) {
                        VcsConfigVariable msgVar = (VcsConfigVariable) variablesByName.get(Variables.MSG_PROMPT_FOR_AUTO_EDIT);
                        String message;
                        if (msgVar != null && msgVar.getValue().length() > 0) message = msgVar.getValue();
                        else message = g("MSG_EditFileCh");
                        throw (UserQuestionException) TopManager.getDefault().getErrorManager().annotate(
                            new UserQuestionException(message) {
                                public void confirmed() {
                                    Table files = new Table();
                                    files.put(name, findResource(name));
                                    VcsAction.doEdit (files, VcsFileSystem.this);
                                }
                            }, g("EXC_CannotDeleteReadOnly", file.toString()));
                    } else {
                        Table files = new Table();
                        files.put(name, findResource(name));
                        VcsAction.doEdit (files, VcsFileSystem.this);
                    }
                }
            }
        }
        if (isLockFilesOn()) {
            VcsCacheFile vcsFile = (cache != null) ? ((VcsCacheFile) cache.getFile (name)) : null;
            // *.orig is a temporary file created by AbstractFileObject
            // on saving every file to enable undo if saving fails
            if (vcsFile==null || vcsFile.isLocal () || name.endsWith (".orig")) return; // NOI18N
            else if (shouldLock(name)) {
                if (isPromptForLockOn ()) {
                    VcsConfigVariable msgVar = (VcsConfigVariable) variablesByName.get(Variables.MSG_PROMPT_FOR_AUTO_LOCK);
                    String message;
                    if (msgVar != null && msgVar.getValue().length() > 0) message = msgVar.getValue();
                    else message = g("MSG_LockFileCh");
                    throw new UserQuestionException(message) {
                        public void confirmed() {
                            Table files = new Table();
                            files.put(name, findResource(name));
                            VcsAction.doLock (files, VcsFileSystem.this);
                        }
                    };
                } else {
                    Table files = new Table();
                    files.put(name, findResource(name));
                    VcsAction.doLock (files, VcsFileSystem.this);
                }
            }
        }
        if (!file.canWrite () && file.exists()) {
            throw new IOException() {
                /** Localized message. */
                public String getLocalizedMessage () {
                    return g("EXC_CannotLockReadOnly", file.toString());
                }
            };
        }
    }

    /** Call the UNLOCK command to unlock the file.
     *
     * @param name name of the file
     */
    public void unlock (String name) {
        //System.out.println("unlock("+name+")");
        if (!isImportant(name)) return; // ignore unlocking of unimportant files
        D.deb("unlock('"+name+"')"); // NOI18N
        if(isLockFilesOn ()) {
            VcsCacheFile vcsFile = (cache != null) ? ((VcsCacheFile) cache.getFile (name)) : null;
            if (vcsFile != null && !vcsFile.isLocal () && !name.endsWith (".orig")) { // NOI18N
                Table files = new Table();
                files.put(name, findResource(name));
                VcsAction.doUnlock (files, this);
            }
        }
    }

    //-------------------------------------------
    /** Does nothing to mark the file as unimportant.
     *
     * @param name the file to mark
     *
    public void markUnimportant (String name) {
      // TODO...
        D.deb(" ==== markUnimportant("+name+") ==== "); // NOI18N
            VcsFile file=cache.getFile(name);
            if( file==null ){
              //E.err("no such file '"+name+"'"); // NOI18N
              return ;
            }
            file.setImportant(false);
}
    */
    
//-------------------- methods from CacheHandlerListener------------------------
    public void cacheAdded(CacheHandlerEvent event) {
//        D.deb("cacheAdded called for:" + event.getCvsCacheFile().getName());
        String root = getRootDirectory().getAbsolutePath();
        String absPath = event.getCacheFile().getAbsolutePath();
        if (absPath.startsWith(root)) { // it belongs to this FS -> do something
            //D.deb("-------- it is in this filesystem");
            String path;
            if (root.length() == absPath.length()) {
                path = "";
            } else {
                path = absPath.substring(root.length() + 1, absPath.length());
                /*
                if (path.charAt(0) == File.separatorChar) { //another sanity check.
                    path = path.substring(1);
                }
                 */
            }
//            D.deb("statusChanged() absPath =" + absPath);
//            D.deb("statusChanged() root =" + root);
//            D.deb("statusChanged() path =" + path);
            path = path.replace(File.separatorChar, '/');
            statusChanged(path);
        }
    }
    
    public void cacheRemoved(CacheHandlerEvent event) {
//        D.deb("cacheRemoved called for:" + event.getCvsCacheFile().getName());
        String root = getRootDirectory().getAbsolutePath();
        CacheFile removedFile = event.getCacheFile();
        String absPath = removedFile.getAbsolutePath();
        if (absPath.startsWith(root)) { // it belongs to this FS -> do something
            String path;
            if (root.length() == absPath.length()) {
                path = "";
            } else {
                path = absPath.substring(root.length() + 1, absPath.length());
            }
            path = path.replace(File.separatorChar, '/');
            if (removedFile instanceof CacheDir) {
                refreshExistingFolders(path);
                statusChanged(path, true);
            } else {
                statusChanged(path);
            }
        }
    }
    
    public void statusChanged(CacheHandlerEvent event) {
        //D.deb("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        //D.deb("statusChanged called for:" + event.getCacheFile().getAbsolutePath());
        //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        //System.out.println("statusChanged called for:" + event.getCacheFile().getAbsolutePath());
        String root = getRootDirectory().getAbsolutePath();
        String absPath = event.getCacheFile().getAbsolutePath();
        if (absPath.startsWith(root)) { // it belongs to this FS -> do something
            //D.deb("-------- it is in this filesystem");
            String path;
            if (root.length() == absPath.length()) {
                path = "";
            } else {
                path = absPath.substring(root.length() + 1, absPath.length());
                /*
                if (path.charAt(0) == File.separatorChar) { //another sanity check.
                    path = path.substring(1);
                }
                 */
            }
//            D.deb("statusChanged() absPath =" + absPath);
//            D.deb("statusChanged() root =" + root);
//            D.deb("statusChanged() path =" + path);
            path = path.replace(File.separatorChar, '/');
            if (event.getCacheFile() instanceof org.netbeans.modules.vcscore.cache.CacheDir) {
                statusChanged(path, event.isRecursive());
            } else {
                statusChanged(path);
            }
            /*
            FileObject fo = findResource(path);
            if (fo == null) {
                E.err("statusChanged().. could not find FileObject.. name=" + path);
                return;
            }
            fireFileStatusChanged (new FileStatusEvent(this, fo, false, true));
             */
        }
    }

    private void addCommandsToHashTable(Node root) {
        Children children = root.getChildren();
        for (Enumeration subnodes = children.nodes(); subnodes.hasMoreElements(); ) {
            Node child = (Node) subnodes.nextElement();
            VcsCommand cmd = (VcsCommand) child.getCookie(VcsCommand.class);
            if (cmd == null) continue;
            commandsByName.put(cmd.getName(), cmd);
            if (!child.isLeaf()) addCommandsToHashTable(child);
        }
    }
    
    /**
     * For compatibility reasons only.
     * This method is to be used when deserialization from NetBeans 3.1 and older is performed.
     */
    private void setAdvancedConfig (Object advanced) {
        //super.setAdvancedConfig (advanced);
        this.advanced = advanced;
        Vector commands = (Vector) advanced;
        int len = commands.size();
        if (len == 0) return ;
        commandsByName = new Hashtable(len * 4 / 3 + 1, 0.75f);
        //mainCommands = new Vector();
        //revisionCommands = new Vector();
        org.netbeans.modules.vcscore.cmdline.UserCommand root =
            new org.netbeans.modules.vcscore.cmdline.UserCommand();
        root.setName("ROOT");
        root.setDisplayName("VCS");
        Children rootCh = new Children.Array();
        commandsRoot = new VcsCommandNode(rootCh, root);
        VcsCommand cmds = (VcsCommand) commands.elementAt(0);
        Children mainCh = new Children.Array();
        rootCh.add(new Node[] { new VcsCommandNode(mainCh, cmds) });
        for(int i = 1; i < len; i++) {
            VcsCommand uc = (VcsCommand) commands.elementAt(i);
            commandsByName.put(uc.getName(), uc);
            mainCh.add(new Node[] { new VcsCommandNode(Children.LEAF, uc) });
            //mainCommands.add(uc);
        }
    }

    /** Set the tree structure of commands.
     * @param root the tree of {@link VcsCommandNode} objects.
     */
    public void setCommands(Node root) {
        //System.out.println("setCommands()");
        Object old = commandsRoot;
        if (root == null) {
            if (advanced != null) {
                setAdvancedConfig(advanced);
            }
        } else {
            if (commandsByName != null) {
                removeCmdActionsFromSupporter();
            }
            commandsRoot = root;
            commandsByName = new Hashtable();
            addCommandsToHashTable(root);
            addCmdActionsToSupporter();
        }
        VariableInputDescriptorCompat.createInputDescriptorFormExec(commandsByName);
        firePropertyChange(PROP_COMMANDS, old, commandsRoot);
    }

    /** Get the commands.
     * @return the root command
     */
    public Node getCommands() {
        return commandsRoot;
    }

    /** Get a command by its name.
     * @param name the name of the command to get
     * @return the command of the given name or <code>null</code>,
     *         when the command is not defined
     */
    public VcsCommand getCommand(String name){
        if (commandsByName == null) {
            Node commands = getCommands();
            if (commands == null) return null;
            setCommands (commands);
        }
        return (VcsCommand) commandsByName.get(name);
    }
    
    private void addCmdActionsToSupporter() {
        for (Iterator it = commandsByName.values().iterator(); it.hasNext(); ) {
            VcsCommand cmd = (VcsCommand) it.next();
            Class actionClass = (Class) cmd.getProperty(VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "generalCommandActionClass"); // NOI18N
            if (actionClass == null) {
                Object actionClassNameObj = cmd.getProperty(VcsCommand.PROPERTY_GENERAL_COMMAND_ACTION_CLASS_NAME);
                if (actionClassNameObj instanceof String) {
                    String actionClassName = (String) actionClassNameObj;
                    try {
                        actionClass = Class.forName(actionClassName, false,
                                                    TopManager.getDefault().currentClassLoader());
                    } catch (ClassNotFoundException e) {
                        TopManager.getDefault().notifyException(
                            TopManager.getDefault().getErrorManager().annotate(e, g("EXC_CouldNotFindAction", actionClassName)));
                        continue;
                    }
                }
                cmd.setProperty(VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "generalCommandActionClass", actionClass); // NOI18N
            }
            actionSupporter.addSupportForAction(actionClass, cmd.getName());
        }
    }
    
    private void removeCmdActionsFromSupporter() {
        for (Iterator it = commandsByName.values().iterator(); it.hasNext(); ) {
            VcsCommand cmd = (VcsCommand) it.next();
            Class actionClass = (Class) cmd.getProperty(VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "generalCommandActionClass"); // NOI18N
            if (actionClass != null) {
                actionSupporter.removeSupportForAction(actionClass);
            }
        }
    }

    public FilenameFilter getLocalFileFilter() {
        return null;
    }
    
    /**
     * This method is called from AbstractFileObject.isVirtual. Tests if file
     * really exists or is missing. Some operation on it may be restricted if returns true.
     * @param name of the file
     * @return  true indicates that the file is missing.
     */
    protected boolean checkVirtual(String name) {
        File file = getFile(name);
        return !file.exists();
    }
    
    /**
     * Perform the check of whether the file is or is not still virtual. This
     * method is called on every file status change with the set of potentially
     * changed files.
     * This method does nothing, subclasses may override it with some meaningfull
     * action (e.g. call setVirtualDataLoader() and invalidate the current
     * DataObject if the setVirtualDataLoader() returns true).
     * @param the set of FileObjects whose status was changed
     */
    protected void checkVirtualFiles(Set foSet) {
    }
    
    /**
     * This method assigns/unassigns special virtual data object to files,
     * that are virtual.
     * Subclasses should call this method on every change of the file "virtual"
     * property.
     * @param fo the FileObject
     * @return whether the data object loader was changed for this file
     */
    protected boolean setVirtualDataLoader(FileObject fo) {
        boolean reload = false;
        try {
            if (checkVirtual(fo.getPackageNameExt('/', '.'))) {
                if (statusProvider != null) {
                    String stat = statusProvider.getFileStatus(fo.getPackageNameExt('/', '.'));
                    if (statusProvider.getLocalFileStatus().equals(stat)) {
                        return reload;
                    }
                }
                DataLoader loader = DataLoaderPool.getPreferredLoader(fo);
                if (loader == null || !loader.getClass().equals(VirtualsDataLoader.class)) {
                    DataLoaderPool.setPreferredLoader(fo,
                        (VirtualsDataLoader) org.openide.util.SharedClassObject.findObject(VirtualsDataLoader.class, true));
                    reload = true;
                    //System.out.println("to vitrual..");
                }
            } else {
                DataLoader loader = DataLoaderPool.getPreferredLoader(fo);
                if (loader != null && loader.getClass().equals(VirtualsDataLoader.class)) {
                    //System.out.println("resetitting loader");
                    DataLoaderPool.setPreferredLoader(fo, null);
                    reload = true;
                }
            }
        } catch (java.io.IOException exc) {}
        return reload;
    }
    
    private void settingsChanged(String propName, Object oldVal, Object newVal) {
        GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        if (GeneralVcsSettings.PROP_USE_GLOBAL.equals(propName)) {
            if (((Boolean) newVal).booleanValue() == true) {
                setOffLine(settings.isOffLine());
                setAutoRefresh(settings.getAutoRefresh());
                setHideShadowFiles(settings.isHideShadowFiles());
            }
        } else {
            if (settings.isUseGlobal()) {
                if (GeneralVcsSettings.PROP_OFFLINE.equals(propName)) {
                    setOffLine(settings.isOffLine());
                } else if (GeneralVcsSettings.PROP_AUTO_REFRESH.equals(propName)) {
                    setAutoRefresh(settings.getAutoRefresh());
                } else if (GeneralVcsSettings.PROP_HOME.equals(propName)) {
                    updateEnvironmentVars();
                } else if (GeneralVcsSettings.PROP_HIDE_SHADOW_FILES.equals(propName)) {
                    setHideShadowFiles(settings.isHideShadowFiles());
                }
            }
        }
    }
    
    private class FSPropertyChangeListener implements PropertyChangeListener {
        
        private String oldFsSystemName;
        
        public FSPropertyChangeListener() {
            oldFsSystemName = VcsFileSystem.this.getSystemName();
        }
        
        public void propertyChange(final PropertyChangeEvent event) {
            String propName = event.getPropertyName();
            Object oldValue = event.getOldValue();
            Object newValue = event.getNewValue();
            if (PROP_ANNOTATION_PATTERN.equals(propName)) {
                FileObject root = findResource("");
                Set foSet = new HashSet();
                Enumeration enum = existingFileObjects(root);
                while (enum.hasMoreElements()) {
                    foSet.add(enum.nextElement());
                }
                fireFileStatusChanged(new FileStatusEvent(VcsFileSystem.this, foSet, false, true));
            } else if (VcsFileSystem.PROP_SYSTEM_NAME.equals(event.getPropertyName())) {
                RuntimeSupport.getInstance().updateRuntime(VcsFileSystem.this, oldFsSystemName);
                oldFsSystemName = VcsFileSystem.this.getSystemName();
            }
        }
    }
    
    private class SettingsPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent event) {
            settingsChanged(event.getPropertyName(), event.getOldValue(), event.getNewValue());
        }
    }
    
    private class VcsList implements AbstractFileSystem.List {
        
        private static final long serialVersionUID = 9164232967348550668L;
        
        public String[] children(String name) {
            return list.children(name);
        }
        
    }

    private class VersioningFolderChangeListener extends Object implements FileChangeListener {
        
        /** the folder name */
        private String name;
        
        public VersioningFolderChangeListener(String name) {
            this.name = name;
        }
        
        /** Fired when a file attribute is changed. */
        public void fileAttributeChanged(org.openide.filesystems.FileAttributeEvent fe) {
        }

        /** Fired when a file is changed. */
        public void fileChanged(org.openide.filesystems.FileEvent fe) {
            refreshVersioning();
        }

        /** Fired when a new file is created. */
        public void fileDataCreated(org.openide.filesystems.FileEvent fe) {
            refreshVersioning();
        }

        /** Fired when a file is deleted. */
        public void fileDeleted(org.openide.filesystems.FileEvent fe) {
            refreshVersioning();
        }

        /** Fired when a new folder is created. */
        public void fileFolderCreated(org.openide.filesystems.FileEvent fe) {
            refreshVersioning();
        }

        /** Fired when a file is renamed. */
        public void fileRenamed(org.openide.filesystems.FileRenameEvent fe) {
            refreshVersioning();
        }
        
        private void refreshVersioning() {
            getStatusChangeRequestProcessor().post(new Runnable() {
                public void run() {
                    //System.out.println("refreshVersioning("+name+")");
                    if (versioningSystem != null) {
                        FileObject fo = versioningSystem.findExistingResource(name);
                        //System.out.println("  resource = "+fo);
                        if (fo != null) fo.refresh();
                    }
                }
            });
        }
    }
    
    public String getBundleProperty(String s) {
        return g(s);
    }

    public String getBundleProperty(String s, Object obj) {
        return g(s, obj);
    }
    
    public IgnoreListSupport getIgnoreListSupport () {
        return this.ignoreListSupport;
    }
    
    public void setIgnoreListSupport (IgnoreListSupport support) {
        this.ignoreListSupport = support;
    }

    //-------------------------------------------
    protected String g(String s) {
        D.deb("getting "+s);
        return NbBundle.getMessage(VcsFileSystem.class, s);
    }
    protected String  g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
    protected String g(String s, Object obj1, Object obj2) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
    }
    protected String g(String s, Object obj1, Object obj2, Object obj3) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
    }
    
    private void D(String debug) {
        //System.out.println("VcsFileSystem(): "+debug);
    }
    //-------------------------------------------
}
