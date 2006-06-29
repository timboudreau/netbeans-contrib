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

package org.netbeans.modules.projectpackager.exporter;

/**
 * Data storage for information about export
 * @author Roman "Roumen" Strobl
 */
public class ExportPackageInfo {

    // following properties remain static so that they are remembered during session
    private static String targetDir = "";
    private static boolean sendMail = false;
    private static String email = "";
    private static boolean deleteZip = false;
    private static String smtpServer = "";
    private static String smtpUsername = "";
    private static String smtpPassword = "";
    private static boolean smtpUseSSL = false;
    private static boolean processed = false;
    
    /** Creates a new instance of PackageInfo */
    private ExportPackageInfo() {        
    }

    /**
     * Is another export running?
     * @return true if running
     */
    public static boolean isProcessed() {
        return processed;
    }

    /**
     * Set if another export is processed
     * @param aProcessed true if processing started, false if it ends
     */
    public static void setProcessed(boolean aProcessed) {
        processed = aProcessed;
    }

    /**
     * Returns target directory
     * @return Target directory
     */
    public static String getTargetDir() {
        return targetDir;
    }

    /**
     * Sets target directory
     * @param aTargetDir target directory
     */
    public static void setTargetDir(String aTargetDir) {
        targetDir = aTargetDir;
    }

    /**
     * Is send mail checked?
     * @return true if checked
     */
    public static boolean isSendMail() {
        return sendMail;
    }

    /**
     * Set send mail checked
     * @param aSendMail true if checked
     */
    public static void setSendMail(boolean aSendMail) {
        sendMail = aSendMail;
    }

    /**
     * Return e-mail address(es)
     * @return e-mail address(es)
     */
    public static String getEmail() {
        return email;
    }

    /**
     * Set e-mail address(es)
     * @param aEmail e-mail address(es)
     */
    public static void setEmail(String aEmail) {
        email = aEmail;
    }

    /**
     * Is delete zip checked?
     * @return true if checked
     */
    public static boolean isDeleteZip() {
        return deleteZip;
    }

    /**
     * Set delete zip checked
     * @param aDeleteZip true if checked
     */
    public static void setDeleteZip(boolean aDeleteZip) {
        deleteZip = aDeleteZip;
    }

    /**
     * Return SMTP server
     * @return SMTP server
     */
    public static String getSmtpServer() {
        return smtpServer;
    }

    /**
     * Set SMTP server
     * @param aSmtpServer SMTP server
     */
    public static void setSmtpServer(String aSmtpServer) {
        smtpServer = aSmtpServer;
    }

    /**
     * Return SMTP username
     * @return SMTP username
     */
    public static String getSmtpUsername() {
        return smtpUsername;
    }

    /**
     * Set SMTP username
     * @param aSmtpUsername SMTP username
     */
    public static void setSmtpUsername(String aSmtpUsername) {
        smtpUsername = aSmtpUsername;
    }

    /**
     * Return SMTP password
     * @return SMTP password
     */
    public static String getSmtpPassword() {
        return smtpPassword;
    }

    /**
     * Set SMTP password
     * @param aSmtpPassword SMTP password
     */
    public static void setSmtpPassword(String aSmtpPassword) {
        smtpPassword = aSmtpPassword;
    }

    /**
     * Use SSL?
     * @return true if checked
     */
    public static boolean getSmtpUseSSL() {
        return smtpUseSSL;
    }

    /**
     * Set use SSL
     * @param aSmtpUseSSL true if checked
     */
    public static void setSmtpUseSSL(boolean aSmtpUseSSL) {
        smtpUseSSL = aSmtpUseSSL;
    }
    
}
