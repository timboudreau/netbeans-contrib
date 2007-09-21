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
package org.netbeans.modules.spellchecker;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusEvent;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.Registry;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    private static ChangeListener l = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            JTextComponent c = Registry.getMostActiveComponent();

            if (c != null) {
                ComponentPeer.assureInstalled(c);
            }
        }
    };

    public void restored() {
        Registry.addChangeListener(l);
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                if (event instanceof FocusEvent) {
                    FocusEvent fe = (FocusEvent) event;

                    if (!fe.isTemporary() && fe.getSource() instanceof JTextArea && ((JTextArea) fe.getSource()).isEditable()) {
                        ComponentPeer.assureInstalled((JTextComponent) fe.getSource());
                    }
                }
            }
        }, AWTEvent.FOCUS_EVENT_MASK);
    }
    
}
