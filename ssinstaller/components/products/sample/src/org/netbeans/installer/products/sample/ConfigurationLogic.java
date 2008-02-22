package org.netbeans.installer.products.sample;

import java.util.Arrays;
import java.util.List;
import org.netbeans.installer.product.components.ProductConfigurationLogic;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.components.WizardComponent;
import org.netbeans.installer.wizard.components.panels.DestinationPanel;

public class ConfigurationLogic extends ProductConfigurationLogic {

    @Override
    public void install(Progress progress) throws InstallationException {
      progress.setPercentage(Progress.COMPLETE);
    }

    @Override
    public void uninstall(Progress progress) throws UninstallationException {
        progress.setPercentage(Progress.COMPLETE);
    }

   
    @Override
    public List<WizardComponent> getWizardComponents() {
       // return Collections.EMPTY_LIST;
        return Arrays.asList((WizardComponent) new DestinationPanel());
    }

}
