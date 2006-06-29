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
