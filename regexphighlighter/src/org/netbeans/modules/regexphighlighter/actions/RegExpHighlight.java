/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.regexphighlighter.actions;

import java.awt.Color;
import javax.swing.text.Position;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class RegExpHighlight {
    
    private Color color;
    private Position start;
    private Position end;
    
    /** Creates a new instance of DefaultHighlight */
    public RegExpHighlight(Color color, Position start, Position end) {
        this.color = color;
        this.start = start;
        this.end = end;
    }
    
    public int getStart() {
        return start.getOffset();
    }
    
    public int getEnd() {
        return end.getOffset();
    }
    
    public Color getColor() {
        return color;
    }
}
