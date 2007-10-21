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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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

package org.netbeans.modules.moduleresolver.ui;

import org.netbeans.modules.moduleresolver.*;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.autoupdate.UpdateElement;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class InstallStep implements WizardDescriptor.FinishablePanel<WizardDescriptor> {
    private InstallPanel component;
    private Collection<UpdateElement> missingModules = null;
    private Collection<UpdateElement> brokenModules = null;
    private final List<ChangeListener> listeners = new ArrayList<ChangeListener> ();
    private boolean isStored = false;

    public Component getComponent () {
        if (component == null) {
            component = new InstallPanel (
                    NbBundle.getMessage (InstallStep.class, "InstallPanel_Name"));
        }
        return component;
    }

    public HelpCtx getHelp () {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean isValid () {
        return missingModules != null && ! missingModules.isEmpty ();
    }

    public synchronized void addChangeListener (ChangeListener l) {
        listeners.add(l);
    }

    public synchronized void removeChangeListener (ChangeListener l) {
        listeners.remove(l);
    }

    private void fireChange () {
        ChangeEvent e = new ChangeEvent (this);
        List<ChangeListener> templist;
        synchronized (this) {
            templist = new ArrayList<ChangeListener> (listeners);
        }
        for (ChangeListener l : templist) {
            l.stateChanged (e);
        }
    }

    @SuppressWarnings ("unchecked")
    public void readSettings (WizardDescriptor settings) {
        Object o = settings.getProperty (InstallMissingModulesIterator.APPROVED_ELEMENTS);
        assert o == null || o instanceof Collection :
            o + " is instanceof Collection<UpdateElement> or null.";
        missingModules = ((Collection<UpdateElement>) o);
        o = settings.getProperty (InstallMissingModulesIterator.CHOSEN_ELEMENTS);
        assert o == null || o instanceof Collection :
            o + " is instanceof Collection<UpdateElement> or null.";
        brokenModules = ((Collection<UpdateElement>) o);
        if (missingModules != null) {
            presentElementsForInstall (missingModules);
        }
    }

    public void storeSettings (WizardDescriptor settings) {
        if (isStored) {
            return ;
        }
        isStored = true;
        if (WizardDescriptor.FINISH_OPTION.equals (settings.getValue ())) {
            new MissingModulesInstaller (brokenModules, missingModules).run ();
        }
    }

    public boolean isFinishPanel () {
        return true;
    }
    
    private void presentElementsForInstall (Collection<UpdateElement> elements) {
        LinkedList<JComponent> components = new LinkedList<JComponent> ();
        if (components.size () == 1) {
            components.add (new JLabel (getBundle ("InstallStep_InstallDescription_Plugin",
                    elements.size ())));
        } else {
            components.add (new JLabel (getBundle ("InstallStep_InstallDescription_Plugins",
                    elements.size ())));
        }
        components.add (new JLabel (getBundle ("InstallStep_DownloadSize",
                getDownloadSizeAsString (getDownloadSize (elements)))));
        components.add (new JLabel (" "));
        components.add (new JLabel (getBundle ("InstallStep_Note",
                getDownloadSizeAsString (getDownloadSize (elements)))));
        components.add (new JLabel (" "));
        for (UpdateElement el : new LinkedList<UpdateElement> (elements)) {
            components.add (new JLabel (
                    getBundle ("InstallStep_PluginDisplay",
                        el.getDisplayName (),
                        el.getCodeName (),
                        el.getSpecificationVersion ())));
        }
        component.replaceComponents (components.toArray (new JComponent [0]));
        fireChange ();
    }
    
    private static String getBundle (String key, Object... params) {
        return NbBundle.getMessage (InstallStep.class, key, params);
    }
    
    private int getDownloadSize (Collection<UpdateElement> elements) {
        int res = 0;
        for (UpdateElement el : elements) {
            res += el.getDownloadSize ();
        }
        return res;
    }

    public static String getDownloadSizeAsString (int size) {
        int gbSize = size / (1024 * 1024 * 1024);
        if (gbSize > 0) {
            return gbSize + getBundle ("InstallStep_DownloadSize_GB");
        }
        int mbSize = size / (1024 * 1024);
        if (mbSize > 0) {
            return mbSize + getBundle ("InstallStep_DownloadSize_MB");
        }
        int kbSize = size / 1024;
        if (kbSize > 0) {
            return kbSize + getBundle ("InstallStep_DownloadSize_kB");
        }
        return size + getBundle ("InstallStep_DownloadSize_B");
    }
    
}

