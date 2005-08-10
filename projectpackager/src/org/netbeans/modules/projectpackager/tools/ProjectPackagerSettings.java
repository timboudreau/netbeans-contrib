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

package org.netbeans.modules.projectpackager.tools;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;


/**
 * Settings of Project Packager
 * @author Roman "Roumen" Strobl
 */
public class ProjectPackagerSettings extends SystemOption {

    // static final long serialVersionUID = ...;
    static final long serialVersionUID = 324234872987395873L;    

    /**
     * SMTP server property
     */
    public static final String PROP_SMTP_SERVER = java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("SMTP_Server");
    /**
     * SMTP username property
     */
    public static final String PROP_SMTP_USERNAME = java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("SMTP_Username");
    /**
     * SMTP password property
     */
    public static final String PROP_SMTP_PASSWORD = java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("SMTP_Password");
    
    /**
     * Version property
     */
    public static final String PROP_VERSION = java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("version");    
    
    /**
     * Current version - 4.1, may need update
     */
    public static final Integer CURRENT_VERSION = new Integer(41);
    
    // No constructor please!

    /**
     * Initialize settings
     */
    protected void initialize () {        
        super.initialize();
        
        putProperty(PROP_VERSION, CURRENT_VERSION, true);
        putProperty(PROP_SMTP_SERVER, "", true);
        putProperty(PROP_SMTP_USERNAME, "", true);
        putProperty(PROP_SMTP_PASSWORD, "", true);
    }

    /**
     * Serialize settings
     * @param out output
     * @throws java.io.IOException when there is an error with serialization
     */
    public void writeExternal (ObjectOutput out) throws IOException {
        out.writeObject(getProperty(PROP_VERSION));
        out.writeObject(getProperty(PROP_SMTP_SERVER));
        out.writeObject(getProperty(PROP_SMTP_USERNAME));
        out.writeObject(getProperty(PROP_SMTP_PASSWORD));
    }
    
    /**
     * Deserialize settings
     * @param in input
     * @throws java.io.IOException when there is problem with deserialization
     * @throws java.lang.ClassNotFoundException when settings class not found
     */
    public void readExternal (ObjectInput in) throws IOException, ClassNotFoundException {
        Object firstProperty = in.readObject();
        if (firstProperty instanceof Integer) {
            int version = ((Integer)firstProperty).intValue();
            readVersionedOptions(in, version);
        } else {
            // something went wrong
            System.err.println(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Unknown_options_for_Project_Packager."));
        }
    }    
    
    private void readVersionedOptions(ObjectInput in, int version) throws IOException, ClassNotFoundException {
        switch (version) {
            case 41:
                readVersion41Options(in);
                break;
            default:
                // weird stuff
                System.err.println(java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Unknown_options_for_Project_Packager_-_version:_")+version);
        }
    }
    
    private void readVersion41Options(ObjectInput in) throws IOException, ClassNotFoundException {
        putProperty(PROP_SMTP_SERVER, in.readObject(), true);
        putProperty(PROP_SMTP_USERNAME, in.readObject(), true);
        putProperty(PROP_SMTP_PASSWORD, in.readObject(), true);
    }
    
    /**
     * Return settings name
     * @return settings name
     */
    public String displayName () {
        return java.util.ResourceBundle.getBundle(Constants.BUNDLE).getString("Project_Packager_Settings");
    }

    /**
     * Return help context
     * @return help context
     */
    public HelpCtx getHelpCtx () {
        return new HelpCtx(ProjectPackagerSettings.class); 
    }
    
    /**
     * Default instance of this system option, for the convenience of associated classes.
     * @return instance of this class
     */
    public static ProjectPackagerSettings getDefault () {
        return (ProjectPackagerSettings) findObject (ProjectPackagerSettings.class, true);
    }
    

    /**
     * Set SMTP server
     * @param newVal SMTP server
     */
    public void setSmtpServer(String newVal) {
        putProperty(PROP_SMTP_SERVER, newVal, true);
    }
    
    /**
     * Return SMTP server
     * @return SMTP server
     */
    public String getSmtpServer() {
        return (String) getProperty(PROP_SMTP_SERVER);
    }

    /**
     * Set SMTP username
     * @param newVal SMTP username
     */
    public void setSmtpUsername(String newVal) {
        putProperty(PROP_SMTP_USERNAME, newVal, true);
    }
    
    /**
     * Return SMTP username
     * @return SMTP username
     */
    public String getSmtpUsername() {
        return (String) getProperty(PROP_SMTP_USERNAME);
    }
    
    /**
     * Set SMTP password
     * @param newVal SMTP password
     */
    public void setSmtpPassword(String newVal) {
        putProperty(PROP_SMTP_PASSWORD, newVal, true);
    }
    
    /**
     * Return SMTP password
     * @return SMTP password
     */
    public String getSmtpPassword() {
        return (String) getProperty(PROP_SMTP_PASSWORD);
    }    
}
