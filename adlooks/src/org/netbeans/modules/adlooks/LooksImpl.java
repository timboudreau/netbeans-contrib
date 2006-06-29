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

package org.netbeans.modules.adlooks;

import java.awt.datatransfer.Transferable;
import java.lang.reflect.Method;
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
import org.netbeans.modules.looks.Accessor;
import org.netbeans.modules.looks.LookEvent;
import org.netbeans.modules.looks.LookListener;
import org.netbeans.modules.looks.SelectorEvent;
import org.netbeans.modules.looks.SelectorListener;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Singletonizer;
import org.netbeans.spi.adaptable.SingletonizerEvent;
import org.netbeans.spi.adaptable.SingletonizerListener;

import org.openide.util.Lookup;

import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.LookSelector;
import org.openide.util.HelpCtx;
import org.openide.util.WeakListeners;

/** Implements the singletonizer by delegating to looks.
 *
 * @author Jaroslav Tulach
 */
public final class LooksImpl extends Object 
implements Singletonizer, SelectorListener, LookListener {
    /** selector to use */
    private LookSelector selector;
    /** listener to notify Singletonizer about changes */
    private SingletonizerListener listener;
    /** associated adaptor */
    private Adaptor adaptor;
    /** look listener weak, so it can be attached to anyone */
    private LookListener weakL;

    /** Singletonizer.Impl for looks */
    private LooksImpl (LookSelector selector) {
        this.selector = selector;
        Accessor.DEFAULT.addSelectorListener(selector, this);
        weakL = WeakListeners.create(LookListener.class, this, null);
    }
    
    /** Creates new AspectProvider */
    public static Adaptor create (LookSelector selector) {
        LooksImpl impl = new LooksImpl(selector);
        Adaptor a = Adaptors.singletonizer(ALL_CLASSES, impl);
        impl.adaptor = a;
        return a;
    }

    private Look findLook(Object obj) {
        Enumeration en = selector.getLooks(obj);
        if (!en.hasMoreElements()) {
            return null;
        }

        Look l = (Look)en.nextElement();
        Accessor.DEFAULT.addLookListener(l, obj, weakL);
        return l;
    }

    public Object invoke (Object obj, java.lang.reflect.Method method, Object[] args) throws Exception {
        Look l = findLook(obj);
        if (l == null) {
            return l;
        }

        int index = ALL.get(method.getDeclaringClass());
        Lookup ctx = AdaptableLookup.getLookup(adaptor, obj);
        switch(index) {
            case 0: { /*Identity.class*/
                String n = l.getName(obj, ctx);
                return n;
            }
            case 1: {
                /*Rename.class*/
                l.rename(obj, (String)args[0], ctx);
                break;
            }
            case 2: /*DisplayName.class*/
                return l.getDisplayName(obj, ctx);
            case 3: /*HtmlDisplayName.class*/
            case 4: /*ShortDescription.class*/
                return l.getShortDescription(obj, ctx);
            case 5:/*, Customizable.class*/
                return l.getCustomizer(obj, ctx);
            case 6: /*HelpCtx.Provider.class*/
                return l.getHelpCtx(obj, ctx);
            case 7: /* ActionProvider.class*/
                if (method.getName().equals("getActions")) { // NOI18N
                    return l.getActions(obj, ctx);
                } else {
                    return l.getDefaultAction(obj, ctx);
                }
            case 8: /* Copy.class, */
                return l.clipboardCopy(obj, ctx);
            case 9: /*Cut.class, */
                return l.clipboardCut(obj, ctx);
            case 10: /*SetOfProperties.class,*/
                return l.getPropertySets(obj, ctx);
            case 11: /* Drag.class*/
                return l.drag(obj, ctx);
            case 12: /* NewTypes.class*/
                return l.getNewTypes(obj, ctx);
            case 13: /*PasteTypes.class*/
                return l.getPasteTypes(obj, (Transferable)args[0], ctx);
            case 14: /*Drop.class*/
                return l.getDropType(obj, (Transferable)args[0], (Integer)args[1], (Integer)args[2], ctx);
            case 15: /*SubHierarchy.class,*/
                return l.getChildObjects(obj, ctx);
            case 16: /* Icon.class, */
                throw new IllegalStateException(index + " for " + method); // NOI18N
            case 17: /*Delete.class,*/
                l.destroy(obj, ctx);
                break;
            default:
                throw new IllegalStateException(index + " for " + method); // NOI18N
        }
        return null;
    }
    
    public boolean isEnabled(Object obj, Class c) {
        Look l = findLook(obj);
        if (l == null) {
            return false;
        }

        Lookup ctx = AdaptableLookup.getLookup(adaptor, obj);
        if (c == SubHierarchy.class) {
            return !l.isLeaf(obj, ctx);
        }
        if (c == Rename.class) {
            return l.canRename(obj, ctx);
        }
        if (c == Cut.class) {
            return l.canCut(obj, ctx);
        }
        if (c == Copy.class) {
            return l.canCopy(obj, ctx);
        }
        if (c == Delete.class) {
            return l.canDestroy(obj, ctx);
        }
        if (c == Customizable.class) {
            return l.hasCustomizer(obj, ctx);
        }

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

    public void contentsChanged(SelectorEvent event) {
        listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, null, ALL_CLASSES));
    }

    public void change(LookEvent evt) {
        long m = evt.getMask();

        if ((m & Look.GET_NAME) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), Identity.class));
        }
        if ((m & Look.GET_DISPLAY_NAME) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), DisplayName.class));
        }
        if ((m & Look.GET_SHORT_DESCRIPTION) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), ShortDescription.class));
        }
        if ((m & (Look.GET_ACTIONS | Look.GET_DEFAULT_ACTION)) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), ActionProvider.class));
        }
        if ((m & Look.GET_CHILD_OBJECTS) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), SubHierarchy.class));
        }
        if ((m & Look.CAN_RENAME) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), Rename.class));
        }
        if ((m & Look.CAN_DESTROY) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), Delete.class));
        }
        if ((m & (Look.CAN_CUT | Look.CLIPBOARD_CUT)) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), Cut.class));
        }
        if ((m & (Look.CAN_COPY | Look.CLIPBOARD_COPY)) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), Copy.class));
        }
        if ((m & Look.GET_PROPERTY_SETS) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), SetOfProperties.class));
        }
        if ((m & (Look.HAS_CUSTOMIZER | Look.GET_CUSTOMIZER)) != 0) {
            listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), Customizable.class));
        }
    }

    public void propertyChange(LookEvent evt) {
        listener.stateChanged(SingletonizerEvent.aValueOfObjectChanged(this, evt.getSource(), SetOfProperties.class));
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
