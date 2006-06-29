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

import de.hunsicker.jalopy.swing.SettingsDialog;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;

import java.awt.Frame;

import javax.swing.Action;
import javax.swing.SwingUtilities;


/**
 * Action to display the Jalopy settings dialog.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @author Frank-Michael Moser
 */
public final class SettingsAction extends CallableSystemAction {

	/** The name of the action to display. */
	private String name = NbBundle.getMessage(
			SettingsAction.class, "LBL_SettingsAction" /* NOI18N */);

	/**
	 * {@inheritDoc}
	 */
	protected void initialize() {
		super.initialize();
		putProperty(
			Action.SHORT_DESCRIPTION,
			NbBundle.getMessage(
				SettingsAction.class, "HINT_SettingsAction" /* NOI18N */));
	}

	/**
	 * {@inheritDoc}
	 */
	public HelpCtx getHelpCtx() {

		// If you will provide context help then use:
		// return new HelpCtx (Callable_actionAction.class);
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
	public boolean asynchronous() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void performAction() {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					Frame frame = WindowManager.getDefault().getMainWindow();
					SettingsDialog dialog = SettingsDialog.create(frame);

					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
				}
			});
	}
}
