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

package org.netbeans.modules.ant.moduleinfotask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.openide.ErrorManager;
import org.openide.modules.Dependency;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/**
 * This generates the crossreference of module dependencies.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class ModuleInfoGenerator {
    
    /** Creates a new instance of ModuleInfoGenerator */
    private ModuleInfoGenerator() {
    }
    
    private static ModuleInfoComparator moduleInfoComparator = new ModuleInfoComparator();
    
    public static void generateHTML(File htmlFile) {
        ModuleInfo[] moduleInfos = getModuleInfos();
        
        PrintWriter out = null;
        try {
            FileWriter fos = new FileWriter(htmlFile);
            out = new PrintWriter(fos);
            
            Map<String, String> cnToDisplayName = new HashMap<String, String>();
            Map<String, String> publicPackageTocCn = new TreeMap<String, String>();
            TreeMap<String, Set<String>> reverseDependenciesMap = new TreeMap<String, Set<String>>();
            for (int i = 0; i < moduleInfos.length; i ++) {
                cnToDisplayName.put(moduleInfos[i].getCodeName(), moduleInfos[i].getDisplayName());
                String publicPackagesAttr = ((String)moduleInfos[i].getAttribute("OpenIDE-Module-Public-Packages"));
                if (publicPackagesAttr != null) {
                    if (!publicPackagesAttr.equals("-")) {
                        String[] publicPackages = publicPackagesAttr.split(",");
                        for (int j = 0; j < publicPackages.length; j++) {
                            String publicPackage = publicPackages[j];
                            publicPackageTocCn.put(publicPackage, moduleInfos[i].getCodeName());
                        }
                    }
                }
            }
            
            out.println("<html>");
            out.println("<head>");
            out.println("<style type=text/css media=all>");
            out.println("@import url(\"http://www.netbeans.org/netbeans.css\");");
            out.println("</style>");
            out.println("</head>");
            out.println("<body style=\"font-size: medium;\">");
            
            out.println("<a name=\"top\"></a>");
            out.println("<h1>Module Info</h1><br>");
            out.println("<a style=\"font-size: x-small;\" href=\"#modules\">&#8595; Modules</a><br>");
            out.println("<a style=\"font-size: x-small;\" href=\"#packages\">&#8595; Public packages of Modules</a><br>");
            out.println("<a style=\"font-size: x-small;\" href=\"#moduledetails\">&#8595; Module details</a><br>");
            out.println("<a style=\"font-size: x-small;\" href=\"#reversedependencies\">&#8595; Reverse Module Dependencies</a><br>");
            out.println("<hr>");
            
            out.println("<a name=\"modules\"></a>");
            out.println("<h1>Modules</h1><br>");
            out.println("<table width=\"100%\" style=\"border: 1px solid black;\">");
            for (int i = 0; i < moduleInfos.length; i ++) {
                ModuleInfo moduleInfo = moduleInfos[i];
                out.println(
                "<tr><td><nobr>"
                + "<a name=\""
                + moduleInfo.getCodeName().replace('.', '_').replace('/',  '_')
                + "_toc\">"
                + "</a>"
                + "<a href=\"#"
                + moduleInfo.getCodeName().replace('.', '_').replace('/',  '_')
                + "_details\">"
                + "&#8595; "
                + moduleInfo.getDisplayName() + " [" + moduleInfo.getCodeName() + "]"
                + "</a>"
                + "</nobr></td></tr>");
            }
            out.println("</table>");
            
            out.println("<a style=\"font-size: x-small;\" href=\"#top\">&#8593;</a>");
            out.println("<a name=\"packages\"></a>");
            out.println("<h1>Public packages of Modules</h1><br>");
            out.println("<table width=\"100%\" style=\"border: 1px solid black;\">");
            for(Iterator iter = publicPackageTocCn.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                String cn = (String) publicPackageTocCn.get(key);
                String displayName = (String) cnToDisplayName.get(cn);
                out.println(
                "<tr valign=\"top\"><td width=\"300\">"
                + key
                + "</td><td><nobr>" +
                "<a href=\"#"
                + cn.replace('.', '_').replace('/',  '_')
                + "_details\">"
                + "&#8595; "
                + displayName + " [" + cn + "]"
                + "</a>"
                + "<nobr></td></tr>");
            }
            out.println("</table>");
            
            out.println("<a style=\"font-size: x-small;\" href=\"#top\">&#8593;</a><br>");
            out.println("<a name=\"moduledetails\"></a>");
            out.println("<h1>Module Details</h1><br>");
            for (int j= 0; j < moduleInfos.length; j++) {
                ModuleInfo moduleInfo = moduleInfos[j];
                out.println("<a name=\""
                + moduleInfo.getCodeName().replace('.', '_').replace('/',  '_')
                + "_details\">"
                + "</a>");
                out.println("<table width=\"100%\" style=\"border: 1px solid black;\" cellspacing=\"2\">");
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Module Name</b></td><td>"
                + "<a href=\"#"
                + moduleInfo.getCodeName().replace('.', '_').replace('/',  '_')
                + "_toc\">"
                + "&#8593; "
                + moduleInfo.getDisplayName()
                + "</a>"
                + "</td></tr>\n");
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Short Description</b></td><td>" + moduleInfo.getLocalizedAttribute("OpenIDE-Module-Short-Description") + "</td></tr>\n");
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Long Descriptionn</td><td>" + moduleInfo.getLocalizedAttribute("OpenIDE-Module-Long-Description") + "</td></tr>\n");
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Code Name</b></td><td>" + moduleInfo.getCodeName() + "</td></tr>\n");
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Code Base Name</b></td><td>" + moduleInfo.getCodeNameBase() + "</td></tr>\n");
                
                Set dependenciesSet = moduleInfo.getDependencies();
                if (dependenciesSet != null) {
                    out.println("<tr valign=\"top\"><td width=\"300\"><b>Dependencies</b></td><td>");
                    for (Iterator iterator = dependenciesSet.iterator(); iterator.hasNext(); ) {
                        Dependency dependency = ((Dependency) iterator.next());
                        String dependencyName = dependency.getName();
                        String dependencyDisplayName = (String) cnToDisplayName.get(dependencyName);
                        String dependencyVersion = dependency.getVersion();
                        dependencyVersion = (dependencyVersion == null ? "/unspecified version" : "/" + dependencyVersion);
                        int dependencyType = dependency.getType();
                        if (dependency.getType() == Dependency.TYPE_MODULE) {
                            addReverseDependency(reverseDependenciesMap, dependencyName, moduleInfo.getCodeName());
                            out.println("<a href=\"#"
                            + dependencyName.replace('.', '_').replace('/',  '_')
                            + "_details\">"
                            +  (dependencyDisplayName == null ?
                                (moduleInfo.getCodeName().compareTo(dependencyName) < 0 ? "&#8595; " : "&#8593; ")
                                :(moduleInfo.getDisplayName().compareTo(dependencyDisplayName) < 0 ? "&#8595; " : "&#8593; "))
                                + dependencyDisplayName
                            + " [ "
                            + dependencyName
                            + dependencyVersion
                            + " ] Type: "
                            + dependencyType
                            + "</a><br>");
                        }
                    }
                    out.println("</td></tr>");
                }
                String publicPackages = ((String)moduleInfo.getAttribute("OpenIDE-Module-Public-Packages"));
                if (publicPackages != null) {
                    out.println("<tr valign=\"top\"><td width=\"300\"><b>Public Packages</b></td><td>" + arrayToString(publicPackages.split(",")) + "</td></tr>");
                }
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Specification Version</b></td><td>" + moduleInfo.getSpecificationVersion() + "</td></tr>");
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Implementation Version</b></td><td>" + moduleInfo.getImplementationVersion() + "</td></tr>");
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Build Version</b></td><td>" + moduleInfo.getBuildVersion() + "</td></tr>");
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Enabled</b></td><td>" + (moduleInfo.isEnabled() ? "Yes" : "No")  + "</td></tr>");
                out.println("<tr valign=\"top\"><td width=\"300\"><b>Providers</b></td><td>" + arrayToString(moduleInfo.getProvides()) + "</td></tr>");
                out.println("</table>");
                out.println("<a style=\"font-size: x-small;\" href=\"#top\">&#8593;</a><br>");
            }
            
            out.println("<a name=\"reversedependencies\"></a>");
            out.println("<h1>Reverse Module Dependencies</h1><br>");
            for(Iterator iter = reverseDependenciesMap.keySet().iterator(); iter.hasNext();) {
                out.println("<table width=\"100%\" style=\"border: 1px solid black;\">");
                String dependee = (String) iter.next();
                String dependeeDisplayName = (String) cnToDisplayName.get(dependee);
                Set<String> dependenciesSet = reverseDependenciesMap.get(dependee);
                if (dependenciesSet != null) {
                    out.println(
                    "<tr valign=\"top\"><td width=\"300\"><nobr>"
                    + "<a href=\"#"
                    + dependee.replace('.', '_').replace('/',  '_')
                    + "_details\">"
                    + (dependeeDisplayName == null ? dependee : dependeeDisplayName)
                    + "</a>"
                    + "</nobr></td><td>");
                    for (Iterator it = dependenciesSet.iterator(); it.hasNext();) {
                        String dependent = (String) it.next();
                        String dependentDisplayName = (String) cnToDisplayName.get(dependent);
                        out.println(
                        "<a href=\"#"
                        + dependent.replace('.', '_').replace('/',  '_')
                        + "_details\">"
                        + (dependentDisplayName == null ? dependent : dependentDisplayName)
                        + "</a>"
                        + "<br>");
                    }
                    out.println(
                    "</td></tr>");
                }
                out.println("</table>");
                out.println("<a style=\"font-size: x-small;\" href=\"#top\">&#8593;</a><br>");
            }
            out.println("</body>");
            out.println("</html>");
        } catch (IOException ioe) {
            ErrorManager.getDefault().annotate(ioe, "Error module info file.");
        } finally {
            try {
                out.close();
            } catch (Throwable _ex) {
            }
        }
