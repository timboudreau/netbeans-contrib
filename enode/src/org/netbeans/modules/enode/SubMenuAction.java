/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.enode;

import javax.swing.*;
import javax.naming.*;
import javax.naming.event.*;

import org.openide.ErrorManager;

/**
 * Special action serving as a wrapper for submenus added to the popup
 * menu.
 * @author David Strupl
 */
public class SubMenuAction extends AbstractAction implements org.openide.util.actions.Presenter.Popup, org.openide.util.actions.Presenter.Menu {

    /** Context from where the content of the submenu is taken.*/
    private Context context;
    /** Name used for the display of this context */
    private String name;
    
    /** Creates a new instance of SubMenuAction and remembers
     * the parameters in private variables.
     */
    public SubMenuAction(Context context, String name) {
        this.context = context;
        this.name = name;
    }
    
    /**
     * This action creates a submenu. So invoking directly this
     * action does not make any sense. This method throws
     * an IllegalStateException.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        throw new IllegalStateException("SubMenuAction should not be performed as action."); // NOI18N
    }
    
    /**
     * Method implementing interface Presenter.Menu.
     */
    public JMenuItem getMenuPresenter() {
        return getPopupPresenter();
    }
    
    /**
     * Method implementing interface Presenter.Popup.
     */
    public JMenuItem getPopupPresenter() {
        return createMenuForContext(context, name);
    }

    /**
     * Meat of this class. Walks through the context c and
     * adds all actions to a newly created JMenu. The JMenu
     * has as its text value of the parameter name.
     */
    private JMenu createMenuForContext(Context c, String name) {
        JMenu menu = new JMenu(); 
        menu.setText(name);
        try {
            NamingEnumeration en = c.listBindings(""); // NOI18N
            while (en.hasMoreElements()) {
                Binding b = (Binding)en.nextElement();
                Object obj = b.getObject();
                if (obj instanceof Action) {
                    if (obj instanceof org.openide.util.actions.Presenter.Popup) {
                        menu.add(((org.openide.util.actions.Presenter.Popup)obj).getPopupPresenter());
                    } else {
                        menu.add((Action)obj);
                    }
                }
                // general JComponents are inserted as they are
                if (obj instanceof JComponent) {
                    menu.add((JComponent)obj);
                }
                //
                if (obj instanceof Context) {
                    b.setRelative(true);
                    String n = b.getName();
                    // recurisvelly add submenus!
                    menu.add(createMenuForContext((Context)obj, n)); 
                }
            }
        } catch (NameNotFoundException nnfe) {
            // no problem
        } catch (NamingException ne) {
            ErrorManager.getDefault().notify(ne);
        }
        return menu;
    }
}
