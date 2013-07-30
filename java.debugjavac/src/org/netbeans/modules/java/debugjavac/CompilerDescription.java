/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.debugjavac;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.openide.filesystems.FileObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 *
 * @author lahvac
 */
public class CompilerDescription {
    public final String displayName;
    public final URL[] jars;

    private CompilerDescription(String displayName, URL[] jars) {
        this.displayName = displayName;
        this.jars = jars;
    }

    public static Collection<? extends CompilerDescription> descriptions() {
        List<CompilerDescription> result = new ArrayList<>();
        
        try {
            result.add(new CompilerDescription("nb-javac", new URL[] {
                InstalledFileLocator.getDefault().locate("modules/ext/nb-javac-api.jar", null, false).toURI().toURL(),
                InstalledFileLocator.getDefault().locate("modules/ext/nb-javac-impl.jar", null, false).toURI().toURL()
            }));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        for (JavaPlatform platform : JavaPlatformManager.getDefault().getInstalledPlatforms()) {
            if (!"j2se".equals(platform.getSpecification().getName())) continue;
            
            for (FileObject installDir : platform.getInstallFolders()) {
                FileObject toolsJar = installDir.getFileObject("lib/tools.jar");
                FileObject rtJar = installDir.getFileObject("jre/lib/rt.jar");
                
                if (toolsJar != null && rtJar != null) {
                    result.add(new CompilerDescription(platform.getDisplayName(), new URL[] {toolsJar.toURL(), rtJar.toURL()}));
                }
            }
        }
        
        return result;
    }
    
    private final AtomicReference<Boolean> valid = new AtomicReference<>();
    
    public boolean isValid() {
        Boolean val = valid.get();
        
        if (val == null) {
            valid.set(val = DecompiledTab.isValid(this));
        }
        
        return val;
    }
}
