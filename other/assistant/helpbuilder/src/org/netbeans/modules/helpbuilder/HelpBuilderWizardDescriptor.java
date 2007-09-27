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
     * Sets up various wizard propertiesto follow recommended
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
