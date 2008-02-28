/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.ssinstaller.products.packages;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Igor Nikiforov
 */
public class FakeNativePackageInstallerTest {

    public static final String FILE_NAME = "testing.abc";
    public static final String PATH_TO_FILE = "/tmp/aaa/" + FILE_NAME;
    
    public FakeNativePackageInstallerTest() {
    }
    
    /**
     * Test of install method, of class FakeNativePackageInstaller.
     */
    @Test
    public void install() {        
        System.out.println("install");
        FakeNativePackageInstaller instance = new FakeNativePackageInstaller();
        assertTrue(instance.install(PATH_TO_FILE, null));
        assertTrue((new File(FakeNativePackageInstaller.FAKE_PACKAGES_DIR + File.separator + FILE_NAME)).exists());
    }

    /**
     * Test of uninstall method, of class FakeNativePackageInstaller.
     */
    @Test
    public void uninstall() {
        System.out.println("uninstall");
        FakeNativePackageInstaller instance = new FakeNativePackageInstaller();
        assertTrue(instance.uninstall(null));
        assertFalse((new File(FakeNativePackageInstaller.FAKE_PACKAGES_DIR + File.separator + FILE_NAME)).exists());
    }

}