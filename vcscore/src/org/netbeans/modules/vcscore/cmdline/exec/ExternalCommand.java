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

package org.netbeans.modules.vcscore.cmdline.exec;

import java.io.*;
import java.util.*;
import java.text.*;

import org.apache.regexp.*;

import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;

/** Single external command to be executed. See {@link TestCommand} for typical usage.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class ExternalCommand {
    private Debug E=new Debug("ExternalCommand",true); // NOI18N
    private Debug D=new Debug("ExternalCommand",true); // NOI18N

    //public static final int SUCCESS=0;
    //public static final int FAILED=1;
    //public static final int FAILED_ON_TIMEOUT=2;

    private String command = null;
    //private long timeoutMilis = 0;
    private int exitStatus = VcsCommandExecutor.SUCCEEDED;
    private String inputData = null;
    private int osType = Utilities.getOperatingSystem();

    private Object stdOutDataLock = new Object();
    //private RegexListener[] stdoutListeners = new RegexListener[0];
    private ArrayList stdOutDataListeners = new ArrayList();
    private ArrayList stdOutRegexps = new ArrayList();

    private Object stdErrDataLock = new Object();
    //private RegexListener[] stderrListeners = new RegexListener[0];
    private ArrayList stdErrDataListeners = new ArrayList();
    private ArrayList stdErrRegexps = new ArrayList();
    //private Object stdOutErrLock = new Object(); // synchronizes stdout and stderr

    private Object stdOutLock = new Object();
    private Object stdErrLock = new Object();
    private ArrayList stdOutListeners = new ArrayList();
    private ArrayList stdErrListeners = new ArrayList();
    
    // The environment variables
    private String[] envp = null;

    /** Creates new ExternalCommand */
    public ExternalCommand() {
    }

    //-------------------------------------------
    public ExternalCommand(String command) {
        setCommand(command);
    }

    //-------------------------------------------
    public ExternalCommand(String command, String input) {
        setCommand(command);
        //setTimeout(timeoutMilis);
        setInput(input);
    }



    //-------------------------------------------
    public void setCommand(String command) {
        this.command = command;
    }


    /*
     * WE DO NOT SUPPORT TIMEOUTS ANY MORE !!
     * You may explicitly kill the command if it blocks somewhere.
    public void setTimeout(long timeoutMilis){
        this.timeoutMilis=timeoutMilis;
    }
     */


    /**
     * Set the input, which will be send to the command standard input.
     */
    public void setInput(String inputData) {
        this.inputData = inputData;
    }
    
    public void setEnv(String[] envp) {
        this.envp = envp;
    }


    private void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }

    /**
     * Get the exit status of the command.
     */
    public int getExitStatus(){
        return exitStatus;
    }

    //-------------------------------------------
    private String[] parseParameters(String s) {
        int NULL = 0x0;  // STICK + whitespace or NULL + non_"
        int INPARAM = 0x1; // NULL + " or STICK + " or INPARAMPENDING + "\ // NOI18N
        int INPARAMPENDING = 0x2; // INPARAM + \
        int STICK = 0x4; // INPARAM + " or STICK + non_" // NOI18N
        int STICKPENDING = 0x8; // STICK + \
        Vector params = new Vector(5,5);
        char c;
        int state = NULL;
        StringBuffer buff = new StringBuffer(20);
        int slength = s.length();
        for (int i = 0; i < slength; i++) {
            c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                if (state == NULL) {
                    params.addElement(buff.toString());
                    buff.setLength(0);
                } else if (state == STICK) {
                    params.addElement(buff.toString());
                    buff.setLength(0);
                    state = NULL;
                } else if (state == STICKPENDING) {
                    buff.append('\\');
                    params.addElement(buff.toString());
                    buff.setLength(0);
                    state = NULL;
                } else if (state == INPARAMPENDING) {
                    state = INPARAM;
                    buff.append('\\');
                    buff.append(c);
                } else {    // INPARAM
                    buff.append(c);
                }
                continue;
            }

            if (c == '\\') {
                if (state == NULL) {
                    ++i;
                    if (i < slength) {
                        char cc = s.charAt(i);
                        if (cc == '"' || cc == '\\') {
                            buff.append(cc);
                        } else if (Character.isWhitespace(cc)) {
                            buff.append(c);
                            --i;
                        } else {
                            buff.append(c);
                            buff.append(cc);
                        }
                    } else {
                        buff.append('\\');
                        break;
                    }
                    continue;
                } else if (state == INPARAM) {
                    state = INPARAMPENDING;
                } else if (state == INPARAMPENDING) {
                    buff.append('\\');
                    state = INPARAM;
                } else if (state == STICK) {
                    state = STICKPENDING;
                } else if (state == STICKPENDING) {
                    buff.append('\\');
                    state = STICK;
                }
                continue;
            }

            if (c == '"') {
                if (state == NULL) {
                    state = INPARAM;
                } else if (state == INPARAM) {
                    state = STICK;
                } else if (state == STICK) {
                    state = INPARAM;
                } else if (state == STICKPENDING) {
                    buff.append('"');
                    state = STICK;
                } else { // INPARAMPENDING
                    buff.append('"');
                    state = INPARAM;
                }
                continue;
            }

            if (state == INPARAMPENDING) {
                buff.append('\\');
                state = INPARAM;
            } else if (state == STICKPENDING) {
                buff.append('\\');
                state = STICK;
            }
            buff.append(c);
        }
        // collect
        if (state == INPARAM) {
            params.addElement(buff.toString());
        } else if ((state & (INPARAMPENDING | STICKPENDING)) != 0) {
            buff.append('\\');
            params.addElement(buff.toString());
        } else { // NULL or STICK
            if (buff.length() != 0) {
                params.addElement(buff.toString());
            }
        }
        String[] ret = new String[params.size()];
        params.copyInto(ret);
        return ret;
    }
    
    private static ArrayList outputGrabbers = new ArrayList();
    private static RequestProcessor outputRequestProcessor;

    /**
     * Executes the external command.
     */
    public int exec(){
        //D.deb("exec()"); // NOI18N
        Process proc=null;
        OutputGrabber output = null;
        WatchDog watchDog = null;

        synchronized (outputGrabbers) {
            if (outputRequestProcessor == null) {
                outputRequestProcessor = new RequestProcessor("External Command Output Grabber Processor"); // NOI18N
                outputRequestProcessor.post(new OutputGrabbersProcessor());
            }
        }
        try{
            //D.deb("Thread.currentThread()="+Thread.currentThread()); // NOI18N

            String[] commandArr=parseParameters(command);
            D.deb("commandArr="+VcsUtilities.arrayToString(commandArr)); // NOI18N
            /*
            if (commandArr.toLowerCase().endsWith(".class")) {
              execClass(commandArr);
        }
            */
	    
            try{
                if (envp == null) {
                    proc = Runtime.getRuntime().exec(commandArr);
                    //System.out.println("exec("+VcsUtilities.array2string(commandArr)+")");
                } else {
                    proc = Runtime.getRuntime().exec(commandArr, envp);
                    //System.out.println("exec("+VcsUtilities.array2string(commandArr)+", envp = "+envp+")");
                }
            }
            catch (IOException e){
                //E.err("Runtime.exec failed."); // NOI18N
                org.openide.TopManager.getDefault().notifyException(
                    org.openide.TopManager.getDefault().getErrorManager().annotate(e,
                    g("EXT_CMD_RuntimeExc", VcsUtilities.array2string(commandArr))));
                stderrNextLine(g("EXT_CMD_RuntimeFailed", command)); // NOI18N
                setExitStatus(VcsCommandExecutor.FAILED);
                return getExitStatus();
            }

            //watchDog = new WatchDog("VCS-WatchDog",timeoutMilis,Thread.currentThread(), proc); // NOI18N
            // timeout 0 means no dog is waitng to eat you
            //if (timeoutMilis > 0) {
            //    watchDog.start();
            //}
            //D.deb("New WatchDog with timeout = "+timeoutMilis); // NOI18N

            if (inputData != null) {
                try{
                    OutputStreamWriter os=new OutputStreamWriter(proc.getOutputStream());
                    //D.deb("stdin>>"+inputData); // NOI18N
                    //System.out.println("stdin>>"+inputData);
                    os.write(inputData);
                    os.flush();
                    os.close();
                }
                catch(IOException e){
                    E.err(e,"writeBytes("+inputData+") failed"); // NOI18N
                }
            }

            output = new OutputGrabber(proc.getInputStream(), proc.getErrorStream());
            synchronized (outputGrabbers) {
                outputGrabbers.add(output);
                //System.out.println("ExternalCommand.exec(): output grabber added.");
                outputGrabbers.notifyAll();
            }

            int exit = proc.waitFor();
            //D.deb("process exit="+exit); // NOI18N

            setExitStatus(exit == 0 ? VcsCommandExecutor.SUCCEEDED
                                    : VcsCommandExecutor.FAILED);
        }
        catch(InterruptedException e){
            D.deb("Ring from the WatchDog."); // NOI18N
            String[] commandArr=parseParameters(command);
            D.deb("commandArr="+VcsUtilities.arrayToString(commandArr)); // NOI18N
            //e.printStackTrace();
            proc.destroy();
            setExitStatus(VcsCommandExecutor.INTERRUPTED);
        } finally {
            D.deb("Processing command output"); // NOI18N
            //processCommandOutput();
            D.deb("watchDog.cancel()"); // NOI18N
            if (watchDog != null) watchDog.cancel();
            if (output != null) { // if exec() throws an exception, output == null !
                output.doStop();
                boolean finished = false;
                do {
                    try {
                        output.waitToFinish();
                        finished = true;
                    } catch (InterruptedException iexc) {
                        // It's dangerous to finish before output finishes
                        output.doReallyStop();
                    }
                } while (!finished);
            }
        }

        D.deb("exec() -> "+getExitStatus()); // NOI18N
        return getExitStatus();
    }

    /*
    private void processCommandOutput() {
      for(Enumeration elements = commandOutput.elements(); elements.hasMoreElements(); ) {
        String what = (String) elements.nextElement();
        if (elements.hasMoreElements()) {
          if (what.equals(STDOUT)) {
            stdoutNextLineCached((String) elements.nextElement());
          } else {
            stderrNextLineCached((String) elements.nextElement());
          }
        }
      }
}
    */

    //-------------------------------------------
    public String toString(){
        return command;
    }

    private class OutputGrabber extends Object implements SafeRunnable {
        
        private static final int LINE_LENGTH = 80;
        private static final int BUFF_LENGTH = 512;
        
        private InputStreamReader stdout;
        private InputStreamReader stderr;

        // Variables indicating EOF of command's output streams.
        // They are needed only for the OpenVMS patch.
        private boolean eof_stdout = false;
        private boolean eof_stderr = false;

        private boolean shouldStop = false;
        private boolean stopped = false;
        private boolean finished = false;
        private StringBuffer outBuffer = new StringBuffer(LINE_LENGTH);
        private StringBuffer errBuffer = new StringBuffer(LINE_LENGTH);
        private char[] buff = new char[BUFF_LENGTH];
        
        public OutputGrabber(InputStream stdout, InputStream stderr) {
            this.stdout = new InputStreamReader(stdout);
            this.stderr = new InputStreamReader(stderr);
        }
        
        /** Stop the grabber */
        public void doStop() {
            shouldStop = true;
        }
        
        public void doReallyStop() {
            // We really need to stop it!
            try {
                stdout.close();
                stderr.close();
            } catch (IOException ioexc) {}
        }
        
        /** Whether the grabber is stopped. If yes, should be flushed and garbage-collected. */
        public boolean isStopped() {
            try {
                //if (shouldStop && !stdout.ready() && !stderr.ready()) stopped = true;
                // If the OS is OpenVMS, we want to stop only if EOF has been reached on both output streams
                if (osType != Utilities.OS_VMS) {
                    if (shouldStop && !stdout.ready() && !stderr.ready()) stopped = true;
                } else {
                    if (shouldStop && eof_stdout && eof_stderr) {
                        stopped = true;
                    } else {
                        stopped = false;
                    }
                }
            } catch (IOException ioexc) {
                stopped = true;
            }
            return stopped;
        }
        
        public void waitToFinish() throws InterruptedException {
            synchronized (this) {
                while (!finished) {
                    wait();
                }
            }
        }
        
        /** Whether there is some output to grab */
        public boolean hasOutput() {
            boolean has;
            try {
                //has = stdout.ready() || stderr.ready();
                // If the OS is OpenVMS, just assume there is output available
                if (osType != Utilities.OS_VMS) {
                    has = stdout.ready() || stderr.ready();
                } else {
                    has = true;
                }
            } catch (IOException ioexc) {
                has = false;
            }
            return has;
        }
        
        /** Run the grabbing. It grabbs some of the output, needs to be invoked periodically
             intil it's not stopped. */
        public void run() {
            int n = 0;
            try {
                //if (stdout.ready() && (n = stdout.read(buff, 0, BUFF_LENGTH)) > -1) {
                // For OpenVMS, we need to see EOF before we're sure we've grabbed all output
                if (((osType == Utilities.OS_VMS) && !eof_stdout) || stdout.ready()) {
                    n = stdout.read(buff, 0, BUFF_LENGTH);
                }
                if (n > -1) {
                    for (int i = 0; i < n; i++) {
                        if (buff[i] == '\n') {
                            stdoutNextLine(outBuffer.toString());
                            outBuffer.delete(0, outBuffer.length());
                        } else {
                            if (buff[i] != 13) {
                                outBuffer.append(buff[i]);
                            }
                        }
                    }
                } else {
                    stopped = true;
                    eof_stdout = true;
                }
                //if (stderr.ready() && (n = stderr.read(buff, 0, BUFF_LENGTH)) > -1) {
                n = 0;
                // For OpenVMS, we need to see EOF before we're sure we've grabbed all output
                if (((osType == Utilities.OS_VMS) && !eof_stderr) || stderr.ready()) {
                    n = stderr.read(buff, 0, BUFF_LENGTH);
                }
                if (n > -1) {
                    for (int i = 0; i < n; i++) {
                        if (buff[i] == '\n') {
                            stderrNextLine(errBuffer.toString());
                            errBuffer.delete(0, errBuffer.length());
                        } else {
                            if (buff[i] != 13) {
                                errBuffer.append(buff[i]);
                            }
                        }
                    }
                } else {
                    stopped = true;
                    eof_stderr = true;
                }
            } catch (IOException ioexc) {
                stopped = true;
            }
        }
        
        /** Flush some remaining output. */
        public void flush() {
            if (outBuffer.length() > 0) stdoutNextLine(outBuffer.toString());
            if (errBuffer.length() > 0) stderrNextLine(errBuffer.toString());
            try {
                stdout.close();
            } catch (IOException ioexc) {}
            try {
                stderr.close();
            } catch (IOException ioexc1) {}
            finished = true;
            synchronized (this) {
                notifyAll();
            }
        }
        
    }
    
    private class OutputGrabbersProcessor extends Object implements Runnable {
        
        public void run() {
            //System.out.println("OutputGrabbersProcessor started.");
            do {
                try {
                    synchronized (outputGrabbers) {
                        //System.out.println("outputGrabbers.size = "+outputGrabbers.size());
                        while (outputGrabbers.size() == 0) {
                            //System.out.println(" waiting...");
                            outputGrabbers.wait();
                            //System.out.println(" notified.");
                        }
                    }
                    boolean processed = false;
                    int n = outputGrabbers.size();
                    //System.out.println("  numGrabbers = "+n);
                    for (int i = 0; i < n; i++) {
                        OutputGrabber output = (OutputGrabber) outputGrabbers.get(i);
                        //System.out.println("  output("+i+"): isStopped() = "+output.isStopped()+", hasOutput() = "+output.hasOutput());
                        if (!output.isStopped()) {
                            if (output.hasOutput()) {
                                output.run();
                                processed = true;
                            }
                        } else {
                            output.flush();
                            outputGrabbers.remove(i);
                            i--;
                            n--;
                        }
                    }
                    if (!processed) {
                        Thread.currentThread().sleep(200);
                    }
                } catch (InterruptedException iexc) {
                    break;
                } catch (ThreadDeath td) {
                    throw td;
                } catch (Throwable t) {
                    org.openide.TopManager.getDefault().notifyException(t);
                }
            } while(true);
        }
        
    }


    /**
     * Add a listener to the standard output with a specific regular expression.
     */
    public void addRegexOutputListener(RegexOutputListener l, String regex) throws BadRegexException {
        synchronized(stdOutDataLock) {
            if (stdOutDataListeners.contains(l)) return;
            RE pattern = null;
            try {
                pattern = new RE(regex);
            } catch(RESyntaxException e) {
                //E.err(e,"RE failed regexp"); // NOI18N
                throw new BadRegexException("Bad regexp.", e); // NOI18N
            }

            stdOutDataListeners.add(l);
            stdOutRegexps.add(pattern);
        }

    }


    /**
     * Add a listener to the standard error output with a specific regular expression.
     */
    public void addRegexErrorListener(RegexOutputListener l, String regex) throws BadRegexException {
        synchronized(stdErrDataLock) {
            if (stdErrDataListeners.contains(l)) return;
            RE pattern = null;
            try {
                pattern = new RE(regex);
            } catch(RESyntaxException e) {
                //E.err(e,"RE failed regexp"); // NOI18N
                throw new BadRegexException("Bad regexp.", e); // NOI18N
            }
            stdErrDataListeners.add(l);
            stdErrRegexps.add(pattern);
        }
    }


    /**
     * Add a listener to the standard output.
     */
    public void addTextOutputListener(TextOutputListener l) {
        synchronized(stdOutLock) {
            this.stdOutListeners.add(l);
        }
    }


    /**
     * Add a listener to the standard error output.
     */
    public void addTextErrorListener(TextOutputListener l) {
        synchronized(stdErrLock) {
            this.stdErrListeners.add(l);
        }
    }

    /**
     * Remove a standard output data listener.
     */
    public void removeRegexOutputListener(RegexOutputListener l) {
        synchronized(stdOutDataLock) {
            int index = stdOutDataListeners.indexOf(l);
            if (index < 0) return;
            stdOutDataListeners.remove(index);
            stdOutRegexps.remove(index);
        }
    }


    /**
     * Remove an error output data listener.
     */
    public void removeRegexErrorListener(RegexOutputListener l) {
        synchronized(stdErrDataLock) {
            int index = stdErrDataListeners.indexOf(l);
            if (index < 0) return;
            stdErrDataListeners.remove(index);
            stdErrRegexps.remove(index);
        }
    }


    //-------------------------------------------
    public static String[] matchToStringArray(RE pattern, String line) {
        Vector v=new Vector(5);
        if (!pattern.match(line)) {
            return new String[0];
        }
        for(int i=1; i < pattern.getParenCount(); i++){
            int subStart=pattern.getParenStart(i);
            int subEnd=pattern.getParenEnd(i);
            if (subStart >= 0 && subEnd > subStart)
                v.addElement(line.substring(subStart, subEnd));
        }
        int count=v.size();
        if (count <= 0) count = 1;
        String[]sa=new String[count];
        v.toArray(sa);
        return sa;
    }

    //-------------------------------------------
    private void stdoutNextLine(String line) {
        synchronized(stdOutDataLock) {
            int n = stdOutDataListeners.size();
            for (int i = 0; i < n; i++) {
                RE pattern = (RE) stdOutRegexps.get(i);
                String[] sa = matchToStringArray(pattern, line);
                if (sa != null && sa.length > 0) ((RegexOutputListener) stdOutDataListeners.get(i)).outputMatchedGroups(sa);
            }
        }
        synchronized(stdOutLock) {
            Iterator it = stdOutListeners.iterator();
            while(it.hasNext()) {
                ((TextOutputListener) it.next()).outputLine(line);
            }
        }
    }

    //-------------------------------------------
    private void stderrNextLine(String line) {
        synchronized(stdErrDataLock) {
            int n = stdErrDataListeners.size();
            for (int i = 0; i < n; i++) {
                RE pattern = (RE) stdErrRegexps.get(i);
                String[] sa = matchToStringArray(pattern, line);
                if (sa != null && sa.length > 0) ((RegexOutputListener) stdErrDataListeners.get(i)).outputMatchedGroups(sa);
            }
        }
        synchronized(stdErrLock) {
            Iterator it = stdErrListeners.iterator();
            while(it.hasNext()) {
                ((TextOutputListener) it.next()).outputLine(line);
            }
        }
    }

    //-------------------------------------------
    String g(String s) {
        return NbBundle.getBundle(ExternalCommand.class).getString (s);
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

