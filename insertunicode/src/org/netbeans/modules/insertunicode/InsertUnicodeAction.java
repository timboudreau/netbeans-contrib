/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.insertunicode;

import java.awt.Component;
import java.awt.Toolkit;
import org.openide.awt.Actions;
import org.openide.awt.JMenuPlus;

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
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.text.BadLocationException;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;
/*
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.WeakListener;
 */

/** @author Jesse Glick */
public class InsertUnicodeAction extends SystemAction implements Presenter.Popup /*, Presenter.Menu, Presenter.Toolbar, PropertyChangeListener */ {
    
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
    
    /*
    public JMenuItem getMenuPresenter() {
        return new MainPopup(true, true);
    }
    
    public Component getToolbarPresenter() {
        return new MainPopup(false, true);
    }
    
    protected void initialize() {
        super.initialize();
        TopComponent.getRegistry().addPropertyChangeListener(WeakListener.propertyChange(this, TopComponent.getRegistry()));
        setEnabled(getPane() != null);
    }

     public void propertyChange(PropertyChangeEvent evt) {
        if (TopComponent.Registry.PROP_CURRENT_NODES.equals(evt.getPropertyName())) {
            JEditorPane pane = getPane();
            setEnabled(pane != null);
        }
    }
     */
    
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
            Actions.setMenuText(this, text, true);
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
                Actions.setMenuText(more, NbBundle.getMessage(InsertUnicodeAction.class, "LBL_more"), true);
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
                Actions.setMenuText(this, a.getName(), true);
            }
            if (showIcon) {
                setIcon(a.getIcon());
            }
            /*
            a.addPropertyChangeListener(WeakListener.propertyChange(this, a));
            setEnabled(a.isEnabled());
             */
        }
        
        /*
        public void propertyChange(PropertyChangeEvent evt) {
            if (SystemAction.PROP_ENABLED.equals(evt.getPropertyName())) {
                setEnabled(((SystemAction)evt.getSource()).isEnabled());
            }
        }
         */
        
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
                        switch (modeChoice) {
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
    static int modeChoice = MODE_RAW;
    // XXX make the mode sensitive to content type of current pane
    // default to MODE_JAVA for text/x-java, text/x-properties
    // default to MODE_XML for text/xml, application/xml, text/*+xml
    // default to MODE_HTML for text/html
    // default to MODE_RAW for all other content types
    
    private static final class ModePopup extends JMenuPlus {
        
        public ModePopup() {
            Actions.setMenuText(this, NbBundle.getMessage(InsertUnicodeAction.class, "LBL_escape_mode"), true);
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
                Actions.setMenuText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_raw"), true);
                break;
            case MODE_JAVA:
                Actions.setMenuText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_java"), true);
                break;
            case MODE_XML:
                Actions.setMenuText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_xml"), true);
                break;
            case MODE_HTML:
                Actions.setMenuText(this, NbBundle.getMessage(InsertUnicodeAction.class, "MODE_html"), true);
                break;
            default:
                throw new IllegalArgumentException();
            }
            setSelected(mode == modeChoice);
            addActionListener(this);
        }
        
        public void actionPerformed(ActionEvent e) {
            modeChoice = mode;
        }
        
    }

}
