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

package org.netbeans.spi.looks;

import org.netbeans.modules.looks.LookListener;
import org.netbeans.modules.looks.SelectorListener;

/** Access to package private functionality.
 *
 * @author Jaroslav Tulach
 */
final class AccessorImpl extends org.netbeans.modules.looks.Accessor {


    public void addLookListener(Look look, Object representedObject, LookListener l) {
        look.addLookListener (representedObject, l);
    }

    public void changeLookListener(Look look, Object representedObject, LookListener oldListener, LookListener newListener) {
        look.changeLookListener (representedObject, oldListener, newListener );
    }
    
    public void removeLookListener(Look look, Object representedObject, LookListener l) {
        look.removeLookListener (representedObject, l);
    }

    public void addSelectorListener (LookSelector ls, SelectorListener l) {
        ls.getImpl().addSelectorListener(l);
    }
        
    public void removeSelectorListener (LookSelector ls, SelectorListener l) {
        ls.getImpl().removeSelectorListener (l);
    }
    
    public org.netbeans.modules.looks.SelectorImpl getSelectorImpl(LookSelector selector) {
        return selector.impl;
    }    
    
    
    /*
    public void setRegistryBridge( NamespaceSelector ns, org.netbeans.modules.looks.RegistryBridge registryBridge ) {
        ns.setRegistryBridge( registryBridge );
    }
    */
    
}
