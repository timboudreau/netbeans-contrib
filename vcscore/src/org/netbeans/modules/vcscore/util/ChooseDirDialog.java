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

/** Select directory dialog.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class ChooseDirDialog extends JDialog {
    private Debug E=new Debug("ChooseDirDialog", false); // NOI18N
    private Debug D=E;

    private JFileChooser chooser=null ;
    private File initialDir=null;
    private File selectedDir=null;


    //-------------------------------------------
    static final long serialVersionUID =3391153941140021894L;
    public ChooseDirDialog(Frame owner, File initialDir){
        super( owner, "", true ); // NOI18N
        setTitle( g("CTL_Select_directory") ); // NOI18N
        this.initialDir=initialDir;
        initComponents();
        pack();
    }


    //-------------------------------------------
    private void initComponents(){
        chooser = new JFileChooser ();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileHidingEnabled(false);

        if( initialDir != null ){
            chooser.setCurrentDirectory(initialDir);
        }
        chooser.setSelectedFile (initialDir);
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
            E.err(e,"getSelectedDir()"); // NOI18N
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
