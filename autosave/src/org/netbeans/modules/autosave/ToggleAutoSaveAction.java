/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2009 Michel Graciano. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is Save Automatically Project. The Initial Developer of
 * the Original Software is Michel Graciano. Portions Copyright 2007-2009 Michel
 * Graciano. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.autosave;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;

public final class ToggleAutoSaveAction extends BooleanStateAction {
   public String getName() {
      return NbBundle.getMessage(ToggleAutoSaveAction.class, "CTL_AutoSave");
   }

   protected void initialize() {
      super.initialize();

      Preferences.userNodeForPackage(AutoSavePanel.class).
            addPreferenceChangeListener(new WeakReference<PreferenceChangeListener>(
            new PreferenceChangeListener() {
               public void preferenceChange(PreferenceChangeEvent evt) {
                  setBooleanState(Preferences.userNodeForPackage(
                        AutoSavePanel.class).
                        getBoolean(AutoSaveAdvancedOption.KEY_ACTIVE, false));
               }
            }).get());
      this.addPropertyChangeListener(new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) {
            if (BooleanStateAction.PROP_BOOLEAN_STATE.equals(
                  evt.getPropertyName())) {
               Preferences.userNodeForPackage(AutoSavePanel.class).putBoolean(
                     AutoSaveAdvancedOption.KEY_ACTIVE, getBooleanState());
            }
         }
      });
      this.setBooleanState(Preferences.userNodeForPackage(AutoSavePanel.class).
            getBoolean(AutoSaveAdvancedOption.KEY_ACTIVE, false));
   }

   /**
    * {@inheritDoc}
    */
   protected String iconResource() {
      return "org/netbeans/modules/autosave/auto_save.png";
   }

   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
}
