/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore.versioning;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import javax.swing.event.EventListenerList;

import org.openide.nodes.Node.Cookie;

/**
 * The representation of a revision item.
 *
 * @author  Martin Entlicher
 */
public abstract class RevisionItem extends Object implements Cookie, Comparable, java.io.Serializable {

    static final long serialVersionUID=6021472313331712674L;

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
    public final String getRevision() {
        return this.revision;
    }
    
    /** Get the original VCS revision of that item.
     * That may differ from getRevision(), due to the fact, that odd-dotted denote
     * revisions and even-dotted denote branches.
     */
    public final String getRevisionVCS() {
        return this.revisionVCS;
    }
    
    public final void setRevisionVCS(String revisionVCS) {
        this.revisionVCS = revisionVCS;
    }

    public boolean isRevision() {
        return (branches == null);
    }

    /**
     * Find whether the current item represents a branch.
     * @return <code>true</code> when the current item represents a branch,
     *         <code>false</code> otherwise.
     */
    public abstract boolean isBranch();
    
    /**
     * Test whether the current item is a direct sub-item of the given item.
     * This method is used to constuct the revision children.
     * @param item The item to test or <code>null</code>.
     */
    public abstract boolean isDirectSubItemOf(RevisionItem item);
    
    public final void setDisplayName(String displayName) {
        if (!displayName.equals(this.displayName)) {
            String oldDisplayName = this.displayName;
            this.displayName = displayName;
            firePropertyChange(PROP_DISPLAY_NAME, oldDisplayName, displayName);
        }
    }
    
    public final String getDisplayName() {
        return this.displayName;
    }
    
    public final void setMessage(String message) {
        if (!message.equals(this.message)) {
            String oldMessage = this.message;
            this.message = message;
            firePropertyChange(PROP_MESSAGE, oldMessage, message);
        }
    }
    
    public final String getMessage() {
        return this.message;
    }
    
    public final void setDate(String date) {
        if (!date.equals(this.date)) {
            String oldDate = this.date;
            this.date = date;
            firePropertyChange(PROP_DATE, oldDate, date);
        }
    }
    
    public final String getDate() {
        return this.date;
    }
    
    public final void setAuthor(String author) {
        if (!author.equals(this.author)) {
            String oldAuthor = this.author;
            this.author = author;
            firePropertyChange(PROP_AUTHOR, oldAuthor, author);
        }
    }
    
    public final String getAuthor() {
        return this.author;
    }

    public final void setLocker(String locker) {
        if (!locker.equals(this.locker)) {
            String oldLocker = this.locker;
            this.locker = locker;
            firePropertyChange(PROP_LOCKER, oldLocker, locker);
        }
    }

    public final String getLocker() {
        return this.locker;
    }

    public final void addTagName(String tagName) {
        tagNames.add(tagName);
        if (isBranch()) setDisplayName(revision+" ("+tagName+")");
        firePropertyChange(PROP_TAGS, null, null);
    }

    public final boolean removeTagName(String tagName) {
        boolean removed = tagNames.remove(tagName);
        if (removed) firePropertyChange(PROP_TAGS, null, null);
        return removed;
    }

    public final void setTagNames(String[] tagNames) {
        this.tagNames = new Vector(Arrays.asList(tagNames));
        firePropertyChange(PROP_TAGS, null, null);
    }

    public final String[] getTagNames() {
        return (String[]) tagNames.toArray(new String[0]);
    }
    
    public final void setCurrent(boolean current) {
        //System.out.println("RevisionItem("+revision+"): current = "+this.current+", setCurrent("+current+")");
        if (current != this.current) {
            this.current = current;
            firePropertyChange(PROP_CURRENT_REVISION, !current ? Boolean.TRUE : Boolean.FALSE, current ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    public final boolean isCurrent() {
        return current;
    }

    public final void addProperty(String name, String value) {
        additionalProperties.put(name, value);
        firePropertyChange(PROP_ADDITIONAL_PROPERTIES, null, null);
    }
    
    public final Map getAdditionalProperties() {
        return additionalProperties;
    }
    
    public final void addAdditionalPropertiesSet(String name, Map properties) {
        additionalPropertiesSets.add(name);
        additionalPropertiesSets.add(properties);
    }
    
    public final String[] getAdditionalPropertiesSetNames() {
        ArrayList names = new ArrayList();
        for (int i = 0; i < additionalPropertiesSets.size(); i += 2) {
            names.add(additionalPropertiesSets.get(i));
        }
        return (String[]) names.toArray(new String[0]);
    }
    
    public final Map[] getAdditionalPropertiesSets() {
        ArrayList sets = new ArrayList();
        for (int i = 1; i < additionalPropertiesSets.size(); i += 2) {
            sets.add(additionalPropertiesSets.get(i));
        }
        return (Map[]) sets.toArray(new Map[0]);
    }
    
    /**
     * Compare to another revision item. See {@link Comparable#compareTo}
     * for the contract definition.
     */
    protected abstract int compareTo(RevisionItem item);
    
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
        return this.compareTo((RevisionItem) p1);
    }
    
    public final void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }

    public final void removePropertyChangeListener(PropertyChangeListener l) {
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
