/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;

import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.RelativeMountDialog;
import org.netbeans.modules.vcscore.cmdline.RelativeMountPanel;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.util.Table;

/**
 * Selector of a PVCS project.
 *
 * @author  Martin Entlicher
 */
public class PvcsProjectSelector extends Object implements VcsAdditionalCommand,
                                                           AbstractFileSystem.List,
                                                           CommandDataOutputListener {
    private static final String ERROR = " [Error]"; // NOI18N
    
    private VcsCommand cmd;
    private VcsFileSystem fileSystem;
    private boolean failed;
    private List subprojects;
    
    /** Creates a new instance of PvcsProjectSelector */
    public PvcsProjectSelector() {
        subprojects = new ArrayList();
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
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
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        if (args.length < 1) {
            stderrListener.outputLine("A project list command name expected as an argument.");
            return false;
        }
        cmd = fileSystem.getCommand(args[0]);
        if (cmd == null) {
            stderrListener.outputLine("Unknown command '"+args[0]+"'.");
            return false;
        }
        String project = (String) vars.get("PROJECT");
        RelativeMountPanel panel =
            new RelativeMountPanel(NbBundle.getMessage(PvcsProjectSelector.class, "ProjectSelectorLabel"),
                                   NbBundle.getMessage(PvcsProjectSelector.class, "ProjectSelectorLabel_mnc").charAt(0),
                                   NbBundle.getMessage(PvcsProjectSelector.class, "ProjectSelectorModule"),
                                   NbBundle.getMessage(PvcsProjectSelector.class, "ProjectSelectorModule_mnc").charAt(0));
        panel.initTree("/", new String[] { project }, false, this);
        RelativeMountDialog dialog =
            new RelativeMountDialog(panel, NbBundle.getMessage(PvcsProjectSelector.class,
                                                               "ProjectSelectorTitle"),
                                    new HelpCtx(PvcsProjectSelector.class));
        if (NotifyDescriptor.OK_OPTION.equals(TopManager.getDefault().notify(dialog))) {
            stdoutDataListener.outputData(new String[] { "/" + panel.getRelMount() });
        }
        return true;
    }
    
    /** List subprojects of a given project */
    public synchronized String[] children(String project) {
        Hashtable additionalVars = new Hashtable();
        Table dummyFiles = new Table();
        project = project.replace(java.io.File.separatorChar, '/');
        while (project.startsWith("/")) project = project.substring(1);
        dummyFiles.put(project+"/foo.txt", null);
        subprojects.clear();
        failed = false;
        VcsCommandExecutor[] execs = VcsAction.doCommand(dummyFiles, cmd, additionalVars, fileSystem,
                                                         null, null, this, null);
        try {
            for (int i = 0; i < execs.length; i++) {
                fileSystem.getCommandsPool().waitToFinish(execs[i]);
            }
        } catch (InterruptedException intrEx) {
        }
        if (failed || execs[0].getExitStatus() != VcsCommandExecutor.SUCCEEDED) subprojects.clear();
        String[] children = (String[]) subprojects.toArray(new String[subprojects.size()]);
        subprojects.clear();
        return children;
    }
    
    /**
     * This method is called, with elements of the output data (subprojects).
     * @param elements the elements of output data.
     */
    public void outputData(String[] elements) {
        if (elements[0] == null || elements[0].length() == 0) return ;
        if (elements[0].indexOf(ERROR) >= 0) {
            failed = true;
            return ;
        }
        subprojects.add(elements[0]);
    }
    
}
