/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openide.ErrorManager;

import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.TextInput;
import org.netbeans.modules.vcscore.commands.TextOutputListener;

/**
 * PCLI command that performs efficient execution using PCLICommandExecutor.
 *
 * @author  Martin Entlicher
 */
public class PCLICommand implements VcsAdditionalCommand, VcsAdditionalCommand.ImmediateOutput,
                                    TextInput {
    
    private String execStr;
    private CommandOutputListener stdoutListener;
    private CommandOutputListener stderrListener;
    private CommandDataOutputListener stdoutDataListener;
    private Pattern dataRegex;
    private CommandDataOutputListener stderrDataListener;
    private Pattern errorRegex;
    private ArrayList stdImmediateOutListeners = new ArrayList();
    private ArrayList stdImmediateErrListeners = new ArrayList();
    private Boolean[] success = { null };
    
    /** Creates a new instance of PCLICommand */
    public PCLICommand() {
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener,
                        CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        PCLICommandExecutor executor = PCLICommandExecutor.getDefault();
        if (args.length < 1 || args[0] == null) {
            stderrListener.outputLine("Expecting the PCLI command as an argument.");
            return false;
        }
        this.execStr = args[0];
        this.stdoutListener = stdoutListener;
        this.stderrListener = stderrListener;
        this.stdoutDataListener = stdoutDataListener;
        this.stderrDataListener = stderrDataListener;
        if (dataRegex == null) {
            dataRegex = ExecuteCommand.DEFAULT_REGEX;
        }
        if (errorRegex == null) {
            errorRegex = ExecuteCommand.DEFAULT_REGEX;
        }
        try {
            this.dataRegex = Pattern.compile(dataRegex);
        } catch (PatternSyntaxException psex) {
            ErrorManager.getDefault().notify(psex);
        }
        try {
            this.errorRegex = Pattern.compile(errorRegex);
        } catch (PatternSyntaxException psex) {
            ErrorManager.getDefault().notify(psex);
        }
        try {
            return executor.runCommand(this);
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public String getStrExec() {
        return execStr;
    }
    
    /**
     * Add a listener to the standard output, that will be noified
     * immediately as soon as the output text is available. It does not wait
     * for the new line and does not send output line-by-line.
     */
    public void addImmediateTextOutputListener(TextOutputListener l) {
        this.stdImmediateOutListeners.add(l);
    }

    /**
     * Add a listener to the standard error output, that will be noified
     * immediately as soon as the output text is available. It does not wait
     * for the new line and does not send output line-by-line.
     */
    public void addImmediateTextErrorListener(TextOutputListener l) {
        this.stdImmediateErrListeners.add(l);
    }

    public void sendInput(String text) {
        PCLICommandExecutor.getDefault().sendInput(text);
    }
    
    void stdOutput(String line) {
        stdoutListener.outputLine(line);
        if (dataRegex != null) {
            String[] sa = ExternalCommand.matchToStringArray(dataRegex, line);
            if (sa != null && sa.length > 0) stdoutDataListener.outputData(sa);
        }
    }
    
    void immediateStdOutput(String text) {
        Iterator it = stdImmediateOutListeners.iterator();
        while(it.hasNext()) {
            ((TextOutputListener) it.next()).outputLine(text);
        }
    }
    
    void errOutput(String line) {
        stderrListener.outputLine(line);
        if (errorRegex != null) {
            String[] sa = ExternalCommand.matchToStringArray(errorRegex, line);
            if (sa != null && sa.length > 0) stderrDataListener.outputData(sa);
        }
    }
    
    void immediateErrOutput(String text) {
        Iterator it = stdImmediateErrListeners.iterator();
        while(it.hasNext()) {
            ((TextOutputListener) it.next()).outputLine(text);
        }
        
    }
    
    void setSucceeded() {
        synchronized (success) {
            success[0] = Boolean.TRUE;
            success.notify();
        }
    }
    
    void setFailed() {
        synchronized (success) {
            success[0] = Boolean.FALSE;
            success.notify();
        }
    }
    
    boolean succeeded() {
        return success[0].booleanValue();
    }
    
    public void waitFinished() throws InterruptedException {
        synchronized (success) {
            if (success[0] == null) {
                success.wait();
            }
        }
    }
    
}
