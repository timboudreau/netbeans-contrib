/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
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

/** Select file dialog.
 * This class provides a dialog for file selection.
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public class ChooseFileDialog extends JDialog {
    private Debug E=new Debug("ChooseFileDialog", false); // NOI18N
    private Debug D=E;

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
            E.err(e,"getSelectedFile()"); // NOI18N
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
