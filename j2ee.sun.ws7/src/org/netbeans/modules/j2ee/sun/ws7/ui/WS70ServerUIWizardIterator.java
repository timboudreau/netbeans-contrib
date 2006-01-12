/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70ServerUIWiazrdIterator.java 
 */

package org.netbeans.modules.j2ee.sun.ws7.ui;
import org.openide.WizardDescriptor;
import java.util.Set;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.event.ChangeListener;
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
            InstanceProperties ip = WS70URIManager.createInstanceProperties(location, host, port, user, password, displayName);
 
            Set result = new HashSet();
            result.add(ip);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("ERROR in creating Instance");
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
