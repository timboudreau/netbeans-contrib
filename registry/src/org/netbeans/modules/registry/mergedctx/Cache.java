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

import org.netbeans.api.registry.ObjectRef;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.*;

/**
 * Provides caching mechanism for BasicContextImpl and RootContextImpl, which should be constructed
 * exclusively by calling these factory methods: createRootContext, createContext, getOrCreateParentCtx
 * and getOrCreateSubCtx.
 *
 * @author Radek Matous
 */
final class Cache {
    /** <String, BasicContextImpl> all live MergedContext provided by this impl.*/
    private final Map contextCache = Collections.synchronizedMap(new WeakHashMap());
    private final Map objectCache = Collections.synchronizedMap(new WeakHashMap());

    Cache() {
    }

    Object getContextSync() {
        return contextCache;
    }

    BasicContextImpl cacheContext(final BasicContextImpl ctxImpl) {
        final boolean isRoot = ctxImpl.getAbsolutePath().isRoot();

        Reference retVal = (isRoot) ? new HardReference(ctxImpl) : new SoftReference(ctxImpl);
        retVal = (Reference) contextCache.put(ctxImpl.getAbsolutePath(), retVal);
        return (retVal == null) ? null : (BasicContextImpl) retVal.get();
    }

    BasicContextImpl getContext(final Resource resource) {
        final Reference ref = (Reference) contextCache.get(resource);
        return (ref == null) ? null : (BasicContextImpl) ref.get();
    }

    void removeContext(final Resource resource) {
        contextCache.remove(resource);
    }

    void cacheObjectRef(final BasicContext root, final BasicContext ctx, final String bindingName, final Object object) {
        if (object != null) {
            objectCache.put(object, SpiUtils.createObjectRef(ctx, null, bindingName));
        }
    }

    void removeObjectRef(final Object object) {
        if (object != null)
            objectCache.remove(object);
    }

    ObjectRef getObjectRef(final Object object) {
        return (ObjectRef) objectCache.get(object);
    }

    Collection existingSubcontexts(final BasicContextImpl ctxImpl) {
        final List eSubctxs = new ArrayList();
        synchronized (contextCache) {
            for (Iterator iterator = contextCache.values().iterator(); iterator.hasNext();) {
                final Reference ref = (Reference) iterator.next();
                final BasicContextImpl iCtx = (BasicContextImpl) ((ref == null) ? null : ref.get());
                if (iCtx != null && ctxImpl.getAbsolutePath().isSuperior(iCtx.getAbsolutePath())) {
                    eSubctxs.add(iCtx);
                }
            }
        }
        Collections.sort(eSubctxs, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final BasicContextImpl ctx1 = (BasicContextImpl) o1;
                final BasicContextImpl ctx2 = (BasicContextImpl) o2;
                final int len1 = ctx1.getAbsolutePath().getPath().length();
                final int len2 = ctx2.getAbsolutePath().getPath().length();
                return (len2 - len1);
            }
        });

        return Collections.unmodifiableCollection(eSubctxs);
    }


    private static final class HardReference extends SoftReference {
        Object referent;

        public HardReference(final Object referent) {
            super(referent);
            this.referent = referent;
        }

        public Object get() {
            return referent;
        }
    }

}
