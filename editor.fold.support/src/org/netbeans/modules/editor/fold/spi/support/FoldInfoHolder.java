/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009-2010 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2009-2010 Sun
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
package org.netbeans.modules.editor.fold.spi.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.text.Document;
import org.netbeans.modules.editor.fold.support.FoldMaintainerImpl;

/**
 *
 * @author lahvac
 */
public class FoldInfoHolder {
    private Map<String, Collection<FoldInfo>> folds = new HashMap<String, Collection<FoldInfo>>();

    //XXX: should not be public:
    public final AtomicReference<FoldMaintainerImpl> maintainer = new AtomicReference<FoldMaintainerImpl>();

    //XXX: should not be public:
    public synchronized Collection<? extends FoldInfo> getFolds() {
        List<FoldInfo> result = new LinkedList<FoldInfo>();

        for (Collection<FoldInfo> c : folds.values()) {
            result.addAll(c);
        }

        return result;
    }

    public void setFolds(String key, Collection<? extends FoldInfo> folds) {
        synchronized (this) {
            this.folds.put(key, new LinkedList<FoldInfo>(folds));
        }

        final FoldMaintainerImpl m = maintainer.get();

        if (m != null)
            m.updateFolds();
    }
    
    public static FoldInfoHolder getHolder(Document doc) {
        FoldInfoHolder h = (FoldInfoHolder) doc.getProperty(FoldInfoHolder.class);

        if (h == null) {
            doc.putProperty(FoldInfoHolder.class, h = new FoldInfoHolder());
        }

        return h;
    }

}
