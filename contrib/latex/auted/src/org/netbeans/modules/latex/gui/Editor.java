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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author Jan Lahoda
 */
public class Editor extends JComponent implements PropertyChangeListener {
    
    public static final String PROP_SELECTION = "selection";
    private NodeStorage storage;
    private List        selection;
    private Rectangle2D selector = null;
    
    /** Creates a new instance of Editor */
    public Editor(NodeStorage storage) {
        this.storage = storage;
        
        if (storage != null)
            storage.register(this);
        
        storage.addPropertyChangeListener(this);
        
        MouseMotionListenerImpl listener = new MouseMotionListenerImpl();
        
        addMouseListener(listener);
        addMouseMotionListener(listener);
        
        selection = new ArrayList();
        
        setBackground(Color.WHITE);
    }
    
    public Dimension getMinusMove() {
//        Rectangle outer = storage.getOuterDimension();
        
        return new Dimension(0, 0);
    }
    
    public NodeStorage getNodeStorage() {
        return storage;
    }
    
    public void paint(Graphics g) {
        if (UIProperties.getDefault().isAntialiasingEnabled())
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Rectangle bounds = g.getClipBounds();
        Color     old    = g.getColor();
        
        g.setColor(getBackground());
        g.fillRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
        g.setColor(old);
        
        if (storage == null)
            return ;

        Dimension realBounds = getMinusMove();
        
        g.translate((int) +realBounds.getWidth(), (int) +realBounds.getHeight());
        
        storage.draw((Graphics2D) g);
        
        Iterator iter = selection.iterator();
        
        while (iter.hasNext()) {
            drawSelectionBoard(g, (Node) iter.next());
        }
        
        if (selection.size() == 1) {
            drawControlPoints(g, (Node) selection.get(0));
        }
        
        drawSelector((Graphics2D) g);

        g.translate((int) -realBounds.getWidth(), (int) -realBounds.getHeight());
    }
    
    private void drawSelectionBoard(Graphics g, Node node) {
        Rectangle2D envelop = node.getOuterDimension();
        
        Color old = g.getColor();
        g.setColor(Color.RED);
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                g.fillRect((int) (envelop.getX() - 2 + x * envelop.getWidth()), (int) (envelop.getY() - 2 + y * envelop.getHeight()), 4, 4);
            }
        }
        
        g.setColor(old);
    }
    
    private void drawControlPoint(Graphics g, Point2D start, Point2D target) {
        Graphics2D g2 = (Graphics2D) g;
        
        Color oldColor = g.getColor();
        Stroke oldStroke = g2.getStroke();
        BasicStroke basic = new BasicStroke();
        Stroke newStroke = new BasicStroke(basic.getLineWidth(), basic.getEndCap(), basic.getLineJoin(), basic.getMiterLimit(), new float[] {3,3}, 0);
        
        g2.setStroke(newStroke);
        g2.setColor(Color.BLUE);
        g2.draw(new Line2D.Double(start, target));
        g2.setStroke(oldStroke);
        g2.draw(new Rectangle2D.Double(target.getX() - 2, target.getY() - 2, 4, 4));
        g2.setColor(oldColor);
    }
    
    private void drawSelector(Graphics2D g) {
        if (selector != null)
            g.draw(selector);
    }
    
    private void drawControlPoints(Graphics g, Node node) {
        if (node instanceof ControllableCurveEdgeNode) {
            ControllableCurveEdgeNode curve = (ControllableCurveEdgeNode) node;

            drawControlPoint(g, curve.getSource().getContourPoint(curve.getSourceAngle()), curve.getSourceControlPoint());
            drawControlPoint(g, curve.getTarget().getContourPoint(curve.getTargetAngle()), curve.getTargetControlPoint());
        }
    }
    
    public Dimension getMinimumSize() {
        if (storage == null)
            return new Dimension();
            
        Rectangle2D outer = storage.getOuterDimension();
        
        return new Dimension((int) (outer.getWidth() + outer.getX()), (int) (outer.getHeight() + outer.getY()));
    }
