/*
 * The contents of this file are subject to the terms of the Common
 * Development
The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 */
package org.netbeans.modules.edm.editor.widgets;

import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JCheckBox;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.model.StateModel;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.ImageWidget;


/**
 * This class represents a pin widget in the EDM visualization style.
 * The pin widget consists of a name and a glyph set.
 *
 * @author David Kaspar
 */
public class EDMPinWidget extends Widget {
    
    private ImageWidget minimizeWidget;
    private LabelWidget nameWidget;
    private EDMGlyphSetWidget glyphsWidget;
    private Widget editorWidget;
    private JCheckBox checkBox;
    private EDMNodeAnchor anchor;
    private Scene scene;
    private StateModel stateModel = new StateModel(2);
    
    
    /**
     * Creates a pin widget.
     * @param scene the scene
     */
    public EDMPinWidget(Scene scene) {
        super(scene);
        this.scene = scene;
        setBorder(EDMNodeWidget.BORDER);
        setBackground(EDMNodeWidget.COLOR_SELECTED);
        setOpaque(false);
        setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.CENTER, 8));
         
        addChild(glyphsWidget = new EDMGlyphSetWidget(scene));
        addChild(nameWidget = new LabelWidget(scene));        
        
        checkBox = new JCheckBox();
        editorWidget = new ComponentWidget(scene, checkBox);
        revalidate();
        notifyStateChanged(ObjectState.createNormal(), ObjectState.createNormal());
       
    }
    

    /**
     * Creates and enables the Inplace Editor widget.
     * 
     */
    public void enableEditor() {
        addChild(0, editorWidget);
        revalidate();
        removeChild(glyphsWidget);
        revalidate();
    }
    
     /**
     * Disables the Inplace Editor widget
      * after the selection/de-selection
     * 
     */
    public void disableEditor(){
        removeChild(editorWidget);
        revalidate();
        addChild(0, glyphsWidget);
        revalidate();
    }
    
    
    public JCheckBox getEditor() {
        return checkBox;
    }
    
    /**
     * Called to notify about the change of the widget state.
     * @param previousState the previous state
     * @param state the new state
     */
    protected void notifyStateChanged(ObjectState previousState, ObjectState state) {
        setOpaque(state.isSelected());
        setBorder(state.isFocused() || state.isHovered() ? EDMNodeWidget.BORDER_HOVERED : EDMNodeWidget.BORDER);
    }
    
    /**
     * Returns a pin name widget.
     * @return the pin name widget
     */
    public Widget getPinNameWidget() {
        return nameWidget;
    }
    
    /**
     * Sets a pin name.
     * @param name the pin name
     */
    public void setPinName(String name) {
        nameWidget.setLabel(name);
        revalidate();
    }
    
    /**
     * Returns a pin name.
     * @return the pin name
     */
    public String getPinName() {
        return nameWidget.getLabel();
    }
    
    /**
     * Sets pin glyphs.
     * @param glyphs the list of images
     */
    public void setGlyphs(List<Image> glyphs) {
        glyphsWidget.setGlyphs(glyphs);
        revalidate();
    }
    
    /**
     * Gets pin glyphs.
     * @return 
     */
    public List<Image> getGlyphs() {
        return glyphsWidget.getGlyphs();
    }    
    
    /**
     * Sets all pin properties at once.
     * @param name the pin name
     * @param glyphs the pin glyphs
     */
    public void setProperties(String name, List<Image> glyphs) {
        setPinName(name);
        glyphsWidget.setGlyphs(glyphs);
    }
    
    /**
     * Creates a horizontally oriented anchor similar to EDMNodeWidget.createAnchorPin
     *
     * @return the anchor
     */
    public Anchor createAnchor() {
        if (anchor == null) {
            anchor = new EDMNodeAnchor(this, false);
            revalidate();
        }
        return anchor;
    }
    
    public void doValidation() {
        scene.validate();
    }
    
    
}
