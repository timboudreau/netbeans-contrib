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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editing;

import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.ElementKind;
import org.netbeans.modules.scala.editing.nodes.FunRef;

/**
 *
 * @author Tor Norbye
 */
public class IndexedFunction extends IndexedElement {

    private List<String> args;

    IndexedFunction(String fqn, String name, String in, ScalaIndex index, String fileUrl, String attributes, int flags, ElementKind kind) {
        super(fqn, name, in, index, fileUrl, attributes, flags, kind);
    }

    @Override
    public String getSignature() {
        if (signature == null) {
            StringBuilder sb = new StringBuilder();
            if (in != null) {
                sb.append(in);
                sb.append('.');
            }
            sb.append(name);
            sb.append("(");
            List<String> myArgs = getArgs();
            if (myArgs.size() > 0) {
                for (int i = 0, n = myArgs.size(); i < n; i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(myArgs.get(i));
                }
            }
            sb.append(")");
            signature = sb.toString();
        }

        return signature;
    }

    public List<String> getArgs() {
        if (args == null) {
            String[] argArray;
            int argIndex = getAttributeSection(ARG_INDEX);
            int endIndex = attributes.indexOf(';', argIndex);
            if (endIndex > argIndex) {
                String argsPortion = attributes.substring(argIndex, endIndex);
                argArray = argsPortion.split(","); // NOI18N
            } else {
                argArray = new String[0];
            }

            args = Arrays.asList(argArray);            
        }

        return args;
    }

    public boolean isReferredBy(FunRef funRef) {
        List<String> myArgs = getArgs();
        /** @todo compare param type */
        boolean containsVariableLengthArg = false;
        for (String myArg : myArgs) {
            int colon = myArg.indexOf(':');
            if (colon == -1) {
                continue;
            }
            String myArgName = myArg.substring(0, colon);
            String myArgType = myArg.substring(colon + 1, myArg.length());
            if (myArgType.endsWith("*")) {
                containsVariableLengthArg = true;
                break;
            }
        }

        if (getSimpleName().equals(funRef.getCall().getSimpleName()) || getSimpleName().equals("apply") && funRef.isLocal()) {
            if (myArgs.size() == funRef.getArgs().size() || containsVariableLengthArg) {
                return true;
            }
        }
        
        return false;
    }
}
