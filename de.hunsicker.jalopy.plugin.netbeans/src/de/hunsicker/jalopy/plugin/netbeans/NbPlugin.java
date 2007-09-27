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
