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
package org.netbeans.modules.autosave.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.Timer;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author Michel Graciano
 */
public final class AutoSaveController {
    
    public static final String KEY_ACTIVE = "autoSaveActive";
    public static final String KEY_INTERVAL = "autoSaveInterval";
    public static final String KEY_SAVE_ON_FOCUS_LOST = "autoSaveOnLostFocus";

    public static Preferences prefs() {
        return NbPreferences.forModule(AutoSaveController.class);
    }

   private static AutoSaveController controller;
   private Timer timer;
   private PropertyChangeListener listener;

   private void startTimerSave() {
      int delay = prefs().getInt(KEY_INTERVAL, 10);

      if (delay == 0 && timer != null) {
         timer.stop();
         return;
      }
      delay = delay * 1000 * 60;

      if (timer == null) {
         timer = new Timer(delay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (prefs().getBoolean(KEY_ACTIVE, false)) {
                  AutoSaveCommand.saveAll();
               }
            }
         });
      } else {
         timer.stop();
      }

      timer.setDelay(delay);
      timer.start();
   }

   private void stopTimerSave() {
      if (timer != null) {
         timer.stop();
      }
   }

   private void startFocusSave() {
      if (listener != null) {
         return;
      }
      EditorRegistry.addPropertyChangeListener(listener = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (EditorRegistry.FOCUS_LOST_PROPERTY.equals(name)) {
               Object old = evt.getOldValue();
               if (old instanceof JTextComponent) {
                  Document doc = ((JTextComponent)old).getDocument();
                  DataObject dobj = (DataObject)doc.getProperty(
                        Document.StreamDescriptionProperty);
                  if (dobj != null) {
                     SaveCookie saveCookie = dobj.getCookie(SaveCookie.class);
                     if (saveCookie != null) {
                        try {
                           saveCookie.save();
                        } catch (IOException ex) {
                           Exceptions.printStackTrace(ex);
                        }
                     }
                  }
               }
            }
         }
      });
   }

   private void stopFocusSave() {
      if (listener != null) {
         return;
      }
      EditorRegistry.removePropertyChangeListener(listener);
   }

   public void stop() {
      stopTimerSave();
      stopFocusSave();
   }

   public void synchronize() {
      if (prefs().getBoolean(KEY_ACTIVE, false)) {
         startTimerSave();
         if (prefs().getBoolean(KEY_SAVE_ON_FOCUS_LOST, false)) {
            startFocusSave();
         } else {
            stopFocusSave();
         }
      } else {
         stop();
      }
   }

   public static AutoSaveController getInstance() {
      if (controller == null) {
         controller = new AutoSaveController();
      }
      return controller;
   }
}
