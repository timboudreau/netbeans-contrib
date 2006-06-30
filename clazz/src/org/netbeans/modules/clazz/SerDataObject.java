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

package org.netbeans.modules.clazz;

import org.openide.util.HelpCtx;
import org.netbeans.modules.classfile.ClassFile;
import org.openide.loaders.InstanceSupport;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.Node;
import org.openide.nodes.CookieSet;
import java.io.InputStream;
import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import java.io.IOException;
import org.openide.util.NbBundle;


/** DataObject which represents JavaBeans (".ser" files).
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Jan Jancura, Ian Formanek, Dafe Simonek
*/
public final class SerDataObject extends ClassDataObject {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 8229229209013849842L;

    /** Constructs a new BeanDataObject */
    public SerDataObject(FileObject fo, ClassDataLoader loader) throws DataObjectExistsException {
        super (fo, loader);
        initCookies();
    }
    
    /** Performs cookie initialization. */
    protected void initCookies () {
        super.initCookies();

        CookieSet cs = getCookieSet();
        cs.add(InstanceCookie.class, this);
    }
    
    /**
     * All serialized objects are treated as JavaBeans since the instance of
     * the object can be obtained (using deserialization).
     * @returns true
     */
    public boolean isJavaBean() {
        return true;
    }

    /** 
     * Creates NodeDelegate for this DataObject
     * @returns node that represent the obejct
    */
    protected Node createNodeDelegate () {
        return new SerDataNode (this);
    }


    protected ClassFile loadClassFile() throws IOException,ClassNotFoundException {
        Class clazz = createInstanceSupport().instanceClass();
        String resourceName='/'+clazz.getName().replace('.','/')+".class"; // NOI18N
        InputStream stream=clazz.getResourceAsStream(resourceName);

        if (stream==null)
            return null;
        try {
            return new ClassFile(stream,false);
        } finally {
            stream.close();
        }
    }

    /**
     * Provides special processing for the serialized objects:
     * if the help can be found for the class that is serialized inside
     * the ser file, returns that help. The default is the help for SerDataObject.
     * @returns appropriate HelpCtx
     */
    public HelpCtx getHelpCtx () {
        HelpCtx test = InstanceSupport.findHelp (createInstanceSupport());
        if (test != null)
            return test;
        else
            return super.getHelpCtx();
    }

    protected FileObject handleRename (String name) throws IOException {
        if (name.indexOf(".")!=-1) { // NOI18N
            throw (IOException)ErrorManager.getDefault().annotate(
            new IOException("Dot in name"), // NOI18N
            ErrorManager.USER,
            null, NbBundle.getMessage(SerDataObject.class, "MSG_INVName"),
            null, null
	    );
        }
        return super.handleRename(name);
    }
}
