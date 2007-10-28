/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.fileopenserver;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class FileOpenServerOptionsCategory extends OptionsCategory {

    public Icon getIcon() {
        return new ImageIcon(Utilities.loadImage("org/netbeans/modules/fileopenserver/resources/OpenFileInExternalEditorIcon32x32.gif"));
    }

    public String getCategoryName() {
        return NbBundle.getMessage(FileOpenServerOptionsCategory.class, "OptionsCategory_Name_FileOpenServer");
    }

    public String getTitle() {
        return NbBundle.getMessage(FileOpenServerOptionsCategory.class, "OptionsCategory_Title_FileOpenServer");
    }

    public OptionsPanelController create() {
        return new FileOpenServerOptionsPanelController();
    }
}
