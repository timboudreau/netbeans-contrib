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

package org.netbeans.modules.moduleresolver;

import org.netbeans.modules.moduleresolver.ui.InstallMissingDisplayer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import org.netbeans.api.autoupdate.OperationContainer;
import org.netbeans.api.autoupdate.OperationSupport;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jirka Rechtacek
 */
public final class FindBrokenModules {
    
    
    public static final String DO_CHECK = "do-check";
    
    private static final String ENABLE_LATER = "enable-later";
    private static boolean fromGui = false;
    private static int WAIT_FOR_CHECK = 25000;
    private static int WAIT_FOR_ENABLE = 5000;
    private static Map<UpdateElement, Collection<UpdateElement>> candidates = null;
    private static RequestProcessor.Task brokenModulesFindingTask = null;
    private static RequestProcessor.Task enableLaterTask = null;

    static void scheduleCheckIt () {
        enableLaterTask = RequestProcessor.getDefault ().post (doEnable, WAIT_FOR_ENABLE);
        if (shouldDoCheck ()) {
            brokenModulesFindingTask = RequestProcessor.getDefault ().post (doCheck, WAIT_FOR_CHECK);
            fromGui = false;
        }
    }
    
    public static RequestProcessor.Task getFindingTask () {
        fromGui = true;
        return brokenModulesFindingTask;
    }
    
    public static RequestProcessor.Task createFindingTask () {
        assert brokenModulesFindingTask == null || brokenModulesFindingTask.isFinished () : "The Finding Task cannot be started nor scheduled.";
        brokenModulesFindingTask = RequestProcessor.getDefault ().create (doCheck);
        fromGui = true;
        return brokenModulesFindingTask;
    }
    
    public static Collection<UpdateElement> getModulesForRepair () {
        return candidates == null ? null : candidates.keySet ();
    }
    
    public static Collection<UpdateElement> getMissingModules (UpdateElement module) {
        assert candidates != null : "candidates cannot be null if getMissingModules (" + module + ") is called.";
        assert candidates.get (module) != null : module + " must be found in " + candidates;
        return candidates == null ? null : candidates.get (module);
    }
    
    public static void clearModulesForRepair () {
        candidates = null;
        brokenModulesFindingTask = null;
        enableLaterTask = null;
    }
    
    public static void writeEnableLater (Collection<UpdateElement> modules) {
        Preferences pref = FindBrokenModules.getPreferences ();
        if (modules == null) {
            pref.remove (ENABLE_LATER);
            return ;
        }
        String value = "";
        for (UpdateElement m : modules) {
            value += value.length () == 0 ? m.getCodeName () : ", " + m.getCodeName (); // NOI18N
        }
        if (value.trim ().length () == 0) {
            pref.remove (ENABLE_LATER);
        } else {
            pref.put (ENABLE_LATER, value);
        }
    }
    
    private static boolean shouldDoCheck () {
        String shouldCheck = System.getProperty ("module.resolver.check");
        return getPreferences ().getBoolean (DO_CHECK, shouldCheck == null ? true : Boolean.valueOf (shouldCheck));
    }
    
    private static Collection<UpdateElement> readEnableLater () {
        Set<UpdateElement> res = new HashSet<UpdateElement> ();
        Preferences pref = FindBrokenModules.getPreferences ();
        String value = pref.get (ENABLE_LATER, null);
        if (value != null && value.trim ().length () > 0) {
            Enumeration en = new StringTokenizer (value, ","); // NOI18N
            while (en.hasMoreElements ()) {
                String codeName = ((String) en.nextElement ()).trim ();
                UpdateElement el = findUpdateElement (codeName, true);
                if (el != null) {
                    res.add (el);
                }
            }
        }
        return res;
    }
    
    public static Preferences getPreferences () {
        return NbPreferences.forModule (FindBrokenModules.class);
    }

    private static Runnable doEnable = new Runnable () {
        public void run() {
            if (SwingUtilities.isEventDispatchThread ()) {
                RequestProcessor.getDefault ().post (doEnable);
                return ;
            }
            Collection<UpdateElement> enableLater = readEnableLater ();
            writeEnableLater (null);
            if (enableLater != null && ! enableLater.isEmpty ()) {
                try {
                    MissingModulesInstaller.enableModules (enableLater);
                } catch (Exception ex) {
                    Logger.getLogger (FindBrokenModules.class.getName ()).
                            log (Level.INFO, ex.getLocalizedMessage (), ex);
                }
            }
        }
    };

    private static Runnable doCheck = new Runnable () {
        public void run() {
            if (SwingUtilities.isEventDispatchThread ()) {
                RequestProcessor.getDefault ().post (doCheck);
                return ;
            }
            checkBrokenDependencies ();
        }

    };

