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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd;

/**
 * CVS password file .cvspass maintainer.
 * @author  mkleint
 */

import java.io.*;
import java.net.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.lib.cvsclient.CVSRoot;

import org.openide.filesystems.*;
import org.openide.*;
import org.openide.util.*;

import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;

/** The purpose of this class is to handle everything around pserver authentication 
 * and the .cvspass file stuff. It finds, reads and writes to the file. If the configuration is not found in the file,
 * it can connect to the server and check it. Then adds the item to the .cvspass file
 */
public class CVSPasswd extends Object {
    
    private static final byte[] scramblingTableAZ = {57, 83, 43, 46, 102, 40, 89, 38, 103, 45, 50,
            42, 123, 91, 35, 125, 55, 54, 66, 124, 126, 59,
            47, 92, 71, 115 };
    private static final byte[] scramblingTableaz = {121, 117, 104, 101, 100, 69, 73, 99, 63, 94, 93,
            39, 37, 61, 48, 58, 113, 32, 90, 44, 98, 60, 51,
            33, 97, 62};
    private static final byte[] scramblingTableKeys = {(byte) '!', (byte) '"', (byte) '%', (byte) '&', (byte) '\'', (byte) '(', (byte) ')', (byte) '*', (byte) '+', (byte) ',', (byte) '-', (byte) '.', (byte) '/', 
        (byte) '_', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) ':', (byte) ';', (byte) '<', (byte) '=', (byte) '>', (byte) '?'};
    private static final byte[] scramblingTableValues = {120, 53, 109, 72, 108, 70, 64, 76, 67, 116, 74, 68, 87, 56, 111, 52, 75, 119, 49,34,82,81,95,65, 112, 86, 118, 110, 122, 105 };


    private static final String BEGIN_AUTH_REQUEST = "BEGIN AUTH REQUEST";
    private static final String END_AUTH_REQUEST = "END AUTH REQUEST";
    private static final String AUTH_SUCCESSFULL = "I LOVE YOU\n";
    
    public static final int STANDARD_PSERVER_PORT = 2401;
                                                                                                                                                                  
    /** The standard name of the file, where CVS stores passwords and cvs root directories
     * (for pserver type of connection only)
     */
    public static final String STD_FILE = ".cvspass";
    private LinkedList entries = new LinkedList();
    /** The list of unrecognized lines, which are written back when .cvspass file is saved */
    private List unrecognizedLines = new ArrayList();
    
    private File passFile = null;
    
    /** Creates new CVSPasswd */
    public CVSPasswd(String dir, String fileName) {
      String sep = File.separator;  
      if (File.separatorChar == '\\') {
         sep = "\\\\";
      } else {
      }           
      passFile = new File(dir + sep + fileName);
      //System.out.println("NOT USED: passFile = "+passFile);
      loadPassFile();
    }

    /* Not ever used:
    public CVSPasswd(String dirAndFile) {
      this(new File(dirAndFile));
    }
    */
    
    /** This is the preffered constuctor. Since it tries to do the standard cvs way
     * of finding the .cvspass file. For  Windoze we need to add some backslashes. 
     * @args cygwinPath for Windoze95/98 we need CygWin to work
     */
    public CVSPasswd(String cygwinPath) {
      String dir = getHome();
      String sep = File.separator;  
      if (File.separatorChar == '\\') {
         sep = "\\\\";
//         D("win!");
      } else {
      }           
      passFile = new File(dir + sep + STD_FILE);
 //     D("HOME=" +dir + sep + STD_FILE);
      //System.out.println("passFile = "+passFile);
      loadPassFile();
        
    }
    
    /** Get the home directory for .cvspass and possibly other cvs configuration files.
     * Looks for HOME env variable, on Windows for HOMEDRIVE + HOMEPATH.
     * If it can not be retrieved from environmental variables, return the user.home at least.
     * @return the home directory
     */
    public String getHome() {
        String home = System.getenv("HOME");
        if (home == null && Utilities.isWindows()) {
            String homePath = System.getenv("homepath");
            String homeDrive = System.getenv("homedrive");
            if (homePath != null && homeDrive != null) {
                home = homeDrive + homePath;
            }
        }
        if (home == null) home = System.getProperty("user.home");
        return home;
    }
    
    /** Reads the .cvspass file.
     */ 
    
    public boolean loadPassFile() {
      //System.out.println("loadPassFile("+passFile+")");
      entries = new LinkedList();
      unrecognizedLines = new ArrayList();
      BufferedReader bf = null;
      try {
        bf = new BufferedReader(new FileReader(passFile.getAbsolutePath()));
        String line;
        do {
          line = bf.readLine();
          if (line != null) {
            PasswdEntry entr = new PasswdEntry();
            boolean ok = entr.setEntry(line);
            if (!ok) {
              unrecognizedLines.add(line);
              continue;
            } else {
              entries.add(entr);
            }
          }  
        } while (line != null); 
        bf.close();
      } catch (IOException exc) {
         if (bf != null) {
             try {
                bf.close();
             } catch (IOException ioex) {}
         }    
         return false;
      }
      return true;
    }
    
    /** Writes the current passwd database to the .cvspass file.
     */
    public boolean savePassFile() {
        //System.out.println("savePassFile("+passFile+")");
      PrintWriter bf = null;
      try {
//        if (!passFile.canWrite()) return false;
        bf = new PrintWriter(new BufferedWriter(new FileWriter(passFile.getAbsolutePath(), false)));
        ListIterator lit = entries.listIterator();
        while (lit.hasNext()) {
          PasswdEntry ent = (PasswdEntry) lit.next();
          bf.println(ent.getEntry());
        }
        for (Iterator it = unrecognizedLines.iterator(); it.hasNext(); ) {
            bf.println((String) it.next());
        }
        bf.close();
        
      } catch (IOException exc) {
        javax.swing.SwingUtilities.invokeLater(new Runnable () {
           public void run () {
              DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message(org.openide.util.NbBundle.getBundle(CVSPasswd.class).getString("CVSPasswd.errorWritingPass")));
           }
         });
         if (bf != null) {
            bf.close();
         }                               
         return false;
      }
      return true;
    }

    /** Adds a new entry into the dtabase. Will be written to the file after savePassFile is run.
     * @param entry - entry supplied by the Customizer
     * @param port - a port number. If non-zero, it will be added into the entry.
     * @param passwd unscrambled password for the account
     * @return the added PasswdEntry. if not added, then null.
     */
    public PasswdEntry add(String entry, int port, String passwd) {
      PasswdEntry psw = new PasswdEntry();
      String scrambled = StandardScrambler.getInstance().scramble(passwd);
      //String scrambled = scramble(passwd);
      boolean ok = psw.setEntry(entry + " " + scrambled);
      if (ok) {
        if (port != 0) psw.getCVSRoot().setPort(port);
        entries.add(psw); 
        return psw;
      }
      return null;
    }
    
    /**
     * Remove the entry from the current set of entries.
     */
    public void remove(String entry, int port) throws IllegalArgumentException {
        PasswdEntry ent = find(entry, port);
        if (ent != null) {
            entries.remove(ent);
        }
    }
    
    /*
    public PasswdEntry add(String type, String user, String server, String root, String passwd) {
      String ent = ":" + type + ":" + user + "@" + server + ":" + root;
      return add(ent, passwd); 
    }
     */
    
   
