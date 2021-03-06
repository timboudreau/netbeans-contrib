/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.options;

import org.netbeans.modules.ada.project.options.AdaGeneralOptionsPanel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.ada.project.options.AdaOptions;
import org.netbeans.modules.ada.editor.formatter.ui.FormattingOptionsPanel;
import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 * @author Andrea Lucarelli
 */
public class AdaOptionsPanelController extends OptionsPanelController implements ChangeListener {

    private static final String TAB_FOLDER = "org.netbeans.modules.ada/options/"; // NOI18N
    private final AdaGeneralOptionsPanel generalOptionsPanel = new AdaGeneralOptionsPanel(null);
    private final FormattingOptionsPanel formattingOptionsPanel = new FormattingOptionsPanel();
    private final Collection<? extends AdvancedOption> options;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private Map<OptionsPanelController, AdvancedOption> controllers2Options;
    private JTabbedPane pane;
    private boolean changed;

    public AdaOptionsPanelController() {
        options = Lookups.forPath(TAB_FOLDER).lookupAll(AdvancedOption.class);
        generalOptionsPanel.addChangeListener(this);
    }

    private synchronized Map<OptionsPanelController, AdvancedOption> getControllers2Options() {
        if (controllers2Options == null) {
            controllers2Options = new LinkedHashMap<OptionsPanelController, AdvancedOption>();
            for (AdvancedOption o : options) {
                OptionsPanelController c = o.create();
                controllers2Options.put(c, o);
            }
        }

        return controllers2Options;
    }

    private Set<OptionsPanelController> getControllers() {
        return getControllers2Options().keySet();
    }

    @Override
    public void update() {
        for (OptionsPanelController c : getControllers()) {
            c.update();
        }

        generalOptionsPanel.setAdaDialects(getAdaOptions().getAdaDialects());
        generalOptionsPanel.setAdaRestrictions(getAdaOptions().getAdaRestrictions());
        generalOptionsPanel.setPkgSpecPrefix(getAdaOptions().getPkgSpecPrefix());
        generalOptionsPanel.setPkgBodyPrefix(getAdaOptions().getPkgBodyPrefix());
        generalOptionsPanel.setSeparatePrefix(getAdaOptions().getSeparatePrefix());
        generalOptionsPanel.setPkgSpecPostfix(getAdaOptions().getPkgSpecPostfix());
        generalOptionsPanel.setPkgBodyPostfix(getAdaOptions().getPkgBodyPostfix());
        generalOptionsPanel.setSeparatePostfix(getAdaOptions().getSeparatePostfix());
        generalOptionsPanel.setPkgSpecExt(getAdaOptions().getPkgSpecExt());
        generalOptionsPanel.setPkgBodyExt(getAdaOptions().getPkgBodyExt());
        generalOptionsPanel.setSeparateExt(getAdaOptions().getSeparateExt());
        
        changed = false;
    }

    @Override
    public void applyChanges() {
        for (OptionsPanelController c : getControllers()) {
            c.applyChanges();
        }

        getAdaOptions().setAdaDialects(generalOptionsPanel.getAdaDialects());
        getAdaOptions().setAdaRestrictions(generalOptionsPanel.getAdaRestrictions());
        getAdaOptions().setPkgSpecPrefix(generalOptionsPanel.getPkgSpecPrefix());
        getAdaOptions().setPkgBodyPrefix(generalOptionsPanel.getPkgBodyPrefix());
        getAdaOptions().setSeparatePrefix(generalOptionsPanel.getSeparatePrefix());
        getAdaOptions().setPkgSpecPostfix(generalOptionsPanel.getPkgSpecPostfix());
        getAdaOptions().setPkgBodyPostfix(generalOptionsPanel.getPkgBodyPostfix());
        getAdaOptions().setSeparatePostfix(generalOptionsPanel.getSeparatePostfix());
        getAdaOptions().setPkgSpecExt(generalOptionsPanel.getPkgSpecExt());
        getAdaOptions().setPkgBodyExt(generalOptionsPanel.getPkgBodyExt());
        getAdaOptions().setSeparateExt(generalOptionsPanel.getSeparateExt());

        changed = false;
    }

    @Override
    public void cancel() {
        for (OptionsPanelController c : getControllers()) {
            c.cancel();
        }
    }

    @Override
    public boolean isValid() {
        for (OptionsPanelController c : getControllers()) {
            if (!c.isValid()) {
                return false;
            }
        }

        return validateComponent();
    }

    @Override
    public boolean isChanged() {
        for (OptionsPanelController c : getControllers()) {
            if (c.isChanged()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
         if (pane == null) {
            pane = new JTabbedPane();
            pane.add(NbBundle.getMessage(AdaOptionsPanelController.class, "LBL_GeneralOPtions"), generalOptionsPanel);
            pane.add(NbBundle.getMessage(AdaOptionsPanelController.class, "LBL_FormattingPtions"), formattingOptionsPanel);
          	formattingOptionsPanel.load();
            for (Entry<OptionsPanelController, AdvancedOption> e : getControllers2Options().entrySet()) {
                OptionsPanelController controller = e.getKey();
                AdvancedOption option = e.getValue();
                pane.add(option.getDisplayName(), controller.getComponent(controller.getLookup()));
            }
        }
        return pane;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    private AdaOptions getAdaOptions() {
        return AdaOptions.getInstance();
    }

    public void stateChanged(ChangeEvent e) {
        changed();
    }

    private boolean validateComponent() {
        // errors

        // everything ok
        generalOptionsPanel.setError(" "); // NOI18N
        return true;
    }

    private void changed() {
        if (!changed) {
            changed = true;
            propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
