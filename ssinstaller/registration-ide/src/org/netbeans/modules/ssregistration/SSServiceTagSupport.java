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

package org.netbeans.modules.ssregistration;

import org.netbeans.modules.servicetag.RegistrationData;
import org.netbeans.modules.servicetag.ServiceTag;
import org.netbeans.modules.servicetag.Registry;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.reglib.NbConnectionSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author Marek Slama
 * 
 */
public class SSServiceTagSupport {
            
    private static String NB_VERSION;
    
    private static String SS_VERSION;
    
    private static final String USER_HOME = System.getProperty("user.home");
    
    private static final String USER_DIR = System.getProperty("netbeans.user");
    
    private static final String ST_DIR = "servicetag";
    
    private static final String ST_FILE = "servicetag";
    
    private static final String REG_FILE = "registration.xml";
    
    /** Dir in home dir */
    private static File svcTagDirHome;
    
    /** Dir in install dir */
    private static File svcTagDirSS;
    
    private static File serviceTagFileHome;
    
    private static File serviceTagFileSS;
    
    
    /** Install dir */
    private static File ssInstallDir;
    //private static File nbInstallDir;
    
    /** File in home dir */
    private static File regXmlFileHome;
    
    /** File in install dir */
    private static File regXmlFileSS;
    
    private static RegistrationData registration;
    
    private static final Logger LOG = Logger.getLogger("org.netbeans.modules.ssregistration"); // NOI18N
    
    private static File registerHtmlParent;
    
    private final static String REGISTRATION_HTML_NAME = "register";
    
    private static boolean inited = false;
    
    private static void init () {
        LOG.log(Level.INFO,"Initializing");     
        NB_VERSION = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.nb.version");    
        SS_VERSION = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.ss.version");
        
              
        ssInstallDir = new File(System.getProperty("spro.home"));
        //nbInstallDir = new File(System.getProperty("netbeans.home"));
        LOG.log(Level.INFO,"Sun Studio install dir is:" + ssInstallDir);
        
       
        svcTagDirSS = new File(ssInstallDir.getPath() + File.separator  
                + "prod" + File.separator + "lib" + File.separator  + "condev");
        svcTagDirHome = new File(USER_HOME + File.separator + ".sunstudio" 
                + File.separator  + "condev" + File.separator + SS_VERSION);
        if (ssInstallDir.canWrite() && (!svcTagDirSS.exists())) {
            svcTagDirSS.mkdirs();
        }
        if (!svcTagDirHome.exists()) {
            svcTagDirHome.mkdirs();
        }
        
        regXmlFileSS = new File(svcTagDirSS,REG_FILE);
        regXmlFileHome = new File(svcTagDirHome,REG_FILE);
        
        serviceTagFileSS = new File(svcTagDirSS,ST_FILE);
        serviceTagFileHome = new File(svcTagDirHome,ST_FILE);
        
        inited = true;
    }

    /**
     * First look in registration data if Sun Studio tag exists.
     * If not then create new service tag.
     * @return service tag instance for Sun Studio
     * @throws java.io.IOException
     */
    public static ServiceTag createSSServiceTag (String source) throws IOException {
        if (!inited) {
            init();
        }
        LOG.log(Level.INFO,"Finding or creating Sun Studio service tag");
        
        ServiceTag st = getSSServiceTag();
        if (st != null) {
            if ((serviceTagFileSS.exists() || serviceTagFileHome.exists())) {
                LOG.log(Level.INFO,
                "Sun Studio service tag is already created and saved in registration.xml");
                return st;
            } else {
                LOG.log(Level.INFO,"Sun Studio service tag is already created");
            }
        }
        
        // New service tag entry if not created
        if (st == null) {
            LOG.log(Level.INFO,"Creating new Sun Studio service tag");
            st = newSSServiceTag(source);
            // Add the service tag to the registration data in NB
            getRegistrationData().addServiceTag(st);
            if (Registry.isSupported()) {
                LOG.log(Level.INFO, "Add service tag to system registry");
                installSystemServiceTag(st);
            } else {
                LOG.log(Level.INFO, "Cannot add service tag to system registry as ST infrastructure is not found");
            }
            writeRegistrationXml();
        }

        return st;
    }
    
