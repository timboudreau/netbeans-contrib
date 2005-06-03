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
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.util.actions.SystemAction;

import java.util.Enumeration;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;


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
		// we have to register popups here, because otherwise users would have
		// to open the "Build" menu first in order to see the popup menus
		//		registerPopups();
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
	 * Indicates whether the action should be enabled based on the currently
	 * activated nodes. The action will only be enabled for Java data objects
	 * (representing Java source files) and data folders.
	 *
	 * @param nodes the set of activated nodes.
	 *
	 * @return <code>true</code> if the action should be enabled.
	 */
	protected boolean enableOld(Node[] nodes) {
		if (!super.enable(nodes)) {
			return false;
		}

		boolean enabled = false;
		boolean single = true;

		if (nodes.length == 1) {

			DataFolder dataFolder =
				(DataFolder) nodes[0].getCookie(DataFolder.class);
			DataObject dataObject =
				(DataObject) nodes[0].getCookie(DataObject.class);

			single = dataFolder == null;
			enabled =
				!single ||
				(dataObject != null && NbHelper.isJavaFile(dataObject));
		}
		else if (nodes.length > 1) {

			int javaDataObjectCount = 0;

			for (int i = 0; i < nodes.length; i++) {

				DataFolder dataFolder =
					(DataFolder) nodes[i].getCookie(DataFolder.class);
				DataObject dataObject =
					(DataObject) nodes[i].getCookie(DataObject.class);

				if (dataFolder != null) {
					single = false;
					enabled = true;
				}

				if (dataObject != null && NbHelper.isJavaFile(dataObject)) {
					enabled = true;
					javaDataObjectCount++;
				}

				if (javaDataObjectCount > 1) {
					single = false;
				}

				if (!single && enabled) {
					break;
				}
			}
		}

		if (single) {
			_name =
				NbBundle.getMessage(
					FormatAction.class, "LBL_FormatSingleAction" /* NOI18N */);
		}
		else {
			_name =
				NbBundle.getMessage(
					FormatAction.class, "LBL_FormatAllAction" /* NOI18N */);
		}

		System.out.println("name " + _name);
		System.out.println("enabled " + enabled);
		return enabled;
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

	private void registerPopups() {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					registerPopups0();
				}
			});
	}

	private void registerPopups0() {

		/*
		 * @todo this code is ugly and subject to change but until the Looks
		 *          API is in place, I really like this better than the
		 *          SystemAction way to integrate items into the Tools submenu
		 */
		DataLoaderPool pool =
			(DataLoaderPool) Lookup.getDefault().lookup(DataLoaderPool.class);
		DataLoader loader = pool.firstProducerOf(DataFolder.class);
		SystemAction[] actions = loader.getActions();



// add our action to the popup menu of folder nodes
SEARCH_FOLDER: 
		for (int i = 0; i < actions.length; i++) {
			if (actions[i] != null) // null means separator
			 {

				String actionName = actions[i].getClass().getName();

				// formerly org.openide.actions.BuildAction
				if (actionName.equals(NbHelper.REFACTORING_ACTION_CLASS_NAME)) {

					SystemAction[] result =
						new SystemAction[actions.length + 2];

					System.arraycopy(actions, 0, result, 0, i + 1);
					result[i + 2] = this; // add the action
					result[i + 3] = null; // and a separator
					System.arraycopy(
						actions, i + 2, result, i + 4, actions.length - i - 2);
					loader.setActions(result);

					break SEARCH_FOLDER;
				}
			}
		}

		// add our action to the popup menu of Java source file nodes
		for (Enumeration loaders = pool.allLoaders();
					loaders.hasMoreElements();) {
			loader = (DataLoader) loaders.nextElement();

			String name = loader.getClass().getName();

			/**
			 * @todo it would be cool to be able to format FormDataNodes
			 * 		 (org.netbeans.modules.form.FormDataLoader) as well, but I
			 * 		 don't how to bypass the guarded sections
			 */
			if (
				name.equals(NbHelper.JAVA_DATA_LOADER_CLASS_NAME) ||
						name.equals(NbHelper.SERVLET_DATA_LOADER_CLASS_NAME)) {
				actions = loader.getActions();
SEARCH_FILE: 
				for (int i = 0; i < actions.length; i++) {
					if (actions[i] != null) // null means separator
					 {

						String actionName = actions[i].getClass().getName();

						// formerly org.openide.actions.BuildAction
						if (
							actionName.equals(
										NbHelper.REFACTORING_ACTION_CLASS_NAME)) {

							SystemAction[] result =
								new SystemAction[actions.length + 2];

							System.arraycopy(actions, 0, result, 0, i + 1);
							result[i + 2] = this; // add the action
							result[i + 3] = null; // and a separator
							System.arraycopy(
								actions, i + 2, result, i + 4,
								actions.length - i - 2);
							loader.setActions(result);

							break SEARCH_FILE;
						}
					}
				}
			}
		}
	}
}
