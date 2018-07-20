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

package org.netbeans.modules.tasklist.export;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.openide.util.actions.SystemAction;
import org.openide.util.actions.Presenter.Menu;

import org.openide.awt.JMenuPlus;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Export action which provides a pullright menu selecting the Export format
 * to use.
 *
 * @author Tor Norbye
 * @author Petr Kuzel, toolbar presenter
 */
public final class ExportAction extends CallableSystemAction implements Menu {

    static final long serialVersionUID = 1L;

    /**
     * Creates an instance
     */
    public ExportAction() {
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    update();
                }
            }
        );
        update();
    }
    
    /**
     * Updates the enabled/disabled state of this action.
     */
    public void update() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        setEnabled(tc instanceof ExportImportProvider);
    }
    
    protected boolean asynchronous() {
        return false;
    }

    public String getName() {
        return NbBundle.getMessage(ExportAction.class, "LBL_Export"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/core/exportAction.gif"; // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
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

                // <<<< from menu presenter
                TopComponent tc = WindowManager.
                    getDefault().getRegistry().getActivated();
                assert tc instanceof ExportImportProvider;

                ExportImportFormat translators[] = 
                    ((ExportImportProvider) tc).getExportFormats();
                assert translators != null;

                for (int i = 0; i < translators.length; i++) {
                    menu.add(createMenuItem(translators[i]));
                }
                // >>>> from menu presenter

                menu.show(original, original.getWidth(), 0);
            }
        });
        return original;
    }

    /* Returns a submenu that will present this action in a Menu.
    * @return the JMenuItem representation for this action
    */
    public JMenuItem getMenuPresenter() {
        JMenu mainItem = new JMenuPlus();
        Mnemonics.setLocalizedText(mainItem, getName());
        mainItem.setIcon(SystemAction.get(
            ExportAction.class).getIcon());
        HelpCtx.setHelpIDString(mainItem,
            ExportAction.class.getName());
        mainItem.addMenuListener(new MainItemListener());
        return mainItem;
    }

    /* Returns a submenu that will present this action in a PopupMenu.
    * @return the JMenuItem representation for this action
    */
    public JMenuItem getPopupPresenter() {
        JMenu mainItem = new JMenuPlus();
        Mnemonics.setLocalizedText(mainItem, getName());
        HelpCtx.setHelpIDString(mainItem,
            ExportAction.class.getName());
        mainItem.addMenuListener(new MainItemListener());
        return mainItem; 
    }

    public void performAction() {
        // all functionality is accomplished by menu listeners
    }

    final private static String PROPNAME = "translator"; // NOI18N

    private static JMenuItem createMenuItem(ExportImportFormat translator) {
        JMenuItem curMenuItem = new JMenuItem();
        Mnemonics.setLocalizedText(curMenuItem, translator.getName());
        curMenuItem.putClientProperty(PROPNAME, translator);
        curMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                TopComponent tc = WindowManager.
                    getDefault().getRegistry().getActivated();
                assert tc instanceof ExportImportProvider;
                
                JComponent jc = (JComponent) evt.getSource();
                ExportImportFormat translator =
                        (ExportImportFormat) jc.getClientProperty(PROPNAME);
                WizardDescriptor wd = translator.getWizard();
                Dialog d = DialogDisplayer.getDefault().createDialog(wd);
                d.setVisible(true);
                if (wd.getValue() == WizardDescriptor.FINISH_OPTION) {
                    translator.doExportImport((ExportImportProvider) tc, wd);
                }
            }
        });
        return curMenuItem;
    }

    // innerclasses .......................................................

    /** Listens to selecting of main item and expands it to the
     * submenu of exiting and new modes
     */
    private static final class MainItemListener implements MenuListener {

        /** Source of the events */
        private JMenu menu;
        
        public MainItemListener () {}

        public void menuCanceled(MenuEvent e) {
        }

        public void menuDeselected(MenuEvent e) {
            JMenu menu = (JMenu) e.getSource();
            menu.removeAll();
        }

        public void menuSelected(MenuEvent e) {
            this.menu = (JMenu) e.getSource();

            // Add the import filters to the menu

            // CRAP. I have to add lookup for these suckers already
            // since I can't include stuff in the submodules (usertasks)

            // Also note: the specific tasks which can be imported depends
            // on the window, right? (Well, the list really.)
            //   So perhaps I can just ask the list to provide it for me?
            //

            // missing common super interface for JPopupMenu and JMenu

            TopComponent tc = WindowManager.
                getDefault().getRegistry().getActivated();
            assert tc instanceof ExportImportProvider;
            
            ExportImportFormat[] translators = 
                ((ExportImportProvider) tc).getExportFormats();
            assert translators != null;
            
            for (int i = 0; i < translators.length; i++) {
                menu.add(createMenuItem(translators[i]));
            }
        }
    }
}
