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

package org.netbeans.modules.vcscore.turbo;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.UnitProfilesFactory;
import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;
import org.netbeans.modules.vcs.profiles.testprofiles.TestProfile;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.DirReaderListener;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.api.vcs.FileStatusInfo;
import org.openide.filesystems.*;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.net.URL;
import java.net.URI;

import junit.framework.TestCase;

/**
 * Tests:
<pre>
Local files:
Test.java
Test.form
Bundle.properties

1) The list of local files is the same as list of files in the repository - test of correct status assignments

Incoming data:
[ Test.java, Up-to-date, Joe, 1.2, null, null, 100, Jan 01 2004, 20:01:59 ]
[ Test.form, Locally Modified, Joe, 1.1, null, null, 50, Jan 02 2004, 22:02:59 ]
[ Bundle.properties, Up-to-date, null, 1.2.3.2, branch, D:\repo, 100, Jan 01 2004, 20:01:59 ]

Results:
File             |Status          |Locker|Revision|Sticky|Attr   |Size|Date       |Time    |
--------------------------------------------------------------------------------------------
Test.java        |Up-to-date      |Joe   | 1.2    |      |       |100 |Jan 01 2004|20:01:59|
Test.form        |Locally Modified|Joe   | 1.1    |      |       |50  |Jan 02 2004|22:02:59|
Bundle.properties|Up-to-date      |      | 1.2.3.2|branch|D:\repo|100 |Jan 01 2004|20:01:59|

2) A file missing in the working dir and a file extra - test of local and virtual files

Incoming data:
[ Test.form, Locally Modified, Joe, null, null, null, null, null, null ]
[ Bundle.properties, Up-to-date, null, null, null, null, null, null, null ]
[ Main.java, Needs Update, John, null, null, null, null, null, null ]

Results:
File             |Status          |Locker|
------------------------------------------
Test.java        |Local           |      |
Test.form        |Locally Modified|Joe   |
Bundle.properties|Up-to-date      |      |
Main.java        |Needs Update    |John  |

3) FileReaderListener - test of removed file:

Incoming data:
File: (array of length == 1)
Test.form

Results:
File             |Status          |Locker|
------------------------------------------
Test.java        |Local           |      |
Test.form        |Local           |      |
Bundle.properties|Up-to-date      |      |
Main.java        |Needs Update    |John  |

4) FileReaderListener - test of removed virtual file:

Incoming data:
File: (array of length == 1)
Main.java

Results:
File             |Status          |Locker|
------------------------------------------
Test.java        |Local           |      |
Test.form        |Local           |      |
Bundle.properties|Up-to-date      |      |
</pre>
 *
 * @author Petr Kuzel
 */
public class TurboUtilTest extends TestCase {

    private FileSystem fs;

    // called before every method
    protected void setUp() throws Exception {

        // create VcsFileSystem instance
        UnitProfilesFactory.setRegistry(TestProfile.getRegistration());
        ProfilesFactory factory = ProfilesFactory.getDefault();
        Profile profile = factory.getProfile("testprofile.xml");

        System.setProperty("netbeans.experimental.RepositoryTest", "true"); // disable object integrity support
        System.setProperty("netbeans.user", "Fake from RepositoryTest");  // vfs requires this property
        CommandLineVcsFileSystem vfs = new CommandLineVcsFileSystem();
        vfs.setProfile(profile);
        File tmp = new File(System.getProperty("java.io.tmpdir") + File.separator + "vcs-populate-test");
        if (tmp.exists() == false) {
            tmp.mkdir();
        }
        vfs.setRootDirectory(tmp);

        // clear
        FileObject[] fos = vfs.getRoot().getChildren();
        for (int i = 0; i < fos.length; i++) {
            FileObject fo = fos[i];
            fo.delete();
        }

        this.fs = vfs;

    }

    /**
     * Executes serie of cases with shared filesystem content
     * keeping a state information passed between cases.
     */
    public void testPopulate() throws Exception {
        usecase1();
        usecase2();
        usecase3();
        usecase4();
    }

    private void usecase1() throws Exception {
        FileObject fo1 = fs.getRoot().createData("Test.java");
        FileObject fo2 = fs.getRoot().createData("Test.form");
        FileObject fo3 = fs.getRoot().createData("Bundle.properties");

        String[] l1 = { "Test.java", "Up-to-date", "Joe", "1.2", null, null, "100", "Jan 01 2004", "20:01:59" };
        String[] l2 = { "Test.form", "Locally Modified", "Joe", "1.1", null, null, "50", "Jan 02 2004", "22:02:59"};
        String[] l3 = { "Bundle.properties", "Up-to-date", null, "1.2.3.2", "branch", "D:\\repo", "100", "Jan 01 2004", "20:01:59" };

        List list = new ArrayList(3);
        list.add(l1);
        list.add(l2);
        list.add(l3);

        DirReaderListener l = TurboUtil.dirReaderListener(fs);
        l.readDirFinished("", list, true);

        // now check results
        FileProperties v1 = Turbo.getMeta(fo1);
        assertEquals(v1.getStatus(), "Up-to-date");
        assertEquals(v1.getLocker(), "Joe");
        FileProperties v2 = Turbo.getMeta(fo2);
        assertEquals(v2.getStatus(), "Locally Modified");
        assertEquals(v2.getLocker(), "Joe");
        FileProperties v3 = Turbo.getMeta(fo3);
        assertEquals(v3.getStatus(), "Up-to-date");
        assertNull(v3.getLocker());

    }

