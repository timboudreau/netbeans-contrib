/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package beans2nbm.ui;

import beans2nbm.gen.*;

/**
 *
 * @author Tim Boudreau
 */
public class BeanItem {
    private String path;
    /** Creates a new instance of BeanItem */
    public BeanItem(String pathInJar) {
        this.path = pathInJar;
        assert path.endsWith(".class");
    }
    
    public String getPath() {
        return path;
    }
    
    public String getClassName() {
        StringBuffer sb = new StringBuffer (path.substring(0, path.length() - ".class".length()));
        for (int i=0; i < sb.length(); i++) {
            if (sb.charAt(i) == '/') {
                sb.setCharAt(i, '.');
            }
        }
        return sb.toString();
    }
    
    public String getPackageName() {
        String s = getClassName();
        int ix = s.lastIndexOf('.');
        if (ix > 0) {
            return s.substring(0, ix);
        } else {
            return s;
        }
    }
    
    public String getSimpleName() {
        String s = getClassName();
        int ix = s.lastIndexOf(".");
        if (ix <= 0) {
            return s;
        } else if (ix < s.length() - 1) {
            return s.substring(ix + 1);
        } else {
            return s;
        }
    }
    
    public String toString() {
        return getSimpleName();
    }
    
    public boolean equals (Object o) {
        return o instanceof BeanItem && ((BeanItem) o).path.equals(path);
    }
    
    public int hashCode() {
        return path.hashCode() * 31;
    }
    
}
