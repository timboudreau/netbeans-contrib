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

import org.openide.ErrorManager;
import org.openide.TopManager;
import org.openide.util.*;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.cache.CacheFile;
import org.netbeans.modules.vcscore.caching.VcsCacheFile;
import org.netbeans.modules.vcscore.caching.VcsCacheDir;
import org.netbeans.modules.vcscore.caching.RefreshCommandSupport;
import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.commands.*;

/**
 * Read VCS directory recursively.
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsDirReaderRecursive extends ExecuteCommand {
    private Debug E=new Debug("CommandLineVcsDirReaderRecursive", true); // NOI18N
    private Debug D=E;

    private String path = null;

    private VcsDirContainer rawData = null;

    private DirReaderListener listener = null;


    /** Creates new CommandLineVcsDirReaderRecursive */
    public CommandLineVcsDirReaderRecursive(DirReaderListener listener, VcsFileSystem fileSystem,
                                            UserCommand listSub, Hashtable vars) {
        super(fileSystem, listSub, vars);
        this.listener = listener;
        this.path = (String)vars.get("DIR"); // NOI18N
        D.deb ("DIR="+(String)vars.get("DIR")); // NOI18N
        //dir = new VcsDir();
        path = path.replace (java.io.File.separatorChar, '/');
        //path = fileSystem.getFile(path).getAbsolutePath().replace(java.io.File.separatorChar, '/');
        //dir = new VcsCacheDir(fileSystem.getCacheIdStr(), fileSystem.getFile(path));
        //dir.setPath (path);
        //dir.setName(VcsUtilities.getFileNamePart(path));
        //if (path.length() == 0) vars.put("DIR", "."); // NOI18N
        D.deb("DIR="+(String)vars.get("DIR")); // NOI18N
    }

    /**
     * Get the graphical visualization of the command.
     * @return null no visualization is desired.
     */
    public VcsCommandVisualizer getVisualizer() {
        return null;
    }
    
    /**
     * Get the set of files being processed by the command.
     * @return the set of files of type <code>String</code> relative
     * to the file system root.
     *
    public Collection getFiles() {
        String path = (String) vars.get("DIR"); // NOI18N
        String file = (String) vars.get("FILE"); // NOI18N
        String fullPath = ((path.length() > 0) ? path.replace(java.io.File.separatorChar, '/') : "") + ((file == null) ? "" : "/" + file); // NOI18N
        return Collections.singleton(fullPath);
    }
     */
    
    /*
     * Get the path of the processed files.
     * The path is relative to file system root.
     *
    public String getPath() {
        return (String) vars.get("DIR");
    }
     */
    
    /**
     * The runCommand() method not supported. This method cause the command to always fail.
     */
    protected void runCommand(String[] execs) {
        //fileSystem.debug("LIST_SUB: "+g("MSG_List_command_failed")+"\n"); // NOI18N
        printErrorOutput("Recursive Command can not execute the command. "+
                         "Please supply a class of instance of VcsListRecursiveCommand."); // NOI18N
        printErrorOutput("LIST_SUB: "+NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class,
                         "MSG_List_command_failed")+"\n"); // NOI18N
        exitStatus = VcsCommandExecutor.FAILED;
    }

    /**
     * Loads class of given name with some arguments and execute its list() method.
     * @param className the name of the class to be loaded
     * @param args the arguments
     */
    protected void runClass(String exec, String className, String[] args) {
        E.deb("runClass: "+className); // NOI18N
        E.deb("Creating new CvsListCommand"); // NOI18N
        boolean success = true;
        Class listClass = null;
        try {
            listClass =  Class.forName(className, true,
                                       org.openide.TopManager.getDefault().currentClassLoader());
        } catch (ClassNotFoundException e) {
            //fileSystem.debug ("LIST_SUB: "+g("ERR_ClassNotFound", className)); // NOI18N
            //container.match("LIST_SUB: "+g("ERR_ClassNotFound", className)); // NOI18N
            printErrorOutput("LIST_SUB: "+NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class, // NOI18N
                             "ERR_ClassNotFound", className)); // NOI18N
            success = false;
        }
        E.deb(listClass+" loaded"); // NOI18N
        VcsListRecursiveCommand listCommand = null;
        try {
            listCommand = (VcsListRecursiveCommand) listClass.newInstance();
        } catch (InstantiationException e) {
            //fileSystem.debug ("LIST_SUB: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
            //container.match("LIST_SUB: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
            printErrorOutput("LIST_SUB: "+NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class, // NOI18N
                             "ERR_CanNotInstantiate", listClass)); // NOI18N
            success = false;
        } catch (IllegalAccessException e) {
            //fileSystem.debug ("LIST_SUB: "+g("ERR_IllegalAccessOnClass", listClass)); // NOI18N
            //container.match(g("LIST_SUB: "+"ERR_IllegalAccessOnClass", listClass)); // NOI18N
            printErrorOutput(NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class,
                             "LIST_SUB: "+"ERR_IllegalAccessOnClass", listClass)); // NOI18N
            success = false;
        }
        E.deb("VcsListCommand created."); // NOI18N
        VcsDirContainer filesByName = new VcsDirContainer(path);
        UserCommand listSub = (UserCommand) getCommand();
        if (success) {
            Hashtable vars = getVariables();
            ExecuteCommand.setAdditionalParams(listCommand, getFileSystem());
            String dataRegex = (String) listSub.getProperty(UserCommand.PROPERTY_DATA_REGEX);
            if (dataRegex == null) dataRegex = ExecuteCommand.DEFAULT_REGEX;
            vars.put("DATAREGEX", dataRegex); // NOI18N
            String errorRegex = (String) listSub.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
            if (errorRegex == null) errorRegex = ExecuteCommand.DEFAULT_REGEX;
            vars.put("ERRORREGEX", errorRegex); // NOI18N
            String input = (String) listSub.getProperty(UserCommand.PROPERTY_INPUT);
            if (input != null) vars.put("INPUT", input); // NOI18N
            //vars.put("TIMEOUT", new Long(listSub.getTimeout())); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_running", listSub.getName()));
            try {
                success = listCommand.listRecursively(vars, args, filesByName,
                                           new CommandOutputListener() {
                                               public void outputLine(String line) {
                                                   printOutput(line);
                                               }
                                           },
                                           new CommandOutputListener() {
                                               public void outputLine(String line) {
                                                   printErrorOutput(line);
                                               }
                                           },
                                           new CommandDataOutputListener() {
                                               public void outputData(String[] data) {
                                                   printDataOutput(data);
                                               }
                                           }, (String) listSub.getProperty(UserCommand.PROPERTY_DATA_REGEX),
                                           new CommandDataOutputListener() {
                                               public void outputData(String[] data) {
                                                   printDataErrorOutput(data);
                                               }
                                           }, (String) listSub.getProperty(UserCommand.PROPERTY_ERROR_REGEX)
                                          );
            //E.deb("shouldFail = "+shouldFail+" after list with "+filesByName.size()+" elements"); // NOI18N
            /*
            for(Enumeration e = filesByName.keys(); e.hasMoreElements() ;) {
              String fileName=(String)e.nextElement();
              String fileStatus=(String)filesByName.get(fileName);
              E.deb("filesByName: "+fileName+" | "+fileStatus);
        }
            */
            } catch (ThreadDeath td) {
                throw td; // re-throw the ThreadDeath
            } catch (Throwable thr) { // Something bad has happened in the called class!
                success = false;
                ErrorManager.getDefault().notify(
                    ErrorManager.getDefault().annotate(thr,
                        NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class, "ERR_EXC_IN_CLASS", className)));
            }
        }
        rawData = filesByName;
        //rawData = new VcsDirContainer();
        translateElementsRecursively(rawData, listSub);
        //putFilesToDirRecursively(dir, filesByName, rawData);
        exitStatus = (success) ? VcsCommandExecutor.SUCCEEDED : VcsCommandExecutor.FAILED;
    }
    
    private void translateElementsRecursively(VcsDirContainer rawData, UserCommand listSub) {
        Hashtable filesByName = (Hashtable) rawData.getElement();
        if (filesByName != null) {
            Hashtable filesByNameTranslated = new Hashtable();
            for (Enumeration enum = filesByName.keys(); enum.hasMoreElements(); ) {
                String name = (String) enum.nextElement();
                String[] elements = (String[]) filesByName.get(name);
                elements = CommandLineVcsDirReader.translateElements(elements, listSub);
                filesByNameTranslated.put(name, elements);
            }
            rawData.setElement(filesByNameTranslated);
        }
        VcsDirContainer[] subdirs = rawData.getSubdirContainers();
        for(int i = 0; i < subdirs.length; i++) {
            translateElementsRecursively(subdirs[i], listSub);
        }
    }

    public void run() {
        Hashtable vars = getVariables();
        String commonParent = (String) vars.get("COMMON_PARENT");
        String dir = (String) vars.get("DIR"); // NOI18N
        if (commonParent != null && commonParent.length() > 0) {
            dir = commonParent + Variables.expand(vars, "${PS}", false) + dir;
        }
        String path = dir.replace (java.io.File.separatorChar, '/');
        String exec = getExec();
        if (exec == null || exec.trim().length() == 0) {
            //String dirName = (((String) vars.get("DIR"))).replace(((String) vars.get("PS")).charAt(0), '/');
            RetrievingDialog rd = new RetrievingDialog(getFileSystem(), path, new javax.swing.JFrame(), false);
            VcsUtilities.centerWindow(rd);
            rd.run();
            exitStatus = VcsCommandExecutor.SUCCEEDED;
            return ;
        } else {
            try {
                super.run();
            } finally {
                int lastSlash = path.lastIndexOf('/');
                if (lastSlash > 0) {
                    path = path.substring(0, lastSlash);
                }
                listener.readDirFinishedRecursive(path, rawData, getExitStatus() == VcsCommandExecutor.SUCCEEDED);
                // After refresh I should ensure, that the next automatic refresh will work if something happens in numbering
                getFileSystem().removeNumDoAutoRefresh(dir); // NOI18N
                //D.deb("run(LIST) '"+dir.name+"' finished"); // NOI18N
            }
        }
    }

    /**
     * Add a file reader listener, that gets the updated attributes of the
     * processed file(s). <p>
     * This is an empty method, the listener is added nowhere. This class uses
     * the passed listener for the notification.
     */
    public void addFileReaderListener(FileReaderListener l) {
    }

}
