/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * BundleLookup.java
 *
 * Created on January 11, 2002, 1:09 PM
 */

package org.netbeans.modules.jemmysupport.bundlelookup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.MissingResourceException;
import javax.swing.table.DefaultTableModel;
import org.openide.TopManager;
import org.openide.filesystems.*;

/** Class performing lookup through all .properties files in all mounted filesystems
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.2
 */
public class BundleLookup {
    
    static boolean compareX(String s1, String s2, boolean cs, boolean sub) {
        if (cs) {
            if (sub) {
                return s2.indexOf(s1)>=0;
            } else {
                return s2.equals(s1);
            }
        } else {
            if (sub) {
                s2=s2.toLowerCase();
                return s2.indexOf(s1)>=0;
            } else {
                return s2.equalsIgnoreCase(s1);
            }
        }
    }

    /** Method performing lookup through all .properties files in all mounted filesystems
     * @param table DefaultTableModel showing the results
     * @param text text to search for
     * @param caseSensitiveText case sensitive switch
     * @param substringText substring switch
     * @param bundle resource bundle filter
     * @param caseSensitiveBundle case sensitive filter switch
     * @param substringBundle substring filter switch
     */    
    public static void lookupText(DefaultTableModel table, String text, boolean caseSensitiveText, boolean substringText, String bundle, boolean caseSensitiveBundle, boolean substringBundle) {
        Enumeration filesystems=TopManager.getDefault().getRepository().getFileSystems();
        FileObject fo;
        ArrayList queue=new ArrayList();
        String name, key;
        Properties res;
        Enumeration keys;
        FileObject ch[];
        while (filesystems.hasMoreElements()) {
            fo=((FileSystem)filesystems.nextElement()).getRoot();
            if (fo.isValid()) {
                queue.add(fo);
            }
        }
        while (!queue.isEmpty()) {
            Thread.yield();
            fo=(FileObject)queue.remove(0);
            if ("properties".equalsIgnoreCase(fo.getExt())) {
                name=fo.getPackageName('.');
                try {
                    if (compareX(bundle,name,caseSensitiveBundle,substringBundle)) {
                        res = new Properties();
                        res.load(fo.getInputStream());
                        keys = res.keys();
                        while (keys.hasMoreElements()) {
                            try {
                                key = (String)keys.nextElement();
                                if (compareX(text,res.getProperty(key),caseSensitiveText,substringText)) {
                                    table.addRow(new String[]{name, key, res.getProperty(key).replace('\n','/')});
                                }
                            } catch (ClassCastException cce) {}
                        }
                    }
                } catch (FileNotFoundException fnfe) {
                } catch (IOException ioe) {}
            }
            ch=fo.getChildren();
            for (int i=0;i<ch.length;i++) {
                queue.add(ch[i]);
            }
        }
    }

}
