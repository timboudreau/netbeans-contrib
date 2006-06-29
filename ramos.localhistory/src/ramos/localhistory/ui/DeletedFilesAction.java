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
 * The Original Software is Ramon Ramos. The Initial Developer of the Original
 * Software is Ramon Ramos. All rights reserved.
 *
 * Copyright (c) 2006 Ramon Ramos
 */
package ramos.localhistory.ui;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import org.openide.windows.TopComponent;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;


/**
 * Action which shows DeletedFiles component.
 */
public class DeletedFilesAction extends AbstractAction {
   /**
    * Creates a new DeletedFilesAction object.
    */
   public DeletedFilesAction() {
      super(NbBundle.getMessage(DeletedFilesAction.class,
            "CTL_DeletedFilesAction"));

      //        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(DeletedFilesTopComponent.ICON_PATH, true)));
   }

   /**
    * DOCUMENT ME!
    *
    * @param evt DOCUMENT ME!
    */
   public void actionPerformed(final ActionEvent evt) {
      TopComponent win = DeletedFilesTopComponent.findInstance();
      win.open();
      win.requestActive();
   }
}
