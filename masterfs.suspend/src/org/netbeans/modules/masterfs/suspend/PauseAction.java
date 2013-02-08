/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.masterfs.suspend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

@ActionID(
    category = "System",
    id = "org.netbeans.core.ui.warmup.PauseAction"
)
@ActionRegistration(
    iconBase = "org/netbeans/modules/masterfs/suspend/pause.png",
    displayName = "#CTL_PauseAction"
)
@ActionReference(path = "Toolbars/Memory", position = 15000)
@Messages({
    "CTL_PauseAction=Pause I/O Checks",
    "# {0} - number of pending events",
    "MSG_Resume=Resume (pending events: {0})"
})
public final class PauseAction implements ActionListener {

    private static final Color FILL_COLOR = new Color( 128, 128, 128, 128 );
    @Override
    public void actionPerformed(ActionEvent e) {
        suspend(1);

        JFrame mainWnd = ( JFrame ) WindowManager.getDefault().getMainWindow();
        Component oldGlass = mainWnd.getGlassPane();
        JPanel newGlass = new JPanel() {

            @Override
            public void paint( Graphics g ) {
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setColor( FILL_COLOR );
                g2d.fillRect( 0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        newGlass.setOpaque( false );
        mainWnd.setGlassPane( newGlass );
        newGlass.setVisible( true );
        final JButton btnResume = new JButton();
        btnText(btnResume);
        final JDialog dlg = new JDialog( mainWnd, true );
        dlg.setUndecorated( true );
        dlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        dlg.getContentPane().setLayout( new BorderLayout());
        dlg.getContentPane().add(  btnResume, BorderLayout.CENTER );
        dlg.pack();
        dlg.setLocationRelativeTo( mainWnd );
        class C implements Runnable, ActionListener {
            private RequestProcessor.Task t;
            
            public C() {
                t = RequestProcessor.getDefault().create(this);
                t.schedule(1500);
            }
            
            @Override
            public void run() {
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(this);
                    return;
                }
                btnText(btnResume);
                dlg.pack();
                t.schedule(1500);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                suspend(-1);
                btnResume.setEnabled(false);
                dlg.setVisible(false);
            }
        }
        C c = new C();
        btnResume.addActionListener( c );
        dlg.setVisible( true );
        newGlass.setVisible( false );
        mainWnd.setGlassPane( oldGlass );
    }
    
    final void btnText(JButton btnResume) {
        int pending = Integer.getInteger("org.netbeans.io.pending", 0); // NOI18N
        btnResume.setText(Bundle.MSG_Resume(pending));
    }

    static void suspend(int delta) {
        final String prop = "org.netbeans.io.suspend".intern(); // NOI18N
        synchronized (prop) {
            int prev = Integer.getInteger(prop, 0);
            prev += delta;
            System.setProperty(prop, "" + prev);
            prop.notifyAll();
        }
    }
}
