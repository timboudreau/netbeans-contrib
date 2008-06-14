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

package org.netbeans.modules.quickfilechooser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileView;

public class Demo {

    public static void main(String[] args) {
        Install.main(null);
        final JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //chooser.setCurrentDirectory(new File(System.getProperty("java.io.tmpdir")));
        File contrib = new File(System.getProperty("contrib"));
        chooser.setCurrentDirectory(contrib);
        chooser.setSelectedFiles(new File[] {new File(contrib, "docbook")});
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
        /*
        chooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() ||
                        pathname.getName().toLowerCase().endsWith(".html");
            }
            public String getDescription() {
                return "HTML Files";
            }
        });
         */
        //chooser.setControlButtonsAreShown(false);
        class Accessory extends JTextArea implements PropertyChangeListener {
            public Accessory() {
                super(4, 20);
                setLineWrap(true);
                update();
                chooser.addPropertyChangeListener(this);
            }
            private void update() {
                StringBuffer buf = new StringBuffer();
                buf.append("Selected file: ");
                buf.append(chooser.getSelectedFile());
                buf.append('\n');
                buf.append("Selected file list: ");
                buf.append(Arrays.asList(chooser.getSelectedFiles()));
                buf.append('\n');
                buf.append("Current dir: ");
                buf.append(chooser.getCurrentDirectory());
                setText(buf.toString());
                setCaretPosition(getText().length());
            }
            public void propertyChange(PropertyChangeEvent evt) {
                update();
            }
        }
        JScrollPane accessory = new JScrollPane(new Accessory());
        accessory.setBorder(BorderFactory.createTitledBorder("Chooser Properties"));
        chooser.setAccessory(accessory);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("Selected: " + Arrays.asList(chooser.getSelectedFiles()));
        }
        System.exit(0);
    }
    
}
