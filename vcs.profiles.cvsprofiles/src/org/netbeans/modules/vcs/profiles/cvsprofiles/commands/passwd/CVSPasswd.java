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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd;

/**
 * CVS password file .cvspass maintainer.
 * @author  mkleint
 */

import java.io.*;
import java.net.*;
import java.beans.PropertyVetoException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;

import org.openide.filesystems.*;
import org.openide.*;
import org.openide.util.*;

import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;

/** The purpose of this class is to handle everything around pserver authentication 
 * and the .cvspass file stuff. It finds, reads and writes to the file. If the configuration is not found in the file,
 * it can connect to the server and check it. Then adds the item to the .cvspass file
 */

public class CVSPasswd extends Object {
    private Debug E=new Debug("CVSPasswd", true); // NOI18N
    private Debug D=E;
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
    private static final int CVS_PORT = 2401;
    PrintWriter bf;
                                                                                                                                                                  
    /** The standard name of the file, where CVS stores passwords and cvs root directories
     * (for pserver type of connection only)
 */
    public static final String STD_FILE = ".cvspass";
    private LinkedList entries = new LinkedList();
    private PasswdEntry lastEntry;
    private volatile String homeDir = System.getProperty("user.home");
    
    private File passFile = null;
    /** Creates new CVSPasswd */

    /*
    private CVSPasswd(File fil) {
      passFile = fil;
      loadPassFile();
    } 
     */   
    
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
        String home = System.getProperty("env-home");
        if (home == null && Utilities.isWindows()) {
            String homePath = System.getProperty("env-homepath");
            String homeDrive = System.getProperty("env-homedrive");
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
      try {
        BufferedReader bf = new BufferedReader(new FileReader(passFile.getAbsolutePath()));
        String line;
        do {
          line = bf.readLine();
          if (line != null) {
            PasswdEntry entr = new PasswdEntry();
            boolean ok = entr.setEntry(line);
            if (!ok) {  //TODO - corrupted line prolly ignore (should write back or skip?)
              D.deb("Line corrupted.");
              continue;
            }
            entries.add(entr);
          }  
        } while (line != null); 
        bf.close();
      } catch (IOException exc) {
         D.deb(".cvspass reading error");
         if (bf != null) {
              bf.close(); 
         }    
         return false;
      }
      return true;
    }
    
    /** Writes the current passwd database to the .cvspass file.
     */
    public boolean savePassFile() {
        //System.out.println("savePassFile("+passFile+")");
      try {
//        if (!passFile.canWrite()) return false;
        bf = new PrintWriter(new BufferedWriter(new FileWriter(passFile.getAbsolutePath(), false)));
        ListIterator it = entries.listIterator();
        while (it.hasNext()) {
          PasswdEntry ent = (PasswdEntry)it.next();
          bf.println(ent.getEntry(true));
        } 
        bf.close();
        
      } catch (IOException exc) {
        javax.swing.SwingUtilities.invokeLater(new Runnable () {
           public void run () {
              DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message(org.openide.util.NbBundle.getBundle(CVSPasswd.class).getString("CVSPasswd.errorWritingPass")));
           }
         });
         D.deb(".cvspass writing error:" +passFile.getAbsolutePath());
         if (bf != null) {
            bf.close();
         }                               
         return false;
      }
      return true;
    }

    /** Adds a new entry into the dtabase. Will be written to the file after savePassFile is run.
     * @param entry - entry supplied by the Customizer
     * @param passwd unscrambled password for the account
     * @return the added PasswdEntry. if not added, then null.
     */
    public PasswdEntry add(String entry, String passwd) {
      PasswdEntry psw = new PasswdEntry();
      String scrambled = StandardScrambler.getInstance().scramble(passwd);
      //String scrambled = scramble(passwd);
      boolean ok = psw.setEntry(entry + " " + scrambled);
      if (ok) {
        entries.add(psw); 
        return psw;
      }
      return null;
    }
    
    /**
     * Remove the entry from the current set of entries.
     */
    public void remove(String entry) {
        Iterator it = entries.iterator();
        while (it.hasNext()) {
           PasswdEntry ent = (PasswdEntry) it.next();
           if (entry.equals(ent.getEntry(false))) {
               it.remove();
           }
        }
    }
    
    public PasswdEntry add(String type, String user, String server, String root, String passwd) {
      String ent = ":" + type + ":" + user + "@" + server + ":" + root;
      return add(ent, passwd); 
    }
    
   
/** Looks for the  current setting (:pserver:username@server:/rootdir) in the database.
 * @return the found PasswdEntry, or if not found null.
 * @param current The current cvs root directory
 */
    public PasswdEntry find(String current) {
      ListIterator it = entries.listIterator();
      PasswdEntry ent = null; 
      String toReturn = null;
      while (it.hasNext()) {
        ent = (PasswdEntry)it.next();
        if (ent.matchToCurrent(current)) {
          return ent;
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
    public static boolean checkLogin(VcsFileSystem fs, StringBuffer message) throws UnknownHostException, IOException {
        VcsCommand cmd = fs.getCommand("LOGIN_CHECK");
        if (cmd == null) return true; // I have no way to check the login => believe that it is O.K.
        //final ExecuteCommand es = new ExecuteCommand(fs, cmd, fs.getVariablesAsHashtable());
        VcsCommandExecutor vce = fs.getVcsFactory().getCommandExecutor(cmd, fs.getVariablesAsHashtable());
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
        });
        fs.getCommandsPool().startExecutor(vce, fs);
        try {
            fs.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fs.getCommandsPool().kill(vce);
            message.delete(0, message.length());
            message.append(iexc.getLocalizedMessage());
            return false;
        }
        if (vce.getExitStatus() == VcsCommandExecutor.SUCCEEDED) return true;
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
     * @return true if authentification succeed, othewise false.
     * @param toCheck The info about the Cvs root. (server + repository + user and password)
 */
    public static boolean checkServer(PasswdEntry toCheck) throws UnknownHostException, IOException {
        OutputStreamWriter outStreamWriter = null;
        InputStreamReader inStreamReader = null;
        OutputStream outStream = null;
        InputStream inStream = null;
        Socket socket;
        boolean ok = false;
        //try { 
            socket = new Socket(toCheck.getServer(), CVS_PORT);
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
            // TODO
              D("Exception while read: "+e);
            }
            String result = buffer.toString();
            ok =  result.equals(AUTH_SUCCESSFULL);
            if (!ok) {
              javax.swing.SwingUtilities.invokeLater(new Runnable () {
                  public void run () {
                    DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message(org.openide.util.NbBundle.getBundle(CVSPasswd.class).getString("CVSPasswd.wrongPassword")));
                  }
              });
            }   
        } catch (IOException e) {
            // TODO
            D("Exception getting input: "+e);
            ok = false;
        }
        finally {
          try {
            if (outStream != null) {outStream.close();}
            if (inStream != null) {inStream.close();}
            if (socket != null) {socket.close();}
          } catch (IOException e) {
              // TODO
              D("Exception when closing the connection: "+e);
          }
        }
        return ok;
        
    }

    private static void D(String debug) {
        //System.out.println("CvsPasswd(): "+debug);
    }
    
}