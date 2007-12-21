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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.edm.editor.graph.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;

import org.netbeans.modules.edm.editor.graph.MashupGraphManager;

/**
 *
 * @author karthikeyan s
 */
public class ZoomCombo extends JComboBox {
    
    public ZoomCombo(final MashupGraphManager manager) {
        super();
        addItem("33.3 %");
        addItem("50 %");
        addItem("75 %");
        addItem("100 %");
        addItem("125 %");
        addItem("150 %");
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String zoomValue = (String) getSelectedItem();
                if(zoomValue.equals("33.3 %")) {
                    manager.zoomGraph(.33f);
                } else if(zoomValue.equals("50 %")) {
                    manager.zoomGraph(.5f);
                } else if(zoomValue.equals("75 %")) {
                    manager.zoomGraph(.75f);
                } else if(zoomValue.equals("100 %")) {
                    manager.zoomGraph(1.0f);
                } else if(zoomValue.equals("125 %")) {
                    manager.zoomGraph(1.25f);
                } else if(zoomValue.equals("150 %")) {
                    manager.zoomGraph(1.5f);
                }
            }
        });
        setSelectedItem("100 %");
        Dimension dim = new Dimension(60, 20);
        setPreferredSize(dim);        
        setSize(dim);      
        setMaximumSize(dim);      
        setMinimumSize(dim);
    }
}