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

package org.netbeans.modules.j2ee.blueprints.catalog;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Bpcatalog;
import org.xml.sax.SAXException;

/**
 * Singleton for accessing the solutions catalog.
 *
 * @author Mark Roth
 */
public class SolutionsCatalog {

    private static SolutionsCatalog theInstance = null;
    private static final String BPCATALOG_XML_PATH =
        "/org/netbeans/modules/j2ee/blueprints/bpcatalog.xml"; // NOI18N
    
    /** Parsed representation of bpcatalog.xml */
    private Bpcatalog bpcatalogXml = null;
    
    /** Private Singleton constructor */
    private SolutionsCatalog() {}

    public static SolutionsCatalog getInstance() {
        if(theInstance == null) {
            createInstance();
        }
        return theInstance;
    }
    
    private synchronized static void createInstance() {
        if(theInstance == null) {
            theInstance = new SolutionsCatalog();
        }
    }
    
    public Bpcatalog getBpcatalogXml() {
        if(bpcatalogXml == null) {
            // Lazily parse bpcatalog.xml
            parseBpcatalogXml();
        }
        return bpcatalogXml;
    }
    
    private synchronized void parseBpcatalogXml() {
        if(this.bpcatalogXml == null) {
            this.bpcatalogXml = new Bpcatalog();
            try {
                InputStream in = getClass().getResourceAsStream(BPCATALOG_XML_PATH);
                this.bpcatalogXml = Bpcatalog.read(in);
                in.close();
            }
            catch(ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            catch(SAXException e) {
                throw new RuntimeException(e);
            }
            catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
