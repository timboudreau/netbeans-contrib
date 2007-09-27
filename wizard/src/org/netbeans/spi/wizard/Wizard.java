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
 * Wizard.java
 *
 * Created on February 22, 2005, 2:18 PM
 */

package org.netbeans.spi.wizard;

import java.util.EventListener;
import java.util.Map;
import javax.swing.JComponent;

/**
 * <b>Note:</b>It is quite rare to need to directly implement this interface.
 * If you want to create a Wizard with a fixed set of steps, implement
 * {@link org.netbeans.spi.wizard.WizardPanelProvider WizardPanelProvider}
 * and call {@link org.netbeans.spi.wizard.WizardPanelProvider#createWizard
 * createWizard} on it.
 * <p>
 * If you have a wizard that has choice-points, where depending on what the
 * user chooses on one pane, the subsequent number or set of panes will change, implement
 * <code>{@link org.netbeans.spi.wizard.WizardBranchController WizardBranchController}</code> to supply different sub-wizards 
 * depending on the user's choices, and call {@link org.netbeans.spi.wizard.WizardBranchController#createWizard createWizard}
 * to get the result.  <code>{@link org.netbeans.spi.wizard.WizardBranchController WizardBranchController}</code>
 * will let you provide a choice of {@link org.netbeans.spi.wizard.WizardPanelProvider WizardPanelProvider}s
 * depending on what the user enters.  For most cases no more is needed.
 * <p>
 * To display any <code>Wizard</code> to the user, call 
 * {@link org.netbeans.api.wizard.WizardDisplayer#show WizardDisplayer.show 
 * (someWizard)}
 * <p>
 * The one case in which it may be necessary to implement this interface 
 * directly is if you have a Wizard that for some reason needs to disable
 * the <code>Prev</code> button (this is not a good idea from a usability
 * standpoint).  For all other cases, consider using one of
 * <code>WizardPanelProvider</code> or <code>WizardBranchController</code> - 
 * they are much simpler to work with.
 * <hr>
 * <p>
 * This class is the Wizard interface - a Wizard is a series of one or more steps represented
 * by panels in the user interface.  Each step is identified by a String ID.
 * For each ID, the <code>navigatedTo()</code> method supplies a component to
 * be displayed to the user.
 * <p>
 * Each panel may add settings to a Map, which is passed to <code>navigatedTo()</code>.
 * At the conclusion of a Wizard (if/when the user presses the <code>Finish</code>
 * button), the method <code>finish (Map settings)</code> is invoked, and the
 * Wizard may instantiate whatever it needs to and return that.  It is up to
 * the caller to do something with the return value.
 * <p>
 * Wizards should never make any changes to their environment except in the
 * <code>finish()</code> method - until that is called, they should simply
 * collect information.
 * <p>
 * The IDs of steps of the wizard should be returned by the method
 * <code>getAllIDs()</code>.  If a Wizard contains <i>branching</i>, such
 * that all of the steps cannot be determined, it should return an array
 * of Strings terminated with the special ID <code>UNDETERMINED_STEP</code>;
 * as the set of following steps becomes known, it should fire <code>
 * stepsChange()</code> to any registered WizardListeners.
 * @see WizardPanelProvider
 * @see WizardBranchController
 * @see org.netbeans.api.wizard.WizardDisplayer
 * 
 * @author Tim Boudreau
 */
public interface Wizard {
    /**
     * Special panel ID key indicating a branch point in the wizard,
     * after which the next step(s) are unknown.
     */
    public static final String UNDETERMINED_STEP = "_#UndeterminedStep";
    
    /**
     * Set which step of the wizard is currently being displayed and get
     * the component for that step. 
     *
     * The ID passed
     * becomes the currently active step of the wizard as of this call;
     * if the user has already been to this step, entered some settings
     * and then pressed the Back button, then pressed Next again, the settings 
     * map may already contain
     * settings for this step.  If this method is called as a result of
     * the user pressing the Back button, the settings map will <i>not</i>
     * contain any settings added by the next panel, only settings from
     * panels that precede this one.
     * <p>
     * The identity of the settings map is guaranteed to be the same for
     * all calls to this method for one wizard.
     * <p>
     * Implementations are expected to return the same component if 
     * <code>navigatingTo()</code> is called repeatedly with the same ID.
     * <blockquote>
     * <b>Note:</b> If a later panel removes or changes a value entered
     * in an earlier step, that change is permanent.  Each step should
     * provide its own keys and values, not modify those from other steps.
     * </blockquote>
     * 
     * @param id The ID of the to-be-current panel
     * @param settings map user-entered settings should be put in
     * @return a component that should be displayed in the wizard
     */
    public JComponent navigatingTo (String id, Map settings);
    
