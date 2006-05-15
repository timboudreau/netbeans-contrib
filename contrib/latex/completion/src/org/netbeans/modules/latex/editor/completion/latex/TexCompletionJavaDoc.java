/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor.completion.latex;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.text.Document;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;

import org.openide.modules.InstalledFileLocator;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.latex.editor.completion.latex.TexCompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.openide.util.NbBundle;

/**
 *
 * @author  Jan Lahoda
 */
public class TexCompletionJavaDoc {
    
    private Reference jarFile;
    private Reference directory;
    
    private RequestProcessor.Task task = null;
    
    private static final boolean debug = true;
    
    public static final String NOT_FOUND = NbBundle.getMessage(TexCompletionJavaDoc.class, "LBL_NoHelpForItem");
    public static final String HELP_NOT_INSTALLED_LINK = "install-help"; // NOI18N
    public static final String HELP_NOT_INSTALLED = NbBundle.getMessage(TexCompletionJavaDoc.class, "LBL_HelpNotInstalled", new Object[] {HELP_NOT_INSTALLED_LINK});
    
    /** Creates a new instance of TexCompletionJavaDoc */
    public TexCompletionJavaDoc() {
    }
    
    private File getHelpSource() {
        //TODO: NB specific (not StandAlone!)
        File help = InstalledFileLocator.getDefault().locate("var/latex/latex2e.jar", null, true); // NOI18N
        
        if (help == null && Boolean.getBoolean("netbeans.module.test")) {
            File moduleJar = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
            
            if (moduleJar.exists() && moduleJar.isFile()) {
                help = new File(new File(moduleJar.getParentFile(), "docs"), "latex2e.jar");
                
                if (!help.exists() || !help.isFile()) {
                    help = null;
                }
            }
        }
        
        if (debug)
            System.err.println("help = " + help );
        
        return help;
    }
    
    private synchronized JarFile getJarFile() throws IOException {
        if (jarFile != null) {
            JarFile file = (JarFile) jarFile.get();
            
            if (file != null)
                return file;
        }
        
	File jarAsFile = getHelpSource();
	
	if (jarAsFile == null)
	    return null;

        JarFile file = new JarFile(jarAsFile);
        
        jarFile = new WeakReference(file);
        
        return file;
    }
    
    public boolean isJavaDocInstalled() {
        try {
            return getJarFile() != null;
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            
            return false;
        }
    }
    
    private synchronized Properties getDirectory() throws IOException {
        if (directory != null) {
            Properties dir = (Properties) directory.get();
            
            if (dir != null)
                return dir;
        }
        
        JarFile file = getJarFile();
	
	if (file == null)
	    return null;

        JarEntry dirEntry = file.getJarEntry("index.properties"); // NOI18N
        
        if (dirEntry == null)
            throw new IllegalArgumentException("The provided help file does not have index.properties file."); // NOI18N
        
        Properties dir = new Properties();
        InputStream ins = file.getInputStream(dirEntry);
        
        dir.load(ins);
        directory = new WeakReference(dir);
        
        return dir;
    }
    
    private InputStream getItemHelp(String item) throws IOException {
        Properties dir  = getDirectory();
        JarFile    file = getJarFile();
	
	if (dir == null || file == null) {
	    return new ByteArrayInputStream(HELP_NOT_INSTALLED.getBytes());
        }

        String     targetName = dir.getProperty(item);
        
        if (targetName == null)
            return null;
        
        JarEntry entry = file.getJarEntry(targetName);
        
        return file.getInputStream(entry);
    }
    
    private String readFile(InputStream ins) throws IOException {
        int read;
        StringBuffer buffer = new StringBuffer();
        
        while ((read = ins.read()) != (-1)) {
            buffer.append((char) read);
        }
        
        return buffer.toString();
    }
    
    private String readItemHelp(String item) throws IOException {
        InputStream ins = getItemHelp(item);
        
        if (ins == null)
            return NOT_FOUND;
        
        return readFile(ins);
    }

    public CompletionTask createDocumentationForName(String name) {
        return new AsyncCompletionTask(new CompletionQueryImpl(name));
    }
   
    private final class CompletionQueryImpl extends AsyncCompletionQuery {

        private String name;
        
        public CompletionQueryImpl(String name) {
            this.name = name;
        }
        
        protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
            try {
                resultSet.setDocumentation(new TexCompletionDocumentation(readItemHelp(name)));
            } catch (IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            resultSet.finish();
        }
        
    }
    
}
