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

package org.netbeans.modules.projectpackager.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 * Tools for executing of ant scripts
 * @author Roman "Roumen" Strobl
 */
public class ExecutionTools {
    
    private ExecutionTools() {
    }
    
    /**
     * Initialize an ant script according to the resource
     * @param resource resource from layer.xml
     * @return created temporary script
     * @throws java.io.IOException thrown when could not create script
     */
    public static FileObject initScript(String resource) throws IOException {
        FileObject script;
        FileObject sfo = Repository.getDefault().getDefaultFileSystem().findResource(resource);
        File sf = File.createTempFile("project-packager","xml");
        sf.deleteOnExit();
        InputStream in = sfo.getInputStream();
        
        FileWriter out = new FileWriter(sf);
        int c;
        while ((c = in.read()) != -1)
            out.write(c);
        in.close();
        out.close();
        
        return FileUtil.toFileObject(FileUtil.normalizeFile(sf));                
    }
    
}
