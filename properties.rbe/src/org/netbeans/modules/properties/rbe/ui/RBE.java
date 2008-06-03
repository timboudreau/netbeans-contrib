/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.ui;

import org.netbeans.modules.properties.PropertiesDataObject;
import org.netbeans.modules.properties.rbe.model.Bundle;

/**
 *
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class RBE {

    /** The display mode */
    private DisplayMode mode = DisplayMode.FLAT;
    /** The properties data object */
    private PropertiesDataObject propertiesDataObject;
    /** The bundle */
    private Bundle bundle;

    public RBE(PropertiesDataObject propertiesDataObject) {
        this.propertiesDataObject = propertiesDataObject;
        bundle = new Bundle(propertiesDataObject.getBundleStructure());
    }

    public DisplayMode getMode() {
        return mode;
    }

    public void setMode(DisplayMode mode) {
        this.mode = mode;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public static enum DisplayMode {

        TREE, FLAT
    }
}
