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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.jackpot.rules;

import com.sun.tools.javac.api.JavacTool;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import javax.tools.JavaCompiler;
import org.netbeans.api.jackpot.Query;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.spi.jackpot.QueryProvider;
import org.netbeans.spi.jackpot.ScriptParsingException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Given a rule script, creates a Query
 * 
 * @author Tom Ball
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.spi.jackpot.QueryProvider.class)
public class QueryFactory implements QueryProvider {
    private static QueryFactory instance = new QueryFactory();
    
    static QueryFactory getInstance() {
        return instance;
    }

    public boolean hasQuery(FileObject script) {
        return script != null && script.getExt().equals("rules");
    }

    @SuppressWarnings("unchecked")
    public Query getQuery(FileObject fobj, String queryDescription) throws Exception {
        String classpath = jackpotJarsPath();
        ClassLoader jscl = JavaSource.class.getClassLoader();
        ClassLoader cl = new JackpotClassLoader(jscl);
        Class cls = Class.forName("org.netbeans.modules.jackpot.rules.parser.RuleTransformerFactory", true, cl);
        Method m = cls.getMethod("getQuery", FileObject.class, String.class);
        try {
            Object q = m.invoke(null, fobj, classpath);
            return (Query)q;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof ScriptParsingException)
                throw (ScriptParsingException)e.getCause();
            else
                throw e;
        }
    }
    
    private static String jackpotJarsPath() throws IOException {
        Class[] representativeClasses = {
            QueryFactory.class, // jackpot.rules
            Query.class, // jackpot
            JavaSource.class, // java.source
            JavaCompiler.class, // libs.javacapi
            JavacTool.class, // libs.javacimpl
        };
        StringBuilder b = new StringBuilder();
        for (Class c : representativeClasses) {
            if (b.length() > 0) {
                b.append(File.pathSeparatorChar);
            }
            URL loc = c.getProtectionDomain().getCodeSource().getLocation();
            File jar = FileUtil.archiveOrDirForURL(loc);
            assert jar != null : loc;
            b.append(jar);
        }
        return b.toString();
    }
  
    private static class JackpotClassLoader extends ClassLoader {
        private final PermissionCollection permissions = new Permissions();
        ClassLoader altLoader = QueryFactory.class.getClassLoader();

        public JackpotClassLoader(ClassLoader parent) {
            super(parent);
            permissions.add(new AllPermission());
        }

        protected @Override Class<?> findClass(String name) throws ClassNotFoundException {
            return altLoader.loadClass(name);
        }

        protected PermissionCollection getPermissions(CodeSource codesource) {
            return permissions;
        }    
    }
}
