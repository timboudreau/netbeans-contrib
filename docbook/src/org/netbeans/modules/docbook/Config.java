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

package org.netbeans.modules.docbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration of the DocBook & Slides libs.
 * @author Jesse Glick
 */
final class Config {

    private Config() {}

    public static final String DOCBOOK_XSL_VERSION;
    public static final String SLIDES_VERSION;
    public static final String DOCBOOK_XML_VERSION;
    public static final String[] BROWSER_FILES;
    public static final String[] GRAPHICS_FILES;

    static {
        Properties p = new Properties();
        try {
            InputStream is = Config.class.getResourceAsStream("lib/config.properties");
            try {
                p.load(is);
            } finally {
                is.close();
            }
            DOCBOOK_XSL_VERSION = p.getProperty("docbook-xsl.version");
            DOCBOOK_XML_VERSION = p.getProperty("docbook-xml.version");
            SLIDES_VERSION = p.getProperty("slides.version");
            BROWSER_FILES = p.getProperty("browser.files").split(",");
            GRAPHICS_FILES = p.getProperty("graphics.files").split(",");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
}
