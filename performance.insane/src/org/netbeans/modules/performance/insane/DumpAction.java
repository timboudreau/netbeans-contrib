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
                int diff = counter.getCountForClass((Class)c2) - 
                           counter.getCountForClass((Class)c1);

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
