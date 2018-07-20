/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.ant.freeform.customcommands;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class NewCommandWizardPanel implements WizardDescriptor.Panel {

    private NewCommandVisualPanel component;
    private String[] likelyCommandNames;

    public NewCommandWizardPanel(String[] likelyCommandNames) {
        this.likelyCommandNames = likelyCommandNames;
    }
    
    public Component getComponent() {
        if (component == null) {
            component = new NewCommandVisualPanel(this, likelyCommandNames);
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx("org.netbeans.modules.ant.freeform.samples.custom-commands");
    }
    
    public boolean isValid() {
        return component.getCommand().length() > 0 &&
                component.getDisplayName().length() > 0 &&
                component.getMenu().length() > 0 &&
                component.getPosition() >= 0;
    }
    
    private final ChangeSupport cs = new ChangeSupport(this);
    public final void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }
    public final void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }
    final void fireChangeEvent() {
        cs.fireChange();
    }
    
    public void readSettings(Object settings) {}
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        d.putProperty("command", component.getCommand()); // NOI18N
        d.putProperty("displayName", component.getDisplayName() + " {0,choice,0#File|1#\"{1}\"|1<Files}"); // XXX I18N
        d.putProperty("menu", component.getMenu()); // NOI18N
        d.putProperty("position", new Integer(component.getPosition())); // NOI18N
    }
    
}

