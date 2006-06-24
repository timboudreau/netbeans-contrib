/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License.  A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is Ramón Ramos. The Initial Developer of the Original
 * Code is Ramón Ramos. All rights reserved.
 *
 * Copyright (c) 2006 Ramón Ramos
 */
package ramos.localhistory;

import org.openide.filesystems.Repository;

import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;

import org.openide.modules.ModuleInstall;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Install extends ModuleInstall {
   /**
    * DOCUMENT ME!
    */
   public void restored() {
      DataObject.getRegistry().addChangeListener(Listener.getInstance());
      DataLoaderPool.getDefault().addOperationListener(Listener.getInstance());

      //Repository.getDefault().getDefaultFileSystem().addFileChangeListener(Listener.getInstance());
   }

   /**
    * DOCUMENT ME!
    */
   public void uninstalled() {
      DataObject.getRegistry().removeChangeListener(Listener.getInstance());
      DataLoaderPool.getDefault().removeOperationListener(Listener.getInstance());

      //Repository.getDefault().getDefaultFileSystem().removeFileChangeListener(Listener.getInstance());
   }
}
