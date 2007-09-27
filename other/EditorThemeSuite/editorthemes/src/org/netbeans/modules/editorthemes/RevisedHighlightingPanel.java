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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.editorthemes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import net.java.dev.colorchooser.*;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.netbeans.modules.editorthemes.ColorModel.Preview;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tim Boudreau
 */
class RevisedHighlightingPanel extends JPanel implements SingleColoringPanel.Factory.Controller {

    private final JPanel innerPanel = new JPanel();
    private final JScrollPane pane = new JScrollPane(innerPanel);
    private ColorModel colorModel;
    private String currentProfile;
    private boolean listen;
    private boolean changed;
    private final SingleColoringPanel.Factory factory;
    private final UIKind kind;
    private final JSplitPane split = new JSplitPane();
    private Preview preview;

    public RevisedHighlightingPanel(UIKind kind) {
        this.kind = kind;
        setName(kind.toString());
        this.factory = new SingleColoringPanel.Factory(this);
        setLayout(new BorderLayout());
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
        add(pane, BorderLayout.CENTER);
        Border b = BorderFactory.createEmptyBorder();
        pane.setBorder(b);
        pane.setViewportBorder(b);
    }

    public void clear() {
        innerPanel.removeAll();
        invalidate();
        revalidate();
        repaint();
    }

    public void update(ColorModel colorModel) {
        this.colorModel = colorModel;
        currentProfile = colorModel.getCurrentProfile();
        listen = false;
        setCurrentProfile(currentProfile);
        refreshUI();
        listen = true;
        changed = false;
    }

    private boolean colorEquals(Color a, Color b) {
        if (a == b) {
            return true;
        } else if ((a == null) != (b == null)) {
            return false;
        } else if (a != null && a.equals(b)) {
            return true;
        } else if (a != null && b != null) {
            return a.getRed() == b.getRed() && a.getGreen() == b.getGreen() && a.getBlue() == b.getBlue();
        } else {
            return false;
        }
    }

    void refreshUI() {
        clear();
        Iterable<AttributeSet> categories = getCategories(currentProfile);
        AttributeSet defaultColoring = getDefaultColoring();
        System.err.println("default coloring: " + defaultColoring);
        Color defFg = defaultColoring == null ? null : (Color) defaultColoring.getAttribute (StyleConstants.Foreground);
        Color defBg = defaultColoring == null ? null : (Color) defaultColoring.getAttribute (StyleConstants.Background);
        if (defBg == null) {
            defBg = Color.WHITE;
        }
        if (defFg == null) {
            defFg = Color.BLACK;
        }
        for (AttributeSet a : categories) {
            SingleColoringPanel pnl = factory.create();
            String name = (String) a.getAttribute(StyleConstants.NameAttribute);
            String desc = (String) a.getAttribute(EditorStyleConstants.DisplayName);
            if (desc == null) {
                desc = (String) a.getAttribute("description");
            }
            if (desc == null) {
                desc = (String) a.getAttribute("display name");
            }
            desc = desc == null ? name : desc;

            Color fg = (Color) a.getAttribute (StyleConstants.Foreground);
            Color bg = (Color) a.getAttribute (StyleConstants.Background);
            Boolean _bold = (Boolean) a.getAttribute (StyleConstants.Bold);
            Boolean _italic = (Boolean) a.getAttribute(StyleConstants.Italic);
            Color _strike = (Color) a.getAttribute(StyleConstants.StrikeThrough);
            Color _underline = (Color) a.getAttribute(StyleConstants.Underline);
            boolean inheritFg = !colorEquals(defFg, fg);
            boolean inheritBg = !colorEquals(defBg, bg);
            boolean bold = _bold == null ? false : _bold.booleanValue();
            boolean italic = _italic == null ? false : _italic.booleanValue();
            boolean strike = _strike == null ? false : true;
            boolean underline = _underline != null;
            boolean inheritEnabled = !"default".equals(a.getAttribute(StyleConstants.NameAttribute));
            Color fx = (Color) a.getAttribute("wave underline color");
            Color defFx = Color.RED;
            if (!inheritEnabled) {
                inheritFg = false;
                inheritBg = false;
            }
            pnl.configure(kind);
            pnl.setup(name, desc, bg, fg, fx, inheritBg, inheritFg, inheritEnabled, italic, bold, strike, underline, defBg, defFg, defFx);
            innerPanel.add(pnl);
        }
        if (preview != null) {
            split.remove(preview);
            remove(preview);
        }
        preview = getPreviewComponent();
        if (preview == null) {
            remove(split);
            add(pane, BorderLayout.CENTER);
        } else {
            remove(pane);
            split.setOrientation(JSplitPane.VERTICAL_SPLIT);
            split.setTopComponent(pane);
            split.setBottomComponent(preview);
            add(split, BorderLayout.CENTER);
            split.setDividerLocation(0.5D);
        }
        EventQueue.invokeLater (new Runnable() {
            //Sigh...
            public void run() {
                if (preview != null) {
                    preview.invalidate();
                    preview.revalidate();
                    preview.repaint();
                }
            }
        });
    }

