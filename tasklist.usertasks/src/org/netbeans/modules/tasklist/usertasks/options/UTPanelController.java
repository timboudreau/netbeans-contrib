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

package org.netbeans.modules.tasklist.usertasks.options;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * PanelController for the options.
 *
 * @author tl
 */
public class UTPanelController extends OptionsPanelController {

    private UTOptionsPanel panel = new UTOptionsPanel();

    /**
     * Creates a new instance of UTPanelController.
     */
    public UTPanelController() {
    }

    public JComponent getComponent(Lookup masterLookup) {
        return panel;
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        panel.removePropertyChangeListener(l);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        panel.addPropertyChangeListener(l);
    }

    public void update() {
        panel.update();
    }

    public boolean isValid() {
        UTUtils.LOGGER.fine("isValid"); // NOI18N
        return panel.isContentValid();
    }

    public boolean isChanged() {
        return panel.isChanged();
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void cancel() {
        panel.cancel();
    }

    public void applyChanges() {
        panel.applyChanges();
    }
    
}
