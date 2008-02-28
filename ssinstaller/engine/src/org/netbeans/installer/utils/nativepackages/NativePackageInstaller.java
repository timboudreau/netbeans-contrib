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
        
    boolean install(String pathToPackage, Product product);
    
    boolean uninstall(Product product);
    
    boolean isCorrectPackageFile(String pathToPackage);

}
