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
 *//*
 * LoggingRepaintManager.java
 *
 * Created on February 23, 2004, 8:24 PM
 */

package org.netbeans.modules.paintcatcher;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.RepaintManager;

/** A repaint manager which will logs information about interesting events.
 *
 * @author  Tim Boudreau
 */
class LoggingRepaintManager extends RepaintManager {
    private Filter filter;
    private Logger logger;
    private RepaintManager orig = null;
    /** Creates a new instance of LoggingRepaintManager */
    public LoggingRepaintManager(Filter f, Logger l) {
        this.filter = f;
        this.logger = l;
    }
    
    public void setEnabled (boolean val) {
        if (isEnabled() != val) {
            if (val) {
                enable();
            } else {
                disable();
            }
        }
    }
    
    public boolean isEnabled() {
        return orig != null;
    }
    
    private void enable() {
        orig = currentManager(new JLabel()); //could be null for standard impl
        setCurrentManager(this);
    }
    
    private void disable() {
        setCurrentManager(orig);
        orig = null;
    }
    
    private boolean hasValidateMatches = false;
    private boolean hasDirtyMatches = false;
    public synchronized void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        if (w != 0 && h != 0 && filter.match (c)) {
            logger.log ("addDirtyRegion " + x + "," + y + "," + w + "," + h, c);
            hasDirtyMatches = true;
        }
        super.addDirtyRegion (c, x, y, w, h);
    }
    
    public synchronized void addInvalidComponent(JComponent c) {
        if (filter.match(c)) {
            logger.log ("addInvalidComponent", c);
            hasValidateMatches = true;
        }
        super.addInvalidComponent(c);
    }
    
    public void paintDirtyRegions() {
        if (hasDirtyMatches) {
            logger.log("paintDirtyRegions");
            hasDirtyMatches = false;
        }
        super.paintDirtyRegions();
    }
    
    public void validateInvalidComponents() {
        if (hasValidateMatches) {
            logger.log("validateInvalidComponents");
            hasValidateMatches = false;
        }
        super.validateInvalidComponents();
    }    
    
}
