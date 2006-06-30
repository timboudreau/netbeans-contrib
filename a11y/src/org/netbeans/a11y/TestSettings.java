/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
