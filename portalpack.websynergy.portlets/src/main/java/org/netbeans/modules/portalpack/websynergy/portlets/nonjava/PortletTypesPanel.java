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
package org.netbeans.modules.portalpack.websynergy.portlets.nonjava;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.portalpack.websynergy.portlets.nonjava.api.NonJavaPortletBuilder;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author satyaranjan
 */
public class PortletTypesPanel implements WizardDescriptor.Panel, ChangeListener {

    private final List/*<ChangeListener>*/ listeners = new ArrayList();
    private PortletTypesPanelGUI component;
    private List<NonJavaPortletBuilder> builders;
    private WizardDescriptor wizard;

    public PortletTypesPanel(List<NonJavaPortletBuilder> builders) {
        this.builders = builders;
    }

    public Component getComponent() {
        if (component == null) {
            component = new PortletTypesPanelGUI(builders);
            component.addChangeListener(this);
        }
        
        return component;
    }

    public HelpCtx getHelp() {

        return HelpCtx.DEFAULT_HELP;
    }

    public boolean isValid() {

        NonJavaPortletBuilder builder = component.getPortletBuilder();
        if (builder == null) {
            wizard.putProperty("WizardPanel_errorMessage",
                NbBundle.getMessage(PortletTypesPanel.class, "NO_PORTLET_TYPE_SELECTED")); // NOI18N

            return false;
        }
        wizard.putProperty("WizardPanel_errorMessage", ""); // NOI18N

        return true;
    }

    public void readSettings(Object settings) {

        wizard = (WizardDescriptor) settings;

        if (component == null) {
            getComponent();
        }
    }

    public void storeSettings(Object settings) {

        if (!isValid()) {
        }
        NonJavaPortletBuilder builder = component.getPortletBuilder();
        wizard.putProperty(NonJavaPortletConstants.PORTLET_BUILDER, builder);

    }

    public synchronized void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }

    public synchronized void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    protected void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        List templist;
        synchronized (this) {
            templist = new ArrayList(listeners);
        }
        Iterator it = templist.iterator();
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(e);
        }
    }

    public void stateChanged(ChangeEvent e) {
        fireChange();
    }
}
