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

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.TopManager;
import org.netbeans.modules.vcscore.caching.*;
import org.netbeans.modules.vcscore.actions.CommandActionSupporter;
import org.netbeans.modules.vcscore.actions.GeneralCommandAction;
import org.netbeans.modules.vcscore.commands.*;
import java.lang.ref.WeakReference;
import java.util.*;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.*;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.Debug;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
import org.netbeans.modules.vcscore.util.WeakList;


/**
 *
 * @author  Milos Kleint
 */
public class VcsActionSupporter extends CommandActionSupporter implements java.io.Serializable {

    protected  transient WeakReference fileSystem = new WeakReference(null);

    private HashMap commandMap;
    
    static final long serialVersionUID = -613064726657052221L;
    
    /** Creates new VcsActionSupporter */
    public VcsActionSupporter(VcsFileSystem filesystem) {
        fileSystem = new WeakReference(filesystem);
        commandMap = new HashMap();
    }
    
    public void setFileSystem(VcsFileSystem fs) {
        fileSystem = new WeakReference(fs);
    }

    public void addSupportForAction(Class actionClass, String commandName) {
        commandMap.put(actionClass, commandName);
    }
    
    public void removeSupportForAction(Class actionClass) {
        commandMap.remove(actionClass);
    }
    
    public boolean isEnabled(GeneralCommandAction action, FileObject[] fileObjects) {
        if (fileObjects == null || fileObjects.length == 0) {
            return false;
        }
        String cmdName = (String)commandMap.get(action.getClass());
        if (cmdName == null) {
            return false;
        }
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        final VcsCommand cmd = fileSystem.getCommand(cmdName);
        
        if (cmd == null) return false;
        Set foSet = new HashSet();
        for (int i = 0; i < fileObjects.length; i++) {
            foSet.add(fileObjects[i]);
        }
        boolean onRoot = isOnRoot(foSet);
        boolean onDir = isOnDirectory(foSet);
        boolean onFile = isOnFile(foSet);
        Set statuses = getSelectedFileStatusAttributes(foSet);
        
        if (    onDir && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_DIR)
        || onFile && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_FILE)
        || onRoot && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_ROOT)
        || VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)) {
            return false;
        }
        boolean disabled = VcsUtilities.isSetContainedInQuotedStrings(
        (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS), statuses);
        //System.out.println("VcsAction: isSetContainedInQuotedStrings("+(String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS)+
        //                   ", "+VcsUtilities.arrayToString((String[]) statuses.toArray(new String[0]))+") = "+disabled);
        if (disabled) {
            return false;
        }
        return true;
    }

    public void performAction(GeneralCommandAction action, FileObject[] fileObjects) {
        if (fileObjects == null || fileObjects.length == 0) {
            return;
        }
        String cmdName = (String)commandMap.get(action.getClass());
        if (cmdName == null) {
            return;
        }
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd != null) {
            VcsAction.performVcsCommand(cmd, fileSystem, Arrays.asList(fileObjects), false);
        }
    }

    
    /** Remove the files for which the command is disabled */
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
            
    /**
     * Test if some of the selected nodes are directories.
     * @return <code>true</code> if some of the selected nodes are directories,
     *         <code>false</code> otherwise.
     */
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
     */
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
     */
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
    
    private String getCommandActionDisplayName(GeneralCommandAction action) {
        String cmdName = (String) commandMap.get(action.getClass());
        if (cmdName != null) {
            VcsCommand cmd = ((VcsFileSystem) fileSystem.get()).getCommand(cmdName);
            if (cmd != null) {
                String name = (String) cmd.getProperty(VcsCommand.PROPERTY_GENERAL_COMMAND_ACTION_DISPLAY_NAME);
                if (name != null) {
                    return name;
                }
            }
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
