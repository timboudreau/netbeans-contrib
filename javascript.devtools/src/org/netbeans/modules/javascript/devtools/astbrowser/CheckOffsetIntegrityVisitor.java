/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript.devtools.astbrowser;

import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.Node;
import java.util.ArrayList;

/**
 *
 * @author petr
 */
public class CheckOffsetIntegrityVisitor extends ScannerVisitor {

    private int wrongOffset = -1;
    private jdk.nashorn.internal.ir.Node parentNode;
    
    private ArrayList<Node> parents = new ArrayList<Node>();

    /**
     * 
     * @return if -1, no wrong offset
     */
    public int checkOffset(jdk.nashorn.internal.ir.Node inode) {
        parentNode = inode;
        inode.accept(this);
        return wrongOffset;
    }


    @Override
    public Node scanOnSet(Node iNode, boolean onset) {
        if (iNode instanceof IdentNode && ((IdentNode)iNode).getName().equals("runScript")) {
            return iNode;
        }
        
        if (onset) {
            System.out.println("Scan - parent: " + parentNode.getClass().getSimpleName()
                + " current: " + iNode.getClass().getSimpleName());
            if (parentNode.getStart() > iNode.getStart()) {
                wrongOffset = iNode.getStart();
                System.out.println("wrong start");
            }
            if (parentNode.getFinish() < iNode.getFinish()) {
                wrongOffset = iNode.getFinish();
                System.out.println("wrong end");
            }
            parents.add(iNode);
            System.out.println("Add parent: " + iNode.getClass().getSimpleName());
        } else {
            parents.remove(parents.size() - 1);
            if (parents.size() > 0) {
  
                System.out.println("Parent back: " + parents.get(parents.size() - 1).getClass().getSimpleName());
                
                
            }
        }
        return iNode;
    }
    
    
}