//    
//    public Rectangle getBounds() {
//        super.getBounds();
//    }
    
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
    public void adjustSize() {
        setSize(getPreferredSize());
    }
    
    private JPopupMenu createPopupMenu(Point pos) {
        JPopupMenu menu = new JPopupMenu();
        
        menu.add(new NormaliseAction());
//        menu.add(new LoadAction());
//        menu.add(new SaveAction());
//        menu.add(new ExportAction());
        menu.addSeparator();
        menu.add(getNewMenu(pos));
        
        return menu;
    }
    
    private Node findNearestAcceptableNode(Point pos) {
        Point p = new Point(pos);
        Dimension move = getMinusMove();
        
        p.translate((int) -move.getWidth(), (int) -move.getHeight());
        
        Node node = storage.findNearestPoint(p);
        
        if (node == null)
            return null;
        
        if (node.distance(p) < MENU_DISTANCE) {
            return node;
        }
        
        return null;
    }

    private static final int MENU_DISTANCE = 50;
    
    protected JMenu getNewMenu(Point pos) {
        JMenu subMenu = new JMenu("New");
        
        subMenu.add(new NewStateAction(pos));
        subMenu.addSeparator();
	subMenu.add(new NewLoopAction());
        subMenu.add(new NewLineEdgeAction());
        subMenu.add(new NewArcEdgeAction());
        
        return subMenu;
    }
    
    private static Action systemFlag;
    public static synchronized Action getSystemMenuFlag() {
        if (systemFlag == null)
            systemFlag = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                }
            };
            
        return systemFlag;
    }
    
    public JPopupMenu getPopupMenu(Point pos) {
        Node node = findNearestAcceptableNode(pos);
        
        if (node != null) {
            JPopupMenu menu = new JPopupMenu();
            Action[]   nodeMenu = node.getPopupMenu();
            
            for (int cntr = 0; cntr < nodeMenu.length; cntr++) {
                Action actual = nodeMenu[cntr];
                
                if (actual == getSystemMenuFlag()) {
                    menu.add(getNewMenu(pos));
                } else {
                    if (actual == null)
                        menu.addSeparator();
                    else
                        menu.add(nodeMenu[cntr]);
                }
            }
            
            return menu;
        } else
            return createPopupMenu(pos);
    }
    
    public void invalidate() {
        super.invalidate();
        repaint(0, 0, (int) getSize().getWidth(), (int) getSize().getHeight());
    }
    
    private Point translate(Point p) {
        Dimension move = getMinusMove();
        Dimension grid = UIProperties.getGridSize();
        
        Point res = new Point((int) Math.round(((p.getX() - move.getWidth()) / grid.getWidth())), (int) Math.round(((p.getY() - move.getHeight()) / grid.getHeight())));
        
        System.err.println("p=" + p + ", move=" + move + ", grid=" + grid + ", res=" + res);
        return res;
    }
    
    private class NewStateAction extends AbstractAction {
        private Point pos;
        
        public NewStateAction(Point pos) {
            super("New State");
            this.pos = pos;
        }
        
        public void actionPerformed(ActionEvent e) {
            Point translated = translate(pos);
            
            storage.addObject(new StateNode((int) translated.getX(),
                                            (int) translated.getY()));
            
            Editor.this.invalidate();
        }
        
    }
    
    private abstract class NewEdgeAction extends AbstractAction {
        
        public NewEdgeAction(String name) {
            super(name);
            setEnabled(selection.size() == 2 && selection.get(0) instanceof StateNode && selection.get(1) instanceof StateNode);
        }
        
        protected abstract EdgeNode createEdge(StateNode source, StateNode target);
        
        public void actionPerformed(ActionEvent e) {
            storage.addObject(createEdge((StateNode) selection.get(0), (StateNode) selection.get(1)));
            Editor.this.invalidate();
        }
        
    }

    private class NewLoopAction extends AbstractAction {
        
        public NewLoopAction() {
            super("Loop Edge");
            setEnabled(selection.size() == 1 && selection.get(0) instanceof StateNode);
        }
        
        
        public void actionPerformed(ActionEvent e) {
            storage.addObject(new LoopEdgeNode((StateNode) selection.get(0)));
            Editor.this.invalidate();
        }
        
    }
    
    private class NewLineEdgeAction extends NewEdgeAction {
        
        public NewLineEdgeAction() {
            super("Line Edge");
        }
        
        protected EdgeNode createEdge(StateNode source, StateNode target) {
            return new LineEdgeNode(source, target);
        }
        
    }

    private class NewArcEdgeAction extends NewEdgeAction {
        
        public NewArcEdgeAction() {
            super("Arc Edge");
        }
        
        protected EdgeNode createEdge(StateNode source, StateNode target) {
            return new AngleEdgeNode(source, target);
        }
        
    }
    
    private class NormaliseAction extends AbstractAction{
        
        public NormaliseAction() {
            super("Normalise");
        }
        
        public void actionPerformed(ActionEvent e) {
            storage.normalise();
//            Editor.this.repaint(new Rectangle(Editor.this.getSize()));
        }
        
    }

    private class SaveAction extends AbstractAction {
        
        public SaveAction() {
            super("Save");
        }
        
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser();
            
            switch (jfc.showSaveDialog(Editor.this)) {
                case JFileChooser.APPROVE_OPTION:
                    OutputStream stream = null;
                    
                    try {
                        File file = jfc.getSelectedFile();
                        
                        save(stream = new FileOutputStream(file));
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                    }
                    
                    break;
                case JFileChooser.CANCEL_OPTION:
            }
        }
        
    }

    private class LoadAction extends AbstractAction {
        
        public LoadAction() {
            super("Load");
        }
        
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser();
            
            switch (jfc.showOpenDialog(Editor.this)) {
                case JFileChooser.APPROVE_OPTION:
                    InputStream stream = null;
                    
                    try {
                        File file = jfc.getSelectedFile();
                        
                        load(stream = new FileInputStream(file));
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace(System.err);
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                    }
                    
                    break;
                case JFileChooser.CANCEL_OPTION:
            }
        }
        
    }

    private class ExportAction extends AbstractAction {
        
        public ExportAction() {
            super("Export");
        }
        
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser();
            
            switch (jfc.showDialog(Editor.this, "Export")) {
                case JFileChooser.APPROVE_OPTION:
                    OutputStream stream = null;
                    PrintWriter  writer = null;
                    try {
                        File file = jfc.getSelectedFile();
                        
                        Editor.this.storage.outputVaucansonSource(writer = new PrintWriter(new OutputStreamWriter(stream = new FileOutputStream(file))));
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    } finally {
                        if (writer != null) {
                            writer.close();
                            stream = null;
                        }
                        
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                    }
                    
                    break;
                case JFileChooser.CANCEL_OPTION:
            }
        }
        
    }

    public void putIntoSelection(Node node, boolean multiple) {
        if (node == null)
            return ;
        
        if (!multiple) {
            selection.removeAll(selection);
            selection.add(node);
        } else {
            if (selection.contains(node)) {
                selection.remove(node);
            } else {
                selection.add(node);
            }
        }
        
        firePropertyChange(PROP_SELECTION, null, selection);
        
        invalidate();
    }
    
    public void clearSelection() {
        selection.clear();
        firePropertyChange(PROP_SELECTION, null, selection);
    }
    
    public List getSelection() {
        return Collections.unmodifiableList(selection);
    }
    
    public void save(OutputStream out) throws IOException {
        storage.save(out);
    }
    
    public void load(InputStream ins) throws IOException, ClassNotFoundException {
        ObjectInputStream in = null;
        
        try {
            in = new ObjectInputStream(ins);
            
            NodeStorage newStorage = (NodeStorage) in.readObject();
            
            storage.unregister(this);
            
            storage = newStorage;
            
            storage.register(this);
            invalidate();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }
    
    public void repaintEditor() {
        invalidate();
        repaint();//new Rectangle(getSize()));
    }

    public void propertyChange(PropertyChangeEvent evt) {
        repaintEditor();
    }
    
//    private class KeyListenerImpl implements KeyListener {
//        
//        public void keyPressed(KeyEvent e) {
//            if (selection.size() == 1) {
//            switch (e.getKeyCode()) {
//                case e.VK_UP:
//                    
//            }
//        }
//        
//        public void keyReleased(KeyEvent e) {
//        }
//        
//        public void keyTyped(KeyEvent e) {
//        }
//        
//    }
//    
    private class MouseMotionListenerImpl implements MouseMotionListener, MouseListener {
        
        private static final int DRAGGING_NONE = 0;
        private static final int DRAGGING_NODE = 1;
        private static final int DRAGGING_CONTROL_SOURCE = 2;
        private static final int DRAGGING_CONTROL_TARGET = 3;
        private static final int DRAGGING_SELECTOR = 4;
        
        private PositionNode currentlyDragging;
        private ControllableCurveEdgeNode currentCurve;
        private Point selectorStart;
        
        private int  oldX, oldY;
        private boolean dragged;
        private int draggingType = DRAGGING_NONE;
        
        private static final double DRAG_DISTANCE = 5;
        
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                putIntoSelection(findNearestAcceptableNode(e.getPoint()),
                                 e.isControlDown());
            }
        }
        
        public void mouseDragged(MouseEvent e) {
//            System.err.println("mouseDragged(" + e + ")");
            if (draggingType == DRAGGING_NONE)
                return ;
            
            dragged = true;
            Point p = translate(e.getPoint());
            Point pBig = new Point(e.getPoint());
            
            pBig.translate((int) -getMinusMove().getWidth(), (int) -getMinusMove().getWidth());
            System.err.println("p = " + p );
            System.err.println("pBig = " + pBig );
            System.err.println("draggingType = " + draggingType );
            switch (draggingType) {
                case DRAGGING_NODE:
                    currentlyDragging.setX((int) p.getX());
                    currentlyDragging.setY((int) p.getY());
                    break;
                case DRAGGING_CONTROL_SOURCE:
                    currentCurve.setSourceControlPoint(pBig);
                    break;
                case DRAGGING_CONTROL_TARGET:
                    currentCurve.setTargetControlPoint(pBig);
                    break;
                case DRAGGING_SELECTOR:
                    Rectangle2D current = Editor.this.selector;
                    double lX = pBig.getX() - selectorStart.getX();
                    double sX = lX >= 0 ? selectorStart.getX() : pBig.getX();
                    double lY = pBig.getY() - selectorStart.getY();
                    double sY = lY >= 0 ? selectorStart.getY() : pBig.getY();
                    
                    lX = lX >= 0 ? lX : -lX;
                    lY = lY >= 0 ? lY : -lY;
                    
                    System.err.println("sX = " + sX );
                    System.err.println("sY = " + sY );
                    System.err.println("lX = " + lX );
                    System.err.println("lY = " + lY );
                    Editor.this.selector = new Rectangle2D.Double(sX, sY, lX, lY); //!!!WIDTH!!!
                    break;
            }
//            Editor.this.repaint(new Rectangle(Editor.this.getSize()));
        }
        
        public void mouseEntered(MouseEvent e) {
        }
        
        public void mouseExited(MouseEvent e) {
        }
        
        public void mouseMoved(MouseEvent e) {
        }
        
        private static final int CONTROL_MAGIC_VALUE = 3;
        private static final int DISTANCE_MAGIC = 20;
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                getPopupMenu(e.getPoint()).show(Editor.this, (int) e.getPoint().getX(), (int) e.getPoint().getY());
                return ;
            }
            
