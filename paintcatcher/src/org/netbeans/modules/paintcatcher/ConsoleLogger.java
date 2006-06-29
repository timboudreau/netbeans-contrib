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
 *//*
 * ConsoleLogger.java
 *
 * Created on February 23, 2004, 9:47 PM
 */

package org.netbeans.modules.paintcatcher;

import java.awt.Component;
import java.awt.event.PaintEvent;
import java.lang.StackTraceElement;
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
            dumpStack();
        }
    }    
    
    public void log(String msg, EventObject eo) {
        maybePrintBreak();
        System.err.println((idx++) + ":" + System.currentTimeMillis() + ":" + (msg != null ? msg + " " : "") + "on " + eventToString(eo));
        if (dumpStack) {
            dumpStack();
        }
    }    
    
    private boolean dontLogCaretEvents = true;
    
    private void dumpStack() {
        if (!dontLogCaretEvents) {
            Thread.dumpStack();
        } else {
            Exception e = new Exception();
            e.fillInStackTrace();
            StackTraceElement[] ste = e.getStackTrace();
            for (int i = 0; i < ste.length; i++) {
                if (ste[i].toString().contains ("BaseCaret")) {
                    return;
                }
            }
            e.printStackTrace();
        }
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
