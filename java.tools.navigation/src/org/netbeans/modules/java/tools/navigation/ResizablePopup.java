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
package org.netbeans.modules.java.tools.navigation;

import org.openide.windows.WindowManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDialog;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
class ResizablePopup {
    private static JDialog javaStructureDialog;

    static JDialog getJavaFileStructureDialog() {
        if (javaStructureDialog == null) {
            javaStructureDialog = new JDialog(WindowManager.getDefault()
                                                           .getMainWindow(),
                    "", false) {
                        public void setVisible(boolean visible) {
                            super.setVisible(visible);

                            if (!visible) {
                                getContentPane().removeAll();
                            }
                        }
                    };
            javaStructureDialog.setUndecorated(true);
            javaStructureDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            javaStructureDialog.getContentPane().setLayout(new BorderLayout());

            Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();
            javaStructureDialog.setBounds(((dimensions.width / 2) - 410),
                ((dimensions.height / 2) - 300), 820, 600);
        }

        return javaStructureDialog;
    }
}
