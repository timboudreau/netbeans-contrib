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

import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.ProxyLook;
import org.netbeans.spi.looks.Selectors;
import org.openide.util.NbBundle;

/**
 * This is base class for delegating to other looks or nodes. All methods
 * delegate to the delegate, unless they are masked.
 * <P>
 * All the methods contain  simple deleagation to the {@link org.netbeans.spi.looks.Look}
 * passed as parameter into the constructor, if the mask contains flag
 * for the method.<P>
 * Use the methods: {@link #setLookMask}, {@link #lookMask}, {@link #lookMask}, 
 * {@link #lookUnmask} for work with the mask.<BR> 
 * The default state is to delegate all methods (i.e. the mask is set to
 * {@link #ALL_METHODS}.)
 *
 * @author Petr Hrebejk
 */
public class FilterLook extends ProxyLook {
    /** Contains current mask of the node */
    private long mask;
    
    /** Contains the Look to delegate to. Array of one member */
    private Look delegate;
        
    /** Creates new filter and delegates all methods to the provided delegate
     * @param delegate The Look to delegate to.
     */
    public FilterLook ( String name, Look delegate) {
        this (name, delegate, ALL_METHODS);
    }
    
    /** Creates new filter look and sets its filtering
     * @param delegate look to delegate to
     * @param mask the mask to use (one of method contants)
     */
    public FilterLook ( String name, Look delegate, long mask) {
        super( name, Selectors.singleton( delegate ) );
        this.delegate = delegate;
        this.mask = mask;
    }

    /** Display name of the look. Composed from the name of the delegate.
     */
    public String getDisplayName () {
        return NbBundle.getMessage (FilterLook.class, "LAB_Filter", delegate.getDisplayName ());
    }
    
    /** A method that checks whether given method should be delegated to 
     * the look provided in constructor or not. 
     * <P>
     * The default implementation ignores the context and just checks 
     * the mask which can be modified by <link>lookMask</link>, <link>lookUnmask</link>
     * methods.
     * <P>
     * Subclasses might override 
     * this method with implementation that bases its decision on different criteria.
     *
     * @param method one of the constants defined here that identifies the method
     *    we want to delegate to
     * @param substitute the substitute that the method will be called with or 
     *    null if we are in notification of changes and we have no substitute
     * @return either the delegate look or null
     */
    protected final boolean delegateTo (long method, Look look, Object representedObject) {
        return ( mask & method ) != 0;
    }
    
    // Methods of look itself -------------------------------------------------
    
    /** Sets mask for this look. Only methods contained in the mask
     * will be delegated. Other methods will return neutral values.
     * @param mask The mask to be set.
     */
    protected final void setLookMask( long mask ) {
        this.mask = mask;
    }
    
    /** Returns the current mask for this filter look.
     * @return Current mask.
     */
    protected final long getLookMask( ) {
        return mask;
    }
    
    /** Masks given methods. Removes given methods from given mask. 
     * The masked methods will be no longer delegated.
     * @param methods Logical <CODE>OR</CODE> of methods which have to be masked.
     * @return Current mask.
     */
    protected final long lookMask( long methods ) {
        mask |= methods;
        return mask;
    }
     
    /** Unmasks given methods. Adds given methods from given mask. 
     * The masked methods will start to be delegated.
     * @param methods Logical <CODE>OR</CODE> of methods which have to be unmasked.
     * @return Current mask.
     */
    protected final long lookUnmask( long methods ) {
        mask &= ~methods; 
        return mask;
    }
    
        
}
