/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.scala.editing.options;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.EditorKit;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.SimpleValueNames;

import org.netbeans.modules.options.editor.spi.PreferencesCustomizer;
import org.netbeans.modules.options.editor.spi.PreviewProvider;
import org.netbeans.modules.scala.editing.ScalaFormatter;
import org.netbeans.modules.scala.editing.ScalaMimeResolver;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Exceptions;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Caoyuan Deng
 * 
 * @todo Add an RHTML options category, such that I can see the effects of
 *   switching the RHTML toggles?
 */
public class FmtOptions {

    public static final String expandTabToSpaces = SimpleValueNames.EXPAND_TABS;
    public static final String tabSize = SimpleValueNames.TAB_SIZE;
    public static final String spacesPerTab = SimpleValueNames.SPACES_PER_TAB;
    public static final String indentSize = SimpleValueNames.INDENT_SHIFT_WIDTH;
    public static final String rightMargin = SimpleValueNames.TEXT_LIMIT_WIDTH; //NOI18N
    public static final String continuationIndentSize = "continuationIndentSize"; //NOI18N
    public static final String reformatComments = "reformatComments"; //NOI18N
    public static final String indentHtml = "indentHtml"; //NOI18N
    public static CodeStyleProducer codeStyleProducer;
    public static Preferences lastValues;
    private static Class<? extends EditorKit> kitClass;
    private static final String DEFAULT_PROFILE = "default"; // NOI18N

    private FmtOptions() {
    }

    public static int getDefaultAsInt(String key) {
        return Integer.parseInt(defaults.get(key));
    }

    public static boolean getDefaultAsBoolean(String key) {
        return Boolean.parseBoolean(defaults.get(key));
    }

    public static String getDefaultAsString(String key) {
        return defaults.get(key);
    }

    public static Preferences getPreferences() {
        return MimeLookup.getLookup(ScalaMimeResolver.MIME_TYPE).lookup(Preferences.class);
    }

    public static boolean getGlobalExpandTabToSpaces() {
        return getPreferences().getBoolean(SimpleValueNames.EXPAND_TABS, getDefaultAsBoolean(SimpleValueNames.EXPAND_TABS));
    }

    public static int getGlobalTabSize() {
        return getPreferences().getInt(SimpleValueNames.TAB_SIZE, getDefaultAsInt(SimpleValueNames.TAB_SIZE));
    }

    // Ruby needs its own indent size; the global "4" isn't a good match
    //    public static int getGlobalIndentSize() {
    //        org.netbeans.editor.Formatter f = (org.netbeans.editor.Formatter)Settings.getValue(getKitClass(), "formatter");
    //        if (f != null)
    //            return f.getShiftWidth();
    //        return getDefaultAsInt(indentSize);
    //    }
    public static int getGlobalRightMargin() {
        return getPreferences().getInt(SimpleValueNames.TEXT_LIMIT_WIDTH, getDefaultAsInt(SimpleValueNames.TEXT_LIMIT_WIDTH));
    }

    public static Class<? extends EditorKit> getKitClass() {
        if (kitClass == null) {
            EditorKit kit = MimeLookup.getLookup(MimePath.get(ScalaMimeResolver.MIME_TYPE)).lookup(EditorKit.class); //NOI18N
            kitClass = kit != null ? kit.getClass() : EditorKit.class;
        }
        return kitClass;
    }

    public static void flush() {
        try {
            getPreferences().flush();
        } catch (BackingStoreException e) {
            Exceptions.printStackTrace(e);
        }
    }

    public static String getCurrentProfileId() {
        return DEFAULT_PROFILE;
    }

    public static CodeStyle createCodeStyle(Preferences p) {
        CodeStyle.getDefault(null);
        return codeStyleProducer.create(p);
    }

