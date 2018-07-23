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
 * The Original Software is the Perforce module.
 * The Initial Developer of the Original Software is David Rees.
 * Portions created by David Rees are Copyright (C) 2004.
 * All Rights Reserved.
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
 *
 * Contributor(s): David Rees, Chris Hogue, Axel Wienberg, David Holscher,
 * Danno Ferrin, Torgeir Veimo.
 */

package org.netbeans.modules.vcs.profiles.perforce.list;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsFactory;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.*;

import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

import java.util.*;
import java.io.*;

/**
 * @author Axel Wienberg 
 */
public class P4ListCommand extends AbstractListCommand {
 
    public P4ListCommand() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        super.setFileSystem(fileSystem);
    }

    File dir;

    protected void initDir(Hashtable vars) {
      String rootDirVar = (String) vars.get("ROOTDIR"); // NOI18N
      if (rootDirVar == null) {
	  rootDirVar = "."; 
      }
      String dirVar = (String) vars.get("DIR"); // NOI18N
      if (dirVar == null) {
	  dirVar = "";
      }
      File rootDir = new File(rootDirVar);
      dir = new File(rootDirVar, dirVar);
    }

 /**
   * List files of the VCS Repository.
   * @param vars Variables used by the command
   * @param args Command-line arguments
   * @param filesByName listing of files with statuses
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
        //initDir(vars);
        
        try {
            runCommand(vars, args[0], true);
        } catch(InterruptedException ex) {
            //Ignore (like runCommand used to do for us in 3.2)
        }
	// ### dirs missing!
	// ### add local files
	if (props != null) {
	    outputFstat(props);
	    props = null;
	}

        try {
            runDirCommand(vars);
        } catch(InterruptedException ex) {
            //Ignore (like runCommand used to do for us in 3.2)
        }

        return !shouldFail;
    } 

    void runDirCommand(Hashtable vars) throws InterruptedException {
        
        CommandDataOutputListener dataOutputListener = new CommandDataOutputListener() {
            public void outputData(String elements[]) {
                if (elements.length != 1) {
                    System.err.println("dirlister: strange elements: "+array2string(elements));
                } else if (elements[0].startsWith("//")) {
                    outputDir(elements[0]);
                } else {
                    System.err.println("dirlister: not a depot path: "+elements[0]);
                }
            }

            public void outputDir(String depotFile) {
                String leafName =
                    depotFile == null ? "*directory name missing*/" : 
                        ((String) depotFile.substring(depotFile.lastIndexOf('/')+1))+"/";
                String[] statusses = new String[] { leafName, "have" };
                filesByName.put(leafName, statusses);
            }
        };

        runCommand(vars, "DIRLIST", dataOutputListener, null);
    }
        
    protected Hashtable filesByName;

    void outputFstat(Hashtable props) {
	String depotFile = (String) props.get(PROP_DEPOT_FILE);
	String leafName = depotFile == null ? "*depotFile name missing*" : 
	    (String) depotFile.substring(depotFile.lastIndexOf('/')+1);
	String headRev = (String) props.get(PROP_HEAD_REV);
	String haveRev = (String) props.get(PROP_HAVE_REV);
	String action = (String) props.get(PROP_ACTION);
	String headType = (String) props.get(PROP_HEAD_TYPE);
	String headAction = (String) props.get(PROP_HEAD_ACTION);
	String headTime = (String) props.get(PROP_HEAD_TIME);

	String fileStatus;
        // 
	if (haveRev == null && !("add".equals(action))) {
            if (headAction != null && headAction.equals("delete"))
                // ignore deleted files (? since they have no rev doe sthat mean they were never in the depot ?)
                return;
	    fileStatus = "missing";
	} else {
	    if (action != null) {
		fileStatus = action;
		if (props.get(PROP_OUR_LOCK) != null) {
		    fileStatus += ",*locked*";
		}
		if (props.get(PROP_UNRESOLVED) != null) {
		    fileStatus += ",unresolved";
		}
	    } else {
		fileStatus = "have";
	    }
	}
	String awareness = "";
	if (haveRev != null && headRev != null && !headRev.equals(haveRev)) {
	    awareness = "out-of-date ";
	}
	if (props.get(PROP_OTHER_OPEN) != null) {
	    awareness += "other-open ";
	}
	if (props.get(PROP_OTHER_LOCK) != null) {
	    awareness += "other-lock ";
	}
	String[] fileStatuses = new String[] { 
	    leafName, fileStatus, haveRev, headRev, headType, headTime, awareness };
	// System.out.println("** Stati: "+array2string(fileStatuses));
	filesByName.put(leafName, fileStatuses);
    }

    static final String PROP_DEPOT_FILE= "depotFile";
    static final String PROP_CLIENT_FILE = "clientFile";
    static final String PROP_HEAD_ACTION = "headAction";
    static final String PROP_HEAD_TYPE = "headType";
    static final String PROP_HEAD_TIME = "headTime";
    static final String PROP_HEAD_REV = "headRev";
    static final String PROP_HEAD_CHANGE = "headChange";
    static final String PROP_HAVE_REV = "haveRev";
    static final String PROP_ACTION = "action";
    static final String PROP_CHANGE = "change";
    static final String PROP_UNRESOLVED = "unresolved";
    static final String PROP_OTHER_OPEN = "otherOpen";
    static final String PROP_OTHER_LOCK = "otherLock";
    static final String PROP_OUR_LOCK = "ourLock";

    static final String NEXT_FILE_MARKER = PROP_DEPOT_FILE;

    protected Hashtable props = null;

    public void outputData(String[] elements) {
	if (elements == null) {
            System.err.println("p4listcommand: no match elements");
	    // ignore
        } else if ((elements.length == 3 || elements.length == 2) && elements[0].equals("...") ) {
            String field = elements[1];
            String value = elements.length > 2 ? elements[2] : "true";
            // System.err.println("field: "+field+" value: "+value);
            if (field.equals(NEXT_FILE_MARKER)) {
                if (props == null) {
                    props = new Hashtable(10);
                } else {
                    outputFstat(props);
                    props.clear();
                }
            }
            props.put(field, value);
        } else {
            System.err.println("P4ListCommand: strange match: elements.length="+elements.length+", "+array2string(elements));
            // ignore
        }
    }
}

