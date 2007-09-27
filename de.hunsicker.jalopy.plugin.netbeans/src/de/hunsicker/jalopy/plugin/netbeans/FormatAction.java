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

import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;


/**
 * The action to format nodes or children thereof.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @author Frank-Michael Moser
 */
public final class FormatAction extends CookieAction {

	/** Cookies for which to enable the action. */
	private static final Class[] COOKIE_CLASSES = new Class[] {
			DataFolder.class, DataObject.class, DataNode.class
		};

	/** The name of the action to display. */
	private String name = NbBundle.getMessage(
			FormatAction.class, "LBL_FormatSingleAction" /* NOI18N */);

	/**
	 * Creates a new FormatAction object.
	 */
	public FormatAction() {
	}

	/**
	 * {@inheritDoc}
	 */
	protected void initialize() {
		super.initialize();
		putProperty(
			Action.SHORT_DESCRIPTION,
			NbBundle.getMessage(
				FormatAction.class, "HINT_FormatAction" /* NOI18N */));
	}

	/**
	 * {@inheritDoc}
	 */
	public HelpCtx getHelpCtx() {

		// If you will provide context help then use:
		// return new HelpCtx (FormatAction.class);
		return HelpCtx.DEFAULT_HELP;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Class[] cookieClasses() {
		return COOKIE_CLASSES;
	}

	/**
	 * {@inheritDoc}
	 */
	protected int mode() {
		return MODE_ANY;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean asynchronous() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void performAction(Node[] nodes) {
		NbPlugin.INSTANCE.project.nodes = nodes;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		try {

			// set the context class loader for the ImportTransformation
			// feature to work
			Thread.currentThread().setContextClassLoader(
				(ClassLoader) Lookup.getDefault().lookup(ClassLoader.class));

			if (nodes.length == 1) {
				DataObject obj = (DataObject) nodes[0].getCookie(
						DataObject.class);

				if (NbHelper.isJavaFile(obj)) {

					// we have to check if the node has an editor opened as
					// Action.FORMAT_ACTIVE relies on an opened view
					if (isOpened(nodes[0])) {
						NbPlugin.INSTANCE.performAction(
							NbPlugin.Action.FORMAT_ACTIVE);
					}
					else {
						NbPlugin.INSTANCE.performAction(
							NbPlugin.Action.FORMAT_SELECTED);
					}
				}
				else if (NbHelper.isFolder(nodes[0])) {
					NbPlugin.INSTANCE.performAction(
						NbPlugin.Action.FORMAT_SELECTED);
				}
			}
			else {
				NbPlugin.INSTANCE.performAction(
					NbPlugin.Action.FORMAT_SELECTED);
			}
		}
		finally {
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	/**
	 * Determines whether the given Java source file node has an opened editor
	 * pane.
	 *
	 * @param node a Java source file node.
	 *
	 * @return <code>true</code> if the node has an opened editor pane.
	 */
	private static boolean isOpened(final Node node) {
            if (!SwingUtilities.isEventDispatchThread()) {
                final boolean[] result = new boolean[1];
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            result[0] = isOpened(node);
                        }
                    });
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
                return result[0];
            }
		boolean open = false;
		EditorCookie cookie = (EditorCookie) node.getCookie(EditorCookie.class);

		if (cookie != null) {
			JEditorPane[] panes = cookie.getOpenedPanes();

			open = (panes != null) && (panes.length > 0);
		}

		return open;
	}
}
