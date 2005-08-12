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

package org.netbeans.modules.projectpackager.exporter;
import org.netbeans.modules.projectpackager.tools.ProjectPackagerSettings;

/**
 * Tools used together with the E-mail settings dialog
 * @author Roman "Roumen" Strobl
 */
public class EmailSettingsUITools {
    private static EmailSettingsDialog esd;
    
    private EmailSettingsUITools() {
    }
    
    /**
     * To send a reference to E-mail settings dialog
     * @param aesd e-mail settings dialog
     */
    public static void setEmailSettingsDialog(EmailSettingsDialog aesd) {
        esd = aesd;
    }
    
    /**
     * Processes the Cancel button - calls dispose
     */
    public static void processCancelButton() {
        esd.dispose();
    }
    
    /**
     * Processes the Ok button - saves the settings
     */
    public static void processOkButton() {
        final ProjectPackagerSettings pps = ProjectPackagerSettings.getDefault();
        ExportPackageInfo.setSmtpServer(esd.getSmtpServer());
        ExportPackageInfo.setSmtpUsername(esd.getSmtpUsername());
        ExportPackageInfo.setSmtpPassword(esd.getSmtpPassword());
        ExportPackageInfo.setSmtpUseSSL(esd.getSmtpUseSSL());
        pps.setSmtpServer(esd.getSmtpServer());
        pps.setSmtpUsername(esd.getSmtpUsername());
        pps.setSmtpPassword(esd.getSmtpPassword());
        pps.setSmtpUseSSL(new Boolean(esd.getSmtpUseSSL()));
        esd.dispose();
    }
}
