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

package org.netbeans.modules.tasklist.suggestions;

import java.awt.Image;
import org.netbeans.modules.tasklist.core.Task;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.Line;



// XXX todo: fire property change whenever anything changes in the node...

/** Class which represents a task in the
 * tasklist.
 * @author Tor Norbye */
final public class SuggestionImpl extends Task {

    //private String action;
    private String filename;
    private String basename;
    private int linenumber;
    private String category = null;
    private SuggestionType stype = null;

    /** Field (package private) used by the SourceScanner as a tag
        to improve search speeds. Don't muck with it. */
    transient boolean scantag;
    
    SuggestionImpl() {
    }

    public SuggestionImpl(String desc, String filename, int linenumber) {
        super(desc, null);
        this.filename = filename;
        this.linenumber = linenumber;
        basename = null;
    }

    /** Return true iff the task has an associated file position */
    public boolean hasAssociatedFilePos() {
	return ((linenumber > 0) && (filename != null) && 
                (filename.length() > 0));
    }
    
    /** Return the name of the file associated with this
     * task, or the empty string if none.
     * @return basename, or empty string */    
    public java.lang.String getFileBaseName() {
        if (basename == null) {

            Line l = getLine();
            if (l == null) {
                basename = "";
            } else {
                DataObject dao = l.getDataObject();
                basename = dao.getPrimaryFile().getNameExt();
            }
        }
        return basename;
    }
    
    /** Return line number associated with the task.
     * @return Line number, or "0" if no particular line is
     * associated. Will always be 0 if there is no
     * associated file.
     */    
    public int getLineNumber() {
        Line l = getLine();
        if (l == null) {
            return 0;
        } else {
            return l.getLineNumber()+1;
        }
    }
    
    /** Generate a string summary of the task; only used
     * for debugging. DO NOT depend on this format for anything!
     * Use generate() instead.
     * @return summary string */    
    public String toString() {
        return "SuggestionImpl(" + super.toString() + ")[\"" + getSummary() + "\", " + filename + ":" + linenumber + ", " + stype + "]"; // NOI18N
    }

    /** Create a node for this item */
    protected Node[] createNode() {
        if (hasSubtasks()) {
            return new Node[] { new SuggestionNode(this, getSubtasks())};
        } else {
            return new Node[] { new SuggestionNode(this)};
        }
    }

    /** Create an identical copy of a task (a deep copy, e.g. the
        list of subtasks will be cloned as well */
    protected Object clone() throws CloneNotSupportedException {
        SuggestionImpl t = new SuggestionImpl();
        t.copyFrom(this);
        return t;
    }

    /** Copy all the fields in the given task into this object.
        Should only be called on an object of the EXACT same type.
        Thus, if you're implementing a subclass of Task, say
        UserTask, you can implement copy assuming that the passed
        in Task parameter is of type UserTask. When overriding,
        remember to call super.copyFrom.
        <p>
        Make a deep copy - except when that doesn't make sense.
        For example, you can share the same icon reference.
        And in particular, the tasklist reference should be the same.
        But the list of subitems should be unique. You get the idea.
    */
    protected void copyFrom(SuggestionImpl from) {
        super.copyFrom(from);

        filename = from.filename;
        linenumber = from.linenumber;
        basename = from.basename;
        category = from.category;

        // TODO XXX Copy fields from Suggestion as well!
    }

    /** Return the category. Derived from the SuggestionType. */
    public String getCategory() {
        if (category == null) {
            if (stype != null) {
                category = stype.getLocalizedName();
            } else {
                category = "";
            }
        }
        return category;
    }
    
    /** Return the NUMERIC value of the priority. Derived from getPriority(). */
    public int getPriorityNumber() {
        return getPriority().intValue();
    }

    /** "Re"defined here to allow access in this package, not just
     * the api package. Just calls super. */
    protected void setType(String type) {
        super.setType(type);
    }

    SuggestionType getSType() {
        return stype;
    }
    
    void setSType(SuggestionType stype) {
        this.stype = stype;
        setType(stype.getName());
    }

    public Image getIcon() {
        if (super.getIcon() != null) {
            return super.getIcon();
        } else if ((stype != null) && (stype.getIconImage() != null)) {
	    return stype.getIconImage();
	} else {
	    return null;
	}
    }    
}



