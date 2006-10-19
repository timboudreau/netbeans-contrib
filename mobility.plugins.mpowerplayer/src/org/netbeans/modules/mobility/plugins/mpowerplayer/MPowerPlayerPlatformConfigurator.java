/*
 * MPowerPlayerPlatformConfigurator.java
 *
 * Created on August 7, 2006, 5:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.mobility.plugins.mpowerplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

import org.netbeans.spi.mobility.cldcplatform.CLDCPlatformDescriptor;
import org.netbeans.spi.mobility.cldcplatform.CustomCLDCPlatformConfigurator;

/**
 * @author bohemius
 */
public class MPowerPlayerPlatformConfigurator implements CustomCLDCPlatformConfigurator {

    private final String displayName = "MPowerPlayer";
    private String home;
    private final String type = "CUSTOM";
    private final String srcPath = ""; //not available for MPowerPlayer
    private String docPath;
    private final String preverifyCommand = "\"{platformhome}{/}osx{/}preverify{/}preverify\" {classpath|-classpath \"{classpath}\"} -d \"{destdir}\" \"{srcdir}\"";
    private final String runCmd = "java -jar {platformhome}{/}player.jar {jadfile}";
    private final String debugCmd = "java -Xdebug -Xrunjdwp:transport={debugtransport},server={debugserver},suspend={debugsuspend},address={debugaddress} -jar {platformhome}{/}player.jar {jadfile}";

    private final List<CLDCPlatformDescriptor.Profile> profiles = Arrays.asList(new CLDCPlatformDescriptor.Profile[]
            {new CLDCPlatformDescriptor.Profile("CLDC", "1.0", "Connected Limited Device Configuration", CLDCPlatformDescriptor.ProfileType.Configuration, "", "${platform.home}/stubs/cldc-1.0.jar", false),
                    new CLDCPlatformDescriptor.Profile("CLDC", "1.1", "Connected Limited Device Configuration", CLDCPlatformDescriptor.ProfileType.Configuration, "", "${platform.home}/stubs/cldc-1.1.jar", true),
                    new CLDCPlatformDescriptor.Profile("MIDP", "1.0", "Mobile Information Device Profile", CLDCPlatformDescriptor.ProfileType.Profile, "", "${platform.home}/stubs/midp-1.0.jar", false),
                    new CLDCPlatformDescriptor.Profile("MIDP", "2.0", "Mobile Information Device Profile", CLDCPlatformDescriptor.ProfileType.Profile, "", "${platform.home}/stubs/midp-2.0.jar", true),
                    new CLDCPlatformDescriptor.Profile("MMAPI", "1.0", "Mobile Media API", CLDCPlatformDescriptor.ProfileType.Optional, "", "${platform.home}/stubs/mmapi.jar", false)
            });

    private final List<CLDCPlatformDescriptor.Device> devices = Collections.singletonList(new CLDCPlatformDescriptor.Device(
            "MPowerPlayer_emulator",
            "Java based device emulator",
            Collections.EMPTY_LIST,
            profiles, new CLDCPlatformDescriptor.Screen(160, 240, 16, true, false)
    ));

    /**
     * Creates a new instance of MPowerPlayerPlatformConfigurator
     */
    public MPowerPlayerPlatformConfigurator() {
    }

    public static void main(final String args[]) {
        MPowerPlayerPlatformConfigurator mppc = new MPowerPlayerPlatformConfigurator();
        if (args.length == 1) {
            if (mppc.isPossiblePlatform(new File(args[0])))
                System.out.println("Found valid MPowerPlayer SDK at " + args[0]);
            else
                System.out.println("Did not find valid MPowerPlayer SDK at " + args[0]);
        } else {
            System.out.println("Usage: MPowerPlayerConfigurator <mpp-sdk root>");
        }
    }

    public boolean isPossiblePlatform(File file) {
        boolean result = false;
        if (file.isDirectory()) {
            if (file.listFiles(new JarFilenameFilter()).length == 4 && file.listFiles(new OSXFileFilter()).length == 1)
            {
                File preverifier = new File(file.getPath() + "/" + "osx/preverify/preverify");
                if (preverifier.exists())
                    result = true;
            }
        }
        return result;
    }

    public CLDCPlatformDescriptor getPlatform(File file) {
        this.home = file.getAbsolutePath();
        this.docPath = home + "/javadoc";

        return new CLDCPlatformDescriptor(this.displayName, this.home, this.type,
                this.srcPath, this.docPath, this.preverifyCommand, this.runCmd, this.debugCmd, this.devices);
    }

    public String getRegistryProviderName() {
        return null;
    }

    private class JarFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".jar");
        }
    }

    private class OSXFileFilter implements FileFilter {
        public boolean accept(File file) {
            //noinspection RedundantIfStatement
            return (file.getName().toLowerCase().equals("osx") && file.isDirectory());

        }
    }


}
