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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.moduleresolver.ui;

import java.awt.Dialog;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.MessageFormat;
import java.util.Collection;
import javax.swing.SwingUtilities;
import org.netbeans.api.autoupdate.UpdateElement;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Jirka Rechtacek
 */
public class InstallMissingDisplayer {
    private InstallMissingDisplayer () {}
    private static InstallMissingDisplayer INSTANCE = new InstallMissingDisplayer ();
    private Dialog wizard = null;

    public static InstallMissingDisplayer getDefault () {
        return INSTANCE;
    }
    
    public void open () {
        if (wizard == null) {
            WizardDescriptor.Iterator<WizardDescriptor> iterator = new InstallMissingModulesIterator ();
            WizardDescriptor wizardDescriptor = new WizardDescriptor (iterator);
            wizardDescriptor.setModal (false);

            wizardDescriptor.setTitleFormat (new MessageFormat(NbBundle.getMessage (InstallMissingDisplayer.class, "InstallMissingDisplayer_MessageFormat")));
            wizardDescriptor.setTitle (NbBundle.getMessage (InstallMissingDisplayer.class, "InstallMissingDisplayer_Title"));

            wizard = DialogDisplayer.getDefault ().createDialog (wizardDescriptor);
            wizard.setVisible (true);
            wizard.addWindowListener(new WindowListener () {
                public void windowOpened (WindowEvent e) {}
                public void windowClosing (WindowEvent e) {
                    wizard = null;
                }
                public void windowClosed (WindowEvent e) {
                    wizard = null;
                }
                public void windowIconified (WindowEvent e) {}
                public void windowDeiconified (WindowEvent e) {}
                public void windowActivated (WindowEvent e) {}
                public void windowDeactivated (WindowEvent e) {}
            });
        }
        wizard.requestFocus ();
    }
    
    public void notifyCandidates (Collection<UpdateElement> candidates) {
        final NotifyDescriptor nd = new NotifyDescriptor (
                new RepairNotification (candidates),
                NbBundle.getMessage (InstallMissingDisplayer.class, "InstallMissingDisplayer_NotifyRepair_Title"),
                NotifyDescriptor.YES_NO_OPTION,
                NotifyDescriptor.INFORMATION_MESSAGE,
                new Object [] { NotifyDescriptor.YES_OPTION, NotifyDescriptor.NO_OPTION },
                NotifyDescriptor.YES_OPTION);
        SwingUtilities.invokeLater(new Runnable () {
            public void run () {
                if (NotifyDescriptor.YES_OPTION.equals (DialogDisplayer.getDefault ().notify (nd))) {
                    open ();
                }
            }
        });
    }

}
