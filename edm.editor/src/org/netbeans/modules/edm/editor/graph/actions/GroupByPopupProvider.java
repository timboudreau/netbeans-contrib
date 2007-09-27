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
import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.sql.framework.model.impl.SQLGroupByImpl;

/**
 * This class implements the popup provider for the group by operator.
 * @author karthikeyan s
 */

public class GroupByPopupProvider implements PopupMenuProvider {
    
    private SQLGroupByImpl grpby;
    
    private MashupDataObject mObj;
    
    /*
     *  Creates an instance of groupby popup provider
     */ 
    public GroupByPopupProvider(SQLGroupByImpl op, MashupDataObject dObj) {
        grpby = op;
        this.mObj = dObj;
    }
    
    /*
     * return the popup menu for this widget type.
     */ 
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        JPopupMenu menu = new JPopupMenu();
        
        // add show sql action.
        JMenuItem showSQL = new JMenuItem("Show SQL");
        showSQL.setAction(new ShowSqlAction(grpby, mObj.getGraphManager(), "Show SQL"));
        menu.add(showSQL);     
        
        menu.addSeparator();
        
        // add edit having condition action.
        JMenuItem editHavingCondition = new JMenuItem("Edit Having Condition");
        editHavingCondition.setAction(new EditHavingConditionAction(mObj, grpby, "Edit Having Condition"));
        menu.add(editHavingCondition);
        
//        // add select column action.
//        JMenuItem selectColumns = new JMenuItem("Select Columns");
//        selectColumns.setAction(new GroupBySelectColumnsAction(mObj,grpby, "Select Columns"));
//        menu.add(selectColumns);        
        
        return menu;
    }
}