    /**
     * Write the registration data to the registration.xml file
     * @throws java.io.IOException
     */
    private static void writeRegistrationXml() throws IOException {
        File targetFile = null;
        if (svcTagDirSS.exists() && svcTagDirSS.canWrite()) {
            //Try to create temp file to verify we can create file on Windows
            File tmpFile = null;
            try {
                tmpFile = File.createTempFile("regtmp", null, svcTagDirSS);
            } catch (IOException exc) {
                LOG.log(Level.INFO,"Warning: Cannot create file in " + svcTagDirSS
                + " Will use user home dir", exc);
            }
            if ((tmpFile != null) && tmpFile.exists()) {
                tmpFile.delete();
                targetFile = regXmlFileSS;
            } else {
                targetFile = regXmlFileHome;    
            }
        } else {
            targetFile = regXmlFileHome;
        }
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(targetFile));
            getRegistrationData().storeToXML(out);
        } catch (IOException ex) {
            LOG.log(Level.INFO,
            "Error: Cannot save registration data to \"" + targetFile + "\":" + ex.getMessage());
            throw ex;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    /**
     * Returns the Sun Studio registration data located in
     * the SS_INST_DIR/prod/condev/registration.xml by default.
     * or in user home HOME_DIR/.sunstudion/condev/X/registration.xml
     *
     * @throws IllegalArgumentException if the registration data
     *         is of invalid format.
     */
     static RegistrationData getRegistrationData () throws IOException {
        if (!inited) {
            init();
        }
        if (registration != null) {
            return registration;
        }
        
        File srcFile = null;
        if (regXmlFileSS.exists()) {
            srcFile = regXmlFileSS;
            LOG.log(Level.INFO,"Service tag will be loaded from SS install dir: " + srcFile);
        } else if (regXmlFileHome.exists()) {
            srcFile = regXmlFileHome;
            LOG.log(Level.INFO,"Service tag will be loaded from user home dir: " + srcFile);
        } else {
            registration = new RegistrationData();
            LOG.log(Level.INFO,"Service tag file not found");
            return registration;
        }
        
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(srcFile));
            registration = RegistrationData.loadFromXML(in);
        } catch (IOException ex) {
            LOG.log(Level.INFO,"Error: Bad registration data \"" +
            srcFile + "\":" + ex.getMessage());
            throw ex;
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return registration;
    }

    /**
     * NOT USED, we do not register NB if data was not generated by installer.
     * Create new service tag instance for NetBeans
     * @param svcTagSource
     * @return
     * @throws java.io.IOException
     */
     /*
    private static ServiceTag newNbServiceTag (String svcTagSource) throws IOException {
        // Determine the product URN and name
        String productURN, productName, parentURN, parentName;

        productURN = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.nb.urn");
        productName = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.nb.name");
        
        parentURN = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.nb.parent.urn");
        parentName = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.nb.parent.name");

        return ServiceTag.newInstance(ServiceTag.generateInstanceURN(),
                                      productName,
                                      NB_VERSION,
                                      productURN,
                                      parentName,
                                      parentURN,
                                      getNbProductDefinedId(),
                                      "NetBeans.org",
                                      System.getProperty("os.arch"),
                                      getZoneName(),
                                      svcTagSource);
    }*/
    
    /**
     * Create new service tag instance for Sun Studio
     * @param svcTagSource
     * @return
     * @throws java.io.IOException
     */
    private static ServiceTag newSSServiceTag (String svcTagSource) throws IOException {
        // Determine the product URN and name
        String productURN, productName, parentURN, parentName;

        productURN = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.ss.urn");
        productName = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.ss.name");
        
        parentURN = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.ss.parent.urn");
        parentName = NbBundle.getMessage(SSServiceTagSupport.class,"servicetag.ss.parent.name");

        return ServiceTag.newInstance(ServiceTag.generateInstanceURN(),
                                      productName,
                                      SS_VERSION,
                                      productURN,
                                      parentName,
                                      parentURN,
                                      getSSProductDefinedId(),
                                      "Sun Microsystems Inc.",
                                      System.getProperty("os.arch"),
                                      getZoneName(),
                                      svcTagSource);
    }
    
    /**
     * Return the Sun Studio service tag from local registration data.
     * Return null if service tag is not found.
     * 
     * @return a service tag for 
     */
    private static ServiceTag getSSServiceTag () throws IOException {
        RegistrationData regData = getRegistrationData();
        Collection<ServiceTag> svcTags = regData.getServiceTags();
        for (ServiceTag st : svcTags) {
            if (st.getProductName().startsWith("Sun Studio")) {
                return st;
            }
        }
        return null;
    }
       

    
    /**
     * Returns the product defined instance ID for Sun Studio.
     * It is a list of comma-separated name/value pairs.
     * id=X,dir=/opt/SUNWspro
     *
     */
    private static String getSSProductDefinedId() {
        StringBuilder definedId = new StringBuilder();
        definedId.append("id=");
        definedId.append(SS_VERSION);

        String location = ",dir=" + ssInstallDir.getPath();
        if ((definedId.length() + location.length()) < 256) {
            definedId.append(location);
        } else {
            // if it exceeds the limit, we will not include the location
            LOG.log(Level.INFO, "Warning: Product defined instance ID exceeds the field limit:");
        }

        return definedId.toString();
    }
    
    /**
     * Return the zonename if zone is supported; otherwise, return
     * "global".
     */
    private static String getZoneName() throws IOException {
        String zonename = "global";
        String command = "/usr/bin/zonename";
        File f = new File(command);
        // com.sun.servicetag package has to be compiled with JDK 5 as well
        // JDK 5 doesn't support the File.canExecute() method.
        // Risk not checking isExecute() for the zonename command is very low.
        if (f.exists()) {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            String output = commandOutput(p);
            if (p.exitValue() == 0) {
                zonename = output.trim();
            }
        }
        return zonename;
    }

    static String commandOutput(Process p) throws IOException {
        Reader r = null;
        Reader err = null;
        try {
            r = new InputStreamReader(p.getInputStream());
            err = new InputStreamReader(p.getErrorStream());
            String output = commandOutput(r);
            String errorMsg = commandOutput(err);
            p.waitFor();
            return output + errorMsg.trim();
        } catch (InterruptedException e) {            
            e.printStackTrace();            
            return e.getMessage();
        } finally {
            if (r != null) {
                r.close();
            }
            if (err != null) {
                err.close();
            }
        }
    }

    static String commandOutput(Reader r) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = r.read()) > 0) {
            if (c != '\r') {
                sb.append((char) c);
            }
        }
        return sb.toString();
    }

    /**
     * Returns the instance urn stored in the servicetag file
     * or empty string if file not exists.
     */
    private static String getInstalledURN() throws IOException {
        if (serviceTagFileSS.exists() || serviceTagFileHome.exists()) {
            File srcFile = null;
            if (serviceTagFileSS.exists()) {
                srcFile = serviceTagFileSS;
            } else if (serviceTagFileHome.exists()) {
                srcFile = serviceTagFileHome;
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(srcFile));
                String urn = in.readLine().trim();
                return urn;
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        return "";
    }
    
    private static void installSystemServiceTag(ServiceTag st) throws IOException {
        if (getInstalledURN().length() > 0) {
            // Already installed
            LOG.log(Level.INFO, "ST is already installed ie. we have file servicetag.");
            return;
        }

        File targetFile;
        if (svcTagDirSS.exists() && svcTagDirSS.canWrite()) {
            //Try to create temp file to verify we can create file on Windows
            File tmpFile = null;
            try {
                tmpFile = File.createTempFile("regtmp", null, svcTagDirSS);
            } catch (IOException exc) {
                LOG.log(Level.INFO,"Error: Cannot create file in " + svcTagDirSS
                + " Will use user home dir", exc);
            }
            if ((tmpFile != null) && tmpFile.exists()) {
                tmpFile.delete();
                targetFile = serviceTagFileSS;
            } else {
                targetFile = serviceTagFileHome;
            }
        } else {
            targetFile = serviceTagFileHome;
        }
        
        if (Registry.isSupported()) {
            //Check if given service tag is already installed in system registry
            if ((Registry.getSystemRegistry().getServiceTag(st.getInstanceURN()) != null)) {
                LOG.log(Level.INFO,"Service tag: " + st.getInstanceURN() 
                + " is already installed in system registry.");
                return;
            }
            //Install in the system ST registry
            Registry.getSystemRegistry().addServiceTag(st);

            // Write the instance_run to the servicetag file
            BufferedWriter out = null;
            try {
                LOG.log(Level.INFO,"Creating file: " + targetFile);
                out = new BufferedWriter(new FileWriter(targetFile));
                out.write(st.getInstanceURN());
                out.newLine();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            //For NB 6.0 save file 'servicetag' to user dir to avoid creating new ST
            //by code in IDE launcher
            if ("6.0".equals(NB_VERSION)) {
                targetFile = new File(USER_DIR + File.separator + ST_FILE);
                try {
                    LOG.log(Level.INFO,"Creating file: " + targetFile + " Specific for 6.0.");
                    out = new BufferedWriter(new FileWriter(targetFile));
                    out.write(st.getInstanceURN());
                    out.newLine();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }
    }
    
    private static File getRegisterHtmlParent() {
        if (registerHtmlParent == null) {
            // Determine the location of the offline registration page
            registerHtmlParent = svcTagDirHome;
        }
        return registerHtmlParent;
    }
    
    /** This should be called after method init is invoked. */
    static File getServiceTagDirHome () {
        if (!inited) {
            init();
        }
        return svcTagDirHome;
    }
    
    /**
     * Returns the File object of the offline registration page localized
     * for the default locale in the $HOME/.netbeans-registration/$NB_VERSION.
     */
    static File getRegistrationHtmlPage(String product, String [] productNames) throws IOException {
        if (!inited) {
            init();
        }

        File parent = getRegisterHtmlParent(); 

        File f = new File(parent, REGISTRATION_HTML_NAME + ".html");
        // Generate the localized version of the offline registration Page
        generateRegisterHtml(parent,product,productNames);

        return f;
    }
    
    // Remove the offline registration pages 
    private static void deleteRegistrationHtmlPage() {
        File parent = getRegisterHtmlParent(); 
        if (parent == null) {
            return;
        }
        
        String name = REGISTRATION_HTML_NAME;
        File f = new File(parent, name + ".html");
        if (f.exists()) {
            f.delete();
        }
    }
    
    private static final String NB_HEADER_PNG_KEY = "@@NB_HEADER_PNG@@";
    private static final String PRODUCT_KEY = "@@PRODUCT@@";
    private static final String REGISTRATION_URL_KEY = "@@REGISTRATION_URL@@";
    private static final String REGISTRATION_PAYLOAD_KEY = "@@REGISTRATION_PAYLOAD@@";

    @SuppressWarnings("unchecked")
    private static void generateRegisterHtml(File parent, String product, String [] productNames) throws IOException {
        RegistrationData regData = getRegistrationData();
        String registerURL = NbConnectionSupport.getRegistrationURL(
            regData.getRegistrationURN(), product).toString();
        
        //Extract image from jar
        String resource = "/org/netbeans/modules/reglib/resources/nb_header.png";
        File img = new File(svcTagDirHome, "nb_header.png");
        String headerImageSrc = img.toURI().toURL().toString();       
        InputStream in = SSServiceTagSupport.class.getResourceAsStream(resource);
        if (in == null) {
            // if the resource file is missing
            LOG.log(Level.INFO,"Missing resource file: " + resource);
        } else {
            LOG.log(Level.INFO,"Generating " + img + " from " + resource);
            BufferedInputStream bis = new BufferedInputStream(in);
            FileOutputStream fos = new FileOutputStream(img);
            try {
                int c;
                while ((c = bis.read()) != -1) {
                    fos.write(c);
                }
            } finally {
                if (bis != null) {
                    bis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
        }
        // Format the registration data in one single line
        String xml = regData.toString();
        String lineSep = System.getProperty("line.separator");
        String payload = xml.replaceAll("\"", "%22").replaceAll(lineSep, " ");

        String name = REGISTRATION_HTML_NAME;
        File f = new File(parent, name + ".html");
        
        in = null;
        Locale l = Locale.getDefault();
        Locale [] locales = new Locale[] {
          new Locale(l.getLanguage(), l.getCountry(), l.getVariant()),
          new Locale(l.getLanguage(), l.getCountry()),
          new Locale(l.getLanguage()),
          new Locale("")
        };
        for (Locale locale : locales) {
           resource = "/org/netbeans/modules/reglib/resources/register" + (locale.toString().equals("") ? "" : ("_" + locale)) + ".html";
           LOG.log(Level.INFO,"Looking for html in: " + resource);
           in = SSServiceTagSupport.class.getResourceAsStream(resource);
           if (in != null) {
               break;
           }
        } 
        LOG.log(Level.INFO,"Found html in: " + resource);
        LOG.log(Level.INFO,"Generating " + f);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        PrintWriter pw = new PrintWriter(f,"UTF-8");
        String line = null;
        String productName = "";
        for (int i = 0; i < productNames.length; i++) {
            if (i > 0) {
                productName +=
                " " + NbBundle.getMessage(SSServiceTagSupport.class,"MSG_junction") + " ";
            }
            productName += "<strong>" + productNames[i] + "</strong>";
        }
        while ((line = reader.readLine()) != null) {
            String output = line;
            if (line.contains(PRODUCT_KEY)) {
                output = line.replace(PRODUCT_KEY, productName);
            } else if (line.contains(NB_HEADER_PNG_KEY)) {
                output = line.replace(NB_HEADER_PNG_KEY, headerImageSrc);
            } else if (line.contains(REGISTRATION_URL_KEY)) {
                output = line.replace(REGISTRATION_URL_KEY, registerURL);
            } else if (line.contains(REGISTRATION_PAYLOAD_KEY)) {
                output = line.replace(REGISTRATION_PAYLOAD_KEY, payload);
            }
            pw.println(output);
        }
        pw.flush();
        pw.close();
        in.close();
    }
    
}
