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
 * @version 
 */
public class CvsRevisionGraph extends Object {

    CvsRevisionGraphItem root;

    /** Creates new CvsRevisionGraph */
    public CvsRevisionGraph() {
        root = new CvsRevisionGraphItem("1.1");
        root.setXPos(0);
        root.setYPos(0);
    }

    public CvsRevisionGraphItem getRoot() {
        return root;
    }

    public void insertRevision(String revision) {
        if (!revision.equals("1.1")) this.root.addRevision(revision);
    }

    public void insertBranch(String branch) {
        this.root.addBranch(branch);
    }


}