/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.looks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.TooManyListenersException;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.LookSelector;
import org.netbeans.spi.looks.LookProvider;
import org.netbeans.spi.looks.ChangeableLookProvider;
import org.netbeans.modules.looks.NamespaceLookProvider;
import org.openide.util.Enumerations;

/** Factory for creating all kinds of selectors.
 *
 * @author Petr Hrebejk
 */
public abstract class SelectorImplFactory  {
    
    
    /** It's a static factory */
    private SelectorImplFactory() {}
    
    public static SelectorImpl provider( LookProvider provider ) {
        return new Impl( provider );
    }
        
    public static SelectorImpl singleton( Look delegate ) {
        return new Impl( delegate );
    }
    
    public static SelectorImpl array( Look delegates[] ) {
        return new Impl( delegates );
    }
        
    public static SelectorImpl changeableProvider( ChangeableLookProvider provider ) {
        return new Impl( provider );
    }
    
    public static SelectorImpl namespaceProvider( NamespaceLookProvider provider, String prefix ) {
        return new Impl( provider, null, prefix );
    }
            
    public static SelectorImpl decorator( LookSelector selector, Look look, boolean asLast, boolean excludable ) {        
        SelectorImpl si = Accessor.DEFAULT.getSelectorImpl( selector );         
        return new Impl( (Impl)si, look, asLast ? Boolean.TRUE : Boolean.FALSE );
    }
    
    public static SelectorImpl first( LookSelector selector ) {
        SelectorImpl si = Accessor.DEFAULT.getSelectorImpl( selector );
        return new Impl( (Impl)si );
    }

    public static SelectorImpl namespaceTypes( String prefix ) {
        return new Impl( null, prefix, true );
    }
    
    public static SelectorImpl namespaceTypes( RegistryBridge registryBridge, String prefix ) {
        return new Impl( registryBridge, prefix, true );
    }
    
    public static SelectorImpl context( RegistryBridge registryBridge, String contextName ) {
        return new Impl( registryBridge, contextName, false );
    }
        
    public static SelectorImpl composite( LookSelector selectors[], boolean removeDuplicates ) {
        Impl[] impls = new Impl[selectors.length];
        for ( int i = 0; i < selectors.length; i++ ) {
            impls[i] = (Impl)Accessor.DEFAULT.getSelectorImpl( selectors[i] );            
        }
        return new Impl( impls, removeDuplicates );
    }
    
    // Innerclasses  -----------------------------------------------------------    

    static class Impl implements SelectorImpl, ChangeListener, SelectorListener {
        
        // Types 
        
        // Fixed impls
        private static final int PROVIDER = 0;          // (LookProvider)
        private static final int SINGLETON = 1;         // (Look)
        private static final int ARRAY = 2;             // (Look[])
        private static final int DECORATOR_FIXED = 3;   // (LookSelector-Fixed, Look)
        private static final int COMPOSITE_FIXED = 4;   // (Impl[], boolean)
        private static final int FIRST_FIXED = 5;       // (LookSelector-Fixed, Look)
        
        // Changeable impls
        private static final int CHANGEABLE_PROVIDER = 100;  // (ChangeableProvider)
        private static final int NAMESPACE_PROVIDER = 101;   // (NamespaceProvider, Context) 
        private static final int NAMESPACE_TYPES = 102;      // (Context) - in context by type
        private static final int DECORATOR_CHANGEABLE = 103; // (LookSelctor-Changeable), Look )
        private static final int CONTEXT = 104;              // (Context) - all in context
        private static final int COMPOSITE_CHANGEABLE = 105; // (Impl[], boolean)
        private static final int FIRST_CHANGEABLE = 106;     // (LookSelector-Changeable, Look)
        
        // --- Extra indexes into arrays for various types of Impl
        private static final int DECORATOR_Selector = 0;
        private static final int DECORATOR_Look = 1;
        private static final int DECORATOR_AsLast = 2;
        private static final int DECORATOR_Cache = 3;
        
        private static final int NAMESPACE_PROVIDER_Provider = 0;
        private static final int NAMESPACE_PROVIDER_Prefix = 1;
        
        private static final int NAMESPACE_TYPES_Prefix = 0;
        private static final int CONTEXT_Prefix = NAMESPACE_TYPES_Prefix;
        private static final int NAMESPACE_TYPES_Listener = 1;
        private static final int CONTEXT_Listener = NAMESPACE_TYPES_Listener;
        
