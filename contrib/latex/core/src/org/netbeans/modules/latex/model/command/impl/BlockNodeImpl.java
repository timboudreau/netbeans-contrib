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
import javax.swing.text.Position;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandCollection;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.TextNode;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.test.TestCertificate;

/**
 *
 * @author Jan Lahoda
 */
public class BlockNodeImpl extends ArgumentContainingNodeImpl implements BlockNode {
    
    public static final Environment NULL_ENVIRONMENT = new Environment("nullenvironments");
    
    private CommandNodeImpl begin;
    private CommandNodeImpl end;
    private TextNodeImpl    content;
    private Environment    environment;
    
    /** Creates a new instance of BlockNodeImpl */
    public BlockNodeImpl(Node parent, NodeImpl previousCommandDefiningNode, Environment env) {
        super(parent, previousCommandDefiningNode, env);
        this.environment = env;
    }
    
    public CommandNode getBeginCommand() {
        return begin;
    }
    
    public void setBeginCommand(CommandNodeImpl begin) {
        this.begin = begin;
        setStartingPosition(begin.getStartingPosition());
        begin.setParent(this);
    }
    
    public CommandNode getEndCommand() {
        return end;
    }
    
    public void setEndCommand(CommandNodeImpl end) {
        this.end = end;
        setEndingPosition(end.getEndingPosition());
        end.setParent(this);
    }
    
    public TextNode getContent() {
        return content;
    }
    
    public void setContent(TextNodeImpl content) {
        this.content = content;
    }
    
    public void traverse(TraverseHandler th) {
        th.blockStart(this);
        getBeginCommand().traverse(th);
        getContent().traverse(th);
        if (getEndCommand() != null)
            getEndCommand().traverse(th);
        th.blockEnd(this);
    }

    protected boolean isInChild(Object file, Position pos) {
        return    begin.isInChild(file, pos)
               || content.isInChild(file, pos)
               || (end != null && end.isInChild(file, pos));
    }
    
    public void dump(TestCertificate tc, PrintWriter pw) {
        pw.println("<BlockNodeImpl>");
        dumpPositions(tc, pw);

        pw.println("<begin>");
        ((NodeImpl) getBeginCommand()).dump(tc, pw);
        pw.println("</begin>");
        pw.println("<content>");
        ((NodeImpl) getContent()).dump(tc, pw);
        pw.println("</content>");
        pw.println("<end>");
        ((NodeImpl) getEndCommand()).dump(tc, pw);
        pw.println("</end>");
        pw.println("</BlockNodeImpl>");
    }
    
    public String getBlockName() {
        CommandNode start = getBeginCommand();
        
        if (start.getArgumentCount() > 0) {
            CharSequence fullName = start.getArgument(0).getFullText();
            
            if (fullName.charAt(0) == '{') {
                fullName = fullName.subSequence(1, fullName.length());
            }
            
            if (fullName.charAt(fullName.length() - 1) == '}') {
                fullName = fullName.subSequence(0, fullName.length() - 1);
            }
        
            return fullName.toString();
        }
        
        return "";
    }
    
    public String toString() {
        return "BlockNodeImpl[name=" + getBlockName() + ", begin=" + getBeginCommand() + ",end=" + getEndCommand() + "]";
    }
    
    public void setCommandCollection(CommandCollection commandCollection) {
        super.setCommandCollection(commandCollection);
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    /**No attributes
     */
    public String getAttribute(String name) {
        return getEnvironment().getAttribute(name);
    }

    /**No attributes
     */
    public boolean hasAttribute(String name) {
        return getEnvironment().hasAttribute(name);
    }

}
