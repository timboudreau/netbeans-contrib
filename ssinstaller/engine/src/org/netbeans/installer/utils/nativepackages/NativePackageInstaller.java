/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.installer.utils.nativepackages;

import org.netbeans.installer.product.components.Product;

/**
 *
 * @author Igor Nikiforov
 */
public interface NativePackageInstaller {
    
    void setDestinationPath(String path);
        
    void install(String pathToPackage, Product product) throws InstallationException;
    
    void uninstall(Product product) throws InstallationException;
    
    boolean isCorrectPackageFile(String pathToPackage);

}
