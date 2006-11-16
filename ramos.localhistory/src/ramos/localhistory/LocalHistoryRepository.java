/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is Ramon Ramos. The Initial Developer of the Original
 * Software is Ramon Ramos. All rights reserved.
 *
 * Copyright (c) 2006 Ramon Ramos
 */
package ramos.localhistory;

import java.io.FileInputStream;
import java.util.zip.GZIPOutputStream;
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
    FileObject fileObject = dataObject.getPrimaryFile();
    final File file = FileUtil.toFile(fileObject);
    String filename = filenameFromPath(file);
    filename += String.valueOf(new Date().getTime()+".gz");
    lastModifiedRecord.put(dataObject, fileObject.lastModified());
    //File copiedFile = new File(FileUtil.toFile(LHRepositoryDir),filename);
    FileLock lock = null;
    GZIPOutputStream gzip = null;
    try {
      FileObject copied = LHRepositoryDir.createData(filename);
      //copiedFile.createNewFile();
      //FileObject copied = FileUtil.toFileObject(copiedFile);
      //FileObject copied = fileObject.copy(LHRepositoryDir, filename, EMPTY);
      copied.setAttribute(PATH, file.getAbsolutePath());
      //copiedList.add(copied);
      if (comment != null) {
        copied.setAttribute("Annotation", comment);
      }
      
      System.out.println("copied: "+copied);
      System.out.println(copied.getAttribute(PATH));
      //System.out.println(copiedFile.getAbsolutePath());
      //FileOutputStream fos = new FileOutputStream(copiedFile);
      lock = copied.lock();
      gzip = new GZIPOutputStream(copied.getOutputStream(lock));
      FileUtil.copy(new FileInputStream(file),gzip);
      
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
  
  private boolean blackList(final OperationEvent e) {
    return blackList(e.getObject().getPrimaryFile());
  }
  private boolean blackList(final FileObject e) {
    //return false;
    String ext = e.getExt();
    
    return (
       e.isFolder() ||
       e.getPath().contains("/build/classes/") || 
       e.getPath().contains("/build/cluster/") || 
       e.getPath().contains("/build/testuserdir/") || 
       e.getPath().contains("/build/updates/") || 
       ext.equalsIgnoreCase("gif") || 
       ext.equalsIgnoreCase("jar") || 
       ext.equalsIgnoreCase("nbm") ||
       ext.equalsIgnoreCase("zip") || 
       ext.equalsIgnoreCase("btd") ||
       ext.equalsIgnoreCase("png") ||
       ext.equalsIgnoreCase("class")
       );
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
