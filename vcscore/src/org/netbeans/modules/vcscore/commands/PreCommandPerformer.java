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

package org.netbeans.modules.vcscore.commands;

import java.util.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
//import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.util.*;
/*
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.CommandsPool;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
 */

/**
 * This class checks if there are any commands to be run during preprocessing
 * the command. If it finds any commands to be run
 * (search for '{INSERT_OUTPUT_OF_<command_name>(<element_index>, ...)}' keys),
 * execute them and replace with their output data elements which are listed
 * as element_index(es).
 *
 * @author  Martin Entlicher
 */
public class PreCommandPerformer extends Object /*implements CommandDataOutputListener */{

    private Debug E=new Debug("PreCommandPerformer", true); // NOI18N
    private Debug D=E;

    //private static final String PRE_COMMAND = "{PRE_COMMAND";
    private static final String INSERT_OUTPUT = "{INSERT_OUTPUT_OF_";

    private VcsFileSystem fileSystem;
    private VcsCommand cmd;
    private Hashtable vars;

    private volatile Vector[] preCommandOutput;
    //private volatile int preCommandExecuting = 0;

    /** Creates new CommandPerformer */
    public PreCommandPerformer(VcsFileSystem fileSystem, VcsCommand cmd, Hashtable vars) {
        this.fileSystem = fileSystem;
        this.cmd = cmd;
        this.vars = vars;
    }

    public String process() {
        String exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        if (exec == null) return null;
        //UserCommand[] preCommands = cmd.getPreCommands();
        ArrayList commands = findPreCommands(exec);
        exec = processPreCommands((String[]) new TreeSet(commands).toArray(new String[0]), exec);
        /*
        if (preCommands.length > 0) {
            if (VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_PRECOMMANDS_EXECUTE)) {
                exec = processPreCommands(preCommands);
            } else {
                exec = putEmptyOutput(preCommands);
            }
        } else {
            exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        }
        D.deb("process(): return exec = "+exec);
         */
        return exec;
    }
    
    /** @return the list of commands to run for the output */
    private ArrayList findPreCommands(String exec) {
        //D.deb("findPreCommands("+exec+")");
        ArrayList commands = new ArrayList();
        int index = 0;
        do {
            int i = exec.indexOf(INSERT_OUTPUT, index);
            if (i >= 0) {
                i += INSERT_OUTPUT.length();
                int end = exec.indexOf('(', i);
                if (end > 0) {
                    String name = exec.substring(i, end);
                    commands.add(name);
                }
            }
            index = i;
        } while (index >= 0);
        return commands;
    }

    /*
    private String putEmptyOutput(UserCommand[] preCommands) {
        String exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        int n = preCommands.length;
        preCommandOutput = new Vector[n];
        for(int i = 0; i < n; i++) {
            preCommandOutput[i] = new Vector();
        }
        exec = insertPreCommandsOutput(exec, n);
        return exec;
    }
     */
    
    private String processPreCommands(String[] preCommands, String exec) {
        preCommandOutput = new Vector[preCommands.length];
        CommandsPool pool = fileSystem.getCommandsPool();
        ArrayList runningExecutors = new ArrayList();
        for (int i = 0; i < preCommands.length; i++) {
            String cmdName = preCommands[i];
            VcsCommand cmd = fileSystem.getCommand(cmdName);
            if (cmd == null) continue; // Nothing to run
            preCommandOutput[i] = new Vector();
            VcsCommandExecutor executor = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            int status = pool.preprocessCommand(executor, vars);
            if (CommandsPool.PREPROCESS_DONE != status) continue; // Something bad has happened
            executor.addDataOutputListener(new DataOutputContainer(i));
            pool.startExecutor(executor);
            runningExecutors.add(executor);
        }
        while (runningExecutors.size() > 0) {
            VcsCommandExecutor vce = (VcsCommandExecutor) runningExecutors.get(0);
            pool.waitToFinish(vce);
            runningExecutors.remove(0);
        }
        return insertPreCommandsOutput(exec, preCommands);
    }

    /*
    private String processPreCommands(UserCommand[] preCommands) {
        String exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        int n = preCommands.length;
        preCommandOutput = new Vector[n];
        for(int i = 0; i < n; i++) {
            preCommandExecuting = i;
            preCommandOutput[i] = new Vector();
            //OutputContainer container = new OutputContainer(preCommands[i]);
            ExecuteCommand ec = new ExecuteCommand(fileSystem, preCommands[i], vars);
            CommandsPool pool = fileSystem.getCommandsPool();
            pool.add(ec);
            //ec.setErrorNoRegexListener(container);
            //ec.setOutputNoRegexListener(container);
            //ec.setErrorContainer(container);
            ec.addDataOutputListener(this);
            pool.startExecutor(ec);
            pool.waitToFinish(ec);
            //ec.start();
            //try {
            //    ec.join();
            //} catch (InterruptedException e) {
                // ignoring the interruption
            //}
        }
        exec = insertPreCommandsOutput(exec, n);
        return exec;
    }
     */
    
