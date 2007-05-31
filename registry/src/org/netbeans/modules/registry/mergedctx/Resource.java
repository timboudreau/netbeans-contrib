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

package org.netbeans.modules.registry.mergedctx;

import org.openide.util.Utilities;

import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * Encapsulates independent view of hierarchical resource path.
 * Implemented as immutable.
 *
 * @author Radek Matous
 */
final class Resource {
    private final String resourcePath;

    Resource(final String resourcePath) {
        if (resourcePath == null) throw new IllegalArgumentException();
        this.resourcePath = getNormalizedPath(resourcePath);
    }

    boolean isRoot() {
        return (getParent() == null) ? true : false;
    }


    Enumeration getElements() {
        final StringTokenizer sTokens = new StringTokenizer(resourcePath, "/");//NOI18N
        return sTokens;
    }

    boolean isSuperior(final Resource subordered) {
        return subordered.getNormalizedPath().startsWith(getNormalizedPath());
    }

    Resource getChild(final String nameExt) {
        final Resource retVal;
        final StringBuffer sb = new StringBuffer(resourcePath);
        if (!resourcePath.endsWith("/")) sb.append("/");//NOI18N
        sb.append(nameExt);
        retVal = new Resource(sb.toString());
        return retVal;
    }

    Resource getParent() {
        final int idx = resourcePath.lastIndexOf('/');
        if (idx == 0 && resourcePath.length() == 1) return null;
        return new Resource((idx <= 0) ? "/" : resourcePath.substring(0, idx));//NOI18N
    }

    String getName() {
        int idx0 = resourcePath.lastIndexOf('/');//NOI18N
        idx0 = (idx0 == -1) ? 0 : (idx0 + 1);
        return resourcePath.substring(idx0, resourcePath.length());
    }


    String getPath() {
        String retValue = resourcePath;
        //retValue = retValue.replace('/', '/');
        //retValue = retValue.replace('.', '.');
        final int idx = (Utilities.isWindows () || (Utilities.getOperatingSystem () == Utilities.OS_OS2)) ? 1 : 0;
        return retValue.substring(idx);
    }

    /** Adds slash at first position if necessary and removes slash from last position */
    private static String getNormalizedPath(String resPath) {
        if (resPath == null) return resPath;
        resPath = resPath.replace('\\', '/');//NOI18N

        if (!resPath.startsWith("/")) //NOI18N
            resPath = "/" + resPath; //NOI18N

        if (resPath.endsWith("/") && resPath.length() != "/".length()) //NOI18N
            resPath = resPath.substring(0, resPath.length() - 1);

        return resPath;
    }

    public int hashCode() {
        return resourcePath.hashCode();
    }

    public boolean equals(final Object obj) {
        String resPath = null;
        if (obj instanceof String) {
            resPath = (String) obj;
        } else if (obj instanceof Resource) {
            resPath = ((Resource) obj).getNormalizedPath();
        }
        return resourcePath.equals(resPath);
    }

    public String toString() {
        return getNormalizedPath();
    }

    private String getNormalizedPath() {
        return resourcePath;
    }

}
