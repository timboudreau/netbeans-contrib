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
import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;

/**
 *
 * @author Tim Boudreau
 */
final class FishEyeTextView extends JComponent implements DocumentListener {
    private Document document;
    private Point locus;
    private List <FiMark> marks;
    private boolean bubblesVisible;
    public FishEyeTextView(Document document, List <FiMark> marks) {
        this.document = document;
        this.marks = marks;
        initFont();
        enableEvents (AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        document.addDocumentListener(this);
    }
    
    public FishEyeTextView(JTextComponent jtc) {
        initFont();
        enableEvents (AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        marks = new ArrayList<FiMark> ();
        setDocument (jtc.getDocument());
    }
    
    public void setDocument (Document doc) {
        if (this.document != null) {
            this.document.removeDocumentListener(this);
        }
        this.document = doc;
        if (doc != null) {
            doc.addDocumentListener (this);
        }
    }
    
    public void setMarks (List <FiMark> marks) {
        this.marks = marks;
        if (isShowing()) repaint();
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (e.getID() == MouseEvent.MOUSE_EXITED) {
            setLocus (e.getPoint());
        } else if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            setLocus (new Point (0, e.getPoint().y));
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        super.processMouseMotionEvent(e);
        if (e.getID() == MouseEvent.MOUSE_MOVED || e.getID() == MouseEvent.MOUSE_DRAGGED) {
            setLocus (new Point (0, e.getPoint().y));
        }
    }
    
    private FontWrapper[] fonts = new FontWrapper[20];
    private void initFont() {
        BufferedImage b = new BufferedImage (1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = b.createGraphics();
        Font f = new Font ("Monospaced", Font.PLAIN, 13);
        for (int i = 0; i < fonts.length; i++) {
            double len = fonts.length;
            double ii = i + 1;
            double scaleY = Math.max (0.25D, ii / len);
            double scaleX;
            if (i < fonts.length / 4) {
                scaleX = 0.4D;
            } else if (i < fonts.length / 2) {
                scaleX = 0.6D;
            } else if (i < (fonts.length / 2) + (fonts.length / 4)) {
                scaleX = 0.8;
            } else if (i <= fonts.length - 1) {
                scaleX = 1.0D;
            } else {
                scaleX = 1.0D;
            }
            scaleX = scaleY;
            fonts[i] = new FontWrapper (f.deriveFont(AffineTransform.getScaleInstance(scaleX, scaleY)), g);
        }
        g.dispose();
    }
    
    private static final class FontWrapper {
        public final Font font;
        public final int charw;
        public final int h;
        public final int maxDescent;
        public final int maxAscent;
        public FontWrapper (Font font, Graphics2D g) {
            this.font = font;
            FontMetrics fm = g.getFontMetrics(font);
            charw = fm.charWidth('x');
            h = fm.getHeight();
            maxDescent = fm.getMaxDescent();
            maxAscent = fm.getMaxAscent();
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension (500, 300);
    }
    
    private Segment seg = new Segment (new char[1024], 0, 1024);
    private Segment seg (int len) {
        if (len > seg.last()) {
            seg = new Segment (new char[len], 0, len);
        }
        return seg;
    }
    
    public Integer getLocusElement (double total, double height) {
        if (locus == null) {
            return null;
        }
        double factor = (double) locus.y / height;
        return (int) (total * factor);
    }
    
    public Integer getLocusElement() {
        Element root = document.getDefaultRootElement();
        int ct = root.getElementCount();
        Insets ins = getInsets();
        int h = getHeight() - (ins.top + ins.bottom) ;
        Integer locus = getLocusElement(ct, h);
        if (locus == null) {
            return null;
        }
        int locusElement = Math.max(0, Math.min (ct-1, locus));
        return locusElement;
    }
    
    float alpha = -1;
    boolean forward = false;
    public float setAlpha (float alpha, boolean forward) {
        this.forward = forward;
        float old = this.alpha;
        this.alpha = alpha;
        return old;
    }
    
    public float getAlpha() {
        return alpha;
    }
    
    Map <FiMark, Integer> marks2yCoords = new HashMap <FiMark, Integer> ();
    @Override
    public void paint (Graphics g) {
        if (document == null) {
            return;
        }
        marks2yCoords.clear();
        Graphics2D gg = (Graphics2D) g;
        if (alpha != -1) {
            alpha += forward ? 0.25F : -0.16F;
            if (alpha <= 0F || alpha > 1F) {
                alpha = -1;
                if (!forward) {
                    return;
                }
            } else {
                gg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            }
        }
        Color yellow = new Color (255, 255, 215);
        g.setColor(yellow);
        g.fillRect (0,0, getWidth(),getHeight());
        g.setColor (Color.BLACK);
        try {
            Element root = document.getDefaultRootElement();
            
            Insets ins = getInsets();
            int elementCount = root.getElementCount();
            int h = getHeight() - (ins.top + ins.bottom) ;
            int locusElement = Math.min (elementCount-1, getLocusElement(elementCount, h));
            
            int numberOfLinesInZoomWindow = 17;
            int locusLine = (numberOfLinesInZoomWindow / 2) + 1;
            int start = locusElement - locusLine;
            if (start < 0) {
                locusLine = locusElement;
                start = 0;
            }
            int end = start + numberOfLinesInZoomWindow;
            if (end > elementCount) {
                end = elementCount;
                if (locusLine > end) {
                    locusLine = end;
                }
            }
            FontWrapper[] fw = new FontWrapper[end - start];
            int windowHeight = 0;
            for (int i=0; i < end - start; i++) {
                fw[i] = fontForIndex (end - start, i, locusLine);
                windowHeight += fw[i].h;
            }
            
            double startY = locus.y - (windowHeight / 2);
            int topHeight = -1;
            if (startY < 20) {
                startY = 5 * locusLine;
                topHeight = (int) startY;
            }
            boolean bottomNotPainted = startY + windowHeight > getHeight();
            int bottomHeight = -1;
            if (bottomNotPainted) {
                startY = getHeight() - windowHeight;
            }

            int half = windowHeight / 2;
            GradientPaint gp = new GradientPaint (0, (int) startY, yellow, 0, (int)(startY + half), Color.WHITE);
            gg.setPaint(gp);
            gg.fillRect(0, (int) startY, getWidth(), half);
            GradientPaint gp2 = new GradientPaint (0,(int) startY + half, Color.WHITE, 0, (int) startY + windowHeight, yellow);
            gg.setPaint(gp2);
            gg.fillRect(0, (int) startY + half, getWidth(), half);
            
            g.setColor(Color.BLACK);
            
            Iterator <FiMark> markIter = marks.iterator();
            FiMark[] currMark = markIter.hasNext() ? new FiMark[] { markIter.next() } :
                new FiMark[1];
            while (currMark[0] != null && currMark[0].getLine() < start && markIter.hasNext()) {
                currMark[0] = markIter.next();
            }
            
            gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int yForAbove = (int) startY;
            paintContent (locusLine, g, start, end - start, startY, root, elementCount, fw, markIter, currMark, marks2yCoords, false );
            double cheight = paintContent (locusLine, g, start, end - start, startY, root, elementCount, fw, markIter, currMark, marks2yCoords, true );

            int startElementForTop = Math.max (0, locusElement - (numberOfLinesInZoomWindow / 2));
            markIter = marks.iterator();
            currMark = markIter.hasNext() ? new FiMark[] { markIter.next() } :
                new FiMark[1];
            
            double y = cheight + paintIt (g, 0, startElementForTop-1, 0, topHeight == -1 ? (int) startY : topHeight, root, markIter, currMark, false);

            markIter = marks.iterator();
            currMark = markIter.hasNext() ? new FiMark[] { markIter.next() } :
                new FiMark[1];
            
            int bh = topHeight == -1 ? (int) startY : topHeight;
            double oldY = cheight + paintIt (g, 0, startElementForTop-1, 0, bh, root, markIter, currMark, true);

            int startElementForBottom = locusElement + (numberOfLinesInZoomWindow / 2) + 1;
            int countForBottom = elementCount - (locusElement + 1);
            
            markIter = marks.iterator();
            currMark = markIter.hasNext() ? new FiMark[] { markIter.next() } :
                new FiMark[1];
            while (currMark[0] != null && markIter.hasNext() && currMark[0] != null && currMark[0].getLine() < startElementForBottom && currMark[0].getEndLine() < startElementForBottom) {
                FiMark old = currMark[0];
                currMark[0] = markIter.next();
            }
            
            int yForBottom = (int) y;
            log = true;
            
            y += paintIt (g, startElementForBottom, countForBottom, y, 
                    bottomHeight == -1 ? h - (int) y : bottomHeight, root, 
                    markIter, currMark, false);
            
            markIter = marks.iterator();
            currMark = markIter.hasNext() ? new FiMark[] { markIter.next() } :
                new FiMark[1];
            while (currMark[0] != null && markIter.hasNext() && currMark[0] != null && currMark[0].getLine() < startElementForBottom && currMark[0].getEndLine() < startElementForBottom) {
                FiMark old = currMark[0];
                currMark[0] = markIter.next();
            }
            oldY += paintIt (g, startElementForBottom, countForBottom, oldY, 
                    bottomHeight == -1 ? h - (int) oldY : bottomHeight, root, 
                    markIter, currMark, true);

            log = false;
            
            gg.setColor (Color.GRAY);
            gg.setFont (new Font ("Courier New", Font.PLAIN, 13));
            String s = locusElement + ":" + elementCount;
            int w = gg.getFontMetrics().stringWidth(s);
            gg.drawString(s, getWidth() - (w + 10), getHeight() - (gg.getFontMetrics().getHeight() + 10));
            
            boolean showBubblesAbove = yForBottom > getHeight() / 2;
            int bubbleY = showBubblesAbove ? yForAbove : yForBottom;
            
            if (!marks2yCoords.isEmpty() && bubblesVisible && bubbleAlpha > 0.0F) {
                gg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                gg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int annY=0;
                for (Iterator i=marks2yCoords.keySet().iterator(); i.hasNext();) {
                    FiMark curr = (FiMark) i.next();
                    if (curr == null) break;
                    String desc = curr.getDescription().trim();
                    g.setFont (new Font("Sans Serif", Font.BOLD, 18)); //NOI18N
                    FontMetrics fm = g.getFontMetrics();
                    
                    int maxLen = (getWidth() / fm.getMaxAdvance()) - 2;
                    
                    int ht = gg.getFontMetrics().getHeight();

                    Bubble bubble = new Bubble (curr, fm, getWidth(), 
                            getHeight());
                    
                    int locY = bubbleY + annY;
                    Rectangle r = bubble.paint(gg, this, locY, bubbleAlpha);
                    if (showBubblesAbove) {
                        annY -= r.height;
                    } else {
                        annY += r.height;
                    }
                }
            }
            if (marks2yCoords.isEmpty()) {
                bubbleAlpha = 0.1F;
                bubblesVisible = false;
            }
        } catch (BadLocationException bleeee) {
            bleeee.printStackTrace();
        }
    }
    
    private static final class Strings {
        final int maxPxWidth;
        final String[] s;
        public Strings (String[] s, int maxPxWidth) {
            this.s = s;
            this.maxPxWidth = maxPxWidth;
        }
        int size() {
            return s.length;
        }
    }
    
    void hidden() {
        if (bubbleTimer != null) {
            bubbleTimer.abort();
            bubbleTimer = null;
        }
        if (bubbleAnimStartTimer != null) {
            bubbleAnimStartTimer.abort();
            bubbleAnimStartTimer = null;
        }
        bubbleAlpha = 0.1F;
        bubblesVisible = false;
        locusOnBubbleStart = null;
    }
    
    boolean log = false;
    FontWrapper fontForIndex (int count, int index, int center) {
        int distToCenter = (int) ((double) Math.abs (center - index) * 1.5);
        return fonts[Math.max (0, Math.min (fonts.length - 1, (fonts.length - 1) - distToCenter))];
    }
    
    public double paintContent (int locusElement, Graphics g, int startElement, int count, double y, Element root, int total, FontWrapper[] fws, Iterator<FiMark> markIter, FiMark[] currMark, Map<FiMark, Integer> marks2yCoords, boolean reallyPaint) throws BadLocationException {
        double startY = y;
        Insets ins = getInsets();
        int x = ins.left;
        String[] lines = wrapString(root, startElement, count, total);
        for (int i = 0; i < lines.length; i++) {
            if (i + startElement >= total) {
                break;
            }
            Graphics2D gg = (Graphics2D) g;
            FontWrapper fw = fws[i];
            boolean isMark = 
                    currMark[0] != null && 
                    i + startElement >= currMark[0].getLine() && 
                    startElement <= currMark[0].getEndLine(); 

            if (isMark && marks2yCoords != null) {
                marks2yCoords.put (currMark[0], (int)y);
            }
            if (isMark && !reallyPaint) {
                Color c = g.getColor();
                g.setColor(currMark[0].getColor());
                g.fillRect(0, (int) y, getWidth(), Math.max( 2, fw.h));
                Icon icon = currMark[0].getIcon();
                if (icon != null) {
                    int iconY;
                    if (fw.h < icon.getIconHeight()) {
                        iconY = ((int) y) + (fw.h / 2);
                    } else {
                        iconY = (int) y;
                    }
                    icon.paintIcon(this, g, getWidth() - (3 + 
                            icon.getIconWidth()), iconY);
                }
                if (markIter.hasNext() && i + startElement >= currMark[0].getEndLine()) {
                    currMark[0] = markIter.next();
                } else if (!markIter.hasNext()) {
                    currMark[0] = null;
                }
                gg.setColor(Color.BLACK);
            }
            if (i == locusElement && !reallyPaint) {
                int fh = fw.h;
                if (!isMark) {
                    g.setColor (new Color (224, 224, 255));
                    g.fillRect(0, (int) (y + fw.maxAscent+fw.maxDescent)-fh, getWidth(), fh);
                    g.setColor (g.getColor().darker());
                } else {
                    g.setColor (Color.BLACK);
                }
                g.drawLine(0, (int) (y + fw.maxAscent+fw.maxDescent)-fh,
                        getWidth(), (int) (y + fw.maxAscent+fw.maxDescent)-fh);
                
                g.drawLine(0, (int) (y + fw.maxAscent+fw.maxDescent),
                        getWidth(), (int) (y + fw.maxAscent+fw.maxDescent));
                
                g.setColor (Color.BLACK);
            }
            double offY = fw.h;
            if (reallyPaint) {
                gg.setFont(fw.font);
                gg.drawString(lines[i], x, (float) y + (float) fw.maxAscent);
            }
            y += (offY - 4);
        }
        return y - startY;
    }
    
    private String[] wrapString (Element root, int startElement, int count, int total) throws BadLocationException {
        if (count + startElement > total) {
            count = total - startElement;
        }
        int firstNonWhitespace = Integer.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            if (startElement + i >= root.getElementCount()) {
                break;
            }
            Element el = root.getElement(startElement + i);
            int start = el.getStartOffset();
            int end = el.getEndOffset();
            int len = end - start;
            String s = document.getText(start, len);
            if (s.trim().length() == 0) {
                continue;
            }
            for (int j = 0; j < len; j++) {
                if (!Character.isWhitespace(s.charAt(j))) {
                    firstNonWhitespace = Math.min (firstNonWhitespace, j);
                    break;
                }
            }
        }
        if (firstNonWhitespace == Integer.MAX_VALUE) {
            firstNonWhitespace = 0;
        }
        List <String> result = new ArrayList<String>(count);
        for (int i = 0; i < count; i++) {
            Element el = root.getElement(startElement + i);
            int start = el.getStartOffset();
            int end = el.getEndOffset();
            int len = end - start;
            String s = document.getText(start, len);
            if (s.trim().length() == 0) {
                result.add (s.trim());
                continue;
            }
            if (firstNonWhitespace > 0) {
                s = s.substring(firstNonWhitespace);
            }
            result.add (s);
        }
        String[] ss = result.toArray(new String[result.size()]);
        return ss;
    }
    
    public double paintIt (Graphics g, int startElement, int count, double y, int h, Element root, Iterator markIter, FiMark[] currMark, boolean reallyPaint) throws BadLocationException {
        Insets ins = getInsets();
        int ct = count;
        int x = ins.left;
        g.setColor (Color.BLACK);
        Graphics2D gg = (Graphics2D) g;
        Segment segment;
        double offY = 0;
        
        double pxPerLine = (double) h / (double) ct;
        int fontSize = 1;
        for (int i = fonts.length - 1; i >= 0; i--) {
            if (h / (fonts[i].h + 1) >= ct) {
                fontSize = i;
                break;
            }
        }
        double startY = y;
        for (int i=startElement; i < startElement + ct; i++) {
            if (i < 0) {
                continue;
            }
            if (i >= root.getElementCount()) {
                break;
            }
            Element el = root.getElement(i);
            if (el == null) {
                break;
            }
            int start = el.getStartOffset();
            int end = el.getEndOffset();
            FontWrapper fw = fonts[fontSize];
            boolean isMark = currMark[0] != null && 
                    i >= currMark[0].getLine() && 
                    i <= currMark[0].getEndLine();
            
            if (isMark && !reallyPaint) {
                Color c = g.getColor();
                g.setColor(currMark[0].getColor());
                g.fillRect(0, (int) y, getWidth(), (int) Math.ceil(pxPerLine));
                
                Icon icon = currMark[0].getIcon();
                if (icon != null) {
                    int iconY = (int) y;
                    if (pxPerLine < icon.getIconHeight()) {
                        iconY -= icon.getIconHeight() / 2;
                    }
                    icon.paintIcon(this, g, getWidth() - 
                            (3 + icon.getIconWidth()), iconY);
                }
                
                if (i == currMark[0].getEndLine() && markIter.hasNext()) {
                    currMark[0] = (FiMark) markIter.next();
                } else if (!markIter.hasNext()) {
                    currMark[0] = null;
                }
                gg.setColor(Color.BLACK);
            }
            gg.setFont(fw.font);
            offY = Math.min (pxPerLine, fw.h);
            if (reallyPaint) {
                document.getText (start, end-start, segment = seg (end-start));
                if (fw.h <= 2) {
                    int xx = 0;
                        for (int j = 0; j < segment.count; j++) {
                            g.setColor (new Color (180, 180, 180));
                            if (!Character.isWhitespace(segment.array[j + segment.offset])) {
                                g.drawLine(xx, (int) y, xx+2, (int) y);
                            }
                            xx+=2;
                        }
                } else {
                    String s = new String (segment.array, segment.offset, segment.count);
                    gg.drawString(s, x, (float) y + (float) fw.maxAscent);
                }
            }
            y += offY;
        }
        return y - startY;
    }

    @SuppressWarnings ("deprecation")
    @Override
    public void reshape (int x, int y, int w, int h) {
        super.reshape (x, y, w, h);
        locus = new Point (0, h / 2);
    }
    
    private final BubbleHandler bubbleHandler = new BubbleHandler();
    private Point locusOnBubbleStart;
    private class BubbleHandler implements ToggleTimer.MultiStepHandler {
        public void start(ToggleTimer timer, boolean direction) {
            setBubbleAlpha (0.1F);
            setBubblesVisible (true);
            locusOnBubbleStart = locus;
        }

        public void finish(ToggleTimer timer, boolean direction) {
            setBubbleAlpha (direction ? 1.0F : 0.1F);
            setBubblesVisible (direction);
            bubbleTimer = null;
        }
        
        public void tick(ToggleTimer timer, int index, boolean direction) {
//            setBubbleAlpha (0.1F * (float) index);
            setBubbleAlpha (bubbleAlpha + 0.1F);
            if (bubbleAlpha >= 1) {
                bubbleAlpha = 1;
                timer.forceFinish();
            }
        }

        public void aborted(ToggleTimer timer, int at, boolean direction) {
            setBubbleAlpha (0.0F);
            setBubblesVisible (false);
            bubbleTimer = null;
        }
    }
    
    @Override
    public void repaint() {
        super.repaint();
    }
    
    float bubbleAlpha = 0.0F;
    
    private static final float MAX_BUBBLE_ALPHA = 0.6F;
    private void setBubbleAlpha (float f) {
        if (bubbleAlpha >= MAX_BUBBLE_ALPHA && f > bubbleAlpha) return;
                
        if (f != bubbleAlpha) {
            bubbleAlpha = f;
//            if (f < 1) {
//                paintImmediately (0, 0, getWidth(), getHeight());
//            } else {
//                repaint();
//            }
            repaint();
        }
    }
    
    private void startPrepareToAnimateTimer() {
        if (bubblesVisible) return;
        if (bubbleAnimStartTimer == null) {
            bubbleAnimStartTimer = new ToggleTimer (bubbleAnimStartHandler, 5, 
                    true, 50);
        } else {
            bubbleAnimStartTimer.restart();
        }
    }
    
    private BubbleAnimStartHandler bubbleAnimStartHandler = 
            new BubbleAnimStartHandler();
    
    private Point initialLocus;
    private class BubbleAnimStartHandler implements ToggleTimer.MultiStepHandler {
        public void tick(ToggleTimer timer, int index, boolean direction) {
            int ix = locus == null ? -1 : locus.y;
            if (Math.abs (ix - initialLocus.y) > 30) {
                
                timer.abort();
            }
        }

        public void aborted(ToggleTimer timer, int at, boolean direction) {
            if (bubbleTimer != null) {
                bubbleTimer.abort();
                bubbleTimer = null;
//                initialLocus = null;
            }
        }

        public void start(ToggleTimer timer, boolean direction) {
            initialLocus = locus == null ? new Point() : locus;
        }

        public void finish(ToggleTimer timer, boolean direction) {
            if (isShowing()) {
                startBubbleAnimation (true);
            }
            bubbleAnimStartTimer = null;
        }
        
    }
    
    private ToggleTimer bubbleTimer;
    private ToggleTimer bubbleAnimStartTimer;
    
    private void startBubbleAnimation(boolean dir) {
        if (dir && bubblesVisible && bubbleAlpha >= 1) {
            return;
        }
        if (bubbleTimer != null) {
            if (bubbleTimer.getDirection() != dir) {
                bubbleTimer.reverse();
            }
            bubbleTimer.restart();
        } else {
            bubbleTimer = new ToggleTimer (bubbleHandler, 70, dir, 50);
        }
        setBubbleAlpha(0.1F);
        setBubblesVisible (true);
        initialLocus = null;
    }
    
    private void setBubblesVisible (boolean val) {
        if (val != bubblesVisible) {
            this.bubblesVisible = val;
            
            repaint();
        }
    }
    
    public void setLocus (Point p) {
        if (this.locus == null || !this.locus.equals(p)) {
            this.locus = p;
            //normalize:
            if (locus.y >= getHeight()) {
                locus.y = getHeight() -1;
            }
            if (locus.y < 0) {
                locus.y = 0;
            }
            if (p == null) {
                locus = new Point (0, getHeight() / 2);
            }
            if (locusOnBubbleStart != null) {
                if (Math.abs (p.y - locusOnBubbleStart.y) > 40) {
                    setBubbleAlpha(0.1F);
                    setBubblesVisible (false);
                    locusOnBubbleStart = null;
                }
            }
            startPrepareToAnimateTimer();
            repaint();
        }
    }

    public void insertUpdate(DocumentEvent e) {
        repaint();
    }

    public void removeUpdate(DocumentEvent e) {
        repaint();
    }

    public void changedUpdate(DocumentEvent e) {
        repaint();
    }
}
