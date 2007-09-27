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
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;


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
            if (!SwingUtilities.isEventDispatchThread()) {
                final Editor[] result = new Editor[1];
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            result[0] = getEditor();
                        }
                    });
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
                return result[0];
            }

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
            if (!SwingUtilities.isEventDispatchThread()) {
                final boolean[] result = new boolean[1];
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            result[0] = isOpened();
                        }
                    });
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
                return result[0];
            }

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
