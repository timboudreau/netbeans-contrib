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
