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
 * Mark.java
 *
 * Created on March 5, 2003, 4:35 PM
 */
package org.netbeans.swing.scrollbars.spi;


/**Interface representing an immutable marked position
 * 
 * @version 1.0
 * @author  Tim Boudreau
 */
public interface Mark {
    /** Get the point in the model's range from 0 to <code>
     * getMaximumMarkLocation()</code> at which this mark starts.
     * @return The start index
     */    
    public int getStart ();
    /** Get the length of the mark, from its start
     * @return The length
     */    
    public int getLength ();
    /** Get the text represented by the mark.  MarkedScrollbarUI
     * uses this to produce the tooltip.
     * @return The text
     */    
    public String getText ();
    /** Get a hint about how the UI should render this mark.
     * Only the string &quot;color&quot; is supported by
     * MarkedScrollbarUI.  Subclasses could support additional
     * hints.
     * @param key String key for the hint
     * @return The object representing the hint, or null if not
     * recognized
     */    
    public Object get (String key);

    /**
     * Called when the mark has been clicked, after it has been scrolled into view. It is not necessary to do
     * anything here, but it can be used to, for example, select some text, etc.
     */
    public void select();

}
