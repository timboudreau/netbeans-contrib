/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * OutlineTableModel.java
 *
 * Created on January 27, 2004, 6:53 PM
 */

package org.netbeans.swing.outline;

import javax.swing.table.TableModel;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeModel;

/** A model for an Outline (&quot;tree-table&quot;).  Implements both
 * TreeModel and TableModel (the default implementation, DefaultOutlineModel,
 * wraps a supplied TreeModel and TableModel).  I
 *
 * @author  Tim Boudreau
 */
public interface OutlineModel extends TableModel, TreeModel {
    public TreePathSupport getTreePathSupport ();
    public AbstractLayoutCache getLayout ();
    public boolean isLargeModel();
}
