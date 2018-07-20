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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.looks;

import java.util.Collections;
import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.LookSelector;
import org.netbeans.spi.looks.Selectors;
import org.openide.util.Lookup;

/** Used from Looks.childrenSelectorProvider (...)
 *
 * @author  Petr Hrebejk
 */
public final class ChildrenSelectorProvider extends org.netbeans.spi.looks.ProxyLook {

    private Look look;
    private LookSelector lookSelector;

    /** Creates a new instance of ChildrenSelectorProvider */
    public ChildrenSelectorProvider( String name, Look look, LookSelector lookSelector) {
        super ( name, Selectors.singleton (look));
        this.look = look;
        this.lookSelector = lookSelector;
    }

    public String getDisplayName() {
        return look.getDisplayName();
    }


    /** Adds the LookSelector in front of items produced by the look.
     */
    public java.util.Collection getLookupItems( Object representedObject, Lookup oldEnv ) {
        java.util.Collection res = super.getLookupItems(representedObject, oldEnv );
        if ( res == null ) {
            return Collections.singleton( new Item() );
        }
        java.util.ArrayList r = new java.util.ArrayList (res.size () + 1);
        r.add (new Item ());
        r.addAll (res);
        return r;
    }

    private final class Item extends org.openide.util.Lookup.Item {

        public String getDisplayName() {
            return getId ();
        }

        public String getId() {
            return getType ().getName ();
        }

        public Object getInstance() {
            return ChildrenSelectorProvider.this.lookSelector;
        }

        public Class getType() {
            return LookSelector.class;
        }

        public boolean equals (Object o) {
            if (o instanceof Item) {
                Item i = (Item)o;
                return getInstance () == i.getInstance ();
            }
            return false;
        }
    }
}
