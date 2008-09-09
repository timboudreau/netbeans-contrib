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
import java.util.List;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.helper.Status;
import org.netbeans.installer.wizard.FileLocationValidator;
import org.netbeans.installer.wizard.Utils;
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
    FileLocationValidator flv;

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
        flv = new FileLocationValidator();
    }

    @Override
    public void initialize() {
        super.initialize();
        final Product product = (Product) getWizard().
                getContext().
                get(Product.class);
        selectedLocation = resolvePath(product.getProperty(Product.INSTALLATION_LOCATION_PROPERTY));
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
    
    
    private String validateNetBeansLocation(File nbRoot) {       
        if (!nbRoot.isDirectory()) {
            return ERROR_FAILED_NOT_DIRECTORY;
        }
        
        if (nbRoot.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals(NB_DIR);
            }
        }).length == 0) {
            return StringUtils.format(ERROR_FAILED_NOT_CONTAIN, NB_DIR);
        }
               
        if (nbRoot.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals(NB_CND_DIR);
            }
        }).length == 0) {
            return StringUtils.format(ERROR_FAILED_NOT_CONTAIN, NB_CND_DIR);
        }
        
        Product nbProduct = Registry.getInstance().getProducts(NB_PRODUCT_UID).get(0);
        Product nbExtra = Registry.getInstance().getProducts(NB_EXTRA_UID).get(0);
        nbExtra.setInstallationLocation(nbRoot);
        nbProduct.setStatus(Status.NOT_INSTALLED);
        nbProduct.setInstallationLocation(nbRoot);        
        nbProduct.setParent(Utils.getSSBase());
        nbExtra.setParent(Utils.getSSBase());
        // install CND pack if needed
        // looks not planned in current release...
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
        File file = FileUtils.eliminateRelativity(value);
        if (locations.contains(file)) {
            return validateNetBeansLocation(file);
        }
        String error = flv.validateInput(value);
        if (error != null) {
            return error;
        }
        Product nbProduct = Registry.getInstance().getProducts(NB_PRODUCT_UID).get(0);
        Product nbExtra = Registry.getInstance().getProducts(NB_EXTRA_UID).get(0);
        nbProduct.setStatus(Status.TO_BE_INSTALLED);
        nbProduct.setInstallationLocation(file);
        nbExtra.setInstallationLocation(file);
        nbProduct.setParent(Utils.getSSBase());
        nbExtra.setParent(Utils.getSSBase());
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
                if (!locations.contains(location)
                        && location.canWrite()) {
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

            
    public static final String DEFAULT_TITLE =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.title"); // NOI18N
    public static final String DEFAULT_DESCRIPTION =
            ResourceUtils.getString(PreInstallSummaryPanel.class,
            "NPrISP.description"); // NOI18N
    
    public static final String LOCATION_LABEL_TEXT = 
            ResourceUtils.getString(NbLocationPanel.class, 
            "NBP.location.label.text"); // NOI18N
    public static final String LIST_LABEL_TEXT = 
            ResourceUtils.getString(NbLocationPanel.class, 
            "NBP.list.label.text"); // NOI18N
  
    public static final String ERROR_FAILED_NOT_DIRECTORY =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.error.not.directory");//NOI18N

    public static final String ERROR_FAILED_NOT_CONTAIN =
            ResourceUtils.getString(NbLocationPanel.class,
            "NBP.error.not.contain");//NOI18N

    
      // public static final String ERROR_FAILED_VERIFY_INPUT_TEXT =
        //    ResourceUtils.getString(NbLocationPanel.class,
          //  "NBP.error.failed.input.verify");//NOI18N
}

