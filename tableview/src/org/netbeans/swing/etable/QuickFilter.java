/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
 */
package org.netbeans.swing.etable;

/**
 * Interface for displaying only rows that have particular value
 * in given column.
 * @author David Strupl
 */
public interface QuickFilter {
    
    /**
     * If the object is accepted its row is displayed by the table.
     */
    public boolean accept(Object aValue);
}
