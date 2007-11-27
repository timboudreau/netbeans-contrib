/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
 *
 * Contributor(s): Libor Martinek.
 */

package org.netbeans.modules.remotefs.ftpfs;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/** Dialogs.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPDialogs extends Object {

  /** Creates new FTPDialogs */
  public FTPDialogs() {
  }

  public static int disconnect(String server) {
        Object ops[] = new String[3];
        ops[0] = "Yes";
        ops[1] = "Yes to all";
        ops[2] = "No";
        Object obj = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
           "Another filesystem is also connected to server "+server+".\n"+
           "Do you realy want to diconnect from server?\n"+
           "If you say "+ops[0]+", only this fileystem will be disconnected, but you will not be able to work offline\n"+
           "If you say "+ops[1]+", all filesystem will be disconneected and you will be able to work offline",
           "Question",NotifyDescriptor.YES_NO_OPTION ,NotifyDescriptor.QUESTION_MESSAGE,ops, ops[2]));
        if (obj == ops[0]) return 0;
        if (obj == ops[1]) return 1;
        if (obj == ops[2]) return 2;
        return 2;
  }
  
  public static boolean connect(String server) {
       Object obj = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
         "Another filesystem is now disconnected from server "+server+".\n"+
         "Do you realy want to connect to server?\n"+
         "If you say Yes, all other filesystems will be connected too.",
         "Question",NotifyDescriptor.YES_NO_OPTION ,NotifyDescriptor.QUESTION_MESSAGE,null, NotifyDescriptor.YES_OPTION));
       if (obj == NotifyDescriptor.YES_OPTION) 
         return true;
       else 
         return false;
  }
  
  public static void startdirNotFound(String startdir, String server) {
      Object ops[] = { "OK" }; 
      DialogDisplayer.getDefault().notify(new NotifyDescriptor(
         "Starting directory "+startdir+" doesn't exist on server "+server+
         ".\nRoot directory will be used instead.",
         "Startdir invalid",NotifyDescriptor.DEFAULT_OPTION ,NotifyDescriptor.INFORMATION_MESSAGE,ops, null));
  }
  
  public static void errorConnect(String error) {
    String ops[] =  {"OK"};
    DialogDisplayer.getDefault().notify(new NotifyDescriptor("Error during connecting to FTP server:\n"+
          error+"\nSet correct parameters and try to connect again.","Error",NotifyDescriptor.DEFAULT_OPTION ,NotifyDescriptor.ERROR_MESSAGE,ops, null));
  }   

  public static void incorrectPassword(String server) {
    Object ops[] = { "OK" }; 
    DialogDisplayer.getDefault().notify(new NotifyDescriptor(
       "Another filesystem is also connected to server "+server+" with the same username,\n"+
       "but with other password. Set correct password and try to connect again.",
       "Invalid password",NotifyDescriptor.DEFAULT_OPTION ,NotifyDescriptor.ERROR_MESSAGE,ops, null));
  }
  
  public static boolean incorrectCache(String oldcache, String newcache, String server) {
     Object obj = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
         "You set the cache to "+oldcache+", but another filesystem is also"+
         "connected\nto server "+server+" with existing cache "+newcache+"\n"+
         "You have to use this existing cache. Do you agree?\n"+
         "If you say No, you will not be connected to server",
         "Question",NotifyDescriptor.YES_NO_OPTION ,NotifyDescriptor.QUESTION_MESSAGE,null, NotifyDescriptor.YES_OPTION));
     return (obj == NotifyDescriptor.YES_OPTION); 
  }
  

}