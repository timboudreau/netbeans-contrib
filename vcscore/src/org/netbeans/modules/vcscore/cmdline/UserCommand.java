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

package org.netbeans.modules.vcscore.cmdline;

import java.io.*;
import java.net.*;
import java.util.*;
import java.beans.*;
import java.text.*;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.util.*;

import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.commands.VcsCommand;

/** Single user defined command.
 * 
 * @author Michal Fadljevic, Pavel Buzek, Martin Entlicher
 */
public class UserCommand extends Object implements VcsCommand, Serializable, Cloneable, Comparable {
    private static Debug E=new Debug("UserCommand", true); // NOI18N
    private static Debug D=E;

    /**
     * The name of the command which refresh a file.
     */
    public static final String NAME_REFRESH_FILE = "LIST_FILE";

    /**
     * When this property is true, refresh of the current folder is performed after successfull execution of this command.
     */
    public static final String PROPERTY_REFRESH_PROCESSED_FILES = "refreshProcessedFiles";

    public static final String PROPERTY_INPUT = "input";
    //public static final String PROPERTY_DISPLAY = "display";
    public static final String PROPERTY_DATA_REGEX = "data.regex";
    public static final String PROPERTY_ERROR_REGEX = "error.regex";
    public static final String PROPERTY_DATA_REGEX_GLOBAL = "data.regex.global";
    public static final String PROPERTY_ERROR_REGEX_GLOBAL = "error.regex.global";
    //public static final String PROPERTY_DOES_NOT_FAIL = "doesNotFail";
    //public static final String PROPERTY_PRECOMMANDS = "preCommands";
    //public static final String PROPERTY_PRECOMMANDS_EXECUTE = "preCommandsExecute";
    
    public static final String PROPERTY_LIST_INDEX_FILE_NAME = "data.fileName.index";
    public static final String PROPERTY_LIST_INDEX_REMOVED_FILE_NAME = "data.removedFileName.index";
    public static final String PROPERTY_LIST_INDEX_STATUS = "data.status.index";
    public static final String PROPERTY_LIST_INDEX_LOCKER = "data.locker.index";
    public static final String PROPERTY_LIST_INDEX_REVISION = "data.revision.index";
    public static final String PROPERTY_LIST_INDEX_STICKY = "data.sticky.index";
    public static final String PROPERTY_LIST_INDEX_ATTR = "data.attr.index";
    public static final String PROPERTY_LIST_INDEX_DATE = "data.date.index";
    public static final String PROPERTY_LIST_INDEX_TIME = "data.time.index";
    public static final String PROPERTY_LIST_INDEX_SIZE = "data.size.index";

    /**
     * The list of pairs of quoted regular expressions and the status strings separated by commas.
     * When this property is non-empty, the status returned by the command will be matched
     * with the regular expressions (from left to right) and when matched, the appropriate
     * status will be returned. When no match would be possible, the file will be refreshed
     * by the LIST_FILE command.
     */
    public static final String PROPERTY_REFRESH_FILE_STATUS_SUBSTITUTIONS = "refreshFileStatusSubstitutions";
    
    /**
     * The file refresh data information is normally read from the standard data output. If you want to
     * add the error data output, set this property as true.
     */
    public static final String PROPERTY_REFRESH_INFO_FROM_BOTH_DATA_OUTS = "refreshInfoFromBothDataOuts";
    
