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

import java.util.HashSet;
import java.util.StringTokenizer;

/** This class only allow use (set, read, write) test settings for UIAccessibilityTester
 * @author  Marian.Mirilovic@Sun.com
 */
public class TestSettings {

    private boolean readedCorrectly = true;

    private String windowTitle;

    public boolean accessibleInterface;
    public boolean AI_showingOnly;

    public boolean accessibleProperties;
    public boolean AP_showingOnly;
    public boolean AP_focusTraversableOnly;
    public boolean AP_accessibleName;
    public boolean AP_accessibleDescription;
    public boolean AP_labelForSet;
    public boolean AP_noLabelFor;
    public boolean AP_nlf_text;
    public boolean AP_nlf_table;
    public boolean AP_nlf_list;
    public boolean AP_nlf_tree;
    public boolean AP_nlf_tabbedPane;
    public boolean AP_mnemonics;
    public boolean AP_m_abstractButtons;
    public boolean AP_m_label;
    public boolean AP_m_defaultCancel;
    
    public boolean tabTraversal;
    public boolean TT_showingOnly;
    
    
    private String cancelButtonLabel;
    
    private HashSet excludedClasses;
    public String excludedSeparator =";";
    
    public boolean report_name;
    public boolean report_description;
    public boolean report_position;
    public boolean storeToXML;
    
    /** Test if component has name. */
    public boolean test_name;
    
    /** Creates new TestSettings */
    public TestSettings() {
        windowTitle = "";
        cancelButtonLabel = "";
        excludedClasses = new HashSet();
    }
    
    public void setExcludedClasses(String excl) {
        if (excl != null){
            StringTokenizer st = new StringTokenizer(excl, excludedSeparator);
            while (st.hasMoreTokens()){
                excludedClasses.add(st.nextToken());
            }
        }
    }
    
    public void setCancelLabel(String label) {
        this.cancelButtonLabel = label;
    }
    
    public String getCancelLabel() {
        return this.cancelButtonLabel;
    }
    
    public void setWindowTitle(String title) {
        this.windowTitle = title;
    }
    
    public String getWindowTitle() {
        return this.windowTitle;
    }
    
    public String getCorrectedWindowTitle() {
        if(windowTitle.length()>20)
            return windowTitle.substring(0,20);
        else
            return windowTitle;
    }
    
    
    public HashSet getExcludedClasses() {
        return excludedClasses;
    }
    
    public boolean getReadedCorrectly() {
        return readedCorrectly;
    }
    
    public void setReadedCorrectly(boolean value) {
        readedCorrectly = value;
    }
    
    
    public void setDefaultSettings() {
        
        accessibleInterface = true;
        AI_showingOnly = false;
        
        accessibleProperties = true;
        AP_showingOnly = false;
        AP_focusTraversableOnly = true;
        AP_accessibleName = true;
        AP_accessibleDescription = true;
        AP_labelForSet = true;
        AP_noLabelFor = true;
        AP_nlf_text = true;
        AP_nlf_table = true;
        AP_nlf_list = true;
        AP_nlf_tree = true;
        AP_nlf_tabbedPane = true;
        AP_mnemonics = true;
        AP_m_abstractButtons = true;
        AP_m_label = true;
        AP_m_defaultCancel = false;
        
        tabTraversal = true;
        TT_showingOnly = true;
        
        //setCancelLabel(java.util.ResourceBundle.getBundle("org/netbeans/a11y/Bundle").getString("TS_Cancel"));
        setCancelLabel("Cancel"); //NOI18N
        setWindowTitle("");
        
        excludedClasses = new HashSet();
        
        report_name = true;
        report_description = true;
        report_position = false;
        storeToXML = false;
    }
    
    public void setAP(boolean value) {
        this.AP_showingOnly = value;
        this.AP_focusTraversableOnly = value;
        
        this.AP_accessibleName = value;
        this.AP_accessibleDescription = value;
        this.AP_labelForSet = value;
        this.AP_noLabelFor = value;
        this.AP_nlf_text = value;
        this.AP_nlf_table = value;
        this.AP_nlf_list = value;
        this.AP_nlf_tree = value;
        this.AP_nlf_tabbedPane = value;
        this.AP_mnemonics = value;
        this.AP_m_abstractButtons = value;
        this.AP_m_label = value;
        this.AP_m_defaultCancel = value;
    }
}
