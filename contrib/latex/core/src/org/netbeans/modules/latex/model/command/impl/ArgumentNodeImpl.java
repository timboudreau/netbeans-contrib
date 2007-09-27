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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.impl;

import java.io.PrintWriter;
import java.util.Iterator;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.model.command.Command.Param;
import org.netbeans.modules.latex.test.TestCertificate;

/**
 *
 * @author Jan Lahoda
 */
public class ArgumentNodeImpl extends GroupNodeImpl implements ArgumentNode {
    
    private boolean present;
    private Param   param;
    
    /** Creates a new instance of ArgumentNodeImpl */
    public ArgumentNodeImpl(ArgumentContainingNodeImpl parent, boolean present, NodeImpl previousCommandDefiningNode) {
        super(parent, previousCommandDefiningNode);
        
//        System.err.println("ArgumentNodeImpl construstor=" + this);
        
//        new Exception("ArgumentNodeImpl constructor, ihc=" + System.identityHashCode(this)).printStackTrace(System.err);
        
        this.present = present;
    }
    
    public boolean isPresent() {
        return present;
    }
    
    public boolean isValidEnum() {
        if (!getArgument().isEnumerable())
            return false;
        
        return getArgument().isValid(getText()) == Param.ENUM;
    }
    
    public Param getArgument() {
        return param;
    }
    
    public void setArgument(Param param) {
        this.param = param;
    }
    
    public ArgumentContainingNode getCommand() {
        return (ArgumentContainingNode) getParent();
    }

    public void traverse(TraverseHandler th) {
        if (th.argumentStart(this)) {
            super.traverseImpl(th);
        }
        
        th.argumentEnd(this);
    }

    public void dump(TestCertificate tc, PrintWriter pw) {
        pw.println("<ArgumentNodeImpl>");
        dumpPositions(tc, pw);

        Iterator iter = getChildrenIterator();
        
        while (iter.hasNext()) {
            ((NodeImpl) iter.next()).dump(tc, pw);
        }
        
        pw.println("</ArgumentNodeImpl>");
    }

    /**No attributes
     */
    public String getAttribute(String name) {
        return getArgument().getAttribute(name);
    }

    /**No attributes
     */
    public boolean hasAttribute(String name) {
        return getArgument().hasAttribute(name);
    }

}
