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

package test.app;

import java.io.*;
import org.netbeans.junit.AssertionFailedErrorException;

public class MyProcessExecutor extends Thread {
    
    BufferedReader br;
    PrintWriter pw;
    String cmd;
    PrintStream output;
    PrintStream info;
    Process proc;
    int skip, read;
    String firstLine;
    
    public MyProcessExecutor(String _cmd, PrintStream _output, PrintStream _info, int _skip, int _read) {
        cmd = _cmd;
        output = _output;
        info = _info;
        skip = _skip;
        read = _read;
        try {
            System.out.println ("COMMAND: " + cmd);
            proc = Runtime.getRuntime().exec (cmd);
            br = new BufferedReader (new InputStreamReader (proc.getInputStream()));
            pw = new PrintWriter (proc.getOutputStream(), true);
        } catch (IOException e) {
            if (info != null) {
                info.println ("IOException while starting command: " + cmd);
                e.printStackTrace (info);
            }
            throw new AssertionFailedErrorException ("IOException while starting command: " + cmd, e);
        }
    }
    
    public void run () {
        try {
            for (;;) {
                String str = br.readLine();
                if (str == null)
                    break;
                if (info != null)
                    info.println (str);
                System.out.println ("OUTPUT:" + str);
                if (firstLine == null)
                    firstLine = str;
                if (skip > 0)
                    skip --;
                else {
                    if (output != null)
                        output.println (str);
                    read --;
                    if (read <= 0)
                        break;
                }
            }
        } catch (IOException e) {
            if (info != null) {
                info.println ("IOException while running command: " + cmd);
                e.printStackTrace (info);
            }
        } finally {
            proc.destroy();
        }
    }
    
    public void input (String line) {
        pw.println (line);
        if (info != null)
            info.println (line);
        System.out.println ("INPUT:" + line);
    }
    
    public void destroy () {
        if (proc != null)
            proc.destroy();
        interrupt();
    }
    
    public String getFirstLine () {
        return firstLine;
    }
        
}
