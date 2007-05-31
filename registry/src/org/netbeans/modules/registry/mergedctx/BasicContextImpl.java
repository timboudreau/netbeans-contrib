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

package org.netbeans.modules.registry.mergedctx;

import org.netbeans.api.registry.*;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.ResettableContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.util.NbBundle;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


class BasicContextImpl implements ResettableContext {
    private /*final */MergedDelegates contextDelegates;

    final private EventListenerList listeners = new EventListenerList();
    final private EventDispatcher dispatcher = new EventDispatcher();

    protected BasicContextImpl() {}

    private BasicContextImpl(final MergedDelegates ctxDelegates) {
        contextDelegates = ctxDelegates;
    }


    public final boolean hasDefault(final String bindingName) {
        final BasicContext[] notMaskedDelegates = getAllDelegates();
        return (notMaskedDelegates == null) ? true : getContextDelegates().hasDefault(bindingName, notMaskedDelegates);
    }

    public final boolean isModified(final String bindingName) {
        boolean isOnActive = false;
        final boolean hasDefault = hasDefault(bindingName);

        if (hasDefault) {
            try {
                if (getContextDelegates().getActiveDelegate(false) != null) {
                    isOnActive = (bindingName == null) ? true : getContextDelegates().getActiveDelegate(false).lookupObject(bindingName) != null;
                    MaskUtils.deleteMaskForBinding(getContextDelegates().getActiveDelegate(false), bindingName);
                }
            } catch (ContextException e) {
                isOnActive = false;
            }
        }

        return ((hasDefault && isOnActive) || !hasDefault);
    }

    public final void revert(final String bindingName) throws ContextException {
        if (isModified(bindingName)) {
            final boolean hasDefault = hasDefault(bindingName);

            /*if hasDefault == true, then also masks are deleted*/
            final BasicContext activeOrMerged = (!hasDefault) ? this : getContextDelegates().getActiveDelegate(false);

            if (bindingName != null && activeOrMerged != null) {
                activeOrMerged.bindObject(bindingName, null);
                if (hasDefault) MaskUtils.deleteMaskForBinding(activeOrMerged, bindingName);
            }

            if (bindingName == null && activeOrMerged != null) {
                destroyContent(activeOrMerged);
            }
        }
    }

    public final String getContextName() {
        return getAbsolutePath().getName();
    }

    public final BasicContext getSubcontext(final String subcontextName) {
        BasicContextImpl retVal;
        final Cache cache = getRootContextImpl().getCache();

        synchronized (cache.getContextSync()) {
            final Resource absolutePath1 = getAbsolutePath().getChild(subcontextName);
            retVal = cache.getContext(absolutePath1);

            if (retVal == null) {
                final MergedDelegates delegs = getContextDelegates().createChild(subcontextName);
                if (delegs != null) delegs.init();
                retVal = (delegs != null) ? new BasicContextImpl(delegs) : null;
                if (retVal != null) cache.cacheContext(retVal);
            }
        }
        return retVal;
    }

    public final BasicContext getParentContext() {
        BasicContextImpl retVal = null;
        final Cache cache = getRootContextImpl().getCache();

        if (!getAbsolutePath().isRoot()) {
            final Resource parent = getAbsolutePath().getParent();
            if (parent == null || !parent.isRoot()) {
                synchronized (cache.getContextSync()) {
                    final Resource absolutePath1 = getAbsolutePath().getParent();
                    retVal = cache.getContext(absolutePath1);

                    if (retVal == null) {
                        final MergedDelegates delegs = getContextDelegates().createParent();
                        if (delegs != null) delegs.init();
                        retVal = (delegs != null) ? new BasicContextImpl(delegs) : null;
                        if (retVal != null) cache.cacheContext(retVal);
                    }
                }
            } else {
                retVal = getRootContextImpl();
            }
        }

        return retVal;
    }

    public final BasicContext createSubcontext(final String subcontextName) throws ContextException {
        validityTest();
        BasicContext retVal = getSubcontext(subcontextName);

        if (retVal != null) {
            /*subcontext with this name already exist*/
            String msg = NbBundle.getMessage(BasicContextImpl.class,
                    "Subcontext_Exists_Exception", subcontextName, getAbsolutePath().getPath());//NOI18N
            throw SpiUtils.createContextException(this, msg);
        }

        getContextDelegates().createSubcontext(subcontextName);
        retVal = getSubcontext(subcontextName);

        if (retVal == null) {
            /*subcontext was not created*/
            String msg = NbBundle.getMessage(BasicContextImpl.class,
                    "Subcontext_Not_Created_Exception", subcontextName, getAbsolutePath().getPath());//NOI18N
            throw SpiUtils.createContextException(this, msg);
        }

        return retVal;
    }

