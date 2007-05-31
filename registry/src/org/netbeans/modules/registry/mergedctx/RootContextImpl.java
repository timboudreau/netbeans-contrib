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
import org.netbeans.spi.registry.MergedContextProvider;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.util.Mutex;
import org.openide.util.WeakListeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class RootContextImpl extends BasicContextImpl {
    private final Mutex.Privileged privilegedMutex = new Mutex.Privileged();
    private final Mutex mutex = new Mutex(privilegedMutex);

    private final MergedContextProvider provider;
    private final Cache cache = new Cache();

    private final MergeCtxProviderListener mergeCtxProviderListener = new MergeCtxProviderListener();;
    private ContextListenerImpl[] listeners;


    public static final BasicContext create(final MergedContextProvider provider) {
        return new RootContextImpl(provider);
    }

    private RootContextImpl(final MergedContextProvider provider) {
        super();
        this.provider = provider;
        setContextDelegates(provider);
        getCache().cacheContext(this);
        this.provider.addPropertyChangeListener(
                (PropertyChangeListener) WeakListeners.create(PropertyChangeListener.class, mergeCtxProviderListener, this.provider));
    }

    private void setContextDelegates(final MergedContextProvider provider) {
        final MergedDelegates rootCtxDelegates = MergedDelegates.createRoot(provider.getDelegates(), RootContextImpl.this);
        if (rootCtxDelegates != null) rootCtxDelegates.init();
        if (getContextDelegates() == null && rootCtxDelegates == null) return;
        removeListeners();
        super.setContextDelegates(rootCtxDelegates);
        addListeners();

        for (Iterator iterator = getCache().existingSubcontexts(this).iterator(); iterator.hasNext();) {
            final BasicContextImpl context = (BasicContextImpl) iterator.next();
            if (context == this) continue;
            final MergedDelegates newDelegate = MergedDelegates.createChild(rootCtxDelegates, context.getAbsolutePath());
            if (newDelegate != null) {
                context.setContextDelegates(newDelegate);
            } else {
                getCache().removeContext(context.getAbsolutePath());
            }
        }
    }

    void setContextDelegates(final MergedDelegates contextDelegates) {
    }

    private synchronized void removeListeners() {
        if (getContextDelegates() != null) {
            final BasicContext[] oldDelegates = getContextDelegates().getDelegates();
            if (listeners != null && listeners.length == oldDelegates.length) {
                for (int i = 0; i < oldDelegates.length; i++) {
                    final BasicContext basicContext = oldDelegates[i];
                    if (basicContext != null && listeners[i] != null) {
                        basicContext.removeContextListener(listeners[i]);
                    }
                }
            }
        }
        listeners = null;
    }

    private synchronized void addListeners() {
        final BasicContext[] newDelegates = getContextDelegates().getDelegates();
        if (listeners == null) {
            final List list = new ArrayList();
            for (int layoutIndex = 0; layoutIndex < newDelegates.length; layoutIndex++) {
                if (newDelegates[layoutIndex] != null) {
                    final String absoluteName = SpiUtils.createContext(newDelegates[layoutIndex]).getAbsoluteContextName();
                    final ContextListenerImpl l = new ContextListenerImpl(absoluteName);
                    newDelegates[layoutIndex].addContextListener(
                            (ContextListener) WeakListeners.create(ContextListener.class, l, newDelegates[layoutIndex]));
                    list.add(l);
                }
            }

            listeners = (ContextListenerImpl[]) list.toArray(new ContextListenerImpl[list.size()]);
        }
    }

    public ObjectRef findObject(final Object object) {
        return cache.getObjectRef(object);
    }

    public void flush() {
    }

    public Mutex.Privileged getMutex() {
        return privilegedMutex;
    }

    final protected Cache getCache() {
        return cache;
    }

    private final class MergeCtxProviderListener implements PropertyChangeListener {
        public final void propertyChange(final PropertyChangeEvent evt) {
            setContextDelegates(provider);
        }
    }


    private final class ContextListenerImpl implements ContextListener {
        private final String absoluteName;

        private ContextListenerImpl(final String absoluteName) {
            this.absoluteName = absoluteName;
        }


        public final void subcontextChanged(final SubcontextEvent evt) {
            /*Mask is ever followed by deleted context on active delegate*/
            if (MaskUtils.isMaskForCtxName(evt.getSubcontextName())) {
                return;
            }

            final Resource contextPath;
            contextPath = new Resource(evt.getContext().getAbsoluteContextName().substring(absoluteName.length()));
            final BasicContextImpl mergedSource = getCache().getContext(contextPath);
            final BasicContextImpl mergedSubcontext = getCache().getContext(contextPath.getChild(evt.getSubcontextName()));

            if (mergedSubcontext != null) {
                updateContext(mergedSource, mergedSubcontext, evt);
            }

            if (mergedSource != null) {
                if (!mergedSource.isInvalid()) {
                    if (mergedSubcontext != null && !mergedSubcontext.isInvalid()) {
                        final int layout = Arrays.asList(listeners).indexOf(this);
                        mergedSubcontext.getContextDelegates().refreshBindingNames(mergedSubcontext.getDispatcher(), null, layout);
                        mergedSubcontext.getContextDelegates().refreshAttributeNames(mergedSubcontext.getDispatcher(), null, layout);
                    }
                    mergedSource.getContextDelegates().refreshSubcontextNames(mergedSource.getDispatcher());
                }
            }
        }

        private void updateContext(final BasicContextImpl mergedSource, final BasicContextImpl mergedSubcontext, final SubcontextEvent evt) {
            BasicContextImpl parent = mergedSource;
            parent = (parent == null) ? (BasicContextImpl) mergedSubcontext.getParentContext() : parent;
            if (parent != null) {
                final MergedDelegates updatedContext = parent.getContextDelegates().createChild(evt.getSubcontextName());
                if (updatedContext != null) {
                    mergedSubcontext.setContextDelegates(updatedContext);
                } else {
                    getCache().removeContext(mergedSubcontext.getAbsolutePath());
                }
            }
        }

        public final void bindingChanged(final BindingEvent evt) {
            if (MaskUtils.isMaskForBindingName(evt.getBindingName())) {
                return;
            }

            final Resource contextPath;
            contextPath = new Resource(evt.getContext().getAbsoluteContextName().substring(absoluteName.length()));
            final BasicContextImpl mergedSource = getCache().getContext(contextPath);

            if (mergedSource != null) {
                if (!mergedSource.isInvalid()) {
                    final int layout = Arrays.asList(listeners).indexOf(this);
                    mergedSource.getContextDelegates().refreshBindingNames(mergedSource.getDispatcher(), evt, layout);//initBindingNames();
                }
            }
        }

        public final void attributeChanged(final AttributeEvent evt) {
            if (MaskUtils.isMaskForAttributeName(evt.getAttributeName())) return;

            final Resource contextPath;
            contextPath = new Resource(evt.getContext().getAbsoluteContextName().substring(absoluteName.length()));
            final BasicContextImpl mergedSource = getCache().getContext(contextPath);


            if (mergedSource != null) {
                if (!mergedSource.isInvalid()) {
                    mergedSource.getContextDelegates().refreshAttributeNames(mergedSource.getDispatcher(), evt, Arrays.asList(listeners).indexOf(this));
                }
            }
        }
    }


}