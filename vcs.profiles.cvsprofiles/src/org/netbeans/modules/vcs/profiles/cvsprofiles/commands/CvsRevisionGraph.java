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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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