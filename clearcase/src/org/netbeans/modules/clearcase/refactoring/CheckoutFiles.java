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

package org.netbeans.modules.clearcase.refactoring;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.modules.clearcase.ClearcaseModuleConfig;
import org.netbeans.modules.clearcase.ui.checkout.CheckoutAction;
import org.netbeans.modules.refactoring.spi.ProblemDetailsImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Stupka
 */
public class CheckoutFiles implements ProblemDetailsImplementation {
    
    private Collection files;
    private Cancellable parent;
    
    /** Creates a new instance of CheckoutFiles */
    public CheckoutFiles(Collection files) {
        this.files = files;
    }
    
    public void showDetails(Action rerunRefactoringAction, Cancellable parent) {
        this.parent = parent;

        // XXX this should be redesigned - change ui, progress bar, cancellable, etc...
                
        if (checkout(files)) {
            rerunRefactoring(rerunRefactoringAction);
        }                
    }
    
    public String getDetailsHint() {
        return NbBundle.getMessage(CheckoutFiles.class, "LBL_Checkout_Files");
    }

    void setFiles(Collection files) {
        this.files = files;
    }

    static boolean checkout(Collection filesObjects) throws MissingResourceException {

        ClearcaseModuleConfig.OnDemandCheckout odc = ClearcaseModuleConfig.getOnDemandCheckout();
        if(odc == ClearcaseModuleConfig.OnDemandCheckout.Disabled) {
            odc = ClearcaseModuleConfig.OnDemandCheckout.ReservedWithFallback; // XXX is this how it should be?
        }
        
        Set<File> files = new HashSet<File>();
        FileObject[] fos = (FileObject[]) filesObjects.toArray(new FileObject[0]);
        for (int i = 0; i < fos.length; i++) {
            File file = FileUtil.toFile(fos[i]);
            if(file != null) {
                files.add(file);
                File parent = file.getParentFile();
                files.add(parent);
            }
        }
        for (File file : files) {
           // XXX CheckoutAction.ensureMutable(file, null);                
        }

        return true;
    }
    
    private void rerunRefactoring(Action rerunRefactoringAction) {
        parent.cancel();
        rerunRefactoringAction.actionPerformed(null);
    }
       
    public Collection getFiles() {
        return files;
    }        
    
}
