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

package org.netbeans.a11y;

import org.netbeans.a11y.ui.AccPropPanel;
import org.netbeans.a11y.ui.AccessibilityPanel;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;



/**
 *  Runs an Accessibility test.
 *
 *  The test is contained in an instance of AccessibilityTestCase. The full
 *  class name is read from a11ytest.test_case or, if that is null, from args[0]
 *  and an instance of this test is created and run.
 *
 *  @author Tristan Bonsall, Marian.Mirilovic@sun.com
 */
public class AccessibilityTestRunner{
    
    private AccessibilityTester tester;
    private AccessibilityPanel accPanel;
    private TestSettings testSettings;
    private String windowTitle;
    
    /** Create new instance.
     */
    public AccessibilityTestRunner(java.awt.Container cont, TestSettings ts, AccessibilityPanel aPanel) {
        testSettings = ts;
        tester = new AccessibilityTester(cont, testSettings);
        accPanel = aPanel;
        windowTitle = testSettings.getWindowTitle();
    }
    
    
    /** Return JSplitPane that contains AWT-tree and AccPropPanel with AWT tree of tested container and appropriate properties.
     * @return  split pane with AWT-tree and AccPropPanel*/
    public JSplitPane getAWTmodel(){
        DefaultMutableTreeNode AWT_model = new DefaultMutableTreeNode("Window : " + windowTitle);
        AWT_model.add(tester.getModel());
        final JTree awtTree = new JTree(AWT_model);
        final JSplitPane AWTtree_splitPane = new javax.swing.JSplitPane();
        final AccPropPanel propertiesPanel = new AccPropPanel();
        JScrollPane jscrollPaneAWTtree = new JScrollPane();
        
        awtTree.addTreeSelectionListener(
                new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)awtTree.getLastSelectedPathComponent();
                
                if ((node == null) || node.isRoot())
                    return;
                
                AccComponent comp = (AccComponent)node.getUserObject();
                propertiesPanel.updatePanel(comp);
            }
        });
        
        jscrollPaneAWTtree.setViewportView(awtTree);
        
        AWTtree_splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        AWTtree_splitPane.setLeftComponent(jscrollPaneAWTtree);
        AWTtree_splitPane.setRightComponent(propertiesPanel);
        
        return AWTtree_splitPane;
    }
    
    /** Test parent = container.
     */
    public void testContainer(){
        if (testSettings.accessibleInterface || testSettings.accessibleProperties || testSettings.test_name) {
            accPanel.setStatusText("Testing Accessibility properties ...");
            tester.testProperties();
            accPanel.setStatusText("Testing Accessibility properties - finished");
        }
        
        if (testSettings.tabTraversal) {
            accPanel.setStatusText("Testing Tab traversal ...");
            boolean tt = tester.testTraversal();
            
            if(!tt)
                accPanel.setStatusText("Impossible test Tab traversal.");
            else
                accPanel.setStatusText("Testing Tab traversal - finished");
            
        }
    }
    
    
    /** Write results : System.out , TXT file or XML file
     * @param resultsFileName results file name
     * @param saveProperties true - save test properties to XML file, false - properties aren't saved
     */
    public void writeResults(String resultsFileName, boolean saveProperties, Writer writer) {
        
        if(writer == null){
            writer = new java.io.PrintWriter(System.out);
        }
        
        if(resultsFileName==null){
            accPanel.setStatusText("Writing to OUT ...");
            ((PrintWriter)writer).println("\n===============================================");
            ((PrintWriter)writer).println(" Tested Window title : "+windowTitle);
            ((PrintWriter)writer).println("===============================================\n");
        }else{
            try{
                accPanel.setStatusText("Writing to file "+resultsFileName+" ...");
                writer = new FileWriter(resultsFileName);
                
                if(saveProperties){
                    String fileNameXML;
                    String set = "_settings.xml";
                    
                    if(resultsFileName.indexOf('.') == -1)
                        fileNameXML = resultsFileName + set;
                    else
                        fileNameXML = resultsFileName.substring(0,resultsFileName.lastIndexOf('.'))+set;
                    
                    Writer settingsWriter = new FileWriter(fileNameXML);
                    TestSettingsLogger settingsLogger = new TestSettingsLogger(tester, testSettings);
                    settingsLogger.getReport(settingsWriter);
                }
            }catch(IOException exc){
                exc.printStackTrace();
                accPanel.setStatusText("Impossible write to file "+resultsFileName+" - writing to ERR ...");
                writer = new PrintWriter(System.err);
            }
        }
        
        if(testSettings.storeToXML){
            XMLReport report = new XMLReport(tester, testSettings);
            report.getReport(writer);
        } else{
            TextReport report = new TextReport(tester, testSettings);
            report.getReport(writer);
        }
        
    }
    
}