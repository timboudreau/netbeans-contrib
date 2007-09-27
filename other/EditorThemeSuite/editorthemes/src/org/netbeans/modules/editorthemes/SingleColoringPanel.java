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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import net.java.dev.colorchooser.ColorChooser;
import net.java.dev.colorchooser.Palette;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
final class SingleColoringPanel extends JPanel implements ActionListener, MouseListener {
    private final ColorChooser bg = new ColorChooser();
    private final ColorChooser fg = new ColorChooser();
    private final ColorChooser fx = new ColorChooser();
    private final JLabel lbl = new SL();
    private final JCheckBox useFg = new JCheckBox ();
    private final JCheckBox useBg = new JCheckBox ();
    private final JCheckBox useFx = new JCheckBox("Wave Underline");
    private final JCheckBox bold = new JCheckBox ("Bold");
    private final JCheckBox italic = new JCheckBox ("Italic");
    private final SingleColoringPanel.Factory factory;
    private final JCheckBox strike = new JCheckBox ("Strikethrough");
    private final JCheckBox underline = new JCheckBox ("Underline");
    //Use panels to keep checkbox and color swatch grouped together on
    //the same row
    private final JPanel fgPnl = new JPanel();
    private final JPanel bgPnl = new JPanel();
    private final JPanel fxPnl = new JPanel();
    private SingleColoringPanel(SingleColoringPanel.Factory factory) {
        this.factory = factory;
        setBorder (BorderFactory.createEmptyBorder(5,5,5,5));
        setOpaque(true);
        lbl.setMinimumSize (new Dimension(150, 24));
        setLayout (new ScrollpaneSensitiveFlowLayout());
        fgPnl.setLayout (new BoxLayout(fgPnl, BoxLayout.LINE_AXIS));
        bgPnl.setLayout (new BoxLayout(bgPnl, BoxLayout.LINE_AXIS));
        fxPnl.setLayout (new BoxLayout(fxPnl, BoxLayout.LINE_AXIS));
        add (lbl);
        fgPnl.add (useFg);
        fgPnl.add (fg);
        add (fgPnl);
        bgPnl.add (useBg);
        bgPnl.add (bg);
        add (bgPnl);
        fxPnl.add (useFx);
        fxPnl.add (fx);
        add (fxPnl);

        swapPalettes (fg, bg, fx);

        add (bold);
        add (italic);
        add (strike);
        add (underline);
        bg.addActionListener(this);
        fg.addActionListener(this);
        fx.addActionListener(this);
        useFg.addActionListener(this);
        useBg.addActionListener(this);
        bold.addActionListener(this);
        italic.addActionListener(this);
        strike.addActionListener(this);
        underline.addActionListener(this);
        useFx.addActionListener(this);
        addMouseListener(this);
        bg.addMouseListener(this);
        fg.addMouseListener(this);
        fx.addMouseListener(this);
        useFg.addMouseListener(this);
        useBg.addMouseListener(this);
        useFx.addMouseListener(this);
        bold.addMouseListener(this);
        italic.addMouseListener(this);
        strike.addMouseListener(this);
        underline.addMouseListener(this);
        fg.setToolTipText(str("TIP_FOREGROUND"));
        bg.setToolTipText(str("TIP_BACKGROUND"));
        fx.setToolTipText(str("TIP_WAVE_UNDERLINE"));
        useFg.setToolTipText(str("TIP_ENABLE_FOREGROUND"));
        useBg.setToolTipText(str("TIP_ENABLE_BACKGROUND"));
        useFx.setToolTipText(str("TIP_USE_WAVE_UNDERLINE"));
        Font f = lbl.getFont() == null ? UIManager.getFont("controlFont") :
            lbl.getFont();
        f = f.deriveFont(Font.BOLD);
        lbl.setFont(f);
    }
    Color origBackground;

    @Override
    public void updateUI() {
        super.updateUI();
        origBackground = UIManager.getColor("control");
    }
    
    private static Palette[] palettes;
    static {
        palettes = Palette.getDefaultPalettes(true);
        Dimension d = new Dimension (256, 172);
        palettes[0] = Palette.createContinuousPalette(ColorsCustomizer.loc(null, "Palette1"), d, 0.25f);
        palettes[1] = Palette.createContinuousPalette(ColorsCustomizer.loc(null, "Palette2"), d, 0.5f);
        palettes[2] = Palette.createContinuousPalette(ColorsCustomizer.loc(null, "Palette3"), d, 0.75f);
        palettes[3] = Palette.createContinuousPalette(ColorsCustomizer.loc(null, "Palette4"), d, 1.0f);
    }

    private void swapPalettes (ColorChooser... c) {
        for (int i=0; i < c.length; i++) {
            c[i].setPalettes(palettes);
            c[i].setFocusable(true);
        }
    }

    public void configure (UIKind kind) {
        configure (kind.controls());
    }

