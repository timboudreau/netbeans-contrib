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

package org.netbeans.installer.products.nb.base;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.product.components.ProductConfigurationLogic;
import org.netbeans.installer.utils.FileProxy;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.applications.JavaUtils;
import org.netbeans.installer.utils.applications.JavaUtils.JavaInfo;
import org.netbeans.installer.utils.applications.NetBeansUtils;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.FilesList;
import org.netbeans.installer.utils.helper.RemovalMode;
import org.netbeans.installer.utils.helper.Text;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.Wizard;
import org.netbeans.installer.wizard.components.WizardComponent;

/**
 *
 * @author Dmitry Lipin
 */
public class ConfigurationLogic extends ProductConfigurationLogic {
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String WIZARD_COMPONENTS_URI =
            FileProxy.RESOURCE_SCHEME_PREFIX +
            "org/netbeans/installer/products/nb/base/wizard.xml"; // NOI18N
    
   
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    private List<WizardComponent> wizardComponents;
    
    public ConfigurationLogic() throws InitializationException {
        wizardComponents = Wizard.loadWizardComponents(
                WIZARD_COMPONENTS_URI,
                getClass().getClassLoader());
    }
    
   public void install(final Progress progress) throws InstallationException {
        final Product product = getProduct();
        final File installLocation = product.getInstallationLocation();
        final FilesList filesList = product.getInstalledFiles();     
        
        final File jreHome = new File (System.getProperty("java.home"));
        try {
            progress.setDetail(getString("CL.install.jdk.home")); // NOI18N
            JavaInfo info = JavaUtils.getInfo(jreHome);
            LogManager.log("Using the following JDK for NetBeans configuration : ");
            LogManager.log("... path    : "  + jreHome);
            LogManager.log("... version : "  + info.getVersion().toJdkStyle());
            LogManager.log("... vendor  : "  + info.getVendor());
            LogManager.log("... final   : "  + (!info.isNonFinal()));
            NetBeansUtils.setJavaHome(installLocation, jreHome);
        } catch (IOException e) {
            throw new InstallationException(
                    getString("CL.install.error.jdk.home"), // NOI18N
                    e);
        }             
               
        try {
            progress.setDetail(getString("CL.install.product.id")); // NOI18N            
            filesList.add(NetBeansUtils.createProductId(installLocation));
        } catch (IOException e) {
            throw new InstallationException(
                    getString("CL.install.error.product.id"), // NOI18N
                    e);
        }     
        progress.setPercentage(Progress.COMPLETE);
    }
    
    public void uninstall(final Progress progress) throws UninstallationException {
        final Product product = getProduct();
        final File installLocation = product.getInstallationLocation();
        LogManager.log("uninstalling NB from " + installLocation.getAbsolutePath());
        NetBeansUtils.warnNetbeansRunning(installLocation);
                
        try {
            FileUtils.deleteFile(installLocation, true);
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        /////////////////////////////////////////////////////////////////////////////
        progress.setPercentage(Progress.COMPLETE);
    }
         
    @Override
    public Text getLicense() {
        return null;
    }
    
    public List<WizardComponent> getWizardComponents() {
        return wizardComponents;
    }
    
    @Override
    public boolean registerInSystem() {
        return false;
    }
    
    @Override
    public RemovalMode getRemovalMode() {
        return RemovalMode.LIST;
    }
}
