/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.lexer.editorbridge.calc;

import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.awt.Image;
import org.openide.ErrorManager;
import org.openide.util.Utilities;
import org.openide.loaders.UniFileLoader;

/**
 * Calc data loader bean info.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class CalcDataLoaderBeanInfo extends SimpleBeanInfo {

    public BeanInfo[] getAdditionalBeanInfo () {
        try {
            return new BeanInfo[] { Introspector.getBeanInfo (UniFileLoader.class) };
        } catch (IntrospectionException ie) {
	    ErrorManager.getDefault().notify(ie);
            return null;
        }
    }

    public Image getIcon(final int type) {
        if ((type == BeanInfo.ICON_COLOR_16x16) ||
            (type == BeanInfo.ICON_MONO_16x16)
        ) {
	    return Utilities.loadImage("org/netbeans/modules/lexer/editorbridge/calc/calcLoader.gif"); // NOI18N
        } else {
	    return Utilities.loadImage ("org/netbeans/modules/lexer/editorbridge/calc/calcLoader32.gif"); // NOI18N
        }
    }

}
