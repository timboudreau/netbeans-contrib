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
package org.netbeans.modules.mobility.plugins.microemulator;

import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Properties;
import java.util.StringTokenizer;

import org.netbeans.spi.mobility.cldcplatform.CLDCPlatformDescriptor;
import org.netbeans.spi.mobility.cldcplatform.CustomCLDCPlatformConfigurator;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import proguard.ProGuard;

public final class MicroemulatorPlatformConfigurator implements CustomCLDCPlatformConfigurator {

    private static final String DISPLY_NAME = "Emulator_displayname"; //NOI18N

    private static final String DESCRIPTION = "Emulator_description"; //NOI18N

    private static final String CONFIGURATIONS = "configurations"; //NOI18N

    private static final String OPTIONAL = "optional"; //NOI18N 

    private static final String PROFILES = "profiles"; //NOI18N

    private static final String PREVERIFY_COMMAND = "preverify.command"; //NOI18N

    private static final String RUN_COMMAND = "run.command"; //NOI18N

    private static final String DEBUG_COMMAND = "debug.command"; //NOI18N

    private static final String TYPE = "CUSTOM"; //NOI18N

    private static final String DEVICES = "devices"; //NOI18N

    private static final String JAVADOCS_FOLDER = "javadoc.folder"; //NOI18N
    
    private static final String SOURCE_FOLDER = "source.folder"; //NOI18N

    private Properties apiSettings;
    private List<CLDCPlatformDescriptor.Device> devices;

    private List<CLDCPlatformDescriptor.Profile> createAPISettings() {
        String allProperties = apiSettings.getProperty(CONFIGURATIONS) + "," + apiSettings.getProperty(PROFILES) + "," + apiSettings.getProperty(OPTIONAL); //NOI18N

        StringTokenizer propertiesTokenizer = new StringTokenizer(allProperties, ","); //NOI18N 

        List<CLDCPlatformDescriptor.Profile> descriptors = new ArrayList<CLDCPlatformDescriptor.Profile>(propertiesTokenizer.countTokens());

        while (propertiesTokenizer.hasMoreTokens()) {
            String token = propertiesTokenizer.nextToken().trim();
            CLDCPlatformDescriptor.ProfileType profileType;
            if (apiSettings.getProperty(CONFIGURATIONS).contains(token)) {
                profileType = CLDCPlatformDescriptor.ProfileType.Configuration;
            } else if (apiSettings.getProperty(PROFILES).contains(token)) {
                profileType = CLDCPlatformDescriptor.ProfileType.Profile;
            } else if (apiSettings.getProperty(OPTIONAL).contains(token)) {
                profileType = CLDCPlatformDescriptor.ProfileType.Optional;
            } else {
                throw new IllegalStateException("Type of profile is necessary to create Platform Descriptor"); //NOI18N
            }
            
            descriptors.add(new CLDCPlatformDescriptor.Profile(
                    apiSettings.getProperty(token + ".displayname"), //NOI18N
                    apiSettings.getProperty(token + ".version"), //NOI18N
                    apiSettings.getProperty(token + ".description"), //NOI18N
                    profileType,
                    apiSettings.getProperty(token + ".dependencies"), //NOI18N
                    apiSettings.getProperty(token + ".bcp"), //NOI18N
                    apiSettings.getProperty("default").contains(token))); //NOI18N

        }
        return Collections.unmodifiableList(descriptors);
    }

    public static void main(final String args[]) {
        MicroemulatorPlatformConfigurator mpc = new MicroemulatorPlatformConfigurator();
        if (args.length == 1) {
            if (mpc.isPossiblePlatform(new File(args[0]))) {
                System.out.println("Found valid Emulator " + args[0]); //NOI18N

            } else {
                System.out.println("Did not find valid Emulator at " + args[0]); //NOI18N

            }
        } else {
            System.out.println("Usage: Emulator"); //NOI18N

        }
    }

    public boolean isPossiblePlatform(File file) {
        if (apiSettings == null) {
            apiSettings = new Properties();
            try {
                apiSettings.load(MicroemulatorPlatformConfigurator.class.getResourceAsStream("Configuration.properties"));//NOI18N

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (file.isDirectory()) {
            for (String nameFile : file.list()) {
                if (nameFile.equals("microemulator.jar")) { //NOI18N
                    return true;
                }
            }
        }
        return false;
    }

    public CLDCPlatformDescriptor getPlatform(File file) {
        if (!isPossiblePlatform(file)) {
            return null;
        }
        String home = file.getAbsolutePath();
        String javadocFolder = apiSettings.getProperty(JAVADOCS_FOLDER);
        String srcPath = apiSettings.getProperty(SOURCE_FOLDER); 

        String runCommand = apiSettings.getProperty(RUN_COMMAND);
        String debugCommand = apiSettings.getProperty(DEBUG_COMMAND);
        String preverifyCommand = apiSettings.getProperty(PREVERIFY_COMMAND);
        String displayName = NbBundle.getMessage(MicroemulatorPlatformConfigurator.class, DISPLY_NAME);
        if (javadocFolder == null) {
            throw new IllegalArgumentException("null javadocs folder"); //NOI18N

        }
        String docPath = home + javadocFolder;

        String allProperties = apiSettings.getProperty(DEVICES);
        StringTokenizer propertiesTokenizer = new StringTokenizer(allProperties, ","); //NOI18N 

        devices = new ArrayList<CLDCPlatformDescriptor.Device>();

        while (propertiesTokenizer.hasMoreTokens()) {
            String token = propertiesTokenizer.nextToken().trim();
            devices.add(
                    new CLDCPlatformDescriptor.Device(
                    apiSettings.getProperty(token + ".name"), //NOI18N
                    NbBundle.getMessage(MicroemulatorPlatformConfigurator.class, DESCRIPTION),
                    Collections.EMPTY_LIST,
                    createAPISettings(),
                    new CLDCPlatformDescriptor.Screen(
                    Integer.valueOf(apiSettings.getProperty(token + ".screen.width").trim()), //NOI18N
                    Integer.valueOf(apiSettings.getProperty(token + ".screen.height").trim()), //NOI18N
                    Integer.valueOf(apiSettings.getProperty(token + ".screen.bitDepth").trim()), //NOI18N
                    Integer.valueOf(apiSettings.getProperty(token + ".screen.color").trim()) == 1 ? Boolean.TRUE : Boolean.FALSE, //NOI18N
                    Integer.valueOf(apiSettings.getProperty(token + ".screen.touch").trim()) == 1 ? Boolean.TRUE : Boolean.FALSE //NOI18N
                    )));
        }
        //URI uri = ProGuard.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        String path = null;
        try {
            path = ProGuard.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }

        preverifyCommand = preverifyCommand.replace("{preverifierdir}", path); //NOI18N
        return new CLDCPlatformDescriptor(displayName, home, TYPE,
                srcPath, docPath, preverifyCommand, runCommand, debugCommand, devices);
    }

    public String getRegistryProviderName() {
        return null;
    }

}
