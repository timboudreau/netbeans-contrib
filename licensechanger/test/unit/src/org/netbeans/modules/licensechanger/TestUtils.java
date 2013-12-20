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
package org.netbeans.modules.licensechanger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import static org.junit.Assert.fail;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Nils Hoffmann
 */
public class TestUtils {

	public static String getLicense() throws Exception {
		return readFile("fake_license.txt");
	}

	public static String getFreemarkerLicense() throws Exception {
		return readFile("license-test.txt");
	}

	public static String readFile(Class<?> clazz, String name) throws Exception {
		InputStream in = clazz.getResourceAsStream(name);
		if (in == null) {
			fail("No input stream for " + name);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			FileUtil.copy(in, out);
		} finally {
			in.close();
			out.close();
		}
		String result = new String(out.toByteArray(), "UTF-8");
		return result.replace("\r\n", "\n");
	}

	public static String readFile(String name) throws Exception {
		return TestUtils.readFile(TestUtils.class, name);
	}

	public static void writeStream(String s, OutputStream os) throws Exception {
		InputStream is = new ByteArrayInputStream(s.getBytes(Charset.forName("UTF-8")));
		try {
			FileUtil.copy(is, os);
		} catch (IOException ioex) {
			throw ioex;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioex) {
					throw ioex;
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException ioex) {
					throw ioex;
				}
			}
		}
	}

	public static void writeFile(String s, File f) throws Exception {
		OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
		writeStream(s, os);
	}
}
