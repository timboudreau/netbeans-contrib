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

package org.netbeans.modules.vcs.advanced;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.openide.ErrorManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.xml.XMLUtil;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommands;
import org.netbeans.modules.vcs.advanced.commands.UserCommandIO;
import org.netbeans.modules.vcs.advanced.commands.UserCommandIOCompat;
import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIOCompat;
import org.netbeans.modules.vcscore.Variables;

/**
 * The factory of VCS profiles.
 *
 * @author  Martin Entlicher
 */
public final class ProfilesFactory extends Object {
    
    /**
     * The name of property change event, that is fired when a new profile is
     * added. The name of the added profile can be obtained as the new value
     * of the property change event.
     */
    public static final String PROP_PROFILE_ADDED = "profileAdded"; // NOI18N
    /**
     * The name of property change event, that is fired when a profile is
     * removed. The name of the removed profile can be obtained as the old value
     * of the property change event.
     */
    public static final String PROP_PROFILE_REMOVED = "profileRemoved"; // NOI18N
    
    private static final String PROFILE_ROOT = "vcs/config"; // NOI18N
    
    private static final String DEFAULT_PROFILE_FILE = "default"; // NOI18N
    
    /**
     * This variable is defined in a profile that is created as a localized copy
     * from the original. It contains the locale as the value. In the original
     * profile it remains undefined.
     */
    public static final String VAR_LOCALIZED_PROFILE_COPY = "LOCALIZED_PROFILE_COPY"; // NOI18N
    
    private static ProfilesFactory factory;
    
    private FileObject profileRoot;
    private ProfileRootFolderListener profileRootFolderListener;
    
    private List profileNames;
    private List profileLabels;
    // Whether labels are processed through VcsUtilities.getBundleString().
    // This is not necessary to do until we're asked for them.
    private boolean areLabelsResolved = false;
    private Map profileLabelsByName;
    private Map profileTypesByName;
    private Map compatibleOSsByName;
    private Map uncompatibleOSsByName;
    private Map resourceBundlesByName;
    private Map profilesByName;
    private Map splitWhenLocalizedByNames;
    private Map origProfileNamesByLocalized;
    private String defaultProfileName;
    private String defaultProfileNameStored;
    
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private static FileSystem registry;

    /** Creates a new instance of ProfilesFactory */
    private ProfilesFactory(FileSystem dfs) {
        profileRoot = dfs.findResource(PROFILE_ROOT);
        profileRootFolderListener = new ProfileRootFolderListener();
        profileRoot.addFileChangeListener(FileUtil.weakFileChangeListener(profileRootFolderListener,
                                                                  profileRoot));
        initProfilesInfo();
    }
    
    /**
     * Get the default instance of the profiles factory.
     */
    public static synchronized ProfilesFactory getDefault() {
        if (factory == null) {
            factory = new ProfilesFactory(getRegistry());
        }
        return factory;
    }

    /** Unit tests entry point. */
    static synchronized void setRegistry(FileSystem registrations) {
        registry = registrations;
    }

    static synchronized FileSystem getRegistry() {
        if (registry == null) {
            registry = Repository.getDefault().getDefaultFileSystem ();
        }
        return registry;
    }
    private void initProfilesInfo() {
        //System.out.println("ProfilesCache.initProfileLabels() root = "+profileRoot);
        profileNames = VariableIO.readConfigurations(profileRoot);
        //System.out.println("initProfilesInfo() have profile names = "+profileNames);
        profileLabels = new ArrayList(profileNames.size());
        profileLabelsByName = new HashMap();
        profileTypesByName = new HashMap();
        compatibleOSsByName = new HashMap();
        uncompatibleOSsByName = new HashMap();
        resourceBundlesByName = new HashMap();
        splitWhenLocalizedByNames = new HashMap();
        origProfileNamesByLocalized = new HashMap();
        String[] splitLocNamePtr = new String[1];
        for (int i = 0; i < profileNames.size(); i++) {
            String name = (String) profileNames.get(i);
            splitLocNamePtr[0] = null;
            boolean added = addProfileInitials(name, splitLocNamePtr);
            if (!added) {
                profileNames.remove(i--);
                continue;
            }
            if (splitLocNamePtr[0] != null) {
                profileNames.add(++i, splitLocNamePtr[0]);
            }
            //System.out.println("name = "+profileNames.get(i)+", label = "+getProfileDisplayName((String) profileNames.get(i)));
        }
        // TODO rewrite other methods to handle empty maps
        assert profileNames.size() > 0 : "Other methods do not check empty maps.";  // NOI18N
        profilesByName = new HashMap();
    }
    
