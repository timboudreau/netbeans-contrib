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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.support.ErrorParserSupport;
import org.openide.ErrorManager;
import org.openide.xml.XMLUtil;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Jan Lahoda
 */
public class XMLErrorParserImpl extends ErrorParserSupport {
    
    /** Creates a new instance of XMLErrorParserImpl */
    public XMLErrorParserImpl() {
    }

    public List parseForErrors(final Document doc) {
        final List result = new ArrayList();

        try {
            XMLUtil.parse(new InputSource(new StringReader(doc.getText(0, doc.getLength()))), true, true, new ErrorHandler() {
                public void error(SAXParseException exception) throws SAXException {
                    result.add(ErrorDescriptionFactory.createErrorDescription(ErrorDescription.SEVERITY_ERROR, exception.getMessage(), doc, exception.getLineNumber()));
                }
                public void fatalError(SAXParseException exception) throws SAXException {
                    result.add(ErrorDescriptionFactory.createErrorDescription(ErrorDescription.SEVERITY_ERROR, exception.getMessage(), doc, exception.getLineNumber()));
                }
                public void warning(SAXParseException exception) throws SAXException {
                    result.add(ErrorDescriptionFactory.createErrorDescription(ErrorDescription.SEVERITY_WARNING, exception.getMessage(), doc, exception.getLineNumber()));
                }
            }, null);
        } catch (SAXException e) {
            //ignore
        } catch (IOException e) {
            //ignore
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        return result;
    }
    
}
