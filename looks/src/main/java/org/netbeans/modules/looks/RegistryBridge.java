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

//import javax.naming.*;                           // UNCOMENT IF USING JNDI
//import org.netbeans.api.naming.NamingSupport;    // UNCOMENT IF USING JNDI

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.registry.AttributeEvent;
import org.netbeans.api.registry.BindingEvent;
import org.netbeans.api.registry.ContextListener;
import org.netbeans.api.registry.SubcontextEvent;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.Enumerations;

/** Class for resolving names of looks. Allowes for switching the registry
 * implementation. e.g. switching from JNDI to registry API.
 *
 * @author Petr Hrebejk
 */
public abstract class RegistryBridge  {
        
    public static RegistryBridge getDefault( FileObject fo ) {
        // return JNDI.getBridge( fo ); // UNCOMENT IF USING JNDI
        return RAPI.getBridge( fo ); // UNCOMENT IF USING RAPI        
    }
    

    /** For testing purposes only
     */
    public static void setDefault( FileObject fo ) {
        //JNDI.setBridge( fo );  // UNCOMENT IF USING JNDI
        RAPI.setBridge( fo );    // UNCOMENT IF USING RegistryAPI
    }
    
    
    public abstract Object resolve( String name );
    
    public abstract Enumeration getObjects( String name, Class type );
    
    public abstract Enumeration getNames( String name );
        
    public abstract void addListener( String name, Listener listener );
            
    public abstract void removeListener( String name, Listener listener );
            
       
    /** Class for use with RegistryAPI. Notice that there are lot of ugly
     *  hacks to get it work with current RegistryAPI.
     */        
    public static class RAPI extends RegistryBridge {
        
        private static final Map cache = new HashMap( );
        
        private org.netbeans.api.registry.Context rootCtx;
        
        private static RegistryBridge defaultBridge; // For testing only
        
        private RAPI( org.netbeans.api.registry.Context rootCtx ) {
            this.rootCtx = rootCtx;                       
        }
        
        /** Parameter fo is ignored can only be called from 
         * methodvalues due to unfinished registry API 
         */
        public static RegistryBridge getBridge( FileObject fo ) {
            
            org.netbeans.api.registry.Context ctx = null;
            
            if ( fo == null ) {
                
                if ( defaultBridge != null ) {
                    return defaultBridge;
                }
                
                // Context over system filesystem;
                ctx = org.netbeans.api.registry.Context.getDefault();
            }
            else {
                // deafult context
                if ( defaultBridge != null ) {
                    return defaultBridge;
                }
            }
            
            
            RegistryBridge rb = (RegistryBridge)cache.get( ctx );
            if ( rb == null ) {
                rb = new RAPI( ctx );
                cache.put( ctx, rb );
            }
            return rb;
        }
        
        /** Creates new registry for given file object. For testing purposes only.
         */
        public static void setBridge( FileObject fo ) {
            org.netbeans.api.registry.Context ctx = SpiUtils.createContext( 
                org.netbeans.api.registry.fs.FileSystemContextFactory.createContext( fo ) );
            
            defaultBridge = new RAPI( ctx );
            // cache.put( ctx, rb );
        }
        
        public Object resolve( String name ) {
            
            int lastSlashIndex = name.lastIndexOf( '/' ); // NOI18N
            
            org.netbeans.api.registry.Context ctx =  lastSlashIndex == -1 ?
                                                     rootCtx :   
                                                     rootCtx.getSubcontext( name.substring( 0, lastSlashIndex  ) );
                                                     
            if ( ctx == null ) {
                return null;
            }
                                                     
            return ctx.getObject( name.substring( lastSlashIndex + 1), null );            
        }
        
        public Enumeration getNames( String name ) {            
            org.netbeans.api.registry.Context ctx =  rootCtx.getSubcontext( name );                        
            return Collections.enumeration( ctx.getOrderedNames() );
        }
        
