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
import java.util.Date;

/**
 *
 * @author Tim Boudreau
 */
public class ModuleInfoModel implements FileModel {
    private final ModuleModel module;
    private final Date date = new Date();
    private final String license;
    private final String author;
    private final String homepage;

    /** Creates a new instance of ModuleInfoModel */
    public ModuleInfoModel(ModuleModel module, String homepage, String author, String license) {
        this.module = module;
        this.author = author == null ? "" : author;
        this.homepage = homepage == null ? "" : homepage;
        this.license = license == null ? "No license" : license;
    }

    public String getPath() {
        return "Info/info.xml";
    }
    
    private String getDateString() {
        int y = date.getYear() + 1900;
        int m = date.getMonth() + 1;
        int d = date.getDay() + 1;
        return y + "/" + twoDigit (m) + "/" + twoDigit (d);
    }
    
    private static String twoDigit (int i) {
        StringBuffer result = new StringBuffer();
        result.append (i);
        if (result.length() == 1) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    public void write(OutputStream stream) throws IOException {
        stream.write (toString().getBytes("UTF-8"));
    }
    
    private static final String MI_HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
     "<!DOCTYPE module PUBLIC \"-//NetBeans//DTD Autoupdate Module Info 2.3//EN\" " +
            "\"http://www.netbeans.org/dtds/autoupdate-info-2_3.dtd\">\n";
    
    public String toString() {
        StringBuffer sb = new StringBuffer (MI_HEADER);
        String indent = "        ";
        sb.append ("<module codenamebase=\"" + module.getCodeName() + "\"\n");
        sb.append (indent);
        sb.append ("homepage=\"");
        sb.append (homepage);
        sb.append ("\"\n");
        sb.append (indent);
        sb.append ("distribution=\"\"\n");
        sb.append (indent);
        sb.append ("license=\"license.txt\"\n");
        sb.append (indent);
        sb.append ("downloadsize=\"0\"\n");
        sb.append (indent);
        sb.append ("needsrestart=\"false\"\n");
        sb.append (indent);
        sb.append ("moduleauthor=\"");
        sb.append (author);
        sb.append ("\"\n");
        sb.append (indent);
        sb.append ("releasedate=\"");
        sb.append (this.getDateString());
        sb.append ("\"\n");
        sb.append (">\n");
        sb.append (module.getManifestXML());
        sb.append ('\n');
        sb.append ("<license name=\"license.txt\"><![CDATA[");
        sb.append (license);
        sb.append ("]]></license>\n</module>");
        return sb.toString();
    }
    
    /*
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE module PUBLIC "-//NetBeans//DTD Autoupdate Module Info 2.3//EN" "http://www.netbeans.org/dtds/autoupdate-info-2_3.dtd">
<module codenamebase="org.yourorghere.testbeanmodule"
        homepage="http://www.netbeans.org/about/legal"
        distribution=""
        license="license.txt"
        downloadsize="0"
        needsrestart="false"
        moduleauthor="Joe Blow"
        releasedate="2006/03/29"
>
  <manifest OpenIDE-Module="org.yourorghere.testbeanmodule"
            OpenIDE-Module-Implementation-Version="060329"
            OpenIDE-Module-Name="TestBeanModule"
            OpenIDE-Module-Requires="org.openide.modules.ModuleFormat1"
            OpenIDE-Module-Specification-Version="1.0"
  />
  <license name="license.txt"><![CDATA[This code is licensed under the Hellish Public License.
If you use it, your head will explode.
]]></license>
</module>
     */    
}
