/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;

import org.openide.filesystems.FileObject;

import org.netbeans.api.diff.Difference;
import org.netbeans.modules.merge.builtin.visualizer.GraphicalMergeVisualizer;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.cmdline.*;

/**
 * This class is used to resolve merge conflicts in a graphical way using a merge visualizer.
 * We parse the file with merge conflicts marked, let the conflicts resolve by the
 * visual merging tool and after successfull conflicts resolution save it back
 * to the original file.
 *
 * @author  Martin Entlicher
 */
public class CvsResolveConflicts implements VcsAdditionalCommand {
    
    private static final String TMP_PREFIX = "merge"; // NOI18N
    private static final String TMP_SUFFIX = "tmp"; // NOI18N
    
    private static final String CHANGE_LEFT = "<<<<<<< "; // NOI18N
    private static final String CHANGE_RIGHT = ">>>>>>> "; // NOI18N
    private static final String CHANGE_DELIMETER = "======="; // NOI18N

    private VcsFileSystem fileSystem = null;

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {

        /*
        if (fileSystem instanceof CvsFileSystem) {
            CvsFileSystem cvsFileSystem = (CvsFileSystem) fileSystem;
         */
        Collection files = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            String fileName = (String) it.next();
            File file = fileSystem.getFile(fileName);
            if (file != null) {
                FileObject fo = fileSystem.findResource(fileName);
                try {
                    handleMergeFor(file, (fo == null) ? "text/plain" : fo.getMIMEType());
                } catch (IOException ioex) {
                    org.openide.TopManager.getDefault().notifyException(ioex);
                }
            }
        }
        return true;
    }
    
    private void handleMergeFor(final File file, String mimeType) throws IOException {
        File f1 = File.createTempFile(TMP_PREFIX, TMP_SUFFIX);
        File f2 = File.createTempFile(TMP_PREFIX, TMP_SUFFIX);
        final File f3 = File.createTempFile(TMP_PREFIX, TMP_SUFFIX);
        
        Difference[] diffs = copyParts(true, file, f1, true);
        copyParts(false, file, f2, false);
        GraphicalMergeVisualizer merge = new GraphicalMergeVisualizer();
        java.awt.Component c;
        c = merge.createView(diffs, file.getName(), file.getAbsolutePath(),
                             new FileReader(f1), file.getName(), file.getAbsolutePath(),
                             new FileReader(f2), file.getName(), file.getAbsolutePath(),
                             new FileWriter(f3), mimeType, new ActionListener() {
                                 public void actionPerformed(ActionEvent evt) {
                                     if ("OK".equals(evt.getActionCommand())) {
                                         try {
                                            org.openide.filesystems.FileUtil.copy(
                                                new FileInputStream(f3), new FileOutputStream(file));
                                         } catch (IOException ioex) {}
                                     }
                                 }
                             });
    }
    
    /**
     * Copy the file and conflict parts into another file.
     */
    private Difference[] copyParts(boolean generateDiffs, File source,
                                   File dest, boolean leftPart) throws IOException {
        //System.out.println("copyParts("+generateDiffs+", "+source+", "+dest+", "+leftPart+")");
        BufferedReader r = new BufferedReader(new FileReader(source));
        BufferedWriter w = new BufferedWriter(new FileWriter(dest));
        ArrayList diffList = null;
        if (generateDiffs) {
            diffList = new ArrayList();
        }
        try {
            String line;
            boolean isChangeLeft = false;
            boolean isChangeRight = false;
            int f1l1 = 0, f1l2 = 0, f2l1 = 0, f2l2 = 0;
            int i = 1, j = 1;
            while ((line = r.readLine()) != null) {
                if (line.startsWith(CHANGE_LEFT)) {
                    if (generateDiffs) {
                        if (isChangeLeft) {
                            f1l2 = i - 1;
                            diffList.add((f1l1 > f1l2) ? new Difference(Difference.ADD, f1l1 - 1, 0, f2l1, f2l2) :
                                         (f2l1 > f2l2) ? new Difference(Difference.DELETE, f1l1, f1l2, f2l1 - 1, 0)
                                                       : new Difference(Difference.CHANGE, f1l1, f1l2, f2l1, f2l2));
                            f1l1 = f1l2 = f2l1 = f2l2 = 0;
                        } else {
                            f1l1 = i;
                        }
                    }
                    isChangeLeft = !isChangeLeft;
                    continue;
                } else if (line.startsWith(CHANGE_RIGHT)) {
                    if (generateDiffs) {
                        if (isChangeRight) {
                            f2l2 = j - 1;
                            diffList.add((f1l1 > f1l2) ? new Difference(Difference.ADD, f1l1 - 1, 0, f2l1, f2l2) :
                                         (f2l1 > f2l2) ? new Difference(Difference.DELETE, f1l1, f1l2, f2l1 - 1, 0)
                                                       : new Difference(Difference.CHANGE, f1l1, f1l2, f2l1, f2l2));
                                                       /*
                            diffList.add(new Difference((f1l1 > f1l2) ? Difference.ADD :
                                                        (f2l1 > f2l2) ? Difference.DELETE :
                                                                        Difference.CHANGE,
                                                        f1l1, f1l2, f2l1, f2l2));
                                                        */
                            f1l1 = f1l2 = f2l1 = f2l2 = 0;
                        } else {
                            f2l1 = j;
                        }
                    }
                    isChangeRight = !isChangeRight;
                    continue;
                } else if (line.equals(CHANGE_DELIMETER)) {
                    if (isChangeLeft) {
                        isChangeLeft = false;
                        isChangeRight = true;
                        f1l2 = i - 1;
                        f2l1 = j;
                        continue;
                    } else if (isChangeRight) {
                        isChangeRight = false;
                        isChangeLeft = true;
                        f2l2 = j - 1;
                        f1l1 = i;
                        continue;
                    }
                }
                if (!isChangeLeft && !isChangeRight || leftPart == isChangeLeft) {
                    w.write(line);
                    w.newLine();
                }
                if (generateDiffs) {
                    if (isChangeLeft) i++;
                    else if (isChangeRight) j++;
                    else {
                        i++;
                        j++;
                    }
                }
            }
        } finally {
            try {
                r.close();
            } finally {
                w.close();
            }
        }
        if (generateDiffs) {
            return (Difference[]) diffList.toArray(new Difference[diffList.size()]);
        } else {
            return null;
        }
    }
}

