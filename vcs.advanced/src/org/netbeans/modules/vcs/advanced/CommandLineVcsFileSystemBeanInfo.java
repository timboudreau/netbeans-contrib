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
import java.beans.*;
import java.util.ResourceBundle;

import org.openide.filesystems.*;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.settings.RefreshModePropertyEditor;
import org.netbeans.modules.vcscore.VcsFileSystem;

/** BeanInfo for CommandLineVcsFileSystem.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class CommandLineVcsFileSystemBeanInfo extends SimpleBeanInfo {

    /* Descriptor of valid properties
    * @return array of properties
    */
    public PropertyDescriptor[] getPropertyDescriptors () {
        PropertyDescriptor[] desc;
        PropertyDescriptor rootDirectory=null;
        PropertyDescriptor debug=null;
        PropertyDescriptor variables=null;
        PropertyDescriptor commands=null;
        PropertyDescriptor cacheId=null;
        PropertyDescriptor config=null;
        //PropertyDescriptor lock=null;
        //PropertyDescriptor lockPrompt=null;
        PropertyDescriptor acceptUserParams = null;
        PropertyDescriptor runRefreshCommand = null;
        PropertyDescriptor processAllFiles = null;
        PropertyDescriptor annotationPattern = null;
        PropertyDescriptor autoRefresh = null;
        PropertyDescriptor notification = null;
        PropertyDescriptor hideShadowFiles = null;
        PropertyDescriptor ignoredGarbageFiles = null;
        PropertyDescriptor createBackupFiles = null;
        PropertyDescriptor filterBackupFiles = null;
        PropertyDescriptor rememberPassword = null;
        PropertyDescriptor shortStatuses = null;
        PropertyDescriptor refreshTime = null;

        try {
            rootDirectory=new PropertyDescriptor
                          (VcsFileSystem.PROP_ROOT, CommandLineVcsFileSystem.class, "getRootDirectory", null); // NOI18N
            debug=new PropertyDescriptor
                  (VcsFileSystem.PROP_DEBUG,CommandLineVcsFileSystem.class, "getDebug", "setDebug"); // NOI18N

            variables=new PropertyDescriptor
                      (VcsFileSystem.PROP_VARIABLES, CommandLineVcsFileSystem.class, "getVariables", "setVariables"); // NOI18N
            variables.setPropertyEditorClass (org.netbeans.modules.vcs.advanced.UserVariablesEditor.class);
            variables.setExpert(true);

            commands=new PropertyDescriptor
                     (VcsFileSystem.PROP_COMMANDS, CommandLineVcsFileSystem.class, "getCommands", "setCommands"); // NOI18N
            commands.setPropertyEditorClass (org.netbeans.modules.vcs.advanced.UserCommandsEditor.class);
            commands.setExpert(true);

            cacheId=new PropertyDescriptor
                    ("cacheId", CommandLineVcsFileSystem.class, "getCacheId", null); // NOI18N
            cacheId.setExpert(true);

            config=new PropertyDescriptor
                   ("config", CommandLineVcsFileSystem.class, "getConfig", null); // NOI18N
            /*
            lock=new PropertyDescriptor
                 (VcsFileSystem.PROP_CALL_LOCK, CommandLineVcsFileSystem.class, "isLockFilesOn", "setLockFilesOn"); // NOI18N
            lock.setExpert(true);

            lockPrompt=new PropertyDescriptor
                       (VcsFileSystem.PROP_CALL_LOCK_PROMPT, CommandLineVcsFileSystem.class, "isPromptForLockOn", "setPromptForLockOn"); // NOI18N
            lockPrompt.setExpert(true);
             */
            acceptUserParams = new PropertyDescriptor
                               (VcsFileSystem.PROP_EXPERT_MODE, CommandLineVcsFileSystem.class, "isExpertMode", "setExpertMode"); // NOI18N
            runRefreshCommand = new PropertyDescriptor
                               (GeneralVcsSettings.PROP_OFFLINE, CommandLineVcsFileSystem.class, "isOffLine", "setOffLine"); // NOI18N
            processAllFiles = new PropertyDescriptor
                               (VcsFileSystem.PROP_PROCESS_UNIMPORTANT_FILES, CommandLineVcsFileSystem.class, "isProcessUnimportantFiles", "setProcessUnimportantFiles"); // NOI18N
            annotationPattern = new PropertyDescriptor
                               (VcsFileSystem.PROP_ANNOTATION_PATTERN, CommandLineVcsFileSystem.class, "getAnnotationPattern", "setAnnotationPattern"); // NOI18N
            annotationPattern.setPropertyEditorClass(CommandLineAnnotPatternEditor.class);
            autoRefresh = new PropertyDescriptor
                               (GeneralVcsSettings.PROP_AUTO_REFRESH, CommandLineVcsFileSystem.class, "getAutoRefresh", "setAutoRefresh"); // NOI18N
            autoRefresh.setPropertyEditorClass(RefreshModePropertyEditor.class);
            notification = new PropertyDescriptor
                               (VcsFileSystem.PROP_COMMAND_NOTIFICATION, CommandLineVcsFileSystem.class, "isCommandNotification", "setCommandNotification"); // NOI18N
            hideShadowFiles = new PropertyDescriptor
                               (GeneralVcsSettings.PROP_HIDE_SHADOW_FILES, CommandLineVcsFileSystem.class, "isHideShadowFiles", "setHideShadowFiles"); // NOI18N
            hideShadowFiles.setExpert(true);
            ignoredGarbageFiles = new PropertyDescriptor
                                (VcsFileSystem.PROP_IGNORED_GARBAGE_FILES, CommandLineVcsFileSystem.class, "getIgnoredGarbageFiles", "setIgnoredGarbageFiles"); // NOI18N
            ignoredGarbageFiles.setExpert(true);
            createBackupFiles = new PropertyDescriptor
                                ("createBackupFiles", CommandLineVcsFileSystem.class, "isCreateBackupFiles", "setCreateBackupFiles"); // NOI18N
            createBackupFiles.setExpert(true);
            filterBackupFiles = new PropertyDescriptor
                                ("filterBackupFiles", CommandLineVcsFileSystem.class, "isFilterBackupFiles", "setFilterBackupFiles"); // NOI18N
            filterBackupFiles.setExpert(true);
            rememberPassword = new PropertyDescriptor
                                ("rememberPassword", CommandLineVcsFileSystem.class, "isRememberPassword", "setRememberPassword"); // NOI18N
            rememberPassword.setExpert(true);
            shortStatuses = new PropertyDescriptor
                                (CommandLineVcsFileSystem.PROP_SHORT_FILE_STATUSES, CommandLineVcsFileSystem.class, "isShortFileStatuses", "setShortFileStatuses"); // NOI18N
            refreshTime = new PropertyDescriptor
                                ("refreshTime", CommandLineVcsFileSystem.class, "getCustomRefreshTime", "setCustomRefreshTime"); // NOI18N
            refreshTime.setExpert(true);


            desc = new PropertyDescriptor[] {
                       rootDirectory, debug, variables, commands, cacheId, config,
                       acceptUserParams, runRefreshCommand, processAllFiles,
                       annotationPattern, autoRefresh, notification, hideShadowFiles,
                       ignoredGarbageFiles, createBackupFiles, filterBackupFiles,
                       rememberPassword, shortStatuses, refreshTime
                   };

            ResourceBundle bundle = NbBundle.getBundle (CommandLineVcsFileSystemBeanInfo.class);
            ResourceBundle bundleSettings = NbBundle.getBundle (GeneralVcsSettings.class);

            rootDirectory.setDisplayName      (bundle.getString("PROP_rootDirectory"));
            rootDirectory.setShortDescription (bundle.getString("HINT_rootDirectory"));
            debug.setDisplayName              (bundle.getString("PROP_debug"));
            debug.setShortDescription         (bundle.getString("HINT_debug"));
            variables.setDisplayName          (bundle.getString("PROP_variables"));
            variables.setShortDescription     (bundle.getString("HINT_variables"));
            commands.setDisplayName           (bundle.getString("PROP_commands"));
            commands.setShortDescription      (bundle.getString("HINT_commands"));
            cacheId.setDisplayName            (bundle.getString("PROP_cacheId"));
            cacheId.setShortDescription       (bundle.getString("HINT_cacheId"));
            config.setDisplayName             (bundle.getString("PROP_config"));
            config.setShortDescription        (bundle.getString("HINT_config"));
            //lock.setDisplayName               (bundle.getString("PROP_lock"));
            //lock.setShortDescription          (bundle.getString("HINT_lock"));
            //lockPrompt.setDisplayName         (bundle.getString("PROP_lockPrompt"));
            //lockPrompt.setShortDescription    (bundle.getString("HINT_lockPrompt"));
            acceptUserParams.setDisplayName   (bundle.getString("PROP_acceptUserParams"));
            acceptUserParams.setShortDescription(bundle.getString("HINT_acceptUserParams"));
            runRefreshCommand.setDisplayName  (bundleSettings.getString("PROP_offline"));
            runRefreshCommand.setShortDescription(bundleSettings.getString("HINT_offline"));
            processAllFiles.setDisplayName    (bundle.getString("PROP_processAllFiles"));
            processAllFiles.setShortDescription(bundle.getString("HINT_processAllFiles"));
            annotationPattern.setDisplayName  (bundle.getString("PROP_annotationPattern"));
            annotationPattern.setShortDescription(bundle.getString("HINT_annotationPattern"));
            autoRefresh.setDisplayName        (bundleSettings.getString("PROP_autoRefresh"));
            autoRefresh.setShortDescription   (bundleSettings.getString("HINT_autoRefresh"));
            notification.setDisplayName       (bundle.getString("PROP_commandNotification"));
            notification.setShortDescription  (bundle.getString("HINT_commandNotification"));
            hideShadowFiles.setDisplayName    (bundleSettings.getString("PROP_hideShadowFiles"));
            hideShadowFiles.setShortDescription(bundleSettings.getString("HINT_hideShadowFiles"));
            ignoredGarbageFiles.setDisplayName(bundle.getString("PROP_ignoredGarbageFiles"));
            ignoredGarbageFiles.setShortDescription(bundle.getString("HINT_ignoredGarbageFiles"));
            createBackupFiles.setDisplayName  (bundle.getString("PROP_createBackupFiles"));
            createBackupFiles.setShortDescription(bundle.getString("HINT_createBackupFiles"));
            filterBackupFiles.setDisplayName  (bundle.getString("PROP_filterBackupFiles"));
            filterBackupFiles.setShortDescription(bundle.getString("HINT_filterBackupFiles"));
            rememberPassword.setDisplayName   (bundle.getString("PROP_rememberPassword"));
            rememberPassword.setShortDescription(bundle.getString("HINT_rememberPassword"));
            shortStatuses.setDisplayName      (bundle.getString("PROP_shortFileStatuses"));
            shortStatuses.setShortDescription (bundle.getString("HINT_shortFileStatuses"));
            refreshTime.setDisplayName        (bundle.getString("PROP_refreshTime"));
            refreshTime.setShortDescription   (bundle.getString("HINT_refreshTime"));

        } catch (IntrospectionException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
            desc = null;
        }
        return desc;
    }

    /* Provides the VCSFileSystem's icon */
    public java.awt.Image getIcon(int type) {
        switch (type) {
            case ICON_COLOR_16x16:
                return Utilities.loadImage("org/netbeans/modules/vcs/advanced/vcsGeneric.gif"); // NOI18N
        }
        return null;
    }

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bd = new BeanDescriptor(CommandLineVcsFileSystem.class,
                                               org.netbeans.modules.vcs.advanced.VcsCustomizer.class);
        bd.setValue(VcsFileSystem.VCS_PROVIDER_ATTRIBUTE, Boolean.TRUE);
        bd.setValue(VcsFileSystem.VCS_FILESYSTEM_ICON_BASE, "org/netbeans/modules/vcs/advanced/vcsGeneric"); // NOI18N
        bd.setValue ("helpID", CommandLineVcsFileSystem.class.getName ()); // NOI18N
        bd.setValue ("propertiesHelpID", CommandLineVcsFileSystem.class.getName() + "_properties"); // NOI18N
        bd.setValue ("expertHelpID", CommandLineVcsFileSystem.class.getName() + "_expert"); // NOI18N
        return bd;
    }

}

