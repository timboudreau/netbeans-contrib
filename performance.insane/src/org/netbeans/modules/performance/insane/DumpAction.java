/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.performance.insane;


import javax.swing.JFileChooser;

import org.openide.util.*;
import org.netbeans.performance.insane.Insane;

/**
 * Action for UI-driven heap dump.
 *
 * @author nenik
 */
public final class DumpAction extends javax.swing.AbstractAction {
    
    public DumpAction() {
        putValue(NAME, NbBundle.getMessage(getClass(), "LBL_DumpHeap"));
        putValue(SMALL_ICON, new javax.swing.ImageIcon(Utilities.loadImage(
                    "org/netbeans/modules/performance/insane/dumpHeap.png"))); // NOI18N
        putValue("iconBase","org/netbeans/modules/performance/insane/dumpHeap.png"); //NOI18N
    }

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(NbBundle.getMessage(getClass(), "LBL_SaveChooserTitle"));
	String buttonText = NbBundle.getMessage(getClass(), "LBL_DumpButton");
        if (JFileChooser.APPROVE_OPTION == chooser.showDialog(null, buttonText)) {
            java.io.File file = chooser.getSelectedFile();
            try {
                Insane.dump(new Insane.XMLVisitor(file.getAbsolutePath()));
            } catch (Exception e) {
                org.openide.ErrorManager.getDefault().notify(e);
            }
        }
    }
}
