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
 * NbPopupFactory.java
 *
 * Created on December 22, 2003, 9:56 PM
 */

package org.netbeans.swing.dropshadow;

import java.awt.Component;
import java.awt.Point;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/** Custom PopupFactory implementation which provides popups with drop shadows
 * for menus, tooltips and other Swing classes which use PopupFactory to 
 * display popups.
 *
 * @author  Tim Boudreau   */
public class NbPopupFactory extends PopupFactory {
    private static final int offset = 5;
    private PopupFactory wrapped;

    private NbPopupFactory (PopupFactory wrapped) {
        this.wrapped = wrapped;
    }

    public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
        if (contents == null) {
            throw new IllegalArgumentException();
        }
        return NbLightweightPopup.getInstance (owner, contents, new Point(x,y));
    }
    
    public static boolean install() {
        PopupFactory pop = PopupFactory.getSharedInstance();
        if (pop instanceof NbPopupFactory) {
            return false;
        } else {
            PopupFactory nue = new NbPopupFactory(pop);
            PopupFactory.setSharedInstance(nue);
            return true;
        }
    }
    
    public static boolean uninstall() {
        PopupFactory pop = PopupFactory.getSharedInstance();
        if (pop instanceof NbPopupFactory) {
            PopupFactory old = ((NbPopupFactory)pop).wrapped;
            PopupFactory.setSharedInstance(old);
            return true;
        } else {
            return false;
        }
    }
}
