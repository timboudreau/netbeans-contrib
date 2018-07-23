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
package org.netbeans.modules.sfsexplorer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.loaders.DataFolder;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.xml.sax.SAXException;

/**
 * This (singleton) objects creates a cache of XMLFileSystems from
 * the layer files.
 * @author David Strupl
 */
public class XMLFileSystemCache {
    
    /**
     * The singleton instance.
     */
    private static XMLFileSystemCache instance = new XMLFileSystemCache();
    
    /**
     * XML URL --> XMLFileSystem
     */
    private Map<URL, XMLFileSystem> filesystemURLs = new HashMap<URL, XMLFileSystem>();
    
    /**
     * XMLFileSystem --> Jar file name
     */
    private Map<XMLFileSystem, String> filesystemNames = new HashMap<XMLFileSystem, String>();
    
    /**
     * Let's call update in case some modules are added/removed.
     */
    private LookupListener moduleInfoListener; 
    
    /** Creates a new instance of XMLFileSystemCache  - not for everybody.*/
    private XMLFileSystemCache() {
        updateCache();
    }
    
    /**
     * We are a singleton.
     */
    public static XMLFileSystemCache getInstance() {
        return instance;
    }
    
    /**
     * Finds the name of the jar file from where the FileObject originates.
     */
    String getModuleName(FileObject f) {
        String res = ""; // NOI18N
        FileSystem[] cachedFSs = getCachedFileSystems();
        for (int i = 0; i < cachedFSs.length; i++) {
            FileObject originalF = cachedFSs[i].findResource(f.getPath());
            if (originalF != null) {
                String s = getFSName(cachedFSs[i]);
                if (res.length() == 0) {
                    res = s;
                } else {
                    res += ", " + s; // NOI18N
                }
            }
        }
        return res;
    }

    /**
     * Updates the XMLFileSystems in our cache based on the installed modules.
     */
    private void updateCache() {
        Lookup.Result<ModuleInfo> moduleInfos = Lookup.getDefault().lookup(new Lookup.Template<ModuleInfo>(ModuleInfo.class));
        ModuleInfo[] modules = moduleInfos.allInstances().toArray(new ModuleInfo[0]);
        if (moduleInfoListener == null) {
            moduleInfoListener = new LookupListener() {
                public void resultChanged(LookupEvent ev) {
                    updateCache();
                }
            };
            moduleInfos.addLookupListener(moduleInfoListener);
        }
        
        ClassLoader systemClassloader = Lookup.getDefault().lookup(ClassLoader.class);
        for (int i = 0; i < modules.length; i++) {
            Object layerFileName = modules[i].getAttribute("OpenIDE-Module-Layer");
            if (layerFileName instanceof String) {
                String lfn = (String)layerFileName;
                URL url = systemClassloader.getResource(lfn);
                if (url != null) {
                    updateCache(url);
                }
            }
        }
    }
    
    /**
     * Tries to find all children of the given folder that were
     * hidden by some layer file.
     */
    FileObject[] getHiddenChildren(DataFolder dataFolder) {
        List<FileObject> res = new ArrayList<FileObject>();
        FileObject folder = dataFolder.getPrimaryFile();
        try {
            FileSystem mainFS = folder.getFileSystem();
            FileSystem[] cachedFSs = getCachedFileSystems();
            for (int i = 0; i < cachedFSs.length; i++) {
                FileObject originalFolder = cachedFSs[i].findResource(folder.getPath());
                if (originalFolder != null) {
                    // This XML FS defines our folder
                    FileObject[] xmlFSFolderChildren = originalFolder.getChildren();
                    for (int j = 0; j < xmlFSFolderChildren.length; j++) {
                        String chPath = xmlFSFolderChildren[j].getPath();
                        if (chPath.endsWith("_hidden")) { // NOI18N
                            // ignore the hiding files themselves:
                            continue;
                        }
                        FileObject fo = mainFS.findResource(chPath);
                        if (fo == null) {
                            // we have something that was hidden!
                            res.add(xmlFSFolderChildren[j]);
                        }
                    }
                }
            }
        } catch (FileStateInvalidException ex) {
            ex.printStackTrace();
        }
        return res.toArray(new FileObject[res.size()]);
    }

    /**
     * Returns the name of the module jar which hides the given file object.
     */
    String whoHides(FileObject theWretch) {
        String res = ""; // NOI18N
        FileSystem[] cachedFSs = getCachedFileSystems();
        for (int i = 0; i < cachedFSs.length; i++) {
            FileObject originalF = cachedFSs[i].findResource(theWretch.getPath()+"_hidden"); // NOI18N
            if (originalF != null) {
                String s = getFSName(cachedFSs[i]);
                if (res.length() == 0) {
                    res = s;
                } else {
                    res += ", " + s; // NOI18N
                }
            }
        }
        return res;
    }
    
    /**
     * If the given URL is not yet tracked adds the corresponding
     * XMLFileSystem to our cache.
     */
    private void updateCache(URL url) {
        if (filesystemURLs.containsKey(url)) {
            return;
        }
        XMLFileSystem xmlFS;
        try {
            xmlFS = new XMLFileSystem(url);
            filesystemURLs.put(url, xmlFS);
            String s = url.getPath();
            if (s.indexOf('!') > 0) {
                s = s.substring(0, s.indexOf('!'));
            }
            if (s.lastIndexOf('/')>0) {
                s = s.substring(s.lastIndexOf('/')+1);
            }
            if ((s.length() > 2) && (s.startsWith("RM"))) { // NOI18N
                // hack for the JNLP version
                s = s.substring(2);
            }
            filesystemNames.put(xmlFS, s);
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns all of the up to now gathered XMLFileSystems.
     */
    private FileSystem[] getCachedFileSystems() {
        return filesystemNames.keySet().toArray(new FileSystem[filesystemNames.size()]);
    }
    
    /**
     * Finds the jar file name of a given XMLFileSystem.
     */
    private String getFSName(FileSystem fs) {
        String s = filesystemNames.get(fs);
        if (s == null) {
            s = ""; // NOI18N
        }
        return s;
    }
    
    //
    // The section bellow was moved from SFSBrowserTopComponent:
    //
    
    static List<FileObject> getDelegates(MultiFileSystem multiFileSystem, FileObject fileObject) {
        List<FileObject> delegates = new LinkedList<FileObject>();
        getDelegates(multiFileSystem, fileObject, delegates);
        Collections.reverse(delegates);
        return delegates;
    }

    private static Method method;
    static {
        try {
            method = MultiFileSystem.class.getDeclaredMethod("delegates", new Class[] { String.class});
            method.setAccessible(true);
        } catch (NoSuchMethodException nsme) {
            // ignore
        }
    }

    private static void getDelegates(MultiFileSystem multiFileSystem, FileObject fileObject, List<FileObject> delegatesSet) {
        if (method != null) {
            try         {
                java.util.Enumeration delegates = (java.util.Enumeration) method.invoke(multiFileSystem,
                        new Object[] { fileObject.getPath()});

                while (delegates.hasMoreElements()) {
                    FileObject delegate = (FileObject) delegates.nextElement();

                    if (delegate.isValid()) {
                        delegatesSet.add(delegate);
                        FileSystem fileSystem = delegate.getFileSystem();

                        if (fileSystem instanceof MultiFileSystem) {
                            getDelegates((MultiFileSystem) fileSystem,
                                    delegate);
                        }
                    }
                }
            } catch (FileStateInvalidException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                // ignore
            } catch (IllegalArgumentException ex) {
                // ignore
            } catch (InvocationTargetException ex) {
                // ignore
            }
        }
    }
}
