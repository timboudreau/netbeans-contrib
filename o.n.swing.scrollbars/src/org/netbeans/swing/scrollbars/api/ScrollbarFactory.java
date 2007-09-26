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
 * ScrollbarFactory.java
 *
 * Created on May 9, 2004, 7:19 PM
 */

package org.netbeans.swing.scrollbars.api;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.swing.scrollbars.impl.MetalMarkedScrollBarUI;
import org.netbeans.swing.scrollbars.impl.WindowsMarkedScrollBarUI;
import org.netbeans.swing.scrollbars.impl.GenericScrollbarUI;
import org.netbeans.swing.scrollbars.spi.MarkingModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ScrollBarUI;
import org.netbeans.swing.scrollbars.spi.Mark;

/**
 * Factory that will produce an appropriate scrollbar for a marking model.
 *
 * @author  Tim Boudreau
 */
public class ScrollbarFactory {
    /** Creates a new instance of ScrollbarFactory */
    private ScrollbarFactory() {
    }
    
    private static MarkingModel currMdl = null;
    public static JScrollPane createScrollPane (MarkingModel mdl) {
        currMdl = mdl;
        JScrollPane result = new Pane(mdl);
        currMdl = null;
        return result;
    }
    
    private static final class Pane extends JScrollPane {
        private final MarkingModel mdl;
        
        Pane (MarkingModel mdl) {
            this.mdl = mdl;
        }
        
        public JScrollBar createVerticalScrollBar() {
            MarkingModel mdl = this.mdl;
            if (mdl == null) {
                //Unfortunately, this method is called in the superclass
                //constructor, and can't return null
                mdl = currMdl;
            }
            String id = UIManager.getLookAndFeel().getID();
            JScrollBar result;
            if ("Windows".equals(id)) {
                result = new ScrollBar(JScrollBar.VERTICAL);
                result.setUI (new WindowsMarkedScrollBarUI (mdl));
            } else if ("Metal".equals(id)) {
                result = new ScrollBar(JScrollBar.VERTICAL);
                result.setUI (new MetalMarkedScrollBarUI (mdl));
            } else {
                result = new GenericMarkedScrollbar(
                    super.createVerticalScrollBar(), mdl);
            }
            return result;
        }
        
