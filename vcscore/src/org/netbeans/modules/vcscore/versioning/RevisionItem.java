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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import javax.swing.event.EventListenerList;

import org.openide.nodes.Node.Cookie;

/**
 *
 * @author  Martin Entlicher
 */
public abstract class RevisionItem extends Object implements Cookie, Comparable, java.io.Serializable {

    public static final String PROP_REVISION = "revision";
    public static final String PROP_CURRENT_REVISION = "currentRevision";
    public static final String PROP_DISPLAY_NAME = "displayName";
    public static final String PROP_MESSAGE = "message";
    public static final String PROP_DATE = "date";
    public static final String PROP_AUTHOR = "author";
    public static final String PROP_LOCKER = "locker";
    public static final String PROP_TAGS = "tags";
    public static final String PROP_ADDITIONAL_PROPERTIES = "additionalProperties";
    
    private String revision;
    private String revisionVCS; // the original VCS revision. May differ from "revision".
    protected Vector branches;
    private String displayName;
    private String message;
    private String date;
    private String author;
    private String locker;
    private Vector tagNames;
    //private RevisionItem next;
    private boolean current;
    private Hashtable additionalProperties;
    private ArrayList additionalPropertiesSets;
    private transient PropertyChangeSupport changeSupport;

    /** Creates new RevisionItem */
    public RevisionItem(String revision) {
        this.revision = revision;
        this.revisionVCS = revision;
        branches = null;
        message = null;
        date = null;
        author = null;
        locker = null;
        tagNames = new Vector();
        //next = null;
        current = false;
        additionalProperties = new Hashtable();
        additionalPropertiesSets = new ArrayList();
        displayName = revision;
        changeSupport = new PropertyChangeSupport(this);
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
        if (!displayName.equals(this.displayName)) {
            String oldDisplayName = this.displayName;
            this.displayName = displayName;
            firePropertyChange(PROP_DISPLAY_NAME, oldDisplayName, displayName);
        }
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setMessage(String message) {
        if (!message.equals(this.message)) {
            String oldMessage = this.message;
            this.message = message;
            firePropertyChange(PROP_MESSAGE, oldMessage, message);
        }
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setDate(String date) {
        if (!date.equals(this.date)) {
            String oldDate = this.date;
            this.date = date;
            firePropertyChange(PROP_DATE, oldDate, date);
        }
    }
    
    public String getDate() {
        return this.date;
    }
    
    public void setAuthor(String author) {
        if (!author.equals(this.author)) {
            String oldAuthor = this.author;
            this.author = author;
            firePropertyChange(PROP_AUTHOR, oldAuthor, author);
        }
    }
    
    public String getAuthor() {
        return this.author;
    }

    public void setLocker(String locker) {
        if (!locker.equals(this.locker)) {
            String oldLocker = this.locker;
            this.locker = locker;
            firePropertyChange(PROP_LOCKER, oldLocker, locker);
        }
    }

    public String getLocker() {
        return this.locker;
    }

    public void addTagName(String tagName) {
        tagNames.add(tagName);
        if (isBranch()) setDisplayName(revision+" ("+tagName+")");
        firePropertyChange(PROP_TAGS, null, null);
    }

    public boolean removeTagName(String tagName) {
        boolean removed = tagNames.remove(tagName);
        if (removed) firePropertyChange(PROP_TAGS, null, null);
        return removed;
    }

    public void setTagNames(String[] tagNames) {
        this.tagNames = new Vector(Arrays.asList(tagNames));
        firePropertyChange(PROP_TAGS, null, null);
    }

    public String[] getTagNames() {
        return (String[]) tagNames.toArray(new String[0]);
    }
    
    public void setCurrent(boolean current) {
        //System.out.println("RevisionItem("+revision+"): current = "+this.current+", setCurrent("+current+")");
        if (current != this.current) {
            this.current = current;
            firePropertyChange(PROP_CURRENT_REVISION, !current ? Boolean.TRUE : Boolean.FALSE, current ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    public boolean isCurrent() {
        return current;
    }

    public void addProperty(String name, String value) {
        additionalProperties.put(name, value);
        firePropertyChange(PROP_ADDITIONAL_PROPERTIES, null, null);
    }
    
    public Hashtable getAdditionalProperties() {
        return additionalProperties;
    }
    
    public void addAdditionalPropertiesSet(String name, Map properties) {
        additionalPropertiesSets.add(name);
        additionalPropertiesSets.add(properties);
    }
    
    public String[] getAdditionalPropertiesSetNames() {
        ArrayList names = new ArrayList();
        for (int i = 0; i < additionalPropertiesSets.size(); i += 2) {
            names.add(additionalPropertiesSets.get(i));
        }
        return (String[]) names.toArray(new String[0]);
    }
    
    public Map[] getAdditionalPropertiesSets() {
        ArrayList sets = new ArrayList();
        for (int i = 1; i < additionalPropertiesSets.size(); i += 2) {
            sets.add(additionalPropertiesSets.get(i));
        }
        return (Map[]) sets.toArray(new Map[0]);
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
    
    /*
    public boolean equals(Object obj) {
        if (obj instanceof RevisionItem)
            return this.revision.equals(((RevisionItem) obj).getRevision());
        else
            return false;
    }
     */
    
    public int compareTo(final java.lang.Object p1) {
        //System.out.println(getRevision()+".compareTo("+((RevisionItem) p1).getRevision()+") = "+this.cmpRev(((RevisionItem) p1).getRevision()));
        return -this.cmpRev(((RevisionItem) p1).getRevision());
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }
    
    protected final void firePropertyChange(String property, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(property, oldValue, newValue);
    }
    
    /*
    public boolean equals(Object obj) {
        boolean isEqual = super.equals(obj);
        System.out.println("RevisionItem: "+this+".equals("+obj+") = "+isEqual);
        return isEqual;
    }
     */
    
    public String toString() {
        return "["+revision+", branches = "+((branches == null) ? null : (new ArrayList(branches)))+", tags = "+getTagNames()+"]";
    }
    
    private void readObject(java.io.ObjectInputStream in) throws ClassNotFoundException, java.io.IOException, java.io.NotActiveException {
        in.defaultReadObject();
        changeSupport = new PropertyChangeSupport(this);
    }
}
