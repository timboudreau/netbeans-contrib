/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://jalopy.sf.net/license-spl.html
 *
 * The Original Code is Marco Hunsicker. The Initial Developer of the Original
 * Code is Marco Hunsicker. All rights reserved.
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
