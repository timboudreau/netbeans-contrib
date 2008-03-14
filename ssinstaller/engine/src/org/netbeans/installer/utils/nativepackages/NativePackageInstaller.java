/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.installer.utils.nativepackages;

import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;

/**
 *
 * @author Igor Nikiforov
 */
public interface NativePackageInstaller {
    
    void setDestinationPath(String path);
        
    void install(String pathToPackage, Product product) throws InstallationException;
    
    void uninstall(Product product) throws UninstallationException;
    
    boolean isCorrectPackageFile(String pathToPackage);

}