    private static void checkBrokenDependencies () {
        Collection<UpdateUnit> units = UpdateManager.getDefault ().getUpdateUnits (UpdateManager.TYPE.MODULE);
        Collection<UpdateElement> disabledElems = getInstalledAndDisabledElements (units);
        Map<UpdateElement, Collection<String>> elements2BrokenElements = getElements2BrokenDependencies (disabledElems);
        candidates = getCandidatesForFixing (elements2BrokenElements);
        if (! fromGui && ! candidates.isEmpty ()) {
            InstallMissingDisplayer.getDefault ().notifyCandidates (candidates.keySet ());
        }
    }
    
    private static Collection<UpdateElement> getInstalledAndDisabledElements (Collection<UpdateUnit> allUnits) {
        Set<UpdateElement> res = new HashSet<UpdateElement> ();
        for (UpdateUnit unit : allUnits) {
            if (unit.getInstalled () != null && ! unit.getInstalled ().isEnabled ()) {
                res.add (unit.getInstalled ());
            }
        }
        return res;
    }
    
    private static Map<UpdateElement, Collection<String>> getElements2BrokenDependencies (Collection<UpdateElement> disabledElements) {
        Map<UpdateElement, Collection<String>> broken = new HashMap<UpdateElement, Collection<String>> ();
        for (UpdateElement el : disabledElements) {
            OperationContainer<OperationSupport> forEnable = OperationContainer.createForEnable ();
            if (forEnable.canBeAdded (el.getUpdateUnit (), el)) {
                OperationContainer.OperationInfo<OperationSupport> info = forEnable.add (el);
                if (info == null) {
                    continue;
                }
                Set<String> breaks = info.getBrokenDependencies ();
                if (! breaks.isEmpty ()) {
                    broken.put (el, breaks);
                }
            }
        }
        Map<UpdateElement, Collection<String>> res = new HashMap<UpdateElement, Collection<String>> (broken);
        for (UpdateElement el : disabledElements) {
            if (broken.containsKey (el)) {
                continue;
            }
            OperationContainer<OperationSupport> forEnable = OperationContainer.createForEnable ();
            if (forEnable.canBeAdded (el.getUpdateUnit (), el)) {
                OperationContainer.OperationInfo<OperationSupport> info = forEnable.add (el);
                if (info == null) {
                    continue;
                }
                Set<UpdateElement> reqs = new HashSet<UpdateElement> (info.getRequiredElements ());
                reqs.retainAll (broken.keySet ());
                if (! reqs.isEmpty ()) {
                    res.put (el, collectBroken (reqs, broken));
                }
            }
        }
        return res;
    }
    
    private static Collection<String> collectBroken (Collection<UpdateElement> elems, Map<UpdateElement, Collection<String>> el2broken) {
        Set<String> res = new HashSet<String> ();
        for (UpdateElement el : elems) {
            res.addAll (el2broken.get (el));
        }
        return res;
    }
    
    private static Map<UpdateElement, Collection<UpdateElement>> getCandidatesForFixing (Map<UpdateElement, Collection<String>> element2brokenDeps) {
        Map<UpdateElement, Collection<UpdateElement>> res = new HashMap<UpdateElement, Collection<UpdateElement>> ();
        for (UpdateElement el : element2brokenDeps.keySet ()) {
            Collection<UpdateElement> missingElements = new HashSet<UpdateElement> ();
            for (String dep : element2brokenDeps.get (el)) {
                UpdateElement missing = tryTakeMissingModule (dep);
                if (missing != null) {
                    missingElements.add (missing);
                }
            }
            // XXX: don't report module which cannot fix
            // TODO: report broken deps instead be silent
            if (! missingElements.isEmpty ()) {
                res.put (el, missingElements);
            }
        }
        return res;
    }
    
    private static UpdateElement tryTakeMissingModule (String dep) {
        UpdateElement res = null;
        if (dep != null && dep.startsWith ("module")) { // NOI18N
            String codeName = dep.substring (6).trim ();
            int end = codeName.indexOf ('/'); // NOI18N
            if (end == -1) {
                end = codeName.indexOf (' '); // NOI18N
            }
            if (end != -1) {
                codeName = codeName.substring (0, end);
            }
            res = findUpdateElement (codeName, false);
        }
        return res;
    }
    
    private static UpdateElement findUpdateElement (String codeName, boolean isInstalled) {
        UpdateElement res = null;
        for (UpdateUnit u : UpdateManager.getDefault ().getUpdateUnits (UpdateManager.TYPE.MODULE)) {
            if (codeName.equals (u.getCodeName ())) {
                if (isInstalled && u.getInstalled () != null) {
                    res = u.getInstalled ();
                } else if (! isInstalled && ! u.getAvailableUpdates ().isEmpty ()) {
                    res = u.getAvailableUpdates ().get (0);
                }
                break;
            }
        }
        return res;
    }
}
