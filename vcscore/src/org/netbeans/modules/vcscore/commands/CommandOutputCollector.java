/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.openide.util.RequestProcessor;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * The collector of commands' output. Temporary disk files are used to store the
 * output to keep a small memory footprint.
 *
 * @author  Martin Entlicher
 */
public class CommandOutputCollector extends Object implements CommandProcessListener {
    
    /**
     * The default number of lines of commands' output to store in the memory.
     * When the output of a command will be longer than this number of lines,
     * the lines will be flushed to the disk and cleared from memory.
     * This is necessary in order not to run out of memory.
     */
    private static final int DEFAULT_NUM_OF_LINES_OF_OUTPUT_TO_COLLECT = 5000;
    
    /** The number of lines of commands' output to store and show to the user. */
    private int numOfLinesOfOutputToCollect = DEFAULT_NUM_OF_LINES_OF_OUTPUT_TO_COLLECT;
    
    private CommandProcessor commandProcessor;
    
    private VcsCommandExecutor vce;
    private long cmdId;
    private VcsCommandsProvider provider;
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
    
    private static final String RUNNING_FOLDER_PATH = "vcs/running";
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

    public CommandOutputCollector(CommandTask task, VcsCommandsProvider provider) {
        if (task instanceof VcsDescribedTask) {
            this.vce = ((VcsDescribedTask) task).getExecutor();
        }
        this.provider = provider;
        this.commandProcessor = CommandProcessor.getInstance();
        this.cmdId = commandProcessor.getTaskID(task);
        this.outputFiles = new File[NUM_OUTPUTS];
        if (runningFolder == null) initRunningFolder();
        createOutputListeners();
        commandProcessor.addCommandProcessListener(this);
    }
    
