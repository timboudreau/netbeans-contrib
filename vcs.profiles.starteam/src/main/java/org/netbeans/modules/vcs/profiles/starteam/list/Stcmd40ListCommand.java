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
 * The Original Software is Forte for Java, Community Edition. The Initial
 * Developer of the Original Software is Sun Microsystems, Inc. Portions
 * Copyright 1997-2006 Sun Microsystems, Inc. All Rights Reserved.
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
        if (args.length < 1) {
            stderrNRListener.outputLine("Expecting a command name as an argument!"); //NOI18N
            return false;
        }
        initVars(vars);
        try {
            runCommand(vars, args[0], false);
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
