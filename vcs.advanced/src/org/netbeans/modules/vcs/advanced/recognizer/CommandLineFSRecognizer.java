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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.vcs.advanced.recognizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRecognizer;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandLineFSRecognizer extends FSRecognizer implements PropertyChangeListener {
    
    public static final String VAR_AUTORECOGNIZE_FROM_FILE = "AUTORECOGNIZE_FROM_FILE"; // NOI18N
    public static final String VAR_AUTORECOGNIZE_FROM_COMMAND = "AUTORECOGNIZE_FROM_COMMAND"; // NOI18N
    
    private Map variablesByProfileNames = new HashMap();
    private Map displayTypesByProfileNames = new HashMap();
    private Map commandsToFillByProfileNames = new HashMap();
    
    /** Creates a new instance of Recognizer */
    public CommandLineFSRecognizer() {
        init();
    }
    
    private void init() {
        ProfilesFactory profilesFactory = ProfilesFactory.getDefault();
        profilesFactory.addPropertyChangeListener(WeakListeners.propertyChange(this, profilesFactory));
        String profileNames[] = profilesFactory.getProfilesNames();
        for (int i = 0; i < profileNames.length; i++) {
            if (profilesFactory.isOSCompatibleProfile(profileNames[i])) {
                registerProfile(profilesFactory.getProfile(profileNames[i]));
            }
        }
    }
    
    private void registerProfile(Profile profile) {
        String profileName = profile.getName();
        ConditionedVariables cvars = profile.getVariables();
        Collection profileVars = cvars.getSelfConditionedVariables(profile.getConditions(), Variables.getDefaultVariablesMap());
        Hashtable profileVarsByName = new Hashtable();
        variablesByProfileNames.put(profileName, profileVarsByName);
        for (Iterator varIt = profileVars.iterator(); varIt.hasNext(); ) {
            VcsConfigVariable var = (VcsConfigVariable) varIt.next();
            if (VAR_AUTORECOGNIZE_FROM_FILE.equals(var.getName())) {
                String autorecFromFileStr = var.getValue();
            } else if (VAR_AUTORECOGNIZE_FROM_COMMAND.equals(var.getName())) {
                String className = var.getValue();
                Class execClass = null;
                try {
                    execClass =  Class.forName(className, true, VcsUtilities.getSFSClassLoader());
                                               //org.openide.TopManager.getDefault().currentClassLoader());
                } catch (ClassNotFoundException e) {}
                if (execClass == null) {
                    try {
                        execClass =  Class.forName(className, true,
                                                   (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class));
                    } catch (ClassNotFoundException e) {
                        ErrorManager.getDefault().notify(e);
                    }
                }
                if (execClass != null) commandsToFillByProfileNames.put(profileName, execClass);
            }
            profileVarsByName.put(var.getName(), var.getValue());
        }
    }
    
    /*
    private Map getCmdFillVars(String autoFillVarsStr) {
        String[] varsCmds = VcsUtilities.getQuotedStrings(autoFillVarsStr);
        Map autoFillVars = new HashMap();
        for (int i = 0; (i + 1) < varsCmds.length; i += 2) {
            autoFillVars.put(varsCmds[i], varsCmds[i+1]);
        }
        return autoFillVars;
    }
     */

    /**
     * Get a filesystem info for the given physical folder.
     * @param folder The folder, that is to be recognized.
     * @return Filesystem info for the given folder or <code>null</code> when
     *         no filesystem is recognized.
     */
    public FSInfo findFSInfo(File folder) {
        for (Iterator profileIt = commandsToFillByProfileNames.keySet().iterator(); profileIt.hasNext(); ) {
            String profileName = (String) profileIt.next();
            Class execClass = (Class) commandsToFillByProfileNames.get(profileName);
            VcsAdditionalCommand execCommand = null;
            try {
                execCommand = (VcsAdditionalCommand) execClass.newInstance();
            } catch (InstantiationException e) {
                ErrorManager.getDefault().notify(e);
            } catch (IllegalAccessException e) {
                ErrorManager.getDefault().notify(e);
            }
            if (execCommand != null) {
                Hashtable vars = (Hashtable) variablesByProfileNames.get(profileName);
                if (vars == null) vars = new Hashtable();
                else vars = new Hashtable(vars);
                vars.put("ROOTDIR", folder.getAbsolutePath());
                //System.out.println("Executing "+execClass+"("+folder.getAbsolutePath()+")");
                boolean status = execCommand.exec(vars, new String[0], null,
                                                  null, null, null, null, null);
                if (status) {
                    String root = (String) vars.get("ROOTDIR");
                    //System.out.println("  root = "+root);
                    folder = FileUtil.normalizeFile(new File(root));
                    return new CommandLineVcsFileSystemInfo(folder, profileName, vars);
                }
            }
        }
        return null;
    }
    
    /**
     * Create an empty customizable filesystem info.
     * That is intended for creating of new filesystem information,
     * that were not recognized automatically.
     */
    public FSInfo createFSInfo() {
        return new CommandLineVcsFileSystemInfo(FileUtil.normalizeFile(new File("")), null, null);
    }
    
    /**
     * This method gets called when ProfilesFactory property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (ProfilesFactory.PROP_PROFILE_ADDED.equals(evt.getPropertyName())) {
            String profileName = (String) evt.getNewValue();
            Profile profile = ProfilesFactory.getDefault().getProfile(profileName);
            if (ProfilesFactory.getDefault().isOSCompatibleProfile(profileName)) {
                registerProfile(profile);
            }
        } else if (ProfilesFactory.PROP_PROFILE_REMOVED.equals(evt.getPropertyName())) {
            String profileName = (String) evt.getOldValue();
            variablesByProfileNames.remove(profileName);
            displayTypesByProfileNames.remove(profileName);
            commandsToFillByProfileNames.remove(profileName);
        }
    }
    
}
