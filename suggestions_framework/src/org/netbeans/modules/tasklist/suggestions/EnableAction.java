/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tasklist.suggestions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.MenuListener;
import java.util.Iterator;

import javax.swing.event.*;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;
import org.openide.awt.*;
import org.netbeans.modules.tasklist.client.SuggestionManager;
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

    private static final long serialVersionUID = 1;

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
        Mnemonics.setLocalizedText(mainItem, getName());
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
        Mnemonics.setLocalizedText(mainItem, getName()); 
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
