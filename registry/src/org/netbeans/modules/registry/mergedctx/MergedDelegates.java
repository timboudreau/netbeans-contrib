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

import org.netbeans.api.registry.AttributeEvent;
import org.netbeans.api.registry.BindingEvent;
import org.netbeans.api.registry.ContextException;
import org.netbeans.api.registry.SubcontextEvent;
import org.netbeans.spi.registry.BasicContext;

import java.util.*;


/**
 * MergedDelegates is responsible for merging and masking
 */
final class MergedDelegates {
    private static final int ACTIVE_DELEGATE_INDEX = 0;
    private final Resource resource;
    private final RootContextImpl rootContext;

    private MergedDelegates.ContextNames subcontextNameCache;
    private MergedDelegates.BindingNames bindingNames;

    private BasicContext[] delegates;

    private MergedDelegates(final Resource resource, final RootContextImpl rootContext, final BasicContext[] delegates) {
        this.delegates = delegates;
        this.resource = resource;
        this.rootContext = rootContext;
    }

    void init() {
        initSubcontextNames();
        initBindingNames();
        initAttributeNames(null);
    }

    static MergedDelegates createRoot(final BasicContext[] rootDelegates, final RootContextImpl rootContext) {
        return (checkValidity(rootDelegates)) ? new MergedDelegates(new Resource("/"), rootContext, rootDelegates) : null;
    }

    private void initSubcontextNames() {
        if (subcontextNameCache == null)
            subcontextNameCache = new ContextNames();
    }

    private void initBindingNames() {
        if (bindingNames == null)
            bindingNames = new BindingNames();
    }

    private void initAttributeNames(final String bindingName) {
        MergedDelegates.AttributeNames attribNames = bindingNames.getAttributeNameCache(bindingName);

        if (attribNames == null) {
            bindingNames.createAttributeNameCache(bindingName);
        }
    }


    void refreshSubcontextNames(final BasicContextImpl.EventDispatcher dispatcher) {
        subcontextNameCache.refresh(dispatcher);
    }

    void refreshBindingNames(final BasicContextImpl.EventDispatcher dispatcher, final BindingEvent evt, final int layout) {
        bindingNames.refresh(dispatcher, evt, layout);

    }

    void refreshAttributeNames(final BasicContextImpl.EventDispatcher dispatcher, final AttributeEvent evt, final int layout) {
        if (evt != null) initAttributeNames(evt.getBindingName());
        final MergedDelegates.AttributeNames attNames = bindingNames.getAttributeNameCache((evt == null) ? null : evt.getBindingName());
        if (attNames != null)
            attNames.refresh(dispatcher, bindingNames, evt, layout);

    }

    Collection/*<String>*/ getSubcontextNames() {
        return subcontextNameCache.getNames();
    }

    Collection/*<String>*/ getBindingNames() {
        return bindingNames.getNames();
    }

    Collection/*<String>*/ getAttributeNames(final String bindingName) {
        initAttributeNames(bindingName);
        final MergedDelegates.AttributeNames attribsNames = bindingNames.createAttributeNameCache(bindingName);
        return attribsNames.getNames();
    }


    MergedDelegates createChild(final String subCtxName) {
        final BasicContext[] delegates = getSubcontexts(subCtxName, true);
        final Resource resource = getAbsolutePath().getChild(subCtxName);
        final MergedDelegates retVal = (checkValidity(delegates)) ? new MergedDelegates(resource, rootContext, delegates) : null;
        return retVal;
    }

    MergedDelegates createParent() {
        final Resource absolutePath = getAbsolutePath();
        return (absolutePath.isRoot()) ? null : createChild(rootContext.getContextDelegates(), absolutePath.getParent());
    }

