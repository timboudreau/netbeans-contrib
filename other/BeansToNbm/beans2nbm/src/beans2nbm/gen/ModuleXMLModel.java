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
package beans2nbm.gen;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Tim Boudreau
 */
public class ModuleXMLModel implements FileModel {
    private final String moduleName;
    private final String version;

    /** Creates a new instance of ModuleXMLModel */
    public ModuleXMLModel(String moduleNameDots, String version) {
        this.moduleName = moduleNameDots;
        this.version = version;
    }

    private String moduleNameDashes() {
        char[] c = moduleName.toCharArray();
        for (int i=0; i < c.length; i++) {
            if (c[i] == '.') {
                c[i] = '-';
            }
        }
        return new String (c);
    }
    
    private static final String MXML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
          "<!DOCTYPE module PUBLIC \"-//NetBeans//DTD Module Status 1.0//EN\"" +
                        " \"http://www.netbeans.org/dtds/module-status-1_0.dtd\">\n";
    /*
<module name="org.yourorghere.testbeanmodule">
    <param name="autoload">false</param>
    <param name="eager">false</param>
    <param name="enabled">true</param>
    <param name="jar">modules/org-yourorghere-testbeanmodule.jar</param>
    <param name="reloadable">false</param>
    <param name="specversion">1.0</param>
</module>
     */

    public String getPath() {
        return "netbeans/config/Modules/" + moduleNameDashes() + ".xml";
    }

    public void write(OutputStream stream) throws IOException {
        stream.write (toString().getBytes("UTF-8"));
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer (MXML_HEADER);
        buf.append("<module name=\"");
        String indent = "    ";
        buf.append (moduleName);
        buf.append ("\">\n");
        buf.append (indent);
        buf.append ("<param name=\"eager\">false</param>\n");
        buf.append (indent);
        buf.append ("<param name=\"enabled\">true</param>\n");
        buf.append (indent);
        buf.append ("<param name=\"jar\">modules/");
        buf.append (moduleNameDashes());
        buf.append (".jar</param>\n");
        buf.append (indent);
        buf.append ("<param name=\"reloadable\">false</param>\n");
        buf.append (indent);
        buf.append ("<param name=\"specversion\">" + version + "</param>\n");
        buf.append ("</module>\n");
        return buf.toString();
    }
}
