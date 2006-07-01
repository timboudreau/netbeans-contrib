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
