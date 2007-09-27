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

package org.netbeans.modules.genericnavigator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.netbeans.api.queries.FileEncodingQuery;
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
 * @author Tim Boudreau
 */
public class GenericNavPanel implements NavigatorPanel, Runnable, ListSelectionListener, LookupListener, ActionListener, DocumentListener {

    private static final Logger LOGGER = Logger.getLogger(GenericNavPanel.class.getName());

    private RequestProcessor rp = new RequestProcessor ("Generic Navigator Scan Thread"); //NOI18N
    private DefaultListModel mdl = new DefaultListModel();
    private JList jl = new JList(mdl);
    private JScrollPane pane = new JScrollPane(jl);
    private JComboBox box = new JComboBox();
    private JPanel pnl = new JPanel();
    private RequestProcessor.Task task = null;
    private Lookup last = null;
    private JPanel innerPanel = new JPanel();
    static final Set<GenericNavPanel> cache = new WeakSet<GenericNavPanel>();

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
        for (GenericNavPanel p : cache) {
            p.refresh();
        }
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

    Lookup.Result<DataObject> res;
    public void panelActivated(Lookup lookup) {
        mdl.clear();
        synchronized (this) {
            if (task == null) {
                rp.post (this);
            } else {
                task.schedule(200);
            }
            last = lookup;
            res = last.lookupResult(DataObject.class);
            res.addLookupListener(this);
        }
    }
    private String mimeType = null;

    public void panelDeactivated() {
        Lookup lkp = Utilities.actionsGlobalContext();
        if (lkp.lookup(DataObject.class) != null) {
            String mimeType = lkp.lookup(DataObject.class).getPrimaryFile().getMIMEType();
            LOGGER.log(Level.FINE, "panel deactivated: {0}", mimeType);
        }
        synchronized (this) {
            if (task != null) {
                task.cancel();
                task = null;
            }
            if (res != null) {
                res.removeLookupListener(this);
            }
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
            DataObject dob = last.lookup(DataObject.class);
            if (dob != null) {
                mimeType = dob.getPrimaryFile().getMIMEType();
            }
        }
        if (mimeType == null) {
            return null;
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

    private void scanFile(List</*XXX should be NavigationItem*/Object> l, Lookup lkp) {
        DataObject ob = lkp.lookup(DataObject.class);
        if (ob != null) {
            LOGGER.log(Level.FINE, "object of type {0}", ob.getPrimaryFile().getMIMEType());
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
            } catch (Exception ex) {
                ErrorManager.getDefault().notify (ex);
            };
        }
        if (l.isEmpty()) {
            l.add (getString ("LBL_NO_HEADINGS")); //NOI18N
        }
    }

    private Reference <Document> doc = null;
    private void setDocumentToListenTo(Document d) {
        Document old = doc == null ? null : doc.get();
        if (old != null) {
            old.removeDocumentListener(this);
        }
        if (d != null) {
            doc = new WeakReference <Document> (d);
            d.addDocumentListener(this);
        }
    }

    private String getFileData(DataObject dob) throws IOException, InterruptedException, InvocationTargetException {
        if (dob != null) {
            if (last != null && last.lookup(DataObject.class) != null) {
                final EditorCookie ck = dob.getCookie(EditorCookie.class);
                final JEditorPane[] pane = new JEditorPane[1];
                if (ck != null) {
                    EventQueue.invokeAndWait(new Runnable() {
                        public void run() {
                            JEditorPane[] panes = ck.getOpenedPanes();
                            if (panes != null && panes.length > 0) {
                                pane[0] = panes[0];
                            }
                        }
                    });
                }
                if (pane[0] != null) {
                    setDocumentToListenTo(pane[0].getDocument());
                    return pane[0].getText();
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
                        CharBuffer decoded;
                        try {
                            decoded = FileEncodingQuery.getEncoding(fob).newDecoder().decode(buf);
                        } catch (CharacterCodingException x) {
                            // Fall back to a "safe" though perhaps inaccurate encoding.
                            decoded = Charset.forName("ISO-8859-1").newDecoder().decode(buf);
                        }
                        return decoded.toString().replace("\r\n", "\n");
                    } catch (FileNotFoundException fnfe) {
                        //Timing issue:  If the user selects a file and chooses
                        //Delete before we've read it, the file may not 
                        //exist by the time we try to scan it
                        return "";
                    }
                }
            }
        }
        return " "; //NOI18N
    }
    
    public void valueChanged(ListSelectionEvent e) {
        int ix = jl.getSelectionModel().getLeadSelectionIndex();
        if (ix != -1 && ix < jl.getModel().getSize()) {
            Object o = jl.getModel().getElementAt(ix);
            if (o instanceof NavigationItem) {
                NavigationItem item = (NavigationItem) o;
                try {
                    DataObject dob = DataObject.find(FileUtil.toFileObject(item.file));
                    EditorCookie ck = dob.getCookie(EditorCookie.class);
                    if (ck != null) {
                        JEditorPane[] ed = ck.getOpenedPanes();
                        if (ed != null && ed.length > 0) {
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
        if (mimeType == null) {
            return;
        }
        PatternItem[] items = PatternItem.getDefaultItems(mimeType);
        DefaultComboBoxModel mdl = new DefaultComboBoxModel (items);
        box.setModel (mdl);
        box.setEnabled (items.length > 1);
        if (Arrays.asList(items).contains(o)) {
            box.setSelectedItem(o);
        }
    }

    public synchronized void resultChanged(LookupEvent lookupEvent) {
        DataObject dob = last.lookup(DataObject.class);
        mimeType = dob != null ? dob.getPrimaryFile().getMIMEType() : null;
        refresh();
        //XXX this is a mess
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                pnl.doLayout();
                pnl.invalidate();
                pnl.revalidate();
                pnl.repaint();
            }
        });
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
