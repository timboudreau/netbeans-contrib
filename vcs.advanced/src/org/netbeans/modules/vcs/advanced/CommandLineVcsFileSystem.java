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

import gnu.regexp.*;
import com.netbeans.ide.util.actions.*;
import com.netbeans.ide.util.NbBundle;
import com.netbeans.enterprise.modules.vcs.*;
import com.netbeans.enterprise.modules.vcs.util.*;
import com.netbeans.ide.filesystems.FileObject;
import com.netbeans.ide.filesystems.FileSystem;
import com.netbeans.ide.filesystems.FileSystem.Status;
import com.netbeans.ide.filesystems.FileUtil;
import com.netbeans.ide.filesystems.FileStateInvalidException;
import com.netbeans.ide.filesystems.AbstractFileSystem;
import com.netbeans.ide.filesystems.DefaultAttributes;

/** Generic command line VCS filesystem - the Bean itself.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class CommandLineVcsFileSystem extends VcsFileSystem 
  implements AbstractFileSystem.List, AbstractFileSystem.Info,
  AbstractFileSystem.Change, FileSystem.Status, Serializable {

  private Debug E=new Debug("CommandLineVcsFileSystem",true );
  private Debug D=new Debug("CommandLineVcsFileSystem",true );

  private static final int REFRESH_TIME = 0;

  /** root file */
  private File rootFile = new File (".");

  /** is read only */
  private boolean readOnly;

  private boolean debug=true;
  
  /** user variables Vector<String> 'name=value' */
  private Vector variables=new Vector(10);

  /** user commands Vector<UserCommand> */
  private Vector commands=new Vector(10);

  /** Just convenience table.
      [key='LIST' value=UserCommand]
  */
  private transient Hashtable commandsByName=null;
  
  private transient VcsCache cache=null;

  private long cacheId=0;

  private static transient String CACHE_ROOT="vcscache";
  private static transient long CACHE_LAST_ID=0;
  
  private transient VcsAction action=null;

  //-------------------------------------------
  public VcsCache getCache(){
    return cache;
  }
  
  //-------------------------------------------
  private void createDir(String path){
    File dir=new File(path);
    if( dir.isDirectory() ){
      return ;
    }
    if( dir.mkdirs()==false ){
      E.err("Unable to create directory "+path);
    }
  }
  
  //-------------------------------------------
  private void init(){
    CACHE_ROOT=System.getProperty("netbeans.home")+
      //"/home/mfadljevic/Development/src_modules/com/netbeans/enterprise/modules/vcs/cmdline"+
      File.separator+"vcscache";
    String cacheDir=CACHE_ROOT+File.separator+cacheId;
    createDir(cacheDir);
    cache=new VcsCache(this,cacheDir);
  }

  //-------------------------------------------
  public CommandLineVcsFileSystem () {
    D.deb("CommandLineVcsFileSystem()");
    info = this;
    change = this;
    DefaultAttributes a = new DefaultAttributes (info, change, this);
    attr = a;
    list = a;
    setRefreshTime (REFRESH_TIME);

    // this is a fast hack... to be changed
    Properties props=UserCommand.readPredefinedProperties("predefined.properties");
    variables=UserCommand.readVariables("st30",props);
    commands=UserCommand.readCommands("st30",props);

    cacheId=getNewCacheId();
    init();
  }

  //-------------------------------------------
  public static long getNewCacheId(){
    return ++CACHE_LAST_ID;
  }

  //-------------------------------------------
  public long getCacheId(){
    return cacheId;
  }

  //-------------------------------------------
  private void readObject(ObjectInputStream in) throws 
    ClassNotFoundException, IOException, NotActiveException{
    long savedCacheLastId=in.readLong();
    in.defaultReadObject();
    init();
    CACHE_LAST_ID=Math.max(savedCacheLastId, CACHE_LAST_ID);
    //D.deb("readObject() - restoring bean");
  }

  //-------------------------------------------
  private void writeObject(ObjectOutputStream out) throws IOException {
    //D.deb("writeObject() - saving bean");
    out.writeLong(CACHE_LAST_ID);
    out.defaultWriteObject();
  }  
  
  //-------------------------------------------
  public void setDebug(boolean debug){
    this.debug=debug;
  }

  //-------------------------------------------
  public boolean getDebug(){
    return debug;
  }  

  //-------------------------------------------
  public Vector getVariables(){
    return variables;
  }

  //-------------------------------------------
  public void setVariables(Vector variables){
    if( variables.equals(this.variables) ){
      return ;
    }
    Vector old=this.variables;
    this.variables=variables;
    firePropertyChange("variables", old, variables);
  }
  
  //-------------------------------------------
  public Hashtable getVariablesAsHashtable(){
    int len=variables.size();
    Hashtable result=new Hashtable(len+5);
    for(int i=0;i<len;i++){
      String line=(String)variables.elementAt(i);
      int eq=line.indexOf('=');
      if( eq<0 ){
	continue;
      }
      String key=line.substring(0,eq);
      String value=line.substring(eq+1);
      result.put(key,value);
    }
    return result;
  }

  //-------------------------------------------
  public Vector getCommands(){
    return commands;
  }

  //-------------------------------------------
  public void setCommands(Vector commands){
    this.commands=commands;
    int len=this.commands.size();
    commandsByName=new Hashtable(len+5);
    for(int i=0;i<len;i++){
      UserCommand uc=(UserCommand)this.commands.elementAt(i);
      commandsByName.put(uc.getName(), uc);
    }
  }

  //-------------------------------------------
  public UserCommand getCommand(String name){
    if( commandsByName==null ){
      setCommands(commands);
    }
    return (UserCommand)commandsByName.get(name);
  }

  //-------------------------------------------
  public FileSystem.Status getStatus(){
    return this;
  }

  //-------------------------------------------
  public Image annotateIcon(Image icon, int iconType, Set files) {
    //D.deb("annotateIcon()");
    return icon;
  }

  //-------------------------------------------
  public String annotateName(String name, Set files) {
    //D.deb("annotateName("+name+")");
    Object[] oo=files.toArray();
    if( oo.length==0 ){
      return name;
    }
    for(int i=0;i<oo.length;i++){
      FileObject ff=(FileObject)oo[i]; 
      //D.deb("oo["+i+"]="+ff);
    }
    return name;
  }

  //-------------------------------------------
  public SystemAction[] getActions(){
    if( action==null ){
      action=new VcsAction(this);
    }
    SystemAction [] actions=new SystemAction[1];
    actions[0]=action;
    return actions;
  }

  //-------------------------------------------
  /* Human presentable name */
  public String getDisplayName() {
    D.deb("getDisplayName() isValid="+isValid());
    if(!isValid())
      return getString("LAB_FileSystemInvalid", rootFile.toString ());
    else
      return getString("LAB_FileSystemValid", rootFile.toString ());
  }

  //-------------------------------------------
  /** Set the root directory of the file system.
  * @param r file to set root to
  * @exception PropertyVetoException if the value if vetoed by someone else (usually
  *    by the {@link com.netbeans.ide.filesystems.Repository Repository})
  * @exception IOException if the root does not exists or some other error occured
  */
  public synchronized void setRootDirectory (File r) throws PropertyVetoException, IOException {
    D.deb("setRootDirectory("+r+")");
    if (!r.exists() || r.isFile ()) {
      throw new IOException(getString("EXC_RootNotExist", r.toString ()));
    }

    setSystemName(computeSystemName (r));

    rootFile = r;

    firePropertyChange("root", null, refreshRoot ());
  }

  //-------------------------------------------
  /** Get the root directory of the file system.
   * @return root directory
  */
  public File getRootDirectory () {
    D.deb("getRootDirectory() ->"+rootFile);
    return rootFile;
  }

  //-------------------------------------------
  /** Set whether the file system should be read only.
   * @param flag <code>true</code> if it should
  */
  public void setReadOnly(boolean flag) {
    D.deb("setReadOnly("+flag+")");
    if (flag != readOnly) {
      readOnly = flag;
      firePropertyChange (PROP_READ_ONLY, new Boolean (!flag), new Boolean (flag));
    }
  }

  //-------------------------------------------
  /* Test whether file system is read only.
   * @return <true> if file system is read only
   */
  public boolean isReadOnly() {
    //D.deb("isReadOnly() ->"+readOnly);
    return readOnly;
  }

  //-------------------------------------------
  /** Prepare environment by adding the root directory of the file system to the class path.
  * @param environment the environment to add to
  */
  public void prepareEnvironment(FileSystem.Environment environment) {
    D.deb("prepareEnvironment() ->"+rootFile.toString());
    environment.addClassPath(rootFile.toString ());
  }

  //-------------------------------------------
  /** Compute the system name of this file system for a given root directory.
  * <P>
  * The default implementation simply returns the filename separated by slashes.
  * @see FileSystem#setSystemName
  * @param rootFile root directory for the filesystem
  * @return system name for the filesystem
  */
  protected String computeSystemName (File rootFile) {
    D.deb("computeSystemName() ->"+rootFile.toString ().replace(File.separatorChar, '/') );
    return rootFile.toString ().replace(File.separatorChar, '/');
  }

  //-------------------------------------------
  /** Creates file for given string name.
  * @param name the name
  * @return the file
  */
  private File getFile (String name) {
    return new File (rootFile, name);
  }

  //-------------------------------------------
  //
  // List
  //

  //-------------------------------------------
  /* Scans children for given name
  */
  public String[] children (String name) {
    D.deb("children('"+name+"')");
    String[] files=null;
    if( cache.isDir(name) ){
      files=cache.getFilesAndSubdirs(name);
      D.deb("files="+MiscStuff.arrayToString(files));
    }
    return files;

    /*
      D.deb("fallback -> return local files");
      File f = getFile (name);
      if (f.isDirectory ()) {
      files=f.list();
      }
      }
    */

  }

  //-------------------------------------------
  //
  // Change
  //

  /* Creates new folder named name.
  * @param name name of folder
  * @throws IOException if operation fails
  */
  public void createFolder (String name) throws java.io.IOException {
    D.deb("createFolder('"+name+"')");
    File f = getFile (name);
    Object[] errorParams = new Object[] {
      f.getName (),
      getDisplayName (),
      f.toString ()
    };
    
    if (name.equals ("")) {
      throw new IOException(MessageFormat.format (getString("EXC_CannotCreateF"), errorParams));
    }
    
    if (f.exists()) {
      throw new IOException(MessageFormat.format (getString("EXC_FolderAlreadyExist"), errorParams));
    }
    
    boolean b = f.mkdir();
    if (!b) {
      throw new IOException(MessageFormat.format (getString("EXC_CannotCreateF"), errorParams));
    }
  }

  //-------------------------------------------
  /* Create new data file.
  *
  * @param name name of the file
  *
  * @return the new data file object
  * @exception IOException if the file cannot be created (e.g. already exists)
  */
  public void createData (String name) throws IOException {
    D.deb("createData("+name+")");
    File f = getFile (name);
    Object[] errorParams = new Object[] {
      f.getName (),
      getDisplayName (),
      f.toString (),
    };

    if (!f.createNewFile ()) {
      throw new IOException(MessageFormat.format (getString("EXC_DataAlreadyExist"), errorParams));
    }
/* JST: Maybe handled by createNewFile, but probably
    if (!tmp.exists())
      throw new IOException(MessageFormat.format (LocalFileSystem.getString("EXC_CannotCreateD"), errorParams));
*/
  }

  //-------------------------------------------
  /* Renames a file.
  *
  * @param oldName old name of the file
  * @param newName new name of the file
  */
  public void rename(String oldName, String newName) throws IOException {
    D.deb("rename(oldName="+oldName+",newName="+newName+")");
    File of = getFile (oldName);
    File nf = getFile (newName);

    if (!of.renameTo (nf)) {
      throw new IOException(getString("EXC_CannotRename", oldName, getDisplayName (), newName));
    }
  }

  //-------------------------------------------
  /* Delete the file. 
  *
  * @param name name of file
  * @exception IOException if the file could not be deleted
  */
  public void delete (String name) throws IOException {
    D.deb("delete("+name+")");
    File file = getFile (name);
    if (!file.delete()) {
      throw new IOException (getString("EXC_CannotDelete", name, getDisplayName (), file.toString ()));
    }
  }
  
  //-------------------------------------------
  //
  // Info
  //

  //-------------------------------------------
  /*
  * Get last modification time.
  * @param name the file to test
  * @return the date
  */
  public java.util.Date lastModified(String name) {
    D.deb("lastModified("+name+")");
    return new java.util.Date (getFile (name).lastModified ());
  }

  //-------------------------------------------
  /* Test if the file is folder or contains data.
  * @param name name of the file
  * @return true if the file is folder, false otherwise
  */
  public boolean folder (String name) {
    return cache.isDir(name);
    // return getFile (name).isDirectory ();
  }

  //-------------------------------------------  
  /* Test whether this file can be written to or not.
  * @param name the file to test
  * @return <CODE>true</CODE> if file is read-only
  */
  public boolean readOnly (String name) {
    //D.deb("readOnly('"+name+"')");
    return !getFile (name).canWrite ();
  }
  
  /** Get the MIME type of the file.
  * Uses {@link FileUtil#getMIMEType}.
  *
  * @param name the file to test
  * @return the MIME type textual representation, e.g. <code>"text/plain"</code>
  */
  public String mimeType (String name) {
    D.deb("mimeType('"+name+"')");
    int i = name.lastIndexOf ('.');
    String s;
    try {
      s = FileUtil.getMIMEType (name.substring (i + 1));
    } catch (IndexOutOfBoundsException e) {
      s = null;
    }
    return s == null ? "content/unknown" : s;
  }

  //-------------------------------------------
  /* Get the size of the file.
  *
  * @param name the file to test
  * @return the size of the file in bytes or zero if the file does not contain data (does not
  *  exist or is a folder).
  */
  public long size (String name) {
    D.deb("size("+name+")");
    return getFile (name).length ();
  }
  
  /* Get input stream.
  *
  * @param name the file to test
  * @return an input stream to read the contents of this file
  * @exception FileNotFoundException if the file does not exists or is invalid
  */
  public InputStream inputStream (String name) throws java.io.FileNotFoundException {
    D.deb("inputStream("+name+")");
    return new FileInputStream (getFile (name));
  }

  //-------------------------------------------
  /* Get output stream.
  *
  * @param name the file to test
  * @return output stream to overwrite the contents of this file
  * @exception IOException if an error occures (the file is invalid, etc.)
  */
  public OutputStream outputStream (String name) throws java.io.IOException {
    D.deb("outputStream("+name+")");
    return new FileOutputStream (getFile (name));
  }

  /** Does nothing to lock the file.
  *
  * @param name name of the file
  */
  public void lock (String name) throws IOException {
    D.deb("lock()");
  }

  /** Does nothing to unlock the file.
  *
  * @param name name of the file
  */
  public void unlock (String name) {
    D.deb("unlock()");
  }

  //-------------------------------------------
  /** Does nothing to mark the file as unimportant.
  *
  * @param name the file to mark
  */
  public void markUnimportant (String name) {
    D.deb("markUnimportant("+name+")");
  }

  //-------------------------------------------
  /** Getter for the resource string
   * @param s the resource name
  * @return the resource
  */
  static String getString(String s) {
    return NbBundle.getBundle
      ("com.netbeans.enterprise.modules.vcs.cmdline.Bundle").getString (s);
  }

  //-------------------------------------------
  /** Creates message for given string property with one parameter.
  * @param s resource name
  * @param obj the parameter to the message
  * @return the string for that text
  */
  static String getString (String s, Object obj) {
    return MessageFormat.format (getString (s), new Object[] { obj });
  }

  //-------------------------------------------
  /** Creates message for given string property with two parameters.
  * @param s resource name
  * @param obj1 the parameter to the message
  * @param obj2 the parameter to the message
  * @return the string for that text
  */
  static String getString (String s, Object obj1, Object obj2) {
    return MessageFormat.format (getString (s), new Object[] { obj1, obj2 });
  }

  //-------------------------------------------
  /** Creates message for given string property with three parameters.
  * @param s resource name
  * @param obj1 the parameter to the message
  * @param obj2 the parameter to the message
  * @param obj3 the parameter to the message
  * @return the string for that text
  */
  static String getString (String s, Object obj1, Object obj2, Object obj3) {
    return MessageFormat.format (getString (s), new Object[] { obj1, obj2, obj3 });
  }

}

/*
 * <<Log>>
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
