/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.loaders.clazz;

import com.netbeans.ide.*;
import com.netbeans.ide.filesystems.FileObject;
import com.netbeans.developer.impl.actions.*;
import com.netbeans.ide.util.actions.SystemAction;

/** The DataLoader for ClassDataObjects.
*
* @author Jan Jancura, Ian Formanek
* @version 0.26, Apr 15, 1998
*/
public class ClassDataLoader extends DataLoader {

  /** Creates a new ClassDataLoader */
  public ClassDataLoader () {
    super (ClassDataObject.class);
    setActions (new SystemAction [] {
        new CustomizeBeanAction (),
        null,
        new ExecuteAction (),
        null,
        new CutAction (),
        new CopyAction (),
        new PasteAction (),
        null,
        new DeleteAction (),
        null,
        new SaveAsTemplateAction(),
        null,
        new PropertiesAction (),
      }
    );
  }

  /** This method is used when you need to find a java DataObject for file object.
  *
  * @param fo file object to recognize
  * @param recognized recognized files buffer.
  *
  * @return suitable data object or <CODE>null</CODE> if the handler cannot
  *   recognize this object
  */
  public DataObject handleFindDataObject (FileObject fo, DataLoaderRecognized recognized)
  throws com.netbeans.ide.loaders.DataObjectExistsException {
    if (fo == null) return null;
    String ext;
    ClassDataObject ret = null;
    if ((ext = fo.getExt ()).equals ("ser")) {
      if (recognized != null) {
        recognized.markRecognized (fo);
//        FileObject classFile;
//        if ((classFile = findFile (fo, "class")) != null)
//          recognized.markRecognized (fo);
        ret = new BeanDataObject (fo);
      }
    } else if (ext.equals ("class")) {
      if (recognized != null) {
        recognized.markRecognized (fo);
//        FileObject serFile;
//        if ((serFile = findFile (fo, "ser")) != null) {
//          recognized.markRecognized (serFile);
//          ret = new BeanDataObject (serFile);
//        } else {
          ret = new ClassDataObject (fo);
//        }
      }
    } else return null;

    return ret;
  }

  /** finds file with the same name and specified extension in the same folder as param javaFile */
  static protected FileObject findFile(FileObject javaFile, String ext) {
    if (javaFile == null) return null;
    return javaFile.getParent().getFileObject (javaFile.getName(), ext);
  }
}

/*
 * Log
 *  2    Gandalf   1.1         1/6/99   Ian Formanek    Reflecting change in 
 *       datasystem package
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 * Beta Change History:
 *  0    Tuborg    0.20        --/--/98 Jan Formanek    SWITCHED TO NODES
 *  0    Tuborg    0.23        --/--/98 Jan Jancura     Bugxix
 *  0    Tuborg    0.24        --/--/98 Jan Formanek    reflecting changes in DataSystem
 *  0    Tuborg    0.25        --/--/98 Jan Jancura     Error data object removed.
 */
