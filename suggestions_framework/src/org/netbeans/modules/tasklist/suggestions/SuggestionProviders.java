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

package org.netbeans.modules.tasklist.suggestions;

import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.openide.util.LookupEvent;
import org.openide.ErrorManager;
import org.netbeans.modules.tasklist.providers.SuggestionProvider;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;

import java.util.*;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;

/**
 * Registry of suggestion providers. Wraps default lookup.
 *
 * @author Petr Kuzel
 */
public final class SuggestionProviders {

    // Providers registry we use

    private List providers = null;
    private List docProviders = null; // subset of elements in providers; these implement DocumentSuggestionProvider
    private Map providersByType = null;

    // keep default (until client exists)
    private static Reference instance;

    // package private so tests can change
    static Lookup lookup = Lookup.getDefault();

    // results must be strongly referenced otherwise it get collected and
    // will not fire change events
    private Lookup.Result lookupResult;

    private SuggestionProviders() {
        // lazy init
    }

    public synchronized static SuggestionProviders getDefault() {
        if (instance == null) {
            return createDefault();
        }
        SuggestionProviders scanner = (SuggestionProviders) instance.get();
        if (scanner == null) {
            return createDefault();
        } else {
            return scanner;
        }
    }

    private static SuggestionProviders createDefault() {
        SuggestionProviders scanner = new SuggestionProviders();
        instance = new WeakReference(scanner);
        return scanner;
    }


    /**
     * Return a list of the providers registered
     *
     * @todo Filter out disabled providers
     */
    public synchronized List getProviders() {
        if (providers == null) {
            providers = new ArrayList(20);
            Lookup.Template template =
                    new Lookup.Template(SuggestionProvider.class);
            lookupResult = lookup.lookup(template);
            lookupResult.addLookupListener(new LookupListener() {
                public void resultChanged(LookupEvent ev) {
                    invalidateCaches();
                }
            });

            Iterator it = lookupResult.allInstances().iterator();
            // Two stage process so we can sort by priority

            ArrayList provList = new ArrayList(20);
            while (it.hasNext()) {
                SuggestionProvider sp = (SuggestionProvider) it.next();
                provList.add(sp);
            }
            SuggestionProvider[] provA =
                    (SuggestionProvider[]) provList.toArray(new SuggestionProvider[provList.size()]);
            final SuggestionTypes types = SuggestionTypes.getDefault();
            Arrays.sort(provA, new Comparator() {
                public int compare(Object o1, Object o2) {
                    SuggestionProvider a = (SuggestionProvider) o1;
                    SuggestionProvider b = (SuggestionProvider) o2;
                    try {
                        SuggestionType at = types.getType(a.getType());
                        SuggestionType bt = types.getType(b.getType());
                        return at.getPosition() - bt.getPosition();
                    } catch (Exception e) {
                        return -1;
                    }
                }
            });
            for (int i = 0; i < provA.length; i++) {
                SuggestionProvider sp = provA[i];
                providers.add(sp);
            }
        }
        return providers;
    }

    public synchronized List getDocProviders() {
        if (docProviders == null) {
            docProviders = new ArrayList(20);
            Iterator it = getProviders().iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (next instanceof DocumentSuggestionProvider) {
                    docProviders.add(next);
                }
            }
        }
        return docProviders;
    }

    /**
     * @return The SuggestionProvider responsible for providing suggestions
     *         of a particular type
     */
    public synchronized SuggestionProvider getProvider(SuggestionType type) {
        if (providersByType == null) {
            SuggestionTypes suggestionTypes = SuggestionTypes.getDefault();
            //Collection types = suggestionTypes.getAllTypes();
            List providers = getProviders();
            providersByType = new HashMap(100); // XXXXX ?<??
            // Perhaps use suggestionTypes.getAllTypes()*2 as a seed?
            // Note, this includes suggestion types that do not have
            // providers
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider) it.next();
                String typeName = provider.getType();
                if (typeName == null) {
                    // Should I just let a NullPointerException occur instead?
                    // After all, non null is required for correct operation.
                    ErrorManager.getDefault().log("SuggestionProvider " + provider + " provides null value to getTypes()");
                    continue;
                }
                SuggestionType tp = suggestionTypes.getType(typeName);
                providersByType.put(tp, provider);
            }
        }
        return (SuggestionProvider) providersByType.get(type);
    }

    // can come from random thread
    private synchronized void invalidateCaches() {
        providers = null;
        docProviders = null;
        providersByType = null;
    }


}
