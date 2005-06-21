/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Leon Chiver. All Rights Reserved.
 */

package org.netbeans.modules.editor.java.doclet.ejb;

import java.io.IOException;
import java.io.InputStream;
import org.netbeans.modules.editor.java.doclet.support.CompletionPrefixSettings;
import org.netbeans.modules.editor.java.doclet.support.XMLDocletDescriptor;
import org.openide.util.SharedClassObject;
import org.xml.sax.SAXException;

/**
 * @author leon chiver
 */
public class EjbDescriptor extends XMLDocletDescriptor {
   
    public EjbDescriptor() throws IOException, SAXException {
    }
    
    public InputStream getDescriptorInputStream() {
        return EjbDescriptor.class.getResourceAsStream("completion.xml");
    }
    
    public CompletionPrefixSettings getCompletionPrefixSettings() {
        return (EjbDocletSettings) SharedClassObject.findObject(EjbDocletSettings.class, true);
    }
}
