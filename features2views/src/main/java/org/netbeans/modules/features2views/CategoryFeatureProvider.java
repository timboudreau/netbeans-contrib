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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.features2views;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.spi.autoupdate.UpdateItem;
import org.netbeans.spi.autoupdate.UpdateProvider;
import org.openide.modules.ModuleInfo;
import org.openide.modules.SpecificationVersion;
import org.openide.util.NbBundle;

/**
 *
 * @author Jiri Rechtacek
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.spi.autoupdate.UpdateProvider.class)
public class CategoryFeatureProvider implements UpdateProvider {
    private static final String UNSORTED_CATEGORY = "CategoryFeatureProvider_NoCategoryName"; // NOI18N
    private static String FEATURE_PROVIDER_NAME = "CategoryFeatureProvider_DisplayName"; // NOI18N
    private static final Logger log = Logger.getLogger (CategoryFeatureProvider.class.getName ()); // NOI18N

    public CategoryFeatureProvider() {
    }

    public String getName () {
        return "category2feature-provider";
    }

    public String getDisplayName () {
        return NbBundle.getMessage (CategoryFeatureProvider.class, FEATURE_PROVIDER_NAME);
    }

    public Map<String, UpdateItem> getUpdateItems () throws IOException {
        Map<String, UpdateItem> res = new HashMap<String, UpdateItem> ();
        
        // map modules to clusters
        Set<ModuleInfo> installed = InstalledModuleProvider.getDefault ().getModuleInfos (false);
        Map<String, Set<ModuleInfo>> category2modules = new HashMap<String, Set<ModuleInfo>> ();
        for (ModuleInfo info : installed) {
            String category = InstalledModuleProvider.getCategory (info);
            
            // skip fixed/eager/autoload modules
            if (category == null) {
                continue;
            }
            
            String categoryName = category == null || category.length () == 0 ? NbBundle.getMessage (CategoryFeatureProvider.class, UNSORTED_CATEGORY) : category;
            
            if (! category2modules.containsKey (categoryName)) {
                category2modules.put (categoryName, new HashSet<ModuleInfo> ());
            }
            
            category2modules.get (categoryName).add (info);
        }
        
        // create features by categories
        for (String category : category2modules.keySet ()) {
            Set<String> containsModules = new HashSet<String> ();
            String version = "";
            String description = "";
            for (ModuleInfo info : category2modules.get (category)) {
                containsModules.add (info.getCodeNameBase () + " = " + info.getImplementationVersion ());
                SpecificationVersion spec = info.getSpecificationVersion ();
                version = addVersion (version, spec);
                description += "<h5>" + info.getDisplayName () + "</h5>";
                String desc = (String) info.getLocalizedAttribute ("OpenIDE-Module-Long-Description");
                description += desc == null ? "" : desc; // NOI18N
            }

            log.log (Level.FINE, "Create feature [" + category + ", " + version +
                    "] containing modules " + containsModules);
            System.out.println ("Create feature [" + category + ", " + version +
                    "] containing modules " + containsModules);
            UpdateItem feature = UpdateItem.createFeature (category, version, containsModules, category, description, null);
            res.put (category + '_' + version, feature);
        }
        
        return res;
    }

    private static String addVersion (String version, SpecificationVersion spec) {
        int [] addend1 = getDigitsInVersion (version);
        int [] addend2 = getDigitsInVersion (spec.toString ());
        
        int length = Math.max (addend1.length, addend2.length);
        int [] result = new int [length];
        
        for (int i = 0; i < result.length; i++) {
            assert i < addend1.length || i < addend2.length;
            int digit = 0;
            if (i < addend1.length) {
                digit += addend1 [i];
            }
            if (i < addend2.length) {
                digit += addend2 [i];
            }
            result [i] = digit;
        }
        
        StringBuilder buf = new StringBuilder ((result.length * 3) + 1);

        for (int i = 0; i < result.length; i++) {
            if (i > 0) {
                buf.append ('.'); // NOI18N
            }

            buf.append (result [i]);
        }

        return buf.toString();
    }
    
    private static int [] getDigitsInVersion (String version) {
        if (version.length () == 0) {
            return new int [0];
        }
        StringTokenizer tok = new StringTokenizer (version, ".", true); // NOI18N
        
        int len = tok.countTokens ();
        assert (len % 2) != 0 : "Even number of pieces in a spec version: `" + version + "`";
        
        int[] digits = new int[len / 2 + 1];
        int i = 0;

        boolean expectingNumber = true;

        while (tok.hasMoreTokens ()) {
            if (expectingNumber) {
                expectingNumber = false;

                int piece = Integer.parseInt (tok.nextToken ());
                assert piece >= 0 : "Spec version component < 0: " + piece;
                digits[i++] = piece;
                
            } else {
                assert ".".equals (tok.nextToken ()) : "Expected dot in spec version: `" + version + "'";

                expectingNumber = true;
            }
        }
        
        return digits;
    }
    
    public boolean refresh (boolean force) throws IOException {
        InstalledModuleProvider.getDefault ().getModuleInfos (force);
        return true;
    }

}
