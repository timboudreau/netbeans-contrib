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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui;

import java.awt.Dimension;

/**
 *
 * @author Jan Lahoda
 */
public class UIProperties {

    private boolean antialiasingEnabled = false;

    private static UIProperties instance = null;

    public synchronized static UIProperties getDefault() {
        if (instance == null)
            instance = new UIProperties();

        return instance;
    }

    /** Creates a new instance of UIProperties */
    private UIProperties() {
    }
    
    public static Dimension getGridSize() {
        return new Dimension(2 * 13, 2 * 13);
    }
    
//    public static Dimension getMinusMove() {
//        Dimension grid = getGridSize();
//        
//        return new Dimension((int) (2 * grid.getWidth()), (int) (2 * grid.getHeight()));
//    }
    
    public static double getCurveControllMagic() {
        return 50;
    }
    
    public boolean isAntialiasingEnabled() {
        return antialiasingEnabled;
    }
    
    public void setAntialiasingEnabled(boolean value) {
        this.antialiasingEnabled = value;
    }
}
