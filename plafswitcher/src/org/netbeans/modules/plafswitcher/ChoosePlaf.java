/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.plafswitcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.JMenuPlus;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;

/**
 * Switch PLAF and remember the choice.
 * @author Petr Nejedly
 */
public final class ChoosePlaf extends CallableSystemAction {

    /* Human presentable name of the action. This should be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String getName() {
        return NbBundle.getMessage(ChoosePlaf.class, "LAB_PLAF");
    }

    /* Help context where to find more about the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx() {
        return null;
    }
    
    /* Returns a submenu with all available L&Fs
    * @return a JMenuItem for the submenu
    */
    public JMenuItem getMenuPresenter() {
        JMenu menu = new JMenuPlus();
        Mnemonics.setLocalizedText(menu, getName());
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
                public void actionPerformed(ActionEvent evt) {
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
                        // Can't happen, we filter them, but ...
                        ErrorManager.getDefault().notify(e);
                    }
                }
            });
            menu.add(item);
        }
        return menu;
    }

    /** Does nothing. This action is just a submenu placeholder. */
    public void performAction() {
        assert false;
    }
    
}
