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

package org.netbeans.installer.products.nb.extra.wizard.panels;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.helper.Status;
import org.netbeans.installer.wizard.components.panels.ApplicationLocationPanel;
import org.netbeans.installer.wizard.components.panels.sunstudio.PreInstallSummaryPanel;

/**
 *
 * @author Leonid Mesnik
 */

public class NbLocationPanel extends ApplicationLocationPanel {
   
    private File selectedLocation;
    private List<File> locations;
    private List<String> labels;

    public NbLocationPanel() {        
        setProperty(LOCATION_LABEL_TEXT_PROPERTY, 
                LOCATION_LABEL_TEXT);        
        setProperty(LIST_LABEL_TEXT_PROPERTY, 
                LIST_LABEL_TEXT);                
       // setProperty(ERROR_FAILED_VERIFY_INPUT_PROPERTY,
      ///          ERROR_FAILED_VERIFY_INPUT_TEXT);
               
        setProperty(TITLE_PROPERTY,
                DEFAULT_TITLE);
        setProperty(DESCRIPTION_PROPERTY,
                DEFAULT_DESCRIPTION);
        
        locations = new ArrayList();
        labels = new ArrayList<String>();
        fetchLocationsFromRegistry(locations);
        fetchLocationsFromFileSystem(locations);
        
        for(File location : locations) {
            labels.add(readNBDescription(location));
        }        
        if (locations.size() > 0 ) {
            selectedLocation = locations.get(0);
        } else {
            selectedLocation = new File(DEFAULT_LOCATION);
        }
    }
    
    @Override
    public List<File> getLocations() {
        return locations;
    }

    @Override
    public List<String> getLabels() {
        return labels;
    }

    @Override
    public File getSelectedLocation() {        
        return selectedLocation;
    }
    
        
    public static final String DEFAULT_TITLE =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.title"); // NOI18N
    public static final String DEFAULT_DESCRIPTION =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.description"); // NOI18N
    
    private String validateNetBeansLocation(File nbRoot) {
        if (nbRoot.getName().equals("netbeans")) {
            return "Don install here, I'm working";
        }
        if (!nbRoot.isDirectory()) {
            return "NetBeans location is not directory";
        }
        
        if (nbRoot.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals(NB_DIR);
            }
        }).length == 0) {
            return "NB directroy does not contain nb61";
        }
               
        if (nbRoot.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals(NB_CND_DIR);
            }
        }).length == 0) {
            return "NB directroy does not contain cnd2";
        }
         
        Product nbProduct = Registry.getInstance().getProducts(NB_PRODUCT_UID).get(0);
        Registry.getInstance().getProducts(NB_EXTRA_UID).get(0).setInstallationLocation(nbRoot);
        nbProduct.setStatus(Status.NOT_INSTALLED);
        nbProduct.setInstallationLocation(nbRoot);
        
        nbProduct.setParent(Registry.getInstance().getProducts("ss-base").get(0));
        Registry.getInstance().getProducts(NB_EXTRA_UID).get(0).setParent(Registry.getInstance().getProducts("ss-base").get(0));
        // install CND pack if needed
        /*
        if (nbRoot.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals(NB_CND_DIR);
            }
        }).length == 0) {
            Registry.getInstance().getProducts(NB_CND_UID).get(0)
                    .setStatus(Status.TO_BE_INSTALLED);
        } else {
            Registry.getInstance().getProducts(NB_CND_UID).get(0)
                    .setStatus(Status.NOT_INSTALLED);
        }*/
        return null;
    }

    @Override
    public String validateLocation(String value) {
        File file = new File(value);        
        if (locations.contains(file)) {
            return validateNetBeansLocation(file);
        }       
                 
        Product nbProduct = Registry.getInstance().getProducts(NB_PRODUCT_UID).get(0);
        nbProduct.setStatus(Status.TO_BE_INSTALLED);
        nbProduct.setInstallationLocation(file);
        Registry.getInstance().getProducts(NB_EXTRA_UID).get(0).setInstallationLocation(file);
            nbProduct.setParent(Registry.getInstance().getProducts("ss-base").get(0));
        Registry.getInstance().getProducts(NB_EXTRA_UID).get(0).setParent(Registry.getInstance().getProducts("ss-base").get(0));
    
        
           
        // TODO : create correct checks       
        return null;
    }
    

    @Override
    public void setLocation(File location) {
        selectedLocation = location;
    }

   
    private String readNBDescription(File nbLocation) {
        StringBuffer description = new StringBuffer("NetBeans (");
        description.append(nbLocation).append(")");
        return description.toString(); 
    }
     
     
    private void fetchLocationsFromFileSystem(final List<File> locations) {
        for (String parentPath : NB_FILESYSTEM_LOCATIONS) {
            File parentDir = new File(parentPath);
            if (!parentDir.exists() || !parentDir.isDirectory()) {
                continue;
            }            
            File[] candidates = parentDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {                   
                    return name.startsWith(NB_DIRECTORY_NAME);
                }
            });        
            for (File location : candidates) {
                if (!locations.contains(location)) {
                    locations.add(location);
                }
            }
        }
    }
    
    private void fetchLocationsFromRegistry(final List<File> locations) {
        for (Product nbBase: Registry.getInstance().getProducts(NB_PRODUCT_UID)) {
            if (nbBase.getStatus() == Status.INSTALLED) {
                if (!locations.contains(nbBase.getInstallationLocation())) {
                    locations.add(nbBase.getInstallationLocation());
                }
            }
        }
    }
    
    static final String NB_PRODUCT_UID = "nb-base";
    static final String NB_CND_UID = "nb-cnd";
    static final String NB_EXTRA_UID = "nb-extra";
     
    public static final String[] NB_FILESYSTEM_LOCATIONS = new String[] {
        "/usr", // NOI18N        
        "/usr/local", // NOI18N
        "/usr/share", // NOI18N        
        "/opt", // NOI18N
    };
    final static String NB_DIRECTORY_NAME = "netbeans";
    final static String NB_CND_DIR = "cnd2";
    final static String NB_DIR = "nb6.1";
    
    public static final String LOCATION_LABEL_TEXT = 
            ResourceUtils.getString(NbLocationPanel.class, 
            "NBP.location.label.text"); // NOI18N
    public static final String LIST_LABEL_TEXT = 
            ResourceUtils.getString(NbLocationPanel.class, 
            "NBP.list.label.text"); // NOI18N
    public static final String DEFAULT_LOCATION = 
            ResourceUtils.getString(NbLocationPanel.class, 
            "NBP.default.location"); // NOI18N

      // public static final String ERROR_FAILED_VERIFY_INPUT_TEXT =
        //    ResourceUtils.getString(NbLocationPanel.class,
          //  "NBP.error.failed.input.verify");//NOI18N
}

