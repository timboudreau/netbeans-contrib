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
package org.netbeans.modules.erd.graphics;
import java.awt.Color;
import java.awt.Image;
import java.util.List;

import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.vmd.VMDGlyphSetWidget;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author uszaty
 */
public class ColumnWidget extends Widget{
    
     private LabelWidget nameWidget;
     private VMDGlyphSetWidget glyphsWidget;
     static final Border BORDER = BorderFactory.createOpaqueBorder (2, 8, 2, 8);
     static final Color COLOR_SELECTED = new Color (0x748CC0);
     static final Border BORDER_HOVERED = BorderFactory.createLineBorder (2, 8, 2, 8, Color.BLACK);     
     public static enum CONSTRAINT_TYPE {PRIMARY_KEY,FP_KEY,FOREIGN_KEY,ORDINARY};
    /**
     * Creates a pin widget.
     * @param scene the scene
     */
    public ColumnWidget (Scene scene) {
        super (scene);

        setBorder (ColumnWidget.BORDER);
        setBackground (ColumnWidget.COLOR_SELECTED);
        setOpaque (false);
        addChild (glyphsWidget = new VMDGlyphSetWidget (scene));
        setLayout (LayoutFactory.createHorizontalLayout (LayoutFactory.SerialAlignment.CENTER, 8));
        addChild (nameWidget = new LabelWidget (scene));
        

        notifyStateChanged (ObjectState.createNormal (), ObjectState.createNormal ());
    }

    /**
     * Called to notify about the change of the widget state.
     * @param previousState the previous state
     * @param state the new state
     */
    protected void notifyStateChanged (ObjectState previousState, ObjectState state) {
        setOpaque (state.isSelected ());
        setBorder (state.isFocused () || state.isHovered () ? ColumnWidget.BORDER_HOVERED : ColumnWidget.BORDER);
//        LookFeel lookFeel = getScene ().getLookFeel ();
//        setBorder (BorderFactory.createCompositeBorder (BorderFactory.createEmptyBorder (8, 2), lookFeel.getMiniBorder (state)));
//        setForeground (lookFeel.getForeground (state));
    }

    /**
     * Returns a pin name widget.
     * @return the pin name widget
     */
    public Widget getPinNameWidget () {
        return nameWidget;
    }

    /**
     * Sets a pin name.
     * @param name the pin name
     */
    public void setPinName (String name) {
        nameWidget.setLabel (name);
    }

    /**
     * Returns a pin name.
     * @return the pin name
     */
    public String getPinName () {
        return nameWidget.getLabel();
    }

    /**
     * Sets pin glyphs.
     * @param glyphs the list of images
     */
    public void setGlyphs (List<Image> glyphs) {
        glyphsWidget.setGlyphs (glyphs);
    }
    
    private CONSTRAINT_TYPE columnType;
    public void setColumnType(CONSTRAINT_TYPE columnType){
        this.columnType=columnType;
    }
    public CONSTRAINT_TYPE getColumnType(){
        return columnType;
    }
    /**
     * Sets all pin properties at once.
     * @param name the pin name
     * @param glyphs the pin glyphs
     */
    public void setProperties (String name, List<Image> glyphs,CONSTRAINT_TYPE columnType) {
        setPinName (name);
        glyphsWidget.setGlyphs (glyphs);
        setColumnType(columnType);
    }

}
