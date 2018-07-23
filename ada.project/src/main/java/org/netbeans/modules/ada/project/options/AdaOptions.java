/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.project.options;

import org.netbeans.modules.ada.project.options.AdaPreferences;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Andrea Lucarelli
 */
public final class AdaOptions {

    private static final String PREFERENCES_PATH = "general"; // NOI18N

    public static final String DEFAULT_ADA_DIALECTS = "Ada 95"; // NOI18N
    public static final String DEFAULT_ADA_RESTRICTIONS = "None"; // NOI18N
    public static final String DEFAULT_PKG_SPEC_PREFIX = "<package name>"; // NOI18N
    public static final String DEFAULT_PKG_BODY_PREFIX = "<package name>"; // NOI18N
    public static final String DEFAULT_SEPARATE_PREFIX = "<package name>"; // NOI18N
    public static final String DEFAULT_PKG_SPEC_POSTFIX = "<none>"; // NOI18N
    public static final String DEFAULT_PKG_BODY_POSTFIX = "<none>"; // NOI18N
    public static final String DEFAULT_SEPARATE_POSTFIX = "<procdure name>"; // NOI18N
    public static final String DEFAULT_PKG_SPEC_EXT = "ads"; // NOI18N
    public static final String DEFAULT_PKG_BODY_EXT = "adb"; // NOI18N
    public static final String DEFAULT_SEPARATE_EXT = "adb"; // NOI18N

    public static final String ADA_DIALECTS = "ada.dialects"; // NOI18N
    public static final String ADA_RESTRICTIONS = "ada.restrictions"; // NOI18N
    public static final String PKG_SPEC_PREFIX = "pkg.spec.prefix"; // NOI18N
    public static final String PKG_BODY_PREFIX = "pkg.body.prefix"; // NOI18N
    public static final String SEPARATE_PREFIX = "separate.prefix"; // NOI18N
    public static final String PKG_SPEC_POSTFIX = "pkg.spec.postfix"; // NOI18N
    public static final String PKG_BODY_POSTFIX = "pkg.body.postfix"; // NOI18N
    public static final String SEPARATE_POSTFIX = "separate.postfix"; // NOI18N
    public static final String PKG_SPEC_EXT = "pkg.spec.ext"; // NOI18N
    public static final String PKG_BODY_EXT = "pkg.body.ext"; // NOI18N
    public static final String SEPARATE_EXT = "separate.ext"; // NOI18N

    private static final AdaOptions INSTANCE = new AdaOptions();

    private AdaOptions() {
    }

    public static AdaOptions getInstance() {
        return INSTANCE;
    }

    public synchronized String getAdaDialects() {
        return getPreferences().get(ADA_DIALECTS, DEFAULT_ADA_DIALECTS);
    }

    public void setAdaDialects(String adaDialects) {
        getPreferences().put(ADA_DIALECTS, adaDialects);
    }

    public synchronized String getAdaRestrictions() {
        return getPreferences().get(ADA_RESTRICTIONS, DEFAULT_ADA_RESTRICTIONS);
    }

    public void setAdaRestrictions(String adaRestriction) {
        getPreferences().put(ADA_RESTRICTIONS, adaRestriction);
    }

    public synchronized String getPkgSpecPrefix() {
        return getPreferences().get(PKG_SPEC_PREFIX, DEFAULT_PKG_SPEC_PREFIX);
    }

    public void setPkgSpecPrefix(String pkgSpecPrefix) {
        getPreferences().put(PKG_SPEC_PREFIX, pkgSpecPrefix);
    }

    public synchronized String getPkgBodyPrefix() {
        return getPreferences().get(PKG_BODY_PREFIX, DEFAULT_PKG_BODY_PREFIX);
    }

    public void setPkgBodyPrefix(String pkgBodyPrefix) {
        getPreferences().put(PKG_BODY_PREFIX, pkgBodyPrefix);
    }

    public synchronized String getSeparatePrefix() {
        return getPreferences().get(SEPARATE_PREFIX, DEFAULT_SEPARATE_PREFIX);
    }

    public void setSeparatePrefix(String pkgBodyPrefix) {
        getPreferences().put(SEPARATE_PREFIX, pkgBodyPrefix);
    }

    public synchronized String getPkgSpecPostfix() {
        return getPreferences().get(PKG_SPEC_POSTFIX, DEFAULT_PKG_SPEC_POSTFIX);
    }

    public void setPkgSpecPostfix(String pkgSpecPostfix) {
        getPreferences().put(PKG_SPEC_POSTFIX, pkgSpecPostfix);
    }

    public synchronized String getPkgBodyPostfix() {
        return getPreferences().get(PKG_BODY_POSTFIX, DEFAULT_PKG_BODY_POSTFIX);
    }

    public void setPkgBodyPostfix(String pkgBodyPostfix) {
        getPreferences().put(PKG_BODY_POSTFIX, pkgBodyPostfix);
    }

    public synchronized String getSeparatePostfix() {
        return getPreferences().get(SEPARATE_POSTFIX, DEFAULT_SEPARATE_POSTFIX);
    }

    public void setSeparatePostfix(String pkgBodyPostfix) {
        getPreferences().put(SEPARATE_POSTFIX, pkgBodyPostfix);
    }

    //
    public synchronized String getPkgSpecExt() {
        return getPreferences().get(PKG_SPEC_EXT, DEFAULT_PKG_SPEC_EXT);
    }

    public void setPkgSpecExt(String pkgSpecExt) {
        getPreferences().put(PKG_SPEC_EXT, pkgSpecExt);
    }

    public synchronized String getPkgBodyExt() {
        return getPreferences().get(PKG_BODY_EXT, DEFAULT_PKG_BODY_EXT);
    }

    public void setPkgBodyExt(String pkgBodyExt) {
        getPreferences().put(PKG_BODY_EXT, pkgBodyExt);
    }

    public synchronized String getSeparateExt() {
        return getPreferences().get(SEPARATE_EXT, DEFAULT_SEPARATE_EXT);
    }

    public void setSeparateExt(String pkgBodyExt) {
        getPreferences().put(SEPARATE_EXT, pkgBodyExt);
    }

    private Preferences getPreferences() {
        return AdaPreferences.getPreferences(true).node(PREFERENCES_PATH);
    }

    public void addPreferenceChangeListener(PreferenceChangeListener preferenceChangeListener) {
        getPreferences().addPreferenceChangeListener(preferenceChangeListener);
    }

    public void removePreferenceChangeListener(PreferenceChangeListener preferenceChangeListener) {
        getPreferences().removePreferenceChangeListener(preferenceChangeListener);
    }

}
