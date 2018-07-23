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

/** Select directory dialog.
 *
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class ChooseDirDialog extends JDialog {

    private JFileChooser chooser=null ;
    private File initialDir=null;
    private File selectedDir=null;


    //-------------------------------------------
    static final long serialVersionUID =3391153941140021894L;
    
    public ChooseDirDialog(Dialog owner, File initialDir) {
        super(owner, g("CTL_Select_directory"), true);
        this.initialDir=initialDir;
        initComponents();
        pack();
    }
    
    public ChooseDirDialog(Frame owner, File initialDir) {
        super(owner, g("CTL_Select_directory"), true); // NOI18N
        this.initialDir=initialDir;
        initComponents();
        pack();
    }


    //-------------------------------------------
    private void initComponents(){
        chooser = new JFileChooser ();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileHidingEnabled(false);

        if( initialDir != null ){
            if (Utilities.getOperatingSystem() != Utilities.OS_VMS) {
                chooser.setCurrentDirectory(initialDir);
            }
        }
        if (Utilities.getOperatingSystem() != Utilities.OS_VMS) {
            chooser.setSelectedFile (initialDir);
        }
        chooser.setApproveButtonText( g("CTL_Select") ); // NOI18N
        chooser.setApproveButtonToolTipText( g("CTL_SelectToolTip") ); // NOI18N

        // attach cancel also to Escape key
        getRootPane().registerKeyboardAction
        (new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 selectedDir=null;
                 close();
             }
         },
         javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true),
         javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        addKeyListener (new java.awt.event.KeyAdapter () {
                            public void keyPressed (java.awt.event.KeyEvent evt) {
                                if (evt.getKeyCode () == java.awt.event.KeyEvent.VK_ESCAPE) {
                                    selectedDir=null;
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
                                               selectedDir=f;
                                               close();
                                           } else if (JFileChooser.CANCEL_SELECTION.equals (evt.getActionCommand ())) {
                                               selectedDir=null;
                                               close();
                                           }
                                       }
                                   });

    }


    //-------------------------------------------
    private void close(){
        setVisible (false);
        dispose ();
    }


    //-------------------------------------------
    /** Returns selected dir or null if no dir was selected.
     */
    public String getSelectedDir(){
        String path=null;
        if( selectedDir==null ){
            return null;
        }
        try{
            path=selectedDir.getCanonicalPath();
        }catch (IOException e){
            path=null;
        }
        return path;
    }


    //-------------------------------------------
    private static String g(String s) {
        return NbBundle.getMessage(ChooseDirDialog.class, s);
    }
    //-------------------------------------------


}
