/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jdic;

import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

/**
 * @author Martin Grebac
 */
public class JdicBrowser implements HtmlBrowser.Factory, java.io.Serializable {

    private static final long serialVersionUID = -6L;
    
    public JdicBrowser() {
    }

    /** Getter for browser name
     *  @return name of browser
     */
    public String getName () {
        return NbBundle.getMessage(JdicBrowser.class, "CTL_JdicBrowserName");
    }
    
    /**
     * Returns a new instance of BrowserImpl implementation.
     * @throws UnsupportedOperationException when method is called and OS is not Windows.
     * @return browserImpl implementation of browser.
     */
    public HtmlBrowser.Impl createHtmlBrowserImpl() {
        JdicBrowserImpl impl = null;
        impl = new JdicBrowserImpl();
        return impl;
    }
            
    private void readObject (java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject();
    }
    
}
