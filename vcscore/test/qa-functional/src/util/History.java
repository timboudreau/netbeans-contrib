/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package util;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import junit.framework.AssertionFailedError;
import org.netbeans.modules.vcscore.runtime.RuntimeCommand;
import org.netbeans.modules.vcscore.runtime.RuntimeCommandsProvider;
import org.openide.filesystems.FileSystem;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;

/** History class for tracing history of VCS command in Runtime tab in Explorer.
 * Typical class usage:
 * <CODE>
 * public void testHistory () {
 *    ... get file system ...
 *    History history = new History (filesytem);
 *    ... invoke some VCS command (for example: CVS|Refresh on "data" package)...
 *    history.waitCommand ("Refresh", "data"); // waits until Refresh command finishs
 * }
 * </CODE>
 */
public class History {
    
    /** Files property name of VCS Generic history item */
    public static final String FILES_PROPERTY_VCS = "Processed Files";
    /** Files property name of Java CVS history item */
    public static final String FILES_PROPERTY_JCVS = "Files";
    /** Execution String property name of VCS Generic history item */
    public static final String EXEC_PROPERTY_VCS = "Execution String";
    /** Execution String property name of Java CVS history item */
    public static final String EXEC_PROPERTY_JCVS = null;
    
    /** File system instance for which history is traced */
    FileSystem filesystem;
    /** RuntimeCommand Provider of traced File system */
    RuntimeCommandsProvider provider;
    /** Breakpoint RuntimeCommand - RuntimeCommands that are started before Breakpoint one are ignored */
    RuntimeCommand breakpoint;
    /** Startpoint RuntimeCommand - just for user's information - it is set to the most recent RuntimeCommand when instance of History class is created */
    RuntimeCommand startpoint;
    /** Logging print stream - if set, all log and debug output from this class is printed into into this stream */
    PrintStream log;
    /** Files property name of history item */
    String filesproperty = FILES_PROPERTY_VCS;
    /** Execution String property name of history item */
    String execproperty = FILES_PROPERTY_VCS;
    /** Timeout value for waiting for command finish */
    int timeout = 60;
    
    /** Creates History class instance for specified file system. Startpoint and breakpoint are set to the most recent RuntimeCommand.
     * @param filesystem Traced file system
     */    
    public History(FileSystem filesystem) {
        this (filesystem, null);
    }
    
    /** Creates History class instance for specified filesystem. It also sets output print stream. Startpoint and breakpoint are set to the most recent RuntimeCommand.
     * @param filesystem Traced file system
     * @param log Output print stream
     */    
    public History(FileSystem filesystem, PrintStream log) {
        this.filesystem = filesystem;
        this.log = log;
        provider = RuntimeCommandsProvider.findProvider(filesystem);
        breakpoint ();
        startpoint = breakpoint;
    }
    
    /** Returns traced file system instance.
     * @return Traced file system
     */    
    public FileSystem getFileSystem () {
        return filesystem;
    }
    
    /** Switchs history instance into VCS Generic mode for tracing RuntimeCommands of VCS Generic file systems. */    
    public void switchToVCS () {
        filesproperty = FILES_PROPERTY_VCS;
        execproperty = EXEC_PROPERTY_VCS;
    }
    
    /** Switchs history instance into VCS Generic mode for tracing RuntimeCommands of Java CVS file systems. */    
    public void switchToJCVS () {
        filesproperty = FILES_PROPERTY_JCVS;
        execproperty = EXEC_PROPERTY_JCVS;
    }
    
    /** Sets timeout for waiting for command finish.
     * @param timeout Timeout in seconds
     */    
    public void setTimeout (int timeout) {
        this.timeout = timeout;
    }
    
    /** Returns instance of breakpoint RuntimeCommand.
     * @return Breakpoint RuntimeCommand
     */    
    public RuntimeCommand getBreakpoint () {
        return breakpoint;
    }
    
    /** Sets breakpoint RuntimeCommand. If null, no RuntimeCommand is ignored (= all command in file system history are checked).
     * @param breakpoint Desired breakpoint RuntimeCommand
     */    
    public void setBreakpoint (RuntimeCommand breakpoint) {
        this.breakpoint = breakpoint;
    }
    
    /** Sets breakpoint to the last (the most recent) RuntimeCommand in file system history (= all RuntimeCommand until now are ignored). */    
    public void breakpoint () {
        RuntimeCommand[] rc = provider.children();
        breakpoint = (rc != null  &&  rc.length > 0) ? rc[rc.length - 1] : null;
    }
    