    public final void destroySubcontext(final String subcontextName) throws ContextException {
        validityTest();
        final BasicContextImpl deletedSubctx = (BasicContextImpl) getSubcontext(subcontextName);

        if (deletedSubctx == null) {
            /*context with this name does not exist*/
            String msg = NbBundle.getMessage(BasicContextImpl.class,
                    "Subcontext_Not_Exist_Exception", subcontextName, getAbsolutePath().getPath());//NOI18N
            throw SpiUtils.createContextException(this, msg);
        }

        final Collection subOrdered = getRootContextImpl().getCache().existingSubcontexts(deletedSubctx);
        for (Iterator iterator = subOrdered.iterator(); iterator.hasNext();) {
            final BasicContextImpl subCtx = (BasicContextImpl) iterator.next();
            final BasicContext activeDelegate = subCtx.getContextDelegates().getActiveDelegate(true);

            MergedDelegates.destroyActiveDelegate(activeDelegate);
        }
    }

    public final Collection/*<String>*/ getSubcontextNames() {
        return getContextDelegates().getSubcontextNames();
    }

    public final Collection/*<String>*/ getBindingNames() {
        return getContextDelegates().getBindingNames();
    }

    public final Collection/*<String>*/ getAttributeNames(final String bindingName) {
        return getContextDelegates().getAttributeNames(bindingName);
    }

    public final Object lookupObject(final String bindingName) throws ContextException {
        final Object retVal = getContextDelegates().lookupObject(bindingName);
        getRootContextImpl().getCache().cacheObjectRef(getRootContextImpl(), this, bindingName, retVal);

        return retVal;
    }

    public final void bindObject(final String bindingName, final Object object) throws ContextException {
        getContextDelegates().bindObject(bindingName, object);
        if (object == null) {
            getRootContextImpl().getCache().removeObjectRef(object);
        } else {
            getRootContextImpl().getCache().cacheObjectRef(getRootContextImpl(), this, bindingName, object);
        }
    }

    public final String getAttribute(final String bindingName, final String attributeName) throws ContextException {
        return getContextDelegates().getAttribute(bindingName, attributeName);
    }

    public final void setAttribute(final String bindingName, final String attributeName, final String value) throws ContextException {
        final Object binding = (bindingName != null) ? lookupObject(bindingName) : null;
        if (binding == null && bindingName != null) {
            String msg = NbBundle.getMessage(BasicContextImpl.class,
                    "Binding_Not_Exist_Exception", attributeName, bindingName, getAbsolutePath().getPath());//NOI18N
            throw SpiUtils.createContextException(this, msg);
        }

        final String originalValue = getAttribute(bindingName, attributeName);
        if (originalValue == value) return;

        getContextDelegates().setAttribute(bindingName, binding, attributeName, value);
    }

    public final void addContextListener(final ContextListener listener) {
        synchronized (listeners) {
            listeners.add(ContextListener.class, listener);
        }
        getCopyOfCtxListeners();

    }

    public final void removeContextListener(final ContextListener listener) {
        synchronized (listeners) {
            listeners.remove(ContextListener.class, listener);
        }
    }

    private List getCopyOfCtxListeners() {
        final ArrayList ctxListeners = new ArrayList();
        synchronized (listeners) {
            if (listeners.getListenerCount() > 0) {
                final Object[] l = listeners.getListenerList();
                for (int i = l.length - 2; i >= 0; i -= 2) {
                    ctxListeners.add(l[i + 1]);
                }
            }
        }
        return ctxListeners;
    }

    final Resource getAbsolutePath() {
        return getContextDelegates().getAbsolutePath();
    }


    protected final MergedDelegates getContextDelegates() {
        return contextDelegates;
    }

    void setContextDelegates(final MergedDelegates contextDelegates) {
        if (contextDelegates != null && this.contextDelegates != contextDelegates) {
            if (this.contextDelegates != null) {
                this.contextDelegates.setDelegates(getDispatcher(), contextDelegates);
            } else {
                this.contextDelegates = contextDelegates;
            }
        }
    }

    final boolean isInvalid() {
        return (getRootContextImpl().getCache().getContext(getAbsolutePath()) == null);
    }

    public BasicContext getRootContext() {
        return getRootContextImpl();
    }
    
    private RootContextImpl getRootContextImpl() {
        return getContextDelegates().getRootContext();
    }

    private void validityTest() throws ContextException {
        if (isInvalid()) {
            String msg = NbBundle.getMessage(BasicContextImpl.class,
                    "Invalid_Context_Exception", getAbsolutePath().getPath());//NOI18N
            throw SpiUtils.createContextException(this, msg);
        }
    }

