/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcscore.turbo;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.VcsFileSystem;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.List;

/**
 * Represents .ignorelist or equivalent listing files that
 * should not be versioned in repository. It's friend
 * client of Turbo. It uses package private FileProperties
 * attribute.
 *
 * TODO add support for DISK and REFRESH levels
 *
 * @author Petr Kuzel
 */
public final class IgnoreList {

    // buffered regular expression, for efficiency reasons
    private Pattern regExp;

    private final List ignoreList;

    IgnoreList(List elements) {
        ignoreList = elements;
    }

    /**
     * Finds ignore list for specified folder either in
     * cache or connect server.
     */
    public static IgnoreList forFolder(FileObject folder) {
        FileProperties fprops = Turbo.getMeta(folder);
        IgnoreList ilist = fprops.getIgnoreList();
        if (ilist == null) {
            try {
                FileSystem fs = folder.getFileSystem();
                if (fs instanceof VcsFileSystem) {
                    VcsFileSystem.IgnoreListSupport is = ((VcsFileSystem)fs).getIgnoreListSupport();
                    createIgnoreList(folder, is);
                }
            } catch (FileStateInvalidException e) {

            }

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

    private static IgnoreList createIgnoreList(FileObject folder, VcsFileSystem.IgnoreListSupport ignSupport) {
        FileObject parent = folder.getParent();
        String path = folder.getPath();
        //System.out.println("createIgnoreList("+folder.getAbsolutePath()+", "+path+"), parent = "+((parent == null) ? "null" : parent.getAbsolutePath()));
        List ignoreList = null;
        if (parent == null) {
            ignoreList = ignSupport.createIgnoreList(path, ignSupport.createInitialIgnoreList());
        } else {
            //CacheDir pd = cache.getDir(parent.getPackageNameExt('/','.'));
            List parentIgnoreList;
            //System.out.println("  parent has ignore list set = "+parent.isIgnoreListSet());
            FileProperties pprops = Turbo.getMeta(parent);
            IgnoreList ilist = pprops.getIgnoreList();
            if (ilist == null) {
                ilist = createIgnoreList(parent, ignSupport);
                pprops.setIgnoreList(ilist);
                parentIgnoreList = ilist.getElements();
            } else {
                parentIgnoreList = ilist.getElements();
            }
            ignoreList = ignSupport.createIgnoreList(path, parentIgnoreList);
        }
        return new IgnoreList(ignoreList);
    }

    private List getElements() {
        return ignoreList;
    }
}
