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
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

final class EntityResolver2URIResolver implements URIResolver {
    private final EntityResolver resolver;
    public EntityResolver2URIResolver(EntityResolver resolver) {
        this.resolver = resolver;
    }
    public Source resolve(String href, String base) throws TransformerException {
        System.err.println("DO RESOLVE HREF " + href + " BASE " + base);
        try {
            String abs = new URL(new URL(base), href).toExternalForm();
            InputSource s = resolver.resolveEntity(null, abs);
            if (s != null) {
                //err.println(href + " in " + base + " -> " + s.getSystemId());
                return new StreamSource(s.getSystemId());
            }  else {
                //err.println(href + " in " + base + " -> zip");
                return null;
            }
        }  catch (SAXException e) {
            throw new TransformerException(e);
        }  catch (IOException e) {
            throw new TransformerException(e);
        }
    }
}    