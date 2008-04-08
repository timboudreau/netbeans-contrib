package org.netbeans.installer.products.sample;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.NativeClusterConfigurationLogic;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.product.components.ProductConfigurationLogic;
import org.netbeans.installer.products.sample.panels.SSBasePanel;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.RemovalMode;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.components.WizardComponent;

public class ConfigurationLogic extends ProductConfigurationLogic {

    @Override
    public void install(Progress progress) throws InstallationException {
      progress.setPercentage(Progress.COMPLETE);
    }

    @Override
    public void uninstall(Progress progress) throws UninstallationException {        
        for (Product product : Registry.getInstance().getProducts()) {
            try {
                if (!(product.getLogic() instanceof NativeClusterConfigurationLogic)) {
                    continue;
                }
                product.getLogic().uninstall(progress);        
                product.getParent().removeChild(product);                
            } catch (InitializationException ex) {
                LogManager.log("Unexpected exception during removal of " + product.getDisplayName());
            }
        }
        File spro = new File(getProduct().getInstallationLocation(), "SUNWspro");
        try {
            FileUtils.deleteFile(spro, true);
        } catch (IOException ex) {
             LogManager.log("Unexpected exception during removal of " + spro.getAbsolutePath());
        }
        progress.setPercentage(Progress.COMPLETE);        
    }

   
    @Override
    public List<WizardComponent> getWizardComponents() {
       // return Collections.EMPTY_LIST;s
        return Arrays.asList((WizardComponent) new SSBasePanel());
    }

    @Override
    public RemovalMode getRemovalMode() {
        return RemovalMode.LIST;
    }

    
}
