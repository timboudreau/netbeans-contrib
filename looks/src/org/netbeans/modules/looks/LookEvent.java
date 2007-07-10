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

package org.netbeans.modules.looks;

import java.util.EventObject;

/** Event fired from the Look to views.
 *
 * @see org.netbeans.spi.looks.LookSelector
 * @see org.netbeans.spi.looks.SelectorListener
 * @author  Petr Hrebejk, Jaroslav Tulach
 */
public final class LookEvent extends EventObject {

    private long mask;
    private String propertyName;


    /** Creates a new instance of SelectorEvent
     * @param source LookSelector whose content was changed
     */
    private LookEvent(Object source, long mask, String propertyName ) {
        super( source );
        this.mask = mask;
        this.propertyName = propertyName;
    } 
      
    
    public LookEvent(Object source, long mask ) {
        this( source, mask, null ); 
    }
    
    public LookEvent(Object source, String propertyName ) {
        this( source, 0, propertyName );
    }
    
    public String getPropertyName() {
        return propertyName;
    }
    
    public long getMask() {
        return mask;
    }
        
}
