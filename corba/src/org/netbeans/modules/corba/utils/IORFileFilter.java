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

package org.netbeans.modules.corba.utils;
import java.io.File;
import org.openide.util.NbBundle;
/**
 *
 * @author  tz97951
 */
public class IORFileFilter extends javax.swing.filechooser.FileFilter {
    
    private static final String IOR_EXT = ".ior";   // NOI18N
    
    /** Creates a new instance of IORFileFilter */
    public IORFileFilter() {
    }
    
    /** Whether the given file is accepted by this filter.
     */
    public boolean accept(File f) {
        if (f == null)
            return false;
        if (f.isDirectory ())
            return true;
        return (f.getName ().toLowerCase ().endsWith (IOR_EXT));
    }    
    
    /** The description of this filter. For example: "JPG and GIF Images"
     * @see FileView#getName
     */
    public String getDescription() {
        return NbBundle.getMessage (IORFileFilter.class, "TXT_IORFile");
    }    
    
}
