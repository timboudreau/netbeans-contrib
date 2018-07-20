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

package org.netbeans.api.mdr;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/** Class used by JMI mapper to obtain output streams for generating JMI interfaces.
 * Subclasses should implement the <code>{@link #createStream(List, String, String)}
 * method to create/open an output stream based on a provided package name, class
 * name and file extension.
 *
 */
public abstract class JMIStreamFactory {

    /** Extension for Java source files  */
    public static final String EXT_JAVA = "java"; //NOI18N

    /** Extension for Java bytecode files  */
    public static final String EXT_CLASS = "class"; //NOI18N

    /** Creates a new output stream based on a provided package name and class name.
     * Assumes that the data generated will be Java source code (e.g. creates FileOutputStream
     * for a file with "java" extension). Calling this method has the same effect
     * as calling {@link #createStream(List, String, String)} with the last parameter
     * set to <code>EXT_JAVA</code>.
     * @param pkg Parsed package name.
     * @param className Class name.
     * @throws IOException I/O error during stream creation.
     * @return Created stream, or <code>null</code> if nothing needs to be written
     *         for the class or interface.
     *
     */
    public OutputStream createStream(List pkg, String className) throws IOException {
        return createStream(pkg, className, EXT_JAVA);
    }

    /** Creates a new output stream based on a provided package name, class name and
     * extension for the returned stream should correspond to  (e.g.
     * {@link #EXT_CLASS} to generate byte code or {@link #EXT_JAVA} to generate
     * source code). The stream factory can return <code>null</code> to indicate
     * that nothing needs to be written for the given class or interface. For
     * example, if the stream factory is able to determine that the destination
     * file already exists and is up to date, then <code>null</code> could be
     * returned so that the file is not needlessly rewritten.
     * @param pkg Parsed package name.
     * @param className Class name.
     * @param extension The type of file that should be generated.
     * @throws IOException I/O error during stream creation.
     * @return Created stream, or <code>null</code> if nothing needs to be written
     *         for the class or interface.
     *
     */
    public abstract OutputStream createStream(List pkg, String className, String extension) throws IOException;
}

