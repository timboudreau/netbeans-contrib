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
     * The name of the command which is used to get a specific revision to open.
     */
    public static final String NAME_REVISION_OPEN = "REVISION_OPEN";
    
    /*
     * Label of the command which will not appear in the popup menu.
     *
    public static final String DISPLAY_NAME_NOT_SHOW = "NO_LABEL";
     */
    
    /*
     * The object denoting a separator in the list of children commands.
     *
    public static final Object COMMAND_SEPARATOR = new String("SEPARATOR");
     */

    /**
     * Command may set this property which will be visible on the VariableInputDialog.
     * Can be used to tell the user about the specific command appearance.
     */
    public static final String PROPERTY_ADVANCED_NAME = "advancedName";
    
    /**
     * This property denotes the executable string. This string may contain
     * any number of variables that are expanded prior to execution.
     */
    public static final String PROPERTY_EXEC = "exec";
    /**
     * This Integer property can contain any combination of EXEC_* constants delimeted by OR operator.
     * <p>i.e.: (EXEC_SERIAL_ON_FILE | EXEC_SERIAL_OF_COMMAND) will run only one command of this name on the supplied file at a time.
     */
    public static final String PROPERTY_CONCURRENT_EXECUTION = "concurrentExec";
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
    /*
     * A boolean property, if true, the command can act everywhere but on the root.
     *
    public static final String PROPERTY_NOT_ON_ROOT = "notOnRoot";
     */
    /*
     * Display the textual output of the command.
     *
    public static final String PROPERTY_DISPLAY = "display";
     */
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
    
    /**
     * If non empty, the user will be asked to confirm this message prior to command execution.
     */
    public static final String PROPERTY_CONFIRMATION_MSG = "confirmationMsg";
    
    /**
     * Value of this property should be an array of Strings.
     * It is set and filled from user input in the filesystem.
     */
    public static final String PROPERTY_USER_PARAMS = "userParams";
    
    /**
     * Whether to run this command on all files or ignore unimportant.
     * If true, all files including unimportant will be processed, if false only files which are important will be processed.
     */
    public static final String PROPERTY_PROCESS_ALL_FILES = "processAllFiles";

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

    /** All execution of this command can be done concurrently with others.
     */
    public static final int EXEC_CONCURRENT_ALL = 0;
    /** Serial execution of commands is guaranteed on each file.
     * That means that two commands of this name will not run on a single file at the same time.
     */
    public static final int EXEC_SERIAL_ON_FILE = 1;
    /** Serial execution of commands is guaranteed in each package.
     * That means that two commands of this name will not run inside a single package at the same time.
     */
    public static final int EXEC_SERIAL_ON_PACKAGE = 2;
    /** Serial execution of commands of this name.
     * That means that only one command of this name will run at a given time.
     * It can be restricted to a single file or a single package by using <code>EXEC_SERIAL_ON_FILE</code>
     * or <code>EXEC_SERIAL_ON_PACKAGE</code> properties.
     */
    public static final int EXEC_SERIAL_OF_COMMAND = 4;
    /** Serial execution of all commands.
     */
    public static final int EXEC_SERIAL_ALL = -1;
    
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
     * @param value the value of the property
     */
    public void setProperty(String propertyName, Object value);
    
    /*
     * This method is called after read of all commands from an external source.
     * Allows some more settings to be done. This method should be static.
     * @param cmds the <code>Vector</code> containing all read commands. 
     *
    public void readFinished(java.util.Vector cmds);
     */
    
}

