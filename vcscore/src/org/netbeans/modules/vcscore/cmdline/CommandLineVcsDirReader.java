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

import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;
import java.net.URL;
import java.net.URLClassLoader;

import org.openide.TopManager;
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

/** Read single VCS directory.
 * 
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public class CommandLineVcsDirReader implements VcsCommandExecutor {
    private Debug E=new Debug("CommandLineVcsDirReader", true); // NOI18N
    private Debug D=E;

    private VcsFileSystem fileSystem = null;
    private UserCommand list = null ;
    private Hashtable vars = null ;
    //private VcsCacheDir dir=null ;
    private String path;

    private ArrayList commandOutputListener = new ArrayList(); 
    private ArrayList commandErrorOutputListener = new ArrayList(); 
    private ArrayList commandDataOutputListener = new ArrayList(); 
    private ArrayList commandDataErrorOutputListener = new ArrayList(); 

    private boolean shouldFail = false ;
    private int exitStatus;

    private Vector /*String[]*/ rawData = new Vector(40);

    private DirReaderListener listener = null ;


    //-------------------------------------------
    public CommandLineVcsDirReader(DirReaderListener listener, VcsFileSystem fileSystem,
                                   UserCommand list, Hashtable vars) {
        //super("VCS-DirReader-"+((String)vars.get("DIR"))); // NOI18N
        this.listener = listener;
        this.fileSystem = fileSystem;
        this.list = list;
        this.vars = vars;
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
    }


    /**
     * Add the listener to the standard output of the command. The listeners are removed
     * when the command finishes.
     */
    public synchronized void addOutputListener(CommandOutputListener l) {
        if (commandOutputListener != null) commandOutputListener.add(l);
    }
    
    /**
     * Add the listener to the error output of the command. The listeners are removed
     * when the command finishes.
     */
    public synchronized void addErrorOutputListener(CommandOutputListener l) {
        if (commandErrorOutputListener != null) commandErrorOutputListener.add(l);
    }
    
    /**
     * Add the listener to the data output of the command. This output may contain
     * a parsed information from its standard output or some other data provided
     * by this command. The listeners are removed when the command finishes.
     */
    public synchronized void addDataOutputListener(CommandDataOutputListener l) {
        if (commandDataOutputListener != null) commandDataOutputListener.add(l);
    }

    /**
     * Add the listener to the data error output of the command. This output may contain
     * a parsed information from its error output or some other data provided
     * by this command. If there are some data given to this listener, the command
     * is supposed to fail. The listeners are removed when the command finishes.
     */
    public synchronized void addDataErrorOutputListener(CommandDataOutputListener l) {
        if (commandDataErrorOutputListener != null) commandDataErrorOutputListener.add(l);
    }

    /**
     * The executed command.
     */
    public VcsCommand getCommand() {
        return list;
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
        return (String) list.getProperty(VcsCommand.PROPERTY_EXEC);
    }

    /**
     * Update the execution string. It may contain user input now.
     * @param exec the execution string updated with user input.
     */
    public void updateExec(String exec) {
    }
    
    /**
     * Get the updated execution string. It may contain user input now.
     */
    public String getExec() {
        return (String) list.getProperty(VcsCommand.PROPERTY_EXEC);
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
     * @return the set of files of type <code>String</code>
     */
    public Set getFiles() {
        String file = (String) vars.get("FILE");
        HashSet set = new HashSet(1);
        set.add(file);
        return set;
    }
    
    /**
     * Get the path of the processed files.
     * The path is relative to file system root.
     */
    public String getPath() {
        return (String) vars.get("DIR");
    }
    
    //-------------------------------------------
    public Vector getRawData(){
        return rawData;
    }

    private void printOutput(String line) {
        for (Iterator it = commandOutputListener.iterator(); it.hasNext(); ) {
            ((CommandOutputListener) it).outputLine(line);
        }
    }

    private void printErrorOutput(String line) {
        for (Iterator it = commandErrorOutputListener.iterator(); it.hasNext(); ) {
            ((CommandOutputListener) it).outputLine(line);
        }
    }

    private void printDataOutput(String[] data) {
        for (Iterator it = commandDataOutputListener.iterator(); it.hasNext(); ) {
            ((CommandDataOutputListener) it).outputData(data);
        }
    }

    private void printDataErrorOutput(String[] data) {
        for (Iterator it = commandDataErrorOutputListener.iterator(); it.hasNext(); ) {
            ((CommandDataOutputListener) it).outputData(data);
        }
    }


    private void runCommand(String exec) {
        ExternalCommand ec = new ExternalCommand(exec);
        //ec.setTimeout(list.getTimeout());
        ec.setInput((String) list.getProperty(UserCommand.PROPERTY_INPUT));
        //D.deb("list.getInput()='"+list.getInput()+"'"); // NOI18N

        String dataRegex = (String) list.getProperty(UserCommand.PROPERTY_DATA_REGEX);
        //D.deb("dataRegex="+list.getDataRegex()); // NOI18N
        //final File parent = new File(dir.getAbsolutePath());
        try{
            ec.addStdoutRegexListener(new CommandDataOutputListener () {
                                          public void outputData(String[] elements) {
                                              //D.deb("stdout match:"+VcsUtilities.arrayToString(elements)); // NOI18N
                                              //fileSystem.debug("stdout: "+VcsUtilities.arrayToString(elements)); // NOI18N
                                              elements = translateElements(elements, list);
                                              rawData.addElement(elements);
                                              printDataOutput(elements);
                                              //CacheFile file = RefreshCommandSupport.matchToFile(elements, list, fileSystem.getPossibleFileStatusesTable(), fileSystem.getCacheIdStr(), parent);
                                              //if(file instanceof VcsCacheDir) {
                                                  //String parent = dir.getPath ();
                                                  //((VcsCacheDir) file).setPath (((parent.length() > 0) ? parent + "/" : "") + file.getName ()); // NOI18N
                                                  //((VcsCacheDir) file).setLoaded(false);
                                              //}
                                              //D.deb("file="+file+", status = "+file.getStatus()); // NOI18N
                                              //dir.add(file);
                                          }
                                      }, dataRegex);
        }
        catch (BadRegexException e) {
            E.err(e,"bad regex"); // NOI18N
        }

        String errorRegex = (String) list.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
        //D.deb("errorRegex="+list.getErrorRegex()); // NOI18N
        try{
            ec.addStderrRegexListener(new CommandDataOutputListener () {
                                          public void outputData(String[] elements) {
                                              //D.deb("stderr match:"+VcsUtilities.arrayToString(elements)); // NOI18N
                                              //fileSystem.debug("stderr: "+VcsUtilities.arrayToString(elements)); // NOI18N
                                              printDataErrorOutput(elements);
                                              shouldFail = true;
                                          }
                                      }, errorRegex);
        }
        catch (BadRegexException e) {
            E.err(e,"bad regex"); // NOI18N
        }

        //ec.addStdoutNoRegexListener(container);
        //ec.addStderrNoRegexListener(container);

        D.deb("ec="+ec); // NOI18N
        //TopManager.getDefault().setStatusText(g("MSG_Command_name_running", list.getName()));
        for (Iterator it = commandOutputListener.iterator(); it.hasNext(); ) {
            ec.addStdoutListener((CommandOutputListener) it.next());
        }
        for (Iterator it = commandErrorOutputListener.iterator(); it.hasNext(); ) {
            ec.addStderrListener((CommandOutputListener) it.next());
        }

        exitStatus = ec.exec();

        if (shouldFail) {
            //fileSystem.debug("LIST: "+g("MSG_Match_on_stderr")+"\n"); // NOI18N
            exitStatus = VcsCommandExecutor.FAILED;
        }

        switch (exitStatus) {
            case VcsCommandExecutor.SUCCEEDED:
                //fileSystem.debug("LIST: "+g("MSG_Command_succeeded")+"\n"); // NOI18N
                //TopManager.getDefault().setStatusText(g("MSG_Command_name_succeeded", list.getName()));
                break ;
            case VcsCommandExecutor.INTERRUPTED:
                //fileSystem.debug("LIST: "+g("MSG_Timeout")); // NOI18N
            case VcsCommandExecutor.FAILED:
                //fileSystem.debug("LIST: "+g("MSG_List_command_failed")+"\n"); // NOI18N
                //TopManager.getDefault().setStatusText(g("MSG_Command_name_failed", list.getName()));
                shouldFail=true ;
        }
    }

    private void runClass(String className, StringTokenizer tokens) {
        E.deb("runClass: "+className); // NOI18N
        E.deb("Creating new CvsListCommand"); // NOI18N
        Class listClass = null;
        try {
            listClass =  Class.forName(className, true,
                                       org.openide.TopManager.getDefault().currentClassLoader());
        } catch (ClassNotFoundException e) {
            //fileSystem.debug ("LIST: "+g("ERR_ClassNotFound", className)); // NOI18N
            //container.match("LIST: "+g("ERR_ClassNotFound", className)); // NOI18N
            printErrorOutput("CLASS LIST: "+g("ERR_ClassNotFound", className)); // NOI18N
            shouldFail = true;
            return;
        }
        E.deb(listClass+" loaded"); // NOI18N
        VcsListCommand listCommand = null;
        try {
            listCommand = (VcsListCommand) listClass.newInstance();
        } catch (InstantiationException e) {
            //fileSystem.debug ("LIST: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
            //container.match("LIST: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
            printErrorOutput("CLASS LIST: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
            shouldFail = true;
            return;
        } catch (IllegalAccessException e) {
            //fileSystem.debug ("LIST: "+g("ERR_IllegalAccessOnClass", listClass)); // NOI18N
            //container.match(g("LIST: "+"ERR_IllegalAccessOnClass", listClass)); // NOI18N
            printErrorOutput("CLASS LIST: "+g("ERR_IllegalAccessOnClass", listClass)); // NOI18N
            shouldFail = true;
            return;
        }
        E.deb("VcsListCommand created."); // NOI18N
        String[] args = new String[tokens.countTokens()];
        int i = 0;
        while(tokens.hasMoreTokens()) {
            args[i++] = tokens.nextToken();
        }

        Hashtable filesByName = new Hashtable();
        if (!shouldFail) {
            vars.put("DATAREGEX", (String) list.getProperty(UserCommand.PROPERTY_DATA_REGEX)); // NOI18N
            vars.put("ERRORREGEX", (String) list.getProperty(UserCommand.PROPERTY_ERROR_REGEX)); // NOI18N
            vars.put("INPUT", (String) list.getProperty(UserCommand.PROPERTY_INPUT)); // NOI18N
            //vars.put("TIMEOUT", new Long(list.getTimeout())); // NOI18N
            TopManager.getDefault().setStatusText(g("MSG_Command_name_running", list.getName()));
            shouldFail = !listCommand.list(vars, args, filesByName,
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
            E.deb("shouldFail = "+shouldFail+" after list with "+filesByName.size()+" elements"); // NOI18N
            /*
            for(Enumeration e = filesByName.keys(); e.hasMoreElements() ;) {
              String fileName=(String)e.nextElement();
              String fileStatus=(String)filesByName.get(fileName);
              E.deb("filesByName: "+fileName+" | "+fileStatus);
        }
            */
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
            rawData.addElement(elements);
            /*
            CacheFile file = RefreshCommandSupport.matchToFile(elements, list, fileSystem.getPossibleFileStatusesTable(), fileSystem.getCacheIdStr(), parent);
            if(file instanceof VcsCacheDir) {
                //String parent = dir.getPath ();
                //((VcsDir)file).setPath (((parent.length() > 0) ? parent + "/" : "") + file.getName ()); // NOI18N
                ((VcsCacheDir) file).setLoaded(false);
            }
            D.deb("adding file="+file); // NOI18N
            dir.add(file);
             */
        }
        //}
        if (shouldFail) {
            exitStatus = VcsCommandExecutor.FAILED;
            //fileSystem.debug("LIST: "+g("MSG_List_command_failed")+"\n"); // NOI18N
            //container.match("LIST: "+g("MSG_List_command_failed")); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_failed", list.getName()));
        } else {
            exitStatus = VcsCommandExecutor.SUCCEEDED;
            //fileSystem.debug("LIST: "+g("MSG_Command_succeeded")+"\n"); // NOI18N
            //container.match("LIST: "+g("MSG_Command_succeeded")); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_succeeded", list.getName()));
        }

    }

    //-------------------------------------------
    public void run() {
        String exec = (String) list.getProperty(VcsCommand.PROPERTY_EXEC);
        exec = Variables.expand(vars, exec, true).trim();
        //fileSystem.debug("LIST: "+exec); // NOI18N

        //ErrorCommandDialog errDlg = fileSystem.getErrorDialog(); //new ErrorCommandDialog(list, new JFrame(), false);
        //OutputContainer container = new OutputContainer(list);
        //container.match("LIST: "+exec); // NOI18N

        StringTokenizer tokens = new StringTokenizer(exec);
        String first = tokens.nextToken();
        E.deb("first = "+first); // NOI18N
        if (first != null && (first.toLowerCase().endsWith(".class"))) { // NOI18N
            runClass(first.substring(0, first.length() - ".class".length()), tokens); // NOI18N
        } else
            runCommand(exec);

        if(shouldFail) {
            //errDlg.putCommandOut(container.getMessages());
            //errDlg.showDialog();
            fileSystem.setPassword(null);
            //fileSystem.debug(g("ERR_LISTFailed")); // NOI18N
            //D.deb("failed reading of dir="+dir); // NOI18N
            /*
            if( dir.getName ().equals("") ){ // NOI18N
                D.deb("root dir patch"); // NOI18N
                dir.setLoaded(true); // failed, but loaded
                listener.readDirFinished(path, rawData, !shouldFail);
            } else {
                dir.setStatus (g("MSG_VCS_command_failed")); // NOI18N
                dir.setLoaded(true); // failed, but loaded
                listener.readDirFinished( dir, rawData, !shouldFail);
            }
             */
            
        }
        /*
        else{
            //errDlg.removeCommandOut();
            //errDlg.cancelDialog();
            //fileSystem.debug("LIST command finished successfully"); // NOI18N
            dir.setLoaded(true);
            listener.readDirFinished( dir, rawData, !shouldFail);
        }
         */
        listener.readDirFinished(path, rawData, !shouldFail);
        // After refresh I should ensure, that the next automatic refresh will work if something happens in numbering
        fileSystem.removeNumDoAutoRefresh((String)vars.get("DIR")); // NOI18N
        //D.deb("run(LIST) '"+dir.name+"' finished"); // NOI18N
        commandOutputListener = null;
        commandErrorOutputListener = null;
        commandDataOutputListener = null;
        commandDataErrorOutputListener = null;
    }

    /**
     * Get the exit status of the execution.
     * @return the exit value, it may be one of {@link SUCCEEDED}, {@link FAILED}, {@link INTERRUPTED}.
     */
    public int getExitStatus() {
        return exitStatus;
    }

    
    /**
     * Translate elements obtained from the command line reader to elements used by {@link RefreshCommandSupport}
     * @param elements the elements from command line reader
     * @param list the refresh command
     * @return new set of elements in the correct form for <code>RefreshCommandSupport</code>
     */
    public static String[] translateElements(String[] cmdElements, UserCommand list) {
        int n = RefreshCommandSupport.NUM_ELEMENTS;
        int[] index = new int[n];
        index[0] = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_FILE_NAME);
        index[1] = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_STATUS);
        index[2] = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_LOCKER);
        index[3] = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_REVISION);
        index[4] = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_ATTR);
        index[5] = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_DATE);
        index[6] = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_TIME);
        index[7] = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_SIZE);
        String[] elements = new String[n];
        int cmdn = cmdElements.length;
        for(int i = 0; i < n; i++) {
            if (index[i] >= 0 && index[i] < cmdn) elements[i] = cmdElements[index[i]];
        }
        return elements;
    }

    //-------------------------------------------
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
    //-------------------------------------------

}

