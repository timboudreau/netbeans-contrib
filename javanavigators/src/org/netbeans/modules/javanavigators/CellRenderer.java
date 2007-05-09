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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.javanavigators;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import org.openide.awt.HtmlRenderer;

/**
 *
 * @author Tim
 */
public class CellRenderer implements ListCellRenderer {
    private final HtmlRenderer.Renderer htmlRenderer = 
            HtmlRenderer.createRenderer();
    private final Border aboveBorder = 
            BorderFactory.createMatteBorder (1, 0, 0, 0,
            UIManager.getColor("textText"));
    private final Border belowBorder = 
            BorderFactory.createMatteBorder (0, 0, 1, 0,
            UIManager.getColor("textText"));
    private final Border emptyBorder = 
            BorderFactory.createEmptyBorder();
    
    public CellRenderer() {
    }
    
    static final int BORDER_NONE = 0;
    static final int BORDER_ABOVE = 1;
    static final int BORDER_BELOW = 2;
    
    public Component getListCellRendererComponent(JList list, Object item,
                                                  int index, boolean selected,
                                                  boolean lead) {
        if (dropFeedbackIndex != -1) {
            selected = false;
            lead = false;
        }
        
        if (index == draggingIndex) {
            
        }
        
        htmlRenderer.setHtml(true);
        Component result = htmlRenderer.getListCellRendererComponent(list, 
                item, index, selected, lead);
        
        if (item instanceof Description) {
            Description d = (Description) item;
            htmlRenderer.setIcon(d.icon);
//            ((JComponent)result).setToolTipText(d.javadoc);
        }
        if (index == dropFeedbackIndex) {
            switch (borderMode) {
                case BORDER_NONE :
                    ((JComponent)result).setBorder(emptyBorder);
                    break;
                case BORDER_ABOVE :
                    ((JComponent)result).setBorder(aboveBorder);
                    break;
                case BORDER_BELOW :
                    ((JComponent)result).setBorder(belowBorder);
                    break;
            }
        } else {
            ((JComponent)result).setBorder(emptyBorder);
        }
        return result;
    }
    
    private int dropFeedbackIndex = -1;
    void setDropFeedbackIndex (int val) {
        dropFeedbackIndex = val;
    }
    
    private int borderMode = BORDER_NONE;
    void setBorderMode(int val) {
        borderMode = val;
        assert val >= 0 && val <= 2;
    }
    
    void setDraggingIndex (int val) {
        draggingIndex = val;
    }
    private int draggingIndex = -1;
    
    int getDraggingIndex() {
        return draggingIndex;
    }
}
