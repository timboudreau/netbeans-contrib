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

package org.netbeans.modules.j2ee.blueprints.ui;

import java.net.URL;
import java.util.Locale;

/**
 * @author Yutaka Yoshida
 */
public class BpcatalogLocalizedResource {

    private String orgpath;
    private Locale defaultLocale;
    private String lang;
    private String country;
    private String variant;
    private int offset;
    private URL articleURL;

    /** Creates a new instance of BpcatalogHtmlResource */
    public BpcatalogLocalizedResource(String resource, String suffix) {
        orgpath = resource;
        defaultLocale = Locale.getDefault();
        lang = defaultLocale.getLanguage();
        country = defaultLocale.getCountry();
        variant = defaultLocale.getVariant();
        offset = resource.indexOf("."+suffix);
    }
    
    public URL getResourceURL() {
        String path = getResourcePath();
        articleURL = (URL)getClass().getResource(path);
        return articleURL;
    }
    
    public String getResourcePath() {
        StringBuffer path = new StringBuffer(orgpath);
        if (!variant.equals("")) {
            if (!country.equals("")) {
                path.insert(offset, "_"+lang+"_"+country+"_"+variant);
                articleURL = getClass().getResource(path.toString());
                if (articleURL!=null) {
                    return path.toString();
                }
            }
        }
        if (!country.equals("")) {
            // in case path was modified
            path.replace(0, path.length(), orgpath);
            path.insert(offset, "_"+lang+"_"+country);
            articleURL = getClass().getResource(path.toString());
            if (articleURL!=null) {
                return path.toString();
            }
        }
        path.replace(0, path.length(), orgpath);
        path.insert(offset, "_"+lang);
        articleURL = getClass().getResource(path.toString());
        if (articleURL!=null) {
            return path.toString();
        } else {
            return orgpath;
        }
    }
    
}
