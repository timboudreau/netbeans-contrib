/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker;

import org.netbeans.modules.spellchecker.spi.language.TokenListProvider;
import org.netbeans.spi.editor.mimelookup.Class2LayerFolder;
import org.netbeans.spi.editor.mimelookup.InstanceProvider;

/**
 *
 * @author Jan Lahoda
 */
public class TokenListProviderFolder implements Class2LayerFolder {
    
    /** Creates a new instance of TokenListProviderFolder */
    public TokenListProviderFolder() {
    }

    public Class getClazz() {
        return TokenListProvider.class;
    }

    public String getLayerFolderName() {
        return "TokenListProvider";
    }

    public InstanceProvider getInstanceProvider() {
        return null;
    }
    
}
