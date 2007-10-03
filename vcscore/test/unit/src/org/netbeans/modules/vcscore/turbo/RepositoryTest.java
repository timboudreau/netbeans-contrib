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
