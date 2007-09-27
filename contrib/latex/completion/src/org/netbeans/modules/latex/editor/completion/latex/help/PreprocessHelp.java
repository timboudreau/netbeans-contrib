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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
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
