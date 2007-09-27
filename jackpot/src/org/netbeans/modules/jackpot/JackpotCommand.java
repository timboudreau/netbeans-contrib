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

package org.netbeans.modules.jackpot;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Iterator;
import org.openide.ErrorManager;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
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
	    String name = makeSettingsName(dir, command);
            JackpotCommand cmd = new JackpotCommand();
	    InstanceDataObject ido = 
		InstanceDataObject.create(directory, name, cmd, getModuleInfo());
	    FileObject newFO = ido.getPrimaryFile();
	    newFO.setAttribute("inspector", name); //NOI18N
	    newFO.setAttribute("SystemFileSystem.icon",
		new URL("nbresloc:/org/netbeans/modules/jackpot/resources/refactoring.png"));
	    cmd.setFileObject(newFO);
	    return ido;
	} catch (Exception e) {
	    ErrorManager.getDefault().notify(e);
	}
	return null;
    }
    
    static DataObject importFile(FileObject fo) {
	try {
            DataFolder directory = InspectionsList.instance().getInspectionsFolder();
	    FileObject dir = directory.getPrimaryFile();
	    String newName = makeUniqueEntry(dir, fo.getName(), fo.getExt());
            FileObject newFO = FileUtil.copyFile(fo, dir, newName);
            JackpotCommand cmd = new JackpotCommand();
	    newFO.setAttribute("inspector", newName); //NOI18N
	    newFO.setAttribute("command", fo.getNameExt()); // NOI18N
	    newFO.setAttribute("SystemFileSystem.icon",
		new URL("nbresloc:/org/netbeans/modules/jackpot/resources/refactoring.png"));
	    cmd.setFileObject(newFO);
	    return DataObject.find(newFO);
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

    private static String makeSettingsName(FileObject dir, String command) throws IOException {
	int i = command.lastIndexOf(File.separatorChar) + 1;
        if (i == 0)
            // may be URL
            i = command.lastIndexOf('/') + 1;
	int j = command.lastIndexOf('.');
	String name = command.substring(i, j);
        return makeUniqueEntry(dir, name, "settings");
    }
    
    private static String makeUniqueEntry(FileObject dir, String name, String ext) {
	FileObject existing = dir.getFileObject(name, ext);
        String entry = name;
	int n = 1;
	while (existing != null) {
	    name = entry + '_' + n++;
	    existing = dir.getFileObject(name, ext);
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
