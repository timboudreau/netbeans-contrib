/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.docbook;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 * This loader fixes the problem that an invalid DocBook file will be opened
 * as a normal XML file, with the wrong DataObject, etc.  If a file has
 * ever been opened as a DocBook XML file, this loader will force creation
 * of a DocBookDataObject for it.
 *
 * @author Tim Boudreau
 */
public final class FallbackDataLoader extends DataLoader {
    private final ThreadLocal<Boolean> loading = new ThreadLocal<Boolean>();
    private static final String ATTR_DOCBOOK = "docbk"; //NOI18N

    public FallbackDataLoader() {
        super("org.netbeans.modules.docbook.DocBookDataObject"); //NOI18N
    }

    @Override
    protected DataObject handleFindDataObject(FileObject fo, RecognizedFiles recognized) throws IOException {
        if (isMarked(fo)) {
            if (isLoading()) {
                return null;
            }
            setLoading(true);
            try {
                //This is the black magic:
                Set<FileObject> fos = new HashSet<FileObject>();
                DataObject.Factory factory = DataLoaderPool.factory(
                        DocBookDataObject.class,
                        "Loaders/text/x-docbook+xml", //NOI18N
                        ImageUtilities.loadImage("org/netbeans/modules/docbook/resources/docbook.png")); //NOI18N
                DataObject result = factory.findDataObject(fo, fos);
                for (FileObject f : fos) {
                    recognized.markRecognized(f);
                }
                return result;
            } finally {
                setLoading(false);
            }
        }
        return null;
    }

    void setLoading(boolean val) {
        loading.set(val);
    }

    boolean isLoading() {
        Boolean res = loading.get();
        return res == null ? false : res.booleanValue();
    }

    static boolean isMarked(FileObject fo) {
        return !isSystemFileSystem(fo) && fo.getAttribute(ATTR_DOCBOOK) != null;
    }

    static void mark (FileObject fo) {
            try {
                fo.setAttribute(ATTR_DOCBOOK, fo.getMIMEType());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
    }

    private static boolean isSystemFileSystem(FileObject fo) {
        try {
            return fo.getFileSystem() == FileUtil.getConfigRoot().getFileSystem();
        } catch (FileStateInvalidException ex) {
            Logger.getLogger(FallbackDataLoader.class.getName()).log(Level.INFO, null, ex);
            return false;
        }
    }
}
