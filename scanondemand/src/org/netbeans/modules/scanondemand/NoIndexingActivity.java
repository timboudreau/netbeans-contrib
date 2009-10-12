/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.scanondemand;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import org.netbeans.modules.parsing.impl.indexing.friendapi.IndexingActivityInterceptor;
import org.openide.filesystems.FileEvent;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 * Used to intercept indexing when event is not expected.
 *
 * @author Pavel Flaska
 */
@ServiceProvider(service=IndexingActivityInterceptor.class)
public class NoIndexingActivity implements IndexingActivityInterceptor, PreferenceChangeListener {
    static final Logger LOG = Logger.getLogger(NoIndexingActivity.class.getPackage().getName());

    private final Preferences includeExclude = NbPreferences.forModule(NoIndexingActivity.class);
    private Map<Pattern, Authorization> map;

    public NoIndexingActivity() {
        includeExclude.addPreferenceChangeListener(this);
        preferenceChange(null);
    }

    @Override
    public Authorization authorizeFileSystemEvent(FileEvent event) {
        final String path = event.getFile().getPath();
        LOG.log(Level.FINE, "Changed file {0}", path); // NOI18N
        for (Map.Entry<Pattern, Authorization> entry : map.entrySet()) {
            if (entry.getKey().matcher(path).matches()) {
                final Authorization ret = entry.getValue();
                LOG.log(Level.FINER, "Found pattern {0} -> {1}", new Object[] { entry.getKey(), ret }); // NOI18N
                return ret;
            }
        }
        LOG.log(Level.FINER, "Accepted for processing."); // NOI18N
        return Authorization.PROCESS;
    }

    public void preferenceChange(PreferenceChangeEvent evt) {
        try {
            Map<Pattern, Authorization> tmp = new LinkedHashMap<Pattern, Authorization>();
            for (String regExp : includeExclude.keys()) {
                final String val = includeExclude.get(regExp, null);
                try {
                    Pattern p = Pattern.compile(regExp);
                    Authorization a = Authorization.valueOf(val);
                    tmp.put(p, a);
                    LOG.log(Level.CONFIG, "New pattern added {0} = {1}", new Object[] { regExp, a }); // NOI18N
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Cannot understand regular expression {0} = {1}", new Object[] { regExp, val }); // NOI18N
                }
            }
            this.map = tmp;
        } catch (BackingStoreException ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
}
