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

package org.netbeans.modules.edm.editor.graph.components;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.actions.AddTableAction;
import org.netbeans.modules.edm.editor.graph.actions.RuntimeInputAction;
import org.netbeans.modules.edm.editor.graph.actions.AutoLayoutAction;
import org.netbeans.modules.edm.editor.graph.actions.CollapseAllAction;
import org.netbeans.modules.edm.editor.graph.actions.EditJoinAction;
import org.netbeans.modules.edm.editor.graph.actions.EditConnectionAction;
import org.netbeans.modules.edm.editor.graph.actions.ExpandAllAction;
import org.netbeans.modules.edm.editor.graph.actions.FitToHeightAction;
import org.netbeans.modules.edm.editor.graph.actions.FitToPageAction;
import org.netbeans.modules.edm.editor.graph.actions.FitToWidthAction;
import org.netbeans.modules.edm.editor.graph.actions.TestRunAction;
import org.netbeans.modules.edm.editor.graph.actions.ShowOutputAction;
import org.netbeans.modules.edm.editor.graph.actions.ValidationAction;
import org.netbeans.modules.edm.editor.graph.actions.ZoomInAction;
import org.netbeans.modules.edm.editor.graph.actions.ZoomOutAction;

/**
 *
 * @author karthikeyan s
 */
public class MashupToolbar extends JToolBar {
    
    private MashupDataObject mObj;
    
    /** Creates a new instance of MashupToolbar */
    public MashupToolbar(MashupDataObject dObj) {
        mObj = dObj;
        setRollover(true);
    }
    
    public JToolBar getToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.addSeparator();
        
        // Fit to page button.
        JButton expandButton = new JButton(new ExpandAllAction(mObj));
        expandButton.setToolTipText("Expand All Widgets");
        toolBar.add(expandButton);
        
        // Auto layout button.
        JButton collapseButton = new JButton(new CollapseAllAction(mObj));
        collapseButton.setToolTipText("Collapse All Widgets");
        toolBar.add(collapseButton);
        
        // Show output button.
        JButton outputButton = new JButton(new ShowOutputAction(mObj));
        outputButton.setToolTipText("Toggle Output");
        toolBar.add(outputButton);
        
        JButton addTableButton = new JButton(new AddTableAction(mObj));
        addTableButton.setToolTipText("Add Table");
        toolBar.add(addTableButton);
        
        toolBar.addSeparator();
        
        // Edit join view button.
        JButton editButton = new JButton(new EditJoinAction(mObj));
        editButton.setToolTipText("Edit Join");
        toolBar.add(editButton);
        
        // Edit join view button.
        JButton editDBButton = new JButton(new EditConnectionAction(mObj));
        editDBButton.setToolTipText("Edit Database Properties");
        toolBar.add(editDBButton);
        
        // Runtime input button.
        JButton runtimeInputButton = new JButton(new RuntimeInputAction(mObj));
        runtimeInputButton.setToolTipText("Edit Runtime Input Arguments");
        toolBar.add(runtimeInputButton);
        
        toolBar.addSeparator();
        
            
        // Fit to page button.
        JButton fitButton = new JButton(new FitToPageAction(mObj));
        fitButton.setToolTipText("Fit to Page");
        toolBar.add(fitButton);
        
        // Fit to width button.
        JButton fitToWidthButton = new JButton(new FitToWidthAction(mObj));
        fitToWidthButton.setToolTipText("Fit to Width");
        toolBar.add(fitToWidthButton);
        
        // Fit to page button.
        JButton fitToHeightButton = new JButton(new FitToHeightAction(mObj));
        fitToHeightButton.setToolTipText("Fit to Height");
        toolBar.add(fitToHeightButton);
        
        toolBar.addSeparator();
        
        // Zoom in button.
        JButton zoominButton = new JButton(new ZoomInAction(mObj));
        zoominButton.setToolTipText("Zoom In");
        toolBar.add(zoominButton);
        
        // Zoom in button.
        JButton zoomoutButton = new JButton(new ZoomOutAction(mObj));
        zoomoutButton.setToolTipText("Zoom Out");
        toolBar.add(zoomoutButton);
        
        // Fit to page button.
        JComboBox zoomBox = new ZoomCombo(mObj.getGraphManager());
        zoomBox.setToolTipText("Zoom graph");
        toolBar.add(zoomBox);
        
        toolBar.addSeparator();
              
        // Auto layout button.
        JButton layoutButton = new JButton(new AutoLayoutAction(mObj));
        layoutButton.setToolTipText("Auto Layout");
        toolBar.add(layoutButton);
        
        // Validate button.
        JButton validateButton = new JButton(new ValidationAction(mObj));
        validateButton.setToolTipText("Validate");
        toolBar.add(validateButton);
     
             
        // Run collaboration button.
        JButton runButton = new JButton(new TestRunAction(mObj));
        runButton.setToolTipText("Run");
        toolBar.add(runButton);
        
        toolBar.addSeparator();
            
        return toolBar;
    }
}