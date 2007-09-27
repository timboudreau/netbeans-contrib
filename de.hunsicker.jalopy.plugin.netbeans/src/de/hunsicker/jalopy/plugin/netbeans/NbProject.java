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
