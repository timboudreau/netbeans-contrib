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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.help;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jan Lahoda
 */
public class CreateMap {

    /** Creates a new instance of CreateMap */
    public CreateMap() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
	    System.err.println("Incorrect number of arguments, one argument containing the location of the base dir is required.");
	    return ;
	}
	
        File baseDir = new File(args[0]);
	
	if (!baseDir.exists()) {
	   System.err.println(baseDir.getAbsolutePath() + " does not exist.");
	   return ;
	}
        
	if (!baseDir.isDirectory()) {
	   System.err.println(baseDir.getAbsolutePath() + " is not a directory.");
	   return ;
	}
	
        File in  = new File(baseDir, "labels.pl");
        File out = new File(baseDir, "LaTeXMap.xml");
        
        Map map = createMapFile(in, out);
        
        File outTOC = new File(baseDir, "LaTeXTOC.xml");
        File inTOC  = new File(baseDir, "LaTeXManual.html");
        
        createTOC(inTOC, outTOC, map);
    }
    
    private static Map createMapFile(File in, File out) throws IOException {
        BufferedReader input  = new BufferedReader(new FileReader(in));
        PrintWriter    output = new PrintWriter(new FileWriter(out));
        Map            map    = new HashMap();

        output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        output.println();
        output.println("<!DOCTYPE map");
        output.println("  PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp Map Version 1.0//EN\"");
        output.println("         \"file:///home/lahvac/netbeans/netbeans/bin/nbuser/sampledir/javahelp/map_1_0.dtd\">");
        output.println("<!--This is the original DTD: \"http://java.sun.com/products/javahelp/map_1_0.dtd\"-->");
        output.println();
        output.println("<map version=\"1.0\">");
        
        output.println("<mapID target=\"latex.default\" url=\"LaTeXManual.html\" />");

        String line = null;
        String actualKey = null;
        
        while ((line = input.readLine()) != null) {
            if (line.startsWith("$key")) {
                actualKey = line.split("/")[1];
                actualKey = "latex." + actualKey.replace(':', '.');
            }
            
            if (line.startsWith("$external_labels")) {
                String url = line.split("\\|")[1];
                
                output.print("<mapID target=\"");
                output.print(actualKey);
                output.print("\" url=\"");
                output.print(url);
                output.println("\"/>");
                
                map.put(url, actualKey);
                
                actualKey = null;
            }
        }

        output.println("</map>");
        output.close();
        input.close();
        
        return map;
    }

    private static void createTOC(File in, File out, Map map) throws IOException {
        BufferedReader input  = new BufferedReader(new FileReader(in));
        PrintWriter    output = new PrintWriter(new FileWriter(out));
        
        String line  = null;
        boolean noStop = false;
        
        output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        output.println();
        output.println("<!DOCTYPE toc");
        output.println("  PUBLIC \"-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 1.0//EN\"");
        output.println("         \"file:///home/lahvac/netbeans/netbeans/bin/nbuser/sampledir/javahelp/toc_1_0.dtd\">");
        output.println("<!--This is the original DTD: \"http://java.sun.com/products/javahelp/toc_1_0.dtd\"-->");
        output.println();
        output.println("<toc version=\"1.0\">");

        while ((line = input.readLine()) != null && line.indexOf("<!--Table of Child-Links-->") == (-1))
            ;
        
        output.println("<tocitem text=\"LaTeX Support Help\" target=\"latex.default\">");
        while ((line = input.readLine()) != null && line.indexOf("<!--End of Table of Child-Links-->") == (-1)) {
            if (line.startsWith("<UL>")) {
                noStop = true;
                continue;
            }
            
            if (line.indexOf("HREF=\"") != (-1)) {
                if (noStop) {
                    noStop = false;
                } else {
                    output.println("</tocitem>");
                }
                
                String file = line.split("\"")[1];
                String name = line.split(">")[1].split("<")[0];
                
                output.println("<tocitem text=\"" + name + "\" target=\"" + map.get(file) + "\">");
                
                continue;
            }
            
            if (line.startsWith("</UL>")) {
                output.println("</tocitem>");
                continue;
            }
        }
        
        output.println("</tocitem>");
        
        output.println("</toc>");
        
        output.close();
        input.close();
    }
}
