/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.cmdline;

import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;
import java.net.URL;
import java.net.URLClassLoader;

import org.openide.ErrorManager;
import org.openide.util.*;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.cache.CacheFile;
import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.caching.VcsCacheFile;
import org.netbeans.modules.vcscore.caching.VcsCacheDir;
import org.netbeans.modules.vcscore.caching.RefreshCommandSupport;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.cmdline.VcsListCommand;

/**
 * Read a single VCS directory.
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public class CommandLineVcsDirReader extends ExecuteCommand {
    private Debug E=new Debug("CommandLineVcsDirReader", true); // NOI18N
    private Debug D=E;

    private String path;

    private List rawData = new ArrayList();
    private boolean classRunning = false;

    private DirReaderListener listener = null ;


    //-------------------------------------------
    public CommandLineVcsDirReader(DirReaderListener listener, VcsFileSystem fileSystem,
                                   UserCommand list, Hashtable vars) {
        super(fileSystem, list, vars);
        //super("VCS-DirReader-"+((String)vars.get("DIR"))); // NOI18N
        this.listener = listener;
        path = (String)vars.get("DIR"); // NOI18N
        D.deb ("DIR="+(String)vars.get("DIR")); // NOI18N
        //System.out.println("CommandLineVcsDirReader(): DIR="+(String)vars.get("DIR")); // NOI18N
        //dir = new VcsCacheDir();
        path = path.replace (java.io.File.separatorChar, '/');
        path = fileSystem.getFile(path).getAbsolutePath().replace(java.io.File.separatorChar, '/');
        //System.out.println("CommandLineVcsDirReader(): path = "+path);
        /*
        dir = (VcsCacheDir) fileSystem.getCache().getDir(path);
        if (dir == null) {
            //dir = new VcsCacheDir(fileSystem.getCacheIdStr(), fileSystem.getFile(path));
            dir = (VcsCacheDir) CacheHandler.getInstance().getCacheFile(
                    fileSystem.getFile(path),
                    CacheHandler.STRAT_TEMP_DISK, fileSystem.getCacheIdStr());
        }
         */
        //dir.setPath (path);
        //dir.setName (VcsUtilities.getFileNamePart (path));
        //if (path.length() == 0) vars.put("DIR", "."); // NOI18N
        D.deb ("DIR="+(String)vars.get("DIR")); // NOI18N
        //this.exec = (String) list.getProperty(VcsCommand.PROPERTY_EXEC);
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
        String path = (String) vars.get("DIR");
        String file = (String) vars.get("FILE");
        String fullPath = ((path.length() > 0) ? path.replace(File.separatorChar, '/') : "") + ((file == null) ? "" : "/" + file);
        return Collections.singleton(fullPath);
        //HashSet set = new HashSet(1);
        //set.add(file);
        //return set;
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
    
    //-------------------------------------------
    public List getRawData(){
        return rawData;
    }

    protected void printDataOutput(String[] data) {
        super.printDataOutput(data);
        if (!classRunning) {
            // Do not add the data here for the class command!
            // They should be passed through filesByName table instead.
            // Command-line commands however pass the data through data output
            data = translateElements(data, (UserCommand) getCommand());
            rawData.add(data);
        }
    }

    /**
     * Loads class of given name with some arguments and execute its list() method.
     * @param className the name of the class to be loaded
     * @param args the arguments
     */
    protected void runClass(String exec, String className, String[] args) {
        E.deb("runClass: "+className); // NOI18N
        E.deb("Creating new CvsListCommand"); // NOI18N
        classRunning = true;
        boolean success = true;
        Class listClass = null;
        try {
            listClass =  Class.forName(className, true,
                                       VcsUtilities.getSFSClassLoader());
        } catch (ClassNotFoundException e) {
            //fileSystem.debug ("LIST: "+g("ERR_ClassNotFound", className)); // NOI18N
            //container.match("LIST: "+g("ERR_ClassNotFound", className)); // NOI18N
            try {
                printErrorOutput("CLASS EXEC: " + NbBundle.getMessage(CommandLineVcsDirReader.class,
                                                                      "ERR_ClassNotFound", className)); // NOI18N
            } catch(java.util.MissingResourceException mrexc) {
                // Likely to be called when the module is being uninstalled
                printErrorOutput("CLASS EXEC: Class " + className + " not found"); // NOI18N
            }
            success = false;
        }
        E.deb(listClass+" loaded"); // NOI18N
        VcsListCommand listCommand = null;
        if (success) {
            try {
                listCommand = (VcsListCommand) listClass.newInstance();
            } catch (InstantiationException e) {
                //fileSystem.debug ("LIST: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
                //container.match("LIST: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
                printErrorOutput("CLASS LIST: " + NbBundle.getMessage(CommandLineVcsDirReader.class,
                                                                      "ERR_CanNotInstantiate", listClass)); // NOI18N
                success = false;
            } catch (IllegalAccessException e) {
                //fileSystem.debug ("LIST: "+g("ERR_IllegalAccessOnClass", listClass)); // NOI18N
                //container.match(g("LIST: "+"ERR_IllegalAccessOnClass", listClass)); // NOI18N
                printErrorOutput("CLASS LIST: " + NbBundle.getMessage(CommandLineVcsDirReader.class,
                                                                      "ERR_IllegalAccessOnClass", listClass)); // NOI18N
                success = false;
            }
        }
        E.deb("VcsListCommand created."); // NOI18N
        Hashtable filesByName = new Hashtable();
        UserCommand list = (UserCommand) getCommand();
        if (success) {
            Hashtable vars = getVariables();
            ExecuteCommand.setAdditionalParams(listCommand, getFileSystem());
            String dataRegex = (String) list.getProperty(UserCommand.PROPERTY_DATA_REGEX);
            if (dataRegex == null) dataRegex = ExecuteCommand.DEFAULT_REGEX;
            vars.put("DATAREGEX", dataRegex); // NOI18N
            String errorRegex = (String) list.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
            if (errorRegex == null) errorRegex = ExecuteCommand.DEFAULT_REGEX;
            vars.put("ERRORREGEX", errorRegex); // NOI18N
            String input = (String) list.getProperty(UserCommand.PROPERTY_INPUT);
            if (input != null) vars.put("INPUT", input); // NOI18N
            //vars.put("TIMEOUT", new Long(list.getTimeout())); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_running", list.getName()));
            try {
                success = listCommand.list(vars, args, filesByName,
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
                                       }, (String) list.getProperty(UserCommand.PROPERTY_DATA_REGEX),
                                       new CommandDataOutputListener() {
                                           public void outputData(String[] data) {
                                               printDataErrorOutput(data);
                                           }
                                       }, (String) list.getProperty(UserCommand.PROPERTY_ERROR_REGEX)
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
                        NbBundle.getMessage(CommandLineVcsDirReader.class, "ERR_EXC_IN_CLASS", className)));
            }
        }
        //if (!shouldFail) {
        //String[] elements = new String[2];
        //D.deb("adding to dir="+dir); // NOI18N
        //File parent = new File(dir.getAbsolutePath());
        for(Enumeration e = filesByName.keys(); e.hasMoreElements() ;) {
            String fileName = (String) e.nextElement();
            String[] elements = (String[]) filesByName.get(fileName);
            //elements[0] = fileName;
            //elements[1] = fileStatus;
            E.deb("Processing: "+fileName+"|"+elements); // NOI18N
            //fileSystem.debug("stdout: "+VcsUtilities.arrayToString(elements)); // NOI18N
            elements = translateElements(elements, list);
            rawData.add(elements);
        }
        exitStatus = (success) ? VcsCommandExecutor.SUCCEEDED : VcsCommandExecutor.FAILED;
    }

    //-------------------------------------------
    public void run() {
        try {
            super.run();
        } finally {
            String commonParent = (String) getVariables().get("COMMON_PARENT");
            String dir = (String) getVariables().get("DIR"); // NOI18N
            dir = dir.replace (java.io.File.separatorChar, '/');
            if (commonParent != null) {
                commonParent = commonParent.replace (java.io.File.separatorChar, '/');
                if (commonParent.length() > 0) dir = commonParent + "/" + dir;
            }
            listener.readDirFinished(dir, rawData, getExitStatus() == VcsCommandExecutor.SUCCEEDED);
            // After refresh I should ensure, that the next automatic refresh will work if something happens in numbering
            getFileSystem().removeNumDoAutoRefresh(dir); // NOI18N
            //D.deb("run(LIST) '"+dir.name+"' finished"); // NOI18N
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

    
    /**
     * Translate elements obtained from the command line reader to elements used by {@link RefreshCommandSupport}
     * @param elements the elements from command line reader
     * @param list the refresh command
     * @return new set of elements in the correct form for <code>RefreshCommandSupport</code>
     */
    public static String[] translateElements(String[] cmdElements, UserCommand list) {
        int removedIndex = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_REMOVED_FILE_NAME);
        if (removedIndex >= 0 && cmdElements.length > removedIndex && cmdElements[removedIndex] != null) {
            return new String[] { cmdElements[removedIndex] };
        }
        int n = RefreshCommandSupport.NUM_ELEMENTS;
        int[] index = new int[n];
        index[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_FILE_NAME);
        index[RefreshCommandSupport.ELEMENT_INDEX_STATUS] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_STATUS);
        index[RefreshCommandSupport.ELEMENT_INDEX_LOCKER] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_LOCKER);
        index[RefreshCommandSupport.ELEMENT_INDEX_REVISION] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_REVISION);
        index[RefreshCommandSupport.ELEMENT_INDEX_STICKY] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_STICKY);
        index[RefreshCommandSupport.ELEMENT_INDEX_ATTR] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_ATTR);
        index[RefreshCommandSupport.ELEMENT_INDEX_DATE] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_DATE);
        index[RefreshCommandSupport.ELEMENT_INDEX_TIME] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_TIME);
        index[RefreshCommandSupport.ELEMENT_INDEX_SIZE] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_SIZE);
        String[] elements = new String[n];
        int cmdn = cmdElements.length;
        for(int i = 0; i < n; i++) {
            if (index[i] >= 0 && index[i] < cmdn) elements[i] = cmdElements[index[i]];
        }
        return elements;
    }

}
