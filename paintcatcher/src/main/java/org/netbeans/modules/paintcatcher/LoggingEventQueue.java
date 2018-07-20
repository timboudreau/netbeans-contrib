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
