/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2002 Sun Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.clearcase.list;

import java.io.*;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.util.*;

import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

/**
 * Implements List command for ClearCase.
 * @author  Alan Tai, Martin Entlicher
 */
public class ClearCaseListCommand extends AbstractListCommand
{

    private Debug E=new Debug("ClearCaseListCommand",true);
    private Debug D=E;

    private String dir=null;

    /** Creates new ClearCaseListCommand */
    public ClearCaseListCommand()
    {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        super.setFileSystem(fileSystem);
    }

    private void initDir(Hashtable vars)
    {
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null)
        {
            rootDir = "."; // NOI18N
        }
        this.dir = (String) vars.get("DIR"); // NOI18N
        if (this.dir == null)
        {
            this.dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        D.deb("rootDir = "+rootDir+", module = "+module+", dir = "+dir); // NOI18N
        if (dir.equals(""))
        { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) dir += File.separator + module;
        } else {
            if (module == null)
                dir=rootDir+File.separator+dir;
            else
                dir=rootDir+File.separator+module+File.separator+dir;
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        D.deb("dir="+dir); // NOI18N
    }

    /**
     * List files of the VCS Repository.
     * @param vars Variables used by the command
     * @param args Command-line arguments
     * filesByName listing of files with statuses
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     */
    public boolean list(Hashtable vars, String[] args, Hashtable filesByName,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex)
    {
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stderrListener = stderrListener;
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        this.filesByName = filesByName;
        initVars(vars);
        initDir(vars);
        try {
            runCommand(vars, args, false);
        } catch (InterruptedException iex) {
            return false;
        }

        return !shouldFail;
    }

    public void outputData(String[] elements)
    {
        D.deb("elements: " + elements[0]);
		final int nameIndex = 0;
        final String statusSep = "@@";
        final String statusEndStr = "Rule: ";
        String line=elements[0];
        D.deb("match: line = "+line);
        int statIndex = line.indexOf(statusSep, nameIndex);
        if (statIndex < 0)
        {
			return; // view private objects will be added by VCS as local files
		}
        int endStatIndex = line.indexOf(statusEndStr, statIndex);
        if (endStatIndex < 0) endStatIndex = line.length() - 1;
        String[] fileInfo = new String[2];
        fileInfo[0] = line.substring(nameIndex, statIndex);
        File file = new File(dir+File.separator+fileInfo[0]);
        if(file != null && file.isDirectory() )
        {
            fileInfo[0] += "/";
        }

		String checkedOut = null;
                if ((endStatIndex + statusEndStr.length()) < line.length())
                {
                    checkedOut = line.substring(endStatIndex + statusEndStr.length(), line.length());
                } else {
                    checkedOut = "";
                }
//		if(checkedOut.trim()=="CHECKEDOUT")
		if(checkedOut.trim().equals("CHECKEDOUT"))
		{
			fileInfo[1] = line.substring(statIndex + statusSep.length(), endStatIndex);
			if (fileInfo[1] != null) fileInfo[1] = fileInfo[1].trim();
		}
		else
		{
			fileInfo[1] = "";
		}

    	filesByName.put(fileInfo[0], fileInfo);
    }
}
