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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
 */

package org.netbeans.modules.debugger.javafx.breakpoints;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerListener;
import org.netbeans.api.debugger.LazyActionsManagerListener;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.Watch;


import org.netbeans.api.debugger.javafx.ClassLoadUnloadBreakpoint;
import org.netbeans.api.debugger.javafx.ExceptionBreakpoint;
import org.netbeans.api.debugger.javafx.FieldBreakpoint;
import org.netbeans.modules.debugger.javafx.JavaFXDebuggerImpl;
import org.netbeans.modules.debugger.javafx.SourcePath;
import org.netbeans.api.debugger.javafx.JavaFXDebugger;
import org.netbeans.api.debugger.javafx.LineBreakpoint;
import org.netbeans.api.debugger.javafx.MethodBreakpoint;
import org.netbeans.api.debugger.javafx.ThreadBreakpoint;
import org.openide.util.RequestProcessor;


/**
 * Listens on JavaFXDebugger.PROP_STATE and DebuggerManager.PROP_BREAKPOINTS, and
 * and creates XXXBreakpointImpl classes for all JavaFXBreakpoints.
 *
 * @author   Jan Jancura
 */
public class BreakpointsEngineListener extends LazyActionsManagerListener 
implements PropertyChangeListener, DebuggerManagerListener {
    
    private static Logger logger = Logger.getLogger("org.netbeans.modules.javafx.debugger.breakpoints"); // NOI18N

    private JavaFXDebuggerImpl        debugger;
    private SourcePath           engineContext;
    private boolean                 started = false;
    private Session                 session;
    private BreakpointsReader       breakpointsReader;


    public BreakpointsEngineListener (ContextProvider lookupProvider) {
        debugger = (JavaFXDebuggerImpl) lookupProvider.lookupFirst 
            (null, JavaFXDebugger.class);
        engineContext = lookupProvider.lookupFirst(null, SourcePath.class);
        session = lookupProvider.lookupFirst(null, Session.class);
        debugger.addPropertyChangeListener (
            JavaFXDebugger.PROP_STATE,
            this
        );
        breakpointsReader = PersistenceManager.findBreakpointsReader();
    }
    
    protected void destroy () {
        debugger.removePropertyChangeListener (
            JavaFXDebugger.PROP_STATE,
            this
        );
        DebuggerManager.getDebuggerManager ().removeDebuggerListener (
            DebuggerManager.PROP_BREAKPOINTS,
            this
        );
        removeBreakpointImpls ();
    }
    
    public String[] getProperties () {
        return new String[] {"asd"};
    }

    public void propertyChange (java.beans.PropertyChangeEvent evt) {
        if (debugger.getState () == JavaFXDebugger.STATE_RUNNING) {
            if (started) return;
            started = true;
            createBreakpointImpls ();
            DebuggerManager.getDebuggerManager ().addDebuggerListener (
                DebuggerManager.PROP_BREAKPOINTS,
                this
            );
        }
        if (debugger.getState () == JavaFXDebugger.STATE_DISCONNECTED) {
            removeBreakpointImpls ();
            started = false;
            DebuggerManager.getDebuggerManager ().removeDebuggerListener (
                DebuggerManager.PROP_BREAKPOINTS,
                this
            );
        }
    }
    
    public void actionPerformed (Object action) {
//        if (action == ActionsManager.ACTION_FIX)
//            fixBreakpointImpls ();
    }

    public void breakpointAdded (final Breakpoint breakpoint) {
        final boolean[] started = new boolean[] { false };
        if (Thread.holdsLock(debugger.LOCK)) {
            createBreakpointImpl (breakpoint);
            return ;
        } // Otherwise:
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                synchronized (debugger.LOCK) {
                    synchronized (started) {
                        started[0] = true;
                        started.notify();
                    }
                    createBreakpointImpl (breakpoint);
                }
            }
        });
        if (!EventQueue.isDispatchThread()) { // AWT should not wait for debugger.LOCK
            synchronized (started) {
                if (!started[0]) {
                    try {
                        started.wait();
                    } catch (InterruptedException iex) {}
                }
            }
        }
    }    

    public void breakpointRemoved (final Breakpoint breakpoint) {
        final boolean[] started = new boolean[] { false };
        if (Thread.holdsLock(debugger.LOCK)) {
            removeBreakpointImpl (breakpoint);
            return ;
        } // Otherwise:
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                synchronized (debugger.LOCK) {
                    synchronized (started) {
                        started[0] = true;
                        started.notify();
                    }
                    removeBreakpointImpl (breakpoint);
                }
            }
        });
        if (!EventQueue.isDispatchThread()) { // AWT should not wait for debugger.LOCK
            synchronized (started) {
                if (!started[0]) {
                    try {
                        started.wait();
                    } catch (InterruptedException iex) {}
                }
            }
        }
    }
    

    public Breakpoint[] initBreakpoints () {return new Breakpoint [0];}
    public void initWatches () {}
    public void sessionAdded (Session session) {}
    public void sessionRemoved (Session session) {}
    public void watchAdded (Watch watch) {}
    public void watchRemoved (Watch watch) {}
    public void engineAdded (DebuggerEngine engine) {}
    public void engineRemoved (DebuggerEngine engine) {}


    // helper methods ..........................................................
    
    private HashMap<Breakpoint, BreakpointImpl> breakpointToImpl = new HashMap<Breakpoint, BreakpointImpl>();
    
    private void createBreakpointImpls () {
        Breakpoint[] bs = DebuggerManager.getDebuggerManager ().getBreakpoints ();
        int i, k = bs.length;
        for (i = 0; i < k; i++)
            createBreakpointImpl (bs [i]);
    }
    
    private void removeBreakpointImpls () {
        Breakpoint[] bs = DebuggerManager.getDebuggerManager ().getBreakpoints ();
        int i, k = bs.length;
        for (i = 0; i < k; i++)
            removeBreakpointImpl (bs [i]);
    }
    
    public void fixBreakpointImpls () {
        Iterator<BreakpointImpl> i = breakpointToImpl.values ().iterator ();
        while (i.hasNext ())
            i.next ().fixed ();
    }

    private synchronized void createBreakpointImpl (Breakpoint b) {
        if (breakpointToImpl.containsKey (b)) return;
        if (b instanceof LineBreakpoint) {
            breakpointToImpl.put (
                b,
                new LineBreakpointImpl (
                    (LineBreakpoint) b,
                    breakpointsReader,
                    debugger,
                    session,
                    engineContext
                )
            );
        } else
        if (b instanceof ExceptionBreakpoint) {
            breakpointToImpl.put (
                b,
                new ExceptionBreakpointImpl (
                    (ExceptionBreakpoint) b,
                    debugger,
                    session
                )
            );
        } else
        if (b instanceof MethodBreakpoint) {
            breakpointToImpl.put (
                b,
                new MethodBreakpointImpl (
                    (MethodBreakpoint) b,
                    debugger,
                    session
                )
            );
        } else
        if (b instanceof FieldBreakpoint) {
            breakpointToImpl.put (
                b,
                new FieldBreakpointImpl (
                    (FieldBreakpoint) b,
                    debugger,
                    session
                )
            );
        } else
        if (b instanceof ThreadBreakpoint) {
            breakpointToImpl.put (
                b,
                new ThreadBreakpointImpl (
                    (ThreadBreakpoint) b,
                    debugger,
                    session
                )
            );
        } else
        if (b instanceof ClassLoadUnloadBreakpoint) {
            breakpointToImpl.put (
                b,
                new ClassBreakpointImpl (
                    (ClassLoadUnloadBreakpoint) b,
                    debugger,
                    session
                )
            );
        }
        logger.finer("BreakpointsEngineListener: created impl "+breakpointToImpl.get(b)+" for "+b);
    }

    private synchronized void removeBreakpointImpl (Breakpoint b) {
        BreakpointImpl impl = breakpointToImpl.get (b);
        if (impl == null) return;
        logger.finer("BreakpointsEngineListener: removed impl "+impl+" for "+b);
        impl.remove ();
        breakpointToImpl.remove (b);
    }
}