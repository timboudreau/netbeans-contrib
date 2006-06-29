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
/*
 * ClassNameFilter.java
 *
 * Created on February 23, 2004, 9:57 PM
 */

package org.netbeans.modules.paintcatcher;

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
            result |= clazz.isAssignableFrom(c.getClass());
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
    
    public void foo() {
        
    }
    
    
    
    
}
