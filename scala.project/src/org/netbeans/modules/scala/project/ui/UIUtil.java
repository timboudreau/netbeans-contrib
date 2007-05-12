package org.netbeans.modules.scala.project.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * UI related utility methods for the module.
 *
 * @author Martin Krauskopf
 */
public final class UIUtil {
    
    private UIUtil() {}
    
    /**
     * Convenient class for listening on document changes. Use it if you do not
     * care what exact change really happened. {@link #removeUpdate} and {@link
     * #changedUpdate} just delegate to {@link #insertUpdate}. So everything
     * what is needed in order to be notified about document changes is to
     * override {@link #insertUpdate} method.
     */
    public abstract static class DocumentAdapter implements DocumentListener {
        public void removeUpdate(DocumentEvent e) { insertUpdate(null); }
        public void changedUpdate(DocumentEvent e) { insertUpdate(null); }
    }
    
}
