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
