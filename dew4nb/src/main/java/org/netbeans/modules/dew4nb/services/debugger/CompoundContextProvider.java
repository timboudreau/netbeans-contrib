/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 * Contributor(s):
 *
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb.services.debugger;

import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.debugger.jpda.JPDAClassType;
import org.netbeans.spi.debugger.jpda.SourcePathProvider;

class CompoundContextProvider extends SourcePathProvider {

        private SourcePathProvider cp1, cp2;

        CompoundContextProvider (
            SourcePathProvider cp1,
            SourcePathProvider cp2
        ) {
            this.cp1 = cp1;
            this.cp2 = cp2;
        }

        public String getURL (String relativePath, boolean global) {
            String p1 = cp1.getURL (relativePath, global);
            if (p1 != null) {
                try {
                    new java.net.URL(p1);
                    return p1;
                } catch (java.net.MalformedURLException muex) {
                    Logger.getLogger(CompoundContextProvider.class.getName()).log(Level.WARNING,
                            "Malformed URL '"+p1+"' produced by "+cp1, muex);
                }
            }
            p1 = cp2.getURL (relativePath, global);
            if (p1 != null) {
                try {
                    new java.net.URL(p1);
                } catch (java.net.MalformedURLException muex) {
                    Logger.getLogger(CompoundContextProvider.class.getName()).log(Level.WARNING,
                            "Malformed URL '"+p1+"' produced by "+cp2, muex);
                    p1 = null;
                }
            }
            return p1;
        }

        public String getURL(JPDAClassType clazz, String stratum) {
            try {
                java.lang.reflect.Method getURLMethod = cp1.getClass().getMethod("getURL", JPDAClassType.class, String.class); // NOI18N
                String url = (String) getURLMethod.invoke(cp1, clazz, stratum);
                if (url != null) {
                    return url;
                }
            } catch (IllegalAccessException ex) {
            } catch (IllegalArgumentException ex) {
            } catch (NoSuchMethodException ex) {
            } catch (SecurityException ex) {
            } catch (InvocationTargetException ex) {
            }
            try {
                java.lang.reflect.Method getURLMethod = cp2.getClass().getMethod("getURL", JPDAClassType.class, String.class); // NOI18N
                String url = (String) getURLMethod.invoke(cp2, clazz, stratum);
                if (url != null) {
                    return url;
                }
            } catch (IllegalAccessException ex) {
            } catch (IllegalArgumentException ex) {
            } catch (NoSuchMethodException ex) {
            } catch (SecurityException ex) {
            } catch (InvocationTargetException ex) {
            }
            return null;
        }

        public String getRelativePath (
            String url,
            char directorySeparator,
            boolean includeExtension
        ) {
            String p1 = cp1.getRelativePath (
                url,
                directorySeparator,
                includeExtension
            );
            if (p1 != null) return p1;
            return cp2.getRelativePath (
                url,
                directorySeparator,
                includeExtension
            );
        }

        public String[] getSourceRoots () {
            String[] fs1 = cp1.getSourceRoots ();
            String[] fs2 = cp2.getSourceRoots ();
            String[] fs = new String [fs1.length + fs2.length];
            System.arraycopy (fs1, 0, fs, 0, fs1.length);
            System.arraycopy (fs2, 0, fs, fs1.length, fs2.length);
            return fs;
        }

        public String[] getOriginalSourceRoots () {
            String[] fs1 = cp1.getOriginalSourceRoots ();
            String[] fs2 = cp2.getOriginalSourceRoots ();
            String[] fs = new String [fs1.length + fs2.length];
            System.arraycopy (fs1, 0, fs, 0, fs1.length);
            System.arraycopy (fs2, 0, fs, fs1.length, fs2.length);
            return fs;
        }

        public void setSourceRoots (String[] sourceRoots) {
            cp1.setSourceRoots (sourceRoots);
            cp2.setSourceRoots (sourceRoots);
        }

