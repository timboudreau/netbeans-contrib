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

package org.netbeans.modules.vcs.advanced;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;
import javax.swing.*;

import org.openide.util.actions.*;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystem.Status;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.DefaultAttributes;

import org.netbeans.modules.vcs.*;
import org.netbeans.modules.vcs.cmdline.UserCommand;
import org.netbeans.modules.vcs.util.*;

/** Generic command line VCS filesystem.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class CommandLineVcsFileSystem extends VcsFileSystem implements java.beans.PropertyChangeListener {
    
    private static final boolean DEFAULT_LOCAL_FILE_FILTER_CASE_SENSITIVE = true;
    
    private Debug D = new Debug ("CommandLineVcsFileSystem", true); // NOI18N
    private /*static transient*/ String CONFIG_ROOT="vcs/config"; // NOI18N
    private FileObject CONFIG_ROOT_FO;
    private transient Hashtable commandsByName=null;
    private Hashtable additionalPossibleFileStatusesTable = null;
    private Vector localFilesFilteredOut = null;
    private boolean localFileFilterCaseSensitive = DEFAULT_LOCAL_FILE_FILTER_CASE_SENSITIVE;
    private Vector docCleanupRemoveItems = null;

    static final long serialVersionUID =-1017235664394970926L;
    //-------------------------------------------
    public CommandLineVcsFileSystem () {
        //D.deb("CommandLineVcsFileSystem()"); // NOI18N
        super ();
        readConfiguration ();
        addPropertyChangeListener(this);
    }

    public VcsFactory getVcsFactory () {
        return new CommandLineVcsFactory ();
    }

    //-------------------------------------------
    public String getConfigRoot(){
        return CONFIG_ROOT;
    }

    public void setConfigRoot(String s) {
        CONFIG_ROOT = s;
    }

    public FileObject getConfigRootFO() {
        return CONFIG_ROOT_FO;
    }

    protected void readConfiguration () {
        D.deb ("readConfiguration ()"); // NOI18N
        CONFIG_ROOT=System.getProperty("netbeans.user")+File.separator+
                    "system"+File.separator+"vcs"+File.separator+"config"; // NOI18N
        CONFIG_ROOT = "vcs"+File.separator+"config"; // NOI18N
        CONFIG_ROOT_FO = TopManager.getDefault ().getRepository ().getDefaultFileSystem ().getRoot ();
        CONFIG_ROOT_FO = CONFIG_ROOT_FO.getFileObject("vcs");
        CONFIG_ROOT_FO = CONFIG_ROOT_FO.getFileObject("config");
        //Properties props=VcsConfigVariable.readPredefinedPropertiesIO(CONFIG_ROOT+File.separator+"empty.properties"); // NOI18N
        Properties props = VcsConfigVariable.readPredefinedProperties(CONFIG_ROOT_FO, "empty.properties"); // NOI18N
        setVariables (VcsConfigVariable.readVariables(props));
        D.deb("setVariables DONE."); // NOI18N
        setAdvancedConfig (getVcsFactory ().getVcsAdvancedCustomizer().readConfig (props));
        D.deb("readConfiguration() done"); // NOI18N
    }

    /**
     * Allows some cleanup of the document which the user is asked for.
     * doc The Document
     * promptNum the order of the document
     * docIdentif some identification that can be set in settting the listener.
     */
    public void filePromptDocumentCleanup(javax.swing.JTextArea ta, int promptNum, Object docIdentif) {
        // Let the document unchanged by default
        javax.swing.text.Document doc = ta.getDocument();
        if (docIdentif instanceof UserCommand) {
            UserCommand cmd = (UserCommand) docIdentif;
            if (docCleanupRemoveItems != null) {
                for(int i = 0; i < docCleanupRemoveItems.size(); i++) {
                    CommandLineVcsFileSystem.DocCleanupRemoveItem item = (CommandLineVcsFileSystem.DocCleanupRemoveItem) docCleanupRemoveItems.get(i);
                    if (cmd.getName().equals(item.getCmdName()) && promptNum == item.getOrder()) {
                        String lineBegin = item.getLineBegin();
                        for(int line = 0; line < ta.getLineCount(); line++) {
                            try {
                                int begin = ta.getLineStartOffset(line);
                                int end = ta.getLineEndOffset(line);
                                String lineStr = doc.getText(begin, end - begin);
                                if (lineStr.regionMatches(0, lineBegin, 0, lineBegin.length())) {
                                    doc.remove(begin, end - begin);
                                    line--;
                                }
                            } catch (javax.swing.text.BadLocationException exc) {
                                org.openide.TopManager.getDefault().notifyException(exc);
                            }
                        }
                    }
                }
            }
        }
    }

    public void propertyChange (PropertyChangeEvent evt) {
        if (evt.getPropertyName() != FileSystem.PROP_VALID) return;
        if (isValid()) {
            D.deb("Filesystem added to the repository, setting refresh time to "+refreshTimeToSet); // NOI18N
            setRefreshTime(refreshTimeToSet);
            warnDirectoriesDoNotExists();
        } else {
            D.deb("Filesystem is not valid any more, setting refresh time to 0"); // NOI18N
            setRefreshTime(0);
        }
    }
    
    private void setPossibleFileStatusesFromVars() {
        VcsConfigVariable varStatuses = (VcsConfigVariable) variablesByName.get ("POSSIBLE_FILE_STATUSES"); // NOI18N
        VcsConfigVariable varStatusesLclz = (VcsConfigVariable) variablesByName.get ("POSSIBLE_FILE_STATUSES_LOCALIZED"); // NOI18N
        if (additionalPossibleFileStatusesTable != null) MiscStuff.removeKeys(possibleFileStatusesTable, additionalPossibleFileStatusesTable);
        additionalPossibleFileStatusesTable = null;
        if (varStatuses != null) {
            additionalPossibleFileStatusesTable = new Hashtable();
            String[] possStatuses = MiscStuff.getQuotedStrings(varStatuses.getValue());
            String[] possStatusesLclz = null;
            if (varStatusesLclz != null) possStatusesLclz = MiscStuff.getQuotedStrings(varStatusesLclz.getValue());
            int i = 0;
            if (possStatusesLclz != null) {
                for(; i < possStatuses.length && i < possStatusesLclz.length; i++) {
                    additionalPossibleFileStatusesTable.put(possStatuses[i], possStatusesLclz[i]);
                }
            }
            for(; i < possStatuses.length; i++) {
                additionalPossibleFileStatusesTable.put(possStatuses[i], possStatuses[i]);
            }
            possibleFileStatusesTable.putAll(additionalPossibleFileStatusesTable);
        }
    }
    
    private void setLocalFileFilterFromVars() {
        VcsConfigVariable varLocalFilter = (VcsConfigVariable) variablesByName.get("LOCAL_FILES_FILTERED_OUT");
        VcsConfigVariable varLocalFilterCS = (VcsConfigVariable) variablesByName.get("LOCAL_FILES_FILTERED_OUT_CASE_SENSITIVE");
        if (varLocalFilter != null) {
            if (varLocalFilterCS != null) {
                localFileFilterCaseSensitive = varLocalFilterCS.getValue().equalsIgnoreCase("true");
            }
            String[] files = MiscStuff.getQuotedStrings(varLocalFilter.getValue());
            localFilesFilteredOut = new Vector(Arrays.asList(files));
        } else localFilesFilteredOut = null;
    }
    
    private void setDocumentCleanupFromVars() {
        VcsConfigVariable docCleanupRemove;
        docCleanupRemoveItems = null;
        for(int i = 1; (docCleanupRemove = (VcsConfigVariable) variablesByName.get("DOCUMENT_CLEANUP_REMOVE"+i)) != null; i++) {
            String[] removeWhat = MiscStuff.getQuotedStrings(docCleanupRemove.getValue());
            if (removeWhat.length < 3) continue;
            int order = 0;
            try {
                order = Integer.parseInt(removeWhat[1]);
                order--;
            } catch (NumberFormatException exc) {
                org.openide.TopManager.getDefault().notifyException(exc);
                continue;
            }
            CommandLineVcsFileSystem.DocCleanupRemoveItem item = new CommandLineVcsFileSystem.DocCleanupRemoveItem(removeWhat[0], order, removeWhat[2]);
            if (docCleanupRemoveItems == null) docCleanupRemoveItems = new Vector();
            docCleanupRemoveItems.add(item);
        }
    }
    
    public void setVariables(Vector variables){
        super.setVariables(variables);
        setPossibleFileStatusesFromVars();
        setLocalFileFilterFromVars();
        setDocumentCleanupFromVars();
    }

    public FilenameFilter getLocalFileFilter() {
        return new FilenameFilter() {
                   public boolean accept(File dir, String name) {
                       if (!localFileFilterCaseSensitive) name = name.toUpperCase();
                       return !localFilesFilteredOut.contains(name);
                       //return !name.equalsIgnoreCase("CVS"); // NOI18N
                   }
               };
    }

    /*
    protected String g(String s) {
      return NbBundle.getBundle
        ("org.netbeans.modules.vcs.cmdline.BundleCVS").getString (s);
}
    */
    
    private class DocCleanupRemoveItem implements Serializable {
        
        private String cmdName;
        private int order;
        private String lineBegin;
        
        static final long serialVersionUID =-1259352637936409072L;
        /**
         * Create new cleanup remove item.
         * @param cmdName The name of the command.
         * @param order the order of JTextArea in the VariableInputDialog.
         * @param lineBegin the beginning of lines which will be removed.
         */
        public DocCleanupRemoveItem(String cmdName, int order, String lineBegin) {
            this.cmdName = cmdName;
            this.order = order;
            this.lineBegin = lineBegin;
        }
        
        public String getCmdName() {
            return cmdName;
        }
        
        public int getOrder() {
            return order;
        }
        
        public String getLineBegin() {
            return lineBegin;
        }
    }
}

