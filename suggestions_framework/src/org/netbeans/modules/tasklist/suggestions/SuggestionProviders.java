/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
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

    private SuggestionProviders() {
        // lazy init
    }

    public static SuggestionProviders getDefault() {
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


    /** Return a list of the providers registered
     * @todo Filter out disabled providers
     */
    public List getProviders() {
        if (providers == null) {
            providers = new ArrayList(20);
            Lookup.Template template =
                    new Lookup.Template(SuggestionProvider.class);
            Lookup.Result result = Lookup.getDefault().lookup(template);
            result.addLookupListener(new LookupListener() {
                public void resultChanged(LookupEvent ev) {
                    invalidateCaches();
                }
            });
            Iterator it = result.allInstances().iterator();
            // Two stage process so we can sort by priority

            ArrayList provList = new ArrayList(20);
            while (it.hasNext()) {
                SuggestionProvider sp = (SuggestionProvider) it.next();
                provList.add(sp);
            }
            SuggestionProvider[] provA =
                    (SuggestionProvider[]) provList.toArray(
                            new SuggestionProvider[provList.size()]);
            final SuggestionTypes types = SuggestionTypes.getTypes();
            Arrays.sort(provA, new Comparator() {
                public int compare(Object o1, Object o2) {
                    SuggestionProvider a = (SuggestionProvider) o1;
                    SuggestionProvider b = (SuggestionProvider) o2;
                    try {
                        SuggestionType at = types.getType(a.getTypes()[0]);
                        SuggestionType bt = types.getType(b.getTypes()[0]);
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

    public List getDocProviders() {
        if (docProviders == null) {
            docProviders = new ArrayList(20);
            Iterator it = getProviders().iterator();
            while (it.hasNext()) {
                Object next = (Object) it.next();
                if (next instanceof DocumentSuggestionProvider) {
                    docProviders.add(next);
                }
            }
        }
        return docProviders;
    }

    /** @return The SuggestionProvider responsible for providing suggestions
     * of a particular type */
    public SuggestionProvider getProvider(SuggestionType type) {
        if (providersByType == null) {
            SuggestionTypes suggestionTypes = SuggestionTypes.getTypes();
            //Collection types = suggestionTypes.getAllTypes();
            List providers = getProviders();
            providersByType = new HashMap(100); // XXXXX ?<??
            // Perhaps use suggestionTypes.getAllTypes()*2 as a seed?
            // Note, this includes suggestion types that do not have
            // providers
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider) it.next();
                String typeNames[] = provider.getTypes();
                if (typeNames == null) {
                    // Should I just let a NullPointerException occur instead?
                    // After all, non null is required for correct operation.
                    ErrorManager.getDefault().log("SuggestionProvider " + provider + " provides null value to getTypes()");
                    continue;
                }
                for (int j = 0; j < typeNames.length; j++) {
                    SuggestionType tp = suggestionTypes.getType(typeNames[j]);
                    providersByType.put(tp, provider);
                }
            }
        }
        return (SuggestionProvider) providersByType.get(type);
    }

    private void invalidateCaches() {
        providers = null;
        docProviders = null;
        providersByType = null;
    }


}
