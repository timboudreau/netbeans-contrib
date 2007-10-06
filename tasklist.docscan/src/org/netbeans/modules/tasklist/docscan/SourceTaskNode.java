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

package org.netbeans.modules.tasklist.docscan;


import javax.swing.*;

import org.openide.util.actions.SystemAction;
import org.openide.nodes.Node;
import org.openide.nodes.Children;


import org.netbeans.modules.tasklist.suggestions.*;
import org.netbeans.modules.tasklist.core.*;

/**
 * Represents one scanned source task as a Node with
 * actions, cookies, properties, clipboard operations and
 * children (at root represents all tasks in list).
 *
 * @author Petr Kuzel
 */
class SourceTaskNode extends SuggestionNode {

    public SourceTaskNode(SuggestionImpl rootItem) {
      super(rootItem, Children.LEAF);
    }


    public SourceTaskNode(SuggestionImpl rootItem, Children children) {
      super(rootItem, children);
    }

    public Node cloneNode () {
      SourceTaskNode clon = new SourceTaskNode((SuggestionImpl)this.item);
      if (!clon.isLeaf()) 
	clon.setChildren((Children)getTaskChildren().clone());
      return clon;
    }

    protected TaskChildren createChildren() {
      return new SourceTaskChildren((SuggestionImpl)this.item);
    }

    public Action[] getActions(boolean context) {
        return new Action[] {
            SystemAction.get(ShowSuggestionAction.class)
        };
    }

//     public String getDisplayName() {
//         return Util.getString("task-col");  // see TreeTableModelAdapter.getColumnName(int column)
//     }

//     public String getShortDescription() {
//         return Util.getString("TODOHint");
//     }

    
}

