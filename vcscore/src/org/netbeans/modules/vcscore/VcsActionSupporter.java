/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

import java.lang.ref.WeakReference;
import java.util.*;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.*;
import org.openide.util.RequestProcessor;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.MessagingCommand;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.actions.CommandActionSupporter;
import org.netbeans.modules.vcscore.actions.VersioningExplorerAction;
import org.netbeans.modules.vcscore.actions.VersioningAction;
import org.netbeans.modules.vcscore.actions.GeneralCommandAction;
import org.netbeans.modules.vcscore.commands.ActionCommandSupport;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * TODO remove it's transitively unused.
 *
 * @author  Milos Kleint
 */
public class VcsActionSupporter extends CommandActionSupporter implements java.io.Serializable {

    /** The map of action classes and sets of it's commands.
     * Theoretically one action can have more than one associated command,
     * each enabled on different set of file objects
     * (e.g. one on files and second on folders). If more than one command
     * is enabled on the given set of file objects, one is randomly picked.
     * It's the command's owner responsibility to set actions so that it will
     * have sense.
     */
    private HashMap commandMap;
    
    static final long serialVersionUID = -613064726657052221L;
    
    /** Creates new VcsActionSupporter */
    public VcsActionSupporter() {
        //fileSystem = new WeakReference(filesystem);
        commandMap = new HashMap();
    }
    
    public void addSupportForAction(Class actionClass, CommandSupport commandSupp) {
        if (!(commandSupp instanceof ActionCommandSupport)) throw new IllegalArgumentException();
        HashSet commandsNamesSet = (HashSet) commandMap.get(actionClass);
        if (commandsNamesSet == null) {
            commandsNamesSet = new HashSet();
            commandMap.put(actionClass, commandsNamesSet);
        }
        commandsNamesSet.add(commandSupp);
        //commandMap.put(actionClass, commandName);
    }
    
    public void removeSupportForAction(Class actionClass) {
        commandMap.remove(actionClass);
    }
    
    public boolean isEnabled(GeneralCommandAction action, FileObject[] fileObjects) {
        if (fileObjects == null || fileObjects.length == 0) {
            return false;
        }
        if (action.getClass().equals(VersioningExplorerAction.class)) {
            return true;
        }
        if (action.getClass().equals(VersioningAction.class)) {
            return true;
        }
        HashSet cmdSet = (HashSet) commandMap.get(action.getClass());
        if (cmdSet == null) {
            return false;
        }
        fileObjects = VcsUtilities.convertFileObjects(fileObjects);
        for (Iterator it = cmdSet.iterator(); it.hasNext(); ) {
            CommandSupport cmdSupp = (CommandSupport) it.next();
            if (enabledFileObjects(cmdSupp, fileObjects) != null) return true;
        }
        return false;
    }
    
    /** @return the FileObjects for which the command is enabled or <code>null</code>. */
    private FileObject[] enabledFileObjects(CommandSupport cmdSupp, FileObject[] fileObjects) {
        if (!(cmdSupp instanceof ActionCommandSupport)) return null;
        return cmdSupp.getApplicableFiles(fileObjects);
    }

