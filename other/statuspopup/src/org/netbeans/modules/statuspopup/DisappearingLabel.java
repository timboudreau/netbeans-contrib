/*
 * TranslucentLabel.java
 *
 * Created on September 23, 2000, 2:54 PM
 */

package org.netbeans.modules.statuspopup;

import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author  Tim Boudreau
 * @version 0.2
 */

class DisappearingLabel extends JLabel implements Timeoutable {//JTextArea implements Timeoutable {
    private long timeout = 4000;
    private long baseTimeout = 4000;
    private long delayPerChar = 150;
    private boolean registered = false;
    protected static final String EmptyString = new String("");
    
    /** Creates new TranslucentLabel */
    public DisappearingLabel() {
        setForeground(java.awt.Color.yellow);
        setFocusable(false);
    }
    
    
    public synchronized void setText(String value) {
        if (!getText().trim().equals(value.trim())) {
            TimeoutRegistry.ensureActive();
            super.setText(value);
            timeout = baseTimeout;
            long charTimeout = (delayPerChar * value.length());
            if (charTimeout > timeout) timeout = timeout + (charTimeout - timeout);
//            setVisible(!getText().trim().equals(EmptyString));
        }
    }
    
    private void register() {
        if (!registered) {
            registered = true;
            TimeoutRegistry.registerComponent(this);
        }
    }
    
    private void unregister() {
        if (registered) {
            TimeoutRegistry.unregisterComponent(this);
            registered = false;
        }
    }
    
    public void addNotify() {
        super.addNotify();
        register();
    }
    
    public void removeNotify() {
        super.removeNotify();
        unregister();
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public Object getTimerPollArg() {
        Object result = getText();
        if (result.equals(EmptyString)) result = null;
        return result;
    }
    
    public void doTimeout() {
        super.setText(EmptyString);
        if (getParent() != null) {
            getParent().repaint(100);
            setVisible(false);
        }
    }
    
    public boolean canDoTimeout(final Object arg) {
        return (!getText().equals(arg));
    }
}