//            System.err.println("mousePressed(" + e + ")");
            
            if (storage == null)
                return ;
            
            Point p = new Point(e.getPoint());
            
            p.translate((int) -getMinusMove().getWidth(), (int) -getMinusMove().getWidth());
            
            //The control points should have absolute priority:
            if (selection.size() == 1 && selection.get(0) instanceof ControllableCurveEdgeNode) {
                ControllableCurveEdgeNode curve = (ControllableCurveEdgeNode) selection.get(0);
                double sourceDist = curve.getSourceControlPoint().distance(p);
                double targetDist = curve.getTargetControlPoint().distance(p);

//                System.err.println("distance = " + distance );
                System.err.println("sourceDist = " + sourceDist );
                System.err.println("targetDist = " + targetDist );
                
                System.err.println("curve.getSourceControlPoint()=" + curve.getSourceControlPoint());
                System.err.println("curve.getTargetControlPoint()=" + curve.getTargetControlPoint());
                
                if (/*(distance > sourceDist || distance > targetDist) && */(sourceDist <= CONTROL_MAGIC_VALUE || targetDist <= CONTROL_MAGIC_VALUE)) {
                    dragged = false;
                    currentCurve = curve;
                    if (sourceDist < targetDist) {
                        draggingType = DRAGGING_CONTROL_SOURCE;
                        
                    } else {
                        draggingType = DRAGGING_CONTROL_TARGET;
                    }
                    return ;
                }
            }
            
            Node node = storage.findNearestPoint(p);
            
            if (node == null)
                return ;
            
            double distance = node.distance(p);
            
            if (!(node instanceof PositionNode) || distance > DISTANCE_MAGIC || node == null) {
                draggingType = DRAGGING_SELECTOR;
                Editor.this.selector = new Rectangle2D.Double(p.getX(), p.getY(), p.getX(), p.getY());
                selectorStart = p;
                
                return ;
            }
            
            currentlyDragging = (PositionNode) node;
