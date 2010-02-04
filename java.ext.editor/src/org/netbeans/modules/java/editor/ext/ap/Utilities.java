/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2009 Sun
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
package org.netbeans.modules.java.editor.ext.ap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.Processor;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.ClassPath.Entry;
import org.netbeans.api.java.source.ClasspathInfo.PathKind;
import org.netbeans.api.java.source.CompilationInfo;
import org.openide.util.Exceptions;

/**
 *
 * @author lahvac
 */
public class Utilities {

    private static final Logger LOG = Logger.getLogger(Utilities.class.getName());
    
    private Utilities() {
    }

    public static Collection<? extends Processor> resolveProcessors(CompilationInfo info, boolean includeAuxiliaryProcessors) {
        ClassPath cp = lookupProcessorPath(info);

        List<URL> urls = new LinkedList<URL>();

        for (Entry e : cp.entries()) {
            urls.add(e.getURL());
        }

        ClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]), new NotDelegatingClassLoader(Processor.class.getClassLoader()));

        Collection<Processor> result = lookupProcessors(cl);

        if (includeAuxiliaryProcessors) {
            result.add(new SuppressWarningsCompletion());
            result.add(new SupportedAnnotationTypesCompletion());
        }

        return result;
    }

    public static Collection<? extends Processor> matching(CompilationInfo info, Collection<? extends Processor> processors, String... annotations) {
        List<Processor> result = new LinkedList<Processor>();

        OUTER: for (Processor p : processors) {
            for (String supp : p.getSupportedAnnotationTypes()) {
                if ("*".equals(supp)) {
                    result.add(p);
                    continue OUTER;
                }

                if (supp.endsWith("*")) {
                    supp = supp.substring(0, supp.length() - 1);

                    for (String a : annotations) {
                        if (a.startsWith(supp)) {
                            result.add(p);
                            continue OUTER;
                        }
                    }
                } else {
                    for (String a : annotations) {
                        if (a.equals(supp)) {//TODO: performance
                            result.add(p);
                            continue OUTER;
                        }
                    }
                }
            }
        }

        return result;
    }

    private static Collection<Processor> lookupProcessors(ClassLoader cl) {
        List<Processor> result = new LinkedList<Processor>();
        Enumeration<URL> resources;
        
        try {
            resources = cl.getResources("META-INF/services/" + Processor.class.getName());
        } catch (IOException ex) {
            LOG.log(Level.FINE, null, ex);
            return result;
        }

        while (resources.hasMoreElements()) {
            try {
                URL url = resources.nextElement();
                for (String line : content(url)) {
                    int hash = line.indexOf('#');
                    line = hash != (-1) ? line.substring(0, hash) : line;
                    line = line.trim();
                    if (line.length() == 0)
                        continue;
                    
                    try {
                        Class<?> clazz = Class.forName(line, true, cl);
                        Object instance = clazz.newInstance();

                        if (instance instanceof Processor) {
                            result.add((Processor) instance);
                        }
                    } catch (ThreadDeath td) {
                        throw td;
                    } catch (Throwable t) {
                        LOG.log(Level.FINE, null, t);
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return result;
    }

    private static Iterable<String> content(URL url) throws IOException {
        Collection<String> result = new LinkedList<String>();
        BufferedReader ins =  new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

        try {
            String line;
            
            while ((line = ins.readLine()) != null) {
                result.add(line);
            }

            return result;
        } finally {
            ins.close();
        }
    }

    private static final ClassPath lookupProcessorPath(CompilationInfo info) {
        ClassPath cp = ClassPath.getClassPath(info.getFileObject(), "classpath/processor");

        if (cp != null) {
            return cp;
        }

        return info.getClasspathInfo().getClassPath(PathKind.COMPILE);
    }
    
    private static final class NotDelegatingClassLoader extends ClassLoader {

        public NotDelegatingClassLoader(ClassLoader delegate) {
            super(delegate);
        }

        @Override
        public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("org.openide.") || name.startsWith("org.netbeans.")) {
                throw new ClassNotFoundException();
            }

            return super.loadClass(name, resolve);
        }

    }

}
