/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions.settings;

import org.openide.util.Lookup;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.nodes.BeanNode;
import org.openide.xml.XMLUtil;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.netbeans.modules.tasklist.suggestions.SuggestionType;
import org.netbeans.modules.tasklist.suggestions.SuggestionTypes;

import java.io.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.beans.IntrospectionException;

/**
 * User's settings. Properties persistence depenends on bean info
 * and its scope on mf-layer attrributes.
 *
 * @author Petr Kuzel
 */
public final class ManagerSettings implements Node.Handle {

    public static final String AFTER_OPEN_SCAN_DELAY = "showScanDelay";
    public static final String AFTER_EDIT_SCAN_DELAY = "editScanDelay";

    // for external providers
    public static final String AFTER_SAVE_SCAN_DELAY = "saveScanDelay";



    /** Delay to wait after a file has been shown before we rescan */
    private int showScanDelay = DEFAULT_SHOW_SCAN_DELAY;

    /** Delay to wait after a file has been edited before we rescan */
    private int editScanDelay = DEFAULT_EDIT_SCAN_DELAY;

    /** Delay to wait after a file has been saved before we rescan */
    private int saveScanDelay = DEFAULT_SAVE_SCAN_DELAY;

    private final static int DEFAULT_SHOW_SCAN_DELAY = 500;
    private final static int DEFAULT_EDIT_SCAN_DELAY = 1000;
    private final static int DEFAULT_SAVE_SCAN_DELAY = 1000;

    private final static boolean DEFAULT_SCAN_ON_SHOW = true;
    private final static boolean DEFAULT_SCAN_ON_EDIT = true;
    private final static boolean DEFAULT_SCAN_ON_SAVE = false;

    private ManagerSettings() {
    }

    public static ManagerSettings getDefault() {
        // note the diference in lifecycle, this singleton may
        // bacome dean and get reloaded again
        return (ManagerSettings) Lookup.getDefault().lookup(ManagerSettings.class);
    }

    public static ManagerSettings layerEntryPoint() {
        return new ManagerSettings();
    }

    public void store() {
        writeTypeRegistry();
    }

    /** Getter for property showScanDelay.
     * @return Value of property showScanDelay.
     *
     */
    public int getShowScanDelay() {
        return showScanDelay;
    }

    /** Setter for property showScanDelay.
     * @param showScanDelay New value of property showScanDelay.
     *
     */
    public void setShowScanDelay(int showScanDelay) {
        if (showScanDelay < 0) {
            showScanDelay = 500;
        }
        this.showScanDelay = showScanDelay;
    }

    /** Getter for property editScanDelay.
     * @return Value of property editScanDelay.
     *
     */
    public int getEditScanDelay() {
        return editScanDelay;
    }

    /** Setter for property editScanDelay.
     * @param editScanDelay New value of property editScanDelay.
     *
     */
    public void setEditScanDelay(int editScanDelay) {
        if (editScanDelay < 0) {
            editScanDelay = 1000;
        }
        this.editScanDelay = editScanDelay;
    }

    /** Getter for property saveScanDelay.
     * @return Value of property saveScanDelay.
     *
     */
    public int getSaveScanDelay() {
        return saveScanDelay;
    }

    /** Setter for property saveScanDelay.
     * @param saveScanDelay New value of property saveScanDelay.
     *
     */
    public void setSaveScanDelay(int saveScanDelay) {
        if (saveScanDelay < 0) {
            saveScanDelay = 500;
        }
        this.saveScanDelay = saveScanDelay;
    }

    /** Getter for property scanOnShow.
     * @return Value of property scanOnShow.
     *
     */
    public boolean isScanOnShow() {
        return getShowScanDelay() != 0;
    }

    /** Getter for property scanOnEdit.
     * @return Value of property scanOnEdit.
     *
     */
    public boolean isScanOnEdit() {
        return getEditScanDelay() != 0;
    }


    /** Getter for property scanOnSave.
     * @return Value of property scanOnSave.
     *
     */
    public boolean isScanOnSave() {
        return getSaveScanDelay() != 0;
    }

