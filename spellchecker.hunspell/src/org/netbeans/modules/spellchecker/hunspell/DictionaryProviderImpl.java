/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.spellchecker.hunspell;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.DictionaryProvider;

/**
 *
 * @author lahvac
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.spellchecker.spi.dictionary.DictionaryProvider.class, position=1000)
public class DictionaryProviderImpl implements DictionaryProvider {

    private static final Logger LOG = Logger.getLogger("org.netbeans.modules.spellchecker.hunspell");
    
    private static final String[] DICTIONARY_LOCATIONS = new String[] {
        "/usr/share/myspell/dicts",
    };
    
    private Map<String, Reference<Dictionary>> locale2Dictionary = new WeakHashMap<String, Reference<Dictionary>>();
    private Map<Dictionary, String> dictionary2Locale = new WeakHashMap<Dictionary, String>();
    
    public Dictionary getDictionary(Locale l) {
        String locale = l.toString();
        
        LOG.log(Level.FINE, "locale: {0}", locale);
            
        Reference<Dictionary> r = locale2Dictionary.get(locale);
        Dictionary d = r != null ? r.get() : null;
        
        if (d != null) {
            LOG.log(Level.FINE, "returning dictionary from cache");
            return null;
        }
        
        for (String location : DICTIONARY_LOCATIONS) {
            LOG.log(Level.FINE, "looking at location: {0}", location);
            
            File aff = new File(location + File.separator + locale + ".aff");
            File dict = new File(location + File.separator + locale + ".dic");
            
            LOG.log(Level.FINE, "files: aff={0}, dic={1}", new Object[] {aff.getAbsolutePath(), dict.getAbsolutePath()});
            
            if (aff.canRead() && dict.canRead()) {
                d = DictionaryImpl.create(aff, dict);
                
                if (d != null) {
                    locale2Dictionary.put(locale, new WeakReference<Dictionary>(d));
                    dictionary2Locale.put(d, locale);
                    return d;
                }
            }
        }
        
        return null;
    }

}
