
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
