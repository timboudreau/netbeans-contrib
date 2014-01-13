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
package org.netbeans.modules.licensechanger.fileHandlers;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import org.junit.Assert;
import org.junit.Test;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.modules.licensechanger.TestUtils;
import static org.netbeans.modules.licensechanger.TestUtils.readFile;
import org.netbeans.modules.licensechanger.wizard.utils.WizardProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Nils Hoffmann
 */
public class FileHandlerTest {

	@Test
	public void testFreemarkerVariableInterpolation() throws Exception {
		String license = TestUtils.getFreemarkerLicense();
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(WizardProperties.KEY_PROJECT, new TestProject());
		for (int i = 1; i <= 12; i++) {
			String filename = "java_" + i + ".txt";
			testOneVersion(license, filename, props);
		}
	}

	private void testOneVersion(String license, String filename, Map<String, Object> properties) throws Exception {
		System.out.println("Test " + filename);
		JavaFileHandler instance = new JavaFileHandler();
		String original = readFile(FileHandlerTest.class, filename);
		Map<String, Object> tmpProperties = new HashMap<String, Object>(properties);
		File f = FileUtil.archiveOrDirForURL(instance.getClass().getResource(filename));
		instance.setFileProperties(FileUtil.toFileObject(f), tmpProperties);
		tmpProperties.put("package", "org.netbeans.modules.licensechanger.fileHandlers");
		String processed = instance.transform(original, license, tmpProperties);
		Assert.assertFalse(processed.contains("${"));
		Assert.assertFalse(processed.contains("Expression "));
		Assert.assertFalse(processed.contains(" undefined "));
	}

	private class TestProject implements Project {

		private final Project project = this;

		@Override
		public FileObject getProjectDirectory() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Lookup getLookup() {
			return Lookups.fixed(new ProjectInformation() {

				@Override
				public String getName() {
					return "testProject";
				}

				@Override
				public String getDisplayName() {
					return "Test Project";
				}

				@Override
				public Icon getIcon() {
					return null;
				}

				@Override
				public Project getProject() {
					return project;
				}

				@Override
				public void addPropertyChangeListener(PropertyChangeListener listener) {

				}

				@Override
				public void removePropertyChangeListener(PropertyChangeListener listener) {

				}
			});
		}

	}
}
