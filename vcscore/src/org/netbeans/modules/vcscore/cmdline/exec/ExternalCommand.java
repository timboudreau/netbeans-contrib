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

import org.openide.util.NbBundle;
import org.apache.regexp.*;

import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

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

    /*
    private volatile Vector commandOutput = null;
    private static final String STDOUT = "Following output comes from the Standard Output of the command:"; // NOI18N
    private static final String STDERR = "Following output comes from the Error Output of the command:"; // NOI18N
    */

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

    /**
     * Executes the external command.
     */
    public int exec(){
        //D.deb("exec()"); // NOI18N
        Process proc=null;
        Thread stdoutThread=null;
        Thread stderrThread=null;
        StdoutGrabber stdoutGrabber=null;
        StderrGrabber stderrGrabber=null;
        WatchDog watchDog = null;
        //commandOutput = new Vector();

        try{
            //D.deb("Thread.currentThread()="+Thread.currentThread()); // NOI18N

            String[] commandArr=parseParameters(command);
            D.deb("commandArr="+VcsUtilities.arrayToString(commandArr)); // NOI18N
            /*
            if (commandArr.toLowerCase().endsWith(".class")) {
              execClass(commandArr);
        }
            */
	    System.out.println ("In ExternalCommand::Exec");
	    if (envp != null) {
		for (int i=0; i<envp.length; i++)
		    System.out.println (envp[i]); 
	    }
	    
            try{
                if (envp == null) {
                    proc = Runtime.getRuntime().exec(commandArr);
                } else {
                    proc = Runtime.getRuntime().exec(commandArr, envp);
                }
            }
            catch (IOException e){
                E.err("Runtime.exec failed."); // NOI18N
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
                    DataOutputStream os=new DataOutputStream(proc.getOutputStream());
                    //D.deb("stdin>>"+inputData); // NOI18N
                    //System.out.println("stdin>>"+inputData);
                    os.writeChars(inputData);
                    os.flush();
                    os.close();
                }
                catch(IOException e){
                    E.err(e,"writeBytes("+inputData+") failed"); // NOI18N
                }
            }

            stdoutGrabber = new StdoutGrabber(proc.getInputStream());
            stdoutThread = new Thread(stdoutGrabber,"VCS-StdoutGrabber"); // NOI18N

            stderrGrabber = new StderrGrabber(proc.getErrorStream());
            stderrThread = new Thread(stderrGrabber,"VCS-StderrGrabber"); // NOI18N

            stdoutThread.start();
            stderrThread.start();

            int exit = proc.waitFor();
            //D.deb("process exit="+exit); // NOI18N

            //D.deb("stdoutThread.join()"); // NOI18N
            stdoutThread.join();

            //D.deb("stderrThread.join()"); // NOI18N
            stderrThread.join();

            //D.deb("watchDog.cancel()"); // NOI18N
            //watchDog.cancel();

            setExitStatus(exit == 0 ? VcsCommandExecutor.SUCCEEDED
                                    : VcsCommandExecutor.FAILED);
        }
        catch(InterruptedException e){
            D.deb("Ring from the WatchDog."); // NOI18N
            String[] commandArr=parseParameters(command);
            D.deb("commandArr="+VcsUtilities.arrayToString(commandArr)); // NOI18N
            //e.printStackTrace();
            //D.deb("Stopping StdoutGrabber."); // NOI18N
            stopThread(stdoutThread,stdoutGrabber);
            //D.deb("Stopping StderrGrabber."); // NOI18N
            stopThread(stderrThread,stderrGrabber);
            //D.deb("Destroy process."); // NOI18N
            proc.destroy();
            setExitStatus(VcsCommandExecutor.INTERRUPTED);
        } finally {
            D.deb("Processing command output"); // NOI18N
            //processCommandOutput();
            D.deb("watchDog.cancel()"); // NOI18N
            if (watchDog != null) watchDog.cancel();
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
    private boolean stopThread(Thread t, SafeRunnable r){

        // 1. be kind - just request stop
        r.doStop();
        long softTimeout=1000;
        try{
            t.join(softTimeout);
        }catch (InterruptedException e){
            D.deb(t.getName()+".join("+softTimeout+") after doStop() failed"); // NOI18N
            // TODO
        }
        if( t.isAlive()==false ){
            D.deb(t.getName()+" stopped after soft kill - great"); // NOI18N
            return true;
        }

        // 2. be more hard - hey thread - do stop
        t.interrupt();
        long hardTimeout=1000;
        try{
            t.join(hardTimeout);
        }catch (InterruptedException e){
            D.deb(t.getName()+".join("+hardTimeout+") failed"); // NOI18N
            // TODO
        }
        if( t.isAlive()==false ){
            D.deb(t.getName()+" stopped after hard kill - good"); // NOI18N
            return true;
        }

        /* Commented out, since Thread.stop is deprecated.
        // 3. last resort
        t.stop();
        long stopTimeout=1000;
        try{
            t.join(stopTimeout);
        }catch (InterruptedException e){
        }
        if(t.isAlive()==false ){
            D.deb(t.getName()+" stopped after stop() - at last"); // NOI18N
            return true;
        }
         */

        E.err("This shouldn't happen "+t.getName()+" is alive="+t.isAlive()); // NOI18N
        return false;
    }

    //-------------------------------------------
    public String toString(){
        return command;
    }


    //-------------------------------------------
    private class StdoutGrabber implements SafeRunnable {
        private Debug D=new Debug("StdoutGrabber",true); // NOI18N
        private boolean shouldStop=false;
        private InputStreamReader is=null;

        //-------------------------------------------
        public StdoutGrabber(InputStream is){
            this.is = new InputStreamReader(is);
        }

        //-------------------------------------------
        public void doStop(){
            shouldStop=true;
        }

        //-------------------------------------------
        private void close(){
            if(is!=null){
                try{
                    is.close();
                }catch (IOException e){
                    //E.err(e,"close() failed"); // NOI18N
                }
            }
        }

        //-------------------------------------------
        public void run(){
            //D.deb("stdout: run()"); // NOI18N
            StringBuffer sb=new StringBuffer(80);
            int b=-1;
            try{
                while( (b=is.read()) > -1 ){
                    char c = (char) b;
                    if( c== '\n' ){
                        String line=new String(sb);
                        //D.deb("stdout: <<"+line); // NOI18N
                        stdoutNextLine(line);
                        sb=new StringBuffer(80);
                    } else {
                        if( b!=13 ){
                            sb.append(c);
                        }
                    }
                    if(shouldStop){
                        D.deb("we should stop..."); // NOI18N
                        return;
                    }
                }
            }
            catch(InterruptedIOException e){
                D.deb("stdout: InterruptedIOException"); // NOI18N
            }
            catch(IOException e){
                E.err(e,"stdout: read() failed"); // NOI18N
            }
            finally{
                close();
            }
            //D.deb("stdout: run() finished"); // NOI18N
        }

    } //StdoutGrabber


    //-------------------------------------------
    private class StderrGrabber implements SafeRunnable {
        private Debug D=new Debug("StderrGrabber",true); // NOI18N
        private boolean shouldStop=false;
        private InputStreamReader is=null;

        //-------------------------------------------
        public StderrGrabber(InputStream is){
            this.is = new InputStreamReader(is);
        }

        //-------------------------------------------
        public void doStop(){
            shouldStop=true;
        }

        //-------------------------------------------
        private void close(){
            if(is!=null){
                try{
                    is.close();
                }catch (IOException e){
                    //E.err(e,"close() failed"); // NOI18N
                }
            }
        }

        //-------------------------------------------
        public void run(){
            //D.deb("stderr: run()"); // NOI18N
            StringBuffer sb=new StringBuffer(80);
            int b=-1;
            try{
                while( (b=is.read()) > -1 ){
                    char c=(char)b;
                    if( c== '\n' ){
                        String line=new String(sb);
                        //D.deb("stderr: <<"+line); // NOI18N
                        stderrNextLine(line);
                        sb=new StringBuffer(80);
                    } else {
                        if( b!=13 ){
                            sb.append(c);
                        }
                    }
                    if(shouldStop){
                        D.deb("we should stop..."); // NOI18N
                        return;
                    }
                }
            }
            catch(InterruptedIOException e){
                D.deb("stderr: InterruptedIOException"); // NOI18N
            }catch(IOException e){
                E.err(e,"stderr: read() failed"); // NOI18N
            }
            finally{
                close();
            }
            //D.deb("stderr: run() finished"); // NOI18N
        }
    } //StderrGrabber


    /**
     * Add a listener to the standard output with a specific regular expression.
     */
    public void addStdoutRegexListener(CommandDataOutputListener l, String regex) throws BadRegexException {
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
    public void addStderrRegexListener(CommandDataOutputListener l, String regex) throws BadRegexException {
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
    public void addStdoutListener(CommandOutputListener l) {
        synchronized(stdOutLock) {
            this.stdOutListeners.add(l);
        }
    }


    /**
     * Add a listener to the standard error output.
     */
    public void addStderrListener(CommandOutputListener l) {
        synchronized(stdErrLock) {
            this.stdErrListeners.add(l);
        }
    }

    /**
     * Remove a standard output data listener.
     */
    public void removeStdoutRegexListener(CommandDataOutputListener l) {
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
    public void removeStderrRegexListener(CommandDataOutputListener l) {
        synchronized(stdErrDataLock) {
            int index = stdErrDataListeners.indexOf(l);
            if (index < 0) return;
            stdErrDataListeners.remove(index);
            stdErrRegexps.remove(index);
        }
    }


    //-------------------------------------------
    private String[] matchToStringArray(RE pattern, String line) {
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
                if (sa != null && sa.length > 0) ((CommandDataOutputListener) stdOutDataListeners.get(i)).outputData(sa);
            }
        }
        synchronized(stdOutLock) {
            Iterator it = stdOutListeners.iterator();
            while(it.hasNext()) {
                ((CommandOutputListener) it.next()).outputLine(line);
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
                if (sa != null && sa.length > 0) ((CommandDataOutputListener) stdErrDataListeners.get(i)).outputData(sa);
            }
        }
        synchronized(stdErrLock) {
            Iterator it = stdErrListeners.iterator();
            while(it.hasNext()) {
                ((CommandOutputListener) it.next()).outputLine(line);
            }
        }
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

