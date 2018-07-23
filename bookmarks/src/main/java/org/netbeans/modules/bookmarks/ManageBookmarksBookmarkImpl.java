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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.bookmarks;

import java.awt.EventQueue;
import org.openide.windows.TopComponent;

/**
 * Special case for bookmarking the Manager bookmark tool itself
 * @author David Strupl
 */
public class ManageBookmarksBookmarkImpl extends BookmarkImpl {

    /** Creates a new instance of ManageBookmarksBookmarkImpl */
    public ManageBookmarksBookmarkImpl() {
        putValue(NAME, ""); // NOI18N - tmp value
        Runnable r = new Runnable() {
            public void run() {
                ManageBookmarksTool mbt = ManageBookmarksTool.getInstance();
     putValue(NAME, mbt.getDisplayName());
            }
        };
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }
    }
    
    public TopComponent getTopComponent() {
        return ManageBookmarksTool.getInstance();
    }
    
    public String getName() {
        return ManageBookmarksTool.getInstance().getDisplayName();
    }
    
    void readProperties(java.util.Properties p) {
    }
    
    void writeProperties(java.util.Properties p) {
    }
    
    public Object clone() throws CloneNotSupportedException {
        BookmarkImpl res = new ManageBookmarksBookmarkImpl();
        return res;
    }
    
}