/** Looks for the  current setting (:pserver:username@server:/rootdir) in the database.
 * @return the found PasswdEntry, or if not found null.
 * @param current The current cvs root directory
 */
    public PasswdEntry find(String current, int port) throws IllegalArgumentException {
      CVSRoot currentRootWithPort = CVSRoot.parse(current);
      if (port > 0) currentRootWithPort.setPort(port);
      if (currentRootWithPort.getPort() == 0) currentRootWithPort.setPort(STANDARD_PSERVER_PORT);
      //CVSRoot currentRootWithPort;
      CVSRoot currentRootWithoutPort; // We'll also check entry that does not have the port associated
      if (currentRootWithPort.getPort() == STANDARD_PSERVER_PORT) {
          currentRootWithoutPort = CVSRoot.parse(current);
          currentRootWithoutPort.setPort(0);
      } else {
          currentRootWithoutPort = null;
      }
      ListIterator it = entries.listIterator();
      PasswdEntry ent = null; 
      String toReturn = null;
      while (it.hasNext()) {
        ent = (PasswdEntry) it.next();
        CVSRoot cvsroot = ent.getCVSRoot();
        int compat = cvsroot.getCompatibilityLevel(currentRootWithPort);
        if (compat == 0 || compat == 1) {
            return ent;
        }
        if (currentRootWithoutPort != null) {
            compat = cvsroot.getCompatibilityLevel(currentRootWithoutPort);
            if (compat == 0 || compat == 1) {
                return ent;
            }
        }
      }
      return null;
    }
    

