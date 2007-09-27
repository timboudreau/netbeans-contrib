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
/*
 * ComponentMapper.java
 *
 * Created on May 22, 2004, 12:45 AM
 */

package org.netbeans.modules.legacymenus;

import java.awt.Component;
import javax.swing.Action;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.openide.awt.Actions;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.netbeans.swing.menus.spi.*;
import org.netbeans.swing.menus.api.*;
import org.openide.nodes.*;
import org.openide.cookies.*;
import org.openide.util.actions.*;
import org.openide.actions.*;
import org.openide.awt.JInlineMenu;
import org.openide.util.Utilities;

/**
 *
 * @author  Tim Boudreau
 */
public class ComponentMapper extends MenuTreeModel.ComponentProvider {
    
    /** Creates a new instance of ComponentMapper */
    public ComponentMapper(SfsMenuModel mdl) {
        super (mdl);
    }
    
    public JComponent createItemFor (Object node) {
        FileObject orig = (FileObject) node;
        
        FileObject resolved = resolve (node);
        
        Object o = findRepresentative (resolved);
        JComponent result = null;
        if (o instanceof JComponent) {
            result = (JComponent) o;
        }
        if (o instanceof Action) {
            result = new JMenuItem ((Action) o);
            prepareMenuItem ((JMenuItem) result, (Action) o);
        }
        if (o instanceof Presenter.Menu) {
            result = ((Presenter.Menu) o).getMenuPresenter();
        }
        
        if (result == null) { //XXX
            result = new JMenuItem (orig.getPath());
        }
        
        if (result instanceof JInlineMenu) {
            
            System.err.println("GOT THE BASTARD: " + ((JInlineMenu) result).getText());
            result = dissectJInlineMenu((JInlineMenu) result);
            result.putClientProperty ("alwaysSync", Boolean.TRUE);
        }
        
        if (result instanceof Actions.SubMenu) {
            System.err.println("Got an Actions.SubMenu");
            final Actions.SubMenu am = (Actions.SubMenu) result;
            result.addNotify();
            String ui = am.getUIClassID();
            if ("MenuItemUI".equals(ui)) {
                System.err.println("It thinks it is a Menu Item");
                result = new JMenuItem ();
                ((JMenuItem)result).setIcon (am.getIcon());
                ((JMenuItem)result).setText (am.getText());
                ((JMenuItem)result).setMnemonic(am.getMnemonic());
                ((JMenuItem)result).setDisplayedMnemonicIndex(am.getDisplayedMnemonicIndex());
                ((JMenuItem)result).setEnabled(am.isEnabled());
            } else if ("MenuUI".equals(ui)) {
                System.err.println("It thinks it is a Menu");
                result = new JMenu();
                ((JMenu)result).setText(am.getText());
                ((JMenu)result).setIcon(am.getIcon());
                ((JMenu)result).setMnemonic(am.getMnemonic());
                ((JMenu)result).setDisplayedMnemonicIndex(am.getDisplayedMnemonicIndex());
                ((JMenu)result).setEnabled(am.isEnabled());
                Component[] c = am.getComponents();
                for (int i=0; i < c.length; i++) {
                    System.err.println("  installing " + c[i]);
                    ((JMenu)result).add(c[i]);
                }
            }
            result.putClientProperty ("alwaysSync", Boolean.TRUE);
            am.removeNotify();
        }
        
        result.putClientProperty ("origin", orig); //NOI18N
        return result;
    }
    
    private void prepareMenuItem (JMenuItem jmi, Action a) {
        String s = (String) a.getValue(Action.NAME);
        if (s != null) {
            prepareMenuItem (jmi, s);
        }
    }
        
    private void prepareMenuItem (JMenuItem jmi, String s) {
        int mn = s.indexOf('&');
        if (mn != -1) {
            s = Utilities.replaceString(s, "&", "");
            jmi.setText(s);
            if (mn != s.length()-1) {
                jmi.setMnemonic (s.charAt(mn));
                jmi.setDisplayedMnemonicIndex(mn);
            }
        }
    }
    
    private JMenu operacniStolek = new JMenu();
    {
        //So it thinks it's always on screen
        new CellRendererPane().add (operacniStolek);
    }
    private JComponent dissectJInlineMenu (JInlineMenu menu) {
        operacniStolek.add (menu);
        menu.addNotify();
        Component[] c = menu.getComponents();
        JComponent result;
        if (c.length == 1) {
            System.err.println("I stuck in my thumb and pulled out a ..." + c[0]);
            result = (JComponent) c[0];
        } else {
            JMenu jm = new JMenu();
            jm.setText (menu.getText());
            jm.setIcon (menu.getIcon());
            jm.setDisplayedMnemonicIndex(menu.getDisplayedMnemonicIndex());
            jm.setMnemonic(menu.getMnemonic());
            
            for (int i=0; i < c.length; i++) {
                if (c[i] instanceof JInlineMenu) {
                    System.err.println("I stuck in my thumb and pulled out a ... " + c[i]);
                    jm.add (dissectJInlineMenu((JInlineMenu) c[i]));
                } else {
                    jm.add (c[i]);
                }
            }
            result = jm;
        }
        
        operacniStolek.remove(menu);
        return result;
    }

    public JComponent syncStateOf (Object node, JComponent proxy) {
        if (proxy.getClass() == JMenu.class) {
            //Always rebuild JInlineMenus
            return createItemFor(node);
        }
        return proxy;
    }

    public void dispose (JComponent comp, Object node) {
        comp.putClientProperty ("origin", null); //NOI18N
    }
    
    private static FileObject resolve(Object o) {
        FileObject fo = (FileObject) o;
        if ("shadow".equals(fo.getExt())) {
            String file = (String) fo.getAttribute("originalFile"); //NOI18N
            //Recursively resolve nested shadows
            fo = resolve(Repository.getDefault().getDefaultFileSystem().findResource(file));
        }
        return fo;
    }    
    
    private Object findRepresentative (FileObject fo) {
        if (fo.isFolder()) {
            JMenu jmb = TreeMenuBar.createMenu (getModel(), fo);
            try {
                DataObject dob = DataObject.find (fo);
                prepareMenuItem (jmb, dob.getNodeDelegate().getDisplayName());
            } catch (Exception e) {
                jmb.setText (fo.getName()); //XXX
            }
            return jmb;
        }
        String instanceClass = (String) fo.getAttribute ("instanceClass");
        String ext = fo.getExt();
        if ("javax.swing.JSeparator".equals(instanceClass)) {
            return new JSeparator();
        }
        if ("instance".equals(ext)) {
            try {
                DataObject dob = DataObject.find (fo);
                InstanceCookie ic = (InstanceCookie) dob.getCookie(InstanceCookie.class);
                try {
                    return ic.instanceCreate();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (DataObjectNotFoundException donfe) {
                donfe.printStackTrace();
                return null;
            }
        }
        System.err.println ("Don't know what to do with " + fo);
        return null;
    }
}
