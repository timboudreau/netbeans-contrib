/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * Main.java
 *
 * Created on April 27, 2004, 12:01 AM
 */

package org.netbeans.modules.hexedit;

import org.netbeans.modules.hexedit.HexEditPanel;

import javax.swing.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.*;

/**
 * Standalone app version of the hex editor
 *
 * @author  Tim Boudreau
 */
public class Main extends JFrame {
    private HexEditPanel editor = new HexEditPanel();
    private JFileChooser jfc;

    /** Creates a new instance of Main */
    public Main() {
        initComponents();
        setBounds (20, 20, 500, 400);
    }

    void initComponents() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        JMenuBar jbm = new JMenuBar();

        JMenu fileMenu = new JMenu (Util.getMessage("MENU_FILE")); //NOI18N
        jbm.add (fileMenu);

        Action openAction = new OpenAction();

        JMenuItem openItem = new JMenuItem (openAction); //NOI18N

        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                "open"); //NOI18N
        getRootPane().getActionMap().put("open", openAction); //NOI18N

        fileMenu.add(openItem);

        setJMenuBar(jbm);
        getContentPane().setLayout (new BorderLayout());
        getContentPane().add(editor, BorderLayout.CENTER);
    }

    private class OpenAction extends AbstractAction {
        public OpenAction () {
            putValue (NAME, Util.getMessage("MENUITEM_OPEN"));
        }
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            showFileDialog();
        }

    }




    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new Main().setVisible(true);
    }


    private void showFileDialog() {
        try {
            if (jfc == null) {
                jfc = new JFileChooser("/tmp/killme"); //XXX
            }
            int returnVal = jfc.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File f = jfc.getSelectedFile();
                if (f.length() > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException (
                            Util.formatMessage("MSG_FILE_TOO_LARGE", new Object[]{f})); //NOI18N
                }
                if (f.isFile() && f.exists() && f.canRead()) {
                    RandomAccessFile raf = new RandomAccessFile (f, f.canWrite() ? "rw" : "r");

                    FileChannel channel = raf.getChannel();
                    editor.setFileChannel (channel, (int) f.length());
                }
                editor.setName(f.toString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
