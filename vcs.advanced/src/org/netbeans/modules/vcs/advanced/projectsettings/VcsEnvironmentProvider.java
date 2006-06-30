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

package org.netbeans.modules.vcs.advanced.projectsettings;

import java.util.Map;
import java.util.HashMap;
import org.openide.ErrorManager;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;

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
        InstanceCookie icookie = (InstanceCookie)
            new CommandLineVcsFileSystemInstance(obj.getPrimaryFile(), doc, ic);
        ic.add(icookie);
        ic.add(new CommandLineVcsFileSystemNode((XMLDataObject) obj, icookie));
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
            ErrorManager.getDefault().notify(iaExc);
        } catch (java.io.IOException ioExc) {
            ErrorManager.getDefault().notify(ioExc);
        } catch (SAXException sExc) {
            ErrorManager.getDefault().notify(sExc);
        }
        Lookup lookup = new AbstractLookup(ic);
        if (lookup.lookup(InstanceCookie.class) == null) {
            ErrorManager.getDefault().notify(new IllegalStateException());  // instance cookie required
        }
        return lookup;
    }

}
