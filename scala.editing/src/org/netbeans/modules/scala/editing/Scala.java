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

package org.netbeans.modules.scala.editing;

import org.netbeans.api.languages.ASTToken;
import org.netbeans.api.languages.CharInput;
import org.netbeans.api.languages.Language;
import org.netbeans.api.languages.LanguageDefinitionNotFoundException;
import org.netbeans.api.languages.LanguagesManager;

/**
 *
 * @author Caoyuan Deng
 */
public class Scala {

    private static final String DOC = "org/netbeans/modules/languages/erlang/Documentation.xml";
    private static final String MIME_TYPE = "text/x-scala";

    public static Object[] parseXmlStart(CharInput input) {
        if (input.read() != '<') {
            throw new InternalError();
        }

        int start = input.getIndex();
        try {
            Language language = LanguagesManager.get().getLanguage(MIME_TYPE);
            while (!input.eof () && input.next () != '<') {
                if (input.next () == '\r' ||
                    input.next () == '\n'
                ) {
                    input.setIndex (start);
                    return new Object[] {
                        ASTToken.create (language, "js_operator", "", 0, 0, null),
                        null
                    };
                }
                if (input.next () == '\\')
                    input.read ();
                input.read ();
            }
            
            
            
            boolean isStop = false;
            if (input.eof()) {
                isStop = true;
            } else {
                char next = input.next();
                if (next == ' ' || next == '\t' || next == '\r' || next == '\n' || next == '%' || next == '-') {
                    isStop = true;
                }
            }

            if (isStop) {
                input.setIndex(start);
                return new Object[]{
                    ASTToken.create(language, "stop", ".", 0, 0, null),
                    null
                };
            } else {
                input.setIndex(start);
                return new Object[]{
                    ASTToken.create(language, "dot", ".", 0, 0, null),
                    null
                };
            }
        } catch (LanguageDefinitionNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
