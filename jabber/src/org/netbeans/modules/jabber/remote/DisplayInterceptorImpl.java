/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the Jabber module.
 * The Initial Developer of the Original Code is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.jabber.remote;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.PopupFactory;


/**
 * A DisplayInterceptor implementation that makes sure that the LocalManager
 * gets notified about every change to the window set, including displaying
 * heavyweight popups, and that the LocalManager can get into the painting
 * loop.
 *
 * @author  nenik
 */
public class DisplayInterceptorImpl extends org.openide.windows.DisplayInterceptor
                        implements ComponentListener, WindowListener {

    private LocalManager man = LocalManager.getDefault();
    private Set activeWindows = new HashSet();
    
    public DisplayInterceptorImpl() {
	installPopupWrapper();
    }
    
    public Graphics createGraphics(Graphics delegate, Component comp) {
	return man.createGraphicsFor((Window)comp, delegate);
    }

    public void windowCreated(Window win) {
        //remove stray listeners for reused windows
        win.removeWindowListener(this);
        win.removeComponentListener(this); 
        win.addWindowListener(this);
        win.addComponentListener(this);
        if (win.isShowing()) notifyWindowOpened(win);
    }
    
    private synchronized void notifyWindowOpened(Window w) {
        if (activeWindows.add(w)) {
            man.windowDisplayed(w);
        }
    }
    
    private synchronized void notifyWindowClosed(Window w) {
        if (activeWindows.remove(w)) {
            man.windowHidden(w);
        }
    }
    
    private synchronized void notifyWindowResized(Window w) {
        if (activeWindows.contains(w)) {
            man.windowResized(w);
        }
    }

    // ComponentListener impl, to forward important window events 
    public void componentResized(ComponentEvent e) {
        notifyWindowResized((Window)e.getComponent());
    }

    public void componentMoved(ComponentEvent e) {}

    public void componentShown(ComponentEvent e) {
        notifyWindowOpened((Window)e.getComponent());
        
    }
    public void componentHidden(ComponentEvent e) {
        notifyWindowClosed((Window)e.getComponent());
    }


    // window listener impl to secure the events    
    public void windowOpened(java.awt.event.WindowEvent e) {
        notifyWindowOpened(e.getWindow());
    }
    
    public void windowClosed(java.awt.event.WindowEvent e) {
        notifyWindowClosed(e.getWindow());
    }
    
    
    private void installPopupWrapper() {
        PopupFactory old = PopupFactory.getSharedInstance();
        PopupFactory.setSharedInstance(new PopupFactoryWrapper(old));
    }
    
    public void windowActivated(java.awt.event.WindowEvent e) {}    
    public void windowClosing(java.awt.event.WindowEvent e) {}
    public void windowDeactivated(java.awt.event.WindowEvent e) {}
    public void windowDeiconified(java.awt.event.WindowEvent e) {}
    public void windowIconified(java.awt.event.WindowEvent e) {}
    
    private static Method getComponentMethod;
    private static Field componentField;
    
    static {
        try {
            getComponentMethod = Popup.class.getDeclaredMethod("getComponent", null);
            getComponentMethod.setAccessible(true);
            componentField = Popup.class.getDeclaredField("component");
            componentField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private class PopupFactoryWrapper extends PopupFactory {
        private PopupFactory delegate;

	PopupFactoryWrapper(PopupFactory delegate) {
            this.delegate = delegate;
        }
        
        public Popup getPopup(Component owner, Component contents,
                          int x, int y) throws IllegalArgumentException {
            Popup delPopup = delegate.getPopup(owner, contents, x, y);
            try {
                Object comp = componentField.get(delPopup);
                if ((comp instanceof JWindow) && !(comp instanceof ReplacementWindow)) {
                    JWindow template = (JWindow)comp;
                    ReplacementWindow rep = new ReplacementWindow(template);
                    componentField.set(delPopup, rep);
                    
                    rep.setLocation(template.getLocation());
                    rep.getContentPane().add(contents, BorderLayout.CENTER);
                    contents.invalidate();
                    rep.pack();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return delPopup;
        }        
    }
    
    
    /**
     * A heavyweight popup replacement
     */
    class ReplacementWindow extends JWindow {
        ReplacementWindow(Window template) {
            super((Window)template.getParent());
            setFocusableWindowState(template.getFocusableWindowState());
            setName(template.getName());
        }

        public Graphics getGraphics() {
            return createGraphics(super.getGraphics(), this);
        }
        
	public void show() {
	    super.show();
	    this.pack();
            windowCreated(this);
	}

        public void hide() {
            notifyWindowClosed(this);
	    super.hide();
	}
    }   
}
