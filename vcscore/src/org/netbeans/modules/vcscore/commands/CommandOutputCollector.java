/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.openide.util.RequestProcessor;

/**
 * The collector of commands' output. Temporary disk files are used to store the
 * output to keep a small memory footprint.
 *
 * @author  Martin Entlicher
 */
class CommandOutputCollector extends Object implements CommandListener {
    
    /**
     * The default number of lines of commands' output to store in the memory.
     * When the output of a command will be longer than this number of lines,
     * the lines will be flushed to the disk and cleared from memory.
     * This is necessary for not running out of memory.
     */
    private static final int DEFAULT_NUM_OF_LINES_OF_OUTPUT_TO_COLLECT = 5000;
    
    /** The number of lines of commands' output to store and show to the user. */
    private int numOfLinesOfOutputToCollect = DEFAULT_NUM_OF_LINES_OF_OUTPUT_TO_COLLECT;
    
    private CommandsPool commandsPool;
    
    private VcsCommandExecutor vce;
    private long cmdId;
    /*
    private ArrayList stdOutput = new ArrayList();
    private ArrayList errOutput = new ArrayList();
    private ArrayList stdDataOutput = new ArrayList();
    private ArrayList errDataOutput = new ArrayList();
    
    private ArrayList stdOutputListeners = new ArrayList();
    private ArrayList errOutputListeners = new ArrayList();
    private ArrayList stdDataOutputListeners = new ArrayList();
    private ArrayList errDataOutputListeners = new ArrayList();
     */
    
    private static final int NUM_OUTPUTS = 4;
    private static final String[] OUTPUT_FILE_ID = new String[] { "so", "eo", "sd", "ed" };
    
    private static final String RUNNING_FOLDER_PATH = "system/vcs/running/";
    private static final String FILE_PREFIX = "cmd";
    private static final String FILE_MIDFIX = "_";
    private static final String FILE_POSTFIX = ".txt";
    private static File runningFolder = null;
    
    private static ArrayList outputCollectorsToFree = new ArrayList();
    private static RequestProcessor.Task collectorsFreeTask = null;
    
    private ArrayList[] cmdOutput;
    private ArrayList[] cmdOutputListeners;
    private File[] outputFiles;
    private boolean finalized = false;

    public CommandOutputCollector(VcsCommandExecutor vce, CommandsPool commandsPool) {
        this.vce = vce;
        this.commandsPool = commandsPool;
        this.cmdId = commandsPool.getCommandID(vce);
        this.outputFiles = new File[NUM_OUTPUTS];
        if (runningFolder == null) initRunningFolder();
        createOutputListeners();
        commandsPool.addCommandListener(this);
    }
    
    private static synchronized void initRunningFolder() {
        if (runningFolder == null) {
            String userHome = System.getProperty("netbeans.user");
            runningFolder = new File(userHome, RUNNING_FOLDER_PATH);
            if (!runningFolder.exists() && !runningFolder.mkdirs()) {
                runningFolder = new File(userHome);
            }
        }
    }
    
