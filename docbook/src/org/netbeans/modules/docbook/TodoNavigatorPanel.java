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

package org.netbeans.modules.docbook;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.ErrorManager;
import org.openide.awt.MouseUtils;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 * Displays an outline of an XML document.
 * @author Tim Boudreau
 */
public final class TodoNavigatorPanel extends FileChangeAdapter implements NavigatorPanel {

    private Lookup.Result selection;
    private final LookupListener selectionListener = new LookupListener() {
        public void resultChanged(LookupEvent ev) {
            Lookup.Result selection = ev == null ? null : (Lookup.Result) ev.getSource();
            if (selection == TodoNavigatorPanel.this.selection) {
                display(selection.allInstances());
            }
        }
    };
    private JComponent panel;
    private final DefaultListModel/*<Item>*/ listModel = new DefaultListModel();
    private ListSelectionModel/*<Item>*/ listSelectionModel;

    /**
     * Default constructor for layer.
     */
    public TodoNavigatorPanel() {}

    public String getDisplayName() {
        return "Documentation To-Dos"; // XXX I18N
    }

    public String getDisplayHint() {
        return "Displays notes and to-do items from DocBook/SolBook sources"; // XXX I18N
    }

    public JComponent getComponent() {
        if (panel == null) {
            listSelectionModel = new DefaultListSelectionModel();
            final JList/*<Item>*/ view = new JList(listModel);
            view.setSelectionModel(listSelectionModel);
            view.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            view.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (MouseUtils.isDoubleClick(e)) {
                        int index = view.locationToIndex(e.getPoint());
                        open(index);
                    }
                }
            });
            view.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "open"); // NOI18N
            view.getActionMap().put("open", new AbstractAction() { // NOI18N
                public void actionPerformed(ActionEvent e) {
                    open(listSelectionModel.getLeadSelectionIndex());
                }
            });
            view.setCellRenderer(new ItemCellRenderer());
            panel = new JScrollPane(view) {
                public boolean requestFocusInWindow() {
                    boolean b = view.requestFocusInWindow();
                    if (!listModel.isEmpty() && listSelectionModel.isSelectionEmpty()) {
                        listSelectionModel.setSelectionInterval(0, 0);
                    }
                    return b;
                }
            };
            ((JScrollPane)panel).setBorder (BorderFactory.createEmptyBorder());
            ((JScrollPane)panel).setViewportBorder (panel.getBorder());
        }
        return panel;
    }

    public void panelActivated(Lookup context) {
        selection = context.lookup(new Lookup.Template(DataObject.class));
        selection.addLookupListener(selectionListener);
        selectionListener.resultChanged(null);
        // XXX should also listen to changes in active Document and reparse after a short delay...
        // workaround: just switch tabs and back
    }

    public void panelDeactivated() {
        selection.removeLookupListener(selectionListener);
        setListeningToFileObject (null);
        selection = null;
    }

    public Lookup getLookup() {
        return null;
    }

    private FileObject listeningTo;
    private void setListeningToFileObject (FileObject ob) {
        if (listeningTo != null && !listeningTo.equals(ob)) {
            listeningTo.removeFileChangeListener (this);
        }
        if (ob != null) {
            listeningTo = ob;
            listeningTo.addFileChangeListener (this);
        }
    }

    private void display(Collection/*<DataObject>*/ selectedFiles) {
        if (selection == null && listeningTo != null) {
            //XXX it appears that panelActivated and panelDeactivated may be
            //called asymetrically
            listeningTo.removeFileChangeListener (this);
        }
        // Show list of targets for selected file:
        if (selectedFiles.size() == 1) {
            final DataObject d = (DataObject) selectedFiles.iterator().next();
            setListeningToFileObject (d.getPrimaryFile());

            // Parse asynch, since it can take a second or two for a big file.
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    try {
                        final Item[] items = parse(d);
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                listModel.clear();
                                for (int i = 0; i < items.length; i++) {
                                    listModel.addElement(items[i]);
                                }
                            }
                        });
                    } catch (Exception e) { // IOException, SAXParseException
                        ErrorManager.getDefault().log(ErrorManager.WARNING,
                            "Could not parse " + FileUtil.getFileDisplayName(d.getPrimaryFile()) + ": " + e.toString());
                    }
                }
            });
        } else {
            listModel.clear();
        }
    }

    private void open(int listIndex) {
        if (listIndex < 0 || listIndex >= listModel.size()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        Item item = (Item) listModel.get(listIndex);
        item.open();
    }

    //Defines the syntax that is recognized by the Navigator Notes 
    //panel of the XML editor
    static final Pattern TODO_PATTERN =
        Pattern.compile (
        "\\[content\\]|" +
        "\\[more content\\]|" +
        "\\[title\\]|" +
        "\\[todo.*?[\\s*|\\w*]\\s*(.*\\n|.*?)\\]|" +
        "XXX\\s*(.*?)\\s*\\n|" +
        "<note>\\s*(.*)\\s*</note>", Pattern.CASE_INSENSITIVE);

/*
\[content\]|\[more content\]|\[title\]|\[todo.*?[\s*|\w*]\s*(.*\n|.*?)\]|XXX\s*(.*?)\s*\n|<note>\s*(.*)\s*</note>
 */

    //parses the document for the TODO_PATTERNs.
    //@returns the nearest element to the matched pattern (for some patterns, not all)
    static Item[] parse(final DataObject d) throws IOException {
        EditorCookie ck = (EditorCookie) d.getCookie(EditorCookie.class);
        CharSequence content = null;
        if (ck != null) {
            StyledDocument doc = ck.openDocument();
            try {
                content = doc.getText(0, doc.getLength());
            } catch (BadLocationException ble) {
               //urph
            }
        }
        if (content == null) {
            File f = FileUtil.toFile (d.getPrimaryFile());
            if (f == null || f.length() > Integer.MAX_VALUE) {
                return new Item[0];
            }
            FileChannel channel = new FileInputStream (f).getChannel();
            ByteBuffer buf = ByteBuffer.allocate ((int)f.length());
            try {
                content = Charset.forName("UTF-8").decode(buf);
            } catch (Exception e) {
                content = buf.asCharBuffer();
            }
        }
        Matcher titleMatcher = TITLE_PATTERN.matcher(content);
        IntMap map = new IntMap ();
        while (titleMatcher.find()) {
            map.put(titleMatcher.start(), titleMatcher.group(1));
        }

        Matcher m = TODO_PATTERN.matcher(content);
        List items = new ArrayList(20);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            String found = m.group(0);
            int type = 0;
            if (m.group(1) != null) {
                found = m.group(1);
                type = 1;
            }
            if (m.group(2) != null) {
                found = m.group(2);
                type = 2;
            }
            if (m.group(3) != null) {
                found = m.group(3);
                type = 3;
            }
            if (found.toLowerCase().startsWith("[content")) {
                found = "Missing content";
                type = 4;
            } else if (found.toLowerCase().startsWith("[more content")) {
                found = "Missing body content";
                type = 5;
            } else if (found.toLowerCase().startsWith("[title")) {
                found = "Missing title";
                type = 6;
            }
            int ttlLoc = map.nearest(end, true);
            String nearestTitle = null;
            if (ttlLoc != -1) {
                nearestTitle = (String) map.get(ttlLoc);
            }
            Item item = new Item (found, start, end, d, type, nearestTitle);
            items.add (item);
        }
        Item[] result = (Item[]) items.toArray (new Item[items.size()]);
        return result;
    }

    private static final Pattern TITLE_PATTERN =
            Pattern.compile ("<title>\\s*(.*?)\\s*</title>", Pattern.CASE_INSENSITIVE |
                Pattern.DOTALL);

    public void fileFolderCreated(FileEvent fe) {
    }

    public void fileDataCreated(FileEvent fe) {
    }

    public void fileChanged(FileEvent fe) {
        selectionListener.resultChanged (null);
    }

    public void fileDeleted(FileEvent fe) {
        setListeningToFileObject (null);
        listModel.clear();
    }

    public void fileRenamed(FileRenameEvent fe) {
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
    }

    static final class Item implements PropertyChangeListener, ActionListener {
        private final String label;
        private final int start;
        private final int end;
        private final DataObject d;
        private final int type;
        private final String nearestTitle;
        public Item(String label, int start, int end, DataObject d, int type, String nearestTitle) {
            this.label = label;
            this.start = start;
            this.end = end;
            this.d = d;
            this.type = type;
            this.nearestTitle = nearestTitle;
        }

        public String getLabel() {
            return label;
        }

        int getOffset() {
            return start;
        }

        int getType() {
            return type;
        }

        String getNearestTitle() {
            return nearestTitle;
        }

        int getEnd() {
            return end;
        }

        public void open() {
            EditorCookie cookie = (EditorCookie) d.getCookie(EditorCookie.class);
            if (cookie == null) {
                OpenCookie oc = (OpenCookie) d.getCookie(OpenCookie.class);
                EditCookie ec = (EditCookie) d.getCookie(EditCookie.class);
                if (oc != null || ec != null) {
                    d.addPropertyChangeListener (this);
                    if (oc != null) oc.open(); else ec.edit();
                    if (!done) {
                        timer = new javax.swing.Timer(1000, this);
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
            } else {
                handleCookie (cookie);
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (!done && DataObject.PROP_COOKIE.equals(evt.getPropertyName())) {
                EditorCookie cookie = (EditorCookie) d.getCookie(EditorCookie.class);
                if (cookie != null) {
                    d.removePropertyChangeListener(this);
                    handleCookie(cookie);
                }
            }
        }

        private javax.swing.Timer timer;
        public void actionPerformed (ActionEvent ae) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
                timer = null;
            }
            d.removePropertyChangeListener (this);
            if (done) return;
            //one last try
            EditorCookie ck = (EditorCookie) d.getCookie(EditorCookie.class);
            if (ck != null) handleCookie (ck);
        }

        private volatile boolean done;
        private void handleCookie(EditorCookie ck) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
                timer = null;
            }
            done = true;
            ck.open();
            try {
                StyledDocument d = ck.openDocument();
                JEditorPane[] panes = ck.getOpenedPanes();
                //XXX compensate for line end differences - if text is from
                //data object it will be crlf but if from document it will be
                //lf.  Ugh.
                if (panes.length > 0) {
                    TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass(
                            TopComponent.class, panes[0]);
                    if (tc != null) {
                        tc.open();
                        tc.requestActive();
                    }
                    if (start < d.getLength()) {
                        panes[0].setSelectionStart (start);
                            Rectangle r = panes[0].modelToView(start);
                            if (r != null) {
                                panes[0].scrollRectToVisible(r);
                            }
                    }
                    if (end < d.getLength()) {
                        panes[0].setSelectionEnd (end);
                    }
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify (ErrorManager.INFORMATIONAL,
                        ioe);
            } catch (BadLocationException ble) {
                //can happen - document may have changed before we
                //ran
            }
        }
    }

    private static final class ItemCellRenderer extends DefaultListCellRenderer {

        public void propertyChange (String prop, Object old, Object nue) {
            //do nothing - performance
        }

        /* Determine which color to render the corresponding to-do pattern
         * in the Navigator Notes window.
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Item item = (Item) value;
            Color c;
            switch (item.getType()) {
                //XXX silly
                case 0 :
                    c = Color.BLUE;
                    break;
                case 1 :
                    c = Color.GREEN;
                    break;
                case 2 :
                    c = Color.RED;
                    break;
                case 3 :
                    c = Color.CYAN;
                    break;
                case 4 :
                    c = Color.RED;
                    break;
                case 5 :
                    c = new Color (85, 128, 23);
                    break;
                default :
                    c = list.getForeground();
            }

            // XXX could also display element and/or header in a different color (use HTMLRenderer)
            String text = item.getLabel();
            String ttip = item.getNearestTitle();
            JComponent result = (JComponent)
                    super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            result.setToolTipText (ttip);
            if (ttip != null) {
                text += ":" + ttip; //XXX
                super.setText (text);
            }
            result.setForeground(c);
            return result;
        }

    }

}
