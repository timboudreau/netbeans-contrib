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
