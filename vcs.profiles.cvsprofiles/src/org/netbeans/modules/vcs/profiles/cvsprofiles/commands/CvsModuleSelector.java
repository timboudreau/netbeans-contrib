/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.util.*;
import java.awt.*;

import org.openide.*;
import org.openide.util.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.openide.DialogDisplayer;

/**
 * The selector of CVS modules
 * @author  Martin Entlicher
 */
public class CvsModuleSelector implements VcsAdditionalCommand, CommandDataOutputListener {
    private Debug E = new Debug("CvsModuleSelector", true); // NOI18N
    private Debug D = E;

    private Hashtable vars;
    private CommandOutputListener stdoutNRListener;
    private CommandOutputListener stderrNRListener;
    private String dataRegex = "^(.*)$";
    private StringBuffer outputBuffer;
    private volatile boolean cmdSuccess = true;  // By default we hope the module list is in cash
    private volatile boolean dlgSuccess = false;
    private volatile boolean dlgFinished = false;
    private boolean isCommand = false;
    
    
    //private volatile boolean modulesStatGetStatus = false;
    //private volatile boolean modulesAllGetStatus = false;
    private volatile CompleteStatusOutput completeOutput = null;
    private VcsFileSystem fileSystem = null;

    /** Creates new CvsModuleSelector */
    public CvsModuleSelector() {
        isCommand = false;
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    public VcsFileSystem getFileSystem () {
        return this.fileSystem;
    }
    
    Hashtable getVariables() {
        return vars;
    }

    
    /** this method is used to start the selector from the CvsAction popup menu.
     *  This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                      satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                      satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars,String[] args,
                        CommandOutputListener stdoutNRListener,
                        CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        isCommand = true;
        String[] returns = execSel(vars, "MODULE", args, stdoutNRListener, stderrNRListener);
        String[] toReturn = new String[1];
        if (returns != null) {
            if (returns.length > 0 && returns[0] != null && returns[0].length() > 0) { 
                toReturn[0] = VcsUtilities.arrayToQuotedString(returns, false);  //TODO for cygwin =true
                stdoutListener.outputData(toReturn); 
            }  
            return true;
        }
        //toReturn[0] = ".";
        //stdoutListener.match(toReturn);
        return false;  
    }
    
    /**
     * This method is used to start the selector.
     * @param vars the VCS variables
     * @param variable the name of the selected variable
     * @param args the command line parametres
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @return the selected values, empty string when the selection was canceled
     *         or null when an error occures.
     */
    public String[] execSel(Hashtable vars, String variable, String[] args,
                       CommandOutputListener stdoutNRListener,
                       CommandOutputListener stderrNRListener) {
        D.deb("exec for "+variable);
        this.vars = vars;
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        /*
        if (!runCommand(args)) return null;
        String[] modules = getModules();
        if (modules == null || modules.length <= 0) {
          javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              NotifyDescriptor nd = new NotifyDescriptor.Message (org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.NoModules"));
              TopManager.getDefault ().notify (nd);
            }
          });
          return null;
    }
        */
        final CvsModuleSelectorDialog panel = new CvsModuleSelectorDialog (this, args);
        panel.calledAsCommand(isCommand);  // if run as command -> is called from CvsAction - disable some stuff
        final DialogDescriptor dd = new DialogDescriptor (panel, NbBundle.getBundle(CvsModuleSelector.class).getString ("CvsModuleSelectorDialog.title"));
        final Dialog fdlg = DialogDisplayer.getDefault().createDialog (dd);
        VcsUtilities.centerWindow(fdlg);
        
        Thread showThread = new Thread() {
            public void run() {
                fdlg.setVisible(true);
                dlgSuccess = (dd.getValue() == DialogDescriptor.OK_OPTION);
                dlgFinished = true;
            }
        };
        javax.swing.SwingUtilities.invokeLater(showThread);
        try {
            while(!dlgFinished) {
                Thread.sleep(200);
            }
            D.deb("showThread is alive = "+showThread.isAlive()+", joining him");
            showThread.join();
        } catch (InterruptedException e) {
            // Interrupted
            dlgSuccess = false;
        }
        D.deb("dlgSuccess = "+dlgSuccess+", cmdSuccess = "+cmdSuccess);
        if (dlgSuccess && cmdSuccess) {
            return panel.getSelection();
        } else {
            if (dlgSuccess) return null;
            else {
                String[] toReturn = {""};
              return toReturn;                        
            }    
        }
    }

    boolean runCommand(String[] args) {
        if (args.length != 2) {
            if (stderrNRListener != null) stderrNRListener.outputLine("Bad number of arguments. "+
                                                                      "Expecting two arguments: cvs co -s AND cvs co -c");
            return false;
        }
        VcsCommand cmd = fileSystem.getCommand(args[0]);
        Hashtable varsCopy1 = new Hashtable(vars);
        VcsCommandExecutor vce1 = fileSystem.getVcsFactory().getCommandExecutor(cmd, varsCopy1);
        vce1.addDataOutputListener(this);
        cmd = fileSystem.getCommand(args[1]);
        Hashtable varsCopy2 = new Hashtable(vars);
        VcsCommandExecutor vce2 = fileSystem.getVcsFactory().getCommandExecutor(cmd, varsCopy2);
        completeOutput = new CompleteStatusOutput();
        vce2.addDataOutputListener(completeOutput);
        CommandsPool pool = fileSystem.getCommandsPool();
        pool.startExecutor(vce1, fileSystem);
        pool.startExecutor(vce2, fileSystem);
        try {
            pool.waitToFinish(vce1);
            pool.waitToFinish(vce2);
        } catch (InterruptedException iexc) {
            pool.kill(vce1);
            pool.kill(vce2);
            return false;
        }
        boolean modulesStatGetStatus = vce1.getExitStatus() == VcsCommandExecutor.SUCCEEDED;
        boolean modulesAllGetStatus = vce2.getExitStatus() == VcsCommandExecutor.SUCCEEDED;
        return modulesStatGetStatus && modulesAllGetStatus;
    }

    private Vector getModules() {
        Vector modules = new Vector();
        String output = outputBuffer.toString();
       //MK rewrite to add status of the modules..  
        String line;
        int index = 0;
        StringBuffer module;
        int addedCount = 0;
        StringTokenizer token = new StringTokenizer(output, "\n", false);
        while (token.hasMoreTokens()) {
            Vector row = new Vector();
            line = token.nextToken();
            if (line.startsWith(" ") || line.startsWith("\t")) continue; // it's not a line with module name
            line = line.replace('\t',' ');
            StringTokenizer lineTok = new StringTokenizer(line, " ", false);
            if (lineTok.countTokens() < 2) continue;
            String prvni = lineTok.nextToken();
            row.add(prvni);
            row.add(lineTok.nextToken());
            modules.add(row);
        }
        completeOutput.insertDifferent(modules);
        return modules;  
    }

    public void outputData(String[] elements) {
        D.deb("match: "+elements[0]);
        if (elements[0].length() > 0 && elements[0].charAt(0) != '#')
            outputBuffer.append(elements[0]+"\n");
    }
    
    
    public Vector getModulesList (String[] args) {
        Vector modules = null;
        outputBuffer = new StringBuffer();
        cmdSuccess = runCommand(args);
        if (cmdSuccess) modules = getModules();
        return modules;
    }

    private final class CompleteStatusOutput implements CommandDataOutputListener {
        private LinkedList statuses;
        
        public CompleteStatusOutput() {
            statuses = new LinkedList();
        }
        
        public void removeFromList(String name) {
            statuses.remove(name);
        }
        
        public void  insertDifferent(Vector modules) {
            Iterator parIter = modules.iterator();
            while(parIter.hasNext()) { // first remove all that are in the list with statuses
                Vector row = (Vector)parIter.next();
                removeFromList((String)row.get(0));
            }
            Iterator it = statuses.iterator();
            while (it.hasNext()) {
                Vector row = new Vector();
                String str = (String)it.next();
                //D("in -c list:" + str);
                row.add(str);
                row.add("");
                modules.add(row);
            }
        }
        
        /** method from the NoRegExpListener.
         * reads al output of the checkout -c command.
         * @param elements lines.
         */
        public void outputData(String[] elements) {
            if (elements[0].length() > 0 && elements[0].charAt(0) != '#') {
                String start = elements[0];
                if (start == null) return;
                if (!start.startsWith(" ")) {
                    int index = start.indexOf(' ');
                    if (index > 0) {
                        String name = start.substring(0,index);
                        statuses.add(name);
                    }
                }
            }
        }
        
    }

}
