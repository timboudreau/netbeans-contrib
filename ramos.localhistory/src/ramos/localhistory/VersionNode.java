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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import org.openide.actions.DeleteAction;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;

import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.ReadWrite;

import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;

import java.awt.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.swing.Action;


public class VersionNode
   extends FilterNode implements Comparable{
  private static final String ICON_PATH =
     "ramos/localhistory/resources/clock.png";
  private static final String ANNOTATION = "Annotation";
  private FileObject fileCopy;
  private String htmlDisplayName;
  private String displayName;
  
  public VersionNode(final FileObject fo)
     throws DataObjectNotFoundException {
    super(DataObject.find(fo).getNodeDelegate());
    handleDeleteIfSecondaries(fo);
    fileCopy = fo;
    htmlDisplayName = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.MEDIUM).format(fileCopy.lastModified());
    displayName = String.valueOf(fileCopy.lastModified().getTime());
  }
  public String getHtmlDisplayName() {
    return htmlDisplayName;
    //return fileCopy.lastModified().toString();
    
  }
  @Override
  public String getDisplayName() {
    return displayName;
    //return getName();
    // return fileCopy.lastModified().toString();
  }
  
  @Override
  public String getName() {
    //return String.valueOf(fileCopy.lastModified().getTime());
    //return fileCopy.lastModified().toString();
    return htmlDisplayName;
  }
  
  @Override
  public boolean canRename() {
    return false;
  }
  
  @Override
  public Action[] getActions(final boolean context) {
    return new Action[] { SystemAction.get(DeleteAction.class) };
  }
  
  @Override
  public Action getPreferredAction() {
    return null;
  }
  
  
  @Override
  public Node.PropertySet[] getPropertySets() {
    //System.out.println("getPropertySets");
    PropertySet[] retValue = new PropertySet[1];
    final ReadWrite prop =
       new AnnotationProperty(ANNOTATION, ANNOTATION, null);
    
    retValue[0] =
       new PropertySet() {
      public Node.Property[] getProperties() {
        return new Property[] { prop };
      }
    };
    
    return retValue;
  }
  
  public String getShortDescription() {
    //return "an older version";
    return null;
  }
  
  public Image getIcon(final int type) {
    return Utilities.loadImage(ICON_PATH);
  }
  
  public void delete()
     throws IOException {
    fileCopy.delete();
  }
  
  public Reader getReader()
     throws IOException {
    Reader retValue;
    if (fileCopy.getExt().equals("gz")){
      //System.out.println("is gz");
      BufferedReader br =
         new BufferedReader(
         new InputStreamReader(
         new GZIPInputStream(fileCopy.getInputStream())));
      retValue = br;
    }else{
      //System.out.println("normal old");
      File file = FileUtil.toFile(fileCopy);
      retValue = new BufferedReader(new FileReader(file));
    }
    return retValue;
  }
  //TODO: reorganize revert method
  public void revert(final FileObject current) {
    
    //primary
    InputStream is = null;
    OutputStream os = null;
    FileLock lock = null;
    FileObject source = fileCopy;
    FileObject target = current;
    try {
      
      lock = target.lock();
      if (source.getExt().equals("gz")){
        is = new GZIPInputStream(source.getInputStream());
      } else {
        is = source.getInputStream();
      }
      os = target.getOutputStream(lock);
      FileUtil.copy(is, os);
//      System.out.println("revert: "+source+" mime: "+source.getMIMEType());
//      System.out.println(source.getAttribute("path"));
      
    } catch (final FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }finally{
      try {
        if (is != null) is.close();
        if (os != null) os.close();
      } catch (IOException ex) {
      }
      if (lock != null) lock.releaseLock();
    }
    Set files = null;
    try {
      files = DataObject.find(current).files();
    } catch (DataObjectNotFoundException ex) {
      ex.printStackTrace();
    }
    //secondaries
    int i = 1;
    Object value;
    while ((value = fileCopy.getAttribute("secondary"+i)) != null){
      //revert secondaries.path to secondaries
      String filenameOfSecondaryCopy = (String)value;
      //get FO
      FileObject dir = Repository.getDefault()
         .getDefaultFileSystem()
         .getRoot()
         .getFileObject("local history");
      FileObject secondaryCopy = dir.getFileObject(filenameOfSecondaryCopy);
      String pathOfSecondary = (String) secondaryCopy.getAttribute("path");
      InputStream secondaryCopyInputStream = null;
      OutputStream secondaryOutputStream = null;
      try {
        if (secondaryCopy.getExt().equals("gz")){
          secondaryCopyInputStream = new GZIPInputStream(secondaryCopy.getInputStream());
        } else {
          secondaryCopyInputStream = secondaryCopy.getInputStream();
        }
        secondaryOutputStream = new FileOutputStream(pathOfSecondary);
        FileUtil.copy(secondaryCopyInputStream, secondaryOutputStream);
//        System.out.println("revert: "+secondaryCopy+" mime: "+secondaryCopy.getMIMEType());
//        System.out.println(secondaryCopy.getAttribute("path"));
      } catch (FileNotFoundException ex) {
        ex.printStackTrace();
      } catch (IOException ex) {
        ex.printStackTrace();
      }finally{
        try {
          if (secondaryCopyInputStream != null) secondaryCopyInputStream.close();
          if (secondaryOutputStream != null) secondaryOutputStream.close();
        } catch (IOException ex) {
        }
        
      }
      i++;
    }
    String annotation = (String) source.getAttribute(ANNOTATION);
    String newAnnotation = "reverted to " + getName();
    if (annotation != null) newAnnotation += " <" + annotation + ">";
    try {
      LocalHistoryRepository.getInstance()
         .makeLocalHistoryCopy(DataObject.find(target),newAnnotation);
    } catch (DataObjectNotFoundException ex) {
      ex.printStackTrace();
    }
    
    
  }
  private List<Object> secondaries = new ArrayList<Object>(3);
  private void handleDeleteIfSecondaries(final FileObject fo) {
    
    if (fo.getAttribute("secondary1")!= null) {
      Object value;
      int j = 1;
      while ((value = fo.getAttribute("secondary"+j)) != null){
        secondaries.add(value);
        j++;
      }
      fo.addFileChangeListener(new FileChangeAdapter(){
        public void fileDeleted(FileEvent fe) {
//          System.out.println(fe.getFile()+" was deleted");
          //delete also secondaries
          for (Object value : secondaries) {
            //revert secondaries.path to secondaries
            String filenameOfSecondaryCopy = (String)value;
            //get FO
            FileObject dir = Repository.getDefault()
               .getDefaultFileSystem()
               .getRoot()
               .getFileObject("local history");
            FileObject secondaryCopy = dir.getFileObject(filenameOfSecondaryCopy);
            try {
              secondaryCopy.delete();
              //System.out.println(secondaryCopy+" was deleted too");
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        }
      });
    }
    
  }
  public int compareTo(Object o) {
    //System.out.println("compareTo");
    VersionNode vn = (VersionNode)o;
    return fileCopy.lastModified().compareTo(vn.fileCopy.lastModified());
  }
  
  public Object getValue(String attributeName) {
    //System.out.println("getValue "+attributeName);
    Object retValue;
    
    retValue = super.getValue(attributeName);
    return retValue;
  }
  
  /**
   * Delegates to original, if no special lookup provided in constructor,
   * Otherwise it delegates to the lookup. Never override this method
   * if the lookup is provided in constructor.
   *
   * @param type the class to look for
   * @return instance of that class or null if this class of cookie
   *    is not supported
   * @see Node#getCookie
   */
  //  public Node.Cookie getCookie(Class type) {
  //    System.out.println("getCookie "+type);
  //    Cookie retValue = null;
  //    if (type.equals(DataObject.class)){
  //      try {
  //        retValue = DataObject.find(FileUtil.toFileObject(new File((String) fileCopy.getAttribute("path"))));
  //      } catch (DataObjectNotFoundException ex) {
  //        ex.printStackTrace();
  //      }
  //    }else{
  //      retValue = super.getCookie(type);
  //      //System.out.println("super.getCookie "+retValue);
  //    }
  //    return retValue;
  //  }
  
  
  
  
  private class AnnotationProperty extends PropertySupport.ReadWrite<String> {
    AnnotationProperty(String name, String displayName, String shortDescription){
      super(name, String.class, displayName, shortDescription);
    }
    public String getValue() throws IllegalAccessException,
       InvocationTargetException {
      //System.out.println("getValue");
      String retValue = "";
      String attr = (String) fileCopy.getAttribute(ANNOTATION);
      
      if (attr != null) {
        retValue = attr;
      }
      
      return retValue;
    }
    
    public void setValue(String value) throws IllegalAccessException,
       IllegalArgumentException, InvocationTargetException {
      //System.out.println("setValue");
      try {
        fileCopy.setAttribute(ANNOTATION, value);
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
      
    }
    //had to use a workaround to avoid showing "null" as annotation value 
    //in tree table view, when popup editor is called, as htmlDisplayValue is 
    //shown then, instead of the plain value as it should when htmlDisplayValue 
    //is null.
    @Override public Object getValue(String attr){
      Object retValue = super.getValue(attr);
      if (attr.equals("htmlDisplayValue"))
        try {
          String converted = (String)getValue();
          converted = converted.replaceAll("<","&lt;");
          converted = converted.replaceAll(">","&gt;");
          retValue =  "<html>"+converted+"</html>";
        } catch (InvocationTargetException ex) {
          ex.printStackTrace();
        } catch (IllegalAccessException ex) {
          ex.printStackTrace();
        }
      return retValue;
    }
    
  }
  
}