    public void configure (Set <Controls> ctrls) {
        fgPnl.setVisible(ctrls.contains(Controls.FOREGROUND));
        bgPnl.setVisible(ctrls.contains(Controls.BACKGROUND));
        italic.setVisible(ctrls.contains(Controls.STRIKETHROUGH));
        bold.setVisible(ctrls.contains(Controls.BOLD));
        strike.setVisible(ctrls.contains(Controls.STRIKETHROUGH));
        underline.setVisible(ctrls.contains(Controls.UNDERLINE));
        fxPnl.setVisible(ctrls.contains(Controls.WAVE_UNDERLINE));
    }

    @Override
    public void removeNotify() {
        factory.returnToPool(this);
        super.removeNotify();
    }

    Color savedFg;
    Color savedBg;
    Color savedFx;
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == bg) {
            savedBg = bg.getColor();
            if (useBg.isEnabled()) {
                if (!useBg.isSelected()) {
                    quiet = true;
                    useBg.setSelected(true);
                    quiet = false;
                } else {
                    if (bg.getColor().equals(defaultBg)) {
                        quiet = true;
                        useBg.setSelected(false);
                        quiet = false;
                    }
                }
            }
        } else if (o == fg) {
            savedFg = fg.getColor();
            if (useFg.isEnabled()) {
                if (!useFg.isSelected()) {
                    quiet = true;
                    useFg.setSelected(true);
                    quiet = false;
                } else {
                    if (fg.getColor().equals(defaultFg)) {
                        quiet = true;
                        useFg.setSelected(false);
                        quiet = false;
                    }
                }
            }
        } else if (o == fx) {
            savedFx = fx.getColor();
            if (useFx.isEnabled()) {
                if (!useFx.isSelected()) {
                    quiet = true;
                    useFx.setSelected(true);
                    quiet = false;
                } else {
                    if (fx.getColor().equals(defaultFx)) {
                        quiet = true;
                        useFx.setSelected(false);
                        quiet = false;
                    }
                }
            }
        } else if (o == useFg) {
            restoreForegroundColor();
        } else if (o == useBg) {
            restoreBackgroundColor();
        } else if (o == useFx) {
            restoreFxColor();
        }
        change();
    }

    private boolean quiet;
    private void change() {
        if (quiet) return;
        factory.changed(this);
    }

    Color getFg() {
        return !useFg.isSelected() ? null : fg.getColor();
    }

    Color getBg() {
        return !useBg.isSelected() ? null : bg.getColor();
    }

    boolean isInheritForeground() {
        return !useFg.isSelected();
    }

    boolean isInheritBackground() {
        return !useBg.isSelected();
    }

    boolean isItalic() {
        return italic.isSelected();
    }

    boolean isBold() {
        return bold.isSelected();
    }

    boolean isStrike() {
        return strike.isSelected();
    }

    void setInheritEnabled(boolean val) {
        useBg.setEnabled(val);
        useFg.setEnabled(val);
    }

    private Color getNonNullFg() {
        return getFg() == null ? defaultFg : getFg();
    }

    private Color getNonNullBg() {
        return getBg() == null ? defaultBg : getBg();
    }

    AttributeSet apply (AttributeSet set) {
        SimpleAttributeSet c = new SimpleAttributeSet(set);
        System.err.println("Apply on " + name());
        if (bg.isVisible()) {
            Color color = getBg();
            if (color != null) {
                c.addAttribute(StyleConstants.Background, color);
            } else {
                System.err.println("remove background");
                c.removeAttribute(StyleConstants.Background);
            }
        }
        if (fg.isVisible()) {
            Color color = getFg();
            if (color != null) {
                c.addAttribute(StyleConstants.Foreground, color);
            } else {
                System.err.println("remove foreground");
                c.removeAttribute(StyleConstants.Foreground);
            }
        }
        if (strike.isVisible()) {
            if (strike.isSelected()) {
                c.addAttribute(StyleConstants.StrikeThrough, getNonNullFg());
            } else {
                System.err.println("remove strikethrough");
                c.removeAttribute(StyleConstants.StrikeThrough);
            }
        }
        if (bold.isVisible()) {
            if (bold.isSelected()) {
                c.addAttribute(StyleConstants.Bold, Boolean.TRUE);
            } else {
                c.removeAttribute(StyleConstants.Bold);
                System.err.println("remove bold");
            }
        }
        if (italic.isVisible()) {
            if (italic.isSelected()) {
                c.addAttribute(StyleConstants.Italic, Boolean.TRUE);
            } else {
                c.removeAttribute(StyleConstants.Italic);
                System.err.println("remove italic");
            }
        }
        if (underline.isVisible()) {
            if (underline.isSelected()) {
                c.addAttribute (StyleConstants.Underline, getNonNullFg());
            } else {
                c.removeAttribute (StyleConstants.Underline);
                System.err.println("remove underline");
            }
        }
        if (useFx.isVisible()) {
            if (useFx.isSelected()) {
                c.addAttribute (EditorStyleConstants.WaveUnderlineColor,
                        fx.getColor());
            } else {
                c.removeAttribute (EditorStyleConstants.WaveUnderlineColor);
            }
        }
        return c;
    }

    private void restoreBackgroundColor() {
        quiet = true;
        if (!useBg.isSelected()) {
            bg.setColor (defaultBg);
        } else if (savedBg != null) {
            bg.setColor (savedBg);
        }
        quiet = false;
    }

    private void restoreForegroundColor() {
        quiet = true;
        if (!useFg.isSelected()) {
            fg.setColor (defaultFg);
        } else if (savedFg != null) {
            fg.setColor (savedFg);
        }
        quiet = false;
    }

    private void restoreFxColor() {
        quiet = true;
        if (!useFx.isSelected()) {
            bg.setColor (defaultFx);
        } else if (savedFx != null) {
            bg.setColor (savedFx);
        }
        quiet = false;
    }

    private Color defaultBg;
    private Color defaultFg;
    private Color defaultFx;
    private String category;
    void setup (String name, String desc, Color bgColor, Color fgColor, Color fxColor,
            boolean inheritFg, boolean inheritBg, boolean inheritEnabled,
            boolean italic, boolean bold, boolean strike, boolean underline,
            Color defaultBg, Color defaultFg, Color defaultFx) {
        quiet = true;
        assert defaultBg != null;
        assert defaultFg != null;
        assert defaultFx != null;
        try {
            setInheritEnabled(inheritEnabled);
            this.category = name;
            savedFg = fgColor;
            savedBg = bgColor;
            savedFx = fxColor;
            fg.setColor (fgColor == null ? defaultFg : fgColor);
            bg.setColor (bgColor == null ? defaultBg : bgColor);
            fx.setColor (fxColor == null ? defaultFx : fxColor);
            useFg.setSelected(!inheritFg);
            useBg.setSelected(!inheritBg);
            useFx.setSelected(fxColor != null);
            lbl.setText(desc);
            this.defaultFg = defaultFg;
            this.defaultBg = defaultBg;
            this.defaultFx = defaultFx;
            this.bold.setSelected(bold);
            this.strike.setSelected(strike);
            this.italic.setSelected(italic);
            this.underline.setSelected(underline);
        } finally {
            quiet = false;
        }
    }

    static String str(String key) {
        return NbBundle.getMessage (SingleColoringPanel.class,
                key);
    }

    String name() {
        return category;
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
        setSelected (true);
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    void setSelected(boolean val) {
        Color c;
        if (val) {
            factory.setSelected(this);
            c = UIManager.getColor ("Tree.selectionBackground");
        } else {
            c = origBackground;
        }
        updateColors (c, this);
    }

    private void updateColors (Color color, Component c) {
        c.setBackground (color);
        if (c instanceof Container) {
            Component[] cc = ((Container) c).getComponents();
            for (int i=0; i < cc.length; i++) {
                updateColors (color, cc[i]);
            }
        }
    }

    boolean selected;
    public boolean isSelected() {
        //can be called from superclass constructor via
        //getBackground(), so we need a null check
        return factory == null ? false : factory.isSelected(this);
    }

    static class Factory {
        private Reference <SingleColoringPanel> selected;

        private Set <Reference<SingleColoringPanel>> unused =
                new HashSet <Reference<SingleColoringPanel>> ();

        private SingleColoringPanel.Factory.Controller controller;
        Factory (Controller controller) {
            this.controller = controller;
        }

        public SingleColoringPanel create() {
//            Iterator <Reference<SingleColoringPanel>> i =
//                    unused.iterator();
            SingleColoringPanel result = null;
//            while (i.hasNext()) {
//                Reference <SingleColoringPanel> r = i.next();
//                result = r.get();
//                if (result != null) {
//                    i.remove();
//                    break;
//                }
//            }
            if (result == null) {
                result = new SingleColoringPanel(this);
            }
            return result;
        }

        public SingleColoringPanel getSelected() {
            SingleColoringPanel result = selected == null ? null : selected.get();
            if (result != null && !result.isDisplayable()) {
                result = null;
            }
            return result;
        }

        public boolean isSelected (SingleColoringPanel pnl) {
            return pnl == getSelected();
        }

        public void setSelected (SingleColoringPanel pnl) {
            SingleColoringPanel old = getSelected();
            if (old != pnl) {
                if (old != null) {
                    old.setSelected(false);
                }
                selected = pnl == null ? null : new WeakReference <SingleColoringPanel> (pnl);
                controller.selectionChanged (old, pnl);
                if (old != null) {
                    old.repaint();
                }
                if (pnl != null) {
                    pnl.repaint();
                }
            }
        }

        void returnToPool (SingleColoringPanel pnl) {
            unused.add (new SoftReference<SingleColoringPanel> (pnl));
            if (selected != null && selected.get() == pnl) {
                setSelected (null);
            }
        }

        void changed (SingleColoringPanel pnl) {
            if (pnl.isDisplayable()) {
                controller.changed (pnl);
            }
        }

        static interface Controller {
            public void selectionChanged (SingleColoringPanel old, SingleColoringPanel nue);
            public void changed (SingleColoringPanel pnl);
        }
    }

    private static final class SL extends JLabel {
        SL (String s) {
            super (s);
        }
        SL() {
            super();
        }
        @Override
        public Dimension getPreferredSize() {
            Dimension result = super.getPreferredSize();
            result.width = Math.max (150, result.width);
            return result;
        }
    }
}
