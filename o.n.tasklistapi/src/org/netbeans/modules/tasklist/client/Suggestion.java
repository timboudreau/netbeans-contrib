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

package org.netbeans.modules.tasklist.client;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.openide.text.Line;

/**
 * A suggestion is an item in the Suggestions Viev. It has an associated
 * description/summary, an associated icon, an associated action, etc.
 * <p>
 * Suggestions have an associated "type".  For example, there
 *       is a copyright suggestion which adds suggestions for how to
 *       fix copyrights in your files; whenever it sees you editing a file
 *       where the current year is not included, it will add a specific
 *       suggestion which when performed edits your code to update the
 *       copyright. These copyright suggestions all have the same
 *       suggestion type: copyright. 
 * 
 * <p>
 * This type is used for two purposes:
 * <ul>
 *  <li> Enabling/disabling a class of suggestions. If the user doesn't
 *       want to change the copyright years, the user can disable
 *       this type of suggestion and it will no longer appear in the
 *       suggestion list.
 *  <li> Filtering the list. For example, you can choose to see only the
 *       suggestions pertaining to code metrics.
 * </ul>
 * <p>
 *
 * If you are providing suggestions, you need to create a new Suggestion
 * Type for yourself. It's very easy. All you need to do is pick a unique
 * id, and a brief (4 words or less) description.
 * For example, the background compilation of java files may add suggestions
 * that list the error messages and file position; the description of this
 * suggestion type should be "Compiler Errors". This text will be shown
 * in the popup menu where users can filter the list by suggestion type
 * ("Show -> "All", "Compiler Errors", "Copyright Problems", "Audit Problems",
 *  "Performance Hotspots", etc.).
 * <p>
 * Then just register this suggestion type in your module's XML layer file,
 * by adding something like this (under the top level &lt;filesystem&gt; tag) :
 *

 * <pre>
 *     &lt;folder name="Suggestions"&gt; 
 *         &lt;folder name="Types"&gt; 
 *             &lt;file name="copyrightcheck.xml" url="copyrightcheck.xml"/&gt;
 *         &lt;/folder&gt;
 *     &lt;/folder&gt;
 * </pre>
 * and then the file copyrightcheck.xml in your module (in the same
 * directory as your layer file) contains something like this:
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;!DOCTYPE type PUBLIC "-//NetBeans//DTD suggestion type 1.0//EN" "http://www.netbeans.org/dtds/suggestion-type-1_0.dtd"&gt;
 * &lt;type
 *     name='CopyrightCheck'
 *     description_key='HINT_COPYRIGHT'
 *     long_description_key='HINT_COPYRIGHT'
 *     localizing_bundle='com.foo.bar.Bundle'
 *     icon='nbresloc:/com/foo/bar/copyrightCheck.gif'
 * /&gt;
 * 
 * </pre>
 *  <p>
 * Then in your Bundle file identified above, add an entry like:
 * <pre>
 *     HINT_COPYRIGHT=Copyright Problems
 *    LONGHINT_copyrighttype=Identify copyright notices in files where the copyright year does not include the current year.
 * </pre>
 * <p>
 * Finally, pass in "copyrights" as the id for this type of suggestion
 * to the Suggestion constructor as the type argument.
 * Note: type ids have to be unique, so make sure you pick some string
 * which is not going to conflict with any other modules' registered
 * suggestion types. The "copyrights" example could probably be made
 * more unique by adding a company prefix to it, e.g.
 * "com.foo.bar-copyrights". I recommend you do that. For NetBeans
 * modules (e.g. modules hosted on NetBeans' CVS server", simply use
 * "nb-{modulename}-whatever". For example, the tasklist module would
 * use a prefix of "nb-tasklist-".
 *
 * @author Tor Norbye
 */


abstract public class Suggestion {

    /** Agent that can mutate this suggestion */
    SuggestionAgent agent;

    private final PropertyChangeSupport supp = new PropertyChangeSupport(this);

    /** Id of bound summary property. */
    public static final String PROP_SUMMARY = "summary";

    /** Id of bound icon property. */
    public static final String PROP_ICON = "icon";

    /** Id of bound details property. */
    public static final String PROP_DETAILS = "details";

    /** Id of bound priority property. */
    public static final String PROP_PRIORITY = "priority";

    /** Id of bound priority property. */
    public static final String PROP_VALID = "valid";

    private boolean valid;



    /** Use {@link org.netbeans.modules.tasklist.client.SuggestionManager#createSuggestion} to create these.
     * <p>
     * NOTE: This constructor may not be called except by a
     * SuggestionManager subclass; SuggestionManager implementations
     * may refuse to add Suggestion instances not created by
     * themselves. 
     * @param type Type of the suggestion. See the class javadoc for a 
     *    description of what this means.
     * @param summary Summary to show for the suggestion
     * @param action Action to perform when the suggestion should be fixed. May 
     *    be null.
     */
    protected Suggestion(final String type, final String summary, final SuggestionPerformer action) {
        this.type = type;
        this.summary = summary;
        this.action = action;
        valid = true;
    }

    // Attributes:

    /** The icon to be used for this task - or null to use the default */
    private Image icon = null;
    /** A summary (one-line description) of the task */
    private String summary = null;

    /** A (possibly) multi-line summary of the task */
    private String details = null;

    // TODO - use special classes (e.g. Category, File, SuggestionType)
    // for the category, file and type fields?

    /** The category of this  task */
    //private String category = null;
    
    /** The priority of this suggestion, defaults to SuggestionPriority.MEDIUM */
    private SuggestionPriority priority = SuggestionPriority.MEDIUM;
    
    /** The type of task; for example source errors and import warnings are
     * different types of tasks. This should be a user-readable (localized)
     * string.
    */
    private String type = null;

