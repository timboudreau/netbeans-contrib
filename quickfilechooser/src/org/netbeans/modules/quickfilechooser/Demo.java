/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.quickfilechooser;

import java.io.File;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;

public class Demo {
    
    public static void main(String[] args) {
        Install.main(null);
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(new File(System.getProperty("java.io.tmpdir")));
        //chooser.setAccessory(new JLabel("extra"));
        chooser.setFileView(new FileView() {
            public Icon getIcon(File f) {
                if (f.getName().endsWith(".gif") || f.getName().endsWith(".png")) {
                    Icon icon = new ImageIcon(f.getAbsolutePath());
                    if (icon.getIconWidth() == 16 && icon.getIconHeight() == 16) {
                        return icon;
                    }
                }
                return null;
            }
        });
        //chooser.setControlButtonsAreShown(false);
        chooser.showOpenDialog(null);
        System.out.println("Selected: " + Arrays.asList(chooser.getSelectedFiles()));
        System.exit(0);
    }
    
}