    // Properties:
    private String name="";          // e.g. "CHECKIN" // NOI18N
    private String advancedName = null;// e.g. "commit" // NOI18N
    private String label="";         // e.g. "Check Int" // NOI18N
    private String exec="";          // e.g. "${STCMD} ci -p ${PROJECT} ..." // NOI18N
    // when exec == null this is only label for command subset
    private String input="";         // e.g. "Cancel\n" // NOI18N
    private long timeout = 0;      // e.g. 20000
    private String dataRegex="(.*$)"; // e.g. (.*$) // NOI18N
    private String errorRegex="";    // e.g. "Error" // NOI18N
    /**
     * If true, then display output of this command in a window.
     */
    private boolean displayOutput = false; // display output in command in window
    /**
     * If true, do an automatic refresh of the current folder after this command.
     */
    private boolean doRefresh = false;  // whether to do refresh after this command
    /**
     * Do an automatic refresh recursively when doRefresh is true and refreshRecursivelyPattern
     * can be found as substring in the command's exec. When empty, do non-recursive refresh.
     */
    private String refreshRecursivelyPattern = "";  // whether to do refresh recursively after this command
    /**
     * When automatic refresh is to be run, if true, refresh the parent folder, if false, refresh the current folder.
     */
    private boolean refreshParent = true;
    /**
     * If the updated files should occure in the Editor without user approval, turn this property on.
     */
    private boolean checkForModifications = false;
    /**
     * If true, this command is visible on directories.
     */
    private boolean onDir = true;  // the command is visible on directories
    /**
     * If true, this command is visible on files.
     */
    private boolean onFile = true;  // the command is visible on files
    /**
     * If true, this command is visible only on the root of the filesystem.
     */
    private boolean onRoot = false;  // the command is visible ONLY on the root of the filesystem.
    /**
     * If true, this command is visible everywhere bun on the root of the filesystem.
     */
    private boolean notOnRoot = false;  // the command is NOT visible on the root of the filesystem.
    /**
     * The text of a confirmation message printed before the command is executed.
     */
    private String confirmationMsg = ""; // the confirmation message which is printed before the command executes
    /*
     * Vector of commands which are supposed to be run before this command.
     *
    private Vector preCommands = null;
    
    /*
     * Whether to really execute precommands. If false,
     * empty output will be inserted to the command's exec.
     *
    private boolean executePreCommands = true;
     */

    /**
     * Whether to process all files. If false, only important files will be processed.
     */
    private boolean processAllFiles = false;

    /**
     * The number of revisions selected to run this command.
     * If it is 0, the command is on the filesystem's popup menu.
     * If it is > 0, the command is on the Revision Explorer's popup menu.
     */
    private int numRevisions = 0;
    
    /**
     * Whether this command changes the number of revisions of the file it acts on.
     */
    private boolean changingNumRevisions = false;
    
    /**
     * Whether this command changes the number of revisions of the file it acts on.
     */
    private boolean changingRevision = false;
    
    /**
     * When thos command changes a revision, its name is in this variable.
     */
    private String changedRevisionVariableName = "";

    /**
     * The order of this command in the pop-up action menu.
     */
    private int[] orderArr = {-1}; // The order in the popup menu

    // for name=="LIST" // NOI18N
    private int statusIndex=-1;
    private int lockerIndex=-1;
    private int attrIndex=-1;
    private int dateIndex=-1;
    private int timeIndex=-1;
    private int sizeIndex=-1;

    private int fileNameIndex=-1;
    
    /**
     * Additional user parameters local to this command.
     */
    public volatile String[] userParams = null;
    
    //private transient CommandBeanInfo commandBeanInfo = null;
    private HashMap properties = new HashMap();
    
    private ArrayList children = null;

    static final long serialVersionUID =6658759487911693730L;

    //-------------------------------------------
    public UserCommand() {
        // I am JavaBean...
        //preCommands = new Vector();
        setPropertiesFromFields();
    }
    
    /** @deprecated
     * This constructor is necessary for backward compatibility to be able to read
     * the command from properties file.
     */
    public UserCommand(String name) {
        this.name = name;
        //this.exec = exec;
        //preCommands = new Vector();
        properties = null;
        setPropertiesFromFields();
        //System.out.println("UserCommand("+name+") from filelds = "+getPropertyNames().length);
    }

    //-------------------------------------------
    public Object clone(){
        UserCommand uc = new UserCommand();
        uc.copyFrom(this);
        return uc;
    }

    /**
     * Fill all properties from the given user command.
     * @params uc the user command to copy the properties from
     */
    public void copyFrom(VcsCommand vc) {
        UserCommand uc = (UserCommand) vc;
        name = uc.name;
        label = uc.label;
        orderArr = (int[]) uc.orderArr.clone();
        /*
        orderArr = new int[uc.orderArr.length];
        for(int i = 0; i < orderArr.length; i++) orderArr[i] = uc.orderArr[i];
         */
        String[] properties = uc.getPropertyNames();
        for (int i = 0; i < properties.length; i++) {
            Object property = uc.getProperty(properties[i]);
            Object newProperty;
            if (property instanceof Boolean) {
                newProperty = property;
            } else if (property instanceof Integer) {
                newProperty = new Integer(((Integer) property).intValue());
                /*
            } else if (property instanceof Cloneable) {
                try {
                    newProperty = ((Cloneable) property.).clone();
                } catch (CloneNotSupportedException exc) {
                    newProperty = property;
                }
            } else {
                newProperty = property;
            }
                 */
            } else if (property instanceof String) {
                newProperty = new String((String) property);
            } else {
                newProperty = property;
            }
            this.setProperty(properties[i], newProperty);
        }
    }
    