/*
 * <<Log>>
 *  57   Jaga      1.55.1.0    2/24/00  Martin Entlicher Read configuration from 
 *       filesystem.
 *  56   Gandalf   1.55        2/11/00  Martin Entlicher 
 *  55   Gandalf   1.54        2/10/00  Martin Entlicher Warning of nonexistent 
 *       directories called when mounted.
 *  54   Gandalf   1.53        1/27/00  Martin Entlicher NOI18N
 *  53   Gandalf   1.52        1/3/00   Martin Entlicher 
 *  52   Gandalf   1.51        12/28/99 Martin Entlicher Yury changes.
 *  51   Gandalf   1.50        12/21/99 Martin Entlicher Refresh time set after 
 *       mounting into the Repository.
 *  50   Gandalf   1.49        11/30/99 Martin Entlicher 
 *  49   Gandalf   1.48        11/27/99 Patrik Knakal   
 *  48   Gandalf   1.47        11/23/99 Martin Entlicher 
 *  47   Gandalf   1.46        10/25/99 Pavel Buzek     copyright
 *  46   Gandalf   1.45        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  45   Gandalf   1.44        10/10/99 Pavel Buzek     
 *  44   Gandalf   1.43        10/9/99  Pavel Buzek     
 *  43   Gandalf   1.42        10/9/99  Pavel Buzek     
 *  42   Gandalf   1.41        10/5/99  Pavel Buzek     
 *  41   Gandalf   1.40        9/30/99  Pavel Buzek     
 *  40   Gandalf   1.39        9/13/99  Pavel Buzek     
 *  39   Gandalf   1.38        9/10/99  Martin Entlicher removed import regexp
 *  38   Gandalf   1.37        9/8/99   Pavel Buzek     
 *  37   Gandalf   1.36        9/8/99   Pavel Buzek     class model changed, 
 *       customization improved, several bugs fixed
 *  36   Gandalf   1.35        8/31/99  Pavel Buzek     
 *  35   Gandalf   1.34        8/31/99  Pavel Buzek     
 *  34   Gandalf   1.33        8/7/99   Ian Formanek    Martin Entlicher's 
 *       improvements
 *  33   Gandalf   1.32        6/10/99  Michal Fadljevic 
 *  32   Gandalf   1.31        6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  31   Gandalf   1.30        6/8/99   Michal Fadljevic 
 *  30   Gandalf   1.29        6/4/99   Michal Fadljevic 
 *  29   Gandalf   1.28        6/1/99   Michal Fadljevic 
 *  28   Gandalf   1.27        6/1/99   Michal Fadljevic 
 *  27   Gandalf   1.26        5/27/99  Michal Fadljevic 
 *  26   Gandalf   1.25        5/27/99  Michal Fadljevic 
 *  25   Gandalf   1.24        5/25/99  Michal Fadljevic 
 *  24   Gandalf   1.23        5/25/99  Michal Fadljevic 
 *  23   Gandalf   1.22        5/24/99  Michal Fadljevic 
 *  22   Gandalf   1.21        5/24/99  Michal Fadljevic 
 *  21   Gandalf   1.20        5/21/99  Michal Fadljevic 
 *  20   Gandalf   1.19        5/21/99  Michal Fadljevic 
 *  19   Gandalf   1.18        5/21/99  Michal Fadljevic 
 *  18   Gandalf   1.17        5/19/99  Michal Fadljevic 
 *  17   Gandalf   1.16        5/18/99  Michal Fadljevic 
 *  16   Gandalf   1.15        5/14/99  Michal Fadljevic 
 *  15   Gandalf   1.14        5/13/99  Michal Fadljevic 
 *  14   Gandalf   1.13        5/11/99  Michal Fadljevic 
 *  13   Gandalf   1.12        5/7/99   Michal Fadljevic 
 *  12   Gandalf   1.11        5/6/99   Michal Fadljevic 
 *  11   Gandalf   1.10        5/4/99   Michal Fadljevic 
 *  10   Gandalf   1.9         5/4/99   Michal Fadljevic 
 *  9    Gandalf   1.8         4/29/99  Michal Fadljevic 
 *  8    Gandalf   1.7         4/28/99  Michal Fadljevic 
 *  7    Gandalf   1.6         4/27/99  Michal Fadljevic 
 *  6    Gandalf   1.5         4/26/99  Michal Fadljevic 
 *  5    Gandalf   1.4         4/22/99  Michal Fadljevic 
 *  4    Gandalf   1.3         4/22/99  Michal Fadljevic 
 *  3    Gandalf   1.2         4/22/99  Michal Fadljevic 
 *  2    Gandalf   1.1         4/21/99  Michal Fadljevic 
 *  1    Gandalf   1.0         4/15/99  Michal Fadljevic 
 * $
 */

