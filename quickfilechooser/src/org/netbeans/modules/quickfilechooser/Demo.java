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
        chooser.setCurrentDirectory(new File("/space/src/nb_all/contrib"));
        chooser.setSelectedFiles(new File[] {new File("/space/src/nb_all/contrib/docbook")});
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
