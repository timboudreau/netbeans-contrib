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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.openide.awt.Actions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;


/**
 * Changes the state of the view (tree/flat).
 * 
 * @author tl
 */
public class AsListAction extends AbstractAction implements Presenter.Toolbar,
PropertyChangeListener {
    private static final long serialVersionUID = 1;

    /** view for this action */
    protected UserTaskView utv;
    
    public AsListAction(UserTaskView view) {
        this.utv = view;
        utv.addPropertyChangeListener(UserTaskView.PROP_STATE, this);
        updateTexts();
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/tasklist/usertasks/" + // NOI18N
                "actions/asList.png"))); // NOI18N
    }
    
    public void actionPerformed(ActionEvent ev) {
    }

    public Component getToolbarPresenter() {
        final JToggleButton tb = new JToggleButton(
                (Icon) getValue(SMALL_ICON));
        tb.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                utv.setState(tb.isSelected() ?  
                        UserTaskView.State.TREE : UserTaskView.State.FLAT);
            }
        });
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                tb.setToolTipText((String) AsListAction.this.getValue(NAME));
                tb.setSelected(utv.getState() == UserTaskView.State.TREE);
            }
        });
        tb.setSelected(utv.getState() == UserTaskView.State.TREE);
        tb.setToolTipText((String) getValue(NAME));
        tb.setFocusable(false);
        return tb;
    }
    
    /**
     * Updates NAME and SHORT_DESCRIPTION. 
     */
    private void updateTexts() {
        String txt = NbBundle.getMessage(AsListAction.class,
                utv.getState() == UserTaskView.State.FLAT ? 
                "AsTree" : "AsList"); //NOI18N
        putValue(NAME, txt);
        putValue(SHORT_DESCRIPTION, txt);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateTexts();
    }
}
