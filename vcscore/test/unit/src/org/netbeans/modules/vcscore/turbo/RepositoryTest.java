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

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.UnitProfilesFactory;
import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;
import org.netbeans.modules.vcs.profiles.testprofiles.TestProfile;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.openide.filesystems.XMLFileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileSystem;

import java.util.Vector;
import java.io.File;
import java.net.URL;
import java.net.URI;

import junit.framework.TestCase;

/**
 * Tests:
 * <ul>
 *   <li>refresh file
 *   <li>refresh folder
 *   <li>refresh folder content
 *   <li>refresh folder content recursively
 * </ul>
 * commands.
 *
 * <p>
 * It requires on classpath the testprofile.
 *
 * @author Petr Kuzel
 */
public class RepositoryTest extends TestCase {

    // How to contruct command support for test provider?

    // it could be addressed by cmdfs with testprofile engine

    public void testSample() throws Exception {

        System.setProperty("netbeans.user", "Fake from RepositoryTest");
        System.setProperty("netbeans.experimental.useVcsTurbo", "true");
        System.setProperty("netbeans.experimental.RepositoryTest", "true");

        UnitProfilesFactory.setRegistry(TestProfile.getRegistration());
        ProfilesFactory factory = ProfilesFactory.getDefault();
        Profile profile = factory.getProfile("testprofile.xml");

        CommandLineVcsFileSystem vfs = new CommandLineVcsFileSystem();
        vfs.setProfile(profile);
        Vector vars = vfs.getVariables();
        URL stage1 = getClass().getResource("stage1.xml");
        VcsConfigVariable repository = new VcsConfigVariable("XML_FS", "XML FS", stage1.toExternalForm(), false, false, false, null);
        vars.add(repository);
        vfs.setVariables(vars); // XML_FS VcsConfigVariable
        File root  = new File(System.getProperty("java.io.tmpdir") + File.separator + "RepositoryTest"); // NOI18N
        vfs.setRootDirectory(root);

        // checkout initial revision

        CommandSupport checkout = vfs.getCommandSupport("CHECKOUT");
        Command cmd = checkout.createCommand();
        cmd.setGUIMode(false);
        CommandTask monitor = cmd.execute();
        boolean ok = monitor.waitFinished(1000L*60);
        assertTrue(ok);

        // move to stage 2 a new file appears in repository
        // after refresh it should exist as virtual file

        URL stage2 = getClass().getResource("stage2.xml");
        repository.setValue(stage2.toExternalForm());
//        CommandSupport refreshFolderContent = vfs.getCommandSupport("LIST");
//        Command refresh = refreshFolderContent.createCommand();
//        refresh.setGUIMode(false);
                
        FileObject fo = vfs.getRoot().getFileObject("stage1");
        Repository.refreshFolderContent(fo);

        int kids = fo.getChildren().length;
        assertTrue("Probably missing virtual", kids == 2);
    }
}
