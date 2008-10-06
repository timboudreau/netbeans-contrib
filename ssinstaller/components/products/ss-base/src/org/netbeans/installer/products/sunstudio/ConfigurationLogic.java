/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.products.sunstudio;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.product.components.ProductConfigurationLogic;
import org.netbeans.installer.products.sunstudio.panels.SSBasePanel;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.helper.RemovalMode;
import org.netbeans.installer.utils.helper.Text;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.components.WizardComponent;
import org.netbeans.installer.utils.progress.CompositeProgress;
import org.netbeans.installer.wizard.Utils;

public class ConfigurationLogic extends ProductConfigurationLogic {
    
    @Override
    public void install(Progress progress) throws InstallationException {
        if (SystemUtils.getCurrentPlatform().equals(Platform.SOLARIS_SPARC)) {
            // TODO remove this
            String v8Name =  getProduct().getInstallationLocation() + "/" + Utils.getMainDirectory() +"/lib/v8plus";
            try {
                FileUtils.mkdirs(new File(v8Name));
                LogManager.log("v8Name was created as "  + v8Name );
            } catch (IOException ex) {
                LogManager.log("v8Name was not created as "  + v8Name, ex);                
            }
        }
        progress.setPercentage(Progress.COMPLETE);
    }

    @Override
    public void uninstall(Progress progress) throws UninstallationException {
        getProduct().getParent().removeChild(getProduct());
        List<Product> products = Registry.getInstance().getProducts();
     
        /*
         * Here the percentage of each product is approximated
         * as a number of its subcomponents
         */
        
        final Map<Product, Integer> percents = new HashMap<Product, Integer>();
        int allProductsSize = 0;
        for (Product product : products) {
            allProductsSize += product.getDataUris().size();
        }
        for (Product product : products) {
            percents.put(product, (Progress.COMPLETE - Progress.START) 
                    * product.getDataUris().size() / allProductsSize);            
        }

                
        CompositeProgress compositeProgress = new CompositeProgress(); 
        progress.synchronizeFrom(compositeProgress);
        File mainDirectory = new File(getProduct().getInstallationLocation(), Utils.getMainDirectory());
        
        for (Product product : products) {
          //  try {
                Progress innerProgress = new Progress();
                compositeProgress.addChild(innerProgress, percents.get(product));                
                product.uninstall(innerProgress);
                product.getParent().removeChild(product);
         //   } catch (InitializationException ex) {
          //      LogManager.log("Unexpected exception during removal of " 
           //             + product.getDisplayName(), ex);
           // }
        }                       
        try {
            FileUtils.deleteFile(new File(mainDirectory, "uninstall.sh"));
            FileUtils.deleteFile(new File(mainDirectory, "modify-install.sh"));
            FileUtils.deleteFile(new File(mainDirectory, "prod/lib/condev"), true);

            // workaround for SS bug
            // the removal of condev should be moved to the beginning
            FileUtils.deleteFile(new File(mainDirectory, "prod"), true);
            FileUtils.deleteFile(new File(mainDirectory, "lib/v8plus"), true);
            // end
            // delete only if empty
            FileUtils.deleteFile(mainDirectory);
        } catch (IOException ex) {
            LogManager.log("Unexpected exception during removal of " 
                    + mainDirectory.getAbsolutePath(), ex);
        }
        progress.setPercentage(Progress.COMPLETE);
    }

    @Override
    public int getLogicPercentage() {
        return 100;        
    }
    
    @Override
    public List<WizardComponent> getWizardComponents() {        
        return Arrays.asList((WizardComponent) new SSBasePanel());
    }

    @Override
    public RemovalMode getRemovalMode() {
        return RemovalMode.LIST;
    }

    @Override
    public Text getLicense() {
        return null;
    }
}
