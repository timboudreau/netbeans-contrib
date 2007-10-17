/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

        Set<Class> ordered = new TreeSet<Class>(new Comparator<Class>() {
            public int compare(Class c1, Class c2) {
                int diff = counter.getSizeForClass(c2) - 
                           counter.getSizeForClass(c1);

                if (diff != 0 || c1 == c2) return diff;
                return c1.getName().compareTo(c2.getName());
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
