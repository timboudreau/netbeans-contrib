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
import java.util.Hashtable;
import org.netbeans.modules.vcscore.VcsFileSystem;

import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

class TeamwareSupport {
    
    static boolean exec(File dir, String[] args,
        CommandOutputListener stdout, CommandOutputListener stderr) {
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();
        boolean success = exec(dir, args, out, err);
        if (!success) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(args[i]);
            }
            stdout.outputLine(sb.toString());
            stdout.outputLine(out.getBuffer().toString());
            stderr.outputLine(err.getBuffer().toString());
        }
        return success;
    }

    static boolean exec(File dir, String[] args,
        Writer out, Writer err) {

        try {
            Process p = Runtime.getRuntime().exec(args, null, dir);
            StreamCopier errCopier = new StreamCopier(p.getErrorStream(), err);
            StreamCopier outCopier = new StreamCopier(p.getInputStream(), out);
            Thread t1 = new Thread(outCopier);
            t1.start();
            Thread t2 = new Thread(errCopier);
            t2.start();
            p.waitFor();
            t1.join();
            t2.join();
            return p.exitValue() == 0;
        } catch (InterruptedException e) {
            /* fail */
        } catch (IOException e) {
            /* fail */
        }
        return false;
    }

    static String getRevision(VcsFileSystem fs,
        File file, String revision) throws InterruptedException {
            
        class StringOutputListener implements TextOutputListener {
            StringBuffer sb = new StringBuffer();
            public void outputLine(String line) {
                sb.append(line);
                sb.append("\n");
            }
        }
    
        Hashtable vars = new Hashtable();
        vars.put("WORKDIR", file.getParent());
        vars.put("FILE", file.getName());
        String cmdName;
        if (revision == null) {
            cmdName = "REVISION_OPEN_LAST";
        } else {
            cmdName = "REVISION_OPEN";
            vars.put("REVISION", revision);
        }
        VcsCommand cmd = fs.getCommand(cmdName);
        VcsCommandExecutor ec =
            fs.getVcsFactory().getCommandExecutor(cmd, vars);
        StringOutputListener listener = new StringOutputListener();
        ec.addTextOutputListener(listener);
        fs.getCommandsPool().startExecutor(ec, fs);
        fs.getCommandsPool().waitToFinish(ec);
        return listener.sb.toString();
    }


    
}
