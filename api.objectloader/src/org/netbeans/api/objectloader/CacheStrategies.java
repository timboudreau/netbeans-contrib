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

package org.netbeans.api.objectloader;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Standard cache strategies.
 * 
 * @author Tim Boudreau
 */
public enum CacheStrategies implements CacheStrategy {
    /**
     * A cache strategy which creates references that strongly reference
     * the passed object
     */
    HARD, 
    /**
     * Factory for soft references
     */
    SOFT, 
    /**
     * Factory for weak references
     */
    WEAK;
    
    public <T> Reference<T> createReference(T obj) {
        Reference<T> result;
        switch (this) {
            case HARD :
                result = new HardRef<T> (obj);
                break;
            case SOFT :
                result = new SoftReference<T> (obj);
                break;
            case WEAK :
                result = new WeakReference<T> (obj);
                break;
            default :
                throw new AssertionError();
        }
        
        return result;
    }

    /**
     * Callback which is passed an object once it has been loaded by an
     * ObjectLoader instance
     * @param T The type of object expected
     */
    private static final class HardRef<T> extends WeakReference<T> {
        private final T t;
        HardRef (T t) {
            super (t);
            this.t = t;
        }
        
        @Override
        public T get() {
            return t;
        }
    }
    
}
