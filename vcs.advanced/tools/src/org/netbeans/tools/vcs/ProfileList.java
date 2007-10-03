/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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