//            System.err.println("currentlyDragging = " + currentlyDragging );
            
//            if (currentlyDragging.distance(e.getPoint()) > DRAG_DISTANCE) {
//                currentlyDragging = null;
//                return ;
//            }
            
            oldX = currentlyDragging.getX();
            oldY = currentlyDragging.getY();
            draggingType = DRAGGING_NODE;
            dragged = false;
        }
        
        public void mouseReleased(MouseEvent e) {
            dragged = false;
            currentlyDragging = null;
            
            if (draggingType == DRAGGING_SELECTOR) {
                Iterator iter = Editor.this.storage.getObjects().iterator();
                Rectangle2D selector = Editor.this.selector;
                Editor.this.selection.clear();
                
                while (iter.hasNext()) {
                    Node node = (Node) iter.next();
                    Rectangle2D r = node.getOuterDimension();
                    
                    if (selector.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight()))
                        Editor.this.selection.add(node);
                }
                
                firePropertyChange(PROP_SELECTION, null, selection);
            }
            
            draggingType = DRAGGING_NONE;
            Editor.this.selector = null;
            
            if (e.isPopupTrigger()) {
                getPopupMenu(e.getPoint()).show(Editor.this, (int) e.getPoint().getX(), (int) e.getPoint().getY());
                return ;
            }
//            System.err.println("mouseReleased(" + e + ")");
//            Editor.this.repaint(new Rectangle(Editor.this.getSize()));
        }
    }
    
    private static class KeyboardListenerImpl implements KeyListener {
        
        public void keyPressed(KeyEvent e) {
        }
        
        public void keyReleased(KeyEvent e) {
        }
        
        public void keyTyped(KeyEvent e) {
        }
        
    }
}