        private static final int COMPOSITE_Delegates = 0;
        private static final int COMPOSITE_RemoveDuplicates = 1;
                 
        private int type;                           // Type of the impl        
        private EventListenerList listeners;        // Listeners for changeable impls.        
        private LookSelector selector;              // Selector which uses this impl
        private HashMap looksCache;                 // Cached results for keys 
        
        private Object delegate;                    // Used impl types which delegate                
        private RegistryBridge rb;                  // Used in context based impls.
        
        // Constructrors -------------------------------------------------------
                
        // General constructor for all impls
        
        Impl( int type, boolean cache ) {
            this.type = type;
            
            if ( cache ) {
                this.looksCache = new HashMap();                
            }
        }
        
        // One constructor for each type of impl
        
        public Impl( LookProvider provider ) {
            this( PROVIDER, false );
            this.delegate = provider;
        }
        
        public Impl( Look look ) {
            this( SINGLETON, false );
            this.delegate = look;            
        }
        
        public Impl( Look looks[] ) {
            this( ARRAY, false );
            this.delegate = new Look[looks.length];            
            System.arraycopy( looks, 0, (Look[])this.delegate, 0, looks.length );
        }
        
        public Impl( Impl selectorImpl, Look look, Boolean asLast ) {
            this( selectorImpl.isFixed() ? DECORATOR_FIXED : DECORATOR_CHANGEABLE, 
                  selectorImpl.isFixed() ? false : true );
            this.delegate = new Object[] { selectorImpl, look, asLast, new HashMap() };                        
        }
        
        public Impl( Impl selectorImpl ) {
            this( selectorImpl.isFixed() ? FIRST_FIXED : FIRST_CHANGEABLE,
                  selectorImpl.isFixed() ? false : true );
            this.delegate = selectorImpl;
        }

        public Impl( ChangeableLookProvider provider ) {
            this( CHANGEABLE_PROVIDER, true );
            this.delegate = provider;
        }
        
        public Impl( NamespaceLookProvider provider, RegistryBridge bridge, String prefix ) {
            this( NAMESPACE_PROVIDER, true );
            this.delegate = new Object[]{ provider, prefix };
            this.rb = bridge == null ? RegistryBridge.getDefault( null ) : bridge;
        }
                
        public Impl( RegistryBridge bridge, String prefix, boolean isTypes ) {
            this( isTypes ? NAMESPACE_TYPES : CONTEXT , true );
            this.delegate = new Object[] { prefix, null };
            this.rb = bridge == null ? RegistryBridge.getDefault( null ) : bridge;
        }
        
        public Impl( Impl delegates[], boolean removeDuplicates ) {
            this( allFixed( delegates ) ? COMPOSITE_FIXED : COMPOSITE_CHANGEABLE , !allFixed( delegates ) );
            this.delegate = new Object[] { delegates, removeDuplicates ? Boolean.TRUE : Boolean.FALSE };            
        }
        
        // Implementation of SelectorImpl interface ----------------------------
        
        public synchronized void setLookSelector(LookSelector selector) throws TooManyListenersException {
            if ( this.selector == null ) {
                this.selector = selector;
            }
            else {
                throw new TooManyListenersException();
            }
        }
        
        public Enumeration getLooks(Object representedObject) {
            Object key = getKey4Object( representedObject );
                    
            if ( key == null ) {                    // No key means no looks
                return Enumerations.empty();
            }            
            else if ( key == SelectorImpl.FIXED ) { // Means no caching
                return getLooks4Key( representedObject );         
            }
            else {                                  // Here we have to cache
                synchronized ( looksCache ) {
            
                    CacheItem ci = (CacheItem)looksCache.get( key );

                    if ( ci == null ) {
                        Enumeration e = getLooks4Key( key );                
                        ci = new CacheItem( e );
                        looksCache.put( key, ci );
                    }

                    return ci.getEnumeration();
                }
            }
        }
                
