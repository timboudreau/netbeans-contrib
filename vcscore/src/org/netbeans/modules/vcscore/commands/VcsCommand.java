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

package org.netbeans.modules.vcscore.commands;

import java.util.ArrayList;

/**
 * The <code>VcsCommand</code> interface should be implemented by any class
 * whose instances are intended to be executed by a VcsCommandExecutor.
 *
 * @author  Martin Entlicher
 * @version 1.0
 */
public interface VcsCommand extends VcsCommandCookie {
    
    /**
     * The name of the command which does non recursive refresh of a folder.
     */
    public static final String NAME_REFRESH = "LIST";
    /**
     * The name of the command which does recursive refresh of a folder.
     */
    public static final String NAME_REFRESH_RECURSIVELY = "LIST_SUB";
    /**
     * The name of the command which does non recursive refresh of a folder.
     */
    public static final String NAME_REFRESH_OFFLINE = "LIST_OFFLINE";
    /**
     * The name of the command which does recursive refresh of a folder.
     */
    public static final String NAME_REFRESH_RECURSIVELY_OFFLINE = "LIST_SUB_OFFLINE";
    
    /**
     * The name of the command which will be called to lock the file in VCS.
     */
    public static final String NAME_LOCK = "LOCK";
    /**
     * The name of the command which will be called to unlock the file in VCS.
     */
    public static final String NAME_UNLOCK = "UNLOCK";
    /**
     * The name of the command which will be called to prepare the file for editing in VCS.
     */
    public static final String NAME_EDIT = "EDIT";
    /**
     * The name of the command which will be called after a file was deleted.
     */
    public static final String NAME_DELETE_FILE = "DELETE_FILE";
    /**
     * The name of the command which will be called after a directory was deleted.
     */
    public static final String NAME_DELETE_DIR = "DELETE_DIR";
    
    /**
     * The name of the command which is used to get a specific revision to open.
     */
    public static final String NAME_REVISION_OPEN = "REVISION_OPEN";
    
    /**
     * Command may set this property which will be visible on the VariableInputDialog.
     * Can be used to tell the user about the specific command appearance.
     */
    public static final String PROPERTY_ADVANCED_NAME = "advancedName";
    
    /**
     * Describe the components, that will appear in the variable input dialog
     * prior the command is executed.
     */
    public static final String PROPERTY_INPUT_DESCRIPTOR = "inputDescriptor";
    /**
     * This property denotes the executable string. This string may contain
     * any number of variables that are expanded prior to execution.
     */
    public static final String PROPERTY_EXEC = "exec";
    /**
     * This Integer property can contain any combination of EXEC_* constants delimeted
     * by OR operator.
     * <p>i.e.: (EXEC_SERIAL_ON_FILE | EXEC_SERIAL_OF_COMMAND) will run only one command
     * of this name on the supplied file at a time.
     */
    public static final String PROPERTY_CONCURRENT_EXECUTION = "concurrentExec";
    /**
     * This property has similar meaning as {@link PROPERTY_CONCURRENT_EXECUTION}, but
     * contains pairs of command name and integer property enclosed in quotes and delimited
     * by commas. The integer concurrent execution property is valid only with respect
     * to the associated command name.
     * <p>i.e.: "ADD 4", "STATUS 1"
     */
    public static final String PROPERTY_CONCURRENT_EXECUTION_WITH = "concurrentExecWith";
    
    /**
     * A boolean property, if true, the command can act on files.
     */
    public static final String PROPERTY_ON_FILE = "onFile";
    /**
     * A boolean property, if true, the command can act on folders.
     */
    public static final String PROPERTY_ON_DIR = "onDir";
    /**
     * A boolean property, if true, the command can act on the root of the filesystem,
     * if false, the command can act everywhere but on the root.
     */
    public static final String PROPERTY_ON_ROOT = "onRoot";

