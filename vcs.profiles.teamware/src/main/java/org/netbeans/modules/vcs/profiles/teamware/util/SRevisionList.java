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
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
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