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

package org.netbeans.modules.vcscore.cmdline;

import java.text.*;
import java.util.*;

import org.openide.TopManager;
import org.openide.util.*;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.cache.CacheFile;
import org.netbeans.modules.vcscore.caching.VcsCacheFile;
import org.netbeans.modules.vcscore.caching.VcsCacheDir;
import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

/**
 * Read VCS directory recursively.
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsDirReaderRecursive implements VcsCommandExecutor {
    private Debug E=new Debug("CommandLineVcsDirReaderRecursive", true); // NOI18N
    private Debug D=E;

    private VcsFileSystem fileSystem = null;
    private UserCommand listSub = null;
    private Hashtable vars = null;
    private VcsCacheDir dir = null;
    private String path = null;

    private boolean shouldFail=false;
    private int exitStatus;

    private VcsDirContainer rawData = null;

    private DirReaderListener listener = null;


    /** Creates new CommandLineVcsDirReaderRecursive */
    public CommandLineVcsDirReaderRecursive(DirReaderListener listener, VcsFileSystem fileSystem,
                                            UserCommand listSub, Hashtable vars) {
        this.listener = listener;
        this.fileSystem = fileSystem;
        this.listSub = listSub;
        this.vars = vars;
        String path = (String)vars.get("DIR"); // NOI18N
        this.path = path;
        D.deb ("DIR="+(String)vars.get("DIR")); // NOI18N
        //dir = new VcsDir();
        path = path.replace (java.io.File.separatorChar, '/');
        dir = new VcsCacheDir(fileSystem.getCacheIdStr(), fileSystem.getFile(path));
        //dir.setPath (path);
        //dir.setName(MiscStuff.getFileNamePart(path));
        //if (path.length() == 0) vars.put("DIR", "."); // NOI18N
        D.deb("DIR="+(String)vars.get("DIR")); // NOI18N
    }

    /**
     * The executed command.
     */
    public VcsCommand getCommand() {
        return listSub;
    }

    /**
     * This method can be used to do some preprocessing of the command which is to be run.
     * The method is called before the prompt for user input is made and therefore can be used to
     * additionally specify the desired input.
     * @param vc the command to be preprocessed.
     * @param vars the variables
     * @return the updated exec property
     */
    public String preprocessCommand(VcsCommand vc, Hashtable vars) {
        return (String) listSub.getProperty(VcsCommand.PROPERTY_EXEC);
    }

    /**
     * Update the execution string. It may contain user input now.
     * @param exec the execution string updated with user input.
     */
    public void updateExec(String exec) {
    }
    
    /**
     * Get the set of files being processed by the command.
     * @return the set of files of type <code>String</code>
     */
    public Set getFiles() {
        String file = (String) vars.get("FILE");
        HashSet set = new HashSet(1);
        set.add(file);
        return set;
    }
    
    private void runCommand(String exec, OutputContainer container) {
        fileSystem.debug("LIST_SUB: "+g("MSG_List_command_failed")+"\n"); // NOI18N
        exitStatus = VcsCommandExecutor.FAILED;
        shouldFail=true ;
    }

    private void runClass(String className, StringTokenizer tokens, OutputContainer container) {
        E.deb("runClass: "+className); // NOI18N
        E.deb("Creating new CvsListCommand"); // NOI18N
        Class listClass = null;
        try {
            listClass =  Class.forName(className, true,
                                       org.openide.TopManager.getDefault().currentClassLoader());
        } catch (ClassNotFoundException e) {
            fileSystem.debug ("LIST_SUB: "+g("ERR_ClassNotFound", className)); // NOI18N
            container.match("LIST_SUB: "+g("ERR_ClassNotFound", className)); // NOI18N
            shouldFail = true;
            return;
        }
        E.deb(listClass+" loaded"); // NOI18N
        VcsListRecursiveCommand listCommand = null;
        try {
            listCommand = (VcsListRecursiveCommand) listClass.newInstance();
        } catch (InstantiationException e) {
            fileSystem.debug ("LIST_SUB: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
            container.match("LIST_SUB: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
            shouldFail = true;
            return;
        } catch (IllegalAccessException e) {
            fileSystem.debug ("LIST_SUB: "+g("ERR_IllegalAccessOnClass", listClass)); // NOI18N
            container.match(g("LIST_SUB: "+"ERR_IllegalAccessOnClass", listClass)); // NOI18N
            shouldFail = true;
            return;
        }
        E.deb("VcsListCommand created."); // NOI18N
        String[] args = new String[tokens.countTokens()];
        int i = 0;
        while(tokens.hasMoreTokens()) {
            args[i++] = tokens.nextToken();
        }

        VcsDirContainer filesByName = new VcsDirContainer(path);
        if (!shouldFail) {
            vars.put("DATAREGEX", (String) listSub.getProperty(UserCommand.PROPERTY_DATA_REGEX)); // NOI18N
            vars.put("ERRORREGEX", (String) listSub.getProperty(UserCommand.PROPERTY_ERROR_REGEX)); // NOI18N
            vars.put("INPUT", (String) listSub.getProperty(UserCommand.PROPERTY_INPUT)); // NOI18N
            //vars.put("TIMEOUT", new Long(listSub.getTimeout())); // NOI18N
            TopManager.getDefault().setStatusText(g("MSG_Command_name_running", listSub.getName()));
            shouldFail = !listCommand.listRecursively(vars, args, filesByName, container, container,
                         null, (String) listSub.getProperty(UserCommand.PROPERTY_DATA_REGEX),
                         new RegexListener () {
                             public void match(String[] elements){
                                 //D.deb("stderr match:"+MiscStuff.arrayToString(elements)); // NOI18N
                                 fileSystem.debug("stderr: "+MiscStuff.arrayToString(elements)); // NOI18N
                                 //shouldFail=true ;
                             }
                         }, (String) listSub.getProperty(UserCommand.PROPERTY_ERROR_REGEX));
            //E.deb("shouldFail = "+shouldFail+" after list with "+filesByName.size()+" elements"); // NOI18N
            /*
            for(Enumeration e = filesByName.keys(); e.hasMoreElements() ;) {
              String fileName=(String)e.nextElement();
              String fileStatus=(String)filesByName.get(fileName);
              E.deb("filesByName: "+fileName+" | "+fileStatus);
        }
            */
        }
        rawData = new VcsDirContainer();
        putFilesToDirRecursively(dir, filesByName, rawData);
        if (shouldFail) {
            exitStatus = VcsCommandExecutor.FAILED;
            fileSystem.debug("LIST_SUB: "+g("MSG_List_command_failed")+"\n"); // NOI18N
            container.match("LIST_SUB: "+g("MSG_List_command_failed")); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_failed", listSub.getName()));
        } else {
            exitStatus = VcsCommandExecutor.SUCCEEDED;
            fileSystem.debug("LIST_SUB: "+g("MSG_Command_succeeded")+"\n"); // NOI18N
            container.match("LIST_SUB: "+g("MSG_Command_succeeded")); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_succeeded", listSub.getName()));
        }

    }

    private void putFilesToDirRecursively(VcsCacheDir dir, VcsDirContainer filesByName,
                                              VcsDirContainer rawData) {
        D.deb("putFilesToDirRecursively("+filesByName.getPath()+")");
        if (dir == null || filesByName == null) return;
        if (rawData.getElement() == null) rawData.setElement(new Vector());
        putFilesToDir(dir, (Hashtable) filesByName.getElement(), (Vector) rawData.getElement());
        D.deb("putFilesToDirRecursively: dir = "+dir);
        String[] subdirs = filesByName.getSubdirs();
        D.deb("subdirs = "+MiscStuff.array2string(subdirs));
        for(int i = 0; i < subdirs.length; i++) {
            VcsDirContainer subFilesByName = filesByName.getDirContainer(subdirs[i]);
            String path = subFilesByName.getPath();
            VcsCacheDir subdir = (VcsCacheDir) dir.getSubDir(subdirs[i]);
            if (subdir == null) {
                D.deb("subdir "+subdirs[i]+" does not exist in dir = "+dir);
                subdir = new VcsCacheDir(dir.getCacheName(), new java.io.File(dir.getAbsolutePath() + java.io.File.separator + subdirs[i]));
                dir.addChildDir(subdir, true);
            }
            //subdir.setPath(path);
            D.deb("subdir path = "+path);
            VcsDirContainer subRawData = rawData.addSubdir(path);
            putFilesToDirRecursively(subdir, subFilesByName, subRawData);
            D.deb("putFilesToDirRecursively("+filesByName.getPath()+") after adding "+subdir+"\n\t\t dir = "+dir);
        }
    }

    private void putFilesToDir(VcsCacheDir dir, Hashtable filesByName, Vector rawData) {
        if (filesByName == null) return;
        java.io.File parent = new java.io.File(dir.getAbsolutePath());
        for(Enumeration e = filesByName.keys(); e.hasMoreElements() ;) {
            String fileName = (String)e.nextElement();
            String[] elements = (String[])filesByName.get(fileName);
            //elements[0] = fileName;
            //elements[1] = fileStatus;
            //E.deb("Processing: "+fileName+"|"+elements); // NOI18N
            //fileSystem.debug("stdout: "+MiscStuff.arrayToString(elements)); // NOI18N
            rawData.addElement(elements);
            CacheFile file = CommandLineVcsDirReader.matchToFile(elements, listSub, fileSystem.getPossibleFileStatusesTable(), fileSystem.getCacheIdStr(), parent);
            if(file instanceof VcsCacheDir) {
                //String parent = dir.getPath ();
                //((VcsDir)file).setPath (((parent.length() > 0) ? parent + "/" : "") + file.getName ()); // NOI18N
                ((VcsCacheDir) file).setLoaded(false);
            }
            //D.deb("adding file="+file); // NOI18N
            dir.add(file);
        }
    }

    public void run() {
        String exec = (String) listSub.getProperty(VcsCommand.PROPERTY_EXEC);
        exec = Variables.expand(vars, exec, true).trim();
        fileSystem.debug("LIST_SUB: "+exec); // NOI18N

        ErrorCommandDialog errDlg = fileSystem.getErrorDialog(); //new ErrorCommandDialog(list, new JFrame(), false);
        OutputContainer container = new OutputContainer(listSub);
        container.match("LIST_SUB: "+exec); // NOI18N

        StringTokenizer tokens = new StringTokenizer(exec);
        String first = tokens.nextToken();
        E.deb("first = "+first); // NOI18N
        if (first != null && (first.toLowerCase().endsWith(".class"))) { // NOI18N
            runClass(first.substring(0, first.length() - ".class".length()), tokens, container); // NOI18N
        } else
            runCommand(exec, container);

        if(shouldFail){
            errDlg.putCommandOut(container.getMessages());
            errDlg.showDialog();
            fileSystem.setPassword(null);
            fileSystem.debug(g("ERR_LISTFailed")); // NOI18N
            D.deb("failed reading of dir="+dir); // NOI18N
            if(!dir.getName ().equals("")) { // NOI18N
                dir.setStatus (g("MSG_VCS_command_failed")); // NOI18N
            }
            dir.setLoadedRecursive(true); // failed, but loaded
            listener.readDirFinishedRecursive(dir, rawData, !shouldFail);
        }
        else{
            //errDlg.removeCommandOut();
            //errDlg.cancelDialog();
            //fileSystem.debug("LIST command finished successfully"); // NOI18N
            dir.setLoadedRecursive(true);
            listener.readDirFinishedRecursive(dir, rawData, !shouldFail);
        }

        // After refresh I should ensure, that the next automatic refresh will work if something happens in numbering
        fileSystem.removeNumDoAutoRefresh((String)vars.get("DIR")); // NOI18N
        //D.deb("run(LIST) '"+dir.name+"' finished"); // NOI18N
    }

    /**
     * Get the exit status of the execution.
     * @return the exit value, it may be one of {@link SUCCEEDED}, {@link FAILED}, {@link INTERRUPTED}.
     */
    public int getExitStatus() {
        return exitStatus;
    }

    String g(String s) {
        return NbBundle.getBundle
               ("org.netbeans.modules.vcscore.cmdline.Bundle").getString (s);
    }
    String  g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
    String g(String s, Object obj1, Object obj2) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
    }
    String g(String s, Object obj1, Object obj2, Object obj3) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
    }
}
