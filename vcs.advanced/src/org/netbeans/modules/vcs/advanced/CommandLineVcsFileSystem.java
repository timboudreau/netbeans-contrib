/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;
import javax.swing.*;

import org.openide.util.actions.*;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystem.Status;
import org.openide.filesystems.FileSystemCapability;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.DefaultAttributes;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.nodes.Children;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.cache.CacheReference;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandsPool;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandNode;
import org.netbeans.modules.vcscore.util.*;

import org.netbeans.modules.vcs.advanced.variables.VariableIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIOCompat;
import org.netbeans.modules.vcs.advanced.projectsettings.CommandLineVcsFileSystemInstance;

/** Generic command line VCS filesystem.
 * 
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public class CommandLineVcsFileSystem extends VcsFileSystem implements java.beans.PropertyChangeListener {

    public static final String PROP_SHORT_FILE_STATUSES = "shortFileStatuses"; // NOI18N
    public static final String PROP_CONFIG = "config"; // NOI18N
    public static final String PROP_CONFIG_ROOT = "configRoot"; // NOI18N
    public static final String PROP_CONFIG_ROOT_FO = "configRootFO"; // NOI18N
    public static final String PROP_CONFIG_FILE_NAME = "configFileName"; // NOI18N
    public static final String PROP_ADDITIONAL_POSSIBLE_FILE_STATUSES_MAP = "additionalPossibleFileStatusesMap"; // NOI18N
    public static final String PROP_LOCAL_FILES_FILTERED_OUT = "localFilesFilteredOut"; // NOI18N
    public static final String PROP_DOC_CLEANUP_REMOVE_ITEM = "docCleanupRemoveItems"; // NOI18N
    public static final String PROP_COMPATIBLE_OS = "compatibleOSs"; // NOI18N
    public static final String PROP_UNCOMPATIBLE_OS = "uncompatibleOSs"; // NOI18N
    
    public static final String VAR_LOCAL_FILES_FILTERED_OUT = "LOCAL_FILES_FILTERED_OUT"; // NOI18N
    public static final String VAR_LOCAL_FILES_FILTERED_OUT_CASE_SENSITIVE = "LOCAL_FILES_FILTERED_OUT_CASE_SENSITIVE"; // NOI18N
    public static final String VAR_POSSIBLE_FILE_STATUSES = "POSSIBLE_FILE_STATUSES"; // NOI18N
    public static final String VAR_POSSIBLE_FILE_STATUSES_LOCALIZED = "POSSIBLE_FILE_STATUSES_LOCALIZED"; // NOI18N
    public static final String VAR_POSSIBLE_FILE_STATUSES_LOCALIZED_SHORT = "POSSIBLE_FILE_STATUSES_LOCALIZED_SHORT"; // NOI18N
    public static final String VAR_NOT_MODIFIABLE_FILE_STATUSES = "NOT_MODIFIABLE_FILE_STATUSES"; // NOI18N
    //public static final String VAR_FILE_STATUS_MODIFIED = "FILE_STATUS_MODIFIED"; // NOI18N
    public static final String VAR_VCS_FILE_STATUS_MISSING = "VCS_FILE_STATUS_MISSING"; // NOI18N
    public static final String VAR_VCS_FOLDER_STATUS_MISSING = "VCS_FOLDER_STATUS_MISSING"; // NOI18N
    public static final String VAR_NOT_MISSINGABLE_FILE_STATUSES = "NOT_MISSINGABLE_FILE_STATUSES"; // NOI18N
    public static final String VAR_NOT_MISSINGABLE_FOLDER_STATUSES = "NOT_MISSINGABLE_FOLDER_STATUSES"; // NOI18N
    public static final String VAR_ICONS_FOR_FILE_STATUSES = "ICONS_FOR_FILE_STATUSES"; // NOI18N
    
    /**
     * The name of a variable, that may contain the file name of file status disk cache.
     * This name is relative to the given file's parent directory.
     * <p>
     * E.g. when DISK_CACHE_FILE_NAME=.status.cache, then the disk cache for all files
     * in folder /home/john/src/ will be /home/john/src/.status.cache
     */
    public static final String VAR_DISK_CACHE_FILE_NAME = "DISK_CACHE_FILE_NAME"; // NOI18N

    /**
     * The name of a variable, that tells whether the cache folder can be created if it does not exist.
     */
    public static final String VAR_DISK_CACHE_FOLDER_CAN_CREATE = "DISK_CACHE_FOLDER_CAN_CREATE"; // NOI18N

    /**
     * The name of a variable, which contains the parent ignore list for
     * CREATE_FOLDER_IGNORE_LIST command.
     */
    public static final String VAR_PARENT_IGNORE_LIST = "PARENT_IGNORE_LIST"; // NOI18N

    /**
     * The name of a variable, which contains names of variables, whose values are compared.
     * If the values are the same, among several filesystems, then their password are shared.
     * Therefore when you have more than one filesystem mounted for the same project, you
     * do not need to re-enter the password for each filesystem.
     */
    public static final String VAR_PASSWORD_SHARE = "SHARE_PASSWORD_WHEN_CONFIG_IS_SAME"; // NOI18N
    
    /**
     * This command is executed to get the initial ignore list.
     * It's supposed to return the ignored files on its data output.
     */
    public static final String CMD_CREATE_INITIAL_IGNORE_LIST = "CREATE_INITIAL_IGNORE_LIST"; // NOI18N
    /**
     * This command is executed to get the ignore list on each folder.
     * It gets the parent ignore list in PARENT_IGNORE_LIST variable.
     * It's supposed to return the ignored files on its data output.
     */
    public static final String CMD_CREATE_FOLDER_IGNORE_LIST = "CREATE_FOLDER_IGNORE_LIST"; // NOI18N
    
    private static final boolean DEFAULT_LOCAL_FILE_FILTER_CASE_SENSITIVE = true;
    
    //private static final String DEFAULT_CONFIG_NAME = "empty.xml";
    //private static final String DEFAULT_CONFIG_NAME_COMPAT = "empty.properties";
    
    private static ResourceBundle resourceBundle = null;

    public static final String TEMPORARY_CONFIG_FILE_NAME = "tmp"; // NOI18N
    
    /** Ugly, but I can not do this in the constuctor, because it can cause deadlock.
     * And I need to initialize the commands somehow, because null will fail somewhere else.
     * Commands should not be stored in Node structure in future releases, since it cause problems (deadlocks) */
    private static final Node EMPTY_COMMANDS = new VcsCommandNode(new Children.Array(), new UserCommand("NONE"));

    private String config = null;//"Empty (Unix)"; // NOI18N

    private Debug D = new Debug ("CommandLineVcsFileSystem", true); // NOI18N
    private /*static transient*/ String CONFIG_ROOT="vcs/config"; // NOI18N
    private FileObject CONFIG_ROOT_FO;
    private String configFileName = null;
    private HashMap additionalPossibleFileStatusesMap = null;
    private transient HashMap additionalStatusIconMap = null;
    private Vector localFilesFilteredOut = null;
    private boolean localFileFilterCaseSensitive = DEFAULT_LOCAL_FILE_FILTER_CASE_SENSITIVE;
    private Vector docCleanupRemoveItems = null;
    private static final String CACHE_FILE_NAME = "vcs.cache"; // NOI18N
    private String cacheRoot;
    private String cachePath;
    private long cacheId = 0;
    private String cacheFileName = null; // The cache file relative to the file's directory.
    private boolean cacheFolderCanCreate = false;
    private boolean shortFileStatuses = false;
    private Set compatibleOSs = null;
    private Set uncompatibleOSs = null;
    private transient boolean doInitialCheckout = false; // whether to do an initial checkout after the FS is mounted
    private transient PropertyChangeListener sharedPasswordChangeListener;
    private Object sharedPasswordKey = null;

    static final long serialVersionUID =-1017235664394970926L;
    /**
     * Create a new command-line VCS FileSystem.
     */
    public CommandLineVcsFileSystem () {
        this(false, null);
    }
    
    /**
     * Create a new command-line VCS FileSystem with a deserialized flag.
     * @param treatAsDeserialized If true, the filesystem is considered
     *        to be read from a settings file rather than created a brand new one.
     */
    public CommandLineVcsFileSystem (boolean treatAsDeserialized) {
        this(treatAsDeserialized, null);
    }
    
    /**
     * Create a new command-line VCS FileSystem.
     * @param cap The capabilities of the filesystem.
     */
    public CommandLineVcsFileSystem(FileSystemCapability cap) {
        this(false, cap);
    }
    
    /**
     * Create a new command-line VCS FileSystem with a deserialized flag.
     * @param treatAsDeserialized If true, the filesystem is considered
     *        to be read from a settings file rather than created a brand new one.
     * @param cap The capabilities of the filesystem.
     */
    public CommandLineVcsFileSystem (boolean treatAsDeserialized, FileSystemCapability cap) {
        //D.deb("CommandLineVcsFileSystem()"); // NOI18N
        super ();
        if (cap != null) setCapability(cap);
        deserialized = treatAsDeserialized;
        /*
        if (attr instanceof VcsAttributes) {
            ((VcsAttributes) attr).setCommandsProvider(new CommandLineCommandsProvider(this));
        }
         */
        //System.out.println("\nNEW CommandLineVcsFileSystem()"+this+"\n");
        setConfigFO();
        //boolean status = readConfiguration (DEFAULT_CONFIG_NAME);
        //if (status == false) readConfigurationCompat(DEFAULT_CONFIG_NAME_COMPAT);
        setCommands(EMPTY_COMMANDS);
        addPropertyChangeListener(this);
        cacheRoot = System.getProperty("netbeans.user")+File.separator+
                    "system"+File.separator+"vcs"+File.separator+"cache"; // NOI18N
        cachePath = createNewCacheDir();
        //System.out.println("  cachePath = "+cachePath);
        setCreateVersioningSystem(true);
        try {
            setRootDirectory(getRootDirectory(), true);
        } catch (PropertyVetoException vetoExc) {
        } catch (IOException ioExc) {
        }
        setIgnoreListSupport(new GenericIgnoreListSupport());
        setCreateBackupFiles(true);
        setFilterBackupFiles(true);
        sharedPasswordChangeListener = new SharedPasswordListener();
        SharedPasswords.getInstance().addPropertyChangeListener(
            org.openide.util.WeakListener.propertyChange(sharedPasswordChangeListener,
                                        SharedPasswords.getInstance()));
    }
    
    protected java.lang.ref.Reference createReference(final FileObject fo) {
        java.lang.ref.Reference ref = super.createReference(fo);
        if (checkVirtual(fo.getPackageNameExt('/','.'))) {
            //System.out.println("   !!! new ref is virtual");
            if (ref instanceof CacheReference) {
                ((CacheReference) ref).setVirtual(true);
            } else {
                setVirtualDataLoader(fo);
            }
        }
        return ref;
    }
    
    /**
     * Perform the check of whether the file is or is not still virtual. This
     * method is called on every file status change with the set of potentially
     * changed files.
     * @param the set of FileObjects whose status was changed
     */
    protected void checkVirtualFiles(Set foSet) {
        //Set reloadFoSet = new HashSet();
        for (Iterator foIt = foSet.iterator(); foIt.hasNext(); ) {
            FileObject fo = (FileObject) foIt.next();
            setVirtualDataLoader(fo);
        }
        /*
        for (Iterator foIt = reloadFoSet.iterator(); foIt.hasNext(); ) {
            FileObject fo = (FileObject) foIt.next();
            try {
                DataObject dob = DataObject.find(fo);
                dob.setValid(false);
            } catch (DataObjectNotFoundException de) {
            } catch (PropertyVetoException exc2) {}
        }
         */
    }
    
    public VcsFactory getVcsFactory () {
        return new CommandLineVcsFactory (this);
    }
    
    /**
     * Get the working directory, without the relative mount point.
     */
    public java.io.File getWorkingDirectory() {
        String defaultRoot = VcsFileSystem.substractRootDir (getRootDirectory ().toString (), getRelativeMountPoint());
        return new java.io.File(defaultRoot);
    }
    
    /**
     * Get the password that should be stored into FS settings.
     * Can be used for possible encryption.
     */
    public String getPasswordStored() {
        if (!isRememberPassword()) return null;
        return getPassword();
    }

    /**
     * Set the password that was stored in FS settings.
     * Can be used for possible encryption.
     */
    public void setPasswordStored(String password) {
        setPassword(password);
    }
    
    public int getRefreshTimeStored() {
        return getVcsRefreshTime();
    }
    
    public void setRefreshTimeStored(int refreshTime) {
        setVcsRefreshTime(refreshTime);
    }

    /**
     * Get the root of the configuration files
     */
    public String getConfigRoot(){
        return CONFIG_ROOT;
    }

    /**
     * Get the root of the configuration as a FileObject.
     */
    public FileObject getConfigRootFO() {
        return CONFIG_ROOT_FO;
    }
    
    public void setConfigRoot(String s) {
        CONFIG_ROOT = s;
        firePropertyChange(PROP_CONFIG_ROOT, null, s);
    }

    public void setConfig(String label) {
        this.config = label;
        firePropertyChange(PROP_CONFIG, null, label);
        firePropertyChange(PROP_DISPLAY_NAME, null, getDisplayName());
    }
    
    /**
     * Get the configuration display name or null, when no configuration is loaded.
     */
    public String getConfig() {
        return config;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
        firePropertyChange(PROP_CONFIG_FILE_NAME, null, configFileName);
    }
    
    public String getConfigFileName() {
        return configFileName;
    }
    
    public String getConfigFileModificationTimeStr() {
        if (CONFIG_ROOT_FO == null) return "0"; // NOI18N
        FileObject fo = CONFIG_ROOT_FO.getFileObject(configFileName);
        if (fo == null) return "0"; // NOI18N
        return fo.lastModified().toGMTString();
    }

    private void setConfigFO() {
        FileSystem dfs = TopManager.getDefault ().getRepository ().getDefaultFileSystem ();
        FileObject fo = dfs.findResource(CONFIG_ROOT);
        if (fo == null) {
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
                public void run () {
                    TopManager.getDefault ().notify (new NotifyDescriptor.Message (CommandLineVcsFileSystem.this.clg("DLG_ConfigurationPathNotFound", CONFIG_ROOT)));
                }
            });
        }
        CONFIG_ROOT_FO = fo;
        firePropertyChange(PROP_CONFIG_ROOT_FO, null, fo);
    }

    /*
     * Get the cache identification.
     *
    public String getCacheIdStr() {
        System.out.println("CmdLineVcsFileSystem.getCacheIdStr(): cacheId = "+cacheId);
        Thread.dumpStack();
        return "VCS_Cache" + getCacheId();
    }
     */
    
    /**
     * Get the ID of the disk cache. Note, that this does not have any connection
     * to VcsFileSystem.getCacheIdStr(), which returns the string identification
     * of a memory cache.
     */
    public long getCacheId() {
        if (cacheId == 0) {
            createNewCacheId();
        }
        return cacheId;
    }

    /**
     * Set the ID of the disk cache. This method should be used ONLY by the
     * settings storage process to restore the previous state.
     * Note, that this does not have any connection to
     * VcsFileSystem.getCacheIdStr(), which returns the string identification
     * of a memory cache.
     */
    public void setCacheId(long cacheId) {
        this.cacheId = cacheId;
        this.cachePath = createNewCacheDir();
    }

    /**
     * Get the full file path where cache information should be stored.
     */
    public String getCacheFileName(String path) {
        if (cacheFileName != null) {
            String cacheFilePath = getFile(path).getAbsolutePath() + File.separator + cacheFileName;
            if (!cacheFolderCanCreate) {
                File cacheFile = new File(cacheFilePath);
                // No cache file when the parent does not exist and I can not create it.
                if (!cacheFile.getParentFile().exists()) return null;
            }
            return (getFile(path).getAbsolutePath() + File.separator + cacheFileName).intern();
        }
        return (cachePath + File.separator + getRelativeMountPoint()
               + File.separator + path + File.separator + CACHE_FILE_NAME).intern();
        /*
        File file = getFile(path);
        if (!file.isDirectory()) file = file.getParentFile();
        return file.getAbsolutePath() + File.separator + CVS_DIRNAME + File.separator + CACHE_FILE_NAME;
         */
    }

    private void createDir(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            return ;
        }
        if (dir.mkdirs() == false) {
            //E.err(g("MSG_UnableToCreateDirectory", path)); // NOI18N
            debug(clg("MSG_UnableToCreateDirectory", path)); // NOI18N
        }
    }
    
    private void createNewCacheId() {
        do {
            cacheId = 10000 * (1 + Math.round (Math.random () * 8)) + Math.round (Math.random () * 1000);
        } while (new File(cacheRoot+File.separator+cacheId).isDirectory ());
        FileSystem dfs = TopManager.getDefault ().getRepository ().getDefaultFileSystem ();
        FileObject vcs = dfs.findResource("vcs");
        try {
            if (vcs == null) {
                vcs = dfs.getRoot().createFolder("vcs");
            }
            FileObject cache = vcs.getFileObject("cache");
            if (cache == null) {
                cache = vcs.createFolder("cache");
            }
        } catch (java.io.IOException exc) {
        }
    }

    private String createNewCacheDir() {
        String dir;
        if (cacheId == 0) {
            createNewCacheId();
        }
        dir = cacheRoot+File.separator+cacheId;
        //createDir(dir); - should not be necessary. The cache is created when needed.
        return dir;
    }

    /**
     * Set whether to do initial checkout after mount or not.
     */
    public void setInitialCheckout(boolean doInitialCheckout) {
        this.doInitialCheckout = doInitialCheckout;
    }
    
    /**
     * Notification, that the filesystem is being added to the repository
     */
    public void addNotify() {
        if (this.doInitialCheckout) {
            Table files = new Table();
            files.put("", findResource(""));
            VcsCommand cmd = getCommand("CHECKOUT");
            if (cmd != null) {
                VcsAction.doCommand(files, cmd, null, this);
            }
        }
        super.addNotify();
    }    
    
    public void removeNotify() {
        File dir = new File (cachePath);
        if(dir.exists () && dir.isDirectory () && dir.canWrite ()) {
            if(!VcsUtilities.deleteRecursive(dir)) {
                // Ignored. Let it be, when I can not remove it.
            }
        }
        super.removeNotify();
    }
    
    public boolean readConfigurationCompat (String configFileName) {
        D.deb ("readConfigurationCompat ()"); // NOI18N
        //CONFIG_ROOT=System.getProperty("netbeans.user")+File.separator+
        //            "system"+File.separator+"vcs"+File.separator+"config"; // NOI18N
        //CONFIG_ROOT = "vcs"+File.separator+"config"; // NOI18N
        //setConfigFO();
        if (CONFIG_ROOT_FO == null) return false;
        //Properties props=VcsConfigVariable.readPredefinedPropertiesIO(CONFIG_ROOT+File.separator+"empty.properties"); // NOI18N
        Properties props = VariableIOCompat.readPredefinedProperties(CONFIG_ROOT_FO, configFileName); // NOI18N
        String label = props.getProperty("label", clg("CTL_No_label_configured"));
        setVariables (VariableIOCompat.readVariables(props));
        D.deb("setVariables DONE."); // NOI18N
        
        setCommands ((org.openide.nodes.Node) CommandLineVcsAdvancedCustomizer.readConfig (props));
        D.deb("readConfigurationCompat() done"); // NOI18N
        setConfig(label);
        return true;
    }

    public boolean readConfiguration (String configFileName) {
        D.deb ("readConfiguration ()"); // NOI18N
        //CONFIG_ROOT=System.getProperty("netbeans.user")+File.separator+
        //            "system"+File.separator+"vcs"+File.separator+"config"; // NOI18N
        //CONFIG_ROOT = "vcs"+File.separator+"config"; // NOI18N
        //setConfigFO();
        if (CONFIG_ROOT_FO == null) return false;
        //Properties props=VcsConfigVariable.readPredefinedPropertiesIO(CONFIG_ROOT+File.separator+"empty.properties"); // NOI18N
        org.w3c.dom.Document doc = VariableIO.readPredefinedConfigurations(CONFIG_ROOT_FO, configFileName); // NOI18N
        if (doc == null) return false;
        String label = VariableIO.getConfigurationLabel(doc);
        setVariables (VariableIO.readVariables(doc));
        D.deb("setVariables DONE."); // NOI18N
        
        setCommands ((org.openide.nodes.Node) CommandLineVcsAdvancedCustomizer.readConfig (doc));
        D.deb("readConfiguration() done"); // NOI18N
        setConfig(label);
        return true;
    }

    /**
     * Call this method to perform the login process.
     */
    public boolean checkLogin(String connectStr, String password) 
            throws IOException, java.net.UnknownHostException {
        VcsCommand cmd = getCommand("DO_LOGIN");
        if (cmd == null) return true; // suppose, that the login is O.K.
        Table files = new Table();
        Hashtable additionalVars = new Hashtable();
        additionalVars.put("CONNECT_STR", connectStr);
        additionalVars.put("PASSWORD", password);
        final StringBuffer errOutput = new StringBuffer();
        CommandOutputListener errListener = new CommandOutputListener() {
            public void outputLine(String line) {
                errOutput.append(line + "\n");
            }
        };
        files.put("", null); // the command will not run if no files would be supplied.
        VcsCommandExecutor[] execs = VcsAction.doCommand(files, cmd, additionalVars, this, null, errListener, null, null);
        if (execs.length == 0) return true;
        VcsCommandExecutor exec = execs[0];
        boolean succeeded;
        try {
            getCommandsPool().waitToFinish(exec);
            succeeded = (exec.getExitStatus() == VcsCommandExecutor.SUCCEEDED);
        } catch (InterruptedException iexc) {
            succeeded = false;
        }
        if (!succeeded) {
            throw new java.io.IOException() {
                public String getLocalizedMessage() {
                    return errOutput.toString();
                }
            };
        }
        return succeeded;
    }
    
    /**
     * Allows some cleanup of the document which the user is asked for.
     * doc The Document
     * promptNum the order of the document
     * docIdentif some identification that can be set in settting the listener.
     */
    public void filePromptDocumentCleanup(javax.swing.JTextArea ta, int promptNum, Object docIdentif) {
        // Let the document unchanged by default
        javax.swing.text.Document doc = ta.getDocument();
        if (docIdentif instanceof UserCommand) {
            UserCommand cmd = (UserCommand) docIdentif;
            if (docCleanupRemoveItems != null) {
                for(int i = 0; i < docCleanupRemoveItems.size(); i++) {
                    CommandLineVcsFileSystem.DocCleanupRemoveItem item = (CommandLineVcsFileSystem.DocCleanupRemoveItem) docCleanupRemoveItems.get(i);
                    if (cmd.getName().equals(item.getCmdName()) && promptNum == item.getOrder()) {
                        String lineBegin = item.getLineBegin();
                        for(int line = 0; line < ta.getLineCount(); line++) {
                            try {
                                int begin = ta.getLineStartOffset(line);
                                int end = ta.getLineEndOffset(line);
                                String lineStr = doc.getText(begin, end - begin);
                                if (lineStr.regionMatches(0, lineBegin, 0, lineBegin.length())) {
                                    if (end > doc.getLength()) end = doc.getLength();
                                    doc.remove(begin, end - begin);
                                    line--;
                                }
                            } catch (javax.swing.text.BadLocationException exc) {
                                org.openide.TopManager.getDefault().notifyException(exc);
                            }
                        }
                    }
                }
            }
        }
    }

    public void propertyChange (PropertyChangeEvent evt) {
        if (PROP_PASSWORD.equals(evt.getPropertyName())) {
            if (sharedPasswordKey != null) {
                SharedPasswords.getInstance().setPassword(sharedPasswordKey, getPassword());
            }
            return ;
        }
        if (evt.getPropertyName() != FileSystem.PROP_VALID) return;
        if (isValid()) {
            D.deb("Filesystem added to the repository, setting refresh time to "+refreshTimeToSet); // NOI18N
            setVcsRefreshTime(refreshTimeToSet);
            warnDirectoriesDoNotExists();
        } else {
            D.deb("Filesystem is not valid any more, setting refresh time to 0"); // NOI18N
            setVcsRefreshTime(0);
        }
    }
    
    public boolean isShortFileStatuses() {
        return shortFileStatuses;
    }
    
    public void setShortFileStatuses(boolean shortFileStatuses) {
        if (this.shortFileStatuses != shortFileStatuses) {
            this.shortFileStatuses = shortFileStatuses;
            setPossibleFileStatusesFromVars();
            refreshStatusOfExistingFiles();
            firePropertyChange(PROP_SHORT_FILE_STATUSES, !shortFileStatuses ? Boolean.TRUE : Boolean.FALSE, shortFileStatuses ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    protected void refreshStatusOfExistingFiles() {
        Enumeration enum = existingFileObjects(getRoot());
        HashSet existingFOs = new HashSet();
        while (enum.hasMoreElements()) {
            existingFOs.add(enum.nextElement());
        }
        fireFileStatusChanged(new org.openide.filesystems.FileStatusEvent(this, existingFOs, false, true));
    }
    
    private void setPossibleFileStatusesFromVars() {
        VcsConfigVariable varStatuses = (VcsConfigVariable) variablesByName.get (VAR_POSSIBLE_FILE_STATUSES);
        VcsConfigVariable varStatusesLclz;
        if (isShortFileStatuses()) {
            varStatusesLclz = (VcsConfigVariable) variablesByName.get (VAR_POSSIBLE_FILE_STATUSES_LOCALIZED_SHORT);
        } else {
            varStatusesLclz = (VcsConfigVariable) variablesByName.get (VAR_POSSIBLE_FILE_STATUSES_LOCALIZED);
        }
        if (additionalPossibleFileStatusesMap != null) VcsUtilities.removeKeys(possibleFileStatusesMap, additionalPossibleFileStatusesMap);
        additionalPossibleFileStatusesMap = null;
        if (varStatuses != null) {
            additionalPossibleFileStatusesMap = new HashMap();
            String[] possStatuses = VcsUtilities.getQuotedStrings(varStatuses.getValue());
            String[] possStatusesLclz = null;
            if (varStatusesLclz != null) possStatusesLclz = VcsUtilities.getQuotedStrings(varStatusesLclz.getValue());
            int i = 0;
            if (possStatusesLclz != null) {
                for(; i < possStatuses.length && i < possStatusesLclz.length; i++) {
                    additionalPossibleFileStatusesMap.put(possStatuses[i], possStatusesLclz[i]);
                }
            }
            for(; i < possStatuses.length; i++) {
                additionalPossibleFileStatusesMap.put(possStatuses[i], possStatuses[i]);
            }
            possibleFileStatusesMap.putAll(additionalPossibleFileStatusesMap);
        }
        //setModifiedAndMissingStates((VcsConfigVariable) variablesByName.get (VAR_FILE_STATUS_MODIFIED),
        //                            (VcsConfigVariable) variablesByName.get (VAR_FILE_STATUS_MISSING));
        setMissingStates((VcsConfigVariable) variablesByName.get (VAR_VCS_FILE_STATUS_MISSING),
                         (VcsConfigVariable) variablesByName.get (VAR_VCS_FOLDER_STATUS_MISSING),
                         (VcsConfigVariable) variablesByName.get (VAR_NOT_MISSINGABLE_FILE_STATUSES),
                         (VcsConfigVariable) variablesByName.get (VAR_NOT_MISSINGABLE_FOLDER_STATUSES));
        firePropertyChange(PROP_ADDITIONAL_POSSIBLE_FILE_STATUSES_MAP, null, additionalPossibleFileStatusesMap);
    }
    
    /*
    private void setModifiedAndMissingStates(VcsConfigVariable modifiedVar, VcsConfigVariable missingVar) {
        String modifiedStatus = null;
        String missingStatus = null;
        if (modifiedVar != null) modifiedStatus = modifiedVar.getValue();
        if (missingVar != null) missingStatus = missingVar.getValue();
        if (modifiedStatus != null || missingStatus != null) {
            FileCacheProvider cache = getCacheProvider();
            if (cache instanceof VcsFSCache) {
                if (modifiedStatus != null) ((VcsFSCache) cache).setStatusStringModified(modifiedStatus);
                if (missingStatus != null) ((VcsFSCache) cache).setStatusStringMissing(missingStatus);
            }
        }
    }
     */
    
    private void setMissingStates(VcsConfigVariable varStatusFileMissing,
                                  VcsConfigVariable varStatusFolderMissing,
                                  VcsConfigVariable varNotMissingableFileStatuses,
                                  VcsConfigVariable varNotMissingableFolderStatuses) {
        String statusFileMissing = null;
        String statusFolderMissing = null;
        if (varStatusFileMissing != null) statusFileMissing = varStatusFileMissing.getValue();
        if (varStatusFolderMissing != null) statusFolderMissing = varStatusFolderMissing.getValue();
        setMissingFileStatus(statusFileMissing);
        setMissingFolderStatus(statusFolderMissing);
        if (statusFileMissing != null || statusFolderMissing != null) {
            if (varNotMissingableFileStatuses != null) {
                java.util.List statuses = Arrays.asList(VcsUtilities.getQuotedStrings(
                    varNotMissingableFileStatuses.getValue()));
                replaceItemsWithMappedValues(statuses, possibleFileStatusesMap);
                setNotMissingableFileStatuses(statuses);
            }
            if (varNotMissingableFolderStatuses != null) {
                java.util.List statuses = Arrays.asList(VcsUtilities.getQuotedStrings(
                    varNotMissingableFolderStatuses.getValue()));
                replaceItemsWithMappedValues(statuses, possibleFileStatusesMap);
                setNotMissingableFolderStatuses(statuses);
            }
        }
    }
    
    private void replaceItemsWithMappedValues(java.util.List list, java.util.Map map) {
        for (int i = 0; i < list.size(); i++) {
            Object value = map.get(list.get(i));
            if (value != null) list.set(i, value);
        }
    }
    
    private void setBadgeIconsFromVars() {
        VcsConfigVariable varStatuses = (VcsConfigVariable) variablesByName.get (VAR_POSSIBLE_FILE_STATUSES);
        VcsConfigVariable varIcons = (VcsConfigVariable) variablesByName.get (VAR_ICONS_FOR_FILE_STATUSES);
        if (additionalStatusIconMap != null) VcsUtilities.removeKeys(statusIconMap, additionalStatusIconMap);
        additionalStatusIconMap = null;
        if (varStatuses != null) {
            additionalStatusIconMap = new HashMap();
            String[] possStatuses = VcsUtilities.getQuotedStrings(varStatuses.getValue());
            String[] iconResources = null;
            if (varIcons != null) iconResources = VcsUtilities.getQuotedStrings(varIcons.getValue());
            if (iconResources != null) {
                FileSystem defaultFS = TopManager.getDefault().getRepository().getDefaultFileSystem();
                for (int i = 0; i < possStatuses.length && i < iconResources.length; i++) {
                    if (iconResources[i].length() == 0) continue;
                    FileObject resourceFile = defaultFS.findResource(iconResources[i]);
                    if (resourceFile == null) {
                        TopManager.getDefault().notify(new NotifyDescriptor.Message(
                            NbBundle.getMessage(CommandLineVcsFileSystem.class, "MSG_CanNotFindIconResource", iconResources[i])));
                        continue;
                    }
                    try {
                        additionalStatusIconMap.put(possStatuses[i], new javax.swing.ImageIcon(resourceFile.getURL()).getImage());
                    } catch (FileStateInvalidException exc) {
                        TopManager.getDefault().notify(new NotifyDescriptor.Message(
                            NbBundle.getMessage(CommandLineVcsFileSystem.class, "MSG_InvalidFileIconResource", iconResources[i])));
                        continue;
                    }
                }
                statusIconMap.putAll(additionalStatusIconMap);
            }
        }
    }
    
    private void setNotModifiableStatusesFromVars() {
        VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(VAR_NOT_MODIFIABLE_FILE_STATUSES);
        if (var != null) {
            String[] statuses = VcsUtilities.getQuotedStrings(var.getValue());
            setNotModifiableStatuses(new ArrayList(Arrays.asList(statuses))); // using Arraylist to be Serializable
        }
    }
    
    private void setLocalFileFilterFromVars() {
        VcsConfigVariable varLocalFilter = (VcsConfigVariable) variablesByName.get(VAR_LOCAL_FILES_FILTERED_OUT);
        VcsConfigVariable varLocalFilterCS = (VcsConfigVariable) variablesByName.get(VAR_LOCAL_FILES_FILTERED_OUT_CASE_SENSITIVE);
        if (varLocalFilter != null) {
            if (varLocalFilterCS != null) {
                localFileFilterCaseSensitive = varLocalFilterCS.getValue().equalsIgnoreCase("true");
            }
            String qfiles = varLocalFilter.getValue();
            if (!localFileFilterCaseSensitive) qfiles = qfiles.toUpperCase();
            String[] files = VcsUtilities.getQuotedStrings(qfiles);
            localFilesFilteredOut = new Vector(Arrays.asList(files));
        } else localFilesFilteredOut = null;
        firePropertyChange(PROP_LOCAL_FILES_FILTERED_OUT, null, localFilesFilteredOut);
    }
    
    private void setDocumentCleanupFromVars() {
        VcsConfigVariable docCleanupRemove;
        docCleanupRemoveItems = null;
        for(int i = 1; (docCleanupRemove = (VcsConfigVariable) variablesByName.get("DOCUMENT_CLEANUP_REMOVE"+i)) != null; i++) {
            String[] removeWhat = VcsUtilities.getQuotedStrings(docCleanupRemove.getValue());
            if (removeWhat.length < 3) continue;
            int order = 0;
            try {
                order = Integer.parseInt(removeWhat[1]);
                order--;
            } catch (NumberFormatException exc) {
                org.openide.TopManager.getDefault().notifyException(exc);
                continue;
            }
            CommandLineVcsFileSystem.DocCleanupRemoveItem item = new CommandLineVcsFileSystem.DocCleanupRemoveItem(removeWhat[0], order, removeWhat[2]);
            if (docCleanupRemoveItems == null) docCleanupRemoveItems = new Vector();
            docCleanupRemoveItems.add(item);
        }
        firePropertyChange(PROP_DOC_CLEANUP_REMOVE_ITEM, null, docCleanupRemoveItems);
    }
    
    private void setAdditionalParamsLabels() {
        VcsConfigVariable userParams = (VcsConfigVariable) variablesByName.get("USER_GLOBAL_PARAM_LABELS");
        if (userParams != null) {
            setUserParamsLabels(VcsUtilities.getQuotedStrings(userParams.getValue()));
        }
        userParams = (VcsConfigVariable) variablesByName.get("USER_LOCAL_PARAM_LABELS");
        if (userParams != null) {
            setUserLocalParamsLabels(VcsUtilities.getQuotedStrings(userParams.getValue()));
        }
    }
    
    /**
     * Set the file system's variables.
     * @param variables the vector of <code>VcsConfigVariable</code> objects.
     */
    public void setVariables(Vector variables){
        super.setVariables(variables);
        setPossibleFileStatusesFromVars();
        setBadgeIconsFromVars();
        setNotModifiableStatusesFromVars();
        setLocalFileFilterFromVars();
        setDocumentCleanupFromVars();
        setAdditionalParamsLabels();
        setSharedPassword();
        setCacheFile();
    }
    
    private void setSharedPassword() {
        Object newSharedPasswordKey = computeSharedPasswordKey();
        if (sharedPasswordKey != null) {
            if (newSharedPasswordKey != null) {
                SharedPasswords.getInstance().changeKey(sharedPasswordKey,
                                                        newSharedPasswordKey);
            } else {
                SharedPasswords.getInstance().setPassword(sharedPasswordKey, null);
            }
        } else {
            if (newSharedPasswordKey != null) {
                SharedPasswords passwords = SharedPasswords.getInstance();
                String password = passwords.getPassword(newSharedPasswordKey);
                if (password != null) {
                    setPassword(password);
                } else {
                    passwords.setPassword(newSharedPasswordKey, getPassword());
                }
            }
        }
        sharedPasswordKey = newSharedPasswordKey;
    }
    
    private Object computeSharedPasswordKey() {
        VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(VAR_PASSWORD_SHARE);
        if (var != null) {
            String configVars = var.getValue();
            if (configVars != null) {
                String[] vars = VcsUtilities.getQuotedStrings(configVars);
                ArrayList values = new ArrayList(vars.length);
                for (int i = 0; i < vars.length; i++) {
                    var = (VcsConfigVariable) variablesByName.get(vars[i]);
                    if (var != null) {
                        values.add(var.getValue());
                    } else {
                        values.add(null);
                    }
                }
                return values;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    private void setCacheFile() {
        VcsConfigVariable var = (VcsConfigVariable) variablesByName.get(VAR_DISK_CACHE_FILE_NAME);
        String newCacheFileName;
        if (var != null) {
            newCacheFileName = var.getValue();
            if (newCacheFileName.length() == 0) newCacheFileName = null;
        } else newCacheFileName = null;
        
        var = (VcsConfigVariable) variablesByName.get(VAR_DISK_CACHE_FOLDER_CAN_CREATE);
        if (var != null) {
            cacheFolderCanCreate = Boolean.TRUE.toString().equalsIgnoreCase(var.getValue());
        } else cacheFolderCanCreate = false;
        
        if (cacheFileName == null && newCacheFileName != null ||
            cacheFileName != null && !cacheFileName.equals(newCacheFileName)) {
            
            cacheFileName = newCacheFileName;
            cacheFileNameChanged();
        }
    }
    
    private void cacheFileNameChanged() {
        if (cache != null) {// && cache instanceof VcsFSCache) {
            //((VcsFSCache) cache).destroyCache();
        }
    }

    public FilenameFilter getLocalFileFilter() {
        return new FilenameFilter() {
                   public boolean accept(File dir, String name) {
                       if (!localFileFilterCaseSensitive) name = name.toUpperCase();
                       if (localFilesFilteredOut == null) return true;
                       else return !localFilesFilteredOut.contains(name);
                       //return !name.equalsIgnoreCase("CVS"); // NOI18N
                   }
               };
    }
    
    /**
     * Finds out, whether the configuration file name is a temporary configuration.
     * Temporary configurations are saved during serialization of the FS.
     */
    public static boolean isTemporaryConfig(String configFileName) {
        if (configFileName.startsWith(TEMPORARY_CONFIG_FILE_NAME)) {
            String tempNo = configFileName.substring(TEMPORARY_CONFIG_FILE_NAME.length());
            if (tempNo.length() == 5) {
                try {
                    Integer.parseInt(tempNo);
                } catch (NumberFormatException exc) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    
    private org.openide.nodes.Node tryToFindDefaultCommands() {
        if (config == null) return null;
        ProfilesCache cache = new ProfilesCache(CONFIG_ROOT_FO);
        return (org.openide.nodes.Node) cache.getProfileCommands(config);
    }
    
    private void loadCurrentConfig() {
        //System.out.println("loadCurrentConfig() configFileName = "+configFileName);
        org.openide.nodes.Node commands = null;
        if (configFileName != null) {
            org.w3c.dom.Document doc = VariableIO.readPredefinedConfigurations(CONFIG_ROOT_FO, configFileName);
            if (doc == null) return ;
            try {
                commands = (org.openide.nodes.Node) CommandLineVcsAdvancedCustomizer.readConfig (doc);
            } catch (org.w3c.dom.DOMException exc) {
                org.openide.TopManager.getDefault().notifyException(exc);
            }
            if (commands == null) {
                commands = tryToFindDefaultCommands();
            }
            if (commands == null) return ;
            this.setCommands(commands);
        }
    }
    
    private void saveCurrentConfig() {
        configFileName = TEMPORARY_CONFIG_FILE_NAME + cacheId + "." + VariableIO.CONFIG_FILE_EXT;
        FileObject file = CONFIG_ROOT_FO.getFileObject(TEMPORARY_CONFIG_FILE_NAME + cacheId, VariableIO.CONFIG_FILE_EXT);
        if (file == null) {
            try {
                file = CONFIG_ROOT_FO.createData(TEMPORARY_CONFIG_FILE_NAME + cacheId, VariableIO.CONFIG_FILE_EXT);
            } catch (IOException ioexc) {
                TopManager.getDefault().notifyException(ioexc);
                return ;
            }
        }
        org.w3c.dom.Document doc = null;
        /*
        org.openide.loaders.DataObject dobj = null;
        try {
            dobj = org.openide.loaders.DataObject.find(file);
        } catch (org.openide.loaders.DataObjectNotFoundException exc) {
            dobj = null;
        }
        if (dobj != null && dobj instanceof org.openide.loaders.XMLDataObject) {
            doc = ((org.openide.loaders.XMLDataObject) dobj).createDocument();
        }
         */
        doc = org.openide.xml.XMLUtil.createDocument(VariableIO.CONFIG_ROOT_ELEM, null, null, null);
        Vector variables = this.getVariables ();
        //org.openide.nodes.Node commands = this.getCommands();
        //String label = selected;
        if (doc != null) {
            org.openide.filesystems.FileLock lock = null;
            try {
                VariableIO.writeVariables(doc, config, variables);
                CommandLineVcsAdvancedCustomizer.writeConfig(doc, this.getCommands());
                lock = file.lock();
                //VariableIO.writeVariables(doc, label, variables);
                org.openide.xml.XMLUtil.write(doc, file.getOutputStream(lock), null);
                //org.openide.loaders.XMLDataObject.write(doc, new BufferedWriter(new OutputStreamWriter(file.getOutputStream(lock))));
            //} catch (org.w3c.dom.DOMException exc) {
            //    org.openide.TopManager.getDefault().notifyException(exc);
            } catch (java.io.IOException ioexc) {
                org.openide.TopManager.getDefault().notifyException(ioexc);
            } finally {
                if (lock != null) lock.releaseLock();
            }
        }
    }

    /** Get the commands. Overide this method of VcsFileSystem to setup
     * the commands if necessary.
     * @return the root command
     */
    public Node getCommands() {
        Node commandRoot = super.getCommands();
        if (commandRoot == null) {
            loadCurrentConfig();
            setBadgeIconsFromVars();
        }
        return super.getCommands();
    }

    /** Getter for property compatibleOSs.
     * @return Value of property compatibleOSs.
     */
    public java.util.Set getCompatibleOSs() {
        return compatibleOSs;
    }
    
    /** Setter for property compatibleOSs.
     * @param compatibleOSs New value of property compatibleOSs.
     */
    public void setCompatibleOSs(java.util.Set compatibleOSs) {
        this.compatibleOSs = compatibleOSs;
        firePropertyChange(PROP_COMPATIBLE_OS, null, compatibleOSs);
    }
    
    /** Getter for property uncompatibleOSs.
     * @return Value of property uncompatibleOSs.
     */
    public java.util.Set getUncompatibleOSs() {
        return uncompatibleOSs;
    }
    
    /** Setter for property uncompatibleOSs.
     * @param uncompatibleOSs New value of property uncompatibleOSs.
     */
    public void setUncompatibleOSs(java.util.Set uncompatibleOSs) {
        this.uncompatibleOSs = uncompatibleOSs;
        firePropertyChange(PROP_UNCOMPATIBLE_OS, null, uncompatibleOSs);
    }
    
    private String createNewFSSettingsName(FileObject folderFO) {
        String settingsName;
        String baseName = getClass().getName().replace('.', '-');
        int extNum = 0;
        FileObject[] children = folderFO.getChildren();
        boolean matched;
        do {
            matched = false;
            settingsName = baseName + ((extNum == 0) ? "" : "_"+extNum);
            for (int i = 0; i < children.length; i++) {
                String name = children[i].getName();
                if (name.equals(settingsName)) {
                    matched = true;
                    break;
                }
            }
            extNum++;
        } while (matched);
        return settingsName;
    }
    
    /**
     * Create a DataObject, that have this filesystem as an InstanceCookie.
     * @param folder the folder in which the DataObject should be created
     * @return the DataObject
     */
    public DataObject createInstanceDataObject(DataFolder folder) {
        FileObject folderFO = folder.getPrimaryFile();
        String settingsName = createNewFSSettingsName(folderFO);
        FileObject fo = null;
        try {
            return CommandLineVcsFileSystemInstance.createVcsInstanceDataObject(folderFO, this, settingsName);
        } catch (IOException ioex) {
            return null;
        }
        /*
        try {
            fo = folderFO.createData(settingsName, SETTINGS_EXT);
            org.w3c.dom.Document doc = CommandLineVcsFileSystemInstance.createEmptyFSPropertiesDocument();
            try {
                CommandLineVcsFileSystemInstance.writeFSProperties(this, doc);
            } catch (org.w3c.dom.DOMException dExc) {
                TopManager.getDefault().notifyException(dExc);
            }
            FileLock lock = fo.lock();
            OutputStream out = fo.getOutputStream(lock);
            try {
                org.openide.xml.XMLUtil.write(doc, out, null);
            } finally {
                out.close();
                lock.releaseLock();
            }
            try {
                DataObject myXMLDataObject = DataObject.find(fo);
                //((CommandLineVcsFileSystemInstance) myXMLDataObject.getCookie(org.openide.cookies.InstanceCookie.Of.class)).setInstance(this);
                org.openide.util.Lookup instanceLookup = org.openide.loaders.Environment.find(myXMLDataObject);
                CommandLineVcsFileSystemInstance myInstance =
                    (CommandLineVcsFileSystemInstance) instanceLookup.lookup(org.openide.cookies.InstanceCookie.class);
                myInstance.setIgnoreSubsequentFileChange(System.currentTimeMillis());
                myInstance.setInstance(this);
                //firePropertyChange("writeAllProperties", null, null);
                //System.out.println("createInstanceDataObject() = "+myXMLDataObject);
                return myXMLDataObject;
            } catch (DataObjectNotFoundException donfExc) {}
        } catch (IOException ioExc) {
            if (fo != null) {
                try {
                    fo.delete(fo.lock());
                } catch (IOException ioExc1) {}
            }
        }
        return null;
         */
    }
    
    // Capabilities setters/getters
    
    public boolean getCapableCompile() {
        return getCapability().capableOf(FileSystemCapability.COMPILE);
    }
    
    public void setCapableCompile(boolean capCompile) {
        FileSystemCapability cap = getCapability();
        if (cap instanceof FileSystemCapability.Bean) {
            ((FileSystemCapability.Bean) cap).setCompile(capCompile);
        }
    }
    
    public boolean getCapableDebug() {
        return getCapability().capableOf(FileSystemCapability.DEBUG);
    }
    
    public void setCapableDebug(boolean capDebug) {
        FileSystemCapability cap = getCapability();
        if (cap instanceof FileSystemCapability.Bean) {
            ((FileSystemCapability.Bean) cap).setDebug(capDebug);
        }
    }
    
    public boolean getCapableDoc() {
        return getCapability().capableOf(FileSystemCapability.DOC);
    }
    
    public void setCapableDoc(boolean capDoc) {
        FileSystemCapability cap = getCapability();
        if (cap instanceof FileSystemCapability.Bean) {
            ((FileSystemCapability.Bean) cap).setDoc(capDoc);
        }
    }
    
    public boolean getCapableExecute() {
        return getCapability().capableOf(FileSystemCapability.EXECUTE);
    }
    
    public void setCapableExecute(boolean capExecute) {
        FileSystemCapability cap = getCapability();
        if (cap instanceof FileSystemCapability.Bean) {
            ((FileSystemCapability.Bean) cap).setExecute(capExecute);
        }
    }
    
    // Object IO
    
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException, NotActiveException {
        in.defaultReadObject();
        setConfigFO();
        setIgnoreListSupport(new GenericIgnoreListSupport());
        if (!isCreateBackupFilesSet()) setCreateBackupFiles(true);
        if (!isFilterBackupFilesSet()) setFilterBackupFiles(true);
        setCreateVersioningSystem(true);
        // We're deserializing the FS. There might be some necessary conversions in variable values:
        convertVarValues();
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        /*if (configFileName == null) */saveCurrentConfig();
        out.defaultWriteObject();
    }
    
    /**
     * When we're deserializing the FS, there might be some necessary conversions in variable values.
     * Perform the same converion, which is done in VariableIO.
     */
    private void convertVarValues() {
        VcsConfigVariable var = (VcsConfigVariable) variablesByName.get("WRAPPER");
        if (var != null) {
            String value = var.getValue();
            int classIndex = value.indexOf(".class");
            if (classIndex > 0) {
                int begin;
                for (begin = classIndex; begin >= 0; begin--) {
                    char c = value.charAt(begin);
                    if (!Character.isJavaIdentifierPart(c) && c != '.') break;
                }
                begin++;
                if (begin < classIndex) {
                    String classNameOrig = value.substring(begin, classIndex);
                    String classNameNew =
                        org.netbeans.modules.vcs.advanced.commands.UserCommandIO.translateExecClass(classNameOrig);
                    if (!classNameOrig.equals(classNameNew)) {
                        value = value.substring(0, begin) + classNameNew + value.substring(classIndex);
                    }
                }
            }
            var.setValue(value);
        }
    }

    private class DocCleanupRemoveItem implements Serializable {
        
        private String cmdName;
        private int order;
        private String lineBegin;
        
        static final long serialVersionUID =-1259352637936409072L;
        /**
         * Create new cleanup remove item.
         * @param cmdName The name of the command.
         * @param order the order of JTextArea in the VariableInputDialog.
         * @param lineBegin the beginning of lines which will be removed.
         */
        public DocCleanupRemoveItem(String cmdName, int order, String lineBegin) {
            this.cmdName = cmdName;
            this.order = order;
            this.lineBegin = lineBegin;
        }
        
        public String getCmdName() {
            return cmdName;
        }
        
        public int getOrder() {
            return order;
        }
        
        public String getLineBegin() {
            return lineBegin;
        }
    }

    private class GenericIgnoreListSupport extends Object implements VcsFileSystem.IgnoreListSupport {
        
        private ArrayList initialIgnoreList = null;
        
        public ArrayList createInitialIgnoreList() {
            if (initialIgnoreList == null) {
                initialIgnoreList = new ArrayList();
                VcsCommand cmd = getCommand(CMD_CREATE_INITIAL_IGNORE_LIST);
                if (cmd != null) {
                    Table files = new Table();
                    files.put ("", findFileObject(""));
                    VcsCommandExecutor[] executors = VcsAction.doCommand(
                        files, cmd, null, CommandLineVcsFileSystem.this, null, null,
                        new CommandDataOutputListener() {
                            public void outputData(String[] data) {
                                for (int i = 0; i < data.length; i++) {
                                    String element = data[i];
                                    if ("!".equals(element)) initialIgnoreList.clear();
                                    else initialIgnoreList.add(element);
                                }
                            }
                        }, null, false);
                    CommandsPool pool = getCommandsPool();
                    for (int i = 0; i < executors.length; i++) {
                        try {
                            pool.waitToFinish (executors[i]);
                        } catch (InterruptedException iexc) {}
                    }
                } else {
                    initialIgnoreList.addAll(createDefaultIgnoreList());
                }
            }
            return initialIgnoreList;
        }
        
        public ArrayList createIgnoreList(String fileName, ArrayList parentIgnoreList) {
            VcsCommand cmd = getCommand(CMD_CREATE_FOLDER_IGNORE_LIST);
            if (cmd == null) {
                return parentIgnoreList;
            }
            String[] parentIgnoreListItems = (String[]) parentIgnoreList.toArray(new String[0]);
            Hashtable additionalVars = new Hashtable();
            additionalVars.put(VAR_PARENT_IGNORE_LIST, VcsUtilities.arrayToQuotedStrings(parentIgnoreListItems));
            final ArrayList ignoreList = new ArrayList();
            Table files = new Table();
            files.put(fileName, findResource(fileName));
            VcsCommandExecutor[] executors = VcsAction.doCommand(
                files, cmd, additionalVars, CommandLineVcsFileSystem.this, null, null,
                new CommandDataOutputListener() {
                    public void outputData(String[] data) {
                        for (int i = 0; i < data.length; i++) {
                            String element = data[i];
                            if ("!".equals(element)) ignoreList.clear();
                            else ignoreList.add(element);
                        }
                    }
                }, null, false);
            CommandsPool pool = getCommandsPool();
            for (int i = 0; i < executors.length; i++) {
                try {
                    pool.waitToFinish(executors[i]);
                } catch (InterruptedException iexc) {}
            }
            return ignoreList;
        }
        
        private final String[] DEFAULT_IGNORE_FILES = { ".#*", "*~" };
        
        private final java.util.List createDefaultIgnoreList() {
            java.util.List ignoreList = Arrays.asList(DEFAULT_IGNORE_FILES);
            return ignoreList;
        }
        
    }
    
    private class SharedPasswordListener extends Object implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (sharedPasswordKey != null && sharedPasswordKey.equals(propertyChangeEvent.getSource())) {
                setPassword((String) propertyChangeEvent.getNewValue());
            }
        }
        
    }
    
    private final static class SharedPasswords extends Object {
        
        private HashMap passwords;
        private HashSet listeners;
        private static SharedPasswords instance;
        
        private SharedPasswords() {
            passwords = new HashMap();
            listeners = new HashSet();
        }
        
        public static SharedPasswords getInstance() {
            synchronized (SharedPasswords.class) {
                if (instance == null) {
                    instance = new SharedPasswords();
                }
            }
            return instance;
        }
        
        public synchronized void setPassword(Object key, String password) {
            if (password == null) {
                Object old = passwords.remove(key);
                firePropertyChange(key, old, null);
            } else {
                passwords.put(key, password);
                firePropertyChange(key, null, password);
            }
        }
        
        public synchronized String getPassword(Object key) {
            return (String) passwords.get(key);
        }
        
        public synchronized void changeKey(Object oldKey, Object newKey) {
            Object value = passwords.remove(oldKey);
            if (value != null) passwords.put(newKey, value);
        }
        
        private void firePropertyChange(Object key, Object oldPass, Object newPass) {
            PropertyChangeEvent event = new PropertyChangeEvent(key, "password", oldPass, newPass);
            HashSet l;
            synchronized (listeners) {
                l = new HashSet(listeners);
            }
            for (Iterator it = l.iterator(); it.hasNext(); ) {
                ((PropertyChangeListener) it.next()).propertyChange(event);
            }
        }
        
        public void addPropertyChangeListener(PropertyChangeListener l) {
            synchronized (listeners) {
                listeners.add(l);
            }
        }
        
        public void removePropertyChangeListener(PropertyChangeListener l) {
            synchronized (listeners) {
                listeners.remove(l);
            }
        }
    }

    private String clg(String s) {
        //D.deb("getting "+s);
        if (resourceBundle == null) {
            synchronized (this) {
                if (resourceBundle == null) {
                    resourceBundle = NbBundle.getBundle(CommandLineVcsFileSystem.class);
                }
            }
        }
        return resourceBundle.getString (s);
    }
    
    private String clg(String s, Object obj) {
        return MessageFormat.format (clg(s), new Object[] { obj });
    }
    
}

