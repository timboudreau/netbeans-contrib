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
package org.netbeans.modules.ide.analysis.modernize.impl;

import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.EnhancedFix;
import org.openide.util.NbBundle;

class ConfigureHintsFix implements EnhancedFix {
    
    private final ModernizeErrorInfo error;
    private final String text;

    ConfigureHintsFix(ModernizeErrorInfo error) {
        this.error = error;
        String id = error.getId();
        this.text = (id.startsWith("-")) ? id.substring(1) : id; //NOI18N
    } 

    @Override
    public String getText() {
        return NbBundle.getMessage(ModernizeErrorProvider.class, "DisableHint", text); // NOI18N
    }

    @Override
    public ChangeInfo implement() throws Exception {
        String provider = ModernizeErrorProvider.NAME;
        String name = error.getId();
        OptionsDisplayer.getDefault().open("Editor/Hints/text/x-cnd+sourcefile/" + provider + "/" + name); // NOI18N
        return null;
    }

    @Override
    public CharSequence getSortText() {
        //Hint opening options dialog should always be the lastest in offered list
        return Integer.toString(Integer.MAX_VALUE);
    }

    public ModernizeErrorInfo getError() {
        return error;
    }
}
