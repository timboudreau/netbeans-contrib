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

import org.openide.util.NbBundle;
import org.openide.filesystems.*;

import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.settings.RefreshModePropertyEditor;
import org.netbeans.modules.vcscore.VcsFileSystem;

/** BeanInfo for CommandLineVcsFileSystem.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class CommandLineVcsFileSystemBeanInfo extends SimpleBeanInfo {

    /** Array of property descriptors. */
    private static PropertyDescriptor[] desc;

    /** Icon for VCS filesystem. */
    private static java.awt.Image icon;
    private static java.awt.Image icon32;

    static {
        PropertyDescriptor rootDirectory=null;
        PropertyDescriptor debug=null;
        PropertyDescriptor variables=null;
        PropertyDescriptor commands=null;
        PropertyDescriptor cacheId=null;
        PropertyDescriptor config=null;
        PropertyDescriptor lock=null;
        PropertyDescriptor lockPrompt=null;
        PropertyDescriptor acceptUserParams = null;
        PropertyDescriptor runRefreshCommand = null;
        PropertyDescriptor processAllFiles = null;
        PropertyDescriptor annotationPattern = null;
        PropertyDescriptor autoRefresh = null;
        PropertyDescriptor notification = null;
        PropertyDescriptor hideShadowFiles = null;

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

            lock=new PropertyDescriptor
                 (VcsFileSystem.PROP_CALL_LOCK, CommandLineVcsFileSystem.class, "isLockFilesOn", "setLockFilesOn"); // NOI18N
            lock.setExpert(true);

            lockPrompt=new PropertyDescriptor
                       (VcsFileSystem.PROP_CALL_LOCK_PROMPT, CommandLineVcsFileSystem.class, "isPromptForLockOn", "setPromptForLockOn"); // NOI18N
            lockPrompt.setExpert(true);
            acceptUserParams = new PropertyDescriptor
                               (VcsFileSystem.PROP_EXPERT_MODE, CommandLineVcsFileSystem.class, "isExpertMode", "setExpertMode"); // NOI18N
            runRefreshCommand = new PropertyDescriptor
                               (GeneralVcsSettings.PROP_OFFLINE, CommandLineVcsFileSystem.class, "isOffLine", "setOffLine"); // NOI18N
            processAllFiles = new PropertyDescriptor
                               (VcsFileSystem.PROP_PROCESS_UNIMPORTANT_FILES, CommandLineVcsFileSystem.class, "isProcessUnimportantFiles", "setProcessUnimportantFiles"); // NOI18N
            annotationPattern = new PropertyDescriptor
                               (VcsFileSystem.PROP_ANNOTATION_PATTERN, CommandLineVcsFileSystem.class, "getAnnotationPattern", "setAnnotationPattern"); // NOI18N
            autoRefresh = new PropertyDescriptor
                               (GeneralVcsSettings.PROP_AUTO_REFRESH, CommandLineVcsFileSystem.class, "getAutoRefresh", "setAutoRefresh"); // NOI18N
            autoRefresh.setPropertyEditorClass(RefreshModePropertyEditor.class);
            notification = new PropertyDescriptor
                               (VcsFileSystem.PROP_COMMAND_NOTIFICATION, CommandLineVcsFileSystem.class, "isCommandNotification", "setCommandNotification"); // NOI18N
            hideShadowFiles = new PropertyDescriptor
                               (GeneralVcsSettings.PROP_HIDE_SHADOW_FILES, CommandLineVcsFileSystem.class, "isHideShadowFiles", "setHideShadowFiles"); // NOI18N
            hideShadowFiles.setExpert(true);


            desc = new PropertyDescriptor[] {
                       rootDirectory, debug, variables, commands, cacheId, config,
                       lock, lockPrompt, acceptUserParams, runRefreshCommand, processAllFiles,
                       annotationPattern, autoRefresh, notification, hideShadowFiles
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
            lock.setDisplayName               (bundle.getString("PROP_lock"));
            lock.setShortDescription          (bundle.getString("HINT_lock"));
            lockPrompt.setDisplayName         (bundle.getString("PROP_lockPrompt"));
            lockPrompt.setShortDescription    (bundle.getString("HINT_lockPrompt"));
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

        } catch (IntrospectionException ex) {
            ex.printStackTrace ();
        }
    }

    /* Provides the VCSFileSystem's icon */
    public java.awt.Image getIcon(int type) {
        if (icon == null) {
            icon = loadImage("/org/netbeans/modules/vcscore/vcs2.gif"); // NOI18N
            icon32 = icon;
        }
        if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) || (type == java.beans.BeanInfo.ICON_MONO_16x16))
            return icon;
        else
            return icon32;
    }

    /* Descriptor of valid properties
    * @return array of properties
    */
    public PropertyDescriptor[] getPropertyDescriptors () {
        return desc;
    }


    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bd = new BeanDescriptor(CommandLineVcsFileSystem.class,
                                               org.netbeans.modules.vcs.advanced.VcsCustomizer.class);
        bd.setValue(VcsFileSystem.VCS_PROVIDER_ATTRIBUTE, new Boolean(true));
        return bd;
    }

}

/*
* <<Log>>
*  18   Gandalf   1.17        1/27/00  Martin Entlicher Locking property added.
*  17   Gandalf   1.16        11/24/99 Martin Entlicher 
*  16   Gandalf   1.15        10/25/99 Pavel Buzek     copyright
*  15   Gandalf   1.14        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  14   Gandalf   1.13        9/30/99  Pavel Buzek     
*  13   Gandalf   1.12        9/8/99   Pavel Buzek     class model changed, 
*       customization improved, several bugs fixed
*  12   Gandalf   1.11        8/31/99  Pavel Buzek     
*  11   Gandalf   1.10        8/7/99   Ian Formanek    Icon for VCS Filesystem
*  10   Gandalf   1.9         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  9    Gandalf   1.8         5/19/99  Michal Fadljevic 
*  8    Gandalf   1.7         5/14/99  Michal Fadljevic 
*  7    Gandalf   1.6         5/4/99   Michal Fadljevic 
*  6    Gandalf   1.5         5/4/99   Michal Fadljevic 
*  5    Gandalf   1.4         4/30/99  Michal Fadljevic 
*  4    Gandalf   1.3         4/29/99  Michal Fadljevic 
*  3    Gandalf   1.2         4/26/99  Michal Fadljevic 
*  2    Gandalf   1.1         4/21/99  Michal Fadljevic 
*  1    Gandalf   1.0         4/15/99  Michal Fadljevic 
* $
*/
