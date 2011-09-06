/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.yii;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.modules.php.yii.extensions.api.YiiProjectConfiguration;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 *
 * @author gevik@netbeans.org
 */
public class YiiProjectConfigurationImpl implements YiiProjectConfiguration {

    static final Logger LOGGER = Logger.getLogger(YiiProjectConfigurationImpl.class.getName());
    private List<String> preloadedClassNames;
    private List<String> appParams;
    private String appname;
    private STGroup stg;
    private ST php_array_item;

    public YiiProjectConfigurationImpl() {
        URL url = YiiProjectConfigurationImpl.class.getResource("/org/netbeans/modules/php/yii/ui/resources/config.stg");
        stg = new STGroupFile(url, "UTF-8", '<', '>');
               
        preloadedClassNames = new ArrayList<String>();
        preloadedClassNames.add("log");
        
        appParams = new ArrayList<String>();
        appParams.add(STArrayItem("adminEmail", "webmaster@example.com", true));
        
        appname = "My Web Application";
    }

    public void setAppName(String name) {
        appname = name;
    }

    public String getAppName() {
        return appname;
    }

    public List<String> getApplicationParameters() {
        return appParams;
    }

    public List<String> getPreLoadedClassNames() {
        return preloadedClassNames;
    }

    public void renderTo(final FileObject configFile) {
        ST config_file = stg.getInstanceOf("config_file");
        config_file.add("items", STArrayItem("name", getAppName(), true));
        config_file.add("items", STArrayItem("preload", preloadedClassNames, true));
        config_file.add("items", STArrayItem("params", appParams, false));
        try {
            FileWriter fileWriter = new FileWriter(configFile.getPath());
            fileWriter.write(config_file.render());
            fileWriter.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private String STArrayItem(String name, List values, boolean isstring) {
        ArrayList<String> v = new ArrayList<String>();
        for (Object item : values) {
            v.add(isstring ? "'" + item.toString() + "'" : item.toString());
        }
        php_array_item = stg.getInstanceOf("php_array");
        php_array_item.add("name", "'" + name + "'");
        php_array_item.add("items", v);
        return php_array_item.render();
    }

    private String STArrayItem(String name, String value, boolean isstring) {
        php_array_item = stg.getInstanceOf("php_array_item");
        php_array_item.add("name", "'" + name + "'");
        php_array_item.add("value", isstring ? "'" + value + "'" : value);
        return php_array_item.render();
    }
}
