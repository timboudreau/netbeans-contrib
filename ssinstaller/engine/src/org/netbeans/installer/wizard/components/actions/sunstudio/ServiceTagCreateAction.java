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
 * If you wish your productVersion of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your productVersion of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */
package org.netbeans.installer.wizard.components.actions.sunstudio;

import org.netbeans.modules.servicetag.ServiceTag;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.wizard.Utils;
import org.netbeans.installer.wizard.components.WizardAction;
import org.netbeans.modules.reglib.NbBundle;
import org.netbeans.modules.servicetag.RegistrationData;
import static org.netbeans.installer.utils.helper.DetailedStatus.INSTALLED_SUCCESSFULLY;
import static org.netbeans.installer.utils.helper.DetailedStatus.INSTALLED_WITH_WARNINGS;
import static org.netbeans.installer.utils.helper.DetailedStatus.UNINSTALLED_SUCCESSFULLY;
import static org.netbeans.installer.utils.helper.DetailedStatus.UNINSTALLED_WITH_WARNINGS;
/**
 *
 * @author Leonid Mesnik
 */
public class ServiceTagCreateAction extends WizardAction {
    
    private static RegistrationData registrationData;
    private static String REGISTRATION_DIR = File.separator + "prod"
            + File.separator + "lib" + File.separator + "condev";
    private static String REGISTRATION_XML = "registration.xml";

