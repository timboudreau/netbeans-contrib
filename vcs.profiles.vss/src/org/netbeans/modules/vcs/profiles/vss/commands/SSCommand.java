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

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.SwingUtilities;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.exec.BadRegexException;
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.NotifyDescriptorInputPassword;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
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
    //private static final String USER_NOT_FOUND_PATTERN = "User \".*\" not found"; // NOI18N (TODO: Uncomment if you're able to grab it from error output)
    private static final String INVALID_PASSWORD = "Invalid password"; // NOI18N
    
    private CommandExecutionContext context;
    private volatile Thread executionThread;
    private volatile ExecuteCommand ec;
    private Hashtable vars;
    private String usernameStr = USERNAME;
    private String passwordStr = PASSWORD;
    //private String userNotFoundPatternStr = USER_NOT_FOUND_PATTERN; (Uncomment if you're able to grab it from error output)
    //private Pattern userNotFoundPattern; (Uncomment if you're able to grab it from error output)
    private String currentUserName;
    private String invalidPasswordStr = INVALID_PASSWORD;
    private volatile boolean haveWrongPassword = false;
    private volatile boolean passwordPromptCanceled = false;
    private FilterListener errorOutputListener;
    
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
            //userNotFoundPatternStr = NbBundle.getBundle("org/netbeans/modules/vcs/profiles/vss/config/BundleLocalizedVSS").getString("USER_NOT_FOUND_PATTERN_Prompt");  (Uncomment if you're able to grab it from error output)
            invalidPasswordStr = NbBundle.getBundle("org/netbeans/modules/vcs/profiles/vss/config/BundleLocalizedVSS").getString("InvalidPassword_Output");
        }
        //userNotFoundPattern = Pattern.compile(userNotFoundPatternStr); (Uncomment if you're able to grab it from error output)
        currentUserName = (String) vars.get("USER_NAME");
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
        
            haveWrongPassword = false; // Be positive ;-)
            boolean status = runCommand(exec, vars, silent ? null : stdoutNRListener,
                                        stderrNRListener, silent ? null : stdoutListener,
                                        dataRegex, stderrListener, errorRegex);
            if (!status || passwordPromptCanceled) {
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
        /* - Not necessary - we use FilterListener for that.
        cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX, dataRegex);
        cmd.setProperty(UserCommand.PROPERTY_ERROR_REGEX, errorRegex);
        cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX_GLOBAL, (String) vars.get("DATA_GLOBAL_REGEX")); // NOI18N
        cmd.setProperty(UserCommand.PROPERTY_ERROR_REGEX_GLOBAL, (String) vars.get("ERROR_GLOBAL_REGEX")); // NOI18N
         */
        String dataGlobalRegex = (String) vars.get("DATA_GLOBAL_REGEX"); // NOI18N
        String errorGlobalRegex = (String) vars.get("ERROR_GLOBAL_REGEX"); // NOI18N
        ExecuteCommand ec = new ExecuteCommand(context, cmd, vars);
        FilterListener stdOutputListener = new FilterListener(false, stdoutNRListener, stdoutListener,
                                                              dataRegex, dataGlobalRegex);
        ec.addOutputListener(stdOutputListener);
        errorOutputListener = new FilterListener(true, stderrNRListener, stderrListener,
                                                 errorRegex, errorGlobalRegex);
        ec.addErrorOutputListener(errorOutputListener);
        ec.addImmediateTextOutputListener(this);
        executionThread = Thread.currentThread();
        this.ec = ec;
        ec.run();
        stdOutputListener.flush();
        errorOutputListener.flush();
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
            String password = context.getPassword();
            //System.out.println("haveWrongPassword = "+haveWrongPassword+", have password: '"+password+"'.");
            if (haveWrongPassword || password == null) {
                password = getPassword(context.getPasswordDescription());
                context.setPassword(password);
                if (password == null) {
                    passwordPromptCanceled = true;
                    executionThread.interrupt();
                    errorOutputListener.outputLine(invalidPasswordStr);
                    return ;
                }
            } else {
                // Maybe that the context have the correct password, test it...
                haveWrongPassword = true; // But suppose that the password is wrong so that we ask the user next time.
            }
            //System.out.println("putting password: '"+password+"'.");
            vars.put("PASSWORD", password); // NOI18N
            ec.sendInput(password + "\n");
        } else if (line.trim().equalsIgnoreCase(usernameStr)) { // NOI18N
            ec.sendInput(currentUserName + "\n"); //NOI18N
            /* The below does not work - is put to error output
        } else if (userNotFoundPattern.matcher(line).matches()) {
            System.out.println("User Not Found matched!!");
            String userName = getUserName(line);
            if (userName == null) {
                executionThread.interrupt();
                return ;
            }
            vars.put("USER_NAME", userName); // NOI18N
            Vector allVars = context.getVariables();
            setVar(allVars, "USER_NAME", userName);
            context.setVariables(allVars);
            currentUserName = userName;
             */
        }
    }
    
    /**
     * Prompt for a password with a given description.
     * @return The new password or <code>null</code> when the prompt was canceled.
     */
    private static String getPassword(String description) {
        if (!isAWTLive(500)) { // If AWT is out of order, do not prompt for anything
            return null;
        }
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
    
    /** Check whether the AWT event queue is live and the requests are being processed. */
    private static boolean isAWTLive(long timeout) {
        final boolean[] gotNotification = new boolean[] { false };
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                synchronized (gotNotification) {
                    gotNotification[0] = true;
                    gotNotification.notifyAll();
                }
            }
        });
        synchronized (gotNotification) {
            if (!gotNotification[0]) {
                try {
                    gotNotification.wait(timeout);
                } catch (InterruptedException iex) {}
            }
        }
        return gotNotification[0];
    }
    
    /*  (Uncomment if you're able to grab the prompt from error output)
    private static String getUserName(String errorLine) {
        NotifyDescriptor.InputLine nd;
        nd = new NotifyDescriptor.InputLine(NbBundle.getMessage(SSCommand.class, "MSG_UserName"), errorLine); // NOI18N
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(nd))) {
            return nd.getInputText ();
        } else {
            return null;
        }
    }
    
    private static void setVar(Vector vars, String name, String value) {
        for (Iterator it = vars.iterator(); it.hasNext(); ) {
            VcsConfigVariable var = (VcsConfigVariable) it.next();
            if (name.equals(var.getName())) {
                var.setValue(value);
                break;
            }
        }
    }
     */
    
    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(org.netbeans.modules.vcscore.commands.CommandCustomizationSupport.class).getString(s);
    }
    
    /**
     * Filters out the Password: and Username: prompts. And "Invalid password" outputs.
     */
    private class FilterListener extends Object implements CommandOutputListener {
        
        private boolean isErr;
        private CommandOutputListener outputListener;
        private CommandDataOutputListener  dataListener;
        private Pattern pattern;
        private Pattern globalPattern;
        private StringBuffer globalDataOutput;
        
        /**
         * @param isErr When we filter error output.
         */
        public FilterListener(boolean isErr, CommandOutputListener outputListener,
                              CommandDataOutputListener dataListener, String regex,
                              String globalRegex) {
            this.isErr = isErr;
            this.outputListener = outputListener;
            this.dataListener = dataListener;
            if (regex != null) {
                try {
                    this.pattern = Pattern.compile(regex);
                } catch (PatternSyntaxException psex) {
                    ErrorManager.getDefault().notify(new BadRegexException(psex.getLocalizedMessage(), psex));
                }
            }
            if (globalRegex != null) {
                try {
                    this.globalPattern = Pattern.compile(globalRegex);
                    globalDataOutput = new StringBuffer();
                } catch (PatternSyntaxException psex) {
                    ErrorManager.getDefault().notify(new BadRegexException(psex.getLocalizedMessage(), psex));
                }
            }
        }
        
        public void outputLine(String line) {
            //System.out.println("FilterListener\""+isErr+"\".outputLine("+line+")");
            line = filterLine(line);
            if (line == null) return ;
            try {
                if (outputListener != null) {
                    outputListener.outputLine(line);
                }
                if (dataListener != null) {
                    String[] sa;
                    if (pattern != null) {
                        sa = ExternalCommand.matchToStringArray(pattern, line);
                    } else {
                        sa = new String[] { line };
                    }
                    if (globalPattern != null) {
                        if (sa != null) {
                            for (int i = 0; i < sa.length; i++) {
                                globalDataOutput.append(sa[i]);
                            }
                            globalDataOutput.append(" ");
                        }
                    } else {
                        if (sa != null && sa.length > 0) {
                            dataListener.outputData(sa);
                        }
                    }
                }
            } catch (ThreadDeath td) {
                throw td;
            } catch (Throwable th) {
                ErrorManager.getDefault().notify(th);
            }
        }
        
        public void flush() {
            if (globalDataOutput != null) {
                String globalDataOutput = this.globalDataOutput.toString();
                if (globalDataOutput.endsWith(" ")) {
                    globalDataOutput = globalDataOutput.substring(0, globalDataOutput.length() - 1);
                }
                String[] parsed = ExternalCommand.matchToStringArray(globalPattern, globalDataOutput);
                dataListener.outputData(parsed);
            }
        }
        
        private String filterLine(String line) {
            if (isErr) {
                if (invalidPasswordStr.equals(line) && !passwordPromptCanceled) {
                    line = null;
                    /* The below does not work - it's buffered!!!!!!!!
                } else if (userNotFoundPattern.matcher(line).matches()) {
                    System.out.println("User Not Found matched!!");
                    String userName = getUserName(line);
                    if (userName == null) {
                        executionThread.interrupt();
                        return line;
                    }
                    vars.put("USER_NAME", userName); // NOI18N
                    Vector allVars = context.getVariables();
                    setVar(allVars, "USER_NAME", userName);
                    context.setVariables(allVars);
                    currentUserName = userName;
                     */
                }
            } else {
                while (line != null) {
                    if (line.startsWith(usernameStr)) {
                        if (line.equals(usernameStr + " " + currentUserName)) {
                            line = null;
                        } else {
                            line = remove(usernameStr, line);
                        }
                    } else if (line.startsWith(passwordStr)) {
                        line = remove(passwordStr, line);
                    } else {
                        break;
                    }
                }
            }
            return line;
        }
        
        private String remove(String prefix, String line) {
            int index = prefix.length();
            while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
                index++;
            }
            return line.substring(index);
        }
    }
}
