/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.api.javafx.source;

import java.net.URL;
import javax.swing.event.ChangeListener;
import javax.tools.JavaFileManager;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.javafx.platform.JavaFXPlatform;
import org.netbeans.modules.javafx.source.classpath.CachingFileManager;
import org.netbeans.modules.javafx.source.classpath.ProxyFileManager;
import org.netbeans.modules.javafx.source.classpath.SourceFileManager;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;

/**
 *
 * @author nenik
 */
public class ClasspathInfo {
    private static final ClassPath EMPTY_PATH = ClassPathSupport.createClassPath(new URL[0]);
    private ClassPath bootPath;
    private ClassPath compilePath;
    private ClassPath srcPath;
    private JavaFileManager fileManager;
    
    static ClasspathInfo create(FileObject fo) {
        ClassPath bootPath = ClassPath.getClassPath(fo, ClassPath.BOOT);
        if (bootPath == null) {
            //javac requires at least java.lang
            bootPath = JavaFXPlatform.getDefaultFXPlatform().getBootstrapLibraries();
        }
        ClassPath compilePath = ClassPath.getClassPath(fo, ClassPath.COMPILE);
        if (compilePath == null) {
            compilePath = EMPTY_PATH;
        }
        ClassPath srcPath = ClassPath.getClassPath(fo, ClassPath.SOURCE);
        if (srcPath == null) {
            srcPath = EMPTY_PATH;
        }
        return create (bootPath, compilePath, srcPath);
    }

    private static ClasspathInfo create(ClassPath bootPath, ClassPath compilePath, ClassPath srcPath) {
        return new ClasspathInfo(bootPath, compilePath, srcPath);
    }

    public ClasspathInfo(ClassPath bootPath, ClassPath compilePath, ClassPath srcPath) {
        this.bootPath = bootPath;
        this.compilePath = compilePath;
        this.srcPath = srcPath;
    }

    public ClassPath getClassPath (PathKind pathKind) {
	switch( pathKind ) {
	    case BOOT:
		return bootPath;
	    case COMPILE:
		return compilePath;
	    case SOURCE:
		return srcPath;
	    default:
		assert false : "Unknown path type";     //NOI18N
		return null;
	}
    }

    public static enum PathKind {	
	BOOT,	
	COMPILE,	
	SOURCE,	
//	OUTPUT,
    }

    
    synchronized JavaFileManager getFileManager() {
        if (fileManager == null) {
            fileManager = new ProxyFileManager (
                    new CachingFileManager(bootPath), // cacheFile, ignoreExcludes
                    new CachingFileManager(compilePath), // ignoreExcludes
                    new SourceFileManager(srcPath)
            );
        }
        return this.fileManager;
    }

    // XXX: Temporal, until there is a file manager implementation
    String getBootPath() {
        return bootPath.toString(ClassPath.PathConversionMode.WARN);
    }

    String getCompilePath() {
        return compilePath.toString(ClassPath.PathConversionMode.WARN);
    }

    String getSrcPath() {
        return srcPath.toString(ClassPath.PathConversionMode.WARN);
    }
        
    void addChangeListener(ChangeListener change) {
        // TODO
    }

}
