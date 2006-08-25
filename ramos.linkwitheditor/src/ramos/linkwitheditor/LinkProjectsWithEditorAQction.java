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
package ramos.linkwitheditor;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.openide.util.actions.CallableSystemAction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public final class LinkProjectsWithEditorAQction
  extends BooleanStateAction
  implements PropertyChangeListener {
  LinkProjectsWithEditorAQction() {
    addPropertyChangeListener(this);
  }

  public String getName() {
    return NbBundle.getMessage(LinkProjectsWithEditorAQction.class,
      "CTL_LinkProjectsWithEditorAQction");
  }

  protected String iconResource() {
    return "ramos/linkwitheditor/ResourceLink.gif";
  }

  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  protected boolean asynchronous() {
    return false;
  }

  public void propertyChange(final PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals(PROP_BOOLEAN_STATE)) {
      link(getBooleanState());
    }
  }

  private void link(final boolean mark) {
    if (mark) {
      LinkWithEditorListener.getInstance().attach();
    } else {
      LinkWithEditorListener.getInstance().detach();
    }
  }

  protected void initialize() {
    super.initialize();
    setBooleanState(false);
  }
}
