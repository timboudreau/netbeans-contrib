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

package org.netbeans.modules.vcscore.versioning;

import java.util.*;

import org.openide.nodes.Node.Cookie;

/**
 *
 * @author  Martin Entlicher
 */
public abstract class RevisionItem extends Object implements Cookie, Comparable, java.io.Serializable {

    private String revision;
    private String revisionVCS; // the original VCS revision. May differ from "revision".
    protected Vector branches;
    private String displayName;
    private String message;
    private String date;
    private String author;
    private Vector tagNames;
    //private RevisionItem next;
    private boolean current;
    private Hashtable additionalProperties;

    /** Creates new RevisionItem */
    public RevisionItem(String revision) {
        this.revision = revision;
        this.revisionVCS = revision;
        branches = null;
        message = null;
        date = null;
        author = null;
        tagNames = new Vector();
        //next = null;
        current = false;
        additionalProperties = new Hashtable();
        displayName = revision;
    }

    /** Get the revision of that item.
     */
    public String getRevision() {
        return this.revision;
    }
    
    /** Get the original VCS revision of that item.
     * That may differ from getRevision(), due to the fact, that odd-dotted denote
     * revisions and even-dotted denote branches.
     */
    public String getRevisionVCS() {
        return this.revisionVCS;
    }
    
    public void setRevisionVCS(String revisionVCS) {
        this.revisionVCS = revisionVCS;
    }

    public boolean isRevision() {
        return (branches == null);
    }

    public abstract boolean isBranch();
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getDate() {
        return this.date;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getAuthor() {
        return this.author;
    }

    public void addTagName(String tagName) {
        tagNames.add(tagName);
        if (isBranch()) displayName = revision+" ("+tagName+")";
    }

    public boolean removeTagName(String tagName) {
        return tagNames.remove(tagName);
    }

    public void setTagNames(String[] tagNames) {
        this.tagNames = new Vector(Arrays.asList(tagNames));
    }

    public String[] getTagNames() {
        return (String[]) tagNames.toArray(new String[0]);
    }
    
    public void setCurrent(boolean current) {
        this.current = current;
    }
    
    public boolean isCurrent() {
        return current;
    }

    public void addProperty(String name, String value) {
        additionalProperties.put(name, value);
    }
    
    public Hashtable getAdditionalProperties() {
        return additionalProperties;
    }
    
    protected abstract RevisionItem getNextItem();

    protected abstract int cmpRev(String revision);

    public abstract RevisionItem addRevision(String revision);

    public abstract RevisionItem addBranch(String branch);
    
    public void putToList(Collection list) {
        if (!list.contains(this)) list.add(this);
        RevisionItem next = getNextItem();
        if (next != null) next.putToList(list);
        if (branches != null) {
            for(Enumeration enum = branches.elements(); enum.hasMoreElements(); ) {
                ((RevisionItem) enum.nextElement()).putToList(list);
            }
        }
    }

    public int hashCode() {
        return revision.hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof RevisionItem)
            return this.revision.equals(((RevisionItem) obj).getRevision());
        else
            return false;
    }
    
    public int compareTo(final java.lang.Object p1) {
        //System.out.println(getRevision()+".compareTo("+((RevisionItem) p1).getRevision()+") = "+this.cmpRev(((RevisionItem) p1).getRevision()));
        return -this.cmpRev(((RevisionItem) p1).getRevision());
    }
}
