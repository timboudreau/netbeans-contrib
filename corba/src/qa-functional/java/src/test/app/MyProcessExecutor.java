/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
//            System.out.println ("COMMAND: " + cmd);
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
//                System.out.println ("OUTPUT:" + str);
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
//        System.out.println ("INPUT:" + line);
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
