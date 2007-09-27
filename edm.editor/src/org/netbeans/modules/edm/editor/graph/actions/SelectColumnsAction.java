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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.openide.windows.WindowManager;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.sql.framework.model.SQLObject;
import org.netbeans.modules.sql.framework.ui.view.TableColumnNode;
import org.netbeans.modules.sql.framework.model.SQLDBColumn;
import org.netbeans.modules.sql.framework.model.SQLDBTable;
import org.netbeans.modules.sql.framework.ui.view.TableColumnTreePanel;

/**
 *
 * @author karthikeyan s
 */
public class SelectColumnsAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    private SQLObject obj;
    
    private MashupGraphManager manager;
    
    /** Creates a new instance of EditJoinAction */
    public SelectColumnsAction(MashupDataObject dObj, SQLObject obj) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.RUNTIMEATTR)));
        mObj = dObj;
        this.manager = dObj.getGraphManager();
        this.obj = obj;
    }
    
    public SelectColumnsAction(MashupDataObject dObj, SQLObject obj, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.RUNTIMEATTR)));
        mObj = dObj;
        this.manager = dObj.getGraphManager();
        this.obj = obj;
    }
    
    public void actionPerformed(ActionEvent e) {        
        List<SQLDBTable> tableList = new ArrayList<SQLDBTable>();
        SQLDBTable dbTable = (SQLDBTable) obj;            
        tableList.add(dbTable);        

        TableColumnTreePanel columnPanel = new TableColumnTreePanel(tableList, true);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        String dlgLabel = "Select columns to display for this table.";
        JLabel lbl = new JLabel(dlgLabel);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        panel.add(lbl, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.bottom = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        panel.add(new JSeparator(), gbc);
        gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(columnPanel, gbc);

        String dlgTitle = "Select Columns";
        int response = JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), panel, dlgTitle, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        boolean userClickedOk = (JOptionPane.OK_OPTION == response);
        if (userClickedOk) {
            List columns = dbTable.getColumnList();
            List tableNodes = columnPanel.getTableColumnNodes();

            boolean wantsReload = false;
            
            Iterator iter = columns.iterator();
            while (iter.hasNext()) {
                SQLDBColumn column = (SQLDBColumn) iter.next();
                boolean userWantsVisible = TableColumnNode.isColumnVisible(column, tableNodes);
                
                if (column.isVisible() && !userWantsVisible) {
                    column.setVisible(false);
                    wantsReload = true;
                } else if (!column.isVisible() && userWantsVisible) {
                    column.setVisible(true);
                    wantsReload = true;
                }
            }
            if(wantsReload) {
                mObj.getMashupDataEditorSupport().synchDocument();
                manager.updateColumnSelection(dbTable);
                manager.setLog("Column selection successfully modified.");
            }
        }    
    }
}