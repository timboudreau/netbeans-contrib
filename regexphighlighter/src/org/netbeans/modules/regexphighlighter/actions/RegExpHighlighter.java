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
package org.netbeans.modules.regexphighlighter.actions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

/**
 * The Regular Expression Highlighter.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class RegExpHighlighter implements PropertyChangeListener, ChangeListener {
    private static RegExpHighlighter INSTANCE = new RegExpHighlighter();

    public static RegExpHighlighter getDefault() {
        return INSTANCE;
    }

    private Color[] colors = new Color[] {
        new Color(202, 255, 112),
        new Color(193, 255, 193),
        new Color(224, 255, 255),
        new Color(255, 193, 193),
        new Color(255, 187, 255),
        new Color(255, 222, 255),
        new Color(255, 239, 213),
    };

    /**
     * Holds value of property regExp.
     */
    private String regExp = "";

    /**
     * Holds value of property matchCase.
     */
    private boolean matchCase;

    /**
     * Holds value of property highlight.
     */
    private boolean highlight;

    /**
     * Holds value of property highlightGroups.
     */
    private boolean highlightGroups;

    private Map/*<JTextComponent, FileObject>*/ comp2FO;
    private Map/*<FileObject, Collection<JTextComponent>>*/ fo2Comp;
    private Map/*<JTextComponent, HighlightLayer>*/ comp2Highlights;

    public RegExpHighlighter() {
        comp2FO = new WeakHashMap/*<JTextComponent, FileObject>*/();
        fo2Comp = new WeakHashMap/*<FileObject, Collection<JTextComponent>>*/();
        comp2Highlights = new WeakHashMap/*<JTextComponent, HighlightLayer>*/();
    }

    public void stateChanged(ChangeEvent e) {
        assureRegistered(Registry.getMostActiveComponent());
        updateHighlightRegExp();
    }

    /**
     * Getter for property regExp.
     * @return Value of property regExp.
     */
    public String getRegExp() {
        return this.regExp;
    }

    /**
     * Setter for property regExp.
     * @param regExp New value of property regExp.
     */
    public void setRegExp(String regExp) {
        this.regExp = regExp;
        if (regExp == null || regExp.length() == 0) {
            clearAllHighlights();
        } else {
            updateHighlightRegExp();
        }
    }

    /**
     * Getter for property matchCase.
     * @return Value of property matchCase.
     */
    public boolean isMatchCase() {
        return this.matchCase;
    }

    /**
     * Setter for property matchCase.
     * @param matchCase New value of property matchCase.
     */
    public void setMatchCase(boolean matchCase) {
        this.matchCase = matchCase;
        updateHighlightRegExp();
    }

    /**
     * Getter for property highlight.
     * @return Value of property highlight.
     */
    public boolean isHighlight() {
        return this.highlight;
    }

    /**
     * Setter for property highlight.
     * @param highlight New value of property highlight.
     */
    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
        if (highlight) {
            updateHighlightRegExp();
        } else {
            clearAllHighlights();
        }
    }

    /**
     * Getter for property highlightGroups.
     * @return Value of property highlightGroups.
     */
    public boolean isHighlightGroups() {
        return this.highlightGroups;
    }

    /**
     * Setter for property highlightGroups.
     * @param highlightGroups New value of property highlightGroups.
     */
    public void setHighlightGroups(boolean highlightGroups) {
        this.highlightGroups = highlightGroups;
        if (highlight) {
            updateHighlightRegExp();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("document".equals(evt.getPropertyName())) { // NOI18N
            updateFileObjectMapping((JTextComponent) evt.getSource());
        }
    }

    Pattern compileRegExp(String regExp) throws PatternSyntaxException {
        if (matchCase) {
            return Pattern.compile(regExp, Pattern.MULTILINE);
        } else {
            return Pattern.compile(regExp, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        }
    }

    private synchronized void assureRegistered(JTextComponent c) {
        if (c == null || comp2Highlights.get(c) != null)
            return ;

        comp2FO.put(c, null);
        c.addPropertyChangeListener(this);
        updateFileObjectMapping(c);
        comp2Highlights.put(c, new ArrayList());
    }

    private synchronized void updateFileObjectMapping(JTextComponent c) {
        Document doc = c.getDocument();
        Object   stream = doc.getProperty(Document.StreamDescriptionProperty);

        FileObject old = (FileObject) comp2FO.put(c, null);

        if (old != null) {
            Collection/*<JTextComponent>*/ components = (Collection) fo2Comp.get(old);

            if (components != null) {
                components.remove(old);
            }
        }

        if (stream != null && stream instanceof DataObject) {
            FileObject fo = ((DataObject) stream).getPrimaryFile();

            comp2FO.put(c, fo);
            getComponents(fo).add(c);
        }
    }

    private Collection/*<JTextComponent>*/ getComponents(FileObject fo) {
        Collection/*<JTextComponent>*/ components = (Collection) fo2Comp.get(fo);

        if (components == null) {
            fo2Comp.put(fo, components = new ArrayList/*<JTextComponent>*/());
        }

        return components;
    }

    private void clearAllHighlights() {
        for (Iterator it = fo2Comp.keySet().iterator(); it.hasNext();) {
            FileObject fileObject = (FileObject) it.next();
            clearHighlights(fileObject);
        }
    }

    private void clearHighlights(FileObject fo) {
        clearHighlights(fo, true);
    }

    private void updateHighlightRegExp() {
        JTextComponent textComponent = Registry.getMostActiveComponent();
        if (textComponent == null) {
            return;
        }

        if (!(textComponent.getDocument() instanceof BaseDocument)) {
            return;
        }

        if (textComponent.getDocument().getLength() == 0) {
            return;
        }

        FileObject fileObject = NbEditorUtilities.getFileObject(textComponent.getDocument());
        if (fileObject == null) {
            return;
        }

        String regExp = getRegExp();
        if (regExp == null || regExp.length() == 0) {
            clearHighlights(fileObject);
        } else {
            if (isHighlight()) {
                try {
                    // is it a valid regexp?
                    Pattern compiledRegExp = compileRegExp(regExp);

                    Document document = textComponent.getDocument();
                    int length = document.getLength();
                    String text = textComponent.getDocument().getText(0, length);
                    Matcher matcher = compiledRegExp.matcher(text);

                    clearHighlights(fileObject, false);
                    List regExpMatches = new ArrayList();
                    while (matcher.find()) {
                        for (int i = 0 ; i < Math.min(colors.length, matcher.groupCount() + 1); i++) {
                            int start = matcher.start(i);
                            int end = matcher.end(i);
                            if (start >= end) {
                                continue;
                            }
                            Position startPosition = NbDocument.createPosition(document, start,  Position.Bias.Forward);
                            Position endPosition = NbDocument.createPosition(document,   end,    Position.Bias.Forward);
                            regExpMatches.add(new RegExpHighlight(colors[i], startPosition, endPosition));
                            if (!highlightGroups) {
                                // bail out after first iteration
                                break;
                            }
                        }
                    }
                    setHighlights(fileObject, regExpMatches);
                } catch (PatternSyntaxException pse) {
                    clearHighlights(fileObject);
                    return;
                } catch (BadLocationException ble) {
                    clearHighlights(fileObject);
                    return;
                }
            } else {
                clearHighlights(fileObject);
            }
        }
    }

    private void setHighlights(FileObject fo, Collection/*<Highlight>*/ highlights) {
        for (Iterator i = getComponents(fo).iterator(); i.hasNext(); ) {
            JTextComponent c = (JTextComponent) i.next();

            //
            Highlighter highlighter = c.getHighlighter();

            // Clear existing highlights
            clearHighlights(fo, false);

            // Add new highlights
            List compHighlights = (List) comp2Highlights.get(c);
            for (Iterator it = highlights.iterator(); it.hasNext();) {
                RegExpHighlight regExpHighlight = (RegExpHighlight) it.next();
                try {
                    Object tag = highlighter.addHighlight(regExpHighlight.getStart(),
                            regExpHighlight.getEnd() - 1, new RegExpHighlightPainter(regExpHighlight.getColor()));
                    compHighlights.add(tag);
                } catch (BadLocationException ble) {

                }
            }
            c.repaint();
        }
    }

    private void clearHighlights(FileObject fo, boolean repaint) {
        for (Iterator i = getComponents(fo).iterator(); i.hasNext(); ) {
            JTextComponent c = (JTextComponent) i.next();
            Highlighter highlighter = c.getHighlighter();

            // Clear existing highlights
            List compHighlights = (List) comp2Highlights.get(c);
            for (Iterator it = compHighlights.iterator(); it.hasNext();) {
                Object tag = (Object) it.next();
                highlighter.removeHighlight(tag);
            }
            compHighlights.clear();
            c.repaint();
        }
    }

    private static class RegExpHighlightPainter implements Highlighter.HighlightPainter {
        private Color color;

        public RegExpHighlightPainter(Color color) {
            this.color = color;
        }

        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            try{
                Rectangle alloc = bounds.getBounds();
                TextUI textUI = c.getUI();
                Rectangle rect0 = textUI.modelToView(c, p0);
                Rectangle rect1 = textUI.modelToView(c, p1);

                if (rect0 == null || rect1 == null) {
                    return;
                }

                g.setColor(color);
                // Single line highlight
                if(rect0.y==rect1.y){
                    Rectangle r = rect0.union(rect1);
                    g.fillRect(r.x, r.y, r.width, r.height);
                } else{
                    // Multi line highlight
                    int p0ToMarginWidth = alloc.x+alloc.width-rect0.x;
                    // first line
                    g.fillRect(rect0.x, rect0.y, p0ToMarginWidth, rect0.height);
                    if((rect0.y+rect0.height)!=rect1.y){
                        // Second to penultimate lines highlight - left to right edge
                        g.fillRect(alloc.x, rect0.y+rect0.height, alloc.width,
                                rect1.y-(rect0.y+rect0.height));
                    }
                    // Last line highlight
                    g.fillRect(alloc.x, rect1.y, (rect1.x-alloc.x), rect1.height);
                }
            } catch(BadLocationException e){
                // can't render
            }
        }
    }
}
