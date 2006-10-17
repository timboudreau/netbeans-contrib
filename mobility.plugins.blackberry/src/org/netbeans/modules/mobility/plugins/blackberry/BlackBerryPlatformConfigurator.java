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

/*
 * BlackBerryPlatformConfigurator.java
 *
 */
package org.netbeans.modules.mobility.plugins.blackberry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.netbeans.spi.mobility.cldcplatform.CLDCPlatformDescriptor;
import org.netbeans.spi.mobility.cldcplatform.CustomCLDCPlatformConfigurator;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Adam Sotona
 */
public class BlackBerryPlatformConfigurator implements CustomCLDCPlatformConfigurator {
    
    /** Creates a new instance of BlackBerryPlatformConfigurator */
    public BlackBerryPlatformConfigurator() {
    }

    private static final String keyFiles[] = new String[] {
        "simulator/handhelds.manifest.txt", "bin/rapc.exe", //NOI18N
        "bin/preverify.exe", "bin/JavaLoader.exe", "lib/net_rim_api.jar"}; //NOI18N
    
    private static final String BB_PLATFORM_NAME = "BlackBerry Device Simulator 2.3"; //NOI18N
    
    private static final String CLASSPATH= "${platform.home}/lib/net_rim_api.jar"; //NOI18N
    
    public String getRegistryProviderName() {
        return "Research In Motion"; //NOI18N
    }

    public boolean isPossiblePlatform(File f) {
        if (!f.isDirectory()) return false;
        for (int i=0; i<keyFiles.length; i++) 
            if (!new File(f, keyFiles[i]).isFile()) return false;
        return true;
    }
    
    public CLDCPlatformDescriptor getPlatform(File f) {
        if (!isPossiblePlatform(f)) return null;
        f = FileUtil.normalizeFile(f);
        File docs = FileUtil.normalizeFile(new File(f, "docs/api")); //NOI18N
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(f, "simulator/handhelds.manifest.txt"))); //NOI18N
            String devNames[] = br.readLine().split(" ");//NOI18N
            ArrayList<CLDCPlatformDescriptor.Device> devices = new ArrayList<CLDCPlatformDescriptor.Device>();
            ArrayList<CLDCPlatformDescriptor.Profile> profiles = new ArrayList<CLDCPlatformDescriptor.Profile>();
            profiles.add(new CLDCPlatformDescriptor.Profile("CLDC", "1.1", "Connected Limited Device Configuration", CLDCPlatformDescriptor.ProfileType.Configuration, null, CLASSPATH, true)); //NOI18N
            profiles.add(new CLDCPlatformDescriptor.Profile("MIDP", "2.0", "Mobile Information Device Profile", CLDCPlatformDescriptor.ProfileType.Profile, null, CLASSPATH, true)); //NOI18N
            profiles.add(new CLDCPlatformDescriptor.Profile("MMAPI", "1.0", "Mobile Media API", CLDCPlatformDescriptor.ProfileType.Optional, null, CLASSPATH, true)); //NOI18N
            profiles.add(new CLDCPlatformDescriptor.Profile("WMA", "1.0", "Wireless Messaging API", CLDCPlatformDescriptor.ProfileType.Optional, null, CLASSPATH, true)); //NOI18N
            profiles.add(new CLDCPlatformDescriptor.Profile("JSR179", "1.0", "Location Based APIs", CLDCPlatformDescriptor.ProfileType.Optional, null, CLASSPATH, true)); //NOI18N
            profiles.add(new CLDCPlatformDescriptor.Profile("JSR75", "1.0", "File Connection and PIM Optional Packages", CLDCPlatformDescriptor.ProfileType.Optional, null, CLASSPATH, true)); //NOI18N
            for (int i=0; i<devNames.length; i++) {
                devices.add(new CLDCPlatformDescriptor.Device(devNames[i], devNames[i], null, profiles, new CLDCPlatformDescriptor.Screen(240, devNames[i].charAt(1) == '1' ? 260 : 160, 16, true, false)));
            }
            return new CLDCPlatformDescriptor(BB_PLATFORM_NAME, f.getAbsolutePath(), "CUSTOM", null, docs.getAbsolutePath(), 
                    "\"{platformhome}{/}bin{/}preverify\" {classpath|-classpath \"{classpath}\"} -d \"{destdir}\" \"{srcdir}\"",  //NOI18N
                    "cmd /C \"cd /D {platformhome}{/}simulator&{device}\"",  //NOI18N
                    "cmd /C \"cd /D {platformhome}{/}bin&jdwp\"", devices); //NOI18N
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
            return null;
        } finally {
            if (br != null) try {br.close();} catch (IOException ioe) {}
        }
    }
    
}
