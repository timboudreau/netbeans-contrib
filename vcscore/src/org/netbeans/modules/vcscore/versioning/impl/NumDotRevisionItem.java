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

    private NumDotRevisionItem next;

    private static final long serialVersionUID = 7946273312693547993L;
    
    /** Creates new RevisionItem */
    public NumDotRevisionItem(String revision) {
        super(revision);
        next = null;
    }

    public boolean isBranch() {
        return (evenDots());
    }
    
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
    
}