    public static boolean isInteger(String optionID) {
        String value = defaults.get(optionID);

        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    public static String getLastValue(String optionID) {
        Preferences p = lastValues == null ? getPreferences() : lastValues;
        return p.get(optionID, getDefaultAsString(optionID));
    }
    // Private section ---------------------------------------------------------
    private static final String TRUE = "true";      // NOI18N
    private static final String FALSE = "false";    // NOI18N
    private static Map<String, String> defaults;
    

    static {
        createDefaults();
    }

    private static void createDefaults() {
        String defaultValues[][] = {
            {SimpleValueNames.TEXT_LIMIT_WIDTH, "80"}, //NOI18N
            {SimpleValueNames.EXPAND_TABS, TRUE}, //NOI18N
            {SimpleValueNames.TAB_SIZE, "2"}, //NOI18N
            {SimpleValueNames.INDENT_SHIFT_WIDTH, "2"}, //NOI18N
            {continuationIndentSize, "2"}, //NOI18N
            {reformatComments, FALSE}, //NOI18N
            {indentHtml, TRUE}, //NOI18N
        };

        defaults = new HashMap<String, String>();

        for (java.lang.String[] strings : defaultValues) {
            defaults.put(strings[0], strings[1]);
        }

    }

    // Support section ---------------------------------------------------------
    public static class CategorySupport implements ActionListener, DocumentListener, PreviewProvider, PreferencesCustomizer {

        public static final String OPTION_ID = "org.netbeans.modules.scala.editing.options.FormattingOptions.ID";
        private static final int LOAD = 0;
        private static final int STORE = 1;
        private static final int ADD_LISTENERS = 2;
        private String previewText = NbBundle.getMessage(FmtOptions.class, "SAMPLE_Default");
        private String forcedOptions[][];
        private boolean changed = false;
        private JPanel panel;
        private final Preferences preferences;
        private final Preferences previewPrefs;
        private final String id;
        private final List<JComponent> components = new LinkedList<JComponent>();
        private JEditorPane previewPane;


        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

        protected CategorySupport(Preferences preferences, String id, JPanel panel, String previewText, String[]... forcedOptions) {
            this.preferences = preferences;
            this.id = id;
            this.panel = panel;
            this.previewText = previewText != null ? previewText : NbBundle.getMessage(FmtOptions.class, "SAMPLE_Default"); //NOI18N

            // Scan the panel for its components
            scan(panel, components);

            // Initialize the preview preferences
            Preferences forcedPrefs = new PreviewPreferences();
            for (String[] option : forcedOptions) {
                forcedPrefs.put( option[0], option[1]);
            }
            this.previewPrefs = new ProxyPreferences(preferences, forcedPrefs);

            // Load and hook up all the components
            loadFrom(preferences);
            addListeners();
        }

        protected void addListeners() {
            scan(ADD_LISTENERS, null);
        }

        protected void loadFrom(Preferences preferences) {
//            loaded = true;
            scan(LOAD, preferences);
//            loaded = false;
        }
//
//        public void applyChanges() {
//            storeTo(preferences);
//        }
//
        protected void storeTo(Preferences p) {
            scan(STORE, p);
        }

        protected void notifyChanged() {
//            if (loaded)
//                return;
            storeTo(preferences);
            refreshPreview();
        }


        public JComponent getPreviewComponent() {
            if (previewPane == null) {
                previewPane = new JEditorPane();
                previewPane.getAccessibleContext().setAccessibleName(NbBundle.getMessage(FmtOptions.class, "AN_Preview")); //NOI18N
                previewPane.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(FmtOptions.class, "AD_Preview")); //NOI18N
                previewPane.putClientProperty("HighlightsLayerIncludes", "^org\\.netbeans\\.modules\\.editor\\.lib2\\.highlighting\\.SyntaxHighlighting$"); //NOI18N
                previewPane.setEditorKit(CloneableEditorSupport.getEditorKit("text/x-scala"));
                previewPane.setEditable(false);
            }
            return previewPane;
        }

        public void refreshPreview() {
            JEditorPane jep = (JEditorPane) getPreviewComponent();
            try {
                int rm = previewPrefs.getInt(SimpleValueNames.TEXT_LIMIT_WIDTH, getDefaultAsInt(SimpleValueNames.TEXT_LIMIT_WIDTH));
                jep.putClientProperty("TextLimitLine", rm); //NOI18N
            }
            catch( NumberFormatException e ) {
                // Ignore it
            }

            int rm = 30;
            try {
                rm = previewPrefs.getInt(SimpleValueNames.TEXT_LIMIT_WIDTH, getDefaultAsInt(SimpleValueNames.TEXT_LIMIT_WIDTH));

                // Estimate text line in preview pane

                JComponent pc = previewPane;
                if (previewPane.getParent() instanceof JViewport) {
                    pc = (JViewport)previewPane.getParent();
                }
                Font font = pc.getFont();
                FontMetrics metrics = pc.getFontMetrics(font);
                int cw = metrics.charWidth('x');
                if (cw > 0) {
                    int nrm = pc.getWidth() / cw;
                    if (nrm > 3) {
                        rm = nrm-2;
                    }
                }

                //pane.putClientProperty("TextLimitLine", rm); // NOI18N
            }
            catch( NumberFormatException e ) {
                // Ignore it
            }

            jep.setIgnoreRepaint(true);
            jep.setText(previewText);

            CodeStyle codeStyle = CodeStyle.get(previewPrefs);
            ScalaFormatter formatter = new ScalaFormatter(codeStyle, rm);
            formatter.reindent(null, jep.getDocument(), 0, jep.getDocument().getLength(), null, false);

            jep.setIgnoreRepaint(false);
            jep.scrollRectToVisible(new Rectangle(0,0,10,10) );
            jep.repaint(100);
        }



        void changed() {
            if (!changed) {
                changed = true;
                pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
            }
            pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
        }

        // ActionListener implementation ---------------------------------------
        public void actionPerformed(ActionEvent e) {
            changed();
        }

        // DocumentListener implementation -------------------------------------
        public void insertUpdate(DocumentEvent e) {
            changed();
        }

        public void removeUpdate(DocumentEvent e) {
            changed();
        }

        public void changedUpdate(DocumentEvent e) {
            changed();
        }

        // Private methods -----------------------------------------------------

        private void performOperation(int operation, JComponent jc, String optionID, Preferences p) {
            switch(operation) {
            case LOAD:
                loadData(jc, optionID, p);
                break;
            case STORE:
                storeData(jc, optionID, p);
                break;
            case ADD_LISTENERS:
                addListener(jc);
                break;
            }
        }

        private void scan(int what, Preferences p ) {
            for (JComponent jc : components) {
                Object o = jc.getClientProperty(OPTION_ID);
                if (o instanceof String) {
                    performOperation(what, jc, (String)o, p);
                } else if (o instanceof String[]) {
                    for(String oid : (String[])o) {
                        performOperation(what, jc, oid, p);
                    }
                }
            }
        }

        private void scan(Container container, List<JComponent> components) {
            for (Component c : container.getComponents()) {
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent)c;
                    Object o = jc.getClientProperty(OPTION_ID);
                    if (o instanceof String || o instanceof String[])
                        components.add(jc);
                }
                if (c instanceof Container)
                    scan((Container)c, components);
            }
        }

        /** Very smart method which tries to set the values in the components correctly
         */
        private void loadData( JComponent jc, String optionID, Preferences node ) {

            if ( jc instanceof JTextField ) {
                JTextField field = (JTextField)jc;
                field.setText( node.get(optionID, getDefaultAsString(optionID)) );
            }
            else if ( jc instanceof JCheckBox ) {
                JCheckBox checkBox = (JCheckBox)jc;
                boolean df = getDefaultAsBoolean(optionID);
                checkBox.setSelected( node.getBoolean(optionID, df));
            }
            else if ( jc instanceof JComboBox) {
                JComboBox cb  = (JComboBox)jc;
                String value = node.get(optionID, getDefaultAsString(optionID) );
                ComboBoxModel model = createModel(value);
                cb.setModel(model);
                ComboItem item = whichItem(value, model);
                cb.setSelectedItem(item);
            }

        }

        private void storeData( JComponent jc, String optionID, Preferences node ) {

            if ( jc instanceof JTextField ) {
                JTextField field = (JTextField)jc;

                String text = field.getText();

                // XXX test for numbers
                if ( isInteger(optionID) ) {
                    try {
                        int i = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        return;
                    }
                }

                // XXX: watch out, tabSize, spacesPerTab, indentSize and expandTabToSpaces
                // fall back on getGlopalXXX() values and not getDefaultAsXXX value,
                // which is why we must not remove them. Proper solution would be to
                // store formatting preferences to MimeLookup and not use NbPreferences.
                // The problem currently is that MimeLookup based Preferences do not support subnodes.
                if (!optionID.equals(tabSize) &&
                    !optionID.equals(spacesPerTab) && !optionID.equals(indentSize) &&
                    getDefaultAsString(optionID).equals(text)
                ) {
                    node.remove(optionID);
                } else {
                    node.put(optionID, text);
                }
            }
            else if ( jc instanceof JCheckBox ) {
                JCheckBox checkBox = (JCheckBox)jc;
                if (!optionID.equals(expandTabToSpaces) && getDefaultAsBoolean(optionID) == checkBox.isSelected())
                    node.remove(optionID);
                else
                    node.putBoolean(optionID, checkBox.isSelected());
            }
            else if ( jc instanceof JComboBox) {
                JComboBox cb  = (JComboBox)jc;
                // Logger.global.info( cb.getSelectedItem() + " " + optionID);
                String value = ((ComboItem) cb.getSelectedItem()).value;
                if (getDefaultAsString(optionID).equals(value))
                    node.remove(optionID);
                else
                    node.put(optionID,value);
            }
        }

        private void addListener( JComponent jc ) {
            if ( jc instanceof JTextField ) {
                JTextField field = (JTextField)jc;
                field.addActionListener(this);
                field.getDocument().addDocumentListener(this);
            }
            else if ( jc instanceof JCheckBox ) {
                JCheckBox checkBox = (JCheckBox)jc;
                checkBox.addActionListener(this);
            }
            else if ( jc instanceof JComboBox) {
                JComboBox cb  = (JComboBox)jc;
                cb.addActionListener(this);
            }
        }


        private ComboBoxModel createModel( String value ) {

//            // is it braces placement?
//            for (ComboItem comboItem : bracePlacement) {
//                if ( value.equals( comboItem.value) ) {
//                    return new DefaultComboBoxModel( bracePlacement );
//                }
//            }
//
//            // is it braces generation?
//            for (ComboItem comboItem : bracesGeneration) {
//                if ( value.equals( comboItem.value) ) {
//                    return new DefaultComboBoxModel( bracesGeneration );
//                }
//            }
//
//            // is it wrap
//            for (ComboItem comboItem : wrap) {
//                if ( value.equals( comboItem.value) ) {
//                    return new DefaultComboBoxModel( wrap );
//                }
//            }

            return null;
        }

        private static ComboItem whichItem(String value, ComboBoxModel model) {

            for (int i = 0; i < model.getSize(); i++) {
                ComboItem item = (ComboItem)model.getElementAt(i);
                if ( value.equals(item.value)) {
                    return item;
                }
            }
            return null;
        }

        // PreferencesCustomizer implementation --------------------------------

        public JComponent getComponent() {
            return panel;
        }

        public String getDisplayName() {
            return panel.getName();
        }

        public String getId() {
            return id;
        }

        public HelpCtx getHelpCtx() {
            return null;
        }


        private static class ComboItem {

            String value;
            String displayName;

            public ComboItem(String value, String key) {
                this.value = value;
                this.displayName = NbBundle.getMessage(FmtOptions.class, key);
            }

            @Override
            public String toString() {
                return displayName;
            }
        }
    }

    // PreferencesCustomizer.Factory implementation ------------------------

    public static final class Factory implements PreferencesCustomizer.Factory {

        private final String id;
        private final Class<? extends JPanel> panelClass;
        private final String previewText;
        private final String[][] forcedOptions;

        public Factory(String id, Class<? extends JPanel> panelClass, String previewText, String[]... forcedOptions) {
            this.id = id;
            this.panelClass = panelClass;
            this.previewText = previewText;
            this.forcedOptions = forcedOptions;
        }

        public PreferencesCustomizer create(Preferences preferences) {
            try {
                return new CategorySupport(preferences, id, panelClass.newInstance(), previewText, forcedOptions);
            } catch (Exception e) {
                return null;
            }
        }
    } // End of CategorySupport.Factory class

    public static interface CodeStyleProducer {

        public CodeStyle create(Preferences preferences);
    }

    public static class PreviewPreferences extends AbstractPreferences {

        private Map<String,Object> map = new HashMap<String, Object>();

        public PreviewPreferences() {
            super(null, ""); // NOI18N
        }

        protected void putSpi(String key, String value) {
            map.put(key, value);
        }

        protected String getSpi(String key) {
            return (String)map.get(key);
        }

        protected void removeSpi(String key) {
            map.remove(key);
        }

        protected void removeNodeSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected String[] keysSpi() throws BackingStoreException {
            String array[] = new String[map.keySet().size()];
            return map.keySet().toArray( array );
        }

        protected String[] childrenNamesSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected AbstractPreferences childSpi(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected void syncSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected void flushSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    // read-only, no subnodes
    public static final class ProxyPreferences extends AbstractPreferences {

        private final Preferences[] delegates;

        public ProxyPreferences(Preferences... delegates) {
            super(null, ""); // NOI18N
            this.delegates = delegates;
        }

        protected void putSpi(String key, String value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected String getSpi(String key) {
            for(Preferences p : delegates) {
                String value = p.get(key, null);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }

        protected void removeSpi(String key) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected void removeNodeSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected String[] keysSpi() throws BackingStoreException {
            Set<String> keys = new HashSet<String>();
            for(Preferences p : delegates) {
                keys.addAll(Arrays.asList(p.keys()));
            }
            return keys.toArray(new String[ keys.size() ]);
        }

        protected String[] childrenNamesSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected AbstractPreferences childSpi(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected void syncSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected void flushSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    } // End of ProxyPreferences class

}
