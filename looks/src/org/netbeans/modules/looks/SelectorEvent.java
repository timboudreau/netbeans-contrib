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

import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.spi.looks.LookSelector;

/** Event fired from the LookSelector when it's content changes.
 *
 * @see org.netbeans.spi.looks.LookSelector
 * @see org.netbeans.spi.looks.SelectorListener
 * @author  Petr Hrebejk, Jaroslav Tulach
 */
public class SelectorEvent extends EventObject {

    private SelectorImpl impl;
    private HashMap oldCache;
    
    /** Creates a new instance of SelectorEvent 
     * @param source LookSelector whose content was changed
     */
    public SelectorEvent( LookSelector source ) {        
        super( source );
        impl = Accessor.DEFAULT.getSelectorImpl( source );
        oldCache = impl.getCache();
    }
    
    /** Determines whether the change in the content of the LookSelector 
     * affects nodes which are used for visualisation of given represented 
     * object.
     * <P>
     * The default implementation returns true for all objects. If extending
     * LookSelector the implementor is responsible for keeping semantics of 
     * looking for Looks/LookSelectors in sync with the semantics of decision 
     * whether objects are affected or node.
     * @param representedObject The represented object of node which may be
     *        affected by the change of LookSelector content.
     * @return <CODE>True</CODE> if node with given represented object is affected 
     *         <CODE>false</CODE> otherwise.
     */
    public boolean affectsObject( Object representedObject ) {
        return true;
    }
    
    public Collection getAddedLooks( Object representedObject ) {
        if ( oldCache == null ) {
            // No cache, no problem
            return Collections.EMPTY_SET;
        }
        
        Object key = impl.getKey4Object( representedObject );
        
        Set diff[] = getDiff4Key( key );
        if ( diff == null ) {
            return Collections.EMPTY_SET;
        }
        else {
            return diff[0];
        }
        
    }
    
    public Collection getRemovedLooks( Object representedObject ) {
        
        if ( oldCache == null ) {
            // No cache, no problem
            return Collections.EMPTY_SET;
        }
        
        Object key = impl.getKey4Object( representedObject );
        
        Set diff[] = getDiff4Key( key );
        if ( diff == null ) {
            return Collections.EMPTY_SET;
        }
        else {
            return diff[1];
        }
        
    }
    
    // Private methods ---------------------------------------------------------
    
    /** Computes diff for given key. When the diff is computed for the first
     * time it will put ito the old cache instad of the original CacheItem
     */
    private Set[] getDiff4Key( Object key ) {
        Object o = oldCache.get( key );
        
        if ( key instanceof Set[] ) {
            // It was already computed, just return
            return (Set[])o;
        }
        else {
            // We have to compute, And put it back into the cache
            
            Set[] diff = new Set[2];
            
            SelectorImplFactory.CacheItem oldItem = (SelectorImplFactory.CacheItem)oldCache.get( key ); 
            Collection oldLooks = oldItem.getCachedLooks( false );
            Collection newLooks = Collections.list( impl.getLooks4Key( key ) );
            
            // Newly added looks 
            diff[0] = new HashSet( newLooks );
            diff[0].removeAll( oldLooks );
            
            // Removed looks
            diff[1] = new HashSet( oldLooks );
            diff[1].removeAll( newLooks );
                        
            return diff;
        }
            
    }
    
    
}