    /**
     * When this property is true, refresh of the current folder is performed after successfull execution of this command.
     */
    public static final String PROPERTY_REFRESH_CURRENT_FOLDER = "refreshCurrentFolder";
    /**
     * When this property is true, refresh of the parent folder is performed after successfull execution of this command.
     */
    public static final String PROPERTY_REFRESH_PARENT_FOLDER = "refreshParentFolder";
    /**
     * Pattern that when matched from the exec string, the refresh is performed recursively.
     */
    public static final String PROPERTY_REFRESH_RECURSIVELY_PATTERN_MATCHED = "refreshRecursivelyPatternMatched";
    /**
     * Pattern that when not matched from the exec string, the refresh is performed recursively.
     */
    public static final String PROPERTY_REFRESH_RECURSIVELY_PATTERN_UNMATCHED = "refreshRecursivelyPatternUnmatched";
    /*
     * An integer property. If a command fails and some refresh is scheduled after finish of this command,
     * the value of this property will be inspected to find whether the refresh command should be performed.
     * The value of this property can be one of REFRESH_ON_FAIL_FALSE, REFRESH_ON_FAIL_TRUE, REFRESH_ON_FAIL_TRUE_ON_FOLDERS.
     */
    public static final String PROPERTY_REFRESH_ON_FAIL = "refreshOnFail";
    
    /**
     * If non empty, the user will be asked to confirm this message prior to command execution.
     */
    public static final String PROPERTY_CONFIRMATION_MSG = "confirmationMsg";
    /**
     * If non empty, the user will be notified, that the command has finished successfully.
     * The content of this message will be displayed.
     */
    public static final String PROPERTY_NOTIFICATION_SUCCESS_MSG = "notificationSuccessMsg";
    /**
     * If non empty, the user will be notified, that the command has failed.
     * The content of this message will be displayed.
     */
    public static final String PROPERTY_NOTIFICATION_FAIL_MSG = "notificationFailMsg";
    
    /**
     * Value of this property should be an array of Strings.
     * It is set and filled from user input in the filesystem.
     */
    public static final String PROPERTY_USER_PARAMS = "userParams";
    
    /**
     * A boolean property, if true a window which shows the output of the command will pop-up.
     */
    public static final String PROPERTY_DISPLAY_PLAIN_OUTPUT = "display";

    /**
     * Whether to run this command on all files or ignore unimportant.
     * If true, all files including unimportant will be processed, if false only files which are important will be processed.
     */
    public static final String PROPERTY_PROCESS_ALL_FILES = "processAllFiles";
    
    /**
     * A boolean property, if true the command can run on multiple files. The executor will
     * be started only once for all selected files.
     */
    public static final String PROPERTY_RUN_ON_MULTIPLE_FILES = "runOnMultipleFiles";
    
    /**
     * A boolean property, if true the command can run on multiple files inside a single folder. The executor will
     * be started only once for all selected files in a folder. If this property is true, PROPERTY_RUN_ON_MULTIPLE_FILES
     * has no effect.
     */
    public static final String PROPERTY_RUN_ON_MULTIPLE_FILES_IN_FOLDER = "runOnMultipleFilesInFolder";
    
    /**
     * A boolean property, if true the files that are to be processed are sorted
     * by the hierarchical order prior calling the command (the child file never goes before its parent).
     */
    public static final String PROPERTY_NEEDS_HIERARCHICAL_ORDER = "needsHierarchicalOrder";
    
    /**
     * When true, the command is supposed to return fail status even when it finish normally.
     * The user will not be warned, that this command has failed.
     * I.e. DIFF command fails when it finds some differences, but the user should not be
     * notified, that something has failed.
     */
    public static final String PROPERTY_IGNORE_FAIL = "ignoreFail";

    /**
     * An integer property that means the number of revisions the command can be applied.
     * When zero, the command will be applied to the whole version-controlled files.
     * When positive, the command will be applied to the appropriate number of individual revisions.
     * When negative, the command will be applied to any number of individual revisions.
     */
    public static final String PROPERTY_NUM_REVISIONS = "numRevisions";

