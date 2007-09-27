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

package org.netbeans.modules.jackpot.engine;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.modules.classfile.ClassFile;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.netbeans.api.jackpot.Query;
import org.netbeans.api.jackpot.QueryContext;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.spi.jackpot.EmptyScriptException;
import org.netbeans.spi.jackpot.QueryProvider;
import org.netbeans.spi.jackpot.RecursiveRuleException;
import org.netbeans.spi.jackpot.ScriptParsingException;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 * Jackpot execution engine.
 */
public class Engine {
    private boolean buildFinished;
    private JavaSource javaSource;
    private ModificationResult modifications;
    private QueryContext context;
    
    static final Logger logger = Logger.getLogger("org.netbeans.modules.jackpot");
    
    /**
     * Creates a Jackpot engine and locates its source files.
     *
     * @param context the application context the query will execute within
     * @param sourcepath the javac sourcepath
     * @param classpath the javac classpath
     * @param bootclasspath the javac bootclasspath
     */
    public Engine(QueryContext context, String sourcepath, String classpath, String bootclasspath) {
        if (buildFinished)
            throw new AssertionError("engine already initialized");
        this.context = context;
        ClassPath bootCP = ClassPathSupport.createClassPath(getPaths(bootclasspath));
        ClassPath classCP = ClassPathSupport.createClassPath(getPaths(classpath));
        ClassPath sourceCP = ClassPathSupport.createClassPath(getPaths(sourcepath));
        ClasspathInfo cpInfo = ClasspathInfo.create(bootCP, classCP, sourceCP);
        List<FileObject> files = findFiles(sourceCP);
        javaSource = JavaSource.create(cpInfo, files);
        buildFinished = true;
    }
    
    /**
     * Utility method to break apart a path string into an array of its path elements.
     */
    private List<? extends PathResourceImplementation> getPaths(String path) {
        List<PathResourceImplementation> list = new ArrayList<PathResourceImplementation> ();
        if (path == null)
            return list;
        StringTokenizer tok = new StringTokenizer(path, File.pathSeparator);
        FileObject[] paths = new FileObject[tok.countTokens()];
        int i = 0;
        while (tok.hasMoreTokens()) {
            try {
                String name = tok.nextToken();
                File file = new File(name);
                name = file.toURI().toURL().toString();
                FileObject fo = FileUtil.toFileObject(file);
                URL url;
                if (fo != null) {
                    url = FileUtil.isArchiveFile(fo) ?
                        new URL("jar:" + name + "!/") :
                        fo.getURL();
                } else {
                    // fo may be null when run from command-line
                    if (name.endsWith(".jar")) {
                        String jarSpec = "jar:" + name + "!/";
                        url = new URL(jarSpec);
                    }
                    else
                        url = file.toURI().toURL();
                }
                list.add(ClassPathSupport.createResource(url));
            } catch (MalformedURLException e) {
                ErrorManager.getDefault().notify (e);
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify (e);
            }
        }
        return list;
    }

    /**
     * Build list of Java source files from a source path.
     */
    private List<FileObject> findFiles(ClassPath sourcePath) {
	List<FileObject> files = new ArrayList<FileObject>();
        for (FileObject fo : sourcePath.getRoots())
            expand(fo, files);
	return files;
    }
    private void expand(FileObject fo, List<FileObject> files) {
        String name = fo.getNameExt();
	if (acceptFile(name)) {
            if (!files.contains(fo))
                files.add(fo);
            return;
        }
        if (acceptDirectory(name))
            for (FileObject child : fo.getChildren())
                expand(child, files);
    }
    private boolean acceptFile(String name) {
	return !name.startsWith(".") &&            // hidden file
               !name.startsWith("s.") &&           // SCCS
               !name.startsWith("p.") &&           // SCCS
                name.endsWith(".java");        
    }
    private boolean acceptDirectory(String name) {
	return ! (name.equals("SCCS") || 
		  name.equals("CVS") ||
		  name.equals("Codemgr_wsdata") ||   // TeamWare
		  name.equals("deleted_files"));     // TeamWare deleted files
    }
    
    /** 
     * Returns true if the engine is currently initialized. 
     * @return true if initialized
     */
    public boolean isInitialized() {
        return buildFinished;
    }

    boolean setProperties(Object command, String title) {
        PropertySheetInfo.find(command.getClass()).loadValues(command);
        return true;
    }

    /**
     * 
     * @param queryName 
     * @param className 
     * @return the modification set, or null if the query was cancelled
     * @throws java.lang.Exception 
     */
    public ModificationResult runCommand(String queryName, String className) throws Exception {
        Query query = null;
        try {
            query = createCommand(className);
            if (setProperties(query, queryName)) {
                return invokeQuery(query);
            }
            return null;
        } catch (EmptyScriptException e) {
            throw e;
	} catch (ScriptParsingException rpe) {
	    throw rpe;
        } catch (Throwable t) {
            throw new EngineException("Error executing operator", t);
        }
    }
    
