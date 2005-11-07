/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.importcruncher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsCategory.PanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * GUI option to set preferences.
 * @author Jesse Glick
 */
public final class Option extends AdvancedOption {
    
    public Option() {
    }

    public String getDisplayName() {
        return NbBundle.getMessage(Option.class, "Option.displayName");
    }

    public String getTooltip() {
        return NbBundle.getMessage(Option.class, "Option.toolTip");
    }

    public OptionsCategory.PanelController create() {
        return new Controller();
    }
    
    private static final class Controller extends OptionsCategory.PanelController implements ActionListener {
        
        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        private final OptionPanel panel = new OptionPanel();
        private boolean changed;
        
        Controller() {
            panel.breakup.addActionListener(this);
            panel.eliminateFqns.addActionListener(this);
            panel.eliminateWildcards.addActionListener(this);
            panel.importNestedClasses.addActionListener(this);
            panel.sort.addActionListener(this);
        }

        public void update() {
            panel.breakup.setEnabled(Prefs.get(Prefs.SORT));
            panel.breakup.setSelected(Prefs.get(Prefs.BREAKUP));
            panel.eliminateFqns.setSelected(Prefs.get(Prefs.NO_FQNS));
            panel.eliminateWildcards.setSelected(Prefs.get(Prefs.NO_WILDCARDS));
            panel.importNestedClasses.setSelected(Prefs.get(Prefs.IMPORT_NESTED_CLASSES));
            panel.sort.setSelected(Prefs.get(Prefs.SORT));
        }

        public void applyChanges() {
            Prefs.set(Prefs.BREAKUP, panel.breakup.isSelected());
            Prefs.set(Prefs.NO_FQNS, panel.eliminateFqns.isSelected());
            Prefs.set(Prefs.NO_WILDCARDS, panel.eliminateWildcards.isSelected());
            Prefs.set(Prefs.IMPORT_NESTED_CLASSES, panel.importNestedClasses.isSelected());
            Prefs.set(Prefs.SORT, panel.sort.isSelected());
        }

        public void cancel() {}

        public boolean isValid() {
            return true;
        }

        public boolean isChanged() {
            return changed;
        }

        public JComponent getComponent(Lookup masterLookup) {
            return panel;
        }

        public HelpCtx getHelpCtx() {
            return HelpCtx.DEFAULT_HELP;
        }

        public void addPropertyChangeListener(PropertyChangeListener l) {
            pcs.addPropertyChangeListener(l);
        }

        public void removePropertyChangeListener(PropertyChangeListener l) {
            pcs.removePropertyChangeListener(l);
        }

        public void actionPerformed(ActionEvent e) {
            if (!changed) {
                changed = true;
                pcs.firePropertyChange(OptionsCategory.PanelController.PROP_CHANGED, false, true);
            }
            panel.breakup.setEnabled(panel.sort.isSelected());
        }
        
    }
    
}
