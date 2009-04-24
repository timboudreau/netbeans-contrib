/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.breadcrumb;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.ViewCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tim Boudreau
 */
final class BreadcrumbComponent extends JPanel implements 
                                               LookupListener, 
                                               MouseListener, 
                                               Runnable, WindowListener {
    
    private final JScrollPane pane = new JScrollPane();
    private final JPanel panel = new JPanel();
    private final Set<String> ignored = new HashSet<String>();
    private Lookup.Result<DataObject> res;
    private Font smallFont;
    private Font smallBoldFont;
    private int maxCrumbs = NbPreferences.forModule(
            BreadcrumbComponent.class).getInt("maxCrumbs", 15);

    public BreadcrumbComponent() {
        super(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        pane.setViewportView(panel);
        pane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setViewportBorder(BorderFactory.createEmptyBorder());
        pane.setOpaque(false);
        pane.getViewport().setOpaque(false);
        panel.setOpaque(false);
        JScrollBar bar = pane.getHorizontalScrollBar();
        bar.setToolTipText(NbBundle.getMessage(BreadcrumbComponent.class,
                "TIP_INSTRUCTIONS")); //NOI18N
        panel.setToolTipText(bar.getToolTipText());
        pane.setToolTipText(bar.getToolTipText());
        bar.setOpaque(false);
        if (Utilities.getOperatingSystem() == Utilities.OS_MAC) {
            //May eventually be useful on Mac OS to get a mini-scrollbar
            bar.putClientProperty("JComponent.sizeVariant", "mini"); //NOI18N
        }
        Dimension d = bar.getPreferredSize();
        d.height = 10;
        bar.setPreferredSize(d);
        setMinimumSize(new Dimension(300, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        super.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        MouseListener popupAdapter = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                popup (evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                popup (evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                popup (evt);
            }
            
            private void popup(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    JPopupMenu m = new JPopupMenu(
                            NbBundle.getMessage(BreadcrumbComponent.class, 
                            "MENU_MAX")); //NOI18N
                    m.add (addFileActions(null));
                    m.add (addCrumbCountActions(null));
                    m.show((Component) evt.getSource(), evt.getX(), evt.getY());
                }
            }
        };
        pane.addMouseListener (popupAdapter);
        pane.getViewport().addMouseListener (popupAdapter);
        bar.addMouseListener (popupAdapter);
        panel.addMouseListener (popupAdapter);
    }

    @Override
    public void setBorder(Border b) {
        //do nothing - toolbar ui will try to set it to null - we don't
        //want it to be null
    }

    @Override
    public void addNotify() {
        super.addNotify();
        startListening();
    }

    @Override
    public void removeNotify() {
        stopListening();
        super.removeNotify();
        Thread.dumpStack();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension result = super.getPreferredSize();
        Frame f = WindowManager.getDefault().getMainWindow();
//        result.width = Math.min(f.getWidth() / 3, result.width);
        result.width = Math.max(100, f.getWidth() / 3);
        return result;
    }

    public void resultChanged(LookupEvent arg0) {
        Collection<? extends DataObject> c = res.allInstances();
        if (c.size() == 0) {
            return;
        }
        final DataObject dob = c.iterator().next();
        final EditorCookie ec = dob.getLookup().lookup(EditorCookie.class);
        if (ec != null) {
            if (ec.getOpenedPanes() != null && ec.getOpenedPanes().length > 0) {
                addToBreadcrumb(dob);
            } else {
                //The file may be being double clicked
                ActionListener al = new ActionListener() {

                    public void actionPerformed(ActionEvent arg0) {
                        if (ec.getOpenedPanes() == null ||
                                ec.getOpenedPanes().length == 0) {
                            return;
                        }
                        addToBreadcrumb(dob);
                    }
                };
                Timer animTimer = new Timer(500, al);
                animTimer.setRepeats(false);
                animTimer.start();
            }
        }
    }

    static boolean metal = UIManager.getLookAndFeel() instanceof MetalLookAndFeel;
    private static final class FastLabel extends JLabel {
        FastLabel (String txt) {
            super (txt);
        }

        @Override
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            //do nothing
        }

        @Override
        public void paint (Graphics g) {
            if (metal) {
                super.paint (g);
            } else {
                Graphics2D gg = (Graphics2D) g;
                gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paint (g);
            }
        }
    }

    private void addToBreadcrumb(DataObject dob) {
        assert EventQueue.isDispatchThread();
        File file = FileUtil.toFile(dob.getPrimaryFile());
        if (panel.getComponentCount() > 0) {
            JLabel lbl = (JLabel) panel.getComponents()[panel.getComponentCount() -1];
            lbl.setFont (smallFont);
        }
        if (file != null) {
            String path = file.getPath();
            if (ignored.contains(path)) {
                return;
            }
            JLabel lbl = new FastLabel(dob.getName() + ">"); //NOI18N
            Icon icon = createIcon (dob);
            lbl.setIcon (icon);
            lbl.setIconTextGap(1);
            if (smallFont == null) {
                Font f = null;
                if (Utilities.isWindows()) {
                    float sz = lbl.getFont().getSize2D() - 3F;
                    f = new Font ("Small Fonts", Font.PLAIN, (int) sz);
                    smallFont = f;
                    smallBoldFont = new Font ("Small Fonts", Font.BOLD, (int) sz);
                    if (!"Small Fonts".equals(f.getName())) {
                        f = null;
                    } else {
                    }
                }
                if (f == null) {
                    f = lbl.getFont();
                    smallFont = f.deriveFont(Font.PLAIN, f.getSize2D() - 3F);
                    smallBoldFont = f.deriveFont(Font.BOLD, f.getSize2D() - 3F);
                }
            }
            lbl.setFont(smallBoldFont);
            for (Component c : panel.getComponents()) {
                JLabel l = (JLabel) c;
                if (path.equals(l.getToolTipText())) {
                    panel.remove(c);
                }
            }
            lbl.setToolTipText(path);
            lbl.addMouseListener(this);
            while (panel.getComponentCount() >= maxCrumbs) {
                panel.remove(0);
            }
            panel.add(lbl);
            panel.invalidate();
            panel.revalidate();
            panel.repaint();
            scrollToEnd();
        }
    }
    
    public Icon createIcon (DataObject ob) {
        Image img = ob.getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16);
        int kind = Utilities.getOperatingSystem() == Utilities.OS_MAC ?
            BufferedImage.TYPE_INT_ARGB_PRE : BufferedImage.TYPE_INT_ARGB;
        BufferedImage nue = new BufferedImage (img.getWidth(this) / 2,
                img.getHeight(this) / 2, kind);
        Graphics2D g = nue.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(img, AffineTransform.getScaleInstance(0.5D, 0.5D), this);
        g.dispose();
        return ImageUtilities.image2Icon(nue);
    }

    private void scrollToEnd() {
        //Defer this until after current event has been processed
        EventQueue.invokeLater(this);
    }

    public void run() {
        JScrollBar bar = pane.getHorizontalScrollBar();
        if (bar.isVisible()) {
            bar.setValue(bar.getMaximum() - bar.getModel().getExtent());
        }
    }
    
    private void showPopup (MouseEvent e) {
        final JLabel lbl = (JLabel) e.getSource();
        final String path = lbl.getToolTipText();
        showPopup(lbl, path, e);
    }

    private void showPopup(final JLabel lbl, final String path, MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        final String nm = new File (path).getName();
        menu.add (new AbstractAction() {
            {
                putValue(NAME, NbBundle.getMessage(BreadcrumbComponent.class,
                        "ACTION_GOTO", nm)); //NOI18N
            }

            public void actionPerformed(ActionEvent arg0) {
                goToFile(path);
            }

        });
        menu.add(new AbstractAction() {

            {
                putValue(NAME, NbBundle.getMessage(BreadcrumbComponent.class,
                        "ACTION_EXCLUDE", nm)); //NOI18N
            }

            public void actionPerformed(ActionEvent arg0) {
                ignore(path, lbl);
            }
        });
        JMenu sub = new JMenu (NbBundle.getMessage(BreadcrumbComponent.class, 
                "MENU_MAX")); //NOI18N
        addCrumbCountActions(sub);
        menu.add(addFileActions(null));
        menu.add (sub);
        menu.show(lbl, e.getX(), e.getY());
    }
    
    private JMenu addCrumbCountActions (JMenu menu) {
        if (menu == null) {
            menu = new JMenu(NbBundle.getMessage(BreadcrumbComponent.class, 
                "MENU_MAX")); //NOI18N
        }
        CrumbCountAction[] actions = new CrumbCountAction[] {
            new CrumbCountAction(5),
            new CrumbCountAction(10),
            new CrumbCountAction(15),
            new CrumbCountAction(25),
            new CrumbCountAction(50),
            new CrumbCountAction(Integer.MAX_VALUE),
        };
        for (CrumbCountAction a : actions) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(a);
            item.setSelected(a.shouldBeChecked());
            item.setEnabled (!item.isSelected());
            menu.add (item);
        }
        return menu;
    }
    
    private JMenu addFileActions (JMenu menu) {
        if (menu == null) {
            menu = new JMenu(NbBundle.getMessage(BreadcrumbComponent.class,
                    "MENU_FILES")); //NOI18N
        }
        DataObject dob = Utilities.actionsGlobalContext().lookup(
                DataObject.class);
        String selPath = ""; //NOI18N
        if (dob != null) {
            File f = FileUtil.toFile (dob.getPrimaryFile());
            if (f != null) {
                selPath = f.getPath();
            }
        }
        for (Component c : panel.getComponents()) {
            JLabel lbl = (JLabel) c;
            String path = lbl.getToolTipText();
            if (new File(path).exists()) {
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(new OpenFileAction(path));
                menu.add (item);
                item.setEnabled (!selPath.equals(path));
                item.setSelected (selPath.equals(path));
            }
        }
        return menu;
    }
    

    private void startListening() {
        res = Utilities.actionsGlobalContext().lookupResult(DataObject.class);
        res.addLookupListener(this);
        resultChanged(null);
    }

    private void stopListening() {
        res.removeLookupListener(this);
        res = null;
    }

    private void ignore(String path, JLabel lbl) {
        panel.remove(lbl);
        ignored.add(path);
        invalidate();
        revalidate();
        repaint();
    }

    private boolean goToFile(String path) {
        File f = new File(path);
        boolean success = false;
        if (f.exists() && f.isFile()) {
            FileObject fob = FileUtil.toFileObject(f);
            if (fob != null) {
                DataObject dob;
                try {
                    dob = DataObject.find(fob);
                    EditCookie ed = dob.getLookup().lookup(EditCookie.class);
                    success = ed != null;
                    if (success) {
                        ed.edit();
                    } else {
                        OpenCookie oc = dob.getLookup().lookup(OpenCookie.class);
                        success = oc != null;
                        if (success) {
                            oc.open();
                        } else {
                            ViewCookie vc = dob.getLookup().lookup(ViewCookie.class);
                            success = vc != null;
                            if (success) {
                                vc.view();
                            }
                        }
                    }
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        if (!success) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            scrollToEnd();
        }
        return success;
    }
    
    public void mousePressed(MouseEvent e) {
        final JLabel lbl = (JLabel) e.getSource();
        final String path = lbl.getToolTipText();
        assert path != null;
        if ((e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0) {
            ignore(path, lbl);
        } else if (!e.isPopupTrigger()) {
            boolean stillExists = goToFile(path);
            if (!stillExists) {
                pane.remove(lbl);
            }
        } else {
            showPopup (lbl, path, e);
        }
    }

    public void mouseClicked(MouseEvent e) {
        stopAnimation();
        if (e.isPopupTrigger()) {
            showPopup (e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup (e);
        } else {
            scrollToEnd();
        }
    }

    public void mouseEntered(MouseEvent e) {
        setContainsMouse (true);
    }

    public void mouseExited(MouseEvent e) {
        setContainsMouse (false);
    }
    
    private void stopAnimation() {
        if (timer != null) {
            timer.stop();
            timer = null;
            WindowManager.getDefault().getMainWindow().removeWindowListener(this);
        }
    }
    
    private Timer timer;
    private void setContainsMouse(boolean val) {
        if (timer == null) {
            if (val) {
                final JScrollBar bar = pane.getHorizontalScrollBar();
                if (!bar.isShowing()) {
                    return;
                }
                timer = new Timer(3000, new ActionListener() {
                    boolean countdown = true;
                    int amt = -2;
                    public void actionPerformed(ActionEvent arg0) {
                        if (countdown) {
                            timer.stop();
                            timer = new Timer (60, this);
                            countdown = false;
                            timer.start();
                        } else {
                            int val = amt + bar.getValue();
                            if (val >= (bar.getMaximum() - 
                                    (bar.getModel().getExtent() + 1)) || val < 0) {
                                amt *= -1;
                                val = (amt * 2) + bar.getValue();
                            }
                            bar.setValue (val);
                        }
                    }
                    
                });
                WindowManager.getDefault().getMainWindow().addWindowListener(
                        this);
                timer.setRepeats(false);
                timer.start();
            } else {
                stopAnimation();
            }
        } else {
            if (!val) {
                stopAnimation();
            }
        }
    }

    public void windowOpened(WindowEvent arg0) {
    }

    public void windowClosing(WindowEvent arg0) {
        stopAnimation();
    }

    public void windowClosed(WindowEvent arg0) {
    }

    public void windowIconified(WindowEvent arg0) {
        stopAnimation();
    }

    public void windowDeiconified(WindowEvent arg0) {
    }

    public void windowActivated(WindowEvent arg0) {
    }

    public void windowDeactivated(WindowEvent arg0) {
        stopAnimation();
    }
    
    public void setMaxCrumbs (int val) {
        if (maxCrumbs != val) {
            maxCrumbs = val;
            NbPreferences.forModule(
                BreadcrumbComponent.class).putInt("maxCrumbs", val);
        }
        while (panel.getComponentCount() >= val) {
            panel.remove(0);
        }
    }
    
    private final class CrumbCountAction extends AbstractAction {
        private int val;
        CrumbCountAction(int val) {
            this.val = val;
            putValue (NAME, val == Integer.MAX_VALUE ? 
                NbBundle.getMessage (CrumbCountAction.class, "COUNT_UNLIMITED") :
                Integer.toString(val));
        }

        public void actionPerformed(ActionEvent arg0) {
            setMaxCrumbs (val);
        }
        
        public boolean shouldBeChecked() {
            return val == maxCrumbs;
        }
    }
    
    private final class OpenFileAction extends AbstractAction {
        private String path;
        OpenFileAction (String path) {
            putValue (NAME, new File(path).getName());
            this.path = path;
        }

        public void actionPerformed(ActionEvent arg0) {
            goToFile (path);
        }
    }
}

