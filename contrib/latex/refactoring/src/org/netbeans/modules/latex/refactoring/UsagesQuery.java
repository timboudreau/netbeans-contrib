/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.refactoring;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.LaTeXSource;
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
    
    public static List<ArgumentNode> findLabelUsages(LaTeXSource source, final String label) {
        final List<ArgumentNode> result = new ArrayList();
        
        source.traverse(new DefaultTraverseHandler() {
            @Override
            public boolean argumentStart(ArgumentNode node) {
                if (node.hasAttribute("#ref") || node.hasAttribute("#label")) {
                    if (label.contentEquals(getArgumentContent(node))) {
                        result.add(node);
                    }
                }
                return true;
            }
        }, LaTeXSource.HEAVY_LOCK);
        
        return result;
    }
    
    public static List<Node> findCommandUsages(LaTeXSource source, final Command cmd) {
        final List<Node> result = new ArrayList();
        
        source.traverse(new DefaultTraverseHandler() {
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
        }, LaTeXSource.HEAVY_LOCK);
        
        return result;
    }
    
    public static List<Node> findEnvironmentUsages(LaTeXSource source, final Environment env) {
        final List<Node> result = new ArrayList();
        
        source.traverse(new DefaultTraverseHandler() {
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
        }, LaTeXSource.HEAVY_LOCK);
        
        return result;
    }
    
//#environmentname
}
