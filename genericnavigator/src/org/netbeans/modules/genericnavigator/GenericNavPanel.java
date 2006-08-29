/* The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
package org.netbeans.modules.genericnavigator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.WeakSet;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 */
public class GenericNavPanel implements NavigatorPanel, Runnable, ListSelectionListener, LookupListener, ActionListener, DocumentListener {
    private RequestProcessor rp = new RequestProcessor ("Generic Navigator Scan Thread"); //NOI18N
    private DefaultListModel mdl = new DefaultListModel();
    private JList jl = new JList(mdl);
    private JScrollPane pane = new JScrollPane(jl);
    private JComboBox box = new JComboBox();
    private JPanel pnl = new JPanel();
    private RequestProcessor.Task task = null;
    private volatile boolean active;
    private Lookup last = null;
    private static final ByteBuffer buf = ByteBuffer.allocate(8192);
    private static final CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
    private JPanel innerPanel = new JPanel();
    static final WeakSet cache = new WeakSet();

    public GenericNavPanel() {
        jl.getSelectionModel().addListSelectionListener(this);
        pane.setBorder (new EmptyBorder (0,0,0,0));
        pane.setViewportBorder(new MatteBorder(5,5,5,5, jl.getBackground()));
        pnl.setLayout (new BorderLayout());
        pnl.add (pane, BorderLayout.CENTER);
        pnl.add (innerPanel, BorderLayout.SOUTH);
        innerPanel.setLayout (new BorderLayout());
        innerPanel.add (box, BorderLayout.CENTER);
        innerPanel.setBorder (new EmptyBorder (5,5,5,5));
        pane.setPreferredSize (new Dimension(300, 200));
        box.addActionListener (this);
        ViewTooltips.register (jl);
        //Used to update the combo box when the options dialog adds/removes
        //pattern items
        cache.add (this);
    }

    static void refreshAll() {
        for (Iterator i = cache.iterator(); i.hasNext();) {
            GenericNavPanel p = (GenericNavPanel) i.next();
            if (p != null) {
                p.refresh();
            }
        }
    }

    public void requestFocus() {
        jl.requestFocus();
    }

    public String getDisplayName() {
        return getString ("NavPanel.lbl"); //NOI18N
    }

    public String getDisplayHint() {
        return getString("NavPanel.hint"); //NOI18N
    }

    public JComponent getComponent() {
        return pnl;
    }

    Lookup.Result res;
    public void panelActivated(Lookup lookup) {
        mdl.clear();
        synchronized (this) {
            if (task == null) {
                rp.post (this);
            } else {
                task.schedule(200);
            }
            last = lookup;
            res = last.lookup(new Lookup.Template(DataObject.class));
            res.addLookupListener(this);
        }
        active = true;
    }
    private String mimeType = null;

    public void panelDeactivated() {
        Lookup lkp = Utilities.actionsGlobalContext();
        if (lkp.lookup(DataObject.class) != null) {
            String mimeType = ((DataObject) lkp.lookup(DataObject.class)).getPrimaryFile().getMIMEType();
            System.err.println("PANEL DEACTIVATED " + mimeType);
        }
        active = false;
        synchronized (this) {
            if (task != null) {
                task.cancel();
                task = null;
            }
            res.removeLookupListener(this);
            res = null;
            last = null;
        }
    }

    public Lookup getLookup() {
        return null;
    }

