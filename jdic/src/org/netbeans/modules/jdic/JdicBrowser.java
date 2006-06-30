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
