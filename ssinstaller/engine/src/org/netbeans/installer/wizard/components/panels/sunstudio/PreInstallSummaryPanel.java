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

package org.netbeans.installer.wizard.components.panels.sunstudio;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.netbeans.installer.Installer;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.RegistryNode;
import org.netbeans.installer.product.RegistryType;
import org.netbeans.installer.product.components.NativeClusterConfigurationLogic;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.ErrorManager;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.NativeException;
import org.netbeans.installer.utils.helper.swing.NbiLabel;
import org.netbeans.installer.utils.helper.swing.NbiPanel;
import org.netbeans.installer.utils.helper.swing.NbiTextPane;
import org.netbeans.installer.wizard.Utils;
import org.netbeans.installer.wizard.components.panels.ErrorMessagePanel;
import org.netbeans.installer.wizard.components.panels.ErrorMessagePanel.ErrorMessagePanelSwingUi;
import org.netbeans.installer.wizard.components.panels.ErrorMessagePanel.ErrorMessagePanelUi;
import org.netbeans.installer.wizard.containers.SwingContainer;
import org.netbeans.installer.wizard.ui.SwingUi;
import org.netbeans.installer.wizard.ui.WizardUi;

public class PreInstallSummaryPanel extends ErrorMessagePanel {
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    public PreInstallSummaryPanel() {
        setProperty(TITLE_PROPERTY,
                DEFAULT_TITLE);
        setProperty(DESCRIPTION_PROPERTY,
                DEFAULT_DESCRIPTION);
        setProperty(NEXT_BUTTON_TEXT_PROPERTY,
                DEFAULT_NEXT_BUTTON_TEXT);         
    }
    
    @Override
    public WizardUi getWizardUi() {
        if (wizardUi == null) {
            wizardUi = new NbPreInstallSummaryPanelUi(this);
        }
        
        return wizardUi;
    }
    
