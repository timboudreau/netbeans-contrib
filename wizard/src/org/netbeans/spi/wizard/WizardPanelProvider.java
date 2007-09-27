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
 * PanelProvider.java
 *
 * Created on March 5, 2005, 7:25 PM
 */

package org.netbeans.spi.wizard;

import java.util.Map;
import javax.swing.JComponent;

/**
 * A simple interface for providing a fixed set of panels for a wizard.
 * To use, simply implement <code>createPanel()</code> to create the
 * appropriate UI component for a given step (a unique String ID - one of the ones passed
 * in the constructor in the <code>steps</code> array), and implement
 * <code>finish()</code> to do whatever should be done when the wizard is
 * finished.
* <p>
 * To control whether the Next/Finish buttons are enabled, components 
 * created in <code>createPanel()</code> should call methods on the <code>
 * WizardController</code> passed.  The created panels should listen on the
 * UI components they create, updating the settings Map when the user changes
 * their input.
 * <p>
 * Super-simple one-pane wizard example - if the checkbox is checked, the user
 * can continue:
 * <pre>
 * public class MyProvider extends WizardPanelProvider {
 *    public MyProvider() {
 *       <font color="gray">//here we pass a localized title for the wizard, 
 *       //the ID of the one step it will have, and a localized description
 *       //the wizard can show for that one step</font>
 *       super ("Click the box", "click", "Click the checkbox");
 *    }
 *    protected abstract JComponent createPanel (final WizardController controller, String id, final Map settings) {
 *       <font color="gray">//A quick sanity check</font>
 *       assert "click".equals (id);
 *       <font color="gray">//Remember this method will only be called <i>once</i> for any panel</font>
 *       final JCheckBox result = new JCheckBox();
 *       result.addActionListener (new ActionListener() {
 *          public void actionPerformed (ActionEvent ae) {
 *             <font color="gray">//Typically you want to write the result of some user 
 *             //action into the settings map as soon as they do it </font>
 *             settings.put ("boxSelected", result.isSelected() ? Boolean.TRUE : Boolean.FALSE);
 *             if (result.isSelected()) {
 *                controller.setProblem(null);
 *             } else {
 *                controller.setProblem("The box is not checked");
 *             }
 *             controller.setCanFinish(true); <font color="gray">//won't matter if we called setProblem() with non-null</font>
 *          }
 *       });
 *       return result;
 *    }
 *
 *    protected abstract Object finish (Map settings) throws WizardException {
 *       <font color="gray">//if we had some interesting information (Strings a user put in a 
 *       //text field or something, we'd generate some interesting object or
 *       //create some files or something here</font>
 *       return null;
 *    }
 * }
 * </pre>
 *
 * @author Tim Boudreau
 */
public abstract class WizardPanelProvider {
    final String title;
    final String[] descriptions;
    final String[] steps;
    
    /**
     * Create a WizardPanelProvider.  The passed array of steps and descriptions
     * will be used as IDs and localized descriptions of the various steps in
     * the wizard.  Use this constructor (which passes not title) for sub-wizards
     * used in a <code>WizardBranchController</code>, where the first pane 
     * will determine the title, and the titles of the sub-wizards will never be 
     * shown.
     * @param steps A set of unique IDs identifying each step of this wizard.  Each
     *   ID must occur only once in the array of steps.
     *   
     * @param descriptions A set of human-readable descriptions corresponding
     *  1:1 with the unique IDs passed as the <code>steps</code> parameter
     */
    protected WizardPanelProvider (String[] steps, String[] descriptions) {
        this (null, steps, descriptions);
    }
    
    /**
     * Create a WizardPanelProvider with the provided title, steps and 
     * descriptions.  The <code>steps</code> parameter are unique IDs of 
     * panels, which will be passed to <code>createPanel</code> to create
     * panels for various steps in the wizard, as the user navigates it.
     * The <code>descriptions</code> parameter is a set of localized descriptions
     * that can appear in the Wizard to describe each step.
     * @param title A human readable title for the wizard dialog
     * @param steps An array of unique IDs for the various panels of this 
     *  wizard
     * @param descriptions An array of descriptions corresponding 1:1 with the
     *  unique IDs.  These must be human readable, localized strings.
     */
    protected WizardPanelProvider (String title, String[] steps, String[] descriptions) {
        this.title = title;
        this.steps = steps;
        this.descriptions = descriptions;
    }
    
    /**
     * Convenience constructor to create a WizardPanelProvider which has only
     * one step to it.  Mainly useful for initial steps in a <code>WizardBranchController</code>.
     * @param title A human readable title for the wizard dialog
     * @param singleStep The unique ID of the only step this wizard has
     * @param singleDescription The human-readable description of what the user
     *  should do in the one step of this one-step wizard or sub-wizard
     */
    protected WizardPanelProvider (String title, String singleStep, String singleDescription) {
        this (title, new String[] {singleStep}, new String[] {singleDescription});
    }
    
    /**
     * Create a panel that represents a named step in the wizard.
     * This method will be called exactly <i>once</i> in the life of 
     * a wizard.  The panel should retain the passed settings Map, and
     * add/remove values from it as the user enters information, calling
     * <code>setProblem()</code> and <code>setCanFinish()</code> as
     * appropriate in response to user input.
     * 
     * @param controller - the object which controls whether the
     *  Next/Finish buttons in the wizard are enabled, and what instructions
     *  are displayed to the user if they are not
     * @param id The name of the step, one of the array of steps passed in
     *  the constructor
     * @param settings A Map containing settings from earlier steps in
     *   the wizard.  It is safe to retain a reference to this map and put
     *   values in it as the user manipulates the UI;  the reference should
     *   be refreshed whenever this method is called again.
     * @return A JComponent that should be displayed in the center of the
     *  wizard
     */
    protected abstract JComponent createPanel (WizardController controller, String id, Map settings);
    
    /**
     * Instantiate whatever object (if any) the wizard creates from its
     * gathered data.
     * @param settings The settings map, now fully populated with all settings needed
     *  to complete the wizard (this method will only be called if 
     *  <code>setProblem(null)</code> and <code>setCanFinish(true)</code> have
     *  been called on the <code>WizardController</code> passed to 
     *  <code>createPanel()</code>.  
     */
    protected abstract Object finish (Map settings) throws WizardException;

    /**
     * The method provides a chance to call setProblem() or setCanFinish() when
     * the user re-navigates to a panel they've already seen - in the case 
     * that the user pressed the Previous button and then the Next button.
     * <p>
     * The default implementation does nothing, which is sufficient for 
     * most implementations.  If whether this panel is valid or not could
     * have changed because of changed data from a previous panel, or it
     * displays data entered on previous panes which may have changed,
     * you may want to override this method to ensure validity and canFinish
     * are set correctly, and that the components have the correct text.
     * <p>
     * This method will <i>not</i> be called when a panel is first instantiated - 
     * <code>createPanel()</code> is expected to set validity and canFinish
     * appropriately.
     * <p>
     * The settings Map passed to this method will always be the same 
     * Settings map instance that was passed to <code>createPanel()</code>
     * when the panel was created.
     */
    protected void recycleExistingPanel (String id, WizardController controller, Map settings, JComponent panel) {
        //do nothing
    }
    
    private Wizard wizard;
    /**
     * Create a Wizard for this PanelProvider.  The instance created by this
     * method is cached and the same instance will be returned on subsequent
     * calls.
     */
    public final Wizard createWizard() {
        if (wizard == null) {
            wizard = new SimpleWizard (this);
        }
        return wizard;
    }
    
}
