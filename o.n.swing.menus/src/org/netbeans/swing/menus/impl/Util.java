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
/*
 * Util.java
 *
 * Created on May 21, 2004, 6:51 PM
 */

package org.netbeans.swing.menus.impl;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

/**
 * Some utility methods with logic used by components that listen on trees
 *
 * @author  Tim Boudreau
 */
public class Util {
    public static final int CHANGED = 0;
    public static final int INSERTED = 1;
    public static final int REMOVED = 2;
    public static final int STRUCTURE = 3;

    /** Creates a new instance of Util */
    private Util() {
    }

   public static boolean isInteresting (TreeModelEvent e, int id, Object root) {
        TreePath path = e.getTreePath();
        boolean result = false;
        switch (id) {
            case CHANGED :
            case INSERTED :
            case REMOVED :
                result = inImmediateSubtree(path, root);
                break;
            case STRUCTURE :
                result = true;
                break;
            default :
                throw new IllegalArgumentException();
        }
        return result;
    }
    
    
    private static boolean inImmediateSubtree (TreePath path, Object node) {
        return path.getLastPathComponent() == node;
    }    
    
    
    
}
