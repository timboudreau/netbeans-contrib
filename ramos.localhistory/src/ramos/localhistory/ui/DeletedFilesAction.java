/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License.  A copy of the License is available at
 * http://www.sun.com
 *
 * The Original Code is Ramon Ramos. The Initial Developer of the Original
 * Code is Ramon Ramos. All rights reserved.
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
