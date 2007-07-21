/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.refactoring;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;

/**
 *
 * @author Jan Lahoda
 */
public class UsagesQuery {
    
    /** Creates a new instance of UsagesQuery */
    private UsagesQuery() {
    }
    
    public static CharSequence getArgumentContent(ArgumentNode node) {
        CharSequence text  = node.getFullText();
        int          start = "{[".indexOf(text.charAt(0)) != (-1) ? 1 : 0;
        int          end   = "}]".indexOf(text.charAt(text.length() - 1)) != (-1) ? text.length() - 1 : text.length();
        
        return text.subSequence(start, end);
    }
    
    public static List<ArgumentNode> findLabelUsages(LaTeXParserResult lpr, final String label) {
        final List<ArgumentNode> result = new ArrayList<ArgumentNode>();
        
        lpr.getDocument().traverse(new DefaultTraverseHandler() {
            @Override
            public boolean argumentStart(ArgumentNode node) {
                if (node.hasAttribute("#ref") || node.hasAttribute("#label")) {
                    if (label.contentEquals(getArgumentContent(node))) {
                        result.add(node);
                    }
                }
                return true;
            }
        });
        
        return result;
    }
    
    public static List<Node> findCommandUsages(LaTeXParserResult lpr, final Command cmd) {
        final List<Node> result = new ArrayList<Node>();
        
        lpr.getDocument().traverse(new DefaultTraverseHandler() {
            @Override
            public boolean commandStart(CommandNode node) {
                if (node.getCommand().equals(cmd)) {
                    result.add(node);
                }
                return true;
            }
            @Override
            public boolean argumentStart(ArgumentNode node) {
                if (node.hasAttribute("#command-def")) {
                    if (cmd.getCommand().contentEquals(getArgumentContent(node))) {
                        result.add(node);
                    }
                }
                return true;
            }
        });
        
        return result;
    }
    
    public static List<Node> findEnvironmentUsages(LaTeXParserResult lpr, final Environment env) {
        final List<Node> result = new ArrayList<Node>();
        
        lpr.getDocument().traverse(new DefaultTraverseHandler() {
            @Override
            public boolean blockStart(BlockNode node) {
                if (node.getEnvironment().equals(env)) {
                    result.add(Utilities.getDefault().getArgumentWithAttribute(node.getBeginCommand(), "#environmentname"));
                    result.add(Utilities.getDefault().getArgumentWithAttribute(node.getEndCommand(), "#environmentname"));
                }
                return true;
            }
            @Override
            public boolean argumentStart(ArgumentNode node) {
                if (node.hasAttribute("#envname")) {
                    if (env.getName().contentEquals(getArgumentContent(node))) {
                        result.add(node);
                    }
                }
                return true;
            }
        });
        
        return result;
    }
    
//#environmentname
}
