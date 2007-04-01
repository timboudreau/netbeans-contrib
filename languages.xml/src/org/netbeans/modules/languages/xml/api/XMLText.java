/*
 * XMLRoot.java
 *
 * Created on March 31, 2007, 2:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.languages.xml.api;


/**
 *
 * @author Jan Jancura
 */
public class XMLText extends XMLItem {
    
    private String text;
    
    XMLText (String text) {
        this.text = text;
    }
    
    public String getText () {
        return text;
    }
}
