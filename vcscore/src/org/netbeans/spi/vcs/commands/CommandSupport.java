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

package org.netbeans.spi.vcs.commands;

/*
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
 */
import java.beans.BeanInfo;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.EventListener;

import org.openide.filesystems.FileObject;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

/**
 *
 * @author  Martin Entlicher
 */
public abstract class CommandSupport extends Object implements Cloneable {
    
    private static final Class[] DEFAULT_IMPLEMENTED_CLASSES =
        new Class[] { Command.class, java.security.PrivilegedAction.class };
    private static final FileObject[] EMPTY_FILES = new FileObject[0];
        
    private Class commandClass;
    private Class[] commandClasses;
    private Command command;
    
    /**
     * Creates a new instance of CommandSupport.
     * @param commandClass The class of the command, that is implemented.
     */
    public CommandSupport(Class commandClass) {
        this.commandClass = commandClass;
    }
    
    /**
     * Creates a new instance of CommandSupport.
     * @param commandClass The class of the command, that is implemented.
     */
    public CommandSupport(Class[] commandClasses) {
        this.commandClasses = commandClasses;
    }
    
    /**
     * Get the name of the command.
     */
    public abstract String getName();
    
    /**
     * Get the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public abstract String getDisplayName();
    
    /*
     * Get the mnemonic displayed for that command. The mnemonic will be set
     * on the menu item of this command. This method returns <code>0</code>
     * by default (no mnemonic).
     * @return The mnemonic character or <code>0</code> when no mnemonic is needed.
     *
    public char getDisplayedMnemonic() {
        return '\0';
    }
     */
    
    /**
     * Find, whether this command can act on a set of files.
     * @param files The array of files to inspect
     * @return an array of files the command can act on or <code>null</code> when
     * it can not act on any file listed.
     */
    public abstract FileObject[] getApplicableFiles(FileObject[] files);
    
    /**
     * Whether the command supports an expert mode. The command should provide
     * a more complex customizer and/or output if in expert mode. If the
     * command does not differentiate expert mode, it should declare, that
     * it does not have an expert mode.
     * @return true If the command differentiate expert mode, false otherwise
     */
    public abstract boolean hasExpertMode();

    /**
     * Use this method to actually create the command. This method clones current
     * CommandSupport instance, and create the {@link org.netbeans.api.vcs.commands.Command}
     * associated with the clonned CommandSupport instance.
     * @return The fresh instance of {@link org.netbeans.api.vcs.commands.Command}.
     */
    public final Command createCommand () {
        try {
            CommandSupport clon = (CommandSupport) this.clone();
            CommandInvocationHandler ciHandler = new CommandInvocationHandler(clon);
            Command cmd = createCommand(ciHandler);
            clon.command = cmd;
            initializeCommand(cmd);
            return cmd;
        } catch (CloneNotSupportedException cnsex) {
            org.openide.ErrorManager.getDefault().notify(cnsex);
            return null;
        }
    }
    
    final Command createCommand(InvocationHandler handler) {
        Class[] implementedClasses;
        if (commandClasses != null) {
            implementedClasses = new Class[DEFAULT_IMPLEMENTED_CLASSES.length + commandClasses.length];
            System.arraycopy(DEFAULT_IMPLEMENTED_CLASSES, 0, implementedClasses, 0, DEFAULT_IMPLEMENTED_CLASSES.length);
            System.arraycopy(commandClasses, 0, implementedClasses, DEFAULT_IMPLEMENTED_CLASSES.length, commandClasses.length);
        } else {
            implementedClasses = new Class[DEFAULT_IMPLEMENTED_CLASSES.length + 1];
            System.arraycopy(DEFAULT_IMPLEMENTED_CLASSES, 0, implementedClasses, 0, DEFAULT_IMPLEMENTED_CLASSES.length);
            implementedClasses[DEFAULT_IMPLEMENTED_CLASSES.length] = commandClass;
        }
        //System.out.println("Creating command of name "+getName()+", impl. classes:");
        Command command =
            (Command) Proxy.newProxyInstance(getClass().getClassLoader(),
                                             implementedClasses, handler);
        //ciHandler.setCommand(command);
        return command;
    }
    
    /**
     * Initialize the command after it's created. This allows the implementator
     * to preset some initial values to the command before it's customized.
     * When sb. is just creates a command through VcsManager.createCommand(..)
     * they will get this customized command.
     * The default implementation just set an empty array of FileObjects to the command.
     * @param cmd The command to be customized.
     */
    protected void initializeCommand(Command cmd) {
        cmd.setFiles(EMPTY_FILES);
    }

    /**
     * Get the command associated with this command support.
     * @return The instance of {@link org.netbeans.api.vcs.commands.Command} or
     *         <code>null</code> if this support does not have any command associated.
     */
    protected final Command getCommand () {
        return command;
    }
    
