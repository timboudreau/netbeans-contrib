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

package org.netbeans.modules.vcscore.versioning.impl;

import java.util.*;

import org.netbeans.modules.vcscore.versioning.RevisionItem;

/**
 *
 * @author  Martin Entlicher
 */
public class NumDotRevisionItem extends RevisionItem {

    /*
    private String revision;
    private String revisionVCS; // the original VCS revision. May differ from "revision".
    private Vector branches;
    private String displayName;
    private String message;
    private String date;
    private String author;
    private Vector tagNames;
     */
    private NumDotRevisionItem next;
    /*
    private boolean current;
    private Hashtable additionalProperties;
     */

    /** Creates new RevisionItem */
    public NumDotRevisionItem(String revision) {
        super(revision);
        next = null;
        /*
        this.revision = revision;
        this.revisionVCS = revision;
        branches = null;
        message = null;
        date = null;
        author = null;
        tagNames = new Vector();
        next = null;
        current = false;
        additionalProperties = new Hashtable();
        displayName = revision;
         */
    }

    //public void setRevision(String revision) {
    //    this.revision = revision;
    //}

    /** Get the revision of that item.
     *
    public String getRevision() {
        return this.revision;
    }
    
    /** Get the original VCS revision of that item.
     * That may differ from getRevision(), due to the fact, that odd-dotted denote
     * revisions and even-dotted denote branches.
     *
    public String getRevisionVCS() {
        return this.revisionVCS;
    }
    
    public void setRevisionVCS(String revisionVCS) {
        this.revisionVCS = revisionVCS;
    }

    public boolean isRevision() {
        return (branches == null);
    }
     */

    public boolean isBranch() {
        return (evenDots());
    }
    
    /*
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
     */
    
    public static int numDots(String str) {
        int num = 0;
        for(int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.') num++;
        }
        return num;
    }

    private boolean evenDots() {
        return (NumDotRevisionItem.numDots(getRevision()) % 2) == 0;
    }

    /*
    private int cmpRev_OLD(String revision) {
        int lastDot1 = this.revision.lastIndexOf('.');
        int lastDot2 = revision.lastIndexOf('.');
        int rev1 = 0;
        int rev2 = 0;
        try {
            rev1 = Integer.parseInt(this.revision.substring(lastDot1+1));
            rev2 = Integer.parseInt(revision.substring(lastDot2+1));
        } catch (NumberFormatException e) {
            return -1000;
        }
        return rev1 - rev2;
    }
    */

    protected int cmpRev(String revision) {
        StringTokenizer tokens1 = new StringTokenizer(getRevision(), ".");
        StringTokenizer tokens2 = new StringTokenizer(revision, ".");
        while(tokens1.hasMoreTokens() && tokens2.hasMoreTokens()) {
            String rev1 = tokens1.nextToken();
            String rev2 = tokens2.nextToken();
            int irev1 = 0;
            int irev2 = 0;
            try {
                irev1 = Integer.parseInt(rev1);
                irev2 = Integer.parseInt(rev2);
            } catch (NumberFormatException e) {
                return -1000;
            }
            if (irev1 != irev2) return irev1 - irev2;
        }
        if (tokens1.hasMoreTokens()) return +1;
        if (tokens2.hasMoreTokens()) return -1;
        return 0;
    }

    public RevisionItem addRevision(String revision) {
        boolean inserted = false;
        RevisionItem addedRevision = null;
        if (next == null) {
            if (numDots(revision) == numDots(getRevision())) {
                next = new NumDotRevisionItem(revision);
                addedRevision = next;
                inserted = true;
            } else if (evenDots() && revision.indexOf(getRevision()) == 0) {// this <- the beginning of a branch
                next = new NumDotRevisionItem(revision);
                addedRevision = next;
                inserted = true;
            }
        } else {
            if (numDots(revision) == numDots(next.getRevision()) && next.cmpRev(revision) > 0) {
                NumDotRevisionItem nextOne = next;
                next = new NumDotRevisionItem(revision);
                addedRevision = next;
                next.setNextItem(nextOne);
                inserted = true;
            } else {
                //System.out.println("Leaving revision "+revision+" to the next."); // NOI18N
                addedRevision = next.addRevision(revision);
            }
        }
        if (!inserted && this.branches != null) {
            Enumeration enum = branches.elements();
            while(enum.hasMoreElements()) {
                RevisionItem branch = ((RevisionItem) enum.nextElement());
                if (revision.indexOf(branch.getRevision()) == 0) addedRevision = branch.addRevision(revision);
            }
        }
        return addedRevision;
    }

    public RevisionItem addBranch(String branch) {
        RevisionItem addedRevision = null;
        if (branch.indexOf(getRevision()) == 0 && (numDots(getRevision()) + 1) == numDots(branch)) {
            if (branches == null) branches = new Vector();
            addedRevision = new NumDotRevisionItem(branch);
            branches.add(addedRevision);
        } else {
            if (next != null) addedRevision = next.addBranch(branch);
            if (branches != null) {
                Enumeration enum = branches.elements();
                while(enum.hasMoreElements())
                    addedRevision = ((RevisionItem) enum.nextElement()).addBranch(branch);
            }
        }
        return addedRevision;
    }
    
    protected RevisionItem getNextItem() {
        return next;
    }
    
    private void setNextItem(NumDotRevisionItem next) {
        this.next = next;
    }
    
    /*
    public void putToList(Collection list) {
        if (!list.contains(this)) list.add(this);
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
     */
}
