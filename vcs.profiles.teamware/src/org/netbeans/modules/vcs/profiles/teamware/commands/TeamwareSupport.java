/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Teamware module.
 * The Initial Developer of the Original Code is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 * 
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.actions.CommandActionSupporter;
import org.netbeans.modules.vcscore.actions.GeneralCommandAction;

import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.openide.filesystems.FileObject;

class TeamwareSupport {
    
    static File getFile(Hashtable vars) {
        File baseDir = getDir(vars);
        String fileName = (String) vars.get("FILE");
        if (fileName == null) {
            return baseDir;
        } else {
            return new File(baseDir, fileName);
        }
    }
    
    static File getDir(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR");
        String module = (String) vars.get("MODULE");
        String dirName = (String) vars.get("DIR");
        File root = new File(rootDir);
        File baseDir = (module != null) ? new File(root, module) : root;
        if (dirName != null) {
            baseDir = new File(baseDir, dirName);
        }
        return baseDir;
    }
    
}
