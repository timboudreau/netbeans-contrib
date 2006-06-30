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

package org.netbeans.modules.projectpackager.tools;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


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
    public static final String PROP_SMTP_SERVER = NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_Server");
    /**
     * SMTP username property
     */
    public static final String PROP_SMTP_USERNAME = NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_Username");
    /**
     * SMTP password property
     */
    public static final String PROP_SMTP_PASSWORD = NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_Password");
    /**
     * Use SSL for SMTP property
     */    
    public static final String PROP_SMTP_USE_SSL = NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_Use_SSL");
    /**
     * Mail From
     */
    public static final String PROP_MAIL_FROM = NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_Mail_From");
    /**
     * Mail Subject
     */
    public static final String PROP_MAIL_SUBJECT = NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_Mail_Subject");
    /**
     * Mail Body
     */
    public static final String PROP_MAIL_BODY = NbBundle.getBundle(Constants.BUNDLE).getString("SMTP_Mail_Body");
            
    
    /**
     * Version property
     */
    public static final String PROP_VERSION = NbBundle.getBundle(Constants.BUNDLE).getString("version");    
    
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
        String smtpServer = System.getProperty("smtp_server");
        if (smtpServer!=null && !smtpServer.equals("")) {
            putProperty(PROP_SMTP_SERVER, smtpServer, true);
        } else {
            putProperty(PROP_SMTP_SERVER, "", true);
        }
        String smtpUsername = System.getProperty("smtp_username");
        if (smtpUsername!=null && !smtpUsername.equals("")) {
            putProperty(PROP_SMTP_USERNAME, smtpUsername, true);
        } else {
            putProperty(PROP_SMTP_USERNAME, "", true);
        }
        String smtpPassword = System.getProperty("smtp_password");
        if (smtpPassword!=null && !smtpPassword.equals("")) {
            putProperty(PROP_SMTP_PASSWORD, smtpPassword, true);
        } else {
            putProperty(PROP_SMTP_PASSWORD, "", true);
        }
        Boolean smtpUseSSL = Boolean.valueOf(System.getProperty("smtp_use_ssl"));        
        if (smtpUseSSL!=null) {
            putProperty(PROP_SMTP_USE_SSL, smtpUseSSL, true);
        } else {
            putProperty(PROP_SMTP_USE_SSL, Boolean.FALSE, true);
        }
        putProperty(PROP_MAIL_FROM, NbBundle.getBundle(Constants.BUNDLE).getString("Mail_From_Default"), true);
        putProperty(PROP_MAIL_SUBJECT, NbBundle.getBundle(Constants.BUNDLE).getString("Mail_Subject_Default"), true);
        putProperty(PROP_MAIL_BODY, NbBundle.getBundle(Constants.BUNDLE).getString("Mail_Body_Default"), true);
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
        out.writeObject(getProperty(PROP_SMTP_USE_SSL));
        out.writeObject(getProperty(PROP_MAIL_FROM));
        out.writeObject(getProperty(PROP_MAIL_SUBJECT));
        out.writeObject(getProperty(PROP_MAIL_BODY));
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
            System.err.println(NbBundle.getBundle(Constants.BUNDLE).getString("Unknown_options_for_Project_Packager."));
        }
    }    
    
    private void readVersionedOptions(ObjectInput in, int version) throws IOException, ClassNotFoundException {
        switch (version) {
            case 41:
                readVersion41Options(in);
                break;
            default:
                // weird stuff
                System.err.println(NbBundle.getBundle(Constants.BUNDLE).getString("Unknown_options_for_Project_Packager_-_version:_")+version);
        }
    }
    
    private void readVersion41Options(ObjectInput in) throws IOException, ClassNotFoundException {
        putProperty(PROP_SMTP_SERVER, in.readObject(), true);
        putProperty(PROP_SMTP_USERNAME, in.readObject(), true);
        putProperty(PROP_SMTP_PASSWORD, in.readObject(), true);
        putProperty(PROP_SMTP_USE_SSL, in.readObject(), true);
        putProperty(PROP_MAIL_FROM, in.readObject(), true);
        putProperty(PROP_MAIL_SUBJECT, in.readObject(), true);
        putProperty(PROP_MAIL_BODY, in.readObject(), true);
    }
    
    /**
     * Return settings name
     * @return settings name
     */
    public String displayName () {
        return NbBundle.getBundle(Constants.BUNDLE).getString("Project_Packager_Settings");
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
    
    /**
     * Set Use SSL
     * @param newVal Use SSL
     */
    public void setSmtpUseSSL(Boolean newVal) {
        putProperty(PROP_SMTP_USE_SSL, newVal, true);
    }
    
    /**
     * Return Uses SSL?
     * @return Uses SSL?
     */
    public Boolean getSmtpUseSSL() {
        return (Boolean) getProperty(PROP_SMTP_USE_SSL);
    }        
    
    /**
     * Set Mail From
     * @param newVal Mail From
     */
    public void setMailFrom(String newVal) {
        putProperty(PROP_MAIL_FROM, newVal, true);
    }
    
    /**
     * Return Mail From
     * @return Mail From
     */
    public String getMailFrom() {
        return (String) getProperty(PROP_MAIL_FROM);
    }         
    
    /**
     * Set Mail Subject
     * @param newVal Mail Subject
     */
    public void setMailSubject(String newVal) {
        putProperty(PROP_MAIL_SUBJECT, newVal, true);
    }
    
    /**
     * Return Mail Subject
     * @return Mail Subject
     */
    public String getMailSubject() {
        return (String) getProperty(PROP_MAIL_SUBJECT);
    }         
    
    /**
     * Set Mail Body
     * @param newVal Mail Body
     */
    public void setMailBody(String newVal) {
        putProperty(PROP_MAIL_BODY, newVal, true);
    }
    
    /**
     * Return Mail Body
     * @return Mail Body
     */
    public String getMailBody() {
        return (String) getProperty(PROP_MAIL_BODY);
    }         
}
