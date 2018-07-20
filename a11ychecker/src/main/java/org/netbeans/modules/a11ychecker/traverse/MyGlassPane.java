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

package org.netbeans.modules.a11ychecker.traverse;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.*;
import org.netbeans.modules.form.FormModel;
import org.netbeans.modules.form.RADComponent;
import org.netbeans.modules.form.VisualReplicator;

/*
 * Glass pane which is used as an overlay over the custom editor window. Catches
 * all mouse actions and processes them accordingly.
 *
 * @author Michal Hapala, Pavel Stehlik
 */
class MyGlassPane extends JComponent implements ItemListener {

    FormModel myModel;
    VisualReplicator myRepl;
    /** all form components with their representative "buttons"  */
    Vector<OverflowLbl> vecButtons = new Vector<OverflowLbl>();
    FocusTraversalPolicyEditor editor;
    Container formPanel;
    Container contentPane;
    /** starting button for the traversal, if defined */
    OverflowLbl startButton;
    /** ending button for the traversal, if defined */
    OverflowLbl endButton;
    
    private boolean firstRun = true;
    private OverflowLbl lastButton;
    private Component lastClickedComp;

    public void setStart(OverflowLbl button) {
        startButton = button;
        repaint();
    }

    public void setEnd(OverflowLbl button) {
        endButton = button;
        repaint();
    }

    public RADComponent getMetaComponent(Component comp) {
        return myModel.getMetaComponent(myRepl.getClonedComponentId(comp));
    }

    /**
     * Finds end button for the traversal by following the traversal from the 
     * starting button
     * @param start button
     * @return end button
     */
    public OverflowLbl getEndButton(OverflowLbl startButton) {
        OverflowLbl r = startButton;
        OverflowLbl act = r;
        OverflowLbl next = r.nextbutton;
        while (r != next) {
            if (next == null) {
                break;
            }
            act = next;
            next = next.nextbutton;
        }
        return act;
    }
    
    OverflowLbl selectedBtn = null;

    /**
     * Processes mouse click on the glass pane. Reselects actual selected button
     * or creates traversal.
     * @param m currently selected button
     * @param actClickedComp currently selected component
     * @param evt mouse event
     */
    public void processClick(OverflowLbl m, Component actClickedComp, MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            // reselect
            lastClickedComp = actClickedComp;
            lastButton = m;
        } else if (evt.getButton() == MouseEvent.BUTTON1) {

            // create traversal
            if (lastClickedComp != null) {
                if (lastClickedComp != actClickedComp) {
                    lastButton.nextbutton = m;
                    lastButton.nextcomp = actClickedComp;
                    lastClickedComp = actClickedComp;
                    lastButton = m;
                }
            } else if (lastClickedComp == null) {
                lastClickedComp = actClickedComp;
                lastButton = m;
            }
        }

        for (OverflowLbl overflowLbl : vecButtons) {
            overflowLbl.setBorder(overflowLbl.state);
            overflowLbl.isSelected = false;
        }

        selectedBtn = m;