    /** The line position associated with the task */
    private Line line = null;
    
    private SuggestionPerformer action = null;
    //private TimeToLive ttl = TimeToLive.SESSION;

    // Note - if you add additional fields, remember to keep
    // Task.copyFrom in sync.

    
    /**
     * Set the summary of the task. This is a one-line description
     * of the task. The summary should not be null.
     *
     * @param summary The summary of the task.
     *
     * @deprecated use SuggestionAgent#setSummary
     */
    protected void setSummary(final String summary) {
        if (summary == null) {
            throw new NullPointerException();
        }
        String old = getSummary();
        if (old.equals(summary)) return;
        this.summary = summary;
        firePropertyChange(PROP_SUMMARY, old, summary);
    }

    /**
     * Get the summary of the task.
     * <p>
     *
     * @return The summary of the task.
     */
    public String getSummary() {
        if (summary == null) {
            summary = "";
        }
        return summary;
    }


    /**
     * Set the details of the task. This could be multiple lines
     * of description of the task. Can be null. 
     *
     * @param details The details of the task
     *
     * @deprecated use SuggestionAgent#setDetails
     */
    protected void setDetails(final String details) {
        String old = getDetails();
        if (old.equals(details)) return;
        this.details = details;
        firePropertyChange(PROP_DETAILS, old, details);
    }

    /**
     * Get the details of the task. Will never be null (but may
     * be an empty string.)
     * <p>
     *
     * @return The details of the task
     */
    public String getDetails() {
        if (details == null) {
            details = "";
        }
        return details;
    }
    
    // No category for now; use the SuggestionType instead?
    //    /**
    //     * Set the category of the task. May be null.
    //     * <p>
    //     *
    //     * @param category The category of the task.
    //     */
    //    public void setCategory(String category) {
    //        this.category = category;
    //    }
    //
    //    /**
    //     * Get the category of the task. May be null if no category
    //     * has been specified.
    //     * <p>
    //     *
    //     * @return The category of the task.
    //     */
    //    public String getCategory() {
    //        return category;
    //    }
    //


    /**
     * Set the priority of the task. 
     * <p>
     *
     * @param priority The priority of the task.
     *
     * @deprecated use SuggestionAgent#setPriority
     */
    protected void setPriority(final SuggestionPriority priority) {
        SuggestionPriority old = getPriority();
        if (old == priority) return;
        this.priority = priority;
        firePropertyChange(PROP_PRIORITY, old, priority);
    }

    /**
     * Get the priority of the task.
     * <p>
     *
     * @return The priority of the task.
     */
    public SuggestionPriority getPriority() {
        return priority;
    }

    

    /**
     * Set the icon for the task. May be null; if so the default icon will
     * be shown.
     * <p>
     *
     * @param icon The icon to be shown with the task.
     *
     * @deprecated use SuggestionAgent#setIcon
     */
    protected void setIcon(final Image icon) {
        Image old = getIcon();
        if (old == icon) return;
        this.icon = icon;
        firePropertyChange(PROP_ICON, old, icon);
    }

    /**
     * Get the icon for the task. May be null if no icon
     * has been specified; if so the default will be used.
     * <p>
     *
     * @return The icon for the task.
     */
    public Image getIcon() {
        return icon;
    }


    /**
     * Set the line (file position) associated with the suggestion.
     * <p>
     *
     * @param line The line associated with the suggestion.
     *
     * @deprecated use SuggestionAgent#setLine, moreover it should be moved to contructor
     */
    protected void setLine(final Line line) {
        this.line = line;
    }

    /**
     * Get the line position for the suggestion.
     * <p>
     *
     * @return The line position for the suggestion.
     */
    public Line getLine() {
        return line;
    }

    
    /**
     * Set the action to be performed when the task is executed.
     * <p>
     *
     * @param action The action that the task represents.
     *
     * @deprecated use SuggestionAgent#setAction
     */
    protected final void setAction(final SuggestionPerformer action) {
        this.action = action;
    }

    /**
     * Get the action to be performed when this task is executed.
     * Will not be null.
     * <p>
     *
     * @return The action number in the task's file.
     */
    public SuggestionPerformer getAction() {
        return action;
    }

    /**
     * Set the type associated with this suggestion.
     * Should not be null.
     * <p>
     *
     * @param type The type name for this suggestion
     *
     * @deprecated should be constant since contruction time
     */
    protected void setType(final String type) {
        this.type = type;
    }

    /**
     * Get the type associated with this suggestion.
     * <p>
     *
     * @return The type name for this suggestion
     */
    public String getType() {
        return type;
    }

    /** 
     * Get data passed by the provider which created this
     * suggestion. Exact meaning is provider dependent.
     *
     * @return The provider's data, or null.
     * @since 1.4
     */
    public abstract Object getSeed();

    /**
     * Provider sets to invalid once it stop maintaining it.
     *
     * @return false if invalid
     * @since 1.11
     */
    public boolean isValid() {
        return valid;
    }

    void invalidate() {
        if (valid == false) return;
        valid = false;
        supp.firePropertyChange(PROP_VALID, true, false);
    }

    /**
     * Listen to changes in bean properties.
     * @param l listener to be notified of changes
     *
     * @since 1.11
     */
    public final void addPropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
        supp.addPropertyChangeListener(l);
    }

    /**
     * Stop listening to changes in bean properties.
     *
     * @param l listener who will no longer be notified of changes
     *
     * @since 1.11
     */
    public final void removePropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
    }


    /**
     * Fires a PropertyChangeEvent
     *
     * @param propertyName changed property
     * @param oldValue old value (may be null)
     * @param newValue new value (may be null)
     *
     * @since 1.11
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        supp.firePropertyChange(propertyName, oldValue, newValue);
    }


}
