package org.netbeans.modules.perspective.options;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.modules.perspective.PerspectiveManager;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class PerspectiveOptionsCategory extends OptionsCategory {

    @Override
    public Icon getIcon() {
        return new ImageIcon(Utilities.loadImage("org/netbeans/modules/perspective/options/multi view32.png"));
    }

    public String getCategoryName() {
        return NbBundle.getMessage(PerspectiveManager.class, "OptionsCategory_Name_Perspective");
    }

    public String getTitle() {
        return NbBundle.getMessage(PerspectiveManager.class, "OptionsCategory_Title_Perspective");
    }

    public OptionsPanelController create() {
        return new PerspectiveOptionsPanelController();
    }
}