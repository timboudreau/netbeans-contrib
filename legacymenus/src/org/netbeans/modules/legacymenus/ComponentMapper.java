/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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
            
//            System.err.println("GOT THE BASTARD: " + ((JInlineMenu) result).getText());
//            result = dissectJInlineMenu((JInlineMenu) result);
//            result.putClientProperty ("alwaysSync", Boolean.TRUE);
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
