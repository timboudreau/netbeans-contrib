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
import java.util.*;
import java.beans.*;
import java.text.*;
import javax.swing.*;

import org.openide.*;
import org.openide.util.actions.*;
import org.openide.util.NbBundle;
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
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.SharedClassObject;
import org.openide.util.WeakListener;

import org.netbeans.modules.vcscore.cache.CacheHandlerListener;
import org.netbeans.modules.vcscore.cache.CacheHandlerEvent;
import org.netbeans.modules.vcscore.caching.*;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.search.VcsSearchTypeFileSystem;
import org.netbeans.modules.vcscore.settings.VcsSettings;
import org.netbeans.modules.vcscore.revision.RevisionListener;

/** Generic VCS filesystem.
 * 
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public abstract class VcsFileSystem extends AbstractFileSystem implements VariableInputDialog.FilePromptDocumentListener,
                                                                          VcsSearchTypeFileSystem,
                                                                          AbstractFileSystem.List, AbstractFileSystem.Info,
                                                                          AbstractFileSystem.Change, FileSystem.Status,
                                                                          CacheHandlerListener, Serializable {
    private Debug E=new Debug("VcsFileSystem", false); // NOI18N
    private Debug D=E;

    public static final String PROP_ROOT = "root"; // NOI18N
    public static final String PROP_VARIABLES = "variables"; // NOI18N
    public static final String PROP_COMMANDS = "commands"; // NOI18N
    
    private static ResourceBundle resourceBundle = null;
    
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
     * This variable can contain the display name, which is prepended to the
     * root path of the file system to create the display name.
     */
    public static final String VAR_FS_DISPLAY_NAME = "FS_DISPLAY_NAME"; // NOI18N

    protected static final int REFRESH_TIME = 15000; // This is default in LocalFileSystem
    protected volatile int refreshTimeToSet = REFRESH_TIME;

    private static final String LOCAL_FILES_ADD_VAR = "SHOWLOCALFILES"; // NOI18N
    private static final String LOCK_FILES_ON = "LOCKFILES"; // NOI18N
    private static final String PROMPT_FOR_LOCK_ON = "PROMPTFORLOCK"; // NOI18N

    private static final String DEFAULT_QUOTING_VALUE = "\\\\\""; // NOI18N

    private static final String DEFAULT_CACHE_ID = "VCS_Cache"; // NOI18N
    /**
     * The name of the variable for which we get user input.
     */
    private static final String PROMPT_FOR = "PROMPT_FOR"; // NOI18N
    /**
     * The name of the variable for which we get user to set true or false.
     */
    private static final String ASK_FOR = "ASK_FOR"; // NOI18N
    /**
     * The name of the variable for which we get user file input.
     */
    private static final String PROMPT_FOR_FILE_CONTENT = "PROMPT_FOR_FILE_CONTENT"; // NOI18N
    private static final String FILE_PROMPT_PREFIX = "tmppf"; // NOI18N
    /**
     * The name of the variable for the global additional parameters.
     */
    private static final String USER_GLOBAL_PARAM = "USER_GLOBAL_PARAM";
    /**
     * The name of the variable for the local additional parameters.
     */
    private static final String USER_PARAM = "USER_PARAM";
    
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
    private volatile boolean promptForLockResult = false;
    private boolean callEditFilesOn = true;

    private boolean debug = false;

    /** user variables Vector<String> 'name=value' */
    private Vector variables = new Vector(10);

    private transient String password = null;

    /** advanced confgiguration */
    //private Object advanced = null; // Not used any more, use commandsRoot instead
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

    private transient Vector unimportantNames = null;
    private Boolean processUnimportantFiles = Boolean.FALSE;

    /**
     * Table used to transfer status name obtained by refresh to the name presented to the user.
     * Can be used to make localization of file statuses.
     */
    protected HashMap possibleFileStatusesMap = null;

    protected boolean ready=false;
    private boolean askIfDownloadRecursively = true;
    private volatile Hashtable numDoAutoRefreshes = new Hashtable();
    
    private transient boolean deserialized; // Whether this class was created by deserialization

    /**
     * Whether to prompt the user for variables for each selected file. Value of this variable
     * willl be the default value in the VariableInputDialog and changing the value there will
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
    private int numOfFinishedCmdsToCollect = CommandsPool.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT;
    
    private ArrayList revisionListeners;

    /** The offline mode.
     * Whether to run command when doing refresh of folders.
     * Recommended to turn this property on when working off-line.
     */
    private volatile boolean offLine; // is set in the constructor
    private volatile int autoRefresh; // is set in the constructor

    private Collection notModifiableStatuses = Collections.EMPTY_SET;

    public boolean isLockFilesOn () { return lockFilesOn; }
    public void setLockFilesOn (boolean lock) { lockFilesOn = lock; }
    public boolean isPromptForLockOn () { return promptForLockOn; }
    public void setPromptForLockOn (boolean prompt) { promptForLockOn = prompt; }
    public boolean getAskIfDownloadRecursively () { return askIfDownloadRecursively; }
    public void setAskIfDownloadRecursively (boolean ask) { askIfDownloadRecursively = ask; }
    public boolean isCallEditFilesOn() { return callEditFilesOn; }
    public void setCallEditFilesOn(boolean edit) { callEditFilesOn = edit; }
    public boolean isUseUnixShell () { return useUnixShell; }

    protected void setUseUnixShell (boolean unixShell) {
        useUnixShell = unixShell;
        last_useUnixShell = unixShell;
    }
    
    /*
    public void setDoCommandRefresh(boolean doCommandRefresh) {
        this.doCommandRefresh = doCommandRefresh;
    }
    
    public boolean isDoCommandRefresh() {
        return this.doCommandRefresh;
    }
     */
    
    public void setAcceptUserParams(boolean acceptUserParams) {
        this.acceptUserParams = acceptUserParams;
    }
    
    public boolean isAcceptUserParams() {
        return acceptUserParams;
    }

    public void setUserLocalParamsLabels(String[] labels) {
        userLocalParamsLabels = labels;
    }
    
    public void setUserParamsLabels(String[] labels) {
        userParamsLabels = labels;
        userParams = new String[labels.length];
    }
    
    public String[] getUserParamsLabels() {
        return userParamsLabels;
    }
    
    public String[] getUserParams() {
        return userParams;
    }
    
    public void setExpertMode(boolean expertMode) {
        this.expertMode = expertMode;
    }
    
    public boolean getExpertMode() {
        return expertMode;
    }

    public CommandsPool getCommandsPool() {
        return commandsPool;
    }
    
    public void setProcessUnimportantFiles(boolean processUnimportantFiles) {
        synchronized (this.processUnimportantFiles) {
            this.processUnimportantFiles = new Boolean(processUnimportantFiles);
        }
    }
    
    public boolean isProcessUnimportantFiles() {
        synchronized (processUnimportantFiles) {
            return processUnimportantFiles.booleanValue();
        }
    }
    
    public void setOffLine(boolean offLine) {
        if (offLine != this.offLine) {
            this.offLine = offLine;
            firePropertyChange (VcsSettings.PROP_OFFLINE, new Boolean(!offLine), new Boolean(offLine));
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
            firePropertyChange (VcsSettings.PROP_AUTO_REFRESH, new Integer(oldAuto), new Integer(autoRefresh));
        }
    }
    
    public void addRevisionListener(RevisionListener listener) {
        if (revisionListeners == null) revisionListeners = new ArrayList();
        revisionListeners.add(listener);
    }
    
    public boolean removeRevisionListener(RevisionListener listener) {
        if (revisionListeners == null) return false;
        return revisionListeners.remove(listener);
    }

    public void fireRevisionsChanged(int whatChanged, FileObject fo, Object info) {
        if (revisionListeners == null) return;
        for(Iterator it = revisionListeners.iterator(); it.hasNext(); ) {
            ((RevisionListener) it.next()).revisionsChanged(whatChanged, fo, info);
        }
    }
    
    protected void setNotModifiableStatuses(Collection notModifiableStatuses) {
        this.notModifiableStatuses = notModifiableStatuses;
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

    /*
    public boolean getLastCommandState () { return lastCommandState; }
    public void setLastCommandState (boolean lastCommandState) { this.lastCommandState = lastCommandState; }
    public boolean getLastCommandFinished () { return lastCommandFinished; }
    public void setLastCommandFinished (boolean lastCommandFinished) { this.lastCommandFinished = lastCommandFinished; }
     */
    
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
            /*
            E.err(io,"setRootDirectory() failed"); // NOI18N
            final String badDir = root.toString();
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
                                                       public void run () {
                                                           if (isRootNotSetDlg) {
                                                               isRootNotSetDlg = false;
                                                               TopManager.getDefault ().notify (new NotifyDescriptor.Message(MessageFormat.format (org.openide.util.NbBundle.getBundle(CvsCustomizer.class).getString("CvsCustomizer.cannotSetDirectory"), new Object[] { badDir } )));
                                                               isRootNotSetDlg = true;
                                                           }
                                                       }
                                                   });
            */
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
        D.deb("==== unimportant("+name+") ====");
        if (!unimportantNames.contains(name)) unimportantNames.addElement(name);
    }

    public boolean isImportant(String name) {
        //D.deb("isImportant("+name+")");
        //D.deb("unimportantNames = "+unimportantNames);
        //D.deb("contains() = "+unimportantNames.contains(name));;
        return !unimportantNames.contains(name);
    }

    /**
     * Perform refresh of status information on all children of a directory
     * @param path the directory path
     * @param recursivey whether to refresh recursively
     */
    public void statusChanged (String path, boolean recursively) {
        //D.deb("statusChanged("+path+")"); // NOI18N
        FileObject fo = findResource(path);
        if (fo == null) return;
        //D.deb("I have root = "+fo.getName()); // NOI18N
        Enumeration enum = fo.getChildren(recursively);
        HashSet hs = new HashSet();
        while(enum.hasMoreElements()) {
            fo = (FileObject) enum.nextElement();
            hs.add(fo);
            //D.deb("Added "+fo.getName()+" fileObject to update status"+fo.getName()); // NOI18N
        }
        Set s = Collections.synchronizedSet(hs);
        fireFileStatusChanged (new FileStatusEvent(this, s, false, true));
    }

    /**
     * Perform refresh of status information of a file
     * @param name the full file name
     */
    public void statusChanged (String name) {
        FileObject fo = findResource(name);
        if (fo == null) return;
        //HashSet hs = new HashSet();
        //hs.add(fo);
        //Set s = Collections.synchronizedSet(hs);
        fireFileStatusChanged (new FileStatusEvent(this, fo, false, true));
    }
    
    public void disableRefresh() {
        synchronized (this) {
            refreshTimeToSet = getRefreshTime();
            setRefreshTime(0);
        }
    }
    
    public void enableRefresh() {
        synchronized (this) {
            setRefreshTime(refreshTimeToSet);
        }
    }
    
    public void setRefreshTimeToSet() {
        setRefreshTime(refreshTimeToSet);
    }

    public void setCustomRefreshTime (int time) {
        if (isValid ()) {
            D.deb("Filesystem valid, setting the refresh time to "+time); // NOI18N
            setRefreshTime (time);
        } else {
            D.deb("Filesystem not valid yet for refresh time "+time); // NOI18N
            refreshTimeToSet = time;
        }
        last_refreshTime = time;
    }

    public int getCustomRefreshTime () {
        if (isValid ()) {
            D.deb("Filesystem valid, getting the refresh time "+getRefreshTime ()); // NOI18N
            return getRefreshTime ();
        } else return refreshTimeToSet;
    }
    
    public void setZeroRefreshTime() {
        setRefreshTime(0);
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


    /*
    //-------------------------------------------
    public void setImportant(boolean important){
        //D.deb("setImportant("+important+")"); // NOI18N
    }
    */


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
        if (cacheID == null) {
            synchronized (this) {
                if (cacheID == null) {
                    cacheID = initCacheIdStr();
                }
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
        if (unimportantNames == null) unimportantNames = new Vector();
        if (tempFiles == null) tempFiles = new Vector();
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
        commandsPool = new CommandsPool(this);
        commandsPool.setCollectFinishedCmdsNum(numOfFinishedCmdsToCollect);
        initListeners();
    }

    private void initListeners() {
        VcsSettings settings = (VcsSettings) SharedClassObject.findObject(VcsSettings.class, true);
        settings.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) { 
                settingsChanged(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            }
        });
        RepositoryListener wl = /*WeakListener.repository (*/new RepositoryListener() {
            public void fileSystemAdded(RepositoryEvent ev) {
                //System.out.println("fileSystemAdded("+ev.getFileSystem());
                //System.out.println("isOffLine() = "+isOffLine()+", auto refresh = "+getAutoRefresh()+", deserialized = "+deserialized);
                if (ev.getFileSystem().equals(VcsFileSystem.this)
                    && !isOffLine()
                    && (deserialized && getAutoRefresh() == VcsSettings.AUTO_REFRESH_ON_RESTART)
                        || (!deserialized && getAutoRefresh() == VcsSettings.AUTO_REFRESH_ON_MOUNT)) {
                    CommandExecutorSupport.doRefresh(VcsFileSystem.this, "", true);
                    //FileCacheProvider cache = getCacheProvider();
                    //if (cache != null) cache.refreshCacheDirRecursive("");
                    //System.out.println("refresh called.");
                }
            }
            public void fileSystemPoolReordered(RepositoryReorderedEvent ev) {
            }
            public void fileSystemRemoved(RepositoryEvent ev) {
                if (ev.getFileSystem().equals(VcsFileSystem.this)) {
                    //System.out.println("FS "+VcsFileSystem.this+" removed");
                    TopManager.getDefault ().getRepository ().removeRepositoryListener (this);
                }
            }
        };//, null);
        TopManager.getDefault ().getRepository ().addRepositoryListener (wl);
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
        DefaultAttributes a = new DefaultAttributes (info, change, this);
        attr = a;
        list = a;
        setRefreshTime (0); // due to customization
        refreshTimeToSet = last_refreshTime;
        /*
        cacheRoot = System.getProperty("netbeans.user")+File.separator+
                    "system"+File.separator+"vcs"+File.separator+"cache"; // NOI18N
         */
        VcsSettings settings = (VcsSettings) SharedClassObject.findObject(VcsSettings.class, true);
        setOffLine(settings.isOffLine());
        setAutoRefresh(settings.getAutoRefresh());
        init();
        //possibleFileStatusesMap = statusProvider.getPossibleFileStatusesTable();
        D.deb("constructor done.");
    }

    public String[] getPossibleFileStatuses() {
        String[] statuses;
        synchronized (possibleFileStatusesMap) {
            statuses = new String[possibleFileStatusesMap.size()];
            int i = 0;
            for(Iterator it = possibleFileStatusesMap.keySet().iterator(); it.hasNext(); i++) {
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
        synchronized (possibleFileStatusesMap) {
            statusesTable = new HashMap(possibleFileStatusesMap);
        }
        return statusesTable;
    }

    /*
    public ErrorCommandDialog getErrorDialog() {
        return errorDialog;
    }

    public void setErrorDialog(ErrorCommandDialog errDlg) {
        errorDialog = errDlg;
    }
     */

    //-------------------------------------------
    //public long getCacheId(){
    //    return cacheId;
    //}

    //-------------------------------------------
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException, NotActiveException {
        // cache is transient
        //System.out.println("VcsFileSystem.readObject() ...");
        //try {
            //ObjectInputStream din = (ObjectInputStream) in;
        deserialized = true;
        boolean localFilesOn = in.readBoolean ();
        in.defaultReadObject();
        //last_rootFile = rootFile;
        last_refreshTime = getCustomRefreshTime ();
        last_useUnixShell = useUnixShell;
        init();
        //cache.setLocalFilesAdd (localFilesOn);
        ProjectChangeHack.restored();
        if (null == processUnimportantFiles) processUnimportantFiles = Boolean.FALSE;
        last_rootFile = new File(getFSRoot());
        //} catch (Throwable thr) {
        //    System.out.println("VcsFileSystem.readObject():");
        //    thr.printStackTrace();
        //}
    }


    //-------------------------------------------
    private void writeObject(ObjectOutputStream out) throws IOException {
        //D.deb("writeObject() - saving bean"); // NOI18N
        // cache is transient
        numOfFinishedCmdsToCollect = commandsPool.getCollectFinishedCmdsNum();
        out.writeBoolean (true/*cache.isLocalFilesAdd ()*/); // for compatibility
        out.defaultWriteObject();
    }


    //-------------------------------------------
    public void setDebug(boolean debug){
        this.debug=debug;
    }


    //-------------------------------------------
    public boolean getDebug(){
        return debug;
    }


    //-------------------------------------------
    public Vector getVariables(){
        return variables;
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
        boolean modDef = false;
        for (int i = 0, n = variables.size (); i < n; i++) {
            var = (VcsConfigVariable) variables.get (i);
            newVarsByName.put (var.getName (), var);
            if (var.getName().equals("MODULE")) {
                modDef = true;
            }
        }
        if (!modDef && mod != null) { // The module was previosly defined, it has to be copied to new variables.
            this.variables.add(mod);
            newVarsByName.put (mod.getName (), mod);
        }
        synchronized (this) {
            variablesByName = new Hashtable(newVarsByName);
        }

        firePropertyChange(PROP_VARIABLES, old, variables);
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
        this.password = password;
    }

    //-------------------------------------------
    public String getPassword(){
        return password;
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
     * Find out what to prompt for the user for before running the command.
     * @param exec The command to exec
     * @param vars The variables to use
     * @return The table of variable labels for the user to input as keys and prompt type as values
     */
    private Table needPromptFor(String exec, Hashtable vars) {
        Table results = new Table();
        String search = "${"+PROMPT_FOR; //+"(";
        int pos = 0;
        int index;
        while((index = exec.indexOf(search, pos)) >= 0) {
            index += search.length();
            int parIndex = exec.indexOf("(", index);
            if (parIndex < 0) break;
            String promptType = exec.substring(index, parIndex);
            String promptIdentifier = exec.substring(index - PROMPT_FOR.length(), parIndex);
            if (PROMPT_FOR_FILE_CONTENT.equals(promptIdentifier)) {
                pos = parIndex;
                continue;
            }
            index = parIndex + 1;
            int index2 = exec.indexOf(")", index);
            if (index2 < 0) break;
            String str = exec.substring(index, index2);
            results.put(str, promptType);
            //results.addElement(str);
            pos = index2;
        }
        //return (String[]) results.toArray(new String[0]);
        return results;
    }

    /**
     * Find out what to ask the user for before running the command.
     * @param exec The command to exec
     * @param vars The variables to use
     * @return The table of questions for the user as keys and prompt type as values
     */
    private Table needAskFor(String exec, Hashtable vars) {
        Table results = new Table();
        HashSet labels = new HashSet();
        String search = /*"${"+*/ASK_FOR; //+"("; // to be able to put this to conditional expression
        int pos = 0;
        int index;
        while((index = exec.indexOf(search, pos)) >= 0) {
            index += search.length();
            int parIndex = exec.indexOf("(", index);
            if (parIndex < 0) break;
            String promptType = exec.substring(index, parIndex);
            index = parIndex + 1;
            int index2 = exec.indexOf(")", index);
            if (index2 < 0) break;
            String str = exec.substring(index, index2);
            if (!labels.contains(str)) { // Do not add one label more than ones.
                labels.add(str);
                results.put(str, promptType);
            }
            pos = index2;
        }
        /*
        // Assure, that the items are unique. I don't wanna use set 'cause don't wanna alter the order.
        for (int i = 0; i < results.size(); i++) {
            if (results.indexOf(results.get(i)) < i) results.remove(i);
        }
        return (String[]) results.toArray(new String[0]);
         */
        return results;
    }

    /**
     * Find out what to prompt the user for to write a content of a temporary file.
     * @param exec The command to exec
     * @param vars The variables to use
     * @return the table of questions and files to read the content from
     */
    private Hashtable needPrompForFileContent(String exec, Hashtable vars, Hashtable varNames) {
        D.deb("needPrompForFileContent("+exec+")");
        Hashtable results = new Hashtable();
        String search = "${"+PROMPT_FOR_FILE_CONTENT+"(";
        int pos = 0;
        int index;
        while((index = exec.indexOf(search, pos)) >= 0) {
            int varBegin = index + 2;
            index += search.length();
            int index2 = exec.indexOf(",", index);
            int index3 = exec.indexOf(")}", index);
            if (index3 < 0) break;
            if (index2 < 0 || index2 > index3) index2 = index3;
            String message = exec.substring(index, index2);
            message = Variables.expandFast(vars, message, true);
            pos = index2 + 1;
            String fileName = "";
            if (pos < index3) {
                fileName = exec.substring(pos, index3).trim();
                fileName = Variables.expand(vars, fileName, true);
                pos = index3;
            }
            D.deb("needPrompForFileContent(): message = "+message+", fileName = "+fileName);
            results.put(message, fileName);
            varNames.put(exec.substring(varBegin, index3 + 1), message);
            D.deb("needPrompForFileContent() varName = "+exec.substring(varBegin, index3 + 1)+", for message = "+message);
        }
        return results;
    }

    /**
     * Find out which additional user parameters prompt the use for.
     * @return The table of parameter labels for the user to input, one for each parameter
     *         and default values.
     */
    private Table needPromptForUserParams(String exec, Hashtable vars, Hashtable varNames, Hashtable userParamsIndexes, VcsCommand cmd) {
        Table results = new Table();
        String search = "${"+USER_GLOBAL_PARAM;
        int pos = 0;
        int index;
        while((index = exec.indexOf(search, pos)) >= 0) {
            int varBegin = index + 2;
            index += search.length();
            char cnum = exec.charAt(index);
            int num = 1;
            if (Character.isDigit(cnum)) {
                num = Character.digit(cnum, 10);
                index++;
            }
            num--;
            int varEnd = VcsUtilities.getPairIndex(exec, index, '{', '}');
            if (varEnd < 0) {
                pos = index; //TODO: wrong command syntax: '}' is missing
                continue;
            }
            String varName = exec.substring(varBegin, varEnd);
            String defaultParam = "";
            if (exec.charAt(index) == '(') {
                index++;
                int index2 = VcsUtilities.getPairIndex(exec, index, '(', ')');
                if (index2 > 0) defaultParam = exec.substring(index, index2);
            }
            if (acceptUserParams && userParamsLabels != null) {
                if (num >= userParamsLabels.length) num = userParamsLabels.length - 1;
                if (userParams[num] != null) defaultParam = userParams[num];
                results.put(userParamsLabels[num], defaultParam);
                varNames.put(varName, userParamsLabels[num]);
                userParamsIndexes.put(varName, new Integer(num));
            } else {
                vars.put(varName, defaultParam);
            }
            pos = varEnd;
        }
        search = "${"+USER_PARAM;
        pos = 0;
        while((index = exec.indexOf(search, pos)) >= 0) {
            int varBegin = index + 2;
            index += search.length();
            char cnum = exec.charAt(index);
            int num = 1;
            if (Character.isDigit(cnum)) {
                num = Character.digit(cnum, 10);
                index++;
            }
            num--;
            int varEnd = VcsUtilities.getPairIndex(exec, index, '{', '}');
            if (varEnd < 0) {
                pos = index; //TODO: wrong command syntax: '}' is missing
                continue;
            }
            String varName = exec.substring(varBegin, varEnd);
            String defaultParam = "";
            if (exec.charAt(index) == '(') {
                index++;
                int index2 = VcsUtilities.getPairIndex(exec, index, '(', ')');
                if (index2 > 0) defaultParam = exec.substring(index, index2);
            }
            if (acceptUserParams && userLocalParamsLabels != null) {
                String[] cmdUserParams = (String[]) cmd.getProperty(VcsCommand.PROPERTY_USER_PARAMS);
                if (cmdUserParams == null) cmdUserParams = new String[userLocalParamsLabels.length];
                if (num >= userLocalParamsLabels.length) num = userLocalParamsLabels.length - 1;
                if (cmdUserParams[num] != null) defaultParam = cmdUserParams[num];
                results.put(userLocalParamsLabels[num], defaultParam);
                varNames.put(varName, userLocalParamsLabels[num]);
                userParamsIndexes.put(varName, new Integer(-num - 1));
            } else {
                vars.put(varName, defaultParam);
            }
            pos = varEnd;
        }
        return results;
    }
    
    //-------------------------------------------
    private boolean needPromptForPR(String name, String exec, Hashtable vars){
        //D.deb("needPromptFor('"+name+"','"+exec+"')"); // NOI18N
        boolean result=false;
        String oldPassword=(String)vars.get("PASSWORD"); vars.put("PASSWORD",""); // NOI18N
        String oldReason=(String)vars.get("REASON"); vars.put("REASON",""); // NOI18N

        String test="variable_must_be_prompt_for"; // NOI18N
        vars.put(name,test);
        String s = Variables.expand(vars, exec, false);
        result = (s.indexOf(test) >= 0) ? true : false ;

        if (oldPassword != null) { vars.put("PASSWORD", oldPassword); } // NOI18N
        if (oldReason != null) { vars.put("REASON", oldReason); } // NOI18N

        return result ;
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
    

    /**
     * Ask the user for the value of some variables.
     * @param exec the updated exec of the command to execute
     * @param vars the variables
     * @param cmd the command
     * @param forEachFile whether to ask for these variables for each file being processed
     * @return true if all variables were entered, false otherways
     */
    public boolean promptForVariables(String exec, Hashtable vars, VcsCommand cmd, boolean[] forEachFile) {
        synchronized (vars) {
        if (needPromptForPR("PASSWORD", exec, vars)) { // NOI18N
            String password = getPassword();
            if (password == null) {
                password = ""; // NOI18N
                NotifyDescriptorInputPassword nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("MSG_Password")); // NOI18N
                if (NotifyDescriptor.OK_OPTION.equals (TopManager.getDefault ().notify (nd))) {
                    password = nd.getInputText ();
                } else {
                    return false;
                }
                setPassword(password);
            }
            vars.put("PASSWORD", password); // NOI18N
            /* Do not change forEachFile, if the command is successful it will not ask any more */
        }
        if (forEachFile == null || forEachFile[0] == true) {
            boolean reasonPrompt = needPromptForPR("REASON", exec, vars);
            String reason=""; // NOI18N
            String file = (String) vars.get("FILE"); // NOI18N
            String path = (String) vars.get("DIR"); // NOI18N
            String filePath = (path.length() == 0) ? file : path.replace(((String) vars.get("PS")).charAt(0), '/')+"/"+file;
            if (filePath != null && cache != null && cache.isDir(filePath)) file = file + java.io.File.separator;
            Table prompt = needPromptFor(exec, vars);
            if (reasonPrompt) {
                if (prompt != null) {
                    prompt.putFirst(new String(g("MSG_Reason")), "");
                } else {
                    prompt = new Table();
                    prompt.put(new String(g("MSG_Reason")), "");
                }
            }
            exec = Variables.expandKnownOnly(vars, exec);
            Table ask = needAskFor(exec, vars);
            Hashtable varNames = new Hashtable(); // Variable names of prompt for file variables with message names
            Hashtable promptFile = needPrompForFileContent(exec, vars, varNames);
            Hashtable userParamsVarNames = new Hashtable(); // Variable names of prompt for additional parameters
            Hashtable userParamsIndexes = new Hashtable();
            Table userParamsPromptLabels = needPromptForUserParams(exec, vars, userParamsVarNames, userParamsIndexes, cmd);
            createTempPromptFiles(promptFile);
            if (prompt != null && prompt.size() > 0 || ask != null && ask.size() > 0 ||
                promptFile.size() > 0 || userParamsPromptLabels.size() > 0) {
                    
                VariableInputDialog dlg = new VariableInputDialog(/*new java.awt.Frame(), true, */new String[] { file });
                dlg.setVCSFileSystem(this, vars);
                //dlg.setCommandDisplayName(cmd.getDisplayName());
                dlg.setFilePromptLabels(promptFile);
                dlg.setVarPromptLabels(prompt);
                dlg.setVarAskLabels(ask);
                dlg.setUserParamsPromptLabels(userParamsPromptLabels, (String) cmd.getProperty(VcsCommand.PROPERTY_ADVANCED_NAME));
                if (promptFile.size() > 0) dlg.setFilePromptDocumentListener(this, cmd);
                if (forEachFile == null) dlg.showPromptEach(false);
                else dlg.setPromptEach(promptForVarsForEachFile);
                String title = java.text.MessageFormat.format(
                    org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.titleWithName"),
                    new Object[] { cmd.getDisplayName() }
                );
                Dialog dialog = TopManager.getDefault().createDialog(new DialogDescriptor(dlg, title, true, dlg.getActionListener()));
                dialog.show();
                if (dlg.isValidInput()) {
                //if (dlg.showDialog()) {
                    String[] values = dlg.getVarPromptValues();
                    int first = 0;
                    Enumeration promptLabels = prompt.keys();
                    if (reasonPrompt) {
                        reason = values[0];
                        first++;
                        if (promptLabels.hasMoreElements()) promptLabels.nextElement(); // throw the first element if the reason was it.
                    }
                    for(int i = first; promptLabels.hasMoreElements(); i++) {
                        String label = (String) promptLabels.nextElement();
                        vars.put(PROMPT_FOR+prompt.get(label)+"("+label+")", VcsUtilities.msg2CmdlineStr(values[i], isUseUnixShell()));
                    }
                    values = dlg.getVarAskValues();
                    Enumeration askLabels = ask.keys();
                    for(int i = 0; askLabels.hasMoreElements(); i++) {
                        String label = (String) askLabels.nextElement();
                        vars.put(ASK_FOR+ask.get(label)+"("+label+")", values[i]);
                    }
                    for (Enumeration enum = varNames.keys(); enum.hasMoreElements(); ) {
                        String varName = (String) enum.nextElement();
                        vars.put(varName, promptFile.get(varNames.get(varName)));
                        D.deb("put("+varName+", "+promptFile.get(varNames.get(varName))+")");
                    }
                    Hashtable valuesTable = dlg.getUserParamsValuesTable();
                    for (Enumeration enum = userParamsVarNames.keys(); enum.hasMoreElements(); ) {
                        String varName = (String) enum.nextElement();
                        //System.out.println("varName = "+varName+", label = "+userParamsVarNames.get(varName));
                        String value = (String) valuesTable.get(userParamsVarNames.get(varName));
                        vars.put(varName, value);
                        int index = ((Integer) userParamsIndexes.get(varName)).intValue();
                        if (index >= 0) userParams[index] = value;
                        else {
                            String[] cmdUserParams = (String[]) cmd.getProperty(VcsCommand.PROPERTY_USER_PARAMS);
                            cmdUserParams[-index - 1] = value;
                            cmd.setProperty(VcsCommand.PROPERTY_USER_PARAMS, cmdUserParams);
                        }
                        D.deb("put("+varName+", "+valuesTable.get(userParamsVarNames.get(varName))+")");
                    }
                    if (forEachFile != null) {
                        forEachFile[0] = dlg.getPromptForEachFile();
                        promptForVarsForEachFile = forEachFile[0];
                    }
                } else return false;
                if (reasonPrompt) vars.put("REASON", VcsUtilities.msg2CmdlineStr(reason, isUseUnixShell())); // NOI18N
            }
        }
        return true;
        }
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
        this.annotationPattern = annotationPattern;
    }
    
    public int[] getMultiFileAnnotationTypes() {
        return multiFilesAnnotationTypes;
    }
    
    public void setMultiFileAnnotationTypes(int[] multiFileAnnotationTypes) {
        if (multiFilesAnnotationTypes.length != RefreshCommandSupport.NUM_ELEMENTS) {
            throw new IllegalArgumentException("Wrong length of the array ("+multiFileAnnotationTypes.length+" != "+RefreshCommandSupport.NUM_ELEMENTS+")");
        }
        this.multiFilesAnnotationTypes = multiFilesAnnotationTypes;
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

    //-------------------------------------------
    public Image annotateIcon(Image icon, int iconType, Set files) {
        //D.deb("annotateIcon()"); // NOI18N
        return icon;
    }


    /** Annotate a single file.
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
        String result = name;

        Object[] oo = files.toArray();
        int len = oo.length;
        if (len == 0 || name.indexOf(getRootDirectory().toString()) >= 0) {
            return result;
        }

        if (statusProvider != null) {
            if (len == 1) {
                FileObject ff = (FileObject) oo[0];
                String fullName = ff.getPackageNameExt('/','.');
                result = RefreshCommandSupport.getStatusAnnotation(name, fullName, annotationPattern, statusProvider);
            } else {
                ArrayList importantFiles = getImportantFiles(oo);
                result = RefreshCommandSupport.getStatusAnnotation(name, importantFiles, annotationPattern, statusProvider, multiFilesAnnotationTypes);
            }
        }
        //System.out.println("annotateName("+name+") -> result='"+result+"'");
        //D.deb("annotateName("+name+") -> result='"+result+"'"); // NOI18N
        return result;
    }


    /**
     * Get the important files.
     * @return the Vector of important files as Strings
     */
    private ArrayList/*VcsFile*/ getImportantFiles(Object[] oo){
        //D.deb("getImportantFiles()"); // NOI18N
        ArrayList result = new ArrayList();
        int len=oo.length;

        boolean processAll = isProcessUnimportantFiles();
        for(int i = 0; i < len; i++) {
            FileObject ff = (FileObject) oo[i];
            String fullName = ff.getPackageNameExt('/','.');
            if (processAll || isImportant(fullName)) {
                result.add(fullName);
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
        //System.out.println("VcsFileSystem.getDisplayName(): commandsRoot = "+commandsRoot);
        VcsConfigVariable preDisplayNameVar = (VcsConfigVariable) variablesByName.get(VAR_FS_DISPLAY_NAME);
        if (preDisplayNameVar != null) {
            return preDisplayNameVar.getValue() + " " + rootFile.toString();
        } else if (commandsRoot != null) {
            String VCSName = commandsRoot.getDisplayName();
            //System.out.println("VCSName = '"+VCSName+"'");
            if (VCSName != null && VCSName.length() > 0) {
                return VCSName + " " + rootFile.toString();
            }
        }
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
            int i = root.indexOf(module);
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
        //D.deb("setRootDirectory("+r+")"); // NOI18N
        if (/*!r.exists() ||*/ r.isFile ()) {
            throw new IOException(g("EXC_RootNotExist", r.toString ())); // NOI18N
        }

        Hashtable vars = getVariablesAsHashtable();
        String module = (String) vars.get("MODULE");
        if (module == null) module = "";
        File root = new File(r, module);
        String name = computeSystemName (root);
        /* Ignoring other filesystems' names => it is possible to mount VCS filesystem with the same name.
        Enumeration en = TopManager.getDefault ().getRepository ().fileSystems ();
        while (en.hasMoreElements ()) {
          FileSystem fs = (FileSystem) en.nextElement ();
          if (fs.getSystemName ()==name) {
            // NotifyDescriptor.Exception nd = new NotifyDescriptor.Exception (
            throw new PropertyVetoException ("Directory already mounted", // NOI18N
              new PropertyChangeEvent (this, "RootDirectory", getSystemName (), name)); // NOI18N
            // TopManager.getDefault ().notify (nd);
          }
    }
        */
        D.deb("Setting system name '"+name+"'"); // NOI18N
        setSystemName(name);

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

    /** Set the root directory of the file system.
     * @param rootFile root directory
     */
    public void setRootFile(File rootFile) {
        this.rootFile = rootFile;
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
    protected String computeSystemName (File rootFile) {
        D.deb("computeSystemName() ->"+rootFile.toString ().replace(File.separatorChar, '/') ); // NOI18N
        return rootFile.toString ().replace(File.separatorChar, '/');
    }

    /** Get file representation for given string name.
     * @param name the name
     * @return the file
     */
    public File getFile (String name) {
        return new File (rootFile, name);
    }

    //-------------------------------------------
    //
    // List
    //

    private String[] getLocalFiles(String name) {
        File dir = new File(getRootDirectory(), name);
        if (dir == null || !dir.exists() || !dir.canRead()) return new String[0];
        String files[] = dir.list(getLocalFileFilter());
        return files;
    }

    private String[] addLocalFiles(String name, String[] cachedFiles) {
        String[] files = getLocalFiles(name);
        if (files == null || files.length == 0) {
            return cachedFiles;
        }
        Vector cached = new Vector(Arrays.asList(cachedFiles));
        Vector local = new Vector(Arrays.asList(files));
        local.removeAll(cached);
        cached.addAll(local);
        return (String[]) cached.toArray(new String[0]);
    }

    /* Get children files inside a folder
     * @param name the name of the folder
     */
    public String[] children (String name) {
        D.deb("children('"+name+"')"); // NOI18N
        //System.out.println("children('"+name+"'), refresh time = "+getRefreshTime());
        String[] vcsFiles = null;
        String[] files = null;

        if (!ready) {
            D.deb("not ready"); // NOI18N
            //System.out.println("children: not ready !!"); // NOI18N
            return new String[0];
        }

        if (cache != null && cache.isDir(name)) {
            vcsFiles = cache.getFilesAndSubdirs(name);
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
        }
        if (vcsFiles == null) files = getLocalFiles(name);
        else files = addLocalFiles(name, vcsFiles);
        //D.deb("children('"+name+"') = "+VcsUtilities.arrayToString(files));
        if (cache != null) {
            VcsCacheDir cacheDir = (VcsCacheDir) cache.getDir(name);
            if (files.length == 0 && (cacheDir == null || (!cacheDir.isLoaded() && !cacheDir.isLocal()))) cache.readDir(name/*, false*/); // DO refresh when the local directory is empty !
        }
        //System.out.println("children = "+files);
        return files;
    }


    // create local folder for existing VCS folder that is missing
    private void checkLocalFolder (String name) throws java.io.IOException {
        StringTokenizer st = new java.util.StringTokenizer (name, "/"); // NOI18N
        String dir = null;
        while(st.hasMoreElements()) {
            dir = dir==null ? (String) st.nextElement () : dir + "/" + (String) st.nextElement (); // NOI18N
            File f = getFile (dir);
            if(f.exists ()) continue;

            Object[] errorParams = new Object[] {
                                       f.getName (),
                                       getDisplayName (),
                                       f.toString ()
                                   };

            boolean b = f.mkdir();
            if (!b) {
                throw new IOException(MessageFormat.format (g("EXC_CannotCreateF"), errorParams)); // NOI18N
            }
            D.deb ("local dir created='"+dir+"'"); // NOI18N
        }
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

        if (lastSeparator > 0) checkLocalFolder (name.substring (0, lastSeparator));


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
        Object[] errorParams = new Object[] {
                                   f.getName (),
                                   getDisplayName (),
                                   f.toString (),
                               };

        int lastSeparator = name.lastIndexOf ("/"); // NOI18N

        //if (lastSeparator < 0) lastSeparator = 0;

        if (lastSeparator > 0) checkLocalFolder (name.substring (0, lastSeparator));


        if (!f.createNewFile ()) {
            throw new IOException(MessageFormat.format (g("EXC_DataAlreadyExist"), errorParams)); // NOI18N
        }
        if (cache != null) {
            cache.addFile(name);
        }
        if (statusProvider != null) {
            statusProvider.setFileStatus(name, statusProvider.getLocalFileStatus());
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

        if (!of.renameTo (nf)) {
            throw new IOException(g("EXC_CannotRename", oldName, getDisplayName (), newName)); // NOI18N
        }
        if (cache != null) cache.rename(oldName, newName);
    }

    /** Delete a file.
     * @param name name of the file
     * @exception IOException if the file could not be deleted
     */
    public void delete (String name) throws IOException {
        D.deb("delete('"+name+"')"); // NOI18N
        File file = getFile (name);
        /*
        if (!file.delete()) {
          throw new IOException (g("EXC_CannotDelete", name, getDisplayName (), file.toString ()));
    }
        */
        if (!file.exists()) return; // silently ignore non existing files
        boolean wasDir = file.isDirectory();
        if (!VcsUtilities.deleteRecursive(file)) {
            throw new IOException (g("EXC_CannotDelete", name, getDisplayName (), file.toString ())); // NOI18N
        }
        if (cache != null) cache.remove(name, wasDir);
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
        return new java.util.Date (getFile (name).lastModified ());
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
        int i = name.lastIndexOf ('.');
        String s;
        try {
            s = FileUtil.getMIMEType (name.substring (i + 1));
        } catch (IndexOutOfBoundsException e) {
            s = null;
        }
        D.deb("mimeType() -> '"+s+"'"); // NOI18N
        return s == null ? "content/unknown" : s; // NOI18N
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
            throw new java.io.FileNotFoundException() {
                public String getLocalizedMessage() {
                    return g("MSG_FileNotExist", fname);
                }
            };
        }
        return in;
    }

    private void fileChanged(String name) {
        D.deb("fileChanged("+name+")");
        if (statusProvider != null) {
            String oldStatus = statusProvider.getFileStatus(name);
            if (!notModifiableStatuses.contains(oldStatus)) {
                statusProvider.setFileModified(name);
                statusChanged(name);
            }
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
            fileChanged(name);
        }
    }

    /** Get output stream to a file.
     * @param name the file to test
     * @return output stream to overwrite the contents of this file
     * @exception IOException if an error occures (the file is invalid, etc.)
     */
    public OutputStream outputStream (String name) throws java.io.IOException {
        D.deb("outputStream("+name+")"); // NOI18N
        FileOutputStream output = new FileOutputStreamPlus (getFile (name), name);
        return output;
    }

    public synchronized boolean getPromptForLockResult() {
        return promptForLockResult;
    }

    public synchronized void setPromptForLockResult(boolean promptForLockResult) {
        this.promptForLockResult = promptForLockResult;
    }

    /** Run the LOCK command to lock the file.
     *
     * @param name name of the file
     */
    public void lock (String name_) throws IOException {
        if (!isImportant(name_)) return; // ignore locking of unimportant files
        final String name = name_;
        //final VcsFileSystem current = this;
        File file = getFile (name);
        if (!file.exists()) return; // Ignore the lock when the file does not exist.
        if (isReadOnly()) { // I'm on a read-only filesystem => can not lock
            throw new IOException ("Cannot Lock "+name); // NOI18N
        }
        if (isCallEditFilesOn()) {
            if (!file.canWrite ()) {
                VcsCacheFile vcsFile = (cache != null) ? ((VcsCacheFile) cache.getFile (name)) : null;
                if (vcsFile != null && !vcsFile.isLocal () && !name.endsWith (".orig")) { // NOI18N
                    Table files = new Table();
                    files.put(name, findResource(name));
                    VcsAction.doEdit (files, this);
                }
            }
        }
        new Thread(new Runnable() {
                       public void run() {
                           D.deb("lock('"+name+"')"); // NOI18N
                           D.deb("this = "+this); // NOI18N
                           //File f = getFile (name);
                           //if(f.canWrite ()) return;
                           if (isLockFilesOn ()) {
                               VcsCacheFile vcsFile = (cache != null) ? ((VcsCacheFile) cache.getFile (name)) : null;
                               // *.orig is a temporary file created by AbstractFileObject
                               // on saving every file to enable undo if saving fails
                               if (vcsFile==null || vcsFile.isLocal () || name.endsWith (".orig")) return; // NOI18N
                               else {
                                   D.deb ("lock on file:"+vcsFile.toString()); // NOI18N
                                   setPromptForLockResult(false);
                                   if (isPromptForLockOn ()) {
                                       try {
                                           javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                                                       public void run() {
                                                           boolean result;
                                                           NotifyDescriptor.Confirmation confirm = new NotifyDescriptor.Confirmation (g("MSG_LockFileCh"), NotifyDescriptor.Confirmation.OK_CANCEL_OPTION); // NOI18N
                                                           result = (TopManager.getDefault ().notify (confirm).equals (NotifyDescriptor.Confirmation.OK_OPTION));
                                                           setPromptForLockResult(result);
                                                       }
                                                   });
                                       } catch (InterruptedException e) {
                                           setPromptForLockResult(true);
                                       } catch (java.lang.reflect.InvocationTargetException e) {
                                           setPromptForLockResult(true);
                                       }
                                   }
                                   if (!isPromptForLockOn () || getPromptForLockResult()) {
                                       Table files = new Table();
                                       files.put(name, findResource(name));
                                       VcsAction.doLock (files, VcsFileSystem.this);
                                   }
                               }
                           }
                       }
                   }, "VCS-Locking Files").start(); // NOI18N
        if (!file.canWrite () && file.exists()) {
            throw new IOException ("Cannot Lock "+name); // NOI18N
        }
    }

    /** Call the UNLOCK command to unlock the file.
     *
     * @param name name of the file
     */
    public void unlock (String name) {
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
    }
    
    public void cacheRemoved(CacheHandlerEvent event) {
//        D.deb("cacheRemoved called for:" + event.getCvsCacheFile().getName());
    }
    
    public void statusChanged(CacheHandlerEvent event) {
        D.deb("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        D.deb("statusChanged called for:" + event.getCacheFile().getAbsolutePath());
        //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        //System.out.println("statusChanged called for:" + event.getCacheFile().getAbsolutePath());
        String root = getRootDirectory().getAbsolutePath();
        String absPath = event.getCacheFile().getAbsolutePath();
        if (absPath.startsWith(root)) { // it belongs to this FS -> do something
            D.deb("-------- it is in this filesystem");
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
    
    /** Set the tree structure of commands.
     * @param root the tree of {@link VcsCommandNode} objects.
     */
    public void setCommands(Node root) {
        Object old = commandsRoot;
        commandsRoot = root;
        commandsByName = new Hashtable();
        addCommandsToHashTable(root);
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
            setCommands (commandsRoot);
        }
        return (VcsCommand) commandsByName.get(name);
    }

    private void settingsChanged(String propName, Object oldVal, Object newVal) {
        VcsSettings settings = (VcsSettings) SharedClassObject.findObject(VcsSettings.class, true);
        if (propName.equals(VcsSettings.PROP_USE_GLOBAL)) {
            if (((Boolean) newVal).booleanValue() == true) {
                setOffLine(settings.isOffLine());
                setAutoRefresh(settings.getAutoRefresh());
            }
        } else {
            if (settings.isUseGlobal()) {
                if (propName.equals(VcsSettings.PROP_OFFLINE)) {
                    setOffLine(settings.isOffLine());
                } else if (propName.equals(VcsSettings.PROP_AUTO_REFRESH)) {
                    setAutoRefresh(settings.getAutoRefresh());
                }
            }
        }
    }
    
    public FilenameFilter getLocalFileFilter() {
        return null;
    }

    public String getBundleProperty(String s) {
        return g(s);
    }

    public String getBundleProperty(String s, Object obj) {
        return g(s, obj);
    }

    //-------------------------------------------
    protected String g(String s) {
        D.deb("getting "+s);
        if (resourceBundle == null) {
            synchronized (this) {
                if (resourceBundle == null) {
                    resourceBundle = NbBundle.getBundle(VcsFileSystem.class);
                }
            }
        }
        return resourceBundle.getString (s);
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
        System.out.println("VcsFileSystem(): "+debug);
    }
    //-------------------------------------------
}
