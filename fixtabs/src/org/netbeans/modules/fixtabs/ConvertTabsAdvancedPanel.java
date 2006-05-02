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

package org.netbeans.modules.fixtabs;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;

/**
 *
 * @author Andrei Badea
 */
public class ConvertTabsAdvancedPanel extends AdvancedOption {

    public String getTooltip() {
        return getDisplayName();
    }

    public String getDisplayName() {
        return NbBundle.getMessage(ConvertTabsAdvancedPanel.class, "LBL_PanelName");
    }

    public OptionsPanelController create() {
        return new Controller();
    }

    private static final class Controller extends OptionsPanelController {

        private ConvertTabsOptionsComponent component;

        public JComponent getComponent(Lookup masterLookup) {
            if (component == null) {
                component = new ConvertTabsOptionsComponent();
            }
            return component;
        }

        public void removePropertyChangeListener(PropertyChangeListener l) {
        }

        public void addPropertyChangeListener(PropertyChangeListener l) {
        }

        public void update() {
            Mutex.EVENT.readAccess(new Runnable() {

                public void run() {
                    component.setHighlightingColor(ConvertTabsOptions.getDefault().getHighlightingColor());
                }
            });
        }

        public boolean isValid() {
            return true;
        }

        public boolean isChanged() {
            return false;
        }

        public HelpCtx getHelpCtx() {
            return HelpCtx.DEFAULT_HELP;
        }

        public void cancel() {
        }

        public void applyChanges() {
            Mutex.EVENT.readAccess(new Runnable() {

                public void run() {
                    ConvertTabsOptions.getDefault().setHighlightingColor(component.getHighlightingColor());
                }
            });
        }
    }
}
