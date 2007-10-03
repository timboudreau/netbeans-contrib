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

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.registry.FSInfo;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;
import org.netbeans.modules.vcs.advanced.projectsettings.CommandLineVcsFileSystemInstance;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariablesUpdater;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.openide.cookies.InstanceCookie;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.InstanceDataObject;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsFileSystemInfo extends Object implements FSInfo,
                                                                    PropertyChangeListener,
                                                                    VetoableChangeListener {
    
    private static String DEFAULT_DISPLAY_TYPE = "VCS"; // NOI18N
    private static String FS_SETTINGS_FOLDER = "VCSMount"; // NOI18N
    
    private File root;
    private String profileName;
    private Map additionalVars;
    private boolean control = true;
    private String settingName = null;
    private String moduleCodeName;
    private volatile boolean explicitelyDisabled;
    
    private transient volatile boolean isModuleEnabled;
    /** Whether this FSInfo was explicitely disabled via setControl(false) */
    private transient ModuleDisabledListener mdListener;
    private transient LookupListener mdLookupListener;
    private transient Reference fileSystemRef = new WeakReference(null);
    private transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private transient VetoableChangeSupport vchangeSupport = new VetoableChangeSupport(this);
    private transient DataFolder settingsFolder;   
    
    static final long serialVersionUID = 8679717370363337670L;
    
    /**
     * Creates a new instance of CommandLineVcsFileSystemInfo.
     * @param profileName The name of the profile. Can be <code>null</code>.
     * @param additionalVars The additional variables. Can be <code>null</code>
     *                       if there are no additional variables.
     */
    public CommandLineVcsFileSystemInfo(File root, String profileName, Map additionalVars) {
        this.root = root;
        this.profileName = profileName;
        this.additionalVars = additionalVars;
        initSettingsFolder();
    }
    
    /**
     * Creates a new instance of CommandLineVcsFileSystemInfo for given filesystem.
     * @param fileSystem The filesystem.
     */
    public CommandLineVcsFileSystemInfo(CommandLineVcsFileSystem fileSystem){
        this.root = fileSystem.getWorkingDirectory();
        this.profileName = fileSystem.getProfile().getName();
        fileSystem.addPropertyChangeListener(WeakListeners.propertyChange(this,fileSystem));
        moduleCodeName = (String)fileSystem.getVariablesAsHashtable().get(CommandLineVcsFileSystemInstance.MODULE_INFO_CODE_NAME_BASE_VAR);
        if(moduleCodeName != null) {
            attachModuleListener(moduleCodeName);
        } else {
            isModuleEnabled = true; // Suppose that the module is hopefully enabled (or it does not need anything more than an XML file).
        }
        if (profileName != null) {
            attachProfileListener();
        }
        fileSystemRef = new WeakReference(fileSystem);
        initSettingsFolder();
        storeFSSettings(fileSystem);
    }
    
    /**
     *Finds a module providing filesystem and attaches listener to it
     *to listen when module is disabled.
     */
    private void attachModuleListener(final String moduleCodeName){        
        Lookup.Result result = Lookup.getDefault().lookup(new Lookup.Template(ModuleInfo.class));
        ModuleInfo info = getModuleInfo(result, moduleCodeName);
        isModuleEnabled = info != null;
        mdListener = new ModuleDisabledListener();
        if (info != null) {
            isModuleEnabled = info.isEnabled();
            info.addPropertyChangeListener(WeakListeners.propertyChange(mdListener, info));
        }
        mdLookupListener = new LookupListener() {
            public void resultChanged (LookupEvent ev) {
                Lookup.Result result = (Lookup.Result) ev.getSource();
                ModuleInfo info = getModuleInfo(result, moduleCodeName);
                if (info != null && mdListener == null) {
                    isModuleEnabled = info.isEnabled();
                    mdListener = new ModuleDisabledListener();
                    info.addPropertyChangeListener(WeakListeners.propertyChange(mdListener, info));
                } else if (info == null && mdListener != null) {
                    isModuleEnabled = false;
                    mdListener = null;
                }
                if (isModuleEnabled) {
                    if (!isControl() && !explicitelyDisabled && existsProfileFile()) {
                        setControlInternally(true);
                    }
                } else {
                    if (isControl()) {
                        setControlInternally(false);
                    }
                }
            }
        };
        result.addLookupListener((LookupListener) WeakListeners.create(LookupListener.class, mdLookupListener, result));
        if (!isModuleEnabled) {
            setControlInternally(false);
        }
    }
    
    private void attachProfileListener() {
        ProfilesFactory.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(this, ProfilesFactory.getDefault()));
    }
    
    private static ModuleInfo getModuleInfo(Lookup.Result result, String moduleCodeName) {
        Collection modules = result.allInstances(); 
        Iterator it = modules.iterator();
        ModuleInfo info = null;
        while(it.hasNext()){
            info = (ModuleInfo)it.next();
            if(info == null)
                continue;
            if(info.getCodeNameBase().equals(moduleCodeName)){                
                break;
            }
        }
        return info;
    }
    
    private void initSettingsFolder() {
        synchronized (CommandLineVcsFileSystemInfo.class) {
            FileObject sfo = Repository.getDefault().getDefaultFileSystem().findResource (FS_SETTINGS_FOLDER);
            if (sfo == null) {
                try {
                    Repository.getDefault().getDefaultFileSystem().getRoot().createFolder(FS_SETTINGS_FOLDER);
                } catch (IOException ioex) {
                    org.openide.ErrorManager.getDefault().notify(ioex); // Should not happen
                }
                sfo = Repository.getDefault().getDefaultFileSystem().findResource (FS_SETTINGS_FOLDER);
            }
            settingsFolder = DataFolder.findFolder(sfo);
        }
    }
    
    /*
     * Get the type of the filesystem, that can be displayed as an additional
     * information.
     */
    public String getDisplayType() {
       if (additionalVars == null) {         
            CommandLineVcsFileSystem fs = (CommandLineVcsFileSystem) fileSystemRef.get();
            if(fs == null)
                return DEFAULT_DISPLAY_TYPE;
            else
                return fs.getProfile().getDisplayName();            
        } else {
            Profile profile = ProfilesFactory.getDefault().getProfile(profileName);
            if(profile == null)
                return DEFAULT_DISPLAY_TYPE;
            else
                return profile.getDisplayName();
            /*
            String displayType = (String) additionalVars.get(CommandLineVcsFileSystem.VAR_FS_DISPLAY_NAME);
            if (displayType == null) {                
                return DEFAULT_DISPLAY_TYPE;
            } else {                
                return displayType;
            }*/
        }
    }
   
    /**
     * Get the root of the filesystem.
     */
    public File getFSRoot() {
        return root;
    }    
 
    /**
     * Get the filesystem instance. This method should create the filesystem
     * if necessary. If the filesystem is still in use, return the same instance.
     */
    public synchronized FileSystem getFileSystem() {
        FileSystem fs = (FileSystem) fileSystemRef.get();
        if (fs == null) {
            fs = createFileSystem();
            fileSystemRef = new WeakReference(fs);
            if (FSRegistry.getDefault().isRegistered(this)) {
                if (fs instanceof CommandLineVcsFileSystem) {
                    ((CommandLineVcsFileSystem) fs).notifyFSAdded();
                }
            }
        }
        return fs;
    }
    
    public FileSystem getExistingFileSystem() {
        return (FileSystem) fileSystemRef.get();
    }
    
    /**
     * Get the icon, that can be used to visually present the filesystem.
     */
    public Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/vcs/advanced/vcsGeneric.gif"); // NOI18N
    }
    
    private FileSystem createFileSystem() {
        CommandLineVcsFileSystem fs;
        if (settingName != null) {
            fs = readFSFromSetting(settingName);
            if (fs == null) { // When the settings are gone
                return null; // No FS without settings!
            }
        } else {
            fs = createNewFS();
        }
        if (settingName == null) {
            settingName = storeFSSettings(fs);
        }
        fs.addPropertyChangeListener(WeakListeners.propertyChange(this, fs));
        fs.addVetoableChangeListener(WeakListeners.vetoableChange(this, fs));
        return fs;
    }
    
    private CommandLineVcsFileSystem createNewFS() {
        CommandLineVcsFileSystem fs = new CommandLineVcsFileSystem();
        if (profileName == null) {
            root = fs.getRootDirectory();
        } else {
            //fs.readConfiguration(profileName);
            Profile profile = ProfilesFactory.getDefault().getProfile(profileName);
            //System.out.println("createFileSystem(): profile = "+profile+", fs = "+fs);
            fs.setProfile(profile);
            //fs.setConfigFileName(profileName);
            try {
                fs.setRootDirectory(root);
            } catch (java.beans.PropertyVetoException vetoExc) {
                ErrorManager.getDefault().notify(vetoExc);
            } catch (java.io.IOException ioExc) {
                ErrorManager.getDefault().notify(ioExc);
            }
            if (additionalVars != null) {
                ConditionedVariables cVars = profile.getVariables();
                ConditionedVariablesUpdater cVarsUpdater = new ConditionedVariablesUpdater(cVars, fs.getVariablesAsHashtable());
                Vector vars = fs.getVariables();
                HashMap varsByName = new HashMap();
                HashMap varValues = new HashMap();
                for (int i = 0, n = vars.size (); i < n; i++) {
                    VcsConfigVariable var = (VcsConfigVariable) vars.get (i);
                    varsByName.put (var.getName (), var);
                    varValues.put (var.getName (), var.getValue());
                }
                for (Iterator addVarIt = additionalVars.keySet().iterator(); addVarIt.hasNext(); ) {
                    String name = (String) addVarIt.next();
                    String value = (String) additionalVars.get(name);
                    VcsConfigVariable var = (VcsConfigVariable) varsByName.get(name);
                    if (var == null) {
                        var = new VcsConfigVariable(name, null, value, false, false, false, null);
                        vars.add(var);
                        varsByName.put(name, var);
                    } else {
                        var.setValue(value);
                    }
                    varValues.put (var.getName (), value);
                }
                Vector variables = cVarsUpdater.updateConditionalValues(cVars, varValues,
                                                                        varsByName, vars);
                fs.setVariables(variables);
            }
        }
        return fs;
    }
    
    private CommandLineVcsFileSystem readFSFromSetting(String settingName) {
        try {
            FileObject sfo = settingsFolder.getPrimaryFile().getFileObject(settingName);
            if (sfo == null) {
                //System.out.println("THE SETTING WAS DELETED!");
                return null;
            }
            DataObject dobj = DataObject.find(sfo);
            InstanceCookie ic = (InstanceCookie) dobj.getCookie (InstanceCookie.class);
            if (ic != null) {
                try {
                    Object o = ic.instanceCreate();
                    if (o instanceof CommandLineVcsFileSystem) {
                        return (CommandLineVcsFileSystem) o;
                    }
                } catch (IOException ioex) {
                    org.openide.ErrorManager.getDefault().notify(ioex);
                } catch (ClassNotFoundException cnfex) {
                    org.openide.ErrorManager.getDefault().notify(cnfex);
                }
            }
            return null;
        } catch (DataObjectNotFoundException donfex) {
            return null;
        }
    }
    
    private String storeFSSettings(CommandLineVcsFileSystem fs) {
        try {
            DataObject dobj = fs.createInstanceDataObject(settingsFolder);
            settingName = dobj.getPrimaryFile().getNameExt();
        } catch (IOException ioex) {
            org.openide.ErrorManager.getDefault().notify(ioex);
            return null;
        }
        return settingName;
    }
    
    public void destroy() {
        if (settingName != null) {
            FileObject fos = settingsFolder.getPrimaryFile().getFileObject(settingName);
            if (fos != null) { // The setting could be already deleted.
                try {
                    fos.delete();
                } catch (IOException ioex) {
                    org.openide.ErrorManager.getDefault().notify(ioex);
                }
            }
            settingName = null;
        }
        fileSystemRef = new WeakReference(null);
    }
    
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (FileSystem.PROP_ROOT.equals(evt.getPropertyName())) {
            fireVetoableChange(PROP_ROOT, evt.getOldValue(), evt.getNewValue());
        }
    }
    
    /** This method gets called when a FS property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (FileSystem.PROP_ROOT.equals(propertyName)) {
            CommandLineVcsFileSystem fs = (CommandLineVcsFileSystem) fileSystemRef.get();
            if (fs != null) {
                root = fs.getRootDirectory();
            }
            firePropertyChange(PROP_ROOT, evt.getOldValue(), root);
        } else if (VcsFileSystem.PROP_VARIABLES.equals(propertyName)) {
            CommandLineVcsFileSystem fs = (CommandLineVcsFileSystem) fileSystemRef.get();
            if (fs != null) {
                String oldDisplayType = getDisplayType();
                additionalVars = fs.getVariablesAsHashtable();
                String newDisplayType = getDisplayType();
                if (!oldDisplayType.equals(newDisplayType)) {
                    firePropertyChange(PROP_TYPE, oldDisplayType, newDisplayType);
                }
            }
        } else if (ProfilesFactory.PROP_PROFILE_ADDED.equals(propertyName)) {
            if (!isControl() && !explicitelyDisabled && isModuleEnabled) {
                if (profileName.equals(evt.getNewValue())) {
                    setControlInternally(true);
                }
            }
        } else if (ProfilesFactory.PROP_PROFILE_REMOVED.equals(propertyName)) {
            if (isControl() && profileName.equals(evt.getOldValue())) {
                setControlInternally(false);
            }
        }
    }
    
    private final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    private final void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
        vchangeSupport.fireVetoableChange(propertyName, oldValue, newValue);
    }
    
    public void addVetoableChangeListener(VetoableChangeListener l) {
        vchangeSupport.addVetoableChangeListener(l);
    }
    
    public void removeVetoableChangeListener(VetoableChangeListener l) {
        vchangeSupport.removeVetoableChangeListener(l);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {       
        changeSupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }
    
    private void setControlInternally(boolean value) {
        if (control != value) {
            control = value;
            changeSupport.firePropertyChange(FSInfo.PROP_CONTROL, !control, control);
        }
    }
 
    public void setControl(boolean value) throws IllegalStateException {
        explicitelyDisabled = (value == false);
        if (value == true && !isModuleEnabled) {
            if (mdListener == null) {
                throw new IllegalStateException("Module "+moduleCodeName+" is not installed.");
            } else {
                throw new IllegalStateException("Module "+moduleCodeName+" is not enabled.");
            }
        }
        setControlInternally(value);
    }
    
    public boolean isControl() {
        return control;
    }
    
    private boolean existsProfileFile() {
        if (profileName != null) {
            return ProfilesFactory.getDefault().getProfile(profileName) != null;
        }
        return true; // Profile name is not defined!
    }
    
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        fileSystemRef = new WeakReference(null);
        changeSupport = new PropertyChangeSupport(this);
        vchangeSupport = new VetoableChangeSupport(this);
        initSettingsFolder();
        if(moduleCodeName != null) {
            attachModuleListener(moduleCodeName);
        } else {
            isModuleEnabled = true; // Suppose that the module is hopefully enabled (or it does not need anything more than an XML file).
        }
        if (profileName != null) {
            attachProfileListener();
        }
        if (isControl() && !existsProfileFile()) {
            setControlInternally(false);
        } else if (!isControl() && !explicitelyDisabled && existsProfileFile() && isModuleEnabled) {
            setControlInternally(true);
        }
    }
    
    private final class ModuleDisabledListener implements PropertyChangeListener{
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (ModuleInfo.PROP_ENABLED.equals(evt.getPropertyName())) {
                CommandLineVcsFileSystemInfo.this.setControlInternally(((ModuleInfo)evt.getSource()).isEnabled());
            }
        }
        
    }
}
