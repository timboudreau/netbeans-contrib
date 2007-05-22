package com.sun.jbi.sapbc.sapwsdlgenerator.explorer;

import com.sun.jbi.sapbc.sapwsdlgenerator.explorer.util.FileSelector;
import java.awt.Dialog;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.NewType;

/**
 * Represents a new SAP Library node type that can be repeatedly created.
 */
public class SAPLibraryNewType extends NewType {
    
    public SAPLibraryNewType() {
    }

    public String getName() {
        return bundle.getString("SAPLibraryNewType.display_name");
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx("com.sun.jbi.sapbapibc.explorer");
    }

    public void create() throws IOException {
        FileSelector selector = new FileSelector();
        
        DialogDescriptor desc = new DialogDescriptor(
            selector.getComponent(),
            bundle.getString("SAPLibraryNewType.create_prompt_title"),
            true,
            null);
        
        Dialog dlg = DialogDisplayer.getDefault().createDialog(desc);
        dlg.setVisible(true);
        
        if (DialogDescriptor.OK_OPTION.equals(desc.getValue())) {
            File libraryPath = selector.getLibraryPath();
            if (libraryPath != null) {
                SAPComponentsNotifier.notifyLibraryAdded(libraryPath);
            }
        }
    }
    
    private static ResourceBundle bundle = NbBundle.getBundle(SAPLibraryNewType.class);
}
