/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.projectsettings;

import java.util.Map;
import java.util.HashMap;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import org.openide.TopManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.Environment;
import org.openide.loaders.XMLDataObject;
import org.openide.util.Lookup;
import org.openide.util.SharedClassObject;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author  Martin Entlicher
 */
class VcsEnvironmentProvider extends SharedClassObject implements Environment.Provider {

    private static transient Map envs = new HashMap();

    private static final long serialVersionUID = 192853893444572L;

    /** Returns a lookup that represents environment.
     * @return the lookup
     */
    public final Lookup getEnvironment(DataObject obj) {
        
        // the obj check is done by core FileEntityResolver that calls us
        
        // we want to create just one instance per FileObject

        FileObject file = obj.getPrimaryFile();
        Lookup lookup = (Lookup) envs.get(file);
        if (lookup == null) {
            lookup = createLookup(obj);
            envs.put(file, lookup);
        }
        return lookup;
    }
    
    /**
     * It is called exactly once per DataObject.
     *
     * @return content of assigned Lookup
     */
    protected InstanceContent createInstanceContent(DataObject obj) throws IllegalArgumentException, java.io.IOException, SAXException {
        if (!(obj instanceof XMLDataObject)) throw new IllegalArgumentException("XML data object required."); // NOI18N
        Document doc = ((XMLDataObject) obj).getDocument();
        //FileObject fo = obj.getPrimaryFile();
        InstanceContent ic = new InstanceContent();
        ic.add((InstanceCookie) new CommandLineVcsFileSystemInstance(obj.getPrimaryFile(), doc));
        return ic;
    }
    
    /**
     * It is called exactly once per DataObject.
     *
     * @return Lookup containing <tt>createInstanceContent()</tt>
     */
    protected Lookup createLookup(DataObject obj) {
        InstanceContent ic = null;
        try {
            ic = createInstanceContent(obj);
        } catch (IllegalArgumentException iaExc) {
            TopManager.getDefault().getErrorManager().notify(iaExc);
        } catch (java.io.IOException ioExc) {
            TopManager.getDefault().getErrorManager().notify(ioExc);
        } catch (SAXException sExc) {
            TopManager.getDefault().getErrorManager().notify(sExc);
        }
        Lookup lookup = new AbstractLookup(ic);
        if (lookup.lookup(InstanceCookie.class) == null) {
            TopManager.getDefault().getErrorManager().notify(new IllegalStateException());  // instance cookie required
        }
        return lookup;
    }

}
