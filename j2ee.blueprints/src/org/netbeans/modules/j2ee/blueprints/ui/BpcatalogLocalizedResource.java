/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
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
        StringBuffer path = new StringBuffer(orgpath);
        if (!variant.equals("")) {
            if (!country.equals("")) {
                path.insert(offset, "_"+lang+"_"+country+"_"+variant);
                articleURL = getClass().getResource(path.toString());
                if (articleURL!=null) {
                    return articleURL;
                }
            }
        }
        if (!country.equals("")) {
            // in case path was modified
            path.replace(0, path.length(), orgpath);
            path.insert(offset, "_"+lang+"_"+country);
            articleURL = getClass().getResource(path.toString());
            if (articleURL!=null) {
                return articleURL;
            }
        }
        path.replace(0, path.length(), orgpath);
        path.insert(offset, "_"+lang);
        articleURL = getClass().getResource(path.toString());
        if (articleURL!=null) {
            return articleURL;
        } else {
            return (URL)getClass().getResource(orgpath);
        }
    }
    
}