    private String insertPreCommandsOutput(String exec, String[] commands) {
        int index = 0;
        do {
            int i = exec.indexOf(INSERT_OUTPUT, index);
            if (i >= 0) {
                int begin = i;
                i += INSERT_OUTPUT.length();
                int end = exec.indexOf('(', i);
                if (end > 0) {
                    String name = exec.substring(i, end);
                    int where = Arrays.binarySearch(commands, name);
                    int endOfInsert = exec.indexOf(")}", end);
                    if (endOfInsert > 0) {
                        exec = insertOutput(exec, begin, endOfInsert + ")}".length(), exec.substring(end + 1, endOfInsert), where);
                    }
                }
            }
            index = i;
        } while (index >= 0);
        return exec;
    }

    /*
    private String insertPreCommandsOutput(String exec, int n) {
        D.deb("insertPreCommandsOutput("+exec+", "+n+")");
        for(int i = 0; i < n; i++) {
            int index = -1;
            do {
                String matchStr = "";
                if (i == 0) {
                    matchStr = PRE_COMMAND+"(";
                    index = exec.indexOf(matchStr);
                }
                if (index < 0) {
                    matchStr = PRE_COMMAND+"_"+Integer.toString(i)+"(";
                    index = exec.indexOf(matchStr);
                }
                if (index >= 0) {
                    exec = insertOutput(exec, index, index + matchStr.length(), i);
                }
            } while(index >= 0);
        }
        return exec;
    }
     */

    private String insertOutput(String exec, int begin, int end, String whichElement, int whichOutput) {
        StringBuffer insertion = new StringBuffer(exec.substring(0, begin));
        try {
            int index = Integer.parseInt(whichElement);
            for (Enumeration enum = preCommandOutput[whichOutput].elements(); enum.hasMoreElements(); ) {
                String[] elements = (String[]) enum.nextElement();
                if (elements.length > index) insertion.append(elements[index]);
            }
        } catch (NumberFormatException exc) {
            // Ignored
        }
        insertion.append(exec.substring(end, exec.length()));
        return insertion.toString();
    }
    /*
    private String insertOutput(String exec, int begin, int index, int which) {
        D.deb("insertOutput("+exec+", "+begin+", "+index+", "+which+")");
        int end = VcsUtilities.getPairIndex(exec, index, '(', ')');
        if (end < 0) return exec;
        int finalEnd = VcsUtilities.getPairIndex(exec, index, '{', '}');
        if (finalEnd < 0) return exec;
        String regex = exec.substring(index, end);
        int[] fields = null;
        if (exec.charAt(end + 1) == '[') {
            int end2 = VcsUtilities.getPairIndex(exec, end+2, '[', ']');
            if (end2 >= 0) {
                String fieldStr = exec.substring(end+2, end2);
                int numFields = VcsUtilities.numChars(fieldStr, ',') + 1;
                fields = new int[numFields];
                int pos = end+2;
                for(int i = 0; i < numFields; i++) {
                    int pos2 = exec.indexOf(',', pos);
                    if (pos2 < 0) pos2 = exec.indexOf(']', pos);
                    String numStr = exec.substring(pos, pos2).trim();
                    int num = 0;
                    try {
                        num = Integer.parseInt(numStr);
                    } catch (NumberFormatException exc) {
                        num = 0;
                    }
                    fields[i] = num;
                }
            }
        }
        StringBuffer insertion = new StringBuffer();
        for(Enumeration enum = preCommandOutput[which].elements(); enum.hasMoreElements(); ) {
            String[] elements = (String[]) enum.nextElement();
            if (fields == null) {
                insertion.append(VcsUtilities.array2string(elements).trim());
            } else {
                for(int i = 0; i < fields.length; i++) {
                    if (fields[i] < elements.length) insertion.append(elements[fields[i]]);
                }
            }
        }
        D.deb("insertion = '"+insertion+"'");
        return exec.substring(0, begin)+insertion.toString()+exec.substring(finalEnd+1);
    }
     */


    private final class DataOutputContainer extends Object implements CommandDataOutputListener {

        private int index;
        
        public DataOutputContainer(int index) {
            this.index = index;
        }

        public void outputData(String[] elements) {
            preCommandOutput[index].add(elements);
        }
    }

}
