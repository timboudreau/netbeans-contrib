/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.installer.products.sample;

import java.io.File;
import org.netbeans.installer.product.components.NativeClusterConfigurationLogic;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.product.Registry;


/**
 *
 * @author Igor Nikiforov
 */
public class ConfigurationLogic extends NativeClusterConfigurationLogic {

    @Override
    public void install(Progress progress) throws InstallationException {
        this.getProduct().setInstallationLocation(new File("/tmp"));
        super.install(progress);
    }

}
