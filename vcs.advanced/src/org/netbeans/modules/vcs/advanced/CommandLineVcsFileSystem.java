/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.vcs.cmdline;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;
import javax.swing.*;

import org.openide.util.actions.*;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystem.Status;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.DefaultAttributes;

import com.netbeans.developer.modules.vcs.*;
//import com.netbeans.developer.modules.vcs.cmdline.*;
import com.netbeans.developer.modules.vcs.util.Debug;

/** Generic command line VCS filesystem.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class CommandLineVcsFileSystem extends VcsFileSystem {
  private Debug D = new Debug ("CommandLineVcsFileSystem", true);
  private static transient String CONFIG_ROOT="vcs/config";
  private transient Hashtable commandsByName=null;
 
  static final long serialVersionUID =-1017235664394970926L;
  //-------------------------------------------
  public CommandLineVcsFileSystem () {
    //D.deb("CommandLineVcsFileSystem()");
    super ();
    readConfiguration ();
  }
  
  public VcsFactory getVcsFactory () {
    D.deb("getVcsFactory ()");
    return new CommandLineVcsFactory ();
  }
  
  //-------------------------------------------
  public String getConfigRoot(){
    return CONFIG_ROOT;
  }
  
  private void readConfiguration () {
    D.deb ("readConfiguration ()");
    CONFIG_ROOT=System.getProperty("netbeans.user")+File.separator+
      "system"+File.separator+"vcs"+File.separator+"config";
    Properties props=VcsConfigVariable.readPredefinedProperties(CONFIG_ROOT+File.separator+"empty.properties");
    setVariables (VcsConfigVariable.readVariables(props));
    D.deb("setVariables DONE.");
    setAdvancedConfig (getVcsFactory ().getVcsAdvancedCustomizer().readConfig (props));
  }  

  /*
  protected String g(String s) {
    return NbBundle.getBundle
      ("com.netbeans.developer.modules.vcs.cmdline.BundleCVS").getString (s);
  }
  */
}

/*
 * <<Log>>
 *  50   Gandalf   1.49        11/30/99 Martin Entlicher 
 *  49   Gandalf   1.48        11/27/99 Patrik Knakal   
 *  48   Gandalf   1.47        11/23/99 Martin Entlicher 
 *  47   Gandalf   1.46        10/25/99 Pavel Buzek     copyright
 *  46   Gandalf   1.45        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  45   Gandalf   1.44        10/10/99 Pavel Buzek     
 *  44   Gandalf   1.43        10/9/99  Pavel Buzek     
 *  43   Gandalf   1.42        10/9/99  Pavel Buzek     
 *  42   Gandalf   1.41        10/5/99  Pavel Buzek     
 *  41   Gandalf   1.40        9/30/99  Pavel Buzek     
 *  40   Gandalf   1.39        9/13/99  Pavel Buzek     
 *  39   Gandalf   1.38        9/10/99  Martin Entlicher removed import regexp
 *  38   Gandalf   1.37        9/8/99   Pavel Buzek     
 *  37   Gandalf   1.36        9/8/99   Pavel Buzek     class model changed, 
 *       customization improved, several bugs fixed
 *  36   Gandalf   1.35        8/31/99  Pavel Buzek     
 *  35   Gandalf   1.34        8/31/99  Pavel Buzek     
 *  34   Gandalf   1.33        8/7/99   Ian Formanek    Martin Entlicher's 
 *       improvements
 *  33   Gandalf   1.32        6/10/99  Michal Fadljevic 
 *  32   Gandalf   1.31        6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  31   Gandalf   1.30        6/8/99   Michal Fadljevic 
 *  30   Gandalf   1.29        6/4/99   Michal Fadljevic 
 *  29   Gandalf   1.28        6/1/99   Michal Fadljevic 
 *  28   Gandalf   1.27        6/1/99   Michal Fadljevic 
 *  27   Gandalf   1.26        5/27/99  Michal Fadljevic 
 *  26   Gandalf   1.25        5/27/99  Michal Fadljevic 
 *  25   Gandalf   1.24        5/25/99  Michal Fadljevic 
 *  24   Gandalf   1.23        5/25/99  Michal Fadljevic 
 *  23   Gandalf   1.22        5/24/99  Michal Fadljevic 
 *  22   Gandalf   1.21        5/24/99  Michal Fadljevic 
 *  21   Gandalf   1.20        5/21/99  Michal Fadljevic 
 *  20   Gandalf   1.19        5/21/99  Michal Fadljevic 
 *  19   Gandalf   1.18        5/21/99  Michal Fadljevic 
 *  18   Gandalf   1.17        5/19/99  Michal Fadljevic 
 *  17   Gandalf   1.16        5/18/99  Michal Fadljevic 
 *  16   Gandalf   1.15        5/14/99  Michal Fadljevic 
 *  15   Gandalf   1.14        5/13/99  Michal Fadljevic 
 *  14   Gandalf   1.13        5/11/99  Michal Fadljevic 
 *  13   Gandalf   1.12        5/7/99   Michal Fadljevic 
 *  12   Gandalf   1.11        5/6/99   Michal Fadljevic 
 *  11   Gandalf   1.10        5/4/99   Michal Fadljevic 
 *  10   Gandalf   1.9         5/4/99   Michal Fadljevic 
 *  9    Gandalf   1.8         4/29/99  Michal Fadljevic 
 *  8    Gandalf   1.7         4/28/99  Michal Fadljevic 
 *  7    Gandalf   1.6         4/27/99  Michal Fadljevic 
 *  6    Gandalf   1.5         4/26/99  Michal Fadljevic 
 *  5    Gandalf   1.4         4/22/99  Michal Fadljevic 
 *  4    Gandalf   1.3         4/22/99  Michal Fadljevic 
 *  3    Gandalf   1.2         4/22/99  Michal Fadljevic 
 *  2    Gandalf   1.1         4/21/99  Michal Fadljevic 
 *  1    Gandalf   1.0         4/15/99  Michal Fadljevic 
 * $
 */

