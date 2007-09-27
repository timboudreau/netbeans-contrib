/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.erd.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.ErrorManager;
import org.openide.util.Mutex;
import org.openide.util.RequestProcessor;

/**
 * A class providing support for running synchronous (in the event dispathing
 * thread) and asynchronous (outside the EDT) actions. Multiple
 * actions can be run at the same time, switching between synchronous and
 * asynchronous ones. A progress panel is displayed for asynchronous actions.
 *
 * <p>A typical use case is running an asynchronous action with a progress dialog.
 * For that just create an {@link #AsynchronouosAction} and send it to the {@link #invoke} method.</p>
 *
 * <p>A more complex use case is mixing actions: first you need to run an asynchronous
 * action, the a synchronous one (but in certain cases only) and then another
 * asynchronous one, showing and hiding the progress panel as necessary.</p>
 *
 * @author Andrei Badea
 */
public class EventRequestProcessor {

    private static final ErrorManager LOGGER = ErrorManager.getDefault().getInstance("org.netbeans.modules.j2ee.persistence.util"); // NOI18N
    private static final boolean LOG = LOGGER.isLoggable(ErrorManager.INFORMATIONAL);

    /**
     * The list of currently executed actions. Static because of the tests.
     */
    List<Action> actions;

    /**
     * The progress panel displayed for asynchronous actions. Static because of the tests.
     */
    ProgressPanel progressPanel;

    private int currentActionIndex;

    public EventRequestProcessor() {
    }

    public void invoke(Collection<Action> actions) {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("This method must be called in the event thread."); // NOI18N
        }
        if (this.actions != null) {
            throw new IllegalStateException("The invoke() method is running."); // NOI18N
        }

        this.actions = new ArrayList(actions);
        this.currentActionIndex = 0;

        try {
            invokeImpl();
        } finally {
            this.actions = null;
        }
    }

    private void invokeImpl() {
        assert SwingUtilities.isEventDispatchThread();

        progressPanel = new ProgressPanel();

        ProgressHandle progressHandle = ProgressHandleFactory.createHandle(null);
        JComponent progressComponent = ProgressHandleFactory.createProgressComponent(progressHandle);

        progressHandle.start();
        progressHandle.switchToIndeterminate();

        final Throwable[] exceptions = new Throwable[1];

        try {
            RequestProcessor.Task task = RequestProcessor.getDefault().create(new Runnable() {
                public void run() {
                    try {
                        invokeActionsUntilThreadSwitch();
                    } catch (Throwable t) {
                        exceptions[0] = t;
                    } finally {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                progressPanel.close();
                            }
                        });
                    }
                }
            });

            while (currentActionIndex < actions.size()) {
                invokeActionsUntilThreadSwitch();

                if (exceptions[0] != null) {
                    if (exceptions[0] instanceof RuntimeException) {
                        throw (RuntimeException)exceptions[0];
                    } else {
                        throw new RuntimeException(exceptions[0].getMessage(), exceptions[0]);
                    }
                }

                if (currentActionIndex < actions.size()) {
                    // more actions to run in the RequestProcessor
                    task.schedule(0);
                    progressPanel.open(progressComponent);
                }
            }
        } finally {
            progressHandle.finish();
            progressPanel = null;
        }
    }

    private void invokeActionsUntilThreadSwitch() {
        boolean isEventThread = SwingUtilities.isEventDispatchThread();

        for (; currentActionIndex < actions.size(); currentActionIndex++) {
            Action action = actions.get(currentActionIndex);
            if (!action.isEnabled()) {
                if (LOG) {
                    LOGGER.log("Skipping " + action); // NOI18N
                }
                continue;
            }
            if (action.getRunInEventThread() != isEventThread) {
                break;
            }
            if (LOG) {
                LOGGER.log("Running " + action); // NOI18N
            }
            if (!isEventThread) {
                final String message = action.getMessage();
                if (message != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            progressPanel.setText(message);
                        }
                    });
                }
            }
            action.run();
        }
    }

    public interface Action extends Runnable {

        public boolean getRunInEventThread();

        public boolean isEnabled();

        public String getMessage();
    }

    public static abstract class SynchronousAction implements Action {

        public boolean getRunInEventThread() {
            return true;
        }

        public boolean isEnabled() {
            return true;
        }

        public String getMessage() {
            return null;
        }
    }

    public static abstract class AsynchronousAction implements Action {

        public boolean getRunInEventThread() {
            return false;
        }

        public boolean isEnabled() {
            return true;
        }
    }
}
