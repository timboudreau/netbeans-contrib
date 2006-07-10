/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.performance.insane;


import javax.swing.JFileChooser;

import java.io.File;
import java.util.*;

import org.openide.util.*;

import org.netbeans.insane.scanner.*;

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
                dump(file);
            } catch (Exception e) {
                org.openide.ErrorManager.getDefault().notify(e);
            }
        }
    }
    
    public static void dump(File file) throws Exception {
        SimpleXmlVisitor visitor = new SimpleXmlVisitor(file);
        final CountingVisitor counter = new CountingVisitor();
        ScannerUtils.scanExclusivelyInAWT(
            null, ScannerUtils.compoundVisitor(new Visitor[] { visitor, counter}),
            Collections.singleton(Thread.currentThread().getContextClassLoader())
        );

        visitor.close();

        Set ordered = new TreeSet(new Comparator() {
            public int compare(Object c1, Object c2) {
                int diff = counter.getSizeForClass((Class)c2) - 
                           counter.getSizeForClass((Class)c1);

                if (diff != 0 || c1 == c2) return diff;
                return ((Class)c1).getName().compareTo(((Class)c2).getName());
            }
        });

        ordered.addAll(counter.getClasses());

        System.out.println("Usage: [instances class.Name: totalSizeInBytes]");
        for (Iterator it = ordered.iterator(); it.hasNext();) {
            Class cls = (Class)it.next();
            System.out.println(counter.getCountForClass(cls) + " " +
                            cls.getName() + ": " + counter.getSizeForClass(cls));
        }

        System.out.println("total: " + counter.getTotalSize() + " in " +
            counter.getTotalCount() + " objects.");
        System.out.println("Classes:" + counter.getClasses().size());
    }
}
