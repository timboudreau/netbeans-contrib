/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

/**
 * This command assures, that when one deletes a local folder, which have
 * it's item in the parent's entries, it is removed from the entries, so that
 * recursive commands will work correctly.
 *
 * @author  Martin Entlicher
 */
public class CvsDeleteFolder extends Object implements VcsAdditionalCommand {

    private static final String ENTRIES = "CVS/Entries";
    
    private VcsFileSystem fileSystem;

    /** Creates new CvsDeleteFolder */
    public CvsDeleteFolder() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        Collection files = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            removeFolderFromEntries((String) it.next());
        }
        return true;
    }
    
    private void removeFolderFromEntries(String folderName) {
        File folder = fileSystem.getFile(folderName);
        File parent = folder.getParentFile();
        File entries = new File(parent, ENTRIES);
        if (entries.exists()) {
            List entriesContent = loadEntries(entries);
            String name = folder.getName();
            if (removeEntriesFolder(name, entriesContent)) {
                writeEntries(entries, entriesContent);
            }
        }
    }
    
    private boolean removeEntriesFolder(String name, List entriesContent) {
        String pattern = "D/"+name+"/";
        for (Iterator it = entriesContent.iterator(); it.hasNext(); ) {
            String line = (String) it.next();
            if (line.startsWith(pattern)) {
                entriesContent.remove(line);
                return true;
            }
        }
        return false;
    }
    
    private static List loadEntries(File entries) {
        ArrayList entriesFiles = new ArrayList();
        if (entries.exists() && entries.canRead() && entries.canWrite()) {
            int fileIndex = -1;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(entries));
                String line;
                while ((line = reader.readLine()) != null) {
                    entriesFiles.add(line);
                }
            } catch (FileNotFoundException fnfExc) {
                // ignore
            } catch (IOException ioExc) {
                // ignore
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException exc) {}
            }
        }
        return entriesFiles;
    }
    
    private static void writeEntries(File entries, List entriesFiles) {
        if (entries.exists() && entries.canRead() && entries.canWrite()) {
            int fileIndex = -1;
            BufferedWriter writer = null;
            File entriesCopy = new File(entries.getAbsolutePath()+".rmdir");
            boolean errors = false;
            try {
                entriesCopy.createNewFile();
                writer = new BufferedWriter(new FileWriter(entriesCopy));
                for (Iterator it = entriesFiles.iterator(); it.hasNext(); ) {
                    String line = (String) it.next();
                    writer.write(line + "\n");
                }
            } catch (FileNotFoundException fnfExc) {
                errors = true;
            } catch (IOException ioExc) {
                errors = true;
            } finally {
                try {
                    if (writer != null) writer.close();
                } catch (IOException exc) {
                    errors = true;
                }
            }
            if (!errors) {
                entries.delete();
                entriesCopy.renameTo(entries);
            }
        }
    }
    
}
