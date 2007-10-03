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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;

/**
 * Creates folder context independent ignore list:
 *
 * <ul>
 * <li> The CVS 1.11.18 list is initialized to include certain file name patterns: names associated with CVS administration, or with other common source control systems; common names for patch files, object files, archive files, and editor backup files; and other names that are usually artifacts of assorted utilities. Currently, the default list of ignored file name patterns is:
 *
 *   <pre>
 *   RCS     SCCS    CVS     CVS.adm
 *   RCSLOG  cvslog.*
 *   tags    TAGS
 *   .make.state     .nse_depinfo
 *   *~      #*      .#*     ,*      _$*     *$
 *   *.old   *.bak   *.BAK   *.orig  *.rej   .del-*
 *   *.a     *.olb   *.o     *.obj   *.so    *.exe
 *   *.Z     *.elc   *.ln
 *   core
 *   </pre>
 *
 * <li> The per-repository list in `$CVSROOT/CVSROOT/cvsignore' is appended to the list, if that file exists.
 * <li> The per-user list in `.cvsignore' in your home directory is appended to the list, if it exists.
 * <li> Any entries in the environment variable $CVSIGNORE is appended to the list.
 *
 * @author  Martin Entlicher
 */
public class CvsCreateInitialIgnoreList extends Object implements VcsAdditionalCommand {

    // The table of file names ehich are defaulty ignored by cvs
    public static final String[] DEFAULT_IGNORE_FILES={".#*","#*",",*","_$*","*~","*$","*.a",
                                                       "*.bak","*.BAK","*.elc","*.exe","*.ln",
                                                       "*.o","*.obj","*.olb","*.old","*.orig",
                                                       "*.rej","*.so","*.Z",".del-*",".make.state",
                                                       ".nse_depinfo","core","CVS","CVS.adm","cvslog.*",
                                                       "RCS","RCSLOG","SCCS","tags","TAGS"};
    public final static String CVS_IGNORE_FILE_NAME = ".cvsignore"; //NOI18N
    
    private VcsFileSystem fileSystem;
    
    /** Creates new CvsCreateInitialIgnoreList */
    public CvsCreateInitialIgnoreList() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    private void addRepositoryIgnoreList(final VcsCommand cmd, final Hashtable vars, final ArrayList ignoreList) {
        if (cmd == null) return ;
        //VcsAction.doCommand(files
        VcsCommandExecutor executor = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        executor.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] data) {
                for (int i = 0; i < data.length; i++) {
                    StringTokenizer tokens = new StringTokenizer(data[i]);
                    while (tokens.hasMoreTokens()) {
                        String element = tokens.nextToken().trim();
                        if ("!".equals(element)) ignoreList.clear();
                        else ignoreList.add (element);
                    }
                }
            }
        });
        CommandsPool pool = fileSystem.getCommandsPool();
        pool.startExecutor(executor, fileSystem);
        try {
            pool.waitToFinish(executor);
        } catch (InterruptedException iexc) {
            pool.kill(executor);
        }
    }
    
    private void addHomeIgnoreList(ArrayList ignoreList) {
        String homeStr = System.getenv("HOME");
        if (homeStr == null && org.openide.util.Utilities.isWindows()) {
            String homeDrive = System.getenv("HOMEDRIVE");
            String homeDir = System.getenv("HOMEPATH");
            if (homeDrive != null && homeDir != null) {
                homeStr = homeDrive + homeDir;
            }
        }
        if (homeStr == null) {
            homeStr = System.getProperty("user.home");
        }
        File home = new File(homeStr);
        File userIgnoreFile = new File(home, CVS_IGNORE_FILE_NAME);
        CvsCreateFolderIgnoreList.addFileIgnoreList(userIgnoreFile, ignoreList);
    }
    
    private void addEnvironmentIgnoreList(ArrayList ignoreList) {
        String line = System.getenv("cvsignore"); // NOI18N
        if (line != null) {
            StringTokenizer tk = new StringTokenizer(line);
            while (tk.hasMoreTokens()) {
                String element = tk.nextToken().trim();
                if (element.length() == 0) {
                    continue;
                } else if ("!".equals(element)) {
                    ignoreList.clear();
                } else {
                    ignoreList.add(element);
                }
            }
        }
    }

    static void returnIgnoreList(ArrayList ignoreList, CommandDataOutputListener stdoutDataListener) {
        int n = ignoreList.size();
        for (int i = 0; i < n; i++) {
            String[] data = { (String) ignoreList.get(i) };
            stdoutDataListener.outputData(data);
        }
    }
    
    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(final Hashtable vars, final String[] args,
                        final CommandOutputListener stdoutListener, final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, final String dataRegex,
                        final CommandDataOutputListener stderrDataListener, final String errorRegex) {
        ArrayList ignoreList = new ArrayList();
        // Start with defaultly ingored files
        ignoreList.addAll(Arrays.asList(DEFAULT_IGNORE_FILES));
        // TODO FIXME strange
        if (args.length < 1) {
            stderrListener.outputLine("A checkout command is expected as an argument!"); // NOI18N
            VcsCommand cmd = fileSystem.getCommand(args[0]);
            // Add the CVSROOT/cvsignore
            addRepositoryIgnoreList(cmd, vars, ignoreList);
        }
        // Next add $HOME/.cvsignore
        addHomeIgnoreList(ignoreList);
        // Finally add $CVSIGNORE
        addEnvironmentIgnoreList(ignoreList);
        returnIgnoreList(ignoreList, stdoutDataListener);
        return true;
    }
    
}
