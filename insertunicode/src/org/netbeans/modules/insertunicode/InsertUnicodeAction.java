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

package org.netbeans.modules.insertunicode;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.text.BadLocationException;
import org.openide.awt.Mnemonics;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.actions.Presenter;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;

/** @author Jesse Glick */
public class InsertUnicodeAction extends SystemAction implements Presenter.Popup {
    
    public void actionPerformed(ActionEvent e) {
        throw new IllegalStateException();
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String getName() {
        return NbBundle.getMessage(InsertUnicodeAction.class, "LBL_insert_unicode");
    }
    
    @Override
    protected String iconResource() {
        return "org/netbeans/modules/insertunicode/unicode.gif";
    }
    
    public JMenuItem getPopupPresenter() {
        return new MainPopup(true, false);
    }
    
    private static JEditorPane getPane() {
        Node[] ns = TopComponent.getRegistry().getCurrentNodes();
        if (ns != null && ns.length == 1) {
            EditorCookie ed = ns[0].getCookie(EditorCookie.class);
            if (ed != null) {
                JEditorPane[] panes = ed.getOpenedPanes();
                if (panes != null) {
                    return panes[0];
                }
            }
        }
        return null;
    }
    
    private static String[] unicodeNames; // String[65536]
    
    private static synchronized String[] getUnicodeNames() {
        if (unicodeNames == null) {
            unicodeNames = new String[65536];
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(InsertUnicodeAction.class.getResourceAsStream("uni-names.txt"), "ISO8859_1"));
                try {
                    String l;
                    while ((l = r.readLine()) != null) {
                        unicodeNames[Integer.parseInt(l.substring(0, 4), 16)] = l.substring(5);
                    }
                } finally {
                    r.close();
                }
            } catch (IOException ioe) {
                throw new IllegalStateException(ioe.toString(), ioe);
            }
        }
        return unicodeNames;
    }
    
    private static abstract class LazyMenu extends JMenu {
        
        protected LazyMenu() {
        }
        
        protected LazyMenu(String text) {
            Mnemonics.setLocalizedText(this, text);
        }
        
        private boolean inited = false;
        
        @Override
        public JPopupMenu getPopupMenu() {
            create();
            return super.getPopupMenu();
        }
        
        private void create() {
            if (inited) {
                return;
            } else {
                inited = true;
            }
            List<JMenuItem> items = doCreate();
            fixupItems(items);
            for (JMenuItem item : items) {
                add(item);
            }
        }
        
        private static final int MAX_ITEMS = 25;
        
        private void fixupItems(List<JMenuItem> items) {
            int idx = 1;
            for (int i = 0; i < Math.min(items.size(), MAX_ITEMS); i++) {
                JMenuItem item = items.get(i);
                if (item.getMnemonic() == 0) {
                    if (idx <= 9) {
                        item.setText(String.valueOf(idx) + " " + item.getText());
                        item.setMnemonic(KeyEvent.VK_0 + idx);
                    } else if (idx <= 9 + 26) {
                        item.setText("" + new Character((char)('a' + idx - 10)) + " " + item.getText());
                        item.setMnemonic(KeyEvent.VK_A + idx - 10);
                    } else {
                        item.setText("  " + item.getText());
                    }
                    idx++;
                }
            }
            if (items.size() > MAX_ITEMS) {
                List<JMenuItem> sub = items.subList(MAX_ITEMS, items.size());
                List<JMenuItem> moreitems = new ArrayList<JMenuItem>(sub);
                sub.clear();
                fixupItems(moreitems);
                JMenuItem more = new JMenu();
                Mnemonics.setLocalizedText(more, NbBundle.getMessage(InsertUnicodeAction.class, "LBL_more"));
                for (JMenuItem item : moreitems) {
                    more.add(item);
                }
                items.add(more);
            }
        }
        
        protected abstract List<JMenuItem> doCreate();
        
    }
    
    private static final class MainPopup extends LazyMenu /* implements PropertyChangeListener */ {
        
        private static List<Character.UnicodeBlock> blocks = null;
        private static List<Integer> starts, ends = null;
        
        private static synchronized void makeBlocks() {
            if (blocks == null) {
                blocks = new ArrayList<Character.UnicodeBlock>();
                starts = new ArrayList<Integer>();
                ends = new ArrayList<Integer>();
                Character.UnicodeBlock curr = null;
                for (int i = 0; i < 0x10000; i++) {
                    char c = (char)i;
                    Character.UnicodeBlock block = Character.isISOControl(c) ? null : Character.UnicodeBlock.of(c);
                    if (block != curr) {
                        if (curr != null) {
                            blocks.add(curr);
                            ends.add((int) c);
                        }
                        if (block != null) {
                            starts.add((int) c);
                        }
                        curr = block;
                    }
                }
            }
        }
        
        public MainPopup(boolean showText, boolean showIcon) {
            SystemAction a = SystemAction.get(InsertUnicodeAction.class);
            if (showText) {
                Mnemonics.setLocalizedText(this, a.getName());
            }
            if (showIcon) {
                setIcon(a.getIcon());
            }
        }
        
        protected List<JMenuItem> doCreate() {
            makeBlocks();
            List<JMenuItem> ms = new ArrayList<JMenuItem>(blocks.size() + 1);
            ms.add(new ModePopup());
            for (int i = 0; i < blocks.size(); i++) {
                Character.UnicodeBlock block = blocks.get(i);
                ms.add(new BlockPopup(block, starts.get(i), ends.get(i)));
            }
            return ms;
        }
        
    }
    
    private static final class BlockPopup extends LazyMenu {
        
        private final Character.UnicodeBlock block;
        private final int start, end;
        
        public BlockPopup(Character.UnicodeBlock block, int start, int end) {
            try {
                setText(NbBundle.getMessage(InsertUnicodeAction.class, "BLOCK_" + block));
            } catch (MissingResourceException mre) {
                // New class. Fine, just show the code name.
                setText(block.toString());
            }
            this.block = block;
            this.start = start;
            this.end = end;
        }
        
        protected List<JMenuItem> doCreate() {
            List<JMenuItem> l = new ArrayList<JMenuItem>(end - start);
            for (int c = start; c < end; c++) {
                final char _c = (char)c;
                if (!Character.isDefined(_c)) continue;
                String text;
                String name = getUnicodeNames()[c];
                if (name == null) {
                    name = NbBundle.getMessage(InsertUnicodeAction.class, "LBL_unknown_char");
                }
                if (Character.isWhitespace(_c)) {
                    text = NbBundle.getMessage(InsertUnicodeAction.class, "LBL_ws_char", hex(c), name);
                } else {
                    text = NbBundle.getMessage(InsertUnicodeAction.class, "LBL_char", Character.toString(_c), hex(c), name);
                }
                JMenuItem m = new JMenuItem(text);
                m.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        JEditorPane pane = getPane();
                        if (pane == null) {
                            Toolkit.getDefaultToolkit().beep();
                            return;
                        }
                        String toInsert;
                        switch (getModeChoice()) {
                        case RAW:
                            toInsert = new String(new char[] {_c});
                            break;
                        case JAVA:
                            toInsert = "\\u" + hex((int)_c);
                            break;
                        case XML:
                            toInsert = "&#x" + hex((int)_c) + ";";
                            break;
                        case HTML:
                            toInsert = "&#" + ((int)_c) + ";";
                            break;
                        default:
                            throw new IllegalStateException();
                        }
                        try {
                            pane.getDocument().insertString(pane.getCaretPosition(), toInsert, null);
                        } catch (BadLocationException ble) {
                            Exceptions.printStackTrace(ble);
                        }
                    }
                });
                l.add(m);
            }
            return l;
        }
        
        private static String hex(int i) {
            String s = Integer.toHexString(i).toUpperCase(Locale.US);
            while (s.length() < 4) s = "0" + s;
            return s;
        }
        
    }

    enum Mode {RAW, JAVA, XML, HTML}
    private static final String KEY_MODE = "unicode.insert.mode"; // NOI18N
    static Mode getModeChoice() {
        return Mode.values()[NbPreferences.forModule(InsertUnicodeAction.class).getInt(KEY_MODE, Mode.RAW.ordinal())];
    }
    static void setModeChoice(Mode mode) {
        NbPreferences.forModule(InsertUnicodeAction.class).putInt(KEY_MODE, mode.ordinal());
    }
    // XXX make the mode sensitive to content type of current pane
    // default to MODE_JAVA for text/x-java, text/x-properties
    // default to MODE_XML for text/xml, application/xml, text/*+xml
    // default to MODE_HTML for text/html
    // default to MODE_RAW for all other content types
    
    private static final class ModePopup extends JMenu {
        
        public ModePopup() {
            Mnemonics.setLocalizedText(this, NbBundle.getMessage(InsertUnicodeAction.class, "LBL_escape_mode"));
            add(new ModeChoice(Mode.RAW));
            add(new ModeChoice(Mode.JAVA));
            add(new ModeChoice(Mode.XML));
            add(new ModeChoice(Mode.HTML));
        }
        
    }
    
    private static final class ModeChoice extends JRadioButtonMenuItem implements ActionListener {
        
        private final Mode mode;
        
        public ModeChoice(Mode mode) {
            this.mode = mode;
            switch (mode) {
            case RAW:
                Mnemonics.setLocalizedText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_raw"));
                break;
            case JAVA:
                Mnemonics.setLocalizedText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_java"));
                break;
            case XML:
                Mnemonics.setLocalizedText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_xml"));
                break;
            case HTML:
                Mnemonics.setLocalizedText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_html"));
                break;
            default:
                throw new IllegalArgumentException();
            }
            setSelected(mode == getModeChoice());
            addActionListener(this);
        }
        
        public void actionPerformed(ActionEvent e) {
            setModeChoice(mode);
        }
        
    }

}
