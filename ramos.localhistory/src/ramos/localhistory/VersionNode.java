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

import java.lang.reflect.InvocationTargetException;
import org.openide.actions.DeleteAction;

import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

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
   extends FilterNode {
  private static final String ICON_PATH =
     "ramos/localhistory/resources/clock.png";
  private static final String ANNOTATION = "Annotation";
  private FileObject fileCopy;
  
  public VersionNode(final FileObject fo)
     throws DataObjectNotFoundException {
    super(DataObject.find(fo).getNodeDelegate());
    this.fileCopy = fo;
  }
  
  @Override
  public String getDisplayName() {
    return getName();
  }
  
  @Override
  public String getName() {
    return fileCopy.lastModified().toString();
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
    PropertySet[] retValue = new PropertySet[1];
    final ReadWrite prop =
       new AnnotationProperty(ANNOTATION, String.class, ANNOTATION, ANNOTATION);
    
    retValue[0] =
       new PropertySet() {
      public Node.Property[] getProperties() {
        return new Property[] { prop };
      }
    };
    
    return retValue;
  }
  
  public String getShortDescription() {
    return "an older version";
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
    File file = FileUtil.toFile(fileCopy);
    
    return new FileReader(file);
  }
  
  public void revert(final FileObject current) {
    try {
      FileObject source = fileCopy;
      FileObject target = current;
      FileLock lock = target.lock();
      InputStream is = source.getInputStream();
      OutputStream os = target.getOutputStream(lock);
      FileUtil.copy(is, os);
      is.close();
      os.close();
      lock.releaseLock();
      
      String annotation = (String) source.getAttribute(ANNOTATION);
      
      if (annotation == null) {
        annotation = "";
      }
      
      LocalHistoryRepository.getInstance()
         .makeLocalHistoryCopy(
         DataObject.find(target),
         "reverted to " + source.lastModified().toString() + " <" + annotation +
         ">");
    } catch (final FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }
  private class AnnotationProperty extends PropertySupport.ReadWrite{
    AnnotationProperty(String name,Class type,String displayName, 
       String shortDescription){
      super(name, type, displayName, shortDescription);
    }
    public Object getValue() throws IllegalAccessException, 
       InvocationTargetException {
      String retValue = "";
      String attr = (String) fileCopy.getAttribute(ANNOTATION);
      
      if (attr != null) {
        retValue = attr;
      }
      
      return retValue;
    }
    
    public void setValue(Object value) throws IllegalAccessException, 
       IllegalArgumentException, InvocationTargetException {
      try {
        fileCopy.setAttribute(ANNOTATION, value);
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
    }
    
  }
}
