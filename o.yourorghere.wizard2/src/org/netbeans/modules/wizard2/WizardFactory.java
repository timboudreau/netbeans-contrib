/*  The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.
    You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.
    When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
package org.netbeans.modules.wizard2;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.netbeans.api.wizard.*;
import org.netbeans.modules.wizard2.WrapperIterator;
import org.netbeans.spi.wizard.*;
/**
 *
 * @author Tim Boudreau
 */
public class WizardFactory {
    private WizardFactory () {}
    private static final String KEY_WIZARD = "wizard";
    
    public static WizardDescriptor.InstantiatingIterator createWrapperWizard (FileObject f) {
        Wizard wiz = (Wizard) f.getAttribute(KEY_WIZARD);
        boolean isProgress = Boolean.TRUE.equals(f.getAttribute("progress"));
        boolean isAsynch = Boolean.TRUE.equals (f.getAttribute("asynchronous"));
        String asynchValidationPanelIds = (String) f.getAttribute("asynchValidatingPanels");
        String validatingPanelIds = (String) f.getAttribute("asynchValidatingPanels");
        Set <String> asynchVPanels = new HashSet <String> ();
        if (asynchValidationPanelIds != null) {
            asynchVPanels.addAll (Arrays.asList(asynchValidationPanelIds.split(","))); //NOI18N
        }
        Set <String> vpanels = new HashSet <String> ();
        if (validatingPanelIds != null) {
            vpanels.addAll (Arrays.asList(validatingPanelIds.split(","))); //NOI18N
        }
        if (isProgress) {
            return new WrapperIterator.ProgressWrapperIterator(wiz, vpanels, asynchVPanels);
        } else if (isAsynch) {
            return new WrapperIterator.AsynchWrapperIterator(wiz, vpanels, asynchVPanels);
        } else {
            return new WrapperIterator (wiz, vpanels, asynchVPanels);
        }
    }
    
}