//                BufferedImage image = new BufferedImage(modulesGraph.getWidth(), modulesGraph.getHeight(), BufferedImage.TYPE_INT_RGB);
//                Graphics2D g2 = image.createGraphics();
//                modulesGraph.paint(g2);
//                g2.dispose();
//                try {
//                    ImageIO.write(image, "jpeg", new File(filePath+".jpg"));
//                } catch (IOException e) {
//                    ErrorManager.getDefault().annotate(e, "Error while writing image file");
//                }
    }
    
    public static void generateXML(File xmlFile) {
        ModuleInfo[] moduleInfos = getModuleInfos();
        
        PrintWriter out = null;
        try {
            FileWriter fos = new FileWriter(xmlFile);
            out = new PrintWriter(fos);
            
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<modules>\n");
            
            
            
            out.write("</modules>\n");
            out.flush();
        } catch(IOException ioe) {
            ErrorManager.getDefault().annotate(ioe, "Error writing bindings file");
        } finally {
            try {
                out.close();
            } catch(Throwable _ex) { }
        }
    }
    
    private static void outputTag(PrintWriter out, String tagName, String value, boolean cdataEscape) throws IOException {
        out.write("    <" + tagName + ">");
        if(cdataEscape)
            out.write("<![CDATA[" + value + "]]>");
        else
            out.write(value);
        out.write("</" + tagName + ">\n");
    }
    
    public static String arrayToString(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return "";
        }
        
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < objects.length; i++) {
            sb.append(objects[i]);
            sb.append("<br>");
        }
        return sb.toString();
    }
    
    private static ModuleInfo[] getModuleInfos() {
        Lookup.Template<ModuleInfo> templ = new Lookup.Template<ModuleInfo>(ModuleInfo.class);
        Lookup.Result<ModuleInfo> result = Lookup.getDefault().lookup(templ);
        Collection<? extends ModuleInfo> modules = result.allInstances(); // Collection<ModuleInfo>
        ModuleInfo[] moduleInfos = (ModuleInfo[])modules.toArray(new ModuleInfo[0]);
        Arrays.sort(moduleInfos, moduleInfoComparator);
        return moduleInfos;
    }
    
    private static class ModuleInfoComparator implements Comparator<ModuleInfo> {
        public int compare(ModuleInfo o1, ModuleInfo o2) {
                return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
        }
    }
    
    private static void addReverseDependency(Map<String, Set<String>> reverseDependenciesMap, String dependee, String dependent) {
        Set<String> dependencies = reverseDependenciesMap.get(dependee);
        if (dependencies == null) {
            dependencies = new TreeSet<String>();
            reverseDependenciesMap.put(dependee, dependencies);
        }
        if (!dependencies.contains(dependent)) {
            dependencies.add(dependent);
        }
    }
}