    private Preview getPreviewComponent() {
        return kind == UIKind.SYNTAX ? colorModel.getSyntaxColoringPreviewComponent(language) : null;
    }

    private AttributeSet getDefaultColoring() {
        Collection<AttributeSet> defaults = colorModel.getCategories(currentProfile, ColorModel.ALL_LANGUAGES);
        for (AttributeSet as : defaults) {
            String name = (String) as.getAttribute(StyleConstants.NameAttribute);
            if (name != null && "default".equals(name)) { //NOI18N
                return as;  
            }
        }
        return null;
    }

    public void setCurrentProfile(String currentProfile) {
        this.currentProfile = currentProfile;
        refreshUI();
        updatePreview();
    }

    private int blinkSequence = 0;
    private RequestProcessor.Task task = new RequestProcessor("SyntaxColoringPanel").create(new Runnable() {

        public void run() {
            updatePreview();
            if (blinkSequence == 0) {
                return;
            }
            blinkSequence--;
            task.schedule(250);
        }
    });

    private void startBlinking() {
        if (preview == null) {
            return;
        }
        blinkSequence = 5;
        task.schedule(0);
    }
    
    Collection<AttributeSet> getAllLanguages() {
        return getCategories(currentProfile, ColorModel.ALL_LANGUAGES);
    }
    
    Collection<AttributeSet> getSyntaxColorings() {
        return getCategories(currentProfile, language);
    }

