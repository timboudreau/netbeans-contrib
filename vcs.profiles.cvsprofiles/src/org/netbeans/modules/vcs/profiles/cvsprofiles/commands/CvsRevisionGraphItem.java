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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.util.*;

/**
 *
 * @author  Martin Entlicher
 */
public class CvsRevisionGraphItem extends Object {

    String revision;
    Vector branches;
    Vector merges;
    CvsRevisionGraphItem next;
    int xPos;
    int yPos;

    /** Creates new CvsRevisionGraphItem */
    public CvsRevisionGraphItem(String revision) {
        this.revision = revision;
        branches = null;
        merges = null;
        next = null;
        xPos = 0;
        yPos = 0;
    }

    private int numDots(String str) {
        int num = 0;
        for(int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.') num++;
        }
        return num;
    }

    private boolean evenDots() {
        return (numDots(this.revision) % 2) == 0;
    }

    private int cmpRev(String revision) {
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

    public void addRevision(String revision) {
        //System.out.println("My rev = "+this.revision+", I'm adding rev = "+revision);
        boolean inserted = false;
        if (next == null) {
            if (numDots(revision) == numDots(this.revision)) {
                next = new CvsRevisionGraphItem(revision);
                inserted = true;
            } else if (evenDots() && revision.indexOf(this.revision) == 0
                       && revision.charAt(this.revision.length()) == '.') {// this <- the beginning of a branch
                next = new CvsRevisionGraphItem(revision);
                inserted = true;
            }
        } else {
            if (numDots(revision) == numDots(next.revision) && next.cmpRev(revision) > 0) {
                // System.out.println("Next has rev = "+next.revision);
                CvsRevisionGraphItem nextOne = next;
                next = new CvsRevisionGraphItem(revision);
                next.next = nextOne;
                inserted = true;
            } else {
                //System.out.println("Leaving revision "+revision+" to the next.");
                next.addRevision(revision);
            }
        }
        //System.out.println("Inserted = "+inserted);
        if (!inserted && this.branches != null) {
            Enumeration enum = branches.elements();
            while(enum.hasMoreElements()) {
                CvsRevisionGraphItem branch = ((CvsRevisionGraphItem) enum.nextElement());
                if (revision.indexOf(branch.revision) == 0) branch.addRevision(revision);
            }
        }
    }

    public void addBranch(String branch) {
        if (branch.indexOf(this.revision) == 0 && branch.charAt(this.revision.length()) == '.'
                                               && (numDots(this.revision) + 1) == numDots(branch)) {
            if (branches == null) branches = new Vector();
            branches.add(new CvsRevisionGraphItem(branch));
        } else {
            if (next != null) next.addBranch(branch);
            if (branches != null) {
                Enumeration enum = branches.elements();
                while(enum.hasMoreElements())
                    ((CvsRevisionGraphItem) enum.nextElement()).addBranch(branch);
            }
        }
    }

    public int getXPos() {
        return this.xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return this.yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public String getRevision() {
        return this.revision;
    }

    public CvsRevisionGraphItem getNext() {
        return this.next;
    }

    public Vector getBranches() {
        return this.branches;
    }

    public Vector getMerges() {
        return this.merges;
    }

    public void print() {
        print("");
    }

    private void print(String preString) {
        System.out.println(preString+"Revision: "+this.revision);
        if (branches != null) {
            Enumeration enum = branches.elements();
            while(enum.hasMoreElements()) {
                CvsRevisionGraphItem branch = ((CvsRevisionGraphItem) enum.nextElement());
                System.out.println(preString+"Starting branch:"+branch.revision);
                if (branch.next != null) branch.next.print(preString+"  ");
            }
        }
        if (next != null) next.print(preString);
    }
}
