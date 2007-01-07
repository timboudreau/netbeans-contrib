/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.actions.SaveAction;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 * Save.
 * 
 * @author tl
 */
public class UTSaveAction extends AbstractAction 
        implements PropertyChangeListener {    
    private DataObject n;
    
    /** 
     * Creates a new instance of UTSaveAction.
     * 
     * @param do_ looking here for SaveCookie
     */
    public UTSaveAction(DataObject do_) {
        this.n = do_;
        putValue(Action.NAME, SystemAction.get(SaveAction.class).
                getValue(NAME));
        putValue(SMALL_ICON, SystemAction.get(SaveAction.class).
                getValue(SMALL_ICON));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, 
                InputEvent.CTRL_MASK));
        n.addPropertyChangeListener(this);
        propertyChange(null);
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            SaveCookie c = n.getCookie(SaveCookie.class);
            c.save();
        } catch (IOException ex) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    NbBundle.getMessage(UTSaveAction.class, 
                    "ErrorSaving", // NOI18N
                    ex.getMessage()), 
                    NotifyDescriptor.ERROR_MESSAGE);
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        setEnabled(n.getCookie(SaveCookie.class) != null);
    }
}
