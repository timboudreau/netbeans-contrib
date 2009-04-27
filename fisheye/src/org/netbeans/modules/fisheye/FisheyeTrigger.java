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
package org.netbeans.modules.fisheye;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Tim Boudreau
 */
public class FisheyeTrigger {
    private final TriggerListener triggerListener = new TriggerListener();
    private final ViewListener viewListener = new ViewListener();
    private final FadeInOutHandler fadeInOutHandler = new FadeInOutHandler();
    
    private FishEyeTextView view;
    private final Reference<JTextComponent> textComponent;
    private final JComponent triggerComponent;
    private final WeakComponentAndDocumentListener l;
    private final FisheyeHandler handler;
    /** Creates a new instance of FisheyeTrigger */
    public FisheyeTrigger(JTextComponent view, JComponent triggerComponent, FisheyeHandler handler) {
        this.view = new FishEyeTextView(view);
        this.textComponent = new WeakReference <JTextComponent> (view);
        l = new WeakComponentAndDocumentListener (viewListener, view);
        this.triggerComponent = triggerComponent;
        this.handler = handler;
        triggerComponent.addMouseListener (triggerListener);
    }

    public int getLocusElement() {
        return view.getLocusElement();
    }

    private class ViewListener implements ChangeListener, ComponentListener {
        public void stateChanged(ChangeEvent e) {
        }

        public void componentResized(ComponentEvent e) {
            setFishEyeViewVisible(false);
        }

        public void componentMoved(ComponentEvent e) {
            setFishEyeViewVisible(false);
        }

        public void componentShown(ComponentEvent e) {
        }

        public void componentHidden(ComponentEvent e) {
            setFishEyeViewVisible(false);
        }    
    }
    
    private final class TriggerListener implements MouseListener, MouseMotionListener, WindowListener {
        public void mousePressed(MouseEvent e) {
            triggerComponent.addMouseMotionListener (this);
            Container c = triggerComponent.getTopLevelAncestor();
            if (c instanceof Frame) {
                ((Frame) c).addWindowListener(this);
            } else if (c instanceof Dialog) {
                ((Dialog) c).addWindowListener(this);
            }
        }

