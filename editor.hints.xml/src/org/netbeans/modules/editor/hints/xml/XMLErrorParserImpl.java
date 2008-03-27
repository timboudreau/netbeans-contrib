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
package org.netbeans.modules.editor.hints.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.xml.services.UserCatalog;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Severity;
import org.netbeans.spi.editor.hints.support.ErrorParserSupport;
import org.openide.ErrorManager;
import org.openide.xml.EntityCatalog;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 *
 * @author Jan Lahoda
 */
public final class XMLErrorParserImpl extends ErrorParserSupport {
    
    private static final ErrorManager ERR = ErrorManager.getDefault().getInstance(XMLErrorParserImpl.class.getName());
    
    /** Creates a new instance of XMLErrorParserImpl */
    public XMLErrorParserImpl() {
    }

    public List parseForErrors(final Document doc) {
        final List result = new ArrayList();

        try {
            String text = doc.getText(0, doc.getLength());
            XMLReader readerForDTD = createParser(false);
            final boolean[] hasDTD = new boolean[1];
            
            try {
                readerForDTD.setProperty("http://xml.org/sax/properties/lexical-handler", new LexicalHandler() {
                    public void comment(char[] ch, int start, int length) throws SAXException {
                    }
                    public void endCDATA() throws SAXException {
                    }
                    public void endDTD() throws SAXException {
                    }
                    public void endEntity(String name) throws SAXException {
                    }
                    public void startCDATA() throws SAXException {
                    }
                    public void startDTD(String name, String publicId, String systemId) throws SAXException {
                        hasDTD[0] = true;
                    }
                    public void startEntity(String name) throws SAXException {
                    }
                });
                
                readerForDTD.setErrorHandler(new ErrorHandler() {
                    public void warning(SAXParseException exception) throws SAXException {
                        //ignore
                    }

                    public void error(SAXParseException exception) throws SAXException {
                        //ignore
                    }

                    public void fatalError(SAXParseException exception) throws SAXException {
                        //ignore
                    }
                });
                
                readerForDTD.parse(new InputSource(new StringReader(text)));
            } catch (SAXException e) {
                //simply ignore:
                if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
                    ERR.notify(ErrorManager.INFORMATIONAL, e);
                }
            }
            
            XMLReader reader = createParser(hasDTD[0]);
            
            reader.setErrorHandler(new ErrorHandler() {
                public void error(SAXParseException exception) throws SAXException {
                    Severity severity = Severity.ERROR;
                    
                    result.add(ErrorDescriptionFactory.createErrorDescription(severity, exception.getMessage(), doc, exception.getLineNumber()));
                }
                public void fatalError(SAXParseException exception) throws SAXException {
                    Severity severity = Severity.ERROR;
                    
                    result.add(ErrorDescriptionFactory.createErrorDescription(severity, exception.getMessage(), doc, exception.getLineNumber()));
                }
                public void warning(SAXParseException exception) throws SAXException {
                    Severity severity = Severity.WARNING;
                    
                    result.add(ErrorDescriptionFactory.createErrorDescription(severity, exception.getMessage(), doc, exception.getLineNumber()));
                }
            });
            
            reader.parse(new InputSource(new StringReader(text)));
        } catch (SAXException e) {
            //ignore
        } catch (IOException e) {
            //ignore
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        return result;
    }

    private static final class CompoundEntityResolver implements EntityResolver {
        
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            InputSource source = UserCatalog.getDefault().getEntityResolver().resolveEntity(publicId, systemId);
            
            if (source != null) {
                return source;
            }
            
            return EntityCatalog.getDefault().resolveEntity(publicId, systemId);
        }
        
    }
    
    /** 
     * Create and preconfigure new parser. Default implementation uses JAXP.
     * @param validate true if validation module is required
     * @return SAX reader that is used for command performing or <code>null</code>
     */
    protected XMLReader createParser(boolean validate) {
       
        XMLReader ret = null;
        final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";         // NOI18N
        final String XERCES_PROPERTY_PREFIX = "http://apache.org/xml/properties/";      // NOI18N
        
       // JAXP plugin parser (bastarded by core factories!)
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(validate);

        //??? It is Xerces specifics, but no general API for XML Schema based validation exists
        if (validate) {
            try {
                factory.setFeature(XERCES_FEATURE_PREFIX + "validation/schema", validate); // NOI18N
            } catch (Exception ex) {
                //ignore?
            }                
        }
	
        try {
            SAXParser parser = factory.newSAXParser();
            ret = parser.getXMLReader();
        } catch (Exception ex) {
            //ignore
            return null;
        }


        if (ret != null) {
            ret.setEntityResolver(new CompoundEntityResolver());
        }
        
        return ret;
        
    }

}
