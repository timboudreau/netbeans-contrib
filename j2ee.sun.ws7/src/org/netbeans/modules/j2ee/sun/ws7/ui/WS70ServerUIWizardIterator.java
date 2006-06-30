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

/*
 * WS70ServerUIWiazrdIterator.java
 */

package org.netbeans.modules.j2ee.sun.ws7.ui;
import org.openide.WizardDescriptor;
import org.openide.ErrorManager;
import java.util.Set;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.event.ChangeListener;
import javax.swing.JComponent;
import java.awt.Component;
import java.util.NoSuchElementException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
        

/**
 *
 * @author Mukesh Garg
 */
public class WS70ServerUIWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private final static String PROP_CONTENT_DATA = "WizardPanel_contentData"; // NOI18N
    private final static String PROP_CONTENT_SELECTED_INDEX = "WizardPanel_contentSelectedIndex"; // NOI18N
    public final static String PROP_ERROR_MESSAGE = "WizardPanel_errorMessage"; // NOI18N
    private final static String PROP_DISPLAY_NAME = "ServInstWizard_displayName"; // NOI18N
    public final static String PROP_LOCAL_SERVER = "LocalServer"; // NOI18N
    public final static String PROP_SSL_PORT = "SSLAdminPort"; // NOI18N
    private WS70AddServerChoicePanel panel;
    private WizardDescriptor wizard;
    /**
     * Creates a new instance of WS70ServerUIWizardIterator
     * 
     */
    public WS70ServerUIWizardIterator() {
    }
    
    //WizardDescriptor.InstantiatingIterator method implementation
    public void initialize(WizardDescriptor wizard){
        this.wizard = wizard;
    }
    //WizardDescriptor.InstantiatingIterator method implementation
    public Set instantiate(){
        WS70AddServerChoiceVisualPanel visualPanel = (WS70AddServerChoiceVisualPanel)panel.getComponent();
        try {
            String host = visualPanel.getAdminHost();
            String port = visualPanel.getAdminPort();
            String user = visualPanel.getAdminUserName();
            String password = visualPanel.getAdminPassword();
            String location  = visualPanel.getServerLocation();
            String displayName = (String)wizard.getProperty(PROP_DISPLAY_NAME);
            boolean localserver = visualPanel.isLocalServer();
            boolean sslport = visualPanel.isAdminOnSSL();
            InstanceProperties ip = WS70URIManager.createInstanceProperties(location, host, port, user, password, displayName);
            ip.setProperty(PROP_LOCAL_SERVER, Boolean.toString(localserver));
            ip.setProperty(PROP_SSL_PORT, Boolean.toString(sslport));
            Set result = new HashSet();
            result.add(ip);
            return result;
        } catch (Exception ex) {
            ErrorManager.getDefault().log(ErrorManager.EXCEPTION, ex.getMessage());
            return null;
        }
    }
    //WizardDescriptor.InstantiatingIterator method implementation
    public void uninitialize(WizardDescriptor wizard){
        
    }
    //WizardDescriptor.Iterator method implementation
    public WizardDescriptor.Panel current(){
        if(panel==null){
            panel = new WS70AddServerChoicePanel();
        }
        Component c = panel.getComponent();
        String[] steps = new String[1];
        steps[0]=c.getName();
        if (c instanceof JComponent) { // assume Swing components
            JComponent jc = (JComponent) c;
            // Sets steps names for a panel
            jc.putClientProperty("WizardPanel_contentData", steps);         //NOI18N
            // Turn on subtitle creation on each step
            jc.putClientProperty("WizardPanel_autoWizardStyle",             //NOI18N
                    Boolean.TRUE);
            // Show steps on the left side with the image on the background
            jc.putClientProperty("WizardPanel_contentDisplayed",            //NOI18N 
                    Boolean.TRUE);
            // Turn on numbering of all steps
            jc.putClientProperty("WizardPanel_contentNumbered",             //NOI18N
                    Boolean.TRUE);
        }        
        return panel;
    }       
 
   //WizardDescriptor.Iterator method implementation
    public void previousPanel(){
        throw new NoSuchElementException();
        
    }        
   //WizardDescriptor.Iterator method implementation
    public void nextPanel(){
        throw new NoSuchElementException();
    }     
   //WizardDescriptor.Iterator method implementation
    public boolean  hasPrevious(){
        return false;
    }         
    //WizardDescriptor.Iterator method implementation
    public boolean hasNext(){
        return false;
    }  
    //WizardDescriptor.Iterator method implementation
    public void addChangeListener(ChangeListener listener){
        
    }
    //WizardDescriptor.Iterator method implementation
    public void removeChangeListener(ChangeListener listener){
        
    }  
   //WizardDescriptor.Iterator method implementation
    public String name(){
        return "";
    }
    
}
