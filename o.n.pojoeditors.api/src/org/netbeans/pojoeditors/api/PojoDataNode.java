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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.pojoeditors.api;

import java.util.List;
import javax.swing.Action;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public class PojoDataNode extends DataNode {
    protected PojoDataNode (PojoDataObject dob, Children kids, Lookup lkp) {
        super (dob, kids, lkp);
    }
    
    protected PojoDataNode (PojoDataObject dob, Children kids) {
        super (dob, kids);
    }
    
    @Override
    public final Action[] getActions (boolean popup) {
        List <Action> actions = ((PojoDataObject) getDataObject()).getOpenActions();
        onGetActions (actions);
        Action[] result = new Action[actions.size()];
        result = actions.toArray(result);
        return result;
    }

    @Override
    public Action getPreferredAction() {
        Action result = ((PojoDataObject) getDataObject()).getDefaultOpenAction();
        return result == null ? super.getPreferredAction() : result;
    }
    
    /**
     * Add actions to the list to have them appear on the popup.  The list
     * is pre-populated with whatever open actions the DataObject supports.
     * 
     * @param actions A list of actions that may be modified
     */
    protected void onGetActions(List<Action> actions) {
        //do nothing by default
    }
    
}