    public void run() {
        synchronized (this) {
            task = null;
        }
        List <Object> l = new ArrayList <Object> ();
        Lookup lkp;
        synchronized (this) {
            lkp = last;
        }
        if (lkp == null) {
            return;
        }
        synchronized (this) {
            task = null;
        }
        scanFile (l, lkp);
        mdl.clear();
        for (Iterator it = l.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof NavigationItem) {
                NavigationItem item = (NavigationItem) o;
                mdl.add(mdl.size(), item);
            } else {
                mdl.add (mdl.size(), o);
            }
        }
    }

    private PatternItem getPatternItem() {
        if (box.isVisible()) {
            Object o = box.getSelectedItem();
            if (o instanceof PatternItem) {
                return ((PatternItem) o);
            }
        }
        if (mimeType == null && last != null) {
            DataObject dob = (DataObject) last.lookup(DataObject.class);
            if (dob != null) {
                mimeType = dob.getPrimaryFile().getMIMEType();
            }
        }
        PatternItem[] items = PatternItem.getDefaultItems(mimeType);
        if (items.length > 0) {
            return items[0];
        } else {
            return null;
        }
    }

    private Pattern getPattern() {
        PatternItem item = getPatternItem();
        return item == null ? null : item.getPattern();
    }

    private void scanFile(List <Object> l, Lookup lkp) {
        DataObject ob = (DataObject) lkp.lookup(DataObject.class);
        System.err.println("OBJECT OF TYPE " + ob.getPrimaryFile().getMIMEType());
        if (ob != null) {
            try {
                CharSequence sq = getFileData(ob);
                if (sq == null || sq.length() < 5) {
                    return;
                }
                PatternItem item = getPatternItem();
                Pattern pattern = getPattern();
                if (pattern != null) {
                    Matcher matcher = pattern.matcher(sq);
                    while (matcher.find()) {
                        int[] grps = item.getIncludeGroups();
                        if (grps.length == 0) {
                            grps = new int[] { 0 };
                        }
                        for (int i = 0; i < grps.length; i++) {
                            String s = matcher.group(grps[i]);
                            File f = FileUtil.toFile(ob.getPrimaryFile());
                            NavigationItem navitem = new NavigationItem (
                                    f, matcher.start(), matcher.end(),
                                    s, item.isStripHtml());
                            l.add (navitem);
                        }
                    }
                }
            } catch (IOException ex) {
                ErrorManager.getDefault().notify (ex);
            };
        }
        if (l.isEmpty()) {
            l.add (getString ("LBL_NO_HEADINGS")); //NOI18N
        }
    }

    private Reference <Document> doc = null;
    private void setDocumentToListenTo(Document d) {
        Document old = doc == null ? null : (Document) doc.get();
        if (old != null) {
            old.removeDocumentListener(this);
        }
        if (d != null) {
            doc = new WeakReference <Document> (d);
            d.addDocumentListener(this);
        }
    }

    private CharSequence getFileData(DataObject dob) throws IOException {
        if (dob != null) {
            if (last != null && last.lookup(DataObject.class) != null) {
                EditorCookie ck = (EditorCookie) dob.getCookie(EditorCookie.class);
                if (ck != null && ck.getOpenedPanes() != null && ck.getOpenedPanes().length > 0) {
                    JEditorPane pane = ck.getOpenedPanes()[0];
                    setDocumentToListenTo (pane.getDocument());
                    return pane.getText();
                } else {
                    FileObject fob = dob.getPrimaryFile();
                    File f = FileUtil.toFile (fob);
                    if (f == null) {
                        return "";
                    }
                    ByteBuffer buf = ByteBuffer.allocate ((int) f.length());
                    Pattern pattern = getPattern();
                    if (pattern == null) {
                        return null;
                    }
                    try {
                        FileChannel ch = new FileInputStream(f).getChannel();
                        ch.read(buf);
                        ch.close();
                        buf.flip();
                        CharBuffer seq = decoder.decode(buf);

                        String sq = Utilities.replaceString(seq.toString(),
                                "\r\n", "\n"); //NOI18N
                        return sq;
                    } catch (FileNotFoundException fnfe) {
                        //Timing issue:  If the user selects a file and chooses
                        //Delete before we've read it, the file may not 
                        //exist by the time we try to scan it
                        return "";
                    }
                }
            }
        }
        return " ";
    }

    public void valueChanged(ListSelectionEvent e) {
        int ix = jl.getSelectionModel().getLeadSelectionIndex();
        if (ix != -1 && ix < jl.getModel().getSize()) {
            Object o = jl.getModel().getElementAt(ix);
            if (o instanceof NavigationItem) {
                NavigationItem item = (NavigationItem) o;
                try {
                    DataObject dob = DataObject.find(FileUtil.toFileObject(item.file));
                    EditorCookie ck = (EditorCookie) dob.getCookie (EditorCookie.class);
                    if (ck != null) {
                        JEditorPane[] ed = ck.getOpenedPanes();
                        if (ed != null || ed.length > 0) {
                            ed[0].setSelectionStart(item.offset);
                            ed[0].setSelectionEnd(item.end);
                            TopComponent tc = (TopComponent)
                                SwingUtilities.getAncestorOfClass(TopComponent.class,
                                    ed[0]);
                            if (tc != null) {
                                tc.requestActive();
                            }
                        }
                    }
                } catch (DataObjectNotFoundException ex) {
                    ErrorManager.getDefault().notify (ex);
                }
            }
        }
    }

    void refresh() {
        Object o = box.getSelectedItem();
        PatternItem[] items = PatternItem.getDefaultItems(mimeType);
        boolean showCombo = items.length > 1;
        DefaultComboBoxModel mdl = new DefaultComboBoxModel (items);
        box.setModel (mdl);
        box.setEnabled (items.length > 1);
        if (new HashSet(Arrays.asList(items)).contains(o)) {
            box.setSelectedItem(o);
        }
    }

    public synchronized void resultChanged(LookupEvent lookupEvent) {
        DataObject dob = (DataObject) last.lookup(DataObject.class);
        FileObject fob = dob.getPrimaryFile();
        mimeType = fob.getMIMEType();
        refresh();
        //XXX this is a mess
        pnl.doLayout();
        pnl.invalidate();
        pnl.revalidate();
        pnl.repaint();
        if (task == null) {
            rp.post (this);
        } else {
            task.schedule(200);
        }
    }

    static String getString(String s) {
        return NbBundle.getMessage (GenericNavPanel.class, s);
    }

    static String getString(String s, String vals) {
        return NbBundle.getMessage (GenericNavPanel.class, s, vals);
    }

    public void actionPerformed(ActionEvent e) {
        synchronized (this) {
            if (task != null) {
                task.schedule(500);
            } else {
                task = rp.post(this);
            }
        }
    }

    public void insertUpdate(DocumentEvent e) {
        synchronized (this) {
            if (task != null) {
                task.schedule(500);
            } else {
                task = rp.post(this);
            }
        }
    }

    public void removeUpdate(DocumentEvent e) {
        insertUpdate (e);
    }

    public void changedUpdate(DocumentEvent e) {
        insertUpdate (e);
    }
}
