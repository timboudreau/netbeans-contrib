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
import java.util.Stack;
import java.util.StringTokenizer;

import org.openide.filesystems.FileObject;

/*
 * @author Dusan Balek
 */

public class FileUtils {
    
    public static String getRealFileName (FileObject fo) {
        try {
            String __filesystem = fo.getFileSystem ().getDisplayName ();
            String __file_name = fo.getPackageNameExt (File.separatorChar, '.');
            if (__file_name != null && __file_name.length() > 0)
                return __filesystem + File.separator + __file_name;
            else
                return __filesystem;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static String getRealPackageName (FileObject fo) {
        try {
            return getRealFileName (fo.getParent ());
        }
        catch (Exception e) {
            return null;
        }
    }        
    
    public static String convert2Canonical (String path) {
        String __canonical = "";
        try {
            Stack __stack = new Stack();
            StringTokenizer __st = new StringTokenizer(path, "/\\");
            while (__st.hasMoreTokens()) {
                String __token = __st.nextToken();
                if (__token.equals(".."))
                    __stack.pop();
                else if (!__token.equals("."))
                    __stack.push(__token);
            }
            if (!__stack.isEmpty()) {
                int __size = __stack.size()-1;
                for (int i = 0; i < __size; i++)
                    __canonical += __stack.get(i) + File.separator;
                __canonical += __stack.get(__size);
            }
        }
        catch (Exception e) {
        }
        return __canonical;
    }
}
