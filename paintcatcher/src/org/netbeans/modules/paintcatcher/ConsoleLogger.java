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
 * ConsoleLogger.java
 *
 * Created on February 23, 2004, 9:47 PM
 */

package org.netbeans.modules.paintcatcher;

import java.awt.Component;
import java.awt.event.PaintEvent;
import java.util.EventObject;
import javax.swing.AbstractButton;

/**
 *
 * @author  Tim Boudreau
 */
class ConsoleLogger implements Logger {
    int idx = 0;
    private boolean dumpStack;
    private long lastLogTime = -1;
    /** Creates a new instance of ConsoleLogger */
    public ConsoleLogger(boolean dumpStack) {
        this.dumpStack = dumpStack;
    }
    
    public void log(String msg) {
        maybePrintBreak();
        long time = System.currentTimeMillis();
        if (time - lastLogTime > 10000) {
            System.err.println("\n--------------------------------------------");
        }
        lastLogTime = time;
        System.err.println((idx++) + ":" + System.currentTimeMillis() + ":" + msg);
    }    
    
    public void log(String msg, Component c) {
        maybePrintBreak();
        System.err.println((idx++) + ":" + System.currentTimeMillis() + ":" + msg + " " + componentToString(c));
        if (dumpStack) {
            Thread.dumpStack();
        }
    }    
    
    public void log(String msg, EventObject eo) {
        maybePrintBreak();
        System.err.println((idx++) + ":" + System.currentTimeMillis() + ":" + (msg != null ? msg + " " : "") + "on " + eventToString(eo));
    }
    
    private void maybePrintBreak() {
        long time = System.currentTimeMillis();
        if (time - lastLogTime > 10000) {
            System.err.println("\n--------------------------------------------");
        }
        lastLogTime = time;
    }
    
    private String componentToString (Component c) {
        if (c == null) {
            return "null";
        }
        String name = c.getName();
        if (c instanceof AbstractButton) {
            name = name + " \"" + ((AbstractButton)c).getText() + "\" ";
        }
        if (name != null) {
            return name + "(" + c.getClass().getName() + ")";
        } else {
            return c.getClass().getName() + "@" + System.identityHashCode(c);
        }
    }
    
    private String eventToString (EventObject eo) {
        StringBuffer result = new StringBuffer();
        if (eo instanceof PaintEvent) {
            PaintEvent p = (PaintEvent) eo;
            result.append ("PAINT " + p.getUpdateRect() + " ");
        }
        Object o = eo.getSource();
        if (o instanceof Component) {
            result.append (componentToString((Component)o));
        }
        return result.toString();
    }
    
}
