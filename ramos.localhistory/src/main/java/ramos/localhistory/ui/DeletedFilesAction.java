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
