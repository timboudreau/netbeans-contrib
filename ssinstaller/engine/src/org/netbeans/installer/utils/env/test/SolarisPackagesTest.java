/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.installer.utils.env.test;

import org.netbeans.installer.utils.env.PackageDescr;
import org.netbeans.installer.utils.env.impl.SolarisPackagesAnalyzer;

/**
 *
 * @author lm153972
 */
public class SolarisPackagesTest {
    public static void main(String args[]) {
        SolarisPackagesAnalyzer analyzer = new SolarisPackagesAnalyzer();
        for(PackageDescr name : analyzer.getInstalledPackageList()) {
            System.out.println("Name="  + name.getName() + ", version=" + name.getVersion()
                    + ", arch=" + name.getArch() + ", base=" + name.getBaseDirectory());
        }
    }
}