    private static synchronized void initRunningFolder() {
        if (runningFolder == null) {
            org.openide.filesystems.FileSystem defaultFS = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
            FileObject fo = defaultFS.findResource(RUNNING_FOLDER_PATH);
            if (fo == null) {
                java.util.StringTokenizer folders = new java.util.StringTokenizer(RUNNING_FOLDER_PATH, "/");
                FileObject root = defaultFS.getRoot();
                while(folders.hasMoreTokens()) {
                    String folder = folders.nextToken();
                    FileObject ff = root.getFileObject(folder);
                    if (ff == null) {
                        try {
                            ff = root.createFolder(folder);
                        } catch (IOException ioex) {
                            ErrorManager.getDefault().notify(ioex);
                            break;
                        }
                    }
                    root = ff;
                }
                fo = defaultFS.findResource(RUNNING_FOLDER_PATH);
            }
            if (fo != null) {
                runningFolder = FileUtil.toFile(fo);
            }
            if (runningFolder == null) { // We were not successfull, use the old approach: (should not occur)
                String userHome = System.getProperty("netbeans.user");
                runningFolder = new File(userHome, RUNNING_FOLDER_PATH);
                if (!runningFolder.exists() && !runningFolder.mkdirs()) {
                    runningFolder = new File(userHome);
                }
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
        if (vce != null) {
            vce.addTextOutputListener(new CollectingOutputListener(0));
            vce.addTextErrorListener(new CollectingOutputListener(1));
            vce.addRegexOutputListener(new CollectingOutputListener(0));
            vce.addRegexErrorListener(new CollectingOutputListener(1));
        }
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
                    ((TextOutputListener) it.next()).outputLine(line);
                }
            } else if (output instanceof String[]) {
                String[] elements = (String[]) output;
                for(Iterator it = cmdOutputListeners[outputId].iterator(); it.hasNext(); ) {
                    ((RegexOutputListener) it.next()).outputMatchedGroups(elements);
                }
            }
        }
    }

    /** Get the commands provider. The listener gets events only from commands,
     * that are instances of ProvidedCommand and their provider equals to this
     * provider. If returns <code>null</code>, the listener gets events from all
     * commands.
     * @return The provider or <code>null</code>.
     *
     */
    public VcsCommandsProvider getProvider() {
        return provider;
    }
    
    /** Called when the preprocessing of the command finished.
     * @param cmd The command which was preprocessed.
     * @param status The status of preprocessing. If false, the command is not executed.
     * Probably never called.
     */
    public void commandPreprocessed(Command cmd, boolean status) {
    }
    
    /** Called when the command is just to be preprocessed.
     * Probably never called.
     */
    public void commandPreprocessing(Command cmd) {
    }
    
    /** This method is called when the command is just to be started.
     *
     */
    public void commandStarting(CommandTaskInfo info) {
        VcsCommandExecutor vce = null;
        CommandTask task = info.getTask();
        if (task instanceof VcsDescribedTask) {
            vce = ((VcsDescribedTask) info.getTask()).getExecutor();
        }
        if (vce == null || !vce.equals(this.vce)) return ;
        this.cmdId = commandProcessor.getTaskID(task);
    }
    
    /** This method is called when the command is done.
     *
     */
    public void commandDone(CommandTaskInfo info) {
        VcsCommandExecutor vce = null;
        if (info.getTask() instanceof VcsDescribedTask) {
            vce = ((VcsDescribedTask) info.getTask()).getExecutor();
        }
        if (vce == null || !vce.equals(this.vce)) return ;
        synchronized (CommandOutputCollector.class) {
            if (collectorsFreeTask == null) {
                collectorsFreeTask = RequestProcessor.getDefault().create(new Runnable() {
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
        commandProcessor.removeCommandProcessListener(this);
    }
    
    private void freeCommandOutput() {
        //try {
            // Wait for all the output from the commands.
            //Thread.currentThread().sleep(5000);
        //} catch (InterruptedException exc) {
        //\\}
        if (cmdOutput == null) return ;
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
                if (listener instanceof TextOutputListener) {
                    TextOutputListener l = (TextOutputListener) listener;
                    for (Iterator it = cmdOutput[outputId].iterator(); it.hasNext(); ) {
                        l.outputLine((String) it.next());
                    }
                }
                if (listener instanceof RegexOutputListener) {
                    RegexOutputListener l = (RegexOutputListener) listener;
                    for (Iterator it = cmdOutput[outputId].iterator(); it.hasNext(); ) {
                        l.outputMatchedGroups((String[]) it.next());
                    }
                }
                if (cmdOutputListeners[outputId] != null) {
                    cmdOutputListeners[outputId].add(listener);
                }
            }
        }
    }
    
    private void addOutputListenerLazily(final int outputId, final Object listener) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                addOutputListener(outputId, listener);
            }
        });
    }
    
    /**
     * Add the listener to the standard output of the command. The listeners are removed
     * when the command finishes.
     * The output is passed to the listener asynchronously.
     */
    public void addTextOutputListener(TextOutputListener l) {
        addOutputListenerLazily(0, l);
    }
    
    /**
     * Add the listener to the standard output of the command. The listeners are removed
     * when the command finishes.
     * @param asynch When <code>true</code>, the output is passed to the listener asynchronously.
     *               When <code>false</code>, the listener gets available output
     *               immediately during the addition.
     */
    public void addTextOutputListener(TextOutputListener l, boolean asynch) {
        if (asynch) {
            addOutputListenerLazily(0, l);
        } else {
            addOutputListener(0, l);
        }
    }
    
    /**
     * Add the listener to the error output of the command. The listeners are removed
     * when the command finishes.
     * The output is passed to the listener asynchronously.
     */
    public void addTextErrorListener(TextOutputListener l) {
        addOutputListenerLazily(1, l);
    }
    
    /**
     * Add the listener to the error output of the command. The listeners are removed
     * when the command finishes.
     * @param asynch When <code>true</code>, the output is passed to the listener asynchronously.
     *               When <code>false</code>, the listener gets available output
     *               immediately during the addition.
     */
    public void addTextErrorListener(TextOutputListener l, boolean asynch) {
        if (asynch) {
            addOutputListenerLazily(1, l);
        } else {
            addOutputListener(1, l);
        }
    }
    
    /**
     * Add the listener to the data output of the command. This output may contain
     * a parsed information from its standard output or some other data provided
     * by this command. The listeners are removed when the command finishes.
     * The output is passed to the listener asynchronously.
     */
    public void addRegexOutputListener(RegexOutputListener l) {
        addOutputListenerLazily(2, l);
    }
    
    /**
     * Add the listener to the data output of the command. This output may contain
     * a parsed information from its standard output or some other data provided
     * by this command. The listeners are removed when the command finishes.
     * @param asynch When <code>true</code>, the output is passed to the listener asynchronously.
     *               When <code>false</code>, the listener gets available output
     *               immediately during the addition.
     */
    public void addRegexOutputListener(RegexOutputListener l, boolean asynch) {
        if (asynch) {
            addOutputListenerLazily(2, l);
        } else {
            addOutputListener(2, l);
        }
    }
    
    /**
     * Add the listener to the data error output of the command. This output may contain
     * a parsed information from its error output or some other data provided
     * by this command. If there are some data given to this listener, the command
     * is supposed to fail. The listeners are removed when the command finishes.
     * The output is passed to the listener asynchronously.
     */
    public synchronized void addRegexErrorListener(RegexOutputListener l) {
        addOutputListenerLazily(3, l);
    }
    
    /**
     * Add the listener to the data error output of the command. This output may contain
     * a parsed information from its error output or some other data provided
     * by this command. If there are some data given to this listener, the command
     * is supposed to fail. The listeners are removed when the command finishes.
     * @param asynch When <code>true</code>, the output is passed to the listener asynchronously.
     *               When <code>false</code>, the listener gets available output
     *               immediately during the addition.
     */
    public void addRegexErrorListener(RegexOutputListener l, boolean asynch) {
        if (asynch) {
            addOutputListenerLazily(3, l);
        } else {
            addOutputListener(3, l);
        }
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
            org.openide.ErrorManager.getDefault().notify(exc);
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
                if (listener instanceof TextOutputListener) {
                    TextOutputListener l = (TextOutputListener) listener;
                    l.outputLine(line);
                }
                if (listener instanceof RegexOutputListener) {
                    RegexOutputListener l = (RegexOutputListener) listener;
                    l.outputMatchedGroups(decodeElements(line));
                }
            }
        } catch (IOException exc) {
            org.openide.ErrorManager.getDefault().notify(exc);
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

    private class CollectingOutputListener extends Object implements TextOutputListener, RegexOutputListener {
        
        private int outIndex;
        
        public CollectingOutputListener(int outIndex) {
            this.outIndex = outIndex;
        }
        
        /** This method is called, with a line of the output text.
         * @param line one line of output text.
         *
         */
        public void outputLine(String line) {
            addOutput(0 + outIndex, line);
        }
        
        /** This method is called, with elements of the parsed data.
         * @param elements the elements of parsed data.
         *
         */
        public void outputMatchedGroups(String[] elements) {
            addOutput(2 + outIndex, elements);
        }
        
    }
    
}
