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

package org.netbeans.modules.j2ee.sun.ws7.serverresources.loaders;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.WS70WizardConstants;

/** Recognizes single files in the Repository as being of a certain type.
 *
 * Code reused from Appserver common API module 
 * 
 */
public class SunWS70ResourceDataLoader extends UniFileLoader {
    
    private static final long serialVersionUID = 1L;
    
    public SunWS70ResourceDataLoader() {
        this("org.netbeans.modules.j2ee.sun.ws7.serverresources.loaders.SunWS70ResourceDataObject"); //NOI18N
    }
    
    // Can be useful for subclasses:
    protected SunWS70ResourceDataLoader(String recognizedObjectClass) {
        super(recognizedObjectClass);
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(SunWS70ResourceDataLoader.class, "LBL_loaderName"); //NOI18N
    }
    
    protected void initialize() {
        super.initialize();
        
        ExtensionList extensions = new ExtensionList();
        extensions.addExtension(WS70WizardConstants.__SunResourceExt);
        setExtensions(extensions);
    }
    
    protected String actionsContext () {
        return "Loaders/xml/sun-ws7-resource/Actions/"; // NOI18N
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new SunWS70ResourceDataObject(primaryFile, this);
    }
    
}
