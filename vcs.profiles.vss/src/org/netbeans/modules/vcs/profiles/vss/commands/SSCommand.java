/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.NotifyDescriptorInputPassword;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * The wrapper of ss.exe command.
 * Executes a ss.exe command and checks for the password prompt.
 *
 * @author  Martin Entlicher
 */
public class SSCommand extends Object implements VcsAdditionalCommand,
                                                 TextOutputListener {
    
    private static final String PASSWORD = "Password:"; // NOI18N
    private static final String USERNAME = "Username:"; // NOI18N
    
    private CommandExecutionContext context;
    private volatile Thread executionThread;
    private volatile ExecuteCommand ec;
    private Hashtable vars;
    private String usernameStr = USERNAME;
    private String passwordStr = PASSWORD;
    
    /** Creates a new instance of SSCommand */
    public SSCommand() {
    }
    
    public void setExecutionContext(CommandExecutionContext context){                
        this.context = context;
    }
    
    /**
     * @param args optional dir=... argument specifying the working directory <br>
     *             optional -mkdir argument, the working directory
     *                             is created when necessary <br>
     *             &lt;command-name&gt; followed by arguments - the ss command
     *             that is to be executed <br>
     *             More commands can be specified, separated by &amp;&amp; <br>
     *             -silent argument can be passed before the command to ignore the standard output
     *             -os argument can be passed before the command to treat it as an OS command.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        if (args.length == 0) {
            stderrNRListener.outputLine("Too few arguments.");
            return false;
        }
        this.vars = vars;
        if (vars.containsKey(ProfilesFactory.VAR_LOCALIZED_PROFILE_COPY)) {
            usernameStr = NbBundle.getBundle("org/netbeans/modules/vcs/profiles/vss/config/BundleLocalizedVSS").getString("USERNAME_Prompt");
            passwordStr = NbBundle.getBundle("org/netbeans/modules/vcs/profiles/vss/config/BundleLocalizedVSS").getString("PASSWORD_Prompt");
        }
        File dir = null;
        String ssExe = (String) vars.get("VSSCMD"); // NOI18N
        //List commands = new ArrayList();
        int index = 0;
        if (args[index].startsWith("dir=")) { // NOI18N
            dir = new File(args[index++].substring("dir=".length())); // NOI18N
        }
        if (args[index].equals("-mkdir")) { // NOI18N
            if (dir != null && !dir.exists()) dir.mkdirs();
            index++;
        }
        String tempFilePath = (String) vars.get(Variables.TEMPORARY_FILE);
        vars.remove(Variables.TEMPORARY_FILE);
        do {
            List arguments = new ArrayList();
            boolean silent = false;
            String osCmd = null;
            boolean first = true;
            for (; index < args.length && !args[index].equals("&&"); index++) { // NOI18N
                if (first && "-silent".equals(args[index])) { // NOI18N
                    silent = true;
                    continue;
                }
                if (first && "-os".equals(args[index])) { // NOI18N
                    osCmd = "";
                    continue;
                }
                if (first && osCmd != null) {
                    osCmd = args[index];
                    first = false;
                    continue;
                }
                first = false;
                arguments.add(new StructuredExec.Argument(args[index], false));
            }
            if (osCmd == null) osCmd = ssExe;
            StructuredExec exec = new StructuredExec(dir, osCmd,
                (StructuredExec.Argument[]) arguments.toArray(new StructuredExec.Argument[0]));
            if (index < args.length && args[index].equals("&&")) index++; // NOI18N
        
            boolean status = runCommand(exec, vars, silent ? null : stdoutNRListener,
                                        stderrNRListener, stdoutListener, dataRegex,
                                        stderrListener, errorRegex);
            if (!status) {
                return status;
            }
        } while(index < args.length);
        if (tempFilePath != null) {
            vars.put(Variables.TEMPORARY_FILE, tempFilePath);
        }
        return true;
    }
    
    private boolean runCommand(StructuredExec exec, Hashtable vars,
                               CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                               CommandDataOutputListener stdoutListener, String dataRegex,
                               CommandDataOutputListener stderrListener, String errorRegex) {
        UserCommand cmd = new UserCommand();
        /*
        System.out.println("runCommand("+exec.getWorking()+", "+exec.getExecutable()+")");
        StructuredExec.Argument[] arguments = exec.getArguments();
        for (int i = 0; i < arguments.length; i++) {
            System.out.println("  '"+arguments[i].getArgument()+"'");
        }
         */
        cmd.setProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED, exec);
        cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX, dataRegex);
        cmd.setProperty(UserCommand.PROPERTY_ERROR_REGEX, errorRegex);
        ExecuteCommand ec = new ExecuteCommand(context, cmd, vars);
        if (stdoutNRListener != null) ec.addOutputListener(stdoutNRListener);
        ec.addErrorOutputListener(stderrNRListener);
        ec.addDataOutputListener(stdoutListener);
        ec.addDataErrorOutputListener(stderrListener);
        ec.addImmediateTextOutputListener(this);
        executionThread = Thread.currentThread();
        this.ec = ec;
        ec.run();
        return ec.getExitStatus() == ExecuteCommand.SUCCEEDED;
    }
    
    public void outputLine(String text) {
        //System.out.println("Immediate output: '"+text+"'");
        String nl = System.getProperty("line.separator"); // NOI18N
        int i1 = 0;
        do {
            int i2 = text.indexOf(nl, i1);
            int i3;
            if (i2 < 0) {
                i2 = text.indexOf('\n', i1);
                if (i2 < 0) {
                    i2 = text.length();
                    i3 = i2;
                } else {
                    i3 = i2 + 1;
                }
            } else {
                i3 = i2 + nl.length();
            }
            String line = text.substring(i1, i2);
            immediateLine(line);
            i1 = i3;
        } while (i1 < text.length());
    }
    
    private void immediateLine(String line) {
        //System.out.println("Immediate line = '"+line+"'");
        if (line.trim().equalsIgnoreCase(passwordStr)) { // NOI18N
            // ss.exe is asking for a password!
            String password = getPassword(context.getPasswordDescription());
            context.setPassword(password);
            if (password == null) {
                executionThread.interrupt();
                return ;
            }
            vars.put("PASSWORD", password); // NOI18N
            ec.sendInput(password + "\n");
        }
        if (line.trim().equalsIgnoreCase(usernameStr)) { // NOI18N
            ec.sendInput(vars.get("USER_NAME") + "\n"); //NOI18N
        }
    }
    
    private static String getPassword(String description) {
        NotifyDescriptorInputPassword nd;
        if (description == null) {
            nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("MSG_Password")); // NOI18N
        } else {
            nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("TITL_Password"), description); // NOI18N
        }
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(nd))) {
            return nd.getInputText ();
        } else {
            return null;
        }
    }
    
    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(org.netbeans.modules.vcscore.commands.CommandCustomizationSupport.class).getString(s);
    }
    
}
