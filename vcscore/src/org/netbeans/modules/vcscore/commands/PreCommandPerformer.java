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

import java.io.*;
import java.util.*;

import org.openide.TopManager;
import org.openide.util.UserCancelException;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.util.*;

/**
 * This class checks if there are any commands to be run during preprocessing
 * the command. If it finds any commands to be run
 * (search for '{INSERT_OUTPUT_OF_<command_name>(<element_index>, ...)}' or 
 * '{FILE_OUTPUT_OF_<command_name>(<element_index>, ...)}' keys),
 * execute them and replace with their output data elements which are listed
 * as element_index.
 *
 * @author  Martin Entlicher
 */
public class PreCommandPerformer extends Object /*implements CommandDataOutputListener */{

    private Debug E=new Debug("PreCommandPerformer", true); // NOI18N
    private Debug D=E;

    //private static final String PRE_COMMAND = "{PRE_COMMAND";
    /**
     * Insert the output of the command instead of this keyword.
     */
    public static final String INSERT_OUTPUT = "{INSERT_OUTPUT_OF_";
    /**
     * Insert the error output of the command instead of this keyword.
     */
    public static final String INSERT_ERROR = "{INSERT_ERROR_OF_";
    /**
     * Write the output of the command to a temporary file and insert the file path
     * instead of this keyword.
     */
    public static final String FILE_OUTPUT = "{FILE_OUTPUT_OF_";
    private static final String TEMP_FILE_PREFIX = "tempVcsCmd";
    private static final String TEMP_FILE_SUFFIX = "output";

    private VcsFileSystem fileSystem;
    //private VcsCommand cmd;
    private Hashtable vars;

    private volatile Vector[] preCommandOutput;
    private volatile Vector[] preCommandError;
    //private volatile int preCommandExecuting = 0;

    /** Creates new CommandPerformer */
    public PreCommandPerformer(VcsFileSystem fileSystem, /*VcsCommand cmd, */Hashtable vars) {
        this.fileSystem = fileSystem;
        //this.cmd = cmd;
        this.vars = vars;
    }

    /**
     * Execute all commands and insert their output to the exec string.
     * @return the exec string with commands output.
     * @throws UserCancelException when the user cancelles the command.
     */
    public String process(String exec) throws UserCancelException {
        return process(exec, null);
    }
    
    /**
     * Execute all commands and insert their output to the exec string.
     * @param cmdOutputStates a collection, that is filled with the exit states
     *        of the executed commands. Boolean.TRUE for successfull execution
     *        and Boolean.FALSE for unsuccessfull execution. The collection can
     *        be null.
     * @return the exec string with commands output.
     * @throws UserCancelException when the user cancelles the command.
     */
    public String process(String exec, Collection cmdExitStates) throws UserCancelException {
        //String exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        if (exec == null) return null;
        //UserCommand[] preCommands = cmd.getPreCommands();
        ArrayList commands = findPreCommands(exec);
        String[] commandNames = (String[]) new TreeSet(commands).toArray(new String[0]);
        Collection exiStates = processPreCommands(commandNames);
        if (cmdExitStates != null) {
            cmdExitStates.clear();
            cmdExitStates.addAll(exiStates);
        }
        exec = insertPreCommandsOutput(exec, commandNames);
        return exec;
    }
    
    /**
     * Execute all commands and insert their output to the provided strings.
     * @return the array of strings of the same size as the original array,
     * with commands output filled to individual strings. The original array
     * is not changed.
     */
    public String[] process(String[] execs) throws UserCancelException {
        if (execs == null) return null;
        String[] processed = new String[execs.length];
        String concatenation = VcsUtilities.array2string(execs);
        ArrayList commands = findPreCommands(concatenation);
        String[] commandNames = (String[]) new TreeSet(commands).toArray(new String[0]);
        processPreCommands(commandNames);
        for (int i = 0; i < execs.length; i++) {
            processed[i] = insertPreCommandsOutput(execs[i], commandNames);
        }
        return processed;
    }
    