    /*
     * Fill all properties from the given user command.
     * @params uc the user command to copy the properties from
     *
    public void copyFrom_old(UserCommand uc) {
        name = uc.name;
        advancedName = uc.advancedName;
        label = uc.label;
        exec = uc.exec;
        input = new String(uc.input);
        timeout = uc.timeout;
        dataRegex = uc.dataRegex;
        errorRegex = uc.errorRegex;
        displayOutput = uc.displayOutput;
        doRefresh = uc.doRefresh;
        refreshRecursivelyPattern = new String(uc.refreshRecursivelyPattern);
        refreshParent = uc.refreshParent;
        checkForModifications = uc.checkForModifications;
        onFile = uc.onFile;
        onDir = uc.onDir;
        onRoot = uc.onRoot;
        notOnRoot = uc.notOnRoot;
        confirmationMsg = new String(uc.confirmationMsg);
        processAllFiles = uc.processAllFiles;
        numRevisions = uc.numRevisions;
        changingNumRevisions = uc.changingNumRevisions;
        changingRevision = uc.changingRevision;
        changedRevisionVariableName = new String(uc.changedRevisionVariableName);
        orderArr = (int[]) uc.orderArr.clone();
        /*
        orderArr = new int[uc.orderArr.length];
        for(int i = 0; i < orderArr.length; i++) orderArr[i] = uc.orderArr[i];
         *
        //preCommands = new Vector(uc.preCommands);
        userParams = uc.userParams;

        statusIndex = uc.statusIndex;
        lockerIndex = uc.lockerIndex;
        attrIndex = uc.attrIndex;
        dateIndex = uc.dateIndex;
        timeIndex = uc.timeIndex;
        sizeIndex = uc.sizeIndex;
        fileNameIndex = uc.fileNameIndex;
    }
     */
    
    /**
     * Compares this command with a specified command for order.
     */
    public int compareTo(final java.lang.Object o) {
        if (!(o instanceof UserCommand)) {
            throw new IllegalArgumentException("Bad object type");
        }
        UserCommand cmd = (UserCommand) o;
        int[] o1 = this.orderArr;
        int[] o2 = cmd.getOrder();
        int l1 = o1.length;
        int l2 = o2.length;
        int i;
        for(i = 0; i < l1 && i < l2; i++) {
            if (o1[i] != o2[i]) {
                return o1[i] - o2[i];
            }
        }
        return l1 - l2;
    }

    //-------------------------------------------
    public void   setName(String name){ this.name=name;}
    public String getName(){ return name;}
    //public void   setAdvancedName(String advancedName){ this.advancedName = advancedName;}
    //public String getAdvancedName(){ return advancedName;}
    //public void   setLabel(String label){ this.label=label;}
    //public String getLabel(){ return label;}
    public void   setDisplayName(String displayName) { this.label = displayName; }
    public String getDisplayName() { return label; }
    //public void   setExec(String exec){ this.exec=exec;}
    //public String getExec(){ return exec;}
    //public void   setInput(String input){ this.input=input;}
    //public String getInput(){ return input;}
    //public void   setTimeout(long timeout){ this.timeout=timeout;}
    //public long   getTimeout(){ return timeout;}
    //public void   setDataRegex(String dataRegex){ this.dataRegex=dataRegex;}
    //public String getDataRegex(){ return dataRegex;}
    //public void   setErrorRegex(String errorRegex){ this.errorRegex=errorRegex;}
    //public String getErrorRegex(){ return errorRegex;}
    //public void   setDisplayOutput (boolean displayOutput) { this.displayOutput=displayOutput; }
    //public boolean isDisplayOutput () { return displayOutput;}
    //public void   setDoRefresh (boolean doRefresh) { this.doRefresh=doRefresh; }
    //public boolean isDoRefresh () { return doRefresh; }
    //public void   setRefreshRecursivelyPattern (String refreshRecursivelyPattern) { this.refreshRecursivelyPattern = refreshRecursivelyPattern; }
    //public String getRefreshRecursivelyPattern() { return refreshRecursivelyPattern; }
    //public void   setRefreshParent (boolean refreshParent) { this.refreshParent = refreshParent; }
    //public boolean isRefreshParent() { return refreshParent; }
    //public void   setCheckForModifications (boolean checkForModifications) { this.checkForModifications = checkForModifications; }
    //public boolean isCheckForModifications() { return checkForModifications; }
    
