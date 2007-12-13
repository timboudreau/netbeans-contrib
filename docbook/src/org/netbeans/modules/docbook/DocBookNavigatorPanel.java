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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.netbeans.api.docbook.ContentHandlerCallback;
import org.netbeans.api.docbook.ParseJob;
import org.netbeans.api.docbook.ParsingService;
import org.netbeans.api.xml.services.UserCatalog;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.awt.MouseUtils;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Displays an outline of an XML document.
 * @author Jesse Glick
 */
public final class DocBookNavigatorPanel implements NavigatorPanel {
    
    private Lookup.Result selection;
    private final LookupListener selectionListener = new LookupListener() {
        public void resultChanged(LookupEvent ev) {
            display(selection.allInstances());
        }
    };
    private JComponent panel;
    private final DefaultListModel/*<Item>*/ listModel = new DefaultListModel();
    private ListSelectionModel/*<Item>*/ listSelectionModel;
    
    /**
     * Default constructor for layer.
     */
    public DocBookNavigatorPanel() {}
    
    public String getDisplayName() {
        return "Titled Sections"; // XXX I18N
    }
    
    public String getDisplayHint() {
        return "Displays an outline of interesting DocBook/SolBook items."; // XXX I18N
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
        selection = null;
        setCurrent (null);
    }
    
    public Lookup getLookup() {
        return null;
    }
    
    public void setItems (List <Item> items) {
        int ix = listSelectionModel.getLeadSelectionIndex();
        listModel.clear();
        if (items != null) {
            for (Item item : items) {
                listModel.addElement (item);
            }
            if (items.size() > ix && ix > 0) {
                listSelectionModel.setLeadSelectionIndex(ix);
            }
        }
    }
    
    ContentCallback callback;
    DataObject current;
    private void display(Collection/*<DataObject>*/ selectedFiles) {
        // Show list of targets for selected file:
        if (selectedFiles.size() == 1) {
            setCurrent ((DataObject) selectedFiles.iterator().next());
        } else {
            setCurrent (null);
        }
    }
    
    void setCurrent (DataObject obj) {
        if (obj != current) {
            if (current != null && callback != null) {
                ParsingService serv = current.getNodeDelegate().getLookup().lookup(
                    ParsingService.class);
                serv.unregister(callback);
            }
            current = obj;
            if (current != null) {
                callback = new ContentCallback(obj);
                ParsingService serv = current.getNodeDelegate().getLookup().lookup(
                    ParsingService.class);
                serv.register(callback);
            } else {
                callback = null;
                setItems (null);
            }
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

    /**
     * Names of elements which should be considered headers.
     */
    private static final List/*<String>*/ HEADERS = Arrays.asList(new String[] {
    });
    /**
     * Names of elements whose parent elements should be considered headers.
     */
    private static final List/*<String>*/ TITLES = Arrays.asList(new String[] {
        "title", // NOI18N
    });
    /**
     * Names of attributes which if on elements should be considered headers.
     */
    private static final List/*<String>*/ NAMES = Arrays.asList(new String[] {
        // XXX
        "name", // NOI18N
        "id", // NOI18N
    });
    
    private ParseJob job;
    class ContentCallback <T extends Handler> extends ContentHandlerCallback implements Runnable {
        ContentCallback(DataObject d) {
            super (new Handler(d));
        }

        protected void done(FileObject f, ParseJob job) {
            if (job.equals(DocBookNavigatorPanel.this.job)) { //XXX test listening to file
                DocBookNavigatorPanel.this.job = null;
                EventQueue.invokeLater(this);
            }
        }
        
        public void run() {
            List items = ((Handler) getProcessor()).items;
            setItems (items);
        }

        protected void start(FileObject f, ParseJob job) {
            ((Handler) getProcessor()).clear();
            DocBookNavigatorPanel.this.job = job;
            super.start(f, job);
        }
    }
    
    static class Handler extends DefaultHandler {
        private Locator locator;
        // XXX besides line, need to know file: if a complex doc includes others w/
        // entity refs, need to jump to subfiles!
        private int line = -1;
        private String element = null;
        private StringBuffer text = null;
        List <Item> items = new ArrayList (20);
        private DataObject d;
        public Handler (DataObject d) {
            this.d = d;
        }
        
        void clear() {
            synchronized (this) {
                items.clear();
            }
        }
        
        public void setDocumentLocator(Locator l) {
            locator = l;
        }
        // Better style would perhaps be to have include/exclude lists
        // which would be pairs of NS-qualified element name plus attr name... TBD.
        public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
            text = null;
            if (HEADERS.contains(qname.toLowerCase(Locale.ENGLISH))) {
                text = new StringBuffer();
                line = locator.getLineNumber();
                element = null;
            } else if (TITLES.contains(qname.toLowerCase(Locale.ENGLISH))) {
                if (element != null) {
                    text = new StringBuffer();
                }
            } else {
                line = locator.getLineNumber();
                element = qname;
                for (int i = 0; i < attr.getLength(); i++) {
                    String name = attr.getQName(i);
                    if (NAMES.contains(name.toLowerCase(Locale.ENGLISH))) {
                        synchronized (this) {
                            items.add(new Item(attr.getValue(i), element, name, line, d));
                        }
                        break;
                    }
                }
            }
        }
        public void endElement(String uri, String localname, String qname) throws SAXException {
            if (text != null) {
                assert line != -1;
                if (element == null) {
                    element = qname;
                }
                items.add(new Item(text.toString(), element, qname, line, d));
                text = null;
                element = null;
                line = -1;
            }
        }
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (text != null) {
                text.append(ch, start, length);
            }
        }
    }
    
    static final class Item {
        private final String label;
        private final String element;
        private final String header;
        private final int line;
        private final DataObject d;
        public Item(String label, String element, String header, int line, DataObject d) {
            this.label = label;
            this.element = element;
            this.header = header;
            this.line = line /* SAX is 1-based */ - 1;
            this.d = d;
        }
        public String getLabel() {
            return label;
        }
        public String getElement() {
            return element;
        }
        public String getHeader() {
            return header;
        }
        int getLine() {
            return line;
        }
        public void open() {
            LineCookie cookie = (LineCookie) d.getCookie(LineCookie.class);
            if (cookie == null) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            Line l;
            try {
                l = cookie.getLineSet().getCurrent(line);
            } catch (IndexOutOfBoundsException ex) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            l.show(Line.SHOW_TOFRONT);
        }
    }
    
    private static final class ItemCellRenderer extends DefaultListCellRenderer {
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Item item = (Item) value;
            // XXX could also display element and/or header in a different color (use HTMLRenderer)
            String text = item.getLabel();
            return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        }
        
    }
    
}
