package ramos.localhistory.ui;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

public final class LocalHistoryAdvancedOption extends AdvancedOption {
  
  public String getDisplayName() {
    return NbBundle.getMessage(LocalHistoryAdvancedOption.class, "AdvancedOption_DisplayName");
  }
  
  public String getTooltip() {
    return NbBundle.getMessage(LocalHistoryAdvancedOption.class, "AdvancedOption_Tooltip");
  }
  
  public OptionsPanelController create() {
    return new LocalHistoryOptionsPanelController();
  }
  
}
