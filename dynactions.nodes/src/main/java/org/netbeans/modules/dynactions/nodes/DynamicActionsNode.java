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

package org.netbeans.modules.dynactions.nodes;

import org.netbeans.api.dynactions.*;
import javax.swing.Action;
import org.netbeans.api.dynactions.ActionFactory;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 * An all-purpose node which looks up its actions dynamically based on its
 * lookup contents.
 *
 * @author Tim Boudreau
 */
public class DynamicActionsNode extends AbstractNode {
    private final ActionFactory actionFactory;
    protected DynamicActionsNode (Children kids, Lookup lkp, ActionFactory factory) {
        super (kids, lkp);
        this.actionFactory = factory;
    }
    
    protected DynamicActionsNode (Children kids, Lookup lkp, String actionContext) {
        this (kids, lkp, actionContext, new Prov());
    }
    
    private DynamicActionsNode (Children kids, Lookup lkp, String actionContext, Prov prov) {
        super (kids, lkp);
        prov.provider = this;
        this.actionFactory = ActionFactory.lookup(prov, actionContext);
    }
    
    @Override
    public Action[] getActions (boolean popup) {
        Action[] actions = super.getActions(popup);
        Action[] others = actionFactory.getActions();
        Action[] result = new Action[actions.length + others.length];
        System.arraycopy(actions, 0, result, 0, actions.length);
        System.arraycopy(others, 0, result, actions.length, others.length);
        return result;
    }

    static class Prov implements Lookup.Provider {
        Lookup.Provider provider;
        public Lookup getLookup() {
            return provider == null ? Lookup.EMPTY : provider.getLookup();
        }
    }
}
