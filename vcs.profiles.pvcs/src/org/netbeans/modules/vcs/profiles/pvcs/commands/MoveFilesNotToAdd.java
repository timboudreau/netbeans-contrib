/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * This class moves the files that are not to be added away from the original
 * directory, so that an efficient addfiles -z can be used to add the directory
 * content.
 * @author Martin Entlicher
 */
public class MoveFilesNotToAdd implements VcsAdditionalCommand {
    
    private CommandOutputListener stderrNRListener;
    private CommandDataOutputListener stdoutListener;
    
    /** Creates a new instance of MoveFilesNotToAdd */
    public MoveFilesNotToAdd() {
    }
    
    /**
     * Expect the folder, whose files are to be moved and a file with list of files,
     * which needs to be kept. Just all remaining files are moved.
     * When the file listing is missing, it moves the files back.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        if (args.length < 1) {
            stderrNRListener.outputLine("Expecting two arguments - folder name and a file with files listing of files that are kept.");
            stderrNRListener.outputLine("Or just the folder name to move the files back.");
            return false;
        }
        this.stderrNRListener = stderrNRListener;
        this.stdoutListener = stdoutListener;
        File folder = new File(args[0]);
        File moved = new File(folder.getParentFile(), folder.getName()+"_FilesNotAdded13579");
        boolean status;
        if (args.length == 1) {
            status = moveFiles(moved, folder);
            if (status) {
                status = delete(moved);
            }
        } else {
            status = moveFiles(folder, moved, new File(args[1]));
            if (!folder.exists()) { // Oops, we've moved everything!
                // There is nothing to add !! We'll cancel the add:
                vars.put(org.netbeans.modules.vcscore.util.VariableInputDialog.VAR_CANCEL_DIALOG_BY_PRECOMMAND, "true");
                // and move everything back:
                status = moveFiles(moved, folder);
                if (status) {
                    status = delete(moved);
                }
                ErrorManager.getDefault().notify(ErrorManager.USER,
                        ErrorManager.getDefault().annotate(new InterruptedException(),
                        NbBundle.getMessage(MoveFilesNotToAdd.class, "NoAddMsg")));
            }
        }
        return status;
    }
    
    /** @return The files to keep or <code>null</code>. */
    private Collection readFilesToKeep(File list) {
        Collection fileNames = new HashSet();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(list));
            String name;
            while ((name = r.readLine()) != null) {
                fileNames.add(name);
            }
        } catch (IOException ioex) {
            stderrNRListener.outputLine(ioex.getLocalizedMessage());
            return null;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ioex) {}
            }
        }
        return fileNames;
    }
    
    private boolean moveFiles(File f1, File f2, File list) {
        Collection c = readFilesToKeep(list);
        if (c == null) return false;
        return moveFilesRec(f1, f2, "", c);
    }
        
    private boolean moveFilesRec(File f1, File f2, String relPath, Collection names) {
        File[] files = f1.listFiles();
        if (files == null) files = new File[0];
        if (relPath.length() > 0) relPath = relPath + "/"; // NOI18N
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (names.contains(relPath + name)) {
                continue;
            }
            if (files[i].isDirectory()) {
                //String newRelPath = (relPath.length() > 0) ? relPath + "/" + name : name;
                boolean status = moveFilesRec(files[i], new File(f2, name), relPath + name, names);
                if (!status) return false;
            } else {
                File fileToMove = new File(f1, name);
                String fileName = fileToMove.getName();
                boolean success;
                if (!f2.exists()) {
                    success = f2.mkdirs();
                    if (!success) {
                        stderrNRListener.outputLine("Can not create folder "+f2.getAbsolutePath());
                        return false;
                    }
                }
                success = fileToMove.renameTo(new File(f2, fileName));
                stdoutListener.outputData(new String[] { "Move", fileToMove.getAbsolutePath(), new File(f2, fileName).getAbsolutePath(), Boolean.toString(success) });
                if (!success) {
                    stderrNRListener.outputLine("Move of "+fileToMove.getAbsolutePath()+
                                                " to "+new File(f2, fileName).getAbsolutePath()+
                                                " failed.");
                    return false;
                }
            }
        }
        // When there were some files moved out, check whether the folder is empty
        // This prevents us from adding folders that contain just files that are not intended to be added.
        if (f2.exists()) {
            String[] newList = f1.list();
            if (newList == null || newList.length == 0) {
                f1.delete();
                stdoutListener.outputData(new String[] { "Delete", f1.getAbsolutePath() });
            }
        }
        return true;
    }
    
    private boolean moveFiles(File f1, File f2) {
        File[] files = f1.listFiles();
        if (files == null) return true;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                boolean status = moveFiles(files[i], new File(f2,  files[i].getName()));
                if (!status) return false;
            } else {
                boolean success;
                if (!f2.exists()) {
                    success = f2.mkdirs();
                    if (!success) {
                        stderrNRListener.outputLine("Can not create folder "+f2.getAbsolutePath());
                        return false;
                    }
                }
                success = files[i].renameTo(new File(f2, files[i].getName()));
                stdoutListener.outputData(new String[] { "Move", files[i].getAbsolutePath(), new File(f2, files[i].getName()).getAbsolutePath(), Boolean.toString(success) });
                if (!success) {
                    stderrNRListener.outputLine("Move of "+files[i].getAbsolutePath()+
                                                " to "+new File(f2, files[i].getName()).getAbsolutePath()+
                                                " failed.");
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean delete(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return true;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                boolean success = delete(files[i]);
                if (!success) return false;
            } else {
                stderrNRListener.outputLine("A file that was not moved backed remained here: "+files[i]);
                return false;
            }
        }
        boolean success = folder.delete();
        stdoutListener.outputData(new String[] { "Delete", folder.getAbsolutePath() });
        if (!success) {
            stderrNRListener.outputLine("Can not delete file "+folder);
        }
        return success;
    }
}
