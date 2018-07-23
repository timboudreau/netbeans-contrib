/*
 * Parser.java
 *
 * Created on March 31, 2007, 2:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.languages.xml.api;

import org.netbeans.api.languages.Language;
import org.netbeans.api.languages.LanguageDefinitionNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.LanguagesManager;
import org.netbeans.api.languages.ParseException;
import org.netbeans.api.languages.LanguageDefinitionNotFoundException;

/**
 *
 * @author Jan Jancura
 */
public class XMLParser {

    private static XMLParser parser;
    
    public static XMLParser create () throws LanguageDefinitionNotFoundException {
        if (parser == null) {
            parser = new XMLParser ();
        }
        return parser;
    }
    
    
    private Language l;
    
    private XMLParser () throws LanguageDefinitionNotFoundException {
        l = LanguagesManager.get ().getLanguage ("text/xml2");
    }
    
    public XMLRoot parse (InputStream is, String sourceName) throws IOException, ParseException {
        ASTNode n = l.parse (is);
        return new XMLRoot (n);
    }
}