    static MergedDelegates createChild(final MergedDelegates relativeDelegates, final Resource absolutePath) {
        if (!relativeDelegates.getAbsolutePath().isSuperior(absolutePath))
            throw new InternalError();

        final Enumeration en1 = relativeDelegates.getAbsolutePath().getElements();
        final Enumeration en = absolutePath.getElements();

        while (en1.hasMoreElements()) {
            if (!en.hasMoreElements()) throw new InternalError();
            en.nextElement();
        }


        MergedDelegates retVal = relativeDelegates;

        while (en.hasMoreElements()) {
            final String subCtxName = (String) en.nextElement();
            retVal = retVal.createChild(subCtxName);
            if (retVal == null) break;
        }

        return retVal;
    }

    void setDelegates(final BasicContextImpl.EventDispatcher dispatcher, final MergedDelegates ctxDelegates) {
        this.delegates = ctxDelegates.delegates;
        refreshSubcontextNames(dispatcher);
        refreshBindingNames(dispatcher, null, -1);
    }

    Resource getAbsolutePath() {
        return resource;
    }

    boolean hasDefault(final String bindingName, final BasicContext[] notMaskedDelegates) {
        boolean retVal = false;
        final Object[] bindingsOrContexts;
        try {
            if (bindingName == null) {
                bindingsOrContexts = notMaskedDelegates;                
            } else {
                bindingsOrContexts = getBindingNameLayout(bindingName, false);                                
            }

            if (bindingsOrContexts != null) {
                for (int i = (ACTIVE_DELEGATE_INDEX + 1);retVal == false && i < bindingsOrContexts.length; i++) {
                    retVal = (bindingsOrContexts[i] != null);
                }
            }
        } catch (ContextException e) {
            retVal = false;
        }

        return retVal;
    }

    BasicContext getActiveDelegate(final boolean create) {
        if (delegates[ACTIVE_DELEGATE_INDEX] == null && create) {
            try {
                BasicContext retVal = rootContext.getContextDelegates().getDelegates()[ACTIVE_DELEGATE_INDEX];
                final Enumeration elems = resource.getElements();

                BasicContext temp = retVal;
                while (elems.hasMoreElements() && retVal != null) {
                    final String subCtxName = (String) elems.nextElement();
                    retVal = retVal.getSubcontext(subCtxName);
                    if (retVal == null) {
                        retVal = temp.createSubcontext(subCtxName);
                    }
                    if (retVal == null) break;
                    temp = retVal;
                }
                delegates[ACTIVE_DELEGATE_INDEX] = retVal;
            } catch (ContextException e) {
                delegates[ACTIVE_DELEGATE_INDEX] = null;
            }
        }
        return delegates[ACTIVE_DELEGATE_INDEX];
    }


    /**
     * @param subctxName
     * @return array of delegates for subcontext with name subctxName. Length of
     * this arry is the same as array of delegates for RootCtximpl. This array may
     * contain also null values.
     */
    BasicContext[] getSubcontexts(final String subctxName, final boolean considerMask) {
        final boolean isMasked = (considerMask && MaskUtils.existMaskForCtx(getActiveDelegate(false), subctxName));
        /*Only active delegate is taken into account if mask exists*/
        final int mergedCount = (isMasked) ? 1 : delegates.length;

        final List subDelegates = new ArrayList();
        for (int layoutIndex = 0; layoutIndex < mergedCount; layoutIndex++) {
            final BasicContext delegate = (delegates[layoutIndex] == null) ? null : delegates[layoutIndex].getSubcontext(subctxName);
            subDelegates.add(delegate);
        }

        return (BasicContext[]) subDelegates.toArray(new BasicContext[delegates.length]);
    }