/*
 * Log
 *  19   Gandalf-post-FCS1.17.1.0    4/4/00   Martin Entlicher setPath fix
 *  18   Gandalf   1.17        2/10/00  Martin Entlicher Automatic refresh after 
 *       last command only.
 *  17   Gandalf   1.16        1/18/00  Martin Entlicher 
 *  16   Gandalf   1.15        1/15/00  Ian Formanek    NOI18N
 *  15   Gandalf   1.14        1/6/00   Martin Entlicher 
 *  14   Gandalf   1.13        1/3/00   Martin Entlicher 
 *  13   Gandalf   1.12        12/29/99 Martin Entlicher Debug messages added.
 *  12   Gandalf   1.11        12/28/99 Martin Entlicher 
 *  11   Gandalf   1.10        12/21/99 Martin Entlicher 
 *  10   Gandalf   1.9         12/14/99 Martin Entlicher Output listener added
 *  9    Gandalf   1.8         11/30/99 Martin Entlicher 
 *  8    Gandalf   1.7         11/23/99 Martin Entlicher 
 *  7    Gandalf   1.6         10/25/99 Pavel Buzek     
 *  6    Gandalf   1.5         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  5    Gandalf   1.4         10/12/99 Martin Entlicher 
 *  4    Gandalf   1.3         10/12/99 Pavel Buzek     
 *  3    Gandalf   1.2         10/10/99 Pavel Buzek     
 *  2    Gandalf   1.1         10/5/99  Pavel Buzek     VCS at least can be 
 *       mounted
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $
 */
