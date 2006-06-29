/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is Marco Hunsicker. The Initial Developer of the Original
 * Software is Marco Hunsicker. All rights reserved.
 *
 * Copyright (c) 2002 Marco Hunsicker
 */
package de.hunsicker.jalopy.plugin.netbeans;

import de.hunsicker.jalopy.plugin.Editor;
import de.hunsicker.jalopy.plugin.Project;
import de.hunsicker.jalopy.plugin.ProjectFile;

import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

import java.io.File;

import javax.swing.JEditorPane;


/**
 * The NetBeans ProjectFile implementation.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @author Frank-Michael Moser
 */
final class NbProjectFile implements ProjectFile {

	/** The physical file. */
	File file;
	FileObject fileObj;

	/** The node object representing this file. */
	Node node;

	/** The project this file is contained in. */
	Project project;

	/**
	 * Creates a new NbProjectFile object.
	 *
	 * @param project the underlying NetBeans project.
	 * @param node the file node.
	 */
	public NbProjectFile(Project project, Node node) {
		this.project = project;
		this.node = node;

		DataObject obj = (DataObject) this.node.getCookie(DataObject.class);

		this.fileObj = obj.getPrimaryFile();
	}

	/**
	 * {@inheritDoc}
	 */
	public Editor getEditor() {

		EditorCookie cookie =
			(EditorCookie) this.node.getCookie(EditorCookie.class);

		/**
		 * @todo maybe this check is obsolete?
		 */
		if (cookie == null) {
			return null;
		}

		JEditorPane[] panes = cookie.getOpenedPanes();

		if (panes == null) {
			return null;
		}

		return new NbEditor(this, panes[0], cookie.getLineSet());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEncoding() {
		return (String) this.fileObj.getAttribute(
			"Content-Encoding" /* NOI18N */);
	}

	/**
	 * {@inheritDoc}
	 */
	public File getFile() {
		if (this.file == null) {
			this.file = FileUtil.toFile(this.fileObj);
		}

		return this.file;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return getFile().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOpened() {

		EditorCookie cookie =
			(EditorCookie) this.node.getCookie(EditorCookie.class);

		/**
		 * @todo maybe this check is obsolete?
		 */
		if (cookie == null) {
			return false;
		}

		JEditorPane[] panes = cookie.getOpenedPanes();

		return ((panes != null) && (panes.length > 0));
	}

	/**
	 * {@inheritDoc}
	 */
	public Project getProject() {
		return this.project;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReadOnly() {
		return !getFile().canWrite();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param o DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof ProjectFile) {
			return getFile().equals((File) ((ProjectFile) o).getFile());
		}

		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String toString() {
		return getFile().toString();
	}
}
