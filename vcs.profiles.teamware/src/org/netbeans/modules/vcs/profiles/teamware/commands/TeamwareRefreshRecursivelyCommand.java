/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.File;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.cmdline.VcsListRecursiveCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

public class TeamwareRefreshRecursivelyCommand extends VcsListRecursiveCommand {

    public boolean listRecursively(
        Hashtable vars, String[] args,
        VcsDirContainer filesByName,
        CommandOutputListener stdoutListener,
        CommandOutputListener stderrListener,
        CommandDataOutputListener stdoutDataListener,
        String dataRegex,
        CommandDataOutputListener stderrDataListener,
        String errorRegex) {
            
        String rootDir = (String) vars.get("ROOTDIR");
        String module = (String) vars.get("MODULE");
        String dirName = (String) vars.get("DIR");
        File root = new File(rootDir);
        File dir = (module != null) ? new File(root, module) : root;
        if (dirName != null) {
            dir = new File(dir, dirName);
        }
        String path = dir.toString().substring(root.toString().length())
            .replace(File.separatorChar, '/');
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        listDir(path, dir, filesByName, stdoutListener, stderrListener);
        return true;

    }
    
    private void listDir(String path, File dir, VcsDirContainer filesByName,
        CommandOutputListener stdout,
        CommandOutputListener stderr) {

        if (TeamwareRefreshSupport.ignoreFile(dir)) {
            stderr.outputLine("Ignoring " + dir);
            return;
        }
        File[] files = TeamwareRefreshSupport.listFilesInDir(dir);
        filesByName.setPath(path);
        filesByName.setName(dir.getName());
        Hashtable table = (Hashtable) filesByName.getElement();
        if (table == null) {
            table = new Hashtable();
            filesByName.setElement(table);
        }
        File sccsDir = new File(dir, "SCCS");
        for (int i = 0 ; i < files.length; i++) {
            String[] data = TeamwareRefreshSupport.listFile(files[i], sccsDir, stderr);
            if (data == null) {
                continue;
            }
            stdout.outputLine(dir + File.separator + data[1] + " [" + data[0] + "]");
            table.put(data[1], data);
            if (data[0].equals("Ignored")) {
                continue;
            }
            String subpath = path;
            if (path.length() > 0) {
                subpath += "/";
            }
            subpath += data[1];
            if (files[i].isDirectory()) {
                VcsDirContainer subDir = filesByName.getContainerWithPath(subpath);
                if (subDir == null) {
                    subDir = filesByName.addSubdir(data[1]);
                }
                listDir(subpath, files[i], subDir, stdout, stderr);
            }
        }
    }
    
}
