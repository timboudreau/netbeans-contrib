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


/** Single external command to be executed. See {@link TestCommand} for typical usage.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class ExternalCommand {
    private Debug E=new Debug("ExternalCommand",true); // NOI18N
    private Debug D=new Debug("ExternalCommand",true); // NOI18N

    public static final int SUCCESS=0;
    public static final int FAILED=1;
    public static final int FAILED_ON_TIMEOUT=2;

    private String command=null;
    private long timeoutMilis = 0;
    private int exitStatus=SUCCESS;
    private String inputData=null;

    private Object stdoutLock=new Object();
    private RegexListener[] stdoutListeners=new RegexListener[0];
    private RE[] stdoutRegexps=new RE[0];

    private Object stderrLock=new Object();
    private RegexListener[] stderrListeners=new RegexListener[0];
    private RE[] stderrRegexps=new RE[0];
    private Object stdOutErrLock = new Object(); // synchronizes stdout and stderr

    private Vector stdoutNoRegexListeners = new Vector();
    private Vector stderrNoRegexListeners = new Vector();

    /*
    private volatile Vector commandOutput = null;
    private static final String STDOUT = "Following output comes from the Standard Output of the command:"; // NOI18N
    private static final String STDERR = "Following output comes from the Error Output of the command:"; // NOI18N
    */

    //-------------------------------------------
    public ExternalCommand(){
    }

    //-------------------------------------------
    public ExternalCommand(String command){
        setCommand(command);
    }

    //-------------------------------------------
    public ExternalCommand(String command, long timeoutMilis){
        setCommand(command);
        setTimeout(timeoutMilis);
    }

    //-------------------------------------------
    public ExternalCommand(String command, long timeoutMilis, String input){
        setCommand(command);
        setTimeout(timeoutMilis);
        setInput(input);
    }



    //-------------------------------------------
    public void setCommand(String command){
        this.command=command;
    }


    //-------------------------------------------
    public void setTimeout(long timeoutMilis){
        this.timeoutMilis=timeoutMilis;
    }


    //-------------------------------------------
    public void setInput(String inputData){
        this.inputData=inputData;
    }


    //-------------------------------------------
    private void setExitStatus(int exitStatus){
        this.exitStatus=exitStatus;
    }

    //-------------------------------------------
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

    //-------------------------------------------
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
            D.deb("commandArr="+MiscStuff.arrayToString(commandArr)); // NOI18N
            /*
            if (commandArr.toLowerCase().endsWith(".class")) {
              execClass(commandArr);
        }
            */
            try{
                proc=Runtime.getRuntime().exec(commandArr);
            }
            catch (IOException e){
                E.err("Runtime.exec failed."); // NOI18N
                stderrNextLine(g("EXT_CMD_RuntimeFailed", command)); // NOI18N
                setExitStatus(FAILED);
                return getExitStatus();
            }

            watchDog=new WatchDog("VCS-WatchDog",timeoutMilis,Thread.currentThread(), proc); // NOI18N
            // timeout 0 means no dog is waitng to eat you
            if (timeoutMilis>0) {
                watchDog.start();
            }
            //D.deb("New WatchDog with timeout = "+timeoutMilis); // NOI18N

            stdoutGrabber=new StdoutGrabber(proc.getInputStream());
            stdoutThread=new Thread(stdoutGrabber,"VCS-StdoutGrabber"); // NOI18N

            stderrGrabber=new StderrGrabber(proc.getErrorStream());
            stderrThread=new Thread(stderrGrabber,"VCS-StderrGrabber"); // NOI18N

            stdoutThread.start();
            stderrThread.start();

            if( inputData!=null ){
                try{
                    DataOutputStream os=new DataOutputStream(proc.getOutputStream());
                    //D.deb("stdin>>"+inputData); // NOI18N
                    os.writeChars(inputData);
                    os.flush();
                    os.close();
                }
                catch(IOException e){
                    E.err(e,"writeBytes("+inputData+") failed"); // NOI18N
                }
            }

            int exit=proc.waitFor();
            //D.deb("process exit="+exit); // NOI18N

            //D.deb("stdoutThread.join()"); // NOI18N
            stdoutThread.join();

            //D.deb("stderrThread.join()"); // NOI18N
            stderrThread.join();

            //D.deb("watchDog.cancel()"); // NOI18N
            //watchDog.cancel();

            setExitStatus( exit==0 ? SUCCESS : FAILED );
        }
        catch(InterruptedException e){
            D.deb("Ring from the WatchDog."); // NOI18N
            String[] commandArr=parseParameters(command);
            D.deb("commandArr="+MiscStuff.arrayToString(commandArr)); // NOI18N
            //e.printStackTrace();
            //D.deb("Stopping StdoutGrabber."); // NOI18N
            stopThread(stdoutThread,stdoutGrabber);
            //D.deb("Stopping StderrGrabber."); // NOI18N
            stopThread(stderrThread,stderrGrabber);
            //D.deb("Destroy process."); // NOI18N
            proc.destroy();
            setExitStatus(FAILED_ON_TIMEOUT);
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
    public class StdoutGrabber implements SafeRunnable {
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
    public class StderrGrabber implements SafeRunnable {
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


    //-------------------------------------------
    private RegexListener[] addListener(RegexListener[] la, RegexListener l){
        int len=la.length;
        RegexListener[] nla=new RegexListener[len+1];
        System.arraycopy(la,0,nla,0,len);
        nla[len]=l;
        return nla;
    }


    //-------------------------------------------
    private RE[] addRegex(RE[] ra, RE r){
        int len=ra.length;
        RE[] nra=new RE[len+1];
        System.arraycopy(ra,0,nra,0,len);
        nra[len]=r;
        return nra;
    }


    //-------------------------------------------
    public void addStdoutRegexListener(RegexListener l, String regex) throws BadRegexException {
        synchronized(stdoutLock){
            int len=stdoutListeners.length;
            for(int i=0;i<len;i++){
                if( stdoutListeners[i]==l ){
                    return;
                }
            }

            RE pattern=null;
            try{
                pattern=new RE(regex);
            }catch(RESyntaxException e){
                //E.err(e,"RE failed regexp"); // NOI18N
                throw new BadRegexException("Bad regexp.",e); // NOI18N
            }

            stdoutListeners=addListener(stdoutListeners,l);
            stdoutRegexps=addRegex(stdoutRegexps,pattern);
        }

    }


    //-------------------------------------------
    public void addStderrRegexListener(RegexListener l, String regex) throws BadRegexException {
        synchronized(stderrLock){
            int len=stderrListeners.length;
            for(int i=0;i<len;i++){
                if( stderrListeners[i]==l ){
                    return;
                }
            }

            RE pattern=null;
            try{
                pattern=new RE(regex);
            }catch(RESyntaxException e){
                //E.err(e,"RE failed regexp"); // NOI18N
                throw new BadRegexException("Bad regexp.",e); // NOI18N
            }

            stderrListeners=addListener(stderrListeners,l);
            stderrRegexps=addRegex(stderrRegexps,pattern);
        }
    }


    //-------------------------------------------
    public void addStdoutNoRegexListener(NoRegexListener l) {
        synchronized(stdoutLock){
            this.stdoutNoRegexListeners.addElement(l);
        }
    }


    //-------------------------------------------
    public void addStderrNoRegexListener(NoRegexListener l) {
        synchronized(stderrLock){
            this.stderrNoRegexListeners.addElement(l);
        }
    }

    //-------------------------------------------
    private int findInArray(RegexListener[] la, RegexListener l){
        int len=la.length;
        for(int i=0;i<len;i++){
            if(la[i]==l){
                return i;
            }
        }
        return -1;
    }


    //-------------------------------------------
    private RegexListener[] removeListenerAt(RegexListener[] la, int index){
        int len=la.length;
        RegexListener[] nla=new RegexListener[len-1];
        /* e.g. We want to remove second element in 'la' index=1; len=4
        la     = [ a, b, c, d ]
        nla    = [ 0, 0, 0 ]
        index  =      1   
          */
        System.arraycopy(la,0,nla,0,len-1);
        /*
        la      = [ a, b, c, d ]
        nla     = [ a, b, c ]
        index   =      1
        */
        if( index!=len-1 ){
            nla[index]=la[len-1];
        }
        /*
          stdoutListeners = [ a, b, c, d ]
          nla             = [ a, d, c ]
          index           =      1
        */
        return nla;
    }


    //-------------------------------------------
    private RE[] removeRegexAt(RE[] ra, int index){
        int len=ra.length;
        RE[] nra=new RE[len-1];
        System.arraycopy(ra,0,nra,0,len-1);
        if( index != len-1 ){
            nra[index]=ra[len-1];
        }
        return nra;
    }


    //-------------------------------------------
    public void removeStdoutRegexListener(RegexListener l){
        synchronized(stdoutLock){
            int index=findInArray(stdoutListeners,l);
            if(index<0){
                return;
            }
            stdoutListeners=removeListenerAt(stdoutListeners,index);
            stdoutRegexps=removeRegexAt(stdoutRegexps,index);
        }
    }


    //-------------------------------------------
    public void removeStderrRegexListener(RegexListener l){
        synchronized(stderrLock){
            int index=findInArray(stderrListeners,l);
            if(index<0){
                return;
            }
            stderrListeners=removeListenerAt(stderrListeners,index);
            stderrRegexps=removeRegexAt(stderrRegexps,index);
        }
    }


    //-------------------------------------------
    private String[] matchToStringArray(RE pattern, String line){
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

    /*
    public synchronized void stdoutNextLine(String line){
      synchronized(stdOutErrLock) {
        commandOutput.addElement(STDOUT);
        commandOutput.addElement(line);
      }
}
    */

    //-------------------------------------------
    public synchronized void stdoutNextLine(String line){
        synchronized(stdOutErrLock) {
            synchronized(stdoutLock){
                //D.deb("stdout <<"+line); // NOI18N
                int len=stdoutListeners.length;
                for(int i=0;i<len;i++){
                    RE pattern=stdoutRegexps[i];
                    String[] sa=matchToStringArray(pattern, line);
                    if (sa != null && sa.length > 0) stdoutListeners[i].match(sa);
                }
                // call No Regex Listeners, which match the whole line
                Enumeration enum = stdoutNoRegexListeners.elements();
                while(enum.hasMoreElements()) {
                    ((NoRegexListener) enum.nextElement()).match(line);
                }
            }
        }
    }

    /*
    public synchronized void stderrNextLine(String line){
      synchronized(stdOutErrLock) {
        commandOutput.addElement(STDERR);
        commandOutput.addElement(line);
      }
}
    */

    //-------------------------------------------
    public void stderrNextLine(String line){
        synchronized(stdOutErrLock) {
            synchronized(stderrLock){
                //D.deb("stderr <<"+line); // NOI18N
                int len=stderrListeners.length;
                for(int i=0;i<len;i++){
                    RE pattern=stderrRegexps[i];
                    String[] sa=matchToStringArray(pattern, line);
                    if (sa != null && sa.length > 0) stderrListeners[i].match(sa);
                }
                // call No Regex Listeners, which match the whole line
                Enumeration enum = stderrNoRegexListeners.elements();
                while(enum.hasMoreElements()) {
                    ((NoRegexListener) enum.nextElement()).match(line);
                }
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


/*
 * Log
 *  16   Jaga      1.10.1.4    4/11/00  Petr Jiricka    New RegExp in 
 *       org.openide.util
 *  15   Jaga      1.10.1.3    3/21/00  Martin Entlicher Fixed for different 
 *       character sets.
 *  14   Jaga      1.10.1.2    3/9/00   Martin Entlicher Change back the output 
 *       to be fired immediatelly.
 *  13   Jaga      1.10.1.1    3/8/00   Martin Entlicher Change the stdout and 
 *       errout output to be buffered for synchronization reasons.
 *  12   Jaga      1.10.1.0    2/24/00  Martin Entlicher Default timeout 
 *       increased.
 *  11   Gandalf   1.10        2/8/00   Martin Entlicher 
 *  10   Gandalf   1.9         1/15/00  Ian Formanek    NOI18N
 *  9    Gandalf   1.8         1/6/00   Martin Entlicher 
 *  8    Gandalf   1.7         12/20/99 Martin Entlicher 
 *  7    Gandalf   1.6         12/16/99 Martin Entlicher 
 *  6    Gandalf   1.5         11/30/99 Martin Entlicher 
 *  5    Gandalf   1.4         10/25/99 Pavel Buzek     
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         10/7/99  Pavel Buzek     
 *  2    Gandalf   1.1         10/5/99  Pavel Buzek     VCS at least can be 
 *       mounted
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $
 */

