/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.recognizer;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.openide.ErrorManager;
import org.openide.filesystems.FileSystem;
import org.openide.util.Utilities;
import org.openide.util.WeakListener;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.registry.FSInfo;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;
import org.netbeans.modules.vcscore.VcsFileSystem;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsFileSystemInfo extends Object implements FSInfo, PropertyChangeListener {
    
    private static String DEFAULT_DISPLAY_TYPE = "VCS"; // NOI18N
    
    private File root;
    private String profileName;
    private Map additionalVars;
    private boolean control = true;
    private transient Reference fileSystemRef = new WeakReference(null);
    private transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    
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
    }
    
    /**
     * Creates a new instance of CommandLineVcsFileSystemInfo for given filesystem.
     * @param fileSystem The filesystem.
     */
    public CommandLineVcsFileSystemInfo(CommandLineVcsFileSystem fileSystem){
        this.root = fileSystem.getWorkingDirectory();
        this.profileName = fileSystem.getProfile().getName();
        fileSystemRef = new WeakReference(fileSystem);
    }
    
    /*
     * Get the type of the filesystem, that can be displayed as an additional
     * information.
     */
    public String getDisplayType() {
        if (additionalVars == null) {
            return DEFAULT_DISPLAY_TYPE;
        } else {
            String displayType = (String) additionalVars.get(CommandLineVcsFileSystem.VAR_FS_DISPLAY_NAME);
            if (displayType == null) {
                return DEFAULT_DISPLAY_TYPE;
            } else {
                return displayType;
            }
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
        }
        return fs;
    }
    
    /**
     * Get the icon, that can be used to visually present the filesystem.
     */
    public Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/vcs/advanced/vcsGeneric.gif"); // NOI18N
    }
    
    private FileSystem createFileSystem() {
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
                Vector vars = fs.getVariables();
                HashMap varsByName = new HashMap();
                for (int i = 0, n = vars.size (); i < n; i++) {
                    VcsConfigVariable var = (VcsConfigVariable) vars.get (i);
                    varsByName.put (var.getName (), var);
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
                }
                fs.setVariables(vars);
            }
        }
        fs.addPropertyChangeListener(WeakListener.propertyChange(this, fs));
        return fs;
    }
    
    /** This method gets called when a FS property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (FileSystem.PROP_ROOT.equals(evt.getPropertyName())) {
            CommandLineVcsFileSystem fs = (CommandLineVcsFileSystem) fileSystemRef.get();
            if (fs != null) {
                root = fs.getRootDirectory();
            }
            firePropertyChange(PROP_ROOT, null, root);
        } else if (VcsFileSystem.PROP_VARIABLES.equals(evt.getPropertyName())) {
            CommandLineVcsFileSystem fs = (CommandLineVcsFileSystem) fileSystemRef.get();
            if (fs != null) {
                String oldDisplayType = getDisplayType();
                additionalVars = fs.getVariablesAsHashtable();
                String newDisplayType = getDisplayType();
                if (!oldDisplayType.equals(newDisplayType)) {
                    firePropertyChange(PROP_TYPE, oldDisplayType, newDisplayType);
                }
            }
        }
    }
    
    private final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }
 
    public void setControl(boolean value) {
        if (control != value) {
            control = value;
            changeSupport.firePropertyChange(FSInfo.PROP_CONTROL, !control, control);
        }
    }
    
    public boolean isControl() {
        return control;
    }
    
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        fileSystemRef = new WeakReference(null);
        changeSupport = new PropertyChangeSupport(this);
    }
}
