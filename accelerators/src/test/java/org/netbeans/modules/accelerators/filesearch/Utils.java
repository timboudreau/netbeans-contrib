/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;

import java.io.File;
import java.util.Enumeration;
import junit.framework.Assert;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
/**
 *
 * @author Andrei Badea
 */
public class Utils {

    private Utils() {
    }

    public static FileObject createSearchRoot(File where) throws Exception {
        FileObject whereFO = FileUtil.toFileObject(where);
        String rootName = FileUtil.findFreeFolderName(whereFO, "SearchWorkerTest");
        FileObject root = whereFO.createFolder(rootName);
        
        for (int i = 0; i < 10; i++) {
            String name = FileUtil.findFreeFileName(root, "foobar", "ext");
            FileUtil.createData(root, name + ".ext");
        }
        for (int i = 0; i < 10; i++) {
            String name = FileUtil.findFreeFileName(root, "foobla", "ext");
            FileUtil.createData(root, name + ".ext");
        }
        for (int i = 0; i < 10; i++) {
            String name = FileUtil.findFreeFileName(root, "baz", "ext");
            FileUtil.createData(root, name + ".ext");
        }
        return root;
    }
    
    public static void checkResult(FileSearchResult result, int count, String prefix) {
        FileObject[] fos = result.getResult();
        Assert.assertEquals(count, fos.length);
        for (int i = 0; i < fos.length; i++) {
            Assert.assertTrue(fos[i].getNameExt().startsWith(prefix));
        }
    }
    
    public static int getFileCount(FileObject root) {
        return getFileCount(root, new PrefixSearchFilter("", true));
    }
    
    public static int getFileCount(FileObject root, String prefix) {
        return getFileCount(root, new PrefixSearchFilter(prefix, true));
    }
    
    private static int getFileCount(FileObject root, SearchFilter filter) {
        int count = 0;
        Enumeration e = root.getChildren(true);
        while (e.hasMoreElements()) {
            FileObject fo = (FileObject)e.nextElement();
            if (filter.accept(fo)) {
                count++;
            }
        }
        return count;
    }
}