    public void performAction(final GeneralCommandAction action, final FileObject[] fileObjects) {
        if (fileObjects == null || fileObjects.length == 0) {
            return;
        }
        final HashSet cmdSet = (HashSet) commandMap.get(action.getClass());
        if (cmdSet == null) {
            return;
        }
        // Leave AWT Event Queue ASAP. This also prevents deadlock with VcsManager.showCustomizer().
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                FileObject[] fos = VcsUtilities.convertFileObjects(fileObjects);
                for (Iterator it = cmdSet.iterator(); it.hasNext(); ) {
                    CommandSupport cmdSupport = (CommandSupport) it.next();
                    FileObject[] applicableFOs = enabledFileObjects(cmdSupport, fos);
                    if (applicableFOs != null) {
                        Command cmd = cmdSupport.createCommand();
                        cmd.setFiles(applicableFOs);
                        if (cmd instanceof MessagingCommand) {
                            String description = (String) action.getValue(GeneralCommandAction.GROUP_DESCRIPTION_PROP);
                            if (description != null) {
                                ((MessagingCommand) cmd).setMessage(description);
                            }
                        }
                        cmd.setGUIMode(true);
                        if (VcsManager.getDefault().showCustomizer(cmd)) {
                            cmd.execute();
                        }
                        break;
                    }
                }
            }
        });
    }

    
    /** Remove the files for which the command is disabled *
    private static Table removeDisabled(FileStatusProvider statusProvider, Table files, VcsCommand cmd) {
        if (statusProvider == null) return files;
        String disabledStatus = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS);
        if (disabledStatus == null) return files;
        Table remaining = new Table();
        for (Enumeration enum = files.keys(); enum.hasMoreElements(); ) {
            String name = (String) enum.nextElement();
            String status = statusProvider.getFileStatus(name);
            boolean disabled = VcsUtilities.isSetContainedInQuotedStrings(
                disabledStatus, Collections.singleton(status));
            if (!disabled) {
                remaining.put(name, files.get(name));
            }
        }
        return remaining;
    }
            */
    /**
     * Test if some of the selected nodes are directories.
     * @return <code>true</code> if some of the selected nodes are directories,
     *         <code>false</code> otherwise.
     *
    protected boolean isOnDirectory(Collection fos) {
        boolean is = false;
        if (fos != null) {
            for (Iterator it = fos.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo.isFolder()) is = true;
            }
            //return false;
        }
        return is && !isOnRoot(fos);
    }

    /**
     * Test if some of the selected nodes are files.
     * @return <code>true</code> if some of the selected nodes are files,
     *         <code>false</code> otherwise.
     *
    protected boolean isOnFile(Collection fos) {
        if (fos != null) {
            for (Iterator it = fos.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                //System.out.println("  fo = "+fo);
                if (!fo.isFolder()) return true;
            }
        }
        return false;
    }

    /**
     * Test if one of the selected nodes is the root node.
     * @return <code>true</code> if at least one of the selected nodes is the root node,
     *         <code>false</code> otherwise.
     *
    protected boolean isOnRoot(Collection fos) {
        if (fos != null) {
           for (Iterator it = fos.iterator(); it.hasNext(); ) {
           FileObject fo = (FileObject) it.next();
                if (fo.getPackageNameExt('/', '.').length() == 0) return true;
           }
        }
       return false;
    }

    private Set getSelectedFileStatusAttributes(Set fileObjects) {
        Set statuses = new HashSet();
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        FileStatusProvider statusProv = fileSystem.getStatusProvider();
        boolean processAll = fileSystem.isProcessUnimportantFiles();
        if (statusProv != null) {
            for (Iterator it = fileObjects.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                String path = fo.getPackageNameExt('/', '.');
                if (processAll || fileSystem.isImportant(path)) {
                    String status = statusProv.getFileStatus(path);
                    if (status != null) statuses.add(status);
                }
            }
        }
        return statuses;
    }
     */
    
    private String getCommandActionDisplayName(GeneralCommandAction action) {
        HashSet cmdSet = (HashSet) commandMap.get(action.getClass());
        if (cmdSet == null) {
            return null;
        }
        for (Iterator it = cmdSet.iterator(); it.hasNext(); ) {
            CommandSupport cmdSupp = (CommandSupport) it.next();
            //if (isEnabled(cmdName, fileObjects)) {
            // Can not recognize the selected fileObjects => taking just the first command
            if (cmdSupp instanceof ActionCommandSupport) {
                return ((ActionCommandSupport) cmdSupp).getActionDisplayName();
            }
            /*
                VcsCommand cmd = ((VcsFileSystem) fileSystem.get()).getCommand(cmdName);
                if (cmd != null) {
                    String name = (String) cmd.getProperty(VcsCommand.PROPERTY_GENERAL_COMMAND_ACTION_DISPLAY_NAME);
                    if (name != null) {
                        return name;
                    }
                }
             */
            //}
        }
        return null;
    }

    /**
     * If the supporter enables the action, it can then add supporter-specific 
     * description to the toolbar tooltip. (use with caution.) <B>Experimental</B>
     */
    public String getToolBarDisplayName(GeneralCommandAction action) {
        String name = getCommandActionDisplayName(action);
        if (name != null) {
            return name;
        } else {
            return super.getToolBarDisplayName(action);
        }
    }

    /**
     * If the supporter enables the action, it can then add supporter-specific 
     * description to the menu name. (use with caution.) <B>Experimental</B>
     */
    
    public String getMenuDisplayName(GeneralCommandAction action) {
        String name = getCommandActionDisplayName(action);
        if (name != null) {
            return name;
        } else {
            return super.getMenuDisplayName(action);
        }
    }

    /**
     * If the supporter enables the action, it can then add supporter-specific 
     * description to the popup menu name. (use with caution.) <B>Experimental</B>
     */
    
    public String getPopupDisplayName(GeneralCommandAction action) {
        String name = getCommandActionDisplayName(action);
        if (name != null) {
            return name;
        } else {
            return getPopupDisplayName(action);
        }
    }
    
}