        public Enumeration getObjects( String name, final Class type ) {
                                
            final org.netbeans.api.registry.Context subContext = rootCtx.getSubcontext( name );
            
            if ( subContext == null ) { 
                // OK there is no such subcontext maybe there could be the object
                Object o = resolve( name );
                if ( o != null && ( type == null || type.isInstance( o ) ) ) {
                    return Enumerations.singleton(o);
                }                                
                // Not even an object
                return Enumerations.empty();
            }
            
            Enumeration en = Collections.enumeration( subContext.getOrderedNames() );

            return Enumerations.convert(en, new Enumerations.Processor() {
                public Object process(Object object, Collection ignore) {
                    String bindingName = (String)object;
                    
                    if ( bindingName.endsWith( "/" ) ) { //NOI18N
                        return null; // Don't return contexts
                    }
                    
                    Object l = subContext.getObject(bindingName, null);

                    if ( type == null ) {
                        return l;
                    }
                    else {
                        return type.isInstance( l ) ? l : null;
                    }
                }
            });
        }
        
        public void addListener( String name, Listener listener ) {
            
            final org.netbeans.api.registry.Context subContext = rootCtx.getSubcontext( name );
            
            if ( subContext == null ) {
                throw new IllegalArgumentException( "Context " + name + " does not exist" ); //NOI18N
            }
            
            listener.setContext( subContext );
            subContext.addContextListener( listener );
            
        }
        
        public void removeListener( String name, Listener listener ) {
            
            if ( listener.context == null ) {
                throw new IllegalArgumentException( "Context " + name + " does not exist" ); //NOI18N
            }
            
            listener.context.removeContextListener( listener );
            
        }
               
        /** Finds a root context for given context 
         */
        private static org.netbeans.api.registry.Context getRootContext( org.netbeans.api.registry.Context ctx ) {
            while( ctx.getParentContext() != null ) {
                ctx = ctx.getParentContext();
            }            
            return ctx;
        }
       
    }
    
    
    public static abstract class Listener implements ContextListener {
    
        private org.netbeans.api.registry.Context context;
        
        
        public void setContext( org.netbeans.api.registry.Context context ) {
            this.context = context;
        }
        
        public abstract void selectorChanged();
        
        public void attributeChanged( AttributeEvent evt ) {
            selectorChanged();
        }
        
        public void bindingChanged( BindingEvent evt ) {
            selectorChanged();
        }
        
        public void subcontextChanged( SubcontextEvent evt ) {
            selectorChanged();
        }
        
    }
    
    
    /** Class for use with JNDI
     */
    /* 
    public static class JNDI extends RegistryBridge {
        
        javax.naming.Context rootCtx;
        
        public JNDI( FileObject rootFo ) {
            try {                                                
                if ( rootFo == null ) {
                    rootCtx = NamingSupport.createSFSInitialContext( null );
                }
                else {
                    Hashtable env = new Hashtable();
                    env.put("rootObject", rootFo); //NOI18N                            
                    rootCtx = NamingSupport.createSFSInitialContext( env );
                }
            }
            catch( NamingException e ) {
                throw new IllegalArgumentException( "Can't create inital context for " + rootFo ); //NOI18N
            }
        }
        
        public Object resolve( String name ) {
            try {
                return rootCtx.lookup( name );
            }
            catch ( NameNotFoundException e ) {
                return null;
            }
            catch ( NamingException e ) {
                throw new IllegalArgumentException( "Can't find " + name + " in " + rootCtx );
            }
        }
        
        public Enumeration getObjects( String name, final Class type ) {
            try {
                    
                NamingEnumeration en = rootCtx.listBindings( name );

                return new AlterEnumeration( en ) {
                    public Object alter( Object object ) {
                        Binding b = (Binding)object;
                        Object l = b.getObject();
                        
                        if ( type == null ) {
                            return l;
                        }
                        else {
                            return type.isInstance( l ) ? l : null;
                        }
                    }
                };
            }    
            catch (NamingException ev) {
                ErrorManager.getDefault ().notify (ev);
                return EmptyEnumeration.EMPTY;
            }
        }        
    }
    */
        
    
}