/*
public class NbLocationPanel extends DestinationPanel {
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    private JdkLocationPanel jdkLocationPanel;
    
    public NbLocationPanel() {
        jdkLocationPanel = new JdkLocationPanel();
        
        setProperty(TITLE_PROPERTY,
                DEFAULT_TITLE);
        setProperty(DESCRIPTION_PROPERTY,
                DEFAULT_DESCRIPTION);
        
        setProperty(DESTINATION_LABEL_TEXT_PROPERTY,
                DEFAULT_DESTINATION_LABEL_TEXT);
        setProperty(DESTINATION_BUTTON_TEXT_PROPERTY,
                DEFAULT_DESTINATION_BUTTON_TEXT);
        
        setProperty(JDK_LOCATION_LABEL_TEXT_PROPERTY,
                DEFAULT_JDK_LOCATION_LABEL_TEXT);
        setProperty(BROWSE_BUTTON_TEXT_PROPERTY,
                DEFAULT_BROWSE_BUTTON_TEXT);
        
        setProperty(JdkLocationPanel.MINIMUM_JDK_VERSION_PROPERTY,
                DEFAULT_MINIMUM_JDK_VERSION);
        setProperty(JdkLocationPanel.MAXIMUM_JDK_VERSION_PROPERTY,
                DEFAULT_MAXIMUM_JDK_VERSION);
    }
    
    @Override
    public WizardUi getWizardUi() {
        if (wizardUi == null) {
            wizardUi = new NbBaseDestinationPanelUi(this);
        }
        
        return wizardUi;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        
        jdkLocationPanel.setWizard(getWizard());
        
        jdkLocationPanel.setProperty(
                JdkLocationPanel.MINIMUM_JDK_VERSION_PROPERTY,
                getProperty(JdkLocationPanel.MINIMUM_JDK_VERSION_PROPERTY));
        jdkLocationPanel.setProperty(
                JdkLocationPanel.MAXIMUM_JDK_VERSION_PROPERTY,
                getProperty(JdkLocationPanel.MAXIMUM_JDK_VERSION_PROPERTY));
        
        if (getProperty(JdkLocationPanel.PREFERRED_JDK_VERSION_PROPERTY) != null) {
            jdkLocationPanel.setProperty(
                    JdkLocationPanel.PREFERRED_JDK_VERSION_PROPERTY,
                    getProperty(JdkLocationPanel.PREFERRED_JDK_VERSION_PROPERTY));
        }
        List <Product> toInstall = Registry.getInstance().getProductsToInstall();
        jdkLocationPanel.setJreAllowed(true);
        for(Product product : toInstall) {
            String uid = product.getUid();
            if(uid.startsWith("nb-") && !uid.matches("nb-(base|cnd|php|ruby)")) {
                jdkLocationPanel.setJreAllowed(false);
                break;
            }
        }
        
        jdkLocationPanel.initialize();
        
        //This makes it possible to perform silent installation with emptry state files 
        //that means that JDK_LOCATION_PROPERTY property is explicitely set to the first location
        //that fits the requirements
        //TODO: Investigate the prons&cons and side affects of moving
        //this code to the end of JdkLocationPanel.initialize() method        
        File jdkLocation = jdkLocationPanel.getSelectedLocation();        
        if(jdkLocation!=null && !jdkLocation.getPath().equals(StringUtils.EMPTY_STRING)) {
            jdkLocationPanel.setLocation(jdkLocation);
        }        
    }
    
    public JdkLocationPanel getJdkLocationPanel() {
        return jdkLocationPanel;
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    // Inner Classes
    public static class NbBaseDestinationPanelUi extends DestinationPanelUi {
        protected NbLocationPanel panel;
        
        public NbBaseDestinationPanelUi(NbLocationPanel panel) {
            super(panel);
            
            
            this.panel = panel;
        }
        
        public SwingUi getSwingUi(SwingContainer container) {
            if (swingUi == null) {
                swingUi = new NbBaseDestinationPanelSwingUi(panel, container);
            }
            
            return super.getSwingUi(container);
        }
    }
    
    public static class NbBaseDestinationPanelSwingUi extends DestinationPanelSwingUi {
        protected NbLocationPanel panel;
        
        private NbiLabel jdkLocationLabel;
        private NbiComboBox jdkLocationComboBox;
        private NbiButton browseButton;
        private NbiLabel statusLabel;
        
        private NbiTextField jdkLocationField;
        
        private NbiDirectoryChooser fileChooser;
        
        public NbBaseDestinationPanelSwingUi(
                final NbLocationPanel panel,
                final SwingContainer container) {
            super(panel, container);
            
            this.panel = panel;
            
            initComponents();
        }
        
        // protected ////////////////////////////////////////////////////////////////
        @Override
        protected void initialize() {
            jdkLocationLabel.setText(
                    panel.getProperty(JDK_LOCATION_LABEL_TEXT_PROPERTY));
            
            final JdkLocationPanel jdkLocationPanel = panel.getJdkLocationPanel();
            
            if (jdkLocationPanel.getLocations().size() == 0) {
                final Version minVersion = Version.getVersion(jdkLocationPanel.getProperty(
                        JdkLocationPanel.MINIMUM_JDK_VERSION_PROPERTY));
                final Version maxVersion = Version.getVersion(jdkLocationPanel.getProperty(
                        JdkLocationPanel.MAXIMUM_JDK_VERSION_PROPERTY));
                
                statusLabel.setText(StringUtils.format(
                        jdkLocationPanel.getProperty(JdkLocationPanel.ERROR_NOTHING_FOUND_PROPERTY),
                        minVersion.toJdkStyle(),
                        minVersion.toJdkStyle()));
            } else {
                statusLabel.clearText();
                statusLabel.setVisible(false);
            }
            
            final List<File> jdkLocations = jdkLocationPanel.getLocations();                        
            final List<String> jdkLabels = jdkLocationPanel.getLabels();
            
            final LocationsComboBoxModel model = new LocationsComboBoxModel(
                    jdkLocations,
                    jdkLabels);            
            
            ((LocationsComboBoxEditor) jdkLocationComboBox.getEditor()).setModel(
                    model);
            jdkLocationComboBox.setModel(
                    model);
            
            final File selectedLocation = jdkLocationPanel.getSelectedLocation();
            final int index = jdkLocations.indexOf(selectedLocation);
            String selectedItem;
            if(index != -1) {
                  selectedItem = jdkLabels.get(index);  
            } else {
                  selectedItem = selectedLocation.toString();
            }  
            model.setSelectedItem(selectedItem);                        
            browseButton.setText(
                    panel.getProperty(BROWSE_BUTTON_TEXT_PROPERTY));
            
            super.initialize();
        }
        
        @Override
        protected void saveInput() {
            super.saveInput();
            
            panel.getJdkLocationPanel().setLocation(
                    new File(jdkLocationField.getText()));
        }
        
        @Override
        protected String validateInput() {
            String errorMessage = super.validateInput();
            if (errorMessage == null) {
                errorMessage = panel.getJdkLocationPanel().validateLocation(
                        jdkLocationField.getText());
            }
            
            return errorMessage;
        }
        
        // private //////////////////////////////////////////////////////////////////
        private void initComponents() {
            // selectedLocationField ////////////////////////////////////////////////
            jdkLocationField = new NbiTextField();
            jdkLocationField.getDocument().addDocumentListener(
                    new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    updateErrorMessage();
                }
                
                public void removeUpdate(DocumentEvent e) {
                 //   updateErrorMessage();  
                }
                
                public void changedUpdate(DocumentEvent e) {
                    updateErrorMessage();
                }
            });
            
            // jdkLocationComboBox //////////////////////////////////////////////////
            final LocationValidator validator = new LocationValidator() {
                public void validate(String location) {
                    jdkLocationField.setText(location);
                }
            };
            
            jdkLocationComboBox = new NbiComboBox();
            jdkLocationComboBox.setEditable(true);
            jdkLocationComboBox.setEditor(new LocationsComboBoxEditor(validator));
            jdkLocationComboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent event) {
                    final ComboBoxModel model = jdkLocationComboBox.getModel();
                    
                    if (model instanceof LocationsComboBoxModel) {
                        jdkLocationField.setText(
                                ((LocationsComboBoxModel) model).getLocation());
                    }
                }
            });
            
            // jdkLocationLabel /////////////////////////////////////////////////////
            jdkLocationLabel = new NbiLabel();
            jdkLocationLabel.setLabelFor(jdkLocationComboBox);
            
            // browseButton /////////////////////////////////////////////////////////
            browseButton = new NbiButton();
            browseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    browseButtonPressed();
                }
            });
            
            // statusLabel //////////////////////////////////////////////////////////
            statusLabel = new NbiLabel();
            
            // fileChooser //////////////////////////////////////////////////////////
            fileChooser = new NbiDirectoryChooser();
            
            // this /////////////////////////////////////////////////////////////////
            add(jdkLocationLabel, new GridBagConstraints(
                    0, 2,                             // x, y
                    2, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(11, 11, 0, 11),        // padding
                    0, 0));                           // padx, pady - ???
            add(jdkLocationComboBox, new GridBagConstraints(
                    0, 3,                             // x, y
                    1, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(4, 11, 0, 0),          // padding
                    0, 0));                           // padx, pady - ???
            add(browseButton, new GridBagConstraints(
                    1, 3,                             // x, y
                    1, 1,                             // width, height
                    0.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(4, 4, 0, 11),          // padding
                    0, 0));                           // padx, pady - ???
            add(statusLabel, new GridBagConstraints(
                    0, 4,                             // x, y
                    2, 1,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(4, 11, 0, 11),         // padding
                    0, 0));                           // padx, pady - ???
        }
        
        private void browseButtonPressed() {
            fileChooser.setSelectedFile(new File(jdkLocationField.getText()));
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                jdkLocationComboBox.getModel().setSelectedItem(
                        fileChooser.getSelectedFile().getAbsolutePath());
            }
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String JDK_LOCATION_LABEL_TEXT_PROPERTY =
            "nbBase.location.label.text"; // NOI18N
    public static final String BROWSE_BUTTON_TEXT_PROPERTY =
            "browse.button.text"; // NOI18N
    
    public static final String DEFAULT_TITLE =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.title"); // NOI18N
    public static final String DEFAULT_DESCRIPTION =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.description"); // NOI18N
    
    public static final String DEFAULT_DESTINATION_LABEL_TEXT =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.destination.label.text"); // NOI18N
    public static final String DEFAULT_DESTINATION_BUTTON_TEXT =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.destination.button.text"); // NOI18N
    
    public static final String DEFAULT_JDK_LOCATION_LABEL_TEXT =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.nbBase.location.label.text"); // NOI18N
    public static final String DEFAULT_BROWSE_BUTTON_TEXT =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.browse.button.text"); // NOI18N
    
    public static final String DEFAULT_MINIMUM_JDK_VERSION =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.minimum.nbBase.version"); // NOI18N
    public static final String DEFAULT_MAXIMUM_JDK_VERSION =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.maximum.nbBase.version"); // NOI18N
}

*/