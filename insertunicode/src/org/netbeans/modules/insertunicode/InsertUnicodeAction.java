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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.prefs.Preferences;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.text.BadLocationException;
import org.openide.ErrorManager;
import org.openide.awt.JMenuPlus;
import org.openide.awt.Mnemonics;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
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
    
    protected String iconResource() {
        return "org/netbeans/modules/insertunicode/unicode.gif";
    }
    
    public JMenuItem getPopupPresenter() {
        return new MainPopup(true, false);
    }
    
    private static JEditorPane getPane() {
        Node[] ns = TopComponent.getRegistry().getCurrentNodes();
        if (ns != null && ns.length == 1) {
            EditorCookie ed = (EditorCookie)ns[0].getCookie(EditorCookie.class);
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
                throw new IllegalStateException(ioe.toString());
            }
        }
        return unicodeNames;
    }
    
    private static abstract class LazyMenu extends JMenuPlus {
        
        protected LazyMenu() {
        }
        
        protected LazyMenu(String text) {
            Mnemonics.setLocalizedText(this, text);
        }
        
        private boolean inited = false;
        
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
            List items = doCreate();
            fixupItems(items);
            Iterator it = items.iterator();
            while (it.hasNext()) {
                add((JMenuItem)it.next());
            }
        }
        
        private static final int MAX_ITEMS = 25;
        
        private void fixupItems(List items) {
            int idx = 1;
            for (int i = 0; i < Math.min(items.size(), MAX_ITEMS); i++) {
                JMenuItem item = (JMenuItem)items.get(i);
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
                List sub = items.subList(MAX_ITEMS, items.size());
                List moreitems = new ArrayList(sub);
                sub.clear();
                fixupItems(moreitems);
                JMenuItem more = new JMenu();
                Mnemonics.setLocalizedText(more, NbBundle.getMessage(InsertUnicodeAction.class, "LBL_more"));
                Iterator it = moreitems.iterator();
                while (it.hasNext()) {
                    more.add((JMenuItem)it.next());
                }
                items.add(more);
            }
        }
        
        protected abstract List doCreate(); // List<JMenuItem>
        
    }
    
    private static final class MainPopup extends LazyMenu /* implements PropertyChangeListener */ {
        
        private static List blocks = null; // List<Character.UnicodeBlock>
        private static List starts, ends = null; // List<int>
        
        private static synchronized void makeBlocks() {
            if (blocks == null) {
                blocks = new ArrayList();
                starts = new ArrayList();
                ends = new ArrayList();
                Character.UnicodeBlock curr = null;
                for (int i = 0; i < 0x10000; i++) {
                    char c = (char)i;
                    Character.UnicodeBlock block = Character.isISOControl(c) ? null : Character.UnicodeBlock.of(c);
                    if (block != curr) {
                        if (curr != null) {
                            blocks.add(curr);
                            ends.add(new Integer(c));
                        }
                        if (block != null) {
                            starts.add(new Integer(c));
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
        
        protected List doCreate() {
            makeBlocks();
            ArrayList ms = new ArrayList(blocks.size() + 1);
            ms.add(new ModePopup());
            for (int i = 0; i < blocks.size(); i++) {
                Character.UnicodeBlock block = (Character.UnicodeBlock)blocks.get(i);
                ms.add(new BlockPopup(block, ((Integer)starts.get(i)).intValue(), ((Integer)ends.get(i)).intValue()));
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
        
        protected List doCreate() {
            ArrayList l = new ArrayList(end - start);
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
                        case MODE_RAW:
                            toInsert = new String(new char[] {_c});
                            break;
                        case MODE_JAVA:
                            toInsert = "\\u" + hex((int)_c);
                            break;
                        case MODE_XML:
                            toInsert = "&#x" + hex((int)_c) + ";";
                            break;
                        case MODE_HTML:
                            toInsert = "&#" + ((int)_c) + ";";
                            break;
                        default:
                            throw new IllegalStateException();
                        }
                        try {
                            pane.getDocument().insertString(pane.getCaretPosition(), toInsert, null);
                        } catch (BadLocationException ble) {
                            ErrorManager.getDefault().notify(ble);
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

    private static final int MODE_RAW = 0, MODE_JAVA = 1, MODE_XML = 2, MODE_HTML = 3;
    private static final String KEY_MODE = "unicode.insert.mode"; // NOI18N
    static int getModeChoice() {
        return NbPreferences.forModule(InsertUnicodeAction.class).getInt(KEY_MODE, MODE_RAW);
    }
    static void setModeChoice(int mode) {
        NbPreferences.forModule(InsertUnicodeAction.class).putInt(KEY_MODE, mode);
    }
    // XXX make the mode sensitive to content type of current pane
    // default to MODE_JAVA for text/x-java, text/x-properties
    // default to MODE_XML for text/xml, application/xml, text/*+xml
    // default to MODE_HTML for text/html
    // default to MODE_RAW for all other content types
    
    private static final class ModePopup extends JMenuPlus {
        
        public ModePopup() {
            Mnemonics.setLocalizedText(this, NbBundle.getMessage(InsertUnicodeAction.class, "LBL_escape_mode"));
            add(new ModeChoice(MODE_RAW));
            add(new ModeChoice(MODE_JAVA));
            add(new ModeChoice(MODE_XML));
            add(new ModeChoice(MODE_HTML));
        }
        
    }
    
    private static final class ModeChoice extends JRadioButtonMenuItem implements ActionListener {
        
        private final int mode;
        
        public ModeChoice(int mode) {
            this.mode = mode;
            switch (mode) {
            case MODE_RAW:
                Mnemonics.setLocalizedText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_raw"));
                break;
            case MODE_JAVA:
                Mnemonics.setLocalizedText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_java"));
                break;
            case MODE_XML:
                Mnemonics.setLocalizedText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_xml"));
                break;
            case MODE_HTML:
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
