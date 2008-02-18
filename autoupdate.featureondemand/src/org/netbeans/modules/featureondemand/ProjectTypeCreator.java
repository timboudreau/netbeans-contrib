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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.featureondemand;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.featureondemand.api.FeatureInfo;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jirka Rechtacek
 */
public class ProjectTypeCreator {
    private ProjectTypeCreator () {}
    
    private static ProjectTypeCreator INSTANCE = new ProjectTypeCreator ();
    
    private Map<URL, String> layer2codeName;
    
    public static ProjectTypeCreator getInstance () {
        return INSTANCE;
    }
    
    public Collection<URL> getLayerURLs () {
        List<URL> res = new ArrayList<URL>();
        Lookup.Result<FeatureInfo> result = featureTypesLookup().lookupResult(FeatureInfo.class);
        for (FeatureInfo pt2m : result.allInstances ()) {
            URL url = FeatureInfoAccessor.DEFAULT.getProjectLayer(pt2m);
            if (url != null) {
                res.add(url);
            }
        }
        return res;
    }
    
    public String getCodeName (URL layer) {
        if (layer == null) {
            return null;
        }
        Lookup.Result<FeatureInfo> result = featureTypesLookup().lookupResult(FeatureInfo.class);
        for (FeatureInfo pt2m : result.allInstances ()) {
            if (layer.equals(FeatureInfoAccessor.DEFAULT.getProjectLayer(pt2m))) {
                return FeatureInfoAccessor.DEFAULT.getCodeName(pt2m);
            }
        }
        return null;
    }
    
    public URL getLayer (String codeName) {
        Lookup.Result<FeatureInfo> result = featureTypesLookup().lookupResult(FeatureInfo.class);
        for (FeatureInfo pt2m : result.allInstances ()) {
            if (codeName.equals(FeatureInfoAccessor.DEFAULT.getCodeName(pt2m))) {
                return FeatureInfoAccessor.DEFAULT.getProjectLayer(pt2m);
            }
        }
        return null;
    }
    
    private static Lookup featureTypesLookup;
    static synchronized Lookup featureTypesLookup() {
        if (featureTypesLookup != null) {
            return featureTypesLookup;
        }
        return featureTypesLookup = Lookups.forPath("FeaturesOnDemand"); // NOI18N
    }
}