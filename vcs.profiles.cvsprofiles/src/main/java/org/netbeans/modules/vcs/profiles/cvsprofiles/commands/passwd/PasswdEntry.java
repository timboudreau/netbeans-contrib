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

import java.util.*;

import org.netbeans.lib.cvsclient.CVSRoot;

/** This class stores info about one cvs repository. It's read by {@link org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.CVSPasswd }
 *
 * @author Milos Kleint
 */
    public class PasswdEntry {

        private CVSRoot cvsroot;
        private String passwd = null;
        boolean format1 = false;

      /**
       * Create an empty password entry.
       * Use {@link #setEntry()} to set the entry.
       */
      public PasswdEntry() {
      }

      /**
       * Set the entry and initalize this object.
       * @param entry The entry (a line from .cvspass file).
       * @return Whether the entry was set successfully.
       */
      public boolean setEntry(String entry) {
        if (entry.startsWith("/1 ")) {
            format1 = true;
            entry = entry.substring("/1 ".length());
        }
        int spacePos = entry.indexOf(" A");
        if (spacePos < 0) {
            return false; // corrupted line.
        }
        String main = entry.substring(0, spacePos);
        passwd = entry.substring(spacePos + 1);
        
        try {
            cvsroot = CVSRoot.parse(main);
        } catch (IllegalArgumentException iaex) {
            return false; // Can not parse the CVSROOT.
        }
        if (format1 && cvsroot.getPort() == 0) {
            cvsroot.setPort(CVSPasswd.STANDARD_PSERVER_PORT);
        }
        return true;
     }

      /**
       * Get the String representation of this entry.
       */
      public String getEntry() {
        if (cvsroot == null) return null; // The entry was not set successfully.
        format1 = cvsroot.getPort() != 0; // The port could be set later
        String prefix = (format1) ? "/1 " : ""; // NOI18N
        //String entry = prefix + ":" + type + ":" + user + "@" + server + ":" +
        //               ((port != null) ? port : "") + root;
        //if (withPasswd) {entry = entry + " " + passwd;}
        return prefix + cvsroot.toString() + " " + passwd;
        //return entry;
      }
      
      /**
       * Get the CVSROOT of this entry.
       */
      public CVSRoot getCVSRoot() {
          return cvsroot;
      }
      
      /**
       * Get the scrambled password.
       */
      public String getPasswd() {
          return passwd;
      }
      
      /**
       * @return The same as {@link #getEntry()}
       */
      public String toString() {
          return getEntry();
      }

/** Method used to compose the authentification string that is send to the server in the beginning of the communication.
 * For details see CVS Client/Server protocol specification.
 * @return the authentification string
 */
      public String getAuthString() {
          if (cvsroot == null) return null; // The entry was not set.
          return (cvsroot.getRepository() + "\n" + cvsroot.getUserName() + "\n" + getPasswd() + "\n");
      }

  }