    /**
     * @deprecated The order of individual commands should not be set.
     * The position in the popup menu is given by the hierarchical structure of nodes.
     */    
    public void   setOrder (int[] orderArr) { this.orderArr = (int[]) orderArr.clone(); }
    /**
     * @deprecated See {@link setOrder} for description.
     */    
    public int[]  getOrder () { return (int[]) orderArr.clone(); }
    //public boolean isOnFile () { return onFile; }
    //public void    setOnFile (boolean onFile) { this.onFile = onFile; }
    //public boolean isOnDir () {return onDir; }
    //public void    setOnDir (boolean onDir) { this.onDir = onDir; }
    //public boolean isOnRootOnly () { return onRoot; }
    //public void    setOnRootOnly (boolean onRoot) { this.onRoot = onRoot; }
    //public boolean isNotOnRoot() { return notOnRoot; }
    //public void   setNotOnRoot(boolean notOnRoot) { this.notOnRoot = notOnRoot; }
    //public String getConfirmationMsg() { return confirmationMsg; }
    //public void setConfirmationMsg(String confirmationMsg) { this.confirmationMsg = confirmationMsg; }
    //public UserCommand[] getPreCommands() { return (UserCommand[]) preCommands.toArray(new UserCommand[0]); }
    //public void addPreCommand(UserCommand cmd) { this.preCommands.add(cmd); }
    //public void removeAllPreCommands() { this.preCommands = new Vector(); }
    //public boolean isExecutePreCommands() { return executePreCommands; }
    //public void setExecutePreCommands(boolean executePreCommands) { this.executePreCommands = executePreCommands; }
    //public boolean isProcessAllFiles() { return processAllFiles; }
    //public void setProcessAllFiles(boolean processAllFiles) { this.processAllFiles = processAllFiles; }
    //public void setNumRevisions(int numRevisions) { this.numRevisions = numRevisions; }
    //public int  getNumRevisions() { return numRevisions; }
    //public void setChangingNumRevisions(boolean changingNumRevisions) { this.changingNumRevisions = changingNumRevisions; }
    //public boolean isChangingNumRevisions() { return changingNumRevisions; }
    //public void setChangingRevision(boolean changingRevision) { this.changingRevision = changingRevision; }
    //public boolean isChangingRevision() { return changingRevision; }
    //public void setChangedRevisionVariableName(String changedRevisionVariableName) { this.changedRevisionVariableName = changedRevisionVariableName; }
    //public String getChangedRevisionVariableName() { return changedRevisionVariableName; }

    //-------------------------------------------
    //public void setStatus(int index){ this.statusIndex=index; }
    //public int  getStatus(){ return statusIndex; }
    //public void setLocker(int index){ this.lockerIndex=index; }
    //public int  getLocker(){ return lockerIndex; }
    //public void setAttr(int index){ this.attrIndex=index; }
    //public int  getAttr(){ return attrIndex; }
    //public void setDate(int index){ this.dateIndex=index; }
    //public int  getDate(){ return dateIndex; }
    //public void setTime(int index){ this.timeIndex=index; }
    //public int  getTime(){ return timeIndex; }
    //public void setSize(int index){ this.sizeIndex=index; }
    //public int  getSize(){ return sizeIndex; }
    //public void setFileName(int index){ this.fileNameIndex=index; }
    //public int  getFileName(){ return fileNameIndex; }


    //-------------------------------------------
    public String toString(){
        if (exec != null) {
            return name+"["+getOrderString()+"]"+"("+label+")("+timeout+")='"+exec+"'"+ // NOI18N
                   ( name.equals("LIST") ? // NOI18N
                     ("[["+statusIndex+","+lockerIndex+","+ attrIndex+","+ // NOI18N
                      dateIndex+","+ timeIndex+", "+ sizeIndex+","+fileNameIndex+"]]") : ""); // NOI18N
        } else {
            return label+"["+getOrderString()+"]";
        }
    }
    