        private class GenericMarkedScrollbar extends JScrollBar implements 
            PropertyChangeListener, AdjustmentListener, ChangeListener {
            
            private final MarkingModel mdl;
            private JScrollBar delegate;
            public GenericMarkedScrollbar (JScrollBar delegate, MarkingModel mdl) {
                this.mdl = mdl;
                init(delegate);
            }

            public void addNotify() {
                super.addNotify();
                System.err.println("ADD NOTIFY - MODEL IS " + mdl);
                mdl.addChangeListener (this);
                ToolTipManager.sharedInstance().registerComponent(this);
            }

            public void removeNotify () {
                mdl.removeChangeListener (this);
                ToolTipManager.sharedInstance().unregisterComponent(this);
                super.removeNotify();
            }

            private void init(JScrollBar delegate) {
                this.delegate = delegate;
                delegate.addPropertyChangeListener(this);
                delegate.addAdjustmentListener(this);
                add (delegate);
                addMouseListener (new MarkAndTrackListener());
            }

            private int getGap() {
                return 8;
            }

            private int getMargin() {
                return 2;
            }

            public String getToolTipText (MouseEvent me) {
                Rectangle r = getMarksRect();
                Point p = me.getPoint();
                if (r.contains (p)) {
                    Mark m = markAtPoint (p);
                    if (m != null) {
                        return m.getText();
                    }
                }
                return null;
            }

            public void paintComponent (Graphics g) {
                super.paintComponent (g);
                paintMarks(g);
            }

            /** Get the rectangle within which marks can be displayed.
             * @return The rectangle
             */
            protected final Rectangle getMarksRect () {
                Rectangle r = new Rectangle (0, 0, getWidth(), getHeight());
                if (getOrientation() == HORIZONTAL) {
                    r.y = r.height - getGap();
                    r.height -= getGap();
                } else {
                    r.x = r.width - getGap();
                    r.width -= getGap();
                }
                return r;
            }

            /** Get the total number of possible thumb positions on the resizable axis of the
             * scrollbar - the track height or width, depending on the scrollbar's
             * orientation
             * @return The total
             */
            protected final int getVisibleRange () {
                Rectangle mrect = getMarksRect();
                return (getOrientation() == JScrollBar.VERTICAL) ?
                    mrect.height : mrect.width;
            }

            /** Translate a location as given by a call to <code>someMark.getStart()</code>
             * into the coordinate space of the scrollbar.
             * @param loc The location given by the Mark object
             * @param max The maximum, given by the model
             * @return The coordinate in the space of the scrollbar, as a position along the
             * scrollbar's resizable axis.
             */
            protected final int translate (int loc, int max) {
                int range = getVisibleRange();
                double factor = range / max;
                double pos = factor * loc;
                return Math.round(Math.round(pos));
            }

            private java.util.HashMap marksRects = new java.util.HashMap();
            /** Find the mark at a given point in the component's coordinate space
             * @return The Mark object at that point, or null
             * @param p A point in the scrollbar's coordinate space
             */
            public Mark markAtPoint (Point p) {
                Rectangle rect = getMarksRect();
                if (!(rect.contains(p))) return null;
                java.util.Iterator i = marksRects.keySet().iterator();
                while (i.hasNext()) {
                    Rectangle r = (Rectangle) i.next();
                    if (r.contains(p)) return (Mark) marksRects.get(r);
                }
                return null;
            }

            /** Paint the marks displayed by this component.  Calculates the rectangle of the
             * mark and calls <code>paintMark()</code> with it.
             * @param g The Graphics object passed to the paint method
             */
            public void paintMarks (Graphics g) {
                boolean vertical = getOrientation() == JScrollBar.VERTICAL;

                Rectangle r = getMarksRect();
                if (mdl.size() == 0) return;

                int markRange = mdl.getMaxMarkLocation();
                java.util.Enumeration e = mdl.getMarks();

                while (e.hasMoreElements()) {
                    Mark m = (Mark) e.nextElement();
                    Rectangle curr;
                    if (vertical) {
                        r.y = translate (m.getStart(), markRange);
                        r.height = Math.max(getGap(), translate (m.getLength(), markRange));
                        r.width = getGap();
                        curr = new Rectangle(r.x-2, r.y, r.width+2, r.height);
                        marksRects.put (curr, m);
                    } else {
                        r.x = translate (m.getStart(), markRange);
                        r.width = Math.max(getGap(), translate (m.getLength(), markRange));
                        r.height = getGap();
                        curr = new Rectangle(r.x, r.y, r.width, r.height);
                        marksRects.put (curr, m);
                    }
                    Color color = (Color) m.get ("color"); //NOI18N
                    if (color == null) {
                        color = UIManager.getColor("windowText"); //NOI18N
                    }
        //            System.err.println ("Mark " + m + " rect " + r);
                    g.setColor (color);
                    g.fillRect (r.x + getMargin(), r.y, r.width-getMargin()-1, r.height);
                    g.setColor (color.darker());
                    g.drawRect (r.x + getMargin(), r.y, r.width-getMargin()-1, r.height);
                }
            }

            public void updateUI() {
                if (getUI() == null) {
                    setUI ((ScrollBarUI) GenericScrollbarUI.createUI(this));
                }
            }

            public void doLayout() {
                delegate.setBounds (0, 0, getWidth() - getGap(), getHeight());
            }

            public BoundedRangeModel getModel() {
                return delegate.getModel();
            }

            public void setModel (BoundedRangeModel mdl) {
                delegate.setModel (mdl);
            }


            public int getMaximum() {
                return delegate.getMaximum();
            }

            public int getMinimum() {
                return delegate.getMinimum();
            }

            public int getOrientation() {
                return delegate.getOrientation();
            }

            public void setOrientation (int o) {
                delegate.setOrientation(o);
            }

            public int getBlockIncrement() {
                return delegate.getBlockIncrement();
            }

            public int getUnitIncrement() {
                return delegate.getUnitIncrement();
            }

            public int getUnitIncrement(int dir) {
                return delegate.getUnitIncrement(dir);
            }

            public int getVisibleAmount() {
                return delegate.getVisibleAmount();
            }

            public void setEnabled (boolean val) {
                super.setEnabled(val);
                delegate.setEnabled(val);
            }

            public void setBlockIncrement (int val) {
                delegate.setBlockIncrement(val);
            }

            public void setValueIsAdjusting (boolean val) {
                delegate.setValueIsAdjusting(val);
            }

            public void setValues (int newValue, int newExtent, int newMin, int newMax) {
                delegate.setValues (newValue, newExtent, newMin, newMax);
            }

            public void setVisibleAmount (int val) {
                delegate.setVisibleAmount(val);
            }

            public boolean getValueIsAdjusting() {
                return delegate.getValueIsAdjusting();
            }

            public int getValue() {
                return delegate.getValue();
            }

            public void setValue (int i) {
                delegate.setValue(i);
            }

            public Dimension getPreferredSize() {
                Dimension result = new Dimension (delegate.getPreferredSize());
                if (getOrientation() == HORIZONTAL) {
                    result.height += getGap();
                } else {
                    result.width += getGap();
                }
                return result;
            }

            public Dimension getMaximumSize() {
                Dimension result = new Dimension (delegate.getMaximumSize());
                if (getOrientation() == HORIZONTAL) {
                    result.height += getGap();
                } else {
                    result.width += getGap();
                }
                return result;
            }

            public Dimension getMinimumSize() {
                Dimension result = new Dimension (delegate.getMaximumSize());
                if (getOrientation() == HORIZONTAL) {
                    result.height += getGap();
                } else {
                    result.width += getGap();
                }
                return result;
            }

            /**
             * This method gets called when a bound property is changed.
             *
             * @param evt A PropertyChangeEvent object describing the event source
             *            and the property that has changed.
             */
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                Object newValue = evt.getNewValue();
                Object oldValue = evt.getOldValue();
                if ("preferredSize".equals(name) || "minimumSize".equals(name) || "maximumSize".equals(name)) { //NOI18N
                    newValue = new Dimension ((Dimension) newValue);
                    oldValue = new Dimension ((Dimension) oldValue);
                    if (getOrientation() == JScrollBar.VERTICAL) {
                        ((Dimension) newValue).width += getGap();
                        ((Dimension) oldValue).width += getGap();
                    } else {
                        ((Dimension) newValue).height += getGap();
                        ((Dimension) oldValue).height += getGap();
                    }
                }
                firePropertyChange (evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            }

            /**
             * Invoked when the value of the adjustable has changed.
             */
            public void adjustmentValueChanged(AdjustmentEvent e) {
                fireAdjustmentValueChanged(e.getID(), e.getAdjustmentType(), e.getValue());
            }

            private void goToMark (Mark m) {
                Rectangle r = getMarksRect();
                int loc = translate (m.getStart(), r.height);
                setValue(translateToScrollbarModel (loc));
                m.select();
            }

            private int translateToScrollbarModel (int i) {
                BoundedRangeModel mod = getModel();
                int min = mod.getMinimum();
                int max = mod.getMaximum();
                int mrange = max - min;

                double factor = mrange / mdl.getMaxMarkLocation();
                double pos = (i * factor) + min;
                return Math.round(Math.round(pos));
            }

            /**
             * Invoked when the target of the listener has changed its state.
             *
             * @param e a ChangeEvent object
             */
            public void stateChanged(ChangeEvent e) {
                Rectangle r = getMarksRect();
                repaint(r.x, r.y, r.width, r.height);
            }

            protected class MarkAndTrackListener extends MouseAdapter {
                public void mousePressed (MouseEvent e) {
                    if (getValueIsAdjusting ()) {
                        super.mousePressed (e);
                        return;
                    }
                    if (SwingUtilities.isRightMouseButton(e) ||
                     SwingUtilities.isMiddleMouseButton(e) || !isEnabled()) {
                        return;
                    }

                    Point p = e.getPoint();
                    Rectangle r = getMarksRect();

                    if (r.contains (p)) {
                        Mark m = markAtPoint (p);
                        if (m != null) {
                            goToMark (m);
                        }
                    } else {
                        super.mousePressed(e);
                    }
                }
            }
        }
    }
}
