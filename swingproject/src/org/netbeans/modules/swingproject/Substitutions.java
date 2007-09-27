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
package org.netbeans.modules.swingproject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 * Replaces strings in content being unzipped to customize the project name to
 * match what the user entered in the wizard.
 *
 * @author Tim Boudreau
 */
final class Substitutions {
    final Properties props;
    final String basePackage;
    final String userEnteredProjectName;
    final String basePath;

    /** Creates a new instance of Substitutions */
    Substitutions(String templateName, String userEnteredProjectName, String basePackage) throws IOException {
        this (new BufferedInputStream (
                Substitutions.class.getResourceAsStream(
                templateName + ".properties")), userEnteredProjectName, basePackage);
    }

    Substitutions (InputStream stream, String userEnteredProjectName, String basePackage) throws IOException {
        if (stream == null) {
            throw new IOException ("Could not find properties file");
        }
        props = new Properties();
        props.load(stream);
        this.userEnteredProjectName = userEnteredProjectName;
        this.basePackage = basePackage;
        this.basePath = Utilities.replaceString(basePackage, ".", "/");
    }


    String substitutePath (String filepath) {
        String result = props.getProperty(filepath);
        if (result != null) {
            result = result.replaceAll("%basepath%", basePath);
        }
        return result == null ? filepath : result;
    }

    InputStream substituteContent (long originalSize, InputStream input, String filename) throws IOException {
        if (filename.endsWith (".gif") || filename.endsWith (".png") || filename.endsWith(".jar")) {
            return input;
        }
        if (originalSize > Integer.MAX_VALUE || originalSize < 0) {
            throw new IllegalArgumentException ("File too large: " +
                    originalSize);
        }
        ByteArrayOutputStream temp = new ByteArrayOutputStream ((int) originalSize);
        FileUtil.copy (input, temp);
        byte[] b = temp.toByteArray();

        //XXX do we want default charset, or UTF-8 - UTF-8 I think...
        CharBuffer cb = Charset.defaultCharset().decode(ByteBuffer.wrap(b));
        String data = cb.toString();

        String user = System.getProperty ("user.name");
        for (Iterator i = props.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String val = props.getProperty(key);
            val = val.replaceAll("%basepackage%", basePackage);
            val = val.replaceAll("%basepath%", basePath);
            val = val.replaceAll("%applicationName%", userEnteredProjectName);
            if (user != null) {
                val = val.replaceAll ("%userName%", user);
            } else {
                val = val.replaceAll("%userName%", "Somebody"); //uh, well...
            }
            Matcher m = Pattern.compile(key).matcher(data);
            data = m.replaceAll(val);
        }

        return new ByteArrayInputStream (data.getBytes());
    }
}

