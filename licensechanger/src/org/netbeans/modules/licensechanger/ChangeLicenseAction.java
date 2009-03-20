/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.licensechanger;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.wizard.WizardDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Tim Boudreau
 */
public class ChangeLicenseAction extends AbstractAction implements ContextAwareAction, LookupListener {
    private final Lookup lkp;
    private final Lookup.Result<DataObject> res;
    
    public ChangeLicenseAction() {
        this (Utilities.actionsGlobalContext());
    }
    
    ChangeLicenseAction (Lookup lkp) {
        this.lkp = lkp;
        res = lkp.lookupResult(DataObject.class);
        putValue (NAME, NbBundle.getMessage(ChangeLicenseAction.class, 
                "LBL_CHANGE_LICENSE")); //NOI18N
        res.allItems();
    }

    public void actionPerformed(ActionEvent e) {
        Set<FileObject> files = new HashSet<FileObject>();
        for (DataObject ob : res.allInstances()) {
            files.add (ob.getPrimaryFile());
        }
        WizardPP wiz = new WizardPP (files.toArray(new FileObject[files.size()]));
        WizardDisplayer.showWizard(wiz.createWizard());
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new ChangeLicenseAction(actionContext);
    }

    public void resultChanged(LookupEvent ev) {
        boolean enable = true;
        for (DataObject ob : res.allInstances()) {
            enable = ob.getPrimaryFile().isValid() && ob.getPrimaryFile().isFolder();
        }
        setEnabled (enable);
    }

}
