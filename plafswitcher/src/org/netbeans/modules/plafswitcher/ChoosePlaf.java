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
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.plafswitcher;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.*;

import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.awt.Actions;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/** Switch PLAF and remember the choice .
 * @author Petr Nejedly
 */
public final class ChoosePlaf extends CallableSystemAction {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -6L;

    /* Human presentable name of the action. This should be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String getName() {
        return NbBundle.getBundle(ChoosePlaf.class).getString("LAB_PLAF");
    }

    /* Help context where to find more about the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx() {
        return new HelpCtx (ChoosePlaf.class);
    }
    
    
    /* Returns a submenu with all available L&Fs
    * @return a JMenuItem for the submenu
    */
    public JMenuItem getMenuPresenter() {
        JMenu menu = new org.openide.awt.JMenuPlus();
        Actions.setMenuText(menu, getName(), true);
        menu.setIcon (getIcon());
        HelpCtx.setHelpIDString (menu, ChoosePlaf.class.getName ());

        // get all L&Fs
	Lookup.Result all = Lookup.getDefault().lookup(new Lookup.Template(LookAndFeel.class));
        for (Iterator it = all.allInstances().iterator(); it.hasNext(); ) {
            final LookAndFeel laf = (LookAndFeel)it.next();
            if (!laf.isSupportedLookAndFeel()) continue;

            JMenuItem item = new JMenuItem(laf.getName());
            item.setToolTipText(laf.getDescription());
            HelpCtx.setHelpIDString (item, ChoosePlaf.class.getName ());
            item.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        UIManager.setLookAndFeel(laf);
                        SwingUtilities.updateComponentTreeUI(WindowManager.getDefault().getMainWindow());
                        Module.setPlaf(laf.getClass().getName());
			DialogDisplayer.getDefault().notify(
			    new NotifyDescriptor.Message(
				NbBundle.getMessage(ChoosePlaf.class, "MSG_Restart"),
				NotifyDescriptor.INFORMATION_MESSAGE)
			);
                    } catch (UnsupportedLookAndFeelException e) {
                        // Can´t happen, we filter them, but ...
                        ErrorManager.getDefault().notify(e);
                    } catch (IOException ioe) {
                        ErrorManager.getDefault().notify(ioe);                        
                    }
                }
            });
            menu.add(item);
        }
        return menu;
    }

    /** Does nothing. This action is just a submenu placeholder. */
    public void performAction() {
    }
}