    public ServiceTagCreateAction() {
        Logger parent = Logger.getLogger(this.getClass().getName()).getParent();
        Handler[] handlers = (parent == null) ? null : parent.getHandlers();
        //if(parent!=null) parent.setLevel(Level.ALL);
        if (handlers != null) {
            for (Handler h : handlers) {
                parent.removeHandler(h);
            }
        }
        parent.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                LogManager.log(record.getSourceClassName() + "." +
                        record.getSourceMethodName() + "(): " + record.getLevel());
                LogManager.log(
                        (record.getParameters() == null) ? 
                            record.getMessage() : 
                            StringUtils.format(record.getMessage(), 
                        record.getParameters()));

                if (record.getThrown() != null) {
                    LogManager.log(record.getThrown());
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });        
    }

    public static RegistrationData getRegistrationData() {
        return registrationData;
    }

    public void execute() {
        LogManager.logEntry("... create service tags action");
        final List<Product> installedProducts = new LinkedList<Product>();
        final List<Product> uninstalledProducts = new LinkedList<Product>();
        final Registry registry = Registry.getInstance();
        installedProducts.addAll(registry.getProducts(INSTALLED_SUCCESSFULLY));
        installedProducts.addAll(registry.getProducts(INSTALLED_WITH_WARNINGS));

        uninstalledProducts.addAll(registry.getProductsToUninstall());
        
        
        try {
            Registry bundledRegistry = new Registry();
            final String bundledRegistryUri = System.getProperty(
                    Registry.BUNDLED_PRODUCT_REGISTRY_URI_PROPERTY);

            bundledRegistry.loadProductRegistry((bundledRegistryUri != null) ? 
                bundledRegistryUri : Registry.DEFAULT_BUNDLED_PRODUCT_REGISTRY_URI);           
         
        } catch (InitializationException e) {
            LogManager.log("Cannot load bundled registry", e);
        }

        File registrationFile = new File(
                registry.getProducts("ss-base").get(0).getInstallationLocation()
                + File.separator + Utils.getMainDirectory()
                + REGISTRATION_DIR , REGISTRATION_XML);
        try {

            if (registrationFile.exists()) {
                registrationData = RegistrationData.loadFromXML(new FileInputStream(registrationFile));
            } else {
                registrationData = new RegistrationData();
                FileUtils.mkdirs(registrationFile.getParentFile());
            }

            // The system service tag regitry.
            org.netbeans.modules.servicetag.Registry stRegistry = null;
                    //org.netbeans.modules.servicetag.Registry.getSystemRegistry();
            if (org.netbeans.modules.servicetag.Registry.isSupported()) {
                stRegistry = org.netbeans.modules.servicetag.Registry.getSystemRegistry();
            }
            ServiceTag st;
            for (Product product : installedProducts) {
                String uid = product.getUid();
                if (uid.equals("nb-base")) {
                    st = newNbServiceTag(product);
                    registrationData.addServiceTag(st);
                    if (stRegistry.isSupported()) {
                        LogManager.log("Add service tags to system registry");
                        stRegistry.addServiceTag(st);
                    }
                } else if (uid.equals("ss-base")) {
                    st = newSSServiceTag(product);
                    registrationData.addServiceTag(st);
                    if (stRegistry.isSupported()) {
                        LogManager.log("Add service tags to system registry");
                        stRegistry.addServiceTag(st);
                    }                    
                }
            }

            String instanceURN;
            for (Product product : uninstalledProducts) {
                String uid = product.getUid();
                if (uid.equals("nb-base") || uid.equals("ss-base")) {
                    instanceURN = getInstanceURN(uid);
                    if (instanceURN == null) {
                        continue;
                    }
                    registrationData.removeServiceTag(instanceURN);
                    if (stRegistry.isSupported()) {
                        LogManager.log("Remove service tags from system registry");
                        stRegistry.removeServiceTag(instanceURN);
                    }
                }
            }
            if (registrationData.getServiceTags().size() > 0) {
                registrationData.storeToXML(new FileOutputStream(registrationFile));
            } else {
                FileUtils.deleteFile(registrationFile.getParentFile(), true);
            }
        } catch (IOException ex) {
            LogManager.log("Unexpected exception during service tage creaion.", ex);
        }
         
    }

    private static String getInstanceURN(String uid) {
        String productURN = NbBundle.getMessage(ServiceTagCreateAction.class,"servicetag."
                + uid.substring(0,2) +".urn");
        for (ServiceTag st : registrationData.getServiceTags()) {
            if (st.getProductURN().equals(productURN)) {
                return st.getInstanceURN();
            }
        }
        return null;
    }

    private static ServiceTag newNbServiceTag (Product product)  {
        // Determine the product URN and name
        String productURN, productName, productVersion, parentURN, parentName;

        productURN = NbBundle.getMessage(ServiceTagCreateAction.class,"servicetag.nb.urn");
        productName = NbBundle.getMessage(ServiceTagCreateAction.class,"servicetag.nb.name");
        productVersion = NbBundle.getMessage(ServiceTagCreateAction.class,"servicetag.nb.version");
        parentURN = NbBundle.getMessage(ServiceTagCreateAction.class,"servicetag.nb.parent.urn");
        parentName = NbBundle.getMessage(ServiceTagCreateAction.class,"servicetag.nb.parent.name");

        return ServiceTag.newInstance(ServiceTag.generateInstanceURN(),
                                      productName,
                                      productVersion,
                                      productURN,
                                      parentName,
                                      parentURN,
                                      "version=" + productVersion + ",dir="
                                      + product.getInstallationLocation().getAbsolutePath(),
                                      "NetBeans.org",
                                      System.getProperty("os.arch"),
                                      getZone(),
                                      SOURCE_NAME);
    }

    private static String getZone() {
        return "global";
    }
  
     
    /**
     * Create new service tag instance for Sun Studio          
     */
    private static ServiceTag newSSServiceTag (Product product) {
        // Determine the product URN and name
        String productURN, productName, parentURN, parentName, productVersion;

        productURN = ResourceUtils.getString(ServiceTagCreateAction.class,"servicetag.ss.urn");
        productName = ResourceUtils.getString(ServiceTagCreateAction.class,"servicetag.ss.name");
        productVersion = ResourceUtils.getString(ServiceTagCreateAction.class,"servicetag.ss.version");
        parentURN = ResourceUtils.getString(ServiceTagCreateAction.class,"servicetag.ss.parent.urn");
        parentName = ResourceUtils.getString(ServiceTagCreateAction.class,"servicetag.ss.parent.name");

        return ServiceTag.newInstance(ServiceTag.generateInstanceURN(),
                                      productName,
                                      productVersion,
                                      productURN,
                                      parentName,
                                      parentURN,
                                      "version=" + productVersion + ",dir="
                                      + product.getInstallationLocation().getAbsolutePath(),
                                      "Sun Microsystems",
                                      System.getProperty("os.arch"),
                                      getZone(),
                                      SOURCE_NAME);
    }    
  
    @Override
    public boolean canExecuteForward() {
        return true;
    }

    @Override
    public WizardActionUi getWizardUi() {
        return null;
    }
    private static final String ALLOW_SERVICETAG_CREATION_PROPERTY =
            "servicetag.allow.create";//NOI18N
    private static final String SOURCE_NAME =
            ResourceUtils.getString(ServiceTagCreateAction.class,
            "NSTCA.installer.source.name");//NOI18N
}
