/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.recognizer;

import java.awt.Image;
import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.openide.ErrorManager;
import org.openide.filesystems.FileSystem;
import org.openide.util.Utilities;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.registry.FSInfo;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsFileSystemInfo extends Object implements FSInfo {
    
    private File root;
    private String profileName;
    private Map additionalVars;
    private Reference fileSystemRef = new WeakReference(null);
    
    /** Creates a new instance of CommandLineVcsFileSystemInfo */
    public CommandLineVcsFileSystemInfo(File root, String profileName,
                                        Map additionalVars) {
        this.root = root;
        this.profileName = profileName;
        this.additionalVars = additionalVars;
    }
    
    /**
     * Get the type of the filesystem, that can be displayed as an additional
     * information.
     */
    public String getDisplayType() {
        return root.getAbsolutePath();
    }
    
    /**
     * Get the root of the filesystem.
     */
    public File getFSRoot() {
        return root;
    }
    
    /**
     * Get the filesystem instance. This method should create the filesystem
     * if necessary. If the filesystem is still in use, return the same instance.
     */
    public synchronized FileSystem getFileSystem() {
        FileSystem fs = (FileSystem) fileSystemRef.get();
        if (fs == null) {
            fs = createFileSystem();
            fileSystemRef = new WeakReference(fs);
        }
        return fs;
    }
    
    /**
     * Get the icon, that can be used to visually present the filesystem.
     */
    public Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/vcs/advanced/vcsGeneric.gif"); // NOI18N
    }
    
    private FileSystem createFileSystem() {
        CommandLineVcsFileSystem fs = new CommandLineVcsFileSystem();
        fs.readConfiguration(profileName);
        fs.setConfigFileName(profileName);
        try {
            fs.setRootDirectory(root);
        } catch (java.beans.PropertyVetoException vetoExc) {
            ErrorManager.getDefault().notify(vetoExc);
        } catch (java.io.IOException ioExc) {
            ErrorManager.getDefault().notify(ioExc);
        }
        if (additionalVars != null) {
            Vector vars = fs.getVariables();
            HashMap varsByName = new HashMap();
            for (int i = 0, n = vars.size (); i < n; i++) {
                VcsConfigVariable var = (VcsConfigVariable) vars.get (i);
                varsByName.put (var.getName (), var);
            }
            for (Iterator addVarIt = additionalVars.keySet().iterator(); addVarIt.hasNext(); ) {
                String name = (String) addVarIt.next();
                String value = (String) additionalVars.get(name);
                VcsConfigVariable var = (VcsConfigVariable) varsByName.get(name);
                if (var == null) {
                    var = new VcsConfigVariable(name, null, value, false, false, false, null);
                    vars.add(var);
                    varsByName.put(name, var);
                } else {
                    var.setValue(value);
                }
            }
            fs.setVariables(vars);
        }
        return fs;
    }
    
}
