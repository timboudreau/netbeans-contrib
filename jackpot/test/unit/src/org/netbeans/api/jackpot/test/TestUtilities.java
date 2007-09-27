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

package org.netbeans.api.jackpot.test;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.netbeans.api.jackpot.Query;
import org.netbeans.api.jackpot.Transformer;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.jackpot.engine.BuildErrorsException;
import org.netbeans.modules.jackpot.engine.CommandLineQueryContext;
import org.netbeans.modules.jackpot.engine.Engine;
import org.netbeans.modules.jackpot.engine.Result;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;

/**
 * Utilities to aid unit testing Jackpot Query and Transformer classes.
 *
 * @author Jaroslav Tulach
 * @author Tom Ball
 */
public final class TestUtilities {
    
    // do not instantiate
    private TestUtilities() {}
    
    /**
     * Tests whether a transformation makes an expected result.
     *
     * @param from   the source text to be transformed.
     * @param result the expected text after transformation.
     * @param clazz  the transformation class to use.
     * @throws TransformationException if the transformed text doesn't match the result.
     * @throws java.lang.Exception 
     */
     public static void assertTransform(String from, String result, Class clazz) throws TransformationException, Exception {
        File src = copyStringToFile(getClassName(from), from);
        String className = clazz.getName();
        apply(tempDirectory, Engine.createCommand(className));

        String txt = copyFileToString(src);
        if (!txt.equals(result))
            throw new TransformationException("expected: \"" + result + "\" got: \"" + txt + "\"");
    }
     
     private static String getClassName(String src) {
         return null; // FIXME
     }

    /**
     * Applies a Query class to a directory of source files.
     * 
     * @param  dir the directory containing the source files to be modified.
     * @param  queryName the query to apply to the source files.
     * @return the matches found
     * @throws BuildErrorsException 
     *         If any errors are found when building the source files.
     * @throws Exception
     *         If any changes were made to the source files after applying the 
     *         Query class.
     */
    public static List<Result> applyQuery(File dir, String queryName) 
            throws BuildErrorsException, Exception {
        return apply(dir, Engine.createCommand(queryName));
    }
    
    /**
     * Applies an instance of a Query to a directory of source files.  This
     * method is useful when testing the query's options.
     * 
     * @param  dir the directory containing the source files to be modified.
     * @param  query the query instance to apply to the source files.
     * @return the matches found
     * @throws BuildErrorsException 
     *         If any errors are found when building the source files.
     * @throws Exception
     *         If any changes were made to the source files after applying the 
     *         Query class.
     */
    public static List<Result> applyQuery(File dir, Query query) 
            throws BuildErrorsException, Exception {
        return apply(dir, query);
    }

    /**
     * Applies a Transformer class to a directory of source files.
     * 
     * @param  dir the directory containing the source files to be modified.
     * @param  transformerName the transformer to apply to the source files.
     * @return the number of matches found
     * @throws BuildErrorsException 
     *         If any errors are found when building the source files.
     * @throws java.lang.Exception 
     */
    public static int applyTransformer(File dir, String transformerName) 
            throws BuildErrorsException, Exception {
        return apply(dir, Engine.createCommand(transformerName)).size();
    }

    /**
     * Applies an instance of a Transformer to a directory of source files.  This
     * method is useful when testing the transformer's options.
     * 
     * @param  dir the directory containing the source files to be modified.
     * @param  transformer the transformer to apply to the source files.
     * @return the number of matches found
     * @throws BuildErrorsException 
     *         If any errors are found when building the source files.
     * @throws java.lang.Exception 
     */
    public static int applyTransformer(File dir, Transformer transformer) 
            throws BuildErrorsException, Exception {
        return apply(dir, transformer).size();
    }

    private static List<Result> apply(File dir, Query query) 
            throws BuildErrorsException, Exception {
        assert dir.isDirectory() : dir.getName() + " is not a directory";
        CommandLineQueryContext context = new CommandLineQueryContext();
        Engine eng = new Engine(context, dir.getPath(), System.getProperty("java.class.path"), null);

        ModificationResult result = eng.invokeQuery(query);
        result.commit();
        return context.getResults();
    }
    
    /**
     * Returns a string which contains the contents of a file.
     *
     * @param f the file to be read
     * @return the contents of the file(s).
     * @throws java.io.IOException 
     */
    public final static String copyFileToString (java.io.File f) throws java.io.IOException {
        int s = (int)f.length ();
        byte[] data = new byte[s];
        int len = new FileInputStream (f).read (data);
        if (len != s)
            throw new EOFException("truncated file");
        return new String (data);
    }

    /**
     * Makes a directory set up as a Repository-registered FileSystem.
     * @param test the unit test instance
     * @return the FileObject for this directory
     * @throws java.io.IOException if there are any problems creating the directory
     */
    public static FileObject makeScratchDir(NbTestCase test) throws IOException {
        test.clearWorkDir();
        File root = test.getWorkDir();
        assert root.isDirectory() && root.list().length == 0;
        String userdir = System.getProperty("netbeans.user");
        if (userdir == null)
            System.setProperty("netbeans.user", root.getAbsolutePath());
        File logdir = new File(root, "var/log");
        logdir.mkdirs();
        FileObject fo = FileUtil.toFileObject(root);
        if (fo != null) {
            // Presumably using masterfs.
            return fo;
        } else {
            // For the benefit of those not using masterfs.
            LocalFileSystem lfs = new LocalFileSystem();
            try {
                lfs.setRootDirectory(root);
            } catch (PropertyVetoException e) {
                assert false : e;
            }
            Repository.getDefault().addFileSystem(lfs);
            return lfs.getRoot();
        }
    }
    
    /**
     * Copies a string to a specified file.
     *
     * @param f the file to use.
     * @param content the contents of the returned file.
     * @return the created file
     * @throws java.lang.Exception 
     */
    public final static File copyStringToFile (File f, String content) throws Exception {
        FileOutputStream os = new FileOutputStream(f);
        InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
        FileUtil.copy(is, os);
        os.close ();
            
        return f;
    }

    private final static File copyStringToFile(String filename, String res) throws Exception {
        File f = new File(tempDirectory, filename);
        f.deleteOnExit ();
        return copyStringToFile(f, res);
    }

    private static File tempDirectory;
    {
        try {
            File f = File.createTempFile("foo", "bar");
            tempDirectory = f.getParentFile();
        } catch (IOException e) {
            tempDirectory = new File("/tmp");
        }
    }
}