        repaint();
    }

    /**
     * Deselects currently selected button
     */
    public void deselectButton() {
        lastClickedComp = null;
        lastButton = null;
        selectedBtn = null;

        for (OverflowLbl overflowLbl : vecButtons) {
            overflowLbl.setBorder(overflowLbl.state);
            overflowLbl.isSelected = false;
        }

        repaint();

    }

    /**
     * Deletes traversal that leads to button m
     * @param m button
     */
    public void deleteClick(OverflowLbl m) {
        // find arrow which leads to this button
        OverflowLbl lbl = null;
        for (OverflowLbl overflowLbl : vecButtons) {
            if (overflowLbl.nextbutton == m) {
                lbl = overflowLbl;
            }
        }

        if (lbl != null) {
            lbl.nextbutton = null;
            lbl.nextcomp = null;
        }

        repaint();
    }
    
    Point pForm;
    
    /**
     * Loads tab traversal saved in savedBtns and assigns it to components in 
     * currently loaded form in the custom editor
     */
    private void loadSavedTabTraversal() {
        if (editor.savedBtns != null) {
            // go through all components and finds their saved traversal
            for (OverflowLbl overflowLbl : vecButtons) {
                for (MySavingButton savedBtn : editor.savedBtns) {
                    if (savedBtn.getName().equals(overflowLbl.getMyCompName())) {
                        Component nextComp = null;
                        // find real next comp
                        for (OverflowLbl overflowLbl2 : vecButtons) {
                            if (overflowLbl2.getMyCompName() != null) {
                                if (overflowLbl2.getMyCompName().equals(savedBtn.getNextName())) {
                                    nextComp = overflowLbl2.mycomp;
                                    overflowLbl.nextcomp = nextComp;
                                    overflowLbl.nextbutton = overflowLbl2;
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Adds component to those that a tab traversal can be assigned to.
     * Skips components like labels or progress bars.
     * @param aComp Component
     */
    private void addEligibleComponent(Component aComp) {
        if (!((aComp instanceof JLabel) || (aComp instanceof JProgressBar) || (aComp instanceof JSeparator))) {
            Point pComp = aComp.getLocationOnScreen();
            // horni levy roh komponenty
            Point upperLeftCornComp = new Point(pComp.x - pForm.x, pComp.y - pForm.y);
            OverflowLbl myOverflowBtn = new OverflowLbl(this, aComp);
            myOverflowBtn.setBounds(upperLeftCornComp.x, upperLeftCornComp.y, aComp.getWidth(), aComp.getHeight());

            add(myOverflowBtn);
            setComponentZOrder(myOverflowBtn, 0);

            vecButtons.add(myOverflowBtn);
        }
    }
    
    /**
     * Traverses component if it is a container or tries to add the component to
     * be eligible for adding tab traversal
     * @param comp Component
     */
    private void traverseFormComponent(Component comp) {
        // component is a container
        if (comp instanceof JPanel || 
                comp instanceof JSplitPane || 
                comp instanceof JScrollPane || 
                comp instanceof JToolBar || 
                comp instanceof JDesktopPane || 
                comp instanceof JInternalFrame || 
                comp instanceof JLayeredPane || 
                comp instanceof JRootPane) {
            for (Component aComp : ((Container) comp).getComponents()) {
                traverseFormComponent((Container) aComp);
            }
        } else {
            // component is a normal component - solve special cases
            if (comp instanceof JViewport) {
                Component viewedOne = ((JViewport) comp).getView();
                if (viewedOne != null) {
                    traverseFormComponent(viewedOne);
                }
            }

            if (getMetaComponent(comp) != null) {
                addEligibleComponent(comp);
            }
        }
    }

    /**
     * Finds all components eligible for tab traversal. Loads saved tab traversal
     * if there is any.
     */
    public void runCreate() {
        if (formPanel != null && firstRun == true) {
            firstRun = false;
            pForm = formPanel.getLocationOnScreen();
            // componentPoints = new Vector<Point>();

            if (formPanel instanceof JRootPane) {
                for (Component aComp : formPanel.getComponents()) {
                    if (aComp instanceof JLayeredPane) {
                        for (Component aaComp : ((Container) aComp).getComponents()) {
                            if (aaComp instanceof JPanel) {
                                traverseFormComponent((Container) aaComp);
                            }
                        }
                    }
                }
            } else if (formPanel instanceof JPanel) {
                traverseFormComponent(formPanel);
            }

            loadSavedTabTraversal();

            // find start and end
            for (int k = 0; k < vecButtons.size(); k++) {
                OverflowLbl b = vecButtons.get(k);
                if (b.getMyCompName() != null) {
                    if (b.getMyCompName().equals(editor.startName)) {
                        setStart(b);
                    }
                    if (b.getMyCompName().equals(editor.endName)) {
                        setEnd(b);
                    }
                }
            }
        }
    }

    /**
     * React to change button clicks.
     */
    public void itemStateChanged(ItemEvent e) {
        runCreate();
        setVisible(e.getStateChange() == ItemEvent.SELECTED);
    }

    public static void drawArrow(Graphics2D g2d, int xCenter, int yCenter, int x, int y, float stroke) {
        double aDir = Math.atan2(xCenter - x, yCenter - y);
        g2d.drawLine(x, y, xCenter, yCenter);
        g2d.setStroke(new BasicStroke(1f));					// make the arrow head solid even if dash pattern has been specified
        Polygon tmpPoly = new Polygon();
        int i1 = 12 + (int) (stroke * 2);
        int i2 = 6 + (int) stroke;						// make the arrow head the same size regardless of the length length
        tmpPoly.addPoint(x, y);							// arrow tip
        tmpPoly.addPoint(x + xCor(i1, aDir + .5), y + yCor(i1, aDir + .5));
        tmpPoly.addPoint(x + xCor(i2, aDir), y + yCor(i2, aDir));
        tmpPoly.addPoint(x + xCor(i1, aDir - .5), y + yCor(i1, aDir - .5));
        tmpPoly.addPoint(x, y);							// arrow tip
        g2d.drawPolygon(tmpPoly);
        g2d.fillPolygon(tmpPoly);						// remove this line to leave arrow head unpainted
    }

    private static int yCor(int len, double dir) {
        return (int) (len * Math.cos(dir));
    }

    private static int xCor(int len, double dir) {
        return (int) (len * Math.sin(dir));
    }

    /**
     * Repaint all traversal arrows and buttons designating start and end
     * component.
     * @param g graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (vecButtons != null) {
            // draw arrows
            g.setColor(Color.BLUE);
            for (OverflowLbl overflowLbl : vecButtons) {
                if (overflowLbl.nextcomp != null) {
                    Rectangle myR1 = overflowLbl.getBounds();
                    Rectangle myR2 = overflowLbl.nextbutton.getBounds();
                    Point p1 = new Point(myR1.x + myR1.width / 2, myR1.y + myR1.height / 2);
                    Point p2 = new Point(myR2.x + myR2.width / 2, myR2.y + myR2.height / 2);
                    drawArrow((Graphics2D) g, p1.x, p1.y, p2.x, p2.y, 1);
                }
                Rectangle myR1 = overflowLbl.getBounds();
            }
            // draw circles
            final int size = 14;
            if (startButton != null) {
                g.setColor(Color.GREEN);
                Rectangle myR1 = startButton.getBounds();
                g.fillOval(myR1.x + myR1.width / 2 - size / 2, myR1.y + myR1.height / 2 - size / 2, size, size);
            }
            if (endButton != null) {
                g.setColor(Color.RED);
                Rectangle myR1 = endButton.getBounds();
                g.fillOval(myR1.x + myR1.width / 2 - size / 2, myR1.y + myR1.height / 2 - size / 2, size, size);
            }
        }
    }
    Graphics2D graphics = (Graphics2D) this.getGraphics();

    public MyGlassPane(FocusTraversalPolicyEditor editor, Container contentPane, Container formPanel, FormModel m, VisualReplicator r) {
        this.contentPane = contentPane;
        this.formPanel = formPanel;
        this.editor = editor;
        setDoubleBuffered(true);
        myModel = m;
        myRepl = r;
       
        addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                final MouseEvent evt = e;
                deselectButton();
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
    }

    /**
     * Adds component c into components that are oblivious to the glass pane,
     * i.e. they can be clicked on as usual. This function basically assigns
     * them a mouse listener.
     * @param c Component
     */
    public void addActiveComponent(Component c) {
        RedispatchListener listener = new RedispatchListener(c, this, contentPane);
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }
}
