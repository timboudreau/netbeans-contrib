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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static List<String> getComponentOrdinalProperties(CompilationInfo info, String component, String prefix, boolean includeOnProperties) {
        PHPParseResult phpresult = (PHPParseResult) info.getEmbeddedResult(PageUtils.getPHPMimeType(), 0);
        PHPIndex index = PHPIndex.get(info.getIndex(PageUtils.getPHPMimeType()));

        Map<String, String> properties = getComponentProperties(index, phpresult, component, prefix, includeOnProperties, false);
        List<String> result = new ArrayList<String>();

        for (String name : properties.keySet()) {
            if (isSimpleType(properties.get(name))) {
                result.add(name);
            }
        }
        return result;
    }

    // XXX this is hack. PHP index doesn't return all fanctions from all superclasse
    // see issue #150303
    private static List<String> knownUnrecognizedMethods = new ArrayList();
    static {
        knownUnrecognizedMethods.add("getID");
    }
    /**
     *
     * @param info
     * @param component
     * @param prefix
     * @param includeOnProperties - whether also it should properties which starts on "on"
     * @return key is the name of property and value is the type
     */
    public static Map<String, String> getComponentProperties(PHPIndex index, PHPParseResult phpresult,
            String component, String prefix,
            boolean includeOnProperties, boolean readOnly) {
        Map<String, String> properties = new HashMap<String, String>();
        Collection<IndexedFunction> methods = index.getAllMethods(phpresult, component, "", NameKind.PREFIX, Modifier.PUBLIC); //NOI18N
        Map<String, String> allGetters = new HashMap<String, String>();
        prefix = prefix.toLowerCase();
        String name;
        for (IndexedFunction indexedFunction : methods) {
            name = indexedFunction.getName();
            if ((name.startsWith("get") && indexedFunction.getArgs().length == 0)
                    || knownUnrecognizedMethods.contains(name)) {  //NOI18N
                name = name.substring(3);
                if (name.toLowerCase().startsWith(prefix)) {
                    allGetters.put(name, indexedFunction.getReturnType());
                }
            }
        }
        if (readOnly) {
            return allGetters;
        }
        for (IndexedFunction indexedFunction : methods) {
            if (indexedFunction.getArgs().length == 1) {
                name = indexedFunction.getName();
                if (indexedFunction.getName().startsWith("set")) {
                    name = name.substring(3);
                    if (name.toLowerCase().startsWith(prefix) && !properties.keySet().contains(name) && allGetters.keySet().contains(name)) {
                        properties.put(name, allGetters.get(name));
                    }
                } else if (includeOnProperties && indexedFunction.getName().startsWith("on")) {
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    if (name.toLowerCase().startsWith(prefix.toLowerCase())
                            && !properties.keySet().contains(name)) {
                        properties.put(name, allGetters.get(name));
                    }
                }
            }
        }
        return properties;
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