    /** @return the list of commands to run for the output */
    private ArrayList findPreCommands(String exec) {
        //D.deb("findPreCommands("+exec+")");
        ArrayList commands = new ArrayList();
        String[] outputs = { INSERT_OUTPUT, INSERT_ERROR, FILE_OUTPUT };
        for (int out = 0; out < outputs.length; out++) {
            int index = 0;
            do {
                int i = exec.indexOf(outputs[out], index);
                if (i >= 0) {
                    i += outputs[out].length();
                    int end = exec.indexOf('(', i);
                    if (end > 0) {
                        String name = exec.substring(i, end);
                        commands.add(name);
                    }
                }
                index = i;
            } while (index >= 0);
        }
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
    
    private Collection processPreCommands(String[] preCommands) throws UserCancelException {
        preCommandOutput = new Vector[preCommands.length];
        preCommandError = new Vector[preCommands.length];
        CommandsPool pool = fileSystem.getCommandsPool();
        ArrayList runningExecutors = new ArrayList();
        for (int i = 0; i < preCommands.length; i++) {
            String cmdName = preCommands[i];
            VcsCommand cmd = fileSystem.getCommand(cmdName);
            if (cmd == null) continue; // Nothing to run
            preCommandOutput[i] = new Vector();
            preCommandError[i] = new Vector();
            VcsCommandExecutor executor = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            int status = pool.preprocessCommand(executor, vars);
            if (CommandsPool.PREPROCESS_CANCELLED == status) throw new UserCancelException();
            if (CommandsPool.PREPROCESS_DONE != status) continue; // Something bad has happened
            executor.addDataOutputListener(new DataOutputContainer(i));
            executor.addDataErrorOutputListener(new DataErrorOutputContainer(i));
            pool.startExecutor(executor);
            runningExecutors.add(executor);
        }
        ArrayList exitStates = new ArrayList();
        while (runningExecutors.size() > 0) {
            VcsCommandExecutor vce = (VcsCommandExecutor) runningExecutors.get(0);
            pool.waitToFinish(vce);
            runningExecutors.remove(0);
            if (vce.getExitStatus() == VcsCommandExecutor.SUCCEEDED) {
                exitStates.add(Boolean.TRUE);
            } else {
                exitStates.add(Boolean.FALSE);
            }
        }
        return exitStates;
        //return insertPreCommandsOutput(exec, preCommands);
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
                        exec = insertOutput(exec, begin, endOfInsert + ")}".length(), exec.substring(end + 1, endOfInsert), where, preCommandOutput);
                    }
                }
            }
            index = i;
        } while (index >= 0);
        index = 0;
        do {
            int i = exec.indexOf(INSERT_ERROR, index);
            if (i >= 0) {
                int begin = i;
                i += INSERT_ERROR.length();
                int end = exec.indexOf('(', i);
                if (end > 0) {
                    String name = exec.substring(i, end);
                    int where = Arrays.binarySearch(commands, name);
                    int endOfInsert = exec.indexOf(")}", end);
                    if (endOfInsert > 0) {
                        exec = insertOutput(exec, begin, endOfInsert + ")}".length(), exec.substring(end + 1, endOfInsert), where, preCommandError);
                    }
                }
            }
            index = i;
        } while (index >= 0);
        index = 0;
        do {
            int i = exec.indexOf(FILE_OUTPUT, index);
            if (i >= 0) {
                int begin = i;
                i += FILE_OUTPUT.length();
                int end = exec.indexOf('(', i);
                if (end > 0) {
                    String name = exec.substring(i, end);
                    int where = Arrays.binarySearch(commands, name);
                    int endOfInsert = exec.indexOf(")}", end);
                    if (endOfInsert > 0) {
                        exec = fileOutput(exec, begin, endOfInsert + ")}".length(), exec.substring(end + 1, endOfInsert), where);
                    }
                }
            }
            index = i;
        } while (index >= 0);
        return exec;
    }

    private String insertOutput(String exec, int begin, int end, String whichElement, int whichOutput, Vector[] preCommandOutput) {
        StringBuffer insertion = new StringBuffer(exec.substring(0, begin));
        try {
            int index = Integer.parseInt(whichElement);
            for (Enumeration enum = preCommandOutput[whichOutput].elements(); enum.hasMoreElements(); ) {
                String[] elements = (String[]) enum.nextElement();
                if (elements.length > index && elements[index] != null) insertion.append(elements[index]);
            }
        } catch (NumberFormatException exc) {
            // Ignored
        }
        insertion.append(exec.substring(end, exec.length()));
        return insertion.toString();
    }
    
    private String fileOutput(String exec, int begin, int end, String whichElement, int whichOutput) {
        File outputFile;
        Writer writer;
        try {
            outputFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            outputFile.deleteOnExit();
            writer = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException ioexc) {
            TopManager.getDefault().notifyException(ioexc);
            return exec;
        }
        int index;
        try {
            index = Integer.parseInt(whichElement);
        } catch (NumberFormatException exc) {
            TopManager.getDefault().notifyException(exc);
            return exec;
        }
        try {
            for (Enumeration enum = preCommandOutput[whichOutput].elements(); enum.hasMoreElements(); ) {
                String[] elements = (String[]) enum.nextElement();
                if (elements.length > index) {
                    writer.write(((elements[index] != null) ? elements[index] : "") + "\n");
                }
            }
            writer.close();
        } catch (IOException ioexc) {
            TopManager.getDefault().notifyException(ioexc);
            return exec;
        }
        return exec.substring(0, begin) + outputFile.getAbsolutePath()
               + exec.substring(end, exec.length());
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
    
    private final class DataErrorOutputContainer extends Object implements CommandDataOutputListener {

        private int index;
        
        public DataErrorOutputContainer(int index) {
            this.index = index;
        }

        public void outputData(String[] elements) {
            preCommandError[index].add(elements);
        }
    }
    

}
