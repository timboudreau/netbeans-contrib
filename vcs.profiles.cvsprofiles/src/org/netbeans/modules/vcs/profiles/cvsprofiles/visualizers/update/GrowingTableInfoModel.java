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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update;

import org.openide.util.*;
//import org.netbeans.modules.cvsclient.commands.*;
import javax.swing.event.TableModelEvent;

import java.util.*;
import org.netbeans.modules.vcscore.util.table.*;

/**
 *
 * @author  mkleint
 */
public class GrowingTableInfoModel extends TableInfoModel {

      public GrowingTableInfoModel() {
          super();
      }

    public GrowingTableInfoModel(int estimatedSize) {
        super(estimatedSize);
    }


      public void addElement(Object object)
      {
          super.addElement(object);
          fireTableRowsInserted(getRowCount(), getRowCount());
      }      
      
      public void prependElement(Object object) {
          super.prependElement(object);
          fireTableRowsInserted(0,0);
      }
}

