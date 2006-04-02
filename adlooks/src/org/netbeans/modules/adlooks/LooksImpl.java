/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.adlooks;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;
import javax.swing.Icon;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.api.adaptable.Facets.*;
import org.netbeans.api.adlookup.AdaptableLookup;
import org.netbeans.api.adnode.NodeFacets.Customizable;
import org.netbeans.api.adnode.NodeFacets.Drop;
import org.netbeans.api.adnode.NodeFacets.NewTypes;
import org.netbeans.api.adnode.NodeFacets.PasteTypes;
import org.netbeans.api.adnode.NodeFacets.SetOfProperties;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.SingletonizerListener;

import org.openide.util.Lookup;

import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.LookSelector;
import org.openide.util.HelpCtx;

/** Implements the singletonizer by delegating to looks.
 *
 * @author Jaroslav Tulach
 */
public final class LooksImpl extends Object 
implements org.netbeans.spi.adaptable.Singletonizer {
    /** selector to use */
    private LookSelector selector;
    /** listener to notify Singletonizer about changes */
    private SingletonizerListener listener;
    /** associated adaptor */
    private Adaptor adaptor;
    
    /** Singletonizer.Impl for looks */
    private LooksImpl (LookSelector selector) {
        this.selector = selector;
    }
    
    /** Creates new AspectProvider */
    public static Adaptor create (LookSelector selector) {
        LooksImpl impl = new LooksImpl(selector);
        Adaptor a = Adaptors.singletonizer(ALL_CLASSES, impl);
        impl.adaptor = a;
        return a;
    }

    private Look getLook(Object obj) {
        Enumeration en = selector.getLooks(obj);
        return (Look)en.nextElement();
    }
    
    public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) throws Exception {
        int index = ALL.get(method.getDeclaringClass());
        Lookup ctx = AdaptableLookup.getLookup(adaptor, obj);
        switch(index) {
            case 0: /*Identity.class*/
                return getLook(obj).getName(obj, ctx);
            case 1: /*Rename.class*/
                getLook(obj).rename(obj, (String)args[0], ctx);
                break;
            case 2: /*DisplayName.class*/
                return getLook(obj).getDisplayName(obj, ctx);
            case 3: /*HtmlDisplayName.class*/
            case 4: /*ShortDescription.class*/
            case 5:/*, Customizable.class, HelpCtx.Provider.class,
        ActionProvider.class, Copy.class, Cut.class, SetOfProperties.class,
        Drag.class, NewTypes.class, PasteTypes.class, Drop.class, SubHierarchy.class,
        Icon.class, Delete.class,
*/
            default:
               throw new IllegalStateException(index + " for " + method); // NOI18N
        }
        return null;
    }
    
    public boolean isEnabled(Object obj, Class c) {
        return ALL.get(c) != null;
    }

    public void addSingletonizerListener(SingletonizerListener listener) throws TooManyListenersException {
        if (this.listener != null) throw new TooManyListenersException ();
        this.listener = listener;
    }

    public void removeSingletonizerListener(SingletonizerListener listener) {
        if (this.listener != listener) return;
        this.listener = null;
    }

    private static final Class[] ALL_CLASSES = {
        Identity.class, Rename.class, DisplayName.class, HtmlDisplayName.class,
        ShortDescription.class, Customizable.class, HelpCtx.Provider.class,
        ActionProvider.class, Copy.class, Cut.class, SetOfProperties.class,
        Drag.class, NewTypes.class, PasteTypes.class, Drop.class, SubHierarchy.class,
        Icon.class, Delete.class,
    };

    private static final Map<Class,Integer> ALL = new HashMap<Class,Integer>();
    static {
        for (int i = 0; i < ALL_CLASSES.length; i++) {
            ALL.put(ALL_CLASSES[i], i);
        }
    }
}
