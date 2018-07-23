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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorInfo;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.openide.util.Lookup;

public class ModernizeErrorInfo implements CsmErrorInfo, ModernizeErrorProvider.ErrorInfoWithId {

    private final YamlParser.Diagnostics diag;
    private final String messagePrefix;
    private final List<String> messageInfixes;
    private final String messageBody;
    private final NativeProject project;

    private final int startOffset;
    private final int endOffset;

    public ModernizeErrorInfo(YamlParser.Diagnostics diag, String messagePrefix, List<String> messageInfixes, String messageBody, Lookup.Provider project) {
        this.diag = diag;
        this.messagePrefix = messagePrefix;
        this.messageInfixes = messageInfixes;
        this.messageBody = messageBody;
        this.project = project.getLookup().lookup(NativeProject.class);

        if (diag.getReplacements().isEmpty()) {
            startOffset = diag.getMessageFileOffset();
            endOffset = diag.getMessageFileOffset();
        } else {
            YamlParser.Replacement first = diag.getReplacements().get(0);
            startOffset = first.offset;
            endOffset = first.offset + first.length;
        }
    }

    public static ModernizeErrorInfo withFixedMessage(YamlParser.Diagnostics diag, String messageBody, Lookup.Provider project) {
        return new ModernizeErrorInfo(diag, "", new ArrayList<>(), messageBody, project);
    }

    public static ModernizeErrorInfo withMutableMessage(YamlParser.Diagnostics diag, String messagePrefix, String messageInfix, String messageBody, Lookup.Provider project) {
        List<String> list = new ArrayList<>();
        list.add(messageInfix);
        return new ModernizeErrorInfo(diag, messagePrefix, list, messageBody, project);
    }

    public static ModernizeErrorInfo withMutableMessage(YamlParser.Diagnostics diag, String messagePrefix, List<String> messageInfixes, String messageBody, Lookup.Provider project) {
        return new ModernizeErrorInfo(diag, messagePrefix, messageInfixes, messageBody, project);
    }

    public void addMessageInfixes(List<String> infix) {
        messageInfixes.addAll(infix);
    }

    public List<String> getMessageInfixes() {
        return Collections.unmodifiableList(messageInfixes);
    }

    public NativeProject getProject() {
        return project;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(messagePrefix).append(' ');
        if (messageInfixes.size() < 4) {
            sb.append(messageInfixes.toString());
        } else {
            ArrayList<String> list = new ArrayList<>(messageInfixes.subList(0, 2));
            list.add(String.format("... (%s more)", messageInfixes.size() - 2));
            sb.append(list.toString());
        }
        sb.append(": "); //NOI18N
        sb.append(messageBody);
        return sb.toString();
    }

    @Override
    public Severity getSeverity() {
        return Severity.valueOf(diag.getLevel().toString().toUpperCase());
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public int getEndOffset() {
        return endOffset;
    }

    public YamlParser.Diagnostics getDiagnostics() {
        return diag;
    }

    @Override
    public String getId() {
        return diag.getCheckName();
    }

    public boolean isNeedConfigureHint() {
        return true;
    }
}
