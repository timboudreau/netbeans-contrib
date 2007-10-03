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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.util.*;
import java.io.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.*;
import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.util.*;

/**
 *
 * @author  Martin Entlicher
 */
public class CvsLogInfo extends Object implements CommandDataOutputListener {

    private StringBuffer logBuffer = new StringBuffer(4096);
    private Vector revisions = new Vector();
    private Vector revisionsFile = new Vector();
    private Vector branches = new Vector();
    private Vector branchesFile = new Vector();
    private Vector orderRevBranch = new Vector();
    private Vector orderRevBranchFile = new Vector();
    private static final int ORDER_REVISION = 0;
    private static final int ORDER_BRANCH = 1;

    /**
     * Contains revision number as the key and the tag name as the value.
     */
    private Hashtable symbolicNames = new Hashtable();
    private Hashtable symbolicNamesFile = new Hashtable();
    private boolean matchingSymbolicNames = false;
    private boolean mergeIsEmpty = true;
    private CvsRevisionGraph revisionGraph = null;

    private String revisionStr = new String("revision"); // NOI18N
    private String branchesStr = new String("branches"); // NOI18N
    private String symbNamesStr = new String("symbolic names"); // NOI18N
    private String nextFileStr = new String("=================="); // NOI18N
    
    private VcsFileSystem fileSystem = null;

    /** Creates new CvsLogInfo */
    public CvsLogInfo() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public Vector getRevisions() {
        return revisions;
    }

    public Vector getBranches() {
        return branches;
    }

    public Hashtable getSymbolicNames() {
        return symbolicNames;
    }

    public Vector getRevisionsWithSymbolicNames() {
        String revision;
        Vector revisions = getRevisions();
        Vector revSN = new Vector(revisions);
        Hashtable sn = getSymbolicNames();
        Enumeration enum = sn.keys();
        while(enum.hasMoreElements()) {
            revision = (String) enum.nextElement();
            String symbName = (String) sn.get(revision);
            int revIndex = revSN.indexOf(revision);
            if (symbName != null) revision += "  "+symbName; // NOI18N
            if (revIndex >= 0) {
                revSN.setElementAt(revision, revIndex);
            } else {
                revSN.addElement(revision);
            }
        }
        return revSN;
    }

    public Vector getBranchesWithSymbolicNames() {
        String revision;
        Vector branches = getBranches();
        Vector brSN = new Vector(branches);
        Hashtable sn = getSymbolicNames();
        Enumeration enum = sn.keys();
        while(enum.hasMoreElements()) {
            revision = (String) enum.nextElement();
            String symbName = (String) sn.get(revision);
                        int brIndex = brSN.indexOf(revision);
            if (brIndex >= 0) {
                if (symbName != null) revision += "  "+symbName; // NOI18N
                brSN.setElementAt(revision, brIndex);
            } else {
                int lastDot = revision.lastIndexOf('.');
                if (lastDot > 1 && revision.charAt(lastDot - 1) == '0' &&
                        revision.charAt(lastDot - 2) == '.') {
                    String branch = revision.substring(0, lastDot-1) + revision.substring(lastDot+1);
                    brIndex = brSN.indexOf(branch);
                    if (symbName != null) branch += "  "+symbName; // NOI18N
                    if (brIndex >= 0) {
                        brSN.setElementAt(branch, brIndex);
                    } else {
                        brSN.addElement(branch);
                    }
                }
            }
        }
        return brSN;
    }

    public CvsRevisionGraph getRevisionGraph() {
        return revisionGraph;
    }

