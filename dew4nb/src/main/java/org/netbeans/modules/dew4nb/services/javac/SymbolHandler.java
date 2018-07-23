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

package org.netbeans.modules.dew4nb.services.javac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.dew4nb.endpoint.BasicRequestHandler;
import org.netbeans.modules.dew4nb.endpoint.RequestHandler;
import org.netbeans.modules.dew4nb.endpoint.Status;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.netbeans.modules.java.source.ui.JavaSymbolDescriptor;
import org.netbeans.modules.jumpto.common.HighlightingNameFormatter;
import org.netbeans.modules.jumpto.common.Utils;
import org.netbeans.modules.jumpto.symbol.SymbolProviderAccessor;
import org.netbeans.spi.jumpto.symbol.SymbolDescriptor;
import org.netbeans.spi.jumpto.symbol.SymbolProvider;
import org.netbeans.spi.jumpto.type.SearchType;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Parameters;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = RequestHandler.class)
public class SymbolHandler extends BasicRequestHandler<JavacQuery, JavacMessageType, JavacSymbolResult> {

    private final HighlightingNameFormatter format;

    public SymbolHandler() {
        super(JavacModels.END_POINT, JavacMessageType.symbols, JavacQuery.class, JavacSymbolResult.class);
        format = HighlightingNameFormatter.createBoldFormatter();
    }

    @Override
    protected Status handle(
            @NonNull final JavacQuery request,
            @NonNull final JavacSymbolResult response) {
        Parameters.notNull("request", request); //NOI18N
        Parameters.notNull("response", response);   //NOI18N
        final JavacMessageType requestType = request.getType();
        if (requestType != JavacMessageType.symbols) {
            throw new IllegalStateException(String.valueOf(requestType));
        }
        final String text = request.getJava();
        if (text != null) {
            final Collection<? extends SymbolProvider> symProviders = getProviders();
            try {
                final WorkspaceResolver resolver = WorkspaceResolver.getDefault();
                if (resolver == null) {
                    throw new IllegalStateException("No WorkspaceResolver in Lookup");  //NOI18N
                }
                final Collection<? extends SymbolDescriptor> symbols = computeSymbols(
                    text,
                    symProviders);
                for (SymbolDescriptor symbol : symbols) {
                    final FileObject file = symbol.getFileObject();
                    if (file != null) {
                        final WorkspaceResolver.Context ctx = resolver.resolveContext(file);
                        if (ctx != null) {
                            response.getSymbols().add(new TypeDescriptor(
                                format.formatName(
                                    symbol.getSymbolName(),
                                    SymbolProviderAccessor.DEFAULT.getHighlightText(symbol),
                                    false),
                                symbol.getOwnerName(),
                                getSymbolKind(symbol),
                                new Context(
                                    ctx.getUser(),
                                    ctx.getWorkspace(),
                                    ctx.getPath()
                                )));
                        }
                    }
                }
            } finally {
                cleanProviders(symProviders);
            }
        }
        return Status.done;
    }

    @NonNull
    private static String getSymbolKind (@NonNull final SymbolDescriptor symbol) {
        final StringBuilder sb = new StringBuilder();
        if (symbol instanceof JavaSymbolDescriptor) {
            final JavaSymbolDescriptor javaSymbol = (JavaSymbolDescriptor) symbol;
            final ElementKind kind = javaSymbol.getElementKind();
            if (kind.isField()) {
                sb.append("F"); //NOI18N
            } else if (kind == ElementKind.CONSTRUCTOR || kind == ElementKind.METHOD) {
                sb.append("M"); //NOI18N
            } else if (kind == ElementKind.ANNOTATION_TYPE) {
                sb.append("A"); //NOI18N
            } else if (kind == ElementKind.ENUM) {
                sb.append("E"); //NOI18N
            } else if (kind == ElementKind.INTERFACE) {
                sb.append("I"); //NOI18N
            } else if (kind == ElementKind.CLASS) {
                sb.append("C"); //NOI18N
            }
            if (sb.length()>0) {
                final Collection<? extends Modifier> modifiers = javaSymbol.getModifiers();
                int flags = 0;
                for (Modifier m : modifiers) {
                    switch (m) {
                        case PUBLIC:
                            flags |= java.lang.reflect.Modifier.PUBLIC;
                            break;
                        case PROTECTED:
                            flags |= java.lang.reflect.Modifier.PROTECTED;
                            break;
                        case PRIVATE:
                            flags |= java.lang.reflect.Modifier.PRIVATE;
                            break;
                        case STATIC:
                            flags |= java.lang.reflect.Modifier.STATIC;
                            break;
                    }
                }
                sb.append(flags);
            }
        }
        return sb.toString();
    }

    @NonNull
    private static Collection<? extends SymbolProvider> getProviders() {
        final Collection<SymbolProvider> result = new ArrayList<>(
            Lookup.getDefault().lookupAll(SymbolProvider.class));
        return Collections.unmodifiableCollection(result);
    }

    private static void cleanProviders(@NonNull final Collection<? extends SymbolProvider> providers) {
        for (SymbolProvider provider : providers) {
            provider.cleanup();
        }
    }

    @NonNull
    private static Collection<? extends SymbolDescriptor> computeSymbols(
        @NonNull final String text,
        @NonNull final Collection<? extends SymbolProvider> providers) {
        Parameters.notNull("text", text);   //NOI18N
        Parameters.notNull("providers", providers); //NOI18N
        final List<SymbolDescriptor> result = new LinkedList<>();
        for (SymbolProvider.Context ctx : createContexts(text)) {
            final SymbolProvider.Result res = SymbolProviderAccessor.DEFAULT.createResult(
            result,
            new String[1],
            ctx);
            for (SymbolProvider provider : providers) {
                provider.computeSymbolNames(ctx, res);
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    private static Collection<? extends SymbolProvider.Context> createContexts(@NonNull String text) {
        Parameters.notNull("text", text);   //NOI18N
        boolean exact = text.endsWith(" "); // NOI18N
        text = text.trim();
        if ( text.length() == 0) {
            return Collections.<SymbolProvider.Context>emptySet();
        }
        Collection<? extends SymbolProvider.Context> contexts;
        int wildcard = Utils.containsWildCard(text);
        if (exact) {
            contexts = Collections.singleton(
                SymbolProviderAccessor.DEFAULT.createContext(null, text, SearchType.CASE_INSENSITIVE_EXACT_NAME));
        } else if ((Utils.isAllUpper(text) && text.length() > 1) || Utils.isCamelCase(text)) {
            contexts = Arrays.asList(
                new SymbolProvider.Context[] {
                    SymbolProviderAccessor.DEFAULT.createContext(null, text, SearchType.CAMEL_CASE),
                    SymbolProviderAccessor.DEFAULT.createContext(null, text, SearchType.CASE_INSENSITIVE_PREFIX)});
        } else if (wildcard != -1) {
            text = Utils.removeNonNeededWildCards(text);
            contexts = Collections.singleton(SymbolProviderAccessor.DEFAULT.createContext(null, text, SearchType.CASE_INSENSITIVE_REGEXP));
        } else {
            contexts = Collections.singleton(SymbolProviderAccessor.DEFAULT.createContext(null, text,SearchType.CASE_INSENSITIVE_PREFIX));
        }
        return contexts;
    }
}
