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
