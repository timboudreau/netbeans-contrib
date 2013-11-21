/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.dew4nb.Context;
import org.netbeans.modules.dew4nb.JavacMessageType;
import org.netbeans.modules.dew4nb.JavacQuery;
import org.netbeans.modules.dew4nb.JavacTypeResult;
import org.netbeans.modules.dew4nb.RequestHandler;
import org.netbeans.modules.dew4nb.Status;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.netbeans.modules.jumpto.common.Utils;
import org.netbeans.modules.jumpto.type.TypeProviderAccessor;
import org.netbeans.spi.jumpto.type.SearchType;
import org.netbeans.spi.jumpto.type.TypeDescriptor;
import org.netbeans.spi.jumpto.type.TypeProvider;
import org.openide.util.Lookup;
import org.openide.util.Parameters;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = RequestHandler.class)
public class TypeHandler extends RequestHandler<JavacQuery, JavacTypeResult> {
    public TypeHandler() {
        super(JavacMessageType.types, JavacQuery.class, JavacTypeResult.class);
    }

    @Override
    protected boolean handle(
        @NonNull final JavacQuery request,
        @NonNull final JavacTypeResult response) {
        Parameters.notNull("request", request); //NOI18N
        Parameters.notNull("response", response);  //NOI18N
        final JavacMessageType requestType = request.getType();
        if (requestType != JavacMessageType.types) {
            throw new IllegalStateException(String.valueOf(requestType));
        }
        final String text = request.getJava();
        final Collection<? extends TypeProvider> typeProviders = getTypeProviders();
        try {
            final Collection<? extends TypeDescriptor> types = callTypeProviders(text, typeProviders);
            final WorkspaceResolver resolver = Lookup.getDefault().lookup(WorkspaceResolver.class);
            if (resolver == null) {
                throw new IllegalStateException("No WorkspaceResolver in Lookup");  //NOI18N
            }
            for (TypeDescriptor td : types) {
                final WorkspaceResolver.Context ctx = resolver.resolveContext(td.getFileObject());
                if (ctx != null) {
                    response.getTypes().add(
                        new org.netbeans.modules.dew4nb.TypeDescriptor(
                        td.getSimpleName(),
                        td.getContextName(),
                        apiToWire(ctx)));
                }
            }
        } finally {
            cleanTypeProviders(typeProviders);
        }
        response.setStatus(Status.success);
        return true;
    }

    @NonNull
    private static Collection<? extends TypeProvider> getTypeProviders() {
        final List<TypeProvider> result = new ArrayList<>();
        for (TypeProvider tp : Lookup.getDefault().lookupAll(TypeProvider.class)) {
            result.add(tp);
        }
        return Collections.unmodifiableCollection(result);
    }

    @NonNull
    private static Collection<? extends TypeDescriptor> callTypeProviders(
            @NonNull final String text,
            @NonNull final Collection<? extends TypeProvider> providers) {
        Parameters.notNull("text", text);   //NOI18N
        Parameters.notNull("providers", providers); //NOI18N
        final Collection<TypeDescriptor> collector = new HashSet<>();
        for (TypeProvider.Context ctx : createContext(text)) {
            final TypeProvider.Result res = createResult(collector, ctx);
            for (TypeProvider tp : providers) {
                tp.computeTypeNames(ctx, res);
            }
        }
        return Collections.unmodifiableCollection(collector);
    }

    private static void cleanTypeProviders(@NonNull Collection<? extends TypeProvider> providers) {
        Parameters.notNull("providers", providers); //NOI18N
        for (TypeProvider tp : providers) {
            tp.cleanup();
        }
    }

    @NonNull
    private static Collection< ? extends TypeProvider.Context> createContext(@NonNull String text) {
        Parameters.notNull("text", text);   //NOI18N
        boolean exact = text.endsWith(" "); // NOI18N
        text = text.trim();
        if ( text.length() == 0) {
            return Collections.<TypeProvider.Context>emptySet();
        }
        Collection<? extends TypeProvider.Context> contexts;
        int wildcard = Utils.containsWildCard(text);
        if (exact) {
            contexts = Collections.singleton(
                TypeProviderAccessor.DEFAULT.createContext(null, text, SearchType.CASE_INSENSITIVE_EXACT_NAME));
        } else if ((Utils.isAllUpper(text) && text.length() > 1) || Utils.isCamelCase(text)) {
            contexts = Arrays.asList(
                new TypeProvider.Context[] {
                    TypeProviderAccessor.DEFAULT.createContext(null, text, SearchType.CAMEL_CASE),
                    TypeProviderAccessor.DEFAULT.createContext(null, text, SearchType.CASE_INSENSITIVE_PREFIX)});
        } else if (wildcard != -1) {
            text = Utils.removeNonNeededWildCards(text);
            contexts = Collections.singleton(TypeProviderAccessor.DEFAULT.createContext(null, text, SearchType.CASE_INSENSITIVE_REGEXP));
        } else {
            contexts = Collections.singleton(TypeProviderAccessor.DEFAULT.createContext(null, text,SearchType.CASE_INSENSITIVE_PREFIX));
        }
        return contexts;
    }

    @NonNull
    private static TypeProvider.Result createResult(
            @NonNull final Collection<? super TypeDescriptor> collector,
            @NonNull final TypeProvider.Context ctx) {
        Parameters.notNull("collector", collector); //NOI18N
        Parameters.notNull("ctx", ctx);   //NOI18N
        return TypeProviderAccessor.DEFAULT.createResult(
             collector,
             new String[1],
             ctx);
    }

    private Context apiToWire(@NonNull WorkspaceResolver.Context ctx) {
        Parameters.notNull("ctx", ctx); //NOI18N
        return new Context(ctx.getUser(), ctx.getWorkspace(), ctx.getPath());
    }
}
