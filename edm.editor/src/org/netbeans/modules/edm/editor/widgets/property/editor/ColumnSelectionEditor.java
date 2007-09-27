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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.edm.editor.widgets.property.editor;

import java.awt.Rectangle;
import java.awt.Rectangle;
import java.awt.Rectangle;
import java.awt.Rectangle;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.EnumSet;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.netbeans.api.visual.action.InplaceEditorProvider;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.widgets.EDMPinWidget;
import org.netbeans.modules.sql.framework.model.SQLDBColumn;
import org.netbeans.modules.sql.framework.model.SQLDBTable;
import org.netbeans.modules.sql.framework.model.SQLObject;

/**
 * Column Selection Editor is an Inplace Editor that enables selection of columns.
 * Depending on whether the column is selected/de-selected it gets reflected in the Table Node.
 * 
 * @author Nithya
 */
public class ColumnSelectionEditor implements InplaceEditorProvider {
    
    private MashupDataObject mObj;
    
    public ColumnSelectionEditor(MashupDataObject dObj) {
        mObj = dObj;
    }
    
    public void notifyOpened(EditorController controller, Widget widget, JComponent editor) {        
        if(controller.isEditorVisible()){
            controller.openEditor(widget);
        }
        EDMPinWidget pin = (EDMPinWidget)widget;
        pin.enableEditor();
    }
    
    public void notifyClosing(EditorController controller, Widget widget, JComponent editor, boolean commit) {
        EDMPinWidget pin = (EDMPinWidget)widget;
        pin.disableEditor();
        SQLObject obj = mObj.getGraphManager().mapWidgetToObject(widget);
        mObj.getGraphManager().updateColumnSelection((SQLDBTable)((SQLDBColumn)obj).getParent());
    }
    
    public JComponent createEditorComponent(EditorController controller, final Widget widget) {
        final EDMPinWidget pin = (EDMPinWidget)widget;
        final SQLObject obj = mObj.getGraphManager().mapWidgetToObject(widget);
        String name = (obj != null)? ((SQLDBColumn)obj).getDisplayName() : "";
        final JCheckBox check = pin.getEditor();
        check.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(widget instanceof EDMPinWidget) {
                    if(check.isSelected()) {
                        ((SQLDBColumn)obj).setVisible(true);
                    } else {
                        ((SQLDBColumn)obj).setVisible(false);
                    }                    
                    mObj.getMashupDataEditorSupport().synchDocument();                      
                }
            }
        });
        if(((SQLDBColumn)obj).isVisible()) {
            check.setSelected(true);
        } else {
            check.setSelected(false);
        }
        return check;
    }
    
    public Rectangle getInitialEditorComponentBounds(EditorController controller, 
            Widget widget, JComponent editor, Rectangle viewBounds) {
        return viewBounds.getBounds();
    }
    
    public EnumSet getExpansionDirections(EditorController controller, 
            Widget widget, JComponent editor) {
        return EnumSet.of(InplaceEditorProvider.ExpansionDirection.LEFT, 
                InplaceEditorProvider.ExpansionDirection.RIGHT);
    }    
}