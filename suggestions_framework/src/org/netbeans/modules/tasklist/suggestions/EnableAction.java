/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;

import java.io.Serializable;
import java.awt.MenuShortcut;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.MenuListener;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JRadioButtonMenuItem;

import javax.swing.event.*;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenu;

import org.openide.NotifyDescriptor;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.Workspace;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;
import org.openide.awt.*;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.XMLDataObject;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.suggestions.settings.ManagerSettings;

/**
 * Let the re-enable disabled suggestion types
 * <p>
 * <b>XXX Note - this class is no longer used. Remove it as soon as
 *  the "Edit Suggestions..." functionality is "complete".</b>
 * <p>
 *
 * @author  Tor Norbye
 */
public final class EnableAction extends CallableSystemAction
    implements Presenter.Menu {

    public EnableAction() {
    }

    protected boolean asynchronous() {
        return false;
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    
    /* @return Returns localized name of this action */
    public String getName() {
        return NbBundle.getMessage(EnableAction.class,
				   "EnableAction"); // NOI18N
    }

    /* Returns a submneu that will present this action in a Menu.
    * @return the JMenuItem representation for this action
    */
    public JMenuItem getMenuPresenter () {
	JMenu mainItem = new JMenuPlus();
        Actions.setMenuText(mainItem, getName(), true);
        //mainItem.setIcon (SystemAction.get(
	//		   EnableAction.class).getIcon());
        //HelpCtx.setHelpIDString (mainItem,
	//			 EnableAction.class.getName());
        mainItem.addMenuListener(new MainItemListener());
        return mainItem;
    }

    /* Returns a submneu that will present this action in a PopupMenu.
    * @return the JMenuItem representation for this action
    */
    public JMenuItem getPopupPresenter() {
	JMenu mainItem = new JMenuPlus();
        Actions.setMenuText(mainItem, getName(), true); 
        //HelpCtx.setHelpIDString (mainItem,
	//			 EnableAction.class.getName());
        mainItem.addMenuListener(new MainItemListener());
        return mainItem;
    }

    /** Presentation in toolbar? */
    /* Not yet implemented
    public java.awt.Component getToolbarPresenter () {
        ExplorerPanel ep = new ExplorerPanel ();
        ep.add (new ChoiceView ());
        ep.getExplorerManager ().setRootContext (getRecentNode ());
        
        return ep;
    }
    */
    
    public void performAction () {
        // all functionality is accomplished by menu listeners
    }

    // innerclasses .......................................................
    
    /** Listens to selecting of main item and expands it to the
     * submenu of exiting and new modes
     */
    private static final class MainItemListener
        implements MenuListener, ActionListener {

        public void menuCanceled (MenuEvent e) {
        }

        public void menuDeselected (MenuEvent e) {
            JMenu menu = (JMenu)e.getSource();
            menu.removeAll();
        }

        public void menuSelected (MenuEvent e) {
            JMenu menu = (JMenu)e.getSource();

	    // Add the disabled types
            int n = 0;
            SuggestionTypes types = SuggestionTypes.getDefault();
            
            Iterator it = types.getAllTypes().iterator();
            while (it.hasNext()) {
                SuggestionType type = (SuggestionType) it.next();
                if (false == ManagerSettings.getDefault().isEnabled(type.getName())) {
                    String category = type.getLocalizedName();
                    menu.add(createMenuItem(category, type));
                    n++;
                }
            }
            if (n == 0) {
        		JMenuItem item = createMenuItem(
                    NbBundle.getMessage(EnableAction.class, "Empty"), null); // NOI18N
                item.setEnabled(false);
                menu.add(item);
            }
        }

        // Property I attach suggestion types to on menu items
        private final static String TYPE = "type"; // NOI18N
        
        private JMenuItem createMenuItem(final String category,
                                         SuggestionType type) {
	    JMenuItem curMenuItem = new JMenuItem(category);
	    curMenuItem.addActionListener(this);
            if (type != null) {
                curMenuItem.putClientProperty(TYPE, type); // NOI18N
            }
	    return curMenuItem;
        }

        // Select the given category
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            SuggestionType type = (SuggestionType)(item.getClientProperty(TYPE));
            assert type != null;
            SuggestionManagerImpl manager =
                (SuggestionManagerImpl)SuggestionManager.getDefault();
            manager.setEnabled(type.getName(), true, false);
        }
    }
}
