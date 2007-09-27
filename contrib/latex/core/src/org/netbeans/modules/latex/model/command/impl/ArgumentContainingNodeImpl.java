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
package org.netbeans.modules.latex.model.command.impl;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.NamedAttributableWithArguments;
import org.netbeans.modules.latex.model.command.Node;

/**
 *
 * @author Jan Lahoda
 */
public abstract class ArgumentContainingNodeImpl extends NodeImpl implements ArgumentContainingNode {

    private NamedAttributableWithArguments command;
    private List<ArgumentNode>    arguments;
    
    public ArgumentContainingNodeImpl(Node parent, NodeImpl previousCommandDefiningNode, NamedAttributableWithArguments command) {
        super(parent, previousCommandDefiningNode);
        this.command = command;
        arguments    = new ArrayList<ArgumentNode>();
    }

    public ArgumentNode getArgument(int index) {
        return (ArgumentNode) arguments.get(index);
    }
    
    public int getArgumentCount() {
        return arguments.size();
    }
    
    public void putArgument(int index, ArgumentNode arg) {
        while (arguments.size() < index) {
            ArgumentNodeImpl an = new ArgumentNodeImpl(this, false, ((NodeImpl) arg).getPreviousCommandDefiningNode());
            
            an.setArgument(command.getArgument(arguments.size()));
            an.setStartingPosition(arg.getStartingPosition());
            an.setEndingPosition(arg.getStartingPosition());
            
            arguments.add(an);
        }
        
        if (arguments.size() == index) {
            arguments.add(index, arg);
        } else {
            arguments.set(index, arg);
        }
    }
    
    public void addArgument(ArgumentNode arg) {
        putArgument(arguments.size(), arg);
    }
    
    public NamedAttributableWithArguments getArgumentsSpecification() {
        return command;
    }
    
}
