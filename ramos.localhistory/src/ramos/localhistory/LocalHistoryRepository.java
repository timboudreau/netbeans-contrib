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
 * The Original Software is Ramon Ramos. The Initial Developer of the Original
 * Software is Ramon Ramos. All rights reserved.
 *
 * Copyright (c) 2006 Ramon Ramos
 */
package ramos.localhistory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;
import org.netbeans.api.queries.SharabilityQuery;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.OperationEvent;
import org.openide.loaders.OperationListener;

import org.openide.util.Utilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author Ramon Ramos
 * TODO: no lh copies after refactorings?
 */
public class LocalHistoryRepository
   implements ChangeListener, OperationListener {
  private static final Comparator<VersionNode> COMPARATOR = new MyComparator();
  final static String PATH = "path";
  private static LocalHistoryRepository instance;
  private static final String FILE_SEP = System.getProperty("file.separator");
  private static final String DOT = ".";
  private static final String DOS_PUNTOS = ":";
  private static final String EMPTY = "";
  private FileObject LHRepositoryDir = Repository.getDefault()
     .getDefaultFileSystem()
     .getRoot()
     .getFileObject("local history");
  private HashMap<DataObject, Date> lastModifiedRecord = new HashMap<DataObject, Date>();
  
  /** Creates a new instance of LocalHistoryRepository */
  private LocalHistoryRepository() {
  }
  
  public static LocalHistoryRepository getInstance() {
    if (instance == null) {
      instance = new LocalHistoryRepository();
    }
    
    return instance;
  }
  
  private boolean old(final FileObject fo) {
    //could collect them and delete them at the end in a thread
    return false;
  }
  
  public Collection<VersionNode> fillRevisionsList(final File projectFile) {
    TreeSet<VersionNode> revisionsSet = new TreeSet<VersionNode>(COMPARATOR);
    if (blackList(projectFile)) return revisionsSet;
    FileObject[] localHistoryFiles = LHRepositoryDir.getChildren();
    
    String projectFilePath = projectFile.getAbsolutePath();
    //    revisionsSet.clear();
    
    for (FileObject fo : localHistoryFiles) {
      if (!fo.isFolder() && (fo.getAttribute(PATH) != null) &&
         fo.getAttribute(PATH).equals(projectFilePath)) {
        
        if (!old(fo)) {
          try {
            revisionsSet.add(new VersionNode(fo));
          } catch (DataObjectNotFoundException ex) {
            ex.printStackTrace();
          }
        }
        
      }
    }
    
    //look if size > max.rev.count
    //    //if so
    //    //for(int i = 0; i < delta; I++){
    //    //VersionNode n = revisionsSet.last();
    //    //revisionsSet.remove(n);
    //    //n.getoriginal().delete;
    //    //n = null;
    //    //}
    return revisionsSet;
  }
  
  /** for doing cleanup on exit, delete old copies due to some revision count limit */
  //private Set<FileObject> copiedList = new HashSet<FileObject>();
  
  /**
   * Creates a new instance of Listener
   */
  
  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  
  /**
   * DOCUMENT ME!
   *
   * @param fileDataObject DOCUMENT ME!
   */
  private void handleLocalHistory(final DataObject fileDataObject) {
    if (!lastModifiedRecord.keySet().contains(fileDataObject)) {
      final FileObject fileObject = fileDataObject.getPrimaryFile();
      
      if (FileUtil.toFile(fileObject) != null) { //not virtual
        lastModifiedRecord.put(fileDataObject, fileObject.lastModified());
        fileDataObject.addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            if (isCopyTrigger(evt)) {
              makeLocalHistoryCopy(fileDataObject);
            }
          }
          
          private boolean isCopyTrigger(PropertyChangeEvent evt) {
            return evt.getPropertyName()
               .equals(DataObject.PROP_MODIFIED) &&
               !fileObject.lastModified()
               .equals(lastModifiedRecord.get(fileDataObject)) &&
               evt.getOldValue().equals(Boolean.TRUE) &&
               evt.getNewValue().equals(Boolean.FALSE);
          }
        });
      }
    }
  }
  
  /**
   * also called from VersionNode
   *
   * @param dataObject DOCUMENT ME!
   */
  protected void makeLocalHistoryCopy(
     final DataObject dataObject,
     final String comment) {
    long timestamp = new Date().getTime();
   
    FileObject primaryFileObject = dataObject.getPrimaryFile();
    final File primaryFile = FileUtil.toFile(primaryFileObject);
    String filenameOfCopy = filenameFromPath(primaryFile);
    filenameOfCopy += String.valueOf(timestamp+".gz");
    lastModifiedRecord.put(dataObject, primaryFileObject.lastModified());
    FileObject copied = null;
    try {
      //File copiedFile = new File(FileUtil.toFile(LHRepositoryDir),filenameOfCopy);
      copied = doCopying(new FileInputStream(primaryFile),  filenameOfCopy, primaryFile.getAbsolutePath(), comment);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
     // iterate over dataobject.files()
    //set "secondaries1" and up attribute of primary to list of secondaries
    int i = 1;
    for (Object elem : dataObject.files()) {
      FileObject aSecondaryFileObject = (FileObject)elem;
      if (!primaryFileObject.equals(aSecondaryFileObject)){
        File secondaryFile = FileUtil.toFile(aSecondaryFileObject);
        String filenameOfSecondaryCopy = filenameFromPath(secondaryFile);
        filenameOfSecondaryCopy += String.valueOf(timestamp+".gz");
        try {
          doCopying(new FileInputStream(secondaryFile),filenameOfSecondaryCopy,secondaryFile.getAbsolutePath(),null);
          copied.setAttribute("secondary"+i,filenameOfSecondaryCopy);
          i++;
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
  }
  
  private FileObject doCopying(InputStream is, String filenameOfCopy, String path, String comment){
    FileLock lock = null;
    GZIPOutputStream gzip = null;
    FileObject copied = null;
    try {
      copied = LHRepositoryDir.createData(filenameOfCopy);
      copied.setAttribute(PATH, path);
      if (comment != null) {
        copied.setAttribute("Annotation", comment);
      }
      lock = copied.lock();
      gzip = new GZIPOutputStream(copied.getOutputStream(lock));
      FileUtil.copy(is,gzip);
            //System.out.println("copied: "+copied+" mime: "+copied.getMIMEType());
    //System.out.println(copied.getAttribute(PATH));
    } catch (IOException ex) {
      ex.printStackTrace();
    }finally{
      try {
        if (gzip != null) gzip.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      if (lock!=null) lock.releaseLock();
    }
    return copied;
  }
  /**
   * DOCUMENT ME!
   *
   * @param ev DOCUMENT ME!
   */
  public void operationCopy(final OperationEvent.Copy ev) {
    if (!isSystemFileSystem(ev.getObject()) && !blackList(ev)) {
      //System.out.println("operationCopy");
      handleLocalHistory(ev.getObject());
      makeLocalHistoryCopy(ev.getObject(),
         "copied from " + ev.getOriginalDataObject().getName());
    }
  }
  
  static boolean blackList(final OperationEvent e) {
    return blackList(e.getObject().getPrimaryFile());
  }
  static boolean blackList(final File file) {
    return blackList(FileUtil.toFileObject(file));
  }
  
  static boolean blackList(final FileObject f) {
    
    File _f = FileUtil.toFile(f);
    if (_f != null && SharabilityQuery.getSharability(_f) ==
       SharabilityQuery.NOT_SHARABLE) {
      return true;
    }
    if (f.getMIMEType().startsWith("text/")) {
      return false;
    }
    //want to allow unrecognized plain text files
    //would like to have a(nother) way to differentiate between binary files and text files
    return !f.getMIMEType().equals("content/unknown") || unknownAndBinary(f);//
  }
  private static String[] unknownAndBinaries = new String[]{"jar","zip","nbm"};
  private static boolean unknownAndBinary(FileObject file){
    String ext = file.getExt();
    for (String elem : unknownAndBinaries) {
      if (elem.equalsIgnoreCase(ext)) return true;
    }
    return false;
    
  }
  
  
  private boolean blackList(final DataObject e) {
    return blackList(e.getPrimaryFile());
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param ev DOCUMENT ME!
   */
  public void operationCreateFromTemplate(final OperationEvent.Copy ev) {
    if (!isSystemFileSystem(ev.getObject()) && !blackList(ev)) {
      //System.out.println("operationCreateFromTemplate");
      handleLocalHistory(ev.getObject());
      makeLocalHistoryCopy(ev.getObject(),
         "created from template " + ev.getOriginalDataObject().getName());
    }
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param ev DOCUMENT ME!
   */
  public void operationCreateShadow(final OperationEvent.Copy ev) {
    //System.out.println("shadow");
  }
  
  /**
   * Saves entry in deleted folder, so we record which files have been
   * deleted.
   *
   * @param ev the OperationEvent containing deleted data object (file)
   */
  public void operationDelete(final OperationEvent ev) {
    //      try {
    //         //ausser wenn es in system filesystem passiert
    //         if (!ev.getObject().getPrimaryFile().getFileSystem()
    //                  .equals(Repository.getDefault().getDefaultFileSystem())) {
    //            //find  folder for store deleted entries
    //            FileObject deletedFolder = LHRepositoryDir.getFileObject("deleted");
    //
    //            //save
    //            FileObject deletedFileObject = ev.getObject().getPrimaryFile();
    //            File deletedFile = FileUtil.toFile(deletedFileObject);
    //            String filenameToUseForEntry = filenameFromPath(deletedFile);
    //
    //            try {
    //               //FileObject entry = deletedFolder.createData(filenameToUseForEntry);
    //               FileObject entry = FileUtil.copyFile(deletedFileObject,
    //                     deletedFolder, filenameToUseForEntry);
    //               entry.setAttribute(PATH, deletedFile.getAbsolutePath());
    //            } catch (final IOException ex) {
    //               ex.printStackTrace();
    //            }
    //         }
    //      } catch (final FileStateInvalidException ex) {
    //         ex.printStackTrace();
    //      }
  }
  
  /**
   * TODO: copying the right file??????
   *
   * @param ev DOCUMENT ME!
   */
  public void operationMove(final OperationEvent.Move ev) {
    if (!isSystemFileSystem(ev.getObject()) && !blackList(ev)) {
      //System.out.println("operationCopy");
      handleLocalHistory(ev.getObject());
      makeLocalHistoryCopy(ev.getObject(),
         "moved from " + ev.getOriginalPrimaryFile().getParent().getPath());
    }
    
    //use parent to build oldPath
    //      try {
    //         if (!ev.getObject().getPrimaryFile().getFileSystem()
    //                  .equals(Repository.getDefault().getDefaultFileSystem())) {
    //            //System.out.println("operationMove");
    ////            handleLocalHistory(ev.getObject());
    ////            makeLocalHistoryCopy(ev.getObject());
    //            //should move paths instead
    //            //String oldPath = FileUtil.toFile(ev.getOriginalPrimaryFile()).getAbsolutePath();
    //            String newPath = FileUtil.toFile(ev.getObject().getPrimaryFile()).getAbsolutePath();
    //            //find all copies and change path to ev.getObject.getPrimaryFile() ...
    //            FileObject[] children = LHRepositoryDir.getChildren();
    //            for (FileObject fileObject : children) {
    //               if (fileObject.getAttribute(PATH).equals(oldPath)){
    //                  try {
    //                     fileObject.setAttribute(PATH,newPath);
    //                  } catch (IOException ex) {
    //                     ex.printStackTrace();
    //                  }
    //               }
    //            }
    //         }
    //      } catch (final FileStateInvalidException ex) {
    //         ex.printStackTrace();
    //      }
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param ev DOCUMENT ME!
   */
  public void operationPostCreate(final OperationEvent ev) {
    //    try {
    //         if (!ev.getObject().getPrimaryFile().getFileSystem()
    //                  .equals(Repository.getDefault().getDefaultFileSystem())) {
    //            //System.out.println("operationCreateFromTemplate");
    //            handleLocalHistory(ev.getObject());
    //            //makeLocalHistoryCopy(ev.getObject(),"POSTCREATE recognition");
    //         }
    //      } catch (final FileStateInvalidException ex) {
    //         ex.printStackTrace();
    //      }
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param ev DOCUMENT ME!
   */
  public void operationRename(final OperationEvent.Rename ev) {
    if (!isSystemFileSystem(ev.getObject()) && !blackList(ev)) {
      //System.out.println("operationCopy");
      handleLocalHistory(ev.getObject());
      makeLocalHistoryCopy(ev.getObject(),
         "renamed from " + ev.getOriginalName());
    }
    
    /*
    System.out.println(ev.getOriginalName()); //can tell what the name really was
                                              //if with same ext file in local history and does not exist anymore. take it.
                                              //else do nothing. ext was changed: not my problem
                                              //entweder selbe SSS.k
     
    String newPath = FileUtil.toFile(ev.getObject().getPrimaryFile())
                             .getAbsolutePath();
    int lastIndexOfDot = newPath.lastIndexOf(".");
     */
    
    //
    //muss parsen: ev.getOriginalName()
    //last index of .   -1 or i path_sep
    //damit oldPath bauen
    
    //     try {
    //         if (!ev.getObject().getPrimaryFile().getFileSystem()
    //                  .equals(Repository.getDefault().getDefaultFileSystem())) {
    //            //System.out.println("operationMove");
    //            //should move paths instead
    //            String oldPath = FileUtil.toFile(ev.getOriginalPrimaryFile()).getAbsolutePath();
    //            String newPath = FileUtil.toFile(ev.getObject().getPrimaryFile()).getAbsolutePath();
    //            //find all copies and change path to ev.getObject.getPrimaryFile() ...
    //            FileObject[] children = LHRepositoryDir.getChildren();
    //            for (FileObject fileObject : children) {
    //               if (fileObject.getAttribute(PATH).equals(oldPath)){
    //                  try {
    //                     fileObject.setAttribute(PATH,newPath);
    //                  } catch (IOException ex) {
    //                     ex.printStackTrace();
    //                  }
    //               }
    //            }
    //         }
    //      } catch (final FileStateInvalidException ex) {
    //         ex.printStackTrace();
    //      }
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void stateChanged(final ChangeEvent e) {
    DataObject[] dataObjects = DataObject.getRegistry()
       .getModified();
    
    for (final DataObject dataObject : dataObjects) {
      if (!blackList(dataObject)) {
        handleLocalHistory(dataObject);
      }
    }
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param file DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  private String filenameFromPath(final File file) {
    String filename = file.getAbsolutePath();
    filename = Utilities.replaceString(filename, DOS_PUNTOS, DOT);
    filename = Utilities.replaceString(filename, FILE_SEP, DOT);
    
    return filename;
  }
  
  private boolean isSystemFileSystem(final DataObject dataObject) {
    boolean ret = false;
    
    try {
      ret = dataObject.getPrimaryFile()
         .getFileSystem()
         .equals(Repository.getDefault().getDefaultFileSystem());
    } catch (final FileStateInvalidException ex) {
      ex.printStackTrace();
    }
    
    return ret;
  }
  
  protected void makeLocalHistoryCopy(final DataObject fileDataObject) {
    makeLocalHistoryCopy(fileDataObject, null);
  }
  
  private static class MyComparator
     implements Comparator<VersionNode>, Serializable {
    public int compare(
       final VersionNode fn1,
       final VersionNode fn2) {
      DataObject do1 = (DataObject) fn1.getLookup()
         .lookup(DataObject.class);
      DataObject do2 = (DataObject) fn2.getLookup()
         .lookup(DataObject.class);
      FileObject fo1 = do1.getPrimaryFile();
      FileObject fo2 = do2.getPrimaryFile();
      
      return fo1.lastModified().compareTo(fo2.lastModified());
    }
    
    public boolean equals(final Object obj) {
      return false;
    }
  }
}
