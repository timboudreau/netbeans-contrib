/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Contributor(s): Ilia Gromov
 */
package com.sun.tools.ide.analysis.modernize.impl;

import org.netbeans.modules.cnd.api.model.syntaxerr.AbstractCodeAudit;
import org.netbeans.modules.cnd.api.model.syntaxerr.AuditPreferences;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorInfo;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorProvider;

final class ModernizeCodeAudit extends AbstractCodeAudit {

    private final String id;
    private final String name;
    private final String description;
    private final String defaultSeverity;
    private final boolean defaultEnabled;
    private final AuditPreferences myPreferences;

    public ModernizeCodeAudit(String id, String name, String description, String defaultSeverity, boolean defaultEnabled, AuditPreferences myPreferences) {
        super(id, name, description, defaultSeverity, defaultEnabled, myPreferences);
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultSeverity = defaultSeverity;
        this.defaultEnabled = defaultEnabled;
        this.myPreferences = myPreferences;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public static CsmErrorInfo.Severity toSeverity(String severity) {
        if ("error".equals(severity)) { // NOI18N
            return CsmErrorInfo.Severity.ERROR;
        } else if ("warning".equals(severity)) { // NOI18N
            return CsmErrorInfo.Severity.WARNING;
        } else {
            return CsmErrorInfo.Severity.HINT;
        }
    }

    @Override
    public String getKind() {
        return "inspection"; //NOI18N
    }

    @Override
    public AuditPreferences getPreferences() {
        return myPreferences;
    }

    @Override
    public boolean getDefaultEnabled() {
        return defaultEnabled;
    }

    @Override
    public String getDefaultSeverity() {
        return defaultSeverity;
    }

    @Override
    public boolean isSupportedEvent(CsmErrorProvider.EditorEvent kind) {
        return true;
    }

    @Override
    public void doGetErrors(CsmErrorProvider.Request request, CsmErrorProvider.Response response) {
        throw new UnsupportedOperationException("Not supported yet."); //NOI18N
    }
}
