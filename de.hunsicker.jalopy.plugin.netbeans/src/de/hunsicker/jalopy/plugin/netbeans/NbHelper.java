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
 * The Original Software is Marco Hunsicker. The Initial Developer of the Original
 * Software is Marco Hunsicker. All rights reserved.
 *
 * Copyright (c) 2002 Marco Hunsicker
 */
package de.hunsicker.jalopy.plugin.netbeans;

import org.openide.loaders.DataObject;
import org.openide.nodes.Node;


/**
 * Some helper stuff.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @author Frank-Michael Moser
 */
final class NbHelper {

	static final String REFACTORING_ACTION_CLASS_NAME =
		"org.netbeans.modules.refactoring.ui.RSMJavaDOAction"; /* NOI18N */
	static final String JAVA_DATA_LOADER_CLASS_NAME =
		"org.netbeans.modules.java.JavaDataLoader"; /* NOI18N */
	static final String SERVLET_DATA_LOADER_CLASS_NAME =
		"org.netbeans.modules.web.core.jsploader.ServletDataLoader"; /* NOI18N */
	static final String JAVA_NODE_CLASS_NAME =
		"org.netbeans.modules.java.JavaNode"; /* NOI18N */
	static final String WEB_LOOK_NODE_CLASS_NAME =
		"org.netbeans.modules.web.core.jsploader.WebLookNode"; /* NOI18N */

	/** The .java file extension. */
	static final String EXTENSION_JAVA = "java";

	/**
	 * Determines whether the given node represents a folder.
	 *
	 * @param node a node.
	 *
	 * @return <code>true</code> if the node represents a folder.
	 *
	 * @since 1.0b8
	 */
	static boolean isFolder(Node node) {
		return (node.getCookie(org.openide.loaders.DataFolder.class) != null);
	}

	/**
	 * Indicates whether the given data object represents a Java source file.
	 *
	 * @param obj a data object.
	 *
	 * @return <code>true</code> if the given object represents a Java source
	 * 		   file.
	 */
	static boolean isJavaFile(DataObject obj) {

		String name = obj.getNodeDelegate().getClass().getName();

		/**
		 * @todo this is ugly, but we can't format
		 * 		 org.netbeans.modules.form.FormDataNode nodes because of their
		 * 		 guarded sections
		 */
		if (
			JAVA_NODE_CLASS_NAME.equals(name) ||
					WEB_LOOK_NODE_CLASS_NAME.equals(name)) {
			if (EXTENSION_JAVA.equals(obj.getPrimaryFile().getExt())) {
				return true;
			}
		}

		return false;
	}
}
