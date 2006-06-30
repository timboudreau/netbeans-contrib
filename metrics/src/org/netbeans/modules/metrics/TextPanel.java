/*
 * TextPanel.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

class TextPanel extends JPanel {
    JTextArea text;

    TextPanel(String msg) {
        super();
	setLayout(new BorderLayout());
        text = new JTextArea("", 10, 80);
        text.setBackground(UIManager.getColor("Label.background"));
        text.setText(msg);
        text.setRows(countRows(msg));

        // Make read-only by gobbling all key events
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                event.consume();
            }
        });
        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(text);
        add("Center", sp);
    }

    // Used by MetricDetailsInvoker.
    JTextArea getTextArea() {
        return text;
    }

    private static int countRows(String s) {
        int rows = 1;
        int i = 0;
        while (true) {
            i = s.indexOf('\n', i);
            if (i == -1)
                break;
            rows++;
            i++;
        }
        return rows;
    }
}