    /*
     * Get the precommands name in a Vector.
     *
    public Vector getPreCommandsStr() {
        Vector preCommandsNames = new Vector();
        for(int j = 0; j < preCommands.size(); j++) {
            preCommandsNames.add(((UserCommand) preCommands.get(j)).getName());
        }
        return preCommandsNames;
    }
    
    /*
     * Set the precommands names from a Vector.
     *
    public void setPreCommandsStr(Vector preCommandsNames) {
        if (preCommandsNames != null) {
            for(int i = 0; i < preCommandsNames.size(); i++) {
                UserCommand preCommand = new UserCommand();
                preCommand.setName((String) preCommandsNames.get(i));
                addPreCommand(preCommand);
            }
        }
    }
     */

    public String getOrderString() {
        return UserCommand.getOrderString(this.orderArr);
    }

    public static String getOrderString(int[] orderArr) {
        String str = "";
        for(int i = 0; i < orderArr.length; i++) {
            str += orderArr[i];
            if (i < orderArr.length - 1) str += ".";
        }
        return str;
    }
    
    /*
     * Add a subcommand to this command.
     *
    public void addChildCommand(UserCommand cmd) {
        if (children == null) children = new ArrayList();
        children.add(cmd);
    }
    
    /*
     * Add a subcommand separator.
     *
    public void addChildSeparatorSeparator() {
        if (children == null) children = new ArrayList();
        children.add(VcsCommand.COMMAND_SEPARATOR);
    }

    /*
     * Get the list of children commands.
     *
    public ArrayList getChildrenCommands() {
        return children;
    }
     */
    /*
    private static void assignPreCommands(List commands) {
        for(int i = 0; i < commands.size(); i++) {
            UserCommand uc = (UserCommand) commands.get(i);
            UserCommand[] pc = uc.getPreCommands();
            if (pc != null && pc.length > 0) {
                uc.removeAllPreCommands();
                for(int j = 0; j < pc.length; j++) {
                    String name = pc[j].getName();
                    int k;
                    for(k = 0; k < commands.size(); k++) {
                        UserCommand puc = (UserCommand) commands.get(k);
                        if (name.equals(puc.getName())) {
                            uc.addPreCommand(puc);
                            break;
                        }
                    }
                    if (k >= commands.size()) {
                        //org.openide.TopManager.getDefault().notifyException(new Throwable(g("MSG_NoPreCommandFound", name)));
                        E.err(g("MSG_NoPreCommandFound", name));
                    }
                }
            }
        }
    }
     */
    
    /**
     * Get the names of all supported properties.
     * @return the array of properties names
     */
    public String[] getPropertyNames() {
        Set props = properties.keySet();
        return (String[]) props.toArray(new String[0]);
    }

