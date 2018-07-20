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

package org.netbeans.jellytools.modules.jemmysupport;

/*
 * ComponentGeneratorOperator.java
 *
 * Created on 7/11/02 2:47 PM
 */
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jemmy.ComponentIsNotVisibleException;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Jemmy Component Generator" NbDialog.
 *
 * @author  <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 1.0
 */
public class ComponentGeneratorOperator extends JDialogOperator {

    /** Creates new ComponentGeneratorOperator that can handle it.
     */
    public ComponentGeneratorOperator() {
        super("Jemmy Component Generator");
    }

    private JTreeOperator _treePackage;
    private JButtonOperator _btStart;
    private JButtonOperator _btStop;
    private JButtonOperator _btClose;
    private JCheckBoxOperator _cbCreateScreenshot;
    private JCheckBoxOperator _cbShowComponentsEditor;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator treePackage() {
        if (_treePackage==null) {
            _treePackage = new JTreeOperator(this);
        }
        return _treePackage;
    }

    /** Tries to find "Start" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btStart() {
        if (_btStart==null) {
            _btStart = new JButtonOperator(this, "Start");
        }
        return _btStart;
    }

    /** Tries to find "Stop" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btStop() {
        if (_btStop==null) {
            _btStop = new JButtonOperator(this, "Stop");
        }
        return _btStop;
    }

    /** Tries to find "Close" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btClose() {
        if (_btClose==null) {
            _btClose = new JButtonOperator(this, "Close");
        }
        return _btClose;
    }

    /** Tries to find " Create Screenshot" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCreateScreenshot() {
        if (_cbCreateScreenshot==null) {
            _cbCreateScreenshot = new JCheckBoxOperator(this, "Create Screenshot");
        }
        return _cbCreateScreenshot;
    }

    /** Tries to find " Show Components Editor" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbShowComponentsEditor() {
        if (_cbShowComponentsEditor==null) {
            _cbShowComponentsEditor = new JCheckBoxOperator(this, "Show Component Editor");
        }
        return _cbShowComponentsEditor;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** clicks on "Start" JButton
     */
    public void start() {
        try {
            btStart().push();
        } catch (ComponentIsNotVisibleException ce) {
        } catch (JemmyException e) {
            if (!(e.getInnerException() instanceof ComponentIsNotVisibleException))
                throw e;
        }
    }

    /** clicks on "Stop" JButton
     */
    public void stop() {
        try {
            btStop().push();
        } catch (ComponentIsNotVisibleException ce) {
        } catch (JemmyException e) {
            if (!(e.getInnerException() instanceof ComponentIsNotVisibleException))
                throw e;
        }
    }

    /** clicks on "Close" JButton
     */
    public void close() {
        btClose().push();
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCreateScreenshot(boolean state) {
        if (cbCreateScreenshot().isSelected()!=state) {
            cbCreateScreenshot().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkShowComponentsEditor(boolean state) {
        if (cbShowComponentsEditor().isSelected()!=state) {
            cbShowComponentsEditor().push();
        }
    }
    
    //*****************************************
    // High-level functionality definition part
    //*****************************************

    public void verifyStatus(String status) {
        long t = getTimeouts().getTimeout("ComponentOperator.WaitComponentTimeout");
        getTimeouts().setTimeout("ComponentOperator.WaitComponentTimeout", 20000);
        try {
            new JLabelOperator(this, status);
        } finally {
            getTimeouts().setTimeout("ComponentOperator.WaitComponentTimeout", t);
        }
    }

    /** Performs verification of ComponentGeneratorOperator by accessing all its components.
     */
    public void verify() {
        treePackage();
        btClose();
        cbCreateScreenshot();
        cbShowComponentsEditor();
    }

    public static ComponentGeneratorOperator invoke() {
        new Action("Tools|Jemmy Component Generator", null).performMenu();
        return new ComponentGeneratorOperator();
    }
}

