/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem;

public class SRevisionList extends RevisionList {

    private SRevisionItem activeRevision = null;

    public synchronized void add(RevisionItem item) {
        String revision = item.getRevision();
        if (NumDotRevisionItem.numDots(revision) > 1) {
            // Assure that we have the branch for that revision created
            String branch = revision.substring(0, revision.lastIndexOf('.'));
            for (Iterator i = iterator(); i.hasNext();) {
                RevisionItem ri = (RevisionItem) i.next();
                if (branch.equals(ri.getRevision())) {
                    branch = null;
                    break;
                }
            }
            if (branch != null) {
                super.add(new SRevisionItem(branch));
            }
        } else {
            if (activeRevision == null) {
                activeRevision = (SRevisionItem) item;
            }
        }
        super.add(item);
    }
    
    public SRevisionItem getActiveRevision() {
        return activeRevision;
    }
    
    public SRevisionItem getRevisionByName(String revision) {
        for (Iterator i = iterator(); i.hasNext();) {
            SRevisionItem ri = (SRevisionItem) i.next();
            if (ri.getRevision().equals(revision)) {
                return ri;
            }
        }
        return null;
    }
    
    public SRevisionItem getRevisionBySerialNumber(String sn) {
        for (Iterator i = iterator(); i.hasNext();) {
            SRevisionItem ri = (SRevisionItem) i.next();
            if (String.valueOf(ri.getSerialNumber()).equals(sn)) {
                return ri;
            }
        }
        return null;
    }

    public Set getSerialNumbers(SRevisionItem item) {
        Set sns = new HashSet();
        LinkedList waitingList = new LinkedList();
        waitingList.add(String.valueOf(item.getSerialNumber()));
        Set nonTransitiveAdditions = new HashSet();
        Set nonTransitiveRemovals = new HashSet();
        Set ignores = new HashSet();
        while (!waitingList.isEmpty()) {
            String sn = (String) waitingList.removeFirst();
            if (!sns.contains(sn)) {
                item = getRevisionBySerialNumber(sn);
                if (item == null) {
                    continue;
                }
                sns.add(sn);
                Set inclusions = item.includedSerialNumbers();
                for (Iterator i = inclusions.iterator(); i.hasNext();) {
                    // add deltas included by inclusions
                    // but not the predecessors of inclusions
                    LinkedList additionalInclusions = new LinkedList();
                    additionalInclusions.add(i.next());
                    while (!additionalInclusions.isEmpty()) {
                        String inclusion = (String)
                            additionalInclusions.removeFirst();
                        nonTransitiveAdditions.add(inclusion);
                        SRevisionItem includedItem
                            = getRevisionBySerialNumber(inclusion);
                        additionalInclusions.addAll(includedItem.includedSerialNumbers());
                    }
                }
                nonTransitiveRemovals.addAll(item.excludedSerialNumbers());
                ignores.addAll(item.ignoredSerialNumbers());
            }
            sn = String.valueOf(item.getPredecessor());
            if (!sns.contains(sn) && !waitingList.contains(sn)) {
                waitingList.add(sn);
            }
        }
        sns.addAll(nonTransitiveAdditions);
        sns.removeAll(nonTransitiveRemovals);
        sns.removeAll(ignores);
        return sns;
    }
    
}