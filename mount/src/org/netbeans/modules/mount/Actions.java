/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.mount;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.spi.project.ActionProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 * Manages actions for the mount dummy project.
 * @author Jesse Glick
 */
final class Actions implements ActionProvider {
    
    private FileObject buildXml;
    
    public Actions() {
        try {
            buildXml = WorkDir.initBuildEnvironment();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            buildXml = null;
        }
    }

    public String[] getSupportedActions() {
        return new String[] {
            // Run <javac> on some files in a single root:
            ActionProvider.COMMAND_COMPILE_SINGLE,
            // XXX others
        };
    }

    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        if (buildXml == null) {
            // Broken - could not make build file.
            return false;
        }
        if (command == ActionProvider.COMMAND_COMPILE_SINGLE) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, null, null, true);
            if (files == null) {
                return false;
            }
            // Check that all files are either *.java or folders.
            for (int i = 0; i < files.length; i++) {
                if (files[i].isData() && !files[i].hasExt("java")) { // NOI18N
                    return false;
                }
            }
            FileObject[] roots = MountList.DEFAULT.getMounts();
            // Assure that there is one correct root.
            FIND_ROOT: for (int i = 0; i < roots.length; i++) {
                if (FileUtil.toFile(roots[i]) == null) {
                    // JAR entry, skip it.
                    continue;
                }
                for (int j = 0; j < files.length; j++) {
                    if (files[j] != roots[i] && !FileUtil.isParentOf(roots[i], files[j])) {
                        continue FIND_ROOT;
                    }
                }
                // All files belong to this root.
                return true;
            }
            // More than one root, or some files not in any root.
            return false;
        } else {
            throw new IllegalArgumentException(command);
        }
    }
    
    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        if (command == ActionProvider.COMMAND_COMPILE_SINGLE) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, null, null, true);
            assert files != null;
            FileObject[] roots = MountList.DEFAULT.getMounts();
            // Find the one root.
            FileObject root = null;
            FIND_ROOT: for (int i = 0; i < roots.length; i++) {
                for (int j = 0; j < files.length; j++) {
                    if (files[j] != roots[i] && !FileUtil.isParentOf(roots[i], files[j])) {
                        continue FIND_ROOT;
                    }
                }
                root = roots[i];
                break;
            }
            assert root != null;
            // Calculate the relative paths to source files and/or folders.
            StringBuffer includes = new StringBuffer();
            for (int i = 0; i < files.length; i++) {
                String relpath = FileUtil.getRelativePath(root, files[i]);
                assert relpath != null : Arrays.asList(files);
                String include;
                if (relpath.length() == 0) {
                    include = "**"; // NOI18N
                } else if (files[i].isFolder()) {
                    include = relpath + "/"; // NOI18N
                } else {
                    include = relpath;
                }
                if (includes.length() > 0) {
                    includes.append(',');
                }
                includes.append(include);
            }
            // Calculate javac source level to use.
            // Could just omit this, but probably best to make it match ClassPathProvider etc.
            String sourceLevel = JavaPlatformManager.getDefault().getDefaultPlatform().getSpecification().getVersion().toString();
            // Run Ant.
            Properties p = new Properties();
            p.setProperty("javac.root", FileUtil.toFile(root).getAbsolutePath()); // NOI18N
            p.setProperty("javac.includes", includes.toString()); // NOI18N
            p.setProperty("javac.source.level", sourceLevel); // NOI18N
            try {
                ActionUtils.runTarget(buildXml, new String[] {"compile-single"}, p); // NOI18N
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        } else {
            throw new IllegalArgumentException(command);
        }
    }

}
