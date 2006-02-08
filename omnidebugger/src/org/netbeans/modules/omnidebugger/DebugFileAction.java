/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.omnidebugger;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.openide.ErrorManager;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 * Action to debug one file.
 * @author Jesse Glick
 */
public class DebugFileAction extends AbstractAction implements ContextAwareAction, DynamicMenuContent {
    
    public DebugFileAction() {
        super("Omniscient Debug File"); // XXX I18N
    }

    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    public Action createContextAwareInstance(Lookup context) {
        return new ContextAction(context);
    }

    public JComponent[] getMenuPresenters() {
        ContextAction a = new ContextAction(Utilities.actionsGlobalContext());
        JMenuItem mi = new JMenuItem(a);
        if (a.isEnabled()) {
            mi.setText("Omniscient Debug " + a.selection.getNameExt()); // XXX I18N
        }
        return new JComponent[] {mi};
    }

    public JComponent[] synchMenuPresenters(JComponent[] items) {
        return getMenuPresenters();
    }
    
    private final class ContextAction extends AbstractAction {
        
        private final FileObject selection;
        
        public ContextAction(Lookup context) {
            DataObject d = (DataObject) context.lookup(DataObject.class);
            if (d != null) {
                selection = d.getPrimaryFile();
            } else {
                selection = (FileObject) context.lookup(FileObject.class);
            }
        }

        public boolean isEnabled() {
            return selection != null && Debug.enabled(selection);
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                Debug.start(selection);
            } catch (IOException x) {
                ErrorManager.getDefault().notify(x);
            }
        }

        public Object getValue(String key) {
            return DebugFileAction.this.getValue(key);
        }

    }
    
}
