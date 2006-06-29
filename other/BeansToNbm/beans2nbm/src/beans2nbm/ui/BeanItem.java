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
