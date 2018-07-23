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

package org.netbeans.modules.tasklist.suggestions.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.event.*;
import javax.swing.*;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;
import org.openide.awt.*;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.filter.AppliedFilterCondition;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.filter.FilterCondition;
import org.netbeans.modules.tasklist.filter.StringFilterCondition;


import org.netbeans.modules.tasklist.suggestions.*;


/**
 * Let the user choose one particular category from
 * pull down menu.
 *
 * @author Tor Norbye
 * @author Petr Kuzel, toolbar presenter
 */
public final class ShowCategoryAction extends CallableSystemAction
        implements Presenter.Menu {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }

    public boolean isEnabled() {
        // FIXME works only for the live suggestions view
        SuggestionsView view = SuggestionsView.getCurrentView();
        if (view != null) {
            return view.isShowing();
        }
        return false;
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/suggestions/showCategoryAction.gif"; // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }


    /* @return Returns localized name of this action */
    public String getName() {
        return NbBundle.getMessage(ShowCategoryAction.class,
                "ShowCategory"); // NOI18N
    }

    /* Returns a Component that presents the Action, that implements this
    * interface, in a ToolBar.
    * @return the Component representation for the Action
    */
    public Component getToolbarPresenter() {
        final Component original = super.getToolbarPresenter();
        AbstractButton ab = (AbstractButton) original;
        ab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPopupMenu menu = new JPopupMenu();

                // FIXME works only for the live suggestions view
                // XXX missing common super interface for JPopupMenu and JMenu
                // <<<< from menu presenter
                SuggestionsView view = SuggestionsView.getCurrentView();
                if (view == null) {
                    // INTERNAL ERROR
                    return;
                }

                String current = null;
                Filter filter = view.getFilter();
                if (view.isFiltered()) {
                    List aconditions = filter.getConditions();
                    if ((aconditions != null) && (aconditions.size() > 0)) {
                        AppliedFilterCondition acondition = (AppliedFilterCondition) aconditions.get(0);
                        if (acondition.getCondition() instanceof StringFilterCondition) {
                            current = ((StringFilterCondition) acondition.getCondition()).getConstant();
                        }
                    }
                }

                // Add All item
                String allDesc = NbBundle.getMessage(ShowCategoryAction.class,
                        "All"); // NOI18N
                menu.add(createMenuItem(allDesc, null, (current == null) ||
                        allDesc.equals(current)));

                // Add separator
                menu.addSeparator();

                Collection types = SuggestionTypes.getDefault().getAllTypes();
                Iterator it = types.iterator();
                while (it.hasNext()) {
                    SuggestionType type = (SuggestionType) it.next();
                    String category = type.getLocalizedName();
                    boolean isSelected = category.equals(current);
                    menu.add(createMenuItem(category, type, isSelected));
                }
                // >>>> from menu presenter

                menu.show(original, original.getWidth(), 0);
            }
        });
        return original;
    }

    /* Returns a submneu that will present this action in a Menu.
    * @return the JMenuItem representation for this action
    */
    public JMenuItem getMenuPresenter() {
        JMenu mainItem = new JMenuPlus();
        Actions.setMenuText(mainItem, getName(), true);
        //mainItem.setIcon (SystemAction.get(
        //		   ShowCategoryAction.class).getIcon());
        //HelpCtx.setHelpIDString (mainItem,
        //			 ShowCategoryAction.class.getName());
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
        //			 ShowCategoryAction.class.getName());
        mainItem.addMenuListener(new MainItemListener());
        return mainItem;
    }


    public void performAction() {
        // all functionality is accomplished by menu listeners
    }

    // Property I attach suggestion types to on menu items
    private final static String TYPE = "type"; // NOI18N

    private static JMenuItem createMenuItem(final String category,
                                     SuggestionType type,
                                     boolean isSelected) {
        JMenuItem curMenuItem = new JRadioButtonMenuItem(category,
                isSelected);
        curMenuItem.addActionListener(new ActionListener() {
            // Select the given category
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();
                SuggestionType type = (SuggestionType) (item.getClientProperty(TYPE));
                String category = null;
                if (type != null) {
                    category = type.getLocalizedName();
                }
                SuggestionsView view = SuggestionsView.getCurrentView();
                if (view == null) {
                    // INTERNAL ERROR
                    return;
                }

                SuggestionManagerImpl manager =
                        (SuggestionManagerImpl) SuggestionManager.getDefault();
                if (type == null) { // All
		  // TODO : boolean parameter? 
		  // view.setFilter(new SuggestionFilter("Simple"), false); // NOI18N
		  view.setFilter(new SuggestionFilter("Simple")); // NOI18N

                    manager.notifyFiltered((SuggestionList) view.getList(), null);
                    // Make sure you do this AFTER view.setFilter
                    view.notifyFiltered(null);
                } else {
                    AppliedFilterCondition cond =
		      new AppliedFilterCondition(
			    SuggestionImplProperties.PROP_CATEGORY,
                            new StringFilterCondition(StringFilterCondition.EQUALS, category));

                    List conditions = new ArrayList(1);
                    conditions.add(cond);
                    // If you ever construct more complicated filters, e.g.
                    // filters which allow multiple SuggestionProviders to
                    // be visible simultaneously, update the code in
                    // SuggestionManagerImpl which deals with the field
                    // named "unfiltered", since it makes an assumption that
                    // one and only one is visible at a time (unless there
                    // is no filtering in effect.)
                    Filter filter = new SuggestionFilter(category);
                    filter.setConditions(conditions);
		    // TODO : boolean parameter?
                    // view.setFilter(filter, true);
                    view.setFilter(filter);
                    manager.notifyFiltered((SuggestionList) view.getList(), type);
                    // Make sure you do this AFTER view.setFilter
                    view.notifyFiltered(type);
                }
            }
        });
        curMenuItem.putClientProperty(TYPE, type); // NOI18N
        return curMenuItem;
    }

    // innerclasses .......................................................

    /** Listens to selecting of main item and expands it to the
     * submenu of exiting and new modes
     */
    private static final class MainItemListener implements MenuListener  {

        public void menuCanceled(MenuEvent e) {
        }

        public void menuDeselected(MenuEvent e) {
            JMenu menu = (JMenu) e.getSource();
            menu.removeAll();
        }

        public void menuSelected(MenuEvent e) {
            JMenu menu = (JMenu) e.getSource();

            // XXX missing common super interface for JPopupMenu and JMenu
            SuggestionsView view = SuggestionsView.getCurrentView();
            if (view == null) {
                // INTERNAL ERROR
                return;
            }

            String current = null;
            Filter filter = view.getFilter();
            if (view.isFiltered()) {
                List conditions = filter.getConditions();
                if ((conditions != null) && (conditions.size() > 0)) {
                    FilterCondition condition = (FilterCondition) conditions.get(0);
                    if (condition instanceof StringFilterCondition) {
                        current = ((StringFilterCondition) condition).getConstant();
                    }
                }
            }

            // Add All item
            String allDesc = NbBundle.getMessage(ShowCategoryAction.class,
                    "All"); // NOI18N
            menu.add(createMenuItem(allDesc, null, (current == null) ||
                    allDesc.equals(current)));

            // Add separator
            menu.addSeparator();

            Collection types = SuggestionTypes.getDefault().getAllTypes();
            Iterator it = types.iterator();
            while (it.hasNext()) {
                SuggestionType type = (SuggestionType) it.next();
                String category = type.getLocalizedName();
                boolean isSelected = category.equals(current);
                menu.add(createMenuItem(category, type, isSelected));
            }
        }


    }
}
