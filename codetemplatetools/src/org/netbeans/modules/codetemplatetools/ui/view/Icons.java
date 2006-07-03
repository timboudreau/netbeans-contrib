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
package org.netbeans.modules.codetemplatetools.ui.view;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.Utilities;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class Icons {
    public static final ImageIcon NEW_TEMPLATE_ICON           = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/newtemplate.gif")); // NOI18N
    public static final ImageIcon SHOW_TEMPLATES_ICON         = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/templates.gif")); // NOI18N
    public static final ImageIcon TEMPLATE_ICON               = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/template.gif")); // NOI18N
    public static final ImageIcon TEMPLATE_FOR_SELECTION_ICON = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/templateforselection.gif")); // NOI18N
    public static final ImageIcon TEMPLATE_FOR_CLIPBOARDCONTENT_ICON = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/templateforclipboardcontent.gif")); // NOI18N
    public static final ImageIcon TEMPLATE_FOR_CLIPBOARDCONTENT_AND_SELECTION_ICON = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/templateforclipboardcontentandselection.gif")); // NOI18N
}
