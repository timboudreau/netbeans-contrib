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

package org.netbeans.swing.menus;

import java.awt.Color;
import java.util.Arrays;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import junit.framework.*;
import org.netbeans.swing.menus.spi.*;
import org.netbeans.swing.menus.api.*;
import org.netbeans.swing.menus.impl.*;
/**
 * A test for menu tree models
 *
 * @author Tim Boudreau
 */
public class TreeMenuBarTest extends TestCase {
    
    public TreeMenuBarTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
//        System.setProperty ("apple.laf.useScreenMenuBar","true");
        TestSuite suite = new TestSuite(TreeMenuBarTest.class);
        return suite;
    }
   
    TestingTreeModel mdl = null;
    TreeMenuBar bar = null;
    JFrame jf = null;
    public void setUp() {
        mdl = new TestingTreeModel ();
        bar = new TreeMenuBar (mdl);
        jf = new JFrame();
        jf.setJMenuBar(bar);
        jf.setBounds (20, 20, 400, 100);
        jf.show();
    }
    
    private void sleep() {
        try {
            Thread.currentThread().sleep (100);
            SwingUtilities.invokeAndWait (new Runnable() {
                public void run() {
                    System.currentTimeMillis();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            fail ("Exception while sleeping");
        }
    }
    
    public void tearDown() {
        jf.hide();
        jf.dispose();
    }
    
    public void testListeningToModel() {
        assertTrue ("TreeMenuBar should be listening to model if it is on screen", bar.listening);
        assertTrue ("TreeMenuBar is lying about listening on the model - it is not in the listener list", mdl.hasListener(bar.lis));
    }
    
    
    public void testInsert() throws Exception {
        Object root = mdl.getRoot();
        Object child = null;
        Object item1 = null;
        Object item2 = null;
        synchronized (bar) {
            root = mdl.getRoot();
            child = mdl.addChild(root, "Another menu", true);
            
            item1 = mdl.addChild(child, "A child item", false);
            item2 = mdl.addChild(child, "Another item", false);
        }
        
        sleep();
        sleep();
        Thread.currentThread().sleep (5000);
        
        final JMenu jm = (JMenu) bar.componentForItem (child);
        assertNotNull ("Menu bar should update immediately and have a menu for " + child, jm);
        
        assertTrue ("Menu title not correct", "Another menu".equals(jm.getText()));
        
        SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
                jm.doClick();
            }
        });

                
        sleep();
        Thread.currentThread().sleep (1000);
        assertTrue ("Menu should contain 2 items, not " + jm.getItemCount(), jm.getItemCount() == 2);
        
        assertTrue ("Popup menu should be showing", jm.isPopupMenuVisible());
        SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
                jm.getModel().setPressed(false);
                jm.getModel().setSelected(false);
                jm.setPopupMenuVisible(false);
            }
        });
        sleep();
        sleep();
        Thread.currentThread().sleep (2000);
        
        SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
                //Make sure the state of our menu is reset
                bar.getMenu(0).doClick();
            }
        });
        
        sleep();
        sleep();
        
        MyIcon icon = new MyIcon();
        
        mdl.setIcon (item1, icon);
        
        sleep();
        Thread.currentThread().sleep (1000);
        
        SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
                jm.doClick();
            }
        });
        sleep();
        sleep();
        Thread.currentThread().sleep (1000);
        
        assertTrue ("Icon should have been painted - it now is set on the action - the action was not updated", icon.wasPainted());
    }
    
    public void testRemove () throws Exception {
        Thread.currentThread().sleep(1000);
        Object root = mdl.getRoot();
        Object child = null;
        Object item1 = null;
        Object item2 = null;
        synchronized (bar) {
            root = mdl.getRoot();
            child = mdl.addChild(root, "Another menu", true);
            
            item1 = mdl.addChild(child, "A child item", false);
            item2 = mdl.addChild(child, "Another item", false);
        }
        
        sleep();
        sleep();
        Thread.currentThread().sleep (1000);
        
        final TreeNodeMenu menu = (TreeNodeMenu) bar.componentForItem (child);
        assertTrue ("Returned the wrong menu", "Another menu".equals(menu.getText()));
        
        
        assertTrue ("Menu should be listening to the tree model", menu.listeningToModel);
        assertTrue ("Menu is lying about listening to the tree model", mdl.hasListener(menu));
        assertTrue ("Menu should be showing", menu.isShowing());
        
        SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
                menu.doClick();
            }
        });        
        sleep();
        
        
        Thread.currentThread().sleep (500);
        
        
        JMenuItem jmi = (JMenuItem) menu.componentForItem(item1);
        
        System.out.println("ITEM 1 IS " + item1 + "@" + System.identityHashCode(item1));
        
        menu.dump();
        
        assertNotNull ("Menu should have a menu item for " + item1, jmi);
        
        assertTrue ("Menu item text not correct", "A child item".equals(jmi.getText()));
        sleep();
        
        
        SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
                //Make sure the state of our menu is reset
                bar.getMenu(0).doClick();
            }
        });
        sleep();
        
        mdl.remove (item1);
        
        sleep();
        sleep();
        Thread.currentThread().sleep (500);
        
        SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
                menu.doClick();
            }
        });
        sleep();
        sleep();
        
        assertFalse ("Item was removed, it should not be showing", jmi.isShowing());
        assertNull ("Item was removed, it should not have a parent", jmi.getParent());
        
        SwingUtilities.invokeAndWait (new Runnable() {
            public void run() {
                menu.getModel().setPressed(false);
                menu.getModel().setSelected(false);
                menu.setPopupMenuVisible(false);
            }
        });        
        
        sleep();
        sleep();
        
        mdl.remove(child);
        sleep();
        sleep();
        
        assertNull ("Menu should have been removed", menu.getParent());
        
    }
    
    private static class MyIcon implements Icon {
        
        public int getIconHeight() {
            return 16;
        }
        
        public int getIconWidth() {
            return 16;
        }
        
        private boolean painted = false;
        public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
            painted = true;
            g.setColor (Color.ORANGE);
            g.fillRect (x, y, 12, 12);
            g.setColor (Color.BLUE);
            g.drawRect (x, y, 12, 12);
        }
        
        public boolean wasPainted () {
            boolean wasPainted = painted;
            painted = false;
            return wasPainted;
        }
        
    }
   
    
}
