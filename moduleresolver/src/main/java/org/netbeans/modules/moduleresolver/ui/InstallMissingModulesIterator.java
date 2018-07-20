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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.moduleresolver.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

// An example action demonstrating how the wizard could be called from within
// your code. You can copy-paste the code below wherever you need.
public final class InstallMissingModulesIterator implements WizardDescriptor.Iterator<WizardDescriptor> {

    private List<WizardDescriptor.Panel<WizardDescriptor>> panels = null;
    private String [] names;
    private int index;
    
    public static final String CHOSEN_ELEMENTS = "chosen-elements"; // NOI18N
    public static final String APPROVED_ELEMENTS = "approved-elements"; // NOI18N

    public InstallMissingModulesIterator () {
        panels = getPanels ();
        index = 0;
    }

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels () {
        if (panels == null) {
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>> ();
            panels.add (new DescriptionStep ());
            panels.add (new LicenseStep ());
            panels.add (new InstallStep ());
            names = new String [] {
                NbBundle.getMessage (InstallMissingModulesIterator.class, "DescriptionStep_Name"),
                NbBundle.getMessage (InstallMissingModulesIterator.class, "LicenseStep_Name"),
                NbBundle.getMessage (InstallMissingModulesIterator.class, "InstallStep_Name")
            };
            String[] steps = new String [panels.size ()];
            assert steps.length == names.length : "As same names as steps must be";
            int i = 0;
            for (WizardDescriptor.Panel p : panels) {
                Component c = p.getComponent ();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps [i] = c.getName ();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty ("WizardPanel_contentSelectedIndex", new Integer (i));
                    // Sets steps names for a panel
                    jc.putClientProperty ("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty ("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty ("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty ("WizardPanel_contentNumbered", Boolean.TRUE);
                }
                i ++;
            }
        }
        return panels;
    }

    public WizardDescriptor.Panel<WizardDescriptor> current () {
        assert panels != null;
        return panels.get (index);
    }

    public String name () {
        return names [index];
    }

    public boolean hasNext () {
        return index < panels.size () - 1;
    }

    public boolean hasPrevious () {
        return index > 0 && !(current () instanceof InstallStep);
    }

    public void nextPanel () {
        if (!hasNext ()) {
            throw new NoSuchElementException ();
        }
        index++;
    }

    public void previousPanel () {
        if (!hasPrevious ()) {
            throw new NoSuchElementException ();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener (ChangeListener l) {
    }

    public void removeChangeListener (ChangeListener l) {
    }
}
