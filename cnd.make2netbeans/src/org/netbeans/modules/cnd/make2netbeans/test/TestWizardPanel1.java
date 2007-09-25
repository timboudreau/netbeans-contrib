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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.make2netbeans.test;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * The first wizard panel
 * @author Andrey Gubichev
 */
public class TestWizardPanel1 implements WizardDescriptor.Panel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Component component;

    /**
     * Get the visual component for the panel. The component
     * is kept separate. This can be more efficient: if the wizard is created
     * but never displayed, or not all panels are displayed, it is better to
     * create only those which really need to be visible.
     * @return a visual component that displays this panel
     */
    public Component getComponent() {
        if (component == null) {
            component = new TestVisualPanel1();
        }
        return component;
    }

    /**
     *
     * @return  a help context for the action
     */
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     *
     * @return  true, if it is always OK to press Next or Finish, then:
     */
    public boolean isValid() {
        return true;
    }

    /**
     *
     * add listener
     */
    public final void addChangeListener(ChangeListener l) {
    }

    /**
     *
     * remove listener
     */
    public final void removeChangeListener(ChangeListener l) {
    }

    /**
     * Normally the settings object will be the WizardDescriptor
     */
    public void readSettings(Object settings) {
    }

    /**
     * Normally the settings object will be the WizardDescriptor
     */
    public void storeSettings(Object settings) {
    }
}