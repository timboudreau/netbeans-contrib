/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.xml.services.UserCatalog;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.openide.ErrorManager;
import org.openide.awt.MouseUtils;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
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
        return "DocBook"; // XXX I18N
    }
    
    public String getDisplayHint() {
        return "Displays an outline of interesting DocBook items."; // XXX I18N
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
    }
    
    public Lookup getLookup() {
        return null;
    }
    
    private void display(Collection/*<DataObject>*/ selectedFiles) {
        listModel.clear();
        // Show list of targets for selected file:
        if (selectedFiles.size() == 1) {
            final DataObject d = (DataObject) selectedFiles.iterator().next();
            final InputSource src = DataObjectAdapters.inputSource(d);
            // Parse asynch, since it can take a second or two for a big file.
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    try {
                        final Item[] items = parse(src, d);
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
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
    
    static Item[] parse(InputSource src, final DataObject d) throws IOException, SAXException, ParserConfigurationException {
        final List/*<Item>*/ items = new ArrayList();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        class Handler extends DefaultHandler {
            private Locator locator;
            // XXX besides line, need to know file: if a complex doc includes others w/
            // entity refs, need to jump to subfiles!
            private int line = -1;
            private String element = null;
            private StringBuffer text = null;
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
                    //System.err.println("HEADERS match on " + qname + " at " + line);
                } else if (TITLES.contains(qname.toLowerCase(Locale.ENGLISH))) {
                    if (element != null) {
                        text = new StringBuffer();
                        //System.err.println("TITLES match on " + qname + " inside " + element + " at line " + line);
                    }
                } else {
                    line = locator.getLineNumber();
                    element = qname;
                    //System.err.println("plain element " + element + " at " + line);
                    for (int i = 0; i < attr.getLength(); i++) {
                        String name = attr.getQName(i);
                        if (NAMES.contains(name.toLowerCase(Locale.ENGLISH))) {
                            //System.err.println("NAMES match on " + name + " in " + element + " at " + line);
                            items.add(new Item(attr.getValue(i), element, name, line, d));
                            break;
                        }
                    }
                }
            }
            public void endElement(String uri, String localname, String qname) throws SAXException {
                if (text != null) {
                    //System.err.println("ending " + qname + " in " + element + " with " + text + " at " + line);
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
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
                InputSource known;
                try {
                    known = UserCatalog.getDefault().getEntityResolver().resolveEntity(publicId, systemId);
                } catch (IOException e) {
                    throw new SAXException(e);
                }
                if (known != null) {
                    // In our IDE catalog, cool.
                    //System.err.println("known match on " + publicId + " / " + systemId + ": " + known.getSystemId());
                    return known;
                } else if (systemId.startsWith("http")) { // NOI18N
                    // Do not load any remote entities or DTDs, too slow.
                    //System.err.println("No known match for remote " + publicId + " / " + systemId);
                    return new InputSource(new StringReader(""));
                } else {
                    // Maybe a local file: URL or similar.
                    return null;
                }
            }
            /*
            public void skippedEntity(String name) throws SAXException {
                System.err.println("skipped: " + name);
            }
             */
        }
        parser.parse(src, new Handler());
        return (Item[]) items.toArray(new Item[items.size()]);
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
