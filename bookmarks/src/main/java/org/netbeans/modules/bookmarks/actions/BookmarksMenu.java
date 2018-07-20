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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.bookmarks.actions;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.Mnemonics;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.awt.Actions;
import org.openide.util.actions.Presenter;

import org.netbeans.modules.bookmarks.*;
/**
 * This is the item in the top level menu. Implements suprisingly
 * Presenter.Toolbar because that is the thing that is added to the
 * top level menu (unlike submenus which must implement Presenter.Menu).
 * @author David Strupl
*/
public class BookmarksMenu implements Presenter.Toolbar, HelpCtx.Provider {
    
    private static BookmarksMenu instance = new BookmarksMenu();
    private JMenu myToolbarPresenter = null;
    
    /**
     * @returns localized name for the action
     */
    public String getName() {
        return NbBundle.getBundle(BookmarksMenu.class).getString("Bookmarks");
    }
    
    /**
     * Method implementint interface HelpCtx.Provider.
     * The ID for the help is created from the class name of this class.
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(BookmarksMenu.class);
    }   

    /**
     * Method implementing interface Presenter.Toolbar. Creates two fixed
     * items for the menu: Add Bookmark and Manager Bookmarks. The rest
     * of this menu is created dynamically by MenuFromFolder.
     */
    public java.awt.Component getToolbarPresenter() {
        if (myToolbarPresenter != null) {
            return myToolbarPresenter;
        }
        JMenuItem addBookmarkItem = new JMenuItem();
        Actions.connect(addBookmarkItem, new AddBookmarkAction(), false);
        JMenuItem manageBookmarkItem = new JMenuItem();
        Actions.connect(manageBookmarkItem, new ManageBookmarksAction(), false);
        JMenuItem[] fixed = new JMenuItem[] {
            addBookmarkItem,
            manageBookmarkItem,
        };
        myToolbarPresenter = new MenuFromFolder(BookmarkServiceImpl.BOOKMARKS_FOLDER, fixed).getMenu();
        Mnemonics.setLocalizedText(myToolbarPresenter, getName());
        return myToolbarPresenter;
    }   

    /** 
     * To be called from the layer file.
     */
    public static BookmarksMenu getInstance() {
        return instance;
    }
}