/* The method does the CVS pserver-style scramling of the password. 
 * See CVS client/server specificaion for details.
 * (It's definitely not secure encryption.)
 * @return The encrypted password
 * @param str the string to encrypt.
 *
  USE StandardScrambler instead !!
 
    public static final String scramble(String str) {
        String astr = new String("A"+str);
        byte[] bytes = astr.getBytes();
        for(int i = 1; i < bytes.length; i++) {
            if (bytes[i] >= 'a' && bytes[i] <= 'z') {
                bytes[i] = scramblingTableaz[bytes[i] - 'a'];
            } else if (bytes[i] >= 'A' && bytes[i] <= 'Z') {
                bytes[i] = scramblingTableAZ[bytes[i] - 'A'];
            } else {
                int j = 0;
                while(j < scramblingTableKeys.length && scramblingTableKeys[j] != bytes[i]) j++;
                if (j < scramblingTableKeys.length) bytes[i] = scramblingTableValues[j];
            }
        }
        return new String(bytes);
    }
 */
  

    /**
     * Check the login by running a command which will succeed when the right password is entered.
     * @return true if authentification succeed, othewise false.
     * @param fs the CVS filesystem to take the command from
     * @param message returns the error message
     * @return true when the login was successfull, false otherways
     */
   // public static boolean checkLogin(VcsFileSystem fs, StringBuffer message) throws UnknownHostException, IOException {
    public static boolean checkLogin(CommandExecutionContext context, StringBuffer message) throws UnknownHostException, IOException {
        return checkLogin(context, message, null);
    }
    
    public static boolean checkLogin(CommandExecutionContext context, StringBuffer message,
                                     java.util.Map vars) throws UnknownHostException, IOException {
        CommandSupport support = context.getCommandSupport("LOGIN_CHECK");
         if (support == null) return true;
        Command cmd = support.createCommand();
        if (vars != null && cmd instanceof VcsDescribedCommand) {
            ((VcsDescribedCommand) cmd).setAdditionalVariables(vars);
        }
        // I have no way to check the login => believe that it is O.K.
        //final ExecuteCommand es = new ExecuteCommand(fs, cmd, fs.getVariablesAsHashtable());
        TextOutputCommand txtCmd = (TextOutputCommand) cmd; 
        final StringBuffer loginCommandOutput = new StringBuffer();
        txtCmd.addTextErrorListener(new CommandOutputListener() {
            public void outputLine(String element) {
                if (element != null) {
                    loginCommandOutput.delete(0, loginCommandOutput.length());
                    loginCommandOutput.append(element+"\n");
                }
            }
        });
        txtCmd.addTextOutputListener(new CommandOutputListener() {
            public void outputLine(String element) {
                if (element != null) {
                    loginCommandOutput.delete(0, loginCommandOutput.length());
                    loginCommandOutput.append(element+"\n");
                }
            }
        });
    /*    VcsCommandExecutor vce = fs.getVcsFactory().getCommandExecutor(cmd, fs.getVariablesAsHashtable());
        final StringBuffer loginCommandOutput = new StringBuffer();
        vce.addErrorOutputListener(new CommandOutputListener() {
            public void outputLine(String element) {
                if (element != null) {
                    loginCommandOutput.delete(0, loginCommandOutput.length());
                    loginCommandOutput.append(element+"\n");
                }
            }
        });
        vce.addOutputListener(new CommandOutputListener() {
            public void outputLine(String element) {
                if (element != null) {
                    loginCommandOutput.delete(0, loginCommandOutput.length());
                    loginCommandOutput.append(element+"\n");
                }
            }
        });*/
        
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iexc) {
            task.stop();
            message.delete(0, message.length());
            message.append(iexc.getLocalizedMessage());
            return false;
        }
        if (task.getExitStatus() == CommandTask.STATUS_SUCCEEDED) return true;
        String output = loginCommandOutput.toString().trim();
        message.delete(0, message.length());
        message.append(output);
        //System.out.println("Exit Status = "+es.getExitStatus()+", output = "+output);
        if (output.indexOf("No route to host") >= 0) {
            throw new UnknownHostException(output);
        }/* else if (output.indexOf("fail")) {
            throw new IOException(output);
        }*/
        return false;
    }
    
