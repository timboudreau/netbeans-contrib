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
package org.netbeans.modules.vcscore.turbo;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.ErrorManager;
import org.netbeans.modules.vcscore.VcsAttributes;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Represents .ignorelist or equivalent listing files that
 * should not be versioned in repository. It's friend
 * client of Turbo. It uses package private FileProperties
 * attribute.
 *
 * TODO add support for DISK and REFRESH levels, current impl looks like DISK_OR_REFRESH
 *
 * @author Petr Kuzel
 */
public final class IgnoreList {

    // buffered regular expression, for efficiency reasons
    private Pattern regExp;

    /** list of expression strings or null. */
    private final List ignoreList;

    private IgnoreList(List elements) {
        ignoreList = elements;
    }

    /**
     * Attribute name holding local ignore list.
     * Attribute value is List of elements <String> or null if
     * a command must be executed to get proper ignore list.
     */
    public static final String ID = "VCS-IgnoreList";  // NOI18N

    /**
     * Finds ignore list for specified folder either in
     * cache or connect server.
     */
    public static IgnoreList forFolder(FileObject folder) {

        if (folder == null) new IgnoreList(null);;
        assert folder.isFolder() : "Ignore list for " + folder;

        try {
            return getIgnoreList(folder);
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(e);
        }
        return new IgnoreList(null);
    }

    /**
     * @param name path to file in test
     */
    public boolean isIgnored(String name) {
        if (ignoreList == null) {
            return false;
        }
        //System.out.println("isIgnored("+name+"), ignoreList = "+org.netbeans.modules.vcscore.util.VcsUtilities.arrayToString((String[]) ignoreList.toArray(new String[0])));
        //System.out.println(" regExp = "+regExp);
        if (regExp == null) {
            String unionExp = VcsUtilities.computeRegularExpressionFromIgnoreList(ignoreList);
            try {
                regExp = Pattern.compile(unionExp);
                //System.out.println(" **** GOT reg EXP: '"+unionExp+"' *********");
            } catch (PatternSyntaxException malformedRE) {
                try {
                    regExp = Pattern.compile(""); // epsilon, no regular file match epsilon // NOI18N
                } catch (PatternSyntaxException innerMalformedRE) {
                }
            }
        }
        //System.out.println(regExp+".match("+name+") = "+regExp.match(name));
        return regExp.matcher(name).matches();

    }

    /** Invalidates last cached value. */
    public static void invalidate(FileObject folder) {
        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        FolderProperties fprops = (FolderProperties) faq.readAttribute(folder, FolderProperties.ID);
        if (fprops != null) {
            fprops.setIgnoreList(null);
        }
        faq.writeAttribute(folder, IgnoreList.ID, null);
    }

    private static IgnoreList getIgnoreList(FileObject folder) throws FileStateInvalidException {

        // consult cache

        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        FolderProperties fprops = (FolderProperties) faq.readAttribute(folder, FolderProperties.ID);

        if (fprops != null) {
            IgnoreList ilist = fprops.getIgnoreList();
            if (ilist != null) {
                return ilist;
            }
        }

        // compute new value and cache it

        VcsFileSystem fs = (VcsFileSystem) folder.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        if (fs == null) {
            throw new FileStateInvalidException("Can not find VCS filesystem for folder "+folder); // NOI18N
        }
        VcsFileSystem.IgnoreListSupport ignSupport = fs.getIgnoreListSupport();

        List globalList = ignSupport.createInitialIgnoreList();

        List elements;
        List localList = (List) faq.readAttribute(folder, IgnoreList.ID);
        if (localList != null) {
            elements = new ArrayList(globalList);
            Iterator it = localList.iterator();
            while (it.hasNext()) {
                String next = (String) it.next();
                // XXX this is CVS specifics, copy&pasted from CLVFS.
                if ("!".equals(next)) { // NOI18N
                    elements.clear();
                } else {
                    elements.add(next);
                }
            }
        }  else {
            // execute command
            String path = folder.getPath();
            elements = ignSupport.createIgnoreList(path, globalList);
        }

        IgnoreList ilist = new IgnoreList(elements);
        FolderProperties mergedProperties;
        if (fprops != null) {
            mergedProperties = new FolderProperties(fprops);
            mergedProperties.setIgnoreList(ilist);
        } else {
            mergedProperties = new FolderProperties();
            mergedProperties.setIgnoreList(ilist);
        }
        faq.writeAttribute(folder, FolderProperties.ID, mergedProperties);
        return ilist;
    }

}
