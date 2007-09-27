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
/*
 * SimpleWizardInfo.java
 *
 * Created on March 4, 2005, 9:46 PM
 */

package org.netbeans.spi.wizard;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import javax.swing.JComponent;

/**
 * Provides information about a simple wizard.  Wraps a
 * WizardPanelProvider and provides a connection to the instance of
 * SimpleWizard created for it, acting as the WizardController for
 * calls to WizardPanelProvider.createPanel().
 */
final class SimpleWizardInfo implements WizardController {
    private WeakReference wizard = null;
    private final String[] descriptions;
    private final String[] steps;
    final Boolean[] knownCanProceed;
    final Boolean[] knownCanFinish;
    private static String UNINIT = "Uninitialized";
    private String problem = UNINIT;
    private final String title;
    private final WizardPanelProvider provider;

    SimpleWizardInfo (WizardPanelProvider provider) {
        this (provider.title, provider.steps, provider.descriptions, provider);
    }
    
    /**
     * Create an instance of Info, which will provide panels for a simple,
     * non-branching wizard, passing a localized title, a list of steps
     * and descriptions.
     */
    protected SimpleWizardInfo (String title, String[] steps, String[] descriptions, WizardPanelProvider provider) {
        if (steps == null) {
            throw new NullPointerException ("Null steps");
        }
        if (descriptions == null) {
            throw new NullPointerException ("Null descriptions");
        }
        this.steps = steps;
        this.descriptions = descriptions;
        if (new HashSet(Arrays.asList(steps)).size() < steps.length) {
            throw new IllegalArgumentException ("Duplicate ID: " + Arrays.asList(steps));
        }
        if (descriptions.length != steps.length) {
            if (steps.length != descriptions.length + 1 && !Wizard.UNDETERMINED_STEP.equals(steps[steps.length-1])) {
                throw new IllegalArgumentException ("Steps and descriptions " +
                        "array lengths not equal: " + Arrays.asList(steps) + ":"
                        + Arrays.asList(descriptions));
            }
        }
        knownCanProceed = new Boolean [steps.length];
        knownCanFinish = new Boolean [steps.length];
        this.title = title;
        this.provider = provider;
    }


    final void setWizard (SimpleWizard wizard) {
        this.wizard = new WeakReference(wizard);
    }

    final SimpleWizard getWizard() {
        return wizard != null ? (SimpleWizard) wizard.get() : null;
    }

    /**
     * Create a panel that represents a named step in the wizard.
     * This method will be called exactly <i>once</i> in the life of 
     * a wizard.  The panel should retain the passed settings Map, and
     * add/remove values from it as the user enters information, calling
     * <code>setProblem()</code> and <code>setCanFinish()</code> as
     * appropriate in response to user input.
     * 
     * @param id The name of the step, as supplied in the constructor
     * @param settings A Map containing settings from earlier steps in
     *   the wizard
     * @return A JComponent
     */
    protected JComponent createPanel (String id, Map settings) {
        return provider.createPanel(this, id, settings);
    }

    /**
     * Instantiate whatever object (if any) the wizard creates from its
     * gathered data.
     */
    protected Object finish (Map settings) throws WizardException {
        return provider.finish (settings);
    }

    /**
     * The method provides a chance to call setProblem() or setCanFinish() when
     * the user re-navigates to a panel they've already seen - in the case 
     * that the user pressed the Previous button and then the Next button.
     * <p>
     * The default implementation does nothing, which is sufficient for 
     * most implementations.  If whether this panel is valid or not could
     * have changed because of changed data from a previous panel,
     * you may want to override this method to ensure validity and canFinish
     * are set correctly.
     * <p>
     * This method will <i>not</i> be called when a panel is first instantiated - 
     * <code>createPanel()</code> is expected to set validity and canFinish
     * appropriately.
     * <p>
     * The settings Map passed to this method will always be the same 
     * Settings map instance that was passed to <code>createPanel()</code>
     * when the panel was created.
     */
    protected void recycleExistingPanel (String id, Map settings, JComponent panel) {
        provider.recycleExistingPanel(id, this, settings, panel);
    }

    private int index() {
        SimpleWizard wizard = getWizard();
        if (wizard != null) {
            return wizard.currentIndex();
        } else {
            return 0;
        }
    }

    /**
     * Set whether or not the contents of this panel are valid.  When
     * user-entered information in a panel changes, call this method as
     * appropriate.
     */
    public final void setProblem (String value) {
        String old = problem;
        this.problem = value;
        fire();
        knownCanProceed[index()] = problem == null ? Boolean.TRUE : Boolean.FALSE;
    }

    private boolean canFinish = false;

    /**
     * Set whether or not the Finish button should be enabled.  Will only
     * affect the state of the Finish button if <code>isValid()</code> is
     * true.
     */
    public final void setCanFinish (boolean value) {
        if (canFinish != value) {
            canFinish = value;
            fire();
            knownCanFinish[index()] = value ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    final String getTitle() {
        return title;
    }

    final void update() {
        int idx = index();
        boolean change = false;
        if (knownCanFinish[idx] != null) {
            boolean known = knownCanFinish[idx].booleanValue();
            if (known != canFinish) {
                change = true;
                canFinish = known;
            }
        }
        if (change) {
            fire();
        }
    }

    final void fire() {
        Wizard wiz = getWizard();
        if (wiz != null) {
            getWizard().fireNavigability();
        }
    }

    final boolean isValid() {
        return getProblem() == null;
    }

    final boolean canFinish() {
        return isValid() && canFinish;
    }

    String[] getDescriptions() {
        return descriptions;
    }

    String[] getSteps() {
        return steps;
    }

    final String getProblem() {
        return problem;
    }
    
    public boolean equals (Object o) {
        if (o.getClass() == getClass()) {
            SimpleWizardInfo info = (SimpleWizardInfo) o;
            return Arrays.equals(info.descriptions, descriptions) &&
                   Arrays.equals(info.steps, steps) &&
                   info.title.equals(title);
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        int result = 0;
        for (int i=0; i < steps.length; i++) {
            result += (steps[i].hashCode() * (i+1)) ^ 31;
        }
        return result + title.hashCode();
    }    
}