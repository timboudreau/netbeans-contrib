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

import de.hunsicker.jalopy.plugin.Project;
import de.hunsicker.jalopy.plugin.ProjectFile;

import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


/**
 * The NetBeans Project implementation.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @author Frank-Michael Moser
 */
final class NbProject implements Project {

	/** The currently accessible nodes. */
	Node[] nodes;

	/**
	 * {@inheritDoc}
	 */
	public ProjectFile getActiveFile() {
		if ((this.nodes != null) && (this.nodes.length == 1)) {
			return new NbProjectFile(this, this.nodes[0]);
		}
		else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getAllFiles() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getOpenedFiles() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getSelectedFiles() {

		List files = new ArrayList(nodes.length);

		for (int i = 0; i < this.nodes.length; i++) {

			DataFolder folder =
				(DataFolder) nodes[i].getCookie(DataFolder.class);

			if (folder == null) {
				if (
					NbHelper.isJavaFile(
								(DataObject) nodes[i].getCookie(
									DataObject.class))) {
					files.add(new NbProjectFile(this, nodes[i]));
				}
			}
			else {
				// it's a folder kind of thing, add all children (recursive)
				for (
					Enumeration children = folder.children(true);
							children.hasMoreElements();) {

					DataObject obj = (DataObject) children.nextElement();

					if (NbHelper.isJavaFile(obj)) {

						NbProjectFile file =
							new NbProjectFile(this, obj.getNodeDelegate());

						// we check if the file is already contained in the list
						// because a user may select both a folder and a
						// contained file or a folder and a subfolder
						if (!files.contains(file)) {
							files.add(file);
						}
					}
				}
			}
		}

		return files;
	}
}