    /**
     * Whether the command changes the number of revisions.
     * If true, the command may add or remove some revisions.
     */
    public static final String PROPERTY_CHANGING_NUM_REVISIONS = "changingNumRevisions";
    /**
     * Whether the command changes one specific revision.
     * If true the command may add or remove not more than one revision.
     * Property {@link PROPERTY_CHANGED_REVISION_VAR_NAME} then should specify the number of revision
     * that has changed to speed up the revision update.
     */
    public static final String PROPERTY_CHANGING_REVISION = "changingRevision";
    /**
     * The name of the variable that specifies the changed revision.
     * This property is used only when property {@link PROPERTY_CHANGING_REVISION} is true.
     */
    public static final String PROPERTY_CHANGED_REVISION_VAR_NAME = "changedRevisionVarName";
    
    /**
     * Whether the command will appear on the actions popup menu.
     */
    public static final String PROPERTY_HIDDEN = "hidden";
    /**
     * A string property, that can contain a list of file status attributes. If non empty,
     * the command menu item will be disabled on files whose status is one of the listed
     * status attributes in this property value.
     */
    public static final String PROPERTY_DISABLED_ON_STATUS = "disabledOnStatus";
    
    /**
     * A boolean property that describes if the command supports advanced mode in filesystem.
     * Such commands are marked by a + sign on the popup (when pressing CTRL key) 
     * Information wheather the CTRL key was pressed is stored in the variable passed to the executor.
     * (
     */
    public static final String PROPERTY_SUPPORTS_ADVANCED_MODE = "supportsAdvancedMode";

    /** All execution of this command can be done concurrently with others.
     */
    public static final int EXEC_CONCURRENT_ALL = 0;
    /** Serial execution is guaranteed on each file.
     * This command will not run on a file if there is another command
     * already running at the same time.
     */
    public static final int EXEC_SERIAL_ON_FILE = 1;
    /** Serial execution is guaranteed inside a single package.
     * This command will not run inside a single package if there is another command
     * already running at the same time.
     */
    public static final int EXEC_SERIAL_ON_PACKAGE = 2;
    /** Serial execution of commands with respect to commands running on a parent folders.
     * The command will not run on a child before
     * all commands on parent folders are finished. This flag is necessary i.e. for ADD command.
     */
    public static final int EXEC_SERIAL_WITH_PARENT = 4;
    /** Serial execution of commands of this name.
     * That means that only one command of this name will run at a given time.
     * It can be restricted to a single file or a single package by using <code>EXEC_SERIAL_ON_FILE</code>
     * or <code>EXEC_SERIAL_ON_PACKAGE</code> properties.
     */
    public static final int EXEC_SERIAL_OF_COMMAND = 8;
    /** Serial execution of all commands. This command will not run if at least one
     * command is already running.
     */
    public static final int EXEC_SERIAL_ALL = -1;
    
    /** When this constant is the value of PROPERTY_REFRESH_ON_FAIL command property,
     * the refresh command will be performed after the command fails.
     */
    public static final Integer REFRESH_ON_FAIL_FALSE = new Integer(0);
    /** When this constant is the value of PROPERTY_REFRESH_ON_FAIL command property,
     * the refresh command will be always performed even when the command fails.
     */
    public static final Integer REFRESH_ON_FAIL_TRUE = new Integer(1);
    /** When this constant is the value of PROPERTY_REFRESH_ON_FAIL command property,
     * the refresh command will be performed after the command fails only when
     * the command acts on a folder.
     */
    public static final Integer REFRESH_ON_FAIL_TRUE_ON_FOLDERS = new Integer(2);
    
    /**
     * Get the name of the command.
     */
    public String getName();
    /**
     * Set the name of the command.
     */
    public void setName(String name);
    /**
     * Get the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public String getDisplayName();
    /**
     * Set the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public void setDisplayName(String displayName);
        
    /**
     * Get the names of all supported properties.
     * @return the array of properties names
     */
    public String[] getPropertyNames();

    /**
     * Get the additional command property.
     * @param propertyName the name of the property
     * @return the value of the property
     */
    public Object getProperty(String propertyName);

    /**
     * Set the additional property to the command.
     * @param propertyName the name of the property
     * @param value the value of the property. The <code>null</code> value should unset the property
     */
    public void setProperty(String propertyName, Object value);
        
}

