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

import gnu.regexp.*;
import org.openide.util.actions.*;
import org.openide.util.NbBundle;
import org.openide.*;
import com.netbeans.enterprise.modules.vcs.*;
import com.netbeans.enterprise.modules.vcs.util.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystem.Status;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.DefaultAttributes;

/** Generic command line VCS filesystem.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class CommandLineVcsFileSystem extends VcsFileSystem {

  /** Just convenience table.
      [key="LIST" value=UserCommand]
  */
  private transient Hashtable commandsByName=null;
  
  public VcsFactory getVcsFactory () {
    return new CommandLineVcsFactory ();
  }
    

  //-------------------------------------------
  public CommandLineVcsFileSystem () {
    //D.deb("CommandLineVcsFileSystem()");
    super ();
  }

  //-------------------------------------------
  public Hashtable getVariablesAsHashtable(){
    int len=getVariables().size();
    Hashtable result=new Hashtable(len+5);
    for(int i=0; i<len; i++) {
      VcsConfigVariable var = (VcsConfigVariable) getVariables().elementAt (i);
      result.put(var.getName (), var.getValue ());
    }

    result.put("netbeans.home",System.getProperty("netbeans.home"));
    result.put("java.home",System.getProperty("java.home"));
    String osName=System.getProperty("os.name");
    result.put("classpath.separator", (osName.indexOf("Win")<0 ? ":":";" ));
    result.put("path.separator", ""+File.separator);

    result.put("ROOTDIR",getRootDirectory().toString());

    return result;
  }


  //-------------------------------------------
  public Vector getCommands(){
    return (Vector) getAdvancedConfig ();
  }


  //-------------------------------------------
  public void setCommands(Vector commands){
    setAdvancedConfig (commands);
    int len=commands.size();
    commandsByName=new Hashtable(len+5);
    for(int i=0;i<len;i++){
      UserCommand uc=(UserCommand)commands.elementAt(i);
      commandsByName.put(uc.getName(), uc);
    }
  }


  //-------------------------------------------
  public UserCommand getCommand(String name){
    if( commandsByName==null ){
      setCommands ((Vector) getAdvancedConfig ());
    }
    return (UserCommand)commandsByName.get(name);
  }


  //-------------------------------------------
  public Vector getAdditionalCommands(){
    Vector commands=getCommands();
    int len=commands.size();
    Vector additionalCommands=new Vector(5);
    for(int i=0;i<len;i++){
      UserCommand uc=(UserCommand)commands.elementAt(i);
      if( isAdditionalCommand(uc.getName()) ){
	additionalCommands.add(uc);
      }
    }
    return additionalCommands;
  }
  



  //-------------------------------------------
  String g(String s) {
    return NbBundle.getBundle
      ("com.netbeans.enterprise.modules.vcs.cmdline.Bundle").getString (s);
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
 * <<Log>>
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



