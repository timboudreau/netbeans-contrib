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
 * PaintCatcher.java
 *
 * Created on February 23, 2004, 8:19 PM
 */

package org.netbeans.modules.paintcatcher;

import java.awt.EventQueue;
import javax.swing.RepaintManager;

/** A handy tool for figuring out who is triggering repaints.
 *
 * @author  Tim Boudreau
 */
public class PaintCatcher {
    private Filter filter;
    private Logger logger;
    private LoggingEventQueue q = null;
    private LoggingRepaintManager mgr = null;
    private boolean logAWTEvents = false;
    /** Creates a new instance of PaintCatcher */
    public PaintCatcher(Filter filter, Logger logger) {
        this.filter = filter;
        this.logger = logger;
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
        return mgr != null;
    }
    
    public boolean isLogAWTEvents() {
        return logAWTEvents;
    }
    
    public void setLogAWTEvents(boolean val) {
        if (isEnabled()) {
            throw new IllegalStateException();
        } else {
            logAWTEvents = val;
        }
    }
    
    private void enable() {
        mgr = new LoggingRepaintManager(filter, logger);
        mgr.setEnabled(true);
        if (isLogAWTEvents()) {
            q = new LoggingEventQueue(filter, logger);
            q.setEnabled(true);
        }
    }
    
    private void disable() {
        mgr.setEnabled(false);
        if (q != null) {
            q.setEnabled(false);
        }
        mgr = null;
        q = null;
    }
    
}
