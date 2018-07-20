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
 * Software is Nokia. Portions Copyright 2003-2005 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.eview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import org.netbeans.api.eview.ControlFactory;
import org.netbeans.api.eview.PanelData;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author David Strupl
 */
public class EViewPanel extends JPanel implements Scrollable {
    
    private static final Logger log = Logger.getLogger(EViewPanel.class.getName());
    private static boolean LOGGABLE = log.isLoggable(Level.FINE);
    
    //~ Static fields/initializers ---------------------------------------------
    
    private static final int SCROLL_INCREMENT = 50;
    
    //~ Instance fields --------------------------------------------------------
    
    /** Map: <string> --> <JLabel> */
    private Map myDescriptionMap;
    
    private int lineCounter = 0;
    private String location;    
    private boolean created;
    
    private Map/*<String, ControlFactory>*/ controls;
    private Map/*<String, JComponent>*/ components;
    private Map/*<String, Object>*/ rememberedValues;
    private PanelDataImpl myPanelData;
    
    private ControlListener controlListener;
    private Map/*<Component, Component>*/ nextFocusableComponent = new HashMap();
    private Map/*<Component, Component>*/ previousFocusableComponent = new HashMap();
    private JComponent firstComp = null;
    private JComponent lastComp = null;
    private List/*<Component>*/ toggleButtons = new ArrayList();
    
    //~ Constructors -----------------------------------------------------------
    