        public Object getKey4Object(Object representedObject) {
            switch( type ) {
                // Fixed impls
                case PROVIDER:
                case SINGLETON:
                case ARRAY:
                case COMPOSITE_FIXED:
                case DECORATOR_FIXED:
                case FIRST_FIXED:
                    return SelectorImpl.FIXED;
                    
                // Changeable impls    
                case CHANGEABLE_PROVIDER:
                    return ((ChangeableLookProvider)delegate).getKeyForObject( representedObject );
                case NAMESPACE_PROVIDER:
                    return ((NamespaceLookProvider)((Object[])delegate)[NAMESPACE_PROVIDER_Provider]).getKeyForObject( representedObject );
                case NAMESPACE_TYPES:
                    return representedObject.getClass();
                case CONTEXT:
                    return delegate;
                    
                // Extra cases
                    
                case DECORATOR_CHANGEABLE:
                    SelectorImpl si = (SelectorImpl)((Object[])delegate)[DECORATOR_Selector];
                    return si.getKey4Object( representedObject );
                case COMPOSITE_CHANGEABLE:
                    SelectorImpl sis[] = (SelectorImpl[])((Object[])delegate)[COMPOSITE_Delegates];                     
                    Object keys[] = new Object[ sis.length ];
                    for( int i = 0; i < sis.length; i++ ) {
                        Object key = sis[i].getKey4Object( representedObject );
                        keys[i] = key == SelectorImpl.FIXED ? representedObject : key;
                    }    
                    return keys;

                case FIRST_CHANGEABLE:
                    SelectorImpl si2 = (SelectorImpl)delegate;
                    return si2.getKey4Object( representedObject );

                default:
                    throw new IllegalStateException( "Unknown impl type " + type ); //NOI18N
                
            }
            
        }
                
        public Enumeration getLooks4Key(Object key) {
            switch( type ) {
                
                // Fixed impls - here key will be the represented object                
                case PROVIDER:
                    return ((LookProvider)delegate).getLooksForObject( key );
                case SINGLETON:
                    return Enumerations.singleton(delegate);
                case ARRAY:
                    return Enumerations.array((Look[]) delegate);
                
                    
                // Changeable impls
                case CHANGEABLE_PROVIDER:
                    return ((ChangeableLookProvider)delegate).getLooksForKey( key );
                case NAMESPACE_PROVIDER:
                    Enumeration names = ((NamespaceLookProvider)((Object[])delegate)[NAMESPACE_PROVIDER_Provider]).getNamesForKey( key );
                    return TypesSearch.findLooks ("", names, rb ); // NOI18N
                case NAMESPACE_TYPES:
                    names = TypesSearch.namesForClass( (Class)key );                    
                    return TypesSearch.findLooks ((String)((Object[])delegate)[NAMESPACE_TYPES_Prefix], names, rb ); 
                case CONTEXT:
                    String contextName = (String)((Object[])delegate)[CONTEXT_Prefix];
                    names = rb.getNames( contextName );
                    return TypesSearch.findLooks ( contextName + "/", names, rb ); // NOI18N
                    
                // Extra cases
                case DECORATOR_FIXED:
                case DECORATOR_CHANGEABLE:
                    SelectorImpl si = (SelectorImpl)((Object[])delegate)[DECORATOR_Selector];
                    Enumeration e = si.getLooks4Key( key );

                    return Enumerations.convert(e, new Enumerations.Processor() {
                        public Object process(Object object, Collection ignore) {
                            return decorateLook((Look) object);
                        }
                    });
                
                case COMPOSITE_FIXED:
                case COMPOSITE_CHANGEABLE:
                    SelectorImpl sis[] = (SelectorImpl[])((Object[])delegate)[COMPOSITE_Delegates];                     
                    boolean removeDups = ((Boolean)((Object[])delegate)[COMPOSITE_RemoveDuplicates]).booleanValue();                     
                    
                    Object keys[] = null;
                    if ( type == COMPOSITE_CHANGEABLE ) {
                        keys = (Object[])key; // Otherwise we have a rep. obj as a key;        
                    }
                    
                    //Object sk[][] = new Object[ sis.length ][2];
                    
                    List sk = new ArrayList();
                    for( int i = 0; i < sis.length; i++ ) {
                        if ( keys != null && keys[i] == null ) {
                            // if not fixed but key is null 
                            continue;
                        }
                        sk.add( new Object[] { sis[i], keys == null ? key : keys[i] } );
                    }

                    Enumeration selEnum = Enumerations.queue(Enumerations.array(sk.toArray()), new Enumerations.Processor() {
                        public Object process(Object object, Collection coll) {
                            if (object instanceof Object[]) {
                                Object sk[] = (Object[]) object;
                                Enumeration looksEnum = ((SelectorImpl) sk[0]).getLooks4Key(sk[1]);
                                coll.addAll(Collections.list(looksEnum));
                            }
                            return object;
                        }
                    });
                    
                    Enumeration resEnum = Enumerations.filter(selEnum, new Enumerations.Processor() {
                        public Object process(Object object, Collection coll) {
                            if (object instanceof Look) {
                                return object;
                            } else {
                                return null;
                            }
                        }
                    });
                    
                    return removeDups ? Enumerations.removeDuplicates(resEnum) : resEnum;

                case FIRST_FIXED:
                case FIRST_CHANGEABLE:
                    SelectorImpl si2 = (SelectorImpl)delegate;
                    Enumeration e2 = si2.getLooks4Key( key );

                    if (e2.hasMoreElements()) {
                        return Enumerations.singleton(e2.nextElement());
                    } else {
                        return Enumerations.empty();
                    }

                default:
                    throw new IllegalStateException( "Unknown impl type " + type ); //NOI18N
            }
        }
                
