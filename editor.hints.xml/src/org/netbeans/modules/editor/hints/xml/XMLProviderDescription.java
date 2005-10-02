
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
package org.netbeans.modules.editor.hints.xml;

import java.util.Arrays;
import java.util.List;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ProviderDescription;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Lahoda
 */
public class XMLProviderDescription implements ProviderDescription {
    
    public static final String KEY_XML_FATAL_ERROR = "fatal-error";
    public static final String KEY_XML_ERROR       = "error";
    public static final String KEY_XML_WARNING     = "warning";
    
    public static final String XML_ERROR_PROVIDER  = "org-netbeans-modules-editor-hints-xml";
    
    /**
     * Creates a new instance of XMLProviderDescription
     */
    public XMLProviderDescription() {
    }
    
    public String getKey() {
        return XML_ERROR_PROVIDER;
    }

    public String getDisplayName() {
        return NbBundle.getMessage(XMLProviderDescription.class, "LBL_Error_Provider");
    }

    public boolean getDefaultState() {
        return true;
    }

    public List getSupportedErrorKeys() {
        return Arrays.asList(new String[] {
            KEY_XML_FATAL_ERROR,
            KEY_XML_ERROR,
            KEY_XML_WARNING,
        });
    }

    public String getErrorDisplayName(String key) {
        return NbBundle.getMessage(XMLProviderDescription.class, "LBL_Error-" + key);
    }

    public int getErrorDefaultSeverity(String key) {
        if (KEY_XML_WARNING.equals(key))
            return ErrorDescription.SEVERITY_WARNING;
        
        return ErrorDescription.SEVERITY_ERROR;
        
    }

    
}
