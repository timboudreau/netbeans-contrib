/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


/*
 * MarkingModel.java
 *
 * Created on March 5, 2003, 4:41 PM
 */

package org.netbeans.swing.scrollbars.spi;

import javax.swing.event.ChangeListener;
import java.util.Enumeration;

/** Model for a list of marks.
 *
 * @author  Tim Boudreau
 * @version 1.0
 */
public interface MarkingModel {
    /** Find out how many marks this model contains.
     * @return The count
     */    
    public int size();
    /** Get a mark at a specific index
     * @param i The index
     * @return The Mark
     */    
    public Mark getMark (int i);
    /** Get an enumeration of all marks.
     * @return The enumeration
     */    
    public Enumeration getMarks();
    /** Get the highest number a Mark could return for its location.
     * @return The extent
     */    
    public int getMaxMarkLocation ();
    
    public void addChangeListener (ChangeListener ch);
    
    public void removeChangeListener(ChangeListener ch);
}
