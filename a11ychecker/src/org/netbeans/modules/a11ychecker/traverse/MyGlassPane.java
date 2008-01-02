/*
 * MyGlassPane.java
 *
 * @author Michal Hapala, Pavel Stehlik
 */
package org.netbeans.modules.a11ychecker.traverse;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.*;


/**
 * We have to provide our own glass pane so that it can paint.
 */
class MyGlassPane extends JComponent implements ItemListener {

    Point point;
    Vector<OverflowLbl> vecButtons = new Vector<OverflowLbl>();
    Vector<Component> components = new Vector<Component>();
    FocusTraversalPolicyEditor editor;
    Container formPanel;
    Container contentPane;
    OverflowLbl startButton;
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

    public String generateTraversalClass() {
        FocusTraversalPolicyGenerator myGenerator = new FocusTraversalPolicyGenerator();
        String myFocusClass = null;
        if (!vecButtons.isEmpty()) {
            // get start and end
            if (startButton == null) {
                startButton = vecButtons.get(0);
            }
            if (endButton == null) {
                endButton = getEndButton(startButton);
            }
            myFocusClass = myGenerator.generate(startButton, endButton, vecButtons);
        }
        return myFocusClass;
    }

    private void clearButtons() {
        for (int i = 0; i < vecButtons.size(); i++) {
            vecButtons.get(i).setInTabOrder(false);
        }
    }

    public void clearTraversals() {
        int res = JOptionPane.showConfirmDialog(formPanel, "You are to clear all designated Tab traversals. Are you sure you want to do this?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            setStart(null);
            setEnd(null);
            for (int i = 0; i < vecButtons.size(); i++) {
                vecButtons.get(i).nextcomp = null;
            }
            clearButtons();
            repaint();
        }
    }
    
    OverflowLbl selectedBtn = null;
    public void processClick(OverflowLbl m, Component actClickedComp, MouseEvent evt) {
        if(evt.getButton()==MouseEvent.BUTTON3)
        {
            // w/ Control - just reselect
            lastClickedComp = actClickedComp;
            lastButton = m;
        }
        else if(evt.getButton()==MouseEvent.BUTTON1)
        {

            // w/o - create traversal
            if (lastClickedComp != null) {
                if(lastClickedComp != actClickedComp) {
                m.setInTabOrder(true);
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
          overflowLbl.isSelected=false;
        }
        
        selectedBtn=m;
        
        repaint();
    }
    
    public void deleteClick(OverflowLbl m) {
        // find arrow which leads to this button
        OverflowLbl lbl = null;
        for (OverflowLbl overflowLbl : vecButtons) {
          if(overflowLbl.nextbutton == m)
              lbl = overflowLbl;
        }
        
        if(lbl != null)
        {
            lbl.nextbutton=null;
            lbl.nextcomp=null;
            lbl.setInTabOrder(false);
        }

        repaint();
    }
    
    
    Point pForm;

    private void loadSavedTabTraversal() {
        if (editor.savedBtns != null) {
            //projit vsechny komponenty a najit k nim ulozeny tabTraversal
            
            for (OverflowLbl overflowLbl : vecButtons) {
                for (MySavingButton savedBtn : editor.savedBtns) {
                    if (savedBtn.getName().equals(overflowLbl.mycomp.getName())) {
                        Component nextComp = null;
                        // find real next comp
                        for (OverflowLbl overflowLbl2 : vecButtons) {
                            if (overflowLbl2.mycomp.getName().equals(savedBtn.getNextName())) {
                                nextComp = overflowLbl2.mycomp;
                                overflowLbl.nextcomp = nextComp;
                                overflowLbl.nextbutton = overflowLbl2;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private void traverseFormPanel(Container container) {
        // projit komponenty a ziskat jen ty ktere mohou mit urceny tabtraversal
        for (Component aComp : container.getComponents()) {
            //pokud je komponenta JPanel rekurzivne opakuj
            if (aComp instanceof JPanel) {
                traverseFormPanel((Container) aComp);
                continue;
            }

            // TODO vyhodit vsechny komponenty ktery nemuzou/nemaji mit urceny tab order
            if (!((aComp instanceof JLabel) || (aComp instanceof JProgressBar) || (aComp instanceof JSeparator) )) {
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
    }

    public void runCreate() {
        if (formPanel != null && firstRun == true) {
            firstRun = false;
            pForm = formPanel.getLocationOnScreen();
            //            componentPoints = new Vector<Point>();

            for (Component aComp : formPanel.getComponents()) {
                if (aComp instanceof JLayeredPane) {
                    for (Component aaComp : ((Container) aComp).getComponents()) {
                        if (aaComp instanceof JPanel) {
                            traverseFormPanel((Container) aaComp);
                        }
                    }
                }
            }

            loadSavedTabTraversal();

            // find start and end
            for (int k = 0; k < vecButtons.size(); k++) {
                OverflowLbl b =  vecButtons.get(k);
                if (b.mycomp.getName().equals(editor.startName)) {
                    setStart(b);
                }
                if (b.mycomp.getName().equals(editor.endName)) {
                    setEnd(b);
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
    //setButtonsVisible(e.getStateChange() == ItemEvent.SELECTED);
    }

    public static void drawArrow(Graphics2D g2d, int xCenter, int yCenter, int x, int y, float stroke) {
        double aDir = Math.atan2(xCenter - x, yCenter - y);
        g2d.drawLine(x, y, xCenter, yCenter);
        g2d.setStroke(new BasicStroke(1f));					// make the arrow head solid even if dash pattern has been specified
        Polygon tmpPoly = new Polygon();
        int i1 = 12 + (int) (stroke * 2);
        int i2 = 6 + (int) stroke;							// make the arrow head the same size regardless of the length length
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
            if(startButton != null)
            {
                g.setColor(Color.GREEN);
                Rectangle myR1 = startButton.getBounds();
                g.fillOval(myR1.x + myR1.width / 2 - size/2, myR1.y + myR1.height / 2 - size/2, size, size);
            }
            if(endButton != null)
            {
                g.setColor(Color.RED);
                Rectangle myR1 = endButton.getBounds();
                g.fillOval(myR1.x + myR1.width / 2 - size/2, myR1.y + myR1.height / 2 - size/2, size, size);
            }
        }
    }
    Graphics2D graphics = (Graphics2D) this.getGraphics();

    public MyGlassPane(FocusTraversalPolicyEditor editor, Container contentPane, Container formPanel) {
        this.contentPane = contentPane;
        this.formPanel = formPanel;
        this.editor = editor;
        setDoubleBuffered(true);
        

    }
    
    
}