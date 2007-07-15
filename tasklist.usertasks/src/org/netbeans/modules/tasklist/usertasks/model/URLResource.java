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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.model;

import java.net.MalformedURLException;
import java.net.URL;
import org.openide.awt.HtmlBrowser;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 * URL.
 *
 * @author tl
 */
public class URLResource implements UserTaskResource {
    private URL url;
    
    /**
     * Constructor.
     * 
     * @param url associated URL 
     */
    public URLResource(URL url) {
        this.url = url;
    }

    @Override
    public URLResource clone() {
        try {
            URLResource copy = (URLResource) super.clone();
            copy.url = new java.net.URL(this.url.toString());
            return copy;
        } catch (CloneNotSupportedException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    /**
     * Returns the associated URL.
     * 
     * @return URL 
     */
    public URL getUrl() {
        return url;
    }
    
    public String getDisplayName() {
        return url.toExternalForm();
    }

    public void open() {
        FileObject fo = URLMapper.findFileObject(url);
        if (fo == null)        
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        else {
            try {
                DataObject do_ = DataObject.find(fo);
                OpenCookie oc = do_.getCookie(OpenCookie.class);
                if (oc != null)
                    oc.open();
            } catch (DataObjectNotFoundException ex) {
                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
            }
        }
    }
}