    public boolean updateLogInfo(Hashtable vars, String cmdName,
                                 CommandOutputListener stdoutListener,
                                 CommandOutputListener stderrListener) {
        revisionGraph = new CvsRevisionGraph();
        mergeIsEmpty = true;
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, new Hashtable(vars));
        vce.addDataOutputListener(this);
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            return false;
        }
        if (vce.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            return false;
        }
        for(int i = 0; i < orderRevBranch.size(); i++) {
            Integer what = (Integer) orderRevBranch.get(i);
            i++;
            if (what.intValue() == ORDER_REVISION) {
                Integer revision = (Integer) orderRevBranch.get(i);
                revisionGraph.insertRevision((String) revisions.get(revision.intValue()));
            } else {
                Integer branch = (Integer) orderRevBranch.get(i);
                revisionGraph.insertBranch((String) branches.get(branch.intValue()));
            }
        }
        //for(int i = 0; i < branches.size(); i++) revisionGraph.insertBranch((String) branches.get(i));
        //for(int i = 0; i < revisions.size(); i++) revisionGraph.insertRevision((String) revisions.get(i));
        return true;
    }

    /**
     * Merge the common content of Hashtables <CODE>h1</CODE> and <CODE>h2</CODE> into <CODE>h1</CODE>.
     * In other words it deletes every entry of <CODE>h1</CODE> which is not contained in <CODE>h2</CODE>.
     * @param h1
     * @param h2
     */
    private void mergeCommonHashtable(Hashtable h1, Hashtable h2) {
        Enumeration enum1 = h1.keys();
        while (enum1.hasMoreElements()) {
            Object o1 = enum1.nextElement();
            boolean contains = false;
            Enumeration enum2 = h2.keys();
            while (enum2.hasMoreElements()) {
                Object o2 = enum2.nextElement();
                if (o1.equals(o2)) { contains = true; break; }
            }
            if (!contains) h1.remove(o1);
        }
    }

    /**
     * Merge the content of order vectors.
     */
    private void mergeRevBr() {
        for(int i = 0; i < orderRevBranch.size(); i++) {
            Integer what = (Integer) orderRevBranch.get(i);
            i++;
            if (what.intValue() == ORDER_REVISION) {
                Integer revision = (Integer) orderRevBranch.get(i);
                String rev = (String) revisions.get(revision.intValue());
                int j;
                for(j = 0; j < orderRevBranchFile.size(); j++) {
                    Integer whatFile = (Integer) orderRevBranchFile.get(j);
                    j++;
                    if (whatFile.intValue() == ORDER_REVISION) {
                        Integer revisionFile = (Integer) orderRevBranchFile.get(j);
                        String revFile = (String) revisionsFile.get(revisionFile.intValue());
                        if (revFile.equals(rev)) break;
                    }
                }
                if (j >= orderRevBranchFile.size()) {
                    orderRevBranch.remove(i-1);
                    orderRevBranch.remove(i);
                    revisions.remove(revision.intValue());
                    i -= 2;
                }
            } else {
                Integer branch = (Integer) orderRevBranch.get(i);
                String br = (String) branches.get(branch.intValue());
                int j;
                for(j = 0; j < orderRevBranchFile.size(); j++) {
                    Integer whatFile = (Integer) orderRevBranchFile.get(j);
                    j++;
                    if (whatFile.intValue() == ORDER_REVISION) {
                        Integer branchFile = (Integer) orderRevBranchFile.get(j);
                        String brFile = (String) branchesFile.get(branchFile.intValue());
                        if (brFile.equals(br)) break;
                    }
                }
                if (j >= orderRevBranchFile.size()) {
                    orderRevBranch.remove(i-1);
                    orderRevBranch.remove(i);
                    branches.remove(branch.intValue());
                    i -= 2;
                }
            }
        }
    }

    private void mergeCommonRevisions() {
        if (mergeIsEmpty) {
            revisions = new Vector(revisionsFile);
            branches = new Vector(branchesFile);
            symbolicNames = new Hashtable(symbolicNamesFile);
            orderRevBranch = new Vector(orderRevBranchFile);
        } else {
            //mergeCommonVector(revisions, revisionsFile);
            //mergeCommonVector(branches, branchesFile);
            mergeCommonHashtable(symbolicNames, symbolicNamesFile);
            mergeRevBr();
            //orderRevBranch = new Vector(orderRevBranchFile);
        }
        mergeIsEmpty = false;
    }

    public void outputData(String[] elements) {
        if (elements[0] == null) return;
        logBuffer.append(elements[0]+"\n"); // NOI18N
        if (elements[0].indexOf(nextFileStr) >= 0) {
            mergeCommonRevisions();
        }
        if (elements[0].indexOf(revisionStr) == 0) {
            int endRev = elements[0].indexOf('\t', revisionStr.length() + 1);
            if (endRev < 0) endRev = elements[0].length();
            String revision = elements[0].substring(revisionStr.length(), endRev).trim();
            revisionsFile.add(revision);
            orderRevBranchFile.add(new Integer(ORDER_REVISION));
            orderRevBranchFile.add(new Integer(revisionsFile.size() - 1));
            //revisionGraph.insertRevision(revision);
        }
        if (elements[0].indexOf(branchesStr) >= 0) {
            int bBegin = branchesStr.length()+1;
            int bEnd = elements[0].indexOf(';', bBegin);
            if (bEnd < 0) bEnd = elements[0].length();
            while (bBegin < bEnd) {
                String branch = elements[0].substring(bBegin, bEnd).trim();
                branchesFile.add(branch);
                orderRevBranchFile.add(new Integer(ORDER_BRANCH));
                orderRevBranchFile.add(new Integer(branchesFile.size() - 1));
                //revisionGraph.insertBranch(branch);
                bBegin = bEnd+1;
                bEnd = elements[0].indexOf(';', bBegin);
                if (bEnd < 0) bEnd = elements[0].length();
            }
        }
        if (matchingSymbolicNames) {
            int keyIndex = 0;
            while (elements[0].charAt(keyIndex) == ' ' || elements[0].charAt(keyIndex) == '\t') keyIndex++;
            if (keyIndex == 0) matchingSymbolicNames = false;
            else {
                int valueIndex = keyIndex;
                while (elements[0].charAt(valueIndex) != ':') valueIndex++;
                /*
                symbolicNames.put(elements[0].substring(keyIndex, valueIndex).trim(),
                                  elements[0].substring(valueIndex + 1, elements[0].length()).trim());
                */
                symbolicNamesFile.put(elements[0].substring(valueIndex + 1, elements[0].length()).trim(),
                                      elements[0].substring(keyIndex, valueIndex).trim());
            }
        }
        if (elements[0].indexOf(symbNamesStr) >= 0) matchingSymbolicNames = true;
    }
}
