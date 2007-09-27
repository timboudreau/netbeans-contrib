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