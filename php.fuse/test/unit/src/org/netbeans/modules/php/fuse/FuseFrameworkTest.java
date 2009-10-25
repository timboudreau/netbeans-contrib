/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.fuse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.netbeans.junit.NbModuleSuite;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import static org.junit.Assert.*;
import org.netbeans.modules.php.fuse.exceptions.InvalidFuseFrameworkException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Martin Fousek
 */
public class FuseFrameworkTest extends NbTestCase {

    public FuseFrameworkTest() {
        super("FuseFrameworkTest");
    }

    @Test
    public void testFrameworkValidity() throws InvalidFuseFrameworkException {
        FuseFramework fuse = new FuseFramework(null);
        assertFalse(fuse.isValid());

        fuse = new FuseFramework("");
        assertFalse(fuse.isValid());

        fuse = new FuseFramework(" ");
        assertFalse(fuse.isValid());

        fuse = new FuseFramework("/fake/fake");
        assertFalse(fuse.isValid());

        File fakeFuseFile = getValidFuse();
        File fakeFuseWrongScaffold = getInvalidFuse();

        if (fakeFuseFile != null) {
            fuse = new FuseFramework(fakeFuseFile.getAbsolutePath());
            assertTrue(fuse.isValid());
        }

        if (fakeFuseWrongScaffold != null) {
            fuse = new FuseFramework(fakeFuseWrongScaffold.getAbsolutePath());
            assertFalse(fuse.isValid());
        }

    }

    @Test
    public void testNetBeansScaffoldValidity() throws InvalidFuseFrameworkException {
        FuseFramework fuseValid = new FuseFramework(getValidFuse().getAbsolutePath());
        FuseFramework fuseInvalid = new FuseFramework(getInvalidFuse().getAbsolutePath());

        String errorMessage = fuseValid.validateNetBeansScaffoldFile();
        assertNull( "Fuse Framework isn't valid", errorMessage);

        errorMessage = fuseInvalid.validateNetBeansScaffoldFile();
        assertEquals(errorMessage, NbBundle.getMessage(FuseFramework.class, "MSG_NotUpdatedFuseFramework"));
    }

    @Test
    public void testExistingFuseProjectValidity() {
        String validFuseProject = getValidProject();
        String invalidFuseProject = getInvalidProject();

        String errorMessage = FuseFramework.validateExisting(validFuseProject);
        assertNull( "Fuse project isn't valid", errorMessage);

        errorMessage = FuseFramework.validateExisting(invalidFuseProject);
        assertEquals("Wrong Fuse project is valid", errorMessage, NbBundle.getMessage(FuseFramework.class, "MSG_InvalidFuseManageScript"));
    }

    @Test
    public void testIsImprovedFuseFramework() {
        FuseFramework fuseValid = new FuseFramework(getValidFuse().getAbsolutePath());
        FuseFramework fuseInvalid = new FuseFramework(getInvalidFuse().getAbsolutePath());

        assertTrue(fuseValid.isImproved());
        assertFalse(fuseInvalid.isImproved());
    }

    @Test
    public void testImprovingFuseFramework() {
        // check invalid FUSE framework
        FuseFramework fuseInvalid = new FuseFramework(getInvalidFuse().getAbsolutePath());
        assertFalse(fuseInvalid.isImproved());

        try {
            // improve it
            fuseInvalid.improveFuseSupport();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        assertTrue(fuseInvalid.isImproved());
        removeNetBeansScaffoldFile();
        assertFalse(fuseInvalid.isImproved());
    }

    public void removeNetBeansScaffoldFile() {
        File nbScaffold = null;
        nbScaffold = new File(getDataDir(), "fuse_fake_wrong_scaffolds" + FuseFramework.CMD_INIT_PROJECT);
        nbScaffold.delete();
    }

    public File getValidFuse() {
        File fakeFuseFile = null;
        fakeFuseFile = new File(getDataDir(), "fuse_fake");
        return fakeFuseFile;
    }

    public File getInvalidFuse() {
        File fakeFuseFile = null;
        fakeFuseFile = new File(getDataDir(), "fuse_fake_wrong_scaffolds");
        return fakeFuseFile;
    }

    public String getValidProject() {
        String fuseProject = null;
        fuseProject = new File(getDataDir(), "fuse_project").getAbsolutePath();
        return fuseProject;
    }

    public String getInvalidProject() {
        String fuseProject = null;
        fuseProject = new File(getDataDir(), "fuse_project_bad").getAbsolutePath();
        return fuseProject;
    }
}