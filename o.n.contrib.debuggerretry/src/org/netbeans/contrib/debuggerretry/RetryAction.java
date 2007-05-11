package org.netbeans.contrib.debuggerretry;

import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;
import org.netbeans.api.debugger.ActionsManager;
import org.netbeans.api.debugger.ActionsManagerListener;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * Simple action that combines the debugger's "pop topmost stack" and
 * "step into" actions, so that a developer can retry debugging a method.
 */
public final class RetryAction extends CallableSystemAction {
    private static final String icon = "org/netbeans/contrib/debuggerretry/PopReenter.gif"; // NOI18N
    
    @Override
    protected void initialize() {
        super.initialize();
        setEnabled(false);
        new Listener();
    }

    public String getName() {
        return NbBundle.getMessage(RetryAction.class, "CTL_RetryAction");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    @Override
    public String iconResource() {
        return icon;
    }
    
    @Override
    protected boolean asynchronous() {
        return false;
    }
    
    public void performAction() {
        ActionsManager am = getEngineActionsManager();
        if (am != null) {
            am.doAction(ActionsManager.ACTION_POP_TOPMOST_CALL);
            am.doAction(ActionsManager.ACTION_STEP_INTO);
        }
    }
    
    private static ActionsManager getEngineActionsManager() {
        DebuggerManager dm = DebuggerManager.getDebuggerManager();
        DebuggerEngine engine = dm.getCurrentEngine();
        return engine != null ? engine.getActionsManager() : null;
    }

    /** Listener class cloned from debuggercore's DebuggerAction class */
    class Listener extends DebuggerManagerAdapter implements ActionsManagerListener {
        
        private ActionsManager currentActionsManager;
        
        Listener () {
            DebuggerManager.getDebuggerManager ().addDebuggerListener (
                DebuggerManager.PROP_CURRENT_ENGINE,
                this
            );
            DebuggerManager.getDebuggerManager ().getActionsManager().addActionsManagerListener(
                ActionsManagerListener.PROP_ACTION_STATE_CHANGED,
                this
            );
            updateCurrentActionsManager ();
        }
        
        @Override
        public void propertyChange (PropertyChangeEvent evt) {
            updateCurrentActionsManager();
            final boolean en = isDebuggerEnabled();
            System.out.println(evt.getPropertyName() + " fired: debugger enabled=" + en);
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    RetryAction.this.setEnabled (en);
                }
            });
        }
        
        public void actionStateChanged (final Object action, final boolean enabled) {
            // ignore the enabled argument, check it with respect to the proper
            // actions manager.
            final boolean en = isDebuggerEnabled();
            //System.out.println(" action state changed: debugger enabled=" + en);
            if (SwingUtilities.isEventDispatchThread()) {
                RetryAction.this.setEnabled(en);
            } else {
                SwingUtilities.invokeLater (new Runnable () {
                    public void run () {
                        RetryAction.this.setEnabled (en);
                    }
                });
            }
        }
        
        public void actionPerformed (Object action) {}
        
        private void updateCurrentActionsManager () {
            ActionsManager newActionsManager = getEngineActionsManager ();
            if (currentActionsManager == newActionsManager) return;
            
            if (currentActionsManager != null)
                currentActionsManager.removeActionsManagerListener
                    (ActionsManagerListener.PROP_ACTION_STATE_CHANGED, this);
            if (newActionsManager != null)
                newActionsManager.addActionsManagerListener
                    (ActionsManagerListener.PROP_ACTION_STATE_CHANGED, this);
            currentActionsManager = newActionsManager;
        }
    
        private boolean isDebuggerEnabled() {
            ActionsManager manager = getEngineActionsManager();
            if (manager != null) {
                if (manager.isEnabled(ActionsManager.ACTION_STEP_OVER)) {
                    return true;
                }
            }
            manager = DebuggerManager.getDebuggerManager().getActionsManager();
            return manager.isEnabled(ActionsManager.ACTION_STEP_OVER);
        }
    }
}
