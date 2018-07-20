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

package org.netbeans.modules.vcscore.util;
import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;

import org.openide.util.*;
import org.openide.filesystems.FileUtil;

/** Select file dialog.
 * This class provides a dialog for file selection.
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public class ChooseFileDialog extends JDialog {

    private JFileChooser chooser = null ;
    private File initialDir = null;
    private File selectedFile = null;
    private boolean propFileFilter = false;

    class PropertiesFileFilter extends javax.swing.filechooser.FileFilter {
        private static final String EXTENSION = "properties"; // NOI18N
        public boolean accept(File f) {
            int dotIndex = f.getName ().indexOf ("."); // NOI18N
            String ext = ""; // NOI18N
            if(dotIndex>0) ext = f.getName ().substring(dotIndex+1);
            if(ext.equals(EXTENSION)) return true;
            else return false;
        }
        public String getDescription() {
            return "Properties files (*." + EXTENSION + ")"; // NOI18N
        }
    }

    //-------------------------------------------
    static final long serialVersionUID =-4725583654994487624L;
    
    public ChooseFileDialog(Frame owner, File initialDir, boolean propFileFilter) {
        super(owner, g("CTL_Select_file"), true); // NOI18N
        this.initialDir = initialDir;
        this.propFileFilter = propFileFilter;
        initComponents();
        pack();
    }

    public ChooseFileDialog(Dialog owner, File initialDir, boolean propFileFilter) {
        super(owner, g("CTL_Select_file"), true); // NOI18N
        this.initialDir = initialDir;
        this.propFileFilter = propFileFilter;
        initComponents();
        pack();
    }


    //-------------------------------------------
    private void initComponents() {
        chooser = new JFileChooser ();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileHidingEnabled(false);
        if (propFileFilter) chooser.setFileFilter (new PropertiesFileFilter ());

        if (initialDir != null) {
            chooser.setCurrentDirectory(initialDir);
        }
        chooser.setApproveButtonText( g("CTL_Select") ); // NOI18N
        chooser.setApproveButtonToolTipText( g("CTL_SelectToolTip") ); // NOI18N

        // attach cancel also to Escape key
        getRootPane().registerKeyboardAction
        (new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 selectedFile=null;
                 close();
             }
         },
         javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true),
         javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        addKeyListener (new java.awt.event.KeyAdapter () {
                            public void keyPressed (java.awt.event.KeyEvent evt) {
                                if (evt.getKeyCode () == java.awt.event.KeyEvent.VK_ESCAPE) {
                                    selectedFile=null;
                                    close();
                                }
                            }
                        });

        getContentPane ().setLayout (new java.awt.BorderLayout ());
        getContentPane ().add (chooser, java.awt.BorderLayout.CENTER);

        chooser.addActionListener (new ActionListener () {
                                       public void actionPerformed (ActionEvent evt) {
                                           if (JFileChooser.APPROVE_SELECTION.equals (evt.getActionCommand ())) {
                                               File f = chooser.getSelectedFile ();
                                               selectedFile=f;
                                               close();
                                           } else if (JFileChooser.CANCEL_SELECTION.equals (evt.getActionCommand ())) {
                                               selectedFile=null;
                                               close();
                                           }
                                       }
                                   });

    }


    //-------------------------------------------
    private void close() {
        setVisible (false);
        dispose ();
    }


    //-------------------------------------------
    /** Returns selected file or null if no file was selected.
     */
    public String getSelectedFile() {
        String path = null;
        if (selectedFile == null) {
            return null;
        }
        try {
            path = selectedFile.getCanonicalPath ();
        } catch (IOException e){
            path = null;
        }
        return path;
    }


    //-------------------------------------------
    private static String g(String s) {
        return NbBundle.getMessage(ChooseFileDialog.class, s);
    }
    //-------------------------------------------


}
