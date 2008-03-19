/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.clearcase.options;

import org.netbeans.spi.options.OptionsPanelController;
import org.netbeans.modules.clearcase.ClearcaseModuleConfig;
import org.openide.util.Lookup;
import org.openide.util.HelpCtx;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.clearcase.Clearcase;

/**
 * Clearcase Options Controller.
 * 
 * @author Maros Sandor
 */
class ClearcaseOptionsController extends OptionsPanelController {
    
    private ClearcaseOptionsPanel panel;

    public void update() {
        setOdc(ClearcaseModuleConfig.getOnDemandCheckout());
        panel.taExecutable.setText(ClearcaseModuleConfig.getPreferences().get(ClearcaseModuleConfig.PROP_CLEARTOOL_EXECUTABLE, "cleartool"));
        panel.cbCheckinViewPrivate.setSelected(ClearcaseModuleConfig.getPreferences().getBoolean(ClearcaseModuleConfig.PROP_ADD_VIEWPRIVATE, true));
        panel.taLabelFormat.setText(ClearcaseModuleConfig.getPreferences().get(ClearcaseModuleConfig.PROP_LABEL_FORMAT, ""));
    }

    public void applyChanges() {
        if (!isValid()) return;
        ClearcaseModuleConfig.setOnDemandCheckout(getOdc());
        ClearcaseModuleConfig.getPreferences().put(ClearcaseModuleConfig.PROP_CLEARTOOL_EXECUTABLE, panel.taExecutable.getText().trim());        
        ClearcaseModuleConfig.getPreferences().putBoolean(ClearcaseModuleConfig.PROP_ADD_VIEWPRIVATE, panel.cbCheckinViewPrivate.isSelected());
        ClearcaseModuleConfig.getPreferences().put(ClearcaseModuleConfig.PROP_LABEL_FORMAT, panel.taLabelFormat.getText().trim());
        Clearcase.getInstance().getAnnotator().refresh();
    }

    public void cancel() {
    }

    public boolean isValid() {
        return true;
    }

    public boolean isChanged() {
        if (getOdc() != ClearcaseModuleConfig.getOnDemandCheckout()) return true;
        return false;
    }

    public ClearcaseModuleConfig.OnDemandCheckout getOdc() {
        if (panel.rbDisabled.isSelected()) return ClearcaseModuleConfig.OnDemandCheckout.Disabled;
        if (panel.rbUnreserved.isSelected()) return ClearcaseModuleConfig.OnDemandCheckout.Unreserved;
        if (panel.cbFallback.isSelected()) return ClearcaseModuleConfig.OnDemandCheckout.ReservedWithFallback;
        return ClearcaseModuleConfig.OnDemandCheckout.Reserved;
    }

    private void setOdc(ClearcaseModuleConfig.OnDemandCheckout odc) {
        panel.rbDisabled.setSelected(odc == ClearcaseModuleConfig.OnDemandCheckout.Disabled);
        panel.rbUnreserved.setSelected(odc == ClearcaseModuleConfig.OnDemandCheckout.Unreserved);
        panel.rbReserved.setSelected(odc == ClearcaseModuleConfig.OnDemandCheckout.Reserved || odc == ClearcaseModuleConfig.OnDemandCheckout.ReservedWithFallback);
        panel.cbFallback.setSelected(odc == ClearcaseModuleConfig.OnDemandCheckout.ReservedWithFallback);
    }
    
    public JComponent getComponent(Lookup lookup) {
        if (panel == null) {
            panel = new ClearcaseOptionsPanel(); 
        }
        return panel;
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(ClearcaseOptionsController.class);
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }
}