    /**
     * Read the basic profile information - label, type, splitloc, OS compatibility and resource bundles.
     * This info is put into the appropriate maps.
     * @param name The name of the profile to read
     * @param splitLocNamePtr A pointer to String - name of the localized profile
     *        is returned here if the profile is split.
     * @return true when profile information was successfully read, false otherwise.
     */
    private boolean addProfileInitials(String name, String[] splitLocNamePtr) {
        String[] typePtr = new String[1];
        String[] splitLocPtr = new String[1];
        String[] labelAndOSs = VariableIO.getConfigurationLabelAndOS(profileRoot, name, typePtr, splitLocPtr);
        if (labelAndOSs == null) {
            return false;
        }
        String label = Variables.expand(Collections.EMPTY_MAP, labelAndOSs[0], false);
        profileLabels.add(label);
        profileLabelsByName.put(name, label);
        profileTypesByName.put(name, typePtr[0]);
        compatibleOSsByName.put(name, (labelAndOSs[1] != null) ? parseOSs(labelAndOSs[1]) : Collections.EMPTY_SET);
        uncompatibleOSsByName.put(name, (labelAndOSs[2] != null) ? parseOSs(labelAndOSs[2]) : Collections.EMPTY_SET);
        String[] resourceBundles = null;
        if (labelAndOSs.length > 3) {
            resourceBundles = new String[labelAndOSs.length - 3];
            System.arraycopy(labelAndOSs, 3, resourceBundles, 0, resourceBundles.length);
            resourceBundlesByName.put(name, resourceBundles);
        }
        if (splitLocPtr[0] != null) {
            splitWhenLocalizedByNames.put(name, splitLocPtr[0]);
            String locale = getLocalizedResourceLocale(splitLocPtr[0]);
            if (locale != null) {
                origProfileNamesByLocalized.put(name + locale, name);
                name = name + locale;
                splitLocNamePtr[0] = name;
                //profileNames.add(++i, name);
                // Get the label of the localized profile
                label = Variables.expand(Collections.singletonMap(VAR_LOCALIZED_PROFILE_COPY, locale), labelAndOSs[0], false);
                profileLabels.add(label);
                profileLabelsByName.put(name, label);
                profileTypesByName.put(name, typePtr[0]);
                compatibleOSsByName.put(name, (labelAndOSs[1] != null) ? parseOSs(labelAndOSs[1]) : Collections.EMPTY_SET);
                uncompatibleOSsByName.put(name, (labelAndOSs[2] != null) ? parseOSs(labelAndOSs[2]) : Collections.EMPTY_SET);
                if (resourceBundles != null) {
                    resourceBundlesByName.put(name, resourceBundles);
                }
            }
        }
        return true;
    }
    
    private Set parseOSs(String oss) {
        Set set;
        if (oss.length() > 0) {
            set = new HashSet();
            String[] strs = VcsUtilities.getQuotedStrings(oss);
            for (int i = 0; i < strs.length; i++) {
                set.add(strs[i]);
            }
        } else {
            set = Collections.EMPTY_SET;
        }
        return set;
    }
    
