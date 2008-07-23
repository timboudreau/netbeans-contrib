/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.utils.env;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.installer.utils.FileProxy;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.Pair;
import org.netbeans.installer.utils.exceptions.DownloadException;
import org.netbeans.installer.utils.helper.Platform;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SystemRequements {
    
    public static final String SYSTEM_REQUEMENTS_DATA = FileProxy.RESOURCE_SCHEME_PREFIX + "org/netbeans/installer/utils/env/SystemRequements.xml";
    
    public static final String ANY_PLATFORM = "any";
    public static final String INTEL_PLATFORM = "intel";
    
    private static SystemRequements instance = null;
    
    private Set<Pair<Pattern, Pattern>> distributions = new HashSet<Pair<Pattern, Pattern>>();
    private Set<Pair<Pattern, Float>> cpus = new HashSet<Pair<Pattern, Float>>();
    private HashMap<Pair<Pattern, Pattern>, Map<String, Set<String>>> patches = new HashMap<Pair<Pattern, Pattern>,  Map<String, Set<String>>>();
    private float memoryMinimum = 0;

    public static synchronized SystemRequements getInstance() {
        if (instance == null) instance = new SystemRequements();
        return instance;
    }
    
    private DefaultHandler handler = new DefaultHandler() {
        
        private boolean distributionsMode = false;
        private Pair<Pattern, Pattern> currentPatchDistribution = null;
        private String currentPlatform = null;
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("distributions")) {
                distributionsMode = true;
            }
            if (distributionsMode && qName.equals("distribution")) {
                distributions.add(new Pair<Pattern, Pattern>(Pattern.compile(attributes.getValue("name")), Pattern.compile(attributes.getValue("version"))));
            }
            if (qName.equals("cpu")) {
                cpus.add(new Pair<Pattern, Float>(Pattern.compile(attributes.getValue("model")), Float.parseFloat(attributes.getValue("minimum-frequency"))));
            }
            if (qName.equals("memory")) {
                memoryMinimum = Float.parseFloat(attributes.getValue("minimum-size"));
            }
            if (!distributionsMode && qName.equals("distribution")) {
                currentPatchDistribution = new Pair<Pattern, Pattern>(Pattern.compile(attributes.getValue("name")), Pattern.compile(attributes.getValue("version")));
                if (!patches.containsKey(currentPatchDistribution)) {
                    Map<String, Set<String>> distribution = new HashMap<String, Set<String>>();
                    patches.put(currentPatchDistribution, distribution);
                    currentPlatform = attributes.getValue("platform");
                    if (currentPlatform == null) currentPlatform = ANY_PLATFORM;
                    distribution.put(currentPlatform, new HashSet<String>());                    
                }
            }
            if (currentPatchDistribution != null && qName.equals("patch")) {
                patches.get(currentPatchDistribution).get(currentPlatform).add(attributes.getValue("id"));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals("distributions")) {
                distributionsMode = false;
            }
            if (qName.equals("distribution")) {
                currentPatchDistribution = null;
                currentPlatform = null;
            }
        }       
        
    };
    
    private SystemRequements() {        
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            SAXParser sp = spf.newSAXParser();
            sp.parse(FileProxy.getInstance().getFile(SYSTEM_REQUEMENTS_DATA, SystemRequements.class.getClassLoader(), true), handler);
        } catch (DownloadException ex) {
            LogManager.log(ex);
        } catch (IOException ex) {
            LogManager.log(ex);
        } catch (ParserConfigurationException ex) {
            LogManager.log(ex);
        } catch (SAXException ex) {
            LogManager.log(ex);
        }       
        if (distributions.isEmpty()) defaultDistributions();
        if (cpus.isEmpty()) defaultCPUs();        
    }
    
    private void defaultDistributions() {
        distributions.clear();
        distributions.add(new Pair<Pattern, Pattern>(Pattern.compile(".*"), Pattern.compile(".*")));        
    }
    
    private void defaultCPUs() {
        cpus.clear();
        cpus.add(new Pair<Pattern, Float>(Pattern.compile(".*"), 0.0F));    
    }
    
    public float getMemoryMinimum() {
        return memoryMinimum;
    }

    public boolean checkDistribution(String name, String version) {
        for(Pair<Pattern, Pattern> entry: distributions) {
            if (entry.getKey().matcher(name).matches() && entry.getValue().matcher(version).matches()) return true;
        }
        return false;
    }
    
    public boolean checkCPU(String model, float frequency) {
        for(Pair<Pattern, Float> entry: cpus) {
            if (entry.getKey().matcher(model).matches() && frequency >= entry.getValue()) return true;
        }
        return false;        
    }
    
    private boolean isIntelPlatform(String platform) {
        return platform.equals(Platform.LINUX_X86.getHardwareArch()) || platform.equals(Platform.LINUX_X64.getHardwareArch());
    }
    
    public Set<String> getPatches(String name, String version, String platform) {
        Set<String> result = new HashSet<String>();
        for(Map.Entry<Pair<Pattern, Pattern>, Map<String, Set<String>>> entry: patches.entrySet()) {
            if (entry.getKey().getKey().matcher(name).matches() && entry.getKey().getValue().matcher(version).matches()) {
                if (entry.getValue().containsKey(platform)) result.addAll(entry.getValue().get(platform));
                if (entry.getValue().containsKey(ANY_PLATFORM)) result.addAll(entry.getValue().get(ANY_PLATFORM));
                if (isIntelPlatform(platform) && entry.getValue().containsKey(INTEL_PLATFORM)) result.addAll(entry.getValue().get(INTEL_PLATFORM));
            }
        }
        return result;
    }
    
    public boolean hasPathesInfo(String name, String version, String platform) {
        for(Map.Entry<Pair<Pattern, Pattern>, Map<String, Set<String>>> entry: patches.entrySet()) {
            if (entry.getKey().getKey().matcher(name).matches() && entry.getKey().getValue().matcher(version).matches()) {
                if (entry.getValue().containsKey(platform) && !entry.getValue().get(platform).isEmpty()) return true;
                if (entry.getValue().containsKey(ANY_PLATFORM) && !entry.getValue().get(ANY_PLATFORM).isEmpty()) return true;
                if (isIntelPlatform(platform) && entry.getValue().containsKey(INTEL_PLATFORM) && !entry.getValue().get(INTEL_PLATFORM).isEmpty()) return true;
            }
        }
        return false;
    }
    
}
