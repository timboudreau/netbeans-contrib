/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.vcscore.ui.views;

import java.io.*;
import org.openide.util.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.CallableSystemAction;
import org.openide.explorer.*;
import org.openide.nodes.*;
import org.openide.awt.JMenuPlus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JMenu;


/**
 * Subclass of TopComponent that is used within the vcs mode.
 * @author  mkleint
 */
public class VcsExplorerPanel extends org.openide.explorer.ExplorerPanel {

    
    static final long serialVersionUID = 5519624846009571015L;
    
    /** Creates new VcsExplorerPanel */
    public VcsExplorerPanel() {
        super();
        putClientProperty("PersistenceType", "Never"); //NOI18N
    }

/*    public void addDisplayer(PersistentCommandDisplayer displ) {
        displayer = displ;
        add(displ.getComponent());
    }
    
    public PersistentCommandDisplayer getEqualDisplayer(File file, Class type,  Object comparisonData) {
        if (displayer == null) return null;
        if (displayer.equalDisplayedData(file, type, comparisonData)) {
            return displayer;
        }
        return null;
    }
    
    public PersistentCommandDisplayer getDisplayer() {
        return displayer;
    }
  */  
    /** Get the system actions which will appear in
     * the popup menu of this component.
     * <p>Subclasses are encouraged to override this method to specify
     * their own sets of actions.
     * <p>Remember to call the super method when overriding and add your actions
     * to the superclass' ones (in some order),?
     * because the default implementation provides support for standard
     * component actions like save, close, and clone.
     * @return system actions for this component
 */
    public SystemAction[] getSystemActions() {
        SystemAction[] retValue;
        retValue = super.getSystemActions();
        return retValue;
/*        SystemAction[] all = new SystemAction[retValue.length + 1];
        all[0] = new JTCAction();
        for (int i=0; i < retValue.length; i++) {
            all[i + 1] = retValue[i];
        }
        return all;
 */
    }  

        /**
         * Disable serialization.
         * @return null
         */
        protected Object writeReplace () throws java.io.ObjectStreamException {
            return null;
        }
    
        /** Called only when top component was closed so that now it is closed
         * on all workspaces in the system. The intent is to provide subclasses
         * information about TopComponent's life cycle across workspaces.
         * Subclasses will usually perform cleaning tasks here.
         */
        protected void closeNotify() {
            super.closeNotify();
        }
    
        protected void updateTitle() {
            //do nothing..
            String name = ""; // NOI18N
            ExplorerManager manager = getExplorerManager();
            if (manager != null) {
                Node n = manager.getRootContext();
                if (n != null) {
                    String nm = n.getDisplayName();
                    if (nm != null) {
                        name = nm;
                    }
                }
            }
        }
}
