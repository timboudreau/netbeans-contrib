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
 * LoggingEventQueue.java
 *
 * Created on February 23, 2004, 8:24 PM
 */

package org.netbeans.modules.paintcatcher;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author  Tim Boudreau
 */
class LoggingEventQueue extends EventQueue {
    private Filter filter;
    private Logger logger;
    /** Creates a new instance of LoggingEventQueue */
    public LoggingEventQueue(Filter f, Logger l) {
        this.filter = f;
        this.logger = l;
    }

    public void postEvent(AWTEvent e) {
        if (filter.match(e)) {
            logger.log("posted", e);
        }
        super.postEvent (e);
    }
    
    public boolean isEnabled() {
        return orig != null;
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
    
    EventQueue orig = null;
    private void enable() {
        if (!isEnabled()) {
            orig = Toolkit.getDefaultToolkit().getSystemEventQueue();
            orig.push (this);
            System.err.println("Installed logging event queue");
        }
    }
    
    private void disable() {
        try {
            if (isEnabled()) {
                Stack stack = new Stack();
                EventQueue curr = Toolkit.getDefaultToolkit().getSystemEventQueue();
                while (curr != this) {
                    curr = popQ();
                    if (curr != this) {
                        stack.push(curr);
                    }
                }
                pop();
                curr = orig;
                assert Toolkit.getDefaultToolkit().getSystemEventQueue() == orig;
                while (!stack.isEmpty()) {
                    EventQueue next = (EventQueue) stack.pop();
                    curr.push(next);
                    curr = next;
                }
            System.err.println("Uninstalled logging event queue");
            }
        } finally {
            orig = null;
        }
    }
    
    public synchronized void push(EventQueue newEventQueue) {
        if (newEventQueue instanceof LoggingEventQueue) {
            return;
        }
    }
    
    private EventQueue popQ() { 
        try {
            Method popMethod = getPopMethod();
            if (popMethod == null) {
                throw new IllegalStateException("Can't access EventQueue.pop");
            }
            EventQueue result = Toolkit.getDefaultToolkit().getSystemEventQueue();
            popMethod.invoke(result, null);
            return result;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new IllegalStateException ("Can't invoke EventQueue.pop"); 
            }
        }
        
    }
    
    private static Method popMethod = null;
    private static Method getPopMethod() {
        if (popMethod == null) {
            try {
                popMethod = EventQueue.class.getDeclaredMethod ("pop", null);
                popMethod.setAccessible(true);
            } catch (Exception e) {
                
            }
        }
        return popMethod;
    }
    
}