    private void createOutputListeners() {
        cmdOutput = new ArrayList[NUM_OUTPUTS];
        cmdOutputListeners = new ArrayList[NUM_OUTPUTS];
        for (int i = 0; i < NUM_OUTPUTS; i ++) {
            cmdOutput[i] = new ArrayList();
            cmdOutputListeners[i] = new ArrayList();
        }
        vce.addOutputListener(new CommandOutputListener() {
            public void outputLine(String line) {
                addOutput(0, line);
            }
        });
        vce.addErrorOutputListener(new CommandOutputListener() {
            public void outputLine(String line) {
                addOutput(1, line);
            }
        });
        vce.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] elements) {
                addOutput(2, elements);
            }
        });
        vce.addDataErrorOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] elements) {
                addOutput(3, elements);
            }
        });
    }
    
    private void addOutput(int outputId, Object output) {
        synchronized (cmdOutput[outputId]) {
            cmdOutput[outputId].add(output);
            if (cmdOutput[outputId].size() > numOfLinesOfOutputToCollect) {
                flushOutput(outputId);
                cmdOutput[outputId].clear();
            }
            if (output instanceof String) {
                String line = (String) output;
                for(Iterator it = cmdOutputListeners[outputId].iterator(); it.hasNext(); ) {
                    ((CommandOutputListener) it.next()).outputLine(line);
                }
            } else if (output instanceof String[]) {
                String[] elements = (String[]) output;
                for(Iterator it = cmdOutputListeners[outputId].iterator(); it.hasNext(); ) {
                    ((CommandDataOutputListener) it.next()).outputData(elements);
                }
            }
        }
    }
    
    /**
     * This method is called when the command is just to be started.
     *
     * WARNING: this method might be never called. The CommandOutputCollector
     * is usually created AFTER the command starts.
     */
    public final void commandStarted(VcsCommandExecutor vce) {
    }
    
    /**
     * This method is called when the command is done.
     */
    public void commandDone(VcsCommandExecutor vce) {
        if (!this.vce.equals(vce)) return ;
        synchronized (CommandOutputCollector.class) {
            if (collectorsFreeTask == null) {
                collectorsFreeTask = new RequestProcessor().create(new Runnable() {
                    public void run() {
                        synchronized (CommandOutputCollector.class) {
                            for (Iterator it = outputCollectorsToFree.iterator(); it.hasNext(); ) {
                                ((CommandOutputCollector) it.next()).freeCommandOutput();
                            }
                            outputCollectorsToFree.clear();
                        }
                    }
                });
                collectorsFreeTask.setPriority(Thread.MIN_PRIORITY);
            }
            if (!finalized) outputCollectorsToFree.add(this);
        };
        collectorsFreeTask.schedule(5000);
        //new Thread(later).start();
        commandsPool.removeCommandListener(this);
    }
    
    private void freeCommandOutput() {
        //try {
            // Wait for all the output from the commands.
            //Thread.currentThread().sleep(5000);
        //} catch (InterruptedException exc) {
        //\\}
        for (int i = 0; i < NUM_OUTPUTS; i++) {
            synchronized (cmdOutput[i]) {
                cmdOutputListeners[i] = null;
                flushOutput(i);
                cmdOutput[i] = null;
            }
        }
        cmdOutput = null;
    }
    
    private void addOutputListener(int outputId, Object listener) {
        sendCommandOutputFromFile(outputId, listener);
        if (cmdOutput != null && cmdOutput[outputId] != null) {
            synchronized (cmdOutput[outputId]) {
                if (cmdOutput == null || cmdOutput[outputId] == null) return ;
                if (listener instanceof CommandOutputListener) {
                    CommandOutputListener l = (CommandOutputListener) listener;
                    for (Iterator it = cmdOutput[outputId].iterator(); it.hasNext(); ) {
                        l.outputLine((String) it.next());
                    }
                }
                if (listener instanceof CommandDataOutputListener) {
                    CommandDataOutputListener l = (CommandDataOutputListener) listener;
                    for (Iterator it = cmdOutput[outputId].iterator(); it.hasNext(); ) {
                        l.outputData((String[]) it.next());
                    }
                }
                if (cmdOutputListeners[outputId] != null) {
                    cmdOutputListeners[outputId].add(listener);
                }
            }
        }
    }
    
    /**
     * Add the listener to the standard output of the command. The listeners are removed
     * when the command finishes.
     */
    public void addOutputListener(CommandOutputListener l) {
        addOutputListener(0, l);
    }
    
    /**
     * Add the listener to the error output of the command. The listeners are removed
     * when the command finishes.
     */
    public void addErrorOutputListener(CommandOutputListener l) {
        addOutputListener(1, l);
    }
    
    /**
     * Add the listener to the data output of the command. This output may contain
     * a parsed information from its standard output or some other data provided
     * by this command. The listeners are removed when the command finishes.
     */
    public void addDataOutputListener(CommandDataOutputListener l) {
        addOutputListener(2, l);
    }
    
    /**
     * Add the listener to the data error output of the command. This output may contain
     * a parsed information from its error output or some other data provided
     * by this command. If there are some data given to this listener, the command
     * is supposed to fail. The listeners are removed when the command finishes.
     */
    public synchronized void addDataErrorOutputListener(CommandDataOutputListener l) {
        addOutputListener(3, l);
    }
    
    private File createOutputFile(int outputId) {
        if (outputFiles[outputId] == null) {
            String file = FILE_PREFIX + cmdId + FILE_MIDFIX + OUTPUT_FILE_ID[outputId] + FILE_POSTFIX;
            outputFiles[outputId] = new File(runningFolder, file);
            try {
                outputFiles[outputId].delete();
                outputFiles[outputId].createNewFile();
            } catch (IOException exc) {}
            outputFiles[outputId].deleteOnExit();
        }
        return outputFiles[outputId];
    }
    
    private void flushOutput(int outputId) {
        File outputFile = createOutputFile(outputId);
        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new FileWriter(outputFile.getAbsolutePath(), true));
            for (Iterator it = cmdOutput[outputId].iterator(); it.hasNext(); ) {
                Object output = it.next();
                String line;
                if (output instanceof String) {
                    line = (String) output;
                } else if (output instanceof String[]) {
                    String[] elements = (String[]) output;
                    line = encodeElements(elements);
                } else line = "";
                w.write(line + "\n");
            }
        } catch (IOException exc) {
            org.openide.TopManager.getDefault().notifyException(exc);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException exc2) {}
            }
        }
    }
    
    private void sendCommandOutputFromFile(int outputId, Object listener) {
        if (outputFiles[outputId] == null) return ;
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(outputFiles[outputId]));
            String line;
            while ((line = r.readLine()) != null) {
                if (listener instanceof CommandOutputListener) {
                    CommandOutputListener l = (CommandOutputListener) listener;
                    l.outputLine(line);
                }
                if (listener instanceof CommandDataOutputListener) {
                    CommandDataOutputListener l = (CommandDataOutputListener) listener;
                    l.outputData(decodeElements(line));
                }
            }
        } catch (IOException exc) {
            org.openide.TopManager.getDefault().notifyException(exc);
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException exc2) {}
            }
        }
    }
    
    private String encodeElements(String[] elements) {
        String line = "";
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] != null) {
                line += org.openide.util.Utilities.replaceString(elements[i], "/", " // ") + " / ";
            } else {
                line += " /null/ ";
            }
        }
        return line;
    }
    
    private String[] decodeElements(String line) {
        ArrayList elements = new ArrayList();
        int index = 0;
        int lastIndex = 0;
        while (true) {
            int index1 = line.indexOf(" / ", lastIndex);
            int index2 = line.indexOf(" /null/ ", lastIndex);
            if (index1 < 0 && index2 < 0) break;
            if (index1 >= 0) index = index1;
            else index = index2;
            int sepLength;
            if (index2 >= 0 && index2 < index) {
                index = index2;
                sepLength = " /null/ ".length();
            } else {
                sepLength = " / ".length();
            }
            String element = line.substring(lastIndex, index);
            elements.add(org.openide.util.Utilities.replaceString(element, " // ", "/"));
            lastIndex = index + sepLength;
        }
        return (String[]) elements.toArray(new String[0]);
    }
    
    protected void finalize() {
        finalized = true;
        synchronized (CommandOutputCollector.class) {
            // It's not worth to write the output if we're gonna to delete it.
            outputCollectorsToFree.remove(this);
        }
        if (outputFiles != null) {
            for (int i = 0; i < NUM_OUTPUTS; i++) {
                if (outputFiles[i] != null) {
                    outputFiles[i].delete();
                }
            }
        }
    }

}