    public ModificationResult invokeQuery(final Query query) throws IOException {
        try {
            query.init(context, javaSource);
            CancellableTask<WorkingCopy> task = new CancellableTask<WorkingCopy>() {
                public void cancel() {
                    query.cancel();
                }
                public void run(WorkingCopy wc) throws Exception {
                    JavaSource.Phase result = wc.toPhase(JavaSource.Phase.RESOLVED);
                    if (result.compareTo(JavaSource.Phase.PARSED) >= 0) {
                        query.attach(wc);
                        query.run();
                        query.release();
                    }
                }
            };
            return javaSource.runModificationTask(task);
        } finally {
            query.destroy();
        }
    }

    /**
     * 
     * @param querySetName 
     * @param queries 
     * @return the modification set, or null if the query was cancelled
     * @throws java.lang.Exception 
     */
    public ModificationResult runCommands(String querySetName, Query[] queries) throws Exception {
        MultiTransformer multi = null;
        try {
            multi = new MultiTransformer(querySetName, queries);
            return invokeQuery(multi);            
        } catch (EmptyScriptException e) {
            throw e;
	} catch (ScriptParsingException rpe) {
	    throw rpe;
        } catch (Throwable t) {
            throw new EngineException("Error executing operator", t);
        }
    }
    
    /**
     * 
     * @param className 
     * @return 
     * @throws java.lang.Exception 
     */
    public static Query createCommand(String className) throws Exception {
        Class c = createQueryClass(className);
        Object obj = c.newInstance();
        if (!(obj instanceof Query))
            throw new EngineException("I don't know how to handle " + c);
        return (Query)obj;
    }
    
    /**
     * 
     * @param queryName 
     * @param path 
     * @return 
     * @throws java.lang.Exception 
     */
    public static Query createScript(String queryName, String path) throws Exception {
        if (path.startsWith("file:"))
            path = path.substring(5);
        final InputStream is;
        final long lastmod;
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(path);
        if (fo == null) {
            File f = new File(path);
            fo = FileUtil.toFileObject(f);
            if (fo == null) 
                throw new FileNotFoundException("cannot access " + path);
        }
        Lookup.Template<QueryProvider> template = new Lookup.Template<QueryProvider>(QueryProvider.class);
        Lookup.Result<QueryProvider> result = Lookup.getDefault().lookup (template);
        for (Class<? extends QueryProvider> cls : result.allClasses()) {
            QueryProvider qp = cls.newInstance();
            if (qp.hasQuery(fo))
                return qp.getQuery(fo, queryName);
        }
        throw new ScriptParsingException(fo.getName() + " is not a recognized Query script");
    }

    /**
     * Executes a rules file script.
     * @param queryName the description of the script
     * @param path the script's file path
     * @return the modification set, or null if the query was cancelled
     * @throws java.lang.Exception if there are any script failures
     */
    public ModificationResult runScript(String queryName, String path) throws Exception {
        Query query = null;
        try {
            query = createScript(queryName, path);
            if (setProperties(query, queryName)) {
                return invokeQuery(query);
            }
            return null;
        } catch (RecursiveRuleException e) {
            throw e;
        } catch (EmptyScriptException e) {
            throw e;
	} catch (ScriptParsingException rpe) {
	    throw rpe;
        } catch (Throwable t) {
            throw new EngineException("Error executing operator", t);
        }
    }

    private static Class createQueryClass(final String className) throws Exception {
        ClassLoader loader = new ClassLoader(Engine.class.getClassLoader()) {
            public Class findClass(String name) throws ClassNotFoundException {
                if (name.endsWith(".class")) {
                    try {
                        if (name.startsWith("file://"))
                            name = name.substring(7);
                        else if (name.startsWith("file:"))
                            name = name.substring(5);
                        FileInputStream in = new FileInputStream(name);
                        int len0 = in.available();
                        byte[] b = new byte[len0];
                        int len1 = in.read(b);
                        in.close();
                        if (len1 != len0)
                            throw new ClassNotFoundException(name + ": read failure");
                        ClassFile cf = new ClassFile(name);
                        return defineClass(cf.getName().getExternalName(), b, 0, b.length);
                    } catch (IOException e) {
                        throw new ClassNotFoundException("failed loading " + name, e);
                    }
                }
                return super.findClass(name);
            }
        };
        return loader.loadClass(className);
    }
    
    /**
     * Commits changes to source files.  Returns true if engine can be closed
     * afterwards, depending upon whether there are changes and what the user
     * indicated should be done with them.
     * 
     * @return true if engine can be closed
     * @throws java.io.IOException if there are any errors during the commit phase
     *
     */
    public boolean commit() throws IOException {
        if (!buildFinished)
            throw new AssertionError("engine not initialized");
        modifications.commit();
        return true;
    }

    /**
     * 
     * @param className 
     * @return 
     */
    public static PropertySheetInfo getPropertySheetInfo(String className) {
        try {
            Class cls = Class.forName(className);
            return PropertySheetInfo.find(cls);
        } catch (Exception e) {
            return null;
        }
    }
}