    /** Returns RuntimeCommand instance of the last command in file system history that has specified display name and is invoked on specified files.
     * @param name Name of RuntimeCommand
     * @param file Processed files of RuntimeCommand
     * @return correspondent RuntimeCommand (if null, no correspondent RuntimeCommand was found)
     */    
    public RuntimeCommand getLastCommand (String name, String file) {
        RuntimeCommand[] rc = provider.children();
        if (rc == null)
            return null;
        for (int a = rc.length - 1; a >= 0; a --) {
            if (rc[a] == breakpoint)
                break;
            Node no = rc[a].getNodeDelegate();
            String s = getPropertyValue (no, "Properties", filesproperty);
            if (name.equals (no.getDisplayName ())  &&  (file == null  ||  file.equals (s)))
                return rc[a];
        }
        return null;
    }
    
    /** Returns value of specified property of specified node.
     * @param node Node
     * @param set Properties set name
     * @param prop Property name
     * @return Property value
     */    
    private String getPropertyValue (Node node, String set, String prop) {
        PropertySet[] ps = node.getPropertySets ();
        if (ps == null)
            return null;
        for (int a = 0; a < ps.length; a ++) {
            Property[] p = ps[a].getProperties ();
            if (p == null  ||  !set.equals (ps[a].getDisplayName ()))
                continue;
            for (int b = 0; b < p.length; b ++) {
                if (!prop.equals (p[b].getDisplayName ()))
                    continue;
                try {
                    Object o = p[b].getValue ();
                    return (o != null) ? o.toString () : null;
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
        return null;
    }
    
    /** Waits for and returns RuntimeCommand (the most recent command that correspond to specified by its display name and processed files).
     * Breakpoint is set to found RuntimeCommand.
     * This method uses getLastCommand method for quering for desired RuntimeCommand. This is repeated every second for timeout-times.
     * If no RuntimeCommand is found until specified time expires, AssertionFailedError is thrown.
     * @param name RuntimeCommand display name
     * @param file RuntimeCommand processed files
     * @return RuntimeCommand
     */    
    public RuntimeCommand getWaitCommand (String name, String file) {
        for (int a = 0; a < timeout; a ++) {
            RuntimeCommand rc = getLastCommand (name, file);
            if (rc != null  &&  rc.getState() == RuntimeCommand.STATE_DONE) {
                breakpoint = rc;
                return rc;
            }
            try { Thread.sleep (1000); } catch (Exception e) { e.printStackTrace (); }
        }
        print ();
        throw new AssertionFailedError ("Timeout: Command does not finished: Command: " + name + " File: " + file);
    }
    
    /** Waits for RuntimeCommand (the most recent command that correspond to specified by its display name and processed files) and returns its exit status.
     * Breakpoint is set to found RuntimeCommand.
     * This method uses getLastCommand method for quering for desired RuntimeCommand. This is repeated every second for timeout-times.
     * If no RuntimeCommand is found until specified time expires, AssertionFailedError is thrown.
     * @param name RuntimeCommand display name
     * @param file RuntimeCommand processed files
     * @return Exit status
     */    
    public boolean waitCommand (String name, String file) {
        for (int a = 0; a < timeout; a ++) {
            RuntimeCommand rc = getLastCommand (name, file);
            if (rc != null  &&  rc.getState() == RuntimeCommand.STATE_DONE) {
                breakpoint = rc;
                return rc.getExitStatus() == RuntimeCommand.SUCCEEDED;
            }
            try { Thread.sleep (1000); } catch (Exception e) { e.printStackTrace (); }
        }
        print ();
        throw new AssertionFailedError ("Timeout: Command does not finished: Command: " + name + " File: " + file);
    }
    
    /** Returns boolean value that specifies exit status of specified RuntimeCommand.
     * @param rc RuntimeCommand
     * @return Exit status - if false, command failed - if true, command succeed
     */    
    public static boolean resultCommand (RuntimeCommand rc) {
        return rc.getExitStatus() == RuntimeCommand.SUCCEEDED;
    }
    
    /** Prints file system history into output print stream. */    
    public void print () {
        if (log == null)
            return;
        log.println ("==== History ====");
        RuntimeCommand[] rc = provider.children();
        log.println ("History Count: " + ((rc == null) ? 0 : rc.length));
        if (rc == null)
            return;
        for (int a = rc.length - 1; a >= 0; a --) {
            if (rc[a] == breakpoint)
                log.print ("==== Breakpoint - ");
            if (rc[a] == startpoint)
                log.print ("==== Startpoint - ");
            Node no = rc[a].getNodeDelegate();
            log.print("History: " + a + " - Status: ");
            if (rc[a].getState() == RuntimeCommand.STATE_DONE)
                log.print ((rc[a].getExitStatus() == RuntimeCommand.SUCCEEDED) ? "OK" : "Fail");
            else
                log.print ("Run" + rc[a].getState ());
            log.print (" - Name: " + no.getDisplayName () + " - File: " + getPropertyValue (no, "Properties", filesproperty));
            if (execproperty != null)
                log.println (" Exec: " + getPropertyValue (no, "Properties", execproperty));
            else
                log.println ();
        }
    }
    
}
