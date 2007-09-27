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
