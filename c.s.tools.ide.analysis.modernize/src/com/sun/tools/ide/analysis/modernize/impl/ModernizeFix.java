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

import java.io.File;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.EnhancedFix;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.PositionRef;

/**
 *
 * @author Ilia Gromov
 */
public class ModernizeFix implements EnhancedFix {
    
    private final YamlParser.Replacement[] replacements;
    private final String text;
    private CloneableEditorSupport ces;
    private final PositionRef[] beg;
    private final PositionRef[] end;

    public ModernizeFix(List<YamlParser.Replacement> replacementList, String id) {
        this.beg = new PositionRef[replacementList.size()];
        this.end = new PositionRef[replacementList.size()];
        this.replacements = replacementList.toArray(new YamlParser.Replacement[replacementList.size()]);
        this.text = (id.startsWith("-")) ? id.substring(1) : id; //NOI18N
        for (int i = 0; i < replacements.length; i++) {
            YamlParser.Replacement replacement = replacements[i];
            FileObject fo = FileUtil.toFileObject(new File(replacement.filePath));
            CsmFile csmFile = CsmUtilities.getCsmFile(fo, false, false);
            if (csmFile != null) {
                ces = CsmUtilities.findCloneableEditorSupport(csmFile);
                if (ces != null) {
                    beg[i] = ces.createPositionRef(replacement.offset, Position.Bias.Forward);
                    end[i] = ces.createPositionRef(replacement.offset + replacement.length, Position.Bias.Backward);
                } else {
                }
            }
        }
    } //NOI18N

    @Override
    public String getText() {
        return "Apply replacement - " + text; //NOI18N
    }

    @Override
    public ChangeInfo implement() throws Exception {
        Document doc = CsmUtilities.openDocument(ces);
        if (doc instanceof BaseDocument) {
            Position min = beg[0];
            Position max = end[0];
            for (int i = 0; i < replacements.length; i++) {
                YamlParser.Replacement replacement = replacements[i];
                PositionRef b = beg[i];
                PositionRef e = end[i];
                if (b != null && e != null) {
                    int newBeg = b.getOffset();
                    int newLength = e.getOffset() - b.getOffset();
                    if (b.getOffset() < min.getOffset()) {
                        min = b;
                    }
                    if (e.getOffset() > max.getOffset()) {
                        max = e;
                    }
                    if (newLength < 1) {
                        // TODO: temp fix
                        newLength = 0;
                    }
                    ((BaseDocument) doc).replace(newBeg, newLength, replacement.replacementText, null);
                }
            }
            return new ChangeInfo(min, max);
        }
        return null;
    }

    @Override
    public CharSequence getSortText() {
        return Integer.toString(Integer.MIN_VALUE);
    }

    public YamlParser.Replacement[] getReplacements() {
        return replacements;
    }
    
}
