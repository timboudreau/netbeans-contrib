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
import org.openide.util.Utilities;
import org.netbeans.modules.tasklist.core.Task;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.text.DataEditorSupport;

import org.netbeans.api.tasklist.*;


// XXX todo: fire property change whenever anything changes in the node...

/** Class which represents a task in the
 * tasklist.
 * @author Tor Norbye */
public class SuggestionImpl extends Task implements Node.Cookie {

    //private String action;
    private String filename = null;
    private String basename = null;
    private int linenumber = 0;
    private Object seed = null;
    private String category = null;
    private SuggestionType stype = null;
    //private boolean highlighted = false;

    /** Field (package private) used by the SourceScanner as a tag
        to improve search speeds. Don't muck with it. */
    transient boolean scantag;
    
    protected SuggestionImpl() {
    }

    public SuggestionImpl(String summary, SuggestionType stype,
                          SuggestionPerformer action,
                          Object data) {
        super(summary, null);
        this.seed = data;
        this.stype = stype;
        setAction(action);
        if (stype != null) {
            setType(stype.getName());
        }
    }

    /** Return true iff the task has an associated file position */
    public boolean hasAssociatedFilePos() {
	return ((linenumber > 0) && (filename != null) && 
                (filename.length() > 0));
    }
    
    /** Return the name of the file associated with this
     * task, or the empty string if none.
     * @return basename, or empty string */    
    public String getFileBaseName() {
        if (basename == null) {

            Line l = getLine();
            if (l == null) {
                basename = "";
            } else {
                DataObject dobj = DataEditorSupport.findDataObject(l);
                if ((dobj != null) && (dobj.getPrimaryFile() != null)) {
                    basename = dobj.getPrimaryFile().getNameExt();
                }
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
        return "SuggestionImpl" + /*(" + super.toString() + ")" */ "[\"" + getSummary() + "\", " + getFileBaseName() + ":" + getLineNumber() + /* ", " + stype + */ "]"; // NOI18N
    }

    /** Create a node for this item */
    public Node[] createNode() {
        if (hasSubtasks()) {
            return new Node[] { new SuggestionNode(this, getSubtasks())};
        } else {
            return new Node[] { new SuggestionNode(this)};
        }
    }

    /** Create an identical copy of a task (a deep copy, e.g. the
        list of subtasks will be cloned as well */
    protected Object clone() {
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
        seed = from.seed;
        stype = from.stype;
        //highlighted = from.highlighted;
        
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

    /** 
     * Get the providedData which created this suggestion.
     * May be null (since not only SuggestionProviders
     * are allowed to register suggestions)
     */
    public Object getSeed() {
         return seed;
    }

/*
    public boolean isHighlighted() {
        return highlighted;
    }
    public void setHighlighted(boolean highlight) {
        //if (highlight) {
        //    setSummary("<html><b>" + getSummary() + "</b></html>");
        //} else {
        //    String desc = getSummary();
        //    setSummary(desc.substring(9, desc.length()-11)); // remove <html><b></b></html>
        //}
        //
        //// getIcon will get called and will report new icon
        //
        updatedValues(); // TODO - just set this on the setIcon method?
    }
*/
}



