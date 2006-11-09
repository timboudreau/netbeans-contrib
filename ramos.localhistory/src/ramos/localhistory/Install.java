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

import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;

import org.openide.modules.ModuleInstall;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Install
  extends ModuleInstall {
  /**
   * DOCUMENT ME!
   */
  public void restored() {
    DataObject.getRegistry()
              .addChangeListener(LocalHistoryRepository.getInstance());
    DataLoaderPool.getDefault()
                  .addOperationListener(LocalHistoryRepository.getInstance());
  }

  /**
   * DOCUMENT ME!
   */
  public void uninstalled() {
    DataObject.getRegistry()
              .removeChangeListener(LocalHistoryRepository.getInstance());
    DataLoaderPool.getDefault()
                  .removeOperationListener(LocalHistoryRepository.getInstance());
  }
}
