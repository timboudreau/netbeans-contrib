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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Customizer;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;

import org.openide.ErrorManager;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.RepositoryListener;
import org.openide.filesystems.RepositoryEvent;
import org.openide.filesystems.RepositoryReorderedEvent;
import org.openide.nodes.BeanNode;
import org.openide.util.RequestProcessor;
import org.openide.util.UserCancelException;
import org.openide.util.WeakListener;
//import org.openide.nodes.AbstractNode;
//import org.openide.nodes.Children;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.VcsAction;
//import org.netbeans.modules.vcscore.runtime.*;
//import org.netbeans.modules.vcscore.cache.FileSystemCache;
//import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.util.Table;
//import org.netbeans.modules.vcscore.util.TopComponentCloseListener;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;

/**
 * This class is used as a container of all external commands which are either running or finished.
 * @author  Martin Entlicher
 */
public class CommandProcessor extends Object /*implements CommandListener */{

    /**
     * The preprocessing of the command was cancelled. The command will not be executed.
     */
    public static final int PREPROCESS_CANCELLED = 0;
    
    /**
     * When there are more files selected, the preprocessing needs to be done for
     * next files again. The command will run on the first file, preprocessing will be
     * done for the rest.
     */
    public static final int PREPROCESS_NEXT_FILE = 1;
    
    /**
     * The preprocessing is done. When more files are selected, the command
     * will not be preprocessed for the rest of them.
     */
    public static final int PREPROCESS_DONE = 2;
    
    /** The maximum number of running commands in the system. This prevents overwhelming
     * the system with too many commands running concurrently */
    private static final int MAX_NUM_RUNNING_COMMANDS = 10;
    /** The maximum number of running refresh commands in the system. This prevents
     * overwhelming the system with too many refresh commands with the ability
     * to still run other commands concurrently */
    private static final int MAX_NUM_RUNNING_LISTS = 7;
    
    private static long lastCommandID = 0;
    
    private static CommandProcessor instance = null;
    
    /** Contains instances of Command, which are to be preprocessed.  */
    private ArrayList commandsToPreprocess;
    /** Contains instances of Command, which are being preprocessed.  */
    private ArrayList commandsPreprocessing;
    /** The table of instances of CommandTask and associated CommandTaskInfo */
    private Hashtable taskInfos;
    /** Contains instances of CommandTaskInfos, which are waiting to run. */
    private ArrayList taskWaitQueue;
    /** Contains instances of CommandTaskInfos, which are running. */
    private ArrayList tasksRunning;
    /** Contains instances of CommandTaskInfos, which are running as an exception.
     *  they are executed to prevent deadlock. The deadlock can occure if there
     *  were executed the maximum number of commands and they need to run some
     *  sub-commands. These subcommands can not be executed without an introduction
     *  of these exceptional commands. */
    private ArrayList tasksExceptionallyRunning;
    /** The containers of output of commands. Contains pairs of instances of VcsCommandExecutor
     * and instances of CommandOutputCollector */
    //private Hashtable outputContainers;
    /** The table of currently opened standard Visualizers */
    //private Hashtable outputVisualizers;
    /** Contains finished instances of CommandTaskInfo. */
    //private ArrayList commandsFinished;
    private int numRunningListCommands;
    
    //private ThreadGroup group;
    private RequestProcessor threadsPool;
    
    /** Map of instances of VcsCommandProvider and associated list of command
     * process listeners. */
    private Map commandListenersByProviders = new HashMap();
    /** List of listeners registered for all providers */
    private List commandListenersForAllProviders = new ArrayList();
    
    private boolean execStarterLoopStarted = false;
    private boolean execStarterLoopRunning = true;

    /** Creates new CommandProcessor */
    private CommandProcessor() {
        commandsToPreprocess = new ArrayList();
        commandsPreprocessing = new ArrayList();
        //commandsToRun = new ArrayList();
        taskInfos = new Hashtable();
        taskWaitQueue = new ArrayList();
        tasksRunning = new ArrayList();
        tasksExceptionallyRunning = new ArrayList();
        //commandsFinished = new ArrayList();
        //outputContainers = new Hashtable();
        //outputVisualizers = new Hashtable();
        numRunningListCommands = 0;
        threadsPool = RequestProcessor.getDefault();
        //group = new ThreadGroup("VCS Commands Group");
        //executorStarterLoop();
    }
    
    /**
     * Get the instance of CommandProcessor.
     */
    public static synchronized CommandProcessor getInstance() {
        if (instance == null) {
            instance = new CommandProcessor();
        }
        return instance;
    }
    
    protected void finalize () {
        cleanup();
    }
    
    /**
     * This stops the execution starter loop.
     * You will not be able to execute any command by CommandProcessor after this method finishes !
     */
    public void cleanup() {
        synchronized (this) {
            //* The FS may still exist i.e. inside a MultiFileSystem => do not interrupt the loop now
            execStarterLoopRunning = false;
            notifyAll();
            // */
        }
    }
    
