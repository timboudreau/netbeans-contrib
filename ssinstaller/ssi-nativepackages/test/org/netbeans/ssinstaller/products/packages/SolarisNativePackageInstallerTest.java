/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.ssinstaller.products.packages;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Igor Nikiforov
 */
public class SolarisNativePackageInstallerTest {

    @Test
    public void deviceAnalizerTest1() {
        System.out.println("DeviceAnalizer1");
        final String pathToPackage = "/tmp/mc-4.6.1-sol10-x86-local";
        final String packageName = "SMCmc";
        assertTrue(SolarisNativePackageInstaller.DeviceFileAnalizer.isCorrectPackage(pathToPackage));
        SolarisNativePackageInstaller.DeviceFileAnalizer analizer = new SolarisNativePackageInstaller.DeviceFileAnalizer(pathToPackage);
        assertEquals(analizer.getPackagesCount(), 1);
        assertEquals(analizer.iterator().next(), packageName);
    }

    @Test
    public void deviceAnalizerTest2() {
        System.out.println("DeviceAnalizer2");
        final String pathToPackage = "/tmp/SUNWmercurial-0.9.5-i386.pkg";
        final String packageName = "SUNWmercurial";
        assertTrue(SolarisNativePackageInstaller.DeviceFileAnalizer.isCorrectPackage(pathToPackage));
        SolarisNativePackageInstaller.DeviceFileAnalizer analizer = new SolarisNativePackageInstaller.DeviceFileAnalizer(pathToPackage);
        assertEquals(analizer.getPackagesCount(), 1);
        assertEquals(analizer.iterator().next(), packageName);
    }
    
    
}