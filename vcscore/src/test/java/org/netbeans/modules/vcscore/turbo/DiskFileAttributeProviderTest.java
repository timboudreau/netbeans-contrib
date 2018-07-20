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

import junit.framework.TestCase;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.netbeans.modules.vcscore.turbo.local.MemoryCacheProvider;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests DiskFileAttributeProvider default database.
 * <p>
 * Tests database write in append mode and rewrite mode
 * using read calls.
 *
 * @author Petr Kuzel
 */
public final class DiskFileAttributeProviderTest extends TestCase {

    private FileSystem fs;
    private File cache;

    // called before every method
    protected void setUp() throws Exception {

        // prepare simple LFS
        LocalFileSystem fs = new LocalFileSystem();
        File tmp = new File(System.getProperty("java.io.tmpdir") + File.separator + "dfap-test");
        if (tmp.exists() == false) {
            tmp.mkdir();
        }
        fs.setRootDirectory(tmp);

        // clear
        FileObject[] fos = fs.getRoot().getChildren();
        for (int i = 0; i < fos.length; i++) {
            FileObject fo = fos[i];
            fo.delete();
        }

        // populate
        FileObject cfo = fs.getRoot().createData("cache.tmp");
        this.fs = fs;
        this.cache = FileUtil.toFile(cfo);
    }

    public void testFileProperties() throws Exception {
        DiskFileAttributeProvider testee = (DiskFileAttributeProvider) DiskFileAttributeProvider.getDefault();
        DiskFileAttributeProvider.setTestCacheFile(cache);

        fs.getRoot().createData("aFile.txt");
        FileObject fo = fs.findResource("aFile.txt");
        FileProperties fprops = new FileProperties();
        fprops.setName("aFile.txt");
        fprops.setStatus("status");
        fprops.setRevision("revision");
        fprops.setSticky("sticky");
        testee.writeAttribute(fo, FileProperties.ID, fprops);

        // tests append mode
        for (int i = 1; i<10; i++) {
            fprops = new FileProperties(fprops);
            fprops.setRevision("revision" +i);
            testee.writeAttribute(fo, FileProperties.ID, fprops);
            Object v = testee.readAttribute(fo, FileProperties.ID, MemoryCacheProvider.getTestMemoryCache());
            FileProperties fpv = (FileProperties) v;
            assertEquals(fpv.getName(), "aFile.txt");
            assertEquals(fpv.getStatus(), "status");
            assertEquals(fpv.getRevision(), "revision" +i);
            assertEquals(fpv.getSticky(), "sticky");
        }

        // test rewrite mode
        DiskFileAttributeProvider.setTestRewrite(true);
        for (int i = 1; i<10; i++) {
            fprops = new FileProperties(fprops);
            fprops.setRevision("REVISION" +i);
            testee.writeAttribute(fo, FileProperties.ID, fprops);
            Object v = testee.readAttribute(fo, FileProperties.ID, MemoryCacheProvider.getTestMemoryCache());
            FileProperties fpv = (FileProperties) v;
            assertEquals(fpv.getName(), "aFile.txt");
            assertEquals(fpv.getStatus(), "status");
            assertEquals(fpv.getRevision(), "REVISION" +i);
            assertEquals(fpv.getSticky(), "sticky");
        }

    }

    public void testFolderProperties() throws Exception {
        DiskFileAttributeProvider testee = (DiskFileAttributeProvider) DiskFileAttributeProvider.getDefault();
        DiskFileAttributeProvider.setTestCacheFile(cache);

        fs.getRoot().createFolder("aFolder");
        FileObject fo = fs.findResource("aFolder");
        FolderProperties fprops = new FolderProperties();
        Set listing = new HashSet();
        listing.add(new FolderEntry("file.txt", false));
        fprops.setFolderListing(listing);
        testee.writeAttribute(fo, FolderProperties.ID, fprops);

        fprops = (FolderProperties) testee.readAttribute(fo, FolderProperties.ID, MemoryCacheProvider.getTestMemoryCache());
        assertNotNull(fprops);

//        testee.writeAttribute(fo, FolderProperties.ID, null);
//        fprops = (FolderProperties) testee.readAttribute(fo, FolderProperties.ID, MemoryCacheProvider.getTestMemoryCache());
//        assertNull(fprops);
    }

}
