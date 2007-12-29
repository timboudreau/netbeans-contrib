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
 * Contributor(s): Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.pojoeditors.api;

import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.dynactions.ActionFactory;
import org.netbeans.modules.dynactions.nodes.DynamicActionsDataNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.PasteType;

/**
 * DataNode subclass for use with PojoDataObject, supporting pluggable actions.
 * Allows dynamic action registration against the node's lookup.
 *
 * @author Tim Boudreau
 */
public class PojoDataNode<T extends Serializable> extends DynamicActionsDataNode {
    protected PojoDataNode (PojoDataObject<T> dob, Children kids, Lookup lkp, String actionContext) {
        super (dob, kids, lkp, actionContext);
    }
    
    protected PojoDataNode (PojoDataObject<T> dob, Children kids, String actionContext) {
        super (dob, kids, actionContext);
    }
    
    protected PojoDataNode (PojoDataObject<T> dob, Children kids, Lookup lkp, ActionFactory factory) {
        super (dob, kids, lkp, factory);
    }
    
    @Override
    public final Action[] getActions (boolean popup) {
        Action[] base = super.getActions(popup);
        List <Action> actions = getPojoDob().getOpenActions();
        actions.addAll(Arrays.asList(base));
        onGetActions (actions);
        Action[] result = new Action[actions.size()];
        result = actions.toArray(result);
        return result;
    }
    
    /**
     * Called when the pojo of the owning DataObject is unloaded because 
     * modifications were reverted.  Typical implementation refreshes the children
     * of this node.  Default implementation does nothing.
     */
    protected void hintChildrenChanged() {
        //do nothing
    }

    /**
     * Override to attach weak listeners to the pojo.  Default implementation
     * does nothing.
     * @param pojo The pojo
     */
    protected void onLoad (T pojo) {
        //do nothing
    }

    @Override
    public Action getPreferredAction() {
        Action result = getPojoDob().getDefaultOpenAction();
        return result == null ? super.getPreferredAction() : result;
    }
    
    @SuppressWarnings("unchecked")
    private PojoDataObject<T> getPojoDob() {
        return (PojoDataObject<T>) getDataObject();
    }
    
    /**
     * Add or sort actions to the list to have them appear on the popup.  The list
     * is pre-populated with whatever open actions the DataObject supports.
     * 
     * @param actions A list of actions that may be modified
     */
    protected void onGetActions(List<Action> actions) {
        //do nothing by default
    }
}
