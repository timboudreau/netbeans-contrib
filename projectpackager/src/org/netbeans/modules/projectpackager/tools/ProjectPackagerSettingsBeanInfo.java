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

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ResourceBundle;
import org.openide.util.Utilities;

/**
 * @author Roman "Roumen" Strobl
 */
public class ProjectPackagerSettingsBeanInfo extends SimpleBeanInfo {
    
    public PropertyDescriptor[] getPropertyDescriptors() {
        ResourceBundle bundle = java.util.ResourceBundle.getBundle(Constants.BUNDLE);
        try {
            PropertyDescriptor smtpServer =
                    new PropertyDescriptor("smtpServer", ProjectPackagerSettings.class);
            smtpServer.setDisplayName(bundle.getString("SMTP_Server"));
            smtpServer.setShortDescription(bundle.getString("HINT_SMTP_Server"));
            PropertyDescriptor smtpUsername =
                    new PropertyDescriptor("smtpUsername", ProjectPackagerSettings.class);
            smtpUsername.setDisplayName(bundle.getString("SMTP_Username"));
            smtpUsername.setShortDescription(bundle.getString("HINT_SMTP_Username"));
            PropertyDescriptor smtpPassword =
                    new PropertyDescriptor("smtpPassword", ProjectPackagerSettings.class);
            smtpPassword.setDisplayName(bundle.getString("SMTP_Password"));
            smtpPassword.setShortDescription(bundle.getString("HINT_SMTP_Password"));
            smtpPassword.setHidden(true);
            PropertyDescriptor smtpUseSSL =
                    new PropertyDescriptor("smtpUseSSL", ProjectPackagerSettings.class);
            smtpUseSSL.setDisplayName(bundle.getString("SMTP_Use_SSL"));
            smtpUseSSL.setShortDescription(bundle.getString("HINT_SMTP_Use_SSL"));
            PropertyDescriptor smtpMailFrom =
                    new PropertyDescriptor("mailFrom", ProjectPackagerSettings.class);
            smtpMailFrom.setDisplayName(bundle.getString("SMTP_Mail_From"));
            smtpMailFrom.setShortDescription(bundle.getString("HINT_Mail_From"));
            PropertyDescriptor smtpMailSubject =
                    new PropertyDescriptor("mailSubject", ProjectPackagerSettings.class);
            smtpMailSubject.setDisplayName(bundle.getString("SMTP_Mail_Subject"));
            smtpMailSubject.setShortDescription(bundle.getString("HINT_Mail_Subject"));
            PropertyDescriptor smtpMailBody =
                    new PropertyDescriptor("mailBody", ProjectPackagerSettings.class);
            smtpMailBody.setDisplayName(bundle.getString("SMTP_Mail_Body"));
            smtpMailBody.setShortDescription(bundle.getString("HINT_Mail_Body"));
            return new PropertyDescriptor[] {smtpServer, smtpUsername, smtpPassword, 
                    smtpUseSSL, smtpMailFrom, smtpMailBody, smtpMailSubject};
        } catch (IntrospectionException ie) {
            System.err.println("Introspection exception thrown: "+ie);
            return null;
        }
    }
    
    public Image getIcon(int type) {
        System.out.println(type);
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            System.out.println("here");
            return Utilities.loadImage(
                    "/org/netbeans/modules/projectpackager/resources/ProjectPackagerIcon16.gif");
        } else {
            return null;
        }
    }
    
}
