/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.debugjavac;

import java.util.List;

/**
 *
 * @author lahvac
 */
public interface Decompiler {
    public Result decompile(Input input);

    public final class Input {
        public String source;
        public List<String> params;

        public Input() {
        }

        public Input(String source, List<String> params) {
            this.source = source;
            this.params = params;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public List<String> getParams() {
            return params;
        }

        public void setParams(List<String> params) {
            this.params = params;
        }

    }
//    public final class Input {
//        public final String source;
//        public final List<String> params;
//        public Input(String source, List<String> params) {
//            this.source = source;
//            this.params = params;
//        }
//    }
    public final class Result {
        public String compileErrors;
        public String decompiledOutput;
        public String decompiledMimeType;
        public String exception;

        public Result() {
        }

        public Result(String compileErrors, String decompiledOutput, String decompiledMimeType) {
            this.compileErrors = compileErrors.trim().isEmpty() ? null : compileErrors;
            this.decompiledOutput = decompiledOutput.trim().isEmpty() ? null : decompiledOutput;
            this.decompiledMimeType = decompiledMimeType;
        }

        public Result(String exception) {
            this.exception = exception;
        }

        public String getCompileErrors() {
            return compileErrors;
        }

        public void setCompileErrors(String compileErrors) {
            this.compileErrors = compileErrors;
        }

        public String getDecompiledOutput() {
            return decompiledOutput;
        }

        public void setDecompiledOutput(String decompiledOutput) {
            this.decompiledOutput = decompiledOutput;
        }

        public String getDecompiledMimeType() {
            return decompiledMimeType;
        }

        public void setDecompiledMimeType(String decompiledMimeType) {
            this.decompiledMimeType = decompiledMimeType;
        }

        public String getException() {
            return exception;
        }

        public void setException(String exception) {
            this.exception = exception;
        }
    }
//    public final class Result {
//        public final String compileErrors;
//        public final String decompiledOutput;
//        public final String decompiledMimeType;
//        public Result(String compileErrors, String decompiledOutput, String decompiledMimeType) {
//            this.compileErrors = compileErrors.trim().isEmpty() ? null : compileErrors;
//            this.decompiledOutput = decompiledOutput.trim().isEmpty() ? null : decompiledOutput;
//            this.decompiledMimeType = decompiledMimeType;
//        }
//    }
}
