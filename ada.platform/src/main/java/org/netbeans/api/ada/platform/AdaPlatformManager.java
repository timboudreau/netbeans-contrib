/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.api.ada.platform;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.io.ReaderInputStream;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaPlatformManager implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AdaPlatformManager.class.getName());
    private static final String PLATFORM_FILE = System.getProperty("netbeans.user") + "/config/ada-platforms.xml";
    private static final String GNAT_EXECUTABLE_NAME = "gnat"; // NOI18N
    private static final String GNAT_PLATFORM_NAME = "GNAT";
    private HashMap<String, AdaPlatform> platforms;
    private String defaultPlatform;

    /**
     * Constructor is a singelton
     */
    private AdaPlatformManager() {
        if (!load()) {
            platforms = new HashMap<String, AdaPlatform>();
        }
        //LOGGER.setLevel(Level.FINE);
    }
    /** Singelton instance variable **/
    private static AdaPlatformManager instance;

    /**
     * Get instance of the platform manager create one if never created before
     * @return Ada Platform Manager
     */
    public static AdaPlatformManager getInstance() {
        if (instance == null) {
            instance = new AdaPlatformManager();
        }
        return instance;
    }

    /**
     * Load Platform data from xml
     * @return Status of load operation
     */
    public boolean load() {
        boolean success = false;
        try {
            File xmlFile = new File(PLATFORM_FILE);
            if (xmlFile.exists()) {
                XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(xmlFile)));
                defaultPlatform = (String) decoder.readObject();
                platforms = (HashMap<String, AdaPlatform>) decoder.readObject();
                decoder.close();
                success = true;
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return success;
    }

    /**
     * Save the Platform data back to the xml file
     */
    public void save() {
        try {
            File xmlFile = new File(PLATFORM_FILE);
            if (!xmlFile.exists()) {
                xmlFile.createNewFile();
            }
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(xmlFile)));
            encoder.writeObject(defaultPlatform);
            encoder.writeObject(platforms);
            encoder.close();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public String getDefaultPlatform() {
        return defaultPlatform;
    }

    public void setDefaultPlatform(String defaultPlatform) {
        this.defaultPlatform = defaultPlatform;
    }

    public void addPlatform(AdaPlatform platform) {
        platforms.put(platform.getName(), platform);
    }

    public AdaPlatform getPlatform(String name) {
        return platforms.get(name);
    }

    public List<String> getPlatformList() {
        ArrayList<String> platformList = new ArrayList<String>();
        platformList.addAll(platforms.keySet());
        return platformList;
    }

    public void removePlatform(String name) {
        platforms.remove(name);
    }

    public static FileObject findTool(String toolName, FileObject root) {
        assert toolName != null;
        FileObject bin = root;
        if (!root.getName().toLowerCase().contains("bin")) {
            bin = root.getFileObject("bin"); //NOI18N
            if (bin == null) {
                return null;
            }
        }
        FileObject tool = bin.getFileObject(toolName, Utilities.isWindows() ? "exe" : null); //NOI18N
        if (tool != null) {
            return tool;
        }
        return null;
    }

    /*
     * To conver the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public AdaPlatform findPlatformProperties(FileObject folder) throws AdaException {
        AdaPlatform platform = null;
        String id = null;

        // Find GNAT Tool
        // ??? Now only GNAT platform is supported
        FileObject gnat = findTool(GNAT_EXECUTABLE_NAME, folder);

        if (gnat != null) {

            try {
                File tool = FileUtil.toFile(gnat);
                AdaExecution adaExec = new AdaExecution();
                adaExec.setCommand(tool.getPath());
                adaExec.setDisplayName("Ada Platform Properties");
                adaExec.setShowControls(false);
                adaExec.setShowInput(false);
                adaExec.setShowWindow(false);
                adaExec.setShowProgress(false);
                adaExec.setShowSuspended(false);
                adaExec.attachOutputProcessor();
                adaExec.setWorkingDirectory(tool.getPath().substring(0, tool.getPath().lastIndexOf(tool.getName())));
                Future<Integer> result = adaExec.run();
                Integer value = result.get();
                if (value.intValue() == 0) {
                    ReaderInputStream is = new ReaderInputStream(adaExec.getOutput());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    try {
                        while ((line = reader.readLine()) != null) {
                            if (line.contains(GNAT_PLATFORM_NAME)) {
                                int startIndex = line.indexOf(GNAT_PLATFORM_NAME) + GNAT_PLATFORM_NAME.length();
                                int endIndex = line.indexOf("(") < 0 ? line.length() : line.indexOf("(");
                                int endIndex2 = line.indexOf(")") < 0 ? line.length() : line.indexOf(")");

                                platform = new AdaPlatform();
                                platform.setName(GNAT_PLATFORM_NAME + line.substring(startIndex, endIndex));
                                if (endIndex < endIndex2) {
                                    platform.setInfo(line.substring(endIndex + 1, endIndex2));
                                }
                                platform.setCompilerCommand(GNAT_EXECUTABLE_NAME);
                                platform.setCompilerPath(tool.getPath().substring(0, tool.getPath().lastIndexOf(tool.getName())));
                                if (platforms.size() == 0) {
                                    setDefaultPlatform(platform.getName());
                                }
                                platforms.put(platform.getName(), platform);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    throw new AdaException("Could not discover Ada properties");
                }
            } catch (AdaException ex) {
                Exceptions.printStackTrace(ex);
                throw ex;
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return platform;
    }

    public synchronized void autoDetect() {
        platforms.clear();

        AdaAutoDetector detector = new AdaAutoDetector();
        // Check the path to see if we find any other Ada installations
        for (String dir : Util.dirsOnPath()) {
            // TODO: this method fix the AdaAutoDetector bug on manage the
            // recursive folders. Remove this method after fix bug.
            detector.setSearchNestedDirectoies(true);
            detector.traverse(new File(dir), GNAT_EXECUTABLE_NAME, false);
        }

        if (Utilities.isWindows()) {
            // TODO: this method fix the AdaAutoDetector bug on manage the
            // recursive folders. Remove this method after fix bug.
            detector.setSearchNestedDirectoies(true);
            detector.traverse(new File("c:/"), GNAT_EXECUTABLE_NAME, false);
            // TODO: this method fix the AdaAutoDetector bug on manage the
            // recursive folders. Remove this method after fix bug.
            detector.setSearchNestedDirectoies(true);
            detector.traverse(new File("c:/program files"), GNAT_EXECUTABLE_NAME, false);
        } else { // Unix and OSX
            // TODO: this method fix the AdaAutoDetector bug on manage the
            // recursive folders. Remove this method after fix bug.
            detector.setSearchNestedDirectoies(true);
            detector.traverse(new File("/usr/bin"), GNAT_EXECUTABLE_NAME, false);
            // TODO: this method fix the AdaAutoDetector bug on manage the
            // recursive folders. Remove this method after fix bug.
            detector.setSearchNestedDirectoies(true);
            detector.traverse(new File("/usr/local"), GNAT_EXECUTABLE_NAME, false);
            // TODO: this method fix the AdaAutoDetector bug on manage the
            // recursive folders. Remove this method after fix bug.
            detector.setSearchNestedDirectoies(true);
            detector.traverse(new File("/opt"), GNAT_EXECUTABLE_NAME, false);
        }

        for (String path : detector.getMatches()) {
            LOGGER.fine("Auto Detect: " + path + " found");
            FileObject fo = FileUtil.toFileObject(new File(path));
            try {
                findPlatformProperties(fo);
            } catch (AdaException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