        public void mouseReleased(MouseEvent e) {
            boolean releasedInBar = triggerComponent.contains(e.getPoint());
            triggerComponent.removeMouseMotionListener(this);
            if (releasedInBar) {
                JTextComponent jc = textComponent.get();
                if (jc != null) {
                    int line = handler.viewToModel(e.getY(), triggerComponent);
                    if (line >= 0 && line < jc.getDocument().getDefaultRootElement().getElementCount()) {
                        Element el = jc.getDocument().getDefaultRootElement().getElement(line);
                        int start = el.getStartOffset();
                        try {
                            Rectangle r = jc.modelToView(start);
                            
                            //Add an offset so the line the user sees shows
                            //up about where it appeared in the fisheye view
                            int relY = triggerComponent.getHeight() - e.getY();
                            r.height += relY;
                            jc.scrollRectToVisible(r);
                            jc.setCaretPosition(el.getEndOffset() - 1);
                        } catch (BadLocationException ex) {
                            Logger.getLogger(FisheyeTrigger.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } else {
                System.err.println("not released in bar");
            }
            Container c = triggerComponent.getTopLevelAncestor();
            if (c instanceof Frame) {
                ((Frame) c).removeWindowListener(this);
            } else if (c instanceof Dialog) {
                ((Dialog) c).removeWindowListener(this);
            }
            setDragging (false);
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            setDragging(true);
            dragOver (e.getPoint().y);
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void windowOpened(WindowEvent e) {
        }

        public void windowClosing(WindowEvent e) {
        }

        public void windowClosed(WindowEvent e) {
        }

        public void windowIconified(WindowEvent e) {
        }

        public void windowDeiconified(WindowEvent e) {
        }

        public void windowActivated(WindowEvent e) {
        }

        public void windowDeactivated(WindowEvent e) {
        }
        
        private boolean dragging = false;
        private boolean setDragging (boolean val) {
            boolean result = val != dragging;
            if (result) {
                dragging = val;
                if (dragging) {
                    draggingBegun();
                } else {
                    draggingFinished();
                }
            }
            return result;
        }
    }
    
    
    private void draggingBegun() {
        setFishEyeViewVisible(true);
//        startFishEyeViewAnimation (true);
    }
    
    private void draggingFinished() {
//        setFishEyeViewVisible(false);
        startFishEyeViewAnimation (false);
    }
    
    private int dragOver (int yCoord) {
        int line = handler.viewToModel (yCoord, triggerComponent);
        view.setLocus (new Point(0, yCoord));
        return line;
    }
    
    public void setFishEyeViewVisible (boolean val) {
        JTextComponent jtc = textComponent.get();
        if (jtc != null) {
            Container c = jtc.getTopLevelAncestor();
            if (c instanceof Container) {
                JRootPane pane = c instanceof JFrame ? ((JFrame) c).getRootPane() :
                    c instanceof JDialog ? ((JDialog) c).getRootPane() : null;
                
                if (pane != null) {
                    Component glassPane = pane.getGlassPane();
                    if (glassPane instanceof Container && ((Container)glassPane).getLayout() != null) {
                        ((Container) glassPane).setLayout (null);
                    }
                    if (glassPane instanceof JComponent) {
                        if (val) {
                            showFishEyeView (jtc, (JComponent) glassPane);
                        } else {
                            hideFishEyeView (jtc, (JComponent) glassPane);
                        }
                    } else {
                        throw new IllegalStateException ("Glass pane for " + 
                                jtc + " is not a JComponent");
                    }
                } else {
                    System.err.println("Cannot show fish eye view - parent" +
                            "is not a JFrame or JDialog");
                }
            } else {
                System.err.println("Cannot show fish eye view, " + jtc + 
                        " is not in a window");
            }
        }
    }
    
    private boolean wasVisible = false;
    private void showFishEyeView (JTextComponent comp, JComponent gp) {
        view.setIgnoreRepaint(false);
        wasVisible = gp.isShowing();
        JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(
                JScrollPane.class, comp);
        if (scroll != null) {
            Point p = new Point (0,0);
            p = SwingUtilities.convertPoint(scroll, p, gp);
            gp.add (view);
            view.setBounds(p.x, p.y, scroll.getWidth(), scroll.getHeight());
            view.setDocument(comp.getDocument());
            view.setMarks (handler.getMarks(comp.getDocument()));
            gp.setVisible (true);
        } else {
            System.err.println("Could not find scroll pane");
        }
    }
    
    private void hideFishEyeView (JTextComponent comp, JComponent gp) {
        if (view.isDisplayable()) {
            view.hidden();
            gp.setVisible (wasVisible);
            gp.remove (view);
            JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(
                    JScrollPane.class, comp);
            if (scroll != null) {
                scroll.repaint();
            }
        }
    }
    
    private ToggleTimer animTimer;
    private void startFishEyeViewAnimation(boolean show) {
        assert EventQueue.isDispatchThread();
        if (animTimer != null) {
            animTimer.stop();
        }
        animTimer = new ToggleTimer (fadeInOutHandler, 5, show, 2);
    }

    private class FadeInOutHandler implements ToggleTimer.MultiStepHandler {
        public void aborted(ToggleTimer timer, int at, boolean direction) {
            setFishEyeViewVisible (!direction);
        }

        public void start(ToggleTimer timer, boolean direction) {
            view.setAlpha(direction ? 0.1F : 1.0F, direction);
            if (direction) {
                setFishEyeViewVisible(direction);
            }
        }

        public void finish(ToggleTimer timer, boolean direction) {
            view.setAlpha(!direction ? 0.1F : -1, direction);
            if (!direction) {
                view.setAlpha(0.01F, direction);
//                view.paintImmediately(0, 0, view.getWidth(), view.getHeight());
                view.repaint();
                setFishEyeViewVisible (false);
            }
        }

        public void tick(ToggleTimer timer, int index, boolean direction) {
            float alpha = view.setAlpha(view.getAlpha() + (direction ? 0.25F : 
                -0.25F), direction);
//            view.paintImmediately(0, 0, view.getWidth(), view.getHeight());
            view.repaint();
//            if (view.getAlpha() >= 1 || view.getAlpha() <= -1) {
//                timer.forceFinish();
//            }
        }
    }
}