    /**
     * may return null
     *
     */
    String[] getBindingNameLayout (final String bindingName, final boolean considerMask) throws ContextException {
        boolean exists = false;
        final List subDelegates = new ArrayList();

        if (bindingName != null) {
            final boolean isMasked = considerMask && MaskUtils.existMaskForBinding(getActiveDelegate(false), bindingName);
            /*Only active delegate is taken into account if mask exists*/
            final int mergedCount = (isMasked) ? 1 : delegates.length;

            for (int layoutIndex = ACTIVE_DELEGATE_INDEX;layoutIndex < mergedCount; layoutIndex++) {
                final Collection bindingNames = (delegates[layoutIndex] == null) ? null : delegates[layoutIndex].getBindingNames();
                subDelegates.add(bindingNames != null && (bindingNames.contains(bindingName)) ? bindingName : null);                
                exists = true;
            }
        }
        return (exists) ? (String[]) subDelegates.toArray(new String[delegates.length]) : null;
    }

    boolean existsBinding (final String bindingName, final boolean considerMask) throws ContextException {
        boolean exists = false;

        if (bindingName != null) {
            final boolean isMasked = considerMask && MaskUtils.existMaskForBinding(getActiveDelegate(false), bindingName);
            /*Only active delegate is taken into account if mask exists*/
            final int mergedCount = (isMasked) ? 1 : delegates.length;

            for (int layoutIndex = ACTIVE_DELEGATE_INDEX;layoutIndex < mergedCount; layoutIndex++) {
                final Collection bindingNames = (delegates[layoutIndex] == null) ? null : delegates[layoutIndex].getBindingNames();
                if (bindingNames.contains(bindingName)) {
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }
    
/*msy return null*/
    Object lookupObject(final String bindingName) throws ContextException {
        //initBindingNames();
        final Object[] bindingLayout = getBindingNameLayout(bindingName, true);
        Object retVal = null;
        if (bindingLayout != null) {
            for (int i = ACTIVE_DELEGATE_INDEX;i < bindingLayout.length; i++) {
                if (bindingLayout[i] == null) continue;
                retVal = delegates[i].lookupObject(bindingName);
                break;
                //if (retVal != null) break;
            }
        }
        return retVal;
    }

    void bindObject(final String bindingName, final Object object) throws ContextException {
        final Object[] bindingLayout = getBindingNameLayout(bindingName, true);
        final boolean existOnActive = (bindingLayout != null && bindingLayout[ACTIVE_DELEGATE_INDEX] != null);
        final BasicContext activeDelegate = getActiveDelegate(true);
        if (object == null) {
            /*destroy of binding*/
            MaskUtils.createMaskForBinding(activeDelegate, bindingName);
            if (existOnActive) activeDelegate.bindObject(bindingName, null);
        } else {
            /*modification or creation of binding*/
            final BasicContext copyAttributesFrom = (!existOnActive) ? getContextAttributesAreCopiedFrom(bindingLayout) : null;
            activeDelegate.bindObject(bindingName, object);

            if (copyAttributesFrom != null)
                copyAttributes(bindingName, activeDelegate, copyAttributesFrom);

        }
    }

    /*may return null*/
    String getAttribute(final String bindingName, final String attributeName) throws ContextException {
        initAttributeNames(bindingName);
        String retVal = null;
        final boolean isBindingAttribute = (bindingName != null);

        final Object[] bindingLayout = getBindingNameLayout(bindingName, true);
        for (int i = ACTIVE_DELEGATE_INDEX;i < delegates.length; i++) {
            final Object binding = (bindingLayout != null) ? bindingLayout[i] : null;
            if (binding == null && isBindingAttribute) continue;

            if (delegates[i] != null) {
                retVal = delegates[i].getAttribute(bindingName, attributeName);
                if (retVal != null || isBindingAttribute) break;
            }
        }

        return retVal;
    }

    void setAttribute(final String bindingName, final Object binding, final String attributeName, final String value) throws ContextException {
        initAttributeNames(bindingName);
        if (binding == null && bindingName != null) return;

        if (bindingName != null) {
            final Object[] bindingLayout = getBindingNameLayout(bindingName, true);
            final boolean existBindingOnActive = (bindingLayout != null && bindingLayout[ACTIVE_DELEGATE_INDEX] != null);
            if (!existBindingOnActive) {
                /*creates binding on active delegate*/
                bindObject(bindingName, binding);
            }
        }

        final BasicContext activeDelegate = getActiveDelegate(true);
        if (value == null) {
            MaskUtils.createMaskForAttributes(activeDelegate, bindingName, attributeName);
        }
        activeDelegate.setAttribute(bindingName, attributeName, value);
    }

    BasicContext createSubcontext(final String subcontextName) throws ContextException {
        //initSubcontextNames();
        final BasicContext activeDelegate = getActiveDelegate(true);
        final BasicContext parentOfActive = activeDelegate.getParentContext();
        /*Mask is automatically created, if its created in context, which is already masked*/
        if (MaskUtils.existMaskForCtx(parentOfActive, activeDelegate.getContextName())) {
            /*Once created mask is never deleted, until whole context where mask exist is deleted*/
            MaskUtils.createMaskForCtx(activeDelegate, subcontextName);
        }
        return activeDelegate.createSubcontext(subcontextName);
    }

    static void destroyActiveDelegate(final BasicContext activeDelegate) throws ContextException {
        if (activeDelegate != null) {
            final BasicContext parentOfActiveDelegate = activeDelegate.getParentContext();
            final String subcontextName = activeDelegate.getContextName();

            if (parentOfActiveDelegate != null) {
                /*Once created mask is never deleted, until whole context where mask exist is deleted*/
                MaskUtils.createMaskForCtx(parentOfActiveDelegate, subcontextName);
                parentOfActiveDelegate.destroySubcontext(subcontextName);
            }
        }
    }

    private static boolean checkValidity(final BasicContext[] delegates) {
        for (int i = ACTIVE_DELEGATE_INDEX;i < delegates.length; i++) {
            final BasicContext delegate = delegates[i];
            if (delegate != null) return true;
        }
        return false;
    }

/*may return null*/
    private BasicContext getContextAttributesAreCopiedFrom(final Object[] bindingLayout) {
        BasicContext copyAttributesFrom = null;
        for (int i = 1; i < bindingLayout.length; i++) {
            final Object binding = bindingLayout[i];
            if (binding != null && delegates[i] != null) {
                copyAttributesFrom = delegates[i];
                break;
            }
        }
        return copyAttributesFrom;
    }


    private static void copyAttributes(final String bindingName, final BasicContext to, final BasicContext from) throws ContextException {
        final Collection names = from.getAttributeNames(bindingName);
        for (Iterator iterator = names.iterator(); iterator.hasNext();) {
            final String attrName = (String) iterator.next();
            final String attrValue = from.getAttribute(bindingName, attrName);
            to.setAttribute(bindingName, attrName, attrValue);
        }
    }


    BasicContext[] getDelegates() {
        return delegates;
    }

    RootContextImpl getRootContext() {
        return rootContext;
    }

    final class ContextNames extends NameCache {
        ContextNames() {
            for (int layoutIndex = ACTIVE_DELEGATE_INDEX;layoutIndex < delegates.length; layoutIndex++) {
                final BasicContext delegate = delegates[layoutIndex];
                if (delegate == null) continue;

                final Collection unfilteredNames = delegate.getSubcontextNames();
                for (Iterator iterator = unfilteredNames.iterator(); iterator.hasNext();) {
                    final String name = (String) iterator.next();
                    if (layoutIndex == ACTIVE_DELEGATE_INDEX) {
                        if (!MaskUtils.isMaskForCtxName(name))
                            add(layoutIndex, name);
                    } else {
                        final boolean existMask = (MaskUtils.existMaskForCtx(getActiveDelegate(false), name));
                        if (!existMask) add(layoutIndex, name);
                    }
                }
            }
        }

        void refresh(final BasicContextImpl.EventDispatcher dispatcher) {
            final List removed;
            final List added;

            synchronized (NameCache.class) {
                final ContextNames contextNames = new ContextNames();

                final Collection originalNames = this.getNames();
                final Collection updatedNames = contextNames.getNames();

                removed = new ArrayList(originalNames);
                removed.removeAll(updatedNames);

                added = new ArrayList(updatedNames);
                added.removeAll(originalNames);

                this.content = contextNames.content;
            }

            for (int i = 0; i < removed.size(); i++) {
                final String name = (String) removed.get(i);
                dispatcher.fireSubcontextEvent(name, SubcontextEvent.SUBCONTEXT_REMOVED);
            }

            for (int i = 0; i < added.size(); i++) {
                final String name = (String) added.get(i);
                dispatcher.fireSubcontextEvent(name, SubcontextEvent.SUBCONTEXT_ADDED);
            }
        }
    }

    final class BindingNames extends NameCache {
        private Map /*<String,NameCacheImpl>*/ attribsImpl;

        BindingNames() {
            for (int layoutIndex = ACTIVE_DELEGATE_INDEX;layoutIndex < delegates.length; layoutIndex++) {
                final BasicContext deleg = delegates[layoutIndex];
                if (deleg == null) continue;

                final Collection unfilteredNames = deleg.getBindingNames();
                for (Iterator iterator = unfilteredNames.iterator(); iterator.hasNext();) {
                    final String name = (String) iterator.next();
                    if (layoutIndex == ACTIVE_DELEGATE_INDEX) {
                        if (!MaskUtils.isMaskForBindingName(name))
                            add(layoutIndex, name);
                    } else {
                        final boolean existMask = (MaskUtils.existMaskForBinding(getActiveDelegate(false), name));
                        if (!existMask) add(layoutIndex, name);
                    }
                }
            }
        }

        AttributeNames getAttributeNameCache(final String bindingName) {
            return (attribsImpl == null) ? null : (AttributeNames) attribsImpl.get(bindingName);
        }

        AttributeNames createAttributeNameCache(final String bindingName) {
            synchronized (NameCache.class) {
                final AttributeNames retVal = new AttributeNames(bindingName);
                if (attribsImpl == null)
                    attribsImpl = new HashMap();

                attribsImpl.put(bindingName, retVal);
                return retVal;
            }
        }

        synchronized void clear() {
            synchronized (NameCache.class) {
                super.clear();
                attribsImpl = null;
            }
        }


        void refresh(final BasicContextImpl.EventDispatcher dispatcher, final BindingEvent evt, int layout) {
            final String modifiedBindingName = (evt != null) ? evt.getBindingName() : null;
            final List removed;
            final List added;
            final List modified = new ArrayList();
            final Collection originalNames;
            final Collection updatedNames;

            synchronized (NameCache.class) {
                final BindingNames bindingNames = new BindingNames();//ctxImpl.getContextDelegates().createBindingNames();

                originalNames = this.getNames();
                updatedNames = bindingNames.getNames();

                removed = new ArrayList(originalNames);
                removed.removeAll(updatedNames);

                added = new ArrayList(updatedNames);
                added.removeAll(originalNames);

                for (Iterator iterator = originalNames.iterator(); iterator.hasNext();) {
                    final String name = (String) iterator.next();
                    if (modifiedBindingName != null && !removed.contains(name) && modifiedBindingName.equals(name)) {
                        final Integer origLayout = (Integer) this.content.get(name);
                        final Integer updLayout = (Integer) bindingNames.content.get(name);
                        if (origLayout != null && updLayout != null) {
                            if (layout < 0) layout = updLayout.intValue();
                            if (layout <= origLayout.intValue()) {
                                modified.add(name);
                            }
                        }
                    }
                }

                this.content = bindingNames.content;
            }

            if (removed.size() == 0 && added.size() == 0) {
                if (evt != null) {
                    final AttributeNames attrs = getAttributeNameCache(evt.getBindingName());
                    if (attrs != null) {
                        attrs.refresh(dispatcher, this, null, -1);
                    }
                }
            }

            for (int i = 0; i < removed.size(); i++) {
                final String name = (String) removed.get(i);
                dispatcher.fireBindingEvent(name, BindingEvent.BINDING_REMOVED);
            }

            for (int i = 0; i < added.size(); i++) {
                final String name = (String) added.get(i);
                dispatcher.fireBindingEvent(name, BindingEvent.BINDING_ADDED);
            }
            for (int i = 0; i < modified.size(); i++) {
                final String name = (String) modified.get(i);
                dispatcher.fireBindingEvent(name, BindingEvent.BINDING_MODIFIED);
            }
        }
    }

    final class AttributeNames extends NameCache {
        private final String bindingName;

        private AttributeNames(final String bindingName) {
            this.bindingName = bindingName;

            Object[] bindingLayout;

            try {
                bindingLayout = getBindingNameLayout(bindingName, true);
            } catch (ContextException e) {
                bindingLayout = null;
            }


            for (int layoutIndex = ACTIVE_DELEGATE_INDEX;layoutIndex < delegates.length; layoutIndex++) {
                final Object binding = (bindingLayout != null) ? bindingLayout[layoutIndex] : null;
                if (bindingName != null && binding == null) continue;
                if (delegates[layoutIndex] != null) {
                    final Collection unfilteredNames = delegates[layoutIndex].getAttributeNames(bindingName);

                    for (Iterator iterator = unfilteredNames.iterator(); iterator.hasNext();) {
                        final String name = (String) iterator.next();
                        if (layoutIndex == ACTIVE_DELEGATE_INDEX) {
                            if (!MaskUtils.isMaskForAttributeName(name)) {
                                add(layoutIndex, name);
                            }
                        } else {
                            final boolean existMask = (MaskUtils.existMaskForAttributes(getActiveDelegate(false), bindingName, name));

                            if (!existMask) {
                                add(layoutIndex, name);
                            }
                        }
                    }
                    if ((bindingName != null)) break;
                }
            }

        }

        void refresh(final BasicContextImpl.EventDispatcher dispatcher, final BindingNames bindings, final AttributeEvent evt, int layout) {
            final String modifiedAttribName = (evt == null) ? null : evt.getAttributeName();
            final List removed;
            final List added;
            final List modified = new ArrayList();

            if (bindings == null) return;

            synchronized (NameCache.class) {
                final AttributeNames attribNames = bindings.createAttributeNameCache(bindingName);
                final Collection originalNames = this.getNames();
                final Collection updatedNames = attribNames.getNames();

                removed = new ArrayList(originalNames);
                removed.removeAll(updatedNames);

                added = new ArrayList(updatedNames);
                added.removeAll(originalNames);

                for (Iterator iterator = originalNames.iterator(); iterator.hasNext();) {
                    final String name = (String) iterator.next();
                    if (modifiedAttribName != null && !removed.contains(name) && modifiedAttribName.equals(name)) {
                        final Integer origLayout = (Integer) this.content.get(name);
                        final Integer updLayout = (Integer) attribNames.content.get(name);
                        if (origLayout != null && updLayout != null) {
                            if (layout < 0) layout = updLayout.intValue();
                            if (layout <= origLayout.intValue()) {
                                modified.add(name);
                            }
                        }
                    }

                }

                this.content = attribNames.content;
            }

            for (int i = 0; i < removed.size(); i++) {
                final String name = (String) removed.get(i);
                dispatcher.fireAttributeEvent(bindingName, name, AttributeEvent.ATTRIBUTE_REMOVED);
            }

            for (int i = 0; i < added.size(); i++) {
                final String name = (String) added.get(i);
                dispatcher.fireAttributeEvent(bindingName, name, AttributeEvent.ATTRIBUTE_ADDED);
            }

            for (int i = 0; i < modified.size(); i++) {
                final String name = (String) modified.get(i);
                dispatcher.fireAttributeEvent(bindingName, name, AttributeEvent.ATTRIBUTE_MODIFIED);
            }

        }
    }
}
