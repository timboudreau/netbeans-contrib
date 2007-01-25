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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Iterator;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * JackpotCommand: a class that maintains the set of properties
 * that define a Jackpot command.  These instances are used to define 
 * operators and transformers in the IDE.
 */
public class JackpotCommand implements Serializable {
    private transient FileObject fileObject;
    private transient PropertyChangeSupport support = new PropertyChangeSupport(this);
    private static final long serialVersionUID = -5198427725141313234L;
    
    // called from layer
    public static JackpotCommand create(final FileObject fo) {
        return new JackpotCommand(fo);
    }
    
    // called from InspectionsList
    static DataObject create(final String command) {
	try {
            DataFolder directory = InspectionsList.instance().getInspectionsFolder();
	    FileObject dir = directory.getPrimaryFile();
	    String name = makeName(dir, command);
            JackpotCommand cmd = new JackpotCommand();
	    InstanceDataObject ido = 
		InstanceDataObject.create(directory, name, cmd, getModuleInfo());
	    FileObject newFO = ido.getPrimaryFile();
	    newFO.setAttribute("inspector", name); //NOI18N
	    newFO.setAttribute("command", command); // NOI18N
	    newFO.setAttribute("SystemFileSystem.icon",
		new URL("nbresloc:/org/netbeans/modules/jackpot/resources/refactoring.png"));
	    cmd.setFileObject(newFO);
	    return ido;
	} catch (Exception e) {
	    ErrorManager.getDefault().notify(e);
	}
	return null;
    }

    private JackpotCommand(FileObject fo) {
        this.fileObject = fo;
    }
    
    private JackpotCommand() {
    }
    
    public FileObject getCommandFileObject() throws MalformedURLException {
	String command = getCommand();
        File f = new File(command);
        if (f.exists())
            return FileUtil.toFileObject(f);
	URL url = new URL(command);
	FileObject fo = URLMapper.findFileObject(url);
	if (fo == null) {
            if (command.startsWith("nbres")) { // NOI18N
                String name = url.getFile();
                url = getClass().getResource(name);
                fo = URLMapper.findFileObject(url);
            }
            else if (command.startsWith("file:")) { //NOI18N
                String name = command.substring(5); // strlen("file:")
                fo = Repository.getDefault().getDefaultFileSystem().findResource(name);
            }
        }
	return fo;
    }
    
    public String getName() {
        return getInspector();
    }

    public String getInspector() {
        return (String)fileObject.getAttribute("inspector");
    }

    public void setInspector(String name) {
        try {
            fileObject.setAttribute("inspector", name); // NOI18N
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }

    public String getTransformer() {
        return (String)fileObject.getAttribute("transformer");
    }

    public void setTransformer(String name) {
        try {
            fileObject.setAttribute("transformer", name); // NOI18N
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }

    public String getDescription() {
        return (String)fileObject.getAttribute("description");
    }

    public void setDescription(String text) {
        try {
            fileObject.setAttribute("description", text); // NOI18N
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }

    public String getCommand() {
        String command = (String)fileObject.getAttribute("command"); // NOI18N
        if (command != null && command.startsWith("/")) // NOI18N
            command = "file:" + command; // NOI18N
	return command;
    }

    public void setCommand(String command) {
	try {
	    fileObject.setAttribute("command", command);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public FileObject getFileObject() {
	return fileObject;
    }
    
    public void setFileObject(FileObject fo) {
	FileObject old = fileObject;
	fileObject = fo;
	support.firePropertyChange("FileObject", old, fileObject);
    }

    private static String makeName(FileObject dir, String command) throws IOException {
	int i = command.lastIndexOf(File.separatorChar) + 1;
        if (i == 0)
            // may be URL
            i = command.lastIndexOf('/') + 1;
	int j = command.lastIndexOf('.');
	String name = command.substring(i, j);
	FileObject existing = dir.getFileObject(name, "settings");
	int n = 1;
	while (existing != null) {
	    name = command.substring(i, j) + '_' + n++;
	    existing = dir.getFileObject(name, "settings");
	}
	return name;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
	support.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
	support.removePropertyChangeListener(l);
    }
    
    private static ModuleInfo getModuleInfo() {
        Lookup.Result<ModuleInfo> modulesResult =
            Lookup.getDefault().lookup(new Lookup.Template<ModuleInfo>(ModuleInfo.class));
        Collection<? extends ModuleInfo> infos = modulesResult.allInstances();
        ModuleInfo curInfo = null;
        boolean equalsName = false;
        for (Iterator<? extends ModuleInfo> iter = infos.iterator(); iter.hasNext(); ) {
            curInfo = iter.next();
            if (curInfo.getCodeNameBase().equals("org.netbeans.modules.jackpot")) {
                return curInfo;
            }
        }
        return null;
    }
}
