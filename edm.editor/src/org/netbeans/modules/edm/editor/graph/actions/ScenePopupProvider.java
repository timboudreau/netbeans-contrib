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
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;

/**
 * This class implements the popup provider for the scene.
 * @author karthikeyan s
 */

public class ScenePopupProvider implements PopupMenuProvider {
    
    private MashupDataObject mObj;
    
    private MashupGraphManager manager;
    
    public ScenePopupProvider(MashupDataObject dObj, MashupGraphManager manager) {
        mObj = dObj;
        this.manager = manager;
    }
    
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        JPopupMenu menu = new JPopupMenu();
        
        // add auto layout action.
        JMenuItem layout = new JMenuItem("Auto Layout");
        layout.setAction(new AutoLayoutAction(mObj, "Auto Layout"));
        menu.add(layout);
        
         // add Validate action
        JMenuItem validate = new JMenuItem("Validate");
        validate.setAction(new ValidationAction(mObj,"Validate"));
        menu.add(validate);
      
         // add ToggleOutput action
        JMenuItem toggleOutput = new JMenuItem("Toggle Output");
        toggleOutput.setAction(new ShowOutputAction("Toggle Output",mObj));
        menu.add(toggleOutput);
        
        menu.addSeparator();
        
        // add edit join view action.
        JMenuItem edit = new JMenuItem("Edit Join");
        edit.setAction(new EditJoinAction(mObj, "Edit Join"));
        menu.add(edit);
        
        // Edit connection action.
        JMenuItem editDB = new JMenuItem("Edit Database Properties");
        editDB.setAction(new EditConnectionAction(mObj, "Edit Database Properties"));
        menu.add(editDB);
        
        // Edit Runtime input action.
        JMenuItem editRuntime = new JMenuItem("Edit Runtime Input Arguments");
        editRuntime.setAction(new RuntimeInputAction(mObj, "Edit Runtime Input Arguments"));
        menu.add(editRuntime);
        
        // Edit Runtime output action.
        JMenuItem editOutputRuntime = new JMenuItem("Edit Runtime Output Arguments");
        editOutputRuntime.setAction(new RuntimeOutputAction(mObj, "Edit Runtime Output Arguments"));
        menu.add(editOutputRuntime);
        
        menu.addSeparator();
        
        //add Expand All action
        JMenuItem expand = new JMenuItem("Expand All");
        expand.setAction(new ExpandAllAction(mObj, "Expand All"));
        menu.add(expand); 
        
         //add collapse All action
        JMenuItem collapse = new JMenuItem("Collapse All");
        collapse.setAction(new CollapseAllAction(mObj, "Collapse All"));
        menu.add(collapse); 
        // add run action.
        JMenuItem run = new JMenuItem("Run");
        run.setAction(new TestRunAction(mObj, "Run"));
        menu.add(run);       
        
        menu.addSeparator();
        
        // add Properties action.
        JMenuItem properties = new JMenuItem("Properties");
        properties.setAction(new PropertiesAction(mObj, "Properties"));
        menu.add(properties);
        
        return menu;
    }
}