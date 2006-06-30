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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.errorstripe;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.editor.errorstripe.privatespi.Mark;
import org.netbeans.modules.editor.errorstripe.privatespi.Status;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.PositionCookie;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXStructureMark implements Mark {
    
    private static Status STATUS_OK = Status.STATUS_OK;
    
    private StructuralElement element;
    
    /** Creates a new instance of AnnotationMark */
    public LaTeXStructureMark(StructuralElement element) {
        this.element = element;
    }

    public Status getStatus() {
        return STATUS_OK;
    }

    public Color getEnhancedColor() {
        int priority = element.getPriority();
        
        return (Color) priority2Color.get(new Integer(priority));
    }

    public int[] getAssignedLines() {
        Node n = StructuralNodeFactory.createNode(element);
        PositionCookie pc = (PositionCookie) n.getCookie(PositionCookie.class);
        SourcePosition position = pc.getPosition();
        int line = position.getLine();
        
        return new int[] {line, line};
    }

    public String getShortDescription() {
        Node n = StructuralNodeFactory.createNode(element);
        
        return n.getShortDescription();
    }
    
    public int getType() {
        return TYPE_ERROR_LIKE;
    }
    
    /*package private*/ static Map/*<Integer, Color>*/ priority2Color;
    
    static {
        priority2Color = new HashMap();
        
        priority2Color.put(new Integer(2000), Color.CYAN); //\chapter
        priority2Color.put(new Integer(3000), Color.MAGENTA); //\section
        priority2Color.put(new Integer(4000), Color.ORANGE); //\subsection
        priority2Color.put(new Integer(5000), Color.PINK); //\subsubsection
    }

    public int getPriority() {
        return PRIORITY_DEFAULT;
    }
    
}
