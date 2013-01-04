/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.licensechanger.wizard.utils;

/**
 * Central class for Wizard-related property keys.
 *
 * @author Nils Hoffmann
 */
public final class WizardProperties {

    public static final String KEY_FILE_HANDLERS = "fileHandlers";
    public static final String KEY_ITEMS = "fileItems";
    public static final String KEY_LICENSE_TEXT = "licenseText";
    public static final String KEY_LICENSE_NAME = "licenseName";
    public static final String KEY_ROOT_FILES = "rootFiles";
    public static final String KEY_FOLDERS = "folders";
    public static final String KEY_UPDATE_DEFAULT_PROJECT_LICENSE = "updateDefaultProjectLicense";
    public static final String VALUE_DEFAULT_LICENSE_TEXT = "No License";
    public static final String PROP_ENDING = "ending";
    public static final String KEY_ENDING = "line_terminator";
    public static final String KEY_PROJECT = "project";
    public static final String KEY_COPYRIGHT_HOLDER = "project.organization";
    public static final String KEY_STORE_IN_USER_PROPERTIES = "storeInUserProperties";

    private WizardProperties() {
    }
}
