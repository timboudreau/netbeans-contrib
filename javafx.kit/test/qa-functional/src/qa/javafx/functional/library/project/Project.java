/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package qa.javafx.functional.library.project;

import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import qa.javafx.functional.library.FXTestException;
import qa.javafx.functional.library.Util;

/**
 *
 * @author Alexandr Scherbatiy
 */
public abstract class Project {
    
    String name;
    String location;
    ProjectType type;
    
    /**
     * Creates a new instance of Project
     * @param name 
     * @param type 
     */
    public Project(String name, ProjectType type) {
        this(name, type, null);
    }

    /**
     * 
     * @param name 
     * @param type 
     * @param location 
     */
    public Project(String name, ProjectType type, String location) {
        this.name = name;
        this.type = type;
        
        this.location = (location == null) ?  Util.WORK_DIR : location;
    }
    
    
    /**
     * 
     * @return 
     */
    public String  getName(){
        return name;
    }
    
    /**
     * 
     * @return 
     */
    public ProjectType getType(){
        return type;
    }
    
    /**
     * 
     * @return 
     */
    public String  getLocation(){
        return location;
    }

    public abstract Node getProjectNode();
    
    
    public static void openProject(String projectPath){
        JMenuBarOperator menuBar = new JMenuBarOperator(MainWindowOperator.getDefault());
        
        menuBar.pushMenuNoBlock("File|Open Project...");
        
        JDialogOperator dialog = new JDialogOperator("Open Project");
        
        JTextFieldOperator textField = new JTextFieldOperator(dialog, 1);
        textField.setText(projectPath);
        
        JButtonOperator button = new JButtonOperator(dialog, "Open Project");
        try {
            button.waitComponentEnabled();
        } catch (InterruptedException ex) {
            throw new FXTestException("Open project button is disabled");
        }
        button.pushNoBlock();
        dialog.waitClosed();
        try{Thread.sleep(100);}catch(Exception ex){}
        JDialogOperator dlg=null;
        try
        {
            dlg=new JDialogOperator("Opening Project");
        }
        catch(Exception ex)
        {
            
        }
        if(dlg!=null)
        {
            dlg.waitClosed();
        }
        //Utils.waitScanningClassPath();
        
    }
    
}
