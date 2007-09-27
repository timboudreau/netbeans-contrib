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
