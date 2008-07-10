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
    static String mailFrom = "";
    static String mailSubject = "";
    static String mailBody = "";
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
