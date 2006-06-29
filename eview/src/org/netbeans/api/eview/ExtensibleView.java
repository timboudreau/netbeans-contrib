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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.api.eview;

import javax.swing.JPanel;
import javax.swing.JTextField;
import org.netbeans.modules.eview.CheckBoxControlFactory;
import org.netbeans.modules.eview.ComboBoxControlFactory;
import org.netbeans.modules.eview.DetailComboFactory;
import org.netbeans.modules.eview.EViewPanel;
import org.netbeans.modules.eview.FillerControlFactory;
import org.netbeans.modules.eview.PanelDataImpl;
import org.netbeans.modules.eview.TextAreaControlFactory;
import org.netbeans.modules.eview.TextFieldControlFactory;
import org.openide.filesystems.FileObject;

/**
 * Factory for creating panels by reading XML layer configuration.
 * @author David Strupl
 */
public class ExtensibleView {
    
    /** Name of the attribute determining the type of the created component. */
    public static final String ATTRIBUTE_TYPE = "type";
    
    /** Possible value for the "type" attribute. */
    public static final String TYPE_TEXTFIELD = "TextField";
    /** Possible value for the "type" attribute. */
    public static final String TYPE_TEXTAREA = "TextArea";
    /** Possible value for the "type" attribute. */
    public static final String TYPE_CHECKBOX = "CheckBox";
    /** Possible value for the "type" attribute. */
    public static final String TYPE_COMBOBOX = "ComboBox";
    /** Possible value for the "type" attribute. */
    public static final String TYPE_DETAILCOMBO = "DetailCombo";
    /** Possible value for the "type" attribute. */
    public static final String TYPE_FILLER = "Filler";
    
    /** Creates a new instance of ExtensibleView */
    private ExtensibleView() {
    }
    
    /**
     * This method creates a panel configured in a folder specified
     * by the location parameter.
     */
    public static JPanel createExtensiblePanel(String location) {
        return new EViewPanel(location);
    }
    
    /**
     * For a panel created by a call to <code>createExtensiblePanel</code>
     * obtain the data contained in the embeded controls.
     */
    public static PanelData getPanelData(JPanel panel) {
        if (panel instanceof EViewPanel) {
            EViewPanel evp = (EViewPanel)panel;
            PanelData data = new PanelDataImpl(evp);
            return data;
        }
        // return empty data for panel that is not EViewPanel
        PanelData data = new PanelDataImpl();
        return data;
    }
    
    /**
     * <p>This method is intended to be called from the layer file using following
     * attribute: 
     * <br></br>
     * &lt;attr name="instanceCreate" methodvalue="org.netbeans.api.eview.ExtensibleView.createControl"/&gt;
     * <br></br></p>
     * <p>
     * Following attributes must be supplied with each entry:<ul>
     *    <li><strong>componentID</strong> - String (must be unique! (in one panel)</li>
     *    <li><strong>type</strong> - String - one of the contants TYPE_xxx
     *        denoting type of the created control
     *    </li>
     *    <li><strong>(optional) label</strong> - String </li>
     * </ul>
     * <p>
     * Additional attributes can be passed along with instanceCreate.
     * Following attributes will be recognized and used:
     * <ul>
     *     <LI> For type TYPE_TEXTFIELD:
     *     <ul>
     *         <li><strong>initValue</strong> - String</li> 
     *         <li><strong>verifier</strong> - javax.swing.InputVerifier</li> 
     *     </ul></LI>
     *     <LI> For type TYPE_TEXTAREA:
     *     <ul>
     *         <li><strong>initValue</strong> - String</li> 
     *         <li><strong>verifier</strong> - javax.swing.InputVerifier</li>
     *         <li><strong>separateLines</strong> - Boolean whether the lines 
     *             are treated as an array of Strings (when true)</li>
     *         <li><strong>lines</strong> - int number of lines (determines
     *             the size of the component </li>
     *     </ul></LI>
     *     <LI> For type TYPE_CHECKBOX:
     *     <ul>
     *         <li><strong>initValue</strong> - Boolean</li> 
     *     </ul></LI>
     *     <LI> For type TYPE_COMBOBOX:
     *     <ul>
     *         <li><strong>configFolder</strong> - String folder name with the
     *             configuration</li> 
     *     </ul></LI>
     *     <LI> For type TYPE_DETAILCOMBO:
     *     <ul>
     *         <li><strong>configFolder</strong> - String folder name with the
     *             configuration</li> 
     *         <li><strong>masterID</strong> - String component ID of a ComboBox
     *             that allows switching of model for this component</li> 
     *     </ul></LI>
     * </ul>
     */
    public static ControlFactory createControl(FileObject config) {
        Object type = config.getAttribute(ATTRIBUTE_TYPE);
        if (TYPE_TEXTFIELD.equals(type)) {
            return new TextFieldControlFactory(config);
        }
        if (TYPE_TEXTAREA.equals(type)) {
            return new TextAreaControlFactory(config);
        }
        if (TYPE_CHECKBOX.equals(type)) {
            return new CheckBoxControlFactory(config);
        }
        if (TYPE_COMBOBOX.equals(type)) {
            return new ComboBoxControlFactory(config);
        }
        if (TYPE_DETAILCOMBO.equals(type)) {
            return new DetailComboFactory(config);
        }
        // use filler by default
        return new FillerControlFactory(config);
    }
    
}