    /**
     * Get the String ID of the next panel.  If the Next button should be
     * disabled, return null.
     * @return The unique ID of the step that follows the one currently
     *  presented in the UI, as determined by the last call to 
     *  <code>navigateTo</code>
     */
    public String getNextStep();
    /**
     * Get the String ID of the previous panel.  If the Prev button should
     * be disabled, return null.
     * @return the String ID of the step that precedes the one currently
     *  presented in this <code>Wizard</code>s UI, or null if it is either
     *  the first step or the preceding step is unknown, as determined by the last call to 
     *  <code>navigateTo</code>
     */
    public String getPreviousStep();
    /**
     * Return true if the Finish button should be enabled. 
     * @return if the finish button should be enabled 
     */
    public boolean canFinish ();
    
    /**
     * Get a human readable description of the reason the Next/Finish button
     * is not enabled (i.e. "#\foo is not a legal filename").
     * @return A localized string that describes why the Next/Finish button
     *  is not enabled, or null if one or the other or both should be enabled
     */
    public String getProblem();
    
    /**
     * Get String IDs for all the steps in the wizard (regardless of whether
     * Finish/Next can be enabled or not).  If there is a branch point in
     * the wizard and it cannot be determined what step will be next beyond
     * some point, make the final entry in the returned array the constant
     * UNDETERMINED_STEP, and fire <code>stepsChange()</code> to any listeners
     * once the later steps become known.
     * <p>
     * The return value of this method must be an array of Strings at least
     * one String in length;  if length == 1, that String may not be
     * UNDETERMINED_STEP; UNDETERMINED_STEP may only be the final ID, and only
     * if there is more than one step.
     * @return An array of strings that constitute unique IDs of each step
     *  in the wizard.  The returned array may not contain duplicate entries.
     */
    public String[] getAllSteps();
    
    /**
     * Get a human-readable description for a given panel, as identified by
     * the passed ID.
     */
    public String getStepDescription (String id);
    
    /**
     * Add a listener for changes in the count or order of steps in this 
     * wizard and for changes in Next/Previous/Finish button enablement.
     * @param listener A listener to add
     */
    public void addWizardListener (WizardListener listener);
    
    /**
     * Remove a listener for changes in the count or order of steps in this 
     * wizard and for changes in Next/Previous/Finish button enablement.
     * @param listener A listener to remove
     */
    public void removeWizardListener (WizardListener listener);
    
    /**
     * Finish the wizard, (optionally) instantiating some Object and returning
     * it.  For cases where the map may contain settings too expensive to
     * validate on the fly, 
     * this method may throw a WizardException with a localized message
     * indicating the problem;  that exception can indicate a step in the
     * wizard to return to to allow the user to correct the information.
     * <p>
     * No methods on a <code>Wizard</code> instance should be called after
     * that <code>Wizard</code>'s <code>finish()</code> method has been 
     * called - the results are undefined.
     * @param settings A map containing all of the settings the user has
     *  entered as they traversed this wizard - presumably enough to do 
     *  whatever this method needs to do (if not, that's a bug in the 
     *  implementation of <code>Wizard</code>).
     */
    public Object finish(Map settings) throws WizardException;
    
    /**
     * Get the title of the wizard.  
     * @return A human-readable, localized title that should be displayed
     *   in any dialog showing this wizard
     */
    public String getTitle();
    
    /**
     * Listener which can detect changes in the state of a wizard as the
     * user proceeds.
     */
    public static interface WizardListener extends EventListener {
        /**
         * Called when a change in the number or names of the steps of the
         * wizard changes (for example, the user made a choice in one pane which
         * affects the flow of subsequent steps).
         * @param wizard The wizard whose steps have changed
         */
        public void stepsChanged(Wizard wizard);
        
        /**
         * Called when the enablement of the next/previous/finish buttons 
         * change, or the problem text changes.
         * @param wizard The wizard whose navigability has changed
         */
        public void navigabilityChanged(Wizard wizard);
    }
}