    /**
     * Get the command associated with the given task. This is a clon of the
     * command returned in getCommand() method after the customization.
     * This clon will not be customized any more, thus it's safe to use
     * this command in execute() method to get the customized data.
     * @param task The task to get the command for.
     * @return The customized command associated with the given task.
     * @throws IllegalArgumentException if the task is of an unknown instance.
     */
    protected final Command getCommand(CommandTask task) throws IllegalArgumentException {
        if (!(task instanceof CommandTaskSupport)) throw new IllegalArgumentException("Task is not instanceof CommandTaskSupport");
        CommandTaskSupport taskSupport = (CommandTaskSupport) task;
        return taskSupport.getCommand();
    }
    
    /**
     * Tell, whether the task can be executed now. The task may wish to aviod parallel
     * execution with other tasks or other events.
     * @return <code>true</code> if the task is to be executed immediately. This is the
     *                           default implementation.
     *         <code>false</code> if the task should not be executed at this time.
     *                            In this case the method will be called later to check
     *                            whether the task can be executed already.
     */
    protected boolean canExecute(CommandTask task) {
        return true;
    }
    
    /**
     * Perform the actual execution of the command from the provided info.
     * This method might be called multiple times and even concurrently
     * for a single CommandSupport instance. It's recommended not to
     * modify any variables from this instance object in this method.
     */
    protected abstract int execute (CommandTask task);
    
    /**
     * Get the list of interfaces, that the CommandTask should implement.
     * The getTaskInvocationHandler() is supposed to return the
     * invocation handler of methods of these interfaces.
     * The default implementation returns just an empty array.
     * @return The array of interfaces.
     *
    protected Class[] getTaskInterfaces() {
        return new Class[0];
    }
    
    /**
     * Get the invocation handler of CommandTask. 
     *
    protected InvocationHandler getTaskInvocationHandler() {
        return null;
    }
     */
    
    /**
     * Create the CommandTask, that is supposed to execute the command.
     * This implementation returns an instance of {@link CommandTaskSupport}.
     * @return The instance of CommandTask.
     */
    final CommandTask createTheTask() {
        Command cmd = getCommand();
        if (cmd == null) return null;
        //CommandTask task = new CommandTaskSupport(this, cmd.getFiles());
        //Object data = retrieveCustomizedData(cmd);
        Command taskCommand = null;
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(cmd);
            if (handler instanceof CommandInvocationHandler) {
                CommandInvocationHandler cHandler = (CommandInvocationHandler) ((CommandInvocationHandler) handler).clone();
                taskCommand = createCommand(cHandler);
            }
        } catch (IllegalArgumentException iaex) {
            org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.getDefault().annotate(iaex, "It was not possible to clon the Command!"));
        }
        if (taskCommand == null) taskCommand = cmd; // It was not possible to clon the command :-((
        return createTask(taskCommand);
    }
    
    /**
     * Create the CommandTask, that is supposed to execute the command.
     * This implementation must return an instance of {@link CommandTaskSupport}.
     * @param taskCommand The copy of the customized command, that can be used
     *                    by the task to get the customized information.
     * @return The instance of CommandTaskSupport.
     */
    protected CommandTaskSupport createTask(Command taskCommand) {
        return new CommandTaskSupport(this, taskCommand);
    }
    
    /**
     * Store the information about the customized command to an object, which is
     * automatically associated with the CommandTask.
     * You need to implement this method only if you do not override createTask()
     * method.
     *
    protected abstract Object retrieveCustomizedData(Command cmd);
     */
    
    /**
     * Stop the command's execution. The default implementation kills
     * the command's thread by hard.
     */
    protected void stop(CommandTask task) {
        killHard(task);
    }
    
    final void killHard(CommandTask task) {
        if (task instanceof CommandTaskSupport) {
            ((CommandTaskSupport) task).killMeHard();
        } else {
            throw new IllegalArgumentException(task.toString());
        }
    }
    
    /**
     * Get the event listeners attached to the associated command, if any.
     * @param listenerType The type of listeners we're looking for.
     * @param command The command, to get the listeners from.
     * @return The array of listeners of given type or <code>null</code>.
     */
    protected final EventListener[] getListeners (Class listenerType, Command command) {
        if (command != null) {
            try {
                InvocationHandler handler = Proxy.getInvocationHandler(command);
                if (handler instanceof CommandInvocationHandler) {
                    return ((CommandInvocationHandler) handler).getListeners(listenerType);
                }
            } catch (IllegalArgumentException iaex) {}
        }
        return null;
    }
    
    /**
     * Subclasses need to implement the clone() method in order the command creation
     * work properly.
     * @return The clonned command support object.
     */
    protected abstract Object clone() throws CloneNotSupportedException;
    
}