    /**
     * Constructor.
     */
    public EViewPanel(String location) {
        super();
        this.location = location;
        myDescriptionMap = new HashMap();
        controls = new HashMap();
        components = new HashMap();
        
        setName("TEst");
        initComponents();
        // after the start the user did not have a chance to modify anything (yet):
        clearModified();
        setFocusCycleRoot(true);
        setFocusable(true);
        setFocusTraversalPolicy(new FocusTraversalPolicy() {
            public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
                Component res = (Component)nextFocusableComponent.get(aComponent);
                while ((res != null) && (!res.isShowing() || !res.isFocusable())) {
                    res = (Component)nextFocusableComponent.get(res);
                }
                if (res == null) {
                    if (toggleButtons.contains(aComponent)) {
                        int index = toggleButtons.indexOf(aComponent);
                        index++;
                        if (index >= toggleButtons.size()) {
                            return getDefaultComponent(focusCycleRoot);
                        }
                        res = (Component)toggleButtons.get(index);
                    } else {
                        res = (Component)toggleButtons.get(0);
                    }
                }
                return res;
            }

            public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
                Component res = (Component)previousFocusableComponent.get(aComponent);
                while ((res != null) && (!res.isShowing()|| !res.isFocusable())) {
                    res = (Component)previousFocusableComponent.get(res);
                }
                if (res == null) {
                    if (toggleButtons.contains(aComponent)) {
                        int index = toggleButtons.indexOf(aComponent);
                        index--;
                        if (index < 0) {
                            return getLastComponent(focusCycleRoot);
                        }
                        res = (Component)toggleButtons.get(index);
                    } else {
                        res = (Component)toggleButtons.get(toggleButtons.size()-1);
                    }
                }
                return res;
            }

            public Component getFirstComponent(Container focusCycleRoot) {
                return (Component)toggleButtons.get(0);
            }

            public Component getLastComponent(Container focusCycleRoot) {
                Component res = lastComp;
                while ((res != null) && (!res.isShowing()|| !res.isFocusable())) {
                    res = (Component)previousFocusableComponent.get(res);
                }
                if (res == null) {
                    res = (Component)toggleButtons.get(toggleButtons.size()-1);
                }
                return res;
            }

            public Component getDefaultComponent(Container focusCycleRoot) {
                Component res = firstComp;
                while ((res != null) && (!res.isShowing() || !res.isFocusable())) {
                    res = (Component)nextFocusableComponent.get(res);
                }
                if (res == null) {
                    res = (Component)toggleButtons.get(0);
                }
                return res;
            }
            
        });
    }
    
    //~ Methods ----------------------------------------------------------------
    
    /**
     * Label factory method. Creates a new object and stores it in the
     * component map.
     *
     * @param key of the object
     *
     * @return new instance
     */
    private JLabel makeDescriptionLabel(String key, Object keys) {
        JLabel label = new JLabel();
        label.setName(key);
        label.putClientProperty("keys", keys);
        myDescriptionMap.put(key, label);
        
        return label;
    }
    
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateDescriptions();
                clearModified();
            }
        });
    }
    
    /**
     * Builds the UI.
     */
    private void initComponents() {
        
        setLayout(new GridBagLayout());
        putClientProperty("foregroundArea",Boolean.TRUE);
        lineCounter = 0;
        Configuration c = Configuration.getInstance(location);
        Configuration.ContainerEntry cc = c.getConfig();
        JComponent previousComp = null;
        putClientProperty("displayName", cc.displayName);
        if (LOGGABLE) log.fine("EViewPanel.initComponents() cc = " + cc);
        for (Iterator it = cc.entries.iterator(); it.hasNext(); ) {
            Object o = it.next();
            String dName = "Display name not specified";
            if (LOGGABLE) log.fine("EViewPanel.initComponents() o = " + o);
            JPanel jp = null;
            if (o instanceof Configuration.ContainerEntry) {
                Configuration.ContainerEntry ic = (Configuration.ContainerEntry)o;
                dName = ic.displayName;
                jp = new JPanel();
                jp.setFocusable(true);
                jp.setFocusCycleRoot(true);
                ArrayList currentPanelKeys = new ArrayList();
                jp.putClientProperty("keys", currentPanelKeys);
                jp.putClientProperty("labelFormat", ic.labelFormat);
                jp.putClientProperty("foregroundArea",Boolean.TRUE);
                //jp.setBorder(new LineBorder(Color.RED));
                if (LOGGABLE) log.fine("EViewPanel.initComponents() ic = " + ic);
                jp.setLayout(new GridBagLayout());
                int i = 0;
                int j = 0;
                boolean twoLines = false;
                for (Iterator it2 = ic.entries.iterator(); it2.hasNext(); ) {
                    Object o2 = it2.next();
                    if (LOGGABLE) log.fine("EViewPanel.initComponents() o2 = " + o2);
                    if (o2 instanceof Configuration.ControlEntry) {
                        Configuration.ControlEntry cce = (Configuration.ControlEntry)o2;
                        if (cce.control != null) {
                            JComponent comp = cce.control.createComponent();
                            if (cce.label != null) {
                                JLabel jlbl = new JLabel();
                                if (cce.labelBundle != null) {
                                    try {
                                        jlbl.setText(NbBundle.getBundle(cce.labelBundle).getString(cce.label));
                                    } catch (Exception x) {
                                        if (LOGGABLE) log.log(Level.FINE, "", x); // NOI18N
                                        jlbl.setText(cce.label);
                                    }
                                } else {
                                    jlbl.setText(cce.label);
                                }
                                jlbl.setLabelFor(comp);
                                if (LOGGABLE) log.fine("Adding label "+jlbl.getText()+" to (" + j + "," + i + ")");
                                jp.add(jlbl, getLabelConstraints(j, i));
                                //jlbl.setBorder(new LineBorder(Color.YELLOW));
                            }
                            if (cce.labelAboveControl) {
                                i++;
                                twoLines = true;
                            } else {
                                // label left of control
                                j++;
                            }
                            //comp.setBorder(new LineBorder(Color.GREEN));
                            if (LOGGABLE) log.fine("Adding control "+comp+" to (" + j + "," + i + ")");
                            // fix for GridBagLayout resizing bug - this is a hack:
                            Dimension prefSize = comp.getPreferredSize();
                            int prefWidth = 1000;
                            int prefHeight = (int)prefSize.getHeight();
                            if (comp instanceof JScrollPane) {
                                // special hack inside hack for JScrollPanes
                                JScrollPane jsp = (JScrollPane)comp;
                                prefHeight = jsp.getViewport().getView().getPreferredSize().height;
                            }
                            comp.setPreferredSize(new Dimension(prefWidth, prefHeight));
                            // ------
                            // fix for tab navigating:
                            if (firstComp == null) {
                                firstComp = comp;
                            }
                            lastComp = comp;
                            if (previousComp != null) {
                                nextFocusableComponent.put(previousComp, comp);
                                previousFocusableComponent.put(comp, previousComp);
                            }
                            previousComp = comp;
                            // -------
                            jp.add(comp, getControlConstraints(j, i));
                            currentPanelKeys.add(cce.id);
                            controls.put(cce.id, cce.control);
                            components.put(cce.id, comp);
                            cce.control.addPropertyChangeListener(comp,
                                    getControlListener());
                            if (cce.labelAboveControl) {
                                i--;
                            }
                            if (j + 1 < ic.columns * (twoLines ? 1 : 2)) {
                                j++;
                            } else {
                                j = 0;
                                i++;
                                if (twoLines) {
                                    twoLines = false;
                                    i++;
                                }
                            }
                        }
                    }
                }
            }
            if (jp != null) {
                if (LOGGABLE) log.fine("EViewPanel.initComponents() addHideablePanel = " + jp);
                addHideablePanel(jp, dName);
            }
        }
        addSeparator(lineCounter++, new JLabel());
        JPanel pp = new JPanel(new FlowLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.gridx = 0;
        gbc.gridy = ++lineCounter;
        add(pp, gbc);
    }

    private GridBagConstraints getLabelConstraints(int i, int j) {
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = i;
        gridBagConstraints.gridy = j;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        return gridBagConstraints;
    }
    
    private GridBagConstraints getControlConstraints(int i, int j) {
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = i;
        gridBagConstraints.gridy = j;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        return gridBagConstraints;
    }
    
    /**
     * Adds a panel and an expandable handle.
     */
    private void addHideablePanel(JComponent panel, String key, boolean visible) {
        ToggleHideButton button = new ToggleHideButton();
//        button.setToolTipText(
//            NbBundle.getBundle(TTDetails.class).getString(key+"_tooltip"));
        addLine(lineCounter+=2, button, key, panel, key);
        button.setHideablePanel(panel);
        button.setPanelVisible(visible);
        toggleButtons.add(button);
    }
    
    /**
     * Adds a panel and an expandable handle.
     */
    private void addHideablePanel(JComponent panel, String key) {
        addHideablePanel(panel, key, true);
    }
    
    /**
     * Adds a separator labelled with the given label.
     *
     * @param line where to insert the separator
     * @param separatorLabel used to label the separator
     */
    private void addSeparator(int line, JLabel separatorLabel) {
        separatorLabel.setFont(null);
        separatorLabel.setBorder(BorderFactory.createEmptyBorder(8, 1, 4, 1));
        separatorLabel.setBackground(new Color(230, 230, 230));
        separatorLabel.setOpaque(true);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = line;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(separatorLabel, gbc);
    }
    
    
    /**
     * Adds a line. The line consists of following elements:
     *
     * <ul>
     * <li>
     * column 0: optional button
     * </li>
     * <li>
     * column 1: optional content label
     * </li>
     * <li>
     * column 2: optional content data
     * </li>
     * </ul>
     *
     *
     * @param line insertion line
     * @param toggle optional toogle button
     * @param labelText
     * @param data optional
     */
    private void addLine(int line, JComponent toggle, String labelText, JComponent data, String descriptionKey) {
        GridBagConstraints gbc;
        
        // icon
        if(toggle != null) {
            toggle.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = line;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new java.awt.Insets(0, 12, 0, 0);
            add(toggle, gbc);
        }
        
        // label
        if(labelText != null) {
            JLabel label = new JLabel();
            label.setText(labelText);
            label.putClientProperty("forTitle", Boolean.TRUE);
            label.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
//            label.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = line;
            gbc.anchor = GridBagConstraints.WEST;
            add(label, gbc);
        }
        
        // data
        if(data != null) {
            data.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.gridx = 1;
            gbc.gridy = line+1;
            add(data, gbc);
        }
        
        if(descriptionKey != null) {
            JLabel label = makeDescriptionLabel(descriptionKey, data.getClientProperty("keys"));
            label.putClientProperty("labelFormat", data.getClientProperty("labelFormat"));
            label.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
//            label.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
            gbc = new GridBagConstraints();
            gbc.gridx = 2;
            gbc.gridy = line;
            gbc.anchor = GridBagConstraints.WEST;
            add(label, gbc);
        }
    }
    
    /*
     *  (non-Javadoc)
     * @see com.nokia.oss.monfmt.fmthlp.view.EntityDetailsView#getLabel(java.lang.Object)
     */
    private JLabel getDescriptionLabel(Object key) {
        JLabel label = (JLabel)myDescriptionMap.get(key);
        
        if(label == null) {
            throw new IllegalArgumentException(
                "Label_not_defined");
        }
        return label;
    }
    
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
     */
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
     */
    public Dimension getPreferredScrollableViewportSize() {
        Dimension prefSize = getPreferredSize();
        return prefSize;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return SCROLL_INCREMENT;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return SCROLL_INCREMENT;
    }
    
    /**
     *
     */
    public JComponent getComponent(String key) {
        return (JComponent)components.get(key);
    }
    
    /**
     *
     */
    public ControlFactory getControlFactory(String key) {
        return (ControlFactory)controls.get(key);
    }
    
    /**
     * This method will fill the data object with the data from its controls.
     */
    void extractDataFromControls(PanelData data) {
        myPanelData = (PanelDataImpl)data;
        for (Iterator it = controls.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            ControlFactory control = (ControlFactory)controls.get(key);
            JComponent comp = (JComponent)components.get(key);
            Object d = control.getValue(comp);
            if (d != null) {
                ((PanelDataImpl)data).put(key, d);
            }
        }
        updateDescriptions();
    }
    
    void updateControl(String key, Object value) {
        ControlFactory control = (ControlFactory)controls.get(key);
        if (control != null) {
            JComponent comp = (JComponent)components.get(key);
            control.setValue(comp, value);
        }
        updateDescriptions();
    }
    
    void updateDescriptions() {
        for (Iterator it = myDescriptionMap.values().iterator(); it.hasNext();) {
            JLabel l = (JLabel)it.next();
            String res = "";
            List keys = (List)l.getClientProperty("keys");
            String format = (String)l.getClientProperty("labelFormat");
            ArrayList params = new ArrayList();
            if (keys != null) {
                for (Iterator i2 = keys.iterator(); i2.hasNext();) {
                    String s = (String)i2.next();
                    ControlFactory control = (ControlFactory)controls.get(s);
                    JComponent comp = (JComponent)components.get(s);
                    Object val = control.getValue(comp);
                    if (format != null) {
                        params.add(control.convertValueToString(comp, val));
                    } else {
                        if (val != null) {
                            String v = control.convertValueToString(comp, val);
                            if (v != null) {
                                if ("".equals(res)) {
                                    res = v;
                                } else {
                                    res +=  ", " + v;
                                }
                            }
                        }
                    }
                }
            }
            if (format != null) {
                res = MessageFormat.format(format, params.toArray());
            }
            l.setText(res);
        }
    }
    
    
    void clearModified() {
        rememberedValues = new HashMap();
        for (Iterator it = controls.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            ControlFactory control = (ControlFactory)controls.get(key);
            JComponent comp = (JComponent)components.get(key);
            Object data = control.getValue(comp);
            rememberedValues.put(key, data);
        }
        updateDescriptions();
    }
    
    boolean isModified() {
        for (Iterator it = controls.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            ControlFactory control = (ControlFactory)controls.get(key);
            JComponent comp = (JComponent)components.get(key);
            Object data = control.getValue(comp);
            Object rememberedData = rememberedValues.get(key);
            if (! Utilities.compareObjects(data, rememberedData)) {
                log.finer("EViewPanel isModified returning true \ndata == " + data + "\n rememberedData == " + rememberedData);
                return true;
            }
        }
        log.finer("EViewPanel isModified returning false");
        return false;
    }
    
    private ControlListener getControlListener() {
        if (controlListener == null) {
            controlListener = new ControlListener();
        }
        return controlListener;
    }
    
    private class ControlListener implements PropertyChangeListener {
        public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
            myPanelData.setModified(true);
            updateDescriptions();
        }
    }
}
