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

package org.netbeans.modules.erd.util;

import javax.swing.*;
import java.awt.*;


public class LoadingPanel extends JPanel {

    private JLabel lLoading;

    public LoadingPanel (String text) {
        setLayout (new GridBagLayout ());

        lLoading = new JLabel (text);
        lLoading.setFont (Font.decode ("dialog-bold").deriveFont (16.0f)); // NOI18N
        lLoading.setForeground (Color.LIGHT_GRAY);

        add (lLoading, new GridBagConstraints (0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets (16, 16, 16, 16), 0, 0));
    }

}
