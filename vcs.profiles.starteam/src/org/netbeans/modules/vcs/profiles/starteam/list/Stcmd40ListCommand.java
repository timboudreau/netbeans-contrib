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
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.starteam.list;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

import java.util.Hashtable;
import java.io.*;

/**
 *
 * @author  Martin Entlicher
 */
public class Stcmd40ListCommand extends AbstractListCommand {

    private Debug E=new Debug("Stcmd40List",true);
    private Debug D=E;

    /** Creates new Stcmd40ListCommand */
    public Stcmd40ListCommand() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        super.setFileSystem(fileSystem);
    }

    /**
     * List files of CVS Repository.
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
                        CommandDataOutputListener stderrListener, String errorRegex) {
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stderrListener = stderrListener;
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        this.filesByName = filesByName;
        initVars(vars);
        try {
            runCommand(vars, args, false);
        } catch (InterruptedException iexc) {
            return false;
        }

        return !shouldFail;
    }

    public void outputData(String[] elements) {
        String line=elements[0];
        D.deb("match: line = "+line);
        String subdir=line.trim();
        if( subdir.endsWith("/") ){
            String[] statuses = {"", "", "", "", "", "", "", subdir};
            filesByName.put(subdir, statuses);
        }
        else if ( subdir.endsWith("\\") ){
            subdir=subdir.substring(0, subdir.lastIndexOf("\\"))+"/";
            String[] statuses = {"", "", "", "", "", "", "", subdir};
            filesByName.put(subdir, statuses);
        }
        else{
            if( line.indexOf("Folder:") < 0 ){
                //D.deb("I have elements = "+VcsUtilities.arrayToString(elements));
                if (elements.length > 7)
                    filesByName.put(elements[7], elements);
                else
                    filesByName.put(elements[0], elements);
            }
        }
    }
}