        public void addSelectorListener( SelectorListener listener) {
            if ( !isFixed() ) {
                if ( listeners == null) {
                    // Someone starts to listen in some cases we need to start 
                    // listening as well
                    
                    switch( type ) {
                        case CHANGEABLE_PROVIDER:
                            try {
                                ((ChangeableLookProvider)delegate).addChangeListener( this );   
                            }
                            catch ( TooManyListenersException e ) {
                                throw new IllegalStateException ( "Too many listeners on provider " + delegate );                                
                            }
                            break;
                        case NAMESPACE_PROVIDER:
                            try {
                                ((NamespaceLookProvider)((Object[])delegate)[NAMESPACE_PROVIDER_Provider]).addChangeListener( this );
                            }
                            catch ( TooManyListenersException e ) {
                                throw new IllegalStateException ( "Too many listeners on provider " + delegate );                                
                            }
                            break;
                        case DECORATOR_CHANGEABLE:
                            ((Impl)((Object[])delegate)[DECORATOR_Selector]).addSelectorListener( this );
                            break;
                        case NAMESPACE_TYPES:
                        case CONTEXT:
                            RbEventTranslator l = new RbEventTranslator();
                            rb.addListener( ((String)((Object[])delegate)[NAMESPACE_TYPES_Prefix]), l  );
                            ((Object[])delegate)[NAMESPACE_TYPES_Listener] = l;
                            break;                            
                        case COMPOSITE_CHANGEABLE:
                            // PENDING
                        default:
                            // Do nothing for other types
                    }
                    
                }
        
                synchronized ( Impl.class ) {
                    if (listeners == null) {
                        listeners = new EventListenerList ();
                    }            
                }
                listeners.add( SelectorListener.class, listener );
            }
        }
        
        public void removeSelectorListener( SelectorListener listener) {
            
            if ( !isFixed() ) {
                synchronized( Impl.class ) {
                    if (listeners != null) {
                        listeners.remove( SelectorListener.class, listener);
                    }
                }
        
                if ( listeners.getListenerCount() == 0 ) {
                    // Nobody listens so stop listeneing
                    switch( type ) {
                        case NAMESPACE_TYPES:
                        case CONTEXT:
                            rb.removeListener( (String)((Object[])delegate)[NAMESPACE_TYPES_Prefix],
                                               (RbEventTranslator)((Object[])delegate)[NAMESPACE_TYPES_Listener] );
                            break;
                        case DECORATOR_CHANGEABLE:
                            ((Impl)((Object[])delegate)[DECORATOR_Selector]).removeSelectorListener( this );
                            break;
                        case COMPOSITE_CHANGEABLE:
                            // PENDING
                        default:
                            // Do nothing for other types
                    }
                    // Delete the listeners list in order to recreate on new add
                    listeners = null;
                }
                               
            }
        }
        
        public HashMap getCache() {
            return looksCache;
        }
        
        
        // Implementation of change listener -----------------------------------
        
        public void stateChanged(javax.swing.event.ChangeEvent e) {
            fireChange( new SelectorEvent( selector ) );            
        }
        
        // Implementation of selector listener
        
        public void contentsChanged( SelectorEvent event ) {
            fireChange( new SelectorEvent( selector ) );
        }
        
        // Private methods -----------------------------------------------------
                
        
        private boolean isFixed() {
            return type < CHANGEABLE_PROVIDER;
        }
        
        private static boolean allFixed( Impl[] impls ) {
            for( int i = 0; i < impls.length; i++ ) {
                if ( !impls[i].isFixed() ) {
                    return false;
                }
            }
            return true;
        }
        
