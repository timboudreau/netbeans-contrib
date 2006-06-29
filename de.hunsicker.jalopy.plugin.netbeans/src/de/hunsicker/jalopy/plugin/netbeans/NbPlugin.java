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

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;
import de.hunsicker.jalopy.plugin.AbstractPlugin;
import de.hunsicker.jalopy.plugin.Project;
import de.hunsicker.jalopy.plugin.ProjectFile;
import de.hunsicker.jalopy.plugin.StatusBar;

import org.openide.windows.WindowManager;

import java.awt.Frame;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;


/**
 * The Jalopy NetBeans Plug-in.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @author Frank-Michael Moser
 */
final class NbPlugin extends AbstractPlugin {

	/** The sole instance of the appender . */
	static final NbAppender APPENDER = new NbAppender();

	/** The sole instance of this class. */
	static final NbPlugin INSTANCE = new NbPlugin();

	/** The current project. */
	NbProject project;

	/**
	 * Creates a new NbPlugin object.
	 */
	private NbPlugin() {
		super(APPENDER);
		this.project = new NbProject();
	}

	/**
	 * {@inheritDoc}
	 */
	public Project getActiveProject() {
		return this.project;
	}

	/**
	 * {@inheritDoc}
	 */
	public FileFormat getFileFormat() {
		// @todo implement
		return FileFormat.AUTO;
	}

	private Frame mainWindow;

	/**
	 * {@inheritDoc}
	 */
	public Frame getMainWindow() {
		if (mainWindow == null) {
			if (SwingUtilities.isEventDispatchThread()) {
				mainWindow = WindowManager.getDefault().getMainWindow();
			}
			else {
				try {
					SwingUtilities.invokeAndWait(
						new Runnable() {
							public void run() {
								mainWindow =
									WindowManager.getDefault().getMainWindow();
							}
						});
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		return mainWindow;
	}

	/**
	 * {@inheritDoc}
	 */
	public StatusBar getStatusBar() {
		return NbStatusBar.INSTANCE;
	}

	/**
	 * MISSING_JAVADOC
	 */
	public void afterEnd() {
		if (jalopy.getState() != Jalopy.State.ERROR) {
			APPENDER.getOut().println("Done.");
		}
		else {
			APPENDER.getOut().println("Failed.");
		}
	}

	/**
	 * MISSING_JAVADOC
	 */
	public void beforeStart() {

		ProjectFile file = project.getActiveFile();
		String fileName = "";

		if (file != null) {
			fileName = file.getName();
		}
		else {
			fileName = "selected files";
		}

		APPENDER.getOut().println("Formatting " + fileName + "...");
	}
}