    /**
     * Get the additional command property.
     * @param propertyName the name of the property
     * @return the value of the property
     */
    public synchronized Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }
    
    /**
     * Set the additional property to the command.
     * @param propertyName the name of the property
     * @param value the value of the property. The <code>null</code> value should unset the property
     */
    public synchronized void setProperty(String propertyName, Object value) {
        if (value == null) {
            properties.remove(propertyName);
        } else {
            properties.put(propertyName, value);
        }
    }

    /**
     * This method is called after read of all commands from an external source.
     * Allows some more settings to be done.
     * @param cmds the <code>Vector</code> containing all read commands. 
     */
    public static void readFinished(java.util.List cmds) {
        //assignPreCommands(cmds);
    }
    
    private void setPropertiesFromFields() {
        if (properties == null) { // Have to create and fill in all properties
            properties = new HashMap();
            if (exec == null) return; // Label command ==> no properties
            // deserialization from older versions must be done:
            setProperty(VcsCommand.PROPERTY_ADVANCED_NAME, advancedName);//getAdvancedName());
            setProperty(VcsCommand.PROPERTY_EXEC, exec);//getExec());
            setProperty(UserCommand.PROPERTY_INPUT, input);//getInput());
            //setProperty("timeout", new Long(getTimeout()));
            setProperty(UserCommand.PROPERTY_DATA_REGEX, dataRegex);//getDataRegex());
            setProperty(UserCommand.PROPERTY_ERROR_REGEX, errorRegex);//getErrorRegex());
            setProperty(VcsCommand.PROPERTY_DISPLAY_PLAIN_OUTPUT, displayOutput ? Boolean.TRUE : Boolean.FALSE);//isDisplayOutput()));
            //setProperty("doRefresh", isDoRefresh() ? Boolean.TRUE : Boolean.FALSE); <- not needed any more
            setProperty(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_MATCHED, refreshRecursivelyPattern);//getRefreshRecursivelyPattern());
            setProperty(VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER, doRefresh/*isDoRefresh()*/ && refreshParent ? Boolean.TRUE : Boolean.FALSE);//isRefreshParent()));
            setProperty(VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER, doRefresh && !refreshParent ? Boolean.TRUE : Boolean.FALSE);
            setProperty(VcsCommand.PROPERTY_CHECK_FOR_MODIFICATIONS, checkForModifications ? Boolean.TRUE : Boolean.FALSE);
            if (onRoot == true) onFile = onDir = false; // The meaning has changed. Instead "on root only" it means "on root too".
            else onRoot = true;                         // if onRoot == false, it means "not on root, but everywhere.
            setProperty(VcsCommand.PROPERTY_ON_FILE, onFile ? Boolean.TRUE : Boolean.FALSE);
            setProperty(VcsCommand.PROPERTY_ON_DIR, onDir ? Boolean.TRUE : Boolean.FALSE);
            setProperty(VcsCommand.PROPERTY_ON_ROOT, onRoot ? Boolean.TRUE : Boolean.FALSE);
            //setProperty(VcsCommand.PROPERTY_NOT_ON_ROOT, notOnRoot ? Boolean.TRUE : Boolean.FALSE);
            setProperty(VcsCommand.PROPERTY_CONFIRMATION_MSG, confirmationMsg);
            setProperty(VcsCommand.PROPERTY_PROCESS_ALL_FILES, processAllFiles ? Boolean.TRUE : Boolean.FALSE);
            setProperty(VcsCommand.PROPERTY_NUM_REVISIONS, new Integer(numRevisions));
            setProperty(VcsCommand.PROPERTY_CHANGING_NUM_REVISIONS, changingNumRevisions ? Boolean.TRUE : Boolean.FALSE);
            setProperty(VcsCommand.PROPERTY_CHANGING_REVISION, changingRevision ? Boolean.TRUE : Boolean.FALSE);
            setProperty(VcsCommand.PROPERTY_CHANGED_REVISION_VAR_NAME, changedRevisionVariableName);
            //setProperty(UserCommand.PROPERTY_PRECOMMANDS, getPreCommandsStr());
            //setProperty(UserCommand.PROPERTY_PRECOMMANDS_EXECUTE, executePreCommands ? Boolean.TRUE : Boolean.FALSE);
            if (VcsCommand.NAME_REFRESH.equals(name) || VcsCommand.NAME_REFRESH_RECURSIVELY.equals(name)) {
                setProperty(UserCommand.PROPERTY_LIST_INDEX_FILE_NAME, new Integer(fileNameIndex));
                setProperty(UserCommand.PROPERTY_LIST_INDEX_STATUS, new Integer(statusIndex));
                setProperty(UserCommand.PROPERTY_LIST_INDEX_LOCKER, new Integer(lockerIndex));
                setProperty(UserCommand.PROPERTY_LIST_INDEX_ATTR, new Integer(attrIndex));
                setProperty(UserCommand.PROPERTY_LIST_INDEX_DATE, new Integer(dateIndex));
                setProperty(UserCommand.PROPERTY_LIST_INDEX_TIME, new Integer(timeIndex));
                setProperty(UserCommand.PROPERTY_LIST_INDEX_SIZE, new Integer(sizeIndex));
            }
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setPropertiesFromFields();
    }
    
    static String g(String s) {
        return NbBundle.getBundle
               ("org.netbeans.modules.vcscore.cmdline.Bundle").getString (s);
    }
    static String g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
    static String g(String s, Object obj1, Object obj2) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
    }
    static String g(String s, Object obj1, Object obj2, Object obj3) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
    }
        
}
