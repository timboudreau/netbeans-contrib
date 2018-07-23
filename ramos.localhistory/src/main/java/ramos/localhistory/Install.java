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
