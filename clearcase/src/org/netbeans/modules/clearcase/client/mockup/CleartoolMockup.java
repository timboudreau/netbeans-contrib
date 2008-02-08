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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.clearcase.client.mockup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.versioning.util.Utils;
import org.openide.util.Exceptions;

/**
 *
 * @author tomas
 */
public class CleartoolMockup extends Process implements Runnable {

    private DelegateInputStream inputStream;    
    private ByteArrayOutputStream outputStream;
    private DelegateInputStream errorStream;

    private Exception throwable;
    private Thread thread;
    
    static final Logger LOG = Logger.getLogger("org.netbeans.modules.clearcase");
    private static String VOB_PATH = "/tmp/vob/";
    
    private String curPath = null;
    
    public CleartoolMockup() {
        outputStream = new ByteArrayOutputStream(200);            
        inputStream = new DelegateInputStream();        
        errorStream = new DelegateInputStream();
    }
    
    public void start() {
        thread = new Thread(this);
        thread.start();
    }
    
    private void process(String cmd) {         
        LOG.fine("Processing: " + cmd);
        if(cmd == null) {
            return;
        }
        cmd = cmd.trim();
        if (cmd.indexOf("i-am-finished-with-previous-command-sir") > -1) {                        
            try {
                while (inputStream.available() > 0) {
                    Thread.sleep(10);
                }
                errorStream.setDelegate(new ByteArrayInputStream("cleartool: Error: Unrecognized command: \"i-am-finished-with-previous-command-sir\"\n".getBytes()));
                //inputStream.setDelegate(new ByteArrayInputStream("\n".getBytes()));
                return;
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        if(cmd.equals("")) {
            return;
        }
        String[] args = cmd.split(" ");        
        String ctCommand = args[0];
        
        if(ctCommand.equals("ls")) {
             processLS(args);            
        } else if (ctCommand.equals("cd")) {
            processCD(args);    
        } else if(ctCommand.equals("checkin")) {
             processCI(args);            
        } else if(ctCommand.equals("checkout")) {
             processCO(args);            
        } else if(ctCommand.equals("lsco")) {
             processLSCO(args);            
        } else if(ctCommand.equals("mkelem")) {
             processMkElem(args);            
        } else if(ctCommand.equals("uncheckout")) {
             processUNCO(args);            
        } else if (ctCommand.equals("quit")) {
            if(thread != null) {
                //thread.destroy();
            }
        }      
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;   
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public InputStream getErrorStream() {
        return errorStream;
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }

    @Override
    public int exitValue() {
        return 0;
    }

    @Override
    public void destroy() {
        notifyAll();        
        if(throwable != null) {
            LOG.log(Level.SEVERE, null, throwable);
        }
    }

    public void run() {
        try {
                
            while(true) {
                try {                
                                       
                    StringBuffer sb = null;
                    byte[] byteArray = outputStream.toByteArray();
                    boolean done = true;
                    if(byteArray.length > 0) {
                        outputStream.reset();
                        if(done) {
                            sb = new StringBuffer(byteArray.length);
                        }
                        
                        for (byte b : byteArray) {
                            if(b == '\n') {                                   
                                process(sb.toString());                      
                                sb = new StringBuffer();
                                done = true;
                            } else {
                                sb.append(Character.toChars(b));
                                done = false;    
                            }                        
                            
                        }
                        
                    }
                    
                    Thread.sleep(10);

                } catch (InterruptedException ex) {
                    break;
                }
            }
        } finally {
            try { inputStream.close(); } catch (IOException alreadyClosed) { }            
            try { outputStream.close(); } catch (IOException alreadyClosed) { }            
            try { errorStream.close(); } catch (IOException alreadyClosed) { }            
        }
    }

    private void processCD(String[] args) {
        curPath = args[1].trim().substring(1);
        curPath = curPath.substring(0, curPath.length() - 1);
    }

    private void processCI(String[] args) {
        List<File> files = new ArrayList<File>();
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("-ptime") || 
               arg.equals("-identical") ||
               arg.equals("-ncomment")) 
            {
                // ignore
            } else if(arg.equals("-cfile") || arg.equals("-comment")) {
                i++; // skip the next arg
                continue;
            } else {
                files.add(new File(curPath + File.separator + arg));
            }
        }
        for (File file : files) {
            FileEntry fe = Repository.getInstance().getEntry(file);
            if(fe == null) {
                LOG.warning("No entry for to be checkedin file " + file);
                continue;
            }
            FileEntry newFe = new FileEntry(fe.getFile(), false, false, fe.getVersion() + 1);
            Repository.getInstance().addEntry(newFe);
        }
    }

    private void processCO(String[] args) {
        List<File> files = new ArrayList<File>();
        boolean reserved = true;
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("-ptime") || 
               arg.equals("-nquery") ||
               arg.equals("-ncomment")) 
            {
                // ignore
            } else if(arg.equals("-cfile") || arg.equals("-comment")) {
                i++; // skip the next arg
                continue;
            } else if(arg.equals("-reserved")) {
                reserved = true;
            } else if(arg.equals("-unreserved")) {
                reserved = false;
            } else {
                files.add(new File(curPath + File.separator + arg));
            }
        }
        for (File file : files) {
            FileEntry fe = Repository.getInstance().getEntry(file);
            if(fe == null) {
                LOG.warning("No entry for to be checkedout file " + file);
                continue;
            }
            FileEntry newFe = new FileEntry(fe.getFile(), true, reserved, fe.getVersion());
            Repository.getInstance().addEntry(newFe);
        }
                
    }

    private void processLS(String[] args) {
        boolean directory = false;
        File file = null;
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if(arg.startsWith("-d")) {
                directory = true;
            } else if (!arg.equals("-long")) {
                file = new File(arg);
            }
        }

        if(!file.getAbsolutePath().startsWith(VOB_PATH)) {
            errorStream.setDelegate(new ByteArrayInputStream(("cleartool: Error: Pathname is not within a VOB: \"" + file.getAbsolutePath() + "\"\n").getBytes()));    
        } else {
            if(!file.exists()) {
                FileEntry entry = Repository.getInstance().getEntry(file);
                if(entry == null) {
                    inputStream.setDelegate(new ByteArrayInputStream(("\n").getBytes()));    
                } else {
                    // XXX could be something else than checkedout?
                    inputStream.setDelegate(new ByteArrayInputStream(("version                " + file.getAbsolutePath() + "@@/main/CHECKEDOUT from /main/" + entry.getVersion() + " [checkedout but removed]\n").getBytes()));    
                }                
            } else {
                if(!directory && file.isDirectory()) {
                    File[] files = file.listFiles();
                    StringBuffer sb = new StringBuffer();
                    if(files == null) {
                        inputStream.setDelegate(new ByteArrayInputStream(("\n").getBytes()));    
                    } else {
                        for (File f : files) {
                            FileEntry fe = Repository.getInstance().getEntry(f);
                            if(fe == null) {
                                sb.append("view private object    ");
                                sb.append(f.getAbsolutePath());
                                sb.append('\n');    
                            } else {                                
                                sb.append("version                ");
                                sb.append(f.getAbsolutePath());
                                sb.append("@@");
                                sb.append(fe.isCheckedout() ? "/main/CHECKEDOUT from /main/" : "/main/");
                                sb.append(fe.getVersion());
                                sb.append("                     Rule: element * /main/LATEST");                                
                                sb.append('\n');    
                            }                            
                            inputStream.setDelegate(new ByteArrayInputStream(sb.toString().getBytes()));        
                        }
                        
                    }                    
                } else {
                    inputStream.setDelegate(new ByteArrayInputStream(("view private object    " + file.getAbsolutePath() + "\n").getBytes()));    
                }                
            }            
        }               
    }

    private void processLSCO(String[] args) {
        boolean directory = false;
        File file = null;
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if(arg.startsWith("-d")) {
                directory = true;
            } else if (arg.equals("-fmt") || arg.equals("-me") || arg.equals("-cview")) {
                i++;
                continue;
            } else {
                file = new File(arg);
            }
        }

        if(!file.getAbsolutePath().startsWith(VOB_PATH)) {
            errorStream.setDelegate(new ByteArrayInputStream(("cleartool: Error: Pathname is not within a VOB: \"" + file.getAbsolutePath() + "\"\n").getBytes()));    
        } else {
            if(!file.exists()) {
                FileEntry entry = Repository.getInstance().getEntry(file);
                if(entry == null) {
                    inputStream.setDelegate(new ByteArrayInputStream(("\n").getBytes()));    
                } 
            } else {
                StringBuffer sb = new StringBuffer();
                FileEntry dirfe = Repository.getInstance().getEntry(file);
                if(dirfe != null && dirfe.isCheckedout()) {
                    sb.append(file.getAbsolutePath());
                    sb.append("<~=~>amigo<~=~>");
                    sb.append(dirfe.isReserved() ? "reserved\n" : "unreserved\n");    
                }
                
                if(!directory && file.isDirectory()) {                    
                    File[] files = file.listFiles();
                    if(files != null) {
                        for (File f : files) {
                            FileEntry fe = Repository.getInstance().getEntry(f);
                            if(fe != null && fe.isCheckedout()) {
                                sb.append(f.getAbsolutePath());
                                sb.append("<~=~>amigo<~=~>");
                                sb.append(fe.isReserved() ? "reserved\n" : "unreserved\n");
                            }   
                        }
                    }                    
                }                
                if(sb.length() > 0) {
                    inputStream.setDelegate(new ByteArrayInputStream(sb.toString().getBytes()));            
                } else {
                    inputStream.setDelegate(new ByteArrayInputStream("\n".getBytes()));    
                }                
            }            
        }
    }

    private void processMkElem(String[] args) {
        boolean checkin = false;
        List<File> files = new ArrayList<File>();
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("-ptime") || 
               arg.equals("-mkpath") ||
               arg.equals("-nco") ||
               arg.equals("-ncomment")) 
            {
                // ignore
            } else if(arg.equals("-cfile") || arg.equals("-comment")) {
                i++; // skip the next arg
                continue;
            } else if(arg.equals("-ci")) {
                checkin = true;
            } else {
                files.add(new File(curPath + File.separator + arg));
            }
        }        
        for (File file : files) {
            FileEntry fe = new FileEntry(file, !checkin, false, 0);           
            Repository.getInstance().addEntry(fe);
        }            
    }

    private void processUNCO(String[] args) {
        boolean keep = false;
        List<File> files = new ArrayList<File>();
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("-rm")) {
                // ignore
            } else if(arg.equals("-keep")) {
                keep = true;
            } else {
                files.add(new File(curPath + File.separator + arg));
            }
        }                
        for (File file : files) {
            FileEntry fe = Repository.getInstance().getEntry(file);
            if(fe == null) {
                LOG.warning("No entry for to be checkedout file " + file);
                continue;
            }
            FileEntry newFe = new FileEntry(fe.getFile(), false, false, fe.getVersion());
            Repository.getInstance().addEntry(newFe);
            if(keep) {
                try {
                    Utils.copyStreamsCloseAll(new FileOutputStream(new File(file.getAbsolutePath() + ".keep")), new FileInputStream(file));
                } catch (IOException ex) {
                    CleartoolMockup.LOG.log(Level.WARNING, null, ex);
                } 
            }
        }
    }

}
