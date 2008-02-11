/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.clearcase;

import org.netbeans.modules.versioning.util.Utils;
import org.openide.util.NbPreferences;

import java.util.prefs.Preferences;
import java.util.*;
import java.util.regex.Pattern;
import java.io.File;

/**
 * @author Maros Sandor
 */
public class ClearcaseModuleConfig {
    
    public static final String PROP_IGNORED_PATTERNS        = "ignoredPatterns";    // NOI18N
    public static final String PROP_COMMIT_EXCLUSIONS       = "commitExclusions";   // NOI18N    
    
    private static Set<String> exclusions;
    
    /**
     * Stores list of file patterns to be ignored (excluded) from clearcase operations, this is analogous to .cvsignore list.
     */
    private static final Set<Pattern> ignoredFilePatterns = new HashSet<Pattern>(1);

    static {
        ignoredFilePatterns.addAll(toPatterns(Utils.getStringList(getPreferences(), PROP_IGNORED_PATTERNS)));
    }

    private static Collection<Pattern> toPatterns(List<String> list) {
        Set<Pattern> patterns = new HashSet<Pattern>(list.size());
        for (String s : list) {
            try {
                patterns.add(Pattern.compile(s));
            } catch (Exception e) {
                Utils.logError(ClearcaseModuleConfig.class, e);
            }
        }
        return patterns;
    }

    public static synchronized void setIgnored(File file) {
        Pattern filePattern = toPattern(file);
        String filename = file.getAbsolutePath();
        for (Pattern pattern : ignoredFilePatterns) {
            if (pattern.matcher(filename).matches()) return;
        }
        ignoredFilePatterns.add(filePattern);
        saveFilePatterns();
    }

    public static synchronized void setUnignored(File file) {
        String filename = file.getAbsolutePath();
        Set<Pattern> toRemove = new HashSet<Pattern>(1);
        for (Pattern pattern : ignoredFilePatterns) {
            if (pattern.matcher(filename).matches()) {
                toRemove.add(pattern);
            }
        }
        if (toRemove.size() > 0) {
            ignoredFilePatterns.removeAll(toRemove);
            saveFilePatterns();
        }
    }

    private static Pattern toPattern(File file) {
        String filePath = file.getAbsolutePath();
        return Pattern.compile("\\Q" + filePath + "\\E");
    }

    public static synchronized boolean isIgnored(File file) {
        String path = file.getAbsolutePath();
        for (Pattern pattern : ignoredFilePatterns) {
            if (pattern.matcher(path).matches()) return true;
        }
        return false;
    }
    
    /**
     * Gets the backing store of module preferences, use this to store and retrieve simple properties and stored values. 
     *  
     * @return Preferences backing store
     */
    public static Preferences getPreferences() {
        return NbPreferences.forModule(ClearcaseModuleConfig.class);
    }

    private static void saveFilePatterns() {
        Utils.put(getPreferences(), PROP_IGNORED_PATTERNS, toStringList(ignoredFilePatterns));
    }

    private static List<String> toStringList(Set<Pattern> patterns) {
        List<String> l = new ArrayList<String>(patterns.size());
        for (Pattern pattern : patterns) {
            l.add(pattern.toString());
        }
        return l;
    }

    /**
     * @param paths collection of paths, of File.getAbsolutePath()
     */
    // XXX VCS candidate
    public static void addExclusionPaths(Collection<String> paths) {
        Set<String> exclusionsSet = getCommitExclusions();
        if (exclusionsSet.addAll(paths)) {
            Utils.put(getPreferences(), PROP_COMMIT_EXCLUSIONS, new ArrayList<String>(exclusionsSet));
        }
    }

    /**
     * @param paths collection of paths, File.getAbsolutePath()
     */
    public static void removeExclusionPaths(Collection<String> paths) {
        Set<String> exclusionsSet = getCommitExclusions();
        if (exclusionsSet.removeAll(paths)) {
            Utils.put(getPreferences(), PROP_COMMIT_EXCLUSIONS, new ArrayList<String>(exclusionsSet));
        }
    }
    
    public static boolean isExcludedFromCommit(String path) {
        return getCommitExclusions().contains(path);
    }
    
    // private methods ~~~~~~~~~~~~~~~~~~
    
    private static synchronized Set<String> getCommitExclusions() {
        if (exclusions == null) {
            exclusions = new HashSet<String>(Utils.getStringList(getPreferences(), PROP_COMMIT_EXCLUSIONS));
        }
        return exclusions;
    }    
}