    static private void destroyContent(final BasicContext activeOrMerged) throws ContextException {
        /*delete all subcontexts*/
        final Collection sNames = activeOrMerged.getSubcontextNames();
        for (Iterator iterator1 = sNames.iterator(); iterator1.hasNext();) {
            final String sName = (String) iterator1.next();
            activeOrMerged.destroySubcontext(sName);
        }

        /*delete all bindings*/
        final Collection bNames = activeOrMerged.getBindingNames();
        for (Iterator iterator = bNames.iterator(); iterator.hasNext();) {
            final String bName = (String) iterator.next();
            final Collection aNames = activeOrMerged.getAttributeNames(bName);

            /*delete all attributes*/
            for (Iterator iterator2 = aNames.iterator(); iterator2.hasNext();) {
                final String aName = (String) iterator2.next();
                activeOrMerged.setAttribute(bName, aName, null);
            }
            activeOrMerged.bindObject(bName, null);
        }
    }

    /**
     * MergedDelegates normally returns array of delegates considering masks. But for revert
     * purposes, there is neceassry not to consider masking.
     */
    private BasicContext[] getAllDelegates() {
        final BasicContext[] notMaskedDelegates;
        final BasicContextImpl parentContext = ((BasicContextImpl) getParentContext());

        if (parentContext != null) {
            notMaskedDelegates = parentContext.getContextDelegates().getSubcontexts(getContextName(), false);
        } else
            notMaskedDelegates = getRootContextImpl().getContextDelegates().getDelegates();
        return notMaskedDelegates;
    }

    final EventDispatcher getDispatcher() {
        return dispatcher;
    }

    final class EventDispatcher {
        //BasicContextImpl getMe () {return BasicContextImpl.this;}
        public final String toString() {
            return (Integer.toString(System.identityHashCode(this)) + " | " + Integer.toString(System.identityHashCode(BasicContextImpl.this)));
        }

        final void fireSubcontextEvent(final String name, final int eventType) {
            Resource res = getAbsolutePath();
            SubcontextEvent se = SpiUtils.createSubcontextEvent(BasicContextImpl.this, name, eventType);
            se = (se.getSource() == this) ? se :
                    SpiUtils.createSubcontextEvent(BasicContextImpl.this, se.getSubcontextName(), se.getType());

            final List thisListeners = getCopyOfCtxListeners();
            for (int i = 0; i < thisListeners.size(); i++) {
                final ContextListener ctxListener = (ContextListener) thisListeners.get(i);
                ctxListener.subcontextChanged(se);
            }

            // iterate to root context
            while (!res.isRoot()) {
                res = res.getParent();
                final BasicContextImpl parent = getRootContextImpl().getCache().getContext(res);

                if (parent != null) {
                    final List parentListeners = parent.getCopyOfCtxListeners();
                    for (int i = 0; i < parentListeners.size(); i++) {
                        final ContextListener ctxListener = (ContextListener) parentListeners.get(i);
                        ctxListener.subcontextChanged(se);
                    }
                }
            }
        }

        final void fireAttributeEvent(final String bindingName, final String name, final int eventType) {
            AttributeEvent ae = SpiUtils.createAttributeEvent(BasicContextImpl.this, bindingName, name, eventType);

            Resource res = getAbsolutePath();
            ae = (ae.getSource() == this) ? ae :
                    SpiUtils.createAttributeEvent(BasicContextImpl.this, ae.getBindingName(), ae.getAttributeName(), ae.getType());

            final List thisListeners = getCopyOfCtxListeners();
            for (int i = 0; i < thisListeners.size(); i++) {
                final ContextListener ctxListener = (ContextListener) thisListeners.get(i);
                ctxListener.attributeChanged(ae);
            }

            // iterate to root context
            while (!res.isRoot()) {
                res = res.getParent();
                final BasicContextImpl parent = getRootContextImpl().getCache().getContext(res);

                if (parent != null) {
                    final List parentListeners = parent.getCopyOfCtxListeners();
                    for (int i = 0; i < parentListeners.size(); i++) {
                        final ContextListener ctxListener = (ContextListener) parentListeners.get(i);
                        ctxListener.attributeChanged(ae);
                    }
                }
            }
        }


        final void fireBindingEvent(final String name, final int eventType) {
            Resource res = getAbsolutePath();
            BindingEvent be = SpiUtils.createBindingEvent(BasicContextImpl.this, name, eventType);
            be = (be.getSource() == this) ? be :
                    SpiUtils.createBindingEvent(BasicContextImpl.this, be.getBindingName(), be.getType());

            final List thisListeners = getCopyOfCtxListeners();
            for (int i = 0; i < thisListeners.size(); i++) {
                final ContextListener ctxListener = (ContextListener) thisListeners.get(i);
                ctxListener.bindingChanged(be);
            }

            // iterate to root context
            while (!res.isRoot()) {
                res = res.getParent();
                final BasicContextImpl parent = getRootContextImpl().getCache().getContext(res);

                if (parent != null) {
                    final List parentListeners = parent.getCopyOfCtxListeners();
                    for (int i = 0; i < parentListeners.size(); i++) {
                        final ContextListener ctxListener = (ContextListener) parentListeners.get(i);
                        ctxListener.bindingChanged(be);
                    }
                }
            }
        }

    }
}