    void updatePreview() {
        String category = getCurrentCategoryName();
        if (preview != null && category != null) {
            Collection<AttributeSet> syntaxColorings = getSyntaxColorings ();
            Collection<AttributeSet> allLanguages = getAllLanguages ();
            if ((blinkSequence % 2) == 1) {
                if (ColorModel.ALL_LANGUAGES.equals(language))
                    allLanguages = invertCategory (allLanguages, getCurrentCategory ());
                else
                    syntaxColorings = invertCategory (syntaxColorings, getCurrentCategory ());
            }
            final Collection<AttributeSet> sc = syntaxColorings;
            final Collection<AttributeSet> al = allLanguages;
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    preview.setParameters (
                        language,
                        al,
                        Collections.<AttributeSet>emptySet(),
                        sc
                    );
                }
            });
        }
    }

    private AttributeSet getCurrentCategory() {
        SingleColoringPanel pnl = factory.getSelected();
        if (pnl == null) {
            return null;
        }
        String nm = pnl.name();
        for (AttributeSet as : getCategories(currentProfile, language)) {
            if (as.getAttribute(StyleConstants.NameAttribute).equals(nm)) {
                return as;
            }
        }
        return null;
    }

    private String getCurrentCategoryName() {
        SingleColoringPanel pnl = factory.getSelected();
        if (pnl != null) {
            return pnl.name();
        } else {
            return null;
        }
    }

    private Collection<AttributeSet> invertCategory(Collection<AttributeSet> c, AttributeSet category) {
        if (category == null) {
            return c;
        }
        List<AttributeSet> result = new ArrayList<AttributeSet>(c);
        int i = indexOf(category, result);
        if (i == -1) {
            System.err.println("Could not find " + category + " in " + c);
            return c;
        }
        SimpleAttributeSet as = new SimpleAttributeSet(category);
        Color highlight = (Color) getValue (language, category, StyleConstants.Background);
        if (highlight == null) {
            return result;
        }
        Color newColor = new Color(255 - highlight.getRed(), 255 - highlight.getGreen(), 255 - highlight.getBlue());
        as.addAttribute(StyleConstants.Underline, newColor);
        result.set(i, as);
        return result;
    }

    private int indexOf(AttributeSet category, List<AttributeSet> c) {
        int max = c.size();
        String lookFor = (String) category.getAttribute(StyleConstants.NameAttribute);
        for (int i = 0; i < max; i++) {
            AttributeSet cat = c.get(i);
            String nm = (String) cat.getAttribute(StyleConstants.NameAttribute);
            if (lookFor.equals(nm)) {
                return i;
            }
        }
        return -1;
    }

    private Object getValue(String language, AttributeSet category, Object key) {
        if (category.isDefined(key)) {
            return category.getAttribute(key);
        }
        return getDefault(language, category, key);
    }

    private Object getDefault(String language, AttributeSet category, Object key) {
        String name = (String) category.getAttribute (EditorStyleConstants.Default);
        if (name == null) {
            name = "default";
        }
        // 1) search current language
        if (!name.equals(category.getAttribute(StyleConstants.NameAttribute))) {
            AttributeSet defaultAS = getCategory(currentProfile, language, name);
            if (defaultAS != null) {
                return getValue(language, defaultAS, key);
            }
        }

        // 2) search default language
        if (!language.equals(ColorModel.ALL_LANGUAGES)) {
            AttributeSet defaultAS = getCategory(currentProfile, ColorModel.ALL_LANGUAGES, name);
            if (defaultAS != null) {
                return getValue(ColorModel.ALL_LANGUAGES, defaultAS, key);
            }
        }

        if (key == StyleConstants.FontFamily) {
            return "Monospaced"; // NOI18N
        }
        if (key == StyleConstants.FontSize) {
            return getDefaultFontSize();
        }
        return null;
    }

    private AttributeSet getCategory(String profile, String language, String name) {
        List v = getCategories(profile, language);
        Iterator it = v.iterator();
        while (it.hasNext()) {
            AttributeSet c = (AttributeSet) it.next ();
            if (c.getAttribute(StyleConstants.NameAttribute).equals(name)) {
                return c;
            }
        }
        return null;
    }

    private static Integer defaultFontSize;

    private static Integer getDefaultFontSize() {
        if (defaultFontSize == null) {
            defaultFontSize = (Integer) UIManager.get("customFontSize"); // NOI18N
            if (defaultFontSize == null) {
                int s = UIManager.getFont("TextField.font").getSize(); // NOI18N
                if (s < 12) {
                    s = 12;
                }
                defaultFontSize = new Integer(s);
            }
        }
        return defaultFontSize;
    }

    private void updateData(SingleColoringPanel changed) {
        List<AttributeSet> categories = getCategories(currentProfile);
        Component[] cc = innerPanel.getComponents();
        int ix = Arrays.asList(cc).indexOf(changed);
        AttributeSet category = categories.get(ix);
        AttributeSet c = changed.apply(category);
        System.err.println("Replacing " + category + " with " + c);
        categories.set(ix, c);
        switch (kind) {
            case ANNOTATIONS:
                colorModel.setAnnotations(currentProfile, categories);
                break;
            case HIGHLIGHTINGS:
                colorModel.setHighlightings(currentProfile, categories);
                break;
            case SYNTAX:
                colorModel.setCategories(currentProfile, language, categories);
                updatePreview();
                break;
            default:
                throw new AssertionError();
        }
    }

    private String language = ColorModel.ALL_LANGUAGES;

    public void setLanguage(String language) {
        this.language = language;
        refreshUI();
        updatePreview();
    }

    private List<AttributeSet> getCategories(String profile) {
        if (colorModel == null) {
            return null;
        }
        Collection<AttributeSet> c;
        switch (kind) {
            case HIGHLIGHTINGS:
                c = colorModel.getHighlightings(profile);
                break;
            case SYNTAX:
                c = getCategories(profile, language);
                break;
            case ANNOTATIONS:
                c = colorModel.getAnnotations(profile);
                break;
            default:
                throw new AssertionError();
        }
        List<AttributeSet> l = c == null ? new ArrayList<AttributeSet>() : new ArrayList<AttributeSet>(c);
        Collections.sort(l, new CategoryComparator());
        return l;
    }

    public boolean isChanged() {
        return changed;
    }

    public void selectionChanged(SingleColoringPanel old, SingleColoringPanel nue) {
        if (!listen) {
            return;
        }
        startBlinking();
    }

    public void changed(SingleColoringPanel pnl) {
        updateData(pnl);
    }

    public JComponent getComponent() {
        return this;
    }

    private List<AttributeSet> getCategories(String profile, String language) {
        if (colorModel == null) {
            return null;
        }
        Collection<AttributeSet> c = colorModel.getCategories(profile, language);
        if (c == null) {
            c = Collections.<AttributeSet>emptySet(); // XXX OK?
        }
        List<AttributeSet> result = new ArrayList<AttributeSet>(c);
        Collections.sort(result, new CategoryComparator());
        return result;
    }
}
