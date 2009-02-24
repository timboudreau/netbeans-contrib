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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erlang.platform;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.erlang.platform.api.RubyPlatform;
import org.netbeans.modules.erlang.platform.api.RubyPlatformManager;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;

/**
 *
 * @author dcaoyuan
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.spi.java.classpath.ClassPathProvider.class)
public final class ErlangPlatformClassPathProvider implements ClassPathProvider {

    // @Todo, use ErlangLaugnage.BOOT_CP ErlangTokenId.MIME_TYPE
    public static final String BOOT_CP = "ErlangOtpLibBootClassPath"; //NOI18N
    public static final String MIME_TYPE = "text/x-erlang"; // NOI18N
    private static FileObject otpLibsFO;
    private static ClassPath bootClassPath;

    public ErlangPlatformClassPathProvider() {
    }

    public ClassPath findClassPath(FileObject file, String type) {
        if (type.equals(BOOT_CP) && file.getMIMEType().equals(MIME_TYPE)) {
            return getBootClassPath();
        } else {
            return null;
        }
    }

    public static synchronized ClassPath getBootClassPath() {
        if (bootClassPath == null) {
            FileObject otpLibs = getOtpLibs();
            if (otpLibs != null) {
                bootClassPath = ClassPathSupport.createClassPath(getOtpLibs());
            }
        }
        return bootClassPath;
    }

    // TODO - add classpath recognizer for these ? No, don't need go to declaration inside these files...
    private static FileObject getOtpLibs() {
        if (otpLibsFO == null) {
            RubyPlatform platform = RubyPlatformManager.getDefaultPlatform();
            if (platform != null) {
                otpLibsFO = platform.getLibFO();
            }
        }
        return otpLibsFO;
    }
}