    /**
     * Get the task's ID. It's a unique task identification number.
     * @param task The task
     * @return the ID or -1 if the task does not have one.
     */
    public long getTaskID(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw != null) return cw.getCommandID();
        else return -1;
    }
    
    /**
     * Pre-process the task. This will show the command's customizer.
     */
    public synchronized void preprocess(Command cmd) {
        //taskInfos.put(info.getTask(), info);
        //info.setSubmittingThread(Thread.currentThread());
        commandsToPreprocess.add(cmd);
        notifyAll();
        if (!execStarterLoopStarted) {
            runExecutorStarterLoop();
        }
    }

    /**
     * Process the task. This will schedule the task for execution.
     */
    public synchronized void process(CommandTaskInfo info) {
        taskInfos.put(info.getTask(), info);
        info.setSubmittingThread(Thread.currentThread());
        taskWaitQueue.add(info);
        notifyAll();
        if (!execStarterLoopStarted) {
            runExecutorStarterLoop();
        }
    }
    
    private synchronized void doPreprocess(final Command cmd) {
        commandsPreprocessing.add(cmd);
        threadsPool.post(new Runnable() {
            public void run() {
                List commandListeners;
                Object provider = null;
                if (cmd instanceof ProvidedCommand) {
                    provider = ((ProvidedCommand) cmd).getProvider();
                }
                synchronized (commandListenersByProviders) {
                    commandListeners = new ArrayList(commandListenersForAllProviders);
                    List providerListeners = (List) commandListenersByProviders.get(provider);
                    if (providerListeners != null) commandListeners.addAll(providerListeners);
                }
                for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                    ((CommandProcessListener) it.next()).commandPreprocessing(cmd);
                }
                boolean status = false;
                try {
                    status = showCustomizer(cmd);
                } catch (IntrospectionException iex) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, iex);
                    status = false;
                } finally {
                    for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                        ((CommandProcessListener) it.next()).commandPreprocessed(cmd, status);
                    }
                }
            }
        });
    }
    
    private boolean showCustomizer(Command cmd) throws IntrospectionException {
        /*
        BeanNode beanNode = new BeanNode(cmd);
        Component cust = beanNode.getCustomizer();
        boolean status;
        if (cust != null) {
            DialogDescriptor dlg = new DialogDescriptor(cust, cmd.getDisplayName(),
                                                        true, DialogDescriptor.OK_CANCEL_OPTION,
                                                        DialogDescriptor.OK_OPTION, null);
            status = NotifyDescriptor.OK_OPTION.equals(TopManager.getDefault().notify(dlg));
        } else {
            //PropertyPanel panel = new PropertyPanel(
            //beanNode.getPropertySets();
            // TODO
            status = false;
        }
        return status;
         */
        FileObject[] files = cmd.getFiles();
        if (files != null) VcsAction.assureFilesSaved(Arrays.asList(files));
        Component cust = null;
        DialogDescriptor dlg = null;
        //System.out.println("cmd instanceof BeanInfo = "+(cmd instanceof BeanInfo));
        if (cmd instanceof BeanInfo) {
            Class customizerClass = null;
            BeanDescriptor descriptor = ((BeanInfo) cmd).getBeanDescriptor();
            if (descriptor != null) customizerClass = descriptor.getCustomizerClass();
            //System.out.println("customizerClass = "+customizerClass);
            if (customizerClass != null) {
                try {
                    Customizer c = (Customizer) customizerClass.newInstance();
                    c.setObject(cmd);
                    if (c instanceof Component) {
                        cust = (Component) c;
                    } else if (c instanceof DialogDescriptor) {
                        dlg = (DialogDescriptor) c;
                        //cust = org.openide.TopManager.getDefault().createDialog((org.openide.DialogDescriptor) c);
                    }
                } catch (Exception ex) {}
            }
        // HACK with the PrivilegedAction to get a custom customizer
        } else if (cmd instanceof java.security.PrivilegedAction) {
            Object customizer;
            try {
                customizer = ((java.security.PrivilegedAction) cmd).run();
            } catch (ThreadDeath td) {
                throw td;
            } catch (Throwable th) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, th);
                customizer = null;
            }
            //System.out.println("customizer of command "+cmd+" = "+customizer);
            //System.out.println("customizer instanceof Component = "+(customizer instanceof Component));
            if (customizer instanceof UserCancelException) {
                return false;
            }
            if (customizer instanceof Component) {
                cust = (Component) customizer;
            } else if (customizer instanceof DialogDescriptor) {
                dlg = (DialogDescriptor) customizer;
                //cust = org.openide.TopManager.getDefault().createDialog((org.openide.DialogDescriptor) customizer);
            } else if (customizer instanceof BeanInfo) {
                BeanDescriptor descriptor = ((BeanInfo) customizer).getBeanDescriptor();
                Class customizerClass = null;
                if (descriptor != null) customizerClass = descriptor.getCustomizerClass();
                //System.out.println("customizerClass = "+customizerClass);
                if (customizerClass != null) {
                    try {
                        Customizer c = (Customizer) customizerClass.newInstance();
                        c.setObject(cmd);
                        if (c instanceof Component) {
                            cust = (Component) c;
                        } else if (c instanceof DialogDescriptor) {
                            dlg = (DialogDescriptor) c;
                            //cust = org.openide.TopManager.getDefault().createDialog((org.openide.DialogDescriptor) c);
                        }
                    } catch (Exception ex) {}
                }
            } else if (customizer != null) {
                BeanNode beanNode = new BeanNode(customizer);
                cust = beanNode.getCustomizer();
            }
        } else if (cust == null && dlg == null) {
            BeanNode beanNode = new BeanNode(cmd);
            cust = beanNode.getCustomizer();
        }
        boolean status;
        //System.out.println("customizer = "+cust);
        if (dlg != null) {
            status = NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dlg));
        } else if (cust != null) {
            ActionListener actionL = null;
            java.lang.reflect.Method addActionListenerMethod = null;
            java.lang.reflect.Method getDisplayNameMethod = null;
            if (cust instanceof ActionListener) actionL = (ActionListener) cust;
            try {
                addActionListenerMethod = cust.getClass().getMethod("addActionListener", new Class[] { ActionListener.class });
            } catch (Exception ex) {}
            try {
                getDisplayNameMethod = cust.getClass().getMethod("getDisplayTitle",null);
            } catch (Exception ex) {}
            String displayName = null;
            if (getDisplayNameMethod != null) {
                try {
                    displayName = (String) getDisplayNameMethod.invoke(cust, null);
                } catch (Exception ex) {}
            }
            if (displayName == null) {
                displayName = cmd.getDisplayName();
            }
            dlg = new DialogDescriptor(cust, displayName,
                                       true, DialogDescriptor.OK_CANCEL_OPTION,
                                       DialogDescriptor.OK_OPTION, actionL);
            if (addActionListenerMethod == null) {
                status = NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dlg));
            } else {
                dlg.setClosingOptions(new Object[] { NotifyDescriptor.CANCEL_OPTION });
                final Dialog dialog = DialogDisplayer.getDefault().createDialog(dlg);
                try {
                    final boolean [] statusContainer = new boolean[1];
                    addActionListenerMethod.invoke(cust, new Object[] { new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            if (evt.getID() == ActionEvent.ACTION_PERFORMED) {
                                if (NotifyDescriptor.OK_OPTION.equals(evt.getSource())) {
                                    statusContainer[0] = true;
                                    dialog.dispose();
                                } else if (NotifyDescriptor.CANCEL_OPTION.equals(evt.getSource())) {
                                    statusContainer[0] = false;
                                    dialog.dispose();
                                }
                            }
                        }
                    }});
                    dialog.setVisible(true);
                    status = statusContainer[0];
                } catch (Exception ex) {
                    status = NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dlg));
                }
            }
        } else {
            //PropertyPanel panel = new PropertyPanel(
            //beanNode.getPropertySets();
            // TODO
            status = true;
        }
        //System.out.println("CommandProcessor.showCustomizer() = "+status);
        return status;
    }

    /**
     * Perform preprocessing of a new command. It will perform any needed input
     * and update the execution string.
     * @param vce the command to preprocess
     * @return the preprocessing status, one of <code>CommandExecutorSupport.PROPEROCESS_*</code> constants
     *
    public int preprocessCommand(VcsCommandExecutor vce, Hashtable vars,
                                 VcsFileSystem fileSystem) {
        return preprocessCommand(vce, vars, fileSystem, null);
    }
     */
    
    /**
     * Perform preprocessing of a new command. It will perform any needed input
     * and update the execution string.
     * @param vce the command to preprocess
     * @param vars the variables for this command
     * @param fileSystem the file system this command is associated with. Can be <code>null</code>
     *        if the command is not associated with any file system
     * @param askForEachFile whether the user should be asked for options for each file
     *        separately. If false, then the user is asked only once when the command acts
     *        on multiple files. Only the first item in the array is used, new value
     *        will be returned.
     * @return the preprocessing status, one of <code>CommandExecutorSupport.PREPROCESS_*</code> constants
     *
    public int preprocessCommand(VcsCommandExecutor vce, Hashtable vars,
                                 VcsFileSystem fileSystem, boolean[] askForEachFile) {
        //setCommandID(vce);
        CommandTaskInfo cw = new CommandTaskInfo(vce, fileSystem);
        taskInfos.put(vce, cw);
        synchronized (commandsToRun) {
            commandsToRun.add(cw);
        }
        VcsCommand cmd = vce.getCommand();
        String name = cmd.getDisplayName();
        if (name == null || name.length() == 0) name = cmd.getName();
        String exec = vce.getExec();
        RuntimeCommand rCom = null;
        if (fileSystem != null) {
            fileSystem.debug(g("MSG_Command_preprocessing", name, exec));
            if (fileSystem.isCreateRuntimeCommands()) {
                //rCom = new VcsRuntimeCommand(vce, this);
                cw.setRuntimeCommand(rCom);
                rCom.setState(RuntimeCommand.STATE_WAITING);
                RuntimeSupport.getInstance().updateCommand(fileSystem.getSystemName(), rCom);
            }
        }
        int preprocessStatus = CommandExecutorSupport.preprocessCommand(fileSystem, vce, vars, askForEachFile);
        if (PREPROCESS_CANCELLED == preprocessStatus) {
            synchronized (this) {
                commandsToRun.remove(cw);
                taskInfos.remove(vce);
                //commandsFinished.add(vce);
                notifyAll();
            }
            if (fileSystem != null) {
                fileSystem.debug(g("MSG_Command_canceled", name));
                if (fileSystem.isCreateRuntimeCommands()) {
                    //RuntimeSupport.addCancelled(runtimeNode, vce, this);
                    RuntimeSupport.getInstance().removeDone(fileSystem.getSystemName(), rCom);
                }
            }
        }
        return preprocessStatus;
    }
     */
    
    private void commandStarted(final CommandTaskInfo cw) {
        //final VcsCommandExecutor vce = cw.getExecutor();
        //setCommandID(vce);
        //final VcsFileSystem fileSystem = cw.getFileSystem();
        //if (fileSystem == null) return ;
        final CommandTask cmdTask = cw.getTask();
        //waitToRun(cmd, vce.getFiles());
        final String name = cmdTask.getDisplayName();
        //if (name == null || name.length() == 0) name = cmd.getName();
        //final String finalName = name;
        if (name != null) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    String cmdName;
                    int i = name.indexOf('&');
                    if (i >= 0) cmdName = name.substring(0, i) + name.substring(i + 1);
                    else cmdName = name;
                    StatusDisplayer.getDefault().setStatusText(g("MSG_Command_name_running", cmdName));
                    /*
                    if (fileSystem != null) {
                        fileSystem.debug(g("MSG_Command_started", name, vce.getExec()));
                    }
                     */
                }
            });
        }
        //System.out.println("command "+cmdTask.getName()+" STARTED.");
        //Command cmd = cmdTask.getCommand();
        Object provider = null;
        if (cmdTask instanceof ProvidedCommand) {
            provider = ((ProvidedCommand) cmdTask).getProvider();
        }
        List commandListeners;
        synchronized (commandListenersByProviders) {
            commandListeners = new ArrayList(commandListenersForAllProviders);
            List providerListeners = (List) commandListenersByProviders.get(provider);
            if (providerListeners != null) commandListeners.addAll(providerListeners);
        }
        for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
            ((CommandProcessListener) it.next()).commandStarting(cw);
        }
        /*
        CommandOutputCollector collector = null;//new CommandOutputCollector(vce, this);
        outputContainers.put(vce, collector);
        VcsCommandVisualizer visualizer = vce.getVisualizer();
        if (visualizer != null) {
            if (!visualizer.openAfterCommandFinish()) visualizer.open();
        }
        if (VcsCommandIO.getBooleanProperty(vce.getCommand(), VcsCommand.PROPERTY_DISPLAY_PLAIN_OUTPUT)) {
            openCommandOutput(vce);
            //CommandOutputVisualizer outputVisualizer = new CommandOutputVisualizer(vce);
            //outputVisualizer.open();
            //outputVisualizers.put(vce, outputVisualizer);
        }
        //System.out.println("command "+vce.getCommand()+" STARTED, LISTENERS DONE.");
        if (fileSystem != null) {
            FileSystemCache cache = CacheHandler.getInstance().getCache(fileSystem.getCacheIdStr());
            if (cache != null && cache instanceof FileReaderListener) {
                vce.addFileReaderListener((FileReaderListener) cache);
            }
        }
         */
        cw.setStartTime(System.currentTimeMillis());
    }
    
    private void commandDone(CommandTaskInfo cw) {
        cw.setFinishTime(System.currentTimeMillis());
        CommandTask cmdTask = cw.getTask();
        //VcsCommandExecutor vce = cw.getExecutor();
        //System.out.println("commandDone("+cw.getExecutor().getCommand().getName()+")");
        //VcsFileSystem fileSystem = cw.getFileSystem();
        //VcsCommand cmd = vce.getCommand();
        String name = cmdTask.getDisplayName();
        //if (name == null || name.length() == 0) name = cmd.getName();
        synchronized (this) {
            if (isListCommandTask(cmdTask)) numRunningListCommands--;
            tasksRunning.remove(cw);
            //commandsFinished.add(cw);
            tasksExceptionallyRunning.remove(cw);
            taskInfos.remove(cmdTask);
            notifyAll();
        }
        //System.out.println("  commandsFinished.size() = "+commandsFinished.size());
        /*
        if (fileSystem != null && fileSystem.isCreateRuntimeCommands()) {
            RuntimeCommand rCom = cw.getRuntimeCommand();
            if (rCom == null) {
                //rCom = new VcsRuntimeCommand(vce, this);
                cw.setRuntimeCommand(rCom);
            }
            rCom.setState(RuntimeCommand.STATE_DONE);
            RuntimeSupport rSupport = RuntimeSupport.getInstance();
            rSupport.updateCommand(fileSystem.getSystemName(), rCom);
        } else {
            synchronized (this) {
                commandsFinished.remove(cw);
                taskInfos.remove(vce);
                outputContainers.remove(vce);
            }
            
        }
        if (!isCollectOutput()) {
            outputContainers.remove(vce);
        }
         */
        //Command cmd = cmdTask.getCommand();
        Object provider = null;
        if (cmdTask instanceof ProvidedCommand) {
            provider = ((ProvidedCommand) cmdTask).getProvider();
        }
        List commandListeners;
        synchronized (commandListenersByProviders) {
            commandListeners = new ArrayList(commandListenersForAllProviders);
            List providerListeners = (List) commandListenersByProviders.get(provider);
            if (providerListeners != null) commandListeners.addAll(providerListeners);
        }
        for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
            ((CommandProcessListener) it.next()).commandDone(cw);
        }
        /*
        if (fileSystem != null) {
            CommandExecutorSupport.postprocessCommand(fileSystem, vce);
        }
         */
        //System.out.println("command "+vce.getCommand()+" DONE, LISTENERS DONE.");
        //System.out.println("command "+cmdTask.getName()+" DONE, LISTENERS DONE.");
        if (name != null) {
            int exit = cmdTask.getExitStatus();
            if (cw.isInterrupted()) exit = CommandTask.STATUS_INTERRUPTED;
            //String name = vce.getCommand().getDisplayName();
            int i = name.indexOf('&');
            if (i >= 0) name = name.substring(0, i) + name.substring(i + 1);
            String message = "";
            switch (exit) {
                case CommandTask.STATUS_SUCCEEDED:
                    message = g("MSG_Command_name_finished", name);
                    break;
                case CommandTask.STATUS_FAILED:
                    if (cmdTask instanceof VcsDescribedTask) {
                        VcsCommand vcsCMD = ((VcsDescribedTask) cmdTask).getVcsCommand();
                        if (VcsCommandIO.getBooleanPropertyAssumeDefault(vcsCMD,
                                                                         VcsCommand.PROPERTY_IGNORE_FAIL)) {
                            message = g("MSG_Command_name_finished", name);
                        } else {
                            message = g("MSG_Command_name_failed", name);
                        }
                    } else {
                        message = g("MSG_Command_name_failed", name);
                    }
                    break;
                case CommandTask.STATUS_INTERRUPTED:
                    message = g("MSG_Command_name_interrupted", name);
                    break;
            }
            final String finalMessage = message;
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    StatusDisplayer.getDefault().setStatusText(finalMessage);
                }
            });
        }
        /*
        String notification = null;
        if (exit != VcsCommandExecutor.SUCCEEDED && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_IGNORE_FAIL)) {
            if (fileSystem != null) fileSystem.debugErr(message);
            printErrorOutput(vce, fileSystem);
            if (fileSystem == null || fileSystem.isCommandNotification()) {
                notification = (String) cmd.getProperty(VcsCommand.PROPERTY_NOTIFICATION_FAIL_MSG);
            }
        } else {
            if (fileSystem != null) fileSystem.debug(message);
            if (fileSystem == null || fileSystem.isCommandNotification()) {
                notification = (String) cmd.getProperty(VcsCommand.PROPERTY_NOTIFICATION_SUCCESS_MSG);
            }
        }
        if (notification != null) {
            CommandExecutorSupport.commandNotification(vce, notification, fileSystem);
        }
        VcsCommandVisualizer visualizer = vce.getVisualizer();
        if (visualizer != null) {
            visualizer.setExitStatus(exit);
            if (visualizer.openAfterCommandFinish()) visualizer.open();
        }
        VcsCommandVisualizer outputVisualizer = (VcsCommandVisualizer) outputVisualizers.get(vce);
        if (outputVisualizer != null) {
            outputVisualizer.setExitStatus(exit);
        }
         */
    }

    /*
    public void removeFinishedCommand(VcsCommandExecutor removedExecutor) {
        CommandTaskInfo removedWrapper = (CommandTaskInfo) taskInfos.get(removedExecutor);
        if (removedWrapper == null) return ;
        synchronized (this) {
            commandsFinished.remove(removedWrapper);
            //System.out.println("commandDone("+removedWrapper.getExecutor().getCommand().getName()+"): removing command "+removedExecutor.getCommand().getName());
            taskInfos.remove(removedExecutor);
            outputContainers.remove(removedExecutor);
        }
        RuntimeCommand runCom = removedWrapper.getRuntimeCommand();
        removedWrapper.setRuntimeCommand(null);
        //VcsFileSystem fs = removedWrapper.getFileSystem();
        //if (runCom != null) rSupport.removeDone(fileSystem.getSystemName(), runCom);
    }
     */
    
    /*
    private void printErrorOutput(VcsCommandExecutor vce, final VcsFileSystem fileSystem) {
        //final VcsFileSystem fileSystem = getVcsFileSystem();
        if (fileSystem == null) return ;
        fileSystem.debugErr(g("MSG_Check_whole_output"));
        CommandOutputCollector collector = (CommandOutputCollector) outputContainers.get(vce);
        //boolean isErrorOutput = false;
        if (collector != null) {
            collector.addErrorOutputListener(new CommandOutputListener() {
                public void outputLine(String line) {
                    //isErrorOutput = true;
                    fileSystem.debugErr(line);
                }
            });
        }
        /*
        if (!isErrorOutput) {
            fileSystem.debugErr(g("MSG_No_error_output"));
        }
         *//*
    }
     */
    
    /**
     * Start the executor. The method starts the executor in a separate thread.
     * @param vce the executor
     *
    public synchronized void startExecutor(final VcsCommandExecutor vce) {
        startExecutor(vce, null);
    }
    
    /**
     * Start the executor. The method starts the executor in a separate thread.
     * @param vce the executor
     * @param fileSystem the file system associated with the command. Can be <code>null</code>.
     *
    public synchronized void startExecutor(final VcsCommandExecutor vce,
                                           final VcsFileSystem fileSystem) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(vce);
        if (cw == null) {
            cw = new CommandTaskInfo(vce, fileSystem);
            taskInfos.put(vce, cw);
        }
        cw.setSubmittingThread(Thread.currentThread());
        //setCommandID(vce);
        commandsToRun.remove(cw);
        taskWaitQueue.add(cw);
        notifyAll(); // executorStarterLoop will start the command
        if (!execStarterLoopStarted) {
            runExecutorStarterLoop();
        }
    }
     */
    
    private synchronized void executorStarter(final CommandTaskInfo cw) {
        tasksRunning.add(cw);
        threadsPool.post(new Runnable() {
            public void run() {
                VcsCommandExecutor vce;
                synchronized (CommandProcessor.this) {
                    //vce = cw.getExecutor();
                    cw.setRunningThread(Thread.currentThread());
                    if (isListCommandTask(cw.getTask())) numRunningListCommands++;
                }
                commandStarted(cw);
                Error err = null;
                try {
                    cw.run();
                } catch (RuntimeException rexc) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, rexc);
                } catch (ThreadDeath tderr) {
                    err = tderr;
                } catch (Throwable t) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, t);
                }
                commandDone(cw);
                if (err != null) throw err;
            }
        });
    }
    
    private synchronized void executorStarterLoop() {
        do {
            CommandTaskInfo cw;
            do {
                cw = null;
                for (Iterator it = taskWaitQueue.iterator(); it.hasNext(); ) {
                    CommandTaskInfo cwTest = (CommandTaskInfo) it.next();
                    if (canRun(cwTest)) {
                        cw = cwTest;
                        break;
                    }
                }
                if (cw != null) {
                    taskWaitQueue.remove(cw);
                    executorStarter(cw);
                }
            } while (cw != null);
            Command cmd = null;
            for (Iterator it = commandsToPreprocess.iterator(); it.hasNext(); ) {
                Command cmdTest = (Command) it.next();
                if (canPreprocess(cmdTest)) {
                    cmd = cmdTest;
                    break;
                }
            }
            if (cmd != null) {
                commandsToPreprocess.remove(cmd);
                doPreprocess(cmd);
            }
            try {
                wait();
            } catch (InterruptedException intrexc) {
                // silently ignored
            }
        } while(execStarterLoopRunning);
    }
    
    private boolean canPreprocess(Command cmd) {
        return true;
    }
    
    private void runExecutorStarterLoop() {
        Thread starterLoopThread = new Thread(new Runnable() {
            public void run() {
                executorStarterLoop();
            }
        }, "VCS Command Tasks Starter Loop");
        starterLoopThread.setDaemon(true);
        starterLoopThread.start();
        execStarterLoopStarted = true;
    }

    /**
     * Open the default visualizer of the command.
     * @return true if the output was successfully opened, false otherwise
     * (i.e. output is not available)
     *
    public boolean openCommandOutput(final VcsCommandExecutor vce) {
        CommandOutputVisualizer visualizer = (CommandOutputVisualizer) outputVisualizers.get(vce);
        if (visualizer != null) {
            visualizer.requestFocus();
            return true;
        }
        CommandOutputCollector outputCollector = (CommandOutputCollector) outputContainers.get(vce);
        if (outputCollector == null) return false;
        final CommandOutputVisualizer outputVisualizer = new CommandOutputVisualizer(vce);
        outputVisualizer.addCloseListener(new TopComponentCloseListener() {
            public void closing() {
                outputVisualizers.remove(vce);
            }
        });
        //if (isRunning(vce)) outputVisualizer.setCommandsPool(this);
        outputVisualizer.open();
        outputVisualizers.put(vce, outputVisualizer);
        outputCollector.addOutputListener(new CommandOutputListener() {
            public void outputLine(String line) {
                outputVisualizer.stdOutputLine(line);
            }
        });
        outputCollector.addErrorOutputListener(new CommandOutputListener() {
            public void outputLine(String line) {
                outputVisualizer.errOutputLine(line);
            }
        });
        outputCollector.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] data) {
                outputVisualizer.stdOutputData(data);
            }
        });
        outputCollector.addDataErrorOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] data) {
                outputVisualizer.errOutputData(data);
            }
        });
        if (!isRunning(vce) && !isWaiting(vce)) outputVisualizer.setExitStatus(vce.getExitStatus());
        return true;
    }
     */
        
    /**
     * Whether some command is still running.
     * @return true when at least one command is running, false otherwise.
     */
    public synchronized boolean isSomeRunning() {
        return (tasksRunning.size() > 0);
    }
    
    /**
     * Tells whether a task is waiting. It can either wait till preprocessing
     * finishes or till other commands which can not run in parallel with it finish.
     * @param task The task
     */
    public synchronized boolean isWaiting(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return false;
        return /*commandsToRun.contains(cw) ||*/ taskWaitQueue.contains(cw);
    }
    
    /**
     * Tells whether the executor is still running.
     * @param task The task
     */
    public synchronized boolean isRunning(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        return (cw != null && tasksRunning.contains(cw));
    }
    
    /**
     * Get display names of running commands.
     */
    public synchronized String[] getRunningCommandsLabels() {
        LinkedList names = new LinkedList();
        for(Iterator it = tasksRunning.iterator(); it.hasNext(); ) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            String label = cw.getTask().getDisplayName();
            if (label == null) label = cw.getTask().getName();
            names.add(label);
        }
        return (String[]) names.toArray(new String[0]);
    }
    
    /** @return true if there are two files contained in the same package folder, false otherwise.
     */
    private static boolean areFilesInSamePackage(Collection files1, Collection files2) {
        for(Iterator it1 = files1.iterator(); it1.hasNext(); ) {
            String file1 = (String) it1.next();
            String dir1 = VcsUtilities.getDirNamePart(file1);
            for(Iterator it2 = files2.iterator(); it2.hasNext(); ) {
                String file2 = (String) it2.next();
                String dir2 = VcsUtilities.getDirNamePart(file2);
                if (dir1.equals(dir2)) return true;
            }
        }
        return false;
    }
    
    /** @return true if a file or folder from <code>files1</code> is contained in a folder
     * from <code>files2</code>
     */
    private static boolean isParentFolder(Collection files1, Collection files2) {
        for(Iterator it1 = files1.iterator(); it1.hasNext(); ) {
            String file1 = (String) it1.next();
            for(Iterator it2 = files2.iterator(); it2.hasNext(); ) {
                String file2 = (String) it2.next();
                if (file1.startsWith(file2)) return true;
            }
        }
        return false;
    }
    
    /**
     * @param concurrencyWith the pairs of command names and concurrent execution integer value
     *   <p> i.e.: "ADD 4", "STATUS 1"
     * @return the map of command names and associated integer values
     */
    private HashMap createConcurrencyMap(String concurrencyWith) {
        HashMap map = new HashMap();
        if (concurrencyWith != null) {
            String[] items = VcsUtilities.getQuotedStrings(concurrencyWith);
            for (int i = 0; i < items.length; i++) {
                int index = items[i].lastIndexOf(' ');
                if (index < 0) continue;
                String cmdName = items[i].substring(0, index);
                String concStr = items[i].substring(index + 1);
                int conc;
                try {
                    conc = Integer.parseInt(concStr);
                } catch (NumberFormatException nfexc) {
                    continue;
                }
                map.put(cmdName, new Integer(conc));
            }
        }
        return map;
    }
    
    private boolean isListCommandTask(CommandTask task) {
        return task.getName().startsWith("LIST");
    }
    
    private Collection getRunningThreadsFromCommands(Collection commandInfos) {
        HashSet threads = new HashSet();
        for (Iterator it = commandInfos.iterator(); it.hasNext(); ) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            Thread t = cw.getRunningThread();
            if (t != null) threads.add(t);
        }
        return threads;
    }
    
    /**
     * Returns true iff all exceptionally running commands are
     * predecessors of the given command.
     */
    private boolean areExcRunningPredecessorsOf(CommandTaskInfo cw) {
        HashSet exceptionallyRunning = new HashSet(tasksExceptionallyRunning);
        boolean is;
        do {
            is = false;
            Thread t = cw.getSubmittingThread();
            for (Iterator it = exceptionallyRunning.iterator(); it.hasNext(); ) {
                CommandTaskInfo testCw = (CommandTaskInfo) it.next();
                if (t.equals(testCw.getRunningThread())) {
                    cw = testCw;
                    exceptionallyRunning.remove(testCw);
                    is = true;
                    break;
                }
            }
        } while (is);
        return exceptionallyRunning.size() == 0;
    }
    
    /**
     * Say whether the command executor can be run now or not. It should be called
     * with a monitor lock on this object.
     * Check its concurrent property and other running or waiting commands.
     * @return true if the command can be run in the current monitor lock, false otherwise.
     */
    private synchronized boolean canRun(CommandTaskInfo cw) {
        // at first check for the maximum number of running commands
        CommandTask task = cw.getTask();
        //System.out.println("canRun("+cmd.getName()+")");
        if (tasksRunning.size() >= MAX_NUM_RUNNING_COMMANDS ||
            isListCommandTask(task) && numRunningListCommands >= MAX_NUM_RUNNING_LISTS) {
            
            //System.out.println("canRun("+vce.getCommand().getName()+") - limit reached.");
            Thread submitter = cw.getSubmittingThread();
            //System.out.println("  submitter = "+submitter);
            //System.out.println("  runningThreads = "+getRunningThreadsFromCommands(tasksRunning));
            if (getRunningThreadsFromCommands(tasksRunning).contains(submitter)) {
                //System.out.println("  tasksExceptionallyRunning = "+tasksExceptionallyRunning);
                if ((tasksExceptionallyRunning.size() == 0 || areExcRunningPredecessorsOf(cw))
                    && cw.canRun()) {
                    
                    tasksExceptionallyRunning.add(cw);
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
        boolean canRun = cw.canRun();
        //System.out.println("haveToWait = "+haveToWait);
        return canRun;
    }
    
    private synchronized void addExecutorsOfCommand(ArrayList executors, Command cmd) {
        String name = cmd.getName();
        /*
        Iterator it = commandsToRun.iterator();
        while (it.hasNext()) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            VcsCommandExecutor executor = cw.getExecutor();
            if (name.equals(executor.getCommand().getName())) {
                executors.add(executor);
            }
        }
         */
        Iterator it = taskWaitQueue.iterator();
        while (it.hasNext()) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            CommandTask task = cw.getTask();
            if (name.equals(task.getName())) {
                executors.add(task);
            }
        }
        //addExecutorsOfCommandFromIterator(executors, cmd, commandsToRun.iterator());
        for (Iterator runIt = tasksRunning.iterator(); runIt.hasNext(); ) {
            CommandTaskInfo cw = (CommandTaskInfo) runIt.next();
            CommandTask task = cw.getTask();
            if (name.equals(task.getName())) {
                executors.add(task);
            }
        }
        //addExecutorsOfCommandFromIterator(executors, cmd, commands.iterator());
    }
    
    /**
     * Wait to finish the execution of command on a set of files.
     * This methods blocks the current thread untill no task of the command is running on
     * any of provided files.
     * @param cmd the command we wait for to finish
     * @param files the set of files of type FileObject
     */
    public void waitToFinish(Command cmd, Set files) throws InterruptedException {
        boolean haveToWait = false;
        do {
            ArrayList executors = new ArrayList();
            addExecutorsOfCommand(executors, cmd);
            for (Iterator itExecutors = executors.iterator(); itExecutors.hasNext(); ) {
                CommandTask task = (CommandTask) itExecutors.next();
                if (files == null) {
                    haveToWait = true;
                } else {
                    FileObject[] execFiles = task.getFiles();
                    for (int i = 0; i < execFiles.length; i++) {
                        //String file = (String) itFiles.next();
                        if (files.contains(execFiles[i])) {
                            haveToWait = true;
                            break;
                        }
                    }
                }
            }
            if (haveToWait) {
                synchronized (this) {
                    wait();
                }
            }
        } while (haveToWait);
    }
    
    /**
     * Wait to finish the task.
     * This methods blocks the current thread untill the task finishes.
     * @param vce the executor
     */
    public void waitToFinish(CommandTask task) throws InterruptedException {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return ;
        //Thread t;
        synchronized (this) {
            while (taskWaitQueue.contains(cw) ||
                   tasksRunning.contains(cw)) {
                //try {
                wait();
                //} catch (InterruptedException iexc) {}
            }
            //t = cw.getRunningThread();
        }
        /*
        if (t != null) {
            if (t.isAlive()) {
                //try {
                t.join();
                //} catch (InterruptedException exc) {
                    // Ignore
                //}
            }
        }
         */
    }
    
    /**
     * Kill all running executors. It tries to interrupt them, it is up to
     * executor implementations if they will terminate or not.
     */
    public synchronized void killAll() {
        for(Iterator it = tasksRunning.iterator(); it.hasNext(); ) {
            CommandTaskInfo cw = (CommandTaskInfo) it.next();
            Thread t = cw.getRunningThread();
            if (t != null) t.interrupt();
            //if (ec.isAlive()) ec.interrupt();
            //commandsFinished.add(ec);
            //commands.remove(i);
            //i--;
        }
    }
    
    /**
     * Kill the executor if it is running. It tries to interrupt it, it is up to
     * executor implementation if it will terminate or not.
     */
    public synchronized void kill(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw != null) {
            Thread t = cw.getRunningThread();
            if (t != null) t.interrupt();
            else {
                taskWaitQueue.remove(cw);
                cw.setInterrupted(true);
                commandDone(cw);
            }
        }
    }
    
    /**
     * Add a command listener.
     */
    public void addCommandProcessListener(CommandProcessListener listener) {
        Object provider = listener.getProvider();
        synchronized (commandListenersByProviders) {
            if (provider == null) {
                commandListenersForAllProviders.add(listener);
            } else {
                List commandListeners = (List) commandListenersByProviders.get(provider);
                if (commandListeners == null) {
                    commandListeners = new ArrayList();
                    commandListenersByProviders.put(provider, commandListeners);
                }
                commandListeners.add(listener);
            }
        }
    }
    
    /**
     * Lazily remove a command listener.
     */
    public void removeCommandProcessListener(final CommandProcessListener listener) {
        org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                Object provider = listener.getProvider();
                synchronized (commandListenersByProviders) {
                    if (provider == null) {
                        commandListenersForAllProviders.remove(listener);
                    } else {
                        List commandListeners = (List) commandListenersByProviders.get(provider);
                        if (commandListeners != null) {
                            commandListeners.remove(listener);
                            if (commandListeners.size() == 0) {
                                commandListenersByProviders.remove(provider);
                            }
                        }
                    }
                }
            }
        });
    }
    
    /** The start time of the command or zero, when the command was not started yet
     * or can not be found.
     */
    public long getStartTime(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return 0;
        return cw.getStartTime();
    }
    
    /** The finish time of the command or zero, when the command did not finish yet
     * or can not be found.
     */
    public long getFinishTime(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return 0;
        return cw.getFinishTime();
    }
    
    /** The execution time of the command or zero, when the command did not finish yet
     * or can not be found.
     */
    public long getExecutionTime(CommandTask task) {
        CommandTaskInfo cw = (CommandTaskInfo) taskInfos.get(task);
        if (cw == null) return 0;
        return cw.getExecutionTime();
    }
    
    /**
     * Get the localized string representation of the command exit status.
     * @param exit the exit status, that will be converted to the string.
     */
    public static String getExitStatusString(int exit) {
        String status;
        if (VcsCommandExecutor.SUCCEEDED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandProcessor.class).getString("CommandExitStatus.finished");
        } else if (VcsCommandExecutor.FAILED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandProcessor.class).getString("CommandExitStatus.failed");
        } else if (VcsCommandExecutor.INTERRUPTED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandProcessor.class).getString("CommandExitStatus.interrupted");
        } else {
            status = org.openide.util.NbBundle.getBundle(CommandProcessor.class).getString("CommandExitStatus.unknown");
        }
        return status;
    }

    
    private static String g(String s) {
        return org.openide.util.NbBundle.getMessage(CommandProcessor.class, s);
    }
    
    private static String  g(String s, Object obj) {
        return org.openide.util.NbBundle.getMessage(CommandProcessor.class, s, obj);
    }

    //private static String  g(String s, Object obj, Object obj2) {
    //    return org.openide.util.NbBundle.getMessage(CommandProcessor.class, s, obj, obj2);
    //}

}