    @Override
    public void initialize() {
        final List<Product> toInstall =
                Registry.getInstance().getProductsToInstall();
        
        if (toInstall.size() > 0) {
            setProperty(NEXT_BUTTON_TEXT_PROPERTY, DEFAULT_NEXT_BUTTON_TEXT);
            setProperty(DESCRIPTION_PROPERTY, DEFAULT_DESCRIPTION);
        } else {
            setProperty(NEXT_BUTTON_TEXT_PROPERTY, DEFAULT_NEXT_BUTTON_TEXT_UNINSTALL);
            setProperty(DESCRIPTION_PROPERTY, DEFAULT_DESCRIPTION_UNINSTALL);
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    // Inner Classes
    public static class NbPreInstallSummaryPanelUi extends ErrorMessagePanelUi {
        protected PreInstallSummaryPanel component;
        
        public NbPreInstallSummaryPanelUi(PreInstallSummaryPanel component) {
            super(component);
            
            this.component = component;
        }
        
        @Override
        public SwingUi getSwingUi(SwingContainer container) {
            if (swingUi == null) {
                swingUi = new SSPreInstallSummaryPanelSwingUi(component, container);
            }
            
            return super.getSwingUi(container);
        }
    }
    
    public static class SSPreInstallSummaryPanelSwingUi extends ErrorMessagePanelSwingUi {
        protected PreInstallSummaryPanel component;
        
        private NbiTextPane locationsPane;
        
        private NbiLabel uninstallListLabel;
        private NbiTextPane uninstallListPane;
        
        private NbiLabel installationSSSummary;
        private NbiLabel installationSSSize;
                  
        private NbiLabel installationSSComponenets;
        
        private NbiLabel installationNBSummary;
        private NbiLabel installationNBSize;
        
        private NbiLabel downloadSizeLabel;
        private NbiLabel downloadSizeValue;
        
        private NbiPanel spacer;
           
        
        public SSPreInstallSummaryPanelSwingUi(
                final PreInstallSummaryPanel component,
                final SwingContainer container) {
            super(component, container);
            
            this.component = component;
            initComponents();
        }
        
        // protected ////////////////////////////////////////////////////////////////
        @Override
        protected void initializeContainer() {
            super.initializeContainer();            
            container.getNextButton().setText(
                    panel.getProperty(NEXT_BUTTON_TEXT_PROPERTY));
        }
        
        @Override
        protected void initialize() {
            final Registry registry = Registry.getInstance();
                        
            long installationSizeSS = 0;
            long installationSizeNb = 0;
            long downloadSize = 0;
            
            final List<Product> dependentOnNb = new LinkedList<Product>();
            final List<Product> dependentOnSS = new LinkedList<Product>();            
            
            for (Product product: registry.getProductsToInstall()) {                
                downloadSize += product.getDownloadSize();                
                    // TODO change to real dependency checking ....
                    if (Utils.isPrerequisite(product)) {
                        dependentOnNb.add(product);
                        installationSizeNb += product.getRequiredDiskSpace();
                    } else {
                        dependentOnSS.add(product);
                        installationSizeSS += product.getRequiredDiskSpace();
                    }               
            }
            
            Product ssProduct = Utils.getSSBase();
            dependentOnSS.remove(ssProduct);
          //  Product nbProduct = registry.getProducts("nb-extra").get(0);

            uninstallListLabel.setText(
                    panel.getProperty(UNINSTALL_LIST_LABEL_TEXT));
            uninstallListPane.setText(
                    StringUtils.asString(registry.getProductsToUninstall()));
            if (dependentOnSS.size() > 0) {
                installationSSSummary.setText(
                        StringUtils.format(INSTALLATION_FOLDER,
                        ssProduct,
                        ssProduct.getInstallationLocation().getAbsolutePath() + File.separator + Utils.getMainDirectory()));

                installationSSSize.setText(StringUtils.format(INSTALLATION_SIZE,
                        StringUtils.formatSize(installationSizeSS)));

                installationSSComponenets.setText(
                        //StringUtils.format(panel.getProperty(INSTALLATION_SIZE_PROPERTY),
                        //Registry.getInstance().getProducts("nb-base").get(0)))
                        StringUtils.format(ADDONS_INSTALL_TEXT, StringUtils.asString(dependentOnSS)));
                installationSSSummary.setVisible(true);
                installationSSSize.setVisible(true);
                installationSSComponenets.setVisible(true);
            } else {
                installationSSSummary.setVisible(false);
                installationSSSize.setVisible(false);   
                installationSSComponenets.setVisible(false);                

            }
            if (dependentOnNb.size() > 0) {
                Product nbProduct = Utils.getNBExtra();
                /*
                String action = dependentOnNb.contains(nbProduct) ? "installed" : "updated";
                installationNBSummary.setText("NetBeans will be " + action + " in folder: "
                        + Utils.getNBExtra().getInstallationLocation()
                        .getAbsolutePath());
                 */
                installationNBSummary.setText(
                    StringUtils.format(INSTALLATION_FOLDER, 
                    nbProduct,
                    nbProduct.getInstallationLocation().getAbsolutePath()));
                installationNBSize.setText(StringUtils.format(INSTALLATION_SIZE,
                    StringUtils.formatSize(installationSizeNb)));
                installationNBSummary.setVisible(true);
                installationNBSize.setVisible(true);
            } else {
                installationNBSummary.setText("");
                installationNBSummary.setVisible(false);
                installationNBSize.setVisible(false);   
            }
            
            
            downloadSizeLabel.setText(
                    panel.getProperty(INSTALLATION_SIZE));
            downloadSizeValue.setText(StringUtils.formatSize(
                    downloadSize));
            
            if (registry.getProductsToInstall().size() == 0) {
                locationsPane.setVisible(false);
            } else {
                locationsPane.setVisible(true);
            }
            
            
            if (registry.getProductsToUninstall().size() == 0) {
                uninstallListLabel.setVisible(false);
                uninstallListPane.setVisible(false);
            } else {
                uninstallListLabel.setVisible(true);
                uninstallListPane.setVisible(true);
            }
            
            downloadSizeLabel.setVisible(false);
            downloadSizeValue.setVisible(false);
            for (RegistryNode remoteNode: registry.getNodes(RegistryType.REMOTE)) {
                if (remoteNode.isVisible()) {
                    downloadSizeLabel.setVisible(true);
                    downloadSizeValue.setVisible(true);
                }
            }
            
            super.initialize();
        }
        
        @Override
        protected String validateInput() {
            try {
                if(!Boolean.getBoolean(SystemUtils.NO_SPACE_CHECK_PROPERTY)) {
                    final List<File> roots =
                            SystemUtils.getFileSystemRoots();
                    final List<Product> toInstall =
                            Registry.getInstance().getProductsToInstall();
                    final Map<File, Long> spaceMap =
                            new HashMap<File, Long>();
                    
                    LogManager.log("Available roots : " + StringUtils.asString(roots));
                    
                    File downloadDataDirRoot = FileUtils.getRoot(
                            Installer.getInstance().getLocalDirectory(), roots);
                    long downloadSize = 0;
                    for (Product product: toInstall) {
                        downloadSize+=product.getDownloadSize();
                    }
                    // the critical check point - we download all the data
                    spaceMap.put(downloadDataDirRoot, new Long(downloadSize));
                    long lastDataSize = 0;
                    for (Product product: toInstall) {
                                                
                        File installLocation = product.getInstallationLocation();
                        try {
                            if (product.getLogic() instanceof NativeClusterConfigurationLogic) {
                                installLocation = Registry.getInstance().getProducts(NativeClusterConfigurationLogic.SS_BASE_UID).get(0).getInstallationLocation();
                            }
                        } catch (InitializationException ex) {
                            LogManager.log(ex);
                        }
                      //  LogManager.log("   Prouct [" + product. + "] <- " + installLocation);
                        final File root = FileUtils.getRoot(installLocation, roots);
                        final long productSize = product.getRequiredDiskSpace();
                        
                        LogManager.log("    [" + root + "] <- " + installLocation);
                        
                        if ( root != null ) {
                            Long ddSize =  spaceMap.get(downloadDataDirRoot);
                            // remove space that was freed after the remove of previos product data
                            spaceMap.put(downloadDataDirRoot,
                                    Long.valueOf(ddSize - lastDataSize));
                            
                            // add space required for next product installation
                            Long size = spaceMap.get(root);
                            size = Long.valueOf(
                                    (size != null ? size.longValue() : 0L) +
                                    productSize);
                            spaceMap.put(root, size);
                            lastDataSize = product.getDownloadSize();
                        } else {
                             return StringUtils.format(
                                    panel.getProperty(ERROR_NON_EXISTENT_ROOT),
                                    product, installLocation);
                        }
                    }
                
                    for (File root: spaceMap.keySet()) {
                        try {
                            final long availableSpace =
                                    SystemUtils.getFreeSpace(root);
                            final long requiredSpace =
                                    spaceMap.get(root) + REQUIRED_SPACE_ADDITION;
                            
                            if (availableSpace < requiredSpace) {
                                return StringUtils.format(
                                        panel.getProperty(ERROR_NOT_ENOUGH_SPACE),
                                        root,
                                        StringUtils.formatSize(requiredSpace - availableSpace));
                            }
                        } catch (NativeException e) {
                            ErrorManager.notifyError(
                                    panel.getProperty(ERROR_CANNOT_CHECK_SPACE),
                                    e);
                        }
                    }
                }

                final List<Product> toUninstall =
                        Registry.getInstance().getProductsToUninstall();
                for (Product product: toUninstall) {
                    if (!FileUtils.canWrite(product.getInstallationLocation())) {
                        return StringUtils.format(
                                panel.getProperty(ERROR_CANNOT_WRITE),
                                product,
                                product.getInstallationLocation());
                    }
                }
                
            } catch (IOException e) {
                ErrorManager.notifyError(
                        panel.getProperty(ERROR_FSROOTS), e);
            }
            
            return null;
        }
        
        // private //////////////////////////////////////////////////////////////////
        private void initComponents() {
            // locationsPane ////////////////////////////////////////////////////////
            locationsPane = new NbiTextPane();
            
            // uninstallListPane ////////////////////////////////////////////////////
            uninstallListPane = new NbiTextPane();
            
            // uninstallListLabel ///////////////////////////////////////////////////
            uninstallListLabel = new NbiLabel();
            uninstallListLabel.setLabelFor(uninstallListPane);
            
            // installationSSSize ////////////////////////////////////////////////
            installationSSSize = new NbiLabel();
            installationSSSize.setFocusable(true);
            
            // installationSSSummary ////////////////////////////////////////////////
            installationSSSummary = new NbiLabel();
            installationSSSummary.setLabelFor(installationSSSize);
                     
            // installationSSSize ////////////////////////////////////////////////
            installationNBSummary = new NbiLabel();
            installationNBSummary.setFocusable(true);
            
            installationNBSize = new NbiLabel();
            installationNBSize.setFocusable(true);
            
            // installationSSSummary ////////////////////////////////////////////////
            installationSSComponenets = new NbiLabel();
            installationSSComponenets.setLabelFor(installationNBSummary);
            // downloadSizeValue ////////////////////////////////////////////////////
            downloadSizeValue = new NbiLabel();
            downloadSizeValue.setFocusable(true);
            
            // downloadSizeLabel ////////////////////////////////////////////////////
            downloadSizeLabel = new NbiLabel();
            downloadSizeLabel.setLabelFor(downloadSizeValue);
            
            // spacer ///////////////////////////////////////////////////////////////
            spacer = new NbiPanel();
            
            // this /////////////////////////////////////////////////////////////////
            add(uninstallListLabel, new GridBagConstraints(
                    0, 1,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.PAGE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(11, 11, 0, 11),        // padding
                    0, 0));                           // padx, pady - ???
            add(uninstallListPane, new GridBagConstraints(
                    0, 2,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.PAGE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(0, 11, 0, 11),         // padding
                    0, 0));                           // padx, pady - ???
             add(installationSSSummary, new GridBagConstraints(
                    0, 3,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(22, 11, 0, 11),        // padding
                    0, 0));                           // padx, pady - ???
            add(installationSSSize, new GridBagConstraints(
                    0, 4,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(4, 22, 0, 11),         // padding
                    0, 0));                           // padx, pady - ???            
            add(installationSSComponenets, new GridBagConstraints(
                    0, 5,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(4, 22, 0, 11),        // padding
                    0, 0));                           // padx, pady - ???
            add(installationNBSummary, new GridBagConstraints(
                    0, 7,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(22, 11, 0, 11),         // padding
                    0, 0));                           // padx, pady - ???
            add(installationNBSize, new GridBagConstraints(
                    0, 8,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(4, 22, 0, 11),         // padding
                    0, 0));                           // padx, pady - ???            
            add(downloadSizeLabel, new GridBagConstraints(
                    0, 8,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(4, 11, 0, 11),         // padding
                    0, 0));                           // padx, pady - ???
            add(downloadSizeValue, new GridBagConstraints(
                    0, 9,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(4, 22, 0, 11),         // padding
                    0, 0));                           // padx, pady - ???
            add(spacer, new GridBagConstraints(
                    0, 25,                            // x, y
                    1, 1,                             // width, height
                    1.0, 1.0,                         // weight-x, weight-y
                    GridBagConstraints.CENTER,        // anchor
                    GridBagConstraints.BOTH,          // fill
                    new Insets(0, 11, 0, 11),         // padding
                    0, 0));                           // padx, pady - ???
        }
        
        @Override
        public void evaluateNextButtonClick() {           
            super.evaluateNextButtonClick();
        }
        
    }
    
/////////////////////////////////////////////////////////////////////////////////
// Constants
    
    //public static final String INSTALLATION_FOLDER =
    //        "installation.folder"; // NOI18N
    /*public static final String INSTALLATION_FOLDER_NETBEANS_PROPERTY =
            "installation.folder.netbeans"; // NOI18N
    public static final String UNINSTALL_LIST_LABEL_TEXT_PROPERTY =
            "uninstall.list.label.text"; // NOI18N
    public static final String INSTALLATION_SIZE_PROPERTY =
            "installation.size"; // NOI18N
    public static final String DOWNLOAD_SIZE_PROPERTY =
            "download.size"; // NOI18N
    public static final String NB_ADDONS_LOCATION_TEXT_PROPERTY =
            "addons.nb.install.location.text"; // NOI18N
    public static final String SS_ADDONS_LOCATION_TEXT_PROPERTY =
            "addons.ss.install.location.text"; // NOI18N
    
    public static final String ERROR_NOT_ENOUGH_SPACE_PROPERTY =
            "error.not.enough.space"; // NOI18N
    public static final String ERROR_CANNOT_CHECK_SPACE_PROPERTY =
            "error.cannot.check.space"; // NOI18N
    public static final String ERROR_LOGIC_ACCESS_PROPERTY =
            "error.logic.access"; // NOI18N
    public static final String ERROR_FSROOTS_PROPERTY =
            "error.fsroots"; // NOI18N
    public static final String ERROR_NON_EXISTENT_ROOT_PROPERTY =
            "error.non.existent.root"; // NOI18N
    public static final String ERROR_CANNOT_WRITE_PROPERTY =
            "error.cannot.write"; // NOI18N
    */
    public static final String DEFAULT_TITLE =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.title"); // NOI18N
    public static final String DEFAULT_DESCRIPTION =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.description"); // NOI18N
    public static final String DEFAULT_DESCRIPTION_UNINSTALL =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.description.uninstall"); // NOI18N
    
    public static final String INSTALLATION_FOLDER =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.installation.folder"); // NOI18N
    public static final String UNINSTALL_LIST_LABEL_TEXT =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.uninstall.list.label.text"); // NOI18N
    public static final String INSTALLATION_SIZE =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.installation.size"); // NOI18N
    //public static final String DOWNLOAD_SIZE =
    //        ResourceUtils.getString(PreInstallSummaryPanel.class,
    //        "NPrISP.download.size"); // NOI18N
     public static final String ADDONS_INSTALL_TEXT =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.addons.install.location.text"); // NOI18N
    //public static final String DEFAULT_NB_ADDONS_LOCATION_TEXT =
    //        ResourceUtils.getString(PreInstallSummaryPanel.class,
    //       "NPrISP.addons.nb.install.location.text"); // NOI18N
    
    public static final String DEFAULT_NEXT_BUTTON_TEXT =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.next.button.text"); // NOI18N
    public static final String DEFAULT_NEXT_BUTTON_TEXT_UNINSTALL =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.next.button.text.uninstall"); // NOI18N

    //public static final String ADDITIONAL_RUNTIMES_TO_DELETE =
    //        ResourceUtils.getString(PreInstallSummaryPanel.class,
    //        "NPrISP.additional.runtimes.to.delete");//NOI18N
    public static final String ERROR_NOT_ENOUGH_SPACE =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.error.not.enough.space"); // NOI18N
    public static final String ERROR_CANNOT_CHECK_SPACE =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.error.cannot.check.space");// NOI18N
 //   public static final String RROR_LOGIC_ACCESS =
   //         ResourceUtils.getString(PreInstallSummaryPanel.class,
   //         "NPrISP.error.logic.access");// NOI18N
    public static final String ERROR_FSROOTS =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.error.fsroots"); // NOI18N
    public static final String ERROR_NON_EXISTENT_ROOT =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.error.non.existent.root"); // NOI18N
    public static final String ERROR_CANNOT_WRITE =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.error.cannot.write"); // NOI18N
    
    public static final long REQUIRED_SPACE_ADDITION =
            10L * 1024L * 1024L; // 10MB
}
