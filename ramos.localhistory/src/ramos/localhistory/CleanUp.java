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

import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;


/**
 * TODO: unmake static
 * @author Ramon Ramos
 */
public class CleanUp {
  private static FileObject theDir = Repository.getDefault()
                                        .getDefaultFileSystem()
                                        .getRoot()
                                        .getFileObject("local history");
  //private int maxcount = 1000;
  private static HashMap<String, List<FileObject>> pathAndListMap;
  private static int deleted = 0;
//     = new HashMap<String, List<FileObject>>();

  /** Creates a new instance of CleanUp */
  //  public CleanUp() {
  //    //
  //  }
  //  void doCleanUp(final VersionNode[] nodes, final int from){
  //    Thread t = new Thread(){
  //      public void run(){
  //        for (int i = from; i < nodes.length; i++) {
  //          try {
  //            nodes[i].delete();
  //          } catch (IOException ex) {
  //            ex.printStackTrace();
  //          }
  //        }
  //      }
  //    };
  //    t.start();
  //  }
  //  void doCleanUp(final Iterator<VersionNode> iterator){
  //    Thread t = new Thread(){
  //      /**
  //       * If this thread was constructed using a separate
  //       * <code>Runnable</code> run object, then that
  //       * <code>Runnable</code> object's <code>run</code> method is called;
  //       * otherwise, this method does nothing and returns.
  //       * <p>
  //       * Subclasses of <code>Thread</code> should override this method.
  //       *
  //       * @see #start()
  //       * @see #stop()
  //       * @see #Thread(ThreadGroup, Runnable, String)
  //       */
  //      public void run() {
  //        while(iterator.hasNext()){
  //          try {
  //            iterator.next().delete();
  //          } catch (IOException ex) {
  //            ex.printStackTrace();
  //          }
  //        }
  //      }
  //      
  //    };
  //    t.start();
  //    doCleanUp();
  //  }
  public static int doCleanUp(int max) {
    pathAndListMap 
     = new HashMap<String, List<FileObject>>();
    deleted = 0;
    //find copies
    //sort by date
    //delete ones that are too old or too many

    //in dir
    //list all
    //purge by date
    //purge by count
    //atribute path wenn zu viele -> sort by date. count complete delete the rest

    /*
     filesystem way

    for fileobject in files:
      get path attr
      add fileobject to list of path
    end-for

    for list in path-lists:
      sort list by date
      remove and delete fileobjects
    end-for

     */
    Enumeration<? extends FileObject> files = theDir.getData(false);

    while (files.hasMoreElements()) {
      FileObject fo = files.nextElement();
      String path = (String) fo.getAttribute("path");
      addToListOfPath(fo, path);
    }

    Collection<List<FileObject>> lists = pathAndListMap.values();

    for (final List<FileObject> list : lists) {
      removeSuperflous(list,max);
    }
    return deleted;
  }

  private static void addToListOfPath(
    final FileObject fo,
    final String path) {
    if (path != null) {
      List<FileObject> listOfPath = getListOfPath(path);
      listOfPath.add(fo);
    }
  }

  private static List<FileObject> getListOfPath(final String path) {
    List<FileObject> listOfPath = pathAndListMap.get(path);

    if (listOfPath == null) {
      listOfPath = new ArrayList<FileObject>(25);
      pathAndListMap.put(path, listOfPath);
    }

    return listOfPath;
  }

  private static List<FileObject> sortByDate(final List<FileObject> list) {
    Collections.sort(list, new FileObjectComparator());

    return list;
  }

  private static void removeSuperflous(final List<FileObject> list, int max) {
    int count = 0;
//    System.out.println("max = "+max);
//    System.out.println("list size = "+list.size());
    if (list.size() > max) {
      List<FileObject> sortedList = sortByDate(list);
      ListIterator<FileObject> it = sortedList.listIterator(max);

      while (it.hasNext()) {
        FileObject fo = it.next();

        try {
          fo.delete();
          count++;
          deleted++;
        } catch (final IOException ex) {
          ex.printStackTrace();
        }
      }
    }
//    System.out.println("deleted "+count);
  }

  private static class FileObjectComparator implements Comparator<FileObject> {
    public int compare(final FileObject o1, final FileObject o2) {
      //return o1.lastModified().compareTo(o2.lastModified());
      return o2.lastModified().compareTo(o1.lastModified());
    }
  }
}
