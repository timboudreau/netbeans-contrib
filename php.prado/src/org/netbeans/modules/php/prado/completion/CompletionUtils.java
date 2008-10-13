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
package org.netbeans.modules.php.prado.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.php.editor.index.IndexedFunction;
import org.netbeans.modules.php.editor.index.PHPIndex;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.astnodes.BodyDeclaration.Modifier;
import org.netbeans.modules.php.prado.PageUtils;

/**
 *
 * @author Petr Pisl
 */
public class CompletionUtils {

    public static List<String> getComponentProperties(CompilationInfo info, String component, String prefix) {
        List<String> arguments = new ArrayList<String>();
        PHPParseResult phpresult = (PHPParseResult) info.getEmbeddedResult(PageUtils.getPHPMimeType(), 0);
        PHPIndex index = PHPIndex.get(info.getIndex(PageUtils.getPHPMimeType()));
        Collection<IndexedFunction> methods = index.getAllMethods(phpresult, component, "", NameKind.PREFIX, Modifier.PUBLIC); //NOI18N
        List<String> allGetters = new ArrayList<String>();
        for (IndexedFunction indexedFunction : methods) {
            if (indexedFunction.getName().startsWith("get") && indexedFunction.getArgs().length == 0 && isSimpleType(indexedFunction.getReturnType())) {  //NOI18N
                allGetters.add(indexedFunction.getName().substring(3));
            }
        }
        prefix = prefix.toLowerCase();
        for (IndexedFunction indexedFunction : methods) {
            if (indexedFunction.getArgs().length == 1) {
                String name = indexedFunction.getName();
                if (indexedFunction.getName().startsWith("set")) {
                    name = name.substring(3);
                    if (name.toLowerCase().startsWith(prefix) && !arguments.contains(name) && allGetters.contains(name)) {
                        arguments.add(name);
                    }
                } else if (indexedFunction.getName().startsWith("on")) {
                    System.out.println(indexedFunction.getName());
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    if (name.toLowerCase().startsWith(prefix.toLowerCase()) && !arguments.contains(name)) {
                        arguments.add(name);
                    }
                }
            }
        }
        return arguments;
    }

    private static boolean isSimpleType(String type) {
        if (type == null) {
            return true;
        }
        type = type.trim().toLowerCase();
        return type.equals("int")
                || type.equals("boolean")
                || type.equals("string")
                || type.equals("integer")
                || type.equals("float")
                || type.equals("tlistselectionmode");
    }
}
