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

package org.netbeans.modules.projectpackager.tools;

import java.util.prefs.Preferences;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 * Settings of Project Packager
 * @author Roman "Roumen" Strobl
 */
public class ProjectPackagerSettings {

    private static Preferences prefs() {
        return NbPreferences.forModule(ProjectPackagerSettings.class);
    }

    public static void setSmtpServer(String newVal) {
        prefs().put("smtpServer", newVal);
    }
    public static String getSmtpServer() {
        return prefs().get("smtpServer", System.getProperty("smtp_server", ""));
    }

    public static void setSmtpUsername(String newVal) {
        prefs().put("smtpUsername", newVal);
    }
    public static String getSmtpUsername() {
        return prefs().get("smtpUsername", System.getProperty("smtp_username", ""));
    }

    public static void setSmtpPassword(String newVal) {
        prefs().put("smtpPassword", newVal);
    }
    public static String getSmtpPassword() {
        return prefs().get("smtpPassword", System.getProperty("smtp_password", ""));
    }

    public static void setSmtpUseSSL(boolean newVal) {
        prefs().putBoolean("smtpUseSSL", newVal);
    }
    public static boolean getSmtpUseSSL() {
        return prefs().getBoolean("smtpUseSSL", Boolean.getBoolean("smtp_use_ssl"));
    }

    public static void setMailFrom(String newVal) {
        prefs().put("mailFrom", newVal);
    }
    public static String getMailFrom() {
        return prefs().get("mailFrom", NbBundle.getBundle(Constants.BUNDLE).getString("Mail_From_Default"));
    }

    public static void setMailSubject(String newVal) {
        prefs().put("mailSubject", newVal);
    }
    public static String getMailSubject() {
        return prefs().get("mailSubject", NbBundle.getBundle(Constants.BUNDLE).getString("Mail_Subject_Default"));
    }

    public static void setMailBody(String newVal) {
        prefs().put("mailBody", newVal);
    }
    public static String getMailBody() {
        return prefs().get("mailBody", NbBundle.getBundle(Constants.BUNDLE).getString("Mail_Body_Default"));
    }

}
