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
 * Software is Nokia. Portions Copyright 2004 Nokia.
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

import java.util.*;
import org.netbeans.api.bookmarks.Bookmark;

import org.netbeans.api.registry.*;
import org.openide.util.NbBundle;

/**
 * Utility class for manipulation with contexts. This should be part
 * of Registry API but unfortunatelly (at least for now) it is not.
 * @author David Strupl
 */
public class RegistryUtil {

    /** Creates a new instance of RegistryUtil */
    private RegistryUtil() {
    }

    /**
     * Copies one context with all bindings, subcontexts and
     * attributes to the target context.
     */
    public static void copy(Context what, Context where, String newName) throws ContextException {
        Context checkWhere = where;
        while (checkWhere != null) {
            if (what.equals(checkWhere)) {
                String s1 = what.getAbsoluteContextName();
                String s2 = where.getAbsoluteContextName();
                s1 = what.getAttribute(null, org.openide.nodes.Node.PROP_DISPLAY_NAME, s1);
                s2 = where.getAttribute(null, org.openide.nodes.Node.PROP_DISPLAY_NAME, s2);

                IllegalStateException ise = new IllegalStateException(
                        "Cannot copy " + what + " to " + where); 
                throw ise;
            }
            checkWhere = checkWhere.getParentContext();
        }
        
        if (newName == null) {
            newName = what.getContextName();
        }
        Context target = where.createSubcontext(newName);
        
        // copy attributes of this context
        Collection ctxAttrNames = what.getAttributeNames(null);
        for (Iterator it = ctxAttrNames.iterator(); it.hasNext(); ) {
            String attrName = (String)it.next();
            target.setAttribute(null, attrName, what.getAttribute(null, attrName, null));
        }
        
        // first copy bindings
        Collection bNames = what.getBindingNames();
        for (Iterator i = bNames.iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            Object obj = what.getObject(name, null);
            if (obj instanceof Bookmark) {
                obj = BookmarkServiceImpl.cloneBookmark((Bookmark)obj);
                BookmarkServiceImpl.saveBookmarkActionImpl(target, name);
            }
            target.putObject(name, obj);
            Collection attrNames = what.getAttributeNames(name);
            for (Iterator it = attrNames.iterator(); it.hasNext(); ) {
                String attrName = (String)it.next();
                target.setAttribute(name, attrName, what.getAttribute(name, attrName, null));
            }
        }
        
        // copy contexts
        Collection cNames = what.getSubcontextNames();
        for (Iterator i = cNames.iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            Context sub = what.getSubcontext(name);
            copy(sub, target, sub.getContextName());
        }
        
        // set order on the newly created context
        List orderedNames = what.getOrderedNames();
        target.orderContext(orderedNames);
    }
}