    /**
     * Find, whether the given resource is localized into the current locale.
     * @return The locale of the provided resource when it matches the current locale
     *         or <code>null</code> when no maching locale is found.
     */
    private String getLocalizedResourceLocale(String splitlocResource) {
        try {
            int dot = splitlocResource.lastIndexOf('.');
            String name, ext;
            if (dot > 0) {
                name = splitlocResource.substring(0, dot);
                ext = splitlocResource.substring(dot + 1);
            } else {
                name = splitlocResource;
                ext = "properties"; // Suppose .properties files by default // NOI18N
            }
            URL locURL = NbBundle.getLocalizedFile(name, ext, Locale.getDefault(), VcsUtilities.getSFSClassLoader());
            String locFile = locURL.getFile();
            dot = locFile.lastIndexOf('.');
            String locName;
            if (dot > 0) {
                locName = locFile.substring(0, dot);
            } else {
                locName = locFile;
            }
            name = name.replace('.', '/');
            int index = locName.indexOf(name);
            if (index >= 0) {
                index += name.length();
                if (locName.length() > (index + 1)) {
                    locName = locName.substring(index + 1); // Take out the "_" as well
                } else {
                    locName = null;
                }
            } else {
                locName = null;
            }
            //System.out.println("getLocalizedResourceLocale("+splitlocResource+"): name = '"+name+"', locName = '"+locName+"'");
            return locName;
        } catch (MissingResourceException mrex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, mrex);
            return null;
        }
    }
    
    public synchronized String[] getProfilesNames() {
        String[] profiles = new String[profileNames.size()];
        profileNames.toArray(profiles);
        return profiles;
    }
    
    public synchronized String getProfileType(String profileName) {
        return (String) profileTypesByName.get(profileName);
    }
    
    public synchronized String[] getProfilesDisplayNames() {
        if (!areLabelsResolved) {
            for (int i = 0; i < profileNames.size(); i++) {
                String name = (String) profileNames.get(i);
                String label = (String) profileLabels.get(i);
                label = VcsUtilities.getBundleString((String[]) resourceBundlesByName.get(name), label);
                profileLabels.set(i, label);
                profileLabelsByName.put(name, label);
            }
            areLabelsResolved = true;
        }
        String[] profiles = new String[profileLabels.size()];
        profileLabels.toArray(profiles);
        return profiles;
    }
    
    public synchronized String getProfileDisplayName(String profileName) {
        if (!areLabelsResolved) {
            String label = (String) profileLabelsByName.get(profileName);
            label = VcsUtilities.getBundleString((String[]) resourceBundlesByName.get(profileName), label);
            profileLabelsByName.put(profileName, label);
        }
        return (String) profileLabelsByName.get(profileName);
    }
    
    /**
     * Get the profile of the given name.
     * @return The profile or <code>null</code> when the profile can not be found.
     */
    public synchronized Profile getProfile(String profileName) {
        Reference profileRef = (Reference) profilesByName.get(profileName);
        Profile profile = null;
        if (profileRef != null) {
            profile = (Profile) profileRef.get();
        }
        if (profile == null) {
            String origName = (String) origProfileNamesByLocalized.get(profileName);
            FileObject profileFile;
            String locale;
            if (origName != null) {
                profileFile = profileRoot.getFileObject(origName);
                locale = profileName.substring(origName.length());
            } else {
                profileFile = profileRoot.getFileObject(profileName);
                locale = null;
            }
            if (profileFile == null) return null;
            profile = new ProfilesFactory.ProfileImpl(profileFile, locale);
            profileRef = new WeakReference(profile);
            profilesByName.put(profileName, profileRef);
        }
        return profile;
    }


    public synchronized boolean isOSCompatibleProfile(String profileName) {
        String osName = System.getProperty ("os.name"); // NOI18N
        Set compatible = (Set) compatibleOSsByName.get(profileName);
        Set uncompatible = (Set) uncompatibleOSsByName.get(profileName);
        if (compatible == null || uncompatible == null) return true;
        if (compatible.contains(osName)) return true;
        if (uncompatible.contains(osName)) return false;
        if (org.openide.util.Utilities.isUnix() && compatible.contains("Unix") ||
            org.openide.util.Utilities.isWindows() && compatible.contains("Windows")) return true;
        if (org.openide.util.Utilities.isUnix() && uncompatible.contains("Unix") ||
            org.openide.util.Utilities.isWindows() && uncompatible.contains("Windows")) return false;
        return (compatible.size() == 0);
    }
    
    /**
     * Get the name of the default profile.
     * @return The name of the default profile or <code>null</code> when no profile
     *         is defined as default.
     */
    public synchronized String getDefaultProfileName() {
        if (defaultProfileName == null) {
            // Check the cached last name read
            if (defaultProfileNameStored != null) {
                if (profileNames.contains(defaultProfileNameStored)) {
                    defaultProfileName = defaultProfileNameStored;
                }
            } else {
                FileObject def = profileRoot.getFileObject(DEFAULT_PROFILE_FILE);
                if (def != null) {
                    BufferedReader r = null;
                    try {
                        r = new BufferedReader(new InputStreamReader(def.getInputStream()));
                        String profileName = r.readLine();
                        if (profileName != null) {
                            defaultProfileNameStored = profileName.intern();
                            if (profileNames.contains(profileName)) {
                                defaultProfileName = profileName.intern();
                            }
                        }
                    } catch (IOException ioex) {
                        ErrorManager.getDefault().notify(ioex);
                    } finally {
                        if (r != null) {
                            try {
                                r.close();
                            } catch (IOException ioex) {
                                ErrorManager.getDefault().notify(ioex);
                            }
                        }
                    }
                } else {
                    defaultProfileNameStored = ""; // NOI18N
                }
            }
        }
        return defaultProfileName;
    }
    
    /**
     * Set the name of the default profile.
     * @param profileName The name of the default profile or <code>null</code>
     *        when no profile should be defined as default.
     * @throws IllegalArgumentException when profile of the given name does not exist.
     */
    public synchronized void setDefaultProfileName(String profileName) throws IllegalArgumentException {
        if (profileName == null) {
            defaultProfileName = profileName;
            FileObject def = profileRoot.getFileObject(DEFAULT_PROFILE_FILE);
            if (def != null) {
                try {
                    def.delete();
                } catch (IOException ioex) {
                    ErrorManager.getDefault().notify(ioex);
                }
            }
            defaultProfileNameStored = ""; // NOI18N
        } else {
            if (!profileNames.contains(profileName)) {
                throw new IllegalArgumentException("Profile '"+profileName+"' does not exist.");
            }
            defaultProfileName = profileName;
            FileObject def = profileRoot.getFileObject(DEFAULT_PROFILE_FILE);
            FileLock lock = null;
            BufferedWriter bw = null;
            try {
                if (def == null) {
                    def = profileRoot.createData(DEFAULT_PROFILE_FILE);
                }
                lock = def.lock();
                bw = new BufferedWriter(new OutputStreamWriter(def.getOutputStream(lock)));
                bw.write(profileName);
                bw.newLine();
            } catch (IOException ioex) {
                ErrorManager.getDefault().notify(ioex);
                return ;
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException ioex) {
                        ErrorManager.getDefault().notify(ioex);
                    }
                }
                if (lock != null) {
                    lock.releaseLock();
                }
            }
            defaultProfileNameStored = profileName;
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }
    
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Remove an existing profile.
     */
    public void removeProfile(String profileName) throws IOException {
        FileObject profileFile = profileRoot.getFileObject(profileName);
        if (profileFile != null) {
            profileFile.delete();
        } else {
            throw new FileNotFoundException(profileName);
        }
        firePropertyChange(PROP_PROFILE_REMOVED, profileName, null);
    }
    
    /**
     * Add a new profile.
     */
    public synchronized Profile addProfile(String name, String displayName,
                                           String type, String[] resourceBundles,
                                           Set compatibleOSs, Set uncompatibleOSs,
                                           ConditionedVariables variables,
                                           ConditionedCommands commands) throws IOException {
        FileObject profileFile = profileRoot.getFileObject(name, VariableIO.CONFIG_FILE_EXT);
        if (profileFile == null) {
            profileFile = profileRoot.createData(name, VariableIO.CONFIG_FILE_EXT);
        }
        name = profileFile.getNameExt();
        profileNames.add(name);
        profileLabels.add(displayName);
        profileTypesByName.put(name, type);
        profileLabelsByName.put(name, displayName);
        resourceBundlesByName.put(name, resourceBundles);
        compatibleOSsByName.put(name, compatibleOSs);
        uncompatibleOSsByName.put(name, uncompatibleOSs);
        Profile profile = new ProfilesFactory.ProfileImpl(profileFile, variables, commands);
        profilesByName.put(name, new WeakReference(profile));
        firePropertyChange(PROP_PROFILE_ADDED, null, name);
        return profile;
    }

    void shutdown() {
        profileRootFolderListener.shutdown();
    }


    public class ProfileImpl extends Profile implements FileChangeListener {
        
        private String profileName;
        private String locale;
        private String origProfileName;
        private Condition[] conditions;
        private ConditionedVariables variables;
        private ConditionedCommands commands;
        private ConditionedCommands globalCommands;
        private volatile boolean isSaving = false;
        
        public ProfileImpl(FileObject profileFile, ConditionedVariables variables,
                           ConditionedCommands commands) {
            this(profileFile);
            this.variables = variables;
            this.commands = commands;
            try {
                saveConfig();
            } catch (org.w3c.dom.DOMException exc) {
                ErrorManager.getDefault().notify(exc);
            } catch (java.io.IOException ioexc) {
                ErrorManager.getDefault().notify(ioexc);
            }
        }
        
        public ProfileImpl(FileObject profileFile) {
            this(profileFile, null);
        }
        
        public ProfileImpl(FileObject profileFile, String locale) {
            this.locale = locale;
            if (locale == null) {
                this.profileName = profileFile.getNameExt();
            } else {
                this.origProfileName = profileFile.getNameExt();
                this.profileName = this.origProfileName + locale;
            }
            profileFile.addFileChangeListener(FileUtil.weakFileChangeListener(this, profileFile));
        }
        
        public String getName() {
            return profileName;
        }
        
        public String getDisplayName() {
            return getProfileDisplayName(profileName);
        }
        
        public String getType() {
            return getProfileType(profileName);
        }
        
        public String getSplitWhenLocalized() {
            return (String) splitWhenLocalizedByNames.get(profileName);
        }
        
        public synchronized void preLoadContent(boolean conditions, boolean variables,
                                                boolean commands, boolean globalCommands) {
            if (origProfileName != null) {
                getProfile(origProfileName).preLoadContent(conditions, variables, commands, globalCommands);
                return ;
            }
            if (this.conditions != null) conditions = false;
            if (this.variables != null) variables = false;
            if (this.commands != null) commands = false;
            if (this.globalCommands != null) globalCommands = false;
            if (conditions || variables || commands || globalCommands) {
                loadConfig(true, conditions, variables, commands, globalCommands);
            }
        }
        
        public String[] getResourceBundles() {
            synchronized (ProfilesFactory.this) {
                return (String[]) resourceBundlesByName.get(profileName);
            }
        }
        
        public Set getCompatibleOSs() {
            synchronized (ProfilesFactory.this) {
                return (Set) compatibleOSsByName.get(profileName);
            }
        }
        
        public Set getUncompatibleOSs() {
            synchronized (ProfilesFactory.this) {
                return (Set) uncompatibleOSsByName.get(profileName);
            }
        }
        
        public synchronized Condition[] getConditions() {
            if (origProfileName != null) {
                return getProfile(origProfileName).getConditions();
            }
            if (conditions == null && !isSaving) { // Nothing can be load while we're saving.
                loadConfig(false, true, false, false, false);
            }
            return conditions;
        }
        
        public synchronized ConditionedVariables getVariables() {
            if (origProfileName != null) {
                ConditionedVariables cvars = getProfile(origProfileName).getVariables();
                Collection lvars = new ArrayList(cvars.getUnconditionedVariables());
                lvars.add(new VcsConfigVariable(VAR_LOCALIZED_PROFILE_COPY, null, locale, false, false, false, null));
                ConditionedVariables lcvars = new ConditionedVariables(lvars, cvars.getConditionsByVariables(), cvars.getVariablesByConditions());
                return lcvars;
            }
            if (variables == null && !isSaving) {
                loadConfig(false, false, true, false, false);
            }
            return variables;
        }
        
        public synchronized ConditionedCommands getCommands() {
            if (origProfileName != null) {
                return getProfile(origProfileName).getCommands();
            }
            if (commands == null && !isSaving) {
                loadConfig(false, false, false, true, false);
            }
            return commands;
        }
        
        public synchronized ConditionedCommands getGlobalCommands() {
            if (origProfileName != null) {
                return getProfile(origProfileName).getGlobalCommands();
            }
            if (globalCommands == null && !isSaving) {
                loadConfig(false, false, false, false, true);
            }
            return globalCommands;
        }
        
        public boolean setConditions(Condition[] conditions) {
            if (origProfileName != null) {
                throw new IllegalStateException("Can not alter the localized copy of a profile. Modify the original instead.");
            }
            synchronized (this) {
                preLoadContent(true, true, true, true);
                this.conditions = conditions;
                try {
                    saveConfig();
                } catch (org.w3c.dom.DOMException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return false;
                } catch (java.io.IOException ioexc) {
                    ErrorManager.getDefault().notify(ioexc);
                    return false;
                }
            }
            this.firePropertyChange(PROP_CONDITIONS, null, conditions);
            return true;
        }
        
        public boolean setVariables(ConditionedVariables variables) {
            if (origProfileName != null) {
                throw new IllegalStateException("Can not alter the localized copy of a profile. Modify the original instead.");
            }
            synchronized (this) {
                preLoadContent(true, true, true, true);
                this.variables = variables;
                try {
                    saveConfig();
                } catch (org.w3c.dom.DOMException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return false;
                } catch (java.io.IOException ioexc) {
                    ErrorManager.getDefault().notify(ioexc);
                    return false;
                }
            }
            this.firePropertyChange(PROP_VARIABLES, null, variables);
            return true;
        }
        
        public boolean setCommands(ConditionedCommands commands) {
            if (origProfileName != null) {
                throw new IllegalStateException("Can not alter the localized copy of a profile. Modify the original instead.");
            }
            synchronized (this) {
                preLoadContent(true, true, true, true);
                this.commands = commands;
                try {
                    saveConfig();
                } catch (org.w3c.dom.DOMException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return false;
                } catch (java.io.IOException ioexc) {
                    ErrorManager.getDefault().notify(ioexc);
                    return false;
                }
            }
            this.firePropertyChange(PROP_COMMANDS, null, commands);
            return true;
        }
        
        public synchronized boolean setGlobalCommands(ConditionedCommands globalCommands) {
            if (origProfileName != null) {
                throw new IllegalStateException("Can not alter the localized copy of a profile. Modify the original instead.");
            }
            synchronized (this) {
                preLoadContent(true, true, true, true);
                this.globalCommands = globalCommands;
                try {
                    saveConfig();
                } catch (org.w3c.dom.DOMException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return false;
                } catch (java.io.IOException ioexc) {
                    ErrorManager.getDefault().notify(ioexc);
                    return false;
                }
            }
            this.firePropertyChange(PROP_GLOBAL_COMMANDS, null, globalCommands);
            return true;
        }
        
        public boolean isLocalizedCopy() {
            return origProfileName != null;
        }

        void unimplementableFromOutside() {
        }
        
        private void loadConfig(boolean os, boolean conditions, boolean variables,
                                boolean commands, boolean globalCommands) {
            if (profileName == null) return ;
            if (profileName.endsWith(VariableIOCompat.CONFIG_FILE_EXT)) {
                Properties props = VariableIOCompat.readPredefinedProperties(profileRoot, profileName);
                if (props == null) return ;
                this.variables = new ConditionedVariables(VariableIOCompat.readVariables(props),
                                                     Collections.EMPTY_MAP, Collections.EMPTY_MAP);
                
                //  TODO commands = (CommandsTree) UserCommandIOCompat.readUserCommands(props, variableMap);
                
                //profileCommandsByLabel.put(profileDisplayName, commands);
                this.conditions = new Condition[0];
            } else {
                //System.out.println("loadConfig("+profileName+"; "+os+", "+conditions+", "+variables+", "+commands+", "+globalCommands+")");
                //long start = System.currentTimeMillis();
                FileObject profileFO = profileRoot.getFileObject(profileName);
                if (profileFO == null) {
                    org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
                        public void run() {
                            org.openide.ErrorManager.getDefault().notify(new FileNotFoundException("Problems while reading predefined properties.") {
                                public String getLocalizedMessage() {
                                    return NbBundle.getMessage(VariableIO.class, "EXC_Problems_while_reading_predefined_properties", profileName);
                                }
                            });
                        }
                    });
                    //E.err(g("EXC_Problems_while_reading_predefined_properties",name)); // NOI18N
                    return ;
                }
                ProfileContentHandler handler = new ProfileContentHandler(os, conditions, variables, commands, globalCommands, null);
                handler.setSourceName(profileName);
                //System.out.println("VariableIO.getConfigurationLabel("+config+")");
                try {
                    XMLReader reader = XMLUtil.createXMLReader();
                    reader.setContentHandler(handler);
                    reader.setEntityResolver(handler);
                    InputSource source = new InputSource(profileFO.getInputStream());
                    reader.parse(source);
                } catch (SAXException exc) {
                    if (!ProfileContentHandler.END_OF_PARSING.equals(exc.getMessage())) {
                        org.openide.ErrorManager.getDefault().notify(
                            org.openide.ErrorManager.getDefault().annotate(
                                exc, NbBundle.getMessage(VariableIO.class, "EXC_Problems_while_reading_predefined_properties", profileName)));
                        return ;
                    }
                } catch (java.io.FileNotFoundException fnfExc) {
                    org.openide.ErrorManager.getDefault().notify(
                        org.openide.ErrorManager.getDefault().annotate(
                            fnfExc, NbBundle.getMessage(VariableIO.class, "EXC_Problems_while_reading_predefined_properties", profileName)));
                    return ;
                } catch (java.io.IOException ioExc) {
                    org.openide.ErrorManager.getDefault().notify(
                        org.openide.ErrorManager.getDefault().annotate(
                            ioExc, NbBundle.getMessage(VariableIO.class, "EXC_Problems_while_reading_predefined_properties", profileName)));
                    return ;
                }
                if (conditions) {
                    this.conditions = handler.getConditions();
                }
                if (variables) {
                    this.variables = handler.getVariables();
                }
                if (commands) {
                    this.commands = handler.getCommands();
                }
                if (globalCommands) {
                    this.globalCommands = handler.getGlobalCommands();
                }
                //long end = System.currentTimeMillis();
                //System.out.println("  loadConfig(,,,,) took "+(end - start)+" milliseconds.");
            }
        }
        
        private void saveConfig() throws org.w3c.dom.DOMException, java.io.IOException {
            FileObject file = profileRoot.getFileObject(profileName);//, VariableIO.CONFIG_FILE_EXT);
            if (file != null) {
                org.openide.filesystems.FileLock lock = null;
                java.io.OutputStream out = null;
                try {
                    isSaving = true;
                    lock = file.lock();
                    out = new BufferedOutputStream(file.getOutputStream(lock));
                    ProfileWriter.write(out, this);
                } finally {
                    try {
                        if (out != null) out.close();
                        if (lock != null) lock.releaseLock();
                    } finally {
                        isSaving = false;
                    }
                }
            }
        }

        public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent) {
        }
        
        public void fileChanged(FileEvent fileEvent) {
            if (!isSaving) {
                // When we're not saving the profile ourselves, we need to reload
                // the profile later.
                synchronized (this) {
                    conditions = null;
                    variables = null;
                    commands = null;
                    globalCommands = null;
                }
            }
            this.firePropertyChange(PROP_CONDITIONS, null, null);
            this.firePropertyChange(PROP_VARIABLES, null, null);
            this.firePropertyChange(PROP_COMMANDS, null, null);
            this.firePropertyChange(PROP_GLOBAL_COMMANDS, null, null);
        }
        
        public void fileDataCreated(FileEvent fileEvent) {
        }
        
        public void fileDeleted(FileEvent fileEvent) {
        }
        
        public void fileFolderCreated(FileEvent fileEvent) {
        }
        
        public void fileRenamed(FileRenameEvent fileRenameEvent) {
        }
        
    }
    
    public static class ProfileImplBeanInfo extends SimpleBeanInfo{
        
        /**
         * Gets the bean's <code>PropertyDescriptor</code>s.
         *
         * @return An array of PropertyDescriptors describing the editable
         * properties supported by this bean.  May return null if the
         * information should be obtained by automatic analysis.
         * <p>
         * If a property is indexed, then its entry in the result array will
         * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
         * A client of getPropertyDescriptors can use "instanceof" to check
         * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
         */
        public PropertyDescriptor[] getPropertyDescriptors() {
            PropertyDescriptor[] properties = null;
            try {
                properties = new PropertyDescriptor[] {
                    new PropertyDescriptor("conditions", ProfileImpl.class),
                    new PropertyDescriptor("variables", ProfileImpl.class),
                    new PropertyDescriptor("commands", ProfileImpl.class),
                    new PropertyDescriptor("globalCommands", ProfileImpl.class),
                };
                
                properties[0].setDisplayName(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("PROP_Conditions"));
                properties[0].setShortDescription(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("HINT_PPROP_Conditions"));
                properties[0].setPropertyEditorClass(UserConditionsEditor.class);
                properties[0].setValue("canEditAsText", Boolean.FALSE);
                properties[1].setDisplayName(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("PROP_Variables"));
                properties[1].setShortDescription(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("HINT_PPROP_Variables"));
                properties[1].setPropertyEditorClass(UserConditionedVariablesEditor.class);
                properties[1].setValue("canEditAsText", Boolean.FALSE);
                properties[2].setDisplayName(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("PROP_Commands"));
                properties[2].setShortDescription(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("HINT_PROP_Commands"));
                properties[2].setPropertyEditorClass(UserConditionedCommandsEditor.class);
                properties[2].setValue("canEditAsText", Boolean.FALSE);
                properties[3].setDisplayName(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("PROP_GlobalCommands"));
                properties[3].setShortDescription(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("HINT_PROP_GlobalCommands"));
                properties[3].setPropertyEditorClass(UserConditionedCommandsEditor.class);
                properties[3].setValue("canEditAsText", Boolean.FALSE);
                
            } catch (java.beans.IntrospectionException intrexc) {
                ErrorManager manager = (ErrorManager)Lookup.getDefault().lookup(ErrorManager.class);
                manager.notify();
            }
            return properties;
        }
        /*
         * This method returns an image object that can be used to
         * represent the bean in toolboxes, toolbars, etc.   Icon images
         * will typically be GIFs, but may in future include other formats.
         *
         * @param  iconKind  The kind of icon requested.  This should be
         *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32,
         *    ICON_MONO_16x16, or ICON_MONO_32x32.
         * @return  An image object representing the requested icon.  May
         *    return null if no suitable icon is available.
         */
        public java.awt.Image getIcon(int iconKind) {
            // return Utilities.loadImage("org/netbeans/modules/vcs/advanced/vcsGeneric.gif"); // NOI18N
            return null; //for now we don't have appropriate icon
        }
        
    }
        
    private class ProfileRootFolderListener extends Object implements FileChangeListener {

        private boolean down;

        /**
         * The config's root folder's attribute changed.
         */
        public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent) {
        }

        /**
         * The config's root folder changed.
         */
        public void fileChanged(FileEvent fileEvent) {
        }

        /**
         * Some data were created in the config's root folder.
         */
        public void fileDataCreated(FileEvent fileEvent) {
            if (down) return;
            FileObject newData = fileEvent.getFile();
            if (newData.getSize() == 0L || !VariableIO.isConfigFile(newData)) return ; // Ignore an empty file
            List currentLocales = VariableIO.getLocalizedConfigurations(new FileObject[] { newData });
            String name = newData.getNameExt();
            if (!currentLocales.contains(name)) return ; // Ignore other locales
            String[] splitLocNamePtr = new String[1];
            synchronized (ProfilesFactory.this) {
                boolean added = addProfileInitials(name, splitLocNamePtr);
                if (!added) {
                    return ;
                }
                if (areLabelsResolved) {
                    String label = (String) profileLabelsByName.get(name);
                    label = VcsUtilities.getBundleString((String[]) resourceBundlesByName.get(name), label);
                    profileLabelsByName.put(name, label);
                    profileLabels.remove(profileLabels.size() - 1);
                    profileLabels.add(label);
                }
                profileNames.add(name);
                if (splitLocNamePtr[0] != null) {
                    profileNames.add(splitLocNamePtr[0]);
                    String label = (String) profileLabelsByName.get(splitLocNamePtr[0]);
                    label = VcsUtilities.getBundleString((String[]) resourceBundlesByName.get(splitLocNamePtr[0]), label);
                    profileLabelsByName.put(splitLocNamePtr[0], label);
                }
            }
            ProfilesFactory.this.firePropertyChange(PROP_PROFILE_ADDED, null, name);
        }

        /**
         * Some data were deleted in the config's root folder.
         */
        public void fileDeleted(FileEvent fileEvent) {
            if (down) return;
            FileObject oldData = fileEvent.getFile();
            String name = oldData.getNameExt();
            synchronized (ProfilesFactory.this) {
                profileNames.remove(name);
                String label = (String) profileLabelsByName.remove(name);
                profileLabels.remove(label);
                compatibleOSsByName.remove(name);
                uncompatibleOSsByName.remove(name);
                if (name.equals(defaultProfileName)) {
                    defaultProfileName = null;
                }
            }
            ProfilesFactory.this.firePropertyChange(PROP_PROFILE_REMOVED, name, null);
        }

        /**
         * Some folder was created in the config's root folder.
         */
        public void fileFolderCreated(FileEvent fileEvent) {
        }

        /**
         * The config's root folder was renamed.
         */
        public void fileRenamed(FileRenameEvent fileRenameEvent) {
        }
        
        void shutdown() {
            down = true;
        }
    }
    
}
