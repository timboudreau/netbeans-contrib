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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor.completion.latex.help;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.ErrorManager;

/**
 *
 * @author Jan Lahoda
 */
/*public*/ class PreprocessHelp {
    
    /** Creates a new instance of PreprocessHelp */
    public PreprocessHelp() {
    }
    
    private void copyStreams(InputStream in, OutputStream out) throws IOException {
        int read;
        
        while ((read = in.read()) != (-1)) {
            out.write(read);
        }
    }
    
    private void isFileHelp(File toc, Properties commandToFile, JarOutputStream output) throws IOException {
        File directory = toc.getParentFile();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(toc)));
        String read;
        Set entered = new HashSet();
//        Pattern pattern = Pattern.compile(".*<LI><A NAME=\"[^\"]*\" HREF=\"([^\"]*)\">(.*)</A>.*");
        Pattern pattern = Pattern.compile(".*<LI><A HREF=\"([^\"]*)\">(.*)</A>.*");
        
        while ((read = in.readLine()) != null) {
            Matcher matcher = pattern.matcher(read);
            
//            System.err.println("LINE=" + read);
            
            if (matcher.matches()) {
                String file = matcher.group(1);
                String name = matcher.group(2);
                
                System.err.println("Adding file=" + file + ", name=" + name);
                
                int number = file.indexOf('#');
                
                if (number != (-1)) {
                    file = file.substring(0, number);
                }
		
                commandToFile.setProperty(name, file);

//                break; //!!!
	    }
	}

        in.close();
        
        Enumeration en = commandToFile.propertyNames();
        
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            String file = (String) commandToFile.get(name);
            
            if (entered.contains(file))
                continue;
            
            entered.add(file);
            
            JarEntry newEntry = new JarEntry(file);
            InputStream source = new BufferedInputStream(new FileInputStream(new File(directory, file)));
	    
            output.putNextEntry(newEntry);
            copyStreams(source, output);
            source.close();
        }
    }
    
    private void createHelpJar(File toc, File outputJar) throws IOException {
        JarOutputStream outs = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputJar)));
        Properties commandToFile = new Properties();

        isFileHelp(toc, commandToFile, outs);
        
        JarEntry entry = new JarEntry("index.properties");
        
        outs.putNextEntry(entry);
        
        commandToFile.store(outs, null);
        
        outs.close();
    }
    
    public static void createHelpJar(String helpDir) {
        try {
            new PreprocessHelp().createHelpJar(new File(helpDir, "latex2e_176.html"), getHelpFile());
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private static File getHelpFile() throws IOException {
        File iconDir = new File(new File(new File(System.getProperty("netbeans.user"), "var"), "latex"), "latex2e.jar");
        
        iconDir.getParentFile().mkdirs();
        
        return iconDir;
    }

}
