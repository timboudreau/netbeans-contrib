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

import java.util.*;
/** This class stores info about one cvs repository. It's read by {@link org.netbeans.modules.vcs.cmdline.passwd.CVSPasswd }
 *
 * @author Milos Kleint
 * @version 1.0
 */
    public class PasswdEntry {
      String entry = null;
      String passwd = null;
      String type = null;
      String user = null;
      String server = null;
      String root = null;
      String main = null;      

      public PasswdEntry() {
      }

      public boolean setEntry(String ent) {
        entry = ent;
        int spacePos = entry.indexOf(' ');
        if (spacePos < 0) {return false;} // corrupted line.
        main = entry.substring(0,spacePos);
        if (main.startsWith(":")) { main = main.substring(1);}
        passwd = entry.substring(spacePos + 1);
        StringTokenizer token = new StringTokenizer(main, ":",false);
        if (token.hasMoreTokens()) {
          type = token.nextToken();
        } else return false;
        if (token.hasMoreTokens()) {
          String userServer = token.nextToken();  
          int ind3 = userServer.indexOf('@');
          if (ind3 < 0) return false; // corrupted line
          user = userServer.substring(0, ind3);  //user name
          server = userServer.substring(ind3 + 1); // server name
        } else return false;
        if (token.hasMoreTokens()) {            
            final StringBuffer temp = new StringBuffer("");
            while (token.hasMoreTokens()) {
                temp.append(token.nextToken());
                if (token.hasMoreTokens())
                    temp.append(':');
            }                
            root = temp.toString();            
        } else return false;
        return true;
     }

      public String getEntry(boolean withPasswd) {
        String entry = ":" + type + ":" + user + "@" + server + ":" + root;
        if (withPasswd) {entry = entry + " " + passwd;}
        return entry;
      }
      public boolean matchToCurrent(String curr){
       String s = getEntry(false);
//       D("matchToCurrent:" + curr + " + " + s);
       if (s.equalsIgnoreCase(curr)) { return true; }
       return false;  
      }

      public String getType() {
        return type;
      }
/*      public void setType(String tp) {
        type = tp;
      }
*/      
      public String getRoot() {
        return root;
      } 

      public String getUser() {
        return user;
      } 

      public String getPasswd() { // already scrambled
        return passwd;
      } 
/*      public void setRoot(String rootDir) {
        root = rootDir;
      } 

      public void setUser(String userName) {
        user= userName;
      } 

      public void setPasswd(String pass) {
        passwd = pass;
      } 
*/      
      
      public String toString() {
        return getEntry(true);
      }

      public String getServer() {
        return server;
      } 
/*
      public void setServer(String serv) {
        server = serv;
      } 
*/

/** Method used to compose the authentification string that is send to the server in the beginning of the communication.
 * For details see CVS Client/Server protocol specification.
 * @return the authentification string
 */
      public String getAuthString() {
        return (getRoot() + "\n" + getUser() + "\n" + getPasswd() + "\n");
      }

      private void D(String debug) {
   //     System.out.println("PasswdEntry(): "+debug);
      }

  }