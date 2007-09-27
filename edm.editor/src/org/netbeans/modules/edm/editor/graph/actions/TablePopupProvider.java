/*
 * The contents of this file are subject to the terms of the Common
 * Development
The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 */

package org.netbeans.modules.edm.editor.graph.actions;

import java.awt.Point;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Widget;

import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;

import org.netbeans.modules.sql.framework.model.SQLObject;
import org.netbeans.modules.sql.framework.model.SourceTable;

/**
 * This class implements the popup provider for the table.
 * @author karthikeyan s
 */

public class TablePopupProvider implements PopupMenuProvider {
    
    private SQLObject obj;
    
    private MashupGraphManager manager;
    
    private MashupDataObject mObj;
    
    public TablePopupProvider(SQLObject obj, MashupDataObject dObj) {
        this.obj = obj;
        this.manager = dObj.getGraphManager();
        this.mObj = dObj;
    }
    
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        JPopupMenu menu = new JPopupMenu();
        
        // add show sql action.
        JMenuItem showData = new JMenuItem("Show Data");
        showData.setAction(new ShowDataAction(mObj, obj, "Show Data"));
        menu.add(showData);  
        
        // add show sql action.
        JMenuItem showSQL = new JMenuItem("Show SQL");
        showSQL.setAction(new ShowSqlAction(obj, mObj.getGraphManager(), "Show SQL"));
        menu.add(showSQL);          
        
        menu.addSeparator();
        
        // add select columns action.
        JMenuItem selectColumns = new JMenuItem("Select Columns");
        selectColumns.setAction(new SelectColumnsAction(mObj, obj, "Select Columns"));
        menu.add(selectColumns);   
        
        // add data extraction action
        JMenuItem dataExtraction = new JMenuItem("Filter Condition");
        dataExtraction.setAction(new ExtractionConditionAction(mObj, obj, "Filter Condition"));
        menu.add(dataExtraction);          
        
        menu.addSeparator();
        
        // add remove table action
        JMenuItem remove = new JMenuItem("Remove Table");
        remove.setAction(new RemoveObjectAction(mObj, obj, "Remove Table"));
        menu.add(remove);      
        
        //add AutoMap action
        if (obj instanceof SourceTable) {
            JMenuItem autoMap = new JMenuItem("Auto Map");
            autoMap.setAction(new AutoMapAction(mObj, obj, "AutoMap"));
            menu.add(autoMap);
            
         menu.addSeparator();
                     
            if (mObj.getModel().getSQLDefinition().getTargetTables().size() == 0) {
                autoMap.setEnabled(false);
            }
        }
        return menu;
    }
}