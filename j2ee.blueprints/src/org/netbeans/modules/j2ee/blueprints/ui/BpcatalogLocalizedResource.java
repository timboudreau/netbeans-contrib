/*
 * BpcatalogHtmlResource.java
 *
 * Created on 2005/02/25, 20:43
 */

package org.netbeans.modules.j2ee.blueprints.ui;

import java.net.URL;
import java.util.Locale;

/**
 *
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
