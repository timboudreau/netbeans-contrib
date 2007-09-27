/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
