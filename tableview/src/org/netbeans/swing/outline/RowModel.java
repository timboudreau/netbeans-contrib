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
 * RowModel.java
 *
 * Created on January 28, 2004, 11:07 PM
 */

package org.netbeans.swing.outline;

/** A model for the rows in an Outline.  This is passed the object in
 * row 0 of an Outline table (the tree column), and provides objects
 * for the other columns - essentially a model for the data in the
 * rows of an Outline.
 * <p>
 * Note that all column indexes passed to this interface are 0-based -
 * that is, column 0 is the first column <strong>after</strong> the
 * tree node column, so the object returned by <code>getValueFor(someObject, 0)</code>
 * is the object that should appear in column <strong>1</strong> of the
 * actual table.
 * <p>
 * 
 *
 * @author Tim Boudreau
 */
public interface RowModel {
    /** Get the column count.  Do not include the base (nodes) column
     * of the Outline, only the number of columns in addition to it
     * that should be displayed. */
    public int getColumnCount();
    /** Get the value at a given column.  
     * @param node The node in column 0 of the Outline
     * @param column The index of the column minus the nodes column  */
    public Object getValueFor (Object node, int column);
    /** Get the object class for the column.  Analogous to 
     * <code>TableModel.getColumnClass(int column)</code> */
    public Class getColumnClass (int column);
    /** Is the cell in this column editable? */
    public boolean isCellEditable (Object node, int column);
    /** Set the value of the object in this column */
    public void setValueFor (Object node, int column);
    /** Get the name of this column */
    public String getColumnName (int column);
}