    private File getRegistryFile(boolean create) {
        String loc = System.getProperty("netbeans.user") + // NOI18N
            File.separatorChar + "system" + File.separatorChar + "TaskList" + //NOI18N
            File.separatorChar + "suggestiontype-registry.xml"; // NOI18N
        File file = new File(loc);
        if (create) {
            if (!file.exists()) {
                File parent = file.getParentFile();
                parent.mkdirs();
            }
        }
        return file;
    }

    private static class TypeXMLHandler extends DefaultHandler {
        private boolean parsingDisabled = false;
        private boolean parsingNoConfirm = false;
        private boolean parsingExpanded = false;
        private Set disabled = null;
        private Set noconfirm = null;
        private Set expanded = null;

        private int showScanDelay = DEFAULT_SHOW_SCAN_DELAY;
        private int editScanDelay = DEFAULT_EDIT_SCAN_DELAY;
        private int saveScanDelay = DEFAULT_SAVE_SCAN_DELAY;

        private boolean scanOnShow = DEFAULT_SCAN_ON_SHOW;
        private boolean scanOnEdit = DEFAULT_SCAN_ON_EDIT;
        private boolean scanOnSave = DEFAULT_SCAN_ON_SAVE;


        TypeXMLHandler() {
        }

        public Set getDisabled() {
            return disabled;
        }

        public Set getNoConfirm() {
            return noconfirm;
        }