        protected void fireChange( SelectorEvent event ) {
            
            Object[] arr;
                        
            synchronized( this ) { // If something changed we need to reset thec cache
                if ( looksCache != null ) {
                    looksCache = new HashMap();
                }
            }
            
            synchronized( Impl.class ) {
               if (listeners == null) {
                   return;
               }
               arr = listeners.getListenerList();
            }

            if ( arr.length == 0 ) {
                return;
            }

            for (int i = arr.length - 1; i >= 0; i -= 2) {
                SelectorListener l = (SelectorListener)arr[i];
                l.contentsChanged( event );
            }            
        }
        
        
        /** Used in DECORATOR_FIXED and DECORATOR_CHANGEABLE
         */
        private Look decorateLook( Look original ) {
            
            HashMap decorationCache = (HashMap)((Object[])delegate)[DECORATOR_Cache];
            boolean asLast = ((Boolean)((Object[])delegate)[DECORATOR_AsLast]).booleanValue();
            Look decorator = (Look)((Object[])delegate)[DECORATOR_Look];
            
            synchronized ( decorationCache ) {
                WeakReference ref = (WeakReference) decorationCache.get( original );
                Look decoratedLook = ref == null ? null : (Look) ref.get();

                if ( decoratedLook == null ) {

                    // Compute the decorated look
                    decoratedLook = new CompositeLook(
                                            "Decorated[" + original.getName(),
                                             asLast ? new Look[] { original, decorator } :
                                                      new Look[] { decorator, original } );

                    /*
                    // Compute decorated selector if necessary
                    LookSelector decoratedSelector = LookNode.findLookSelector (original, null); 
                    if ( !excludable && decoratedSelector != null ) {
                        decoratedSelector = new DecoratorSelector ( decoratedSelector, look, asLast, false );
                    }

                    // Join
                    decoratedLook = Looks.childrenSelectorProvider( "Decorated[" + original.getName(),
                                                                 decoratedLook, decoratedSelector );
                    */
                    decorationCache.put( original, new WeakReference( decoratedLook ) );
                }
                return decoratedLook;
            }
        }
        
        
        private class RbEventTranslator extends RegistryBridge.Listener {
        
            public void selectorChanged() {
                Impl.this.fireChange( new SelectorEvent( selector ) );
            }
        
    }
                        
    }
    
    /** An item in the cache consist of list of already asked for Looks
     * and rest of the enumeration. This class is used in the SelectorEvent
     * class.
     */        
    static class CacheItem {

        private Enumeration enumeration;
        private List list;

        public CacheItem( Enumeration enumeration ) {
            this.enumeration = enumeration;
            list = new ArrayList();
        }

        /** Return enumeration which caches */
        public Enumeration getEnumeration() {
            return new CacheEnumeration( this );
        }

        public Object get( int index ) {
            if ( index >= list.size() ) {
                for( int i = list.size(); i <= index; i++ ) {

                    if ( !enumeration.hasMoreElements() ) {
                        return null;
                    }

                    list.add( enumeration.nextElement() );
                }
            }

            return list.get( index );
        }

        boolean has( int index ) {
            if ( index < list.size() ) {
                return true;
            }
            else if ( index == list.size() )  {
                return enumeration.hasMoreElements();                
            }
            else {
                return get( index + 1 ) != null;
            }
        }
        
        /** Returns looks in the cache 
         * @param all Should really all looks be returned (i.e. iterate through
         *            the enum or should we only return content of the list
         */
        Collection getCachedLooks( boolean all ) {
            List result = new ArrayList();
            if ( all ) {
                for( Enumeration e = getEnumeration(); e.hasMoreElements(); ) {
                    result.add( e.nextElement() );
                }
                
                
            }
            else {
                result.addAll( list );
            }
            
            return result;
        }
        
        
        /** Enumeration over the CacheItem. First iterates the List then the
         * enumaration and adds the items into the list
         */
        private static class CacheEnumeration implements Enumeration {

            private CacheItem cacheItem;
            int index;

            public CacheEnumeration( CacheItem cacheItem ) {
                this.cacheItem = cacheItem;
                index = -1;            
            }

            public boolean hasMoreElements() {
                return cacheItem.has( index + 1 );
            }

            public Object nextElement() {            
                return cacheItem.get( ++index );            
            }

        }

    }
    
    
}
