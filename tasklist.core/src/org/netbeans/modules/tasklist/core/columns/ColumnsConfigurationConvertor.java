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

package org.netbeans.modules.tasklist.core.columns;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.settings.Convertor;
import org.netbeans.spi.settings.DOMConvertor;
import org.netbeans.spi.settings.Saver;
import org.openide.ErrorManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Convertor for column widths in XML format.
 * Public ID: "-//NetBeans org.netbeans.modules.tasklist//DTD Column Widths 1.0//EN"
 *
 * @author Tim Lebedkov
 */
public final class ColumnsConfigurationConvertor extends DOMConvertor
implements ChangeListener {
    /**
     * Creates a converter for the specified FO
     *
     * @param fo an XML FO with Column Widths DTD
     */
    private static Object create(org.openide.filesystems.FileObject fo) {
        ColumnsConfigurationConvertor conv = new ColumnsConfigurationConvertor();
        return conv;
    }
    
    private Saver saver;
    
    /**
     * Creates a new instance
     */
    public ColumnsConfigurationConvertor() {
        super("-//NetBeans org.netbeans.modules.tasklist//DTD Columns 1.0//EN", // NOI18N
            "http://tasklist.netbeans.org/dtd/columns-1_0.dtd", "columns"); // NOI18N
    }
    
    protected Object readElement(Element element) {
        NodeList nl = element.getChildNodes();
        
        ArrayList w = new ArrayList();
        ArrayList n = new ArrayList();
        boolean ascending = true;
        String sortingColumn = null;
        
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element node = (Element) nl.item(i);
            
            n.add(node.getAttribute("property")); // NOI18N
            w.add(node.getAttribute("width")); // NOI18N
            
            if (sortingColumn == null) {
                String sort = node.getAttribute("sort"); // NOI18N
                if (sort.equals("ascending")) { // NOI18N
                    sortingColumn = node.getAttribute("property"); // NOI18N
                    ascending = true;
                } else if (sort.equals("descending")) { // NOI18N
                    sortingColumn = node.getAttribute("property"); // NOI18N
                    ascending = false;
                }
            }
        }
        
        int[] ww = new int[w.size()];
        for (int i = 0; i < w.size(); i++) {
            try {
                ww[i] = Integer.parseInt((String) w.get(i));
            } catch (NumberFormatException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        String[] nn = (String[]) n.toArray(new String[n.size()]);
        
        return new ColumnsConfiguration(nn, ww, sortingColumn, ascending);
    }

    protected void writeElement(Document doc, Element element, Object obj) {
        ColumnsConfiguration cw = (ColumnsConfiguration) obj;
        int[] w = cw.getWidths();
        String[] n = cw.getProperties();
        String sortingColumn = cw.getSortingColumn();
        boolean ascending = cw.getSortingOrder();
        for (int i = 0; i < n.length; i++) {
            Element col = doc.createElement("column"); // NOI18N
            col.setAttribute("property", n[i]); // NOI18N
            col.setAttribute("width", String.valueOf(w[i])); // NOI18N
            if (n[i].equals(sortingColumn)) {
                col.setAttribute("sort", ascending ? "ascending" : "descending"); // NOI18N
            }
            element.appendChild(col);
        }
    }

    public void registerSaver(Object inst, Saver s) {
        this.saver = s;
        ((ColumnsConfiguration) inst).addChangeListener(this);
    }

    public void unregisterSaver(Object inst, Saver s) {
        if (s == null || s != saver) {
            ErrorManager.getDefault().notify(ErrorManager.ERROR, 
                new IllegalArgumentException(
                    "Wrong argument for unregisterSaver(Object=" + inst + // NOI18N
                        ", Saver=" + s + ")")); // NOI18N
        }
        this.saver = null;
        ((ColumnsConfiguration) inst).removeChangeListener(this);
    }

    public void stateChanged(ChangeEvent ev) {
        try {
            saver.requestSave();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
}

