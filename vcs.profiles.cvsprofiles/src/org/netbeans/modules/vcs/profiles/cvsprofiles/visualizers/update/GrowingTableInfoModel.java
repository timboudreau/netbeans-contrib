/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update;

import org.openide.util.*;
//import org.netbeans.modules.cvsclient.commands.*;
import javax.swing.event.TableModelEvent;

import org.netbeans.modules.vcscore.util.Debug;
import java.util.*;
import org.netbeans.modules.vcscore.util.table.*;

/**
 *
 * @author  mkleint
 */
public class GrowingTableInfoModel extends TableInfoModel {
    private Debug E=new Debug("UpdateTableInfoModel", true); // NOI18N
    private Debug D=E;
      
      public GrowingTableInfoModel() {
          super();
      }
      
      public void addElement(Object object)
      {
          super.addElement(object);
          fireTableRowsInserted(list.size(), list.size());
      }      
      
      public void prependElement(Object object) {
          super.prependElement(object);
          fireTableRowsInserted(0,0);
      }
}

