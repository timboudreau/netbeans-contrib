/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * ClassNameFilter.java
 *
 * Created on February 23, 2004, 9:57 PM
 */

package org.netbeans.modules.paintcatcher;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.PaintEvent;
import javax.swing.SwingUtilities;

/**
 *
 * @author  tim
 */
public class ComponentPaintFilter implements Filter {
    private Class clazz;
    private boolean subs = false;
    private boolean anc = false;
    
    /** Creates a new instance of ClassNameFilter */
    public ComponentPaintFilter (Class clazz, boolean allowSubclasses, boolean matchIfAncestor) {
        this.clazz = clazz;
        this.subs = allowSubclasses;
        this.anc = matchIfAncestor;
    }
    
    public boolean match(Component c) {
        if (c == null) {
            return false;
        }
        boolean result = c.getClass() == clazz;
        if (subs) {
            result |= clazz.isAssignableFrom(clazz);
        }
        if (anc) {
            Object o = SwingUtilities.getAncestorOfClass(clazz, c);
            result |= o != null;
        }
        return result;
    }    
    
    public boolean match(java.util.EventObject eo) {
        /*
        boolean result = false;
        if (eo instanceof PaintEvent) {
            result = match ((Component) eo.getSource());
        }
         */
        boolean result = eo instanceof PaintEvent;
        return result;
    }    
    
}
