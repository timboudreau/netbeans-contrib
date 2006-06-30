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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.spi.enode;

import org.openide.filesystems.FileObject;

/**
 * Utility class with a static factory method. The factory
 * method should be called from the layer.
 * @author David Strupl
 */
public final class LookupContentFactoryManager {

    /**
     * No instances of this class should be created.
     */
    private LookupContentFactoryManager() {
    }

    /**
     * This method is intended to be called from the layer
     * by means of the methodvalue file attribute. <UL>The following
     * attributes of the file object are examined:
     *     <LI><EM>"implements"</EM> - comma delimited list of interfaces/classes
     *          that the resulting object implements. These are the
     *          only classes that are examined when someone asks the
     *          lookup</LI>
     *      <LI><EM>"factoryClass"</EM> - class on which the method <code>newInstance</code>
     *          is called to create the resulting object or lookup</LI></UL>
     * @param FileObject must be from the system file system
     * @return LookupContentFactory usually wrapped in an utility wrapper
     *      that is used for passing the node argument in methods of LookupContentFactory
     *      and also as a performance optimalization in following sense: the class
     *      specified by the "factoryClass" attribute is not loaded until
     *      someone calls a lookup with argument containing a class from the list
     *      specified in the "implements" list
     */
    public static LookupContentFactory create(FileObject f) {
        return new org.netbeans.modules.enode.FactoryWrapper(f);
    }
    
}
