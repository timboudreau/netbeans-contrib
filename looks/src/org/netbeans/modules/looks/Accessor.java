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

import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.LookSelector;
// import org.netbeans.spi.looks.NamespaceSelector;

/**
 * Accessor to hidden features of SPI package.
 *
 * @author Jaroslav Tulach
 */
public abstract class Accessor extends Object {
    public static Accessor DEFAULT;

    static {
        // invokes Look's static initializers
        Class c = org.netbeans.spi.looks.Look.class;
        try {
            Class.forName(c.getName(), true, c.getClassLoader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // Methods which form the API of Looks -------------------------------------
    
    // Listenrs for Looks
    public abstract void addLookListener (Look look, Object representedObject, LookListener l);
    public abstract void changeLookListener( Look look, Object representedObject, LookListener oldListener, LookListener newListener );
    public abstract void removeLookListener (Look look, Object representedObject, LookListener l);
    
    // Listeners for LookSelector
    public abstract void addSelectorListener (LookSelector lookSelector, SelectorListener l);
    public abstract void removeSelectorListener (LookSelector lookSelector, SelectorListener l);
    
    // Methods used only for communication between API and Impl ----------------
    
    public abstract SelectorImpl getSelectorImpl( LookSelector selector );
    
    // Trash
    
    // public abstract void setRegistryBridge( NamespaceSelector ns, RegistryBridge registryBridge );
    
}
