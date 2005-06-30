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


/**
 * The action to format nodes or children thereof.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @author Frank-Michael Moser
 */
public final class FormatAction extends CookieAction {

	/** Cookies for which to enable the action. */
	private static final Class[] COOKIE_CLASSES =
		new Class[] {DataFolder.class, DataObject.class, DataNode.class};

	/** The name of the action to display in 'Build' menu. */
	private String _name =
		NbBundle.getMessage(
			FormatAction.class, "LBL_FormatSingleAction" /* NOI18N */);

	/**
	 * Creates a new FormatAction object.
	 */
	public FormatAction() {
	}

	/**
	 * {@inheritDoc}
	 */
	public HelpCtx getHelpCtx() {
		return HelpCtx.DEFAULT_HELP;

		// If you will provide context help then use:
		// return new HelpCtx (FormatAction.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return _name;
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
	protected void performAction(org.openide.nodes.Node[] nodes) {
		NbPlugin.INSTANCE.project.nodes = nodes;

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		try {
			// set the context class loader for the ImportTransformation
			// feature to work
			Thread.currentThread().setContextClassLoader(
				(ClassLoader) Lookup.getDefault().lookup(ClassLoader.class));

			if (nodes.length == 1) {

				DataObject obj =
					(DataObject) nodes[0].getCookie(DataObject.class);

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
	private static boolean isOpened(Node node) {

		EditorCookie cookie = (EditorCookie) node.getCookie(EditorCookie.class);

		// @todo maybe this check is obsolete?
		if (cookie == null) {
			return false;
		}

		JEditorPane[] panes = cookie.getOpenedPanes();

		return ((panes != null) && (panes.length > 0));
	}
}
