/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.netbeans.modules.vcscore.commands.CommandsTree;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommands;
import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;

/**
 * The representation of a VCS profile.
 *
 * @author  Martin Entlicher
 */
public abstract class Profile extends Object {
    
    public static final String PROP_CONDITIONS = "conditions"; // NOI18N
    public static final String PROP_COMMANDS = "commands"; // NOI18N
    public static final String PROP_GLOBAL_COMMANDS = "globalCommands"; // NOI18N
    public static final String PROP_VARIABLES = "variables"; // NOI18N

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Get the name of the profile.
     */
    public abstract String getName();

    /**
     * Get the display name of the profile.
     */
    public abstract String getDisplayName();
    
    /**
     * Get the type of the VCS support.
     */
    public abstract String getType();
    
    /**
     * Pre-load some properties from the persistent storage.
     * This method can be used for performance reasons, because the getters
     * for the preloaded properties should be fast.
     */
    public void preLoadContent(boolean conditions, boolean variables,
                               boolean commands, boolean globalCommands) {
        // The default implementation does nothing.
    }
    
    /**
     * Get the list of resource bundle files that are used to retrieve
     * the localized messages.
     */
    public abstract String[] getResourceBundles();

    public abstract Set getCompatibleOSs();

    public abstract Set getUncompatibleOSs();
    
    /**
     * Get the array of conditions.
     */
    public abstract Condition[] getConditions();

    /**
     * Set the array of conditions.
     */
    public abstract boolean setConditions(Condition[] conditions);

    /**
     * Get the conditioned variables. It provides set of variables dependent
     * on conditional variables.
     */
    public abstract ConditionedVariables getVariables();

    /**
     * Set the collection of VcsConfigVariable objects.
     */
    public abstract boolean setVariables(ConditionedVariables variables);

    /**
     * Get the tree structure of popup commands.
     */
    public abstract ConditionedCommands getCommands();

    /**
     * Set the tree structure of popup commands.
     */
    public abstract boolean setCommands(ConditionedCommands commands);

    /**
     * Get the tree structure of global commands.
     */
    public abstract ConditionedCommands getGlobalCommands();

    /**
     * Set the tree structure of global commands.
     */
    public abstract boolean setGlobalCommands(ConditionedCommands commands);
    
    /**
     * When the returned resource is localized into the current locale,
     * this profile should split into two (the original is kept for the default
     * English locale, a copy is made for the current locale). <p>
     * This is necessary for profiles, which integrate version control systems,
     * that can be localized into the current locale. English version is there
     * for non-localized VCS, localized copy is there for localized VCS.
     * @return The resource which is checked for the localized version or
     *         <code>null</code> when no copy is made.
     */
    public String getSplitWhenLocalized() {
        return null;
    }
    
    /**
     * Find out whether this is a localized copy of a profile and should be
     * treated differently (no editing, saving, etc.)
     */
    public boolean isLocalizedCopy() {
        return false;
    }

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    abstract void unimplementableFromOutside();

}
