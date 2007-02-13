/*
 * Context.java
 *
 * Created on February 13, 2007, 10:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.visualweb.project.importpage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.w3c.dom.Document;

/**
 *
 * @author joelle
 */
public class ImportContext {
    
    private static ImportContext importContext = null;
    
    /** Creates a new instance of Context */
    public ImportContext() {
    }
    
    public static ImportContext getInstance() {
        if (importContext == null) {
            importContext = new ImportContext();
        } 
        return importContext;         
    }
    
        boolean fragment;
        Document parsedDocument;
        Project project;
        FileObject webformFile;
        DataObject webformDobj;
        Map paletteComponents;
        ArrayList formsAdded;
        Set names;
        ArrayList tagLibs;
        boolean haveOldJsp;
        URL base;
        URL fullUrl;
        boolean warnMissingFile = true;
        HashMap resources;
        boolean copyResources;
        Map nameSpaces;
    
}
