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
 */
/*
 * PopupStatusDisplayer.java
 *
 * Created on December 21, 2003, 6:57 PM
 */

package org.netbeans.modules.statuspopup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.awt.StatusDisplayer;
import org.openide.windows.WindowManager;

/** An implementation of StatusDisplayer that uses a popup
 *
 * @author  Tim Boudreau
 */
public final class PopupStatusDisplayer extends StatusDisplayer {
    private HashSet listeners = new HashSet();
    private NbTranslucentStatusDisplay lbl = null;
    static PopupStatusDisplayer instance = null;
    /** Creates a new instance of PopupStatusDisplayer */
    public PopupStatusDisplayer() {
        System.err.println("Creating an instance of status displayer");
        instance = this;
        attachToMainWindow();
    }
    
    public static synchronized StatusDisplayer getInstance() {
        if (instance == null) {
            instance = new PopupStatusDisplayer();
        }
        return instance;
    }
    
    private void attachToMainWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            private boolean attached = false;
            private boolean removed = false;
            private int tries = 0;
            private long time = System.currentTimeMillis();
            public void run() {
                if (WindowManager.getDefault().getMainWindow() == null || (System.currentTimeMillis() - time) < 4000) {
                    SwingUtilities.invokeLater(this);
                    Thread.currentThread().yield();
                    return;
                }
                
                tries++;

                if (!removed) {
                    JFrame f = (JFrame) WindowManager.getDefault().getMainWindow();
                    Component[] c = f.getContentPane().getComponents();
                    for (int i=0; i < c.length; i++) {
                        if ("statusLine".equals(c[i].getName())) {
                            f.getContentPane().remove(c[i]);
                            ((JComponent)f.getContentPane()).putClientProperty("originalStatus", c[i]);
                            removed = true;
                            time = System.currentTimeMillis();
                            if (!attached) {
                                attached = true;
                                JComponent inst = (JComponent)((JFrame) WindowManager.getDefault().getMainWindow()).getRootPane().getGlassPane();
                                inst.setVisible(true);
                                lbl = new NbTranslucentStatusDisplay();
                                inst.add(lbl);
                            }
                        }
                    }
                }
                
                if (!removed && tries < 1000) {
                    SwingUtilities.invokeLater(this);
                }
            }
        });
    }
    
    void detachFromMainWindow() {
        System.err.println(" Detaching from main window");
        if (lbl != null && lbl.getParent() != null) {
            lbl.getParent().remove(lbl);
            instance = null;
            JFrame f = (JFrame) WindowManager.getDefault().getMainWindow();
            JComponent c = (JComponent) ((JComponent) f.getContentPane()).getClientProperty("originalStatus");
            if (c != null) {
                f.getContentPane().add(c, BorderLayout.SOUTH);
            }
        }
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    public String getStatusText() {
        if (lbl != null) {
            return lbl.getText();
        } else {
            return "";
        }
    }
    
    public void removeChangeListener(javax.swing.event.ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    private void fire() {
        if (listeners.size() == 0) {
            return;
        }
        ChangeListener[] l = null;
        synchronized (listeners) {
            l = new ChangeListener[listeners.size()];
            l = (ChangeListener[]) listeners.toArray(l);
        }
        if (l.length > 0) {
            ChangeEvent ce = new ChangeEvent(this);
            for (int i=0; i < l.length; i++) {
                l[i].stateChanged(ce);
            }
        }
    }
    
    public void setStatusText(String text) {
        System.err.println("Status text set to " + text);
        if (lbl != null) {
            final String txt = text;
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        lbl.setText(txt);
                        System.err.println("Label parent is " + lbl.getParent());
                        System.err.println("Label ancestor " + lbl.getTopLevelAncestor());
                        System.err.println("Label visible " + lbl.isShowing() + " bounds: " + lbl.getBounds());
                        fire();
                    }
                });
            } else {
                lbl.setText(text);
                System.err.println("Label parent is " + lbl.getParent());
                System.err.println("Label ancestor " + lbl.getTopLevelAncestor());
                System.err.println("Label visible " + lbl.isShowing() + " bounds: " + lbl.getBounds());
                fire();
            }
        }
    }
    
}