        public Set getExpanded() {
            return expanded;
        }

        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startElement(String uri, String localName,
                                 String name, Attributes attrs)
            throws SAXException {
            if (name.equals("type")) { // NOI18N
                if (parsingDisabled) {
                    String type = attrs.getValue("id"); // NOI18N
                    if (disabled == null) {
                        disabled = new HashSet(50);
                    }
                    disabled.add(type);
                } else if (parsingNoConfirm) {
                    String id = attrs.getValue("id"); // NOI18N
                    if (noconfirm == null) {
                        noconfirm = new HashSet(50);
                    }
                    SuggestionType type = SuggestionTypes.getDefault().getType(id);
                    noconfirm.add(type);
                } else if (parsingExpanded) {
                    String id = attrs.getValue("id"); // NOI18N
                    if (expanded == null) {
                        expanded = new HashSet(50);
                    }
                    SuggestionType type = SuggestionTypes.getDefault().getType(id);
                    expanded.add(type);
                } else {
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "SuggestionType Registry Parsing Error: " + name + ", " + attrs); // NOI18N
                }
            } else if (name.equals("disabled")) { // NOI18N
                parsingDisabled = true;
            } else if (name.equals("noconfirm")) { // NOI18N
                parsingNoConfirm = true;
            } else if (name.equals("expanded")) { // NOI18N
                parsingExpanded = true;
            } else if (name.equals("scan-preference")) { // NOI18N
                String event = attrs.getValue("event"); // NOI18N
                String enabled = attrs.getValue("enabled"); // NOI18N
                String delay = attrs.getValue("delay"); // NOI18N
                if ((event == null) || (enabled == null) || (delay == null)) {
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "Got scan-preference event="+event+", enabled="+enabled+", "+delay);
                    return;
                }
                boolean on = "on".equals(enabled); // NOI18N
                int interval = -1;
                try {
                    interval = Integer.parseInt(delay);
                } catch (NumberFormatException e) {
                }
                if ("show".equals(event)) { // NOI18N
                    scanOnShow = on;
                    showScanDelay = interval;
                } else if ("save".equals(event)) { // NOI18N
                    scanOnSave = on;
                    saveScanDelay = interval;
                } else if ("edit".equals(event)) { // NOI18N
                    scanOnEdit = on;
                    editScanDelay = interval;
                }
            }
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            if (name.equals("disabled")) { // NOI18N
                parsingDisabled = false;
            } else if (name.equals("noconfirm")) { // NOI18N
                parsingNoConfirm = false;
            } else if (name.equals("expanded")) { // NOI18N
                parsingExpanded = false;
            }

        }



        public int getShowScanDelay() {
            return showScanDelay;
        }
        public int getEditScanDelay() {
            return editScanDelay;
        }
        public int getSaveScanDelay() {
            return saveScanDelay;
        }
        public boolean isScanOnShow() {
            return scanOnShow;
        }
        public boolean isScanOnEdit() {
            return scanOnEdit;
        }
        public boolean isScanOnSave() {
            return scanOnSave;
        }


        /** No validation - don't read the DTD. Assume importers won't
            require external entities. */
        public InputSource resolveEntity(String pubid, String sysid) {
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }
    }

    /** Have we read the type registry yet? */
    private boolean registryRead = false;

    /** Read in the SuggestionType registry preferences.
     * @return True iff the registry was completely initialized without error
     */
    private boolean readTypeRegistry() {
        if (registryRead) {
            return true;
        }
        registryRead = true;
        File file = getRegistryFile(false);
        if (file.exists()) {
            try {
                Reader fileReader = new BufferedReader(new FileReader(file));
                try {
                    XMLReader reader = XMLUtil.createXMLReader(false);

                    TypeXMLHandler handler = new TypeXMLHandler();
                    reader.setContentHandler(handler);
                    reader.setErrorHandler(handler);
                    reader.setEntityResolver(handler);
                    reader.parse(new InputSource(fileReader));
                    disabled = handler.getDisabled();
                    noconfirm = handler.getNoConfirm();
                    expandedTypes = handler.getExpanded();
                    showScanDelay = handler.getShowScanDelay();
                    editScanDelay = handler.getEditScanDelay();
                    saveScanDelay = handler.getSaveScanDelay();
                    return true;
                } catch (SAXException e) {
                    ErrorManager.getDefault().notify(
                                               ErrorManager.INFORMATIONAL, e);
                }
                fileReader.close();
            } catch (Exception e) {
                ErrorManager.getDefault().notify(
                                               ErrorManager.INFORMATIONAL, e);
            }
        }
        return false;
    }

    public synchronized boolean isEnabled(String id) {
        if (disabled == null) {
            readTypeRegistry();
            if (disabled == null) {
                disabled = new HashSet(40);
            }
        }
        return !disabled.contains(id);
    }

    /** Map containing names of Suggestion Types that have been disabled
     * by the user. */
    private Set disabled = null;

    /** Write out the SuggestionType registry preferences.
     * @param view The current view that we're focused on (used to
     *     persist type expansion state)
     * @return True iff the registry was completely written out without error
     */
    boolean writeTypeRegistry()  {
        File file = getRegistryFile(true);
	try {
            Writer writer = new BufferedWriter(new FileWriter(file));
            writer.write("<?xml version=\"1.0\"?>\n"); // NOI18N
            writer.write("<!DOCTYPE suggestionregistry PUBLIC '-//NetBeans//DTD suggestion registry 1.0//EN' 'http://www.netbeans.org/dtds/suggestion-registry-1_0.dtd'>\n"); // NOI18N
            writer.write("<typeregistry>\n"); // NOI18N
            Iterator it;
            if (disabled != null) {
                it = disabled.iterator();
                if (it.hasNext()) {
                    writer.write("  <disabled>\n"); // NOI18N
                    while (it.hasNext()) {
                        String typeName = (String)it.next();
                        writer.write("    <type id=\""); // NOI18N
                        writer.write(typeName);
                        writer.write("\"/>\n"); // NOI18N
                    }
                    writer.write("  </disabled>\n"); // NOI18N
                }
            }

            if (noconfirm != null) {
                it = noconfirm.iterator();
                if (it.hasNext()) {
                    writer.write("  <noconfirm>\n"); // NOI18N
                    while (it.hasNext()) {
                        SuggestionType type = (SuggestionType)it.next();
                        writer.write("    <type id=\""); // NOI18N
                        writer.write(type.getName());
                        writer.write("\"/>\n"); // NOI18N
                    }
                    writer.write("  </noconfirm>\n"); // NOI18N
                }
            }

            // Write node-expansion settings
            if (expandedTypes != null) {
                it = expandedTypes.iterator();
                if (it.hasNext()) {
                    writer.write("  <expanded>\n"); // NOI18N
                    while (it.hasNext()) {
                        SuggestionType type = (SuggestionType)it.next();
                        writer.write("    <type id=\""); // NOI18N
                        writer.write(type.getName());
                        writer.write("\"/>\n"); // NOI18N
                    }
                    writer.write("  </expanded>\n"); // NOI18N
                }
            }

            // Write out the scanning preferences (if different
            // from the default)
            if ((isScanOnShow() != DEFAULT_SCAN_ON_SHOW) ||
                (showScanDelay != DEFAULT_SHOW_SCAN_DELAY)) {
                writer.write("  <scan-preference event=\"show\" enabled=\""); // NOI18N
                writer.write(isScanOnShow() ? "on" : "off"); // NOI18N
                writer.write("\" delay=\""); // NOI18N
                writer.write(Integer.toString(showScanDelay));
                writer.write("\"/>\n"); // NOI18N
            }
            if ((isScanOnEdit() != DEFAULT_SCAN_ON_EDIT) ||
                (editScanDelay != DEFAULT_EDIT_SCAN_DELAY)) {
                writer.write("  <scan-preference event=\"edit\" enabled=\""); // NOI18N
                writer.write(isScanOnEdit() ? "on" : "off"); // NOI18N
                writer.write("\" delay=\""); // NOI18N
                writer.write(Integer.toString(editScanDelay));
                writer.write("\"/>\n"); // NOI18N
            }
            if ((isScanOnSave() != DEFAULT_SCAN_ON_SAVE) ||
                (saveScanDelay != DEFAULT_SAVE_SCAN_DELAY)) {
                writer.write("  <scan-preference event=\"save\" enabled=\""); // NOI18N
                writer.write(isScanOnSave() ? "on" : "off"); // NOI18N
                writer.write("\" delay=\""); // NOI18N
                writer.write(Integer.toString(saveScanDelay));
                writer.write("\"/>\n"); // NOI18N
            }

            writer.write("</typeregistry>\n"); // NOI18N
            writer.close();
            return true;
        } catch (Exception e) {
            ErrorManager.getDefault().notify(
                                           ErrorManager.INFORMATIONAL, e);
        }
        return false;
    }

    public synchronized void setEnabled(String id, boolean enabled) {
        if (disabled == null) {
            disabled = new HashSet(40);
        }

        if (enabled) {
            disabled.remove(id);
            // Have EditTypes... gui now : setConfirm(type, true, false);
        } else {
            disabled.add(id);
        }
    }

    public synchronized boolean isConfirm(SuggestionType type) {
        if (noconfirm == null) {
            readTypeRegistry();
            if (noconfirm == null) {
                noconfirm = new HashSet(40);
            }
        }
        return !noconfirm.contains(type);
    }


    /** Map containing names of Suggestion Types that the user wants to
     * fix without a confirmation dialog */
    private Set noconfirm = null;

    public synchronized void setConfirm(SuggestionType type, boolean confirm) {
        if (noconfirm == null) {
            noconfirm = new HashSet(40);
        }

        if (confirm) {
            noconfirm.remove(type);
        } else {
            noconfirm.add(type);
        }
    }

    /** List of SuggestionTypes that should be expanded */
    private Set expandedTypes = null;

    public boolean isExpandedType(SuggestionType type) {
        readTypeRegistry();
        if (expandedTypes == null) {
            // Special case: default parse errors to expanded
            return (type.getName() == "nb-java-errors"); // NOI18N
        }
        return expandedTypes.contains(type);
    }


    public void setExpandedType(SuggestionType type, boolean expanded) {
        readTypeRegistry();
        if (expandedTypes == null) {
            expandedTypes = new HashSet(2*SuggestionTypes.getDefault().getCount());
            // Ensure that we default to showing java compilation errors
            // expanded
            SuggestionType jc =
                SuggestionTypes.getDefault().getType("nb-java-errors"); // NOI18N
            if (jc != null) {
                expandedTypes.add(jc);
            }
        }
        if (expanded) {
            expandedTypes.add(type);
        } else {
            expandedTypes.remove(type);
        }
    }

    // XXX Node.Handle gets special support from InstanceDataObject
    // it takes effect once customized using tools: options
    public Node getNode() throws IOException {
        try {
            Node node = new BeanNode(getDefault());
            return node;
        } catch (IntrospectionException e) {
            IOException io = new IOException();
            io.initCause(e);
            throw io;
        }
    }

}
