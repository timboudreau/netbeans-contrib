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
package org.netbeans.modules.erlang.platform.ui;

import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import org.netbeans.modules.erlang.platform.api.RubyInstallation;

import org.netbeans.modules.erlang.platform.api.RubyPlatformManager;
import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;


/**
 * Based on options panel in stripwhitespace module by Andrei Badea
 *
 * @author Tor Norbye
 */
public class RubyOptionsAdvancedPanel extends AdvancedOption {
    public String getTooltip() {
        return getDisplayName();
    }

    public String getDisplayName() {
        return NbBundle.getMessage(RubyOptionsAdvancedPanel.class, "LBL_OptionsPanelName");
    }

    public OptionsPanelController create() {
        return new Controller();
    }

    private static final class Controller extends OptionsPanelController {
        private RubyHomeOptionsPanel component;

        public JComponent getComponent(Lookup masterLookup) {
            if (component == null) {
                component = new RubyHomeOptionsPanel();
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
                        component.setRuby(RubyPlatformManager.getDefaultPlatform().getInterpreter());
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
                        RubyInstallation config = RubyInstallation.getInstance();
                        config.setRuby(component.getRuby());
                    }
                });
        }
    }
}
