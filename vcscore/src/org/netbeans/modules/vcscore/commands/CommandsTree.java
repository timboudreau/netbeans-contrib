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

package org.netbeans.modules.vcscore.commands;

import java.util.ArrayList;
import java.util.List;

import org.netbeans.spi.vcs.commands.CommandSupport;

/**
 * This class represents a tree structure of commands. This structure is used
 * e.g. to construct the popup menu of commands.
 * Every instance of this class represents a single node in the tree structure.
 *
 * @author  Martin Entlicher
 */
public final class CommandsTree extends Object {
    
    /**
     * An empty commands tree element, that can be used for separators.
     */
    public static final CommandsTree EMPTY = new CommandsTree(null);
    
    private static final Object CHANGE_LOCK = new Object();
    
    private CommandSupport cmdSupport;
    private List children;
    
    /**
     * Creates a new instance of CommandsTree.
     * @param cmd The Command or <code>null</code> for a separator.
     */
    public CommandsTree(CommandSupport cmdSupport) {
        this.cmdSupport = cmdSupport;
        children = new ArrayList();
    }
    
    /**
     * Get the command of this item.
     * @return The command or <code>null</code> for a separator.
     */
    public final CommandSupport getCommandSupport() {
        return cmdSupport;
    }
    
    /**
     * Add a new child to this tree node.
     * @param child The new child to be added.
     */
    public final void add(CommandsTree child) {
        synchronized (CHANGE_LOCK) {
            children.add(child);
        }
    }
    
    /**
     * Tells whether this node of the tree has some children.
     * @return True if there are some children.
     */
    public final boolean hasChildren() {
        synchronized (CHANGE_LOCK) {
            return children.size() > 0;
        }
    }
    
    /**
     * Provides the array of children.
     * @return The array of children (empty array if there are no children).
     */
    public final CommandsTree[] children() {
        synchronized (CHANGE_LOCK) {
            return (CommandsTree[]) children.toArray(new CommandsTree[children.size()]);
        }
    }
    
    /**
     * Provider of CommandsTree.
     */
    public static interface Provider {
        
        /**
         * Get the commands.
         * @return The root of the commands tree.
         */
        public CommandsTree getCommands();
        
        /**
         * Set the commands.
         * @param commands The root of the commands tree.
         */
        public void setCommands(CommandsTree commands);
        
        /**
         * Get the expert mode of this commands provider. If it's true, all commands
         * have the expert mode turned on by default;
         */
        public boolean isExpertMode();
        
        /**
         * Set the expert mode of this commands provider. If it's true, all commands
         * have the expert mode turned on by default;
         */
        public void setExpertMode(boolean expertMode);
        
    }
}
