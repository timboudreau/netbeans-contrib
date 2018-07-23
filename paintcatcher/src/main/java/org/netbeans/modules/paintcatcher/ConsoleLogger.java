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
