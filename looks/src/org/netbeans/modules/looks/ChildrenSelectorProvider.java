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
