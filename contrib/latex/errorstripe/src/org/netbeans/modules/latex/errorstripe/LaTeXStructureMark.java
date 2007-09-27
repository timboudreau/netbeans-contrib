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
