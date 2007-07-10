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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;
import org.netbeans.spi.looks.LookSelector;

/** Interface which serves as a delegate for the LookSelector
 * final class in the SPI. The only thing the LookSelector does
 * is to delegate to it's implementatation. The two innerclasses
 * of the interface provide the implementation for the two LookProvoders
 * (I.e. the fixed LookProvider and the ChangeableLookProvider which
 * may change it's content during runtime.
 *
 */
public interface SelectorImpl {
        
    public void setLookSelector( LookSelector selector ) throws TooManyListenersException;
    
    public Enumeration getLooks( Object representedObject );
          
    public void addSelectorListener( SelectorListener listener );
    
    public void removeSelectorListener( SelectorListener listener );
    
    // Package private members -------------------------------------------------
    // To be used only from the instances of
    
    static final Object FIXED = new String("org.netbeans.modules.looks.SelectorImpl.FIXED"); //NOI18N

    Object getKey4Object( Object representedObject );
    
    Enumeration getLooks4Key( Object key );
    
    HashMap getCache();
    
}
