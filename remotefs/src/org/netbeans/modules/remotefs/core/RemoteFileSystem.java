/*                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version 
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is RemoteFS. The Initial Developer of the Original
 * Code is Libor Martinek. Portions created by Libor Martinek are 
 * Copyright (C) 2000. All Rights Reserved.
 * 
 * Contributor(s): Libor Martinek. 
 */

package org.netbeans.modules.remotefs.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.*;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Date;
import java.net.*;

import org.openide.*;
import org.openide.filesystems.*;
import org.openide.options.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;
import org.openide.util.enum.SingletonEnumeration;
import org.openide.util.enum.SequenceEnumeration;
import org.openide.util.RequestProcessor;

/** Remote FIleSystem class
 * @author Libor Martinek
 * @version 1.0
 */
public abstract class RemoteFileSystem extends AbstractFileSystem 
implements AbstractFileSystem.List, AbstractFileSystem.Info, AbstractFileSystem.Change, 
           RemoteFile.Notify, RemoteFile.RequestProcessor {
  static final long serialVersionUID = 5562503683369874863L;
  
  private static final boolean DEBUG = true;
  
  /** remote client */
  protected transient RemoteClient client;
  /** root file */
  protected transient RemoteFile rootFile;
  /** Root of cache directory */
  protected File cachedir = null;

  /** Server start directory */
  protected String startdir = "/";

  /** Login information */
  protected LogInfo loginfo;
  
  /** is read only */
  protected boolean readOnly;

  protected transient RequestProcessor requestproc;
  
  /** Constructor.
  */
  public RemoteFileSystem() {
    info = this;
    change = this;
    DefaultAttributes a = new DefaultAttributes (info, change, this);
    attr = a;
    list = a;
    
    // Set listeners
    TopManager.getDefault().getRepository().addRepositoryListener( new RepositoryListener() {
        public void fileSystemAdded(RepositoryEvent ev) {
           addedFS(ev.getFileSystem());
        }  
        public void fileSystemRemoved(RepositoryEvent ev) {
           removedFS(ev.getFileSystem());
        }
        public void fileSystemPoolReordered(RepositoryReorderedEvent ev) {
        }
    });  
  }

  public void removedFS(org.openide.filesystems.FileSystem fs) {
     if (fs==this) {
          if (requestproc != null) {
            requestproc.stop();
            requestproc = null;
          }
          if (isConnected()) 
              removeClient();
     }
  }

  public void addedFS(org.openide.filesystems.FileSystem fs) {
     if (fs==this) { 
        connectOnBackground(true);
/*        post(new java.lang.Runnable() {
            public void run() {
               setConnected(true);  
               getRoot().refresh();
            }
          }
        );
*/     }  
  }

  /** Constructor that allows user to provide own capabilities
  * for this file system.
  * @param cap capabilities for this file system
  */
  public RemoteFileSystem(FileSystemCapability cap) {
    this ();
    setCapability (cap);
  }

  private void readObject(ObjectInputStream s)  throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    TopManager.getDefault().getRepository().addRepositoryListener( new RepositoryListener() {
        public void fileSystemAdded(RepositoryEvent ev) {
           addedFS(ev.getFileSystem());
        }  
        public void fileSystemRemoved(RepositoryEvent ev) {
           removedFS(ev.getFileSystem());
        }
        public void fileSystemPoolReordered(RepositoryReorderedEvent ev) {
        }
    });
  }
  
  private void writeObject(ObjectOutputStream s)  throws IOException {
      removeClient(); 
      s.defaultWriteObject();
  }
 
  /** Return system action for this filesystem */
  public SystemAction[] getActions () {
    SystemAction actions[] = super.getActions();
    SystemAction newactions[] = new SystemAction[actions.length+4];
    for (int i=0;i<actions.length;i++) 
        newactions[i]=actions[i];
    newactions[actions.length] = new SynchronizeAction();
    newactions[actions.length+1] = new DownloadAllAction();
    newactions[actions.length+2] = new CleanCacheAction();
    newactions[actions.length+3] = new ConnectAction();
    ((ConnectAction)newactions[actions.length+3]).setFS(this);
    return newactions;
  }
  
   protected void removeClient() {
    if (client != null) {
      client.close();
      client = null;
    }
    rootFile = null;
  }
 
  public void connectOnBackground(final boolean b) {
       post(new java.lang.Runnable() {
            public void run() {
               setConnected(b);  
               getRoot().refresh();
            }
          }
        );
  }

   /** Whether filesystem is connected to server.
   * @return Value of property connected.
   */
  public boolean isConnected() {
    if (client==null)  return false;
    return client.isConnected();
  }
  
  /** Connect to  or disconnect from server.
   * @param connected New value of property connected.
   */
  public void setConnected(boolean connected) {
    // is new state different?
    if (isConnected() == connected)   return; 
    if (!connected) {  // will be disconnected
           client.disconnect();
    }    
    else {
       try { 
            if (client == null || (client != null && client.compare(loginfo) != 0)) {
                 client = createClient(loginfo,cachedir);
                 rootFile = null; 
            }
            client.connect();
            if (rootFile == null) {
                RemoteFile root = createRootFile(client,cachedir);
                rootFile = root.getRoot(startdir);
                if (rootFile == null) {
                      startdirNotFound(startdir,loginfo.displayName());
                      startdir = "/";
                      rootFile = root;
                }     
            }
       }
       catch(IOException e) {
         if (connected && client!=null) client.close(); 
         errorConnect(e.toString());
       }
       synchronize("/");
    }  
    fireFileStatusChanged(new FileStatusEvent(this,getRoot(),true,true));
    //refreshRoot();
    //try { org.openide.loaders.DataObject.find(super.getRoot()).getNodeDelegate().setDisplayName(getDisplayName()); }
    //catch (org.openide.loaders.DataObjectNotFoundException e) {}
    firePropertyChange("connected", null, new Boolean(isConnected()));
    //firePropertyChange(PROP_SYSTEM_NAME, "", getSystemName());
  }


  public abstract RemoteClient createClient(LogInfo loginfo, File cache) throws IOException;

  public RemoteFile createRootFile(RemoteClient client, File cache) throws IOException {
    return new RemoteFile(client,this, this, cache);
  }
  
  
  /** Set whether the file system should be read only.
   * @param flag <code>true</code> if it should
  */
  public void setReadOnly(boolean flag) {
    if (flag != readOnly) {
      readOnly = flag;
      firePropertyChange (PROP_READ_ONLY, new Boolean (!flag), new Boolean (flag));
    }
  }

  /* Test whether file system is read only.
   * @return <true> if file system is read only
   */
  public boolean isReadOnly() {
    return readOnly;
  }
  
  /** Prepare environment by adding the root directory of the file system to the class path.
  * @param environment the environment to add to
  */
  public void prepareEnvironment(org.openide.filesystems.FileSystem.Environment environment) {
    environment.addClassPath(cachedir.toString ());
  }

  /** Test whether filesystem is ready to write. If no, throws exception */
  protected abstract void isReadyToModify() throws IOException;

  /** Test whether filesystem is ready to read. If no, throws exception */
  protected abstract void isReadyToRead() throws IOException ;

  /** Test whether filesystem is ready. */
  protected abstract boolean isReady() ;

  /** Get the RemoteFile for entered name
   * @param name
   * @return
   */
  protected RemoteFile getRemoteFile(String name) throws IOException {
    RemoteFile ftpfile = rootFile.find(name);
    // hack: if attributes file is not found, create new
    if (ftpfile==null && (name.endsWith(DefaultAttributes.ATTR_NAME_EXT) || name.endsWith(".nbattrs"))) {
          createData (name);
          ftpfile = rootFile.find(name);
    }
    return ftpfile;
  }


  /** Synchronize specified directory */
  public void synchronize(String name) {
    if (!isReady()) return;
    try {
      final RemoteFile f = getRemoteFile (name);
      if (f != null) {
        post(new Runnable() {
            public void run() {
                try { f.synchronize(); }
                catch(IOException e) {  TopManager.getDefault().notifyException(e); }
            }
        });
      }
      //else System.out.println("RemoteFileSystem.refreshAll: ftpfile "+name+" NOT FOUND");
    }
    catch(IOException e) { 
      //if (DEBUG) e.printStackTrace(); 
      TopManager.getDefault().notifyException(e);
    }
  }
  
  /** Download directory */
  public void downloadAll(String name) {
    if (!isReady()) return;
    try {
      final RemoteFile f = getRemoteFile (name);
      if (f != null) {
         post(new Runnable() {
            public void run() {
                try {f.downloadAll(); }
                catch(IOException e) {  TopManager.getDefault().notifyException(e); }
            }
         });
      }
      //else System.out.println("FTPFileSystem.downloadAll: ftpfile "+name+" NOT FOUND");
    }
    catch(IOException e) { 
      //if (DEBUG) e.printStackTrace(); 
      TopManager.getDefault().notifyException(e);
    }
  }
  
  /** Clean directory in cache.*/
  public void cleanCache(String name) {
    if (!isReady()) return;
    try {
      RemoteFile f = getRemoteFile (name);
      if (f != null)
          f.cleanCache(); 
      //else System.out.println("FTPFileSystem.cleanCache: ftpfile "+name+" NOT FOUND");
    }
    catch(IOException e) { 
      //if (DEBUG) e.printStackTrace(); 
      TopManager.getDefault().notifyException(e);
    }
  }
 
  //
  // List
  //

  /* Scans children for given name
   * @param name
   * @return
   */
  public String[] children(String name) {
    //System.out.println("*** FTPFileSystem.children: name="+name);
    String[] seznam = new String[0];
    if (!isReady())  
           return seznam;
    try {
      RemoteFile f = getRemoteFile (name);
      if (f != null) {
          if (f.isDirectory ())
          seznam = f.getStringChildren();
      }  
      //else System.out.println("FTPFileSystem.children: ftpfile "+name+" NOT FOUND");
    }
    catch (IOException e) {
      TopManager.getDefault().notifyException(e);
    }
    return seznam;
  }

  //
  // Change
  //

  /* Creates new folder named name.
  * @param name name of folder
  * @throws IOException if operation fails
  */
  /**
   * @param name
   * @throws IOException
   */
  public void createFolder(String name) throws java.io.IOException {
    //System.out.println("*** FTPFileSystem.createFolder: name="+name);
    isReadyToModify();
    RemoteFile f = null;
    String relname = null;
    int lastslash = name.lastIndexOf("/");
    if (lastslash ==-1) { 
       relname = name; 
       f = rootFile; 
    }
    else { 
       relname=name.substring(lastslash+1);
       f = rootFile.find(name.substring(0,lastslash));
    }
    if (f != null)
      f.createFolder(relname);
    //else System.out.println("FTPFileSystem.createFolder: parent of ftpfile "+name+" NOT FOUND");
}
  
  
  /** Creates new folder and all necessary subfolders
   * @param name
   * @throws IOException
   */
  public void createData (String name) throws IOException {
    //System.out.println("*** FTPFileSystem.createData: name="+name);
    isReadyToModify();
    RemoteFile f = null;
    String relname = null;
    int lastslash = name.lastIndexOf("/");
    if (lastslash ==-1) { 
       relname = name; 
       f = rootFile; 
    }
    else { 
       relname=name.substring(lastslash+1);
       f = rootFile.find(name.substring(0,lastslash));
    }
    if (f != null) 
      f.createData(relname);
    //else System.out.println("FTPFileSystem.createData: parent of ftpfile "+name+" NOT FOUND");
}

  /* Renames a file.
  *
  * @param oldName old name of the file
  * @param newName new name of the file
  * @throws IOException
  */
  public void rename(String oldName,String newName) throws IOException {
    //System.out.println("*** FTPFileSystem.rename: oldname="+oldName+" newname="+newName);
    isReadyToModify();
    RemoteFile of = getRemoteFile (oldName);
    if (of != null) {
        String name = null;
        String oname=oldName ,nname=newName;
        if (!oldName.startsWith("/")) oname="/"+oldName;
        if (!newName.startsWith("/")) nname="/"+newName;
        int slash1 = oname.lastIndexOf('/');
        int slash2 = nname.lastIndexOf('/');
        if (slash1 != slash2 || !oname.substring(0,slash1).equals(nname.substring(0,slash2))) {
          IOException e = new IOException("Can't rename !!!!!!");
          e.printStackTrace();
          throw e;
        }
        if (slash2 == -1) name = newName;
        else name = nname.substring(slash2+1);
        of.rename(name);
    }
    //else System.out.println("FTPFileSystem.rename: ftpfile "+oldName+" NOT FOUND");
}

  /* Delete the file. 
  *
  * @param name name of file
  * @exception IOException if the file could not be deleted
  * @throws IOException
  */
  public void delete(String name) throws IOException {
    //System.out.println("*** FTPFileSystem.delete: name="+name);
    isReadyToModify();
    RemoteFile file = getRemoteFile (name);
    if (file != null)
      file.delete();
    // else System.out.println("FTPFileSystem.delete: ftpfile "+name+" NOT FOUND");
  }

  //
  // Info
  //

  /*
  * Get last modification time.
  * @param name the file to test
  * @return the date
  */
  /**
   * @param name
   * @return
   */
  public java.util.Date lastModified(String name) {
    //System.out.println("*** FTPFileSystem.lastModified: name="+name);
    java.util.Date date  = new java.util.Date(0);
    if (!isReady()) return date;
    try {
      RemoteFile f = getRemoteFile(name); 
      if (f != null) 
        date = f.lastModified();
      // else System.out.println("FTPFileSystem.lastModified: ftpfile "+name+" NOT FOUND");
    }
    catch (IOException e) {
      TopManager.getDefault().notifyException(e);
    }
    return date;
  }

  /* Test if the file is folder or contains data.
  * @param name name of the file
  * @return true if the file is folder, false otherwise
  */
  public boolean folder(String name) {
    //System.out.println("*** FTPFileSystem.folder: name="+name);
    if (!isReady())  return true;
    try {
      RemoteFile f = getRemoteFile (name);
      if (f != null)
          return  f.isDirectory (); 
      //else System.out.println("FTPFileSystem.folder: ftpfile "+name+" NOT FOUND");
    }
    catch (IOException e) {
      TopManager.getDefault().notifyException(e);
    }
    return true;
  }
  
  /* Test whether this file can be written to or not.
  * @param name the file to test
  * @return <CODE>true</CODE> if file is read-only
  */
  public boolean readOnly(String name) {
    //System.out.println("*** FTPFileSystem.readOnly: name="+name);
    if (!isReady()) return false;
    try {  
      RemoteFile f = getRemoteFile (name);
      if (f != null)
          return f.isReadOnly();
      //else System.out.println("FTPFileSystem.readOnly: ftpfile "+name+" NOT FOUND");
    }
    catch (IOException e) {
       TopManager.getDefault().notifyException(e);
    }  
    return false;
  }
  
  /** Get the MIME type of the file.
  * Uses {@link FileUtil#getMIMEType}.
  *
  * @param name the file to test
  * @return the MIME type textual representation, e.g. <code>"text/plain"</code>
  */
  public String mimeType (String name) {
    //System.out.println("*** FTPFileSystem.mimetype: name="+name);
    int i = name.lastIndexOf ('.');
    String s;
    try {
      s = FileUtil.getMIMEType (name.substring (i + 1));
    } catch (IndexOutOfBoundsException e) {
      s = null;
    }
    return s == null ? "content/unknown" : s; // NOI18N
  }

  /** Get the size of the file.
  *
  * @param name the file to test
  * @return the size of the file in bytes or zero if the file does not contain data (does not
  *  exist or is a folder).
  */
  public long size (String name) {
    //System.out.println("*** FTPFileSystem.size: name="+name);
    if (!isReady()) return 0;
    try {  
      RemoteFile f = getRemoteFile (name);
      if (f != null)
          return f.getSize();
      //else System.out.println("FTPFileSystem.size: ftpfile "+name+" NOT FOUND");
    }
    catch (IOException e) {
      TopManager.getDefault().notifyException(e);
    }  
    return 0;
  }

  /** Get input stream.
  *
  * @param name the file to test
  * @return an input stream to read the contents of this file
  * @exception FileNotFoundException if the file does not exists or is invalid
  */
  public InputStream inputStream (String name) throws java.io.FileNotFoundException {
    //System.out.println("*** FTPFileSystem.inputStream: name"+name);
    InputStream is = null;
    try {
      isReadyToRead();
      RemoteFile f = getRemoteFile (name);
      if (f != null)
          is = f.getInputStream (); 
      //else System.out.println("FTPFileSystem.inputStream: ftpfile "+name+" NOT FOUND");
    }
    catch (IOException e) { 
      throw new FileNotFoundException(e.toString()); 
    }
    return is;
  }

  /** Get output stream.
  *
  * @param name the file to test
  * @return output stream to overwrite the contents of this file
  * @exception IOException if an error occures (the file is invalid, etc.)
  */
  public OutputStream outputStream (String name) throws java.io.IOException {
    //System.out.println("*** FTPFileSystem.outputStream: name="+name);
    isReadyToModify();
    RemoteFile f = getRemoteFile (name);
    if (f != null)
        return f.getOutputStream (); 
    //else System.out.println("FTPFileSystem.outputStream: ftpfile "+name+" NOT FOUND");
    return null;
  }
  
  
  /** Does nothing to lock the file.
   * @param name name of the file
   * @throws IOException
   */
  public void lock (String name) throws IOException {
     //System.out.println("*** FTPFileSystem.lock: name="+name);
  }
  
  /** Does nothing to unlock the file.
  *
  * @param name name of the file
  */
  public void unlock (String name) {
    //System.out.println("*** FTPFileSystem.unlock: name="+name);
  }

  /** Does nothing to mark the file as unimportant.
  *
  * @param name the file to mark
  */
  public void markUnimportant (String name) {
  }
  
  /** Informs user that startdir was not found on server. */
  protected abstract void startdirNotFound(String startdir, String server);
   
  /** Informs user that some error occurs during connecting. */
  protected abstract void errorConnect(String error);

  public void post(Runnable run) {
     if (requestproc == null) requestproc = new RequestProcessor("Remote Filesystem Request Processor for "+loginfo.displayName());
     requestproc.post(run);
  }
} 