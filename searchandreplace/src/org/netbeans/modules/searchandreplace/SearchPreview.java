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

package org.netbeans.modules.searchandreplace;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.BadLocationException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.searchandreplace.model.Item;
import org.netbeans.modules.searchandreplace.model.Problem;
import org.netbeans.modules.searchandreplace.model.Search;
import org.netbeans.modules.searchandreplace.model.TextReceiver;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * TopComponent showing a preview of changes from a global replace.
 *
 * @author Timothy Boudreau
 */
class SearchPreview extends TopComponent implements ActionListener, ListSelectionListener, MouseListener, KeyListener {
    private ItemTableModel model;
    private final JTable table;
    private final Editor editor = new Editor();
    private final JEditorPane content = new JEditorPane();
    private final String searchText;
    private final List listeners = Collections.synchronizedList(
            new LinkedList());
    private final Receiver receiver = new Receiver();
    private RequestProcessor.Task task = null;
    private final Search search;

    private JButton replaceButton = null;
    private JLabel pathLabel = new JLabel();

    /** Creates a new instance of SearchPreview */
    public SearchPreview(Search search, Item[] items) {
        this.search = search;
        String searchText = search.getSearchText();
        String replaceText = search.getReplacementText();

        setLayout (new BorderLayout());

        this.searchText = searchText;
        model = new ItemTableModel(items, replaceText != null);
        table = new JTable (model);
        table.addMouseListener(this);
        table.addKeyListener(this);
        JScrollPane pane = new JScrollPane (table);
        JScrollPane contentScroll = new JScrollPane(content);

        contentScroll.setViewportBorder (BorderFactory.createEmptyBorder());
        contentScroll.setBorder (BorderFactory.createEmptyBorder());
        pane.setViewportBorder (BorderFactory.createEmptyBorder());
        pane.setBorder (BorderFactory.createEmptyBorder());
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add (pathLabel, BorderLayout.NORTH);
        contentPanel.add (contentScroll, BorderLayout.CENTER);
        
        Border b = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, 
                UIManager.getColor("controlShadow")), 
                BorderFactory.createEmptyBorder(5, 5, 1, 5));
        
        pathLabel.setBorder (b);

        JSplitPane split = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,
                pane, contentPanel);
        add (split, BorderLayout.CENTER);
        if (replaceText != null) {
            JPanel buttons = new JPanel();
            add (buttons, BorderLayout.SOUTH);
            buttons.setLayout (new FlowLayout(FlowLayout.LEADING));
            JButton jb = replaceButton = new JButton (NbBundle.getMessage (SearchPreview.class,
                    "LBL_Replace")); //NOI18N
            jb.setDisplayedMnemonicIndex(Mnemonics.findMnemonicAmpersand(
                    jb.getText()));
            Mnemonics.setLocalizedText(jb, jb.getText());
            jb.addActionListener (this);
            buttons.add (jb);
            boolean mac = Utilities.getOperatingSystem() == Utilities.OS_MAC;
            String key = mac ? "TIP_SearchPreview" : "TIP_SearchPreview.mac";
            String replaceButtonTip = NbBundle.getMessage (SearchPreview.class,
                    key);
            jb.setToolTipText (replaceButtonTip);
        }
        table.setDefaultRenderer(String.class, model);
        table.setDefaultRenderer(Boolean.TYPE, model);
        
        int rowMargin = table.getRowMargin();
        int pureRowHeight = model.getTableCellRendererComponent(table, "dummy", false, false, 0, 0).getPreferredSize().height;
        table.setRowHeight(pureRowHeight + rowMargin);
        
        table.setDefaultEditor(Boolean.TYPE, editor);
        table.putClientProperty ("JTable.autoStartsEdit", Boolean.TRUE); //NOI18N
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); //NOI18N
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        table.setFocusCycleRoot(false);

        setFocusTraversalPolicy(new FTP());

        content.addKeyListener(this);
        content.setEditable(false);
        content.getCaret().setBlinkRate(0);


        search.setItemStateObserver(model);

        if (items.length > 0) {
            table.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    public void removeNotify() {
        super.removeNotify();
        search.setItemStateObserver (null);
    }

    public void addNotify() {
        super.addNotify();
        search.setItemStateObserver (model);
    }

    private void setItems (Item[] items) {
        System.err.println("Setting items to " + Arrays.asList(items));
        Mutex.EVENT.readAccess(new TableModelCreator (items));
    }

    private void rescan() {
        LifecycleManager.getDefault().saveAll();
        search.getRequestProcessor().post( new Rescanner() );
    }
    public void actionPerformed(ActionEvent e) {
        final Item[] items = (Item[]) model.getItems().clone();
        Arrays.sort (items, new ItemComparator());
        final ProgressHandle progress = ProgressHandleFactory.createHandle(NbBundle.getMessage(SearchPreview.class, "LBL_Replacing"));
        progress.start(items.length * 2);
        search.getRequestProcessor().post(new Replacer(items, progress));
    }

    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    public void open() {
        Mode m = WindowManager.getDefault().findMode ("output"); //NOI18N
        if (m != null) {
            m.dockInto(this);
        }
        super.open();
    }

    protected String preferredID() {
        return "searchAndReplace"; //NOI18N
    }

    public void componentActivated() {
        table.requestFocus();
    }

    public void valueChanged(ListSelectionEvent e) {
        int row = table.getSelectedRow();
        synchronized (receiver) {
            if (task != null) {
                task.cancel();
                task = null;
            }
            if (row != -1) {
                Item item = model.getItems()[row];
                search.requestText(item, receiver);
                String description = item.getDescription() + 
                        File.separator + item.getName();
                pathLabel.setText (description);
                //in case it doesn't fit
                pathLabel.setToolTipText (description);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            Point p = e.getPoint();
            int column = table.columnAtPoint(p);
            int row = table.rowAtPoint(p);
            if (row >= 0 && column >= 0) { //XXX exclude checkbox column
                Item item = model.getItems()[row];
                if (item != null) {
                    openItem (item);
                }
            }
        }
    }

    private void openItem (final Item item) {
        if (!item.isValid()) {
            return;
        }
        File file = item.getFile();
        FileObject fob = FileUtil.toFileObject(file);
        if (fob != null) {
            try {
                DataObject ob = DataObject.find (fob);
                final Node n = ob.getNodeDelegate();
                EditCookie eck = (EditCookie) n.getLookup().lookup(
                        EditCookie.class);

                boolean opened = eck != null;
                if (opened) {
                    eck.edit();
                } else {
                    OpenCookie ck = (OpenCookie) n.getLookup().lookup(
                            OpenCookie.class);
                    opened = ck != null;
                    if (opened) {
                        ck.open();
                    }
                }
                if (opened) {
                    new TimedEditorCookieListener (n.getLookup(),
                            item.getLocation().x, item.getLocation().y);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } catch (DataObjectNotFoundException donfe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                        donfe);
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            boolean isCtrl = (e.getModifiers() &
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;

            if (replaceButton != null && isCtrl) {
                e.consume();
                replaceButton.doClick();
            } else if (e.getSource() == table) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    e.consume();
                    Item item = model.getItems()[row];
                    openItem (item);
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        //do nothing
    }

    public void mouseReleased(MouseEvent e) {
        //do nothing
    }

    public void mouseEntered(MouseEvent e) {
        //do nothing
    }

    public void mouseExited(MouseEvent e) {
        //do nothing
    }

    public void keyTyped(KeyEvent e) {
        //do nothing
    }

    public void keyPressed(KeyEvent e) {
        //do nothing
        if (e.getSource() == table && e.getKeyCode() == KeyEvent.VK_TAB) {
            Component toFocus;
            if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                toFocus = replaceButton == null ? (Component) content :
                    (Component) replaceButton;
            } else {
                toFocus = content;
            }
            toFocus.requestFocus();
            e.consume();
        }
    }

    private static List openedPreviews = new LinkedList();
    protected void componentClosed() {
        for (Iterator i = openedPreviews.iterator(); i.hasNext();) {
            Reference ref = (WeakReference) i.next();
            SearchPreview prev = (SearchPreview) ref.get();
            if (prev == null || prev == this) {
                i.remove();
            }
        }
    }

    protected void componentOpened() {
        if (replaceButton != null) {
            replaceButton.requestFocus();
        }
        openedPreviews.add (0, new WeakReference(this));
    }

    public static SearchPreview getLastSearchComponent() {
        for (Iterator i = openedPreviews.iterator(); i.hasNext();) {
            Reference ref = (WeakReference) i.next();
            SearchPreview prev = (SearchPreview) ref.get();
            if (prev == null) {
                i.remove();
            } else {
                return prev;
            }
        }
        return null;
    }

    /**
     * Cell editor for the checkbox we use for editing.
     */
    private class Editor extends FocusAdapter implements TableCellEditor, ActionListener {
        private final JCheckBox box = new JCheckBox();
        boolean ignore = false;

        Editor() {
            box.addFocusListener(this);
            box.addActionListener(this);
            box.setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException (value.toString());
            }
            try {
                ignore = true;
                box.setSelected (Boolean.TRUE.equals(value));
            } finally {
                ignore = false;
            }
            if (isSelected) {
                box.setForeground(table.getSelectionForeground());
                box.setBackground(table.getSelectionBackground());
            } else {
                box.setForeground(table.getForeground());
                box.setBackground(table.getBackground());
            }
            return box;
        }

        public Object getCellEditorValue() {
            return box.isSelected() ? Boolean.TRUE : Boolean.FALSE;
        }

        public boolean isCellEditable(EventObject anEvent) {
            boolean shifted = false;
            if (anEvent instanceof InputEvent) {
                InputEvent ie = (InputEvent) anEvent;

                shifted = (ie.getModifiersEx() &
                    InputEvent.SHIFT_DOWN_MASK)
                    != 0;
            }
            model.setShiftedEdit(shifted);
            return true;
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        public boolean stopCellEditing() {
            fire (false);
            return true;
        }

        public void cancelCellEditing() {
            fire (true);
        }

        public void addCellEditorListener(CellEditorListener l) {
            listeners.add (l);
        }

        public void removeCellEditorListener(CellEditorListener l) {
            listeners.remove(l);
        }

        private void fire (boolean cancelled) {
            CellEditorListener[] l =
                    (CellEditorListener[]) listeners.toArray(new CellEditorListener[0]);
            if (l.length > 0) {
                ChangeEvent ce = new ChangeEvent (this);
                for (int i=0; i < l.length; i++) {
                    if (cancelled) {
                        l[i].editingCanceled(ce);
                    } else {
                        l[i].editingStopped(ce);
                    }
                }
            }
        }


        public void focusGained(FocusEvent e) {
            if (!ignore) {
                box.doClick();
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (!ignore) {
                stopCellEditing();
            }
        }
    }

    /**
     * Implementation of TextReceiver which is passed to get the text of
     * an item.  The text is fetched from the file asynchronously, and then
     * passed to setText() to set the text, select the text the item represents
     * and scroll it into view.
     */
    private class Receiver implements TextReceiver, Runnable {
        private Point position;
        public void setText(final String txt, String mimeType, final Point position) {
            assert EventQueue.isDispatchThread();
            content.setText(txt);

            if ("content/unknown".equals(mimeType)) { //NOI18N
                mimeType = "text/plain"; //Good idea?  Bad? Hmm...
            }
            content.setContentType(mimeType);

            //Force a paint, so the view's cache is up to date, or the
            //editor view can claim it doesn't yet know about the position
            //we ask to scroll to
//            content.paintImmediately(0, 0, content.getWidth(),
//                    content.getHeight());

            this.position = position;
            //Let the L&F do anything it needs to do before we try to fiddle
            //with it - get out of its way.  Some Swing View classes don't
            //have accurate position data until they've painted once.
            EventQueue.invokeLater(this);
        }

        public void run() {
            try {
                if (!content.isShowing()) {
                    return;
                }
                if (position != null) {
                    content.setSelectionStart(position.x);
                    content.setSelectionEnd (position.y);
                    Rectangle r = content.modelToView(position.x);
                    if (r != null) {
                        //Editor kit not yet updated, what to do
                        content.scrollRectToVisible(r);
                    }
                    content.getCaret().setBlinkRate(0);
                    content.repaint();
                }
            } catch (BadLocationException e) {
                //Maybe not even notify this - not all editors
                //will have a 1:1 correspondence to file positions -
                //it's perfectly reasonable for this to be thrown
                ErrorManager.getDefault().notify (
                        ErrorManager.INFORMATIONAL, e);
            }
        }
    }

    private static class ItemComparator implements Comparator {
        //Inverse sorts search matches so we replace from the tail of the file up
        public int compare (Object a, Object b) {
            Item i1 = (Item) a;
            Item i2 = (Item) b;

            Point p1 = i1.getLocation() == null ?
                new Point() :
                i1.getLocation();
            Point p2 = i2.getLocation() == null ?
                    new Point() :
                    i2.getLocation();

            int result = i1.getFile().getPath().compareTo(
                    i2.getFile().getPath()) * 16384;

            return result + (p2.x - p1.x);
        }
    }


    /**
     * Runnable which actually does the work of replacing items.  Replacement
     * runs in a background thread, then either closing the component if
     * successful, or asking the user to rescan if unsuccessful.
     */
    private class Replacer implements Runnable {
        private final Item[] items;
        private final ProgressHandle progress;
        public Replacer (final Item[] items, final ProgressHandle progress) {
            this.items = items;
            this.progress = progress;
        }

        public void run() {
            if (!EventQueue.isDispatchThread()) {
                try {
                    try {
                        checkForErrors();
                    } catch (IOException ioe) {
                        if (userRequestsRescan(ioe)) {
                            rescan();
                        }
                        return;
                    }

                    //We do still need a list of problems - theoretically
                    //the files could change after we check for errors,
                    //so we handle exceptions that occur when we really try
                    //to write files
                    final List problems = doReplace();

                    if (!problems.isEmpty()) {
                        //Replace the model for the table with a model showing
                        //the problems encountered
                        displayProblemsToUser(problems);
                    } else {
                        String success =
                                NbBundle.getMessage(SearchPreview.class,
                                "MSG_Success"); //NOI18N

                        StatusDisplayer.getDefault().setStatusText(success);
                                
                        EventQueue.invokeLater(this);
                    }
                    repaint();
                } finally {
                    progress.finish();
                }
            } else {
                //EQ invocation
                closePreviewAndSendFocusToEditor();
            }
        }

        /**
         * Shows a dialog with the message from the IOException
         * and asks the user if they want to rescan files and
         * try again.
         */
        private boolean userRequestsRescan (IOException ioe) {
            System.err.println("GOT ONE: " + ioe.getMessage());
            NotifyDescriptor nd =
                    new NotifyDescriptor.Message (
                        ioe.getLocalizedMessage(),
                        NotifyDescriptor.QUESTION_MESSAGE);

            String rerunOption = NbBundle.getMessage (
                    SearchPreview.class, "LBL_RERUN"); //NOI18N

            nd.setOptions(new Object[] { rerunOption,
                    NotifyDescriptor.CANCEL_OPTION });

            Object dlgResult =
                    DialogDisplayer.getDefault().notify(nd);

            return rerunOption.equals(dlgResult);
        }

        private void checkForErrors() throws IOException {
            for (int i=0; i < items.length; i++) {
                items[i].checkValid();
            }
        }

        private List doReplace() {
            List problems = new ArrayList(items.length);
            for (int i=0; i < items.length; i++) {
                progress.progress(items[i].getName(), i + items.length);
                if (items[i].isShouldReplace() && items[i].isValid()) { //XXX test unneeded now?
                    try {
                        items[i].replace();
                    } catch (IOException ex) {
                        ex.printStackTrace(); //XXX
                        problems.add (new Problem (items[i].getFile(), ex));
                    }
                }
            }
            return problems;
        }

        private void displayProblemsToUser(List problems) {
            StatusDisplayer.getDefault().setStatusText(
                    NbBundle.getMessage(SearchPreview.class, "MSG_Failure")); //NOI18N

            Item[] troubles =
                    (Item[]) problems.toArray(new Item[problems.size()]);
            setItems (troubles);
        }

        private void closePreviewAndSendFocusToEditor() {
            //Success - we've been invokeLatered to close the component
            close();
            //Activating the editor will force a reload
            Mode m = WindowManager.getDefault().findMode("editor");
            if (m != null) {
                TopComponent tc = m.getSelectedTopComponent();
                if (tc != null) {
                    tc.requestActive();
                }
            }
        }
    }

    /**
     * Class which re-scans the files and replaces the model for the table
     * with a new one representing their current state.
     */
    private class Rescanner implements Runnable {
        public void run() {
            System.err.println("DOING RESCAN");
            Item[] oldItems = model.getItems();
            ProgressHandle progress = ProgressHandleFactory.createHandle(
                    NbBundle.getMessage (SearchPreview.class,
                    "LBL_FindingFiles")); //NOI18N
            progress.start();
            try {
                Item[] items = search.search(progress);

                //Okay, a slightly weird use of a map
                Map m = new HashMap();

                for (int i = 0; i < oldItems.length; i++) {
                    m.put (oldItems[i], oldItems[i]);
                }

                for (int i=0; i < items.length; i++) {
                    Item old = (Item) m.get(items[i]);
                    if (old != null) {
                        System.err.println("Found match in old file " + items[i]);
                        items[i].setShouldReplace(old.isShouldReplace());
                        items[i].setEntireFileShouldReplace(
                                old.isEntireFileShouldReplace());
                    }
                }
                System.err.println("Setting items from rescan");
                setItems (items);
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            } finally {
                progress.finish();
            }
        }
    }

    private class TableModelCreator implements Runnable {
        private final Item[] items;
        public TableModelCreator (Item[] items) {
            this.items = items;
        }

        public void run() {
            model = new ItemTableModel (items,
                    search.getReplacementText() != null);
            table.setModel (model);

            if (items.length > 0) {
                table.getSelectionModel().setSelectionInterval(0, 0);
            }
            table.repaint();
        }
    }

    private class FTP extends FocusTraversalPolicy {
        public Component getComponentAfter(Container a, Component c) {
            if (a == replaceButton) {
                return table;
            } else if (a == table) {
                return content;
            } else {
                return replaceButton == null ? (Component) table :
                    (Component) content;
            }
        }

        public Component getComponentBefore(Container a, Component c) {
            if (a == replaceButton) {
                return content;
            } else if (a == content) {
                return table;
            } else {
                return replaceButton == null ? (Component) table :
                    (Component) replaceButton;
            }
        }

        public Component getFirstComponent(Container a) {
            return replaceButton == null ? (Component) table :
                (Component) replaceButton;
        }

        public Component getLastComponent(Container a) {
            return content;
        }

        public Component getDefaultComponent(Container a) {
            return getFirstComponent(a);
        }
    }
}