//--------------------------------------------------------------------------------------
// ------------------------- server check ----------------------------------------------
 
    /** The method connects to the server specified in the PasswdEntry parameter.
     * After opening, it does the authentification (See CVS cleint/server protocol).
     * Then it disconnects.
     * @param toCheck The info about the Cvs root. (server + repository + user and password)
     * @param message returns the error message
     * @return true if authentification succeed, othewise false.
     */
    public static boolean checkServer(PasswdEntry toCheck, StringBuffer message)
            throws UnknownHostException, IOException {
        
        OutputStreamWriter outStreamWriter = null;
        InputStreamReader inStreamReader = null;
        OutputStream outStream = null;
        InputStream inStream = null;
        Socket socket;
        boolean ok = false;
        CVSRoot cvsroot = toCheck.getCVSRoot();
        int port = cvsroot.getPort();
        if (port == 0) {
            port = STANDARD_PSERVER_PORT;
        }
        //try { 
            socket = new Socket(cvsroot.getHostName(), port);
            /*
        } catch (UnknownHostException e) {
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
               public void run () {
                TopManager.getDefault ().notify (new NotifyDescriptor.Message(org.openide.util.NbBundle.getBundle(CVSPasswd.class).getString("CVSPasswd.unknownHost")));
               }
             });
            D("Exception: "+e);
            return false;
        } catch (IOException e) {
            javax.swing.SwingUtilities.invokeLater(new Runnable () {
               public void run () {
                TopManager.getDefault ().notify (new NotifyDescriptor.Message(org.openide.util.NbBundle.getBundle(CVSPasswd.class).getString("CVSPasswd.connectionIOError")));
               }
             });
            D("Exception when connecting: "+e);
            return false;
        }
             */
        try {
            outStream = socket.getOutputStream();
            outStreamWriter = new OutputStreamWriter(outStream);
            inStream = socket.getInputStream();
            inStreamReader = new InputStreamReader(inStream);
            String req = BEGIN_AUTH_REQUEST+"\n" + toCheck.getAuthString() +  END_AUTH_REQUEST+"\n";
            outStreamWriter.write(req);
            outStreamWriter.flush();
            int n;
            StringBuffer buffer = new StringBuffer();
            try {
                while(inStream.available() == 0);
                while((n = inStream.available()) > 0) {
                    byte[] bbuffer = new byte[n];
                    inStream.read(bbuffer);
                    buffer.append(new String(bbuffer));
                }
            } catch (IOException e) {
                message.append(e.getLocalizedMessage());
                return false;
            }
            String result = buffer.toString();
            ok =  result.equals(AUTH_SUCCESSFULL);
            if (!ok) {
                message.append(org.openide.util.NbBundle.getBundle(CVSPasswd.class).getString("CVSPasswd.wrongPassword"));
            }   
        } catch (IOException e) {
            message.append(e.getLocalizedMessage());
            ok = false;
        }
        finally {
            try {
                if (outStream != null) {outStream.close();}
                if (inStream != null) {inStream.close();}
                if (socket != null) {socket.close();}
            } catch (IOException e) {
                message.append(e.getLocalizedMessage());
            }
        }
        return ok;
        
    }

}
