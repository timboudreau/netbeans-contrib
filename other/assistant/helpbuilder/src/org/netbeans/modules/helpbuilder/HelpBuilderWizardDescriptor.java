/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.helpbuilder;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.net.MalformedURLException;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import java.text.MessageFormat;

/** 
 * A wizard descriptor.
 *
 * @author  Richard Gregor
 */
public class HelpBuilderWizardDescriptor extends WizardDescriptor {

    private final HelpBuilderWizardIterator iterator;

    /** Make a descriptor suited to use JavaHelp_WizardIterator.
     * Sets up various wizard properties to follow recommended
     * style guidelines.
     */
    public HelpBuilderWizardDescriptor() {
        this (new HelpBuilderWizardIterator());
    }
    private HelpBuilderWizardDescriptor(HelpBuilderWizardIterator iterator) {
        super (iterator);
        this.iterator = iterator;
        // Set title for the dialog:
        setTitle(NbBundle.getMessage(HelpBuilderWizardDescriptor.class, "TITLE_wizard"));
        setTitleFormat(new MessageFormat("{0}"));
        // Make the left pane appear:
        putProperty ("WizardPanel_autoWizardStyle", Boolean.TRUE); // NOI18N
        // Make the left pane show list of steps etc.:
        putProperty ("WizardPanel_contentDisplayed", Boolean.TRUE); // NOI18N
        // Number the steps.
        putProperty ("WizardPanel_contentNumbered", Boolean.TRUE); // NOI18N
        /*
        // Optional: make nonmodal.
        setModal (false);
        // Optional: show a help tab with special info about the pane:
        putProperty ("WizardPanel_helpDisplayed", Boolean.TRUE); // NOI18N
        // Optional: set the size of the left pane explicitly:
        putProperty ("WizardPanel_leftDimension", new Dimension (100, 400)); // NOI18N
        // Optional: if you want a special background image for the left pane:
        try {
            putProperty ("WizardPanel_image", // NOI18N
                Toolkit.getDefaultToolkit ().getImage
                (new URL ("nbresloc:/wizard/JavaHelp_WizardImage.gif"))); // NOI18N
        } catch (MalformedURLException mfue) {
            throw new IllegalStateException (mfue.toString ());
        }
        */
    }

    // Called when user moves forward or backward etc.:
    protected void updateState () {
        super.updateState ();
        putProperty ("WizardPanel_contentData", iterator.getSteps ()); // NOI18N
        putProperty ("WizardPanel_contentSelectedIndex", new Integer (iterator.getIndex ())); // NOI18N
    }

}