        public String[] getAdditionalSourceRoots() {
            String[] additionalSourceRoots1;
            String[] additionalSourceRoots2;
            //System.err.println("\nCompoundContextProvider["+toString()+"].getadditionalSourceRoots()...\n");
            try {
                java.lang.reflect.Method getAdditionalSourceRootsMethod = cp1.getClass().getMethod("getAdditionalSourceRoots", new Class[] {}); // NOI18N
                additionalSourceRoots1 = (String[]) getAdditionalSourceRootsMethod.invoke(cp1, new Object[] {});
            } catch (Exception ex) {
                additionalSourceRoots1 = new String[0];
            }
            try {
                java.lang.reflect.Method getAdditionalSourceRootsMethod = cp2.getClass().getMethod("getAdditionalSourceRoots", new Class[] {}); // NOI18N
                additionalSourceRoots2 = (String[]) getAdditionalSourceRootsMethod.invoke(cp2, new Object[] {});
            } catch (Exception ex) {
                additionalSourceRoots2 = new String[0];
            }
            if (additionalSourceRoots1.length == 0) {
                //System.err.println("\nCompoundContextProvider.getAdditionalSourceRoots() = "+java.util.Arrays.toString(additionalSourceRoots2)+"\n");
                return additionalSourceRoots2;
            }
            if (additionalSourceRoots2.length == 0) {
                //System.err.println("\nCompoundContextProvider.getAdditionalSourceRoots() = "+java.util.Arrays.toString(additionalSourceRoots1)+"\n");
                return additionalSourceRoots1;
            }
            String[] additionalSourceRoots = new String[additionalSourceRoots1.length + additionalSourceRoots2.length];
            System.arraycopy (additionalSourceRoots1, 0, additionalSourceRoots, 0, additionalSourceRoots1.length);
            System.arraycopy (additionalSourceRoots2, 0, additionalSourceRoots, additionalSourceRoots1.length, additionalSourceRoots2.length);
            //System.err.println("\nCompoundContextProvider.getAdditionalSourceRoots() = "+java.util.Arrays.toString(additionalSourceRoots)+"\n");
            return additionalSourceRoots;
        }

        public void setSourceRoots (String[] sourceRoots, String[] additionalRoots) {
            try {
                java.lang.reflect.Method setSourceRootsMethod = cp1.getClass().getMethod("setSourceRoots", String[].class, String[].class); // NOI18N
                setSourceRootsMethod.invoke(cp1, new Object[] { sourceRoots, additionalRoots });
            } catch (Exception ex) {
                cp1.setSourceRoots(sourceRoots);
            }
            try {
                java.lang.reflect.Method setSourceRootsMethod = cp2.getClass().getMethod("setSourceRoots", String[].class, String[].class); // NOI18N
                setSourceRootsMethod.invoke(cp2, new Object[] { sourceRoots, additionalRoots });
            } catch (Exception ex) {
                cp2.setSourceRoots(sourceRoots);
            }
        }

        public void reorderOriginalSourceRoots(int[] permutation) {
            try {
                java.lang.reflect.Method reorderOriginalSourceRootsMethod = cp1.getClass().getMethod("reorderOriginalSourceRoots", int[].class); // NOI18N
                reorderOriginalSourceRootsMethod.invoke(cp1, new Object[] { permutation });
            } catch (Exception ex) {

            }
            try {
                java.lang.reflect.Method reorderOriginalSourceRootsMethod = cp2.getClass().getMethod("reorderOriginalSourceRoots", int[].class); // NOI18N
                reorderOriginalSourceRootsMethod.invoke(cp2, new Object[] { permutation });
            } catch (Exception ex) {

            }
        }

        public void addPropertyChangeListener (PropertyChangeListener l) {
            cp1.addPropertyChangeListener (l);
            cp2.addPropertyChangeListener (l);
        }

        public void removePropertyChangeListener (PropertyChangeListener l) {
            cp1.removePropertyChangeListener (l);
            cp2.removePropertyChangeListener (l);
        }
}

