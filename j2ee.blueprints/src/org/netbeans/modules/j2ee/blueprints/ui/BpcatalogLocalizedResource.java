/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
