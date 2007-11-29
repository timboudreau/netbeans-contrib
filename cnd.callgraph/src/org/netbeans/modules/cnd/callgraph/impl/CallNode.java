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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.callgraph.impl;

import org.netbeans.modules.cnd.callgraph.api.*;
import java.awt.Image;
import java.awt.Point;
import javax.swing.Action;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.cnd.callgraph.api.ui.CallGraphActionsFactory;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Alexander Simon
 */
public class CallNode extends AbstractNode {
    private Call object;
    private CallGraphState model;
    private boolean isCalls;

    public CallNode(Call element, CallGraphState model, boolean isCalls) {
        this(element, model, isCalls, false);
    }

    public CallNode(Call element, CallGraphState model, boolean isCalls, boolean isRoot) {
        super(new CallChildren(element, model, isCalls));
        object = element;
        this.model = model;
        this.isCalls = isCalls;
        if (isCalls) {
            setName(element.getFunctionDescription());
        } else {
            setName(element.getOwnerDescription());
        }

        Function toFunction = new Function(element,true);
        Widget to = model.getScene().findWidget(toFunction);
        if (to == null){
            to = model.getScene().addNode(toFunction);
            to.setPreferredLocation (new Point (100, 100));
        }
        if (!isRoot && element.getCallOwner() != null) {
            Function fromFunction = new Function(element, false);
            Widget from = model.getScene().findWidget(fromFunction);
            if (from == null) {
                from = model.getScene().addNode(fromFunction);
                from.setPreferredLocation(new Point(10, 10));
            }
            if (model.getScene().findEdgesBetween(toFunction, fromFunction).size()==0) {
                model.getScene().addEdge(element);
                model.getScene().setEdgeSource(element, fromFunction);
                model.getScene().setEdgeTarget(element, toFunction);
            }
        }
        model.getScene().validate();
        model.getSceneLayout().invokeLayout();
    }
    
    @Override
    public Image getIcon(int param) {
        Image res = null;
        if (isCalls) {
            res = object.getFunctionIcon();
        } else {
            res = object.getOwnerIcon();
        }
        if (res == null){
            res = super.getIcon(param);
        }
        return res;
    }
    
    @Override
    public Image getOpenedIcon(int param) {
        return getIcon(param);
    }
    
    @Override
    public Action getPreferredAction() {
        return CallGraphActionsFactory.getDefault().gotoAction(object);
    }

    @Override
    public Action[] getActions(boolean context) {
        Action action = getPreferredAction();
        if (action != null){
            return new Action[]{action};
        }
        return new Action[]{};
    }
}