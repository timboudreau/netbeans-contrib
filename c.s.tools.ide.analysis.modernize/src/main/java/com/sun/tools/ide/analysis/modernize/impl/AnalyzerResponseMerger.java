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
 * Contributor(s):
 */
package com.sun.tools.ide.analysis.modernize.impl;

import com.sun.tools.ide.analysis.modernize.impl.ModernizeAnalyzerImpl.ModernizeResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.netbeans.modules.cnd.analysis.api.AnalyzerResponse;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Ilia Gromov
 */
public class AnalyzerResponseMerger {

    private static class ErrorDesc {

        final ModernizeErrorInfo info;
        final FileObject fo;

        public ErrorDesc(ModernizeErrorInfo info, FileObject fo) {
            this.info = info;
            this.fo = fo;
        }

        public boolean isSame(ErrorDesc o2) {
            return isSame(this.info, o2.info) && this.fo.equals(o2.fo);
        }

        public boolean isSame(ModernizeErrorInfo o1, ModernizeErrorInfo o2) {
            return o1.getStartOffset() == o2.getStartOffset()
                    && o1.getEndOffset() == o2.getEndOffset()
                    && o1.getDiagnostics().getCheckName().equals(o2.getDiagnostics().getCheckName());
        }
    }

    private final List<ErrorDesc> errors = new ArrayList<>();
    private final ModernizeResponse delegate;

    public AnalyzerResponseMerger(ModernizeResponse delegate) {
        this.delegate = delegate;
    }

    public void addError(ModernizeErrorInfo info, FileObject fo) {
        ErrorDesc errorDesc = new ErrorDesc(info, fo);
        Optional<ErrorDesc> found = errors.stream()
                .filter(o1 -> o1.isSame(errorDesc))
                .findAny();

        if (found.isPresent()) {
            found.get().info.addMessageInfixes(errorDesc.info.getMessageInfixes());
        } else {
            errors.add(errorDesc);
        }
    }

    public List<ErrorDescription> done() {
        return errors.stream()
                .map(error -> delegate.addErrorImpl(error.info, error.fo))
                .collect(Collectors.toList());
    }
}
