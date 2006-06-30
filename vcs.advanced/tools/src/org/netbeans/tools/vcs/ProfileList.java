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

package org.netbeans.tools.vcs;

import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.*;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * List content of profile definition. Takes
 * the profile file and outputs all defined
 * variables, commadns and global commands.
 *
 * @author  Petr Kuzel
 */

public final class ProfileList {

    public static void main(String args[]) throws Exception {
        if (args.length != 1) {
            System.err.println("Profile filename parameter required.");
            System.exit(1);
        }

        String cwd = System.getProperty("user.dir");
        File cwdf = new File(cwd);
        File profile = new File(cwdf, args[0]);

        if (profile.exists() == false || profile.canRead() == false) {
            System.err.println("Cannot read " + profile.toString());
            System.exit(2);
        }

        XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler(new ParserListener());
        parser.setEntityResolver(new EntityResolver() {

            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if ("-//NetBeans//DTD VCS Configuration 1.1//EN".equals(publicId)) {
                    InputSource in = new InputSource();
                    in.setSystemId(systemId);
                    in.setCharacterStream(new StringReader(""));
                    return in;
                }
                return null;
            }
        });
        parser.parse(profile.toURI().toString());
    }

    private static class ParserListener implements ContentHandler {

        private int state = 0;
        private static final int VARIABLES = 1;
        private static final int COMMANDS = 2;
        private static final int GLOBAL_COMMANDS = 3;

        public void setDocumentLocator(Locator locator) {
        }

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        public void endPrefixMapping(String prefix) throws SAXException {
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            if ("variables".equals(qName)) {
                println("Variables:");
                state = VARIABLES;
            } else if ("commands".equals(qName)) {
                println("Commands:");
                state = COMMANDS;
            } else if ("globalCommands".equals(qName)) {
                println("Global commands:");
                state = GLOBAL_COMMANDS;
            } else if ("variable".equals(qName)) {
                println("\t" + atts.getValue("name"));
            } else if ("command".equals(qName)) {
                println("\t" + atts.getValue("name"));
            }


        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        }

        public void characters(char ch[], int start, int length) throws SAXException {
        }

        public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
        }

        public void processingInstruction(String target, String data) throws SAXException {
        }

        public void skippedEntity(String name) throws SAXException {
        }
    }

    private static void println(String line) {
        System.out.println(line);
    }
}