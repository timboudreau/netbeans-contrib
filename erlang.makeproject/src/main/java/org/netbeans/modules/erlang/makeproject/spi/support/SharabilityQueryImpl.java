/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.erlang.makeproject.spi.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.spi.queries.SharabilityQueryImplementation;
import org.openide.util.WeakListeners;

/**
 * Standard impl of {@link SharabilityQueryImplementation}.
 * @author Jesse Glick
 */
final class SharabilityQueryImpl implements SharabilityQueryImplementation, PropertyChangeListener {

    private final RakeProjectHelper h;
    private final PropertyEvaluator eval;
    private final String[] includes;
    private final String[] excludes;
    /** Absolute paths of directories or files to treat as sharable (except for the excludes). */
    private String[] includePaths;
    /** Absolute paths of directories or files to treat as not sharable. */
    private String[] excludePaths;
    
    SharabilityQueryImpl(RakeProjectHelper h, PropertyEvaluator eval, String[] includes, String[] excludes) {
        this.h = h;
        this.eval = eval;
        this.includes = includes;
        this.excludes = excludes;
        computeFiles();
        eval.addPropertyChangeListener(WeakListeners.propertyChange(this, eval));
    }
    
    /** Compute the absolute paths which are and are not sharable. */
    private void computeFiles() {
        String[] _includePaths = computeFrom(includes);
        String[] _excludePaths = computeFrom(excludes);
        synchronized (this) {
            includePaths = _includePaths;
            excludePaths = _excludePaths;
        }
    }
    
    /** Compute a list of absolute paths based on some abstract names. */
    private String[] computeFrom(String[] list) {
        List<String> result = new ArrayList<String>(list.length);
        for (String s : list) {
            String val = eval.evaluate(s);
            if (val != null) {
                File f = h.resolveFile(val);
                result.add(f.getAbsolutePath());
            }
        }
        // XXX should remove overlaps somehow
        return result.toArray(new String[result.size()]);
    }
    
    public synchronized int getSharability(File file) {
        String path = file.getAbsolutePath();
        if (contains(path, excludePaths, false)) {
            return SharabilityQuery.NOT_SHARABLE;
        }
        return contains(path, includePaths, false) ?
            (contains(path, excludePaths, true) ? SharabilityQuery.MIXED : SharabilityQuery.SHARABLE) :
            SharabilityQuery.UNKNOWN;
    }
    
    /**
     * Check whether a file path matches something in the supplied list.
     * @param a file path to test
     * @param list a list of file paths
     * @param reverse if true, check if the file is an ancestor of some item; if false,
     *                check if some item is an ancestor of the file
     * @return true if the file matches some item
     */
    private static boolean contains(String path, String[] list, boolean reverse) {
        for (String s : list) {
            if (path.equals(s)) {
                return true;
            } else {
                if (reverse ? s.startsWith(path + File.separatorChar) : path.startsWith(s + File.separatorChar)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        computeFiles();
    }
    
}
