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

import javax.swing.Action;
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
        }
        if (o instanceof Presenter.Menu) {
            result = ((Presenter.Menu) o).getMenuPresenter();
        }
        
        if (result == null) { //XXX
            result = new JMenuItem (orig.getPath());
        }
        
        result.putClientProperty ("origin", orig); //NOI18N
        return result;
    }

    public JComponent syncStateOf (Object node, JComponent proxy) {
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
                jmb.setText (dob.getNodeDelegate().getDisplayName());
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