    private void usecase2() throws Exception {
        String[] l1 = { "Test.form", "Locally Modified", "Joe", null, null, null, null, null, null };
        String[] l2 = { "Bundle.properties", "Up-to-date", null, null, null, null, null, null, null };
        String[] l3 = { "Main.java", "Needs Update", "John", null, null, null, null, null, null };

        List list = new ArrayList(3);
        list.add(l1);
        list.add(l2);
        list.add(l3);

        DirReaderListener l = TurboUtil.dirReaderListener(fs);
        l.readDirFinished("", list, true);

        // now check results
        FileObject fo1 = fs.getRoot().getFileObject("Test.java");
        FileObject fo2 = fs.getRoot().getFileObject("Test.form");
        FileObject fo3 = fs.getRoot().getFileObject("Bundle.properties");
        FileObject fo4 = fs.getRoot().getFileObject("Main.java");

        FileProperties v1 = Turbo.getMeta(fo1);
        assertEquals(v1.getStatus(), FileStatusInfo.LOCAL.getName());
        assertNull(v1.getLocker());
        FileProperties v2 = Turbo.getMeta(fo2);
        assertEquals(v2.getStatus(), "Locally Modified");
        assertEquals(v2.getLocker(), "Joe");
        FileProperties v3 = Turbo.getMeta(fo3);
        assertEquals(v3.getStatus(), "Up-to-date");
        assertNull(v3.getLocker());
        FileProperties v4 = Turbo.getMeta(fo4);
        assertEquals(v4.getStatus(), "Needs Update");
        assertEquals(v4.getLocker(), "John");

    }

    private void usecase3() throws Exception {
        String[] l1 = { "Test.form"};

        List list = new ArrayList(3);
        list.add(l1);

        FileReaderListener l = TurboUtil.fileReaderListener(fs);
        l.readFileFinished("", list);

        // now check results
        FileObject fo1 = fs.getRoot().getFileObject("Test.java");
        FileObject fo2 = fs.getRoot().getFileObject("Test.form");
        FileObject fo3 = fs.getRoot().getFileObject("Bundle.properties");
        FileObject fo4 = fs.getRoot().getFileObject("Main.java");

        FileProperties v1 = Turbo.getMeta(fo1);
        assertEquals(v1.getStatus(), FileStatusInfo.LOCAL.getName());
        assertNull(v1.getLocker());
        FileProperties v2 = Turbo.getMeta(fo2);
        assertEquals(v2.getStatus(), FileStatusInfo.LOCAL.getName());
        assertNull(v2.getLocker());
        FileProperties v3 = Turbo.getMeta(fo3);
        assertEquals(v3.getStatus(), "Up-to-date");
        assertNull(v3.getLocker());
        FileProperties v4 = Turbo.getMeta(fo4);
        assertEquals(v4.getStatus(), "Needs Update");
        assertEquals(v4.getLocker(), "John");
    }

    private void usecase4() throws Exception {
        String[] l1 = { "Main.java"};
        List list = new ArrayList(3);
        list.add(l1);

        FileReaderListener l = TurboUtil.fileReaderListener(fs);
        l.readFileFinished("", list);

        // now check results
        FileObject fo1 = fs.getRoot().getFileObject("Test.java");
        FileObject fo2 = fs.getRoot().getFileObject("Test.form");
        FileObject fo3 = fs.getRoot().getFileObject("Bundle.properties");
        FileObject fo4 = fs.getRoot().getFileObject("Main.java");
        assertNull(fo4);

        FileProperties v1 = Turbo.getMeta(fo1);
        assertEquals(v1.getStatus(), FileStatusInfo.LOCAL.getName());
        assertNull(v1.getLocker());
        FileProperties v2 = Turbo.getMeta(fo2);
        assertEquals(v2.getStatus(), FileStatusInfo.LOCAL.getName());
        assertNull(v2.getLocker());
        FileProperties v3 = Turbo.getMeta(fo3);
        assertEquals(v3.getStatus(), "Up-to-date");
        assertNull(v3.getLocker());

    }

}
